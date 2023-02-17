package util

import org.springframework.core.io.Resource
import org.springframework.core.io.UrlResource
import org.springframework.stereotype.Component
import org.springframework.util.FileSystemUtils
import org.springframework.web.multipart.MultipartFile
import java.io.IOException
import java.net.MalformedURLException
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption

@Component
class VideoStorageUtil : FileStorageUtil {

    private val ROOT_PATH: Path = Paths.get("/tmp/videoserver")

    override fun init() {
        try {
            Files.createDirectories(ROOT_PATH)
        } catch (e: IOException) {
            throw RuntimeException("Error Initializing Video Storage")
        }
    }

    override fun upload(file: MultipartFile) {
        try {
            val destinationPath = this.ROOT_PATH.resolve(Paths.get(file.originalFilename))
                .normalize()
                .toAbsolutePath()
            if (!destinationPath.parent.equals(this.ROOT_PATH.toAbsolutePath())) {
                throw RuntimeException("Input file path provided is not valid.")
            }
            file.inputStream.use { fileStream ->
                Files.copy(fileStream, destinationPath, StandardCopyOption.REPLACE_EXISTING)
            }
        } catch (e: IOException) {
            throw RuntimeException("Error with input. Exception message: ${e.message}")
        }
    }

    override fun download(filePath: String): Resource {
        try {
            val videoFile = ROOT_PATH.resolve(filePath)
            val videoResource = UrlResource(videoFile.toUri())
            if (videoResource.exists() || videoResource.isReadable) {
                return videoResource
            } else {
                throw RuntimeException("Video does not exist or is not readable.")
            }
        } catch (e: MalformedURLException) {
            throw RuntimeException("Video could not be found at the specified path.")
        }
    }

    override fun doesFileExist(file: MultipartFile): Boolean {
        val destinationPath = this.ROOT_PATH.resolve(Paths.get(file.originalFilename))
            .normalize()
            .toAbsolutePath()
        return Files.exists(destinationPath)
    }

    override fun delete(filePath: String) {
        val filePath = ROOT_PATH.resolve(filePath)
        try {
            FileSystemUtils.deleteRecursively(filePath)
        } catch (e: IOException) {
            throw RuntimeException("Error deleting video. Exception message: ${e.message}")
        }
    }

    override fun deleteAll() {
        FileSystemUtils.deleteRecursively(ROOT_PATH)
    }

}