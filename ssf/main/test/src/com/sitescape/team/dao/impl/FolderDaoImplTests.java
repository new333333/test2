/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
package com.sitescape.team.dao.impl;

import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.HistoryStamp;
import com.sitescape.team.domain.NoFolderByTheIdException;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.support.AbstractTestBase;
/**
 * Integration unit tests for data access layer. 
 * 
 * @author Jong Kim
 */
public class FolderDaoImplTests extends AbstractTestBase {
	private static String zoneName ="testZone";
	
	@Autowired(required = true)
	protected FolderDaoImpl folderDao;
	
	/*
	 * This method is provided to set the CoreDaoImpl instance being tested
	 * by the Dependency Injection, which is done automatically by the
	 * superclass.
	 */
	public void setFolderDaoImpl(FolderDaoImpl fdi) {
		this.folderDao = fdi;
	}
	@Test
	public void testAddFolder() {
		Workspace top = setupWorkspace(zoneName).getSecond();
		Folder folder = createFolder(top, "testFolder");
		assertNull(folder.getTopFolder());
		assertNull(folder.getParentFolder());
		assertEquals(folder.getParentBinder(), top);
		assertEquals(folder.getBinderKey().getLevel(), 2);
		assertEquals(folder.getBinderKey().getSortKey(), top.getBinderKey().getSortKey() + "00004");
	}
	@Test
	public void testAddSubFolder() {
		Workspace top = setupWorkspace(zoneName).getSecond();
		Folder folder = createFolder(top, "testFolder");
 		Folder sub = createFolder(folder, "subFolder1");
		assertEquals(sub.getTopFolder(), folder);
		assertEquals(sub.getParentFolder(), folder);
		assertEquals(sub.getBinderKey().getLevel(), 3);
		assertEquals(folder.getBinderKey().getSortKey() + "00001", sub.getBinderKey().getSortKey());
		//add another
		sub = createFolder(folder, "subFolder2");
		assertEquals(sub.getTopFolder(), folder);
		assertEquals(sub.getParentFolder(), folder);
		assertEquals(sub.getBinderKey().getLevel(), 3);
		assertEquals(folder.getBinderKey().getSortKey() + "00002", sub.getBinderKey().getSortKey());

		Folder sub2 = createFolder(sub, "subFolder2.1");
		assertEquals(sub2.getTopFolder(), folder);
		assertEquals(sub2.getParentFolder(), sub);
		assertEquals(sub2.getBinderKey().getLevel(), 4);
		assertEquals(sub.getBinderKey().getSortKey() + "00001", sub2.getBinderKey().getSortKey());
		assertEquals(folder.getBinderKey().getSortKey() + "0000200001", sub2.getBinderKey().getSortKey());

		sub2 = createFolder(sub, "subFolder2.2");
		assertEquals(sub2.getTopFolder(), folder);
		assertEquals(sub2.getParentFolder(), sub);
		assertEquals(sub2.getBinderKey().getLevel(), 4);
		assertEquals(sub.getBinderKey().getSortKey() + "00002", sub2.getBinderKey().getSortKey());
		assertEquals(folder.getBinderKey().getSortKey() + "0000200002", sub2.getBinderKey().getSortKey());
		FilterControls fc = new FilterControls("topFolder", folder);
		
		RequestContext mrc = fakeRequestContext();
		expect(mrc.getZoneId()).andStubReturn(top.getZoneId());
		replay(mrc);
		
		assertEquals(coreDao.countObjects(Folder.class, fc, top.getZoneId()), 4);
	}
	@Test
	public void testFindFolderById() {
		Workspace top = setupWorkspace(zoneName).getSecond();
		Folder folder = createFolder(top, "testFolder");
		coreDao.clear();
		Folder f = folderDao.loadFolder(folder.getId(), top.getZoneId());
		assertEquals(f, folder);
		assertEquals(f.getName(),"testFolder"); 
	}
	
