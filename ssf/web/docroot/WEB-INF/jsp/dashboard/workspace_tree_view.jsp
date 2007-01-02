<%
// The dashboard "workspace tree" component
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
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.start[0] == 'this'}">

<c:set var="rootOpen" value="true"/>
<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.rootOpen[0]}">
  <c:set var="rootOpen" 
    value="${ssDashboard.dashboard.components[ssComponentId].data.rootOpen[0]}"/>
</c:if>

<c:if test="${empty ssDashboard.beans[ssComponentId].workspaceTree}">
  <span class="ss_bold"><ssf:nlt checkIfTag="true"
    tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[ssComponentId].name]}"/></span><br/>
  <span class="ss_italic"><ssf:nlt tag="dashboard.displayNotAvailable"/></span>
</c:if>

<c:if test="${!empty ssDashboard.beans[ssComponentId].workspaceTree}">
<ssf:tree treeName="wsTreeComponent${ssComponentId}" 
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}" 
  topId="${ssDashboard.beans[ssComponentId].topId}" 
  highlightNode="${ssDashboard.beans[ssComponentId].topId}" 
  rootOpen="${rootOpen}" 
  noInit="true" />
</c:if>
</c:if>

<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.start[0] == 'select'}">
</c:if>
