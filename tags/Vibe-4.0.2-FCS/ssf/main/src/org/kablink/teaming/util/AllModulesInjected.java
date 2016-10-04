/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.conferencing.ConferencingModule;
import org.kablink.teaming.module.dashboard.DashboardModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.file.ConvertedFileModule;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.keyshield.KeyShieldModule;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.mobiledevice.MobileDeviceModule;
import org.kablink.teaming.module.netfolder.NetFolderModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.proxyidentity.ProxyIdentityModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;

/**
 * This convenience interface acts as a facade for all business modules and 
 * services defined in the system that should be accessible to a variety of 
 * client handler layers such as presentation layer for web application, 
 * web services handlers, and WebDAV handlers, etc.
 * 
 * @author jong
 */
public interface AllModulesInjected {

	void setBinderModule(BinderModule binderModule);

	BinderModule getBinderModule();

	void setWorkspaceModule(WorkspaceModule workspaceModule);

	WorkspaceModule getWorkspaceModule();

	void setFolderModule(FolderModule folderModule);

	FolderModule getFolderModule();

	void setNetFolderModule(NetFolderModule netFolderModule);
	
	NetFolderModule getNetFolderModule();

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

	void setConvertedFileModule(ConvertedFileModule convertedFileModule);

	ConvertedFileModule getConvertedFileModule();

	void setReportModule(ReportModule reportModule);

	ReportModule getReportModule();

	void setResourceDriverModule(ResourceDriverModule resourceDriverModule);
	ResourceDriverModule getResourceDriverModule();

	void setDashboardModule(DashboardModule dashboardModule);

	DashboardModule getDashboardModule();
	
	void setConferencingModule(ConferencingModule conferencingModule);

	ConferencingModule getConferencingModule();	
	
	void setIcalModule(IcalModule icalModule);
	IcalModule getIcalModule();
	
	void setRssModule(RssModule rssModule);
	RssModule getRssModule();
	
	void setLicenseModule(LicenseModule licenseModule);
	LicenseModule getLicenseModule();
	
	void setZoneModule(ZoneModule zoneModule);
	ZoneModule getZoneModule();
	
	void setSharingModule(SharingModule sharingModule);
	SharingModule getSharingModule();
	
	void setMobileDeviceModule(MobileDeviceModule mobileDeviceModule);
	MobileDeviceModule getMobileDeviceModule();
	
	void setKeyShieldModule(KeyShieldModule keyShieldModule);
	KeyShieldModule getKeyShieldModule();
	
	void setProxyIdentityModule(ProxyIdentityModule proxyIdentityModule);
	ProxyIdentityModule getProxyIdentityModule();
}
