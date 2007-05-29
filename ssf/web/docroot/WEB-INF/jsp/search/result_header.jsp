<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>

		<div class="ss_searchResult_header ssPageNavi">
			<div class="ss_searchResult_numbers ssVisibleEntryNumbers">			
				<c:choose>
				  <c:when test="${ssTotalRecords == '0'}">
					[<ssf:nlt tag="search.NoResults" />]
				  </c:when>
				  <c:otherwise>
					<ssf:nlt tag="search.results">
					<ssf:param name="value" value="${ssPageStartIndex}"/>
					<ssf:param name="value" value="${ssPageEndIndex}"/>
					<ssf:param name="value" value="${ssTotalRecords}"/>
					</ssf:nlt>
				  </c:otherwise>
				</c:choose>
			</div>
			<div class="ss_paginator"> 
			
				<span class="ss_go_to_page"><ssf:nlt tag="folder.GoToPage"/></span>
				<input class="form-text" type="text" size="1" id="ssGoToPageInput<portlet:namespace />"/>
				<a class="ss_linkButton" onclick="ss_goToSearchResultPageByInputValue('ssGoToPageInput<portlet:namespace />'); return false;" href="javascript: ;">Go</a>
			
			<c:if test="${empty isDashboard || isDashboard == 'no'}">
				<c:if test="${ss_pageNumber > 1}">
					<a href="javascript: // ; " <ssf:alt tag="general.previousPage"/> onclick="ss_goToSearchResultPage(${ss_pageNumber-1});">&lt;&lt;</a>
				</c:if>
				
				<span class="ss_pageNumber">
				<c:forEach var="page" items="${ssPageNumbers}" varStatus="status">
					<c:if test="${page == ss_pageNumber}">
						<span class="ssCurrentPage">${page}</span>
					</c:if>
					<c:if test="${page != ss_pageNumber}">
						<a href="javascript: // ;" onclick="ss_goToSearchResultPage(${page});" 
							class="ssPageNumber">${page}</a>
					</c:if>
				</c:forEach>
				</span>
				
				<c:if test="${ssPageEndIndex < ssTotalRecords}">
					<a href="javascript:// ; " <ssf:alt tag="general.nextPage"/> onclick="ss_goToSearchResultPage(${ss_pageNumber+1});">&gt;&gt;</a>
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
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'search');"
					>&gt;&gt;</a>
				</c:if>
				<span class="ss_pageNumber">${ss_pageNumber+1}</span>
				<c:if test="${ssPageEndIndex < ssTotalRecords}">
					<a href="javascript: ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}', '${ss_divId}', '${componentId}', 'search');"
					>&lt;&lt;</a>
				</c:if>
			</c:if>
			</div>
			<div class="ss_clear"></div>
		</div>