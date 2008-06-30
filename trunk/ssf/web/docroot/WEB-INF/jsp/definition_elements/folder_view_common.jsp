<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.ssfs.util.SsfsUtil" %>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "";
if (isIECheck) strBrowserType = "ie";
%>
<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>

<c:set var="slidingTableStyle" value="sliding"/>
<c:if test="${empty ss_folderViewStyle || ss_folderViewStyle == 'folder'}">
  <c:set var="slidingTableStyle" value="fixed"/>
</c:if>

<script type="text/javascript">
var ss_saveFolderColumnsUrl = "<ssf:url action="${action}" actionUrl="true"
		binderId="${ssFolder.id}"><ssf:param 
		name="operation" value="save_folder_columns"/></ssf:url>";
</script>
<jsp:include page="/WEB-INF/jsp/forum/view_forum_page_navigation_init.jsp" />

<div <c:if test="${slidingTableStyle == 'fixed'}">id="ss_folder_view_common${renderResponse.namespace}"</c:if>>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_view_common2.jsp" %>
</div>
<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>