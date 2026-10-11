
package com.tabish.proproject.exception;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.tabish.proproject.constants.ErrorCodes;
import com.tabish.proproject.exception.custom.BadRequestException;
import com.tabish.proproject.exception.custom.InvalidCredentialsException;
import com.tabish.proproject.exception.custom.ResourceNotFoundException;
import com.tabish.proproject.exception.custom.TokenRefreshException;
import com.tabish.proproject.exception.custom.UnauthorizedException;
import com.tabish.proproject.exception.custom.UserAlreadyExistsException;
import com.tabish.proproject.exception.custom.UserNotFoundException;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestControllerAdvice
public class GlobalHandler {

	// Handle @Valid request-body validation failures
	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<ErrorResponse> handleValidationErrors(MethodArgumentNotValidException ex) {

		List<String> details = ex.getBindingResult().getFieldErrors().stream()
				.map(error -> error.getField() + ": " + error.getDefaultMessage()).toList();

		ErrorResponse response = ErrorResponse.builder().success(false).status(HttpStatus.BAD_REQUEST.value())
				.errorCode(ErrorCodes.VALIDATION_FAILED).message("Input validation failed").details(details)
				.timestamp(LocalDateTime.now()).build();

		return ResponseEntity.badRequest().body(response);
	}

	// Handle method-level validation failures
	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<ErrorResponse> handleConstraintViolation(ConstraintViolationException ex) {

		List<String> details = ex.getConstraintViolations().stream()
				.map(cv -> cv.getPropertyPath() + ": " + cv.getMessage()).toList();

		ErrorResponse response = ErrorResponse.builder().success(false).status(HttpStatus.BAD_REQUEST.value())
				.errorCode(ErrorCodes.VALIDATION_FAILED).message("Validation constraint violated").details(details)
				.timestamp(LocalDateTime.now()).build();

		return ResponseEntity.badRequest().body(response);
	}

	// Handle user not found
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex) {

		return buildError(HttpStatus.NOT_FOUND, ErrorCodes.USER_NOT_FOUND, ex.getMessage());
	}

	// Handle duplicate user
	@ExceptionHandler(UserAlreadyExistsException.class)
	public ResponseEntity<ErrorResponse> handleUserAlreadyExists(UserAlreadyExistsException ex) {

		return buildError(HttpStatus.CONFLICT, ErrorCodes.USER_ALREADY_EXISTS, ex.getMessage());
	}

	// Handle resource not found
	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity<ErrorResponse> handleResourceNotFound(ResourceNotFoundException ex) {

		return buildError(HttpStatus.NOT_FOUND, ErrorCodes.RESOURCE_NOT_FOUND, ex.getMessage());
	}

	// Handle custom invalid credentials
	@ExceptionHandler(InvalidCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleInvalidCredentials(InvalidCredentialsException ex) {

		return buildError(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_CREDENTIALS, ex.getMessage());
	}

	// Handle Spring Security authentication failures
	@ExceptionHandler(BadCredentialsException.class)
	public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {

		return buildError(HttpStatus.UNAUTHORIZED, ErrorCodes.INVALID_CREDENTIALS, "Invalid username or password");
	}

	// Handle unauthorized requests
	@ExceptionHandler(UnauthorizedException.class)
	public ResponseEntity<ErrorResponse> handleUnauthorized(UnauthorizedException ex) {

		return buildError(HttpStatus.UNAUTHORIZED, ErrorCodes.UNAUTHORIZED, ex.getMessage());
	}

	// Handle access denied
	@ExceptionHandler(AccessDeniedException.class)
	public ResponseEntity<ErrorResponse> handleAccessDenied(AccessDeniedException ex) {

		return buildError(HttpStatus.FORBIDDEN, ErrorCodes.ACCESS_DENIED,
				"You do not have permission to access this resource");
	}

	// Handle refresh-token errors
	@ExceptionHandler(TokenRefreshException.class)
	public ResponseEntity<ErrorResponse> handleTokenRefresh(TokenRefreshException ex) {

		return buildError(HttpStatus.FORBIDDEN, ErrorCodes.REFRESH_TOKEN_EXPIRED, ex.getMessage());
	}

	// Handle custom bad requests
	@ExceptionHandler(BadRequestException.class)
	public ResponseEntity<ErrorResponse> handleBadRequest(BadRequestException ex) {

		return buildError(HttpStatus.BAD_REQUEST, ErrorCodes.BAD_REQUEST, ex.getMessage());
	}

	// Catch unexpected application errors
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> handleGlobalException(Exception ex) {

		// Log the exception and complete stack trace in the server console.
		log.error("Unhandled exception: {}", ex.getClass().getName(), ex);

		String msg = (ex.getMessage() != null && !ex.getMessage().isBlank())
				? ex.getMessage()
				: "An unexpected error occurred. Please try again later.";

		return buildError(HttpStatus.INTERNAL_SERVER_ERROR, ErrorCodes.INTERNAL_SERVER_ERROR, msg);
	}

	// Shared error-response builder
	private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String errorCode, String message) {

		ErrorResponse response = ErrorResponse.builder().success(false).status(status.value()).errorCode(errorCode)
				.message(message).timestamp(LocalDateTime.now()).build();

		return ResponseEntity.status(status).body(response);
	}
}
