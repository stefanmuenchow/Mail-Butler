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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

import com.stefanmuenchow.mailbutler.exception.DaemonException;
import com.stefanmuenchow.mailbutler.exception.DaemonException.ErrorCode;
import com.stefanmuenchow.mailbutler.plugin.Plugin;
import com.stefanmuenchow.mailbutler.plugin.PluginRepository;
import com.stefanmuenchow.mailbutler.util.LogUtil;

public class MailDaemon implements Runnable {
	private DaemonConfiguration butlerConfig;
	private PluginRepository pluginRepository;
	private Session	session;
	private Store store = null;
	private Folder folder = null;
	
	public MailDaemon(DaemonConfiguration config, PluginRepository pluginRepository) { 
		this.butlerConfig = config;
		this.pluginRepository = pluginRepository;
		this.session = Session.getDefaultInstance(config.getMailSessionProperties());
	}
	
	public void run() {
		while(isRunning()) {
			try {
				fetchAndProcessMails();
			} catch (DaemonException e) {
				LogUtil.logException(e);
			} finally {
				tryCloseFolderAndStore();
			}
			
			sleep();
		}
	}
	
	private boolean isRunning() {
		return !Thread.currentThread().isInterrupted();
	}

	private void fetchAndProcessMails() {
		connectToStore();
		openInboxFolder();
		processMessages();
	}

	private void connectToStore() {
		try {
			store = session.getStore(butlerConfig.getProtocol());
			store.connect(butlerConfig.getHost(), butlerConfig.getUser(), butlerConfig.getPassword());
		} catch (NoSuchProviderException e) {
			throw new DaemonException(ErrorCode.CONNECTION_FAILURE,
					butlerConfig.getProtocol() + ", " + butlerConfig.getHost());
		} catch (MessagingException e) {
			throw new DaemonException(ErrorCode.CONNECTION_FAILURE,
					butlerConfig.getProtocol() + ", " + butlerConfig.getHost()
							+ ", " + butlerConfig.getUser());
		}
	}
	
	private void openInboxFolder() {
		try {
			folder = store.getFolder(butlerConfig.getInboxName());
			folder.open(Folder.READ_WRITE);
		} catch (MessagingException e) {
			throw new DaemonException(ErrorCode.CONNECTION_FAILURE, butlerConfig.getInboxName());
		}
	}
	
	private void processMessages() {
		try {
			Message messages[] = folder.getMessages();
			for (Message m : messages) {
				handleMessage(m);
			}
		} catch (MessagingException e) {
			throw new DaemonException(ErrorCode.MESSAGE_READ_FAILURE);
		}
	}
	
	private void handleMessage(Message m) {
		try {
			if(m.getSubject().startsWith("butler")) {
				handleTaskMessage(new TaskMessage(m));
			}
		} catch (MessagingException e) {
			throw new DaemonException(ErrorCode.MESSAGE_READ_FAILURE);
		}
	}

	private void handleTaskMessage(TaskMessage taskMessage) {
		Plugin plugin = pluginRepository.getPlugin(taskMessage.getType());
		plugin.process(taskMessage);
	}

	private void sleep() {
		try {
			Thread.sleep(butlerConfig.getFetchCycleInMs());
		} catch(InterruptedException ie) {
			Thread.currentThread().interrupt();
		}
	}
	
	private void tryCloseFolderAndStore() {
		try {
			folder.close(true);
			store.close();
		} catch (MessagingException e) {
			DaemonException de = new DaemonException(ErrorCode.CLOSE_FAILURE, e.getMessage());
			LogUtil.logException(de);
		}
	}
}
