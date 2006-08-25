<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
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
<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle %>";
</script>

<div class="ss_folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>

<div style="margin:0px;">
<div class="ss_folder_border" style="position:relative; top:2; margin:2px; padding:2px;
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">
<table cellspacing="0" cellpadding="0" width="95%">
<tr><td align="left" width="20%">
<% // Then include the navigation widgets for this view %>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</td>
<td align="left" width="50%">
<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<c:set var="ss_toolbar" value="${ssEntryToolbar}" scope="request" />
<c:set var="ss_toolbar_style" value="ss_entry_toolbar" scope="request" />
<%@ include file="/WEB-INF/jsp/definition_elements/toolbar_view.jsp" %>
</c:if>
</td>
<td width="30%" nowarp>
<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
</td>
<td align="right">&nbsp;</td>
</tr>
</table>
</div>
</div>
<ssf:slidingTable id="ss_folder_table" type="<%= slidingTableStyle %>" 
 height="<%= ssFolderTableHeight %>" folderId="${ssFolder.id}">

<ssf:slidingTableRow headerRow="true">
  <c:if test="${!empty ssFolderColumns['number']}">
    <ssf:slidingTableColumn width="10%"><ssf:nlt tag="folder.column.Number"/></ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['title']}">
    <ssf:slidingTableColumn width="30%"><ssf:nlt tag="folder.column.Title"/></ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['state']}">
    <ssf:slidingTableColumn width="20%"><ssf:nlt tag="folder.column.State"/></ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['author']}">
    <ssf:slidingTableColumn width="20%"><ssf:nlt tag="folder.column.Author"/></ssf:slidingTableColumn>
  </c:if>
  <c:if test="${!empty ssFolderColumns['date']}">
    <ssf:slidingTableColumn width="20%"><ssf:nlt tag="folder.column.Date"/></ssf:slidingTableColumn>
  </c:if>
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

 <c:if test="${!empty ssFolderColumns['number']}">
  <ssf:slidingTableColumn>
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._docNum}"/>.</span></a>&nbsp;&nbsp;&nbsp;
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['title']}">
  <ssf:slidingTableColumn>
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><c:if test="${empty entry1.title}"
    ><span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span
    ></c:if><span <%= seenStyle %>><c:out value="${entry1.title}"/></span></a>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['state']}">
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry1._workflowStateCaption}">
    <a href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._workflowStateCaption}"/></span></a>
    </c:if>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['author']}">
  <ssf:slidingTableColumn>
	<ssf:presenceInfo user="<%=(User)entry1.get("_principal")%>"/> 
	<span <%= seenStyle %>><c:out value="${entry1._principal.title}"/></span>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['date']}">
  <ssf:slidingTableColumn>
    <span <%= seenStyle %>><fmt:formatDate 
     value="${entry1._modificationDate}" type="both" 
	 pattern="dd MMMM yyyy, HH:mm" /> GMT</span>
  </ssf:slidingTableColumn>
 </c:if>
</ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
<div align="right">
  <a href="<ssf:url
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="configure_folder_columns" />
	<ssf:param name="binderId" value="${ssBinder.id}" />
	<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
	</ssf:url>" onClick="ss_configureColumns(this, '${ssBinder.id}');return false;">
    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
<script type="text/javascript">
var ss_saveFolderColumnsUrl = "<portlet:actionURL windowState="maximized">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="operation" value="save_folder_columns"/>
		</portlet:actionURL>";
</script>
</div>
</div>

