/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.portlet.administration;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.context.request.RequestContextHolder;
import org.kablink.teaming.module.admin.AdminModule.AdminOperation;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.teaming.web.tree.SearchTreeHelper;
import org.kablink.teaming.web.tree.WsDomTreeBuilder;
import org.kablink.teaming.web.util.BinderHelper;
import org.kablink.teaming.web.util.PortletRequestUtils;
import org.springframework.web.portlet.ModelAndView;

/**
 * This class handles the XSS Report.
 * 
 * @author Peter Hurley
 */
@SuppressWarnings({"unchecked", "unused"})
public class XssReportController extends  AbstractReportController {
	/**
	 */
	@Override
	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
			RenderResponse response) throws Exception {
		PortletURL	url;
		Map formData;
		Map model;

		formData = request.getParameterMap();
		
		model = new HashMap();
		populateModel( request, model );
	
		Boolean gwtReport= PortletRequestUtils.getBooleanParameter(request, WebKeys.URL_GWT_REPORT);
		if (null == gwtReport) {
			gwtReport = Boolean.FALSE;
		}
		model.put(WebKeys.URL_GWT_REPORT, String.valueOf(gwtReport));
		
		Document pTree = DocumentHelper.createDocument();
    	Element rootElement = pTree.addElement(DomTreeBuilder.NODE_ROOT);
    	Document wsTree = getBinderModule().getDomBinderTree(RequestContextHolder.getRequestContext().getZoneId(), 
				new WsDomTreeBuilder(null, true, this, new SearchTreeHelper()),1);
    	//merge the trees
    	rootElement.appendAttributes(wsTree.getRootElement());
    	rootElement.appendContent(wsTree.getRootElement());
 		model.put(WebKeys.WORKSPACE_DOM_TREE_BINDER_ID, RequestContextHolder.getRequestContext().getZoneId().toString());
		model.put(WebKeys.WORKSPACE_DOM_TREE, pTree);		
		
		return new ModelAndView( chooseView( formData ), model );
	}
	
	/**
	 */
	@Override
	protected void populateModel( RenderRequest request, Map model ) {
		
		super.populateModel(request, model);

		//Initialize the acl bean
		Map accessControlMap = BinderHelper.getAccessControlMapBean(model);
		accessControlMap.put("generateReport", getAdminModule().testAccess(AdminOperation.report));
	}

	/**
	 */
	@Override
	protected String chooseView( Map formData ) {
		return WebKeys.VIEW_XSS_REPORT;
	}
}
