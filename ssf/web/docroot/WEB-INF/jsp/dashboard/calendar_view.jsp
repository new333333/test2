<%
// The dashboard "search" component
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
  //this is used by penlets and portlets
 //Don't include "include.jsp" directly 
%>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
<c:set var="prefix" value="${ssComponentId}${ss_namespace}" />

<c:set var="ss_pageNumber" value="0"/>
<c:if test="${ssConfigJspStyle != 'template'}">

	<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
		<table class="ss_style" cellspacing="0" cellpadding="0">
			<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
				<tr>
				  <td>
				    <a href="javascript: ;"
						onClick="return ss_gotoPermalink('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}', '${ss_namespace}', 'yes');"
						>${folder.parentBinder.title}</a> // 
				    <a href="javascript: ;"
						onClick="return ss_gotoPermalink('${folder.id}', '${folder.id}', 'folder', '${ss_namespace}', 'yes');"
						><span class="ss_bold">${folder.title}</span></a></td>
				</tr>
			</c:forEach>
		</table>
		<br/>
		
			<c:set var="binderIds" value="" />
			<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}" varStatus="status">
				<c:choose>
					<c:when test="${status.first}">
						<c:set var="binderIds" value="binderIds=${folder.id}" />
					</c:when>
					<c:otherwise>
						<c:set var="binderIds" value="${binderIds}&binderIds=${folder.id}" />
					</c:otherwise>
				</c:choose>
			</c:forEach>
			<input type="hidden" id="ssDashboardFolderIds${prefix}" value="${binderIds}" />
	
	</c:if>

	<div id="${ss_divId}" width="100%">
		<%@ include file="/WEB-INF/jsp/dashboard/calendar_view2.jsp" %>
	</div>
</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
	<c:if test="${!empty ssDashboard.beans[componentId].ssFolderList}">
		<table class="ss_style" cellspacing="0" cellpadding="0">
			<c:forEach var="folder" items="${ssDashboard.beans[componentId].ssFolderList}">
				<tr>
				  <td>
				    ${folder.parentBinder.title} // <span class="ss_bold">${folder.title}</span>
				   </td>
				</tr>
			</c:forEach>
		</table>
		<br/>
	</c:if>
</c:if>
