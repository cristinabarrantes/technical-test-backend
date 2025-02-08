package com.playtomic.tests.wallet.api;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

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
    public ResponseEntity<Map<String,String>> handleValidationExceptions(MethodArgumentNotValidException ex) {
    	List<String> errors = new ArrayList<>();
        ex.getBindingResult()
        .getAllErrors()
        .forEach(error -> {
            errors.add("Value for " + ((FieldError)error).getField() + " " + error.getDefaultMessage());
        });        
        return ResponseEntity.badRequest().body(errorDetails(String.join(", ", errors)));
    }

    private Map<String,String> errorDetails(String msg) {
        Map<String, String> errorDetails = new HashMap<>();
        errorDetails.put("status", "error");
        errorDetails.put("message", msg);
        return errorDetails;
    }
}
