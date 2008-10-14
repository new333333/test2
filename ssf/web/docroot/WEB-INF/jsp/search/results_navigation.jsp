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
			
			<c:if test="${ss_pageNumber != 1 || ssPageEndIndex != ssTotalRecords}">
				<span class="ss_go_to_page"><ssf:nlt tag="folder.GoToPage"/></span>
				<input class="form-text" type="text" size="1" id="ssGoToPageInput${renderResponse.namespace}"/>
				<a class="ss_linkButton" onclick="ss_goToSearchResultPageByInputValue('ssGoToPageInput${renderResponse.namespace}'); return false;" href="javascript: ;">Go</a>
			</c:if>
			<c:if test="${empty isDashboard || isDashboard == 'no'}">
				<c:if test="${ss_pageNumber > 1}">
					<a href="javascript: // ; " <ssf:alt tag="general.previousPage"/> onclick="ss_goToSearchResultPage(${ss_pageNumber-1});return false;">&lt;&lt;</a>
				</c:if>
				
				<span class="ss_pageNumber">
					<c:if test="${ss_pageNumber >= 7}">
						<a href="javascript: // ;" onclick="ss_goToSearchResultPage(1);return false;" 
									class="ssPageNumber">1</a>
						...
					</c:if>
									
					<c:set var="lastPage" value="1" />
					<c:forEach var="page" items="${ssPageNumbers}" varStatus="status">
						<c:if test="${ss_pageNumber > 1 || ssPageEndIndex < ssTotalRecords}">	
							<c:if test="${page == ss_pageNumber}">
								<span class="ssCurrentPage">${page}</span>
							</c:if>
							<c:if test="${page != ss_pageNumber}">
								<a href="javascript: // ;" onclick="ss_goToSearchResultPage(${page});return false;" 
									class="ssPageNumber">${page}</a>
							</c:if>
							<c:set var="lastPage" value="${page}" />
						</c:if>
					</c:forEach>
					
					<c:if test="${ssPageCount > lastPage}">
						<c:choose>
							<c:when test="${ssPageCount == lastPage + 2}">
								<a href="javascript: // ;" onclick="ss_goToSearchResultPage(${ssPageCount-1});return false;" 
									class="ssPageNumber">${ssPageCount-1}</a>
							</c:when>
							<c:otherwise>
								<c:if test="${ssPageCount > lastPage + 2}">
									...
								</c:if>
							</c:otherwise>
						</c:choose>
						<a href="javascript: // ;" onclick="ss_goToSearchResultPage(${ssPageCount});return false;" 
									class="ssPageNumber">${ssPageCount}</a>
					</c:if>
				</span>
				
				<c:if test="${ssPageEndIndex < ssTotalRecords}">
					<a href="javascript:// ; " <ssf:alt tag="general.nextPage"/> onclick="ss_goToSearchResultPage(${ss_pageNumber+1});return false;">&gt;&gt;</a>
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
						onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber - 1}', '${ss_pageSize}',  '${renderResponse.namespace}', '${ss_divId}', '${componentId}', 'search');"
						<ssf:alt tag="general.previousPage"/> >&gt;&gt;</a>
				</c:if>
				<span class="ss_pageNumber">${ss_pageNumber+1}</span>
				<c:if test="${ssPageEndIndex < ssTotalRecords}">
					<a href="javascript: ;"
						onClick="ss_moreDashboardSearchResults('${binderId}', '${ss_pageNumber + 1}', '${ss_pageSize}',  '${renderResponse.namespace}', '${ss_divId}', '${componentId}', 'search');"
						<ssf:alt tag="general.nextPage"/> >&lt;&lt;</a>
				</c:if>
			</c:if>
			</div>
			<div class="ss_clear"></div>
