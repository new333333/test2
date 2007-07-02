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
<%@ include file="/WEB-INF/jsp/dashboard/common_setup.jsp" %>

<c:set var="treeName" value="wsTree_${ss_namespace}_${componentId}"/>

<c:set var="rootOpen" value="true"/>
<c:if test="${!empty ssDashboard.dashboard.components[componentId].data.rootOpen}">
  <c:set var="rootOpen" 
    value="${ssDashboard.dashboard.components[componentId].data.rootOpen}"/>
</c:if>

<c:if test="${empty ssDashboard.beans[componentId].workspaceTree}">
  <span class="ss_bold"><ssf:nlt checkIfTag="true"
    tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[componentId].name]}"/></span><br/>
  <span class="ss_italic"><ssf:nlt tag="dashboard.displayNotAvailable"/></span>
</c:if>

<c:if test="${!empty ssDashboard.beans[componentId].workspaceTree}">
<ssf:tree treeName="${treeName}" 
  treeDocument="${ssDashboard.beans[componentId].workspaceTree}" 
  topId="${ssDashboard.beans[componentId].topId}" 
  highlightNode="${ssDashboard.beans[componentId].topId}" 
  rootOpen="${rootOpen}" 
  noInit="true"/>
</c:if>
