<%
// The dashboard "guestbook summary" component
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
<%@ page import="java.util.ArrayList" %>
<br/>
<br/>

<script type="text/javascript">
function t_guestbookFolder_wsTree_showId(forum, obj) {
	var formObj = ss_getContainingForm(obj);
	if (formObj["ss_folder_id_"+forum] && formObj["ss_folder_id_"+forum].checked) {
		formObj["ss_folder_id_"+forum].checked=false
	} else {
		formObj["ss_folder_id_"+forum].checked=true
	}
	return false
}
</script>

<span class="ss_bold">
  <ssf:nlt tag="dashboard.guestbook.selectGuestbookFolder"/>
</span>
<br>
<br>
<div class="ss_indent_large">
<ssf:tree 
  treeName="<%= "t_guestbookFolder_wsTree" %>" 
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
  rootOpen="false" 
  multiSelect="<%= new ArrayList() %>" 
  multiSelectPrefix="ss_folder_id_"
/>
</div>

<br/>