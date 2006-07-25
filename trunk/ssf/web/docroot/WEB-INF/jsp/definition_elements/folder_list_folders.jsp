<% // Folders %>
<script type="text/javascript">
function folderTree_showId(id, obj, action) {
	//Build a url to go to
	var url = "<portlet:renderURL windowState="maximized">
				<portlet:param name="action" value="ssActionPlaceHolder"/>
				<portlet:param name="binderId" value="ssBinderIdPlaceHolder"/>
				</portlet:renderURL>"
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", id);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	self.location.href = url;
	return false;
}

</script>
<div>
<c:if test="${!empty ssFolder.parentFolder && !empty ssFolder.parentFolder.id}">
	  <ssf:tree treeName="folderTree" treeDocument="${ssFolderDomTree}" 
	    rootOpen="false" 
	    nodeOpen="${ssFolder.parentFolder.id}" 
	    highlightNode="${ssFolder.id}" />
</c:if>
<c:if test="${empty ssFolder.parentFolder || empty ssFolder.parentFolder.id}">
	  <ssf:tree treeName="folderTree" treeDocument="${ssFolderDomTree}" 
	    rootOpen="false" 
	    highlightNode="${ssFolder.id}" />
</c:if>
</div>
