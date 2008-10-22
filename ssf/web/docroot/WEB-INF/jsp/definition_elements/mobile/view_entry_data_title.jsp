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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="title_entry" value="${ssDefinitionEntry}"/>
<jsp:useBean id="title_entry" type="com.sitescape.team.domain.FolderEntry" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<% //Title view %>
<div>
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		%><img border="0" <ssf:alt tag="alt.unseen"/> src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
<c:if test="${!empty ssDefinitionEntry.docNumber}">
<span class="ss_bold">${ssDefinitionEntry.docNumber}.</span>
</c:if>
<a href="<ssf:url adapter="true" portletName="ss_forum" 
					folderId="${ssBinder.id}" 
					entryId="${ssDefinitionEntry.id}"
					action="__ajax_mobile" 
					operation="mobile_show_entry" 
					actionUrl="false" />"
><span class="ss_bold"><c:if test="${empty ssDefinitionEntry.title}" >
(<ssf:nlt tag="entry.noTitle" />)
</c:if>
<c:out value="${ssDefinitionEntry.title}" /></span></a>
<br/>
</div>

<c:if test="${ss_showSignatureAfterTitle && !ss_signatureShown}">
  <%@ include file="/WEB-INF/jsp/definition_elements/mobile/view_entry_signature2.jsp" %>
  <c:set var="ss_signatureShown" value="true" scope="request"/>
</c:if>
