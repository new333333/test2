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
<% //View an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view_init.jsp" />
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:set var="ss_popular_view_seen" value="true" scope="request"/>
<c:set var="ss_entry_view_style" value="wiki" scope="request"/>
<c:set var="ss_commentsAndAttachmentsSectionRequested" value="false" scope="request"/>
<c:set var="ss_delayShowingCommentsAndAttachments" value="true" scope="request"/>
<c:if test="${!ss_pseudoEntity}">
  <c:set var="ss_seenHistoryTab" value="" scope="request"/>
  <c:set var="ss_pseudoEntity" value="" scope="request"/>
  <c:set var="ss_pseudoEntityRevert" value="" scope="request"/>
</c:if>
<c:set var="ss_seenTagView" value="false" scope="request" />
<c:set var="binderDefinition" value="${ssBinder.entryDef.definition}" />
<jsp:useBean id="binderDefinition" type="org.dom4j.Document" />
<%
	//Get a dispalyable number for the replies
	String docNumber = "";
	String fontSize = "ss_largeprint";
	String entryType = "reply";
	boolean seen = true;
	
	java.lang.Object thisEntry = (java.lang.Object) request.getAttribute("ssDefinitionEntry");
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
   <c:set var="modificationPrincipal" value="<%= ((FolderEntry)thisEntry).getModification().getPrincipal() %>"/>
   <c:set var="modificationDate" value="<%= ((FolderEntry)thisEntry).getModification().getDate() %>"/>
   <c:if test="<%= ((FolderEntry)thisEntry).getReservation() != null %>">
     <c:set var="reservationPrincipal" value="<%= ((FolderEntry)thisEntry).getReservation().getPrincipal() %>"/>
   </c:if>
   <c:set var="totalReplyCount" value="<%= ((FolderEntry)thisEntry).getTotalReplyCount() %>"/>
   <c:if test="${empty totalReplyCount}"><c:set var="totalReplyCount" value="0"/></c:if>
   <c:set var="attachmentCount" value="<%= ((FolderEntry)thisEntry).getTotalReplyCount() %>"/>
   <c:if test="${empty totalReplyCount}"><c:set var="totalReplyCount" value="0"/></c:if>

<c:if test="${entryType == 'entry'}">
  <c:set var="ss_hideEntrySignature" value="true" scope="request"/>
  <c:set var="ss_hideEntrySignature" value="false" scope="request"/>
</c:if>

<div class="wiki-entry-wrap">

<% // Show the wiki tabs %>
<% // Show the Wiki Page search widget %>
<c:set var="ss_showHelpIcon" value="true" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_find_page.jsp" %>

<% // Show the folder title links %>
<c:if test="${!ss_binderTitleShown}">
  <c:set var="ss_savedEntryDefinition" value="${ssDefinitionEntry}"/>
  <c:set var="ssDefinitionEntry" value="${ssDefinitionEntry.parentBinder}" scope="request"/>
  <jsp:include page="/WEB-INF/jsp/definition_elements/view_binder_title_folder.jsp" />
  <c:set var="ssDefinitionEntry" value="${ss_savedEntryDefinition}" scope="request"/>
  <div class="ss_clear"></div>
</c:if>

<c:set var="ss_savedEntryDefinition" value="${ssDefinitionEntry}"/>
<c:set var="ss_commentsAndAttachmentsReplyCount" value="${totalReplyCount}" scope="request"/>
<c:if test="${empty ssPrimaryFileAttribute}">
  <c:set var="ss_commentsAndAttachmentsAttachmentCount" value="${fn:length(ssDefinitionEntry.fileAttachments)}" scope="request"/>
</c:if>
<c:if test="${!empty ssPrimaryFileAttribute && !empty ssDefinitionEntry.fileAttachments}">
  <c:set var="ss_commentsAndAttachmentsAttachmentCount" value="${fn:length(ssDefinitionEntry.fileAttachments) - 1}" scope="request"/>
</c:if>
<c:set var="ss_wikiEntryBeingShown" value="${ssDefinitionEntry}" scope="request"/>
<c:set var="ss_wikiCurrentTab" value="page" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_tabs.jsp" %>

<c:choose>
  <c:when test="${propertyValues_type[0] == 'file'}">
	<jsp:include page="/WEB-INF/jsp/definition_elements/file/file_view.jsp" />
    <jsp:include page="/WEB-INF/jsp/definition_elements/entry_view2.jsp" />
  </c:when>
  <c:otherwise>
    <jsp:include page="/WEB-INF/jsp/definition_elements/entry_view2.jsp" />
  </c:otherwise>
</c:choose>
<c:set var="ssDefinitionEntry" value="${ss_savedEntryDefinition}" scope="request"/>

<c:if test="${entryType == 'entry'}">
<c:set var="ss_popular_view_seen" value="false" scope="request"/>
	<table id="wiki-footer" style="position: relative;" width="100%" border="0" cellpadding="0" cellspacing="0" 
	  class="ws-nw margintop2 nv-footer-wiki" style="display: block; ">
	  <tr>
		<td>
		  <span class="wiki-entry-date"><ssf:nlt tag="wiki.author"/></span>
		</td>
		<td>
		  <ssf:showUser user="${creationPrincipal}"/>
		  <span class="wiki-entry-date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	     	value="${creationDate}" type="both" 
		 	dateStyle="medium" timeStyle="short" /> </span>
		</td>
		<td align="right" width="100%">
		  <%-- Subscribe, Ratings bar, visits --%>
		  <c:if test="${objectType == 'FolderEntry' && entryType == 'entry'}">
			<div style="padding-left: 22px">
			<jsp:include page="/WEB-INF/jsp/definition_elements/popular_view.jsp" />
			</div>
	
			<c:set var="entryIdString" value="${docId}"/>
		  </c:if>
		</td>
	  </tr>
	  
	  <c:if test="${!empty modificationPrincipal && modificationDate > creationDate}">
	  <tr>
		<td>
		  <span class="wiki-entry-date"><ssf:nlt tag="wiki.modified"/></span>
		</td>
		<td>
		  <ssf:showUser user="${modificationPrincipal}"/>
		  <span class="wiki-entry-date"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	     	value="${modificationDate}" type="both" 
		 	dateStyle="medium" timeStyle="short" /> </span>
		</td>
		<td align="right">&nbsp;</td>
	  </tr>
	  </c:if>

      <c:if test="${!empty reservationPrincipal}">
	  <tr>
		<td>
		  <img style="margin: 7px 3px 7px 0px;" <ssf:alt tag="alt.locked"/> align="absmiddle" 
		  	    src="<html:imagesPath/>pics/sym_s_caution.gif"/>
		  <span class="wiki-entry-date"><ssf:nlt tag="wiki.locked"/></span>
		</td>
		<td>
		  <ssf:showUser user="${reservationPrincipal}"/>
		</td>
		<td align="right">&nbsp;</td>
	  </tr>
      </c:if>

	</table>
    <jsp:include page="/WEB-INF/jsp/definition_elements/wiki/wiki_tabs2.jsp" />
</c:if>
</div>
