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
package org.kablink.teaming.gwt.server.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Node;

import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.domain.Binder;
import org.kablink.teaming.domain.Definition;
import org.kablink.teaming.domain.Folder;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserProperties;
import org.kablink.teaming.gwt.client.GwtTeamingException;
import org.kablink.teaming.gwt.client.rpc.shared.HtmlElementInfoRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.JspHtmlRpcResponseData;
import org.kablink.teaming.gwt.client.rpc.shared.VibeJspHtmlType;
import org.kablink.teaming.gwt.client.rpc.shared.HtmlElementInfoRpcResponseData.HtmlElementInfo;
import org.kablink.teaming.gwt.client.util.BinderInfo;
import org.kablink.teaming.util.AllModulesInjected;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.web.util.MiscUtil;

/**
 * Helper methods for HTML element panels embedded GWT views.
 *
 * @author drfoster@novell.com
 */
public class GwtHtmlElementHelper {
	protected static Log m_logger = LogFactory.getLog(GwtHtmlElementHelper.class);

	/*
	 * Class constructor that prevents this class from being
	 * instantiated.
	 */
	private GwtHtmlElementHelper() {
		// Nothing to do.
	}
	
	/**
	 * Returns true if a binder's view definition has user_list
	 * <item>'s and false otherwise.
	 * 
	 * @param bs
	 * @param request
	 * @param binderInfo
	 * 
	 * @return
	 */
	public static boolean getBinderHasHtmlElement(AllModulesInjected bs, HttpServletRequest request, BinderInfo binderInfo) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtHtmlElementHelper.getBinderHasHtmlElement(1)");
		try {
			// Does the folder have any user_list <item>'s?
			Binder binder = bs.getBinderModule().getBinder(binderInfo.getBinderIdAsLong());
			return getBinderHasHtmlElement(binder);
		}
		
