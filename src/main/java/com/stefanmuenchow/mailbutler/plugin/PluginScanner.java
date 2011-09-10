package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.mail.ButlerConfiguration;
import com.stefanmuenchow.mailbutler.util.LogUtil;

public class PluginScanner implements Runnable {
	private PluginRepository pluginRepo;
	private ButlerConfiguration daemonConfig;

	public PluginScanner(PluginRepository pluginRepo, ButlerConfiguration daemonConfig) {
		this.pluginRepo = pluginRepo;
		this.daemonConfig = daemonConfig;
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				scanForConfigs();
				sleep();
			} catch(ButlerException e) {
				LogUtil.logException(e);
			}
		}
	}

	private void sleep() {
		try {
			Thread.sleep(daemonConfig.getPluginScanCycleInMs());
		} catch (InterruptedException e) {
			Thread.currentThread().interrupt();
		}
	}

	private void scanForConfigs() {
		File pluginPath = new File(daemonConfig.getPluginPath());
		List<File> configFiles = findConfigFiles(pluginPath);
		List<PluginConfiguration> pluginConfigs = createPluginConfigs(configFiles);
		try {
			pluginRepo.loadPlugins(pluginConfigs);
		} catch(Exception e) {
			//TODO handle err
		}
		
	}

	private List<File> findConfigFiles(File pluginDir) {
		File[] children = pluginDir.listFiles();
		List<File> result = new LinkedList<File>();
		
		for(File f : children) {
			if (f.isFile() && f.getName().endsWith(".xml")) {
				result.add(f);
			} else {
				result.addAll(findConfigFiles(f));
			}
		}
		
		return result;
	}
	
	private List<PluginConfiguration> createPluginConfigs(List<File> configFiles) {
		List<PluginConfiguration> configs = new ArrayList<PluginConfiguration>(configFiles.size());
		
		for(File f : configFiles) {
			configs.add(new PluginConfiguration(f));
		}
		
		return configs;
	}
}
