package controller

import database.VideoMetadataRepository
import jakarta.annotation.Resource
import model.DatabaseFile
import model.UploadedFile
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.core.io.ByteArrayResource
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.mock.web.MockMultipartFile
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import util.VideoStorageUtil
import java.time.OffsetDateTime
import java.time.ZoneOffset
import java.util.Optional
import kotlin.test.assertEquals


@WebMvcTest(FilesApiController::class)
@ExtendWith(SpringExtension::class)
@ContextConfiguration(classes = [FilesApiController::class])
class FilesApiControllerTest {

    @MockBean
    private lateinit var videoMetadataRepository: VideoMetadataRepository

    @MockBean
    private lateinit var videoStorageUtil: VideoStorageUtil

    @Resource
    private lateinit var filesApiController: FilesApiController

//    @Test
//    fun `when Valid File ID Is Provided Then Return OK`() {
//        val fileid: Long = 1
//        val databaseFile = DatabaseFile(
//            1,
//            "test.mp4",
//            8,
//            OffsetDateTime.now(ZoneOffset.UTC),
//            "/test.mp4"
//        )
//        Mockito.`when`(videoMetadataRepository.findById(fileid)).thenReturn(Optional.of(databaseFile))
////        videoStorageUtil.delete(databaseFile.path)
////        mvc.perform(
////                delete("/v1/files/1"))
////            .andExpect(status().isOk)
//        val response: ResponseEntity<String> = filesApiController.filesFileidDelete("1")
//        assertEquals(response.statusCode.value(), 200)
//    }
//
//    @Test
//    fun `when IOException Occurs Then Return Bad Request Response`() {
//        val databaseFile = DatabaseFile(
//            2,
//            "test.mp4",
//            8,
//            OffsetDateTime.now(ZoneOffset.UTC),
//            "/test.mp4"
//        )
//        Mockito.`when`(videoMetadataRepository.findById(2)).thenReturn(Optional.of(databaseFile))
//        Mockito.`when`(videoStorageUtil.delete(databaseFile.path)).thenThrow(RuntimeException("dummy exception"))
//        val response: ResponseEntity<String> = filesApiController.filesFileidDelete("2")
//        assertEquals(response.statusCode.value(), 404)
//    }
//
//    @Test
//    fun `when File ID Is Invalid Then Return Bad Request Response`() {
//        val response: ResponseEntity<String> = filesApiController.filesFileidDelete("ID")
//        assertEquals(response.statusCode.value(), 404)
//    }
//
//    @Test
//    fun `when Video Exists Then Return Resource`() {
//        val fileid = "2"
//        val databaseFile = DatabaseFile(
//            2,
//            "test.mp4",
//            8,
//            OffsetDateTime.now(ZoneOffset.UTC),
//            "/test.mp4"
//        )
//        val resource = ByteArrayResource("test".toByteArray())
//        Mockito.`when`(videoMetadataRepository.findById(2)).thenReturn(Optional.of(databaseFile))
//        Mockito.`when`(videoStorageUtil.download(databaseFile.path)).thenReturn(resource)
//        val response = filesApiController.filesFileidGet(fileid)
//
//        if (response != null) {
//            assertEquals(200, response.statusCode.value())
//            assertEquals(resource, response.body)
//        }
//    }
//
//    @Test
//    fun `when There Are No Videos Then Return Empty List`() {
//        val response = filesApiController.filesGet()
//        assert(response.body.isEmpty())
//    }
//
//    @Test
//    fun `when Videos Are Present Then Return List Of Uploaded Files`() {
//        val databaseFile = DatabaseFile(
//            1,
//            "test.mp4",
//            8,
//            OffsetDateTime.now(ZoneOffset.UTC),
//            "/test.mp4"
//        )
//        val iterableDatabaseFiles = listOf(databaseFile).asIterable()
//        Mockito.`when`(videoMetadataRepository.findAll()).thenReturn(iterableDatabaseFiles)
//        val response = filesApiController.filesGet()
//        val uploadedFile = UploadedFile(
//            databaseFile.fileid.toString(),
//            databaseFile.name,
//            databaseFile.propertySize,
//            databaseFile.createdAt
//        )
//        assertEquals(uploadedFile, response.body[0])
//    }
//
//    @Test
//    fun `when Video Already Exists Then Return Conflict Response`() {
//        val mockFile = MockMultipartFile(
//            "video.mp4",
//            MediaType.MULTIPART_FORM_DATA_VALUE,
//            "video/mp4",
//            "test".toByteArray()
//        )
//
//        Mockito.`when`(videoStorageUtil.doesFileExist(mockFile)).thenReturn(true)
//        val response = filesApiController.filesPost(mockFile)
//
//        assertEquals(409, response.statusCode.value())
//    }
//
//    @Test
//    fun `when File Is Not The Right Content Type Then Return Unspported Type Response`() {
//        val mockFile = MockMultipartFile(
//            "song.mp3",
//            MediaType.MULTIPART_FORM_DATA_VALUE,
//            "audio/mp3",
//            "test".toByteArray()
//        )
//        val response = filesApiController.filesPost(mockFile)
//
//        assertEquals(415, response.statusCode.value())
//    }
}
