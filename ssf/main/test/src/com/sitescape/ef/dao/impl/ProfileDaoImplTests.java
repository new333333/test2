package com.sitescape.ef.dao.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.LazyInitializationException;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.sitescape.ef.dao.util.FilterControls;
import com.sitescape.ef.domain.FileAttachment;
import com.sitescape.ef.domain.FileItem;
import com.sitescape.ef.domain.Group;
import com.sitescape.ef.domain.NoUserByTheNameException;
import com.sitescape.ef.domain.NoUserByTheIdException;
import com.sitescape.ef.domain.NoWorkspaceByTheNameException;
import com.sitescape.ef.domain.ProfileBinder;
import com.sitescape.ef.domain.Attachment;
import com.sitescape.ef.domain.Event;
import com.sitescape.ef.domain.WorkflowState;
import com.sitescape.ef.domain.Membership;
import com.sitescape.ef.domain.CustomAttribute;
import com.sitescape.ef.domain.User;
import com.sitescape.ef.domain.Principal;
import com.sitescape.ef.domain.SeenMap;
import com.sitescape.ef.domain.UserProperties;
import com.sitescape.ef.domain.Workspace;

/**
 * Integration unit tests for data access layer. 
 * 
 * @author Jong Kim
 */
public class ProfileDaoImplTests extends AbstractTransactionalDataSourceSpringContextTests {

