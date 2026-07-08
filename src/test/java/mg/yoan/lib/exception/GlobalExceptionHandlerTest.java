package mg.yoan.lib.exception;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import jakarta.servlet.http.HttpServletRequest;
import javax.naming.AuthenticationException;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

class GlobalExceptionHandlerTest {

  private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
  private final HttpServletRequest request = org.mockito.Mockito.mock(HttpServletRequest.class);

  @Test
  void handleNotFoundException_returns404() {
    when(request.getRequestURI()).thenReturn("/books/unknown");

    var response = handler.handleNotFoundException(new NotFoundException("missing"), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(404);
    assertThat(response.getBody().error()).isEqualTo("Not Found");
    assertThat(response.getBody().message()).isEqualTo("missing");
    assertThat(response.getBody().path()).isEqualTo("/books/unknown");
  }

  @Test
  void handleIllegalArgumentException_returns400() {
    when(request.getRequestURI()).thenReturn("/books");

    var response =
        handler.handleIllegalArgumentException(new IllegalArgumentException("bad"), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(400);
    assertThat(response.getBody().error()).isEqualTo("Bad Request");
    assertThat(response.getBody().message()).isEqualTo("bad");
    assertThat(response.getBody().path()).isEqualTo("/books");
  }

  @Test
  void handleException_returns500() {
    when(request.getPathInfo()).thenReturn("/internal");

    var response = handler.handleException(new RuntimeException("boom"), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(500);
    assertThat(response.getBody().error()).isEqualTo("Internal Server Error");
    assertThat(response.getBody().message()).isEqualTo("boom");
    assertThat(response.getBody().path()).isEqualTo("/internal");
  }

  @Test
  void handleAuthenticationException_returns401() {
    when(request.getRequestURI()).thenReturn("/secured");

    var response =
        handler.handleAuthenticationException(new AuthenticationException("unauthorized"), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(401);
    assertThat(response.getBody().error()).isEqualTo("Unauthorized");
    assertThat(response.getBody().message()).isEqualTo("unauthorized");
    assertThat(response.getBody().path()).isEqualTo("/secured");
  }

  @Test
  void handleIllegalStateException_returns409() {
    when(request.getRequestURI()).thenReturn("/sales");

    var response =
        handler.handleIllegalStateException(new IllegalStateException("conflict"), request);

    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
    assertThat(response.getBody()).isNotNull();
    assertThat(response.getBody().status()).isEqualTo(409);
    assertThat(response.getBody().error()).isEqualTo("Conflict");
    assertThat(response.getBody().message()).isEqualTo("conflict");
    assertThat(response.getBody().path()).isEqualTo("/sales");
  }
}
