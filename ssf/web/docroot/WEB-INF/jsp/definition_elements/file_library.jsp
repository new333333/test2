<% // File library %>
<%
	String folderId = ssFolder.getId().toString();
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>
<script type="text/javascript">
function ss_loadEntry(obj,id) {
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="loading" text="Loading..."/>");
	highlightLineById(id);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<table class="ss_style" width="100%">
<tr>
  <th align="left">Folders</th>
</tr>
<tr>
  <td>
	<div>
	  <ssf:tree treeName="folderTree" treeDocument="<%= ssFolderDomTree %>" 
	    rootOpen="false" 
	    nodeOpen="<%= parentFolderId %>" highlightNode="<%= folderId %>" />
	</div>
  </td>
 </tr>
</table>
</div>
<br>
<div class="folder">
<table class="ss_style" width="100%">
<tr>
  <th align="left"><img border="0" src="<html:imagesPath/>pics/sym_s_unseen_header.gif"></th>
  <th align="left">Title</th>
  <th align="left">Date</th>
  <th align="left">Author</th>
</tr>
<c:forEach var="fileEntry" items="${ssFolderEntries}" >
<jsp:useBean id="fileEntry" type="com.sitescape.ef.domain.Entry" />
<tr id="folderLine_<c:out value="${fileEntry.id}"/>">
  <td align="right" valign="top" width="1%">
<%
	if (ssSeenMap.checkIfSeen(fileEntry)) {
%>&nbsp;<%
	} else {
%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
  </td>
  <td valign="top" width="40%">
    <a href="<ssf:url folderId="<%= folderId %>" action="view_entry" 
    entryId="<%= fileEntry.getId().toString() %>" />" 
    onClick="ss_loadEntry(this,'folderLine_<c:out value="${fileEntry.id}"/>');return false;" >
    <c:if test="${empty fileEntry.title}">
    <span class="ss_fineprint"><i>(no title)</i></span>
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
