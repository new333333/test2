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
package org.kablink.teaming.web.servlet;

import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


public abstract class SAbstractController extends AbstractController
implements AllModulesInjected {
	
	protected Log logger = LogFactory.getLog(getClass());
	
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
	private IcalModule icalModule;
	private ConferencingModule conferencingModule;
	private RssModule rssModule;
	private LicenseModule licenseModule;
	private ZoneModule zoneModule;
	private SharingModule sharingModule;
	
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
	
    public TemplateModule getTemplateModule() {
    	return templateModule;
    }
    public void setTemplateModule(TemplateModule templateModule) {
    	this.templateModule = templateModule;
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

	public ConvertedFileModule getConvertedFileModule() {
		return convertedFileModule;
	}

	public void setConvertedFileModule(ConvertedFileModule convertedFileModule) {
		this.convertedFileModule = convertedFileModule;
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
	
	public void setResourceDriverModule(ResourceDriverModule resourceDriverModule) {
		this.resourceDriverModule = resourceDriverModule;
	}
	
	public ResourceDriverModule getResourceDriverModule() {
		return resourceDriverModule;
	}
	
	public ConferencingModule getConferencingModule() {
		return conferencingModule;
	}

	public void setConferencingModule(ConferencingModule conferencingModule) {
		this.conferencingModule = conferencingModule;
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

	public SharingModule getSharingModule() {
		if(sharingModule == null)
			sharingModule = (SharingModule) SpringContextUtil.getBean("sharingModule");
		return sharingModule;
	}
	public void setSharingModule(SharingModule sharingModule) {
		this.sharingModule = sharingModule;
	}

	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {
		Map formData = req.getParameterMap();
		Map newFormData = StringCheckUtil.check(formData);
		HttpServletRequest newReq;
		if(newFormData != formData) {
			newReq = new ParamsWrappedHttpServletRequest(req, newFormData);
		}
		else {
			newReq = req;
		}
		return handleRequestAfterValidation(newReq, res);
	}
	
	/**
	 * Subclasses must override this method.
	 * Default implementation throws a ServletException
	 */
	protected ModelAndView handleRequestAfterValidation(HttpServletRequest req, HttpServletResponse res) throws Exception {
	    throw new ServletException("Subclass must override this method");
	}

}
