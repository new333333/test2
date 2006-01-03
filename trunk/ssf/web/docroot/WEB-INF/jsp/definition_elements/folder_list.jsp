<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssFolder" type="com.sitescape.ef.domain.Folder" scope="request" />
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.ef.domain.UserProperties" scope="request" />
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
    if (obj == null) {
    	//Didn't find it by this name. Look for it by its other names.
    	if (ss_columnCount && ss_columnCount > 0) {
    		//This is a sliding table. Go highlight all of the columns.
    		for (var i = 0; i <= ss_columnCount; i++) {
    			var rowId = id + "_" + i;
			    var rowObj = self.document.getElementById(rowId)
			    if (rowObj != null) {
					//Found a row; go highlight it
					if (i == 0 && highlightedLine != null) {
						//Reset the previous line color
						for (var j = 0; j <= ss_columnCount; j++) {
			    			var rowIdPrev = highlightedLine + "_" + j;
						    var rowObjPrev = self.document.getElementById(rowIdPrev)
						    if (rowObjPrev != null) {
								rowObjPrev.className = savedHighlightClassName;
							}
						}
					}
					if (i == ss_columnCount) {
						savedHighlightClassName = rowObj.className;
					}
					highlightedLine = id;
					rowObj.className = highlightClassName;
			    }
    		}
    	}
    	
    } else {
		//Found the id, this must be a single line; go highlight it
		if (highlightedLine != null) {
			highlightedLine.className = savedHighlightClassName;
		}
		if (obj != null) {
			highlightedLine = obj;
			savedHighlightClassName = highlightedLine.className;
			highlightedLine.className = highlightClassName;
		}
	}
}
</script>

<div class="ss_folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
<% // Then include the navigation widgets for this view %>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
<br />
<ssf:slidingTable type="sliding" folderId="<%= folderId %>">
<ssf:slidingTableRow headerRow="true">
  <ssf:slidingTableColumn width="15"><img border="0" alt="Unread entries" src="<html:imagesPath/>pics/sym_s_unseen_header.gif"></ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="10%">Number</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="10%">State</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="40%">Title</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%">Author</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%">Date</ssf:slidingTableColumn>
</ssf:slidingTableRow>

<c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) entry1.get("_docId");
%>
<ssf:slidingTableRow id="<%= folderLineId %>">

  <ssf:slidingTableColumn>
<%
	if (ssSeenMap.checkIfSeen(entry1)) {
%>&nbsp;<%
	} else {
%><img border="0" src="<html:imagesPath/>pics/sym_s_unseen.gif"><%
	}
%>
  </ssf:slidingTableColumn>

  <ssf:slidingTableColumn>
    <a class="ss_link" href="<ssf:url     
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><c:out value="${entry1._docNum}"/>.</a>&nbsp;&nbsp;&nbsp;
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry1._workflowState}">
    <a class="ss_link" href="<ssf:url     
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><c:out value="${entry1._workflowState}"/></a>
    </c:if>
  </ssf:slidingTableColumn>
  <ssf:slidingTableColumn>
    <a class="ss_link" href="<ssf:url     
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><c:if test="${empty entry1._title}"
    ><span class="fineprint">--no title--</span
    ></c:if><c:out value="${entry1._title}"/></a>
  </ssf:slidingTableColumn>
  <ssf:slidingTableColumn>
	<c:out value="${entry1._principal.title}"/> <ssf:presenceInfo user="<%=(User)entry1.get("_principal")%>"/>
  </ssf:slidingTableColumn>
  <ssf:slidingTableColumn>
    <c:out value="${entry1._modificationDate}"/>
  </ssf:slidingTableColumn>
 </ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>

