package com.sitescape.ef.taglib;

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

import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.ef.web.util.WebUrlUtil;

import javax.portlet.PortletURL;
import com.sitescape.util.Validator;


/**
 * @author Peter Hurley
 *
 */
public class UrlTag extends BodyTagSupport implements ParamAncestorTag {
	private String url;
	private String action;
    private String binderId;
    private String entryId;
    private String operation;
    private String webPath;
    private boolean adapter=false;
    private String portletName = "";
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
	private void setup() {
		if (_params != null) {
			_params.clear();
		}
		//need to reinitialize - class must be cached
		binderId=null;
		entryId=null;
		url = null;
		action = null;
		operation=null;
		webPath = null;
		adapter=false;
		actionUrl = true;
		stayInFrame=false;
		portletName="";
	}
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest req =
				(HttpServletRequest)pageContext.getRequest();
			
			RenderRequest renderRequest = (RenderRequest) req.getAttribute("javax.portlet.request");
			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

			
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
			if (!Validator.isNull(operation)) {
				params.put(WebKeys.URL_OPERATION, new String[] {operation});
			} 

			if (!Validator.isNull(webPath)) {
				if (!Validator.isNull(action)) {
					params.put("action", new String[] {this.action});
				}
				
				String webUrl = WebUrlUtil.getServletRootURL(req) + webPath + "?";
				Iterator it = params.entrySet().iterator();
				while (it.hasNext()) {
					Map.Entry me = (Map.Entry) it.next();
					webUrl += me.getKey() + "=" + ((String[])me.getValue())[0] + "&";
				}
				if (_params != null ) {
					Iterator _it = _params.entrySet().iterator();
					while (_it.hasNext()) {
						Map.Entry me = (Map.Entry) _it.next();
						webUrl += me.getKey() + "=" + ((String[])me.getValue())[0] + "&";
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
					adapterUrl.setParameter((String) me.getKey(), ((String[])me.getValue())[0]);
				}
				if (_params != null ) {
					Iterator _it = _params.entrySet().iterator();
					while (_it.hasNext()) {
						Map.Entry me = (Map.Entry) _it.next();
						adapterUrl.setParameter((String) me.getKey(), ((String[])me.getValue())[0]);
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
				portletURL.setWindowState(new WindowState(WindowState.MAXIMIZED.toString()));
				portletURL.setParameters(params);
				if (_params != null) {
					portletURL.setParameters(_params);
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
	        throw new JspException(e);
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

	public void setWebPath(String webPath) {
	    this.webPath = webPath;
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

}


