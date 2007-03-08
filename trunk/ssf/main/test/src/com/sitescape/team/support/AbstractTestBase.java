package com.sitescape.team.support;

import org.springframework.test.AbstractTransactionalDataSourceSpringContextTests;

import com.sitescape.team.ObjectKeys;
import com.sitescape.team.dao.impl.CoreDaoImpl;
import com.sitescape.team.dao.impl.ProfileDaoImpl;
import com.sitescape.team.domain.Group;
import com.sitescape.team.domain.NoWorkspaceByTheNameException;
import com.sitescape.team.domain.ProfileBinder;
import com.sitescape.team.domain.User;
import com.sitescape.team.domain.Workspace;


public abstract class AbstractTestBase extends AbstractTransactionalDataSourceSpringContextTests {
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
			
			Group group = new Group();
			group.setName(adminGroup);
			group.setZoneId(top.getId());
			group.setParentBinder(profiles);
			cdi.save(group);
			
			User user = new User();
			user.setName(adminUser);
			user.setZoneId(top.getId());
			user.setParentBinder(profiles);
			cdi.save(user);
			group.addMember(user);
			cdi.flush();
			
			top = cdi.findTopWorkspace(name);
			assertEquals(top.getName(), name);
		}
		return top;
		
	}


}
