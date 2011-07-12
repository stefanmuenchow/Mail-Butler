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

package com.stefanmuenchow.mailbutler.util;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.ResourceBundle;

public class MessagesUtil {
	private static final String MESSAGES 	= "messages";
	
	private static Locale currentLocale = new Locale("de", "DE");
	private static ResourceBundle messages = ResourceBundle.getBundle(MESSAGES, currentLocale);
	
	public static void setLocale(String language, String country) {
		currentLocale = new Locale(language, country);
		messages = ResourceBundle.getBundle(MESSAGES, currentLocale);
	}

	public static String getString(String key) {
		return messages.getString(key);
	}
	
	public static String getString(String key, Object... arguments) {
		MessageFormat formatter = new MessageFormat("");
		formatter.setLocale(currentLocale);
		formatter.applyPattern(messages.getString(key));
		
		return formatter.format(arguments);
	}
	
	private MessagesUtil() { 
	}
}
