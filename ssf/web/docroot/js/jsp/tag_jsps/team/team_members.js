

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
				if (checkAll) checkbox.setAttribute("checked", "checked");
				
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

	ss_teamMembersCheckboxes[prefix] = checkboxes;
	
	if (members.length > 0) {
						
		var hrefSelectAllObj = document.createElement("a");
		hrefSelectAllObj.href = "javascript: //";
		hrefSelectAllObj.setAttribute("onClick", "ss_teamMembersSelectAll('" + prefix + "')");
		hrefSelectAllObj.className = "ss_linkButton";
		hrefSelectAllObj.style.marginRight = "5px";
		hrefSelectAllObj.appendChild(document.createTextNode("Select all"));

		var hrefDeselectAllObj = document.createElement("a");
		hrefDeselectAllObj.href = "javascript: //";
		hrefDeselectAllObj.setAttribute("onClick", "ss_teamMembersDeselectAll('" + prefix + "')");
		hrefDeselectAllObj.className = "ss_linkButton";
		hrefDeselectAllObj.style.marginRight = "5px";
		hrefDeselectAllObj.appendChild(document.createTextNode("Clear all"));

		$(ajaxLoadingIndicatorPane).appendChild(document.createElement("br"));
		$(ajaxLoadingIndicatorPane).appendChild(hrefSelectAllObj);
		$(ajaxLoadingIndicatorPane).appendChild(hrefDeselectAllObj);
		$(ajaxLoadingIndicatorPane).appendChild(document.createElement("br"));
		$(ajaxLoadingIndicatorPane).appendChild(tableObj);
		
	} else {
		$(ajaxLoadingIndicatorPane).innerHTML = "There are no users in team";
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


