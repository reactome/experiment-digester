package org.reactome.server.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.reactome.server.exception.ErrorInfo;
import org.reactome.server.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;

/**
 * Global exception handler controller
 * This controller will deal with all exceptions thrown by the other controllers if they don't treat them individually
 * <p>
 * Created by:
 *
 * @author Florian Korninger (florian.korninger@ebi.ac.uk)
 * @since 18.05.16.
 */
@ControllerAdvice
@SuppressWarnings("unused")
class GlobalExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger("errorLogger");

    //================================================================================
    // NotFound
    //================================================================================

    @ResponseStatus(HttpStatus.NOT_FOUND)
    @ExceptionHandler(NotFoundException.class)
    @ResponseBody
    ResponseEntity<String> handleNotFoundException(HttpServletRequest request, NotFoundException e) {
        //no logging here!
        return toJsonResponse(HttpStatus.NOT_FOUND, request, e.getMessage());
    }

    /*
     * Adding a JSON String manually to the response.
     *
     * Some services return a binary file or text/plain, etc. Then an ErrorInfo instance is manually converted
     * to JSON and written down in the response body.
     */
    private ResponseEntity<String> toJsonResponse(HttpStatus status, HttpServletRequest request, String exceptionMessage) {
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add("Content-Type", "application/json");
        try {
            StringBuffer requestURL = (request == null) ? new StringBuffer("") : request.getRequestURL();
            ObjectMapper mapper = new ObjectMapper();
            return ResponseEntity.status(status)
                    .headers(responseHeaders)
                    .body(mapper.writeValueAsString(new ErrorInfo(status, requestURL, exceptionMessage)));
        } catch (JsonProcessingException e1) {
            logger.error("Could not process to JSON the given ErrorInfo instance", e1);
            return ResponseEntity.status(status)
                    .headers(responseHeaders).body("");
        }
    }
}
