package com.stefanmuenchow.mailbutler.mail;

import javax.mail.Address;
import javax.mail.Folder;
import javax.mail.Message;
import javax.mail.Store;
import javax.mail.internet.InternetAddress;

import org.easymock.EasyMock;
import org.junit.Test;

import com.stefanmuenchow.mailbutler.plugin.Plugin;
import com.stefanmuenchow.mailbutler.plugin.PluginRepository;

public class MailDaemonTest {
    private ButlerConfiguration configMock;
    private PluginRepository pluginRepoMock;
    private MailSession sessionMock;

    public MailDaemonTest() throws Exception {
        /* Mock ButlerConfiguration */
        configMock = EasyMock.createMock(ButlerConfiguration.class);
        EasyMock.expect(configMock.getHost()).andReturn("host").anyTimes();
        EasyMock.expect(configMock.getUser()).andReturn("user").anyTimes();
        EasyMock.expect(configMock.getPassword()).andReturn("password").anyTimes();
        EasyMock.expect(configMock.getProtocol()).andReturn("protocol").anyTimes();
        EasyMock.expect(configMock.getInboxName()).andReturn("inboxName").anyTimes();
        EasyMock.expect(configMock.getFetchCycleInMs()).andReturn(500l).anyTimes();
        EasyMock.replay(configMock);
        
        /* Mock parts of javax.mail API */
        Message messageMock = EasyMock.createMock(Message.class);
        EasyMock.expect(messageMock.getSubject()).andReturn("butler test").anyTimes();
        EasyMock.expect(messageMock.getContent()).andReturn("").anyTimes();
        EasyMock.expect(messageMock.getFrom())
                .andReturn(new Address[] { new InternetAddress("someone@foo.com") }).anyTimes();
        EasyMock.replay(messageMock);
        
        Folder folderMock = EasyMock.createMock(Folder.class);
        EasyMock.expect(folderMock.getMessages()).andReturn(new Message[] { messageMock }).times(1, 2);
        folderMock.open(Folder.READ_WRITE);
        EasyMock.expectLastCall().times(1, 2);
        folderMock.close(true);
        EasyMock.expectLastCall().times(1, 2);
        EasyMock.replay(folderMock);
        
        Store storeMock = EasyMock.createMock(Store.class);
        EasyMock.expect(storeMock.getFolder(configMock.getInboxName())).andReturn(folderMock).times(1, 2);
        storeMock.connect(configMock.getHost(), configMock.getUser(), configMock.getPassword());
        EasyMock.expectLastCall().times(1, 2);
        storeMock.close();
        EasyMock.expectLastCall().times(1, 2);
        EasyMock.replay(storeMock);
        
        sessionMock = EasyMock.createMock(MailSession.class);
        EasyMock.expect(sessionMock.getStore(configMock.getProtocol())).andReturn(storeMock).times(1, 2);
        EasyMock.replay(sessionMock);
        
        /* Mock PluginRepository and Plugin */
        TaskMessage taskMessage = new TaskMessage(messageMock);
        
        Plugin pluginMock = EasyMock.createMock(Plugin.class);
        pluginMock.process(taskMessage);
        EasyMock.expectLastCall().times(1, 2);
        EasyMock.replay(pluginMock);
        
        pluginRepoMock = EasyMock.createMock(PluginRepository.class);
        EasyMock.expect(pluginRepoMock.getPlugin(taskMessage)).andReturn(pluginMock).times(1, 2);
        EasyMock.replay(pluginRepoMock);
    }

    @Test
    public void testRun() throws Exception {
        MailDaemon daemon = new MailDaemon(configMock, pluginRepoMock, sessionMock);
        Thread daemonThread = new Thread(daemon);
        daemonThread.start();
        
        sleep();
        
        daemonThread.interrupt();
        daemonThread.join();
        EasyMock.verify(configMock, pluginRepoMock, sessionMock);
    }

    private void sleep() {
        try {
            Thread.sleep(1000l);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
