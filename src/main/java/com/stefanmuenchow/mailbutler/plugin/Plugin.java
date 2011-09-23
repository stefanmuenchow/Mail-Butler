package com.stefanmuenchow.mailbutler.plugin;

import java.util.Properties;

import com.stefanmuenchow.mailbutler.mail.TaskMessage;

public interface Plugin {
    void setConfig(Properties props);
	void process(TaskMessage task);
}
