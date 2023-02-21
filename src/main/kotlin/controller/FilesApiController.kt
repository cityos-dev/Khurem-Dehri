package controller

import database.VideoMetadataRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import jakarta.validation.Valid
import model.DatabaseFile
import model.UploadedFile
import org.apache.tomcat.util.http.fileupload.FileUploadException
import org.jboss.logging.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.Resource
import org.springframework.http.ContentDisposition
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.util.MimeType
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestPart
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.multipart.MultipartFile
import util.VideoStorageUtil
import java.nio.file.Files
import java.nio.file.Path
import java.time.OffsetDateTime
import java.time.ZoneOffset


@RestController
@Validated
@ControllerAdvice
@RequestMapping("\${api.base-path:/v1}")
class FilesApiController {

    @Autowired
    private lateinit var videoMetadataRepository: VideoMetadataRepository

    @Autowired
    private lateinit var videoStorageUtil: VideoStorageUtil

    private final val SUPPORTED_MIME_TYPES = listOf("video/mpeg", "video/mp4")

    private final val logger = Logger.getLogger(FilesApiController::class.java)

    @Operation(
        summary = "",
        operationId = "filesFileidDelete",
        description = "Delete a video file",
        responses = [ApiResponse(responseCode = "204", description = "File was successfully removed"), ApiResponse(
            responseCode = "404",
            description = "File not found"
        )]
    )
    @RequestMapping(
        method = [RequestMethod.DELETE], value = ["/files/{fileid}"]
    )
    fun filesFileidDelete(
        @Parameter(
            description = "", required = true
        ) @PathVariable("fileid") fileid: String
    ): ResponseEntity<String> {

        val databaseFile = videoMetadataRepository.findById(fileid)
        if (databaseFile.isPresent) {
            return try {
                videoStorageUtil.delete(databaseFile.get().path)
                videoMetadataRepository.delete(databaseFile.get())
                logger.info("Deleted ${databaseFile.get().name} from the video storage server.")
                ResponseEntity("File was successfully removed", HttpStatus.NO_CONTENT)
            } catch (e: Exception) {
                logger.warn("Delete of file ${databaseFile.get().name} unsuccessful. File could not be found.")
                ResponseEntity("File not found", HttpStatus.NOT_FOUND)
            }
        }


        return ResponseEntity("File not found", HttpStatus.NOT_FOUND)

    }

    @Operation(
        summary = "",
        operationId = "filesFileidGet",
        description = "Download a video file by fileid. The file name will be restored as it was when you uploaded it.",
        responses = [ApiResponse(
            responseCode = "200",
            description = "OK",
            content = [Content(schema = Schema(implementation = Resource::class))]
        ), ApiResponse(responseCode = "404", description = "File not found")]
    )
    @RequestMapping(
        method = [RequestMethod.GET], value = ["/files/{fileid}"], produces = ["video/mp4", "video/mpeg"]
    )
    fun filesFileidGet(
        @Parameter(
            description = "", required = true
        ) @PathVariable("fileid") fileid: String
    ): ResponseEntity<out Any>? {

        val databaseFile = videoMetadataRepository.findById(fileid)

        if (databaseFile.isPresent) {
            logger.info("Video file located. Downloading file ${databaseFile.get().name} at path ${databaseFile.get().path}")
            val contentDisposition = ContentDisposition.builder("inline").filename(databaseFile.get().name).build()
            val video = videoStorageUtil.download(databaseFile.get().path)
            val responseHeader = HttpHeaders()
            val mimeType = Files.probeContentType(Path.of(video.uri))
            responseHeader.contentDisposition = contentDisposition
            responseHeader.contentType = MediaType.asMediaType(MimeType.valueOf(mimeType))
            return ResponseEntity(video, responseHeader, 200)
        }
        logger.warn("Video file with ID $fileid could not be found.")
        return ResponseEntity<Any>("File not found", HttpStatus.NOT_FOUND)
    }

    @Operation(
        summary = "", operationId = "filesGet", description = "List uploaded files", responses = [ApiResponse(
            responseCode = "200",
            description = "File list",
            content = [Content(schema = Schema(implementation = UploadedFile::class))]
        )]
    )
    @RequestMapping(
        method = [RequestMethod.GET], value = ["/files"], produces = ["application/json"]
    )
    fun filesGet(): ResponseEntity<List<UploadedFile>> {

        val responseHeader = HttpHeaders()
        responseHeader.contentType = MediaType.APPLICATION_JSON
        return ResponseEntity(
            videoMetadataRepository.findAll().map {
                    UploadedFile(it.fileid, it.name, it.propertySize, it.createdAt)
            }, responseHeader, 200
        )
    }

    @Operation(
        summary = "",
        operationId = "filesPost",
        description = "Upload a video file",
        responses = [ApiResponse(responseCode = "201", description = "File uploaded"), ApiResponse(
            responseCode = "400",
            description = "Bad request"
        ), ApiResponse(responseCode = "409", description = "File exists"), ApiResponse(
            responseCode = "415",
            description = "Unsupported Media Type"
        )]
    )
    @RequestMapping(
        method = [RequestMethod.POST], value = ["/files"], consumes = ["multipart/form-data"]
    )
    fun filesPost(@Parameter(description = "file detail") @Valid @RequestPart("data") data: MultipartFile?): ResponseEntity<String> {
        if (data == null || data.isEmpty) {
            logger.error("Data to upload is empty.")
            return ResponseEntity.badRequest().body("Bad request")
        }

        if (!SUPPORTED_MIME_TYPES.contains(data.contentType)) {
            logger.error("Data to upload is the wrong content type. Content type: ${data.contentType}")
            return ResponseEntity("Unsupported Media Type", HttpStatus.UNSUPPORTED_MEDIA_TYPE)
        }


        if (videoStorageUtil.doesFileExist(data)) {
            logger.error("File with same name already exists in storage. Name: ${data.name}")
            return ResponseEntity("File exists", HttpStatus.CONFLICT)
        }

        return try {
            logger.info("Creating entry into database for file with name: ${data.name}")
            val createdDateTime = OffsetDateTime.now(ZoneOffset.UTC)
            val pathToSave = videoStorageUtil.upload(data)
            val fileToUpload = DatabaseFile(
                fileid = data.originalFilename,
                createdAt = createdDateTime.toString(),
                name = data.originalFilename,
                path = pathToSave,
                propertySize = data.size.toInt()
            )
            val savedRequest = videoMetadataRepository.save(fileToUpload)
            val responseHeaders = HttpHeaders()
            responseHeaders.set("Location", savedRequest.path)
            ResponseEntity(responseHeaders, HttpStatus.CREATED)
        } catch (e: Exception) {
            ResponseEntity.badRequest().body("Bad request")
        }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Bad Request")
    @ExceptionHandler(FileUploadException::class)
    fun fileUploadNotFound(exception: FileUploadException) {
    }
}
