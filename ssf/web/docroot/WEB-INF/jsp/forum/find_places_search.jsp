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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="<c:out value="${ss_divId}"/>" parseInBrowser="true">
	  <c:set var="count" value="0"/>
	  <div id="<c:out value="${ss_divId}"/>" style="padding:2px;margin:2px;">
		<c:if test="${!empty ssEntries}">
	      <ul>
		    <c:forEach var="entry" items="${ssEntries}">
		      <c:set var="count" value="${count + 1}"/>
		      <li id="<c:out value="ss_findPlaces_id_${entry._docId}"/>"><a 
		          href="javascript: ;" 
		          onClick="ss_findPlacesSelectItem${ss_namespace}(this.parentNode, '${entry._entityType}');return false;" 
		          ><span style="white-space:nowrap;"><c:out value="${entry._extendedTitle}"/></span></a></li>
		    </c:forEach>
	      </ul>
          <c:if test="${ss_searchTotalHits > ss_pageSize}">
			<table class="ss_typeToFindNav" cellpadding="0" cellspacing="0" border="0"><tbody>
			<tr><td width="10%">
            <c:if test="${ss_pageNumber > 0}">
              <a href="javascript:;" onClick="ss_findPlacesPrevPage${ss_namespace}();return false;"
              ><img border="0" style="margin-right: 20px;" title="<ssf:nlt tag="general.Previous"/>" src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></a>
             </c:if>
             </td><td width="80%"></td><td width="10%">
            <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
              <a href="javascript:;" onClick="ss_findPlacesNextPage${ss_namespace}();return false;"
              ><img border="0" style="margin-left: 20px;" title="<ssf:nlt tag="general.Next"/>" src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
            </c:if>
           </td></tr></tbody></table>
           </c:if>
		</c:if>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>
