package com.stefanmuenchow.mailbutler.plugin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;



public class PluginRepository {
	private boolean initiated;
	private Map<String, Plugin> plugins;
	
	public PluginRepository(String pluginPath) {
		plugins = new HashMap<String, Plugin>();
		initiated = false;
		
//		File file  = new File("c:\\myjar.jar");
//		 URL url = file.toURL();  
//		 URL[] urls = new URL[]{url};
//		 ClassLoader cl = new URLClassLoader(urls);
//
//		 Class cls = cl.loadClass("com.mypackage.myclass");
	}

	public synchronized Plugin getPlugin(String taskType) {
		if (!isInitiated()) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		return plugins.get(taskType);
	}

	public synchronized void updatePlugins(List<PluginConfiguration> pluginConfigs) {
		
		// TODO Read jar files and load classes
		
		setInitiated(true);
	}

	private synchronized boolean isInitiated() {
		return initiated;
	}

	private synchronized void setInitiated(boolean initiated) {
		if(!isInitiated()) {
			this.initiated = initiated;
			notify();
		}
	}
}
