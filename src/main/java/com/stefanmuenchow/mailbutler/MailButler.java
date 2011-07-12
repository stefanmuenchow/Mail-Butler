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

package com.stefanmuenchow.mailbutler;

import org.apache.log4j.Logger;

import com.stefanmuenchow.mailbutler.mail.MailDaemon;
import com.stefanmuenchow.mailbutler.util.MessagesUtil;


public class MailButler {
	private static final Logger logger		= Logger.getLogger(MailButler.class);

	public static void main(String[] args) {
		String configFile = "butler.xml";
		
		try {
			MailDaemon daemon = MailDaemon.newFromConfig(configFile);
			Thread daemonThread = new Thread(daemon);
			daemonThread.start();
			
			try {
				Thread.sleep(300000);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
			
			daemonThread.interrupt();
		} catch (Exception e) {
			logger.error(MessagesUtil.getString("error_fileCannotBeRead", configFile));
		}
	}
}
