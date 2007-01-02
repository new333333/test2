<%
// The dashboard "workspace tree" init component
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
