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
package com.sitescape.team.web.portlet;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.portlet.mvc.AbstractController;

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
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.rss.RssModule;
import com.sitescape.team.module.template.TemplateModule;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.module.zone.ZoneModule;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.stringcheck.StringCheckUtil;

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

	public void setIcBrokerModule(ICBrokerModule icBrokerModule) {
		this.icBrokerModule = icBrokerModule;
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

	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		Map formData = request.getParameterMap();
		Map newFormData = StringCheckUtil.check(formData);
		ActionRequest newReq;
		if(newFormData != formData) {
			if(request instanceof MultipartFileSupport)
				newReq = new ParamsWrappedActionRequestWithMultipartFileSupport(request, newFormData);
			else
				newReq = new ParamsWrappedActionRequest(request, newFormData);
		}
		else {
			newReq = request;
		}
		handleActionRequestAfterValidation(newReq, response);
	}
	
	/**
	 * <p>Subclasses are meant to override this method if the controller 
	 * is expected to handle action requests.</p>
	 * <p>Default implementation throws a PortletException.</p>
	 * <p>The contract is the same as for handleActionRequestInternal.</p>
	 * @see #handleActionRequestInternal
	 */
	protected void handleActionRequestAfterValidation(ActionRequest request, ActionResponse response)
		throws Exception {
	    throw new PortletException("This controller does not handle action requests");
	}

}
