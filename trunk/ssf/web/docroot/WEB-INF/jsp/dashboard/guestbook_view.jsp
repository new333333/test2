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

<div id="ss_searchResults_${ssNamespace}">
<%@ include file="/WEB-INF/jsp/dashboard/guestbook_view2.jsp" %>
</div>
