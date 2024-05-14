package com.camus.backend.global.Exception;

import org.springframework.http.HttpStatus;

import com.amazonaws.http.SdkHttpMetadata;

import lombok.Getter;

@Getter
public class CustomException extends RuntimeException {

	private final HttpStatus httpStatusCode;
	private final Integer errorKey;

	public CustomException(ErrorCode error) {
		super(error.getErrorMessage());
		this.httpStatusCode = error.getHttpStatusCode();
		this.errorKey = error.getErrorKey();
	}

}
