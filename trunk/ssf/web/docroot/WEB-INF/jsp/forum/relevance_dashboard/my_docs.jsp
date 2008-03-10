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

<c:if test="${empty ss_myDocs}">
<span><ssf:nlt tag="relevance.docs.none"/></span>
</c:if>
<c:if test="${!empty ss_myDocs}">
<ul>
  <c:forEach var="entry" items="${ss_myDocs}">
    <jsp:useBean id="entry" type="java.util.Map" />
    <li>
	 
	  <span class="ss_link_2">
		<c:set var="isDashboard" value="yes"/>
		<ssf:titleLink 
			entryId="${entry._docId}" binderId="${entry._binderId}" 
			entityType="${entry._entityType}" 
			namespace="${ss_namespace}" 
			isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
			<ssf:param name="url" useBody="true">
				<ssf:url adapter="true" portletName="ss_forum" folderId="${entry._binderId}" 
				  action="view_folder_entry" entryId="${entry._docId}" actionUrl="true" />
			</ssf:param>
			<c:out value="${entry.title}" escapeXml="false"/>
		</ssf:titleLink>
	 
	  </span><br/>
	  <span class="ss_link_1">
	  
		<ssf:showUser user="<%=(com.sitescape.team.domain.User)entry.get("_principal")%>" /> 
	  
	  </span>
	  <span class="ss_link_2">
	  
		<c:set var="path" value=""/>

		<c:if test="${!empty ss_myDocsFolders[entry._binderId]}">
			<c:set var="path" value="${ss_myDocsFolders[entry._binderId]}"/>
			<c:set var="title" value="${ss_myDocsFolders[entry._binderId].title} (${ss_myDocsFolders[entry._binderId].parentBinder.title})"/>
		</c:if>
		<c:set var="isDashboard" value="yes"/>
		<c:if test="${!empty path}">
    		<br/><a href="javascript: ;"
				onClick="return ss_gotoPermalink('${entry._binderId}', '${entry._binderId}', 'folder', '${ss_namespace}', 'yes');"
				title="${path}"
				><span>${title}</span></a>
		</c:if>
	  
	  </span>
	  <c:if test="${!empty entry._desc}">
	    <br/>
	    <span class="ss_summary"><ssf:textFormat 
	      formatAction="limitedDescription" 
	      textMaxWords="10">${entry._desc}</ssf:textFormat></span>
	  </c:if>
	
    </li>
  </c:forEach>
</ul>
</c:if>
