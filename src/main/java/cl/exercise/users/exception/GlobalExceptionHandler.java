package cl.exercise.users.exception;

import cl.exercise.users.util.Constants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.Map;
import java.util.UUID;

@Slf4j
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidationErrors(MethodArgumentNotValidException ex) {
        log.error("Error: ", ex);
        String msg = ex.getBindingResult().getFieldErrors().stream()
                .map(DefaultMessageSourceResolvable::getDefaultMessage)
                .findFirst()
                .orElse(Constants.VALIDATION_ERROR);

        return ResponseEntity.badRequest().body(Map.of(Constants.MSG, msg));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<Map<String, String>> handleEmptyRequestBody(HttpMessageNotReadableException ex) {
        log.error("Error: ", ex);
        return ResponseEntity.badRequest().body(Map.of(Constants.MSG, Constants.BODY_CANT_EMPTY));
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<Map<String, String>> handleValidationException(ValidationException ex) {
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(Constants.MSG, ex.getMessage()));
    }

    @ExceptionHandler(EmailExistException.class)
    public ResponseEntity<Map<String, String>> handleEmailExistException(EmailExistException ex) {
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(Constants.MSG, ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, String>> handleAllOtherExceptions(Exception ex) {
        log.error("Error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(Constants.MSG, Constants.INTERNAL_SERVER));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<Map<String, String>> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        log.error("Error: ", ex);
        if (ex.getRequiredType() == UUID.class) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of(Constants.MSG,Constants.INVALID_UUID));
        }
        return ResponseEntity
                .badRequest()
                .body(Map.of(Constants.MSG,Constants.INVALID_PARAMETER + ex.getMessage()));
    }
}
