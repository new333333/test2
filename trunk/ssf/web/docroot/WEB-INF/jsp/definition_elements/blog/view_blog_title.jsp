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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<% //Blog title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="title_entry" value="${ssDefinitionEntry}"/>

<c:if test="${empty ss_namespace}">
	<c:set var="ss_namespace" value="${renderResponse.namespace}" />
</c:if>

<jsp:useBean id="title_entry" type="com.sitescape.team.domain.FolderEntry" />

<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<%
	//Get a dispalyable number for the replies
	String docNumber = "";
	String fontSize = "ss_largeprint";
	if (title_entry.getTopEntry() != null) fontSize = "ss_largerprint";
	if (title_entry.getTopEntry() != null && title_entry.getDocNumber() != null) {
		docNumber = title_entry.getDocNumber();
		int i = docNumber.indexOf(".");
		if (i > 0) {
			docNumber = docNumber.subSequence(i+1, docNumber.length()) + ". ";
		}
	}
%>

<div class="ss_blog_title">
<div class="ss_header_bar_timestamp">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${ssDefinitionEntry.creation.date}" type="both" 
	 timeStyle="short" dateStyle="medium" />
 <ssf:nlt tag="general.title.timestamp.by"/> <a 
 	href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
			value="${ssDefinitionEntry.creation.principal.parentBinder.id}"/><ssf:param name="entryId" 
			value="${ssDefinitionEntry.creation.principal.id}"/>
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"
 ><ssf:showUser user="${ssDefinitionEntry.creation.principal}"/></a>
</div>
<%
	if (!ssSeenMap.checkIfSeen(title_entry)) {
		%>
		
		<a id="ss_sunburstDiv${ssFolder.id}_${ssDefinitionEntry.id}" href="javascript: ;" 
		  title="<ssf:nlt tag="sunburst.click"/>"
		  onClick="ss_hideSunburst('${ssDefinitionEntry.id}', '${ssFolder.id}');return false;"
		><span 
		  style="display:${ss_sunburstVisibilityHide};"
		  id="ss_sunburstShow${renderResponse.namespace}" 
		  class="ss_fineprint">
		  	<img border="0" <ssf:alt tag="alt.unseen"/> src="<html:imagesPath/>pics/sym_s_unseen.gif">
		  </span>
		  </a>
		
		<%
	}
%>
<div class="ss_header_bar_title_text">
<span class="ss_header_bar_title_text">
	<%= docNumber %>
	<ssf:titleLink action="view_folder_entry" entryId="${ssDefinitionEntry.id}" 
	binderId="${ssDefinitionEntry.parentFolder.id}" entityType="${ssDefinitionEntry.entityType}"
	namespace="${ss_namespace}_${ssDefinitionEntry.id}">

		<ssf:param name="url" useBody="true">
			<ssf:url adapter="true" portletName="ss_forum" folderId="${ssDefinitionEntry.parentFolder.id}" 
			action="view_folder_entry" entryId="${ssDefinitionEntry.id}" actionUrl="true" />
		</ssf:param>
		<c:out value="${ssDefinitionEntry.title}"/>
	</ssf:titleLink>

</span>
</div>
</div>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<%-- Subscribe, Ratings bar, visits --%>
<c:if test="${empty ssDefinitionEntry.parentEntry}">
<div style="padding-left: 22px">
<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view.jsp" />
</div>

<c:set var="entryIdString" value="<%= title_entry.getId().toString() %>"/>

</c:if>

