package com.stefanmuenchow.mailbutler.mail;

import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.mail.Flags;
import javax.mail.Message;
import javax.mail.MessagingException;

import com.stefanmuenchow.mailbutler.exception.DaemonException;
import com.stefanmuenchow.mailbutler.exception.DaemonException.ErrorCode;

public class TaskMessage {
	private Message message;
	private String type;
	private Properties content; 
	private boolean processed = false;
	
	public TaskMessage(Message m) {
		try {
			message = m;
			extractTypeAndContent(m);
		} catch (MessagingException e) {
			throw new DaemonException(ErrorCode.MESSAGE_READ_FAILURE);
		}
		
	}

	private void extractTypeAndContent(Message m) throws MessagingException {
		type = extractType(m.getSubject());
		content = extractContent(m);
	}
	
	private  String extractType(String subject) {
		String[] parts = subject.split(" ");
		
		if( parts.length != 2 ) {
			throw new DaemonException(ErrorCode.INVALID_SUBJECT);
		}
		
		return parts[1];
	}

	private Properties extractContent(Message message) {
		String content = "";
		
		try {
			content = (String) message.getContent();
		} catch(Exception e) {
			throw new DaemonException(ErrorCode.INVALID_CONTENT);
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
				properties.setProperty(keyVal[0], keyVal[1]);
			} else {
				throw new DaemonException(ErrorCode.INVALID_CONTENT);
			}
		}
		
		return properties;
	}
	
	public String getType() {
		return type;
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
			message.setFlag(Flags.Flag.DELETED, true);
		} catch (MessagingException e) {
			throw new DaemonException(ErrorCode.MESSAGE_DELETION_FAILURE);
		}
	}
}
