package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import com.stefanmuenchow.mailbutler.exception.DaemonException;
import com.stefanmuenchow.mailbutler.mail.DaemonConfiguration;
import com.stefanmuenchow.mailbutler.util.LogUtil;

public class PluginScanner implements Runnable {
	private PluginRepository pluginRepo;
	private DaemonConfiguration daemonConfig;

	public PluginScanner(PluginRepository pluginRepo, DaemonConfiguration daemonConfig) {
		this.pluginRepo = pluginRepo;
		this.daemonConfig = daemonConfig;
	}

	@Override
	public void run() {
		while(!Thread.currentThread().isInterrupted()) {
			try {
				scan();
				sleep();
			} catch(DaemonException e) {
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

	private void scan() {
		File pluginPath = new File(daemonConfig.getPluginPath());
		List<File> configFiles = findConfigFiles(pluginPath);
		List<PluginConfiguration> pluginConfigs = createPluginConfigs(configFiles);
		pluginRepo.updatePlugins(pluginConfigs);
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
