
if (!window.ss_teamMembersCountLoaded)
	window.ss_teamMembersCountLoaded = new Array();
if (!window.ss_teamMembersLoaded)
	window.ss_teamMembersLoaded  = new Array();
if (!window.ss_teamMembersLoadUrl)
	window.ss_teamMembersLoadUrl  = new Array();
if (!window.ss_teamMembersClickRoutine)
	window.ss_teamMembersClickRoutine  = new Array();

function ss_setTeamMembersVariables(prefix, url, clickRoutine) {
	ss_teamMembersLoadUrl[prefix]  = url;
	ss_teamMembersClickRoutine[prefix]  = clickRoutine;
}


function ss_displayTeamMembersMenu(url, prefix) {
	if (ss_teamMembersCountLoaded[prefix]) {
		var aR = new ss_AjaxRequest("");
		aR.setData("prefix", prefix);	
		ss_displayTeamMembersMenu_postRequest(aR);
		return;
	}		

	url += "&ss_divId=teamMembersAmount_" + prefix;
	
	var ajaxRequest = new ss_AjaxRequest(url);	
	ajaxRequest.setPostRequest(ss_displayTeamMembersMenu_postRequest);
	ajaxRequest.setUseGET();
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.sendRequest();
}

function ss_displayTeamMembersMenu_postRequest(ajaxRequest) {
	var prefix = ajaxRequest.getData("prefix");
	var divObj = document.getElementById("ss_teamOptions_" + prefix);
	ss_moveDivToBody("ss_teamOptions_" + prefix);
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_teamIcon_" + prefix)) + + ss_getDivWidth("ss_teamIcon_" + prefix))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_teamIcon_" + prefix)))
	ss_showDivActivate("ss_teamOptions_" + prefix);	
	
	if (ss_teamMembersLoaded[prefix] || ss_teamMembersCountLoaded[prefix]) 
		return;
	
	ss_teamMembersCountLoaded[prefix] = true;
	var ulObj = document.getElementById("ss_teamOptionsListUL_" + prefix);
	if (ulObj) {
		var getAllLIObj = document.createElement("li");
		getAllLIObj.setAttribute("class", "ss_getAllUsers");
		getAllLIObj.setAttribute("id", "ss_teamMembersList_" + prefix);
		getAllLIObj.setAttribute("onmouseover", "this.style.backgroundColor='#333'; this.style.color='#FFF'; ss_loadTeamMembers('" + prefix + "');");
		getAllLIObj.setAttribute("onmouseout", "this.style.backgroundColor='#FFF'; this.style.color='#333';");
		
		var getAllIMGObj = document.createElement("img");
		getAllIMGObj.setAttribute("class", "ss_getAllUsers");
		getAllIMGObj.setAttribute("border", "0");
		getAllIMGObj.setAttribute("src", ss_imagesPath + "pics/sym_s_collapse.gif");

		getAllLIObj.appendChild(getAllIMGObj);
		ulObj.appendChild(getAllLIObj);
	}
}

function ss_addAllUsersFromTeam(ajaxRequest) {
	var prefix = ajaxRequest.getData("prefix");
	var ulObj = document.getElementById('ss_teamOptionsListUL_' + prefix);
	var lisObj = ulObj.getElementsByTagName("li");
	for (var i = 0; i < lisObj.length; i++) {
		if (lisObj[i].getElementsByTagName("a") && 
			lisObj[i].getElementsByTagName("a").length > 0 &&
			lisObj[i].getElementsByTagName("a").item(0).onclick) {
			lisObj[i].getElementsByTagName("a").item(0).onclick();
		}
	}
}

function ss_loadTeamMembers(prefix, afterPostRoutine) {
	if (ss_teamMembersLoaded[prefix]) {
		if (afterPostRoutine) {
			var aR = new ss_AjaxRequest("");
			aR.setData("prefix", prefix);	
			afterPostRoutine(aR);
		}
		return;
	}
	ss_toggleAjaxLoadingIndicator("ss_teamMembersList_" + prefix, false);
	ss_teamMembersLoaded[prefix] = true;
	
	url = ss_teamMembersLoadUrl[prefix];
	url += "&ss_divId=ss_teamMembersList_" + prefix;
	url += "&clickRoutine=" + ss_teamMembersClickRoutine[prefix];
	
	var ajaxRequest = new ss_AjaxRequest(url);	
	if (afterPostRoutine)
		ajaxRequest.setPostRequest(afterPostRoutine);
	ajaxRequest.setUseGET();
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.sendRequest();
}

		