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
<c:if test="${empty ss_sharedEntities}">
<span><ssf:nlt tag="relevance.none"/></span>
</c:if>
<c:if test="${!empty ss_sharedEntities}">
<div id="ss_subtitle">Do we need a subtitle?</div>
<div id="ss_para" class="ss_pt_para">

  <c:forEach var="sharedItem" items="${ss_sharedEntities}">
   
	  <ssf:nlt tag="relevance.sharedEntityLine">
	  <ssf:param name="value" useBody="true">
	    <span class="ss_link_1">
	    <ssf:showUser user="${sharedItem.referer}" titleStyle="ss_link_1" />
	    </span>
	  </ssf:param>
	  <span class="ss_link_2">
	  <ssf:param name="value" useBody="true">
    	<c:if test="${sharedItem.entity.entityType == 'workspace' || sharedItem.entity.entityType == 'folder'}">
    	  <a href="javascript: ;"
			onClick="return ss_gotoPermalink('${sharedItem.entity.id}', '${sharedItem.entity.id}', '${sharedItem.entity.entityType}', '${ss_namespace}', 'yes');"
			><span>${sharedItem.entity.title}</span></a>
		</c:if>
		<c:if test="${sharedItem.entity.entityType == 'folderEntry'}">
		  <span class="ss_link_2">
			<c:set var="isDashboard" value="yes"/>
			<ssf:titleLink hrefClass="ss_link_2"
				entryId="${sharedItem.entity.id}" binderId="${sharedItem.entity.parentBinder.id}" 
				entityType="${sharedItem.entity.entityType}" 
				namespace="${ss_namespace}" 
				isDashboard="${isDashboard}" dashboardType="${ssDashboard.scope}">
				<ssf:param name="url" useBody="true">
					<ssf:url adapter="true" portletName="ss_forum" folderId="${sharedItem.entity.parentBinder.id}" 
					  action="view_folder_entry" entryId="${sharedItem.entity.id}" actionUrl="true" />
				</ssf:param>
				<c:out value="${sharedItem.entity.title}" escapeXml="false"/>
			</ssf:titleLink>
		  </span>
		</c:if>
	  </ssf:param>
	  </ssf:nlt>
   <br/>
  </c:forEach>

</div>
</c:if>
