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

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.BodyTagSupport;
import javax.servlet.jsp.tagext.TagSupport;

import org.kablink.teaming.web.WebKeys;
import org.kablink.util.servlet.DynamicServletRequest;
import org.kablink.util.servlet.StringServletResponse;



/**
 * @author Peter Hurley
 *
 */
public class Find extends BodyTagSupport implements ParamAncestorTag {
    private Set userList;    
    private String formName = "";
    private String formElement;
    private String width = "70px";
    private String type;
    private Boolean singleItem;
    private Boolean leaveResultsVisible;
    private Boolean sendingEmail;
    private String clickRoutine = "";
    private String clickRoutineObj = "";
    private String findMultipleObj = "";
    private String binderId = "";
    private Boolean searchSubFolders;
    private Boolean foldersOnly;
    private String instanceCount;
    private String accessibilityText;
    private Boolean addCurrentUser;
    private Boolean showFolderTitles;
    private Boolean showUserTitleOnly;
    private Boolean displayValue;
    private Boolean displayValueOnly;
    
	private Map _params;

	public int doStartTag() {
		return EVAL_BODY_BUFFERED;
	}

	public int doAfterBody() {
		return SKIP_BODY;
	}

	public int doEndTag() throws JspException {
		try {
			HttpServletRequest httpReq = (HttpServletRequest) pageContext.getRequest();
			HttpServletResponse httpRes = (HttpServletResponse) pageContext.getResponse();
			
			if (this._params == null) this._params = new HashMap();
			
			if (this.userList == null) this.userList = new HashSet();			
			if (this.type == null) this.type = WebKeys.FIND_TYPE_USER;
			if (singleItem == null) singleItem = false;
			if (leaveResultsVisible == null) leaveResultsVisible = false;
			if (sendingEmail == null) sendingEmail = false;
			if (searchSubFolders == null) searchSubFolders = false;
			if (showFolderTitles == null) showFolderTitles = false;
			if (showUserTitleOnly == null) showUserTitleOnly = false;
			if (displayValue == null) displayValue = false;
			if (displayValueOnly == null) displayValueOnly = false;
			if (foldersOnly == null) foldersOnly = false;
			if (addCurrentUser == null) addCurrentUser = false;
			this.instanceCount = UUID.randomUUID().toString();
			
			//Output the start of the area
			RequestDispatcher rd;
			String jsp;
			
			
			if (singleItem) {
				jsp = "/WEB-INF/jsp/tag_jsps/find/single.jsp";
			} else {
				jsp = "/WEB-INF/jsp/tag_jsps/find/list.jsp";
			}
			rd = httpReq.getRequestDispatcher(jsp);

			ServletRequest req = null;
			req = new DynamicServletRequest(httpReq, _params);
			req.setAttribute(WebKeys.FIND_USER_LIST, this.userList);			
			req.setAttribute(WebKeys.FIND_FORM_NAME, this.formName);
			req.setAttribute(WebKeys.FIND_FORM_ELEMENT, this.formElement);
			req.setAttribute(WebKeys.FIND_ELEMENT_WIDTH, this.width);
			req.setAttribute(WebKeys.FIND_LIST_TYPE, this.type);
			req.setAttribute(WebKeys.FIND_SINGLE_ITEM, this.singleItem);
			req.setAttribute(WebKeys.FIND_CLICK_ROUTINE, this.clickRoutine);
			req.setAttribute(WebKeys.FIND_CLICK_ROUTINE_OBJ, this.clickRoutineObj);
			req.setAttribute(WebKeys.FIND_MULTIPLE_OBJ, this.findMultipleObj);
			req.setAttribute(WebKeys.FIND_INSTANCE_COUNT, this.instanceCount);
			req.setAttribute(WebKeys.FIND_INSTANCE_CODE, this.hashCode() + "_" + this.formName + "_" + this.formElement);
			req.setAttribute(WebKeys.FIND_LEAVE_RESULTS_VISIBLE, this.leaveResultsVisible.toString());
			req.setAttribute(WebKeys.FIND_SENDING_EMAIL, this.sendingEmail.toString());
			req.setAttribute(WebKeys.FIND_FOLDERS_ONLY, this.foldersOnly.toString());
			req.setAttribute(WebKeys.FIND_SEARCH_SUBFOLDERS, this.searchSubFolders.toString());
			req.setAttribute(WebKeys.FIND_SHOW_FOLDER_TITLES, this.showFolderTitles.toString());
			req.setAttribute(WebKeys.FIND_SHOW_USER_TITLE_ONLY, this.showUserTitleOnly.toString());
			req.setAttribute(WebKeys.FIND_DISPLAY_VALUE, this.displayValue.toString());
			req.setAttribute(WebKeys.FIND_DISPLAY_VALUE_ONLY, this.displayValueOnly.toString());
			req.setAttribute(WebKeys.URL_BINDER_ID, this.binderId);
			req.setAttribute(WebKeys.FIND_ACCESSIBILITY_TEXT, this.accessibilityText);
			req.setAttribute(WebKeys.FIND_ADD_CURRENT_USER, this.addCurrentUser);
			
			StringServletResponse res = new StringServletResponse(httpRes);
			rd.include(req, res);
			pageContext.getOut().print(res.getString());

			return EVAL_PAGE;
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
		finally {
			this.formName = "";
			this.userList = null;
			this.singleItem = false;
			this.width = "70px";
			this.clickRoutine = "";
			this.clickRoutineObj = "";
			this.findMultipleObj = "";
			this.leaveResultsVisible = false;
			this.sendingEmail = false;
			this.binderId = "";
			this.searchSubFolders = false;
			this.showFolderTitles = false;
			this.showUserTitleOnly = false;
			this.displayValue = false;
			this.displayValueOnly = false;
			this.foldersOnly = false;
			this.instanceCount = null;
			this.accessibilityText = null;
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
		} else {
			String[] newValues = new String[values.length + 1];
			System.arraycopy(values, 0, newValues, 0, values.length);
			newValues[newValues.length - 1] = value;
			values = newValues;
		}
		_params.put(name, values);
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

	public void setClickRoutineObj(String clickRoutineObj) {
		this.clickRoutineObj = clickRoutineObj;
	}
	
	public void setFindMultipleObj(String findMultipleObj) {
		this.findMultipleObj = findMultipleObj;
	}
	
	public void setSingleItem(Boolean singleItem) {
	    this.singleItem = singleItem;
	}

	public void setLeaveResultsVisible(Boolean leaveResultsVisible) {
	    this.leaveResultsVisible = leaveResultsVisible;
	}

	public void setSendingEmail(Boolean sendingEmail) {
	    this.sendingEmail = sendingEmail;
	}

	public void setBinderId(String binderId) {
	    this.binderId = binderId;
	}

	public void setSearchSubFolders(Boolean searchSubFolders) {
	    this.searchSubFolders = searchSubFolders;
	}
	
	public void setShowFolderTitles(Boolean showFolderTitles) {
	    this.showFolderTitles = showFolderTitles;
	}
	
	public void setShowUserTitleOnly(Boolean showUserTitleOnly) {
	    this.showUserTitleOnly = showUserTitleOnly;
	}
	
	public void setDisplayValue(Boolean displayValue) {
	    this.displayValue = displayValue;
	}
	
	public void setDisplayValueOnly(Boolean displayValueOnly) {
	    this.displayValueOnly = displayValueOnly;
	}
	
	public void setFoldersOnly(Boolean foldersOnly) {
	    this.foldersOnly = foldersOnly;
	}
	
	public void setAccessibilityText(String accessibilityText) {
	    this.accessibilityText = accessibilityText;
	}

	public void setAddCurrentUser(Boolean addCurrentUser) {
		this.addCurrentUser = addCurrentUser;
	}	
}
