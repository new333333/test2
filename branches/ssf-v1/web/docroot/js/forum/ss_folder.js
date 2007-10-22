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
//Routines used by folder views

function ss_highlightLineById(id) {
	if (ss_displayStyle == "accessible") {return;}
    if (id == "") {return;}
    var obj = self.document.getElementById(id)
    if (obj == null) {
    	//Didn't find it by this name. Look for it by its other names.
    	
    	if (((typeof ss_columnCount) != "undefined") && ss_columnCount > 0) {
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
								dojo.html.removeClass(rowObjPrev, ss_highlightClassName);
							}
						}
					}
					ss_highlightedLine = id;
					dojo.html.addClass(rowObj, ss_highlightClassName);
					ss_clearUnseen(rowObj);
			    }
			    if (colObj != null) {
					//Found a col; go highlight it
					if (i == 0 && ss_highlightedColLine != null) {
						//Reset the previous line color
						for (var j = 0; j <= ss_columnCount; j++) {
			    			var colIdPrev = ss_highlightedColLine + "_col_" + j;
						    var colObjPrev = self.document.getElementById(colIdPrev)
						    if (colObjPrev != null) {
								dojo.html.removeClass(colObjPrev, ss_highlightClassName);
							}
						}
					}
					ss_highlightedColLine = id;
					dojo.html.addClass(colObj, ss_highlightColClassName);
					ss_clearUnseen(colObj);
			    }
    		}
    	}
    	
    } else {
		//Found the id, this must be a single line; go highlight it
		if (ss_highlightedLine != null) {
			dojo.html.removeClass(ss_highlightedLine, ss_highlightClassName);
		}
		if (obj != null) {
			ss_highlightedLine = obj;
			dojo.html.addClass(ss_highlightedLine, ss_highlightClassName);
			ss_clearUnseen(obj);
		}
	}
}

function ss_clearUnseen(obj) {
	var objs = ss_getElementsByClass("ss_unseen", obj);
	for (var i = 0; i < objs.length; i++) {
		objs[i].className = ss_replaceSubStr(objs[i].className, "ss_unseen", "");
	}
}

function ss_showEntryInDiv(str) {
	//If we ever get here, it was an error (pmh)
	return
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

function ss_getNextEntryId(imageId) {
	var nextEntry = "";
	var nextBinderId = "";
	var entityType = "";
	if (typeof ss_currentEntryId == "undefined" || ss_currentEntryId == "") {
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
			ss_debug('i = '+i+', entry = '+ss_entryList[i])
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
	} else if (type == 'tags') {
		divId = 'ss_search_results_tags_div'
		tabId = 'ss_search_results_tags_tab';
		folderTableId = 'ss_tags_table';
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
	if (self.ss_setFolderDivHeight) ss_setFolderDivHeight();

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

