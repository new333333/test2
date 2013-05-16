<%
// Return from request to upload an image file
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

<c:set var="ss_servlet" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>

<title>{$lang_ss_addimage_insert_image_title}</title>
<script type="text/javascript">
function passBackUrl() {
	//Get the handle on the form from the parent page
	var formObj = parent.document.forms[0];
	
	var form0 = document.getElementById('form0');
	var fileName = form0.file1.value;
	if (fileName == '') return;
	var iDot = fileName.lastIndexOf(".");
	if (iDot > 0) {
		var ext = fileName.substring(iDot+1, fileName.length);
		ext = ext.toLowerCase();
	    if (ext != 'gif' && ext != 'jpg' && ext != 'jpeg' && ext != 'png') {
			alert(self.parent.opener.ss_imageUploadError1);
			return;
		}
	} else {
		alert(self.parent.opener.ss_imageUploadError1)
		return;
	}
	
	//The parent form indicates the url to submit the form to
	form0.action = self.parent.opener.ss_imageUploadUrl;
	form0.submit();
}
function saveImageUrl(url) {
	var formObj = parent.document.forms[0];
	formObj.src.value = url;
	if (self.parent && self.parent.showPreviewImage) self.parent.showPreviewImage(url)
}
var urlToView = "${ss_upload_file_url}";

</script>
</head>
<body onLoad="saveImageUrl(urlToView);">
<form id="form0" name="form0" method="post" enctype="multipart/form-data" style="display:block;">
<input id="inputFile" name="file1" type="file" style="width:250px;" onChange="passBackUrl()" />
</form>
</body>
</html>
