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
<div id="ss_hot_para">
<div id="ss_nextPage" align="right">
<c:if test="${ss_whatsHotPage > '0'}">
<a href="javascript: ;" 
  onClick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'hot', '${ss_whatsHotPage}', 'previous', 'ss_dashboardWhatsHot', '${ss_relevanceDashboardNamespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
  title="<ssf:nlt tag="general.previousPage"/>"/>
</a>
</c:if>
<c:if test="${empty ss_whatsHotPage || ss_whatsHotPage <= '0'}">
<img src="<html:imagesPath/>pics/sym_arrow_left_g.gif"/>
</c:if>
<c:if test="${!empty ss_whatsHot}">
<a href="javascript: ;" 
  onClick="ss_showDashboardPage('${ssBinder.id}', '${ssRDCurrentTab}', 'hot', '${ss_whatsHotPage}', 'next', 'ss_dashboardWhatsHot', '${ss_relevanceDashboardNamespace}');return false;">
<img src="<html:imagesPath/>pics/sym_arrow_right_.gif"
  title="<ssf:nlt tag="general.nextPage"/>"/>
</a>
</c:if>
<c:if test="${empty ss_whatsHot}">
<img src="<html:imagesPath/>pics/sym_arrow_right_g.gif"/>
</c:if>
</div>
<div id="ss_hints"><em><ssf:nlt tag="relevance.hint.hotItems"/></em></div>
  <c:forEach var="entry" items="${ss_whatsHot}">
    <jsp:useBean id="entry" type="com.sitescape.team.domain.Entry" />
    <li>
	  <span>
		<c:set var="isDashboard" value="yes"/>
		<ssf:titleLink hrefClass="ss_link_3"
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
	  </span>
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
	  </span>&nbsp;<images/icons/folder_cyan_sm.png" alt="folder" width="11" height="10" hspace="2" border="0" align="absmiddle" />
	  <c:if test="${!empty entry.description}">
	    <br/>
	    <span class="ss_summary"><ssf:textFormat 
	      formatAction="limitedDescription" 
	      textMaxWords="10"><ssf:markup  entity="${entry}" type="view">${entry.description}</ssf:markup></ssf:textFormat></span>
	  </c:if>
	
    </li><br/>
  </c:forEach>
  <c:if test="${empty ss_whatsHot && ss_pageNumber > '0'}">
    <span class="ss_italic"><ssf:nlt tag="whatsnew.noMoreEntriesFound"/></span>
  </c:if>
</div><!-- end of ss_hot_para -->
</div><!-- end of ss_para -->
