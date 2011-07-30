/**
 * Copyright (c) Stefan Münchow. All rights reserved.
 * 
 * The use and distribution terms for this software are covered by the
 * Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
 * which can be found in the file epl-v10.html at the root of this distribution.
 * By using this software in any fashion, you are agreeing to be bound by
 * the terms of this license.
 * You must not remove this notice, or any other, from this software.
 **/

package com.stefanmuenchow.mailbutler.mail;

import java.util.Properties;

import javax.mail.Flags;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.log4j.Logger;

import com.stefanmuenchow.mailbutler.plugin.Plugin;
import com.stefanmuenchow.mailbutler.plugin.PluginRepository;
import com.stefanmuenchow.mailbutler.plugin.Task;
import com.stefanmuenchow.mailbutler.util.MessagesUtil;

public class MailDaemon implements Runnable {
	private static final Logger logger = Logger.getLogger(MailDaemon.class);
	
	private ButlerConfiguration butlerConfig;
	private PluginRepository pluginRepository;
	private Session	session;
	
	public MailDaemon(ButlerConfiguration config, PluginRepository pluginRepository) { 
		this.butlerConfig = config;
		this.pluginRepository = pluginRepository;
		this.session = Session.getDefaultInstance(createProperties(config));
	}
	
	private static Properties createProperties(ButlerConfiguration config) {
		Properties properties = new Properties();
		properties.setProperty("host", config.getHost());
		properties.setProperty("username", config.getUser());
		properties.setProperty("password", config.getPassword());
		
		return properties;
	}
	
	public void run() {
		int retries = 0;
		
		while( !Thread.currentThread().isInterrupted() 
				&& retries < butlerConfig.getNumFetchRetries()) {
			Store store = null;
			Folder folder = null;
			
			try {
				store = session.getStore(butlerConfig.getProtocol());
				store.connect(butlerConfig.getHost(), butlerConfig.getUser(), butlerConfig.getPassword());
				
				folder = store.getFolder(butlerConfig.getInboxName());
				folder.open(Folder.READ_WRITE);
				
				Message messages[] = folder.getMessages();
				for (Message m : messages) {
					if(isTaskMessage(m)) {
						handleTaskMessage(m);
					}
				}
				
				try {
					Thread.sleep(butlerConfig.getFetchCycleInMs());
				} catch(InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			} catch (Exception e) {
				retries++;
				logger.error(MessagesUtil.getString("error_readingMessages"), e);
			} finally {
				closeFolderAndStore(folder, store);
			}
		}
	}

	private boolean isTaskMessage(Message m) throws MessagingException {
		return m.getSubject().startsWith("butler");
	}

	private void handleTaskMessage(Message m) {
		Task task = new Task(m);
		Plugin plugin = pluginRepository.getPluginForTask(task);
		task = plugin.execute(task);
		
		if (task.isDone()) {
			markMessageToDelete(m);
		}
	}

	private void markMessageToDelete(Message message) {
		try {
			message.setFlag(Flags.Flag.DELETED, true);
		} catch (MessagingException e) {
			logger.error(MessagesUtil.getString("error_deletingMessage"), e);
		}
	}
	
	private void closeFolderAndStore(Folder folder, Store store) {
		try {
			folder.close(true);
		} catch (MessagingException e) {
			logger.error(MessagesUtil.getString("error_closeFolder"), e);
		}
		
		try {
			store.close();
		} catch (MessagingException e) {
			logger.error(MessagesUtil.getString("error_closeStore"), e);
		}
	}
}
