<% // Calendar view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="com.sitescape.ef.domain.FolderEntry" %>
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssEventDates" type="java.util.HashMap" scope="request" />
<jsp:useBean id="ssCalendarViewMode" type="java.lang.String" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="java.lang.String" scope="request" />
<%
	String folderId = ssFolder.getId().toString();
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>

<script language="javascript">
var ss_currentEntryId = "";
function ss_loadEntry(obj,id) {
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="loading" text="Loading..."/>");
	highlightLineById(folderLine);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

function ss_loadEntryUrl(url,id) {
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="loading" text="Loading..."/>");
	highlightLineById(folderLine);
	ss_showForumEntry(url, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="ss_folder">
<h1 class="ss_folderTitle">Folders</h1>
<table width="100%" border="0" cellpadding="2" cellspacing="0">
 <tr>
  <td>
	  <ssf:tree treeName="folderTree" treeDocument="<%= ssFolderDomTree %>" 
	    rootOpen="false" 
	    nodeOpen="<%= parentFolderId %>" highlightNode="<%= folderId %>" /></td>
 </tr>
</table>

</div>

<hr>
<b>Calendar view</b><p>
<c:choose>
<c:when test="${ssCalendarViewMode == 'week'}">
<%@ include file="/WEB-INF/jsp/definition_elements/calendar_view_week.jsp" %>
</c:when>
<c:otherwise>
Unknown view mode: ${ssCalendarViewMode}
</c:otherwise>
</c:choose>
