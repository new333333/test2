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
 function ss_tagShow(namespace, divNumber) {
	var divId = 'ss_tags' + namespace + '_' + parseInt(divNumber) + '_pane';
	if (ss_isAdapter == 'false') {
		ss_moveDivToBody(divId);
	}
	var divObj = document.getElementById(divId);
	divObj.style.display = "block";
	divObj.visibility = "visible";
	divObj.style.zIndex = ssMenuZ;
	divObj.focus();
	var anchorName = 'ss_tags_anchor' + namespace + '_' + parseInt(divNumber);
	var anchorObj = document.getElementById(anchorName);
	if (ss_isAdapter == 'false') {
		ss_setObjectTop(divObj, (ss_getDivTop(anchorName) + 20) + "px");
	}
	var leftEdge = parseInt(ss_getDivLeft(anchorName)) + 10;
	var rightEdge = parseInt(leftEdge + ss_getObjectWidth(divObj));
	if (leftEdge < 0) leftEdge = 0;
	self.parent.ss_debug("top = "+ss_getDivTop(anchorName) + ", left = " +leftEdge)
	if (ss_isAdapter == 'false') {
		ss_setObjectLeft(divObj, leftEdge + "px")
	}
	ss_showDiv(divId);
	
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	if (parent.ss_setWikiIframeSize) parent.ss_setWikiIframeSize(namespace);
}
function ss_tagHide(namespace, divNumber) {
	var divId = 'ss_tags' + namespace + '_' + parseInt(divNumber) + '_pane';
	ss_hideDivNone(divId);
}
function ss_tagAdd(namespace, divNumber, binderId, entityType, entryId) {
	ss_tagModify('add', namespace, '', divNumber, binderId, entityType, entryId);	
}

function ss_tagDelete(namespace, tagId, divNumber, binderId, entityType, entryId) {
	ss_tagModify('delete', namespace, tagId, divNumber, binderId, entityType, entryId);
}
function ss_tagModify(operation2, namespace, tagId, divNumber, binderId, entityType, entryId) {
	ss_setupStatusMessageDiv();
	var tagToDelete = "";
	if (operation2 == 'delete') tagToDelete = tagId;
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"modify_tags", binderId:binderId});
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("operation2", operation2)
	ajaxRequest.addKeyValue("namespace", namespace)
	ajaxRequest.addKeyValue("tagToDelete", tagToDelete)
	ajaxRequest.addKeyValue("tagDivNumber", divNumber)
	ajaxRequest.addKeyValue("entityId", entryId);
	ajaxRequest.addKeyValue("entityType", entityType);
	ajaxRequest.addFormElements("ss_modifyTagsForm" + namespace + "_" + divNumber);
	ajaxRequest.setData("divNumber", divNumber);
	ajaxRequest.setData("entityId", entryId);
	ajaxRequest.setData("entityType", entityType);
	ajaxRequest.setData("namespace", namespace);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postModifyTags);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postModifyTags(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_debug("post: "+obj.getData("divNumber"))
	ss_tagShow(obj.getData("namespace"), obj.getData("divNumber"));
	
}