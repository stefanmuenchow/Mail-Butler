package com.stefanmuenchow.mailbutler.exception;

public class ButlerException extends RuntimeException {
	private static final long serialVersionUID = -2657325216939463485L;
	
	private ErrorCode errorCode;
	
	public enum ErrorCode {
		UNDEFINED, 
		CONFIG_READ_FAILURE,
		INVALID_SUBJECT, 
		MESSAGE_READ_FAILURE, 
		INVALID_CONTENT, 
		MESSAGE_DELETION_FAILURE, 
		CLOSE_FAILURE, 
		CONNECTION_FAILURE, 
		GET_SENDER_FAILURE
	}
	
	public ButlerException() {
		this(ErrorCode.UNDEFINED, "");
	}
	
	public ButlerException(String message) {
		this(ErrorCode.UNDEFINED, message);
	}
	
	public ButlerException(ErrorCode errorCode) {
		this(errorCode, "");
	}
	
	public ButlerException(ErrorCode errorCode, String additionalText) {
		super(additionalText);
		setErrorCode(errorCode);
	}
	
	public ErrorCode getErrorCode() {
		return errorCode;
	}

	private void setErrorCode(ErrorCode errorCode) {
		this.errorCode = errorCode;
	}

	public Object getErrorMessage() {
		switch (getErrorCode()) {
		case CLOSE_FAILURE:
			return "CLOSE_FAILURE";
		case CONFIG_READ_FAILURE:
			String additional = getMessage().equals("") ? "" : (": " + getMessage());
			return "CONFIG_READ_FAILURE" + additional;
		case CONNECTION_FAILURE:
			return "CONNECTION_FAILURE";
		case GET_SENDER_FAILURE:
			return "GET_SENDER_FAILURE";
		case INVALID_CONTENT:
			return "INVALID_CONTENT";
		case INVALID_SUBJECT:
			return "INVALID_SUBJECT";
		case MESSAGE_DELETION_FAILURE:
			return "MESSAGE_DELETION_FAILURE";
		case MESSAGE_READ_FAILURE:
			return "MESSAGE_READ_FAILURE";
		case UNDEFINED:
		default:
			return super.getMessage();
		}
	}
}
