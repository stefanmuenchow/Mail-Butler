package com.stefanmuenchow.mailbutler.plugin;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.easymock.EasyMock;
import org.junit.Before;
import org.junit.Test;

import com.stefanmuenchow.mailbutler.mail.TaskMessage;

public class PluginRepositoryTest {
    private final String pluginDir = "src/test/resources/plugins";
    private final String pluginFile = "testPlugin.xml";
    private PluginRepository pluginRepo;
    private TaskMessage taskMsgMock;
    
    @Before
    public void setUp() throws InterruptedException {
        pluginRepo = new PluginRepository(pluginDir);
        taskMsgMock = EasyMock.createMock(TaskMessage.class);
        EasyMock.expect(taskMsgMock.getPluginName()).andReturn("test");
        EasyMock.expect(taskMsgMock.getFromAddress()).andReturn("someone@foo.com");
        EasyMock.replay(taskMsgMock);
    }
    
    @Test
    public void testAddPlugin() {
        PluginConfiguration pluginConfigMock = EasyMock.createMock(PluginConfiguration.class);
        EasyMock.expect(pluginConfigMock.getPluginName()).andReturn("test");
        List<String> allowed = Arrays.asList(new String[] { "someone@foo.com" });
        EasyMock.expect(pluginConfigMock.getAllowedUsers()).andReturn(allowed);
        Plugin pluginMock = EasyMock.createMock(Plugin.class);
        EasyMock.replay(pluginConfigMock);
        EasyMock.replay(pluginMock);
        
        pluginRepo.addPlugin(pluginConfigMock, pluginMock);
        assertNotNull(pluginRepo.getPlugin(taskMsgMock));
        EasyMock.verify(pluginConfigMock);
    }

    @Test
    public void testGetPluginNotAllowed() {
        PluginConfiguration pluginConfigMock = EasyMock.createMock(PluginConfiguration.class);
        EasyMock.expect(pluginConfigMock.getPluginName()).andReturn("test");
        List<String> allowed = Arrays.asList(new String[] { "someone@bar.com" });
        EasyMock.expect(pluginConfigMock.getAllowedUsers()).andReturn(allowed);
        Plugin pluginMock = EasyMock.createMock(Plugin.class);
        EasyMock.replay(pluginConfigMock);
        EasyMock.replay(pluginMock);
        
        pluginRepo.addPlugin(pluginConfigMock, pluginMock);
        assertNull(pluginRepo.getPlugin(taskMsgMock));
    }
    
    @Test
    public void testGetPluginNotAvailable() {
        assertNull(pluginRepo.getPlugin(taskMsgMock));
        EasyMock.verify(taskMsgMock);
    }
    
    @Test
    public void testLoadPlugins() {
        PluginConfiguration conf = new PluginConfiguration(pluginDir + File.separator + pluginFile);
        pluginRepo.loadPlugins(Arrays.asList(new PluginConfiguration[] { conf }));
        
        assertNotNull(pluginRepo.getPlugin(taskMsgMock));
    }
}
