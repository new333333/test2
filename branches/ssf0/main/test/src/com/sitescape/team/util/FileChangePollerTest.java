package com.sitescape.team.util;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.junit.Assert.*;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.support.AbstractTestBase;
import com.sitescape.util.FileChangePoller;

public class FileChangePollerTest extends AbstractTestBase {
	
	private FileChangePoller fcp = new FileChangePoller();
	private File target; 
	
	@Before
	public void setup() throws Exception {
		target = File.createTempFile("tmp", "");
		target.setLastModified(0L);
		fcp.setTarget(target);
		fcp.setLastModified(target.lastModified());
	}
	
	@Test
	public void changeNotification() throws Exception {
		final List<Boolean> ns = new ArrayList<Boolean>();
		EventListener<FileChangePoller, File> l = new EventListener<FileChangePoller, File>() {
			public void onNotification(FileChangePoller source, File event) {
				ns.add(true);
			}};
		fcp.register(l);
		target.setLastModified(1000L);
		fcp.check();
		assertEquals(1, ns.size());		
	}
	
	@Test
	public void updateLastModified() throws Exception {
		final List<Boolean> ns = new ArrayList<Boolean>();
		EventListener<FileChangePoller, File> l = new EventListener<FileChangePoller, File>() {
			public void onNotification(FileChangePoller source, File event) {
				ns.add(true);
			}};
		fcp.register(l);
		target.setLastModified(1000L);
		fcp.check();
		fcp.check(); // intentional duplication!
		assertEquals(1, ns.size());		
	}

}
