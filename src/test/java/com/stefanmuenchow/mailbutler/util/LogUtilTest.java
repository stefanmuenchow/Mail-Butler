package com.stefanmuenchow.mailbutler.util;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.easymock.EasyMock;
import org.junit.Test;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;

public class LogUtilTest {
	private ButlerException butlerException;
	private Logger loggerMock;
	
	public LogUtilTest() {
		butlerException = new ButlerException(ErrorCode.CLOSE_FAILURE, new Exception("This is some text."));
		loggerMock = EasyMock.createMock(Logger.class);
		LogUtil.setLogger(loggerMock);
	}
	
	@Test
	public void testLogExceptionDebug() {
		EasyMock.expect(loggerMock.getEffectiveLevel()).andReturn(Level.DEBUG);
		loggerMock.error(butlerException.getErrorMessage(), butlerException);
		EasyMock.expectLastCall();
		EasyMock.replay(loggerMock);
		
		LogUtil.logException(butlerException);
		EasyMock.verify(loggerMock);
	}

	@Test
	public void testLogException() {
		EasyMock.expect(loggerMock.getEffectiveLevel()).andReturn(Level.INFO);
		loggerMock.error(butlerException.getErrorMessage());
		EasyMock.expectLastCall();
		EasyMock.replay(loggerMock);
		
		LogUtil.logException(butlerException);
		EasyMock.verify(loggerMock);
	}

}