	@Test
	public void testFindFolderNoFolderByTheIdException() {
		// Test three slightly different cases: It throws the same exception
		// for all cases. Throwing different exception for each case will make
		// the database lookup more expensive, so it's not worth it. 
		Workspace top = setupWorkspace(zoneName).getSecond();
		Folder folder = createFolder(top, "testFolder");
		
		// Test the situation where zone exists but folder does not. 
		try {
			folderDao.loadFolder(Long.valueOf(-1), top.getZoneId());			
			fail("Should throw NoFolderByTheIdException");
		}
		catch(NoFolderByTheIdException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where folder exists but zone doesn't.
		try {
			folderDao.loadFolder(folder.getId(), Long.valueOf(-1));			
			fail("Should throw NoFolderByTheIdException");
		}
		catch(NoFolderByTheIdException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where folder and zone don't exist
		try {
			folderDao.loadFolder(Long.valueOf(-1), Long.valueOf(-1));			
			fail("Should throw NoFolderByTheIdException");
		}
		catch(NoFolderByTheIdException e) {
			assertTrue(true); // Ok
		}
	}
	
	/**
	 * Create a folderEntr with some custom attributes
	 * Verify attributes exist
	 *
	 */
	@Test
	public void testAddEntry() {
		Workspace top = setupWorkspace(zoneName).getSecond();
		Folder folder = createFolder(top, "testFolder");
		int oldCount = folder.getNextEntryNumber();
 		FolderEntry entry = createBaseEntry(folder);
		assertEquals(folder.getNextEntryNumber(), oldCount+1);

		FilterControls fc = new FilterControls("owner.folderEntry", entry);
		//make sure attributes are there
		if (coreDao.countObjects(CustomAttribute.class, fc, top.getZoneId()) != entry.getCustomAttributes().size())
			fail("Custom attributes missing");
		if (coreDao.countObjects(Attachment.class, fc, top.getZoneId()) != entry.getAttachments().size())
			fail("Attachments missing");
		 
		if (coreDao.countObjects(Event.class, fc, top.getZoneId()) != entry.getEvents().size())
			fail("Events missing");
		if (coreDao.countObjects(WorkflowState.class, fc, top.getZoneId()) != entry.getWorkflowStates().size())
			fail("WorkflowStates missing");
	}
	
	@Test
	public void testLoadFolderEntryAndLazyLoading() {
		// phase1: Load it. 
		Workspace top = setupWorkspace(zoneName).getSecond();
		Folder folder = createFolder(top, "testFolder");
		FolderEntry entry = createBaseEntry(folder);
		//clear session
		coreDao.clear();
		assertNotNull(folderDao.loadFolderEntry(folder.getId(), entry.getId(), top.getZoneId()));
	}
	
	/**
	 * Test deleteing users with various associations.
	 * Ensure associations are deleted.
	 * This test uses hibernate delete
	 *
	 */
	@Test
	public void testDeleteEntry() {
		Workspace top = setupWorkspace("testZone").getSecond();
		Folder folder = createFolder(top, "testFolder");
		FolderEntry entry = createBaseEntry(folder);
		
		//delete as a hibernate object - will delete all associations with cascade=delete-all-orphan
		coreDao.delete((Object)entry);
		//make sure attributes are gone
		checkDeleted(entry);
		
	}
	/**
	 * Test folderDao.delete of a list of users
	 *
	 */
	@Test
	public void testDeleteEntries() {
		Workspace top = setupWorkspace("testZone").getSecond();
		Folder folder = createFolder(top, "testFolder");
		List entries = fillFolderEntries(folder);
		
		//have to clear session cause we are bypassing hibernate cascade.
		coreDao.clear();
		folderDao.deleteEntries(folder, entries);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((FolderEntry)entries.get(i));
		}
		
	}
	/**
	 * Delete the profile binder and all its entries.
	 * Test profileDao.deleteEntries and delete of the binder
	 *
	 */
	@Test
	public void testDeleteFolder() {
		Workspace top = setupWorkspace("testZone").getSecond();
		Folder folder = createFolder(top, "testFolder");
		List entries = fillFolderEntries(folder);
		profileDao.loadUserProperties(Long.valueOf(0), folder.getId());
		//have to clear session cause we are bypassing hibernate cascade.
		coreDao.clear();
		
		folderDao.delete(folder);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((FolderEntry)entries.get(i));
		}
		
	}

