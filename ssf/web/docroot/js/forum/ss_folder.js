//Routines used by folder views

function ss_highlightLineById(id) {
	if (ss_displayStyle == "accessible") {return;}
    if (id == "") {return;}
    var obj = self.document.getElementById(id)
    if (obj == null) {
    	//Didn't find it by this name. Look for it by its other names.
    	if (ss_columnCount && ss_columnCount > 0) {
    		//This is a sliding table. Go highlight all of the columns.
    		for (var i = 0; i <= ss_columnCount; i++) {
    			var rowId = id + "_" + i;
    			var colId = id + "_col_" + i;
			    var rowObj = self.document.getElementById(rowId)
			    var colObj = self.document.getElementById(colId)
			    if (rowObj != null) {
					//Found a row; go highlight it
					if (i == 0 && ss_highlightedLine != null) {
						//Reset the previous line color
						for (var j = 0; j <= ss_columnCount; j++) {
			    			var rowIdPrev = ss_highlightedLine + "_" + j;
						    var rowObjPrev = self.document.getElementById(rowIdPrev)
						    if (rowObjPrev != null) {
								rowObjPrev.className = ss_savedHighlightClassName;
							}
						}
					}
					if (i == 1) {
						ss_savedHighlightClassName = rowObj.className;
					}
					ss_highlightedLine = id;
					rowObj.className = ss_highlightClassName;
			    }
			    if (colObj != null) {
					//Found a col; go highlight it
					if (i == 0 && ss_highlightedColLine != null) {
						//Reset the previous line color
						for (var j = 0; j <= ss_columnCount; j++) {
			    			var colIdPrev = ss_highlightedColLine + "_col_" + j;
						    var colObjPrev = self.document.getElementById(colIdPrev)
						    if (colObjPrev != null) {
								colObjPrev.className = ss_savedHighlightColClassName;
							}
						}
					}
					if (i == 1) {
						ss_savedHighlightColClassName = colObj.className;
					}
					ss_highlightedColLine = id;
					colObj.className = ss_highlightColClassName;
			    }
    		}
    	}
    	
    } else {
		//Found the id, this must be a single line; go highlight it
		if (ss_highlightedLine != null) {
			ss_highlightedLine.className = ss_savedHighlightClassName;
		}
		if (obj != null) {
			ss_highlightedLine = obj;
			ss_savedHighlightClassName = ss_highlightedLine.className;
			ss_highlightedLine.className = ss_highlightClassName;
		}
	}
}

