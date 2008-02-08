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


if (!window.ss_teamMembersLoaded)
	window.ss_teamMembersLoaded  = new Array();
if (!window.ss_teamMembersFormElement)
	window.ss_teamMembersFormElement  = new Array();
if (!window.ss_teamMembersCheckboxes)
	window.ss_teamMembersCheckboxes  = new Array();
	
function ss_setTeamMembersVariables(prefix, formElement) {
	ss_teamMembersFormElement[prefix]  = formElement;
}

function ss_toggleShowTeamMembersIcon(iconId) {
	var img = $(iconId);
	if (img && (img.src.indexOf("sym_s_expand") > -1)) {
		img.src = ss_imagesPath + "pics/sym_s_down.gif";
	} else if (img && (img.src.indexOf("sym_s_expand") == -1)) {
		img.src = ss_imagesPath + "pics/sym_s_expand.gif";
	}	
}

function ss_toggleTeamMembersList(objId) {
	var obj = $(objId);
	if (obj && obj.style.display == "block") {
		obj.style.display = "none";
		obj.style.visibility = 'hidden';
	} else if (obj && (obj.style.display == "none" || obj.style.display == "")) {
		obj.style.display = "block";
		obj.style.visibility = 'visible';
	}
}

function ss_loadTeamMembersList (url, prefix, checkAll) {
	var ajaxLoadingIndicatorPane = "ss_teamMembersList_" + prefix;
	ss_toggleShowTeamMembersIcon("ss_teamIcon_" + prefix);
	
	if (window.ss_teamMembersLoaded[prefix]) {
		ss_toggleTeamMembersList(ajaxLoadingIndicatorPane);
	} else {
		ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
		url += "\&randomNumber="+ss_random++;
		var bindArgs = {
	    	url: url,
			error: function(type, data, evt) {
				ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
			},
			load: function(type, data, evt) {
				window.ss_teamMembersLoaded[prefix] = true;
				ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
				ss_buildTeamMembersListTable(ajaxLoadingIndicatorPane, data, prefix, checkAll);
			},
			mimetype: "text/json",
			method: "get"
		};
	   
		dojo.io.bind(bindArgs);
	}
}

function ss_buildTeamMembersListTable(ajaxLoadingIndicatorPane, members, prefix, checkAll) {
try {
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
				var checkboxId = "teamMemberEl_" + window.ss_teamMembersFormElement[prefix] + "_" + prefix + "_" + (i * rows) + j;
				checkbox.setAttribute("type", "checkbox");
				checkbox.setAttribute("name", window.ss_teamMembersFormElement[prefix]);
				checkbox.setAttribute("id", checkboxId);
				checkbox.setAttribute("value", members[(j * rows) + i][0]);
				
				cell.appendChild(checkbox);
				
				if (checkAll) {
					checkbox.checked = "checked";// must be after appendChild (IE problem)
				}
				
				var label = document.createElement("label");
				label.setAttribute("for", checkboxId);
	
				var txtNode = document.createTextNode(members[(j * rows) + i][1]);
				label.appendChild(txtNode);
				
				cell.appendChild(label);
				
				checkboxes.push(checkbox);
			}
		}		
	}

	ss_teamMembersCheckboxes[prefix] = checkboxes;
	
	if (members.length > 0) {
						
		var hrefSelectAllObj = document.createElement("a");
		hrefSelectAllObj.href = "javascript: //";
//		hrefSelectAllObj.setAttribute("onClick", "ss_teamMembersSelectAll('" + prefix + "')");
		dojo.event.connect(hrefSelectAllObj, "onclick", function(evt) {
			ss_teamMembersSelectAll(prefix);
	    });
			
		
		hrefSelectAllObj.className = "ss_linkButton";
		hrefSelectAllObj.style.marginRight = "5px";
		hrefSelectAllObj.appendChild(document.createTextNode(ss_selectAllBtnText));

		var hrefDeselectAllObj = document.createElement("a");
		hrefDeselectAllObj.href = "javascript: //";
//		hrefDeselectAllObj.setAttribute("onClick", "ss_teamMembersDeselectAll('" + prefix + "')");
		dojo.event.connect(hrefDeselectAllObj, "onclick", function(evt) {
			ss_teamMembersDeselectAll(prefix);
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
		$(ajaxLoadingIndicatorPane).innerHTML = ss_noTeamMembersText;
	}
	
} catch (e){alert(e)}	
}

function ss_teamMembersSelectAll(prefix) {
	for (var i = 0; i < ss_teamMembersCheckboxes[prefix].length; i++) {
		ss_teamMembersCheckboxes[prefix][i].checked = true;
	}
}

function ss_teamMembersDeselectAll(prefix) {
	for (var i = 0; i < ss_teamMembersCheckboxes[prefix].length; i++) {
		ss_teamMembersCheckboxes[prefix][i].checked = false;
	}
}


