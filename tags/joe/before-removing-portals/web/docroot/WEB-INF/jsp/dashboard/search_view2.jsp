<%
// The dashboard "search" component
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
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="ss_pageSize" value="${ssDashboard.beans[componentId].ssSearchFormData.ss_pageSize}" />
<c:set var="summaryWordCount" value="30"/>
<c:if test="${!empty ssDashboard.dashboard.components[componentId].data.summaryWordCount}">
	<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[componentId].data.summaryWordCount}"/>
</c:if>

<c:set var="ssResultEntries" value="${ssDashboard.beans[componentId].ssSearchFormData.searchResults}"/>
<c:set var="ssResultTotalRecords" value="${ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchCount}" />
<c:set var="ssPageEndIndex" value="${ss_pageNumber * ss_pageSize + ssDashboard.beans[componentId].ssSearchFormData.ssEntrySearchRecordReturned}" />
<c:set var="ssPageStartIndex" value="${ss_pageNumber * ss_pageSize + 1}" />
<c:set var="isDashboard" value="yes"/>

		<div class="ss_searchResult_dashboardHeader">
			
			<div class="ss_dashboardPaginator"> 
			
			<c:if test="${empty isDashboard || isDashboard == 'no'}">
				<c:if test="${ss_pageNumber > 1}">
					<img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
					  onClick="ss_goToSearchResultPage(${ss_pageNumber-1});" />
				</c:if>
				<c:if test="${ss_pageNumber > 1}">
					<span class="ss_pageNumber">${ss_pageNumber}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
					  onClick="ss_goToSearchResultPage(${ss_pageNumber+1});" />
				</c:if>
			</c:if>
			<c:if test="${isDashboard == 'yes'}">
				<c:if test="${ssDashboard.scope != 'portlet'}">
					<c:set var="binderId" value="${ssBinder.id}"/>
				</c:if>
				<c:if test="${ssDashboard.scope == 'portlet'}">
					<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
				</c:if>
				<c:if test="${ss_pageNumber > 0}">
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" /></a>
				</c:if>
				<c:if test="${ss_pageNumber > 1 || ssPageEndIndex < ssResultTotalRecords}">
				<span class="ss_pageNumber">${ss_pageNumber+1}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
				</c:if>
			</c:if>
			</div>
			<div class="ss_searchResult_dashboardNumbers">			
				<c:choose>
				  <c:when test="${empty ssResultTotalRecords || ssResultTotalRecords == '0'}">
					<ssf:nlt tag="search.NoResults" />
				  </c:when>
				  <c:otherwise>
					<ssf:nlt tag="search.results">
					<ssf:param name="value" value="${ssPageStartIndex}"/>
					<ssf:param name="value" value="${ssPageEndIndex}"/>
					<ssf:param name="value" value="${ssResultTotalRecords}"/>
					</ssf:nlt>
				  </c:otherwise>
				</c:choose>
			</div>
			<div class="ss_clear"></div>
		</div>

		<c:set var="ssFolderEntries" value="${ssResultEntries}" />
		<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
		<%@ include file="/WEB-INF/jsp/search/result_list.jsp" %>
		
		<div id="ss_searchResult_header">
			<div class="ss_dashboardPaginator"> 
			
			<c:if test="${empty isDashboard || isDashboard == 'no'}">
				<c:if test="${ss_pageNumber > 1}">
					<img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
					  onClick="ss_goToSearchResultPage(${ss_pageNumber-1});" />
				</c:if>
				<c:if test="${ss_pageNumber > 1}">
					<span class="ss_pageNumber">${ss_pageNumber}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
					  onClick="ss_goToSearchResultPage(${ss_pageNumber+1});" />
				</c:if>
			</c:if>
			<c:if test="${isDashboard == 'yes'}">
				<c:if test="${ssDashboard.scope != 'portlet'}">
					<c:set var="binderId" value="${ssBinder.id}"/>
				</c:if>
				<c:if test="${ssDashboard.scope == 'portlet'}">
					<c:set var="binderId" value="${ssDashboardPortlet.id}"/>
				</c:if>
				<c:if test="${ss_pageNumber > 0}">
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" /></a>
				</c:if>
				<c:if test="${ss_pageNumber > 1 || ssPageEndIndex < ssResultTotalRecords}">
					<span class="ss_pageNumber">${ss_pageNumber+1}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
				</c:if>
			</c:if>
			</div>
			<div class="ss_clear"></div>
		</div>

	