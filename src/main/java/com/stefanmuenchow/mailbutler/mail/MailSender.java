package com.stefanmuenchow.mailbutler.mail;

import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Transport;
import javax.mail.internet.AddressException;
import javax.mail.internet.InternetAddress;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;

public class MailSender {
    private static MailSender instance = null;
    private final MailSession mailSession;
    
    public static MailSender getDefaultInstance() {
        if (instance == null) {
            throw new ButlerException(ErrorCode.SENDER_NOT_INITIATED);
        }
        
        return instance;
    }
    
    public static void setDefaultInstance(MailSender instance) {
        MailSender.instance = instance;
    }
    
    public MailSender(MailSession mailSession) {
        this.mailSession = mailSession;
    }
    
    public void send(String subject, String content, String sender, String... recipients) {
        try {
            Message message = mailSession.newMimeMessage();
            message.setFrom(new InternetAddress(sender));

            InternetAddress[] recipientAdresses = new InternetAddress[recipients.length];
            for (int i = 0; i < recipients.length; i++) {
                recipientAdresses[i] = new InternetAddress(recipients[i]);
            }
            
            message.setRecipients(Message.RecipientType.TO, recipientAdresses);
            message.setSubject(subject);
            message.setContent(content, "text/plain");
            Transport.send(message);
        } catch (AddressException e) {
            throw new ButlerException(ErrorCode.INVALID_ADDRESS, e);
        } catch (MessagingException e) {
            throw new ButlerException(ErrorCode.MESSAGE_SEND_FAILURE, e);
        }
    }
}
