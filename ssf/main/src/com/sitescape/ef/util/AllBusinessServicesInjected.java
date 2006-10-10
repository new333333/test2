package com.sitescape.ef.util;

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
}
