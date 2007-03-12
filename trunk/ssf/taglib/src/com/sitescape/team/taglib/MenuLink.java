package com.sitescape.team.taglib;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;

import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Hemanth Chokkanathan
 *
 */
public class MenuLink extends BodyTagSupport implements ParamAncestorTag {
	private String _bodyContent;
	private String action = "";
	private Boolean adapter = Boolean.TRUE;
	private String entryId = "";
	private String folderId = "";
	private String binderId = "";
	private String entityType = "";
	private String displayDiv = "false";
	private String menuDivId = "";
	private String seenStyle = ""; 
	private String seenStyleFine = "";
	
	private Map _params;
    
	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		_bodyContent = getBodyContent().getString();

		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (this._params == null) this._params = new HashMap();
			
			RequestDispatcher rd;
			
			if ("false".equalsIgnoreCase(displayDiv)) {
				//Output the start of the area
				rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menulink/menulink.jsp");
				
				if (_bodyContent != null) _params.put("title", new String[] {_bodyContent});
				_params.put("action", new String[] {this.action});
				
				String strAdapterValue = "true";
				if (adapter != null && adapter.booleanValue() == false) strAdapterValue = "false";
	
				_params.put("adapter", new String[] {strAdapterValue});
				_params.put("entryId", new String[] {this.entryId});
				_params.put("folderId", new String[] {this.folderId});
				_params.put("binderId", new String[] {this.binderId});
				_params.put("entityType", new String[] {this.entityType});
				_params.put("seenStyle", new String[] {this.seenStyle});
				_params.put("seenStyleFine", new String[] {this.seenStyleFine});
				
				ServletRequest req = null;
				req = new DynamicServletRequest(httpReq, _params);
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			} else {
				//Output the start of the area
				rd = httpReq.getRequestDispatcher("/WEB-INF/jsp/tag_jsps/menulink/menulinkdiv.jsp");
				
				_params.put("menuDivId", new String[] {this.menuDivId});
				
				ServletRequest req = null;
				req = new DynamicServletRequest(httpReq, _params);
				StringServletResponse res = new StringServletResponse(httpRes);
				rd.include(req, res);
				pageContext.getOut().print(res.getString());
			}

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			action = "";
			adapter= Boolean.TRUE;
			entryId = "";
			folderId = "";
			binderId = "";
			entityType = "";
			seenStyle = "";
			if (_params != null) {
				_params.clear();
			}
		}
	}

	public void addParam(String name, String value) {
		if (_params == null) {
			_params = new HashMap();
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

	public void setAction(String action) {
	    this.action = action;
	}
	
	public void setAdapter(Boolean adapter) {
	    this.adapter = adapter;
	}
	
	public void setEntryId(String entryId) {
	    this.entryId = entryId;
	}
	
	public void setFolderId(String folderId) {
	    this.folderId = folderId;
	}
	
	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setEntityType(String entityType) {
	    this.entityType = entityType;
	}
	
	public void setDisplayDiv(String displayDiv) {
	    this.displayDiv = displayDiv;
	}
	
	public void setMenuDivId(String menuDivId) {
	    this.menuDivId = menuDivId;
	}
	
	public void setSeenStyle(String seenStyle) {
	    this.seenStyle = seenStyle;
	}
	
	public void setSeenStyleFine(String seenStyleFine) {
	    this.seenStyleFine = seenStyleFine;
	}
}
