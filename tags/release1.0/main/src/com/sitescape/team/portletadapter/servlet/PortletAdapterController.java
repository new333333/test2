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
package com.sitescape.team.portletadapter.servlet;

import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.portletadapter.portlet.ActionRequestImpl;
import com.sitescape.team.portletadapter.portlet.ActionResponseImpl;
import com.sitescape.team.portletadapter.portlet.RenderRequestImpl;
import com.sitescape.team.portletadapter.portlet.RenderResponseImpl;
import com.sitescape.team.portletadapter.support.AdaptedPortlets;
import com.sitescape.team.portletadapter.support.KeyNames;
import com.sitescape.team.portletadapter.support.PortletInfo;
import com.sitescape.team.util.SPropsUtil;
import com.sitescape.team.web.servlet.SAbstractController;

/**
 * This controller invokes portlet.
 * 
 * @author jong
 *
 */
public class PortletAdapterController extends SAbstractController {
	
	// This class overrides handleRequestInternal instead of handleRequestAfterValidation
	// that regular controllers would normally extend. That is because this portletadapter
	// (which is in itself a framework) would pass those paramaters to our portlet super
	// controller, which would perform the same validation before passing the data to
	// the individual portlet application controller. Therefore we can safely skip the
	// validation at servlet level in this case.
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {

		Map data = req.getParameterMap();
		String portletName = RequestUtils.getRequiredStringParameter(req,
				KeyNames.PORTLET_URL_PORTLET_NAME);

		PortletInfo portletInfo = (PortletInfo) AdaptedPortlets.getPortletInfo(portletName);
		if (portletInfo == null)
			throw new ServletException("Specified portlet '" + portletName
					+ "' is not recognized by the adapter");

		Portlet portlet = portletInfo.getPortlet();

		int actionInt = RequestUtils.getIntParameter(req,
				KeyNames.PORTLET_URL_ACTION, 0);

		try {
			Map params = null;
			
			if (actionInt == 1) {
				ActionRequestImpl actionReq = new ActionRequestImpl(req,
						portletInfo, AdaptedPortlets.getPortletContext());
				
				ActionResponseImpl actionRes = new ActionResponseImpl(
						actionReq, res, portletName);
				
				actionReq.defineObjects(portletInfo.getPortletConfig(), actionRes);

				portlet.processAction(actionReq, actionRes);
				
				params = actionRes.getRenderParameters();
				
				params.put(KeyNames.PORTLET_URL_PORTLET_NAME, new String[] {portletName});
			} 
			
			RenderRequestImpl renderReq = new RenderRequestImpl(req,
					portletInfo, AdaptedPortlets.getPortletContext());
			
			if(params != null)
				renderReq.setRenderParameters(params);
			
			RenderResponseImpl renderRes = new RenderResponseImpl(renderReq,
					res, portletName);
			String charEncoding = SPropsUtil.getString("web.char.encoding", "UTF-8");
			renderRes.setContentType("text/html; charset=" + charEncoding);
			renderReq.defineObjects(portletInfo.getPortletConfig(), renderRes);
			
			portlet.render(renderReq, renderRes);
			
		} catch (PortletException e) {
			throw new ServletException(e);
		}
		return null;
	}

}
