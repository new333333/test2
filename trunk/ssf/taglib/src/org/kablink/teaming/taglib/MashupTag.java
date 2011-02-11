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
package org.kablink.teaming.taglib;

import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspTagException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.ObjectKeys;
import org.kablink.teaming.dao.ProfileDao;
import org.kablink.teaming.domain.Group;
import org.kablink.teaming.domain.Principal;
import org.kablink.teaming.domain.User;
import org.kablink.teaming.domain.UserPrincipal;
import org.kablink.teaming.module.profile.ProfileModule;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.SpringContextUtil;
import org.kablink.teaming.util.Utils;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.tree.DomTreeBuilder;
import org.kablink.util.servlet.StringServletResponse;


/**
 * Show the user's name and presence.
 * 
 * 
 * @author Peter Hurley
 * 
 */
public class MashupTag extends BodyTagSupport {

	protected static final Log logger = LogFactory.getLog(MashupTag.class);
	
	private String id = "";
	private String type = "";
	private String value = "";
	private String view = "";

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspTagException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			if (value != null && !value.equals("")) {
				String[] mashupItemValues = value.split(",");
				if (mashupItemValues.length > 0) {
					String type = mashupItemValues[0].trim();
					if (type != null && !type.equals("")) {
						//Build a map of attributes
						Map mashupItemAttributes = new HashMap();
						for (int i = 1; i < mashupItemValues.length; i++) {
							int k = mashupItemValues[i].indexOf("=");
							if (k > 0) {
								String a = mashupItemValues[i].substring(0, k);
								String v = mashupItemValues[i].substring(k+1, mashupItemValues[i].length());
								String value1 = v;
								try {
									value1 = URLDecoder.decode(v.replaceAll("\\+", "%2B"), "UTF-8");
								} catch(Exception e) {}
								mashupItemAttributes.put(a, value1);
							}
						}
						httpReq.setAttribute("mashup_id", id);
						httpReq.setAttribute("mashup_type", type);
						httpReq.setAttribute("mashup_values", mashupItemValues);
						httpReq.setAttribute("mashup_attributes", mashupItemAttributes);
						httpReq.setAttribute("mashup_view", view);
						
						// Output the start of the mashup table element
						String jsp = "/WEB-INF/jsp/tag_jsps/mashup/"+type+".jsp";
						
						// Are we working with a custom jsp?
						if (type.equals("customJsp") && !view.equals("form")) {
							// Yes
							if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_NAME) && 
									!mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_NAME).equals("")) {
								jsp = "/WEB-INF/jsp/custom_jsps/" + 
									mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_NAME);
								if (mashupItemAttributes.containsKey(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_PATH_TYPE) &&
										mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_PATH_TYPE).
										equals(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_PATH_TYPE_EXTENSION)) {
									jsp = "/WEB-INF/ext/" + Utils.getZoneKey() + "/" +
										mashupItemAttributes.get(ObjectKeys.MASHUP_ATTR_CUSTOM_JSP_NAME);
								}
							}
						}
						// Are we working with an enhanced view?
						else if ( type.equalsIgnoreCase( "enhancedView" ) && !view.equals( "form" ) )
						{
							// Yes
							if ( mashupItemAttributes.containsKey( ObjectKeys.MASHUP_ATTR_ENHANCED_VIEW_JSP_NAME) )
							{
								String jspName;
								
								// Get the name of the landing page extension jsp
								jspName = (String) mashupItemAttributes.get( ObjectKeys.MASHUP_ATTR_ENHANCED_VIEW_JSP_NAME );
								if ( jspName != null && jspName.length() > 0 )
								{
									jsp = "/WEB-INF/jsp/landing_page_enhanced_views/" + jspName;
								}
							}
						}
						
						RequestDispatcher rd = httpReq.getRequestDispatcher(jsp);
						ServletRequest req = pageContext.getRequest();
						StringServletResponse res = new StringServletResponse(httpRes);
						try {
							rd.include(req, res);
							pageContext.getOut().print(res.getString().trim());
						} catch (Exception e) {
							String errorTag = "errorcode.unexpectedError";
							if (type.equals("customJsp")) {
								errorTag = "mashup.customJspError";
							}
							String[] errorArgs = new String[] {e.getLocalizedMessage()};
							pageContext.getOut().print(NLT.get(errorTag, errorArgs));
						}
					}
				}
			}

		} catch (Exception e) {
			throw new JspTagException(e.getLocalizedMessage());
		} finally {
			type = "";
			id = "";
			value = "";
			view = "";
		}

		return EVAL_PAGE;
	}

	public void setId(String id) {
	    this.id = id;
	}

	public void setValue(String value) {
	    this.value = value;
	}
	
	public void setView(String view) {
	    this.view = view;
	}
	
}
