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
<% //File entry title view %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
%>

<div class="ss_entryContent">
<span class="ss_entryTitle">
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="com.sitescape.team.domain.FolderEntry" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		%><img border="0" <ssf:alt tag="alt.unseen"/> src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<c:set var="fileHandle" value="${ssDefinitionEntry.customAttributes['_fileEntryTitle'].value}"/>
<c:if test="${empty fileHandle}">
<c:set var="fileHandle" value="${ssDefinitionEntry.customAttributes['title'].value}"/>
</c:if>
<a style="text-decoration: none;" href="<ssf:url 
    webPath="readFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}"
    entityType="${ssDefinitionEntry.entityType}" >
    <ssf:param name="fileId" value="${fileHandle.id}"/>
    <ssf:param name="fileTime" value="${fileHandle.modification.date.time}"/>
    <ssf:param name="filename" value="${fileHandle.fileItem.name}"/>
    </ssf:url>">
<c:if test="${empty ssDefinitionEntry.title}">
    <span class="ss_light">--<ssf:nlt tag="entry.noTitle"/>--</span>
</c:if><c:out value="${ssDefinitionEntry.title}"/></a></span>

<ssf:ifSupportsEditInPlace relativeFilePath="${fileHandle.fileItem.name}" browserType="<%=strBrowserType%>">
<a style="text-decoration: none;"
	href="<ssf:ssfsInternalTitleFileUrl 
		binder="${ssDefinitionEntry.parentBinder}"
		entity="${ssDefinitionEntry}"
		fileAttachment="${fileHandle}"/>">
		<span class="ss_edit_button ss_smallprint">[<ssf:nlt tag="Edit"/>]</span></a>
</ssf:ifSupportsEditInPlace>

<c:set var="versionCount" value="0"/>
<c:forEach var="fileVersion" items="${fileHandle.fileVersions}">
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
<c:if test="${!empty fileHandle.fileVersions && versionCount > 1}">
<div class="ss_indent_medium">
<span class="ss_bold"><ssf:nlt tag="entry.PreviousVersions"/></span>
<br>
<c:set var="versionCount" value="0"/>
<table class="ss_compact20">
<c:forEach var="fileVersion" items="${fileHandle.fileVersions}">
<c:if test="${versionCount > 0}">
<tr>
<td class="ss_compact20"><a style="text-decoration: none;"
  href="<ssf:url 
    webPath="viewFile"
    folderId="${ssDefinitionEntry.parentBinder.id}"
    entryId="${ssDefinitionEntry.id}"
    entityType="${ssDefinitionEntry.entityType}" >
    <ssf:param name="fileId" value="${fileHandle.id}"/>
    <ssf:param name="versionId" value="${fileVersion.id}"/>
    <ssf:param name="fileTime" value="${fileVersion.modification.date.time}"/>
    </ssf:url>"><ssf:nlt tag="entry.version"/> ${fileVersion.versionNumber}</a></td>
<td class="ss_compact20"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${fileVersion.modification.date}" type="both" 
	 timeStyle="short" dateStyle="short" /></td>
<td class="ss_compact20"><span class="ss_smallprint">(${fileVersion.fileItem.lengthKB}KB)</span></td>
</tr>
</c:if>
<c:set var="versionCount" value="${versionCount + 1}"/>
</c:forEach>
</table>
</div>
<br>
</c:if>

</div>
