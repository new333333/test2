<%
// The blog summary portlet
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

<table class="ss_style" width="100%"><tr><td>
<span class="ss_bold"><ssf:nlt tag="portlet.forum.selected.folder"/></span>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssSearchFormData.ssBinderIdList}">
<c:set var="binderId" value="${ssDashboard.beans[ssComponentId].ssSearchFormData.ssBinderIdList[0]}"/>
<a href="<portlet:renderURL><portlet:param 
		name="action" value="view_folder_listing"/><portlet:param 
		name="binderId" value="${binderId}"/></portlet:renderURL>">
		${ssDashboard.beans[ssComponentId].ssSearchFormData.ssBinderData[binderId].title}</a>
</c:if>

<br/><br/>

<%@ include file="/WEB-INF/jsp/dashboard/blog_view.jsp" %>

</td></tr></table>