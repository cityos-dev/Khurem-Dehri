package model

import kotlinx.serialization.Serializable

/**
 * 
 * @param fileid 
 * @param name filename
 * @param propertySize file size (bytes)
 * @param createdAt Time when the data was saved on the server side.
 */
@Serializable
data class UploadedFile(
    val fileid: String,
    val name: String,
    val propertySize: Int,
    val createdAt: String
) {

}

