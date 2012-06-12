<%
/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2011 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2011 Novell, Inc. All Rights Reserved.
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
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE html<% if (org.kablink.teaming.web.util.MiscUtil.isHtmlQuirksMode()) { %> PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"<% } %>>
<html <c:if test="${!empty ssUser && !empty ssUser.locale}"> lang="${ssUser.locale}"</c:if>>
<head>
<META http-equiv="Content-Script-Type" content="text/javascript">
<META http-equiv="Content-Style-Type" content="text/css">
<c:set var="ss_notAdapter" value="1" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>

<script type="text/javascript">

//This parameter needs to be set to the number of screens that the user sees 
//  before the login screen appears (including the login screen itself).
//For example, in Liferay, the user gets a general page. Then he clicks "sign-in".
//  This takes the user to the actual login page. Thus the number of screens is 2.
var ss_screens_to_login_screen = ${ss_screens_to_login_screen};


//This parameter counts the number of screens that come up after logging in,
//  but before the user is actually logged in. Liferay takes 2 screens.
var ss_screens_after_login_screen_to_logged_in = ${ss_screens_after_login_screen_to_logged_in};

var ss_targetUrlLoadCount = 0;
function ss_loadTargetUrl() {
	if (ss_watcherTimer != null) {
		clearTimeout(ss_watcherTimer);
		ss_watcherTimer = null;
	}
	var iframeDiv = document.getElementById('iframe_window')
	var iframeHeight = parseInt(ss_getWindowHeight()) - 40;
	if (iframeHeight > 0) {
		iframeDiv.style.height = iframeHeight + "px"
	}

	ss_targetUrlLoadCount++;
	//alert(ss_targetUrlLoadCount)
	if (ss_targetUrlLoadCount > ss_screens_to_login_screen) {
		//ss_showHideObj('iframe_window', 'hidden', 'block');
	}

 	//Check to see if the user is logged in yet
	ss_setupStatusMessageDiv()
 	var url = url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"check_if_logged_in"});
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postCheckIfLoggedIn);
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postCheckIfLoggedIn(obj) {
	if (self.document.getElementById("ss_status_message").innerHTML == "ok") {
		//The user is logged in.
		ss_showHideObj('iframe_window', 'hidden', 'block');
		self.location.reload(true);
	} else {
		ss_startWatcher()
	}
}

var ss_watcherTimer = null;
function ss_startWatcher() {
	if (ss_watcherTimer != null) {
		clearTimeout(ss_watcherTimer);
		ss_watcherTimer = null;
	}
	ss_watcherTimer = setTimeout("ss_loadTargetUrl();", 1000);
}

var ss_transferUrl = self.location.href;

</script>

</head>
<body onLoad="ss_startWatcher();">
 <iframe id="iframe_window" name="iframe_window" 
    style="width:100%; height:600px; display:block;"
    src="${ss_portalLoginUrl}" frameBorder="0" onLoad="ss_loadTargetUrl();"
    title="<ssf:nlt tag="navigation.emptyFrame"/>" role="presentation">Novell Vibe</iframe>

</body>
</html>
