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
package org.kablink.teaming.taglib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.PortletURL;
import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.kablink.teaming.portletadapter.AdaptedPortletURL;
import org.kablink.teaming.util.Constants;
import org.kablink.teaming.util.NLT;
import org.kablink.teaming.util.ReleaseInfo;
import org.kablink.teaming.web.WebKeys;
import org.kablink.teaming.web.util.MiscUtil;
import org.kablink.teaming.web.util.WebUrlUtil;
import org.kablink.util.Html;
import org.kablink.util.Validator;

/**
 * ?
 * 
 * @author Peter Hurley
 */
@SuppressWarnings({"unchecked", "unused"})
public class UrlTag extends BodyTagSupport implements ParamAncestorTag {
	private static Log logger = LogFactory.getLog(UrlTag.class);

	private String url;
	private String action;
	private String feature;
    private String binderId;
    private String entryId;
    private String entityType;
    private String operation;
    private String rootPath;
    private String webPath;
    private String windowState;
    private boolean adapter=false;
    private boolean crawlable=false;
    private String portletName = "ss_forum";
    private boolean actionUrl = true;
	private boolean stayInFrame = false;
	private Map<String, String[]> _params;
	
	public UrlTag() {
		setup();
	}
	
	/** 
	 * Initalize params at end of call and creation
	 */
	protected void setup() {
		if (_params != null) {
			_params.clear();
		}
		
		// Need to reinitialize - class must be cached.
		binderId=null;
		entryId=null;
		entityType=null;
		url = null;
		action = null;
		feature = null;
		operation=null;
		rootPath = null;
		webPath = null;
		adapter=false;
		crawlable=false;
		actionUrl = true;
		stayInFrame=false;
		portletName = "ss_forum";
	}
	
	@Override
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest req =
				(HttpServletRequest)pageContext.getRequest();
			
			RenderRequest renderRequest = (RenderRequest) req.getAttribute("javax.portlet.request");
			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");
			//If there is no request object, then this must be from an adaptor and not a portlet url
			if (renderRequest == null || renderResponse == null) this.adapter = true;

			
			//See if a url was specified
			String ctxPath = req.getContextPath();
			if (!Validator.isNull(url)) {
				//Yes, a url was explicitly specified. Just add the portal context and return
				String fullUrl = ctxPath + Constants.SLASH + this.url;
				pageContext.getOut().print(escapeUrl(fullUrl));

				return SKIP_BODY;				
			}

			//There was no explicit url specified, so build the url
			//Get the SiteScape url parameters
			Map params = new HashMap();
			
