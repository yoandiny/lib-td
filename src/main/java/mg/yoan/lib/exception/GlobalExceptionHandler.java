package mg.yoan.lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.time.Instant;
import javax.naming.AuthenticationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<ErrorResponse> handleNotFoundException(
      NotFoundException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.NOT_FOUND)
        .body(
            new ErrorResponse(
                Instant.now(), 404, "Not Found", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(IllegalArgumentException.class)
  public ResponseEntity<ErrorResponse> handleIllegalArgumentException(
      IllegalArgumentException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.BAD_REQUEST)
        .body(
            new ErrorResponse(
                Instant.now(), 400, "Bad Request", ex.getMessage(), request.getRequestURI()));
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleException(Exception e, HttpServletRequest request) {
    ErrorResponse error =
        new ErrorResponse(
            Instant.now(),
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase(),
            e.getMessage(),
            request.getPathInfo());

    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
  }

  @ExceptionHandler(AuthenticationException.class)
  public ResponseEntity<ErrorResponse> handleAuthenticationException(
      AuthenticationException ex, HttpServletRequest request) {
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
        .body(
            new ErrorResponse(
                Instant.now(), 401, "Unauthorized", ex.getMessage(), request.getRequestURI()));
  }
}
