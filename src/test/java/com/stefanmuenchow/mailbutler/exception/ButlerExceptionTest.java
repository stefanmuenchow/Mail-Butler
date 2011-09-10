package com.stefanmuenchow.mailbutler.exception;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;

public class ButlerExceptionTest {

	@Test
	public void testButlerException() {
		ButlerException ex = new ButlerException();
		assertEquals(ErrorCode.UNDEFINED, ex.getErrorCode());
		assertEquals("", ex.getErrorMessage());
	}

	@Test
	public void testButlerExceptionString() {
		ButlerException ex = new ButlerException("Kaboom!");
		assertEquals(ErrorCode.UNDEFINED, ex.getErrorCode());
		assertEquals("Kaboom!", ex.getErrorMessage());
	}

	@Test
	public void testButlerExceptionErrorCode() {
		ButlerException ex = new ButlerException(ErrorCode.CONFIG_READ_FAILURE);
		assertEquals(ErrorCode.CONFIG_READ_FAILURE, ex.getErrorCode());
		assertEquals("CONFIG_READ_FAILURE", ex.getErrorMessage());
	}

	@Test
	public void testButlerExceptionErrorCodeString() {
		ButlerException ex = new ButlerException(ErrorCode.CONFIG_READ_FAILURE, "config.xml");
		assertEquals(ErrorCode.CONFIG_READ_FAILURE, ex.getErrorCode());
		assertEquals("CONFIG_READ_FAILURE: config.xml", ex.getErrorMessage());
	}

	@Test
	public void testGetErrorCode() {
		ButlerException ex = new ButlerException(ErrorCode.CONFIG_READ_FAILURE);
		assertEquals(ErrorCode.CONFIG_READ_FAILURE, ex.getErrorCode());
	}

	@Test
	public void testGetErrorMessage() {
		ButlerException ex = new ButlerException(ErrorCode.CONFIG_READ_FAILURE);
		assertEquals("CONFIG_READ_FAILURE", ex.getErrorMessage());
	}
}
