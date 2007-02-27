
if (!window.ss_findUser_searchText) window.ss_findUser_searchText = new Array();
if (!window.ss_findUser_pageNumber)	window.ss_findUser_pageNumber = new Array();
if (!window.ss_findUserDivTopOffset) window.ss_findUserDivTopOffset = new Array();

if (!window.ss_findUserSearchInProgress) window.ss_findUserSearchInProgress = new Array();
if (!window.ss_findUserSearchWaiting) window.ss_findUserSearchWaiting = new Array();
if (!window.ss_findUserSearchStartMs) window.ss_findUserSearchStartMs = new Array();
if (!window.ss_findUserSearchLastText) window.ss_findUserSearchLastText = new Array();
if (!window.ss_findUserSearchLastTextObjId) window.ss_findUserSearchLastTextObjId = new Array();
if (!window.ss_findUserSearchLastElement) window.ss_findUserSearchLastElement = new Array();
if (!window.ss_findUserSearchLastfindUserGroupType) window.ss_findUserSearchLastfindUserGroupType = new Array();
if (!window.ss_findUserClickRoutine) window.ss_findUserClickRoutine = new Array();
if (!window.ss_findUserViewUrl) window.ss_findUserViewUrl = new Array();
if (!window.ss_findUserLeaveResultsVisible) window.ss_findUserLeaveResultsVisible = new Array();
if (!window.ss_findUserSearchUrl) window.ss_findUserSearchUrl = new Array();


function ss_findUserConfVariableForPrefix(prefix, clickRoutine, viewUrl, leaveResultsVisible, userSearchUrl) {
	ss_findUser_searchText[prefix] = "";
	ss_findUser_pageNumber[prefix] = 0;
	ss_findUserDivTopOffset[prefix] = 2;
	
	ss_findUserSearchInProgress[prefix] = 0;
	ss_findUserSearchWaiting[prefix] = 0;
	ss_findUserSearchStartMs[prefix] = 0;
	ss_findUserSearchLastText[prefix] = "";
	ss_findUserSearchLastTextObjId[prefix] = "";
	ss_findUserSearchLastElement[prefix] = "";
	ss_findUserSearchLastfindUserGroupType[prefix] = "";
	ss_findUserClickRoutine[prefix] = clickRoutine;
	ss_findUserViewUrl[prefix] = viewUrl;
	ss_findUserLeaveResultsVisible[prefix] = leaveResultsVisible;
	ss_findUserSearchUrl[prefix] = userSearchUrl;
}

