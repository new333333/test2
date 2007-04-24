/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
	var img = $(iconId);
	if (img && (img.src.indexOf("sym_s_expand") > -1)) {
		img.src = ss_imagesPath + "pics/sym_s_down.gif";
	} else if (img && (img.src.indexOf("sym_s_expand") == -1)) {
		img.src = ss_imagesPath + "pics/sym_s_expand.gif";
	}	
}

function ss_toggleClipboardUsersList(objId) {
	var obj = $(objId);
	if (obj && obj.style.display == "block") {
		obj.style.display = "none";
		obj.style.visibility = 'hidden';
	} else if (obj && (obj.style.display == "none" || obj.style.display == "")) {
		obj.style.display = "block";
		obj.style.visibility = 'visible';
	}
}

function ss_loadClipboardUsersList (url, prefix) {
	var ajaxLoadingIndicatorPane = "ss_clipboardUsersList_" + prefix;
	ss_toggleShowClipboardUsersIcon("ss_clipboardUsersIcon_" + prefix);
	
	if (window.ss_clipboardUsersLoaded[prefix]) {
		ss_toggleClipboardUsersList(ajaxLoadingIndicatorPane);
	} else {
		ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
		url += "\&randomNumber="+ss_random++;
		var bindArgs = {
	    	url: url,
			error: function(type, data, evt) {
				ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
			},
			load: function(type, data, evt) {
				window.ss_clipboardUsersLoaded[prefix] = true;
				ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
				ss_buildClipboardUsersListTable(ajaxLoadingIndicatorPane, data, prefix);
			},
			mimetype: "text/json",
			method: "get"
		};
	   
		dojo.io.bind(bindArgs);
	}
}

function ss_buildClipboardUsersListTable(ajaxLoadingIndicatorPane, members, prefix) {
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
				checkbox.setAttribute("checked", "checked");
				
				cell.appendChild(checkbox);
				
				var label = document.createElement("label");
				label.setAttribute("for", checkboxId);
	
				var txtNode = document.createTextNode(members[(j * rows) + i][1]);
				label.appendChild(txtNode);
				
				cell.appendChild(label);
				
				checkboxes.push(checkbox);
			}
		}		
	}
	
	ss_clipboardUsersCheckboxes[prefix] = checkboxes;
	
	if (members.length > 0) {
	
		var hrefSelectAllObj = document.createElement("a");
		hrefSelectAllObj.href = "javascript: //";
		dojo.event.connect(hrefSelectAllObj, "onclick", function(evt) {
			ss_clipboardUsersSelectAll(prefix);
	    });		
		hrefSelectAllObj.className = "ss_linkButton";
		hrefSelectAllObj.style.marginRight = "5px";
		hrefSelectAllObj.appendChild(document.createTextNode(ss_selectAllBtnText));

		var hrefDeselectAllObj = document.createElement("a");
		hrefDeselectAllObj.href = "javascript: //";
		dojo.event.connect(hrefDeselectAllObj, "onclick", function(evt) {
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

