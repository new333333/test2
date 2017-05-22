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
<% //View the listing part of a wiki folder %>
<%@ page import="java.util.Date" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="selectedSortBy" value="${ssUserFolderPropertyObj.properties.sortBy}"/>

<c:set var="topWikiFolder" value="${ssBinder}"/>
<c:forEach var="blogPage" items="${ssBlogPages}">
  <c:set var="blogPageParentFound" value="false"/>
  <c:forEach var="blogPage2" items="${ssBlogPages}">
    <c:if test="${blogPage.parentBinder == blogPage2}">
      <c:set var="blogPageParentFound" value="true"/>
    </c:if>
  </c:forEach>
  <c:if test="${!blogPageParentFound}">
    <c:set var="topWikiFolder" value="${blogPage}"/>
  </c:if>
</c:forEach>

<c:set var="ss_wikiEntryBeingShown" value="${ss_wikiHomepageEntry}" scope="request"/>
<c:set var="ss_wikiCurrentTab" value="list" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_tabs.jsp" %>

<div class="ss_wiki_folder">
   
	<ssHelpSpot helpId="workspaces_folders/misc_tools/wiki_controls" offsetX="-14" offsetY="8" 
	   			title="<ssf:nlt tag="helpSpot.wikiControls"/>">
	</ssHelpSpot>
	
	<div class="wiki-content">
		<ssf:toolbar toolbar="${ssEntryToolbar}" format="wiki" style="ss_actions_bar13 ss_actions_bar" />
	</div>
	
	<div align="right">
		<jsp:include page="/WEB-INF/jsp/forum/add_files_to_folder.jsp" />
	</div>
	
	<div class="wiki-content">
	  <div class="wiki-topics">
		<span class="ss_nowrap ss_bold"><ssf:nlt tag="wiki.topics"/></span>
		<a class="<c:if test="${topWikiFolder.id == ssBinder.id}"> ss_navbar_current</c:if>" 
		  href="<ssf:url 
		    action="view_folder_listing" 
		    binderId="${topWikiFolder.id}"
		    ><ssf:param name="wiki_folder_list" value="1"/></ssf:url>"
	     ><c:out value="${topWikiFolder.title}" escapeXml="true" /></a>

		 <c:forEach var="blogPage" items="${ssBlogPages}">
		   <c:if test="${topWikiFolder != blogPage}">
			   <a class="wiki-topic-a <c:if test="${blogPage.id == ssBinder.id}">wiki-topic-selected</c:if>" 
				  href="<ssf:url 
				  			action="view_folder_listing" 
				  			binderId="${blogPage.id}"
				  		><ssf:param name="wiki_folder_list" value="1"/></ssf:url>"
			   ><c:out value="${blogPage.title}" escapeXml="true" /></a>
		   </c:if>
		 </c:forEach>
	  </div>

	  <div class="wiki-topic-content margintop2">
		<%@ include file="/WEB-INF/jsp/definition_elements/wiki/wiki_folder_page.jsp" %>
	  </div>
		

    <c:if test="${0 == 1}">
      <c:if test="${!empty ssFolderEntryCommunityTags}"> 
		<div class="ss_wiki_sidebar_subhead"><ssf:nlt tag="tags.community"/></div>
	    <div class="ss_wiki_sidebar_box">		
			 <c:if test="${!empty ssFolderEntryCommunityTags}">
			   <c:forEach var="tag" items="${ssFolderEntryCommunityTags}">
				   	<a href="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
						name="binderId" value="${ssBinder.id}"/><ssf:param 
						name="cTag" value="${tag.ssTag}"/></ssf:url>" 
						class="ss_displaytag  ${tag.searchResultsRatingCSS} 
						<c:if test="${!empty cTag && cTag == tag.ssTag}">ss_bold</c:if>
						<c:if test="${empty cTag || cTag != tag.ssTag}">ss_normal</c:if>"
						  <ssf:title tag="title.search.entries.in.folder.for.community.tag">
						  	<ssf:param name="value" value="${tag.ssTag}" />
						  </ssf:title>
						>${tag.ssTag}</a>&nbsp;&nbsp;
			   </c:forEach>
			 </c:if>
	    </div>
      </c:if>
      <c:if test="${!empty ssFolderEntryPersonalTags}"> 
		<div class="ss_wiki_sidebar_subhead"><ssf:nlt tag="tags.personal"/></div>
	    <div class="ss_wiki_sidebar_box">		
			<c:if test="${!empty ssFolderEntryPersonalTags}">
			  <c:forEach var="tag" items="${ssFolderEntryPersonalTags}">
			   	<a href="<ssf:url action="view_folder_listing" actionUrl="true"><ssf:param 
					name="binderId" value="${ssBinder.id}"/><ssf:param 
					name="pTag" value="${tag.ssTag}"/></ssf:url>" 
					class="ss_displaytag  ${tag.searchResultsRatingCSS} 
					<c:if test="${!empty pTag && pTag == tag.ssTag}">ss_bold</c:if>
					<c:if test="${empty pTag || pTag != tag.ssTag}">ss_normal</c:if>"
					  <ssf:title tag="title.search.entries.in.folder.for.personal.tag">
					  	<ssf:param name="value" value="${tag.ssTag}" />
					  </ssf:title>
					>${tag.ssTag}</a>&nbsp;&nbsp;
							
			  </c:forEach>
			</c:if>
	    </div>		
	  </c:if>
	</c:if>
		
</div>
