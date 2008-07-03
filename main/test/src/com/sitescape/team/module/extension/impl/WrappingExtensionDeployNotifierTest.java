package com.sitescape.team.module.extension.impl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.*;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.team.util.EventListener;
import com.sitescape.team.util.EventSource;
import com.sitescape.util.FileChangePoller;

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
		File f = createMock(File.class);
		expect(f.lastModified()).andReturn(1L).times(2);
		replay(f);
		poller.setTarget(f);
		poller.setLastModified(0L);
		poller.check();
		while(notifications.size() < 1) {
			; // await notification
		}
		assertTrue(true); // notification received
	}
}
