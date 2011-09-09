package com.stefanmuenchow.mailbutler.plugin;

import com.stefanmuenchow.mailbutler.mail.TaskMessage;

public interface Plugin {
	void process(TaskMessage task);
}
