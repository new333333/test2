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
