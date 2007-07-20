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

<title><ssf:nlt tag="wiki.link.popup.title"/></title>
	<script language="javascript" type="text/javascript" src="<html:rootPath/>js/tiny_mce/tiny_mce_popup.js"></script>
	<script language="javascript" type="text/javascript" src="<html:rootPath/>js/tiny_mce/plugins/ss_wikilink/jscripts/functions.js"></script>
	<base target="_self" />
</head>
<body onload="tinyMCEPopup.executeOnLoad('init();');" style="display: none">
<form>
<ssf:nlt tag="wiki.link.tofolder"/>: <b><span id="linkToFolderName">(<ssf:nlt tag="wiki.link.currentfolder"/>)</span></b>
<input type="hidden" name="binderId" id="binderId" size="5" value="${binderId}"/>

<a href="javascript:;" onclick="ss_popup_folder();">[<ssf:nlt tag="button.change"/>]</a>
</p>
<p>
<b><ssf:nlt tag="wiki.link.topage"/>:</b> <input name="pageName" id="pageName" size="30"/>
<a href="javascript:;" onclick="ss_popup_page();">[<ssf:nlt tag="button.find"/>]</a>
</p>
</form>

<div id="folder_popup" style="position: absolute; display: none; background: #CCCCCC; top: 30px; padding: 10px;  border: 1px solid #333333;">
<ssf:nlt tag="wiki.link.differentfolder"/>:
<br/>
<form method="post" name="ss_findLinkPlaceForm"
	action="">
 <ssf:find formName="ss_findLinkPlaceForm" 
    formElement="searchTitle" 
    type="places"
    width="140px" 
    binderId="${binderId}"
    searchSubFolders="false"
    foldersOnly="true"
    singleItem="true"
    clickRoutine="ss_loadLinkBinderId"
    accessibilityText="wiki.findFolder"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
</div>
<div id="page_popup" style="position: absolute; display: none; background: #CCCCCC; top: 60px; padding: 10px; border: 1px solid #333333;">
<ssf:nlt tag="wiki.link.findpage"/>:
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
    accessibilityText="wiki.findPage"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
</div>


<a href="javascript:;" onClick="ss_insertICElinkFromForm('${binderId}');"><ssf:nlt tag="button.insert"/></a>
&nbsp;&nbsp;<a href="javascript:;" onClick="ss_cancelICElinkEdit();"><ssf:nlt tag="button.cancel"/></a>
</p>
</body>
</html>
