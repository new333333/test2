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


if (!window.ss_clipboardUsersLoaded)
	window.ss_clipboardUsersLoaded  = new Array();
if (!window.ss_clipboardUsersFormElement)
	window.ss_clipboardUsersFormElement  = new Array();
if (!window.ss_clipboardUsersCheckboxes)
	window.ss_clipboardUsersCheckboxes  = new Array();
	
function ss_setClipboardUsersVariables(prefix, formElement) {
	ss_clipboardUsersFormElement[prefix]  = formElement;
}

function ss_toggleShowClipboardUsersIcon(iconId) {
	ss_toggleImage(iconId, "sym_s_expand.gif", "sym_s_collapse.gif");
}


function ss_loadClipboardUsersList(prefix) {
	var ajaxLoadingIndicatorPane = "ss_clipboardUsersList_" + prefix;
	
	if (window.ss_clipboardUsersLoaded[prefix]) {
		ss_toggleShowClipboardUsersIcon("ss_clipboardUsersIcon_" + prefix);
		ss_showHide(ajaxLoadingIndicatorPane);
	} else {
		var callData = {prefix:prefix};
		ss_get_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_clipboard_users"}, "clipboard"),
					ss_buildClipboardUsersListTable, callData, "ss_toggleAjaxLoadingIndicator('" + ajaxLoadingIndicatorPane + "')");
	}
}

function ss_buildClipboardUsersListTable(members, callbackData) {
	var prefix = callbackData['prefix'];
	ss_toggleShowClipboardUsersIcon("ss_clipboardUsersIcon_" + prefix);
	var cols = members.length <= 3 ? 1 : (members.length <= 6 ? 2 : 3);
	var rows = Math.ceil(members.length / cols);
	
	var tableObj = document.createElement("table");
	
	var checkboxes = new Array();
	for (var i = 0; i < rows; i++) {
		var row = tableObj.insertRow(i);
		for (var j = 0; j < cols; j++) {
			var cell = row.insertCell(j);

			if (members[(j * rows) + i]) {
				var checkbox = document.createElement("input");
				var checkboxId = "clipboardEl_" + window.ss_clipboardUsersFormElement[prefix] + "_" + prefix + "_" + (i * rows) + j;
				
				
				checkbox.setAttribute("type", "checkbox");
				checkbox.setAttribute("name", window.ss_clipboardUsersFormElement[prefix]);
				checkbox.setAttribute("id", checkboxId);
				checkbox.setAttribute("value", members[(j * rows) + i][0]);
				
				cell.appendChild(checkbox);
				checkbox.checked = "checked";// must be after appendChild (IE problem)
				
				var label = document.createElement("label");
				
	
				var txtNode = document.createTextNode(members[(j * rows) + i][1]);
				label.appendChild(txtNode);
				
				cell.appendChild(label);
				label.htmlFor = checkboxId;
				
				checkboxes.push(checkbox);
			}
		}		
	}
	
	ss_clipboardUsersCheckboxes[prefix] = checkboxes;
	window.ss_clipboardUsersLoaded[prefix] = true;
	var ajaxLoadingIndicatorPane = "ss_clipboardUsersList_" + prefix;
	
	if (members.length > 0) {
	
		var hrefSelectAllObj = document.createElement("a");
		hrefSelectAllObj.href = "javascript: //";
		dojo.connect(hrefSelectAllObj, "onclick", function(evt) {
			ss_clipboardUsersSelectAll(prefix);
	    });		
		hrefSelectAllObj.className = "ss_linkButton";
		hrefSelectAllObj.style.marginRight = "5px";
		hrefSelectAllObj.appendChild(document.createTextNode(ss_selectAllBtnText));

		var hrefDeselectAllObj = document.createElement("a");
		hrefDeselectAllObj.href = "javascript: //";
		dojo.connect(hrefDeselectAllObj, "onclick", function(evt) {
			ss_clipboardUsersDeselectAll(prefix);
	    });
		hrefDeselectAllObj.className = "ss_linkButton";
		hrefDeselectAllObj.style.marginRight = "5px";
		hrefDeselectAllObj.appendChild(document.createTextNode(ss_clearAllBtnText));

		$(ajaxLoadingIndicatorPane).appendChild(document.createElement("br"));
		$(ajaxLoadingIndicatorPane).appendChild(hrefSelectAllObj);
		$(ajaxLoadingIndicatorPane).appendChild(hrefDeselectAllObj);
		$(ajaxLoadingIndicatorPane).appendChild(document.createElement("br"));
		$(ajaxLoadingIndicatorPane).appendChild(tableObj);
		
	} else {
		$(ajaxLoadingIndicatorPane).innerHTML = ss_noUsersOnClipboardText;
	}
}


function ss_clipboardUsersSelectAll(prefix) {
	for (var i = 0; i < ss_clipboardUsersCheckboxes[prefix].length; i++) {
		ss_clipboardUsersCheckboxes[prefix][i].checked = true;
	}
}

function ss_clipboardUsersDeselectAll(prefix) {
	for (var i = 0; i < ss_clipboardUsersCheckboxes[prefix].length; i++) {
		ss_clipboardUsersCheckboxes[prefix][i].checked = false;
	}
}

