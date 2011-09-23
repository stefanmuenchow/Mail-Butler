package com.stefanmuenchow.mailbutler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.stefanmuenchow.mailbutler.exception.ButlerExceptionTest;
import com.stefanmuenchow.mailbutler.mail.ButlerConfigurationTest;
import com.stefanmuenchow.mailbutler.mail.MailDaemonTest;
import com.stefanmuenchow.mailbutler.mail.MailSenderTest;
import com.stefanmuenchow.mailbutler.mail.MailSessionTest;
import com.stefanmuenchow.mailbutler.mail.TaskMessageTest;
import com.stefanmuenchow.mailbutler.plugin.PluginConfigurationTest;
import com.stefanmuenchow.mailbutler.plugin.PluginRepositoryTest;
import com.stefanmuenchow.mailbutler.plugin.PluginScannerTest;
import com.stefanmuenchow.mailbutler.util.LogUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ButlerExceptionTest.class, 
			   ButlerConfigurationTest.class,
			   MailDaemonTest.class,
			   MailSenderTest.class,
			   MailSessionTest.class,
			   TaskMessageTest.class,
			   PluginConfigurationTest.class,
			   PluginRepositoryTest.class,
			   PluginScannerTest.class,
			   LogUtilTest.class})
public class AllTests {
	
}
