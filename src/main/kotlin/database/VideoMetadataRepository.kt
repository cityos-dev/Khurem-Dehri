package database

import model.DatabaseFile
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.CrudRepository

interface VideoMetadataRepository : CrudRepository<DatabaseFile, String> {
}