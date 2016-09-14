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

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.ArrayList;

import org.hibernate.LazyInitializationException;
import org.kablink.teaming.dao.impl.CoreDaoImpl;
import org.kablink.teaming.dao.impl.ProfileDaoImpl;
import org.kablink.teaming.support.AbstractTestBase;

import org.kablink.teaming.dao.util.FilterControls;
import org.kablink.teaming.domain.Attachment;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.CustomAttribute;
import org.kablink.teaming.domain.Event;
import org.kablink.teaming.domain.FileAttachment;
import org.kablink.teaming.domain.FileItem;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.Membership;
import org.kablink.teaming.domain.NoUserByTheIdException;
import org.kablink.teaming.domain.NoUserByTheNameException;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.SeenMap;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.domain.WorkflowState;
import org.kablink.teaming.domain.Workspace;

import org.junit.Assert;

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
		User user = pdi.findUserByName(adminUser, zoneName);
		Assert.assertNotNull(user);
	}
	
	public void testFindUserByNameNoUserByTheNameException() {
		createZone(zoneName);
		// Test three slightly different cases:
		// Test the situation where zone exists but username does not. 
		try {
			pdi.findUserByName("nonExistingUser", zoneName);
			Assert.fail("Should throw NoUserByTheNameException");
		}
		catch(NoUserByTheNameException e) {
			Assert.assertTrue(true); // Ok
		}
		
		// Test the situation where username exists but zone doesn't.
		try {
			pdi.findUserByName(adminUser, "nonExistingZone");
			Assert.fail("Should throw NoUserByTheNameException");
		}
		catch(NoWorkspaceByTheNameException e) {
			Assert.assertTrue(true); // Ok
		}
		
		// Test the situation where neither exists.
		try {
			pdi.findUserByName("nonExistingUser", "nonExistingZone");
			Assert.fail("Should throw NoUserByTheNameException");
		}
		catch(NoWorkspaceByTheNameException e) {
			Assert.assertTrue(true); // Ok
		}
	}
	
	public void testLoadUserAndLazyLoading() {
		// phase1: Load it. 
		Binder top = createZone(zoneName);

		User user =	pdi.findUserByName(adminUser, zoneName);	
		cdi.evict(user);
		user = pdi.loadUser(user.getId(), top.getId());
		Assert.assertNotNull(user);
		
		// phase2: Test lazy loading, by ending the transation (it rolls back).
		// Here we expect LazyInitializationException from Hibernate because
		// the session is already closed. If we had open-session-in-view
		// setup, lazy loading would have worked. But that is not the case here.
		//super.endTransaction();
		try {
			Map customAttrs = user.getCustomAttributes();
			for(Iterator i = customAttrs.entrySet().iterator(); i.hasNext();) {
				Map.Entry ent = (Map.Entry) i.next();
				Object key = ent.getKey();
				Object val = ent.getValue();
			}
			// If you're still here, something's wrong.
			Assert.fail("Should throw LazyInitializationException");
		}
		catch(LazyInitializationException e) {
			Assert.assertTrue(true); // As expected
		}
	}
	
	public void testAddGroup() {
		Binder top = createZone(zoneName);
		long count = cdi.countObjects(Group.class, null, top.getZoneId());
		
		Group newGroup = new Group(new IdentityInfo());
		newGroup.setName("brandNewGroup");
		newGroup.setForeignName("brandNewGroup");
		newGroup.setZoneId(top.getZoneId());
		
		cdi.save(newGroup);
		
		long newCount = cdi.countObjects(Group.class, null, top.getZoneId());

		Assert.assertEquals(count + 1, newCount);
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
		long count = cdi.countObjects(User.class, filter, top.getZoneId());
		User user = createBaseUser(top, userName);
		long newCount = cdi.countObjects(User.class, filter, top.getZoneId());
		Assert.assertEquals(count + 1, newCount);

		FilterControls fc = new FilterControls("owner.principal", user);
		//make sure attributes are there
		if (cdi.countObjects(CustomAttribute.class, fc, top.getZoneId()) != 3)
			Assert.fail("Custom attributes missing");
		if (cdi.countObjects(Attachment.class, fc, top.getZoneId()) != 1)
			Assert.fail("Attachments missing");
		if (cdi.countObjects(Event.class, fc, top.getZoneId()) != 0)
			Assert.fail("Events missing");
		if (cdi.countObjects(WorkflowState.class, fc, top.getZoneId()) != 0)
			Assert.fail("WorkflowStates missing");
		if (cdi.countObjects(Membership.class, new FilterControls("userId", user.getId()), top.getZoneId()) != 1)
			Assert.fail("Membership not added for user " + user.getName());
	}
	/**
	 * test loadUsers,countUsers,loadGroups,countGroups with null filter
	 * test loadPrincipals with ids
	 *
	 */
	public void testLoadPrincipals() {
		Workspace top = createZone("testZone");
		long count = cdi.countObjects(User.class, null, top.getZoneId());
		List users = pdi.loadUsers(new FilterControls(), top.getZoneId());
		Assert.assertEquals(count, users.size());

		count = cdi.countObjects(Group.class, null, top.getZoneId());
		List groups = pdi.loadGroups(new FilterControls(), top.getZoneId());
		Assert.assertEquals(count, groups.size());
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
		
		List prins = pdi.loadUserPrincipals(ids, top.getZoneId(), true);
		if (prins.size() != (users.size() + groups.size())) {
			Assert.fail("Principals don't add up " + prins.size());
		}
		for (int i=0; i<prins.size(); ++i) {
			Principal p = (Principal)prins.get(i);
			if (!p.getClass().equals(User.class) && 
					!p.getClass().equals(Group.class))
				Assert.fail("Got a proxy back");
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
			Assert.fail("Disabled user loaded with loadUserOnlyIfEnabled");
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
			Assert.fail("Disabled user loaded with loadEnabledUsers");

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
		User user = new User(new IdentityInfo());
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
		Assert.assertNotNull(att.getId());
		user.addCustomAttribute("aFile", att);
		cdi.save(user);
		Assert.assertNotNull(user.getId());
		//add user to a group
		Group group = (Group)pdi.loadGroups(new FilterControls("name", adminGroup), top.getZoneId()).get(0);
		group.addMember(user);
		try {
			user = pdi.findUserByName(name, top.getName());			
		} catch (NoUserByTheNameException e) {
			Assert.fail("New user test not found");
		}
		Assert.assertNotNull(user.getCustomAttribute("aString"));
		Assert.assertNotNull(user.getCustomAttribute("aFile"));
		Set sVal = (Set)user.getCustomAttribute("aList").getValue();
		Assert.assertEquals(sVal.toArray(vals), vals);
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
		
		Group group1 = new Group(new IdentityInfo());
		group1.setName("group1");
		group1.setForeignName("group1");
		group1.setZoneId(top.getZoneId());
		group1.setParentBinder(user1.getParentBinder());
		cdi.save(group1);
		entries.add(group1);
		
		Group group2 = new Group(new IdentityInfo());
		group2.setName("group2");
		group2.setForeignName("group2");
		group2.setZoneId(top.getZoneId());
		group2.setParentBinder(user1.getParentBinder());
		cdi.save(group2);
		entries.add(group2);
		
		Group group3 = new Group(new IdentityInfo());
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
		if (cdi.countObjects(CustomAttribute.class, fc, p.getZoneId()) != 0)
			Assert.fail("Custom attributes not deleted from user " + p.getName());
		if (cdi.countObjects(Attachment.class, fc, p.getZoneId()) != 0)
			Assert.fail("Attachments not deleted from user " + p.getName());
		if (cdi.countObjects(Event.class, fc, p.getZoneId()) != 0)
			Assert.fail("Events not deleted from user " + p.getName());
		if (cdi.countObjects(WorkflowState.class, fc, p.getZoneId()) != 0)
			Assert.fail("WorkflowStates not deleted from user " + p.getName());
		if (cdi.countObjects(UserProperties.class, new FilterControls("id.principalId", p.getId()), p.getZoneId()) != 0)
			Assert.fail("User properties were not deleted for user " + p.getName());
		if (cdi.countObjects(SeenMap.class, new FilterControls("principalId", p.getId()), p.getZoneId()) != 0)
			Assert.fail("Seen map was not deleted for user " + p.getName());
		
	}
}
