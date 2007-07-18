<%
// Return for requesting a find place popup
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

<script type="text/javascript">
function ss_saveFolderId(id, type, clickObj) {
	var obj = self.parent.document.getElementById('${propertyId}');
	var objTitle = self.parent.document.getElementById('${propertyId}_title');
	if (obj != null) {
		obj.value = id
	}
	if (objTitle != null && clickObj != null) {
		objTitle.innerHTML = clickObj.innerHTML
	}
}
</script>

</head>
<body>
<form method="post" name="ss_findFolderForm"
	action="">
 <ssf:find formName="ss_findFolderForm" 
    formElement="searchTitle" 
    type="places"
    width="140px" 
    binderId="${binderId}"
    searchSubFolders="false"
    foldersOnly="true"
    singleItem="true"
    clickRoutine="ss_saveFolderId"
    accessibilityText="iclink.folder"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
</body>
</html>
