<% // Folders %>
<%
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>
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
