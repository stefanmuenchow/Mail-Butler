package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;
import com.stefanmuenchow.mailbutler.mail.TaskMessage;
import com.stefanmuenchow.mailbutler.util.LogUtil;



public class PluginRepository {
	private String pluginDir;
	private ConcurrentMap<String, Plugin> plugins;
	private ConcurrentMap<String, PluginConfiguration> configs;
	
	public PluginRepository(String pluginDir) {
		this.pluginDir = pluginDir;
		plugins = new ConcurrentHashMap<String, Plugin>();
		configs = new ConcurrentHashMap<String, PluginConfiguration>();
	}

	public Plugin getPlugin(TaskMessage taskMessage) {
		return plugins.get(taskMessage.getType());
	}

	public void loadPlugins(List<PluginConfiguration> pluginConfigs) {
		for(PluginConfiguration c : pluginConfigs) {
			try {
				initiatePluginFromConfig(c);
			} catch (ButlerException e) {
				LogUtil.logException(e);
			}
		}
	}

	private void initiatePluginFromConfig(PluginConfiguration c) {
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

	private void tryInitPlugin(PluginConfiguration c)
			throws MalformedURLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		
		String jarPath = pluginDir + File.separator + c.getJarFileName(); 
		ClassLoader loader = new URLClassLoader(new URL[] { new File(jarPath).toURI().toURL() });
		Class<?> newClass = loader.loadClass(c.getClassName());
		Plugin newPlugin = (Plugin) newClass.newInstance();
		
		addPlugin(c, newPlugin);
	}

    void addPlugin(PluginConfiguration config, Plugin plugin) {
        String pluginName = config.getPluginName();
        
        configs.put(pluginName, config);
		plugins.put(pluginName, plugin);
    }
}
