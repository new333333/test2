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
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>
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

<div style="margin:0px;">
<div style="position:relative; top:2; margin:2px; 
  border-top:solid #666666 1px; border-right:solid #666666 1px; border-left:solid #666666 1px; 
  background-color:#cecece;">
<% // Then include the navigation widgets for this view %>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</div>
</div>
<ssf:slidingTable type="sliding" folderId="<%= folderId %>">

<ssf:slidingTableRow headerRow="true">
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
	String seenStyle = "";
	if (!ssSeenMap.checkIfSeen(entry1)) {
		seenStyle = "ss_bold";
	}
%>
<ssf:slidingTableRow id="<%= folderLineId %>">

  <ssf:slidingTableColumn>
    <a class="ss_link_nodec" href="<ssf:url     
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span class="<%= seenStyle %>"><c:out value="${entry1._docNum}"/>.</span></a>&nbsp;&nbsp;&nbsp;
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry1._workflowState}">
    <a class="ss_link_nodec" href="<ssf:url     
    adapter="true" 
    portletName="ss_forum" 
    folderId="<%= folderId %>" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span class="<%= seenStyle %>"><c:out value="${entry1._workflowState}"/></span></a>
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
    ><span class="<%= seenStyle %> ss_fineprint">--no title--</span
    ></c:if><span class="<%= seenStyle %>"><c:out value="${entry1._title}"/></span></a>
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
	<ssf:presenceInfo user="<%=(User)entry1.get("_principal")%>"/> 
	<span class="<%= seenStyle %>"><c:out value="${entry1._principal.title}"/></span>
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <span class="<%= seenStyle %>"><fmt:formatDate 
     value="${entry1._modificationDate}" type="both" 
	 pattern="dd MMMM yyyy, HH:mm" /><c:out value="${entry1._modificationDate}"/>GMT</span>
  </ssf:slidingTableColumn>
  
 </ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>

