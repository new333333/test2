<% // Folders %>
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
