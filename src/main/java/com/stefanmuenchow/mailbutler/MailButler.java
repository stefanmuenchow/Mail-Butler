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

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.mail.ButlerConfiguration;
import com.stefanmuenchow.mailbutler.mail.MailDaemon;
import com.stefanmuenchow.mailbutler.mail.MailSender;
import com.stefanmuenchow.mailbutler.mail.MailSession;
import com.stefanmuenchow.mailbutler.plugin.PluginRepository;
import com.stefanmuenchow.mailbutler.plugin.PluginScanner;
import com.stefanmuenchow.mailbutler.util.LogUtil;


public class MailButler {
	public static void main(String[] args) {
		String configFileName = "butler.xml";
		
		try {
			configureAndStartMailDaemon(configFileName);
		} catch (ButlerException e) {
			LogUtil.logException(e);
		}
	}

	private static void configureAndStartMailDaemon(String configFileName) {
		ButlerConfiguration butlerConfig = new ButlerConfiguration(configFileName);
		PluginRepository pluginRepository = new PluginRepository(butlerConfig.getPluginPath());
		PluginScanner pluginScanner = new PluginScanner(pluginRepository, butlerConfig);
		MailSession mailSession = new MailSession(butlerConfig.getMailSessionProperties());
		MailDaemon mailDaemon = new MailDaemon(butlerConfig, pluginRepository, mailSession);
		MailSender mailSender = new MailSender(mailSession);
		MailSender.setDefaultInstance(mailSender);
		
		final Thread pluginScannerThread = new Thread(pluginScanner);
		final Thread mailDaemonThread = new Thread(mailDaemon);
		
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				pluginScannerThread.interrupt();
				mailDaemonThread.interrupt();
				
				try {
					pluginScannerThread.join();
					mailDaemonThread.join();
				} catch (InterruptedException e) {
					Thread.currentThread().interrupt();
				}
			}
		});
		
		pluginScannerThread.start();
		mailDaemonThread.start();
	}
}
