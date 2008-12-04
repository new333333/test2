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
package org.kablink.teaming.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.kablink.teaming.module.admin.AdminModule;
import org.kablink.teaming.module.authentication.AuthenticationModule;
import org.kablink.teaming.module.binder.BinderModule;
import org.kablink.teaming.module.dashboard.DashboardModule;
import org.kablink.teaming.module.definition.DefinitionModule;
import org.kablink.teaming.module.file.FileModule;
import org.kablink.teaming.module.folder.FolderModule;
import org.kablink.teaming.module.ic.ICBrokerModule;
import org.kablink.teaming.module.ical.IcalModule;
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;


public class AbstractAllModulesInjected implements AllModulesInjected {

	protected final Log logger = LogFactory.getLog(getClass());

	private WorkspaceModule workspaceModule;
	private FolderModule folderModule;
	private TemplateModule templateModule;
	private AdminModule adminModule;
	private AuthenticationModule authenticationModule;
	private ProfileModule profileModule;
	private DefinitionModule definitionModule;
	private WorkflowModule workflowModule;
	private BinderModule binderModule;
	private LdapModule ldapModule;
	private ReportModule reportModule;
	private FileModule fileModule;
	private DashboardModule dashboardModule;
	private ICBrokerModule icBrokerModule;
	private IcalModule icalModule;
	private RssModule rssModule;
	private LicenseModule licenseModule;
	private ZoneModule zoneModule;

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
	
	public void setTemplateModule(TemplateModule templateModule) {
		this.templateModule = templateModule;
	}
	
	public TemplateModule getTemplateModule() {
		return templateModule;
	}

	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	
	public AdminModule getAdminModule() {
		return adminModule;
	}

	public void setAuthenticationModule(AuthenticationModule authenticationModule) {
		this.authenticationModule = authenticationModule;
	}
	
	public AuthenticationModule getAuthenticationModule() {
		return authenticationModule;
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
	
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
	
	public DashboardModule getDashboardModule() {
		return dashboardModule;
	}

	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}
	
	public ReportModule getReportModule() {
		return reportModule;
	}
	
	public ICBrokerModule getIcBrokerModule() {
		return icBrokerModule;
	}

	public void setIcBrokerModule(ICBrokerModule icBroker) {
		this.icBrokerModule = icBroker;
	}
	
	public IcalModule getIcalModule() {
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	
	
	public RssModule getRssModule() {
		return rssModule;
	}
	public void setRssModule(RssModule rssModule) {
		this.rssModule = rssModule;
	}
	
	public LicenseModule getLicenseModule() {
		return licenseModule;
	}
	public void setLicenseModule(LicenseModule licenseModule) {
		this.licenseModule = licenseModule;
	}
	
	public ZoneModule getZoneModule() {
		return zoneModule;
	}
	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

}
