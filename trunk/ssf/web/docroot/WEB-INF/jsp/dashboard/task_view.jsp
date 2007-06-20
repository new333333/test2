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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
	<c:set var="ssNamespace" value="${ssNamespace}_${ssComponentId}"/>
</c:if>
<c:set var="portletNamespace" value=""/>
<ssf:ifnotadapter>
	<c:set var="portletNamespace" value="${renderResponse.namespace}"/>
</ssf:ifnotadapter>

<c:set var="ss_divId" value="ss_searchResults_${ssNamespace}"/>
<c:set var="ss_pageNumber" value="0"/>

<c:set var="componentId" value="${ssComponentId}"/>
<c:if test="${empty ssComponentId}">
	<c:set var="componentId" value="${ssDashboard.ssComponentId}" />
</c:if>
<c:if test="${ssDashboard.scope == 'portlet'}">
<%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
</c:if>
<c:if test="${ssConfigJspStyle != 'template'}">
<script type="text/javascript">    	
function ${ss_divId}_taskurl(binderId, entryId, type) {
	return ss_gotoPermalink(binderId, entryId, type, '${portletNamespace}', 'yes');
}
</script>
</c:if>

<c:if test="${ssConfigJspStyle == 'template'}">
<script type="text/javascript">
function ${ss_divId}_taskurl(binderId, entryId, type) {
	return false;
}
</script>
</c:if>

<c:if test="${!empty ssDashboard.beans[ssComponentId].ssFolderList}">
<table cellspacing="0" cellpadding="0" width="100%">
<c:forEach var="folder" items="${ssDashboard.beans[ssComponentId].ssFolderList}">
<tr>
  <td>
    <a href="javascript: ;"
		onClick="return ${ss_divId}_taskurl('${folder.parentBinder.id}', '${folder.parentBinder.id}', '${folder.parentBinder.entityIdentifier.entityType}');"
		>${folder.parentBinder.title}</a> // 
    <a href="javascript: ;"
		onClick="return ${ss_divId}_taskurl('${folder.id}', '${folder.id}', 'folder');"
		><span class="ss_bold">${folder.title}</span></a>
  </td>
</tr>
</c:forEach>
</table>
</c:if>

<div id="${ss_divId}" width="100%">
<%@ include file="/WEB-INF/jsp/dashboard/task_view2.jsp" %>
</div>

