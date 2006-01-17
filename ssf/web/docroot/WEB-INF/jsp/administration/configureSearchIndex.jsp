<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
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
<%@ page import="java.util.ArrayList" %>
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table class="ss_style" width="100%"><tr><td>

<form class="ss_style" action="<portlet:actionURL>
			  <portlet:param name="action" value="configure_folder_index"/>
		      </portlet:actionURL>" method="post" name="<portlet:namespace />fm">

<br>
<br>
<span class="ss_bold"><ssf:nlt tag="forum.selectIndexForums" text="Select the forums to be re-indexed:"/></span>
<br>
<br>
<script language="javascript">
function t_<portlet:namespace/>_wsTree_showId(forum, obj) {
	if (self.document.<portlet:namespace />fm["id_"+forum] && self.document.<portlet:namespace />fm["id_"+forum].checked) {
		self.document.<portlet:namespace />fm["id_"+forum].checked=false
	} else {
		self.document.<portlet:namespace />fm["id_"+forum].checked=true
	}
	return false
}
</script>
<ssf:tree treeName="wsTree" treeDocument="<%= ssWsDomTree %>"  
  rootOpen="true" multiSelect="<%= new ArrayList() %>" multiSelectPrefix="id_" />

<br>
<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;
<input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
</form>
<br>

</td></tr></table>

