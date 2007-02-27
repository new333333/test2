
if (!window.ss_clipboardUsersLoaded)
	ss_clipboardUsersLoaded = new Array();

function ss_displayClipboardMenu(prefix) {
	var divObj = $("ss_clipboardOptions_" + prefix);
	ss_moveDivToBody("ss_clipboardOptions_" + prefix);
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_clipboardIcon_" + prefix)) + + ss_getDivWidth("ss_clipboardIcon_" + prefix))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_clipboardIcon_" + prefix)))
	ss_showDivActivate("ss_clipboardOptions_" + prefix);
}
	
function ss_clickAllClipboardUsers(ajaxRequest) { 
	var prefix = ajaxRequest.getData("prefix");
	var ulObj = $("ss_clipboardOptionsListUL_" + prefix);
	var lisObj = ulObj.getElementsByTagName("li");
	for (var i = 0; i < lisObj.length; i++) {
		if (lisObj[i].getElementsByTagName("a") && 
			lisObj[i].getElementsByTagName("a").length > 0 &&
			lisObj[i].getElementsByTagName("a").item(0).onclick) {
			lisObj[i].getElementsByTagName("a").item(0).onclick();
		}
	}
}

function ss_loadClipboardUsers(url, prefix, clickRoutine, afterPostRoutine) {
	if (ss_clipboardUsersLoaded[prefix]) {
		if (afterPostRoutine) {
			var aR = new ss_AjaxRequest("");
			aR.setData("prefix", prefix);	
			afterPostRoutine(aR);
		}
		return;
	}
	ss_toggleAjaxLoadingIndicator("ss_clipboardUsersList_" + prefix, false);
	ss_clipboardUsersLoaded[prefix] = true;

	url += "&ss_divId=ss_clipboardUsersList_" + prefix;
	url += "&clickRoutine=" + clickRoutine;
	
	var ajaxRequest = new ss_AjaxRequest(url);	
	if (afterPostRoutine)
		ajaxRequest.setPostRequest(afterPostRoutine);
	ajaxRequest.setUseGET();
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.sendRequest();
}
    