<%
// The dashboard "workspace tree" component
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
