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
function ssAccessControl(namespace, workAreaId, workAreaType) {
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
		if (ss_getUserDisplayStyle() == 'accessible') {
			selectPrincipalAccessible();
		} else {
			if (1 == 1 || ss_isIE) {
				//Always use this routine. It will cause the text box to be cleared
				//IE does not display the table right, so repaint the screen
				selectPrincipalAccessible();
			} else {
				selectPrincipalAjax();
			}
		}
	}
	function selectPrincipalAccessible() {
		setTimeout("document.forms['"+rolesFormName+"'].submit();", 100)
	}
	function selectPrincipalAjax() {
		ss_setupStatusMessageDiv()
	 	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_access_control_table", namespace:namespace, workAreaId:workAreaId, workAreaType:workAreaType});
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addFormElements(rolesFormName);
		//ajaxRequest.setEchoDebugInfo();
		ajaxRequest.setPostRequest(ss_postRequestAlertError);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
		
		ss_hideDiv('ss_addGroupsMenu' + namespace);
		ss_hideDiv('ss_addUsersMenu' + namespace);	
	}

	this.addAccessControlRole = function (id) {
		var formObj = document.getElementById(rolesFormName);
		formObj.btnClicked.value = "addRole";
		formObj.roleIdToAdd.value = id;
		if (ss_getUserDisplayStyle() == 'accessible') {
			selectPrincipalAccessible();
		} else {
			if (ss_isIE) {
				//IE does not display the table right, so repaint the screen
				selectPrincipalAccessible();
			} else {
				selectPrincipalAjax();
			}
		}
		ss_hideDiv('ss_addRolesMenu' + namespace);
	}
	
	this.selectOwner = function (ownerId, propagate) {
		ss_setupStatusMessageDiv()
		self.document.getElementById("ss_status_message").innerHTML = ss_operationFailed;
	 	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"set_binder_owner_id", namespace:namespace, ownerId:ownerId, workAreaId:workAreaId, workAreaType:workAreaType, propagate:propagate});
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.setPostRequest(ss_selectOwnerCallBack);
		ajaxRequest.sendRequest();  //Send the request
		ss_hideDiv('ss_changeOwnerMenu' + namespace)
	}
	function ss_selectOwnerCallBack() {
		//See if there was an error
		if (self.document.getElementById("ss_status_message").innerHTML == "error") {
			alert(ss_not_logged_in);
		} else if (self.document.getElementById("ss_status_message").innerHTML == "ok") {
			var spanObj = document.getElementById('ss_changeOwnerMenuOkSpan' + namespace);
			spanObj.innerHTML = ss_operationSucceeded
		} else {
			var spanObj = document.getElementById('ss_changeOwnerMenuOkSpan' + namespace);
			spanObj.innerHTML = ss_operationFailed
		}
		
		var divObj = document.getElementById('ss_changeOwnerMenuOk' + namespace);
		ss_showDiv('ss_changeOwnerMenuOk' + namespace)
		setTimeout("ss_hideDivNone('ss_changeOwnerMenuOk" + namespace + "');", 2500);
	}
	
	this.selectRole = function () {
		var formObj = document.getElementById(rolesFormName);
		formObj.btnClicked.value = "addRole";
		selectPrincipalAjax();
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
			divObj.focus();
		}
	}
	this.addClipboardUsers = function () {
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_clipboard_users"}, "clipboard");
		ss_get_url(url, ss_createDelegate(this, addClipboardUsersCallback));
	}
	function addClipboardUsersCallback(data) {
		var userIds = new Array();
		for (var i = 0; i < data.length; i++) {
			userIds.push(data[i][0]);
		}
		this.selectPrincipals(userIds);		
	}	
}
