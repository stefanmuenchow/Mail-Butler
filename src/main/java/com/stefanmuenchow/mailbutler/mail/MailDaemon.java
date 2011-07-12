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

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Store;

import org.apache.log4j.Logger;

import com.stefanmuenchow.mailbutler.util.MessagesUtil;

public class MailDaemon implements Runnable {
	private static final Logger logger		= Logger.getLogger(MailDaemon.class);
	
	private static final int 	MAX_RETRIES = 3;
	private static final long	SLEEP_TIME	= 60000;
	
	private String 	host;
	private String 	username;
	private String 	password;
	private Session	session;
	
	public static MailDaemon newFromConfig(String fileName) throws Exception {
		Properties props = new Properties();
		props.loadFromXML(new FileInputStream(new File(fileName)));
		
		MailDaemon daemon = new MailDaemon();
		daemon.setHost(props.getProperty("host"));
		daemon.setUsername(props.getProperty("username"));
		daemon.setPassword(props.getProperty("password"));
		daemon.setSession(Session.getDefaultInstance(props));
		
		return daemon;
	}
	
	private MailDaemon() { }
	
	public void setHost(String host) {
		this.host = host;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	private void setSession(Session session) {
		this.session = session;
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
				&& retries < MAX_RETRIES) {
			Store store = null;
			Folder folder = null;
			
			try {
				store = session.getStore("pop3");
				store.connect(host, username, password);
				
				folder = store.getFolder("INBOX");
				folder.open(Folder.READ_WRITE);
				
				Message message[] = folder.getMessages();
				for (int i=0, n=message.length; i<n; i++) {
					System.out.println(i + ": " + message[i].getFrom()[0] + "\t" + message[i].getSubject());
				}
				
				try {
					Thread.sleep(SLEEP_TIME);
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
