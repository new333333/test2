<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<%
	String folderId = ssFolder.getId().toString();
	String parentFolderId = "";
	if (ssFolder instanceof Folder) {
		Folder parentFolder = ((Folder) ssFolder).getParentFolder();
		if (parentFolder != null) parentFolderId = parentFolder.getId().toString();
	}
%>
<script language="javascript">
function highlightLineById(id) {
    if (id == "") {return;}
    var obj = null
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(id)
    } else {
        obj = self.document.all[id]
    }

	if (highlightedLine != null) {
		highlightedLine.className = savedHighlightClassName;
	}
	if (obj != null) {
		highlightedLine = obj;
		savedHighlightedLineClassName = highlightClassName;
		highlightedLine.className = highlightClassName;
	}
}
</script>

<div class="ss_folder">
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>

<table width="100%" border="0" cellpadding="2" cellspacing="0">
 <tr>
  <td class="ss_contentbold"><img border="0" alt="Unread entries" src="<html:imagesPath/>pics/sym_s_unseen_header.gif"></td>
  <td class="ss_contentbold">Number</td>
  <td class="ss_contentbold">Title</td>
  <td class="ss_contentbold">Author</td>
  <td class="ss_contentbold">Date</td>
</tr>
<c:forEach var="entry" items="${ssFolderEntries}" >
<jsp:useBean id="entry" type="com.sitescape.ef.domain.FolderEntry" />
<tr id="folderLine_<c:out value="${entry.id}"/>">
  <td align="right" valign="top" width="1%" class="ss_content">
<%
	if (ssSeenMap.checkIfSeen(entry)) {
%>&nbsp;<%
	} else {
%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%></td>
  <td align="right" valign="top" width="5%" class="ss_content">
	<c:out value="${entry.docNumber}"/>.&nbsp;&nbsp;&nbsp;</td>
  <td valign="top" width="40%" class="ss_content">
    <a class="ss_link" href="<ssf:url     
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= entry.getId().toString() %>" actionUrl="false" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry.id}"/>');return false;" >
    <c:if test="${empty entry.title}">
    <span class="fineprint">--no title--</span>
    </c:if>
    <c:out value="${entry.title}"/></a></td>
  <td valign="top" width="30%" class="ss_content">
    <c:out value="${entry.creation.principal.title}"/></td>
  <td valign="top" width="24%" class="ss_content">
    <c:out value="${entry.modification.date}"/></td>
 </tr>
</c:forEach>
</table>
</div>
