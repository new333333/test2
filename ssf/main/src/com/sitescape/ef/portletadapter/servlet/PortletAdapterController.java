package com.sitescape.ef.portletadapter.servlet;

import java.util.Map;

import javax.portlet.Portlet;
import javax.portlet.PortletException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.bind.RequestUtils;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.ef.portletadapter.portlet.ActionRequestImpl;
import com.sitescape.ef.portletadapter.portlet.ActionResponseImpl;
import com.sitescape.ef.portletadapter.portlet.RenderRequestImpl;
import com.sitescape.ef.portletadapter.portlet.RenderResponseImpl;
import com.sitescape.ef.portletadapter.support.AdaptedPortlets;
import com.sitescape.ef.portletadapter.support.KeyNames;
import com.sitescape.ef.portletadapter.support.PortletInfo;
import com.sitescape.ef.web.servlet.SAbstractController;

/**
 * This controller invokes portlet.
 * 
 * @author jong
 *
 */
public class PortletAdapterController extends SAbstractController {
	
	protected ModelAndView handleRequestInternal(HttpServletRequest req, HttpServletResponse res) throws Exception {

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
			
			renderReq.defineObjects(portletInfo.getPortletConfig(), renderRes);
			
			portlet.render(renderReq, renderRes);
			
		} catch (PortletException e) {
			throw new ServletException(e);
		}
		
		return null;
	}
}
