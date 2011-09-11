package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;
import com.stefanmuenchow.mailbutler.mail.TaskMessage;
import com.stefanmuenchow.mailbutler.util.LogUtil;



public class PluginRepository {
	private boolean initiated;
	private String pluginDir;
	private Map<String, Plugin> plugins;
	private Map<String, PluginConfiguration> configs;
	
	public PluginRepository(String pluginDir) {
		this.pluginDir = pluginDir;
		plugins = new HashMap<String, Plugin>();
		configs = new HashMap<String, PluginConfiguration>();
		initiated = false;
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

	public synchronized void loadPlugins(List<PluginConfiguration> pluginConfigs) {
		for(PluginConfiguration c : pluginConfigs) {
			try {
				initiatePluginFromConfig(c);
			} catch (ButlerException e) {
				LogUtil.logException(e);
			}
		}
		
		setInitiated(true);
	}

	private synchronized void initiatePluginFromConfig(PluginConfiguration c) {
		try {
			tryInitPlugin(c);
		} catch (MalformedURLException e) {
			throw new ButlerException(ErrorCode.JAR_NOT_FOUND, c.getJarFileName());
		} catch (ClassNotFoundException e) {
			throw new ButlerException(ErrorCode.CLASS_NOT_FOUND, c.getClassName());
		} catch (InstantiationException e) {
			throw new ButlerException(ErrorCode.INSTANTIATION_FAILURE);
		} catch (IllegalAccessException e) {
			throw new ButlerException(ErrorCode.INSTANTIATION_FAILURE);
		}
	}

	private synchronized void tryInitPlugin(PluginConfiguration c)
			throws MalformedURLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		
		String jarPath = pluginDir + File.separator + c.getJarFileName(); 
		ClassLoader loader = new URLClassLoader(new URL[] { new File(jarPath).toURI().toURL() });
		Class<?> newClass = loader.loadClass(c.getClassName());
		Plugin newPlugin = (Plugin) newClass.newInstance();
		
		configs.put(c.getPluginName(), c);
		plugins.put(c.getPluginName(), newPlugin);
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
