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

function ss_declareFindTagSearchVariables () {
	window.ss_findTag_searchText = new Array();
	window.ss_findTag_pageNumber = new Array();
	window.ss_findTag_pageNumberBefore = new Array();
	window.ss_findTagDivTopOffset = new Array();
	window.ss_findTagSearchInProgress = new Array();
	window.ss_findTagSearchWaiting = new Array();
	window.ss_findTagSearchStartMs = new Array();
	window.ss_findTagSearchLastText = new Array();
	window.ss_findTagSearchLastTextObjId = new Array();
	window.ss_findTagSearchLastElement = new Array();
	window.ss_findTagSearchLastfindTagType = new Array();
	window.ss_findTagClickRoutine = new Array();
	
	window.ss_findTagViewUrl = new Array();
	window.ss_findTagLeaveResultsVisible = new Array();
	window.ss_findTagSearchUrl = new Array();
	
	window.ss___findTagIsMouseOverList = new Array();
}

function ss_confFindTagSearchVariables(prefix, clickRoutine, viewUrl, leaveResultsVisible, userSearchUrl) {
	if (!window.ss_findTag_searchText) {
		ss_declareFindTagSearchVariables();
	}
	ss_findTag_searchText[prefix] = "";
	ss_findTag_pageNumber[prefix] = 0;
	ss_findTag_pageNumberBefore[prefix] = 0;
	ss_findTagDivTopOffset[prefix] = 2;
	ss_findTagSearchInProgress[prefix] = 0;
	ss_findTagSearchWaiting[prefix] = 0;
	ss_findTagSearchStartMs[prefix] = 0;
	ss_findTagSearchLastText[prefix] = "";
	ss_findTagSearchLastTextObjId[prefix] = "";
	ss_findTagSearchLastElement[prefix] = "";
	ss_findTagSearchLastfindTagType[prefix] = "";
	ss_findTagClickRoutine[prefix] = clickRoutine;
	
	ss_findTagViewUrl[prefix] = viewUrl;
	ss_findTagLeaveResultsVisible[prefix] = leaveResultsVisible;
	ss_findTagSearchUrl[prefix] = userSearchUrl;
	
}

