package com.stefanmuenchow.mailbutler.plugin;

import java.io.File;
import java.util.List;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;

public class PluginConfiguration {
	private String pluginName;
	private String jarFileName;
	private String className;
	private List<String> allowedUsers;
	private Configuration customConfig;

	public PluginConfiguration(String filePath) {
		this(new File(filePath));
	}
	
	@SuppressWarnings("unchecked")
	public PluginConfiguration(File file) {
		XMLConfiguration xmlConfig;
		
		try {
			xmlConfig = new XMLConfiguration(file);
			xmlConfig.setThrowExceptionOnMissing(true);
			setPluginName(xmlConfig.getString("name"));
			setJarFileName(xmlConfig.getString("jarFile"));
			setClassName(xmlConfig.getString("class"));
			setAllowedUsers((List<String>) xmlConfig.getList("security.allowedUsers"));
			setCustomConfig(xmlConfig.configurationAt("customConfig"));
		} catch (ConfigurationException e) {
			throw new ButlerException(ErrorCode.CONFIG_READ_FAILURE, e);
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
        result = prime * result + ((className == null) ? 0 : className.hashCode());
        result = prime * result + ((jarFileName == null) ? 0 : jarFileName.hashCode());
        result = prime * result + ((pluginName == null) ? 0 : pluginName.hashCode());
        return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		
		if (obj == null || getClass() != obj.getClass()) {
			return false;
		}
		
		PluginConfiguration other = (PluginConfiguration) obj;
		if (className.equals(other.className)
		    && jarFileName.equals(other.jarFileName)
		    && pluginName.equals(other.pluginName)) {
			return true;
		}

		return false;
	}
	
}
