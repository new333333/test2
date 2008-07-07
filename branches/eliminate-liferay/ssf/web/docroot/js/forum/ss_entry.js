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
    infoDiv.style.left = parseInt(parseInt(ss_getImageLeft(imgId)) + 20) + "px";
    infoDiv.style.top = parseInt(parseInt(ss_getImageTop(imgId)) - 30) + "px";
    infoDiv.style.display = "block"
    infoDiv.style.visibility = "visible"
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

function ss_confirmDeleteEntry() {
	if (confirm(ss_confirmDeleteEntryText)) {
		return true
	} else {
		return false
	}
}

function ss_confirmUnlockEntry() {
	if (confirm(ss_confirmUnlockEntryText)) {
		return true
	} else {
		return false
	}
}