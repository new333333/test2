<% // Folders %>
<ssf:expandableArea title="${ssFolder.title}">
<table class="ss_style" width="100%" border="0" cellpadding="2" cellspacing="0">
 <tr>
  <td>
	  <ssf:tree treeName="folderTree" treeDocument="<%= ssFolderDomTree %>" 
	    rootOpen="true" 
	    nodeOpen="<%= parentFolderId %>" highlightNode="<%= folderId %>" /></td>
 </tr>
</table>
</ssf:expandableArea>
