package com.stefanmuenchow.mailbutler.plugin;

import static org.junit.Assert.assertEquals;

import java.util.LinkedList;
import java.util.List;

import org.junit.Test;

public class PluginConfigurationTest {
	private String testConfigPath = "src/test/resources/plugins/testPlugin.xml";
	private PluginConfiguration testConfig;
	
	public PluginConfigurationTest() {
		testConfig = new PluginConfiguration(testConfigPath);
	}
	
	@Test
	public void testGetPluginName() {
		assertEquals("test", testConfig.getPluginName());
	}

	@Test
	public void testGetJarFileName() {
		assertEquals("testPlugin.jar", testConfig.getJarFileName());
	}

	@Test
	public void testGetClassName() {
		assertEquals("com.stefanmuenchow.mailbutler.test.TestPlugin", testConfig.getClassName());
	}

	@Test
	public void testGetAllowedUsers() {
		List<String> expected = new LinkedList<String>();
		expected.add("someone@foo.com");
		expected.add("someoneelse@foo.com");
		
		assertEquals(expected, testConfig.getAllowedUsers());
	}

	@Test
	public void testGetCustomConfig() {
		assertEquals("test", testConfig.getCustomConfig().getString("attr1"));
	}
	
	@Test
	public void testHashCode() {
	    PluginConfiguration testConfig2 = new PluginConfiguration(testConfigPath);
	    assertEquals(testConfig.hashCode(), testConfig2.hashCode());
	}
}
