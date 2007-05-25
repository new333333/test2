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

function ss_declareFindUserSearchVariables () {
	window.ss_findUser_searchText = new Array();
	window.ss_findUser_pageNumber = new Array();
	window.ss_findUser_pageNumberBefore = new Array();
	window.ss_findUserDivTopOffset = new Array();

	window.ss_findUserSearchInProgress = new Array();
	window.ss_findUserSearchWaiting = new Array();
	window.ss_findUserSearchStartMs = new Array();
	window.ss_findUserSearchLastText = new Array();
	window.ss_findUserSearchLastTextObjId = new Array();
	window.ss_findUserSearchLastElement = new Array();
	window.ss_findUserSearchLastfindUserGroupType = new Array();
	window.ss_findUserClickRoutine = new Array();
	window.ss_findUserClickRoutineArgs = new Array();
	window.ss_findUserViewUrl = new Array();
	window.ss_findUserLeaveResultsVisible = new Array();
	window.ss_findUserSearchUrl = new Array();
	window.ss___findUserIsMouseOverList = new Array();
}

function ss_findUserConfVariableForPrefix(prefix, clickRoutine, clickRoutineArgs, viewUrl, leaveResultsVisible, userSearchUrl) {
	if (!window.ss_findUser_searchText) {
		ss_declareFindUserSearchVariables();
	}
	ss_findUser_searchText[prefix] = "";
	ss_findUser_pageNumber[prefix] = 0;
	ss_findUser_pageNumberBefore[prefix] = 0;
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
	if (clickRoutineArgs && clickRoutineArgs != "")
		ss_findUserClickRoutineArgs[prefix] = clickRoutineArgs.split(",");
	else
		ss_findUserClickRoutineArgs[prefix] = new Array();	
}

