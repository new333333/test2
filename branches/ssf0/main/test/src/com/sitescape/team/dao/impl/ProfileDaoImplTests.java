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
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.Attachment;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.CustomAttribute;
import com.sitescape.team.domain.Event;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Group;
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
	
	@Test
	public void testFindUserByName() {
		setupWorkspace(zoneName);
		User user = profileDao.findUserByName(adminUser, zoneName);
		assertNotNull(user);
	}
	
	@Test
	public void testFindUserByNameNoUserByTheNameException() {
		setupWorkspace(zoneName);
		// Test three slightly different cases:
		// Test the situation where zone exists but username does not. 
		try {
			profileDao.findUserByName("nonExistingUser", zoneName);			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where username exists but zone doesn't.
		try {
			profileDao.findUserByName(adminUser, "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoWorkspaceByTheNameException e) {
			assertTrue(true); // Ok
		}
		
		// Test the situation where neither exists.
		try {
			profileDao.findUserByName("nonExistingUser", "nonExistingZone");			
			fail("Should throw NoUserByTheNameException");
		}
		catch(NoWorkspaceByTheNameException e) {
			assertTrue(true); // Ok
		}
	}
	
	@Test
	public void testLoadUser() {
		// phase1: Load it. 
		Binder top = setupWorkspace(zoneName).getSecond();

		User user =	profileDao.findUserByName(adminUser, zoneName);	
		coreDao.evict(user);
		user = profileDao.loadUser(user.getId(), top.getId());
		assertNotNull(user);
	}
	
	@Test
	public void testAddGroup() {
		Binder top = setupWorkspace(zoneName).getSecond();
		long count = coreDao.countObjects(Group.class, null, top.getZoneId());
		
		Group newGroup = new Group();
		newGroup.setName("brandNewGroup");
		newGroup.setForeignName("brandNewGroup");
		newGroup.setZoneId(top.getZoneId());
		
		coreDao.save(newGroup);
		
		long newCount = coreDao.countObjects(Group.class, null, top.getZoneId());
		
		assertEquals(count + 1, newCount);
	}
	/**
	 * Create a user with some custom attributes
	 * Verify attributes exist
	 *
	 */
	@Test
	public void testAddUser() {
		String userName = "testUser";
		Workspace top = setupWorkspace(zoneName).getSecond();
		
		FilterControls filter = new FilterControls("zoneId", top.getId());
		long count = coreDao.countObjects(User.class, filter, top.getZoneId());
		User user = createBaseUser(top, userName);
		long newCount = coreDao.countObjects(User.class, filter, top.getZoneId());
		assertEquals(count + 1, newCount);

		FilterControls fc = new FilterControls("owner.principal", user);
		//make sure attributes are there
		if (coreDao.countObjects(CustomAttribute.class, fc, top.getZoneId()) != 3)
			fail("Custom attributes missing");
		if (coreDao.countObjects(Attachment.class, fc, top.getZoneId()) != 1)
			fail("Attachments missing");
		if (coreDao.countObjects(Event.class, fc, top.getZoneId()) != 0)
			fail("Events missing");
		if (coreDao.countObjects(WorkflowState.class, fc, top.getZoneId()) != 0)
			fail("WorkflowStates missing");
		// XXX Membership does not have a reference to zoneId
//		if (coreDao.countObjects(Membership.class, new FilterControls("userId", user.getId()), top.getZoneId()) != 1)
//			fail("Membership not added for user " + user.getName());
	}
	/**
	 * test loadUsers,countUsers,loadGroups,countGroups with null filter
	 * test loadPrincipals with ids
	 *
	 */
	@Test
	public void testLoadPrincipals() {
		Workspace top = setupWorkspace("testZone").getSecond();
		long count = coreDao.countObjects(User.class, null, top.getZoneId());
		List users = profileDao.loadUsers(new FilterControls(), top.getZoneId());
		assertEquals(count,users.size());

		count = coreDao.countObjects(Group.class, null, top.getZoneId());
		List groups = profileDao.loadGroups(new FilterControls(), top.getZoneId());
		assertEquals(count,groups.size());
		List ids = new ArrayList();
		for (int i=0; i<users.size(); ++i) {
			User u = (User)users.get(i);
			ids.add(u.getId());
			coreDao.evict(u);
		}
		
		for (int i=0; i<groups.size(); ++i) {
			Group g = (Group)groups.get(i);
			ids.add(g.getId());
			coreDao.evict(g);
		}
		
		List prins = profileDao.loadUserPrincipals(ids, top.getZoneId(), true);
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
	@Test
	public void testDisablePrincipals() {
		Workspace top = setupWorkspace("testZone").getSecond();
		
		User user1 = createBaseUser(top, "user1");
		user1.setDisabled(true);
		coreDao.flush();
		coreDao.clear();
		try {
			profileDao.loadUser(user1.getId(), top.getZoneId());
			fail("Disabled user loaded with loadUserOnlyIfEnabled");
		} catch (NoUserByTheIdException nu) {}
		//load all users
		List users = profileDao.loadUsers(new FilterControls(), top.getZoneId());
		List ids = new ArrayList();
		for (int i=0; i<users.size(); ++i) {
			User u = (User)users.get(i);
			ids.add(u.getId());
			coreDao.evict(u);
		}
		users = profileDao.loadUsers(ids, top.getZoneId());
		if (users.contains(user1))
			fail("Disabled user loaded with loadEnabledUsers");

	}
		
	/**
	 * Test deleteing users with various associations.
	 * Ensure associations are deleted.
	 * This test uses hibernate delete
	 *
	 */
	@Test
	public void testDeleteBaseUser() {
		Workspace top = setupWorkspace("testZone").getSecond();
		User user = createBaseUser(top, "testUser");
		//remove user from groups
		user.setMemberOf(new ArrayList());
		//delete as a hibernate object - will delete all associations with cascade=delete-all-orphan
		coreDao.delete((Object)user);
		//make sure attributes are gone
		checkDeleted(user);
		
	}
	/**
	 * Test deleteing user and associations not maintained by hibernate.
	 * This test uses profileDao delete which is more efficient than
	 * hibernate deletes because of cascade.
	 *
	 */
	@Test
	public void testDeleteFullUser() {

		Workspace top = setupWorkspace("testZone").getSecond();
		//Now add another association not handled by hibernate cascade
		User user = createBaseUser(top, "testUser2");
			
		coreDao.flush();
		//have to clear cache cause group owns membership and may try to re-add the user
		coreDao.clear();
		profileDao.delete(user);
		//make sure attributes are gone
		checkDeleted(user);
	}
	/**
	 * Test profileDao.delete of a list of users
	 *
	 */
	@Test
	public void testDeleteFullPrincipals() {
		Workspace top = setupWorkspace("testZone").getSecond();
		List<Principal> entries = fillProfile(top);
		
		//have to clear session cause we are bypassing hibernate cascade.
		coreDao.clear();
		profileDao.deleteEntries(entries);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted(entries.get(i));
		}
		
	}
	/**
	 * Delete the profile binder and all its entries.
	 * Test profileDao.deleteEntries and delete of the binder
	 *
	 */
	@Test
	public void testDeleteBinder() {
		Workspace top = setupWorkspace("testZone").getSecond();
		List<Principal> entries = fillProfile(top);
		
		//have to clear session cause we are bypassing hibernate cascade.
		coreDao.clear();
		
		ProfileBinder p = profileDao.getProfileBinder(top.getZoneId());
		profileDao.delete(p);
		for (int i=0; i<entries.size(); ++i) {
			checkDeleted(entries.get(i));
		}
		
	}
	
	private List<Principal> fillProfile(Workspace top) {
		
		List<Principal> entries = new ArrayList<Principal>();
		User user1 = createBaseUser(top, "testUser1");
		
		RequestContext mRequestContext = fakeRequestContext();
		expect(mRequestContext.getZoneId()).andReturn(top.getId());
		expectLastCall().times(2);
		replay(mRequestContext);
		
		entries.add(user1);
		profileDao.loadUserProperties(user1.getId());
		profileDao.loadSeenMap(user1.getId());
		
		User user2 = createBaseUser(top, "testUser2");
		
		mRequestContext = fakeRequestContext();
		expect(mRequestContext.getZoneId()).andReturn(top.getId());
		expectLastCall().times(2);
		replay(mRequestContext);
		
		entries.add(user2);
		profileDao.loadUserProperties(user2.getId());
		profileDao.loadSeenMap(user2.getId());

		User user3 = createBaseUser(top, "testUser3");
		
		mRequestContext = fakeRequestContext();
		expect(mRequestContext.getZoneId()).andReturn(top.getId());
		expectLastCall().times(2);
		replay(mRequestContext);
		
		entries.add(user3);
		profileDao.loadUserProperties(user3.getId());
		profileDao.loadSeenMap(user3.getId());
		
		Group group1 = new Group();
		group1.setName("group1");
		group1.setForeignName("group1");
		group1.setZoneId(top.getZoneId());
		group1.setParentBinder(user1.getParentBinder());
		coreDao.save(group1);
		entries.add(group1);
		
		Group group2 = new Group();
		group2.setName("group2");
		group2.setForeignName("group2");
		group2.setZoneId(top.getZoneId());
		group2.setParentBinder(user1.getParentBinder());
		coreDao.save(group2);
		entries.add(group2);
		
		Group group3 = new Group();
		group3.setName("group3");
		group3.setForeignName("group3");
		group3.setZoneId(top.getZoneId());
		group3.setParentBinder(user1.getParentBinder());
		coreDao.save(group3);
		entries.add(group3);

		group1.addMember(user1);
		group1.addMember(group2);
		group2.addMember(user2);
		group2.addMember(group3);
		group3.addMember(user3);
		group3.addMember(user1);
		
		coreDao.flush();
		return entries;
	}
	private void checkDeleted(Principal p) {
		FilterControls fc = new FilterControls("owner.principal", p);
		if (coreDao.countObjects(CustomAttribute.class, fc, p.getZoneId()) != 0)
			fail("Custom attributes not deleted from user " + p.getName());
		if (coreDao.countObjects(Attachment.class, fc, p.getZoneId()) != 0)
			fail("Attachments not deleted from user " + p.getName());
		if (coreDao.countObjects(Event.class, fc, p.getZoneId()) != 0)
			fail("Events not deleted from user " + p.getName());
		if (coreDao.countObjects(WorkflowState.class, fc, p.getZoneId()) != 0)
			fail("WorkflowStates not deleted from user " + p.getName());
		if (coreDao.countObjects(UserProperties.class, new FilterControls("id.principalId", p.getId()), p.getZoneId()) != 0)
			fail("User properties were not deleted for user " + p.getName());
		if (coreDao.countObjects(SeenMap.class, new FilterControls("principalId", p.getId()), p.getZoneId()) != 0)
			fail("Seen map was not deleted for user " + p.getName());
		
	}

	private User createBaseUser(Workspace top, String name) {
		RequestContext mRequestContext = fakeRequestContext();
		reset(mRequestContext);
		expect(mRequestContext.getZoneId()).andReturn(top.getId());
		expectLastCall().times(7);
		replay(mRequestContext);
		
		User user = new User();
		user.setZoneId(top.getZoneId());
		user.setName(name);
		user.setForeignName(name);
		user.setParentBinder(profileDao.getProfileBinder(top.getZoneId()));
		//add some attributes
		user.addCustomAttribute("aString", "I am a string");
		String vals[] = new String[] {"red", "white", "blue"};
		user.addCustomAttribute("aList", vals);
		FileAttachment att = new FileAttachment("aFile");
		FileItem fi = new FileItem();
		fi.setName("dummy.txt");
		att.setFileItem(fi);
		coreDao.save(att);
		user.addCustomAttribute("aFile", att);
		coreDao.save(user);
		//add user to a group
		Group group = (Group)profileDao.loadGroups(new FilterControls("name", adminGroup), top.getZoneId()).get(0);
		group.addMember(user);
		user = profileDao.findUserByName(name, top.getName());
		
		return user;
	}
}
