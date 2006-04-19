package com.sitescape.ef.dao.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Date;

import org.hibernate.LazyInitializationException;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.NoFolderByTheIdException;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Workspace;
import com.sitescape.ef.domain.Folder;
import com.sitescape.ef.domain.FolderEntry;

/**
 * Integration unit tests for data access layer. 
 * 
 * @author Jong Kim
 */
public class FolderDaoImplTests extends AbstractTransactionalDataSourceSpringContextTests {

	protected CoreDaoImpl cdi;
	protected FolderDaoImpl fdi;
	private static String zoneName ="testZone";
	private static String adminGroup = "administrators";
	private static String adminUser = "administrator";
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/ef/dao/impl/applicationContext-folderdao.xml"};
	}
	
	/*
	 * This method is provided to set the CoreDaoImpl instance being tested
	 * by the Dependency Injection, which is done automatically by the
	 * superclass.
	 */
	public void setCoreDaoImpl(CoreDaoImpl cdi) {
		this.cdi = cdi;
	}
	
	public void setFolderDaoImpl(FolderDaoImpl fdi) {
		this.fdi = fdi;
	}
	public void testAddFolder() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		assertEquals(folder.getNextFolderNumber(), 1);
		assertNull(folder.getTopFolder());
		assertNull(folder.getParentFolder());
		assertEquals(folder.getParentBinder(), top);
		assertEquals(folder.getFolderHKey().getLevel(), 1);
		assertEquals(folder.getFolderHKey().getSortKey(), folder.getEntryRootHKey().getSortKey() + "00001");
	}
	public void testAddSubFolder() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		int oldCount = folder.getNextFolderNumber();
 		Folder sub = createFolder(folder, "subFolder1");
		assertEquals(folder.getNextFolderNumber(), oldCount+1);
		assertEquals(sub.getTopFolder(), folder);
		assertEquals(sub.getParentFolder(), folder);
		assertEquals(sub.getFolderHKey().getLevel(), 2);
		assertEquals(folder.getFolderHKey().getSortKey() + "0001", sub.getFolderHKey().getSortKey());
		//add another
		oldCount = folder.getNextFolderNumber();
		sub = createFolder(folder, "subFolder2");
		assertEquals(folder.getNextFolderNumber(), oldCount+1);
		assertEquals(sub.getTopFolder(), folder);
		assertEquals(sub.getParentFolder(), folder);
		assertEquals(sub.getFolderHKey().getLevel(), 2);
		assertEquals(folder.getFolderHKey().getSortKey() + "0002", sub.getFolderHKey().getSortKey());

		oldCount = sub.getNextFolderNumber();
		Folder sub2 = createFolder(sub, "subFolder2.1");
		assertEquals(sub.getNextFolderNumber(), oldCount+1);
		assertEquals(sub2.getTopFolder(), folder);
		assertEquals(sub2.getParentFolder(), sub);
		assertEquals(sub2.getFolderHKey().getLevel(), 3);
		assertEquals(sub.getFolderHKey().getSortKey() + "0001", sub2.getFolderHKey().getSortKey());
		assertEquals(folder.getFolderHKey().getSortKey() + "00020001", sub2.getFolderHKey().getSortKey());

		oldCount = sub.getNextFolderNumber();
		sub2 = createFolder(sub, "subFolder2.2");
		assertEquals(sub.getNextFolderNumber(), oldCount+1);
		assertEquals(sub2.getTopFolder(), folder);
		assertEquals(sub2.getParentFolder(), sub);
		assertEquals(sub2.getFolderHKey().getLevel(), 3);
		assertEquals(sub.getFolderHKey().getSortKey() + "0002", sub2.getFolderHKey().getSortKey());
		assertEquals(folder.getFolderHKey().getSortKey() + "00020002", sub2.getFolderHKey().getSortKey());
		FilterControls fc = new FilterControls("topFolder", folder);
		assertEquals(cdi.countObjects(Folder.class, fc), 4);
	}
	public void testFindFolderById() {
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		cdi.clear();
		Folder f = fdi.loadFolder(folder.getId(), zoneName);
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
			fdi.loadFolder(Long.valueOf(-1), zoneName);			
			fail("Should throw NoFolderByTheIdException");
		}
		catch(NoFolderByTheIdException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where folder exists but zone doesn't.
		try {
			fdi.loadFolder(folder.getId(), "nonExistingZone");			
			fail("Should throw NoFolderByTheIdException");
		}
		catch(NoFolderByTheIdException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where folder and zone don't exist
		try {
			fdi.loadFolder(Long.valueOf(-1), "nonExistingZone");			
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
		if (cdi.countObjects(CustomAttribute.class, fc) != entry.getCustomAttributes().size())
			fail("Custom attributes missing");
		if (cdi.countObjects(Attachment.class, fc) != entry.getAttachments().size())
			fail("Attachments missing");
		 
		if (cdi.countObjects(Event.class, fc) != entry.getEvents().size())
			fail("Events missing");
		if (cdi.countObjects(WorkflowState.class, fc) != entry.getWorkflowStates().size())
			fail("WorkflowStates missing");
	}
	
	public void testLoadFolderEntryAndLazyLoading() {
		// phase1: Load it. 
		Workspace top = createZone(zoneName);
		Folder folder = createFolder(top, "testFolder");
		FolderEntry entry = createBaseEntry(folder);
		//clear session
		cdi.clear();
		FolderEntry partial = fdi.loadFolderEntry(folder.getId(), entry.getId(), zoneName);
		
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
		fdi.deleteEntries(entries);
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
		fdi.loadUserFolderProperties(Long.valueOf(0), folder.getId());
		//have to clear session cause we are bypassing hibernate cascade.
		cdi.clear();
		
		fdi.deleteEntries(folder);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((FolderEntry)entries.get(i));
		}
		fdi.delete(folder);
		
	}
	private Workspace createZone(String name) {
		Workspace top;
		try { 
			top = cdi.findTopWorkspace(name);
		} catch (NoWorkspaceByTheNameException nw) {
			top = new Workspace();
			top.setName(name);
			top.setZoneName(name);
			cdi.save(top);
			ProfileBinder profiles = new ProfileBinder();
			profiles.setName("_profiles");
			profiles.setZoneName(name);
			profiles.setParentBinder(top);
			//	generate id for top
			cdi.save(profiles);
			Group group = new Group();
			group.setName(adminGroup);
			group.setZoneName(name);
			group.setParentBinder(profiles);
			cdi.save(group);
			User user = new User();
			user.setName(adminUser);
			user.setZoneName(name);
			user.setParentBinder(profiles);
			cdi.save(user);
			group.addMember(user);
			cdi.flush();
			
			top = cdi.findTopWorkspace(name);
			assertEquals(top.getName(), name);
		}
		return top;
		
	}
	private Folder createFolder(Workspace top, String name) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneName(top.getZoneName());
		cdi.save(folder);
		top.addBinder(folder);
		return folder;
		
	}
	private Folder createFolder(Folder top, String name) {
		Folder folder = new Folder();
		folder.setName(name);
		folder.setZoneName(top.getZoneName());
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
		for (int i=0; i<4; ++i) {
			Event event = new Event();
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
		FilterControls fc = new FilterControls("owner.folderEntry", e);
		if (cdi.countObjects(CustomAttribute.class, fc) != 0)
			fail("Custom attributes not deleted from entry " + e.getId());
		if (cdi.countObjects(Attachment.class, fc) != 0)
			fail("Attachments not deleted from entry " + e.getId());
		if (cdi.countObjects(Event.class, fc) != 0)
			fail("Events not deleted from entry " + e.getId());
		if (cdi.countObjects(WorkflowState.class, fc) != 0)
			fail("WorkflowStates not deleted from entry " + e.getId());
		
	}
}
