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
}

function ss_confFindTagSearchVariables(prefix, clickRoutine, viewUrl, leaveResultsVisible, userSearchUrl) {
	if (!window.ss_findTag_searchText) {
		ss_declareFindTagSearchVariables();
	}
	ss_findTag_searchText[prefix] = "";
	ss_findTag_pageNumber[prefix] = 0;
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
// Renata: I have removed "text == '' ||" from the next if line, 
// I dont understand why it shoud be, "next" page dont work with it if you look for all tags
	if (text != ss_findTagSearchLastText[prefix]) ss_findTag_pageNumber[prefix] = 0;
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
 	var ajaxRequest = new ss_AjaxRequest(ss_findTagSearchUrl[prefix]); //Create AjaxRequest object
	var searchText = text;
	if (searchText.length > 0 && searchText.charAt(searchText.length-1) != " ") {
		if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	}
	ajaxRequest.addKeyValue("searchText", searchText);
	ajaxRequest.addKeyValue("maxEntries", "10");
	ajaxRequest.addKeyValue("pageNumber", ss_findTag_pageNumber[prefix]);
	ajaxRequest.addKeyValue("findType", findTagType);
	ajaxRequest.addKeyValue("listDivId", "available_"+prefix);
	ajaxRequest.addKeyValue("namespace", prefix);
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preFindTagRequest);
	// TODO implement add param ajaxRequest.setPostRequestParam like ajaxRequest.setPostRequest  
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
	ss_findTagSearchInProgress[obj.getData("prefix")] = 0;

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
	ss_findTag_pageNumber[prefix]++;
	ss_findTagSearch(prefix, ss_findTagSearchLastTextObjId[prefix], ss_findTagSearchLastElement[prefix], ss_findTagSearchLastfindTagType[prefix]);
}

function ss_findTagPrevPage(prefix) {
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
		ss_createOnSubmitObj(prefix + 'onSubmit', formName, saveFindTagData.invoke);
	}
}

