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
%>
<% // Folders %>
<c:if test="${!empty ssFolderDomTree}">
<script type="text/javascript">
function ss_folderTree_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>"
	if (action != null && action != "") {
		url = "<portlet:renderURL windowState="maximized"><portlet:param 
			name="action" value="ssActionPlaceHolder"/><portlet:param 
			name="binderId" value="ssBinderIdPlaceHolder"/></portlet:renderURL>"
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	}
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	self.location.href = url;
	return false;
}

</script>
<div>
<c:if test="${!empty ssFolder.parentFolder && !empty ssFolder.parentFolder.id}">
	  <ssf:tree treeName="ss_folderTree" treeDocument="${ssFolderDomTree}" 
	    rootOpen="false" 
	    nodeOpen="${ssFolder.parentFolder.id}" 
	    highlightNode="${ssFolder.id}" />
</c:if>
<c:if test="${empty ssFolder.parentFolder || empty ssFolder.parentFolder.id}">
	  <ssf:tree treeName="ss_folderTree" treeDocument="${ssFolderDomTree}" 
	    rootOpen="false" 
	    highlightNode="${ssFolder.id}" />
</c:if>
</div>
</c:if>