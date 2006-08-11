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
