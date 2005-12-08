package com.sitescape.ef.taglib;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.portlet.PortletKeys;

import javax.portlet.PortletURL;


/**
 * @author Peter Hurley
 *
 */
public class UrlTag extends BodyTagSupport implements ParamAncestorTag {
	private String url = "";
	private String action = "";
    private String folderId = "";
    private String entryId = "";
    private String operation = "";
    private boolean popup = false;
    private boolean actionUrl = true;
    private boolean stayInFrame = false;
	private Map _params;
    
	public int doEndTag() throws JspException {
		try {
			HttpServletRequest req =
				(HttpServletRequest)pageContext.getRequest();
			
			RenderRequest renderRequest = (RenderRequest) req.getAttribute("javax.portlet.request");
			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

			
			//See if a url was specified
			if (!this.url.equals("")) {
				//Yes, a url was explicitly specified. Just add the portal context and return
				String ctxPath = renderRequest.getContextPath();
				String fullUrl = ctxPath + "/" + this.url;
				pageContext.getOut().print(fullUrl);

				return SKIP_BODY;				
			}

			//There was no explicit url specified, so build the url
			PortletURL portletURL;
			if (this.actionUrl) {
				portletURL = renderResponse.createActionURL();
			}
			else {
				portletURL = renderResponse.createRenderURL();
			}

			portletURL.setWindowState(new WindowState(WindowState.MAXIMIZED.toString()));
			//Add the SiteScape url parameters
			Map params = new HashMap();
			
			if (this.popup) {
				params.put("popup", new String[] {"1"});
			}
			
			if (!this.action.equals("")) {
				params.put("action", new String[] {this.action});
			}
			
			if (this.folderId.equals("")) folderId = (String) req.getAttribute(PortletKeys.FORUM_URL_FORUM_ID);
			if (this.folderId != null && !this.folderId.equals("")) {
				params.put(PortletKeys.FORUM_URL_FORUM_ID, new String[] {folderId});
			} else {
				this.folderId = "";
			}
			if (this.entryId.equals("")) entryId = (String) req.getAttribute(PortletKeys.FORUM_URL_ENTRY_ID);
			if (this.entryId != null && !this.entryId.equals("")) {
				params.put(PortletKeys.FORUM_URL_ENTRY_ID, new String[] {entryId});
			} else {
				this.entryId = "";
			}
			if (this.operation.equals("")) operation = (String) req.getAttribute(PortletKeys.FORUM_URL_OPERATION);
			if (this.operation != null && !this.operation.equals("")) {
				params.put(PortletKeys.FORUM_URL_OPERATION, new String[] {operation});
			} else {
				this.operation = "";
			}
			portletURL.setParameters(params);
			
			if (_params != null) {
				portletURL.setParameters(_params);
			}

			String portletURLToString = portletURL.toString();

			pageContext.getOut().print(portletURLToString);

			return SKIP_BODY;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			if (_params != null) {
				_params.clear();
			}
		}
	}

	public void setUrl(String url) {
	    this.url = url;
	}

	public void setAction(String action) {
	    this.action = action;
	}

	public void setFolderId(String folderId) {
	    this.folderId = folderId;
	}

	public void setEntryId(String entryId) {
	    this.entryId = entryId;
	}

	public void setPopup(boolean popup) {
	    this.popup = popup;
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


