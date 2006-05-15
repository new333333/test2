<% // File library %>
<script type="text/javascript">
function ss_loadEntry(obj,id) {
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="Loading" text="Loading..."/>");
	highlightLineById(id);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
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
    <a href="<ssf:url folderId="${ssFolder.id}" action="view_folder_entry" 
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
