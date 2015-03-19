/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
//Routines to support showing an entry

function ss_saveRating(rating, id, namespace) {
	ss_setupStatusMessageDiv()
	var ratingDiv = "ss_rating_div_" + namespace + id
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_rating"});
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("rating", rating)
	ajaxRequest.addKeyValue("ratingDivId", ratingDiv)
	ajaxRequest.addKeyValue("entryId", id)
	ajaxRequest.addKeyValue("binderId", ss_binderId)
	ajaxRequest.addKeyValue("namespace", namespace)
	ajaxRequest.setData("entryId", id)
	ajaxRequest.setData("namespace", namespace)
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postSaveRatingRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postSaveRatingRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	if (obj.getData('entryId') == ss_currentRatingInfoId) {
		var infoDiv = document.getElementById("ss_rating_info_div"+obj.getData('namespace'))
    	infoDiv.style.display = "none"
    	infoDiv.style.visibility = "hidden"
    	ss_currentRatingInfoId = "";
	}
}
function ss_showRating(rating, id, namespace) {
	if (typeof ss_ratings_info == "undefined") return;
	var iRating = parseInt(rating);
	for (var i = 1; i <= iRating; i++) {
		var imgId = "ss_rating_img_" + id + "_" + i;
		var imgObj = document.getElementById(imgId)
		if (ss_ratingImages[imgId] == null) ss_ratingImages[imgId] = imgObj.src;
		imgObj.src = ss_ratingRedStar;
	}
	ss_moveDivToBody("ss_rating_info_div" + namespace)
	var infoDiv = document.getElementById("ss_rating_info_div" + namespace)
	var infoSpan = document.getElementById("ss_rating_info" + namespace)
	if (infoDiv == null || infoSpan == null) return;
	infoSpan.innerHTML = ss_ratings_info[rating];
    infoDiv.style.display = "block";
    infoDiv.style.whiteSpace = "nowrap";
    var infoDivWidth = parseInt(ss_getObjectWidth(infoDiv));
    infoDiv.style.left = parseInt(parseInt(ss_getImageLeft(imgId)) + 20 - infoDivWidth) + "px";
    infoDiv.style.top = parseInt(parseInt(ss_getImageTop(imgId)) + 20) + "px";
    infoDiv.style.visibility = "visible"
    infoDiv.focus();
    ss_currentRatingInfoId = id;
}
function ss_clearRating(rating, id, namespace) {
	var iRating = parseInt(rating);
	for (var i = 1; i <= iRating; i++) {
		var imgId = "ss_rating_img_" + id + "_" + i;
		var imgObj = document.getElementById(imgId)
		if (ss_ratingImages[imgId] != null) imgObj.src = ss_ratingImages[imgId];
	}
	var infoDiv = document.getElementById("ss_rating_info_div" + namespace)
    if (infoDiv != null && typeof infoDiv != 'undefined') {
	    infoDiv.style.display = "none"
	    infoDiv.style.visibility = "hidden"
	}
    ss_currentRatingInfoId = "";
}

/*
 * Runs the delete entry confirmation dialog.
 */
function ss_confirmDeleteEntry(eA, sFolderId, sEntryId) {
	ss_hideSpannedAreas();
	if (window.top.ss_deleteForumEntryGwt) {
		window.top.ss_deleteForumEntryGwt(sFolderId, sEntryId);
	}
	else {
		if (ss_binderMirrored && ("true" == ss_binderMirrored)) {
			if (confirm(ss_confirmDeleteEntryText)) {
				var url = eA.href;
				ss_postToThisUrl(url);
			}
		}
		else {
		   	ss_confirmDeleteEntry_Create(eA);
			ss_confirmDeleteEntry_Show('ss_confirm_entry_delete_div');
		}
	}
 	return false;
}

/*
 * Creates the delete entry confirmation dialog if it doesn't
 * already exist.
 */
