package com.stefanmuenchow.mailbutler.mail;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.mail.NoSuchProviderException;
import javax.mail.internet.MimeMessage;

import org.junit.Test;

public class MailSessionTest {
    private MailSession session;
    
    public MailSessionTest() {
        session = new MailSession(new Properties());
    }

    @Test
    public void testGetStore() throws NoSuchProviderException {
        assertNotNull(session.getStore("pop3"));
    }
    
    @Test
    public void testNewMimeMessage() {
        assertNotNull(session.newMimeMessage());
        assertEquals(MimeMessage.class, session.newMimeMessage().getClass());
    }
}
