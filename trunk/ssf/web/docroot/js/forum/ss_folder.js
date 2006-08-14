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

function ss_getNextEntryId(imageId) {
	var nextEntry = "";
	if (!ss_currentEntryId || ss_currentEntryId == "") {
		if (ss_entryCount > 0) {nextEntry = ss_entryList[0];}
	} else {
		for (var i = 0; i < ss_entryCount; i++) {
			if (ss_entryList[i] == ss_currentEntryId) {
				i++;
				if (i < ss_entryCount) {nextEntry = ss_entryList[i];}
				break;
			}
		}
	}
	ss_restoreImages(imageId);
	if (nextEntry != "") {
		var url = ss_baseHistoryUrl + '&entryId=' + nextEntry;
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
    if (!ss_currentEntryId || ss_currentEntryId == "") {
		if (ss_entryCount > 0) {nextEntry = ss_entryList[0];}
	} else {
		for (var i = 0; i < ss_entryCount; i++) {
			if (ss_entryList[i] == ss_currentEntryId) {
				i--;
				if (i >= 0) {nextEntry = ss_entryList[i];}
				break;
			}
		}
	}
	ss_restoreImages(imageId);
	if (nextEntry != "") {
		var url = ss_baseHistoryUrl + '&entryId=' + nextEntry;
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
    if (ss_entryCount > 0) {firstEntry = ss_entryList[0];}
    ss_restoreImages(imageId);
    if (firstEntry == "" || ss_currentEntryId == firstEntry) {
    	//alert("You are already viewing the last entry.")
    	ss_swapNextLast(imageId)
    } else {
        var url = ss_baseHistoryUrl + '&entryId=' + firstEntry;
		ss_loadEntryUrl(url, firstEntry);
		ss_swapNextLast(imageId)
    }
	return false;
}

function ss_getLastEntryId(imageId) {
	var lastEntry = "";
    if (ss_entryCount > 0) {lastEntry = ss_entryList[ss_entryCount - 1];}
    ss_restoreImages(imageId);
    if (lastEntry == "" || ss_currentEntryId == lastEntry) {
    	//alert("You are already viewing the first entry.")
    	ss_swapPrevFirst(imageId)
    } else {
        var url = ss_baseHistoryUrl + '&entryId=' + lastEntry;
		ss_loadEntryUrl(url, lastEntry);
		ss_swapPrevFirst(imageId)
    }
	return false;
}
