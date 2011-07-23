package com.stefanmuenchow.mailbutler.mail;

import org.apache.commons.configuration.XMLConfiguration;

public class MailConfiguration {
	private String host;
	private String user;
	private String password;
	private String protocol;
	private String inboxName;
	private long   fetchCycleInMs;
	private int	   numFetchRetries;
	
	public static MailConfiguration newFromXML(String fileName) throws Exception {
	    XMLConfiguration xmlConfig = new XMLConfiguration("butler.xml");
	    
	    MailConfiguration mailConfig = new MailConfiguration();
	    mailConfig.setHost(xmlConfig.getString("mail.host"));
	    mailConfig.setUser(xmlConfig.getString("mail.user"));
	    mailConfig.setPassword(xmlConfig.getString("mail.password"));
	    mailConfig.setProtocol(xmlConfig.getString("mail.protocol"));
	    mailConfig.setInboxName(xmlConfig.getString("mail.inboxName"));
	    mailConfig.setFetchCycleInMs(xmlConfig.getLong("mail.fetchCycle") * 1000);
	    mailConfig.setNumFetchRetries(xmlConfig.getInt("mail.numFetchRetries"));
	    
	    return mailConfig;
	}

	private MailConfiguration() { }

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
}