function ss_findTagSearch(prefix, textObjId, elementName, findTagType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text != ss_findTagSearchLastText[prefix]) {
		ss_findTag_pageNumber[prefix] = 0;
		ss_findTag_pageNumberBefore[prefix] = 0;
	}
	ss_setupStatusMessageDiv();
	ss_moveDivToBody('ss_findTagNavBarDiv'+prefix);
	//Are we already doing a search?
	if (ss_findTagSearchInProgress[prefix] == 1) {
		//Yes, hold this request until the current one finishes
		ss_findTagSearchLastText[prefix] = text;
		ss_findTagSearchLastTextObjId[prefix] = textObjId;
		ss_findTagSearchLastElement[prefix] = elementName;
		ss_findTagSearchLastfindTagType[prefix] = findTagType;
		ss_findTagSearchWaiting[prefix] = 1;
		var d = new Date();
		var curr_msec = d.getTime();
		if (ss_findTagSearchStartMs[prefix] == 0 || curr_msec < parseInt(ss_findTagSearchStartMs[prefix] + 1000)) {
			ss_debug('  hold search request...')
			if (ss_findTagSearchStartMs[prefix] == 0) ss_findTagSearchStartMs[prefix] = curr_msec;
			return;
		}
		//The user waited for over a second, let this request go through
		ss_findTagSearchStartMs[prefix] = 0;
		ss_debug('   Stopped waiting');
	}
	ss_findTagSearchInProgress[prefix] = 1;
	ss_findTagSearchWaiting[prefix] = 0;
	ss_findTagSearchLastTextObjId[prefix] = textObjId;
	ss_findTagSearchLastElement[prefix] = elementName;
	ss_findTagSearchLastText[prefix] = text;
	ss_findTagSearchLastfindTagType[prefix] = findTagType;
 	//Save the text in case the user changes the search type
 	ss_findTag_searchText[prefix] = text;
 	
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
		var ulObj = document.getElementById('available_'+prefix);
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findTagSelectItem(prefix, liObjs[0]);
			return;
		}
 	}
 	//Fade the previous selections
 	var savedColor = "#000000";
 	var divObj = document.getElementById('available_'+prefix);
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
		ss_findTagSearchAccessible(prefix, searchText, elementName, findTagType, crFound);
		ss_findTagSearchInProgress[prefix] = 0;
		return;
	}
 	var ajaxRequest = new ss_AjaxRequest(ss_findTagSearchUrl[prefix]); //Create AjaxRequest object
	ajaxRequest.addKeyValue("searchText", searchText);
	ajaxRequest.addKeyValue("maxEntries", "10");
	ajaxRequest.addKeyValue("pageNumber", ss_findTag_pageNumber[prefix]);
	ajaxRequest.addKeyValue("findType", findTagType);
	ajaxRequest.addKeyValue("listDivId", "available_"+prefix);
	ajaxRequest.addKeyValue("namespace", prefix); 
	ajaxRequest.setPostRequest(ss_postFindTagRequest);
	ajaxRequest.setData("prefix", prefix);
	ajaxRequest.setData("elementName", elementName);
	ajaxRequest.setData("savedColor", savedColor);
	ajaxRequest.setData("crFound", crFound);
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFindTagRequest(obj) {
	var prefix = obj.getData("prefix");
	ss_debug('ss_postFindTagRequest'+prefix);
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findTagSearchInProgress[prefix] = 0;

	ss_showFindTagSelections(prefix);
	
 	//Show this at full brightness
	var divObj = document.getElementById('ss_findTagNavBarDiv_'+prefix);
 	divObj = document.getElementById('available_'+prefix);
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
	
	//See if there is another search request to be done
	function runItLater(a, b, c, d) {
      return (function () {
        ss_findTagSearch(a, b, c, d)
      });
    }
	var runitRef = runItLater(prefix,ss_findTagSearchLastTextObjId[prefix],ss_findTagSearchLastElement[prefix], ss_findTagSearchLastfindTagType[prefix]);
	if (ss_findTagSearchWaiting[prefix] == 1) {
		setTimeout(runitRef, 100);
	}

	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_'+prefix);
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout(function () {ss_findTagSelectItem0(prefix);}, 100);
			return;
		}
	}
}
function ss_showFindTagSelections (prefix) {
	var divObj = document.getElementById('ss_findTagNavBarDiv_'+prefix);
	ss_moveDivToBody('ss_findTagNavBarDiv_'+prefix);
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findTag_searchText_bottom_"+prefix) + ss_findTagDivTopOffset[prefix]));
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findTag_searchText_bottom_"+prefix)));
	ss_showDivActivate('ss_findTagNavBarDiv_'+prefix);
}
function ss_findTagSelectItem0 (prefix) {
	var ulObj = document.getElementById('available_'+prefix);
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findTagSelectItem (prefix, liObjs[0]);
	}
}
//Routine called when item is clicked
function ss_findTagSelectItem (prefix, obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var id = ss_replaceSubStr(obj.id, 'ss_findTag_id_', "");
	if (ss_findTagClickRoutine[prefix] != "") {
		eval(ss_findTagClickRoutine[prefix] + "('"+id+"');");
		if (ss_findTagLeaveResultsVisible[prefix]) {
		  setTimeout("ss_showFindTagSelections('"+prefix+"');", 200)
		}
	} else {
		var url = ss_replaceSubStrAll(ss_findTagViewUrl[prefix], 'ss_tagPlaceHolder', id);
		self.location.href = url;
	}
}

function ss_saveFindTagData(prefix) {
	this.prefix = prefix;
		
	var me = this;
	
	this.invoke = function() {
		ss_debug('ss_saveFindTagData')
		var ulObj = document.getElementById('available_'+me.prefix);
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findTagSelectItem(me.prefix, liObjs[0]);
		}
		return false;
	}
}

