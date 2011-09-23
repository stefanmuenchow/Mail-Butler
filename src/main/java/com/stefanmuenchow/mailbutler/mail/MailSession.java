package com.stefanmuenchow.mailbutler.mail;

import java.util.Properties;

import javax.mail.Message;
import javax.mail.NoSuchProviderException;
import javax.mail.Session;
import javax.mail.Store;
import javax.mail.internet.MimeMessage;

public class MailSession {
    private final Session session;
    
    public MailSession(Properties mailSessionProperties) {
        session = Session.getDefaultInstance(mailSessionProperties);
    }

    public Store getStore(String protocol) throws NoSuchProviderException {
        return session.getStore(protocol);
    }
    
    public Message newMimeMessage() {
        return new MimeMessage(session);
    }
}
