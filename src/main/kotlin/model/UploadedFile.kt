package model

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * 
 * @param fileid 
 * @param name filename
 * @param propertySize file size (bytes)
 * @param createdAt Time when the data was saved on the server side.
 */
data class UploadedFile(
    @JsonProperty("fileid") val fileid: String,
    @JsonProperty("name") val name: String,
    @JsonProperty("size") val propertySize: Int,
    @JsonProperty("created_at") val createdAt: String
) {

}