function ss_findTagNextPage(prefix) {
	ss_findTag_pageNumberBefore[prefix] = ss_findTag_pageNumber[prefix];
	ss_findTag_pageNumber[prefix]++;
	ss_findTagSearch(prefix, ss_findTagSearchLastTextObjId[prefix], ss_findTagSearchLastElement[prefix], ss_findTagSearchLastfindTagType[prefix]);
}

function ss_findTagPrevPage(prefix) {
	ss_findTag_pageNumberBefore[prefix] = ss_findTag_pageNumber[prefix];
	ss_findTag_pageNumber[prefix]--;
	if (ss_findTag_pageNumber[prefix] < 0) ss_findTag_pageNumber[prefix] = 0;
	ss_findTagSearch(prefix, ss_findTagSearchLastTextObjId[prefix], ss_findTagSearchLastElement[prefix], ss_findTagSearchLastfindTagType[prefix]);
}

function ss_findTagClose(prefix) {
	document.getElementById('ss_findTag_searchText_'+prefix).focus();
}

function ss_findTagInitializeForm(formName, prefix) {
	if (formName != '') {
		var saveFindTagData = new ss_saveFindTagData(prefix);
		ss_createOnSubmitObj(prefix + 'onSubmit_single_tag', formName, saveFindTagData.invoke);
	}
}

function ss_findTagBlurTextArea(prefix) {
	if (!ss___findTagIsMouseOverList[prefix]) {
		setTimeout(function() { ss_hideDiv('ss_findTagNavBarDiv_' + prefix) } , 200);
	}
}

function ss_findTagMouseOverList(prefix) {
	ss___findTagIsMouseOverList[prefix] = true;
}

function ss_findTagMouseOutList(prefix) {
	ss___findTagIsMouseOverList[prefix] = false;
}

function ss_findTagSearchAccessible(prefix, searchText, elementName, findTagType, crFound) {
	//In accessibility mode, wait for the user to type cr
	if (!crFound && parseInt(ss_findTag_pageNumber[prefix]) == 0 && 
			parseInt(ss_findTag_pageNumberBefore[prefix]) == 0) return;
	
    var iframeDivObj = self.document.getElementById("ss_findTagIframeDiv");
    var iframeObj = self.document.getElementById("ss_findTagIframe");
    var iframeDivObjParent = self.parent.document.getElementById("ss_findTagIframeDiv");
    var iframeObjParent = self.parent.document.getElementById("ss_findTagIframe");
    var textObj = self.document.getElementById('ss_findTag_searchText_bottom_'+prefix);
    if (iframeDivObjParent == null && iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
	    iframeDivObjParent = iframeDivObj;
        iframeDivObj.setAttribute("id", "ss_findTagIframeDiv");
		iframeDivObj.className = "ss_popupMenu";
		iframeDivObj.style.zIndex = ssPopupZ;
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_findTagIframe");
        iframeObj.style.width = "400px"
        iframeObj.style.height = "300px"
		iframeDivObj.appendChild(iframeObj);
	    var closeDivObj = self.document.createElement("div");
	    closeDivObj.style.border = "2px solid gray";
	    closeDivObj.style.marginTop = "1px";
	    closeDivObj.style.padding = "6px";
	    iframeDivObj.appendChild(closeDivObj);
	    var aObj = self.document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDiv('ss_findTagIframeDiv');ss_findTagClose('"+prefix+"');");
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
	ss_showDiv("ss_findTagIframeDiv");
	var url = ss_AjaxBaseUrl;
	url = ss_replaceSubStrAll(url, "&amp;", "&");
	url += "&operation=find_user_search";
	url += "&searchText=" + searchText;
	url += "&maxEntries=" + "10";
	url += "&pageNumber=" + ss_findTag_pageNumber[prefix];
	url += "&findType=" + findTagType;
	url += "&listDivId=" + "available_" + prefix;
	url += "&namespace=" + prefix;
    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
		self.location.href = url;
	} else {
		iframeObj.src = url;
	}
}
