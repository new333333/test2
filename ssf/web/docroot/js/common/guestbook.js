/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */

var ss_signGuestbookIframeOffset = 50;

function ss_showSignGuestbookIframe(namespace, obj) {
	var targetDiv = document.getElementById(namespace + '_add_entry_from_iframe');
	var iframeDiv = document.getElementById(namespace + '_new_guestbook_entry_iframe');
	if (window.frames[namespace + '_new_guestbook_entry_iframe'] != null) {
		// avoids error on dashboard in firefox 
		var iframeDoc = window.frames[namespace + '_new_guestbook_entry_iframe'].document ? window.frames[namespace + '_new_guestbook_entry_iframe'].document : obj.contentDocument;

		var iframeHeight = parseInt(iframeDoc.body.scrollHeight);
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_signGuestbookIframeOffset + "px"
		}
		iframeDiv.style.border = "1px solid #CCCCCC";
	}
}

function ss_signGuestbook(namespace, obj) {
	var targetDiv = document.getElementById(namespace + '_add_entry_from_iframe');
	if (targetDiv != null) {
		if (targetDiv.style.visibility == 'visible') {
			targetDiv.style.visibility = 'hidden';
			targetDiv.style.display = 'none';
			return;
		}
	}
	targetDiv.style.visibility = 'visible';
	targetDiv.style.display = 'block';
	var iframeDiv = document.getElementById(namespace + '_new_guestbook_entry_iframe');
	iframeDiv.src = obj.href;
}

function ss_hideAddEntryIframe(namespace) {
	var targetDiv = document.getElementById(namespace + '_add_entry_from_iframe');
	if (targetDiv != null) {
		targetDiv.style.visibility = 'hidden'
		targetDiv.style.display = 'none'
	}
}
