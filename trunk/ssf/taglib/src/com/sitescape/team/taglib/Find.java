package com.sitescape.team.taglib;

import java.util.HashSet;
import java.util.Set;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import com.sitescape.team.web.WebKeys;
import com.sitescape.util.servlet.DynamicServletRequest;
import com.sitescape.util.servlet.StringServletResponse;


/**
 * @author Peter Hurley
 *
 */
public class Find extends TagSupport {
    private Set userList;    
    private String formName = "";
    private String formElement;
    private String width = "30";
    private String type;
    private Boolean singleItem;
    private Boolean leaveResultsVisible;
    private String clickRoutine = "";
    private String binderId = "";
    private Boolean searchSubFolders;
    private Boolean showClipboard;
    private Boolean showTeamMembers;
    private Integer instanceCount = 0;
    private Boolean initOnly = false;
    private Boolean noInit = false;
    
	public int doStartTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (this.userList == null) this.userList = new HashSet();			
			if (this.type == null) this.type = WebKeys.USER_SEARCH_USER_GROUP_TYPE_USER;
			if (singleItem == null) singleItem = false;
			if (leaveResultsVisible == null) leaveResultsVisible = false;
			if (searchSubFolders == null) searchSubFolders = false;
			if (this.showClipboard == null) this.showClipboard = false;
			if (this.showTeamMembers == null) this.showTeamMembers = false;
			this.instanceCount++;
			
			//Output the start of the area
			RequestDispatcher rd;
			String jsp;
			if (singleItem) {
				if (type.equals("places")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_place.jsp";
				} else if (type.equals("tags") || type.equals("personalTags") || type.equals("communityTags")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_tag.jsp";
				} else if (type.equals("entries")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_entry.jsp";
				} else {
					jsp = "/WEB-INF/jsp/tag_jsps/find/single_user.jsp";
				}
			} else {
				if (type.equals("places")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/places_list.jsp";
				} else if (type.equals("tags")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/tag_list.jsp";
				} else if (type.equals("entries")) {
					jsp = "/WEB-INF/jsp/tag_jsps/find/entries_list.jsp";
				} else {
					jsp = "/WEB-INF/jsp/tag_jsps/find/user_list.jsp";
				}
			}
			rd = httpReq.getRequestDispatcher(jsp);

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq);
			req.setAttribute("user_list", this.userList);			
			req.setAttribute("form_name", this.formName);
			req.setAttribute("form_element", this.formElement);
			req.setAttribute("element_width", this.width);
			req.setAttribute("list_type", this.type);
			req.setAttribute("singleItem", this.singleItem);
			req.setAttribute("clickRoutine", this.clickRoutine);
			req.setAttribute("instanceCount", this.instanceCount);
			req.setAttribute("leaveResultsVisible", this.leaveResultsVisible);
			req.setAttribute("searchSubFolders", this.searchSubFolders.toString());
			req.setAttribute("binderId", this.binderId);
			req.setAttribute("show_clipboard", this.showClipboard);
			req.setAttribute("show_team_members", this.showTeamMembers);
			req.setAttribute("initOnly", this.initOnly);
			req.setAttribute("noInit", this.noInit);
			
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
			// TODO: temporary, remove later
	    	// System.out.println("EXCEPTION:"+e);
	        throw new JspException(e);
	    }
		finally {
			this.formName = "";
			this.userList = null;
			this.showClipboard = false;
			this.singleItem = false;
			this.width = "30";
			this.clickRoutine = "";
			this.leaveResultsVisible = false;
			this.binderId = "";
			this.searchSubFolders = false;
			this.showTeamMembers = false;
			this.initOnly = false;
			this.noInit = false;
		}
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setUserList(Set userList) {
	    this.userList = userList;
	}

	public void setFormName(String formName) {
	    this.formName = formName;
	}

	public void setFormElement(String formElement) {
	    this.formElement = formElement;
	}

	public void setWidth(String width) {
	    this.width = width;
	}

	public void setType(String type) {
	    this.type = type;
	}

	public void setClickRoutine(String clickRoutine) {
	    this.clickRoutine = clickRoutine;
	}

	public void setSingleItem(Boolean singleItem) {
	    this.singleItem = singleItem;
	}

	public void setLeaveResultsVisible(Boolean leaveResultsVisible) {
	    this.leaveResultsVisible = leaveResultsVisible;
	}

	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setSearchSubFolders(Boolean searchSubFolders) {
	    this.searchSubFolders = searchSubFolders;
	}

	public void setShowClipboard(Boolean showClipboard) {
		this.showClipboard = showClipboard;
	}

	public void setShowTeamMembers(Boolean showTeamMembers) {
		this.showTeamMembers = showTeamMembers;
	}

	public void setInitOnly(Boolean initOnly) {
		this.initOnly = initOnly;
	}

	public void setNoInit(Boolean noInit) {
		this.noInit = noInit;
	}

}
