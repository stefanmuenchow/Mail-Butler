package com.stefanmuenchow.mailbutler.mail;

import java.util.Properties;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;

public class ButlerConfiguration {
	private String host;
	private String user;
	private String password;
	private String protocol;
	private String inboxName;
	private long fetchCycleInMs;
	private int numFetchRetries;
	private String pluginPath;
	private long pluginScanCycleInMs;
	
	public ButlerConfiguration(String fileName) { 
		XMLConfiguration xmlConfig;
		
		try {
			xmlConfig = new XMLConfiguration(fileName);
			xmlConfig.setThrowExceptionOnMissing(true);
			setHost(xmlConfig.getString("mail.host"));
			setUser(xmlConfig.getString("mail.user"));
			setPassword(xmlConfig.getString("mail.password"));
			setProtocol(xmlConfig.getString("mail.protocol"));
			setInboxName(xmlConfig.getString("mail.inboxName"));
			setFetchCycleInMs(xmlConfig.getLong("mail.fetchCycle") * 1000);
			setNumFetchRetries(xmlConfig.getInt("mail.numFetchRetries"));
			setPluginPath(xmlConfig.getString("plugins.path"));
			setPluginScanCycleInMs(xmlConfig.getLong("plugins.scanCycle") * 1000);
		} catch (ConfigurationException e) {
			throw new ButlerException(ErrorCode.CONFIG_READ_FAILURE, e);
		}
	}

	public String getHost() {
		return host;
	}

	private void setHost(String host) {
		this.host = host;
	}

	public String getUser() {
		return user;
	}

	private void setUser(String user) {
		this.user = user;
	}

	public String getPassword() {
		return password;
	}

	private void setPassword(String password) {
		this.password = password;
	}
	
	public Properties getMailSessionProperties() {
		Properties properties = new Properties();
		properties.setProperty("host", getHost());
		properties.setProperty("username", getUser());
		properties.setProperty("password", getPassword());
		
		return properties;
	}

	public String getProtocol() {
		return protocol;
	}

	private void setProtocol(String protocol) {
		this.protocol = protocol;
	}

	public String getInboxName() {
		return inboxName;
	}

	private void setInboxName(String inboxName) {
		this.inboxName = inboxName;
	}

	public long getFetchCycleInMs() {
		return fetchCycleInMs;
	}

	private void setFetchCycleInMs(long fetchCycleInMs) {
		this.fetchCycleInMs = fetchCycleInMs;
	}

	public int getNumFetchRetries() {
		return numFetchRetries;
	}

	private void setNumFetchRetries(int numFetchRetries) {
		this.numFetchRetries = numFetchRetries;
	}

	public String getPluginPath() {
		return pluginPath;
	}

	private void setPluginPath(String pluginPath) {
		this.pluginPath = pluginPath;
	}

	public long getPluginScanCycleInMs() {
		return pluginScanCycleInMs;
	}

	private void setPluginScanCycleInMs(long pluginScanCycleInMs) {
		this.pluginScanCycleInMs = pluginScanCycleInMs;
	}
}
