package com.sitescape.team.util;

import com.sitescape.team.ic.ICBroker;
import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ldap.LdapModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.sample.EmployeeModule;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.rss.RssGenerator;

/**
 * This convenience interface acts as a facade for all business modules and 
 * services defined in the system that should be accessibe to a variety of 
 * client handler layers such as presentation layer for web application, 
 * web services handlers, and WebDAV handlers, etc.
 * 
 * @author jong
 *
 */
public interface AllBusinessServicesInjected {
	void setEmployeeModule(EmployeeModule employeeModule);;

	EmployeeModule getEmployeeModule();;

	void setBinderModule(BinderModule binderModule);

	BinderModule getBinderModule();

	void setWorkspaceModule(WorkspaceModule workspaceModule);

	WorkspaceModule getWorkspaceModule();

	void setFolderModule(FolderModule folderModule);

	FolderModule getFolderModule();

	void setAdminModule(AdminModule adminModule);

	AdminModule getAdminModule();

	void setProfileModule(ProfileModule profileModule);

	ProfileModule getProfileModule();

	void setDefinitionModule(DefinitionModule definitionModule);

	DefinitionModule getDefinitionModule();

	WorkflowModule getWorkflowModule();

	void setWorkflowModule(WorkflowModule workflowModule);

	void setLdapModule(LdapModule ldapModule);

	LdapModule getLdapModule();

	void setFileModule(FileModule fileModule);

	FileModule getFileModule();

	void setRssGenerator(RssGenerator rssGenerator);

	RssGenerator getRssGenerator();

	void setDashboardModule(DashboardModule dashboardModule);

	DashboardModule getDashboardModule();
	
	void setIcBroker(ICBroker icBroker);

	ICBroker getIcBroker();	
}
