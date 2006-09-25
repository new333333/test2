<% // Search results listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String displayStyle2 = ssUser.getDisplayStyle();
	if (displayStyle2 == null) displayStyle2 = "";
	
	String slidingTableStyle2 = "sliding";
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
		slidingTableStyle2 = "sliding_scrolled";
	}
	boolean useAdaptor2 = true;
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
		useAdaptor2 = false;
	}
	String ssFolderTableHeight2 = "";
	if (ssUserProperties != null && ssUserProperties.containsKey("folderEntryHeight")) {
		ssFolderTableHeight2 = (String) ssUserProperties.get("folderEntryHeight");
	}
	if (ssFolderTableHeight2 == null || ssFolderTableHeight2.equals("") || 
			ssFolderTableHeight2.equals("0")) ssFolderTableHeight2 = "400";
%>
<script type="text/javascript">
var ss_displayStyle = "<%= displayStyle2 %>";
var ss_saveFolderColumnsUrl = "<portlet:actionURL windowState="maximized">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_columns"/>
		</portlet:actionURL>";
</script>

<div class="ss_folder">

<div style="margin:0px;">
<%
	if (ssUser.getDisplayStyle() != null && 
	        ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
<div align="right" style="margin:0px 4px 0px 0px;">
  <a href="<ssf:url
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="configure_search_results_columns" />
	<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
	</ssf:url>" onClick="ss_configureColumns(this);return false;">
    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
</div>
<%
	}
%>
<div class="ss_folder_border" style="position:relative; top:2; margin:2px; padding:2px;
  border-top:solid #666666 1px; 
  border-right:solid #666666 1px; 
  border-left:solid #666666 1px;">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar">

<ssf:toolbar style="ss_actions_bar" item="true">
<c:set var="ss_history_bar_table_class" value="ss_actions_bar_background ss_actions_bar_history_bar" scope="request"/>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</ssf:toolbar>

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar" item="true" />
</c:if>

</ssf:toolbar>

</div>
</div>
<ssf:slidingTable id="ss_folder_table" type="<%= slidingTableStyle2 %>" 
 height="<%= ssFolderTableHeight2 %>" folderId="${ssFolder.id}">

<ssf:slidingTableRow headerRow="true">
  <c:if test="${!empty ssFolderColumns['folder']}">
    <ssf:slidingTableColumn width="20%"><ssf:nlt tag="folder.column.Folder"/></ssf:slidingTableColumn>
  </c:if>
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

 <c:if test="${!empty ssFolderColumns['folder']}">
  <ssf:slidingTableColumn>
    <a href="<ssf:url 
  		folderId="${entry1._binderId}" 
  		action="view_folder_listing"/>" 
    ><span <%= seenStyle %>>${entry1._binderId}</span>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['number']}">
  <ssf:slidingTableColumn>
    <a href="<ssf:url     
    adapter="<%= useAdaptor2 %>" 
    portletName="ss_forum" 
    folderId="${entry1._binderId}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._docNum}"/>.</span></a>&nbsp;&nbsp;&nbsp;
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['title']}">
  <ssf:slidingTableColumn>
    <a href="<ssf:url     
    adapter="<%= useAdaptor2 %>" 
    portletName="ss_forum" 
    folderId="${entry1._binderId}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this, '${entry1._docId}');return false;" 
    onMouseOver="ss_showTitleOptions(this, '${entry1._docId}');"
    onMouseOut="ss_hideTitleOptions(this, '${entry1._docId}');"
    ><c:if test="${empty entry1.title}"
    ><span <%= seenStyleFine %>>--<ssf:nlt tag="entry.noTitle"/>--</span
    ></c:if><span <%= seenStyle %>><c:out value="${entry1.title}"/></span></a>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['state']}">
  <ssf:slidingTableColumn>
    <c:if test="${!empty entry1._workflowStateCaption}">
    <a href="<ssf:url     
    adapter="<%= useAdaptor2 %>" 
    portletName="ss_forum" 
    folderId="${entry1._binderId}" 
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
    <span <%= seenStyle %>><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${entry1._modificationDate}" type="both" 
	 timeStyle="short" dateStyle="short" /></span>
  </ssf:slidingTableColumn>
 </c:if>
</ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
<%
	if (ssUser.getDisplayStyle() == null || 
	        !ssUser.getDisplayStyle().equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
%>
<div align="right">
  <a href="<ssf:url
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="configure_search_results_columns" />
	<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
	</ssf:url>" onClick="ss_configureColumns(this, '${ssBinder.id}');return false;">
    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
</div>
<%
	}
%>
</div>

