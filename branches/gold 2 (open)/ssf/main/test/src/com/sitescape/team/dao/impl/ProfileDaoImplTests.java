/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.dao.impl;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.LazyInitializationException;
import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.sitescape.team.dao.impl.CoreDaoImpl;
import com.sitescape.team.dao.impl.ProfileDaoImpl;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.Membership;
import com.sitescape.team.domain.NoUserByTheIdException;
import com.sitescape.team.domain.NoUserByTheNameException;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.Principal;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.SeenMap;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.UserProperties;
import com.sitescape.team.domain.WorkflowState;
import com.sitescape.team.domain.Workspace;
import com.sitescape.team.support.AbstractTestBase;

/**
 * Integration unit tests for data access layer. 
 * 
 * @author Jong Kim
 */
public class ProfileDaoImplTests extends AbstractTestBase {

	private static String zoneName ="testZone";
	protected String[] getConfigLocations() {
		return new String[] {"/com/sitescape/team/dao/impl/applicationContext-coredao.xml"};
	}
	
	public void testFindUserByName() {
		createZone(zoneName);
		User user = pdi.findUserByName("liferay.com.1", "liferay.com");
		assertNotNull(user);
	}
	
	public void testFindUserByNameNoUserByTheNameException() {
		createZone(zoneName);
		// Test three slightly different cases:
		// Test the situation where zone exists but username does not. 
		try {
			pdi.findUserByName("nonExistingUser", zoneName);			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where username exists but zone doesn't.
		try {
			pdi.findUserByName(adminUser, "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoWorkspaceByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where neither exists.
		try {
			pdi.findUserByName("nonExistingUser", "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoWorkspaceByTheNameException e) {
			assertTrue(true); // Ok
		}
	}
	
	public void testLoadUserAndLazyLoading() {
		// phase1: Load it. 
		Binder top = createZone(zoneName);

		User user =	pdi.findUserByName(adminUser, zoneName);	
		cdi.evict(user);
		user = pdi.loadUser(user.getId(), top.getId());
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
		Binder top = createZone(zoneName);
		FilterControls filter = new FilterControls("zoneId", top.getZoneId());
		long count = cdi.countObjects(Group.class, filter);
		
		Group newGroup = new Group();
		newGroup.setName("brandNewGroup");
		newGroup.setForeignName("brandNewGroup");
		newGroup.setZoneId(top.getZoneId());
		
		cdi.save(newGroup);
		
		long newCount = cdi.countObjects(Group.class, filter);
		
		assertEquals(count + 1, newCount);
	}
	/**
	 * Create a user with some custom attributes
	 * Verify attributes exist
	 *
	 */
	public void testAddUser() {
		String userName = "testUser";
		Workspace top = createZone(zoneName);;
		FilterControls filter = new FilterControls("zoneId", top.getId());
		long count = cdi.countObjects(User.class, filter);
		User user = createBaseUser(top, userName);
		long newCount = cdi.countObjects(User.class, filter);
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
		FilterControls filter = new FilterControls("zoneId",top.getZoneId());
		long count = cdi.countObjects(User.class, filter);
		List users = pdi.loadUsers(new FilterControls(), top.getZoneId());
		assertEquals(count,users.size());

		count = cdi.countObjects(Group.class, filter);
		List groups = pdi.loadGroups(new FilterControls(), top.getZoneId());
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
		
		List prins = pdi.loadPrincipals(ids, top.getZoneId(), true);
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
			pdi.loadUser(user1.getId(), top.getZoneId());
			fail("Disabled user loaded with loadUserOnlyIfEnabled");
		} catch (NoUserByTheIdException nu) {}
		//load all users
		List users = pdi.loadUsers(new FilterControls(), top.getZoneId());
		List ids = new ArrayList();
		for (int i=0; i<users.size(); ++i) {
			User u = (User)users.get(i);
			ids.add(u.getId());
			cdi.evict(u);
		}
		users = pdi.loadUsers(ids, top.getZoneId());
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
			
		cdi.flush();
		//have to clear cache cause group owns membership and may try to re-add the user
		cdi.clear();
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
		
		ProfileBinder p = pdi.getProfileBinder(top.getZoneId());
		pdi.delete(p);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted((Principal)entries.get(i));
		}
		
	}

	private User createBaseUser(Workspace top, String name) {
		User user = new User();
		user.setZoneId(top.getZoneId());
		user.setName(name);
		user.setForeignName(name);
		user.setParentBinder(pdi.getProfileBinder(top.getZoneId()));
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
		Group group = (Group)pdi.loadGroups(new FilterControls("name", adminGroup), top.getZoneId()).get(0);
		group.addMember(user);
		try {
			user = pdi.findUserByName(name, top.getName());			
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
		group1.setForeignName("group1");
		group1.setZoneId(top.getZoneId());
		group1.setParentBinder(user1.getParentBinder());
		cdi.save(group1);
		entries.add(group1);
		
		Group group2 = new Group();
		group2.setName("group2");
		group2.setForeignName("group2");
		group2.setZoneId(top.getZoneId());
		group2.setParentBinder(user1.getParentBinder());
		cdi.save(group2);
		entries.add(group2);
		
		Group group3 = new Group();
		group3.setName("group3");
		group3.setForeignName("group3");
		group3.setZoneId(top.getZoneId());
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
