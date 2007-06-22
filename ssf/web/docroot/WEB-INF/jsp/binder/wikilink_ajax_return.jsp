<%
// Return for requesting a wikilink popup
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

<title>Insert ICEcore Link</title>
	<script language="javascript" type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce_popup.js"></script>
	<script language="javascript" type="text/javascript" src="<html:rootPath/>js/tiny_mce/plugins/ss_wikilink/jscripts/functions.js"></script>
	<base target="_self" />
</head>
<body onload="tinyMCEPopup.executeOnLoad('init();');" style="display: none">
<p>
Popup: To link to page in a different folder, enter the folder
name...
<br/>
<form method="post" name="ss_findLinkPlaceForm"
	action="">
 <ssf:find formName="ss_findLinkPlaceForm" 
    formElement="searchTitle" 
    type="places"
    width="140px" 
    binderId="${binderId}"
    searchSubFolders="false"
    singleItem="true"
    clickRoutine="ss_loadLinkBinderId"
    accessibilityText="iclink.folder"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
</p>
<p>
<form>
Link to folder: <b><span id="linkToFolderName">(current folder)</span></b>
<input type="hidden" name="binderId" id="binderId" size="5"/>
</p>
<p>
<b>Page name to link to:</b> <input name="pageName" id="pageName" size="30"/>
</p>
<p>
</form>
<p>
Popup: find page... 
<form method="post" name="ss_findLinkEntryForm"
	action="">
 <ssf:find formName="ss_findLinkEntryForm"
    formElement="searchTitle" 
    type="entries"
    width="140px" 
    binderId="${binderId}"
    searchSubFolders="false"
    singleItem="true"
    clickRoutine="ss_loadLinkEntryId"
    accessibilityText="iclink.entry"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
</p>


<a href="javascript:;" onClick="ss_insertICElinkFromForm('${binderId}');">Insert</a>
&nbsp;&nbsp;<a href="javascript:;" onClick="ss_cancelICElinkEdit();">Cancel</a>
</p>
</body>
</html>
