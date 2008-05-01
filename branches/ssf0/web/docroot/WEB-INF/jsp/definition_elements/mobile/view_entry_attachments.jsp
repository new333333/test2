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
<% // View entry attachments %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "nonie";
if (isIECheck) strBrowserType = "ie";
%>
<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<br/>
<c:if test="${!empty property_caption}">
<strong>${property_caption}</strong>
<br/>
</c:if>

<div>
<table cellpadding="0" cellspacing="0">
<tbody>
<c:set var="selectionCount" value="0"/>
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}" >
  <jsp:useBean id="selection" type="com.sitescape.team.domain.FileAttachment" />
<%
	String fn = selection.getFileItem().getName();
	String ext = "";
	if (fn.lastIndexOf(".") >= 0) ext = fn.substring(fn.lastIndexOf("."));
%>

  <c:set var="selectionCount" value="${selectionCount + 1}"/>
  <c:set var="versionCount" value="0"/>
  <c:forEach var="fileVersion" items="${selection.fileVersionsUnsorted}">
    <c:set var="versionCount" value="${versionCount + 1}"/>
  </c:forEach>
  <c:set var="thumbRowSpan" value="${versionCount}"/>
  <c:if test="${versionCount > 1}">
    <c:set var="thumbRowSpan" value="${thumbRowSpan + 2}"/>
  </c:if>
  <c:if test="${versionCount == 1}">
    <c:set var="thumbRowSpan" value="1"/>
  </c:if>
	  <tr>
		<td class="ss_att_title">
		  <a style="text-decoration: none;" 
			href="<ssf:url 
		    webPath="readFile"
		    folderId="${ssDefinitionEntry.parentBinder.id}"
		    entryId="${ssDefinitionEntry.id}"
		    entityType="${ssDefinitionEntry.entityType}" >
		    <ssf:param name="fileId" value="${selection.id}"/>
		    <ssf:param name="fileTime" value="${selection.modification.date.time}"/>
		    <ssf:param name="fileName" value="${selection.fileItem.name}"/>
		    </ssf:url>" 
		    <ssf:title tag="title.open.file">
			    <ssf:param name="value" value="${selection.fileItem.name}" />
		    </ssf:title>
			><c:out value="${selection.fileItem.name} "/></a>
			<c:if test="${!empty selection.fileLock}">
			  <br/>
			  <img <ssf:alt tag="alt.locked"/> src="<html:imagesPath/>pics/sym_s_caution.gif"/>
			  <span class="ss_mobile_small"><ssf:nlt tag="entry.lockedBy">
	    		<ssf:param name="value" value="${selection.fileLock.owner.title}"/>
			  </ssf:nlt></span>
			</c:if>
		  <ssf:ifSupportsViewAsHtml relativeFilePath="${selection.fileItem.name}" 
		    browserType="<%=strBrowserType%>">
				&nbsp;&nbsp;&nbsp;<a style="text-decoration: none;" href="<ssf:url 
				    webPath="viewFile"
				    folderId="${ssDefinitionEntry.parentBinder.id}"
			   	 	entryId="${ssDefinitionEntry.id}"
				    entityType="${ssDefinitionEntry.entityType}" >
			    	<ssf:param name="fileId" value="${selection.id}"/>
			    	<ssf:param name="fileTime" value="${selection.modification.date.time}"/>
			    	<ssf:param name="viewType" value="html"/>
			    	</ssf:url>"><span class="ss_mobile_small">[<ssf:nlt tag="entry.HTML" />]</span></a>
		  </ssf:ifSupportsViewAsHtml>
		</td>
	</tr>
</c:forEach>
</tbody>
</table>
</div>
</c:if>

