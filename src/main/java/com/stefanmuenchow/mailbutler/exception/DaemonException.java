package com.stefanmuenchow.mailbutler.exception;

public class DaemonException extends RuntimeException {
	private static final long serialVersionUID = -2657325216939463485L;
	
	private ErrorCode errorCode = ErrorCode.UNDEFINED;
	
	public enum ErrorCode {
		UNDEFINED, 
		CONFIG_READ_FAILURE,
		INVALID_SUBJECT, 
		MESSAGE_READ_FAILURE, 
		INVALID_CONTENT, 
		MESSAGE_DELETION_FAILURE, 
		CLOSE_FAILURE, 
		CONNECTION_FAILURE
	}
	
	public DaemonException() {
	}
	
	public DaemonException(String message) {
		super(message);
	}
	
	public DaemonException(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}
	
	public DaemonException(ErrorCode errorCode, String additionalText) {
		super(additionalText);
		this.errorCode = errorCode;
	}
	
	public Object errorMessage() {
		switch (errorCode) {
		case UNDEFINED:
		default:
			return super.getMessage();
		}
	}
}