function ss_confirmDeleteEntry_Create(eA)
{
	// If we haven't create the <DIV> for the dialog yet...
	var eDIV = document.getElementById('ss_confirm_entry_delete_div');
	if (!eDIV) {
		// ...create it now...
		eDIV = document.createElement("div");
		eDIV.className = "ss_confirmationDlg";
        eDIV.id = "ss_confirm_entry_delete_div";
        eDIV.style.position = "absolute";
        eDIV.style.visibility = "hidden";
        eDIV.style.display = "none";
		
		// ...store the URL from and patch the <A>'s href...
		eDIV.n_href = eA.href;
		eA.href = "javascript://";

		// ...add the banner text...		
		var ePBanner = document.createElement("p");
    	ePBanner.innerHTML = ss_confirmDeleteEntryText;
        eDIV.appendChild(ePBanner);
		ePBanner.className = "ss_bold";

		// ...add the purge immediately checkbox...		
		var ePCBox = document.createElement("p");
        eDIV.appendChild(ePCBox);
		var eNOBR = document.createElement("div");
		ePCBox.appendChild(eNOBR);
		var eCB = document.createElement("input");
		eCB.type = "checkbox";
		eCB.id = "ss_purgeImmediately";
		eNOBR.appendChild(eCB);
		var eCBSpan = document.createElement("span");
		eCBSpan.innerHTML = ss_purgeEntryImmediately
		eNOBR.appendChild(eCBSpan);

		// ...add a container for the push buttons...
		var eBR = document.createElement("br");
		eDIV.appendChild(eBR);		
		eNOBR = document.createElement("div");
		eNOBR.className = "teamingDlgBoxFooter";
		eNOBR.style.backgroundColor = "#fff";
		eDIV.appendChild(eNOBR);
		
		// ...add the OK push button...
		var eOK = document.createElement("input");
		eOK.type = "button";
		dojo.connect(eOK, "onclick", function(evt) {
			// Close the confirmation DIV. 
			ss_cancelPopupDiv('ss_confirm_entry_delete_div');
			
			// Are we running in the GWT UI?
			if (ss_isGwtUIActive) {
				// Yes!  Is the function to hide the popup entry DIV
				//  defined?
				if (typeof window.top.gwtContentIframe.ss_hideEntryDiv == 'function') {
					// Yes!  Call it.  Note:  When entries are being
					// viewed in a popup DIV, the DIV needs to get
					// hidden manually.  With the traditional UI, this
					// happened because of the full screen refresh.  
					window.top.gwtContentIframe.ss_hideEntryDiv();
				}
			}
			
			// The remainder of this code is unchanged from what was
			// here BEFORE the GWT UI was implemented.
			ss_showSpannedAreas();
			var url = this.n_div.n_href;
			if (document.getElementById("ss_purgeImmediately").checked) {
				url += "&purgeImmediately=true";
			}
			ss_postToThisUrl(url);
			return false;
	    });
		eOK.setAttribute("name", ss_buttonOK);
		eOK.setAttribute("value", ss_buttonOK);
		eOK.id = "ss_confirmEntryDeleteOK";
        eOK.n_div = eDIV;
        eNOBR.appendChild(eOK);

		// ...add a spacer between the ok and cancel push buttons.
		var eSpace = document.createTextNode(" ");
		eNOBR.appendChild(eSpace);
				
		// ...add the cancel push button...
		var eCancel = document.createElement("input");
		eCancel.type = "button";
		dojo.connect(eCancel, "onclick", function(evt) {
			ss_cancelPopupDiv('ss_confirm_entry_delete_div');
			ss_showSpannedAreas();
			return false;
	    });
		eCancel.setAttribute("name", ss_buttonCancel);
		eCancel.setAttribute("value", ss_buttonCancel);
		eCancel.id = "ss_confirmEntryDeleteCancel";
        eNOBR.appendChild(eCancel);

		// ...and finally, add the <DIV> to the page's <BODY>.		
    	document.getElementsByTagName("body").item(0).appendChild(eDIV);
	}
}

/*
 * Shows the delete entry confirmation dialog.
 */
function ss_confirmDeleteEntry_Show(divId, focusId) {
	// Write the <DIV> in a lightbox...
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	lightBox.onclick = function(e) {
		ss_showSpannedAreas();
		ss_cancelPopupDiv(divId);
	};
	var divObj = document.getElementById(divId);
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	
	// ...show it...
    ss_moveObjectToBody(divObj); 
	ss_setupPopupDiv(divObj);
	
	// ...and put the focus in its cancel push button.
	try {document.getElementById("ss_confirmEntryDeleteCancel").focus();} catch(e){}
}

function ss_confirmUnlockEntry(obj) {
	if (confirm(ss_confirmUnlockEntryText)) {
		var url = obj.href;
		ss_postToThisUrl(url);
		return false
	} else {
		return false
	}
}
