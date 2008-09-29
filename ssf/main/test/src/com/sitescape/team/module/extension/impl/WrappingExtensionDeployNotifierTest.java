package com.sitescape.team.module.extension.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.EventListener;
import com.sitescape.team.util.EventSource;
import com.sitescape.team.util.FileChangePoller;

public class WrappingExtensionDeployNotifierTest<S extends EventSource<S, File>> extends AbstractTestBase {
	
	@Autowired(required = true)
	private WrappingExtensionDeployNotifier<S> notifier;
	@Autowired(required = true)
	private FileChangePoller poller;
	
	@Test(timeout = 1000)
	public void wrapsNotifications() throws Exception {
		final List<Boolean> notifications = new ArrayList<Boolean>();
		EventListener<WrappingExtensionDeployNotifier<S>, File> listener = new EventListener<WrappingExtensionDeployNotifier<S>, File>() {
			public void onNotification(
					WrappingExtensionDeployNotifier<S> source, File event) {
				notifications.add(true);				
			}			
		};
		notifier.register(listener);
		File d = createMock(File.class);
		File f = createMock(File.class);
		expect(d.lastModified()).andReturn(1L).times(2);
		expect(d.isDirectory()).andStubReturn(true);
		expect(d.listFiles(isA(FileFilter.class))).andReturn(new File[]{f});
		expect(f.isDirectory()).andReturn(false);
		expect(f.lastModified()).andReturn(1L);
		expect(f.getPath()).andReturn("wrapsNotifications").times(3);
		replay(d);
		replay(f);
		poller.setTarget(d);
		poller.setLastModified(0L);
		poller.check();
		while(notifications.size() < 1) {
			; // await notification
		}
		assertTrue(true); // notification received
	} 
}
