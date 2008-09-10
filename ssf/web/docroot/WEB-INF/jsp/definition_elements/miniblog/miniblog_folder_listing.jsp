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
<% //View the listing part of a survey folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${ssConfigJspStyle != 'template'}">
<table>
<tr>
<td valign="top">
	  <a href="<ssf:url action="view_ws_listing" binderId="${ssBinder.owner.workspaceId}" />" 
	  title="${ssBinder.owner.title}"><ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_medium" 
					photos="${ssUser.customAttributes['picture'].value}" 
					folderId="${ssUser.parentBinder.id}" entryId="${ssUser.id}" /></a>
</td>
<td valign="top">
<ul>
<c:forEach var="entry" items="${ssFolderEntries}" >
	<jsp:useBean id="entry" type="java.util.HashMap" />

	    <li style="padding-bottom:10px;">
		  <div><ssf:titleLink action="view_folder_entry" entryId="${entry._docId}" 
					binderId="${entry._binderId}" entityType="${entry._entityType}" 
					namespace="${renderResponse.namespace}">
					<ssf:param name="url" useBody="true">
						<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
						action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
					</ssf:param>
					<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
					      value="${entry._modificationDate}" type="both" 
						  timeStyle="short" dateStyle="medium" />
				</ssf:titleLink></div>
		  <div style="padding-left:10px;">
		    <span class="ss_italic" ><ssf:markup search="${entry}">${entry._desc}</ssf:markup></span>
		  </div>
	    </li>

</c:forEach>
</ul>
</td>
</tr>
</table>
</c:if>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
