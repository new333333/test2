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
<jsp:useBean id="wsDomTree" type="org.dom4j.Document" scope="request" />

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table width="100%"><tr><td>
<c:if test="${!empty folder}">
<span>Currently showing forum: <c:out value="${folder.title}" /></span>
<br>
</c:if>

<form action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">

<input type="hidden" name="forumId" value="<c:out value="${folder.id}"/>" />
<br>
<br>
Select the forum to be shown:
<br>
<br>
<script language="javascript">
function t_<portlet:namespace/>_wsTree_showId(forum, obj) {
	self.document.<portlet:namespace />fm.forumId.value=forum
	self.document.<portlet:namespace />fm.submit()
	return false
}
</script>
<ssf:tree treeName="wsTree" treeDocument="<%= wsDomTree %>"  rootOpen="true" />
</form>
<br>

</td></tr></table>

