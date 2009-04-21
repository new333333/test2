<%
// The search results page
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table class="ss_style" style="width:100%;">
<tr>
  <th align="left"><ssf:nlt tag="folder.column.Title"/></th>
  <th align="left"><ssf:nlt tag="folder.column.Author"/></th>
  <th align="left"><ssf:nlt tag="folder.column.Date"/></th>
</tr>
<c:forEach var="fileEntry" items="${ss_searchResults}" >
<tr>
  <td valign="top" width="35%">
  	<c:choose>
  	<c:when test="${fileEntry._entityType == 'folderEntry'}">
    <a target="_blank" href="<ssf:url action="view_folder_entry" 
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'user' || fileEntry._entityType == 'group'}">
    <a target="_blank" href="<ssf:url action="view_profile_entry" 
    folderId="${fileEntry._binderId}"
    entryId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'folder'}">
    <a target="_blank" href="<ssf:url action="view_folder_listing" 
    folderId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="${fileEntry._entityType == 'workspace'}">
    <a target="_blank" href="<ssf:url action="view_ws_listing" 
    folderId="${fileEntry._docId}" />" >
    </c:when>
    <c:when test="{fileEntry._entityType == 'profiles'}">
    <a target="_blank" href="<ssf:url action="view_profile_listing" 
    folderId="${fileEntry._docId}" />" >
    </c:when>
 	</c:choose>
    <c:if test="${empty fileEntry.title}">
    <span class="ss_fineprint"><i>(<ssf:nlt tag="entry.noTitle" />)</i></span>
    </c:if>
    <c:out value="${fileEntry.title}"/></a>
  </td>
  <td valign="top" width="30%">
    <c:out value="${fileEntry._principal.title}"/>&nbsp;&nbsp;
  </td>
  <td valign="top" width="35%">
    <c:out value="${fileEntry._modificationDate}"/>
  </td>
</tr>
</c:forEach>
</table>
