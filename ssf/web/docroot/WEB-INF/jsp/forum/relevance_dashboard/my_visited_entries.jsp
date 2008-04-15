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
<%@ page import="com.sitescape.team.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<div id="ss_para">
<div id="ss_viewedItems">
<div id="ss_nextPage" align="right">
<c:if test="${ssEntriesViewedPage > '0'}">
<a href="javascript: ;" 
  onClick="ss_showDashboardPage('${ssBinder.id}', 'entriesViewed', '${ssEntriesViewedPage}', 'previous', 'ss_dashboardEntriesViewed${renderResponse.namespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
  title="<ssf:nlt tag="general.previousPage"/>"/>
</a>
</c:if>
<c:if test="${empty ssEntriesViewedPage || ssEntriesViewedPage <= '0'}">
<img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"/>
</c:if>
<c:if test="${!empty ssEntriesViewed}">
<a href="javascript: ;" 
  onClick="ss_showDashboardPage('${ssBinder.id}', 'entriesViewed', '${ssEntriesViewedPage}', 'next', 'ss_dashboardEntriesViewed${renderResponse.namespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_right_.gif"
  title="<ssf:nlt tag="general.nextPage"/>"/>
</a>
</c:if>
<c:if test="${empty ssEntriesViewed}">
<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"/>
</c:if>
</div>
<c:set var="count" value="0"/>
  <c:forEach var="entryMap" items="${ssEntriesViewed}">
    <c:if test="${entryMap.type == 'view'}">
    <c:set var="entry" value="${entryMap.entity}"/>
    <jsp:useBean id="entry" type="com.sitescape.team.domain.Entry" />
    
    <div class="ss_v_entries">
    <ul>
    <li>
		<c:set var="isDashboard" value="yes"/>
		<ssf:titleLink hrefClass="ss_link_2"
			entryId="${entry.id}" binderId="${entry.parentBinder.id}" 
			entityType="${entry.entityType}" 
			namespace="${ss_namespace}" 
			isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
			<ssf:param name="url" useBody="true">
				<ssf:url adapter="true" portletName="ss_forum" folderId="${entry.parentBinder.id}" 
				  action="view_folder_entry" entryId="${entry.id}" actionUrl="true" />
			</ssf:param>
			<c:out value="${entry.title}" escapeXml="false"/>
		</ssf:titleLink>
	  
	  <br/>
	  <span>
	    <ssf:showUser user="${entry.creation.principal}" titleStyle="ss_link_1"/>
	  </span>
	  <span class="ss_link_4">
	    <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      value="${entry.modification.date}" type="both" 
	  timeStyle="short" dateStyle="medium" />
	  </span>
	   
	  <br/>
	  <span class="ss_link_2">
    	<a href="javascript: ;"
			onClick="return ss_gotoPermalink('${entry.parentBinder.id}', '${entry.parentBinder.id}', 'folder', '${ss_namespace}', 'yes');"
			><span>${entry.parentBinder.title} (${entry.parentBinder.parentBinder.title})</span></a>
	  </span>&nbsp;<img src="<html:rootPath/>images/icons/folder_cyan_sm.png" alt="folder" width="11" height="10" hspace="2" border="0" align="absmiddle" /> 
	  <c:if test="${!empty entry.description}">
	    <br/>
	    <span class="ss_summary"><ssf:textFormat 
	      formatAction="limitedDescription" 
	      textMaxWords="10">${entry.description}</ssf:textFormat></span>
	  </c:if>
	<c:set var="count" value="${count + 1}"/>
	</li>
	</ul>
	</div><!-- end of viewed entries -->
	
    </c:if>

    <c:if test="${entryMap.type == 'download'}">
    <c:set var="entry2" value="${entryMap.entity}"/>
    <jsp:useBean id="entry2" type="com.sitescape.team.domain.Entry" />

    
	<div id="ss_viewedItems" class="ss_v_attachments">
	<ul>
    <li>
	  
	  <span class="ss_link_3">
	  	<a target="_blank" href="<ssf:url 
					    webPath="viewFile"
					    folderId="${entry2.parentBinder.id}"
					    entryId="${entry2.id}"
					    entityType="${entry2.entityType}" >
					    <ssf:param name="fileId" value="${entryMap.file_id}"/>
					    </ssf:url>">${entryMap.description}</a>
	  </span>
	  <br/>
	  <span>
	    <ssf:showUser user="${entry2.creation.principal}" titleStyle="ss_link_1"/>
	  </span>
	  <span class="ss_link_4">
	    <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      value="${entry2.modification.date}" type="both" 
	  timeStyle="short" dateStyle="medium" />
	  </span>
	  <br/>
		<c:set var="isDashboard" value="yes"/>
		<span class="ss_link_2">
		<ssf:titleLink hrefClass="ss_link_2"
			entryId="${entry2.id}" binderId="${entry2.parentBinder.id}" 
			entityType="${entry2.entityType}" 
			namespace="${ss_namespace}" 
			isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
			<ssf:param name="url" useBody="true">
				<ssf:url adapter="true" portletName="ss_forum" folderId="${entry2.parentBinder.id}" 
				  action="view_folder_entry" entryId="${entry2.id}" actionUrl="true" />
			</ssf:param>
			<c:out value="${entry2.title}" escapeXml="false"/>
		</ssf:titleLink>
		</span>
	  
	  <br/>
	  <span class="ss_link_2">
    	<a href="javascript: ;"
			onClick="return ss_gotoPermalink('${entry2.parentBinder.id}', '${entry2.parentBinder.id}', 'folder', '${ss_namespace}', 'yes');"
			><span>${entry2.parentBinder.title} (${entry2.parentBinder.parentBinder.title})</span></a>
	  </span>&nbsp;<img src="<html:rootPath/>images/icons/folder_cyan_sm.png" alt="folder" width="11" height="10" hspace="2" border="0" align="absmiddle" /> 
	  <c:if test="${!empty entry2.description}">
	    <br/>
	    <span class="ss_summary"><ssf:textFormat 
	      formatAction="limitedDescription" 
	      textMaxWords="10">${entry2.description}</ssf:textFormat></span>
	  </c:if>
	
	<c:set var="count" value="${count + 1}"/>
	</li>
	</ul>
	</div><!-- end of viewed attachments -->
	

    </c:if>
  </c:forEach>
</div> <!-- end of viewed items -->
</div> <!-- end of ss_para -->
