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
function ssAccessControl(namespace, binderId) {
	var namespace = namespace;
	var binderId = binderId;
	var rolesFormName = namespace + 'rolesForm';
	
	this.selectPrincipals = function (ids) {
		var formObj = document.getElementById(rolesFormName);
	
		for (var i = 0; i < ids.length; i++) {
			var inputObj = document.createElement("input");
			inputObj.setAttribute("type", "hidden");
			inputObj.setAttribute("name", "principalId");
			inputObj.setAttribute("value", ids[i]);
		
			formObj.appendChild(inputObj);
		}
	
		formObj.btnClicked.value = "addPrincipal";
		if (ss_userDisplayStyle == 'accessible') {
			this.selectPrincipalAccessible();
		} else {
			if (ss_isIE) {
				//IE does not display the table right, so repaint the screen
				this.selectPrincipalAccessible();
			} else {
				this.selectPrincipalAjax();
			}
		}
	}
	this.selectPrincipalAccessible = function () {
		setTimeout("document.forms[rolesFormName].submit();", 100)
	}
	this.addAccessControlRole = function (id) {
		var formObj = document.getElementById(rolesFormName);
		formObj.btnClicked.value = "addRole";
		formObj.roleIdToAdd.value = id;
		if (ss_userDisplayStyle == 'accessible') {
			this.selectPrincipalAccessible();
		} else {
			if (ss_isIE) {
				//IE does not display the table right, so repaint the screen
				this.selectPrincipalAccessible();
			} else {
				this.selectPrincipalAjax();
			}
		}
		ss_hideDiv('ss_addRolesMenu' + namespace);
	}
	
	this.selectOwnerAjax = function (ownerId, obj) {
		ss_setupStatusMessageDiv()
	 	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"set_binder_owner_id", namespace:namespace, ownerId:ownerId, binderId:binderId});
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.setPostRequest(ss_postRequestAlertError);
		ajaxRequest.sendRequest();  //Send the request
		ss_hideDiv('ss_changeOwnerMenu' + namespace)
	}
	
	
	this.selectPrincipalAjax = function () {
		ss_setupStatusMessageDiv()
	 	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_access_control_table", namespace:namespace, binderId:binderId});
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addFormElements(rolesFormName);
		//ajaxRequest.setEchoDebugInfo();
		ajaxRequest.setPostRequest(ss_postRequestAlertError);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
		
		ss_hideDiv('ss_addGroupsMenu' + namespace);
		ss_hideDiv('ss_addUsersMenu' + namespace);	
	}
	
	
	this.selectRole = function () {
		var formObj = document.getElementById(rolesFormName);
		formObj.btnClicked.value = "addRole";
		this.selectPrincipalAjax();
	}
	
	this.showChangeOwnerMenu = function (obj, divName) {
		var divObj = document.getElementById(divName);
		ss_moveObjectToBody(divObj)
		var objTopOffset = 10;
		var objLeftOffset = -10;
		ss_setObjectTop(divObj, parseInt(ss_getClickPositionY() + objTopOffset))
		ss_setObjectLeft(divObj, parseInt(ss_getClickPositionX(obj) + objLeftOffset))
		if (divObj.style.display == 'block' && divObj.style.visibility == 'visible') {
			ss_hideDiv(divName)
		} else {
			ss_showDiv(divName)
		}
	}
	
	this.showMenu = function (obj, divName, objTopOffset, objLeftOffset) {
		var divObj = document.getElementById(divName);
		ss_moveObjectToBody(divObj)
		ss_setObjectTop(divObj, parseInt(ss_getObjectTopAbs(obj) + objTopOffset))
		ss_setObjectLeft(divObj, parseInt(ss_getObjectLeftAbs(obj) + objLeftOffset))
		if (divObj.style.display == 'block' && divObj.style.visibility == 'visible') {
			ss_hideDiv(divName)
		} else {
			ss_showDiv(divName)
		}
	}
	this.addClipboardUsers = function () {
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_clipboard_users"}, "clipboard");
		ss_get_url(url, this.addClipboardUsersCallback)
	}
	this.addClipboardUsersCallback = function (data) {
		var userIds = new Array();
		for (var i = 0; i < data.length; i++) {
			userIds.push(data[i][0]);
		}
		this.selectPrincipals(userIds);		
	}	
}