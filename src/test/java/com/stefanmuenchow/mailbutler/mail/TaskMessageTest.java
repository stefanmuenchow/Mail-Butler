package com.stefanmuenchow.mailbutler.mail;

import static org.junit.Assert.assertEquals;

import java.util.Properties;

import javax.mail.Address;
import javax.mail.Flags.Flag;
import javax.mail.Message;
import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Test;

public class TaskMessageTest {
	private Message mockMessage;
	private TaskMessage taskMessage;
	
	public TaskMessageTest() throws Exception {
		mockMessage = EasyMock.createMock(Message.class);
		EasyMock.expect(mockMessage.getFrom()).andReturn(new Address[] { new InternetAddress("someone@foo.com") });
		EasyMock.expect(mockMessage.getSubject()).andReturn("butler action");
		EasyMock.expect(mockMessage.getContentType()).andReturn("text/plain");
		EasyMock.expect(mockMessage.getContent()).andReturn(
				"key1      : val1\r\n key2 =    	val2\n--------blaaaa--------");
		mockMessage.setFlag(Flag.DELETED, true);
		EasyMock.expectLastCall();
		EasyMock.replay(mockMessage);
		
		taskMessage = new TaskMessage(mockMessage);
	}
	
	@Test
	public void testGetType() {
		assertEquals("action", taskMessage.getType());
	}

	@Test
	public void testGetFromAddress() {
		assertEquals("someone@foo.com", taskMessage.getFromAddress());
	}

	@Test
	public void testSetFromAddress() {
		TaskMessage tm = taskMessage;
		tm.setFromAddress("someoneelse@foo.com");
		assertEquals("someoneelse@foo.com", tm.getFromAddress());
		
	}

	@Test
	public void testGetContent() {
		Properties expected = new Properties();
		expected.setProperty("key1", "val1");
		expected.setProperty("key2", "val2");
		
		assertEquals(expected, taskMessage.getContent());
	}

	@Test
	public void testIsProcessed() {
		assertEquals(false, taskMessage.isProcessed());
	}

	@Test
	public void testSetProcessed() {
		TaskMessage tm = taskMessage;
		tm.setProcessed(true);
		
		assertEquals(true, tm.isProcessed());
	}

}
