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
package org.kablink.teaming.spring.web.portlet;

import javax.portlet.PortletException;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.springframework.web.portlet.ModelAndView;
import org.springframework.web.servlet.View;

public class DispatcherPortlet extends org.springframework.web.portlet.DispatcherPortlet {
	protected void render(ModelAndView mv, RenderRequest request, RenderResponse response) throws Exception {
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
			Object viewObject = mv.getView();
			if (viewObject == null) {
				throw new PortletException("ModelAndView [" + mv + "] neither contains a view name nor a " +
						"View object in portlet with name '" + getPortletName() + "'");
			}
			if (!(viewObject instanceof View)) {
				throw new PortletException(
						"View object [" + viewObject + "] is not an instance of [org.springframework.web.servlet.View] - " +
						"DispatcherPortlet does not support any other view types");
			}
			view = (View) viewObject;
		}

		if (view == null) {
			throw new PortletException("Could not resolve view with name '" + mv.getViewName() +
					"' in portlet with name '" + getPortletName() + "'");
		}

/* commented out for mobile WAP
 * 		// Set the content type on the response if needed and if possible.
		// The Portlet spec requires the content type to be set on the RenderResponse;
		// it's not sufficient to let the View set it on the ServletResponse.
		if (response.getContentType() != null) {
			if (logger.isDebugEnabled()) {
				logger.debug("Portlet response content type already set to [" + response.getContentType() + "]");
			}
		}
		else {
			// No Portlet content type specified yet -> use the view-determined type.
			String contentType = view.getContentType();
			if (contentType != null) {
				if (logger.isDebugEnabled()) {
					logger.debug("Setting portlet response content type to view-determined type [" + contentType + "]");
				}
				response.setContentType(contentType);
			}
		}
*/
		doRender(view, mv.getModel(), request, response);
	}

}