function ss_findUserSearch(prefix, textObjId, elementName, findUserGroupType) {
	var textObj = $(textObjId);
	var text = textObj.value;
	if (text == '' || text != ss_findUserSearchLastText[prefix]) ss_findUser_pageNumber[prefix] = 0;
	ss_setupStatusMessageDiv()
	//ss_moveDivToBody('ss_findUserNavBarDiv${prefix}');
	//Are we already doing a search?
	if (ss_findUserSearchInProgress[prefix] == 1) {
		//Yes, hold this request until the current one finishes
		ss_findUserSearchLastText[prefix] = text;
		ss_findUserSearchLastTextObjId[prefix] = textObjId;
		ss_findUserSearchLastElement[prefix] = elementName;
		ss_findUserSearchLastfindUserGroupType[prefix] = findUserGroupType;
		ss_findUserSearchWaiting[prefix] = 1;
		var d = new Date();
		var curr_msec = d.getTime();
		if (ss_findUserSearchStartMs[prefix] == 0 || curr_msec < parseInt(ss_findUserSearchStartMs[prefix] + 1000)) {
			ss_debug('  hold search request...')
			if (ss_findUserSearchStartMs[prefix] == 0) ss_findUserSearchStartMs[prefix] = curr_msec;
			return;
		}
		//The user waited for over a second, let this request go through
		ss_findUserSearchStartMs[prefix] = 0;
		ss_debug('   Stopped waiting')
	}
	ss_findUserSearchInProgress[prefix] = 1;
	ss_findUserSearchWaiting[prefix] = 0;
	ss_findUserSearchLastTextObjId[prefix] = textObjId;
	ss_findUserSearchLastElement[prefix] = elementName;
	ss_findUserSearchLastText[prefix] = text;
	ss_findUserSearchLastfindUserGroupType[prefix] = findUserGroupType;
 	//Save the text in case the user changes the search type
 	ss_findUser_searchText[prefix] = text;
 	
 	//See if the user ended the string with a CR. If so, then try to launch.
 	var newText = "";
 	var crFound = 0;
 	for (var i = 0; i < text.length; i++) {
 		if (text.charCodeAt(i) == 10 || text.charCodeAt(i) == 13) {
 			crFound = 1;
 			break;
 		} else {
 			newText += text.charAt(i);
 		}
 	}
 	if (crFound == 1) {
 		textObj.value = newText;
 		text = textObj.value;
		var ulObj = $('available_' + prefix)
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findUserSelectItem(prefix, liObjs[0]);
			return;
		}
 	}
 	//Fade the previous selections
 	var savedColor = "#000000";
 	var divObj = $('available_' + prefix);
 	if (divObj != null && divObj.style && divObj.style.color) {
 		savedColor = divObj.style.color;
 	}
 	if (divObj != null) divObj.style.color = "#cccccc";

 	ss_debug("//"+text+"//")
	var ajaxRequest = new ss_AjaxRequest(ss_findUserSearchUrl[prefix]); //Create AjaxRequest object
	var searchText = text;
	if (searchText.length > 0 && searchText.charAt(searchText.length-1) != " ") {
		if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	}
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findUser_pageNumber[prefix])
	ajaxRequest.addKeyValue("findType", findUserGroupType)
	ajaxRequest.addKeyValue("listDivId", "available_" + prefix)
	ajaxRequest.addKeyValue("namespace", prefix);
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preFindUserRequest);
	ajaxRequest.setPostRequest(ss_postFindUserRequest);
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setData("crFound", crFound)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFindUserRequest(obj) {
	var prefix = obj.getData("prefix");
	ss_debug('ss_postFindUserRequest');
	//See if there was an error
	if (self.$("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findUserSearchInProgress[prefix] = 0;

	ss_showFindUserSelections(prefix);
	
 	//Show this at full brightness
	var divObj = $('ss_findUserNavBarDiv_' + prefix);
 	divObj = $('available_' + prefix);
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
	
	//See if there is another search request to be done
	if (ss_findUserSearchWaiting[prefix] == 1) {
		setTimeout('ss_findUserSearch(' + prefix + ', ' + ss_findUserSearchLastTextObjId[prefix] + ', ' + ss_findUserSearchLastElement[prefix] + ', ' + ss_findUserSearchLastfindUserGroupType[prefix] +')', 100)
	}

	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = $('available_' + prefix)
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout("ss_findUserSelectItem0('" + prefix + "');", 100);
			return;
		}
	}
}
function ss_showFindUserSelections(prefix) {
	var divObj = $('ss_findUserNavBarDiv_' + prefix);
	ss_moveDivToBody('ss_findUserNavBarDiv_' + prefix);
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findUser_searchText_bottom_" + prefix) + ss_findUserDivTopOffset[prefix]))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findUser_searchText_bottom_" + prefix)))
	ss_showDivActivate('ss_findUserNavBarDiv_' + prefix);
}
function ss_findUserSelectItem0(prefix) {
	var ulObj = $('available_' + prefix);
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findUserSelectItem(prefix, liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findUserSelectItem(prefix, obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var id = ss_replaceSubStr(obj.id, 'ss_findUser_id_', "");
		var textObj = $(ss_findUserSearchLastTextObjId[prefix]);
		textObj.value = "";
	if (ss_findUserClickRoutine[prefix] != "") {
		eval(ss_findUserClickRoutine[prefix]+"(id, obj);")
		if (ss_findUserLeaveResultsVisible[prefix]) {
		  setTimeout("ss_showFindUserSelections('" + prefix + "');", 200)
		}
	} else {
		url = ss_replaceSubStr(ss_findUserViewUrl[prefix], 'ss_entryIdPlaceholder', id);
		self.location.href = url;
	}
}

function ss_saveFindUserData(prefix) {
	this.prefix = prefix;
		
	var me = this;
	
	this.invoke = function() {
		ss_debug('ss_saveFindUserData')
		var ulObj = $('available_' + me.prefix)
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findUserSelectItem(me.prefix, liObjs[0]);
		}
		return false;
	}
}

function ss_findUserNextPage(prefix) {
	ss_findUser_pageNumber[prefix]++;
	ss_findUserSearch(prefix, ss_findUserSearchLastTextObjId[prefix], ss_findUserSearchLastElement[prefix], ss_findUserSearchLastfindUserGroupType[prefix]);
}

function ss_findUserPrevPage(prefix) {
	ss_findUser_pageNumber[prefix]--;
	if (ss_findUser_pageNumber[prefix] < 0) ss_findUser_pageNumber[prefix] = 0;
	ss_findUserSearch(prefix, ss_findUserSearchLastTextObjId[prefix], ss_findUserSearchLastElement[prefix], ss_findUserSearchLastfindUserGroupType[prefix]);
}

function ss_findUserClose(prefix) {
	$('ss_findUser_searchText_' + prefix).focus();
}


function ss_findUserInitializeForm(formName, prefix) {
	if (formName != '') {
		var saveFindUserData = new ss_saveFindUserData(prefix);
		ss_createOnSubmitObj(prefix + 'onSubmit', formName, saveFindUserData.invoke);
	}
}
