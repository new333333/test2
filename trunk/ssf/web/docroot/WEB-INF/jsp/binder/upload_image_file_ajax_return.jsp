<%
// Return from request to upload an image file
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
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/html; charset=UTF-8" %>

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/1999/REC-html401-19991224/loose.dtd">
<html xmlns:svg="http://www.w3.org/2000/svg-20000303-stylable">
<head>

<c:set var="ss_servlet" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>

<title>{$lang_ss_addimage_insert_image_title}</title>
<link href="css/ss_addimage.css" rel="stylesheet" type="text/css" />
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
	parent.showPreviewImage(url)
}
var urlToView = "${ss_upload_file_url}";

</script>
</head>
<body onLoad="saveImageUrl(urlToView);">
<form action="" id="form0" name="form0" method="post" enctype="multipart/form-data">
<input id="inputFile" name="file1" type="file" size="35" onChange="passBackUrl()" />
</form>
</body>
</html>
