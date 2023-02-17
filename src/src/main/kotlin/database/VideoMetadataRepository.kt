package database

import model.DatabaseFile
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface VideoMetadataRepository : CrudRepository<DatabaseFile, Long> {

    @Query("select id from DatabaseFile")
    fun getAllIds(): List<Long>
}