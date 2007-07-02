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
<c:if test="${ssDashboard.dashboard.components[ssComponentId].data.start == 'this'}">
<script type="text/javascript">
function wsTreeComponent${ssComponentId}_showId(id, obj, action) {
	//Build a url to go to
	var url = "<ssf:url action="ssActionPlaceHolder" binderId="ssBinderIdPlaceHolder"/>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}

</script>

<ssf:tree treeName="wsTreeComponent${ssComponentId}" 
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}" 
  topId="${ssDashboard.beans[ssComponentId].topId}" 
  highlightNode="${ssDashboard.beans[ssComponentId].topId}" 
  initOnly="true"/>


</c:if>
