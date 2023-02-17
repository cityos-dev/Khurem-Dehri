package model

import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.Column
import jakarta.persistence.GenerationType

//keep all the values needed for uploadedfile plus path
@Entity
@Table(name = "videoMetadata")
data class DatabaseFile (
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    var fileid: Long = 0,

    @Column(name = "name", nullable = false)
    val name: String,

    @Column(name = "propertySize", nullable = false)
    val propertySize: Int,

    @Column(name = "createdAt", nullable = false)
    val createdAt: java.time.OffsetDateTime,

    @Column(name = "path", nullable = false)
    val path: String
)