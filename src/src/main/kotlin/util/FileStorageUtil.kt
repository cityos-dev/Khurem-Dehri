package util

import org.springframework.core.io.Resource
import org.springframework.web.multipart.MultipartFile

interface FileStorageUtil {

    fun init()

    fun upload(file: MultipartFile)

    fun download(filePath: String): Resource

    fun doesFileExist(file: MultipartFile): Boolean

    fun delete(filePath: String)

    fun deleteAll()
}