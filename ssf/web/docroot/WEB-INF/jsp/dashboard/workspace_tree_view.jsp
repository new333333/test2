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
<c:if test="${ssDashboard.dashboard.components[ssDashboardId].data.start[0] == 'this'}">
<script type="text/javascript">
function wsTreeComponent_showId(id, obj, action) {
	//Build a url to go to
	var url = "<ssf:url action="ssActionPlaceHolder" binderId="ssBinderIdPlaceHolder"/>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}

</script>

<c:set var="rootOpen" value="true"/>
<c:if test="${!empty ssDashboard.dashboard.components[ssDashboardId].data.rootOpen[0]}">
  <c:set var="rootOpen" 
    value="${ssDashboard.dashboard.components[ssDashboardId].data.rootOpen[0]}"/>
</c:if>

<c:if test="${empty ssDashboard.beans[ssDashboardId].workspaceTree || empty ssDefinitionEntry.id}">
  <span class="ss_bold"><ssf:nlt checkIfTag="true"
    tag="${ssDashboard.component_titles[ssDashboard.dashboard.components[ssDashboardId].name]}"/></span><br/>
  <span class="ss_italic"><ssf:nlt tag="dashboard.displayNotAvailable"/></span>
</c:if>

<c:if test="${!empty ssDashboard.beans[ssDashboardId].workspaceTree && !empty ssDefinitionEntry.id}">
<ssf:tree treeName="wsTreeComponent" treeDocument="${ssDashboard.beans[ssDashboardId].workspaceTree}" 
  topId="${ssDashboard.beans[ssDashboardId].topId}" highlightNode="${ssDefinitionEntry.id}" 
  rootOpen="${rootOpen}" />
</c:if>
</c:if>

<c:if test="${ssDashboard.dashboard.components[ssDashboardId].data.start[0] == 'select'}">
</c:if>
