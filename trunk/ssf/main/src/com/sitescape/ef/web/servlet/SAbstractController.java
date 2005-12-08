package com.sitescape.ef.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.mvc.AbstractController;

import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.sample.EmployeeModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.portlet.forum.ForumActionModule;


public abstract class SAbstractController extends AbstractController {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private EmployeeModule employeeModule;
	private WorkspaceModule workspaceModule;
	private FolderModule folderModule;
	private AdminModule adminModule;
	private ProfileModule profileModule;
	private DefinitionModule definitionModule;
	private ForumActionModule forumActionModule;
	private LdapModule ldapModule;

	public void setEmployeeModule(EmployeeModule employeeModule) {
		this.employeeModule = employeeModule;
	}
	
	protected EmployeeModule getEmployeeModule() {
		return employeeModule;
	}
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	
	protected WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	
	protected FolderModule getFolderModule() {
		return folderModule;
	}
	
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	
	protected AdminModule getAdminModule() {
		return adminModule;
	}

	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	protected ProfileModule getProfileModule() {
		return profileModule;
	}
	
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	
	protected DefinitionModule getDefinitionModule() {
		return definitionModule;
	}
	public void setForumActionModule(ForumActionModule forumActionModule) {
		this.forumActionModule = forumActionModule;
	}
	
	protected ForumActionModule getForumActionModule() {
		return forumActionModule;
	}	
	public void setLdapModule(LdapModule ldapModule) {
		this.ldapModule = ldapModule;
	}
	protected LdapModule getLdapModule() {
		return ldapModule;
	}
}
