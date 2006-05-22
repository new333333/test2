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
<ssf:expandableArea title="${ssFolder.title}">
<table class="ss_style" width="100%" border="0" cellpadding="2" cellspacing="0">
 <tr>
  <td>
<c:if test="${!empty ssFolder.parentFolder && !empty ssFolder.parentFolder.id}">
	  <ssf:tree treeName="folderTree" treeDocument="${ssFolderDomTree}" 
	    rootOpen="true" 
	    nodeOpen="${ssFolder.parentFolder.id}" 
	    highlightNode="${ssFolder.id}" />
</c:if>
<c:if test="${empty ssFolder.parentFolder || empty ssFolder.parentFolder.id}">
	  <ssf:tree treeName="folderTree" treeDocument="${ssFolderDomTree}" 
	    rootOpen="true" 
	    highlightNode="${ssFolder.id}" />
</c:if>
  </td>
 </tr>
</table>
</ssf:expandableArea>
