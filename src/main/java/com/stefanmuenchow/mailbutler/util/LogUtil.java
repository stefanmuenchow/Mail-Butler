package com.stefanmuenchow.mailbutler.util;

import org.apache.log4j.Logger;

import com.stefanmuenchow.mailbutler.exception.DaemonException;

public class LogUtil {
	private static final Logger log = Logger.getLogger(LogUtil.class);

	public static void logException(DaemonException e) {
		log.error(e.errorMessage());
		log.debug(e);
	}
}
