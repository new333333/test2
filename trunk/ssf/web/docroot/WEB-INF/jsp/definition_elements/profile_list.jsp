<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssBinder" type="com.sitescape.ef.domain.Binder" scope="request" />

<% // Toolbar %>
<c:set var="toolbar" value="${ssFolderToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>

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
<br />
<table width="100%" border="0" cellpadding="2" cellspacing="0" class="ss_borderTable">
 <tr class="ss_headerRow">
  <td class="ss_contentbold">Title</td>
  <td class="ss_contentbold">Email</td>
  <td class="ss_contentbold">LoginName</td>
</tr>

<c:set var="rowClass" value="ss_highlightGray"/>
<c:forEach var="entry" items="${ssEntries}" >
<jsp:useBean id="entry" type="java.util.HashMap" />

<c:choose>
<c:when test="${rowClass == 'ss_highlightGray'}">
  <c:set var="rowClass" value=""/>
  <tr id="folderLine_<c:out value="${entry._docId}"/>">
</c:when>
<c:otherwise>
  <c:set var="rowClass" value="ss_highlightGray"/>
  <tr id="folderLine_<c:out value="${entry._docId}"/>"  class="<c:out value="${rowClass}"/>">
</c:otherwise>
</c:choose>

   <td valign="top" width="40%" class="ss_content">
    <a class="ss_link" href="<ssf:url     
    adapter="true" 
    portletName="ss_profile" 
    folderId="${ssBinder.id}" 
    action="view_entry" 
    entryId="<%= entry.get("_docId").toString() %>" actionUrl="false" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry._docId}"/>');return false;" >
    <c:if test="${empty entry._title}">
    <span class="fineprint">--no title--</span>
    </c:if>
    <c:out value="${entry._title}"/></a></td>
  <td valign="top" width="30%" class="ss_content"><a class="ss_link" href="mailto:${entry._email}">
    <c:out value="${entry._email}"/></a></td>
  <td valign="top" width="24%" class="ss_content">
    <c:out value="${entry._userName}"/></td>
 </tr>
</c:forEach>
</table>
</div>
