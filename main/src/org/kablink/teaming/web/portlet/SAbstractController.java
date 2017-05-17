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
package org.kablink.teaming.web.portlet;

import java.util.Map;

import javax.portlet.ActionRequest;
import javax.portlet.ActionResponse;
import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

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
import org.kablink.teaming.portletadapter.MultipartFileSupport;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.stringcheck.StringCheckUtil;
import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.portlet.mvc.AbstractController;

/**
 * ?
 * 
 * @author ?
 */
@SuppressWarnings("unchecked")
public abstract class SAbstractController extends AbstractController implements AllModulesInjected {
	protected Log logger = LogFactory.getLog(getClass());
	
	private WorkspaceModule workspaceModule;
	private FolderModule folderModule;
	private NetFolderModule netFolderModule;
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
	private MobileDeviceModule mobileDeviceModule;
	private KeyShieldModule keyShieldModule;
	private ProxyIdentityModule proxyIdentityModule;

	@Override
	public void setBinderModule(BinderModule binderModule) {
		this.binderModule = binderModule;
	}
	
	@Override
	public BinderModule getBinderModule() {
		return binderModule;
	}

	@Override
	public void setWorkspaceModule(WorkspaceModule workspaceModule) {
		this.workspaceModule = workspaceModule;
	}
	
	@Override
	public WorkspaceModule getWorkspaceModule() {
		return workspaceModule;
	}

	@Override
	public void setFolderModule(FolderModule folderModule) {
		this.folderModule = folderModule;
	}
	
	@Override
	public FolderModule getFolderModule() {
		return folderModule;
	}
	
	@Override
	public void setNetFolderModule(NetFolderModule netFolderModule) {
		this.netFolderModule = netFolderModule;
	}
	
	@Override
	public NetFolderModule getNetFolderModule() {
		return netFolderModule;
	}
	
    @Override
	public TemplateModule getTemplateModule() {
    	return templateModule;
    }
    @Override
	public void setTemplateModule(TemplateModule templateModule) {
    	this.templateModule = templateModule;
    }

	@Override
	public void setAdminModule(AdminModule adminModule) {
		this.adminModule = adminModule;
	}
	
	@Override
	public AdminModule getAdminModule() {
		return adminModule;
	}

	@Override
	public void setAuthenticationModule(AuthenticationModule authenticationModule) {
		this.authenticationModule = authenticationModule;
	}
	
	@Override
	public AuthenticationModule getAuthenticationModule() {
		return authenticationModule;
	}

	@Override
	public void setProfileModule(ProfileModule profileModule) {
		this.profileModule = profileModule;
	}
	
	@Override
	public ProfileModule getProfileModule() {
		return profileModule;
	}
	
	@Override
	public void setDefinitionModule(DefinitionModule definitionModule) {
		this.definitionModule = definitionModule;
	}
	
	@Override
	public DefinitionModule getDefinitionModule() {
		return definitionModule;
	}

	@Override
	public WorkflowModule getWorkflowModule() {
		return workflowModule;
	}

	@Override
	public void setWorkflowModule(WorkflowModule workflowModule) {
		this.workflowModule = workflowModule;
	}
	
	@Override
	public void setLdapModule(LdapModule ldapModule) {
		this.ldapModule = ldapModule;
	}
	
	@Override
	public LdapModule getLdapModule() {
		return ldapModule;
	}
	
	@Override
	public void setFileModule(FileModule fileModule) {
		this.fileModule = fileModule;
	}
	
	@Override
	public FileModule getFileModule() {
		return fileModule;
	}
	
	@Override
	public ConvertedFileModule getConvertedFileModule() {
		return convertedFileModule;
	}

	@Override
	public void setConvertedFileModule(ConvertedFileModule convertedFileModule) {
		this.convertedFileModule = convertedFileModule;
	}

	@Override
	public void setDashboardModule(DashboardModule dashboardModule) {
		this.dashboardModule = dashboardModule;
	}
	
	@Override
	public DashboardModule getDashboardModule() {
		return dashboardModule;
	}
	
