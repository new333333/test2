<%
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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ssf_support_files_loaded" value="" scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="ssNamespace" value="${renderResponse.namespace}"/>
<c:if test="${!empty ssComponentId}">
  <c:set var="ssNamespace" value="${renderResponse.namespace}_${ssComponentId}"/>
</c:if>

<div class="ss_portlet_style ss_portlet">
<% // Navigation bar %>
<c:set var="ss_navbar_style" value="portlet" scope="request"/>
<jsp:include page="/WEB-INF/jsp/definition_elements/navbar.jsp" />
</div>