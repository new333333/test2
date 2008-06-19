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
package com.sitescape.team.taglib;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.team.portletadapter.AdaptedPortletURL;

import javax.portlet.PortletURL;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sitescape.team.web.WebKeys;
import com.sitescape.team.web.util.WebUrlUtil;
import com.sitescape.util.Validator;


/**
 * @author Peter Hurley
 *
 */
public class UrlTag extends BodyTagSupport implements ParamAncestorTag {
	private static Log logger = LogFactory.getLog(UrlTag.class);

	private String url;
	private String action;
    private String binderId;
    private String entryId;
    private String entityType;
    private String operation;
    private String webPath;
    private String windowState;
    private boolean adapter=false;
    private String portletName = "ss_forum";
    private boolean actionUrl = true;
    private boolean stayInFrame = false;
	private Map _params;
	
	public UrlTag() {
		setup();
	}
	/** 
	 * Initalize params at end of call and creation
	 * 
	 *
	 */
	protected void setup() {
		if (_params != null) {
			_params.clear();
		}
		//need to reinitialize - class must be cached
		binderId=null;
		entryId=null;
		entityType=null;
		url = null;
		action = null;
		operation=null;
		webPath = null;
		adapter=false;
		actionUrl = true;
		stayInFrame=false;
		portletName = "ss_forum";
	}
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
				String fullUrl = ctxPath + "/" + this.url;
				pageContext.getOut().print(fullUrl);

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

			if (!Validator.isNull(webPath) && this.webPath.equals(WebKeys.ACTION_READFILE)) {
				String webUrl = getWebUrl(req);
				webUrl += "/" + entityType;
				webUrl += "/" + binderId;
				if (entryId != null) {
					webUrl += "/" + entryId;
				} else {
					webUrl += "/-";
				}
				webUrl += "/" + ((String[])_params.get(WebKeys.URL_FILE_ID))[0];
				webUrl += "/" + ((String[])_params.get(WebKeys.URL_FILE_TIME))[0];
				webUrl += "/" + ((String[])_params.get(WebKeys.URL_FILE_NAME))[0];
				pageContext.getOut().print(webUrl);
			} else if (!Validator.isNull(webPath)) {
				if (!Validator.isNull(action)) {
					params.put("action", new String[] {this.action});
				}
				
				String webUrl = getWebUrl(req) + "?";
				Iterator it = params.entrySet().iterator();
				String separator = "";
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					webUrl += separator + me.getKey() + "=" + ((String[])me.getValue())[0];
					separator = "&amp;";
				}
				if (_params != null ) {
					Iterator _it = _params.entrySet().iterator();
					while (_it.hasNext()) {
						Map.Entry me = (Map.Entry) _it.next();
						webUrl += separator + me.getKey() + "=" + ((String[])me.getValue())[0];
						separator = "&amp;";
					}
				}
				pageContext.getOut().print(webUrl);
			
			} else if (this.adapter) {
				if (!Validator.isNull(action)) {
					params.put("action", new String[] {this.action});
				}
				AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, this.portletName, this.actionUrl);
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
				pageContext.getOut().print(adapterUrl.toString());
				
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

				String portletURLToString = portletURL.toString();

				pageContext.getOut().print(portletURLToString);
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

	public void setWebPath(String webPath) {
	    this.webPath = webPath;
	}

	public void setWindowState(String windowState) {
	    this.windowState = windowState;
	}

	public void setAdapter(boolean adapter) {
	    this.adapter = adapter;
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

}


