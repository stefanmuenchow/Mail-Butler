package com.stefanmuenchow.mailbutler.mail;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Address;
import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;

public class TaskMessage {
	private Message message;
	private String type;
	private String fromAddress;
	private Properties content; 
	private boolean processed = false;
	
	public TaskMessage(Message m) {
		try {
			setMessage(m);
			extractFromAdress(m);
			extractTypeAndContent(m);
		} catch (MessagingException e) {
			throw new ButlerException(ErrorCode.MESSAGE_READ_FAILURE);
		}
		
	}

	private void extractFromAdress(Message m) {
		try {
			Address[] from = m.getFrom();
			String fromStr = from[0].toString();
			setFromAddress(fromStr);
		} catch (MessagingException e) {
			throw new ButlerException(ErrorCode.GET_SENDER_FAILURE);
		}
	}

	private void extractTypeAndContent(Message m) throws MessagingException {
		type = extractType(m.getSubject());
		content = extractContent(m);
	}
	
	private  String extractType(String subject) {
		String[] parts = subject.split(" ");
		
		if( parts.length != 2 ) {
			throw new ButlerException(ErrorCode.INVALID_SUBJECT);
		}
		
		return parts[1];
	}

	private Properties extractContent(Message message) {
		String content = "";
		
		try {
			content = (String) message.getContent();
		} catch(Exception e) {
			throw new ButlerException(ErrorCode.INVALID_CONTENT);
		}
		
		return parseProperties(content);
	}

	private  Properties parseProperties(String content) {
		Pattern pattern = Pattern.compile("\\w+\\s*[:=]\\s*\\w+");
		Matcher matcher = pattern.matcher(content);
		Properties properties = new Properties();
		
		while(matcher.find()) {
			String[] keyVal = matcher.group().split("[:=]");
			
			if(keyVal.length == 2) {
				properties.setProperty(keyVal[0].trim(), keyVal[1].trim());
			} else {
				throw new ButlerException(ErrorCode.INVALID_CONTENT);
			}
		}
		
		return properties;
	}
	
	private Message getMessage() {
		return message;
	}

	private void setMessage(Message message) {
		this.message = message;
	}

	public String getPluginName() {
		return type;
	}

	public String getFromAddress() {
		return fromAddress;
	}

	public void setFromAddress(String fromAddress) {
		this.fromAddress = fromAddress;
	}

	public Properties getContent() {
		return content;
	}
	
	public boolean isProcessed() {
		return processed;
	}
	
	public void setProcessed(boolean value) {
		processed = value;
		
		if(value) {
			markToDelete();
		}
	}
	
	private void markToDelete() {
		try {
			getMessage().setFlag(Flags.Flag.DELETED, true);
		} catch (MessagingException e) {
			throw new ButlerException(ErrorCode.MESSAGE_DELETION_FAILURE);
		}
	}
	
	@Override
	public boolean equals(Object obj) {
	    if (obj != null && obj instanceof TaskMessage) {
	        TaskMessage other = (TaskMessage) obj;
	        return message.equals(other.getMessage());
	    }
	    
	    return false;
	}
	
	@Override
	public int hashCode() {
	    return message.hashCode();
	}
}
