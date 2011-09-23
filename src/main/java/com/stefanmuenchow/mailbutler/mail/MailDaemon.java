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
import javax.mail.Store;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;
import com.stefanmuenchow.mailbutler.plugin.Plugin;
import com.stefanmuenchow.mailbutler.plugin.PluginRepository;
import com.stefanmuenchow.mailbutler.util.LogUtil;

public class MailDaemon implements Runnable {
	private ButlerConfiguration butlerConfig;
	private PluginRepository pluginRepository;
	private MailSession	session = null;
	private Store store = null;
	private Folder folder = null;
	
	public MailDaemon(ButlerConfiguration config, PluginRepository pluginRepository, MailSession session) { 
		this.butlerConfig = config;
		this.pluginRepository = pluginRepository;
		this.session = session;
	}
	
	public void run() {
		while(isRunning()) {
			try {
				fetchAndProcessMails();
			} catch (ButlerException e) {
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
			throw new ButlerException(ErrorCode.CONNECTION_FAILURE, e);
		} catch (MessagingException e) {
			throw new ButlerException(ErrorCode.CONNECTION_FAILURE, e);
		}
	}
	
	private void openInboxFolder() {
		try {
			folder = store.getFolder(butlerConfig.getInboxName());
			folder.open(Folder.READ_WRITE);
		} catch (MessagingException e) {
			throw new ButlerException(ErrorCode.CONNECTION_FAILURE, e);
		}
	}
	
	private void processMessages() {
		try {
			Message messages[] = folder.getMessages();
			for (Message m : messages) {
				handleMessage(m);
			}
		} catch (MessagingException e) {
			throw new ButlerException(ErrorCode.MESSAGE_READ_FAILURE);
		}
	}
	
	private void handleMessage(Message m) {
		try {
			if(m.getSubject().startsWith("butler")) {
				handleTaskMessage(new TaskMessage(m));
			}
		} catch (MessagingException e) {
			throw new ButlerException(ErrorCode.MESSAGE_READ_FAILURE);
		}
	}

	private void handleTaskMessage(TaskMessage taskMessage) {
		Plugin plugin = pluginRepository.getPlugin(taskMessage);
		
		if (plugin != null) {
		    plugin.process(taskMessage);
		}
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
			ButlerException de = new ButlerException(ErrorCode.CLOSE_FAILURE, e);
			LogUtil.logException(de);
		}
	}
}
