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
package org.kablink.teaming.portletadapter.servlet;

import java.util.HashMap;
import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.kablink.teaming.portletadapter.portlet.ActionRequestImpl;
import org.kablink.teaming.portletadapter.portlet.ActionResponseImpl;
import org.kablink.teaming.portletadapter.portlet.RenderRequestImpl;
import org.kablink.teaming.portletadapter.portlet.RenderResponseImpl;
import org.kablink.teaming.portletadapter.support.AdaptedPortlets;
import org.kablink.teaming.portletadapter.support.KeyNames;
import org.kablink.teaming.portletadapter.support.PortletInfo;
import org.kablink.teaming.util.SPropsUtil;
import org.kablink.teaming.web.servlet.ParamsWrappedHttpServletRequest;
import org.kablink.util.StringUtil;
import org.kablink.util.Validator;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;


/**
 * This controller invokes portlet.
 * 
 * @author jong
 *
 */
public class BasePortletAdapterController extends AbstractController {
	
	// This class overrides handleRequestInternal instead of handleRequestAfterValidation
	// that regular controllers would normally extend. That is because this portletadapter
	// (which is in itself a framework) would pass those paramaters to our portlet super
	// controller, which would perform the same validation before passing the data to
	// the individual portlet application controller. Therefore we can safely skip the
	// validation at servlet level in this case.
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {
		req = getRequest(req);
		
		String portletName = ServletRequestUtils.getRequiredStringParameter(req,
				KeyNames.PORTLET_URL_PORTLET_NAME);

		PortletInfo portletInfo = (PortletInfo) AdaptedPortlets.getPortletInfo(portletName);
		if (portletInfo == null)
			throw new ServletException("Specified portlet '" + portletName
					+ "' is not recognized by the adapter");

		Portlet portlet = portletInfo.getPortlet();

		int actionInt = ServletRequestUtils.getIntParameter(req,
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
				
				String redirectLocation = actionRes.getRedirectLocation();
				if(Validator.isNotNull(redirectLocation)) {
					res.sendRedirect(redirectLocation);
					return null;
				}
				
				params = actionRes.getRenderParameterMap();
				
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

	protected HttpServletRequest getRequest(HttpServletRequest req) {
		String pathInfo = req.getPathInfo();
		if(pathInfo.startsWith("/c/")) { // adapter url for crawler
			Map pathParams = getPathParams(pathInfo.substring(3));
			if(pathParams != null && pathParams.size() > 0) {
				pathParams.putAll(req.getParameterMap());
				return new ParamsWrappedHttpServletRequest(req, pathParams);
			}
		}
		return req;
	}
	
	protected Map<String, String[]> getPathParams(String pathInfo) {
		if(pathInfo == null)
			return null;
		String[] pathElems = StringUtil.split(pathInfo, "/");
		if(pathElems == null || pathElems.length < 2)
			return null;
		Map map = new HashMap();
		int count = pathElems.length / 2;
		for(int i = 0; i < count; i++) {
			map.put(pathElems[i*2], new String[]{pathElems[i*2+1]});
		}
		return map;
	}
	
}
