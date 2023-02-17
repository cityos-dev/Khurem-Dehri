package util

import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType
import org.springframework.mock.web.MockMultipartFile
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class VideoStorageUtilTest {

    private val videoStorageUtil: VideoStorageUtil = VideoStorageUtil()

    private val file = MockMultipartFile(
        "test",
        "test.mp4",
        MediaType.MULTIPART_FORM_DATA_VALUE,
        "test".byteInputStream()
    )

    @BeforeEach
    fun init() {
        videoStorageUtil.deleteAll()
        videoStorageUtil.init()
    }

    @Test
    fun `when video is uploaded Then Loading It Should Succeed`() {
        videoStorageUtil.upload(file)
        assertTrue(videoStorageUtil.download("test.mp4").exists())
    }

    @Test
    fun `when Checking For A Video That Does Not Exist Then Return False`() {
        assertFalse(videoStorageUtil.doesFileExist(file))
    }

    @Test
    fun `when Checking For A Video That Has Been Uploaded Then Return True`() {
        videoStorageUtil.upload(file)
        assertTrue(videoStorageUtil.doesFileExist(file))
    }

    @Test
    fun `when An Uploaded File Is Deleted Then Existence Check Returns False`() {
        videoStorageUtil.upload(file)
        assertTrue(videoStorageUtil.doesFileExist(file))
        videoStorageUtil.delete("/tmp/videoserver/test.mp4")
        assertFalse(videoStorageUtil.doesFileExist(file))
    }

    @Test
    fun `when Absolute Path Is Uploaded Then Throw Exception`() {
        val failFile = MockMultipartFile(
            "test",
            "/etc/test",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "test".byteInputStream()
        )
        val exception = assertFailsWith<RuntimeException>(
            block = { videoStorageUtil.upload(failFile) }
        )
        assertEquals("Input file path provided is not valid.", exception.message)
    }

    @Test
    fun `when Relative Path To File Is Uploaded Then Throw Exception`() {
        val failFile = MockMultipartFile(
            "test",
            "../tmp/test.mp4",
            MediaType.MULTIPART_FORM_DATA_VALUE,
            "test".byteInputStream()
        )
        val exception = assertFailsWith<RuntimeException>(
            block = { videoStorageUtil.upload(failFile) }
        )
        assertEquals("Input file path provided is not valid.", exception.message)
    }

}