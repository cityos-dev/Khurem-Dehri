import controller.FilesApiController
import controller.HealthApiController
import database.VideoMetadataRepository
import model.DatabaseFile
import org.springframework.boot.CommandLineRunner
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import util.VideoStorageUtil
import java.util.logging.Logger
import kotlin.system.exitProcess

@SpringBootApplication
@ComponentScan(
    basePackageClasses = [
        FilesApiController::class,
        VideoMetadataRepository::class,
        HealthApiController::class,
        VideoStorageUtil::class
    ]
)
@EntityScan(basePackageClasses = [DatabaseFile::class])
@EnableJpaRepositories
class Application {

    val logger: Logger = Logger.getLogger("VideoServerApplication")

    @Bean
    fun init(videoStorageUtil: VideoStorageUtil) = CommandLineRunner {
        try {
            videoStorageUtil.deleteAll()
            videoStorageUtil.init()
        } catch (e: Exception) {
            logger.info("Video Storage Failed to Initialize with error: ${e.message}")
            exitProcess(1)
        }
    }
}

fun main() {
    runApplication<Application>()
}


