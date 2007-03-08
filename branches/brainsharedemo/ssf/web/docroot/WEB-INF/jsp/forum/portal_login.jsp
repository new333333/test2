<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd">
<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
<head>

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
	var iframeDiv = document.getElementById('iframe_window')
	if (window.frames['iframe_window'] != null) {
		var iframeHeight = parseInt(window.iframe_window.document.body.scrollHeight);
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + 40 + "px"
		}
	}
	ss_targetUrlLoadCount++;
	//alert(ss_targetUrlLoadCount)
	if (ss_targetUrlLoadCount > ss_screens_to_login_screen) {
		ss_showHideObj('iframe_window', 'hidden', 'block');
	}

 	//Check to see if the user is logged in yet
	ss_setupStatusMessageDiv()
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="operation" value="check_if_logged_in" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postCheckIfLoggedIn);
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postCheckIfLoggedIn(obj) {
	if (self.document.getElementById("ss_status_message").innerHTML == "ok") {
		//The user is logged in.
		self.location.reload(true);
	}
}

var ss_transferUrl = self.location.href;

</script>

</head>
<body>
 <iframe id="iframe_window" name="iframe_window" 
    style="width:100%; height:95%; display:block;"
    src="${ss_portalLoginUrl}" frameBorder="0" onLoad="ss_loadTargetUrl();">xxx</iframe>

</body>
</html>