	protected CoreDaoImpl cdi;
	protected ProfileDaoImpl pdi;
	private static String adminGroup = "administrators";
	private static String adminUser = "administrator";
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/ef/dao/impl/applicationContext-coredao.xml"};
	}
	
	/*
	 * This method is provided to set the CoreDaoImpl instance being tested
	 * by the Dependency Injection, which is done automatically by the
	 * superclass.
	 */
	public void setCoreDaoImpl(CoreDaoImpl cdi) {
		this.cdi = cdi;
	}
	
	public void setProfileDaoImpl(ProfileDaoImpl pdi) {
		this.pdi = pdi;
	}
	public void testFindUserByName() {
		User user = pdi.findUserByName("liferay.com.1", "liferay.com");
		assertNotNull(user);
	}
	
	public void testFindUserByNameNoUserByTheNameException() {
		// Test three slightly different cases: It throws the same exception
		// for all cases. Throwing different exception for each case will make
		// the database lookup more expensive, so it's not worth it. 
		
		// Test the situation where zone exists but username does not. 
		try {
			pdi.findUserByName("nonExistingUser", "liferay.com");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where username exists but zone doesn't.
		try {
			pdi.findUserByName("liferay.com.1", "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where username exists but zone doesn't.
		try {
			pdi.findUserByName("nonExistingUser", "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
	}
	
	public void testLoadUserAndLazyLoading() {
		// phase1: Load it. 
		User user = pdi.loadUser(new Long(59), "liferay.com");
		assertNotNull(user);
		
		// phase2: Test lazy loading, by ending the transation (it rolls back).
		// Here we expect LazyInitializationException from Hibernate because
		// the session is already closed. If we had open-session-in-view
		// setup, lazy loading would have worked. But that is not the case here.
		endTransaction();
		try {
			Map customAttrs = user.getCustomAttributes();
			for(Iterator i = customAttrs.entrySet().iterator(); i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				Object key = ent.getKey();
				Object val = ent.getValue();
			}
			// If you're still here, something's wrong.
			fail("Should throw LazyInitializationException");
		}
		catch(LazyInitializationException e) {
			assertTrue(true); // As expected
		}
	}
	
	public void testAddGroup() {
		FilterControls filter = new FilterControls();
		int count = pdi.countGroups(filter, "liferay.com");
		
		Group newGroup = new Group();
		newGroup.setName("brandNewGroup");
		newGroup.setZoneName("liferay.com");
		
		cdi.save(newGroup);
		
		int newCount = pdi.countGroups(filter, "liferay.com");
		
		assertEquals(count + 1, newCount);
	}
	/**
	 * Create a user with some custom attributes
	 * Verify attributes exist
	 *
	 */
	public void testAddUser() {
		String zoneName="testZone";
		String userName = "testUser";
		Workspace top = createZone(zoneName);
		FilterControls filter = new FilterControls();
		int count = pdi.countUsers(filter, top.getZoneName());
		User user = createBaseUser(top, userName);
		int newCount = pdi.countUsers(filter, top.getZoneName());
		assertEquals(count + 1, newCount);

		FilterControls fc = new FilterControls("owner.principal", user);
		//make sure attributes are there
		if (cdi.countObjects(CustomAttribute.class, fc) != 3)
			fail("Custom attributes missing");
		if (cdi.countObjects(Attachment.class, fc) != 1)
			fail("Attachments missing");
		if (cdi.countObjects(Event.class, fc) != 0)
			fail("Events missing");
		if (cdi.countObjects(WorkflowState.class, fc) != 0)
			fail("WorkflowStates missing");
		if (cdi.countObjects(Membership.class, new FilterControls("userId", user.getId())) != 1)
			fail("Membership not added for user " + user.getName());
	}
	/**
	 * test loadUsers,countUsers,loadGroups,countGroups with null filter
	 * test loadPrincipals with ids
	 *
	 */
	public void testLoadPrincipals() {
		Workspace top = createZone("testZone");
		FilterControls filter = new FilterControls();
		int count = pdi.countUsers(filter, top.getZoneName());
		List users = pdi.loadUsers(new FilterControls(), top.getZoneName());
		assertEquals(count,users.size());

		count = pdi.countGroups(filter, top.getZoneName());
		List groups = pdi.loadGroups(new FilterControls(), top.getZoneName());
		assertEquals(count,groups.size());
		List ids = new ArrayList();
		for (int i=0; i<users.size(); ++i) {
			User u = (User)users.get(i);
			ids.add(u.getId());
			cdi.evict(u);
		}
		
		for (int i=0; i<groups.size(); ++i) {
			Group g = (Group)groups.get(i);
			ids.add(g.getId());
			cdi.evict(g);
		}
		
		List prins = pdi.loadPrincipals(ids, top.getZoneName());
		if (prins.size() != (users.size() + groups.size())) {
			fail("Principals don't add up " + prins.size());
		}
		for (int i=0; i<prins.size(); ++i) {
			Principal p = (Principal)prins.get(i);
			if (!p.getClass().equals(User.class) && 
					!p.getClass().equals(Group.class))
				fail("Got a proxy back");
		}
		
	}
	/**
	 * Create a user and disable it.
	 * Test loadUserOnlyifEnabled nad loadEnabledUsers
	 *
	 */
	public void testDisablePrincipals() {
		Workspace top = createZone("testZone");
		User user1 = createBaseUser(top, "user1");
		user1.setDisabled(true);
		cdi.flush();
		cdi.clear();
		try {
			pdi.loadUserOnlyIfEnabled(user1.getId(), top.getZoneName());
			fail("Disabled user loaded with loadUserOnlyIfEnabled");
		} catch (NoUserByTheIdException nu) {}
		//load all users
		List users = pdi.loadUsers(new FilterControls(), top.getZoneName());
		List ids = new ArrayList();
		for (int i=0; i<users.size(); ++i) {
			User u = (User)users.get(i);
			ids.add(u.getId());
			cdi.evict(u);
		}
		users = pdi.loadEnabledUsers(ids, top.getZoneName());
		if (users.contains(user1))
			fail("Disabled user loaded with loadEnabledUsers");

	}
		
	/**
	 * Test deleteing users with various associations.
	 * Ensure associations are deleted.
	 * This test uses hibernate delete
	 *
	 */
	public void testDeleteBaseUser() {
		Workspace top = createZone("testZone");
		User user = createBaseUser(top, "testUser");
		//remove user from groups
		user.setMemberOf(new ArrayList());
		//delete as a hibernate object - will delete all associations with cascade=delete-all-orphan
		cdi.delete((Object)user);
		//make sure attributes are gone
		checkDeleted(user);
		
	}
	/**
	 * Test deleteing user and associations not maintained by hibernate.
	 * This test uses profileDao delete which is more efficient than
	 * hibernate deletes because of cascade.
	 *
	 */
	public void testDeleteFullUser() {

		Workspace top = createZone("testZone");
		//Now add another association not handled by hibernate cascade
		User user = createBaseUser(top, "testUser2");
		
		pdi.loadUserProperties(user.getId());
		if (cdi.countObjects(UserProperties.class, new FilterControls("id.principalId", user.getId())) != 1)
			fail("User properties were not created for user " + user.getName());

		pdi.loadSeenMap(user.getId());
		if (cdi.countObjects(SeenMap.class, new FilterControls("principalId", user.getId())) != 1)
			fail("Seen map was not created for user " + user.getName());
		
		cdi.flush();
		//have to clear cache cause group owns membership and may try to re-add the user
		cdi.clear();
		//Use the profileDelete - this is the only way to remove userProperties
		pdi.delete(user);
		//make sure attributes are gone
		checkDeleted(user);
	}
	/**
	 * Test profileDao.delete of a list of users
	 *
	 */
	public void testDeleteFullPrincipals() {
		Workspace top = createZone("testZone");
		List entries = fillProfile(top);
		
		//have to clear session cause we are bypassing hibernate cascade.
		cdi.clear();
		pdi.deleteEntries(entries);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((Principal)entries.get(i));
		}
		
	}
	/**
	 * Delete the profile binder and all its entries.
	 * Test profileDao.deleteEntries and delete of the binder
	 *
	 */
	public void testDeleteBinder() {
		Workspace top = createZone("testZone");
		List entries = fillProfile(top);
		
		//have to clear session cause we are bypassing hibernate cascade.
		cdi.clear();
		
		ProfileBinder p = pdi.getProfileBinder(top.getZoneName());
		pdi.deleteEntries(p);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((Principal)entries.get(i));
		}
		pdi.delete(p);
		
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
	private User createBaseUser(Workspace top, String name) {
		User user = new User();
		user.setZoneName(top.getZoneName());
		user.setName(name);
		user.setParentBinder(pdi.getProfileBinder(top.getZoneName()));
		//add some attributes
		user.addCustomAttribute("aString", "I am a string");
		String vals[] = new String[] {"red", "white", "blue"};
		user.addCustomAttribute("aList", vals);
		FileAttachment att = new FileAttachment("aFile");
		FileItem fi = new FileItem();
		fi.setName("dummy.txt");
		att.setFileItem(fi);
		cdi.save(att);
		assertNotNull(att.getId());
		user.addCustomAttribute("aFile", att);
		cdi.save(user);
		assertNotNull(user.getId());
		//add user to a group
		Group group = (Group)pdi.loadGroups(new FilterControls("name", adminGroup), top.getZoneName()).get(0);
		group.addMember(user);
		try {
			user = pdi.findUserByName(name, top.getZoneName());			
		} catch (NoUserByTheNameException e) {
			fail("New user test not found");
		}
		assertNotNull(user.getCustomAttribute("aString"));
		assertNotNull(user.getCustomAttribute("aFile"));
		Set sVal = (Set)user.getCustomAttribute("aList").getValue();
		assertEquals(sVal.toArray(vals), vals);
		return user;
		
	}
	private List fillProfile(Workspace top) {
		List entries = new ArrayList();
		String zoneName = top.getZoneName();
		User user1 = createBaseUser(top, "testUser1");
		entries.add(user1);
		pdi.loadUserProperties(user1.getId());
		pdi.loadSeenMap(user1.getId());
		
		User user2 = createBaseUser(top, "testUser2");
		entries.add(user2);
		pdi.loadUserProperties(user2.getId());
		pdi.loadSeenMap(user2.getId());

		User user3 = createBaseUser(top, "testUser3");
		entries.add(user3);
		pdi.loadUserProperties(user3.getId());
		pdi.loadSeenMap(user3.getId());
		
		Group group1 = new Group();
		group1.setName("group1");
		group1.setZoneName(zoneName);
		group1.setParentBinder(user1.getParentBinder());
		cdi.save(group1);
		entries.add(group1);
		
		Group group2 = new Group();
		group2.setName("group2");
		group2.setZoneName(zoneName);
		group2.setParentBinder(user1.getParentBinder());
		cdi.save(group2);
		entries.add(group2);
		
		Group group3 = new Group();
		group3.setName("group3");
		group3.setZoneName(zoneName);
		group3.setParentBinder(user1.getParentBinder());
		cdi.save(group3);
		entries.add(group3);

		group1.addMember(user1);
		group1.addMember(group2);
		group2.addMember(user2);
		group2.addMember(group3);
		group3.addMember(user3);
		group3.addMember(user1);
		
		cdi.flush();
		return entries;
	}
	private void checkDeleted(Principal p) {
		FilterControls fc = new FilterControls("owner.principal", p);
		if (cdi.countObjects(CustomAttribute.class, fc) != 0)
			fail("Custom attributes not deleted from user " + p.getName());
		if (cdi.countObjects(Attachment.class, fc) != 0)
			fail("Attachments not deleted from user " + p.getName());
		if (cdi.countObjects(Event.class, fc) != 0)
			fail("Events not deleted from user " + p.getName());
		if (cdi.countObjects(WorkflowState.class, fc) != 0)
			fail("WorkflowStates not deleted from user " + p.getName());
		if (cdi.countObjects(UserProperties.class, new FilterControls("id.principalId", p.getId())) != 0)
			fail("User properties were not deleted for user " + p.getName());
		if (cdi.countObjects(SeenMap.class, new FilterControls("principalId", p.getId())) != 0)
			fail("Seen map was not deleted for user " + p.getName());
		
	}
}
