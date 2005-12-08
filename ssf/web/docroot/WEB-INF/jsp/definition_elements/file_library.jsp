<% // File library %>
<%
	String folderId = ss_forum_forum.getStringId();
	String parentFolderId = "";
	if (ss_forum_forum instanceof Folder) {
		Folder parentFolder = ((Folder) ss_forum_forum).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>
<script language="javascript">
function loadEntry(obj,id) {
	<c:out value="${showEntryMessageRoutine}"/>("<sitescape:nlt tag="loading" text="Loading..."/>");
	highlightLineById(id);
	showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<table width="100%">
<tr>
  <th align="left">Folders</th>
</tr>
<tr>
  <td>
	<div>
	  <sitescape:tree treeName="folderTree" treeDocument="<%= ss_folder_tree %>" 
	    commonImg="<%= COMMON_IMG %>" rootOpen="false" 
	    nodeOpen="<%= parentFolderId %>" highlightNode="<%= folderId %>" />
	</div>
  </td>
 </tr>
</table>
</div>
<br>
<div class="folder">
<table width="100%">
<tr>
  <th align="left"><img border="0" src="<%= contextPath %>/html/pics/sym_s_unseen_header.gif"></th>
  <th align="left">Title</th>
  <th align="left">Date</th>
  <th align="left">Author</th>
</tr>
<c:forEach var="fileEntry" items="${ss_folder_entries}" >
<jsp:useBean id="fileEntry" type="com.sitescape.ef.domain.FolderEntry" />
<tr id="folderLine_<c:out value="${fileEntry.id}"/>">
  <td align="right" valign="top" width="1%">
<%
	if (ss_folder_seenmap.checkIfSeen(fileEntry)) {
%>&nbsp;<%
	} else {
%><img border="0" src="<%= contextPath %>/html/pics/sym_s_unseen.gif"><%
	}
%>
  </td>
  <td valign="top" width="40%">
    <a href="<sitescape:url folderId="<%= folderId %>" operation="view_entry" 
    entryId="<%= fileEntry.getId().toString() %>" popup="true" />" 
    onClick="loadEntry(this,'folderLine_<c:out value="${fileEntry.id}"/>');return false;" >
    <c:if test="${empty fileEntry.title}">
    <span class="fineprint"><i>(no title)</i></span>
    </c:if>
    <c:out value="${fileEntry.title}"/></a>
  </td>
  <td valign="top" width="20%">
    <c:out value="${fileEntry.modification.date}"/>
  </td>
  <td valign="top" width="30%">
    <c:out value="${fileEntry.creation.principal.title}"/>
  </td>
</tr>
</c:forEach>
</table>
</div>
