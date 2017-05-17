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
package org.kablink.teaming.support;

import org.junit.Assert;
import org.kablink.teaming.dao.impl.CoreDaoImpl;
import org.kablink.teaming.dao.impl.ProfileDaoImpl;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.IdentityInfo;
import org.kablink.teaming.domain.NoWorkspaceByTheNameException;
import org.kablink.teaming.domain.ProfileBinder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.Workspace;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;


public abstract class AbstractTestBase extends AbstractTransactionalJUnit4SpringContextTests {
	protected CoreDaoImpl cdi;
	protected ProfileDaoImpl pdi;
	protected static String adminGroup = "administrators";
	protected static String adminUser = "administrator";

	
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

	protected Workspace createZone(String name) {
		Workspace top;
		try { 
			top = cdi.findTopWorkspace(name);
		} catch (NoWorkspaceByTheNameException nw) {
			top = new Workspace();
			top.setName(name);
			//temporary until have read id
			top.setZoneId(new Long(-1));
			top.setTitle("administration.initial.workspace.title");
			top.setPathName("/"+top.getTitle());
			top.setInternalId(ObjectKeys.TOP_WORKSPACE_INTERNALID);
			//generate id for top and profiles
			cdi.save(top);
			top.setupRoot();
			top.setZoneId(top.getId());
			
			ProfileBinder profiles = new ProfileBinder();
			profiles.setName("_profiles");
			profiles.setTitle("administration.initial.profile.title");
			profiles.setPathName(top.getPathName() + "/" + profiles.getTitle());
			profiles.setZoneId(top.getId());
			profiles.setInternalId(ObjectKeys.PROFILE_ROOT_INTERNALID);
			top.addBinder(profiles);
			
			//generate id for profiles
			cdi.save(profiles);
			cdi.updateFileName(top, profiles, null, profiles.getTitle());

			Workspace global = new Workspace();
			
			global.setName("_global");
			global.setTitle("Global");
			global.setPathName(top.getPathName() + "/" + global.getTitle());
			global.setZoneId(top.getId());
			global.setInternalId(ObjectKeys.GLOBAL_ROOT_INTERNALID);
			top.addBinder(global);
			
			//generate id globa
			cdi.save(global);
			cdi.updateFileName(top, global, null, global.getTitle());

			Workspace team = new Workspace();
			team.setName("_teams");
			team.setTitle("Teams");
			team.setPathName(top.getPathName() + "/" + team.getTitle());
			team.setZoneId(top.getId());
			team.setInternalId(ObjectKeys.TEAM_ROOT_INTERNALID);
			top.addBinder(team);
			
			//generate id for top and profiles
			cdi.save(team);
			cdi.updateFileName(top, team, null, team.getTitle());
			
			Group group = new Group(new IdentityInfo());
			group.setName(adminGroup);
			group.setForeignName(adminGroup);
			group.setZoneId(top.getId());
			group.setParentBinder(profiles);
			cdi.save(group);
			
			User user = new User(new IdentityInfo());
			user.setName(adminUser);
			user.setForeignName(adminUser);
			user.setZoneId(top.getId());
			user.setParentBinder(profiles);
			cdi.save(user);
			group.addMember(user);
			cdi.flush();
			
			top = cdi.findTopWorkspace(name);
			Assert.assertEquals(top.getName(), name);
		}
		return top;
		
	}


}
