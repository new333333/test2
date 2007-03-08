<%
// Return from request to upload an image file
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
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
