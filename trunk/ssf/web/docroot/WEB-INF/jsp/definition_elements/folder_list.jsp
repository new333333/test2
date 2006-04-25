<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssFolderDomTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssUserFolderProperties" type="com.sitescape.ef.domain.UserProperties" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />
<%
	String displayStyle = ssUser.getDisplayStyle();
	if (displayStyle == null) displayStyle = "";
	
	String slidingTableStyle = "sliding";
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
		slidingTableStyle = "sliding_scrolled";
	}
	boolean useAdaptor = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor = false;
	}
	String ssFolderTableHeight = "";
	Map ssFolderPropertiesMap = ssUserFolderProperties.getProperties();
	if (ssFolderPropertiesMap != null && ssFolderPropertiesMap.containsKey("folderEntryHeight")) {
		ssFolderTableHeight = (String) ssFolderPropertiesMap.get("folderEntryHeight");
	}
	if (ssFolderTableHeight == null || ssFolderTableHeight.equals("") || 
			ssFolderTableHeight.equals("0")) ssFolderTableHeight = "400";
%>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>

<% // Navigation bar %>
<%@ include file="/WEB-INF/jsp/definition_elements/navbar.jsp" %>

<% // Toolbar %>
<c:if test="${!empty ssFolderToolbar}">
<c:set var="ss_toolbar" value="${ssFolderToolbar}" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>

<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle %>";
function highlightLineById(id) {
	if (ss_displayStyle == "accessible") {return;}
    if (id == "") {return;}
    var obj = self.document.getElementById(id)
    if (obj == null) {
    	//Didn't find it by this name. Look for it by its other names.
    	if (ss_columnCount && ss_columnCount > 0) {
    		//This is a sliding table. Go highlight all of the columns.
    		for (var i = 0; i <= ss_columnCount; i++) {
    			var rowId = id + "_" + i;
    			var colId = id + "_col_" + i;
			    var rowObj = self.document.getElementById(rowId)
			    var colObj = self.document.getElementById(colId)
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
					if (i == 1) {
						savedHighlightClassName = rowObj.className;
					}
					highlightedLine = id;
					rowObj.className = highlightClassName;
			    }
			    if (colObj != null) {
					//Found a col; go highlight it
					if (i == 0 && highlightedColLine != null) {
						//Reset the previous line color
						for (var j = 0; j <= ss_columnCount; j++) {
			    			var colIdPrev = highlightedColLine + "_col_" + j;
						    var colObjPrev = self.document.getElementById(colIdPrev)
						    if (colObjPrev != null) {
								colObjPrev.className = savedHighlightColClassName;
							}
						}
					}
					if (i == 1) {
						savedHighlightColClassName = colObj.className;
					}
					highlightedColLine = id;
					colObj.className = highlightColClassName;
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
<div class="ss_folder_border" style="position:relative; top:2; margin:2px; 
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
<table cellspacing="0" cellpadding="0" width="95%">
<tr><td align="left">
<% // Then include the navigation widgets for this view %>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</td>
<td>
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</td>
<td align="right">&nbsp;</td>
</tr>
</table>
</div>
</div>
<c:set var="ss_folderTableId" value="ss_folder_table" scope="request"/>
<ssf:slidingTable id="ss_folder_table" type="<%= slidingTableStyle %>" 
 height="<%= ssFolderTableHeight %>" folderId="${ssFolder.id}">

<ssf:slidingTableRow headerRow="true">
  <ssf:slidingTableColumn width="10%">Number</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="30%">Title</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%">State</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%">Author</ssf:slidingTableColumn>
  <ssf:slidingTableColumn width="20%">Date</ssf:slidingTableColumn>
</ssf:slidingTableRow>

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
<ssf:slidingTableRow id="<%= folderLineId %>">

  <ssf:slidingTableColumn>
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._docNum}"/>.</span></a>&nbsp;&nbsp;&nbsp;
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><c:if test="${empty entry1.title}"
    ><span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span
    ></c:if><span <%= seenStyle %>><c:out value="${entry1.title}"/></span></a>
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry1._workflowStateCaption}">
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._workflowStateCaption}"/></span></a>
    </c:if>
  </ssf:slidingTableColumn>

  <ssf:slidingTableColumn>
	<ssf:presenceInfo user="<%=(User)entry1.get("_principal")%>"/> 
	<span <%= seenStyle %>><c:out value="${entry1._principal.title}"/></span>
  </ssf:slidingTableColumn>
  
  <ssf:slidingTableColumn>
    <span <%= seenStyle %>><fmt:formatDate 
     value="${entry1._modificationDate}" type="both" 
	 pattern="dd MMMM yyyy, HH:mm" /> GMT</span>
  </ssf:slidingTableColumn>
  
 </ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>

