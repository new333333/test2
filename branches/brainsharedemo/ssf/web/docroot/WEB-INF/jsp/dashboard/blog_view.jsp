<%
// The dashboard "search" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */ 
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
<c:set var="ssNamespace" value="${ssNamespace}_${ssComponentId}"/>
</c:if>

<c:set var="ss_divId" value="ss_searchResults_${ssNamespace}"/>
<c:set var="ss_pageNumber" value="0"/>
<c:if test="${ssDashboard.scope == 'portlet'}">
<%@ include file="/WEB-INF/jsp/dashboard/portletsupport.jsp" %>
</c:if>
<script type="text/javascript">
function ${ss_divId}_searchurl(binderId, entryId, type) {
	//Build a url to go to
	ss_dashboardPorletUrlSupport(ss_dashboardViewBinderUrl, ss_dashboardViewEntryUrl, binderId, entryId, type);
	return false;
}
</script>

<c:if test="${ssConfigJspStyle == 'template'}">
<script type="text/javascript">
function ${ss_divId}_searchurl(binderId, entryId, type) {
	return false;
}
</script>
</c:if>

<c:if test="${!empty ssDashboard.beans[ssComponentId].ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<c:forEach var="folder" items="${ssDashboard.beans[ssComponentId].ssFolderList}">
<tr>
  <td>
    <c:if test="${empty folder.iconName}">
      <img src="<html:imagesPath/>icons/folder.gif"/>
    </c:if>
    <c:if test="${!empty folder.iconName}">
      <img src="<html:imagesPath/>${folder.iconName}"
    </c:if>
  </td>
  <td><a href="javascript: ;"
		onClick="ss_dashboardPorletUrlSupport(ss_dashboardViewBinderUrl, ss_dashboardViewEntryUrl, '${folder.id}', '', 'folder'); return false;"
		>${folder.title}</a></td>
</tr>
</c:forEach>
</table>
<br/>
</c:if>

<div id="${ss_divId}">
<%@ include file="/WEB-INF/jsp/dashboard/search_view2.jsp" %>
</div>


