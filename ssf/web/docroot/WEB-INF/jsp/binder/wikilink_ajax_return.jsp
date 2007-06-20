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
<form method="post" name="ss_findWikiPageForm"
	action="">
 <ssf:find formName="ss_findWikiPageForm" 
    formElement="searchTitle" 
    type="entries"
    width="140px" 
    binderId="${ssBinderId}"
    searchSubFolders="false"
    singleItem="true"
    clickRoutine="ss_loadWikiEntryId"
    accessibilityText="wiki.findPage"
    /> 
<input type="hidden" name="searchTitle"/>
</form>
<a href="javascript:;" onClick="ss_insertICElink('4', 'testpage', 'test text', '${binderId}');">TEST</a>
<p>
<a href="javascript:;" onClick="ss_insertICElink('${binderId}', 'testpage', 'local page', '${binderId}');">Local</a>
</body>
</html>
