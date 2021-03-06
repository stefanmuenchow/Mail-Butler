package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.commons.configuration.Configuration;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;
import com.stefanmuenchow.mailbutler.mail.TaskMessage;
import com.stefanmuenchow.mailbutler.util.LogUtil;



public class PluginRepository {
	private String pluginDir;
	private Map<String, Plugin> plugins;
	private Map<String, PluginConfiguration> configs;
	
	public PluginRepository(String pluginDir) {
		this.pluginDir = pluginDir;
		plugins = new HashMap<String, Plugin>();
		configs = new HashMap<String, PluginConfiguration>();
	}

	public synchronized Plugin getPlugin(TaskMessage taskMessage) {
	    String pluginName = taskMessage.getPluginName();
	    
	    if(isAllowedUser(pluginName, taskMessage.getFromAddress())) {
	        return plugins.get(pluginName);
	    } else {
	        return null;
	    }
	}

	private synchronized boolean isAllowedUser(String pluginName, String fromAddress) {
	    PluginConfiguration pluginConfig = configs.get(pluginName);
	    
	    if(pluginConfig != null) {
	        return pluginConfig.getAllowedUsers().contains(fromAddress);
	    } else {
	        return false;
	    }
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
			throw new ButlerException(ErrorCode.JAR_NOT_FOUND, e);
		} catch (ClassNotFoundException e) {
			throw new ButlerException(ErrorCode.CLASS_NOT_FOUND, e);
		} catch (InstantiationException e) {
			throw new ButlerException(ErrorCode.INSTANTIATION_FAILURE, e);
		} catch (IllegalAccessException e) {
			throw new ButlerException(ErrorCode.INSTANTIATION_FAILURE, e);
		}
	}

	private void tryInitPlugin(PluginConfiguration c)
			throws MalformedURLException, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		
		String jarPath = pluginDir + File.separator + c.getJarFileName(); 
		ClassLoader loader = new URLClassLoader(new URL[] { new File(jarPath).toURI().toURL() });
		Class<?> newClass = loader.loadClass(c.getClassName());
		Plugin newPlugin = (Plugin) newClass.newInstance();
		newPlugin.setConfig(getCustomProperties(c));
		
		addPlugin(c, newPlugin);
	}

    @SuppressWarnings("unchecked")
    private Properties getCustomProperties(PluginConfiguration c) {
        Configuration customConfig = c.getCustomConfig();
        Properties properties = new Properties();
        Iterator<String> it = customConfig.getKeys();
        
        while (it.hasNext()) {
            String key = it.next();
            String value = customConfig.getString(key);
            properties.setProperty(key, value);
        }
        
        return properties;
    }

    synchronized void addPlugin(PluginConfiguration config, Plugin plugin) {
        String pluginName = config.getPluginName();
        
        configs.put(pluginName, config);
		plugins.put(pluginName, plugin);
    }
}
