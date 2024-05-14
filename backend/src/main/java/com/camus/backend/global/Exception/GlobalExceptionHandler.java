package com.camus.backend.global.Exception;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
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

	@ExceptionHandler(HttpMessageNotReadableException.class)
	public ResponseEntity<?> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
		Map<String, Object> body = new LinkedHashMap<>();
		body.put("status", ErrorCode.INVALID_PARAMETER.getHttpStatusCode());
		body.put("errorKey", ErrorCode.INVALID_PARAMETER.getErrorKey());
		body.put("message", ErrorCode.INVALID_PARAMETER.getErrorMessage());

		return new ResponseEntity<>(body, ErrorCode.INVALID_PARAMETER.getHttpStatusCode());
	}
}
