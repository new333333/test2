<%
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
<%
/**
 * This is an example of a custom landing page folder jsp 
 * 
 * These beans are set up as request attributes:
	 *   ss_mashupBinderEntries - Map<String, List<Map>> indexed by binderId
	 *     The List contains a list of Maps, one for each entry as returned by the search function
	 *   ss_mashupEntryReplies - Map<String, Map> indexed by entryId
	 *     ss_mashupEntryReplies[entryId][folderEntryDescendants] is a list of reply objects
	 *     ss_mashupEntryReplies[entryId][folderEntryAncestors] is a list of parent entry objects
 */
%>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%  
	Long ss_mashupTableNumber = (Long) request.getAttribute("ss_mashupTableNumber");
	Long ss_mashupTableDepth = (Long) request.getAttribute("ss_mashupTableDepth");
	Map ss_mashupTableItemCount = (Map) request.getAttribute("ss_mashupTableItemCount");
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "folder");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<%@ page import="java.util.TreeMap" %>
<%@ page import="org.kablink.teaming.comparator.StringComparator" %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:set var="mashupBinderId" value="${mashup_attributes['folderId']}"/>
<c:set var="mashupBinder" value="${ss_mashupBinders[mashupBinderId]}"/>
<c:if test="${!empty mashup_attributes['zoneUUID']}">
  <c:set var="zoneBinderId" value="${mashup_attributes['zoneUUID']}.${mashup_attributes['folderId']}" />
  <c:if test="${!empty ss_mashupBinders[zoneBinderId]}">
    <c:set var="mashupBinder" value="${ss_mashupBinders[zoneBinderId]}"/>
    <c:set var="mashupBinderId" value="${mashupBinder.id}"/>
  </c:if>
</c:if>

<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupBinder}">
<li>
</c:if>
<% } %>
<div class="ss_mashup_element">
    <div class="ss_mashup_round_top"><div></div></div>
      <c:if test="${!empty mashup_attributes['showTitle']}">
		<div class="ss_mashup_folder_header_view">
			<c:set var="anchorPermalink"><ssf:url crawlable="true" adapter="true" portletName="ss_forum"
																						action="view_permalink"
																						binderId="${mashupBinder.id}">
				<ssf:param name="entityType" value="${mashupBinder.entityType}"/>
				<ssf:param name="seen_by_gwt" value="1" />
			</ssf:url></c:set>
			<a href="${anchorPermalink}" onclick="ss_setContentLocation('${anchorPermalink}'); return false;">
				<span>${mashupBinder.title}</span>
			</a>
	
			<c:if test="${!empty mashupBinder.description.text}">
			  <div class="ss_mashup_folder_description">
				<ssf:markup entity="${mashupBinder}">${mashupBinder.description.text}</ssf:markup>
				<div class="ss_clear"></div>
			  </div>
			</c:if>

		</div>
      </c:if>
	
	<div class="ss_mashup_folder_list_open">
<% 
	TreeMap tm = new java.util.TreeMap(new StringComparator(ssUser.getLocale()));
%>
	<c:forEach var="entryMap" items="${ss_mashupBinderEntries[mashupBinderId]}" varStatus="status">
	  <c:set var="mashupEntryId" value="${entryMap['_docId']}"/>
	  <c:set var="mashupEntryTitle" value="${entryMap['title']}"/>
	  <c:set var="mashupEntry" value="${ss_mashupEntries[mashupEntryId]}"/>
	  <jsp:useBean id="entryMap" type="java.util.Map" />
	  <jsp:useBean id="mashupEntryTitle" type="java.lang.String" />
	  <c:forEach var="selection" items="${mashupEntry.fileAttachments}" >
		<jsp:useBean id="selection" type="org.kablink.teaming.domain.FileAttachment" />
		<%
			tm.put(selection.getFileItem().getName(), selection);
		%>
	  </c:forEach>
	</c:forEach>
	<c:forEach var="selectionMe" items="<%= tm %>" varStatus="status">

	    	<div style="padding-bottom:6px;padding-left:10px;">
		  <a href="<ssf:fileUrl file="${selectionMe.value}"/>"><span>${selectionMe.value.fileItem.name}</span></a>
		</div>
	</c:forEach>
	</div>
  <div class="ss_mashup_round_bottom"><div></div></div>
</div>
<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupBinder}">
</li>
</c:if>
<% } %>
