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

if (typeof ssFind === "undefined" || !ssFind) {
    var ssFind = {};
	ssFind.objectsByFormName = {};
	
	ssFind.getObjectByFormName = function(formName, elementName) {
		if (ssFind.objectsByFormName && ssFind.objectsByFormName[formName] && 
				ssFind.objectsByFormName[formName][elementName]) {
			return ssFind.objectsByFormName[formName][elementName];
		}
		return null;
	}
	
	ssFind.addObjectByFormName = function(formName, elementName, obj) {
		if (!("objectsByFormName" in ssFind)) {
			ssFind.objectsByFormName = {};
		}
		
		if (!(formName in ssFind.objectsByFormName)) {
			ssFind.objectsByFormName[formName] = {};
		}
		
		ssFind.objectsByFormName[formName][elementName] = obj;
	}
}



/*
 	Properties:
 		prefix - used to find HTML elements by idS
 		clickRoutineObj - object to call clickRoutine on
 		clickRoutine - function to call on all or remove element to/from list 
 						if clickRoutineObj is defined then clickRoutine is called as object method
		formName - form name where find is places
		elementName - find data name					
 */
ssFind.configMultiple = function(params) {
	var thisName = ("thisName" in params) ? params.thisName : null;
	var prefix = ("prefix" in params) ? params.prefix : null;
	var clickRoutineObj = ("clickRoutineObj" in params) ? params.clickRoutineObj : null;	
	var clickRoutine = ("clickRoutine" in params) ? params.clickRoutine : null;
	
	var formName = ("formName" in params) ? params.formName : null;
	var elementName = ("elementName" in params) ? params.elementName : null;	
	
	var obj = new ssFind.Find(thisName, prefix, clickRoutineObj, clickRoutine);
	
	ssFind.addObjectByFormName(formName, elementName, obj);
	
	return obj;
}

/*
 	Properties:
 		findMultipleObj - Find object name (created by find multiple)
 		prefix - used to find HTML elements by idS
 		clickRoutineObj - object to call clickRoutine on
 		clickRoutine - function to call on all or remove element to/from list 
 						if clickRoutineObj is defined then clickRoutine is called as object method
 */
ssFind.configSingle = function(params) {
	var findObj = null;
		
	// find already defined Find object (created by find multiple)
	var findMultipleObjName = ("findMultipleObj" in params) ? params.findMultipleObj : null;
	if (findMultipleObjName != null && findMultipleObjName != "" && window[findMultipleObjName] != null) {
		findObj = window[findMultipleObjName];
	}
	
	// no multiple, create new Find object
	if (findObj == null) {
		var thisName = ("thisName" in params) ? params.thisName : null;
		findObj = new ssFind.Find(thisName);
	}
	
	// initialize single find
	var prefix = ("prefix" in params) ? params.prefix : null;
	var clickRoutineObj = ("clickRoutineObj" in params) ? params.clickRoutineObj : null;	
	var clickRoutine = ("clickRoutine" in params) ? params.clickRoutine : null;
	var viewUrl = ("viewUrl" in params) ? params.viewUrl : null;
	var viewAccesibleUrl = ("viewAccesibleUrl" in params) ? params.viewAccesibleUrl : null;
	var searchUrl = ("searchUrl" in params) ? params.searchUrl : null;
	var leaveResultsVisible = ("leaveResultsVisible" in params) ? params.leaveResultsVisible : null;
	var listType = ("listType" in params) ? params.listType : null;
	var renderNamespace = ("renderNamespace" in params) ? params.renderNamespace : null;
	var binderId = ("binderId" in params) ? params.binderId : null;
	var subFolders = ("subFolders" in params) ? params.subFolders : null;
	var foldersOnly = ("foldersOnly" in params) ? params.foldersOnly : null;
	
	findObj.single(prefix, clickRoutineObj, clickRoutine, viewUrl, viewAccesibleUrl, searchUrl,
					leaveResultsVisible, listType, renderNamespace, binderId, subFolders, foldersOnly);
	
	return findObj;
}


