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
<c:if test="${!empty ssBinder}">
<b><ssf:nlt tag="portlet.workspace.selected.workspace"/></b><br/><br/>
<c:out value="${ssBinder.title}" />
<br/>
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
<br/>
<span class="ss_bold"><ssf:nlt tag="portlet.workspace.select.workspace" /></span>
<br>
<script type="text/javascript">
function <portlet:namespace/>_wsTree_showId(forum, obj) {
	var r = self.document.<portlet:namespace />fm.topWorkspace;
    for (var b = 0; b < r.length; b++) {
      if (r[b].value == forum) 	r[b].checked=true;
	}
	return false;
}
</script>
<c:set var="singleSelect" value=""/>
<c:if test="${!empty ssBinder}">
	<c:set var="singleSelect" value="${ssBinder.id}"/>
</c:if>
<ssf:tree treeName="${renderResponse.namespace}_wsTree" treeDocument="${ssWsDomTree}"  
  rootOpen="true" singleSelect="${singleSelect}" singleSelectName="topWorkspace" />

<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
</form>
<br>

</td></tr></table>

