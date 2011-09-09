package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.stefanmuenchow.mailbutler.exception.DaemonException;
import com.stefanmuenchow.mailbutler.exception.DaemonException.ErrorCode;

public class PluginConfiguration {
	private String pluginName;
	private String jarFileName;
	private String className;
	private List<String> allowedUsers;
	private Configuration customConfig;

	@SuppressWarnings("unchecked")
	public PluginConfiguration(File file) { 
		XMLConfiguration xmlConfig;
		
		try {
			xmlConfig = new XMLConfiguration(file);
			xmlConfig.setThrowExceptionOnMissing(true);
			setPluginName(xmlConfig.getString("name"));
			setJarFileName(xmlConfig.getString("jarFile"));
			setClassName(xmlConfig.getString("pluginClass"));
			setAllowedUsers((List<String>) xmlConfig.getList("security.allowedUsers"));
			setCustomConfig(xmlConfig.configurationAt("customConfig"));
		} catch (ConfigurationException e) {
			throw new DaemonException(ErrorCode.CONFIG_READ_FAILURE, fileName);
		}
	}

	public String getPluginName() {
		return pluginName;
	}

	private void setPluginName(String pluginName) {
		this.pluginName = pluginName;
	}

	public String getJarFileName() {
		return jarFileName;
	}

	private void setJarFileName(String jarFileName) {
		this.jarFileName = jarFileName;
	}

	public String getClassName() {
		return className;
	}

	private void setClassName(String className) {
		this.className = className;
	}

	public List<String> getAllowedUsers() {
		return allowedUsers;
	}

	private void setAllowedUsers(List<String> allowedUsers) {
		this.allowedUsers = allowedUsers;
	}

	public Configuration getCustomConfig() {
		return customConfig;
	}

	private void setCustomConfig(Configuration customConfig) {
		this.customConfig = customConfig;
	}
	
}
