<%
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
<%@ page import="org.kablink.util.BrowserSniffer" %>
<% //Blog title view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="org.kablink.teaming.domain.SeenMap" scope="request" />

<c:if test="${empty ss_namespace}">
	<c:set var="ss_namespace" value="${renderResponse.namespace}" />
</c:if>

<%
	//Get a dispalyable number for the replies
	String docNumber = "";
	String fontSize = "ss_largeprint";
	String entryType = "reply";
	boolean seen = true;
	
	java.lang.Object thisEntry = (java.lang.Object) request.getAttribute("ssDefinitionEntry");
%>

<%	if (thisEntry instanceof FolderEntry) {
%>
  <c:set var="objectType" value="FolderEntry"/>
  <c:set var="docId" value="<%= ((FolderEntry)thisEntry).getId() %>"/>
  <c:set var="binderId" value="<%= ((FolderEntry)thisEntry).getParentBinder().getId() %>"/>
  <c:set var="entryType" value="reply"/>
  <% if (((FolderEntry)thisEntry).getParentEntry() == null) { %><c:set var="entryType" value="entry"/><% } %>
  <c:set var="entityType" value="<%= ((FolderEntry)thisEntry).getEntityType() %>"/>
  <c:set var="title" value="<%= ((FolderEntry)thisEntry).getTitle() %>"/>
  <c:set var="creationDate" value="<%= ((FolderEntry)thisEntry).getCreation().getDate() %>"/>
  <c:set var="creationPrincipal" value="<%= ((FolderEntry)thisEntry).getCreation().getPrincipal() %>"/>
  <c:set var="totalReplyCount" value="<%= ((FolderEntry)thisEntry).getTotalReplyCount() %>"/>
  <c:if test="${empty totalReplyCount}"><c:set var="totalReplyCount" value="0"/></c:if>
<%
		//Get a dispalyable number for the replies
		if (((FolderEntry)thisEntry).getParentEntry() == null) entryType = "entry";
		docNumber = (String) ((FolderEntry)thisEntry).getDocNumber() + ".";
		if ("reply".equals(entryType) && ((FolderEntry)thisEntry).getDocNumber() != null) {
			int i = docNumber.indexOf(".");
			if (i > 0) {
				docNumber = docNumber.subSequence(i+1, docNumber.length()) + " ";
			}
		}
		if (!ssSeenMap.checkIfSeen((FolderEntry)thisEntry)) seen = false;
	} else {
%>
  <c:set var="objectType" value="Map"/>
  <c:set var="docId" value='<%= ((Map)thisEntry).get("_docId") %>'/>
  <c:set var="binderId" value='<%= ((Map)thisEntry).get("_binderId") %>'/>
  <c:set var="entryType" value='<%= ((Map)thisEntry).get("_entryType") %>'/>
  <c:set var="entityType" value='<%= ((Map)thisEntry).get("_entityType") %>'/>
  <c:set var="title" value='<%= ((Map)thisEntry).get("title") %>'/>
  <c:set var="creationDate" value='<%= ((Map)thisEntry).get("_creationDate") %>'/>
  <c:set var="creationPrincipal" value='<%= ((Map)thisEntry).get("_principal") %>'/>
  <c:set var="totalReplyCount" value='<%= ((Map)thisEntry).get("_totalReplyCount") %>'/>
  <c:if test="${empty totalReplyCount}"><c:set var="totalReplyCount" value="0"/></c:if>
<%
		//Get a dispalyable number for the replies
		entryType = (String) ((Map)thisEntry).get("_entryType");
		docNumber = (String) ((Map)thisEntry).get("_docNum") + ".";
		if ("reply".equals(entryType) && ((Map)thisEntry).get("_docNum") != null) {
			int i = docNumber.indexOf(".");
			if (i > 0) {
				docNumber = docNumber.subSequence(i+1, docNumber.length()) + ". ";
			}
		}
		if (!ssSeenMap.checkIfSeen((Map)thisEntry)) seen = false;
	}
%>
<%
	if ("entry".equals(entryType)) {
%>
<c:if test="${!empty property_showDocNumber && property_showDocNumber == 'false'}">
<%
		docNumber = "";
%>
</c:if>
<%
	}
%>
<div class="ss_blog_title">
<!-- 20110812 (DRF):  Eliminated as per bug#703285.
	<%
		if (!seen) {
			%>
			
			<a id="ss_sunburstDiv${ssFolder.id}_${docId}" href="javascript: ;" 
			  title="<ssf:nlt tag="sunburst.click"/>"
			  onClick="ss_hideSunburst('${docId}', '${ssFolder.id}');return false;"
			><span 
			  style="display:${ss_sunburstVisibilityHide};"
			  id="ss_sunburstShow${renderResponse.namespace}" 
			  class="ss_fineprint">
			  	<img border="0" <ssf:alt tag="alt.unseen"/> src="<html:imagesPath/>pics/sym_s_unseen.png">
			  </span>
			  </a>
			
			<%
		}
	%>
-->
<div class="ss_header_bar_title_text">
  <span class="ss_header_bar_title_text">
	<%= docNumber %>
	<ssf:titleLink action="view_folder_entry" entryId="${docId}" 
	binderId="${binderId}" entityType="${entityType}"
	namespace="${ss_namespace}_${docId}">
		<ssf:param name="url" useBody="true">
			<ssf:url adapter="true" portletName="ss_forum" folderId="${binderId}" 
			action="view_folder_entry" entryId="${docId}" actionUrl="true" />
		</ssf:param>
		<c:out value="${title}"/>
	</ssf:titleLink>
  </span>
</div>
<div class="ss_header_bar_timestamp">
<a href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
		value="${creationPrincipal.parentBinder.id}"/><ssf:param name="entryId" 
		value="${creationPrincipal.id}"/>
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"
 ><ssf:showUser user="${creationPrincipal}"/></a>
<span style="padding-left: 5px;">
 <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${creationDate}" type="both" 
	 dateStyle="medium" timeStyle="short" /> 
</span>
<span style="padding-left: 5px;">
<ssf:nlt tag="popularity.Comments"/>: <b>${totalReplyCount}</b></div>
</span>
</div>
<%
boolean isIE = BrowserSniffer.is_ie(request);
%>
<%-- Subscribe, Ratings bar, visits --%>
<c:if test="${objectType == 'FolderEntry' && entryType == 'entry'}">
<div style="padding-left: 22px">
<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view.jsp" />
</div>

<c:set var="entryIdString" value="${docId}"/>

</c:if>

