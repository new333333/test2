<%
// The dashboard "blog summary" component
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

<c:set var="treeName" value="${ssComponentId}${renderResponse.namespace}"/>
<script type="text/javascript">
function ${treeName}_showId(forum, obj) {
	var formObj = ss_getContainingForm(obj);
	if (formObj["ss_folder_id_"+forum] && formObj["ss_folder_id_"+forum].checked) {
		formObj["ss_folder_id_"+forum].checked=false
	} else {
		formObj["ss_folder_id_"+forum].checked=true
	}
	return false
}
</script>
<br/>
<table class="ss_style" width="100%"><tr><td>
<c:if test="${!empty ssDashboard.beans[ssComponentId].ssFolderList}">
<table class="ss_style" cellspacing="0" cellpadding="0">
<tr><th align="left"><ssf:nlt tag="portlet.forum.selected.forums"/></th></tr>
<c:forEach var="folder" items="${ssDashboard.beans[ssComponentId].ssFolderList}">
<tr><td><c:out value="${folder.title}" /></td></tr>
</c:forEach>
</table>
</c:if>
<br/>
<br/>
<c:set var="resultsCount" value="${ssDashboard.dashboard.components[ssComponentId].data.resultsCount[0]}"/>
<c:if test="${empty resultsCount}"><c:set var="resultsCount" value="5"/></c:if>
<c:set var="summaryWordCount" value="${ssDashboard.dashboard.components[ssComponentId].data.summaryWordCount[0]}"/>
<c:if test="${empty summaryWordCount}"><c:set var="summaryWordCount" value="20"/></c:if>
<table>
<tr>
<td><span><ssf:nlt tag="dashboard.search.resultsCount"/></span></td>
<td style="padding-left:10px;"><input type="text" name="data_resultsCount" size="5"
  value="${resultsCount}"/></td>
</tr>
<tr>
<td><span><ssf:nlt tag="dashboard.search.summardWordCount"/></span></td>
<td style="padding-left:10px;"><input type="text" name="data_summaryWordCount" size="5" 
  value="${summaryWordCount}"/></td>
</tr>
</table>
<br/>
<br/>

<span class="ss_bold">
  <ssf:nlt tag="dashboard.blog.selectBlogFolder"/>
</span>
<br>
<br>
<div class="ss_indent_large">
<ssf:tree 
  treeName="${treeName}" 
  treeDocument="${ssDashboard.beans[ssComponentId].workspaceTree}"  
  rootOpen="false" 
  multiSelect="${ssDashboard.beans[ssComponentId].ssBinderIdList}" 
  multiSelectPrefix="ss_folder_id_"
/>
</div>

<br/>