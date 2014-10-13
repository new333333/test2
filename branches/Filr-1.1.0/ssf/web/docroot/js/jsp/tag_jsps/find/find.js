/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

if (typeof ssFind === "undefined" || !ssFind) {
    var ssFind = {};
	
	
	ssFind.createMultiple = function(params) {
		var prefix = ("prefix" in params) ? params.prefix : null;
		var elementName = ("elementName" in params) ? params.elementName : null;
		
		var container = ("container" in params) ? params.container : false;
		var label = ("label" in params) ? params.label : "";
		var inputId = ssFind.createMultipleFromTemplate(container, prefix, elementName, label);
		params.inputId = inputId;
		params.findMultipleObj = ssFind.configMultiple(params);
		params.clickRoutine = "addValueByElement";
		params.clickRoutineObj = params.findMultipleObj;
		ssFind.configSingle(params);
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
	var prefix = ("prefix" in params) ? params.prefix : null;
	var clickRoutineObj = ("clickRoutineObj" in params) ? params.clickRoutineObj : null;	
	var clickRoutine = ("clickRoutine" in params) ? params.clickRoutine : null;
	var displayValue = ("displayValue" in params) ? params.displayValue : null;
	var displayValueOnly = ("displayValueOnly" in params) ? params.displayValueOnly : null;
	var sendingEmail = ("sendingEmail" in params) ? (params.sendingEmail != "false" ? true : false) : false;
	
	var formName = ("formName" in params) ? params.formName : null;
	var elementName = ("elementName" in params) ? params.elementName : null;
		
	var obj = new ssFind.Find(prefix, clickRoutineObj, clickRoutine, displayValue, displayValueOnly, sendingEmail);
	
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
	if (findMultipleObjName != null && typeof findMultipleObjName == "string" && findMultipleObjName != "" && window[findMultipleObjName] != null) {
		findObj = window[findMultipleObjName];
	} else if (findMultipleObjName != null && typeof findMultipleObjName == "object") {
		findObj = findMultipleObjName;
	}
	
	// no multiple, create new Find object
	if (findObj == null) {
		findObj = new ssFind.Find();
	}
	
	// initialize single find
	var inputId = ("inputId" in params) ? params.inputId : null;
	var prefix = ("prefix" in params) ? params.prefix : null;
	var clickRoutineObj = ("clickRoutineObj" in params) ? params.clickRoutineObj : null;	
	var clickRoutine = ("clickRoutine" in params) ? params.clickRoutine : null;
	var viewUrl = ("viewUrl" in params) ? params.viewUrl : null;
	var viewAccesibleUrl = ("viewAccesibleUrl" in params) ? params.viewAccesibleUrl : null;
	var searchUrl = ("searchUrl" in params) ? params.searchUrl : null;
	var appendToSearchUrlRoutine = ("appendToSearchUrlRoutine" in params) ? params.appendToSearchUrlRoutine : null;
	var leaveResultsVisible = ("leaveResultsVisible" in params) ? params.leaveResultsVisible : null;
	var sendingEmail = ("sendingEmail" in params) ? (params.sendingEmail != "false" ? true : false) : false;
	var listType = ("listType" in params) ? params.listType : null;
	var renderNamespace = ("renderNamespace" in params) ? params.renderNamespace : null;
	var binderId = ("binderId" in params) ? params.binderId : null;
	var subFolders = ("subFolders" in params) ? params.subFolders : null;
	var foldersOnly = ("foldersOnly" in params) ? params.foldersOnly : null;
	var showFolderTitles = ("showFolderTitles" in params) ? params.showFolderTitles : false;
	var showUserTitleOnly = ("showUserTitleOnly" in params) ? params.showUserTitleOnly : false;
	var displayArrow = ("displayArrow" in params) ? (params.displayArrow != "false" ? true : false) : false;
	var displayValue = ("displayValue" in params) ? (params.displayValue != "false" ? true : false) : false;
	var displayValueOnly = ("displayValueOnly" in params) ? (params.displayValueOnly != "false" ? true : false) : false;
	var searchOnInitialClick = ("searchOnInitialClick" in params) ? (params.searchOnInitialClick != "false" ? true : false) : false;
    var clearSubordinates = ("clearSubordinates" in params) ? params.clearSubordinates : null;

	var addCurrentUserToResult = ("addCurrentUserToResult" in params) ? (params.addCurrentUserToResult != "false" ? true : false) : false;
		
	findObj.single(inputId, prefix, clickRoutineObj, clickRoutine, viewUrl, viewAccesibleUrl, searchUrl, appendToSearchUrlRoutine, 
					leaveResultsVisible, sendingEmail, listType, renderNamespace, binderId, subFolders, foldersOnly, 
					showFolderTitles, showUserTitleOnly, displayArrow, displayValue, displayValueOnly, 
					addCurrentUserToResult, searchOnInitialClick, clearSubordinates);
	
	return findObj;
}


ssFind.Find = function(multiplePrefix, multipleClickRoutineObj, multipleClickRoutine, displayValue, displayValueOnly, sendingEmail) {
	var that = this;
	
	this.inputId = false;
	this._inputObj = false;
	
	this._multiplePrefix = multiplePrefix;
	this._multipleClickRoutineObj = multipleClickRoutineObj;
	this._multipleClickRoutine = multipleClickRoutine;
	
	this._displayValue = displayValue;
	this._displayValueOnly = displayValueOnly;
	this._sendingEmail = sendingEmail;

	this._singlePrefix;
	this._singleClickRoutineObj;
	this._singleClickRoutine;
	this._singleViewUrl;
	this._singleViewAccesibleUrl;
	this._singleSearchUrl;
	this._appendToSearchUrlRoutine;
	this._singleLeaveResultsVisible;
	this._singleSendingEmail;
	this._singleListType;
	this._singleRenderNamespace;
	this._singleBinderId;
	this._singleSubFolders;
	this._singleFoldersOnly;
	this._showFolderTitles;
	this._showUserTitleOnly;
	this._displayArrow = false;
	this._addCurrentUserToResult = false;
	
	this._listContainer = false;
	this._listContainerInnerDiv = false;
	this._searchResultsList = false;
	this._nextPrevTableObj = false;
	this._arrowDown = false;
	this._hiddenInputSelectedId = false;
	this._hiddenInputSelectedType = false;
	
	var lastText = "";
	var pageNumber = 0;
	var pageNumberBefore = 0;
	var searchInProgress = false;
	var textWaitingForSearch = false;
	var searchWaiting = false;
	var searchStartMs;
	var searchText;
	var isMouseOverList = false;
	var divTopOffset = 2;
	var lastHrefId = "";
	var pageRequested = 0;
	
	var onAddCallbacks = [];
	var onDeleteCallbacks = [];
	
	this.init = function() {
		if (that.inputId) {
			that._inputObj = document.getElementById(that.inputId);
		}
		
		if (that._inputObj) {
			dojo.connect(that._inputObj, "onkeyup", function() {
				that.search();
			});

			if (that._searchOnInitialClick) {
				dojo.connect(that._inputObj, "onclick", function() {
					that.search();
				});
			}
			
			dojo.connect(that._inputObj, "onblur", function() {
				that.blurTextArea();
			});
		}
		
		// Destroy list container if it already exists
		var listContainerId = "ss_autoCompleteComboBoxListContainer_" + that._singlePrefix;
		
		that._listContainer = dojo.byId(listContainerId);
		if (that._listContainer) {
			ss_debug('that._listContainer removed')
			that._listContainer.parentNode.removeChild(that._listContainer);
		}
		
		that._listContainer = document.createElement("div");
		that._listContainer.id = listContainerId;
		that._listContainer.className = "ss_typeToFindResults";
		that._listContainer.style.display = "none";
		dojo.connect(that._listContainer, "onmouseover", function() {
			that.mouseOverList();
		});	
		dojo.connect(that._listContainer, "onmouseout", function() {
			that.mouseOutList();
		});
			
		that._listContainerInnerDiv = document.createElement("div");
		that._listContainerInnerDiv.id = "available_" + that._singlePrefix;
		that._listContainerInnerDiv.style.padding = "2px";
		that._listContainerInnerDiv.style.margin = "2px";
		
		that._searchResultsList = document.createElement("ul");

		that._listContainerInnerDiv.appendChild(that._searchResultsList);
		that._listContainer.appendChild(that._listContainerInnerDiv);
		document.getElementsByTagName( "body" ).item(0).appendChild(that._listContainer);
			
		if (that._inputObj && that._inputObj.id + "_selected" && that._inputObj.name + "_selected") {
			that._hiddenInputSelectedId = document.createElement("input");
			that._hiddenInputSelectedId.type = "hidden";
			that._hiddenInputSelectedId.id = that._inputObj.id + "_selected";
			that._hiddenInputSelectedId.name = that._inputObj.name + "_selected";
			that._inputObj.parentNode.insertBefore(that._hiddenInputSelectedId, that._inputObj.nextSibling); 
			
			that._hiddenInputSelectedType = document.createElement("input");
			that._hiddenInputSelectedType.type = "hidden";
			that._hiddenInputSelectedType.id = that._inputObj.id + "_type";
			that._hiddenInputSelectedType.name = that._inputObj.name + "_type";
			that._inputObj.parentNode.insertBefore(that._hiddenInputSelectedType, that._inputObj.nextSibling); 
		}
		
		
		
		if (that._displayArrow) {
/*
			that._arrowDown = document.createElement("a");
			// TODO style
			that._arrowDown.style.border = "0.01em solid #999999";
//			that._arrowDown.style.cssFloat = "right";
			that._arrowDown.style.height = "13px";
			that._arrowDown.style.width = "13px";
//			that._inputObj.style.cssFloat = "right";
			
			var imgObj = document.createElement("img");
			imgObj.style.height = "13px";
			imgObj.style.width = "13px";			
			imgObj.style.border = "0";
			imgObj.src = ss_imagesPath + "icons/arrow_down_combobox.gif";
			
			that._arrowDown.appendChild(imgObj);
			
			that._inputObj.parentNode.insertBefore(that._arrowDown, that._inputObj.nextSibling); 
			*/
		}	
	}

	this.single = function(inputId, singlePrefix, singleClickRoutineObj, singleClickRoutine, 
						   singleViewUrl, singleViewAccesibleUrl, singleSearchUrl, appendToSearchUrlRoutine,
						   singleLeaveResultsVisible, singleSendingEmail, singleListType, singleRenderNamespace,
						   singleBinderId, singleSubFolders, singleFoldersOnly,
						   showFolderTitles, showUserTitleOnly, displayArrow, displayValue, displayValueOnly,
						   addCurrentUserToResult, searchOnInitialClick, clearSubordinates) {
		that.inputId = inputId;
		if (that.inputId) {
			that._inputObj = document.getElementById(that.inputId);
		}
		that._singlePrefix = singlePrefix;
		that._singleClickRoutineObj = singleClickRoutineObj;
		that._singleClickRoutine = singleClickRoutine;
		that._singleViewUrl = singleViewUrl;
		that._singleViewAccesibleUrl = singleViewAccesibleUrl;
		that._singleSearchUrl = singleSearchUrl;
		that._appendToSearchUrlRoutine = appendToSearchUrlRoutine;
		that._singleLeaveResultsVisible = singleLeaveResultsVisible;
		that._singleSendingEmail = singleSendingEmail;
		that._singleListType = singleListType;
		that._singleRenderNamespace = singleRenderNamespace;
		that._singleBinderId = singleBinderId;
		that._singleSubFolders = singleSubFolders;
		that._singleFoldersOnly = singleFoldersOnly;
		that._showFolderTitles = showFolderTitles;
		that._showUserTitleOnly = showUserTitleOnly;
		that._displayArrow = displayArrow;
		that._displayValue = displayValue;
		that._displayValueOnly = displayValueOnly;
		that._addCurrentUserToResult = addCurrentUserToResult;
		that._searchOnInitialClick = searchOnInitialClick;
		that._clearSubordinates = clearSubordinates;
		that.init();
	}
	
	this.setValue = function(id, txt, type) {
		that._inputObj.value = txt;
		that._hiddenInputSelectedId.value = id;
		that._hiddenInputSelectedType.value = type;
	}
	
	this.getSingleValue = function() {
		return that._inputObj.value;
	}

	this.getSingleId = function() {
		return that._hiddenInputSelectedId.value;
	}
	
	this.getSingleType = function() {
		return that._hiddenInputSelectedType.value;
	}
	
	this.search = function (searchText) {
		var text = that._inputObj.value;
		if (searchText) {
			text = searchText;
		}
		if (ss_trim(text) != ss_trim(lastText)) {
			pageNumber = 0;
			pageNumberBefore = 0;
		}
		ss_setupStatusMessageDiv();
		//Are we already doing a search?
		if (searchInProgress) {
			//Yes, hold this request until the current one finishes
			lastText = text;
			searchWaiting = true;
			textWaitingForSearch = text;
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
	 		that._inputObj.value = newText;
	 		text = that._inputObj.value;
			var liObjs = that._listContainer.getElementsByTagName('li');
			if (liObjs.length == 1) {
				eval("var type = type_"+liObjs[0].id);
				that.selectItem(liObjs[0], type);
				return;
			}
			if (lastHrefId != '') {
				var aObj = self.document.getElementById(lastHrefId);
				if (aObj != null) {
					try {aObj.focus();} catch(e){}
				} else {
					lastHrefId = "";
					pageRequested = 0;
				}
			}
	 	}
	 	//Fade the previous selections
	 	var savedColor = "#000000";
	 	if (that._listContainerInnerDiv != null && that._listContainerInnerDiv.style && that._listContainerInnerDiv.style.color) {
	 		savedColor = that._listContainerInnerDiv.style.color;
	 	}
	 	if (that._listContainerInnerDiv != null) that._listContainerInnerDiv.style.color = "#cccccc";
	
	 	ss_debug("//"+text+"//")
		var searchText = text;
		if (searchText.length > 0 && searchText.charAt(searchText.length-1) != " ") {
			if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
		}
		if (ss_getUserDisplayStyle() == 'accessible') {
			that._searchAccessible(searchText, crFound);
			searchInProgress = false;
			return;
		}
		var url = that._singleSearchUrl  + "&searchText=" + encodeURI(searchText)
										+ "&maxEntries=10" 
										+ "&pageNumber=" + pageNumber 
										+ "&findType=" + that._singleListType 
										+ "&listDivId=" + that._listContainerInnerDiv.id
										+ "&showFolderTitles=" + that._showFolderTitles
										+ "&showUserTitleOnly=" + that._showUserTitleOnly
										+ "&namespace=" + that._singlePrefix
										+ "&addCurrentUser=" + that._addCurrentUserToResult;
		if (that._singleBinderId != null) url += "&binderId=" + that._singleBinderId;
		if (that._singleSubFolders != null) url +="&searchSubFolders=" + that._singleSubFolders;
		if (that._singleFoldersOnly != null) url += "&foldersOnly=" + that._singleFoldersOnly;
		
		if (that._appendToSearchUrlRoutine && typeof that._appendToSearchUrlRoutine !== "undefined")
			url = that._appendToSearchUrlRoutine(url);
			
		dojo.xhrGet({
	    	url: url,
	    	error: function (err) {
				alert(ss_not_logged_in);
			},
			load: function (data) {
				searchInProgress = false;
				
				if (!data || !data.items || data.items.length == 0) {
					that._clearSearchResultsList();
					ss_debug('search load nothing');
					that._listContainer.style.display = "none";
					pageRequested = 0;
					lastHrefId = "";
					return;
				}
				
				that._clearSearchResultsList();
				ss_debug('search clear results');
				
				lastHrefId = "";
				if (data.items.length > 0) lastHrefId = "id_ss_find_" + data.items[0].id;
				for (var i = 0; i < data.items.length; i++) {
					that._addListElement(data.items[i]);
				}
				
				that._addNextPrevToSearchList(data);
			
				that.showResultList();

			 	//Show this at full brightness
			 	that._listContainerInnerDiv.style.color = savedColor;
				
				//See if there is another search request to be done
				if (searchWaiting) {
					(function(textWaitingForSearch) {
						setTimeout(function () {
							that.search(textWaitingForSearch)
						}, 100);
					})(textWaitingForSearch);
				}
			
				//See if the user typed a return. If so, see if there is a unique value to go to
				if (crFound == 1) {
					var liObjs = that._listContainerInnerDiv.getElementsByTagName('li');
					if (liObjs.length == 1) {
						setTimeout(function (){that.selectItem0();}, 100);
						return;
					}
					if (lastHrefId != '') {
						var aObj = self.document.getElementById(lastHrefId);
						if (aObj != null) {
							try {aObj.focus();} catch(e) {}
						} else {
							lastHrefId = "";
							pageRequested = 0;
						}
					}
				}
				if (lastHrefId != '' && pageRequested) {
					var aObj = self.document.getElementById(lastHrefId);
					if (aObj != null) {
						try {aObj.focus();} catch(e) {}
					} else {
						lastHrefId = "";
						pageRequested = 0;
					}
				}
			},
			handleAs: "json-comment-filtered",
			preventCache: true
		});
	}
	
	this._addListElement = function (item) {
		var liObj = document.createElement("li");
		liObj.id = "ss_find_id_" + (item.id || item.name);
		
		var hrefObj = document.createElement("a");
		hrefObj.id = "id_ss_find_" + item.id;
		hrefObj.href = "javascript: //";
		dojo.connect(hrefObj, "onclick", function(evt) {
			// in setIdAndValue?? that._hiddenInputSelectedId.value = item.id || item.name;
			// if (that._singleListType == "personalTags" || that._singleListType == "communityTags") {
			if (that._displayValue || that._displayValueOnly) {
				that.setValue(item.id || item.name, item.name, item.type);
			}
			if (!that._displayValueOnly) {
				that.selectItem(liObj, item.type);
			}
			return false;
	    });
	    
	    if (typeof item.title != "undefined") hrefObj.title = item.title;
		
		var spanObj = document.createElement("span");
		spanObj.innerHTML = item.name;
		
		hrefObj.appendChild(spanObj);
		liObj.appendChild(hrefObj);
		that._searchResultsList.appendChild(liObj);
	}
	
	this._clearSearchResultsList = function() {
		if (that._searchResultsList && that._searchResultsList.hasChildNodes()) {
		    while (that._searchResultsList.childNodes.length >= 1 ) {
		        that._searchResultsList.removeChild(that._searchResultsList.firstChild);       
		    }
		}
		
		if (that._listContainerInnerDiv && that._nextPrevTableObj) {
			try {
				that._listContainerInnerDiv.removeChild(that._nextPrevTableObj);
			} catch(err){}
		}

		// Since we are repopulating the results list, get rid of the selection.
		that._hiddenInputSelectedId.value = "";

		// If there are any subordinate structures dependent on the results
		// we can opt to clear them out here.
		if (that._clearSubordinates) {
			that._clearSubordinates();
		}

	}

	this._addNextPrevToSearchList = function(data) {
		var hasPrev = data.pageNumber;
		var hasNext = (data.count + data.pageNumber * data.pageSize < data.totalHits);
		if (hasPrev || hasNext) {
			that._nextPrevTableObj = document.createElement("table");
			that._nextPrevTableObj.className = "ss_typeToFindNav";
			that._nextPrevTableObj.cellpadding = "0";
			that._nextPrevTableObj.cellspacing = "0";
			
			var tbodyObj = document.createElement("tbody");
			that._nextPrevTableObj.appendChild(tbodyObj);
			
			var trObj = document.createElement("tr");
			tbodyObj.appendChild(trObj);
			
			var tdPrevObj = document.createElement("td");
			tdPrevObj.style.width = "50%";
			tdPrevObj.style.textAlign = "left";
			trObj.appendChild(tdPrevObj);
			
			if (hasPrev) {
				var hrefPrevObj = document.createElement("a");
				hrefPrevObj.href = "javascript:;";
				dojo.connect(hrefPrevObj, "onclick", function(evt) {
					that.prevPage();
					return false;
			    });
				tdPrevObj.appendChild(hrefPrevObj);
				
				var imgPrevObj = document.createElement("img");
				imgPrevObj.style.border = "0";
				imgPrevObj.style.marginRight = "20px";
				imgPrevObj.title = data.prevLabel;
				imgPrevObj.src = ss_imagesPath + "pics/sym_arrow_left_.gif";
				hrefPrevObj.appendChild(imgPrevObj);
			}
			
			var tdNextObj = document.createElement("td");
			tdNextObj.style.width = "50%";
			tdNextObj.style.textAlign = "right";
			trObj.appendChild(tdNextObj);
			
			if (hasNext) {
				var hrefNextObj = document.createElement("a");
				hrefNextObj.href = "javascript:;";
				dojo.connect(hrefNextObj, "onclick", function(evt) {
					that.nextPage();
					return false;
			    });
				tdNextObj.appendChild(hrefNextObj);
				
				var imgNextObj = document.createElement("img");
				imgNextObj.style.border = "0";
				imgNextObj.style.marginLeft = "20px";
				imgNextObj.title = data.nextLabel;
				imgNextObj.src = ss_imagesPath + "pics/sym_arrow_right_.gif";
				hrefNextObj.appendChild(imgNextObj);
			}
			
			
			that._searchResultsList.parentNode.insertBefore(that._nextPrevTableObj, that._searchResultsList.nextSibling); 
		}
	}
	
	this.showResultList = function() {
		ss_setObjectTop(that._listContainer, parseInt(ss_getDivTop(that._inputObj) + ss_getObjectHeight(that._inputObj) + divTopOffset));
		ss_setObjectLeft(that._listContainer, parseInt(ss_getDivLeft(that._inputObj)))
		ss_showDivActivate(that._listContainer.id, true);
	}
	
	this.selectItem0 = function () {
		var liObjs = that._listContainerInnerDiv.getElementsByTagName('li');
		if (liObjs.length == 1) {
			eval("var type = type_"+liObjs[0].id);
			that.selectItem(liObjs[0], type);
		}
	}
	
	//Routine called when item is clicked
	this.selectItem = function(obj, entityType) {
		if (!obj || !obj.id || obj.id == undefined || entityType == undefined) return false;
		var spanObjs = obj.getElementsByTagName("span");
		if (spanObjs.length > 0) {
			//Put the selected item into the textarea not including the (...)
			var innerText = spanObjs[0].innerHTML;
			if (innerText.indexOf("(") > 0) innerText = innerText.substr(0, innerText.indexOf("(")-1);
			that._inputObj.value = innerText;
		}
		var id = ss_replaceSubStr(obj.id, 'ss_find_id_', "");
		if (that._singleClickRoutine != "") {
			that._callRoutineSingle(id, obj, entityType);
		} else {
			if (that._singleListType == "tags" || that._singleListType == "communityTags" || that._singleListType == "personalTags") {
				var url = ss_replaceSubStrAll(that._singleViewUrl, 'ss_tagPlaceHolder', id);
				setTimeout("self.location.href = '"+url+"';",100);
			} else if (that._singleListType == "workflows") {
				// no link to show
			} else if (that._singleListType == "entries") {
				var url = ss_replaceSubStr(that._singleViewUrl, 'ss_entryIdPlaceholder', id);
				setTimeout("self.location.href = '"+url+"';",100);
			} else if (that._singleListType == "places") {
			    var url = that._singleViewUrl; 
				url = ss_replaceSubStr(url, 'ss_binderIdPlaceholder', id);
				url = ss_replaceSubStr(url, 'ss_entityTypePlaceholder', entityType);
				if (ss_gotoPermalink(id, id, entityType, that._singleRenderNamespace, 'yes')) {
				setTimeout("self.location.href = '"+url+"';",100);
				}
				return false;
			} else { // users
				var url = ss_replaceSubStr(that._singleViewUrl, 'ss_entryIdPlaceholder', id);
				setTimeout("self.location.href = '"+url+"';",100);
			}
		}
	}

	//Routine called when item is clicked in accessible mode
	this.selectItemAccessible = function(obj, entityType) {
		if (that._singleListType != "entries") {
			return that.selectItem(obj, entityType);
		} else {
			if (!obj || !obj.id ||obj.id == undefined) return false;
			var id = ss_replaceSubStr(obj.id, 'ss_find_id_', "");
			var url = that._singleViewAccesibleUrl; 
			url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
			setTimeout("self.location.href = '"+url+"';",100);
		}
	}
	
	this.nextPage = function() {
		pageRequested = 1;
		pageNumberBefore = pageNumber;
		pageNumber++;
		that.search();
	}	

	this.prevPage = function() {
		pageRequested = 1;
		pageNumberBefore = pageNumber;
		pageNumber--;
		if (pageNumber < 0) pageNumber = 0;
		that.search();
	}

	this.close = function() {
		pageNumber = 0;
		pageNumberBefore = 0;
		pageRequested = 0;
		lastHrefId = "";

		// TODO: accessible mode here		
		var textObj = document.getElementById('ss_combobox_autocomplete_' + that._singlePrefix);
		if (textObj == null) {
			textObj = self.parent.document.getElementById('ss_combobox_autocomplete_' + that._singlePrefix);
		}
		if (textObj != null) {
			try {textObj.focus();} catch(e){}
		}
	}

	this.blurTextArea = function() {
		pageRequested = 0;
		lastHrefId = "";
		if (!isMouseOverList) {
			setTimeout(function() { 
				ss_debug('blur hide list')
				//that._listContainer.style.display = "none";
			} , 200);
		}
	}

	this.mouseOverList = function() {
		isMouseOverList = true;
	}

	this.mouseOutList = function() {
		isMouseOverList = false;
	}

	this._searchAccessible = function(searchText, crFound) {
		//In accessibility mode, wait for the user to type cr
		if (!crFound && parseInt(pageNumber) == 0 && 
				parseInt(pageNumberBefore) == 0) return;
		
	    var iframeDivObj = self.document.getElementById("ss_findIframeDiv");
	    var iframeObj = self.document.getElementById("ss_findIframe");
	    var iframeDivObjParent = self.parent.document.getElementById("ss_findIframeDiv");
	    var iframeObjParent = self.parent.document.getElementById("ss_findIframe");
	    var textObj = that._inputObj; // TODO: change positionnig - was after div
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
			dojo.connect(aObj, "onclick", function(evt) {
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
	    // if (self.parent == self && textObj != null) {
	    	var x = dojo.coords(textObj, true).x;
	    	var y = dojo.coords(textObj, true).y;
		    ss_setObjectTop(iframeDivObj, y + ss_getObjectHeight(textObj) + "px");
		    ss_setObjectLeft(iframeDivObj, x + "px");
		// }
		ss_showDiv("ss_findIframeDiv");
		var objId = dojox.uuid.generateRandomUuid();
		window[objId] = that;
		var urlParams = {operation:"find_user_search", searchText:searchText,
						maxEntries:"10", pageNumber: pageNumber,
						findType: that._singleListType, listDivId: that._listContainerInnerDiv.id,
						namespace: that._singlePrefix, findObjectName: objId,
						sendingEmail: that._singleSendingEmail};
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_find");
	    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
			self.location.href = url;
		} else {
			iframeObj.src = url;
		}
	}

	
	this.addValueByElement = function(id, obj) {
		if (that._itemAlreadyAddedMultiple(id)) {
			that._inputObj.value = "";
			try {that._inputObj.focus();} catch(e){}
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
		dojo.connect(newAnchorObj, "onclick", function(evt) {
			that.removeValueByElement(evt.target.parentNode, id, spanObj.innerHTML);
	    });	
		var newImgObj = document.createElement("img");
		newImgObj.setAttribute("src", ss_imagesPath + "pics/delete.png");
		newImgObj.setAttribute("border", "0");
		newImgObj.style.paddingLeft = "10px";
		newAnchorObj.appendChild(newImgObj);
		newLiObj.appendChild(newAnchorObj);
		ulObj.appendChild(newLiObj);
		that.addValue(id);
		that._callRoutineMultiple(id, spanObj.innerHTML, onAddCallbacks);
		that._highlight(newLiObj);
		that._inputObj.value = "";
		try {that._inputObj.focus();} catch(e){}
	}
	
	this.addGroupValueByElement = function(id, obj) {
		if (that._itemAlreadyAddedMultiple(id)) {
			return;
		}
		var spanObj = obj.getElementsByTagName("span").item(0);
		var ulObj = document.getElementById('added_' + that._multiplePrefix);
		var newLiObj = document.createElement("li");
		newLiObj.setAttribute("id", id);
		if (!ss_isIE) {
			//This does not work on IE, so skip making the group name hot 
			var newGroupAnchorObj = document.createElement("a");
			var showGroupUrl = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_group_list", groupId:id});
			newGroupAnchorObj.setAttribute("href", showGroupUrl);
			newGroupAnchorObj.setAttribute("onclick", "self.window.open(this.href, '_blank', 'directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=400,height=600');return false;");
			newGroupAnchorObj.className = "ss_nowrap";
			newGroupAnchorObj.innerHTML = spanObj.innerHTML;
			newLiObj.appendChild(newGroupAnchorObj);
		} else {
			var newGroupAnchorObj = document.createElement("span");
			newGroupAnchorObj.className = "ss_nowrap";
			newGroupAnchorObj.innerHTML = spanObj.innerHTML;
			newLiObj.appendChild(newGroupAnchorObj);
		}
		var newAnchorObj = document.createElement("a");
		newAnchorObj.setAttribute("href", "javascript: ;");
		dojo.connect(newAnchorObj, "onclick", function(evt) {
			that.removeValueByElement(evt.target.parentNode, id, spanObj.innerHTML);
	    });	
		var newImgObj = document.createElement("img");
		newImgObj.setAttribute("src", ss_imagesPath + "pics/delete.png");
		newImgObj.setAttribute("border", "0");
		newImgObj.style.paddingLeft = "10px";
		newAnchorObj.appendChild(newImgObj);
		newLiObj.appendChild(newAnchorObj);
		ulObj.appendChild(newLiObj);
		that.addValue(id);
		that._callRoutineMultiple(id, spanObj.innerHTML, onAddCallbacks);
		that._highlight(newLiObj);
		that._inputObj.value = "";
		try {that._inputObj.focus();} catch(e){}
	}
	
	this._callRoutineMultiple = function(id, txt, callbacks) {
		var obj = window;
		if (that._multipleClickRoutineObj && typeof that._multipleClickRoutineObj == "string" && window[that._multipleClickRoutineObj]) {
			obj = window[that._multipleClickRoutineObj];
		} else if (that._multipleClickRoutineObj && typeof that._multipleClickRoutineObj == "object") {
			obj = that._multipleClickRoutineObj;
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
		if (that._singleClickRoutineObj && typeof that._singleClickRoutineObj == "string" && window[that._singleClickRoutineObj]) {
			callbackObj = window[that._singleClickRoutineObj];
		} else if (that._singleClickRoutineObj && typeof that._singleClickRoutineObj == "object") {
			callbackObj = that._singleClickRoutineObj;
		}
		if (that._singleClickRoutine && callbackObj && callbackObj[that._singleClickRoutine]) {
			var objMethod = callbackObj[that._singleClickRoutine];
			var name = "";
			if (obj && obj.firstChild && obj.firstChild.firstChild && obj.firstChild.firstChild.innerText) {
				name = obj.firstChild.firstChild.innerText;
			} else if (obj && obj.firstChild && obj.firstChild.firstChild && obj.firstChild.firstChild.textContent) {
				name = obj.firstChild.firstChild.textContent;
			}
			if (that._singleListType == "places") {
				objMethod.apply(this, [id, entityType, obj, name]);
			} else {
				objMethod.apply(this, [id, obj, name]);
			}
		} else if (that._singleClickRoutine && typeof that._singleClickRoutine == "function") {
			that._singleClickRoutine();
		}
		if (that._singleLeaveResultsVisible && that._singleLeaveResultsVisible != 'false') {
			setTimeout(function() {that.showResultList()}, 200);
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
		dojox.fx.highlight({node:obj, color:'#FFFF33', duration:500}).play();
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

	// for pure js widget creation
	ssFind.multipleTemplateStartListHTML = 
		  "<input type=\"hidden\" name=\"${formElement}\" id=\"${prefix}_ss_find_multiple_input\"/>"
		+ "<table class=\"ss_style ss_combobox_autocomplete_list\" cellspacing=\"0\" cellpadding=\"0\">"
		+ "<tbody><tr><td style=\"width:80px;\"><textarea class=\"ss_combobox_autocomplete\" name=\"${formElement}_single\" id=\"${inputId}\"></textarea>"
		+ "<div><span class=\"ss_fineprint\">${label}</span></div>"
		+ "</td><td>"
		+ "<ul id=\"added_${prefix}\">";
		/* TODO: list initial values are not implemented yet
					<c:forEach var=\"item\" items=\"${ssFindUserList}\">
						<li id=\"<c:out value=\"${item.id}\"/>\" ><c:out value=\"${item.title}\"/>
							<a href=\"javascript: ;\" 
								onclick=\"window['findMultiple${prefix}'].removeValueByElement(this, '<c:out value=\"${item.id}\"/>', '<c:out value=\"${item.title}\"/>'); return false;\"
								><img <ssf:alt tag=\"alt.delete\"/> 
									src=\"<html:imagesPath/>pics/sym_s_delete.gif\"/></a>
						</li>
					</c:forEach>
		*/
		
		
	ssFind.multipleTemplateEndListHTML = "</ul></td></tr></tbody></table>";
	
	ssFind.createMultipleFromTemplate = function(container, prefix, formElement, label) {
		var inputId = "ss_combobox_autocomplete_" + prefix;
		container.innerHTML = ssFind.multipleTemplateStartListHTML.replace(/\$\{prefix\}/g, prefix)
										.replace(/\$\{formElement\}/g, formElement)
										.replace(/\$\{label\}/g, label)
										.replace(/\$\{inputId\}/g, inputId)	 + ssFind.multipleTemplateEndListHTML;
		return inputId;
	}

	/*
	 	Collect all ssFind.Find objects by formName and elementName for later reference.
	 	Used by event scheduler to refer attendee list.
	 */
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
	
	//Routine to conditionally submit the form (called from an <input type="button"...>)
	function ss_submitFindForm(formName) {
		var formObj = self.document.forms[formName];
		if (formObj != null) {
			if (ss_onSubmit(formObj, true)) {
				//Ok to submit
				//But first, add the selected button to the form
			    var hiddenObj = document.createElement("input");
			    hiddenObj.setAttribute("type", "hidden");
			    hiddenObj.setAttribute("name", ss_buttonSelected);
			    hiddenObj.setAttribute("value", ss_buttonSelected);
			    formObj.appendChild(hiddenObj);
				formObj.submit();
			}
		}
	}
}