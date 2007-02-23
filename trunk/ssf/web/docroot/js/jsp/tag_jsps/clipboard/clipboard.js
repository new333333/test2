
if (!window.clipboardUsersLoaded)
	clipboardUsersLoaded = new Array();

function displayClipboardMenu(prefix) {
	var divObj = $("clipboardMenu_" + prefix);
	ss_moveDivToBody("clipboardMenu_" + prefix);
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("clipboardIcon_" + prefix)) + + ss_getDivWidth("clipboardIcon_" + prefix))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("clipboardIcon_" + prefix)))
	ss_showDivActivate("clipboardMenu_" + prefix);
}
	
function clickAllClipboardUsers(ajaxRequest) { 
	var prefix = ajaxRequest.getData("prefix");
	var ulObj = $("clipboardUsersListUL_" + prefix);
	var lisObj = ulObj.getElementsByTagName("li");
	for (var i = 0; i < lisObj.length; i++) {
		if (lisObj[i].getElementsByTagName("a") && 
			lisObj[i].getElementsByTagName("a").length > 0 &&
			lisObj[i].getElementsByTagName("a").item(0).onclick) {
			lisObj[i].getElementsByTagName("a").item(0).onclick();
		}
	}
}

function loadClipboardUsers(url, prefix, clickRoutine, afterPostRoutine) {
	if (clipboardUsersLoaded[prefix]) {
		if (afterPostRoutine) {
			var aR = new ss_AjaxRequest("");
			aR.setData("prefix", prefix);	
			afterPostRoutine(aR);
		}
		return;
	}
	toggleAjaxLoadingIndicator("clipboardUsersList_" + prefix, false);
	clipboardUsersLoaded[prefix] = true;

	url += "&ss_divId=clipboardUsersList_" + prefix;
	url += "&clickRoutine=" + clickRoutine;
	
	var ajaxRequest = new ss_AjaxRequest(url);	
	if (afterPostRoutine)
		ajaxRequest.setPostRequest(afterPostRoutine);
	ajaxRequest.setUseGET();
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.sendRequest();
}
    