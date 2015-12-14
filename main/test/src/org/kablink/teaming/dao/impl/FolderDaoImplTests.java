/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.dao.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.hibernate.LazyInitializationException;
import org.kablink.teaming.support.AbstractTestBase;

import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.FolderEntry;
import org.kablink.teaming.domain.HistoryStamp;
import org.kablink.teaming.domain.NoFolderByTheIdException;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;
/**
 * Integration unit tests for data access layer. 
 * 
 * @author Jong Kim
 */
public class FolderDaoImplTests extends AbstractTestBase {
	private static String zoneName ="testZone";
	protected FolderDaoImpl fdi;
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/team/dao/impl/applicationContext-folderdao.xml"};
	}
	
	/*
	 * This method is provided to set the CoreDaoImpl instance being tested
	 * by the Dependency Injection, which is done automatically by the
	 * superclass.
	 */
	public void setFolderDaoImpl(FolderDaoImpl fdi) {
		this.fdi = fdi;
	}
	public void testAddFolder() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		assertNull(folder.getTopFolder());
		assertNull(folder.getParentFolder());
		assertEquals(folder.getParentBinder(), top);
		assertEquals(folder.getBinderKey().getLevel(), 2);
		assertEquals(folder.getBinderKey().getSortKey(), top.getBinderKey().getSortKey() + "00004");
	}
	public void testAddSubFolder() {
		Workspace top = createZone(zoneName);
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
		assertEquals(cdi.countObjects(Folder.class, fc, top.getZoneId()), 4);
	}
	public void testFindFolderById() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		cdi.clear();
		Folder f = fdi.loadFolder(folder.getId(), top.getZoneId());
		assertEquals(f, folder);
		assertEquals(f.getName(),"testFolder"); 
	}
	
	public void testFindFolderNoFolderByTheIdException() {
		// Test three slightly different cases: It throws the same exception
		// for all cases. Throwing different exception for each case will make
		// the database lookup more expensive, so it's not worth it. 
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		
		// Test the situation where zone exists but folder does not. 
		try {
			fdi.loadFolder(Long.valueOf(-1), top.getZoneId());			
			fail("Should throw NoFolderByTheIdException");
		}
		catch(NoFolderByTheIdException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where folder exists but zone doesn't.
		try {
			fdi.loadFolder(folder.getId(), Long.valueOf(-1));			
			fail("Should throw NoFolderByTheIdException");
		}
		catch(NoFolderByTheIdException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where folder and zone don't exist
		try {
			fdi.loadFolder(Long.valueOf(-1), Long.valueOf(-1));			
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
	public void testAddEntry() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		int oldCount = folder.getNextEntryNumber();
 		FolderEntry entry = createBaseEntry(folder);
		assertEquals(folder.getNextEntryNumber(), oldCount+1);

		FilterControls fc = new FilterControls("owner.folderEntry", entry);
		//make sure attributes are there
		if (cdi.countObjects(CustomAttribute.class, fc, top.getZoneId()) != entry.getCustomAttributes().size())
			fail("Custom attributes missing");
		if (cdi.countObjects(Attachment.class, fc, top.getZoneId()) != entry.getAttachments().size())
			fail("Attachments missing");
		 
		if (cdi.countObjects(Event.class, fc, top.getZoneId()) != entry.getEvents().size())
			fail("Events missing");
		if (cdi.countObjects(WorkflowState.class, fc, top.getZoneId()) != entry.getWorkflowStates().size())
			fail("WorkflowStates missing");
	}
	
	public void testLoadFolderEntryAndLazyLoading() {
		// phase1: Load it. 
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		FolderEntry entry = createBaseEntry(folder);
		//clear session
		cdi.clear();
		FolderEntry partial = fdi.loadFolderEntry(folder.getId(), entry.getId(), top.getZoneId());
		
		// phase2: Test lazy loading, by ending the transation (it rolls back).
		// Here we expect LazyInitializationException from Hibernate because
		// the session is already closed. If we had open-session-in-view
		// setup, lazy loading would have worked. But that is not the case here.
		endTransaction();
		try {
			Map customAttrs = partial.getCustomAttributes();
			for(Iterator i = customAttrs.entrySet().iterator(); i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				CustomAttribute val = (CustomAttribute)ent.getValue();
				val.getValue();
				System.out.println(val);
			}
			// If you're still here, something's wrong. 
			fail("Should throw LazyInitializationException");
		}
		catch (LazyInitializationException e) {
			assertTrue(true); // As expected
		}
	}
	
	/**
	 * Test deleteing users with various associations.
	 * Ensure associations are deleted.
	 * This test uses hibernate delete
	 *
	 */
	public void testDeleteEntry() {
		Workspace top = createZone("testZone");
		Folder folder = createFolder(top, "testFolder");
		FolderEntry entry = createBaseEntry(folder);
		
		//delete as a hibernate object - will delete all associations with cascade=delete-all-orphan
		cdi.delete((Object)entry);
		//make sure attributes are gone
		checkDeleted(entry);
		
	}
	/**
	 * Test folderDao.delete of a list of users
	 *
	 */
	public void testDeleteEntries() {
		Workspace top = createZone("testZone");
		Folder folder = createFolder(top, "testFolder");
		List entries = fillFolderEntries(folder);
		
		//have to clear session cause we are bypassing hibernate cascade.
		cdi.clear();
		fdi.deleteEntries(folder, entries);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((FolderEntry)entries.get(i));
		}
		
	}
	/**
	 * Delete the profile binder and all its entries.
	 * Test profileDao.deleteEntries and delete of the binder
	 *
	 */
	public void testDeleteFolder() {
		Workspace top = createZone("testZone");
		Folder folder = createFolder(top, "testFolder");
		List entries = fillFolderEntries(folder);
		pdi.loadUserProperties(Long.valueOf(0), folder.getId());
		//have to clear session cause we are bypassing hibernate cascade.
		cdi.clear();
		
		fdi.delete(folder);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((FolderEntry)entries.get(i));
		}
		
	}

	private Folder createFolder(Workspace top, String name) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneId(top.getId());
		cdi.save(folder);
		top.addBinder(folder);
		return folder;
		
	}
	private Folder createFolder(Folder top, String name) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneId(top.getZoneId());
		cdi.save(folder);
		assertNotNull(folder.getId());
		top.addFolder(folder);
		folder.addCustomAttribute("aString", "I am a string");
		String vals[] = new String[] {"red", "white", "blue"};
		folder.addCustomAttribute("aList", vals);
		Event event = new Event();
		cdi.save(event);
		assertNotNull(event.getId());
		folder.addCustomAttribute("anEvent", event);
		assertNotNull(folder.getCustomAttribute("aString"));
		assertNotNull(folder.getCustomAttribute("anEvent"));
		Set sVal = (Set)folder.getCustomAttribute("aList").getValue();
		assertEquals(sVal.toArray(vals), vals);
		return folder;
		
	}
	
	private FolderEntry createBaseEntry(Folder top) {
		FolderEntry entry = new FolderEntry();
		top.addEntry(entry);
		//add some attributes
		entry.addCustomAttribute("aString", "I am a string");
		String vals[] = new String[] {"red", "white", "blue"};
		entry.addCustomAttribute("aList", vals);
		Event event = new Event();
		cdi.save(event);
		assertNotNull(event.getId());
		entry.addCustomAttribute("anEvent", event);
		cdi.save(entry);
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
			cdi.save(event);
			eVals.add(event);
			assertNotNull(event.getId());
		}
		entry.addCustomAttribute("anEventList", eVals);
		cdi.save(entry);
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
		if (cdi.countObjects(CustomAttribute.class, fc, zoneId) != 0)
			fail("Custom attributes not deleted from entry " + e.getId());
		if (cdi.countObjects(Attachment.class, fc, zoneId) != 0)
			fail("Attachments not deleted from entry " + e.getId());
		if (cdi.countObjects(Event.class, fc, zoneId) != 0)
			fail("Events not deleted from entry " + e.getId());
		if (cdi.countObjects(WorkflowState.class, fc, zoneId) != 0)
			fail("WorkflowStates not deleted from entry " + e.getId());
		
	}
}
