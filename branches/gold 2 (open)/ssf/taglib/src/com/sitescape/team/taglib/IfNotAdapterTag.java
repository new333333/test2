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
package com.sitescape.team.taglib;

import javax.portlet.PortletConfig;
import javax.servlet.ServletRequest;
import javax.servlet.jsp.JspException;

import com.sitescape.team.web.WebKeys;

public class IfNotAdapterTag extends IfAdapterTag {

	/**
	 * Return <code>EVAL_BODY_INCLUDE</code> only if the request is being 
	 * made through the portlet container. Otherwise (that is, the request 
	 * came through our own portlet adapter or as a servlet), it returns 
	 * <code>SKIP_BODY</code>.
	 */
	public int doStartTag() throws JspException {
		if(super.doStartTag() == EVAL_BODY_INCLUDE) { // adapter
			return SKIP_BODY;
		}
		else { // not adapter
			ServletRequest req = pageContext.getRequest();
			
			PortletConfig portletConfig =
				(PortletConfig)req.getAttribute(WebKeys.JAVAX_PORTLET_CONFIG);

			if (portletConfig != null) // container
				return EVAL_BODY_INCLUDE;
			else // not container
				return SKIP_BODY;
		}
	}

}