function ss_showEntryInDiv(str) {
    //Make sure the absolute div is in the body
    self.ss_moveDivToBody('ss_showentrydiv');
    
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
    var wObj1 = null
    var wObj2 = null
    if (isNSN || isNSN6 || isMoz5) {
        wObj1 = self.document.getElementById('ss_showentrydiv')
        wObj2 = self.document.getElementById('ss_showentry')
    } else {
        wObj1 = self.document.all['ss_showentrydiv']
        wObj2 = self.document.all['ss_showentry']
    }
    
    if (str.indexOf('<body onLoad="self.location =') >= 0) {self.loaction.reload();}
    wObj1.style.display = "block";
    wObj2.innerHTML = str;
    wObj1.style.visibility = "visible";

    //If the entry div needs dynamic positioning after adding the entry, do it now
    if (self.ss_positionEntryDiv) {ss_positionEntryDiv();}
        
    //Keep a high water mark for the page so the scrolling doesn't bounce around
    setWindowHighWaterMark('ss_showentryhighwatermark');
    
    //Get the position of the div displaying the entry
    if (autoScroll == "true") {
	    var entryY = ss_getDivTop('ss_showentrydiv')
	    var entryH = ss_getDivHeight('ss_showentrydiv')
	    var bodyY = self.document.body.scrollTop
	    var windowH = ss_getWindowHeight()
	    if (entryY >= bodyY) {
	    	if (entryY >= parseInt(bodyY + windowH)) {
	    		if (entryH > windowH) {
	    			smoothScroll(0,entryY)
	    		} else {
	    			var newY = parseInt(entryY - (windowH - entryH))
	    			smoothScroll(0,newY)
	    		}
	    	} else if (parseInt(entryY + entryH) > parseInt(bodyY + windowH)) {
	    		var overhang = parseInt((entryY + entryH) - (bodyY + windowH))
	    		var newY = parseInt(bodyY + overhang)
	    		if (newY > entryY) {newY = entryY}
	    		smoothScroll(0,newY)
	    	}
	    } else {
	    	smoothScroll(0,entryY)
	    }
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_swapImages (id, img, alt) {
	if (document.getElementById(id)) {
		document.getElementById(id).src = img;
		document.getElementById(id).alt = alt;
	}
}

function ss_swapPrevFirst (imageId) {
	ss_swapImages('ss_first'+imageId, left_end_g, g_alt)
	ss_swapImages('ss_prev'+imageId, left_g, g_alt)
}

function ss_swapNextLast (imageId) {
	ss_swapImages('ss_last'+imageId, right_end_g, g_alt)
	ss_swapImages('ss_next'+imageId, right_g, g_alt)
	return false;
}

function ss_restoreImages (imageId, currentEntry) {
	ss_swapImages('ss_last'+imageId, right_end, right_end_alt)
	ss_swapImages('ss_next'+imageId, right, right_alt)
	ss_swapImages('ss_first'+imageId, left_end, left_end_alt)
	ss_swapImages('ss_prev'+imageId, left, left_alt)
	if (currentEntry != null) {
		if (currentEntry == ss_entryList[ss_entryCount - 1]) {				
			ss_swapPrevFirst(imageId);
		} else if (currentEntry == ss_entryList[0]) {
			ss_swapNextLast(imageId);
		}
	}
	return false;
}

function ss_getActionFromEntity(entityType) {
	if (entityType == 'folderEntry') return 'view_folder_entry';
	if (entityType == 'user') return 'view_profile_entry';
	if (entityType == 'group') return 'view_profile_entry';
	if (entityType == 'folder') return 'view_folder_listing';
	if (entityType == 'workspace') return 'view_ws_listing';
	if (entityType == 'profiles') return 'view_profile_listing';
	return 'view_folder_entry'
}

function ss_getNextEntryId(imageId) {
	var nextEntry = "";
	var nextBinderId = "";
	var entityType = "";
	if (!ss_currentEntryId || ss_currentEntryId == "") {
		if (ss_entryCount > 0) {
			nextEntry = ss_entryList[0];
			nextBinderId = ss_entryList2[0];
			entityType = ss_entryList3[0];
		}
	} else {
		for (var i = 0; i < ss_entryCount; i++) {
			if (ss_entryList[i] == ss_currentEntryId) {
				i++;
				if (i < ss_entryCount) {
					nextEntry = ss_entryList[i];
					nextBinderId = ss_entryList2[i];
					entityType = ss_entryList3[i];
				}
				break;
			}
		}
	}
	ss_restoreImages(imageId);
	if (nextEntry != "" && (entityType == 'folder' || entityType == 'workspace' || entityType == 'group')) {
		ss_currentEntryId = nextEntry;
		ss_getNextEntryId(imageId);
	} else if (nextEntry != "") {
		var url = ss_baseHistoryUrl;
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", nextBinderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", nextEntry);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", ss_getActionFromEntity(entityType));
		ss_loadEntryUrl(url, nextEntry);
		if (nextEntry == ss_entryList[ss_entryCount - 1]) {
			ss_swapPrevFirst(imageId);
		} 
	} else {
		//alert("There are no more entries to view.")
		ss_swapPrevFirst(imageId);
	}
	return false;
}

function ss_getPreviousEntryId(imageId) {
	var nextEntry = "";
	var nextBinderId = "";
	var entityType = "";
    if (!ss_currentEntryId || ss_currentEntryId == "") {
		if (ss_entryCount > 0) {
			nextEntry = ss_entryList[0];
			nextBinderId = ss_entryList2[0];
			entityType = ss_entryList3[0];
		}
	} else {
		for (var i = 0; i < ss_entryCount; i++) {
			if (ss_entryList[i] == ss_currentEntryId) {
				i--;
				if (i >= 0) {
					nextEntry = ss_entryList[i];
					nextBinderId = ss_entryList2[i];
					entityType = ss_entryList3[i];
				}
				break;
			}
		}
	}
	ss_restoreImages(imageId);
	if (nextEntry != "" && (entityType == 'folder' || entityType == 'workspace' || entityType == 'group')) {
		ss_currentEntryId = nextEntry;
		ss_getPreviousEntryId(imageId);
	} else if (nextEntry != "") {
		var url = ss_baseHistoryUrl + '&entryId=' + nextEntry;
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", nextBinderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", nextEntry);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", ss_getActionFromEntity(entityType));
		ss_loadEntryUrl(url, nextEntry);
		if (nextEntry == ss_entryList[0]) {
			ss_swapNextLast(imageId);
		}
	} else {
		//alert("There are no more entries to view.")
		ss_swapNextLast(imageId)
	}
	return false;
}

