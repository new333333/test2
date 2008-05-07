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
<% // Folder listing - select the style that the folder should be displayed in %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.team.module.definition.DefinitionUtils" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<%

//Get the folder type of this definition (folder, file, or event)
String folderViewStyle = DefinitionUtils.getViewType(ssConfigDefinition);
if (folderViewStyle == null || folderViewStyle.equals("")) folderViewStyle = "folder";
%>
<c:set var="ss_folderViewStyle" value="<%= folderViewStyle %>" scope="request" />
<script type="text/javascript" src="<html:rootPath/>js/forum/ss_folder.js"></script>

<c:if test="${ss_folderViewStyle == 'event'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/calendar/calendar_view.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'file'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/file_folder_view.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'blog'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/blog.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'wiki'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/wiki/wiki.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'photo'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/photo/photo.jsp" />
</c:if>
<c:if test="${empty ss_folderViewStyle || ss_folderViewStyle == 'folder'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/searchview/searchview.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'guestbook'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/guestbook/guestbook.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'task'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/task/task.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'survey'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/survey/survey.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'milestone'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/milestone/milestone_folder_view.jsp" />
</c:if>
<c:if test="${ss_folderViewStyle == 'table'}">
<jsp:include page="/WEB-INF/jsp/definition_elements/template_folder_view.jsp" />
</c:if>