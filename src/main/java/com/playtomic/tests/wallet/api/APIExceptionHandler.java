package com.playtomic.tests.wallet.api;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.context.MessageSourceResolvable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

import com.playtomic.tests.wallet.service.StripeAmountTooSmallException;

@ControllerAdvice
public class APIExceptionHandler {

	@ExceptionHandler(NoSuchElementException.class)
	public ResponseEntity<Map<String,String>> handleNotFound(NoSuchElementException e) {
		 return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorDetails("Resource not found"));
	}

	@ExceptionHandler(IllegalStateException.class)
		public ResponseEntity<Map<String,String>> handleIllegalState(IllegalStateException e) {
		 return ResponseEntity.status(HttpStatus.CONFLICT).body(errorDetails(e.getMessage()));
	}

	@ExceptionHandler(StripeAmountTooSmallException.class)
	public ResponseEntity<Map<String, String>> handleAmountTooSmall(StripeAmountTooSmallException e) {
		return ResponseEntity.badRequest().body(errorDetails("Amount too small"));
	}

	@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String,String>> handleArgumentValidationExceptions(MethodArgumentNotValidException e) {
		List<String> errors = e.getBindingResult()
			.getAllErrors()
			.stream()
			.map(this::errorMsg)
			.toList();
		return ResponseEntity.badRequest().body(errorDetails(errors));
	}

	@ExceptionHandler(HandlerMethodValidationException.class)
	public ResponseEntity<Map<String,String>> handleMethodValidationExceptions(HandlerMethodValidationException e) {
		List<String> errors = e.getAllValidationResults()
			.stream()
			.flatMap(validationResult -> validationResult.getResolvableErrors().stream())
			.map(error -> errorMsg(error))
			.toList();
		return ResponseEntity.badRequest().body(errorDetails(errors));
	}

	private String errorMsg(MessageSourceResolvable error) {
		return (error instanceof FieldError fieldError) ? fieldErrorMsg(fieldError) : error.getDefaultMessage();
	}

	private String fieldErrorMsg(FieldError error) {
		return "Value for " + error.getField() + " " + error.getDefaultMessage();
	}

	private Map<String,String> errorDetails(List<String> msgs) {
		return errorDetails(String.join(", ", msgs));
	}

	private Map<String,String> errorDetails(String msg) {
		Map<String, String> errorDetails = new HashMap<>();
		errorDetails.put("status", "error");
		errorDetails.put("message", msg);
		return errorDetails;
	}
}
