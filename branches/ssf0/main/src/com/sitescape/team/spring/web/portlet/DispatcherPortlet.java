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
package com.sitescape.team.spring.web.portlet;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.servlet.ViewRendererServlet;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;

public class DispatcherPortlet extends org.springframework.web.portlet.DispatcherPortlet {
	protected void render(ModelAndView mv, RenderRequest request, RenderResponse response)
	throws Exception {

		View view = null;
		if (mv.isReference()) {
			// We need to resolve the view name.
			view = resolveViewName(mv.getViewName(), mv.getModel(), request);
			if (view == null) {
				throw new PortletException("Could not resolve view with name '" + mv.getViewName() +
						"' in portlet with name '" + getPortletName() + "'");
			}
		}
		else {
			// No need to lookup: the ModelAndView object contains the actual View object.
			view = mv.getView();
			if (view == null) {
				throw new PortletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
						"View object in portlet with name '" + getPortletName() + "'");
			}
		}
		
		// These attributes are required by the ViewRendererServlet
		request.setAttribute(ViewRendererServlet.VIEW_ATTRIBUTE, view);
		request.setAttribute(ViewRendererServlet.MODEL_ATTRIBUTE, mv.getModel());
		request.setAttribute(ViewRendererServlet.WEB_APPLICATION_CONTEXT_ATTRIBUTE, getPortletApplicationContext());
		
		// include the content of the view in the render response
		getPortletContext().getRequestDispatcher(getPortletContextAttributeName()).include(request, response);
	}
}
