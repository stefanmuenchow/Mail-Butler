package com.stefanmuenchow.mailbutler.plugin;

import static org.junit.Assert.*;

import java.io.File;
import java.lang.Thread.State;
import java.util.Arrays;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.stefanmuenchow.mailbutler.mail.TaskMessage;

public class PluginRepositoryTest {
    private final String pluginDir = "";
    private final PluginRepository pluginRepo;
    private TaskMessage taskMsgMock;
    
    public PluginRepositoryTest() {
        pluginRepo = new PluginRepository(pluginDir);
    }
    
    @Before
    public void setUp() throws InterruptedException {
        taskMsgMock = EasyMock.createMock(TaskMessage.class);
        EasyMock.replay(taskMsgMock);
    }

    @Test
    public void testGetPluginBlock() throws InterruptedException {
        Thread thread = startGetPluginThread();
        sleep();
        
        assertEquals(State.WAITING, thread.getState());
        
        interruptGetPluginThread(thread);
    }

    private void interruptGetPluginThread(Thread thread) throws InterruptedException {
        thread.interrupt();
        thread.join();
    }

    private void sleep() {
        try {
            Thread.sleep(200);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private Thread startGetPluginThread() {
        Runnable runnable = new Runnable() {
            public void run() {
                pluginRepo.getPlugin(taskMsgMock);
            }
        };
        
        Thread thread = new Thread(runnable);
        thread.start();
        return thread;
    }

    @Test
    public void testLoadPlugins() {
        PluginConfiguration conf = new PluginConfiguration(pluginDir + File.separator + "");
        pluginRepo.loadPlugins(Arrays.asList(new PluginConfiguration[] { conf }));
    }
}
