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
	  <div id="<c:out value="${ss_divId}"/>" style="padding:2px;">
		<c:if test="${!empty ss_tags}">
	      <ul>
	      	
		    <c:forEach var="tag" items="${ss_tags}">
		      <c:set var="count" value="${count + 1}"/>

		      <li id="<c:out value="ss_findTag_id_${tag.ssTag}"/>"><a 
		          href="javascript: ;" onClick="
		          <c:if test="${ss_userGroupType == 'personalTags' || ss_userGroupType == 'communityTags' }">
		      		ss_putValueInto('ss_findTag_searchText_${ss_namespace}', '${tag.ssTag}');
		      	  </c:if>
  		          <c:if test="${ss_userGroupType != 'personalTags' && ss_userGroupType != 'communityTags' }">
  		          	ss_findTagSelectItem('${ss_namespace}', this.parentNode);return false;
  		          </c:if>"><span style="white-space:nowrap;"><c:out value="${tag.ssTag}"/></span></a></li>
		    </c:forEach>
	      </ul>
          <c:if test="${ss_searchTotalHits > ss_pageSize}">
			<table class="ss_typeToFindNav" cellpadding="0" cellspacing="0" border="0"><tbody>
			<tr><td width="10%">
            <c:if test="${ss_pageNumber > 0}">
              <a href="javascript:;" onClick="ss_findTagPrevPage('${ss_namespace}');return false;"
              ><img border="0" title="<ssf:nlt tag="general.Previous"/>" src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></a>
             </c:if>
             </td><td width="80%"></td><td width="10%">
            <c:if test="${count + ss_pageNumber * ss_pageSize < ss_searchTotalHits}">
              <a href="javascript:;" onClick="ss_findTagNextPage('${ss_namespace}');return false;"
              ><img border="0" title="<ssf:nlt tag="general.Next"/>" src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>
            </c:if>
           </td></tr></tbody></table>
           </c:if>
		</c:if>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>