ssFind.Find = function(thisName, multiplePrefix, multipleClickRoutineObj, multipleClickRoutine) {
	var that = this;
	
	var thisObjectName = thisName;
	
	this._multiplePrefix = multiplePrefix;
	this._multipleClickRoutineObj = multipleClickRoutineObj;
	this._multipleClickRoutine = multipleClickRoutine;

	this._singlePrefix;
	this._singleClickRoutineObj;
	this._singleClickRoutine;
	this._singleViewUrl;
	this._singleViewAccesibleUrl;
	this._singleSearchUrl;
	this._singleLeaveResultsVisible;
	this._singleListType;
	this._singleRenderNamespace;
	this._singleBinderId;
	this._singleSubFolders;
	this._singleFoldersOnly;
	
	var lastText = "";
	var pageNumber = 0;
	var pageNumberBefore = 0;
	var searchInProgress = false;
	var searchLastTextObjId;
	var searchLastElement;
	var searchWaiting = false;
	var searchStartMs;
	var searchText;
	var isMouseOverList = false;
	var divTopOffset = 2;
	
	var onAddCallbacks = [];
	var onDeleteCallbacks = [];

	this.single = function(singlePrefix, singleClickRoutineObj, singleClickRoutine, singleViewUrl, singleViewAccesibleUrl, singleSearchUrl, singleLeaveResultsVisible, singleListType, singleRenderNamespace, singleBinderId, singleSubFolders, singleFoldersOnly) {
		that._singlePrefix = singlePrefix;
		if (!multiplePrefix && that._singlePrefix) {
			thisObjectName = singlePrefix + "ssFind_Find";
			window[thisObjectName] = that;
		}
		that._singleClickRoutineObj = singleClickRoutineObj;
		that._singleClickRoutine = singleClickRoutine;
		that._singleViewUrl = singleViewUrl;
		that._singleViewAccesibleUrl = singleViewAccesibleUrl;
		that._singleSearchUrl = singleSearchUrl;
		that._singleLeaveResultsVisible = singleLeaveResultsVisible;
		that._singleListType = singleListType;
		that._singleRenderNamespace = singleRenderNamespace;
		that._singleBinderId = singleBinderId;
		that._singleSubFolders = singleSubFolders;
		that._singleFoldersOnly = singleFoldersOnly;
	}
	
	this.search = function (textObjId, elementName) {
		var textObj = document.getElementById(textObjId);
		var text = textObj.value;
		if (text.trim() != lastText.trim()) {
			pageNumber = 0;
			pageNumberBefore = 0;
		}
		ss_setupStatusMessageDiv();
		ss_moveDivToBody('ss_findUserNavBarDiv' + that._singlePrefix);
		//Are we already doing a search?
		if (searchInProgress) {
			//Yes, hold this request until the current one finishes
			lastText = text;
			searchLastTextObjId = textObjId;
			searchLastElement = elementName;
			searchWaiting = true;
			var d = new Date();
			var curr_msec = d.getTime();
			if (searchStartMs == 0 || curr_msec < parseInt(searchStartMs + 1000)) {
				ss_debug('  hold search request...')
				if (searchStartMs == 0) {
					searchStartMs = curr_msec;
				}
				return;
			}
			//The user waited for over a second, let this request go through
			searchStartMs = 0;
			ss_debug('   Stopped waiting')
		}
		searchInProgress = true;
		searchWaiting = false;
		searchLastTextObjId = textObjId;
		searchLastElement = elementName;
		lastText = text;
	 	//Save the text in case the user changes the search type
	 	searchText = text;
	 	
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
			var ulObj = document.getElementById('available_'+that._singlePrefix)
			var liObjs = ulObj.getElementsByTagName('li');
			if (liObjs.length == 1) {
				that._selectItem(liObjs[0]);
				return;
			}
	 	}
	 	//Fade the previous selections
	 	var savedColor = "#000000";
	 	var divObj = document.getElementById('available_' + that._singlePrefix);
	 	if (divObj != null && divObj.style && divObj.style.color) {
	 		savedColor = divObj.style.color;
	 	}
	 	if (divObj != null) divObj.style.color = "#cccccc";
	
	 	ss_debug("//"+text+"//")
		var searchText = text;
		if (searchText.length > 0 && searchText.charAt(searchText.length-1) != " ") {
			if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
		}
		if (ss_userDisplayStyle == 'accessible') {
			that._searchAccessible(searchText, elementName, crFound);
			searchInProgress = false;
			return;
		}
		var ajaxRequest = new ss_AjaxRequest(that._singleSearchUrl); //Create AjaxRequest object
		ajaxRequest.addKeyValue("searchText", searchText);
		ajaxRequest.addKeyValue("maxEntries", "10");
		ajaxRequest.addKeyValue("pageNumber", pageNumber);
		ajaxRequest.addKeyValue("findObjectName", thisObjectName);
		ajaxRequest.addKeyValue("findType", that._singleListType);
		if (that._singleBinderId != null) { 
			ajaxRequest.addKeyValue("binderId", that._singleBinderId);
		}
		if (that._singleSubFolders != null) { 
			ajaxRequest.addKeyValue("searchSubFolders", that._singleSubFolders);
		}
		ajaxRequest.addKeyValue("listDivId", "available_" + that._singlePrefix);
		ajaxRequest.addKeyValue("namespace", that._singlePrefix);
		ajaxRequest.setPostRequest(that.postFindRequest);
		ajaxRequest.setData("prefix", that._singlePrefix);
		ajaxRequest.setData("elementName", elementName);
		ajaxRequest.setData("savedColor", savedColor);
		ajaxRequest.setData("crFound", crFound);
		ajaxRequest.setUseGET();
		ajaxRequest.sendRequest();  //Send the request
	}
	
	this.postFindRequest = function (obj) {
		//See if there was an error
		if (self.document.getElementById("ss_status_message").innerHTML == "error") {
			alert(ss_not_logged_in);
		}
		searchInProgress = false;
	
		var ulObj = document.getElementById('available_'+that._singlePrefix);
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 0) {
			ss_hideDiv('ss_findUserNavBarDiv_' + that._singlePrefix);
			return;
		}
	
		that.showSelections();
		
	 	//Show this at full brightness
		var divObj = document.getElementById('ss_findUserNavBarDiv_' + that._singlePrefix);
	 	divObj = document.getElementById('available_' + that._singlePrefix);
	 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
		
		//See if there is another search request to be done
		if (searchWaiting) {
			(function(searchLastTextObjId, searchLastElement) {
				setTimeout(function () {
					that.search(searchLastTextObjId, searchLastElement)
				}, 100);
			})(searchLastTextObjId, searchLastElement);
		}
	
		//See if the user typed a return. If so, see if there is a unique value to go to
		if (obj.getData('crFound') == 1) {
			var ulObj = document.getElementById('available_' + that._singlePrefix);
			var liObjs = ulObj.getElementsByTagName('li');
			if (liObjs.length == 1) {
				setTimeout(function (){that.selectItem0();}, 100);
				return;
			}
		}
	}
	
	this.showSelections = function() {
		var divObj = document.getElementById('ss_findUserNavBarDiv_' + that._singlePrefix);
		ss_moveDivToBody('ss_findUserNavBarDiv_' + that._singlePrefix);
		ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findUser_searchText_bottom_" + that._singlePrefix) + divTopOffset))
		ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findUser_searchText_bottom_" + that._singlePrefix)))
		ss_showDivActivate('ss_findUserNavBarDiv_' + that._singlePrefix);
	}
	
	this.selectItem0 = function () {
		var ulObj = document.getElementById('available_' + that._singlePrefix);
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			that._selectItem(liObjs[0])
		}
	}
	
	//Routine called when item is clicked
	this._selectItem = function(obj, entityType) {
		if (!obj || !obj.id ||obj.id == undefined) return false;
		var id = ss_replaceSubStr(obj.id, 'ss_findUser_id_', "");
		if (that._singleClickRoutine != "") {
			that._callRoutineSingle(id, obj, entityType);
		} else {
			if (that._singleListType == "tags" || that._singleListType == "communityTags" || that._singleListType == "personalTags") {
				var url = ss_replaceSubStrAll(that._singleViewUrl, 'ss_tagPlaceHolder', id);
				self.location.href = url;
			} else if (that._singleListType == "entries") {
				var url = ss_replaceSubStr(that._singleViewUrl, 'ss_entryIdPlaceholder', id);
				self.location.href = url;
			} else if (that._singleListType == "places") {
			    var url = that._singleViewUrl; 
				url = ss_replaceSubStr(url, 'ss_binderIdPlaceholder', id);
				url = ss_replaceSubStr(url, 'ss_entityTypePlaceholder', entityType);
				if (ss_gotoPermalink(id, id, entityType, that._singleRenderNamespace, 'yes')) {
					self.location.href = url;
				}
				return false;
			} else { // user
				var url = ss_replaceSubStr(that._singleViewUrl, 'ss_entryIdPlaceholder', id);
				self.location.href = url;
			}
		}
	}

	//Routine called when item is clicked in accessible mode
	this.selectItemAccessible = function(obj, entityType) {
		if (that._singleListType != "entries") {
			return that._selectItem(obj, entityType);
		} else {
			if (!obj || !obj.id ||obj.id == undefined) return false;
			var id = ss_replaceSubStr(obj.id, 'ss_findUser_id_', "");
			var url = that._singleViewAccesibleUrl; 
			url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
			self.location.href = url;
		}
	}
	
	this.nextPage = function() {
		pageNumberBefore = pageNumber;
		pageNumber++;
		that.search(searchLastTextObjId, searchLastElement);
	}	

	this.prevPage = function() {
		pageNumberBefore = pageNumber;
		pageNumber--;
		if (pageNumber < 0) pageNumber = 0;
		that.search(searchLastTextObjId, searchLastElement);
	}

	this.close = function() {
		pageNumber = 0;
		pageNumberBefore = 0;
		
		var textObj = document.getElementById('ss_findUser_searchText_' + that._singlePrefix);
		if (textObj == null) {
			textObj = self.parent.document.getElementById('ss_findUser_searchText_' + that._singlePrefix);
		}
		if (textObj != null) textObj.focus();
	}

	this.blurTextArea = function() {
		if (!isMouseOverList) {
			setTimeout(function() { ss_hideDiv('ss_findUserNavBarDiv_' + that._singlePrefix) } , 200);
		}
	}

	this.mouseOverList = function() {
		isMouseOverList = true;
	}

	this.mouseOutList = function() {
		isMouseOverList = false;
	}

	this._searchAccessible = function(searchText, elementName, crFound) {
		//In accessibility mode, wait for the user to type cr
		if (!crFound && parseInt(pageNumber) == 0 && 
				parseInt(pageNumberBefore) == 0) return;
		
	    var iframeDivObj = self.document.getElementById("ss_findIframeDiv");
	    var iframeObj = self.document.getElementById("ss_findIframe");
	    var iframeDivObjParent = self.parent.document.getElementById("ss_findIframeDiv");
	    var iframeObjParent = self.parent.document.getElementById("ss_findIframe");
	    var textObj = self.document.getElementById('ss_findUser_searchText_bottom_'+that._singlePrefix);
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
			aObj.setAttribute("href", "javascript: ;");
			dojo.event.connect(aObj, "onclick", function(evt) {
				ss_hideDiv('ss_findIframeDiv');
				that.close();
		    });
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
		var urlParams = {operation:"find_user_search", searchText:searchText,
						maxEntries:"10", pageNumber: pageNumber,
						findType: that._singleListType, listDivId:"available_" + that._singlePrefix,
						namespace: that._singlePrefix};
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_find");
	    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
			self.location.href = url;
		} else {
			iframeObj.src = url;
		}
	}

	
	this.addValueByElement = function(id, obj) {
		if (that._itemAlreadyAddedMultiple(id)) {
			return;
		}
		var spanObj = obj.getElementsByTagName("span").item(0);
		var ulObj = document.getElementById('added_' + that._multiplePrefix);
		var newLiObj = document.createElement("li");
		newLiObj.setAttribute("id", id);
		newLiObj.className = "ss_nowrap";
		newLiObj.innerHTML = spanObj.innerHTML;
		var newAnchorObj = document.createElement("a");
		newAnchorObj.setAttribute("href", "javascript: ;");
		dojo.event.connect(newAnchorObj, "onclick", function(evt) {
			that.removeValueByElement(evt.target.parentNode, id, spanObj.innerHTML);
	    });	
		var newImgObj = document.createElement("img");
		newImgObj.setAttribute("src", ss_imagesPath + "pics/sym_s_delete.gif");
		newImgObj.setAttribute("border", "0");
		newImgObj.style.paddingLeft = "10px";
		newAnchorObj.appendChild(newImgObj);
		newLiObj.appendChild(newAnchorObj);
		ulObj.appendChild(newLiObj);
		that.addValue(id);
		that._callRoutineMultiple(id, spanObj.innerHTML, onAddCallbacks);
		that._highlight(newLiObj);
	}
	
	this._callRoutineMultiple = function(id, txt, callbacks) {
		var obj = window;
		if (that._multipleClickRoutineObj && window[that._multipleClickRoutineObj]) {
			obj = window[that._multipleClickRoutineObj];
		}

		if (that._multipleClickRoutine && obj && obj[that._multipleClickRoutine]) {
			obj[that._multipleClickRoutine]();
		}
		for (var i = 0; i < callbacks.length; i++) {
			callbacks[i]({id: id, name: txt});
		}
	}

	this._callRoutineSingle = function(id, obj, entityType) {
		var callbackObj = window;
		if (that._singleClickRoutineObj && window[that._singleClickRoutineObj]) {
			callbackObj = window[that._singleClickRoutineObj];
		}
		if (that._singleClickRoutine && callbackObj && callbackObj[that._singleClickRoutine]) {
			var objMethod = callbackObj[that._singleClickRoutine];
			if (that._singleListType == "places") {
				objMethod.apply(this, [id, entityType, obj]);
			} else {
				objMethod.apply(this, [id, obj]);
			}
		}
		if (that._singleLeaveResultsVisible) {
			setTimeout(function() {that.showSelections()}, 200);
		}
	}

	// Check if user allready added to list, if yes highlight it
	this._itemAlreadyAddedMultiple = function (id) {
		var ulObj = document.getElementById('added_' + that._multiplePrefix);
		var lisObj = ulObj.childNodes;
		for (var i = 0; i < lisObj.length; i++) {
			if (lisObj[i].id == id) {
				that._highlight(lisObj[i]);
				return true;
			}
		}
		return false;
	}
	
	this.getList = function() {
		var r = {};
		var ulObj = document.getElementById('added_' + that._multiplePrefix);
		var lisObj = ulObj.childNodes;
		for (var i = 0; i < lisObj.length; i++) {
			if (lisObj[i].tagName == "LI") {
				r[lisObj[i].id] = lisObj[i].firstChild.nodeValue;
			}
		}
		return r;
	}
	
	this.addValue = function (value) {
		var hiddenValueInputObj = document.getElementById(that._multiplePrefix + "_ss_find_multiple_input");
	
		var newValue = "";
		if (hiddenValueInputObj.value) {
			newValue = hiddenValueInputObj.value;
		}
	
		newValue += " " + value + " ";
	
		hiddenValueInputObj.value = newValue;
	}
	
	this.removeValueByElement = function (obj, value, txt) {
		var liObj = obj.parentNode;
		liObj.parentNode.removeChild(liObj);
		
		that._callRoutineMultiple(value, txt, onDeleteCallbacks);
		
		var hiddenValueInputObj = document.getElementById(that._multiplePrefix + "_ss_find_multiple_input");
		var inputValue = hiddenValueInputObj.value;
		inputValue = inputValue.replace(" " + value + " ", " ");
		hiddenValueInputObj.value = inputValue;
	}
	
	this._highlight = function(obj) {
		dojo.lfx.html.highlight(obj, "#FFFF33", 500).play();
	}
	
	this.addListener = function(on, callback) {
		if (on == "onAdd" && typeof callback != "undefined") {
			onAddCallbacks.push(callback);
		}
		if (on == "onDelete" && typeof callback != "undefined") {
			onDeleteCallbacks.push(callback);
		}
	}

}
