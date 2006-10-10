package com.sitescape.ef.web.servlet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.servlet.mvc.AbstractController;

import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.sample.EmployeeModule;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.rss.RssGenerator;
import com.sitescape.ef.util.AllBusinessServicesInjected;

public abstract class SAbstractController extends AbstractController
implements AllBusinessServicesInjected {
	
	protected Log logger = LogFactory.getLog(getClass());
	
	private EmployeeModule employeeModule;
	private WorkspaceModule workspaceModule;
	private FolderModule folderModule;
	private AdminModule adminModule;
	private ProfileModule profileModule;
	private DefinitionModule definitionModule;
	private WorkflowModule workflowModule;
	private BinderModule binderModule;
	private LdapModule ldapModule;
	private FileModule fileModule;
	private RssGenerator rssGenerator;
		
	public void setEmployeeModule(EmployeeModule employeeModule) {
		this.employeeModule = employeeModule;
	}
	
	public EmployeeModule getEmployeeModule() {
		return employeeModule;
	}
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	public BinderModule getBinderModule() {
		return binderModule;
	}

	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	
	public WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	
	public FolderModule getFolderModule() {
		return folderModule;
	}
	
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	
	public AdminModule getAdminModule() {
		return adminModule;
	}

	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	public ProfileModule getProfileModule() {
		return profileModule;
	}
	
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	
	public DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	public WorkflowModule getWorkflowModule() {
		return workflowModule;
	}

	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	public void setLdapModule(LdapModule ldapModule) {
		this.ldapModule = ldapModule;
	}
	
	public LdapModule getLdapModule() {
		return ldapModule;
	}	
	
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	public FileModule getFileModule() {
		return fileModule;
	}

	public void setRssGenerator(RssGenerator rssGenerator) {
		this.rssGenerator = rssGenerator;
	}
	
	public RssGenerator getRssGenerator() {
		return rssGenerator;
	}
}
