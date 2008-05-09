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
package com.sitescape.team.support;


import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.expectLastCall;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.reset;
import static org.junit.Assert.assertEquals;

import javax.annotation.Resource;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.mock.jndi.SimpleNamingContextBuilder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.context.request.RequestContext;
import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.dao.impl.CoreDaoImpl;
import com.sitescape.team.dao.impl.ProfileDaoImpl;
import com.sitescape.team.dao.util.FilterControls;
import com.sitescape.team.domain.FileAttachment;
import com.sitescape.team.domain.FileItem;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;


// XXX Not desirable to load mock-liferay-context.xml at this level but ugly singletons require it (for now)
@ContextConfiguration(	locations= {	"/com/sitescape/team/applicationContext.xml", 
														"/com/sitescape/team/additionalContext.xml","/com/sitescape/team/liferay/mock-liferay-context.xml" })
public abstract class AbstractTestBase extends AbstractTransactionalJUnit4SpringContextTests {
	protected static final String adminGroup = "administrators";
	protected static final String adminUser = "admin";
	protected static final SimpleNamingContextBuilder contextBuilder;
	private static final String DRIVER = "com.mysql.jdbc.Driver";
	private static final String SSF_URL = "jdbc:mysql://localhost:3306/sitescape?useUnicode=true&amp;characterEncoding=UTF-8";
	private static final String LIFERAY_URL = "jdbc:mysql://localhost:3306/lportal?useUnicode=true&amp;characterEncoding=UTF-8";
	private static final String USERNAME = "sitescape";
	private static final String PASSWORD = "sitescape";
	private static final DriverManagerDataSource SSF_DATA_SOURCE = new DriverManagerDataSource(
			DRIVER, SSF_URL, USERNAME, PASSWORD);
	private static final DriverManagerDataSource LIFERAY_DATA_SOURCE = new DriverManagerDataSource(
			DRIVER, LIFERAY_URL, USERNAME, PASSWORD);

	static {
		contextBuilder = new SimpleNamingContextBuilder();
		contextBuilder.bind("java:comp/env/jdbc/SiteScapePool", SSF_DATA_SOURCE);
		contextBuilder.bind("java:comp/env/jdbc/LiferayPool", LIFERAY_DATA_SOURCE);
		try {
			contextBuilder.activate();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (NamingException e) {
			e.printStackTrace();
		}
	}
	
	@Autowired(required = true)
	protected CoreDaoImpl coreDao;
	@Autowired(required = true)
	protected ProfileDaoImpl profileDao;

	/* (non-Javadoc)
	 * @see org.springframework.test.context.junit4.AbstractTransactionalJUnit4SpringContextTests#setDataSource(javax.sql.DataSource)
	 */
	@Override
	@Resource(name = "dataSource")
	public void setDataSource(DataSource dataSource) {
		super.setDataSource(dataSource);
	}

	protected Workspace createZone(String name) {
		Workspace top;
		Long topId = new Long(-1);
		RequestContext mrc = fakeRequestContext();
		expect(mrc.getZoneId()).andReturn(topId);
		RequestContextHolder.setRequestContext(mrc);
		expectLastCall().times(3);
		replay(mrc);
		try { 
			top = coreDao.findTopWorkspace(name);
		} catch (NoWorkspaceByTheNameException nw) {
			top = new Workspace();
			top.setName(name);
			//temporary until have read id
			top.setZoneId(topId);
			top.setTitle("administration.initial.workspace.title");
			top.setPathName("/"+top.getTitle());
			top.setInternalId(ObjectKeys.TOP_WORKSPACE_INTERNALID);
			//generate id for top and profiles
			coreDao.save(top);
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
			coreDao.save(profiles);
			coreDao.updateFileName(top, profiles, null, profiles.getTitle());

			Workspace global = new Workspace();
			
			global.setName("_global");
			global.setTitle("Global");
			global.setPathName(top.getPathName() + "/" + global.getTitle());
			global.setZoneId(top.getId());
			global.setInternalId(ObjectKeys.GLOBAL_ROOT_INTERNALID);
			top.addBinder(global);
			
			//generate id globa
			coreDao.save(global);
			coreDao.updateFileName(top, global, null, global.getTitle());

			Workspace team = new Workspace();
			team.setName("_teams");
			team.setTitle("Teams");
			team.setPathName(top.getPathName() + "/" + team.getTitle());
			team.setZoneId(top.getId());
			team.setInternalId(ObjectKeys.TEAM_ROOT_INTERNALID);
			top.addBinder(team);
			
			//generate id for top and profiles
			coreDao.save(team);
			coreDao.updateFileName(top, team, null, team.getTitle());
			
			Group group = new Group();
			group.setName(adminGroup);
			group.setForeignName(adminGroup);
			group.setZoneId(top.getId());
			group.setParentBinder(profiles);
			coreDao.save(group);
			
			User user = new User();
			user.setName(adminUser);
			user.setForeignName(adminUser);
			user.setZoneId(top.getId());
			user.setParentBinder(profiles);
			coreDao.save(user);
			group.addMember(user);
			coreDao.flush();
			
			top = coreDao.findTopWorkspace(name);
			assertEquals(top.getName(), name);
		}
		return top;
		
	}

	protected User createBaseUser(Workspace top, String name) {
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
	
	protected RequestContext fakeRequestContext() {
		 RequestContext mRequestContext = createMock(RequestContext.class);
		 RequestContextHolder.setRequestContext(mRequestContext);
		 return mRequestContext;
	}

}
