package com.sitescape.team.web.portlet;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.web.portlet.mvc.AbstractController;

import com.sitescape.ef.module.sample.EmployeeModule;
import com.sitescape.ef.module.file.FileModule;
import com.sitescape.ef.module.folder.FolderModule;
import com.sitescape.ef.module.workspace.WorkspaceModule;
import com.sitescape.ef.module.dashboard.DashboardModule;
import com.sitescape.ef.module.ldap.LdapModule;
import com.sitescape.ef.module.profile.ProfileModule;
import com.sitescape.ef.module.admin.AdminModule;
import com.sitescape.ef.module.definition.DefinitionModule;
import com.sitescape.ef.module.workflow.WorkflowModule;
import com.sitescape.ef.module.binder.BinderModule;
import com.sitescape.team.portletadapter.MultipartFileSupport;
import com.sitescape.team.rss.RssGenerator;
import com.sitescape.team.util.AllBusinessServicesInjected;
import com.sitescape.team.util.XSSCheck;

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
	private DashboardModule dashboardModule;
	
	public RssGenerator getRssGenerator() {
		return rssGenerator;
	}

	/**
	 * @param rssGenerator The rssGenerator to set.
	 */
	public void setRssGenerator(RssGenerator rssGenerator) {
		this.rssGenerator = rssGenerator;
	}

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
	
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
	
	public DashboardModule getDashboardModule() {
		return dashboardModule;
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
