<%
// Return for requesting a wikilink popup
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

<title><ssf:nlt tag="wiki.link.popup.title"/></title>
	<script language="javascript" type="text/javascript" src="<html:tinyMcePath/>tiny_mce_popup.js"></script>
	<script language="javascript" type="text/javascript" src="<html:tinyMcePath/>plugins/ss_wikilink/jscripts/functions.js"></script>
	<base target="_self" />
</head>
<body onload="tinyMCEPopup.executeOnLoad('init();');" style="display: none">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
var ss_wikiLinkUrl = "<ssf:url 
    adapter="true" 
    actionUrl="true"
    portletName="ss_forum" 
    action="__ajax_request">
	  <ssf:param name="operation" value="wikilink_form" />
	  <ssf:param name="binderId" value="ssBinderIdPlaceHolder" />
	  <ssf:param name="originalBinderId" value="ssOriginalBinderIdPlaceHolder" />
    </ssf:url>";
</script>
<form method="post" name="ss_findLinkForm"
>
<ssf:showHelp guideName="user" pageId="entry_linking" sectionId="entry_linking_otherfolder" /> 
<ssf:nlt tag="wiki.link.tofolder"/> <b><span id="linkToFolderName">(${ssBinder.title})</span></b>
<input type="hidden" name="binderId" id="binderId" value="${ssBinder.id}"/>
<input type="hidden" name="originalBinderId" id="originalBinderId" value="${ssOriginalBinder.id}"/>
<a href="javascript:;" onclick="ss_popup_folder();">[<ssf:nlt tag="button.change"/>]</a>
<div id="folder_popup" style="display: none; background: #CCCCCC; top: 30px; padding: 10px;  border: 1px solid #333333;">
<div align="right"><a href="javascript:;" onclick="ss_close_popup_folder();">
  <img border="0" src="<html:imagesPath/>pics/popup_close_box.gif" alt="x" title=""/></a>
</div>
<ssf:nlt tag="wiki.link.differentfolder"/>
<br/>
 <ssf:find formName="ss_findLinkForm" 
    type="places"
    width="350px" 
    binderId="${ssBinder.id}"
    searchSubFolders="false" 
    foldersOnly="false"
    singleItem="true"
    clickRoutine="ss_loadLinkBinderId"
    accessibilityText="wiki.findFolder"
    /> 
<input type="hidden" name="searchTitleFolder" id="searchTitleFolder" value="<ssf:escapeQuotes>${ssBinder.title}</ssf:escapeQuotes>"/>
</div>
<p>
<b><ssf:nlt tag="wiki.link.topage"/></b> <input type="text" name="pageName" id="pageName" size="30"/>
<a href="javascript:;" onclick="ss_popup_page();">[<ssf:nlt tag="button.find"/>]</a>
</p>

<div id="page_popup" style="display: none; background: #CCCCCC; top: 60px; padding: 10px; border: 1px solid #333333;">
<div align="right" ><a href="javascript:;" onclick="ss_close_popup_page();"><img border="0" src="<html:imagesPath/>pics/popup_close_box.gif" alt="x" title=""/></a></div>
<ssf:nlt tag="wiki.link.findpage"/>:
 <ssf:find formName="ss_findLinkForm"
    type="entries"
    width="350px" 
    binderId="${ssBinder.id}"
    searchSubFolders="false"
    singleItem="true"
    clickRoutine="ss_loadLinkEntryId"
    accessibilityText="wiki.findPage"
    /> 
<input type="hidden" name="searchTitle" id="searchTitle"/>
  <sec:csrfInput />
</form>
</div>


<a href="javascript:;" onClick="ss_insertICElinkFromForm('${ssBinder.id}');"><ssf:nlt tag="button.insert"/></a>
&nbsp;&nbsp;<a href="javascript:;" onClick="ss_cancelICElinkEdit();"><ssf:nlt tag="button.cancel"/></a>
</p>
</body>
</html>
