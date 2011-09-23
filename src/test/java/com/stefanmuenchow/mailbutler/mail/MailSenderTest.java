package com.stefanmuenchow.mailbutler.mail;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.stefanmuenchow.mailbutler.exception.ButlerException;
import com.stefanmuenchow.mailbutler.exception.ButlerException.ErrorCode;

public class MailSenderTest {
    private MailSession mailSessionMock;
    private Message messageMock;

    public MailSenderTest() throws Exception {
        mailSessionMock = EasyMock.createMock(MailSession.class);
        messageMock = EasyMock.createMock(MimeMessage.class);
        EasyMock.expect(mailSessionMock.newMimeMessage()).andReturn(messageMock);
        messageMock.setFrom(new InternetAddress("butler@home.com"));
        EasyMock.expectLastCall();
        messageMock.setRecipients(EasyMock.eq(Message.RecipientType.TO),
                EasyMock.anyObject(InternetAddress[].class));
        EasyMock.expectLastCall();
        messageMock.setSubject("The subject");
        EasyMock.expectLastCall();
        messageMock.setContent("The content", "text/plain");
        EasyMock.expectLastCall();
        messageMock.saveChanges();
        EasyMock.expectLastCall();
        EasyMock.expect(messageMock.getAllRecipients()).andReturn(
                new InternetAddress[] { new InternetAddress("soneone@foo.com") });

        EasyMock.replay(mailSessionMock);
        EasyMock.replay(messageMock);
    }

    @Before
    public void setUp() {
        MailSender.setDefaultInstance(null);
    }

    @Test
    public void testGetDefaultInstance() {
        MailSender.setDefaultInstance(new MailSender(mailSessionMock));
        assertNotNull(MailSender.getDefaultInstance());
    }

    @Test(expected = ButlerException.class)
    public void testSetDefaultInstanceFail() {
        MailSender.getDefaultInstance();
    }

    @Test
    public void testSend() {
        MailSender.setDefaultInstance(new MailSender(mailSessionMock));

        try {
            MailSender.getDefaultInstance().send("The subject", "The content", "butler@home.com",
                    "someone@foo.com");
            fail();
        } catch (ButlerException e) {
            assertEquals(ErrorCode.MESSAGE_SEND_FAILURE, e.getErrorCode());
        }
        
        EasyMock.verify(messageMock, mailSessionMock);
    }
}
