package com.stefanmuenchow.mailbutler.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.stefanmuenchow.mailbutler.exception.ButlerException;

public class LogUtil {
	private static Logger logger = Logger.getLogger(LogUtil.class);

	public static void setLogger(Logger logger) {
		LogUtil.logger = logger;
	}
	
	public static void logException(ButlerException e) {
		if (logger.getEffectiveLevel() == Level.DEBUG) {
			logger.error(e.getErrorMessage(), e);
		} else {
			logger.error(e.getErrorMessage());
		}
	}
}
