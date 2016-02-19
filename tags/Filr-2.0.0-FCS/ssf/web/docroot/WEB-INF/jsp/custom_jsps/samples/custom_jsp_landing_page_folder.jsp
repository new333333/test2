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
<c:set var="mashupBinderId" value="${mashup_attributes['folderId']}"/>
<c:set var="mashupBinder" value="${ss_mashupBinders[mashupBinderId]}"/>

<div class="ss_mashup_element">
    <div class="ss_mashup_round_top"><div></div></div>
	<div class="ss_mashup_folder_header_view">
		<a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum" 
		  action="view_permalink" 
		  binderId="${mashupBinder.id}">
		  <ssf:param name="entityType" value="${mashupBinder.entityType}"/>
		  </ssf:url>"><span>${mashupBinder.title}</span></a>

		<c:if test="${!empty mashupBinder.description.text}">
		  <div class="ss_mashup_folder_description">
			<ssf:markup entity="${mashupBinder}">${mashupBinder.description.text}</ssf:markup>
			<div class="ss_clear"></div>
		  </div>
		</c:if>

	</div>

	<c:forEach var="entryMap" items="${ss_mashupBinderEntries[mashupBinderId]}" varStatus="status">
		<c:set var="mashupEntryId" value="${entryMap['_docId']}"/>
		<c:set var="mashupEntry" value="${ss_mashupEntries[mashupEntryId]}"/>
		<c:set var="mashupEntryReplies" value="${ss_mashupEntryReplies[mashupEntryId]}"/>
	    <c:if test="${empty mashup_attributes['entriesToShow'] || status.count <= mashup_attributes['entriesToShow']}">

<jsp:useBean id="mashupEntry" type="org.kablink.teaming.domain.DefinableEntity" />
<% 
	//See if this is a survey entry. If it is, just show the survey and not the replies
	Element configEle = (Element)mashupEntry.getEntryDefDoc().getRootElement().selectSingleNode("//item[@name='entryDataItem' and @formItem='survey']");
%>
<c:set var="configEle" value="<%= configEle %>" />

	      <div class="ss_mashup_folder_list_open">
			<div class="ss_mashup_folder_list_open_title">
		  	  <a href="<ssf:url crawlable="true" 
		  	    adapter="true" portletName="ss_forum"    
		        action="view_permalink" 
		        binderId="${entryMap._binderId}"
		        entryId="${entryMap._docId}"
		      ><ssf:param name="entityType" value="folderEntry"/>
		      </ssf:url>"><span>${entryMap.title}</span></a>
			</div>
		    <c:if test="${empty configEle}">
                  <div style="padding-left:6px;" class="ss_smallprint">
		        <span><ssf:showUser user="${mashupEntry.creation.principal}"/></span>
		        <span style="padding-left:10px;"><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     			  value="${mashupEntry.modification.date}" type="both" 
	 			timeStyle="short" dateStyle="medium" /></span>
		      </div>
                </c:if>
			<div class="ss_mashup_folder_list_open_entry">
			  <c:if test="${!empty entryMap._desc}">
			    <ssf:markup search="${entryMap}">${entryMap._desc}</ssf:markup>
			    <div class="ss_clear"></div>
			  </c:if>

			<c:if test="${!empty configEle}">
			  <c:set var="originalDefinitionEntry" value="${ssDefinitionEntry}"/>
			  <c:set var="originalBinder" value="${ssBinder}"/>
			  <c:set var="originalEntry" value="${ssEntry}"/>
			  <c:set var="ssDefinitionEntry" value="${mashupEntry}" scope="request"/>
			  <c:set var="ssBinder" value="${mashupEntry.parentFolder}" scope="request"/>
			  <c:set var="ssEntry" value="${mashupEntry}" scope="request"/>
			  <c:set var="property_name" value="survey" scope="request"/>
			  <c:set var="property_caption" value="" scope="request"/>
			  <ssf:displayConfiguration 
			    configDefinition="${mashupEntry.entryDefDoc}" 
			    configElement="<%= configEle %>"
			    configJspStyle="view" 
			    entry="${mashupEntry}" 
			    processThisItem="true" />
			  <c:set var="ssDefinitionEntry" value="${originalDefinitionEntry}" scope="request"/>
			  <c:set var="ssBinder" value="${originalBinder}" scope="request"/>
			  <c:set var="ssEntry" value="${originalEntry}" scope="request"/>
			</c:if>
		  
			<c:if test="${empty configEle}">
			  <c:forEach var="reply" items="${mashupEntryReplies['folderEntryDescendants']}" varStatus="status2">
	    	   <c:if test="${status2.count <= 10}">
				<div style="padding-left:20px;">
				  <div class="ss_mashup_folder_list_open_title">
				    <a href="<ssf:url crawlable="true" adapter="true" portletName="ss_forum"    
				      action="view_permalink" 
				      binderId="${reply.parentFolder.id}"
				      entryId="${reply.id}"
				      ><ssf:param name="entityType" value="folderEntry"/>
				      </ssf:url>"><span>${reply.title}</span></a>
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
			</c:if>
			  
			</div>
		  </div>
		  
		</c:if>
	</c:forEach>
  <div class="ss_mashup_round_bottom"><div></div></div>
</div>