		catch (Exception e) {
			// Log the error and assume there are no user_list's.
			GwtLogHelper.error(m_logger, "GwtHtmlElementHelper.getBinderHasHtmlElement( 1: SOURCE EXCEPTION ):  ", e);
			return false;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	public static boolean getBinderHasHtmlElement(Binder binder) {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtHtmlElementHelper.getBinderHasHtmlElement(2)");
		try {
			// Does the binder have any HTML <item>'s?
			List<Node> userListNodes = getBinderHtmlElementNodes(binder);
			return MiscUtil.hasItems(userListNodes);
		}
		
		catch (Exception e) {
			// Log the error and assume there are no user_list's.
			GwtLogHelper.error(m_logger, "GwtHtmlElementHelper.getBinderHasHtmlElement( 2: SOURCE EXCEPTION ):  ", e);
			return false;
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/**
	 * Returns a HtmlElementInfoRpcResponseData corresponding to the
	 * HTML <item>'s from a binder view definition.
	 * 
	 * @param bs
	 * @param request
	 * @param response
	 * @param servletContext
	 * @param binderInfo
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static HtmlElementInfoRpcResponseData getBinderHtmlElementInfo(AllModulesInjected bs, HttpServletRequest request, HttpServletResponse response, ServletContext servletContext, BinderInfo binderInfo) throws GwtTeamingException {
		GwtServerProfiler gsp = GwtServerProfiler.start(m_logger, "GwtHtmlElementHelper.getBinderHtmlElementInfo()");
		try {
			// Allocate a HtmlElementInfoRpcResponseData we can fill in
			// and return.
			HtmlElementInfoRpcResponseData reply = new HtmlElementInfoRpcResponseData();

			// Does the folder have any user_list <item>'s?
			Binder binder = bs.getBinderModule().getBinder(binderInfo.getBinderIdAsLong());
			List<Node> htmlElementNodes = getBinderHtmlElementNodes(binder);
			int htmlElementCount = ((null == htmlElementNodes) ? 0 : htmlElementNodes.size());
			if (0 < htmlElementCount) {
				// Yes!  Scan them.
				for (Node htmlElementNode:  htmlElementNodes) {
					// Determine this <item>'s caption.
					Node captionNode = htmlElementNode.selectSingleNode("properties/property[@name='caption']");
					String caption;
					if (null == captionNode) {
						caption = NLT.get("__html");
						int count = reply.getHtmlElementInfoListCount();
						if (0 < count) {
							caption += ("_" + (count + 1));
						}
					}
					else {
						caption = ((Element) captionNode).attributeValue("value");
					}

					// Does this <item> have a data name?
					Node dataNameNode = htmlElementNode.selectSingleNode("properties/property[@name='name']");
					if (null != dataNameNode) {
						// Yes!  Extract its data name.
						String dataName = ((Element) dataNameNode).attributeValue("value");

						// Is there a custom JSP specified?
						String htmlTop;
						String htmlBottom;
						Node customJspNode = htmlElementNode.selectSingleNode("jsps/jsp[@name='custom']");
						String customJsp = ((null == customJspNode) ? "" : ((Element) customJspNode).attributeValue("value"));
						String customJspHtml;
						if (MiscUtil.hasString(customJsp)) {
							// Yes!  Execute it to generate its HTML...
							Map<String, Object> model = new HashMap<String,Object>();
							model.put("binderId",  binderInfo.getBinderId());
							model.put("customJsp", customJsp               );
							JspHtmlRpcResponseData customJspData = GwtViewHelper.getJspHtml(
								bs,
								request,
								response,
								servletContext,
								VibeJspHtmlType.CUSTOM_JSP,
								model);
							customJspHtml = customJspData.getHtml();

							// ...and assume there is no HTML top/bottom.
							htmlTop    =
							htmlBottom = "";
						}
						
						else {
							// No, there wasn't a custom JSP specified!
							// Assume no custom JSP HTML...
							customJspHtml = "";

							// ...and look for any HTML top/bottom nodes.
							Node htmlTopNode = htmlElementNode.selectSingleNode("properties/property[@name='htmlTop']");
							htmlTop = ((null == htmlTopNode) ? "" : htmlTopNode.getText());
							
							Node htmlBottomNode = htmlElementNode.selectSingleNode("properties/property[@name='htmlBottom']");
							htmlBottom = ((null == htmlBottomNode) ? "" : htmlBottomNode.getText());
						}
						
						// Finally, create the HtmlElementInfo for it.
						HtmlElementInfo htmlElementInfo = new HtmlElementInfo(caption, dataName, htmlTop, htmlBottom, customJspHtml);
						reply.addHtmlElementInfo(htmlElementInfo);
					}
				}
			}
			
			return reply;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtHtmlElementHelper.getBinderHtmlElementInfo( SOURCE EXCEPTION ):  ");
		}
		
		finally {
			gsp.stop();
		}
	}
	
	/*
	 * Returns a List<Node> of the HTML <item>'s from a binder's view
	 * definition.
	 */
	@SuppressWarnings("unchecked")
	private static List<Node> getBinderHtmlElementNodes(Binder binder) {
		// Do we have a Binder?
		List<Node> reply = null;
		if (null != binder) {
			// Yes!  Can we access it's view definition document?
			Definition viewDef    = binder.getDefaultViewDef();
			Document   viewDefDoc = ((null == viewDef) ? null : viewDef.getDefinition());
			if (null != viewDefDoc) {
				// Yes!  Does it contain any HTML <item>'s?
				String viewName;
				if (binder instanceof Folder)
				     viewName = "forumView";
				else viewName = "workspaceView";
		  		reply = viewDefDoc.selectNodes("//item[@name='" + viewName + "']//item[@name='htmlView']");
			}
		}
		
		// If we get here, reply refers to a List<Node> of the binder's
		// HTML <item>'s or is null.  Return it.
		return reply;
	}
	
	/**
	 * Return true of the HTML element panel should be visible on the
	 * given binder and false otherwise.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean getHtmlElementStatus(AllModulesInjected bs, HttpServletRequest request, Long binderId) throws GwtTeamingException {
		try {
			User			user                 = GwtServerHelper.getCurrentUser();
			UserProperties	userFolderProperties = bs.getProfileModule().getUserProperties(user.getId(), binderId);
			
			// Has the user saved the status of the HTML element panel
			// on this binder?
			Boolean htmlElementStatus = ((Boolean) userFolderProperties.getProperty(ObjectKeys.USER_PROPERTY_BINDER_SHOW_HTML_ELEMENT));
			if (null == htmlElementStatus) {
				// No!  Then we default to show it.  Save this status
				// in the user's properties for this binder.
				htmlElementStatus = Boolean.TRUE;
				saveHtmlElementStatus(bs, request, binderId, htmlElementStatus);
			}
			
			// If we get here, htmlElementStatus contains true if we
			// should show the HTML element panel and false otherwise.
			// Return it.
			return htmlElementStatus;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtHtmlElementHelper.getHtmlElementStatus( SOURCE EXCEPTION ):  ");
		}
	}
	
	/**
	 * Saves whether the HTML element panel should be visible on the
	 * given binder.
	 * 
	 * @param bs
	 * @param request
	 * @param binderId
	 * @param showHtmlElementPanel
	 * 
	 * @return
	 * 
	 * @throws GwtTeamingException
	 */
	public static Boolean saveHtmlElementStatus(AllModulesInjected bs, HttpServletRequest request, Long binderId, boolean showHtmlElementPanel) throws GwtTeamingException {
		try {
			// Save the HTML element status...
			bs.getProfileModule().setUserProperty(
				GwtServerHelper.getCurrentUserId(),
				binderId,
				ObjectKeys.USER_PROPERTY_BINDER_SHOW_HTML_ELEMENT,
				new Boolean(showHtmlElementPanel));
			
			// ...and return true.
			return Boolean.TRUE;
		}
		
		catch (Exception e) {
			// Convert the exception to a GwtTeamingException and throw
			// that.
			throw
				GwtLogHelper.getGwtClientException(
					m_logger,
					e,
					"GwtHtmlElementHelper.saveHtmlElementStatus( SOURCE EXCEPTION ):  ");
		}
	}
}