	@Override
	public void setReportModule(ReportModule reportModule) {
		this.reportModule = reportModule;
	}
	
	@Override
	public ReportModule getReportModule() {
		return reportModule;
	}
	
	@Override
	public void setResourceDriverModule(ResourceDriverModule resourceDriverModule) {
		this.resourceDriverModule = resourceDriverModule;
	}
	@Override
	public ResourceDriverModule getResourceDriverModule() {
		return resourceDriverModule;
	}
	
	@Override
	public ConferencingModule getConferencingModule() {
		return conferencingModule;
	}

	@Override
	public void setConferencingModule(ConferencingModule conferencingModule) {
		this.conferencingModule = conferencingModule;
	}

	@Override
	public IcalModule getIcalModule() {
		return icalModule;
	}
	@Override
	public void setIcalModule(IcalModule icalModule) {
		this.icalModule = icalModule;
	}
	
	@Override
	public RssModule getRssModule() {
		return rssModule;
	}
	@Override
	public void setRssModule(RssModule rssModule) {
		this.rssModule = rssModule;
	}

	@Override
	public LicenseModule getLicenseModule() {
		return licenseModule;
	}
	@Override
	public void setLicenseModule(LicenseModule licenseModule) {
		this.licenseModule = licenseModule;
	}

	@Override
	public ZoneModule getZoneModule() {
		return zoneModule;
	}
	@Override
	public void setZoneModule(ZoneModule zoneModule) {
		this.zoneModule = zoneModule;
	}

	@Override
	public SharingModule getSharingModule() {
		if(sharingModule == null)
			sharingModule = (SharingModule) SpringContextUtil.getBean("sharingModule");
		return sharingModule;
	}
	@Override
	public void setSharingModule(SharingModule sharingModule) {
		this.sharingModule = sharingModule;
	}

	@Override
	public MobileDeviceModule getMobileDeviceModule() {
		if (null == mobileDeviceModule) {
			mobileDeviceModule = ((MobileDeviceModule) SpringContextUtil.getBean("mobileDeviceModule"));
		}
		return mobileDeviceModule;
	}
	@Override
	public void setMobileDeviceModule(MobileDeviceModule mobileDeviceModule) {
		this.mobileDeviceModule = mobileDeviceModule;
	}

	@Override
	public KeyShieldModule getKeyShieldModule() {
		if (null == keyShieldModule) {
			keyShieldModule = ((KeyShieldModule) SpringContextUtil.getBean("keyShieldModule"));
		}
		return keyShieldModule;
	}
	@Override
	public void setKeyShieldModule(KeyShieldModule keyShieldModule) {
		this.keyShieldModule = keyShieldModule;
	}

	@Override
	public ProxyIdentityModule getProxyIdentityModule() {
		if (null == proxyIdentityModule) {
			proxyIdentityModule = ((ProxyIdentityModule) SpringContextUtil.getBean("proxyIdentityModule"));
		}
		return proxyIdentityModule;
	}
	@Override
	public void setProxyIdentityModule(ProxyIdentityModule proxyIdentityModule) {
		this.proxyIdentityModule = proxyIdentityModule;
	}

	@Override
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

	@Override
	protected ModelAndView handleRenderRequestInternal(RenderRequest request, RenderResponse response)
	throws Exception {
		Map formData = request.getParameterMap();
		Map newFormData = StringCheckUtil.check(formData);
		RenderRequest newReq;
		if(newFormData != formData) {
			if(request instanceof MultipartFileSupport)
				newReq = new ParamsWrappedRenderRequestWithMultipartFileSupport(request, newFormData);
			else
				newReq = new ParamsWrappedRenderRequest(request, newFormData);
		}
		else {
			newReq = request;
		}
		return handleRenderRequestAfterValidation(newReq, response);
	}

	protected ModelAndView handleRenderRequestAfterValidation(RenderRequest request, RenderResponse response)
	throws Exception {
		throw new PortletException("This controller does not handle render requests");
	}
}