function ss_findUserSearch(prefix, textObjId, elementName, findUserGroupType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text.trim() != ss_findUserSearchLastText[prefix].trim()) {
		ss_findUser_pageNumber[prefix] = 0;
		ss_findUser_pageNumberBefore[prefix] = 0;
	}
	ss_setupStatusMessageDiv();
	ss_moveDivToBody('ss_findUserNavBarDiv' + prefix);
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
		var ulObj = document.getElementById('available_'+prefix)
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findUserSelectItem(prefix, liObjs[0]);
			return;
		}
 	}
 	//Fade the previous selections
 	var savedColor = "#000000";
 	var divObj = document.getElementById('available_' + prefix);
 	if (divObj != null && divObj.style && divObj.style.color) {
 		savedColor = divObj.style.color;
 	}
 	if (divObj != null) divObj.style.color = "#cccccc";

 	ss_debug("//"+text+"//")
	var searchText = text;
	if (searchText.length > 0 && searchText.charAt(searchText.length-1) != " ") {
		if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	}
	if (ss_userDisplayStyle && ss_userDisplayStyle == 'accessible') {
		ss_findUserSearchAccessible(prefix, searchText, elementName, findUserGroupType, crFound);
		ss_findUserSearchInProgress[prefix] = 0;
		return;
	}
	var ajaxRequest = new ss_AjaxRequest(ss_findUserSearchUrl[prefix]); //Create AjaxRequest object
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findUser_pageNumber[prefix])
	ajaxRequest.addKeyValue("findType", findUserGroupType)
	ajaxRequest.addKeyValue("listDivId", "available_" + prefix)
	ajaxRequest.addKeyValue("namespace", prefix);
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
	ss_debug('ss_postFindUserRequest'+prefix);
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findUserSearchInProgress[prefix] = 0;

	ss_showFindUserSelections(prefix);
	
 	//Show this at full brightness
	var divObj = document.getElementById('ss_findUserNavBarDiv_' + prefix);
 	divObj = document.getElementById('available_' + prefix);
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
	
	//See if there is another search request to be done
	function runItLater(a, b, c, d) {
      return (function () {
        ss_findUserSearch(a, b, c, d)
      });
    }
    var runitRef = runItLater(prefix, ss_findUserSearchLastTextObjId[prefix], ss_findUserSearchLastElement[prefix], ss_findUserSearchLastfindUserGroupType[prefix]);
	if (ss_findUserSearchWaiting[prefix] == 1) {
		setTimeout(runitRef, 100);
	}

	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_' + prefix);
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout(function (){ss_findUserSelectItem0(prefix);}, 100);
			return;
		}
	}
}
function ss_showFindUserSelections(prefix) {
	var divObj = document.getElementById('ss_findUserNavBarDiv_' + prefix);
	ss_moveDivToBody('ss_findUserNavBarDiv_' + prefix);
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findUser_searchText_bottom_" + prefix) + ss_findUserDivTopOffset[prefix]))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findUser_searchText_bottom_" + prefix)))
	ss_showDivActivate('ss_findUserNavBarDiv_' + prefix);
}
function ss_findUserSelectItem0(prefix) {
	var ulObj = document.getElementById('available_' + prefix);
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findUserSelectItem(prefix, liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findUserSelectItem(prefix, obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var id = ss_replaceSubStr(obj.id, 'ss_findUser_id_', "");
	if (ss_findUserClickRoutine[prefix] != "") {
		//eval(ss_findUserClickRoutine[prefix] + "('"+id+"');");
		window[ss_findUserClickRoutine[prefix]].apply(this, [id, obj].concat(ss_findUserClickRoutineArgs[prefix]));
		if (ss_findUserLeaveResultsVisible[prefix]) {
			//setTimeout("ss_showFindUserSelections('"+prefix+"');", 200)
			setTimeout(function() {ss_showFindUserSelections(prefix)}, 200); // faster then "ss_showFindUserSelections(prefix)"
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
		var ulObj = document.getElementById('available_' + me.prefix)
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findUserSelectItem(me.prefix, liObjs[0]);
		}
		return false;
	}
}

function ss_findUserNextPage(prefix) {
	ss_findUser_pageNumberBefore[prefix] = ss_findUser_pageNumber[prefix];
	ss_findUser_pageNumber[prefix]++;
	ss_findUserSearch(prefix, ss_findUserSearchLastTextObjId[prefix], ss_findUserSearchLastElement[prefix], ss_findUserSearchLastfindUserGroupType[prefix]);
}

function ss_findUserPrevPage(prefix) {
	ss_findUser_pageNumberBefore[prefix] = ss_findUser_pageNumber[prefix];
	ss_findUser_pageNumber[prefix]--;
	if (ss_findUser_pageNumber[prefix] < 0) ss_findUser_pageNumber[prefix] = 0;
	ss_findUserSearch(prefix, ss_findUserSearchLastTextObjId[prefix], ss_findUserSearchLastElement[prefix], ss_findUserSearchLastfindUserGroupType[prefix]);
}

function ss_findUserClose(prefix) {
	var textObj = document.getElementById('ss_findUser_searchText_' + prefix);
	if (textObj == null) {
		textObj = self.parent.document.getElementById('ss_findUser_searchText_' + prefix);
	}
	if (textObj != null) textObj.focus();
}

function ss_findUserInitializeForm(formName, prefix) {
	if (formName != '') {
		var saveFindUserData = new ss_saveFindUserData(prefix);
		ss_createOnSubmitObj(prefix + 'onSubmit_single_user', formName, saveFindUserData.invoke);
	}
}

function ss_findUserBlurTextArea(prefix) {
	if (!ss___findUserIsMouseOverList[prefix]) {
		setTimeout(function() { ss_hideDiv('ss_findUserNavBarDiv_' + prefix) } , 200);
	}
}

function ss_findUserMouseOverList(prefix) {
	ss___findUserIsMouseOverList[prefix] = true;
}

function ss_findUserMouseOutList(prefix) {
	ss___findUserIsMouseOverList[prefix] = false;
}


function ss_findUserSearchAccessible(prefix, searchText, elementName, findUserGroupType, crFound) {
	//In accessibility mode, wait for the user to type cr
	if (!crFound && parseInt(ss_findUser_pageNumber[prefix]) == 0 && 
			parseInt(ss_findUser_pageNumberBefore[prefix]) == 0) return;
	
    var iframeDivObj = self.document.getElementById("ss_findIframeDiv");
    var iframeObj = self.document.getElementById("ss_findIframe");
    var iframeDivObjParent = self.parent.document.getElementById("ss_findIframeDiv");
    var iframeObjParent = self.parent.document.getElementById("ss_findIframe");
    var textObj = self.document.getElementById('ss_findUser_searchText_bottom_'+prefix);
    if (iframeDivObjParent == null && iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
	    iframeDivObjParent = iframeDivObj;
        iframeDivObj.setAttribute("id", "ss_findIframeDiv");
		iframeDivObj.className = "ss_popupMenu";
		iframeDivObj.style.zIndex = ssPopupZ;
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_findIframe");
        iframeObj.style.width = "400px"
        iframeObj.style.height = "300px"
		iframeDivObj.appendChild(iframeObj);
	    var closeDivObj = self.document.createElement("div");
	    closeDivObj.style.border = "2px solid gray";
	    closeDivObj.style.marginTop = "1px";
	    closeDivObj.style.padding = "6px";
	    iframeDivObj.appendChild(closeDivObj);
	    var aObj = self.document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDiv('ss_findIframeDiv');ss_findUserClose('"+prefix+"');");
	    aObj.style.border = "2px outset black";
	    aObj.style.padding = "2px";
	    aObj.appendChild(document.createTextNode(ss_findButtonClose));
	    closeDivObj.appendChild(aObj);
		self.document.getElementsByTagName( "body" ).item(0).appendChild(iframeDivObj);
    }
    if (iframeDivObj == null) iframeDivObj = iframeDivObjParent;
    if (iframeObj == null) iframeObj = iframeObjParent;
    if (self.parent == self && textObj != null) {
    	var x = dojo.html.getAbsolutePosition(textObj, true).x
    	var y = dojo.html.getAbsolutePosition(textObj, true).y
	    ss_setObjectTop(iframeDivObj, y + "px");
	    ss_setObjectLeft(iframeDivObj, x + "px");
	}
	ss_showDiv("ss_findIframeDiv");
	var url = ss_AjaxBaseUrl;
	url = ss_replaceSubStrAll(url, "&amp;", "&");
	url += "&operation=find_user_search";
	url += "&searchText=" + searchText;
	url += "&maxEntries=" + "10";
	url += "&pageNumber=" + ss_findUser_pageNumber[prefix];
	url += "&findType=" + findUserGroupType;
	url += "&listDivId=" + "available_" + prefix;
	url += "&namespace=" + prefix;
    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
		self.location.href = url;
	} else {
		iframeObj.src = url;
	}
}
