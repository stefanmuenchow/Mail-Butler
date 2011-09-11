package com.stefanmuenchow.mailbutler.plugin;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Test;

import com.stefanmuenchow.mailbutler.mail.ButlerConfiguration;

public class PluginScannerTest {
	private static final String pluginPath = "src/test/java/com/stefanmuenchow/mailbutler/plugin";
	private static final String pluginConfigPath = pluginPath + File.separator + "testPlugin.xml";
	private PluginScanner pluginScanner;
	private PluginRepository pluginRepoMock;
	
	public PluginScannerTest() {
		pluginRepoMock = EasyMock.createMock(PluginRepository.class);
		PluginConfiguration pluginConfig = new PluginConfiguration(new File(pluginConfigPath));
		pluginRepoMock.loadPlugins(Arrays.asList(new PluginConfiguration[] { pluginConfig }));
		EasyMock.expectLastCall();
		EasyMock.replay(pluginRepoMock);
		
		ButlerConfiguration butlerConfigMock = EasyMock.createMock(ButlerConfiguration.class);
		EasyMock.expect(butlerConfigMock.getPluginPath()).andReturn(pluginPath);
		EasyMock.expect(butlerConfigMock.getPluginScanCycleInMs()).andReturn(300000l);	
		EasyMock.replay(butlerConfigMock);
		
		pluginScanner = new PluginScanner(pluginRepoMock, butlerConfigMock);
	}
	
	@Test
	public void testRun() {
		Thread pluginScannerThread = new Thread(pluginScanner);
		pluginScannerThread.start();
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
		
		EasyMock.verify(pluginRepoMock);
	}

	@Test
	public void testFindConfigFiles() {
		List<File> expected = new LinkedList<File>();
		expected.add(new File(pluginConfigPath));
		
		assertEquals(expected, pluginScanner.findConfigFiles(new File(pluginPath)));
	}

	@Test
	public void testIsXmlFile() {
		assertTrue(pluginScanner.isXmlFile(new File(pluginConfigPath)));
	}
}
