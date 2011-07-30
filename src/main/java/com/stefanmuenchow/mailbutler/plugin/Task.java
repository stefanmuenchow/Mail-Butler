package com.stefanmuenchow.mailbutler.plugin;

import java.util.Properties;

import javax.mail.Message;

public class Task {
	private String type;
	private Properties properties;
	private boolean done;

	public Task(Message m) {
		// TODO Auto-generated constructor stub
	}

	public void setDone(boolean done) {
		this.done = done;
	}

	public boolean isDone() {
		return done;
	}
}
