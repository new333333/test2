<%
// The dashboard "wiki summary" component
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
<br/>
<c:set var="treeName" value="${ssComponentId}${renderResponse.namespace}"/>
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
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssBinder}">
<span><ssf:nlt tag="portlet.forum.selected.folder"/></span>
<span class="ss_bold">${ssDashboard.beans[ssComponentId].ssBinder.title}</span>
</c:if>
<br/><br/>
<span class="ss_bold">
  <ssf:nlt tag="dashboard.wiki.selectWikiFolder"/>
</span>
<br/>
<div class="ss_indent_large">
<c:if test="${ssDashboard.scope == 'binder' || ssDashboard.scope == 'local' }">
<table><tr><td>&nbsp;&nbsp;&nbsp;<input type="checkbox" name="chooseFolder" 
	<c:if test="${!empty ssDashboard.dashboard.components[ssComponentId].data.chooseViewType}">checked="checked"</c:if>><span>
  <ssf:nlt tag="dashboard.wiki.selectFolderRelative"/>
</span></td></tr></table>
</c:if>
<ssf:tree treeName="${treeName}"
	treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
 	rootOpen="true"
	singleSelect="${ssDashboard.beans[ssComponentId].ssBinder.id}" 
	singleSelectName="ss_folder_id" />

</div>

<br/>