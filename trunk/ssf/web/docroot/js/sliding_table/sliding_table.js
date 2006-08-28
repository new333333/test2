//Routines that support the sliding table

function ss_getSlidingTableX(divId) {
	return ss_getDivLeft(divId);
}
function ss_getSlidingTableY(divId) {
	return ss_getDivTop(divId);
}
function ss_position_sTableCol(divName, x, y, w) {
    var divObj = self.document.getElementById(divName)
    if (divObj.style.position == "absolute") ss_moveDivToBody(divName);
    var ss_sTableHeight = ss_getDivHeight(ss_slidingTableId_2)
    var ss_sTableInnerHeight = parseInt(ss_sTableHeight- ss_sTableMarginTop - ss_sTableMarginBottom)

    if (isNSN || isNSN6 || isMoz5) {
        self.document.getElementById(divName).style.left= x + "px"
        self.document.getElementById(divName).style.top= y + "px"
        self.document.getElementById(divName).style.clip = "rect(-9px " + parseInt(w) + "px " + ss_sTableInnerHeight + "px -9px)"
    } else {
        self.document.all[divName].style.left=x
        self.document.all[divName].style.top=y
        self.document.all[divName].style.clip = "rect(-9px " + parseInt(w) + "px " + ss_sTableInnerHeight + "px -9px)"
    }
}

function ss_slidingTableDrag(evt) {
    var ss_sTableLeft = ss_getSlidingTableX(ss_slidingTableId_2)
    var ss_sTableWidth = ss_getDivWidth(ss_slidingTableId_2)
    if (ss_sTableWidth == 0) ss_sTableWidth = parseInt(ss_defTableWidth);
    var ss_sTableInnerWidth = parseInt(ss_sTableWidth - ss_sTableMarginLeft - ss_sTableMarginRight);
    var ss_sTableHeight = ss_getDivHeight(ss_slidingTableId_2)
    var ss_sTableInnerHeight = parseInt(ss_sTableHeight- ss_sTableMarginTop - ss_sTableMarginBottom)

    if (!evt) evt = window.event;
    if (ss_slidingTableDragObj) {
        if (ss_slidingTableStartingToDrag == 1) {
            if (evt.layerX) {
                if (isNSN || isNSN6 || isMoz5) {
                    ss_slidingTableOffsetX = evt.layerX;
                    ss_slidingTableOffsetY = evt.layerY;
                }
            }
            ss_slidingTableStartingToDrag = 0
        }
		var scrollDivLeft = parseInt(ss_getDivLeft(ss_slidingTableId)) - parseInt(ss_getDivScrollLeft(ss_slidingTableId))
        var dObjLeft
        if (isNSN || isNSN6 || isMoz5) {
            dObjLeft = evt.pageX - ss_slidingTableOffsetX;
        } else {
            dObjLeft = evt.clientX - ss_slidingTableOffsetX;
        }
        //Don't let the column go beyond the right or left margins
        if (parseInt(dObjLeft) < parseInt(ss_sTableLeft + ss_sTableMarginLeft)) {
        	dObjLeft = parseInt(ss_sTableLeft + ss_sTableMarginLeft)
        }
        //Leave some room to grab the table to drag it back (if necessary)
        if (parseInt(dObjLeft) > parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth - 8)) {
        	dObjLeft = parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth - 8)
        }
        dObjLeft = parseInt(dObjLeft)
        ss_slidingTableDragObj.style.left = parseInt(dObjLeft) + "px"
        var dObjClipWidth = parseInt(ss_sTableLeft + ss_sTableMarginLeft + ss_sTableInnerWidth - dObjLeft)
        if (dObjClipWidth < 4) dObjClipWidth = 4
        
        ss_slidingTableDragObj.style.clip = "rect(-9px " + dObjClipWidth + "px " + ss_sTableInnerHeight + "px -9px)"
        return false
    
    } else {
        return true
    }
}
ss_createEventObj('ss_slidingTableDrag', 'MOUSEMOVE');

function ss_showMouseOverInfo(obj) {
	if (ss_slidingTableMouseOverInfoDisabled) return;
	
	if (obj != ss_slidingTableMosueOverObj) {
		ss_moveObjectToBody(document.getElementById('ss_info_popup'))
		var ihtml = obj.innerHTML;
		if (ihtml != "" && ihtml != "&nbsp;" && ihtml != "&nbsp;&nbsp;" && 
		    ihtml != "&nbsp;&nbsp;&nbsp;") {
		    if (ihtml.length == 1 && ihtml.charCodeAt(0) == 160) {
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
		    } else if (ihtml.length == 2 && ihtml.charCodeAt(0) == 160 && ihtml.charCodeAt(1) == 160) {
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
		    } else if (ihtml.length == 3 && ihtml.charCodeAt(0) == 160 && ihtml.charCodeAt(1) == 160 && ihtml.charCodeAt(2) == 160) {
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
		    } else {
		    	ss_showHideObj("ss_info_popup", "hidden", "none");
				var s = "<table cellspacing='0' cellpadding='0' style='border: solid black 1px;'>"
				s += "<tr><td class='" + obj.className + "' nowrap>"
				s += obj.innerHTML
				s += "&nbsp;&nbsp;&nbsp;</td></tr></table>"
				ss_setDivHtml("ss_info_popup", s)
				if (self.document.getElementById("ss_info_popup")) {
					var x = parseInt(obj.offsetParent.offsetParent.style.left) - 1
					var y = parseInt(parseInt(obj.offsetTop) + parseInt(obj.offsetParent.offsetTop) + parseInt(obj.offsetParent.offsetParent.style.top)) - 1
					self.document.getElementById("ss_info_popup").style.left = x + "px"
					self.document.getElementById("ss_info_popup").style.top = y + "px"
					ss_showHideObj("ss_info_popup", "visible", "block")
					
					//See if this is a new maximum width
					ss_moveObjectToBody(document.getElementById('ss_info_popup_sizer'))
					var w = parseInt(x + ss_getDivWidth("ss_info_popup"))
					if (w > ss_popUp_sizer_width) {
						ss_popUp_sizer_width = w;
						ss_setObjectLeft(self.document.getElementById("ss_info_popup_sizer"), "0px")
						ss_setObjectWidth(self.document.getElementById("ss_info_popup_sizer"), w)
					}
				}
			}
		} else {
			ss_showHideObj("ss_info_popup", "hidden", "none")
		}
	}
	ss_slidingTableMosueOverObj = obj
}

function ss_clearMouseOverInfo(obj) {
	if (!obj || obj == null) {
		ss_slidingTableMosueOverObj = null;
		ss_showHideObj("ss_info_popup", "hidden", "none")
	} else if (obj != ss_slidingTableMosueOverObj) {
		ss_slidingTableMosueOverObj = obj
 		ss_showHideObj("ss_info_popup", "hidden", "none")
	}
}
