/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
import com.sitescape.team.module.binder.BinderModule;
import com.sitescape.team.module.dashboard.DashboardModule;
import com.sitescape.team.module.definition.DefinitionModule;
import com.sitescape.team.module.file.FileModule;
import com.sitescape.team.module.folder.FolderModule;
import com.sitescape.team.module.ic.ICBrokerModule;
import com.sitescape.team.module.ical.IcalModule;
import com.sitescape.team.module.ical.impl.IcalModuleImpl;
import com.sitescape.team.module.ldap.LdapModule;
import com.sitescape.team.module.profile.ProfileModule;
import com.sitescape.team.module.report.ReportModule;
import com.sitescape.team.module.rss.RssModule;
import com.sitescape.team.module.workflow.WorkflowModule;
import com.sitescape.team.module.workspace.WorkspaceModule;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.util.AllModulesInjected;
import com.sitescape.team.util.XSSCheck;

public abstract class SAbstractController extends AbstractController 
implements AllModulesInjected {

	protected Log logger = LogFactory.getLog(getClass());
	
	private WorkspaceModule workspaceModule;
	private FolderModule folderModule;
	private AdminModule adminModule;
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

	protected void handleActionRequestInternal(ActionRequest request, ActionResponse response)
	throws Exception {
		Map formData = request.getParameterMap();
		Map newFormData = XSSCheck.check(formData);
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
