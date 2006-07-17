<% // File library %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
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
  <th align="left">Title</th>
  <th align="left">Date</th>
  <th align="left">Author</th>
</tr>
<c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) entry1.get("_docId");
	String seenStyle = "";
	String seenStyleFine = "class=\"ss_finePrint\"";
	if (!ssSeenMap.checkIfSeen(entry1)) {
		seenStyle = "class=\"ss_bold\"";
		seenStyleFine = "class=\"ss_bold ss_fineprint\"";
	}
%>
<tr id="folderLine_${entry1._docId}">
  <td valign="top" width="40%">
    <a href="<ssf:url folderId="${ssFolder.id}" action="view_folder_entry" 
    entryId="${entry1._docId}" />" 
    onClick="ss_loadEntry(this,'folderLine_${entry1._docId}');return false;" >
    <c:if test="${empty entry1.title}">
    <span <%= seenStyleFine %> ><i>(no title)</i></span>
    </c:if>
    <span <%= seenStyle %> ><c:out value="${entry1.title}"/></span></a>
  </td>
  <td valign="top" width="20%">
	<span <%= seenStyle %>><fmt:formatDate 
      value="${entry1._modificationDate}" type="both" 
	  pattern="dd MMMM yyyy, HH:mm" /> GMT</span>
  </td>
  <td valign="top" width="30%">
	<ssf:presenceInfo user="<%=(User)entry1.get("_principal")%>"/> 
	<span <%= seenStyle %>><c:out value="${entry1._principal.title}"/></span>
  </td>
</tr>
</c:forEach>
</table>
</div>
