<%
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
 //only implementing single select, so don't use dashboard/blog_config 
%>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<c:set var="treeName" value="${renderResponse.namespace}"/>
<script type="text/javascript">
function ${treeName}_showId(forum, obj) {
	var formObj = ss_getContainingForm(obj);
	var r = formObj.ss_folder_id;
    for (var b = 0; b < r.length; b++) {
      if (r[b].value == forum) 	r[b].checked=true;
	}
	ss_clearSingleSelect('${treeName}');
	
	return false;
}
</script>
<form class="ss_style ss_form" action="<portlet:actionURL/>" 
	method="post" name="<portlet:namespace />fm"
	onSubmit="return ss_onSubmit(this);">
<input type="hidden" name="componentName" value="blog"/>

<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<tr><th align="left"><ssf:nlt tag="portlet.forum.selected.folder"/></th></tr>
<c:forEach var="folder" items="${ssDashboard.beans[ssComponentId].ssFolderList}">
<tr><td><c:out value="${folder.title}" /></td></tr>
</c:forEach>
</table>
</c:if>

<br>
<span class="ss_bold"><ssf:nlt tag="portlet.forum.select.folder"/></span>
<br>
<ssf:tree treeName="${treeName}"
	treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
 	rootOpen="true"
	singleSelect="${ssDashboard.beans[ssComponentId].ssBinderIdList[0]}" 
	singleSelectName="ss_folder_id" />

<br>
<input type="submit" class="ss_submit" name="applyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
<input style="margin-left:15px;" type="submit" class="ss_submit" name="closeBtn"
 value="<ssf:nlt tag="button.close"/>"
 onClick="self.location.href='<portlet:renderURL windowState="normal" portletMode="view"/>';return false;"
</td></tr></table>
</form>
<br>


