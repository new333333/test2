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
//Routines that support the sliding table

function ss_getSlidingTableX(divId) {
	return ss_getDivLeft(divId);
}
function ss_getSlidingTableY(divId) {
	return ss_getDivTop(divId);
}
function ss_position_sTableCol(divName, x, y, w) {
    if (typeof ss_slidingTableId_2 == "undefined") return;
    var divObj = self.document.getElementById(divName)
    //ss_debug('Position cols: ' + divName +', '+ x +', '+ y +', '+ w + ', z-index: '+divObj.style.zIndex)
    if (divObj.style.position == "absolute") ss_moveDivToBody(divName);
    var ss_sTableHeight = ss_getDivHeight(ss_slidingTableId_2)
    var ss_sTableInnerHeight = parseInt(ss_sTableHeight- ss_sTableMarginTop - ss_sTableMarginBottom)
    self.document.getElementById(divName).style.left = x+1 + "px"
    self.document.getElementById(divName).style.top = y+1 + "px"
    self.document.getElementById(divName).style.clip = "rect(-9px " + parseInt(w) + "px " + ss_sTableInnerHeight + "px -9px)"
    //The next lines workaround an IE failure to show backgrounds correctly
    self.document.getElementById(divName).style.visibility = "hidden"
    self.document.getElementById(divName).style.visibility = "visible"
}

function ss_slidingTableDrag(evt) {
    if (typeof ss_slidingTableId_2 == "undefined" || typeof ss_slidingTableId == "undefined") return true;
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
                if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
                    ss_slidingTableOffsetX = evt.layerX;
                    ss_slidingTableOffsetY = evt.layerY;
                }
            }
            ss_slidingTableStartingToDrag = 0
        }
		var scrollDivLeft = parseInt(ss_getDivLeft(ss_slidingTableId)) - parseInt(ss_getDivScrollLeft(ss_slidingTableId))
        var dObjLeft
        if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
            dObjLeft = evt.pageX - ss_slidingTableOffsetX;
            if (typeof ss_slidingTableOffsetX != 'undefined') dObjLeft = dObjLeft - ss_slidingTableOffsetX;
        } else {
            dObjLeft = evt.clientX - ss_slidingTableOffsetX;
            if (typeof ss_slidingTableOffsetX != 'undefined') dObjLeft = dObjLeft - ss_slidingTableOffsetX;
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
		if (obj.getElementsByTagName("input").length > 0 ) {
			//If there is an input tag in this, don't show the hover over because it obscures the real input element
			return;
		}
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
				var s = "<table cellspacing='0' cellpadding='2' class='ss_mouseOverInfo'>"
				s += "<tr><td class='" + obj.className + "' nowrap>"
				s += obj.innerHTML
				s += "&nbsp;</td></tr></table>"
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
	var oldObj = ss_slidingTableMosueOverObj;
	if (!obj || obj == null) {
		ss_slidingTableMosueOverObj = null;
		ss_showHideObj("ss_info_popup", "hidden", "none")
	} else if (obj != ss_slidingTableMosueOverObj) {
		ss_slidingTableMosueOverObj = obj
 		ss_showHideObj("ss_info_popup", "hidden", "none")
	}
	//The next lines workaround an IE failure to show backgrounds correctly
	fObj = document.getElementById("ss_folder_table_parent")
	if (isIE && fObj != null) {
	    fObj.style.visibility = "hidden"
	    fObj.style.visibility = "visible"
	}
}
