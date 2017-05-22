<%
// The dashboard "search" component
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
					  onclick="ss_goToSearchResultPage(${ss_pageNumber-1});" />
				</c:if>
				<c:if test="${ss_pageNumber > 1}">
					<span class="ss_pageNumber">${ss_pageNumber}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
					  onclick="ss_goToSearchResultPage(${ss_pageNumber+1});" />
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
					<a href="javascript: ;"
						onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" /></a>
				</c:if>
				<c:if test="${ss_pageNumber > 1 || ssPageEndIndex < ssResultTotalRecords}">
				<span class="ss_pageNumber">${ss_pageNumber+1}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<a href="javascript: ;"
						onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
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
				    <c:if test="${searchCountTotalApproximate}">
					  <ssf:nlt tag="search.resultsApproximate">
					    <ssf:param name="value" value="${ssPageStartIndex}"/>
					    <ssf:param name="value" value="${ssPageEndIndex}"/>
					  </ssf:nlt>
					</c:if>
				    <c:if test="${!searchCountTotalApproximate}">
					  <ssf:nlt tag="search.results">
					    <ssf:param name="value" value="${ssPageStartIndex}"/>
					    <ssf:param name="value" value="${ssPageEndIndex}"/>
					    <ssf:param name="value" value="${ssResultTotalRecords}"/>
					  </ssf:nlt>
					</c:if>
				  </c:otherwise>
				</c:choose>
			</div>
			<div class="ss_clear"></div>
		</div>

		<c:set var="ssFolderEntriesResults" value="${ssResultEntries}" />
		<jsp:include page="/WEB-INF/jsp/search/result_list.jsp" />
		
		<div id="ss_searchResult_header">
			<div class="ss_dashboardPaginator"> 
			
			<c:if test="${empty isDashboard || isDashboard == 'no'}">
				<c:if test="${ss_pageNumber > 1}">
					<img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" 
					  onclick="ss_goToSearchResultPage(${ss_pageNumber-1});" />
				</c:if>
				<c:if test="${ss_pageNumber > 1}">
					<span class="ss_pageNumber">${ss_pageNumber}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif" 
					  onclick="ss_goToSearchResultPage(${ss_pageNumber+1});" />
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
					<a href="javascript: ;"
						onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.previousPage"/> src="<html:imagesPath/>pics/sym_arrow_left_.gif" /></a>
				</c:if>
				<c:if test="${ss_pageNumber > 1 || ssPageEndIndex < ssResultTotalRecords}">
					<span class="ss_pageNumber">${ss_pageNumber+1}</span>
				</c:if>
				<c:if test="${ssPageEndIndex < ssResultTotalRecords}">
					<a href="javascript: ;"
						onclick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}',  '${ss_namespace}', '${ss_divId}', '${componentId}', 'search');"
					><img <ssf:alt tag="general.nextPage"/> src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
				</c:if>
			</c:if>
			</div>
			<div class="ss_clear"></div>
		</div>

	