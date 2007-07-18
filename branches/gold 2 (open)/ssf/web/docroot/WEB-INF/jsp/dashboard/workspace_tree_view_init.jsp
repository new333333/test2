<%
// The dashboard "workspace tree" init component
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
<script type="text/javascript">
function ${treeName}_showId(id, obj, action) {
<c:if test="${ssConfigJspStyle != 'template'}">
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="ssActionPlaceHolder"/><portlet:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
</c:if>
	return false;
}
</script>


<ssf:tree treeName="${treeName}" 
  treeDocument="${ssDashboard.beans[componentId].workspaceTree}" 
  topId="${ssDashboard.beans[componentId].topId}" 
  highlightNode="${ssDashboard.beans[componentId].topId}" 
  initOnly="true"/>


