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

		<div id="ss_searchResult_header">
			<div id="ss_searchResult_numbers">			
				<c:choose>
				  <c:when test="${ssTotalRecords == '0'}">
					<ssf:nlt tag="search.NoResults" />
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
			<div id="ss_paginator"> 
				<c:if test="${ss_pageNumber > 1}">
					<img src="<html:imagesPath/>pics/sym_arrow_left_.gif" onClick="ss_goToSearchResultPage(${ss_pageNumber-1});" />
				</c:if>
				<span class="ss_pageNumber">${ss_pageNumber}</span>
				<c:if test="${ssPageEndIndex < ssTotalRecords}">
					<img src="<html:imagesPath/>pics/sym_arrow_right_.gif" onClick="ss_goToSearchResultPage(${ss_pageNumber+1});" />
				</c:if>
			</div>
			<div class="ss_clear"></div>
		</div>