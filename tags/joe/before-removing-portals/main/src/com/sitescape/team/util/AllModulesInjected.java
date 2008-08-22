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
package com.sitescape.team.util;

import com.sitescape.team.module.admin.AdminModule;
import com.sitescape.team.module.authentication.AuthenticationModule;
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ic.ICBrokerModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.ldap.LdapModule;
import com.sitescape.team.module.license.LicenseModule;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.rss.RssModule;
import com.sitescape.team.module.template.TemplateModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.module.zone.ZoneModule;

/**
 * This convenience interface acts as a facade for all business modules and 
 * services defined in the system that should be accessibe to a variety of 
 * client handler layers such as presentation layer for web application, 
 * web services handlers, and WebDAV handlers, etc.
 * 
 * @author jong
 *
 */
public interface AllModulesInjected {

	void setBinderModule(BinderModule binderModule);

	BinderModule getBinderModule();

	void setWorkspaceModule(WorkspaceModule workspaceModule);

	WorkspaceModule getWorkspaceModule();

	void setFolderModule(FolderModule folderModule);

	FolderModule getFolderModule();

	void setTemplateModule(TemplateModule templateModule);

	TemplateModule getTemplateModule();

	void setAdminModule(AdminModule adminModule);

	AdminModule getAdminModule();

	void setAuthenticationModule(AuthenticationModule authenticationModule);

	AuthenticationModule getAuthenticationModule();

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

	void setReportModule(ReportModule reportModule);

	ReportModule getReportModule();

	void setDashboardModule(DashboardModule dashboardModule);

	DashboardModule getDashboardModule();
	
	void setIcBrokerModule(ICBrokerModule icBrokerModule);

	ICBrokerModule getIcBrokerModule();	
	
	void setIcalModule(IcalModule icalModule);
	IcalModule getIcalModule();
	
	void setRssModule(RssModule rssModule);
	RssModule getRssModule();
	
	void setLicenseModule(LicenseModule licenseModule);
	LicenseModule getLicenseModule();
	
	void setZoneModule(ZoneModule zoneModule);
	ZoneModule getZoneModule();
}
