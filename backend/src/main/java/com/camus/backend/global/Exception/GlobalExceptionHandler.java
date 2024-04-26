package com.camus.backend.global.Exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(CustomException.class)
	public ResponseEntity<?> handleCustomException(CustomException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", ex.getHttpStatusCode());
		body.put("errorKey", ex.getErrorKey());
		body.put("message", ex.getMessage());
		return new ResponseEntity<>(body, ex.getHttpStatusCode());
	}
}