			if (!Validator.isNull(binderId)) {
				params.put(WebKeys.URL_BINDER_ID, new String[] {binderId});
			} 
			if (!Validator.isNull(entryId)) {
				params.put(WebKeys.URL_ENTRY_ID, new String[] {entryId});
			} 
			if (!Validator.isNull(entityType)) {
				params.put(WebKeys.URL_ENTITY_TYPE, new String[] {entityType});
			} 
			if (!Validator.isNull(operation)) {
				params.put(WebKeys.URL_OPERATION, new String[] {operation});
			} 
			if (!Validator.isNull(webPath)) {
				if (webPath.equals(WebKeys.SERVLET_VIEW_CSS)) {
					//This is a special case for the viewCss file. We will add a unique qualifier that causes 
					//  the browsers to use the latest version after an install or system restart.
					webPath += "/" + WebKeys.SERVLET_VIEW_CSS_START_TIME + "/" + ReleaseInfo.getStartTime();
				}
				if (!Validator.isNull(action)) {
					params.put("action", new String[] {this.action});
				}
				if (!Validator.isNull(feature)) {
					params.put("feature", new String[] {this.feature});
				}
				
				String webUrl = getWebUrl(req);
				if (!params.entrySet().isEmpty() || (_params != null && !_params.entrySet().isEmpty())) {
					webUrl += Constants.QUESTION;
				}
				Iterator it = params.entrySet().iterator();
				String separator = "";
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					webUrl += separator + me.getKey() + Constants.EQUAL + ((String[])me.getValue())[0];
					separator = Constants.ESCAPED_AMPERSAND;
				}
				if (_params != null ) {
					Iterator _it = _params.entrySet().iterator();
					while (_it.hasNext()) {
						Map.Entry me = (Map.Entry) _it.next();
						webUrl += separator + me.getKey() + Constants.EQUAL + ((String[])me.getValue())[0];
						separator = Constants.ESCAPED_AMPERSAND;
					}
				}
				pageContext.getOut().print(escapeUrl(webUrl));
			
			} else if (!Validator.isNull(rootPath)) {
				pageContext.getOut().print(escapeUrl(WebUrlUtil.getSSFContextRootURL(req) + MiscUtil.getStaticPath()));
			
			} else if (this.adapter) {
				if (!Validator.isNull(action)) {
					params.put("action", new String[] {this.action});
				}
				if (!Validator.isNull(feature)) {
					params.put("feature", new String[] {this.feature});
				}
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, this.portletName, this.actionUrl, crawlable);
				Iterator it = params.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					adapterUrl.setParameter((String) me.getKey(), (String[])me.getValue());
				}
				if (_params != null ) {
					Iterator _it = _params.entrySet().iterator();
					while (_it.hasNext()) {
						Map.Entry me = (Map.Entry) _it.next();
						adapterUrl.setParameter((String) me.getKey(), (String[])me.getValue());
					}
				}
				pageContext.getOut().print(escapeUrl(adapterUrl.toString()));
				
			} else {
				PortletURL portletURL = null;
				if (this.actionUrl) {
					portletURL = renderResponse.createActionURL();
				} else {
					portletURL = renderResponse.createRenderURL();
				}
				if (this.windowState == null) {
					//If not specified, assume the window state is maximized
					portletURL.setWindowState(new WindowState(WindowState.MAXIMIZED.toString()));
				} else if (!this.windowState.equals("")) {
					//If specified, use the windowState; 
					//  if windowState explicitly set to "", then leave the windowState unspecified in the URL
					portletURL.setWindowState(new WindowState(this.windowState));
				}
				Iterator it = params.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					portletURL.setParameter((String) me.getKey(), (String[])me.getValue());
				}
				if (_params != null) {
					it = _params.entrySet().iterator();
					while (it.hasNext()) {
						Map.Entry me = (Map.Entry) it.next();
						portletURL.setParameter((String) me.getKey(), (String[])me.getValue());
					}
				}
				if (!Validator.isNull(action)) {
					portletURL.setParameter("action", new String[] {this.action});
				}
				if (!Validator.isNull(feature)) {
					portletURL.setParameter("feature", new String[] {this.feature});
				}

				String portletURLToString = portletURL.toString();

				pageContext.getOut().print(escapeUrl(portletURLToString));
			}

			return SKIP_BODY;
		}
	    catch(Exception e) {
	    	// Sometimes this tag is called from an error jsp page to render information
	    	// about the previous error occured during the normal flow of control.
	    	// Often in time, the state is such that the code in this method fails because
	    	// an output stream or reader was previously obtained from the same request
	    	// object by the code executing the normal flow. Throwing another error from
	    	// this place is the last thing we want to do in that case, since it can 
	    	// make the bad situation even uglier. For that reason, we don't want to 
	    	// propogate the exception up the call stack. Instead, just log the error
	    	// and return normally from here.  
	        //throw new JspException(e);
	        logger.warn(e.toString());
	        return SKIP_BODY;
	    }
		finally {
			setup();
		}
	}

	public void setUrl(String url) {
	    this.url = url;
	}

	public void setAction(String action) {
	    this.action = action;
	}

	public void setFeature(String feature) {
	    this.feature = feature;
	}

	public void setFolderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}
	
	public void setEntryId(String entryId) {
	    this.entryId = entryId;
	}

	public void setEntityType(String entityType) {
	    this.entityType = entityType;
	}

	public void setRootPath(String rootPath) {
	    this.rootPath = rootPath;
	}

	public void setWebPath(String webPath) {
	    this.webPath = webPath;
	}

	public void setWindowState(String windowState) {
	    this.windowState = windowState;
	}

	public void setAdapter(boolean adapter) {
	    this.adapter = adapter;
	}

	public void setCrawlable(boolean crawlable) {
		this.crawlable = crawlable;
	}
	
	public void setPortletName(String portletName) {
	    this.portletName = portletName;
	}

	public void setActionUrl(boolean actionUrl) {
	    this.actionUrl = actionUrl;
	}

	public void setStayInFrame(boolean stayInFrame) {
	    this.stayInFrame = stayInFrame;
	}

	public void setOperation(String operation) {
	    this.operation = operation;
	}

	@Override
	public void addParam(String name, String value) {
		if (_params == null) {
			_params = new LinkedHashMap();
		}

		String[] values = (String[])_params.get(name);

		if (values == null) {
			values = new String[] {value};
		}
		else {
			String[] newValues = new String[values.length + 1];

			System.arraycopy(values, 0, newValues, 0, values.length);

			newValues[newValues.length - 1] = value;

			values = newValues;
		}

		_params.put(name, values);
	}

	protected String getWebUrl(HttpServletRequest req) {
		return WebUrlUtil.getServletRootURL(req) + webPath;
	}
	
	/*
	 * Escapes any JavaScript embedded in the URL for output to the
	 * page.
	 * 
	 * Implements the fix for Bugzilla 956081.
	 */
	private String escapeUrl(String urlIn) {
		String urlOut;
		if ((null == urlIn) || (0 == urlIn.length()))
		     urlOut = urlIn;
		else urlOut = Html.formatTo(urlIn);	// Escapes any embedded JavaScript.
		return urlOut;
	}
}
