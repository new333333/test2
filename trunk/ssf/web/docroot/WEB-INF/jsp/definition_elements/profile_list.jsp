<% // Profile listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssBinder" type="com.sitescape.ef.domain.ProfileBinder" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.ef.domain.UserProperties" scope="request" />

<%
	String binderId = ssBinder.getId().toString();
%>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // Toolbar %>
<c:set var="toolbar" value="${ssFolderToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>

<script type="text/javascript">
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

<ssf:slidingTable type="sliding" folderId="<%= binderId %>">

<ssf:slidingTableRow headerRow="true">
  <ssf:slidingTableColumn width="30%">Title</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="50%">Email</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%">LoginName</ssf:slidingTableColumn>
</ssf:slidingTableRow>

<c:forEach var="entry" items="${ssEntries}" >
<jsp:useBean id="entry" type="java.util.HashMap" />
<%
	String folderLineId = "";
	String docId = "";
	if (entry != null && entry.get("_docId") != null) {
		docId = (String) entry.get("_docId");
		folderLineId = "folderLine_" + docId;
	}
%>

<ssf:slidingTableRow id="<%= folderLineId %>">

  <ssf:slidingTableColumn>
	<ssf:presenceInfo user="<%=(User)entry.get("_principal")%>"/> 
    <a class="ss_link" href="<ssf:url     
    adapter="true" 
    portletName="ss_profile" 
    folderId="${ssBinder.id}" 
    action="view_entry" 
    entryId="<%= docId %>" actionUrl="false" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry._docId}"/>');return false;" >
    <c:if test="${empty entry._title}">
    <span class="ss_fineprint">--no title--</span>
    </c:if>
    <c:out value="${entry._title}"/></a>
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
	<a class="ss_link" href="mailto:${entry._email}">
    <c:out value="${entry._email}"/></a>
  </ssf:slidingTableColumn>

  <ssf:slidingTableColumn>
    <c:out value="${entry._userName}"/>
  </ssf:slidingTableColumn>
  
 </ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>
