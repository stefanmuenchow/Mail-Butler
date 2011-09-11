package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.util.LinkedList;
import java.util.List;

import com.stefanmuenchow.mailbutler.mail.ButlerConfiguration;

public class PluginScanner implements Runnable {
	private PluginRepository pluginRepo;
	private ButlerConfiguration butlerConfig;

	public PluginScanner(PluginRepository pluginRepo, ButlerConfiguration butlerConfig) {
		this.pluginRepo = pluginRepo;
		this.butlerConfig = butlerConfig;
	}

	@Override
	public void run() {
		while(isRunning()) {
			findAndLoadConfigs();
			sleep();
		}
	}

	private boolean isRunning() {
		return !Thread.currentThread().isInterrupted();
	}

	private void sleep() {
		try {
			Thread.sleep(butlerConfig.getPluginScanCycleInMs());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void findAndLoadConfigs() {
		File pluginPath = new File(butlerConfig.getPluginPath());
		List<File> configFiles = findConfigFiles(pluginPath);
		List<PluginConfiguration> pluginConfigs = createPluginConfigs(configFiles);
		pluginRepo.loadPlugins(pluginConfigs);
	}

	List<File> findConfigFiles(File pluginDir) {
		File[] children = pluginDir.listFiles();
		List<File> result = new LinkedList<File>();
		
		for(File f : children) {
			addXmlFilesRecursively(result, f);
		}
		
		return result;
	}

	private void addXmlFilesRecursively(List<File> result, File f) {
		if (isXmlFile(f)) {
			result.add(f);
		} else if (f.isDirectory()) {
			result.addAll(findConfigFiles(f));
		}
	}
	
	boolean isXmlFile(File f) {
		return f.isFile() && f.getName().endsWith(".xml");
	}

	private List<PluginConfiguration> createPluginConfigs(List<File> configFiles) {
		List<PluginConfiguration> configs = new LinkedList<PluginConfiguration>();
		
		for(File f : configFiles) {
			configs.add(new PluginConfiguration(f));
		}
		
		return configs;
	}
}
