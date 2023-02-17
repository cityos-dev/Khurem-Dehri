package controller

import database.VideoMetadataRepository
import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.enums.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import io.swagger.v3.oas.annotations.security.*
import jakarta.validation.Valid
import model.DatabaseFile
import model.UploadedFile
import org.apache.commons.lang3.StringUtils.isNumeric
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.multipart.MultipartFile
import util.VideoStorageUtil
import java.time.OffsetDateTime
import java.time.ZoneOffset

@RestController
@Validated
@RequestMapping("\${api.base-path:/v1}")
class FilesApiController {

    @Autowired
    private lateinit var videoMetadataRepository: VideoMetadataRepository

    @Autowired
    private lateinit var videoStorageUtil: VideoStorageUtil

    @Operation(
        summary = "",
        operationId = "filesFileidDelete",
        description = "Delete a video file",
        responses = [
            ApiResponse(responseCode = "204", description = "File was successfully removed"),
            ApiResponse(responseCode = "404", description = "File not found")]
    )
    @RequestMapping(
        method = [RequestMethod.DELETE],
        value = ["/files/{fileid}"]
    )
    fun filesFileidDelete(@Parameter(description = "", required = true) @PathVariable("fileid") fileid: String): ResponseEntity<String> {
        if (isNumeric(fileid) && fileid.toLongOrNull() != null) {
            val databaseFile = videoMetadataRepository.findById(fileid.toLong())
            if (databaseFile.isPresent) {
                return try {
                    videoStorageUtil.delete(databaseFile.get().path)
                    ResponseEntity.ok("File was successfully removed")
                } catch (e: Exception) {
                    ResponseEntity("File not found", HttpStatus.NOT_FOUND)
                }
            }
        }

        return ResponseEntity("File not found", HttpStatus.NOT_FOUND)

    }

    @Operation(
        summary = "",
        operationId = "filesFileidGet",
        description = "Download a video file by fileid. The file name will be restored as it was when you uploaded it.",
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "OK",
                content = [Content(schema = Schema(implementation = org.springframework.core.io.Resource::class))]
            ),
            ApiResponse(responseCode = "404", description = "File not found")]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/files/{fileid}"],
        produces = ["video/mp4", "video/mpeg"]
    )
    fun filesFileidGet(@Parameter(description = "", required = true) @PathVariable("fileid") fileid: String): ResponseEntity<out Any>? {
        val databaseFile = videoMetadataRepository.findById(fileid.toLong())
        if (databaseFile.isPresent) {
            val video = videoStorageUtil.download(databaseFile.get().path)
            return ResponseEntity.ok(video)
        }

        return ResponseEntity<Any>("File not found", HttpStatus.NOT_FOUND)
    }

    @Operation(
        summary = "",
        operationId = "filesGet",
        description = "List uploaded files",
        responses = [
            ApiResponse(responseCode = "200", description = "File list", content = [Content(schema = Schema(implementation = UploadedFile::class))])]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/files"],
        produces = ["application/json"]
    )
    fun filesGet(): ResponseEntity<List<UploadedFile>> {

        return ResponseEntity.ok(
            videoMetadataRepository.findAll().map {
                UploadedFile(it.fileid.toString(), it.name, it.propertySize, it.createdAt)
            }
        )
    }

    @Operation(
        summary = "",
        operationId = "filesPost",
        description = "Upload a video file",
        responses = [
            ApiResponse(responseCode = "201", description = "File uploaded"),
            ApiResponse(responseCode = "400", description = "Bad request"),
            ApiResponse(responseCode = "409", description = "File exists"),
            ApiResponse(responseCode = "415", description = "Unsupported Media Type")]
    )
    @RequestMapping(
        method = [RequestMethod.POST],
        value = ["/files"],
        consumes = ["multipart/form-data"]
    )
    fun filesPost(@Parameter(description = "file detail") @Valid @RequestPart("file") data: MultipartFile): ResponseEntity<String> {

        if (data.isEmpty) {
            ResponseEntity.badRequest().body("Bad request")
        }

        when (data.contentType) {
            "video/mp4" -> "mp4"
            "video/mpeg" -> "mpeg"
            else -> return ResponseEntity("Unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        }

        val createdDateTime = OffsetDateTime.now(ZoneOffset.UTC)
        val fileToUpload = DatabaseFile(
            createdAt = createdDateTime,
            name = data.name,
            path = data.originalFilename,
            propertySize = data.size.toInt()
        )

        videoMetadataRepository.save(fileToUpload)
        if (videoStorageUtil.doesFileExist(data)) {
            return ResponseEntity("File exists", HttpStatus.CONFLICT)
        }

        try {
            videoStorageUtil.upload(data)
        } catch (e: RuntimeException) {
            return ResponseEntity.badRequest().body("Bad request")
        }

        val successMessage = "File uploaded"
        return ResponseEntity.ok(successMessage)
    }
}