	private Folder createFolder(Workspace top, String name) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneId(top.getId());
		coreDao.save(folder);
		top.addBinder(folder);
		return folder;
		
	}
	private Folder createFolder(Folder top, String name) {
		RequestContext mrc = fakeRequestContext();
		expect(mrc.getZoneId()).andReturn(top.getZoneId());
		replay(mrc);
		
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneId(top.getZoneId());
		coreDao.save(folder);
		assertNotNull(folder.getId());
		top.addFolder(folder);
		folder.addCustomAttribute("aString", "I am a string");
		String vals[] = new String[] {"red", "white", "blue"};
		folder.addCustomAttribute("aList", vals);
		Event event = new Event();
		coreDao.save(event);
		assertNotNull(event.getId());
		folder.addCustomAttribute("anEvent", event);
		assertNotNull(folder.getCustomAttribute("aString"));
		assertNotNull(folder.getCustomAttribute("anEvent"));
		Set sVal = (Set)folder.getCustomAttribute("aList").getValue();
		assertEquals(sVal.toArray(vals), vals);
		return folder;
		
	}
	
	private FolderEntry createBaseEntry(Folder top) {
		RequestContext mrc = fakeRequestContext();
		expect(mrc.getZoneId()).andStubReturn(top.getZoneId());
		replay(mrc);
		
		FolderEntry entry = new FolderEntry();
		top.addEntry(entry);
		//add some attributes
		entry.addCustomAttribute("aString", "I am a string");
		String vals[] = new String[] {"red", "white", "blue"};
		entry.addCustomAttribute("aList", vals);
		Event event = new Event();
		coreDao.save(event);
		assertNotNull(event.getId());
		entry.addCustomAttribute("anEvent", event);
		coreDao.save(entry);
		assertNotNull(entry.getId());
		assertNotNull(entry.getCustomAttribute("aString"));
		assertNotNull(entry.getCustomAttribute("anEvent"));
		Set sVal = (Set)entry.getCustomAttribute("aList").getValue();
		assertEquals(sVal.toArray(vals), vals);
		return entry;
		
	}
	private FolderEntry createBaseEntry(FolderEntry top) {
		FolderEntry entry = new FolderEntry();
		top.addReply(entry);
		//add some attributes
		entry.addCustomAttribute("aString", "I am a string");
		Set dVals = new HashSet();
		dVals.add(new Date());
		dVals.add(new Date());
		dVals.add(new Date());
		entry.addCustomAttribute("aDateList", dVals);
		Set eVals = new HashSet();
		HistoryStamp stamp = new HistoryStamp();
		stamp.setDate(new Date());
		for (int i=0; i<4; ++i) {
			Event event = new Event();
			event.setCreation(stamp); //needed for setValue ordering
			coreDao.save(event);
			eVals.add(event);
			assertNotNull(event.getId());
		}
		entry.addCustomAttribute("anEventList", eVals);
		coreDao.save(entry);
		assertNotNull(entry.getId());
		assertNotNull(entry.getCustomAttribute("aString"));
		assertEquals(entry.getCustomAttribute("anEventList").getValueSet(), eVals);
		assertEquals(entry.getCustomAttribute("aDateList").getValueSet(), dVals);
		return entry;
		
	}
	
	private List fillFolderEntries(Folder top) {
		List entries = new ArrayList();
		FolderEntry e1 = createBaseEntry(top);
		entries.add(e1);
		//add 2 replies
		entries.add(createBaseEntry(e1));
		entries.add(createBaseEntry(e1));

		//add reply with 2 replies of its own
		FolderEntry e2 = createBaseEntry(top);
		entries.add(e2);
		FolderEntry e2r1 = createBaseEntry(e2);
		entries.add(e2r1);
		entries.add(createBaseEntry(e2r1));
		entries.add(createBaseEntry(e2r1));
		entries.add(createBaseEntry(e2r1));

		FolderEntry e3 = createBaseEntry(top);
		entries.add(e3);
			
		return entries;
	}
	private void checkDeleted(FolderEntry e) {
		Long zoneId = e.getParentFolder().getZoneId();
		FilterControls fc = new FilterControls("owner.folderEntry", e);
		if (coreDao.countObjects(CustomAttribute.class, fc, zoneId) != 0)
			fail("Custom attributes not deleted from entry " + e.getId());
		if (coreDao.countObjects(Attachment.class, fc, zoneId) != 0)
			fail("Attachments not deleted from entry " + e.getId());
		if (coreDao.countObjects(Event.class, fc, zoneId) != 0)
			fail("Events not deleted from entry " + e.getId());
		if (coreDao.countObjects(WorkflowState.class, fc, zoneId) != 0)
			fail("WorkflowStates not deleted from entry " + e.getId());
		
	}
}
