package com.stefanmuenchow.mailbutler.mail;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import org.junit.Test;

public class ButlerConfigurationTest {
	private String testConfigPath = "src/test/java/com/stefanmuenchow/mailbutler/testButler.xml";
	ButlerConfiguration testConfig;
	
	public ButlerConfigurationTest() {
		testConfig = new ButlerConfiguration(testConfigPath);
	}
	
	@Test
	public void testGetHost() {
		assertEquals("pop3.test.com", testConfig.getHost());
	}

	@Test
	public void testGetUser() {
		assertEquals("testUser", testConfig.getUser());
	}

	@Test
	public void testGetPassword() {
		assertEquals("testPassword", testConfig.getPassword());
	}

	@Test
	public void testGetMailSessionProperties() {
		Properties expectedProperties = new Properties();
		expectedProperties.setProperty("host", testConfig.getHost());
		expectedProperties.setProperty("username", testConfig.getUser());
		expectedProperties.setProperty("password", testConfig.getPassword());
		
		assertEquals(expectedProperties, testConfig.getMailSessionProperties());
	}

	@Test
	public void testGetProtocol() {
		assertEquals("pop3", testConfig.getProtocol());
	}

	@Test
	public void testGetInboxName() {
		assertEquals("INBOX", testConfig.getInboxName());
	}

	@Test
	public void testGetFetchCycleInMs() {
		assertEquals(60000l, testConfig.getFetchCycleInMs());
	}

	@Test
	public void testGetNumFetchRetries() {
		assertEquals(5, testConfig.getNumFetchRetries());
	}

	@Test
	public void testGetPluginPath() {
		assertEquals("plugins", testConfig.getPluginPath());
	}

	@Test
	public void testGetPluginScanCycleInMs() {
		assertEquals(300000l, testConfig.getPluginScanCycleInMs());
	}
}
