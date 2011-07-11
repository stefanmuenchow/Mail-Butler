package com.stefanmuenchow.mailbutler;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;

public class MailDaemon {
	
	public static void main(String[] args) {
		Properties props = new Properties();
		try {
			props.loadFromXML(new FileInputStream(new File("butler.xml")));
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		// Get session
		Session session = Session.getDefaultInstance(props, null);
		
		try {
			// Get the store
			Store store = session.getStore("pop3");
			store.connect(props.getProperty("host"), props.getProperty("username"), props.getProperty("password"));
			
			// Get folder
			Folder folder = store.getFolder("INBOX");
			folder.open(Folder.READ_ONLY);
			
			// Get directory
			Message message[] = folder.getMessages();
			for (int i=0, n=message.length; i<n; i++) {
				System.out.println(i + ": " + message[i].getFrom()[0] + "\t" + message[i].getSubject());
			}
			
			// Close connection
			folder.close(false);
			store.close();
		} catch (NoSuchProviderException e) {
			e.printStackTrace();
		} catch (MessagingException e) {
			e.printStackTrace();
		}
	}
}
