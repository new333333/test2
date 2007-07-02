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
 
%>

<% // see task_view_init for setup  %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>
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
</c:if>

<div id="${ss_divId}" width="100%">
<%@ include file="/WEB-INF/jsp/dashboard/task_view2.jsp" %>
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
