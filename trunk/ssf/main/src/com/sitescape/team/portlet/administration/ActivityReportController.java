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

import org.dom4j.Document;

import com.sitescape.team.context.request.RequestContextHolder;
import com.sitescape.team.domain.Binder;
import com.sitescape.team.domain.Folder;
import com.sitescape.team.module.binder.BinderModule.BinderOperation;
import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.tree.SearchTreeHelper;
import com.sitescape.team.web.tree.WsDomTreeBuilder;
import com.sitescape.team.web.util.BinderHelper;
import com.sitescape.team.web.util.PortletRequestUtils;

public class ActivityReportController extends  AbstractReportController {
	
	protected void populateModel(RenderRequest request, Map model) {
		super.populateModel(request, model);
		Long binderId = PortletRequestUtils.getLongParameter(request, WebKeys.URL_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId());


		Binder binder = null;
		try {
			binder = getBinderModule().getBinder(binderId);
		} catch (Exception ex) {
			binder = getWorkspaceModule().getWorkspace();				
		}
		//Initialize the acl bean
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		accessControlMap.put("generateReport", getBinderModule().testAccess(binder, BinderOperation.report));
		Document wsTree = null;
		if(binder instanceof Folder) {
			wsTree = getFolderModule().getDomFolderTree(binder.getId(), 
				new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
		} else {
			wsTree = getWorkspaceModule().getDomWorkspaceTree(binder.getId(), 
				new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
		}

		model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
		model.put(WebKeys.WORKSPACE_DOM_TREE, wsTree);		

		BinderHelper.buildNavigationLinkBeans(this, binder, model);
		model.put(WebKeys.DEFINITION_ENTRY, binder);
		model.put(WebKeys.BINDER, binder);
	}

	protected String chooseView(Map formData) {
		return WebKeys.VIEW_REPORT;
	}
}
