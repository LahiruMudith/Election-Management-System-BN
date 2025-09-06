package lk.ijse.election_backend.exception;

import io.jsonwebtoken.ExpiredJwtException;
import lk.ijse.election_backend.dto.ApiResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;

@RequiredArgsConstructor
@ControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiResponse handleRuntimeException(RuntimeException ex) {
        return new ApiResponse(500, "Server Error", null);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ApiResponse handleUserNameNotFoundException(UsernameNotFoundException ex) {
        return new ApiResponse(404, "User Not Found", null);
    }

    @ExceptionHandler(BadCredentialsException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleBadCredentials(BadCredentialsException ex) {
        return new ApiResponse(400, "Invalid Credentials", null);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    @ResponseStatus(HttpStatus.UNAUTHORIZED)
    public ApiResponse handleJWTTokenExpiredException(ExpiredJwtException ex) {
        return new ApiResponse(401, "JWT Token Expired", null);
    }

    @ExceptionHandler(UserAlreadyRegisteredException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiResponse handleUserAlreadyRegisteredException(UserAlreadyRegisteredException ex) {
        return new ApiResponse(400, ex.getMessage(), null);
    }
}