function ss_getFirstEntryId(imageId) {
	var firstEntry = "";
	var firstBinderId = "";
	var entityType = "";
    if (ss_entryCount > 0) {
    	firstEntry = ss_entryList[0];
    	firstBinderId = ss_entryList2[0];
		entityType = ss_entryList3[0];
    }
    ss_restoreImages(imageId);
	if (entityType == 'folder' || entityType == 'workspace' || entityType == 'group') {
		//Can't show this, so do nothing
    	ss_swapNextLast(imageId)
    } else if (firstEntry == "" || ss_currentEntryId == firstEntry) {
    	//alert("You are already viewing the last entry.")
    	ss_swapNextLast(imageId)
    } else {
        var url = ss_baseHistoryUrl;
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", firstBinderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", firstEntry);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", ss_getActionFromEntity(entityType));
		ss_loadEntryUrl(url, firstEntry);
		ss_swapNextLast(imageId)
    }
	return false;
}

function ss_getLastEntryId(imageId) {
	var lastEntry = "";
	var lastBinderId = "";
	var entityType = "";
    if (ss_entryCount > 0) {
    	lastEntry = ss_entryList[ss_entryCount - 1];
    	lastBinderId = ss_entryList2[ss_entryCount - 1];
		entityType = ss_entryList3[ss_entryCount - 1];
    }
    ss_restoreImages(imageId);
	if (entityType == 'folder' || entityType == 'workspace' || entityType == 'group') {
		//Can't show this, so do nothing
    	ss_swapPrevFirst(imageId)
    } else if (lastEntry == "" || ss_currentEntryId == lastEntry) {
    	//alert("You are already viewing the first entry.")
    	ss_swapPrevFirst(imageId)
    } else {
        var url = ss_baseHistoryUrl;
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", lastBinderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", lastEntry);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", ss_getActionFromEntity(entityType));
		ss_loadEntryUrl(url, lastEntry);
		ss_swapPrevFirst(imageId)
    }
	return false;
}

function ss_showSearchResults(type) {
	if (self.ss_clearMouseOverInfo) ss_clearMouseOverInfo();
	
	var divId = 'ss_search_results_things_div'
	var tabId = 'ss_search_results_things_tab';
	var folderTableId = 'ss_folder_table';
	if (type == 'things') {
		divId = 'ss_search_results_things_div'
		tabId = 'ss_search_results_things_tab';
		folderTableId = 'ss_folder_table';
	} else if (type == 'people') {
		divId = 'ss_search_results_people_div'
		tabId = 'ss_search_results_people_tab';
		folderTableId = 'ss_people_table';
	} else if (type == 'places') {
		divId = 'ss_search_results_places_div'
		tabId = 'ss_search_results_places_tab';
		folderTableId = 'ss_places_table';
	}
	if (ss_currentSearchResultsDiv != null) {
		var obj = document.getElementById(ss_currentSearchResultsDiv);
		obj.style.display = "none"
	}
	if (ss_currentSearchResultsTab != null) {
		var obj = document.getElementById(ss_currentSearchResultsTab);
		obj.className = "ss_search_results_selection_inactive"
	}
	var obj = document.getElementById(divId);
	obj.style.display = "block"
	ss_currentSearchResultsDiv = divId;
	obj = document.getElementById(tabId);
	obj.className = "ss_search_results_selection_active"
	ss_currentSearchResultsTab = tabId;
	
	//Tell the table drag routines which table is in play
	ss_folderTableId = folderTableId;
	
	//If there is a routine to set the div height, call it now
	if (ss_setFolderDivHeight) ss_setFolderDivHeight();

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}
