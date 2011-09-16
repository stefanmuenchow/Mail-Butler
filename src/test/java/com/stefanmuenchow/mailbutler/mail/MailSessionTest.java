package com.stefanmuenchow.mailbutler.mail;

import static org.junit.Assert.*;

import java.util.Properties;

import javax.mail.NoSuchProviderException;

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
}
