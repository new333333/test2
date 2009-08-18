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

var ss_signGuestbookIframeOffset = 20;

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
