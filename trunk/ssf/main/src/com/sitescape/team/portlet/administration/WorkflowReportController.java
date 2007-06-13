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
package com.sitescape.team.portlet.administration;
import java.util.Map;

import javax.portlet.RenderRequest;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.FolderEntry;
import com.sitescape.team.module.folder.FolderModule.FolderOperation;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

public class WorkflowReportController extends  AbstractReportController {
	
	protected void populateModel(RenderRequest request, Map model) {
		super.populateModel(request, model);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId());
		Long entryId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_ENTRY_ID, RequestContextHolder.getRequestContext().getZoneId());
		model.put(WebKeys.ENTRY_ID, entryId);
		FolderEntry entry = getFolderModule().getEntry(binderId, entryId);
		//Initialize the acl bean
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		accessControlMap.put("generateLoginReport", getFolderModule().testAccess(entry, FolderOperation.report));
	}

	protected String chooseView(Map formData) {
		return WebKeys.VIEW_REPORT;
	}
}
