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
package org.kablink.teaming.util;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
import org.kablink.teaming.module.ldap.LdapModule;
import org.kablink.teaming.module.license.LicenseModule;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.module.report.ReportModule;
import org.kablink.teaming.module.resourcedriver.ResourceDriverModule;
import org.kablink.teaming.module.rss.RssModule;
import org.kablink.teaming.module.sharing.SharingModule;
import org.kablink.teaming.module.template.TemplateModule;
import org.kablink.teaming.module.workflow.WorkflowModule;
import org.kablink.teaming.module.workspace.WorkspaceModule;
import org.kablink.teaming.module.zone.ZoneModule;


public abstract class AbstractAllModulesInjected implements AllModulesInjected {

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
	private ResourceDriverModule resourceDriverModule;
	private FileModule fileModule;
	private ConvertedFileModule convertedFileModule;
	private DashboardModule dashboardModule;
	private ConferencingModule conferencingModule;
	private IcalModule icalModule;
	private RssModule rssModule;
	private LicenseModule licenseModule;
	private ZoneModule zoneModule;
	private SharingModule sharingModule;

	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	public BinderModule getBinderModule() {
		if(binderModule == null)
			binderModule = (BinderModule) SpringContextUtil.getBean("binderModule");
		return binderModule;
	}

	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	
	public WorkspaceModule getWorkspaceModule() {
		if(workspaceModule == null)
			workspaceModule = (WorkspaceModule) SpringContextUtil.getBean("workspaceModule");
		return workspaceModule;
	}

	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	
	public FolderModule getFolderModule() {
		if(folderModule == null)
			folderModule = (FolderModule) SpringContextUtil.getBean("folderModule");
		return folderModule;
	}
	
	public void setTemplateModule(TemplateModule templateModule) {
		this.templateModule = templateModule;
	}
	
	public TemplateModule getTemplateModule() {
		if(templateModule == null)
			templateModule = (TemplateModule) SpringContextUtil.getBean("templateModule");
		return templateModule;
	}

	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	
	public AdminModule getAdminModule() {
		if(adminModule == null)
			adminModule = (AdminModule) SpringContextUtil.getBean("adminModule");
		return adminModule;
	}

	public void setAuthenticationModule(AuthenticationModule authenticationModule) {
		this.authenticationModule = authenticationModule;
	}
	
	public AuthenticationModule getAuthenticationModule() {
		if(authenticationModule == null)
			authenticationModule = (AuthenticationModule) SpringContextUtil.getBean("authenticationModule");
		return authenticationModule;
	}

	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	public ProfileModule getProfileModule() {
		if(profileModule == null)
			profileModule = (ProfileModule) SpringContextUtil.getBean("profileModule");
		return profileModule;
	}
	
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	
	public DefinitionModule getDefinitionModule() {
		if(definitionModule == null)
			definitionModule = (DefinitionModule) SpringContextUtil.getBean("definitionModule");
		return definitionModule;
	}

	public WorkflowModule getWorkflowModule() {
		if(workflowModule == null)
			workflowModule = (WorkflowModule) SpringContextUtil.getBean("workflowModule");
		return workflowModule;
	}

	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	
	public void setLdapModule(LdapModule ldapModule) {
		this.ldapModule = ldapModule;
	}
	
	public LdapModule getLdapModule() {
		if(ldapModule == null)
			ldapModule = (LdapModule) SpringContextUtil.getBean("ldapModule");
		return ldapModule;
	}
	
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	
	public FileModule getFileModule() {
		if(fileModule == null)
			fileModule = (FileModule) SpringContextUtil.getBean("fileModule");
		return fileModule;
	}
	
	public ConvertedFileModule getConvertedFileModule() {
		if(convertedFileModule == null)
			convertedFileModule = (ConvertedFileModule) SpringContextUtil.getBean("convertedFileModule");
		return convertedFileModule;
	}

	public void setConvertedFileModule(ConvertedFileModule convertedFileModule) {
		this.convertedFileModule = convertedFileModule;
	}

	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
	
	public DashboardModule getDashboardModule() {
		if(dashboardModule == null)
			dashboardModule = (DashboardModule) SpringContextUtil.getBean("dashboardModule");
		return dashboardModule;
	}

	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}
	
	public ReportModule getReportModule() {
		if(reportModule == null)
			reportModule = (ReportModule) SpringContextUtil.getBean("reportModule");
		return reportModule;
	}
	
	public void setResourceDriverModule(ResourceDriverModule resourceDriverModule) {
		this.resourceDriverModule = resourceDriverModule;
	}
	
	public ResourceDriverModule getResourceDriverModule() {
		if(resourceDriverModule == null)
			resourceDriverModule = (ResourceDriverModule) SpringContextUtil.getBean("resourceDriverModule");
		return resourceDriverModule;
	}
	
	public ConferencingModule getConferencingModule() {
		if(conferencingModule == null)
			conferencingModule = (ConferencingModule) SpringContextUtil.getBean("conferencingModule");
		return conferencingModule;
	}

	public void setConferencingModule(ConferencingModule conferencingModule) {
		this.conferencingModule = conferencingModule;
	}
	
	public IcalModule getIcalModule() {
		if(icalModule == null)
			icalModule = (IcalModule) SpringContextUtil.getBean("icalModule");
		return icalModule;
	}
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}	
	
	public RssModule getRssModule() {
		if(rssModule == null)
			rssModule = (RssModule) SpringContextUtil.getBean("rssModule");
		return rssModule;
	}
	public void setRssModule(RssModule rssModule) {
		this.rssModule = rssModule;
	}
	
	public LicenseModule getLicenseModule() {
		if(licenseModule == null)
			licenseModule = (LicenseModule) SpringContextUtil.getBean("licenseModule");
		return licenseModule;
	}
	public void setLicenseModule(LicenseModule licenseModule) {
		this.licenseModule = licenseModule;
	}
	
	public ZoneModule getZoneModule() {
		if(zoneModule == null)
			zoneModule = (ZoneModule) SpringContextUtil.getBean("zoneModule");
		return zoneModule;
	}
	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}
	
	public SharingModule getSharingModule() {
		if(sharingModule == null)
			sharingModule = (SharingModule) SpringContextUtil.getBean("sharingModule");
		return sharingModule;
	}
	public void setSharingModule(SharingModule sharingModule) {
		this.sharingModule = sharingModule;
	}

}
