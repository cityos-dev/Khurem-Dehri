package controller

import io.swagger.v3.oas.annotations.*
import io.swagger.v3.oas.annotations.enums.*
import io.swagger.v3.oas.annotations.media.*
import io.swagger.v3.oas.annotations.responses.*
import io.swagger.v3.oas.annotations.security.*
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity

import org.springframework.web.bind.annotation.*
import org.springframework.validation.annotation.Validated
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.beans.factory.annotation.Autowired

import kotlin.collections.List
import kotlin.collections.Map

@RestController
@Validated
@RequestMapping("\${api.base-path:/v1}")
class HealthApiController() {

    @Operation(
        summary = "",
        operationId = "healthGet",
        description = "Return the health of the service as HTTP 200 status. Useful to check if everything is configured correctly.",
        responses = [
            ApiResponse(responseCode = "200", description = "OK") ]
    )
    @RequestMapping(
        method = [RequestMethod.GET],
        value = ["/health"]
    )
    fun healthGet(): ResponseEntity<String> {
        return ResponseEntity.ok("OK")
    }
}
