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
/**
 * 
 */
function ss_invokeTagDlg( entryId, entryTitle, tagDivId )
{
	if ( typeof window.top.ss_invokeTagDlg != "undefined" )
	{
		var div;
		
		div = document.getElementById( tagDivId );
		window.top.ss_invokeTagDlg( entryId, entryTitle, div );
	}
}


 function ss_tagShowHide(namespace, divNumber) {
	var divId = 'ss_tags' + namespace + '_' + parseInt(divNumber) + '_pane';
	var divObj = document.getElementById(divId);
	if (divObj == null) return;
	if (typeof divObj.style.display != "undefined" && divObj.style.display == "block") {
		ss_tagHide(namespace, divNumber);
		return;
	}
	divObj.style.display = "block";
	divObj.visibility = "visible";
	divObj.style.zIndex = ssMenuZ;
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
}
 function ss_tagShow(namespace, divNumber) {
	var divId = 'ss_tags' + namespace + '_' + parseInt(divNumber) + '_pane';
	var divObj = document.getElementById(divId);
	if (divObj == null) return;
	if (typeof divObj.style.display != "undefined" && divObj.style.display != "block") {
		ss_tagShowHide(namespace, divNumber);
		return;
	}
 }
 function ss_tagHide(namespace, divNumber) {
	var divId = 'ss_tags' + namespace + '_' + parseInt(divNumber) + '_pane';
	ss_hideDivNone(divId);
	
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
}
function ss_tagAdd(namespace, divNumber, binderId, entityType, entryId) {
	ss_tagModify('add', namespace, '', divNumber, binderId, entityType, entryId);	
}

function ss_tagDelete(namespace, tagId, divNumber, binderId, entityType, entryId) {
	ss_tagModify('delete', namespace, tagId, divNumber, binderId, entityType, entryId);
}
function ss_tagModify(operation2, namespace, tagId, divNumber, binderId, entityType, entryId) {
	var formObj = document.getElementById("ss_modifyTagsForm" + namespace + "_" + divNumber);
	if (formObj != null) {
		if ((formObj.personalTag && formObj.personalTag.value && formObj.personalTag.value.indexOf(" ") != -1) || 
			(formObj.communityTag && formObj.communityTag.value && formObj.communityTag.value.indexOf(" ") != -1)) {
			alert(ss_tagConfirmBadCharacters)
		}
		
		if ((formObj.personalTag && formObj.personalTag.value && formObj.personalTag.value.length > 60) || 
			(formObj.communityTag && formObj.communityTag.value && formObj.communityTag.value.length > 60)) {
			alert(ss_tagConfirmTooLong)
		}
		
        var punct = "([!\"#$%&'()*+,./:;<=>?@[\\\\\\]^`{|}~-]*)";
		var pattern = new RegExp("[!\"#$%&'()*+,./:;<=>?@[\\\\\\]^`{|}~-]");
		if (formObj.personalTag && formObj.personalTag.value) {
			// Does the tag have an underscore in it?
			if ( formObj.personalTag.value.indexOf( "_" ) >= 0 )
			{
				// Yes, tell the user about the problem and bail.
				alert( ss_tagConfirmNoUnderscore );
				return;
			}
			
			if (pattern.test(formObj.personalTag.value) ) {
				alert(ss_tagConfirmNoPunct)
				return
			}
		}
		if (formObj.communityTag && formObj.communityTag.value) {
			// Does the tag have an underscore in it?
			if ( formObj.communityTag.value.indexOf( "_" ) >= 0 )
			{
				// Yes, tell the user about the problem and bail.
				alert( ss_tagConfirmNoUnderscore );
				return;
			}
			
			if (pattern.test(formObj.communityTag.value) ) {
				alert(ss_tagConfirmNoPunct)
				return
			}
		}
		                
		
		
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
}
function ss_postModifyTags(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_debug("post: "+obj.getData("divNumber"))
	ss_tagShowHide(obj.getData("namespace"), obj.getData("divNumber"));
	
}