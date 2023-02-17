package model

/**
 * 
 * @param fileid 
 * @param name filename
 * @param propertySize file size (bytes)
 * @param createdAt Time when the data was saved on the server side.
 */
data class UploadedFile(
    val fileid: String,
    val name: String,
    val propertySize: Int,
    val createdAt: java.time.OffsetDateTime
) {

}

