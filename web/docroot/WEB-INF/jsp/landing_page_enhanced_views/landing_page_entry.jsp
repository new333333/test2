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
 * This is an example of a custom jsp for showing an entry on a landing page
 * 
 * The following special beans are set up for use in this jsp:
 *   mashup_attributes['entryId'] - the entry to be displayed
 *   ss_mashupEntries - Map<String, Entry> indexed by entryId
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
	ss_mashupTableItemCount.put(ss_mashupTableNumber, "entry");  
	request.setAttribute("ss_mashupTableItemCount", ss_mashupTableItemCount);

	Long ss_mashupListDepth = (Long) request.getAttribute("ss_mashupListDepth");
%>
<c:set var="mashupEntryId" value="${mashup_attributes['entryId']}"/>
<c:set var="mashupEntry" value="${ss_mashupEntries[mashupEntryId]}"/>
<c:if test="${!empty mashup_attributes['zoneUUID']}">
  <c:set var="zoneEntryId" value="${mashup_attributes['zoneUUID']}.${mashup_attributes['entryId']}" />
  <c:if test="${!empty ss_mashupEntries[zoneEntryId]}">
    <c:set var="mashupEntry" value="${ss_mashupEntries[zoneEntryId]}"/>
	<c:set var="mashupEntryId" value="${mashupEntry.id}"/>
  </c:if>
</c:if>
<c:set var="mashupEntryReplies" value="${ss_mashupEntryReplies[mashupEntryId]}"/>

<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupEntry}">
<li>
</c:if>
<% } %>

<div class="ss_mashup_element">
  <div class="ss_mashup_round_top"><div></div></div>
  <div class="ss_mashup_folder_list_open">
    <div >

		<ssf:titleLink action="view_permalink" entryId="$mashupEntry.id}" 
			binderId="${mashupEntry.parentFolder.id}" entityType="folderEntry"
                        seenStyle="class='ss_size_20px ss_bold'"
		>
			<ssf:param name="url" useBody="true">
				<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
					action="view_permalink" 
					binderId="${mashupEntry.parentFolder.id}"
					entryId="${mashupEntry.id}">
					<ssf:param name="entityType" value="folderEntry"/>
					<ssf:param name="seen_by_gwt" value="1" />
				</ssf:url>
			</ssf:param>
			<c:out value="${mashupEntry.title}"/>
		</ssf:titleLink>

      <a href="<ssf:url crawlable="true" 
		  	    adapter="true" portletName="ss_forum"    
		        action="view_permalink" 
		        binderId="${mashupEntry.parentFolder.id}"
		        entryId="${mashupEntry.id}"
		      ><ssf:param name="entityType" value="folderEntry"/>
		      </ssf:url>"><span class="ss_size_20px ss_bold">${mashupEntry.title}</span></a>
    </div>
	<div class="ss_mashup_entry_content ss_smallprint">
	  <span><ssf:showUser user="${mashupEntry.modification.principal}"/><span>
	  <span style="padding-left:10px;"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
	   	value="${mashupEntry.modification.date}" type="both" 
		timeStyle="short" dateStyle="medium" /></span>
	</div>
  
	  
    <div class="ss_mashup_entry_content">
	  <c:set var="textFormat" value=""/>
	  <c:if test="${!empty mashupEntry.description.format}">
		  <c:set var="textFormat" value="${mashupEntry.description.format}"/>
	  </c:if>
	  <ssf:editable entity="${mashupEntry}" element="description">
		<c:if test="${textFormat == '2'}">
		  <ssf:textFormat formatAction="textToHtml">${mashupEntry.description.text}</ssf:textFormat>
		</c:if>
		<c:if test="${textFormat != '2'}">
		  <div>
			<ssf:markup entity="${mashupEntry}" leaveSectionsUnchanged="true" 
			>${mashupEntry.description.text}</ssf:markup>
		  </div>
		</c:if>
		<div class="ss_clear"></div>
	  </ssf:editable>
    </div>

    <c:forEach var="reply" items="${mashupEntryReplies['folderEntryDescendants']}" varStatus="status2">
      <c:if test="${status2.count <= 10}">
        <div style="padding-left:20px;">
          <div class="ss_mashup_folder_list_open_title">

		<ssf:titleLink action="view_permalink" entryId="$reply.id}" 
			binderId="${reply.parentFolder.id}" entityType="folderEntry"
		>
			<ssf:param name="url" useBody="true">
				<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
					action="view_permalink" 
					binderId="${reply.parentFolder.id}"
					entryId="${reply.id}">
					<ssf:param name="entityType" value="folderEntry"/>
					<ssf:param name="seen_by_gwt" value="1" />
				</ssf:url>
			</ssf:param>
			<c:out value="${reply.title}"/>
		</ssf:titleLink>

          </div>
	      <div style="padding-left:6px;" class="ss_smallprint">
	        <span><ssf:showUser user="${reply.modification.principal}"/></span>
	        <span style="padding-left:10px;"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
    			value="${reply.modification.date}" type="both" 
 			    timeStyle="short" dateStyle="medium" /></span>
	      </div>
          <div class="ss_mashup_entry_content">
            <ssf:markup entity="${reply}">${reply.description.text}</ssf:markup>
            <div class="ss_clear"></div>
          </div>
        </div>
      </c:if>
    </c:forEach>
  </div>
  <div class="ss_mashup_round_bottom"><div></div></div>
</div>

<% if (ss_mashupListDepth > 0) { %>
<c:if test="${!empty mashupEntry}">
</li>
</c:if>
<% } %>
