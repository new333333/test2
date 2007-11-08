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
<% //View the listing part of a guestbook folder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/guestbook/guestbook_sign.jsp" %>

<c:forEach var="entry" items="${ssFolderEntries}" >
<table class="ss_guestbook" width="100%">
		<tr>
			<td class="ss_miniBusinessCard" style="padding-bottom: 5px;">
				<ssf:miniBusinessCard user="${entry._principal}"/> 
			</td>
			<td class="ss_guestbookContainer">
				<span class="ss_entryTitle ss_normalprint">
				<ssf:titleLink action="view_folder_entry" entryId="${entry._docId}" 
					binderId="${entry._binderId}" entityType="${entry._entityType}" 
					namespace="${renderResponse.namespace}">
					
						<ssf:param name="url" useBody="true">
							<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
							action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
						</ssf:param>
					
						<c:if test="${empty entry.title}">
					    	${entry._principal.title} <ssf:nlt tag="guestbook.author.wrote"/>: 
					    </c:if>
						<c:out value="${entry.title}" escapeXml="false"/>
					</ssf:titleLink>
				</span>

				<span class="ss_entrySignature"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${entry._modificationDate}" type="both" 
					  timeStyle="short" dateStyle="short" /></span>
				
				<c:if test="${!empty entry._desc}">
				<div class="ss_entryContent">
					<span><ssf:markup type="view" entryId="${entry._docId}" 
					binderId="${entry._binderId}"><c:out 
					  value="${entry._desc}" escapeXml="false"/></ssf:markup></span>
				</div>
				</c:if>
			</td>
		</tr>
</table>
</c:forEach>

</table>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>
