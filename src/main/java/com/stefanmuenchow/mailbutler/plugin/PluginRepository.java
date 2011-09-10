package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stefanmuenchow.mailbutler.mail.TaskMessage;



public class PluginRepository {
	private boolean initiated;
	private Map<String, Plugin> plugins;
	private Map<String, PluginConfiguration> configs;
	
	public PluginRepository() {
		plugins = new HashMap<String, Plugin>();
		configs = new HashMap<String, PluginConfiguration>();
		initiated = false;
		
//		File file  = new File("c:\\myjar.jar");
//		 URL url = file.toURL();  
//		 URL[] urls = new URL[]{url};
//		 ClassLoader cl = new URLClassLoader(urls);
//
//		 Class cls = cl.loadClass("com.mypackage.myclass");
	}

	public synchronized Plugin getPlugin(TaskMessage taskMessage) {
		if (!isInitiated()) {
			try {
				wait();
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
			}
		}
		
		return plugins.get(taskMessage.getType());
	}

	public synchronized void loadPlugins(List<PluginConfiguration> pluginConfigs) throws MalformedURLException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		ClassLoader loader;
		Class<?> newClass;
		
		for(PluginConfiguration c : pluginConfigs) {
			configs.put(c.getPluginName(), c);
			loader = new URLClassLoader(new URL[] {new File(c.getJarFileName()).toURI().toURL()});
			newClass = loader.loadClass(c.getClassName());
			Plugin newPlugin = (Plugin) newClass.newInstance();
			plugins.put(c.getPluginName(), newPlugin);
		}
		
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
