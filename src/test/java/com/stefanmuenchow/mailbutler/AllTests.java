package com.stefanmuenchow.mailbutler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;

import com.stefanmuenchow.mailbutler.exception.ButlerExceptionTest;
import com.stefanmuenchow.mailbutler.mail.ButlerConfigurationTest;
import com.stefanmuenchow.mailbutler.mail.TaskMessageTest;
import com.stefanmuenchow.mailbutler.plugin.PluginConfigurationTest;
import com.stefanmuenchow.mailbutler.util.LogUtilTest;

@RunWith(Suite.class)
@SuiteClasses({ButlerExceptionTest.class, 
			   ButlerConfigurationTest.class,
			   TaskMessageTest.class,
			   PluginConfigurationTest.class,
			   LogUtilTest.class})
public class AllTests {
	
}
