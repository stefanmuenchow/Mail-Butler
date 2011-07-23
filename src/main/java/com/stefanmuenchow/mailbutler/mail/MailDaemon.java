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

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.log4j.Logger;

import com.stefanmuenchow.mailbutler.util.MessagesUtil;

public class MailDaemon implements Runnable {
	private static final Logger logger = Logger.getLogger(MailDaemon.class);
	
	private MailConfiguration config;
	private Session	session;
	
	private static Properties createProperties(MailConfiguration config) {
		Properties properties = new Properties();
		properties.setProperty("host", config.getHost());
		properties.setProperty("username", config.getUser());
		properties.setProperty("password", config.getPassword());
		
		return properties;
	}
	
	public static MailDaemon newFromConfig(MailConfiguration config) {
		MailDaemon daemon = new MailDaemon(config);
		return daemon;
	}
	
	private MailDaemon(MailConfiguration config) { 
		this.config = config;
		this.session = Session.getDefaultInstance(createProperties(config));
	}
	
	private void closeFolderAndStore(Folder folder, Store store) {
		try {
			folder.close(true);
		} catch (MessagingException e) {
			logger.error(MessagesUtil.getString("error_closeFolder"));
		}
		
		try {
			store.close();
		} catch (MessagingException e) {
			logger.error(MessagesUtil.getString("error_closeStore"));
		}
	}

	public void run() {
		int retries = 0;
		
		while( !Thread.currentThread().isInterrupted() 
				&& retries < config.getNumFetchRetries()) {
			Store store = null;
			Folder folder = null;
			
			try {
				store = session.getStore(config.getProtocol());
				store.connect(config.getHost(), config.getUser(), config.getPassword());
				
				folder = store.getFolder(config.getInboxName());
				folder.open(Folder.READ_WRITE);
				
				Message message[] = folder.getMessages();
				for (int i=0, n=message.length; i<n; i++) {
					System.out.println(i + ": " + message[i].getFrom()[0] + "\t" + message[i].getSubject());
				}
				
				try {
					Thread.sleep(config.getFetchCycleInMs());
				} catch(InterruptedException ie) {
					Thread.currentThread().interrupt();
				}
			} catch (Exception e) {
				retries++;
				logger.error(MessagesUtil.getString("error_readingMessages") + ":" + e.getMessage());
			} finally {
				closeFolderAndStore(folder, store);
			}
		}
	}
}
