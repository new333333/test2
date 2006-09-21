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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<tr><th align="left"><ssf:nlt tag="portlet.forum.selectd.forums" text="Currently selected forums:"/></th></tr>
<tr><td>&nbsp;</td></tr>
<c:forEach var="folder" items="${ssFolderList}">
<tr><td><c:out value="${folder.title}" /></td></tr>
</c:forEach>
</table>
<br>
</c:if>

<form class="ss_style ss_form" action="<portlet:actionURL/>" method="post" name="<portlet:namespace />fm">
<%
/* Liferay handles this already
<table>
<tr><td><span class="ss_labelLeft"><ssf:nlt tag="portlet.title"/></span>
</td><td><input class="ss_text" name="title" size="20" value="${portletTitle}"/>
</td></tr>
</table>
*/
%>

<br>
<span class="ss_bold"><ssf:nlt tag="portlet.forum.select.forums" text="Select the forums to be shown:"/></span>
<br>
<script type="text/javascript">
function <portlet:namespace/>_wsTree_showId(forum, obj) {
	if (self.document.<portlet:namespace />fm["id_"+forum] && self.document.<portlet:namespace />fm["id_"+forum].checked) {
		self.document.<portlet:namespace />fm["id_"+forum].checked=false
	} else {
		self.document.<portlet:namespace />fm["id_"+forum].checked=true
	}
	return false
}
</script>
<ssf:tree treeName="${renderResponse.namespace}_wsTree" treeDocument="${ssWsDomTree}"  
  rootOpen="true" multiSelect="${ssBinderIdList}" multiSelectPrefix="id_" />

<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
</form>
<br>

</td></tr></table>

