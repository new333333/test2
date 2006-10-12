<% // Search results listing of "things" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

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
	<ssf:param name="operation" value="configure_folder_columns" />
	<ssf:param name="operation2" value="search" />
	<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
	</ssf:url>" onClick="ss_createPopupDiv(this, 'ss_folder_column_menu');return false;">
    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
</div>
<%
	}
%>
<div class="ss_folder_border" style="position:relative; top:2; 
  margin:0px 2px 2px 2px; padding:2px;
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
<ssf:slidingTable id="ss_folder_table" parentId="ss_folder_table_parent" type="<%= slidingTableStyle2 %>" 
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
    <c:if test="${entry1._entityType == 'folderEntry' || 
      		entry1._entityType == 'reply'}">
      <a href="<ssf:url 
  		folderId="${entry1._binderId}" 
  		action="view_folder_listing">
    	<ssf:param name="binderId" value="${entry1._binderId}"/>
    	<ssf:param name="newTab" value="1"/>
    	</ssf:url>" 
       ><span <%= seenStyle %>
      <c:if test="${empty ssBinderData[entry1._binderId].iconName}">
        style="background:url(<html:imagesPath/>icons/folder.gif)  no-repeat left;
        padding-left:20px;"
      </c:if>
      <c:if test="${!empty ssBinderData[entry1._binderId].iconName}">
        style="background:url(<html:imagesPath/>${ssBinderData[entry1._binderId].iconName})  no-repeat left;
        padding-left:20px;"
      </c:if>
       >${ssBinderData[entry1._binderId].title}</span></a>
    </c:if>
    <c:if test="${entry1._entityType == 'user'}">
      <a href="<ssf:url 
  		folderId="${entry1._binderId}" 
  		action="view_profile_listing" >
    	<ssf:param name="binderId" value="${entry1._binderId}"/>
    	<ssf:param name="newTab" value="1"/>
    	</ssf:url>" 
       ><span <%= seenStyle %>>${ssBinderData[entry1._binderId].title}</span>
    </c:if>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['number']}">
  <ssf:slidingTableColumn>
  <c:if test="${!empty entry1._docNum}">
    <a href="<ssf:url     
    adapter="<%= useAdaptor2 %>" 
    portletName="ss_forum" 
    folderId="${entry1._binderId}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" 
    actionUrl="true" />" 
    onClick="ss_loadEntry(this,'<c:out value="${entry1._docId}"/>');return false;" 
    ><span <%= seenStyle %>><c:out value="${entry1._docNum}"/>.</span></a>&nbsp;&nbsp;&nbsp;
  </c:if>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['title']}">
  <ssf:slidingTableColumn>
    <a class="ss_title_menu"
      <c:if test="${entry1._entityType == 'folderEntry' || 
      		entry1._entityType == 'reply'}">
        href="<ssf:url     
          adapter="<%= useAdaptor2 %>" 
          portletName="ss_forum" 
          folderId="${entry1._binderId}" 
          action="view_folder_entry" 
          entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
        onClick="ss_loadEntry(this, '${entry1._docId}');return false;" 
      </c:if>
      <c:if test="${entry1._entityType == 'user'}">
        href="<ssf:url     
          adapter="<%= useAdaptor2 %>" 
          portletName="ss_forum" 
          folderId="${entry1._binderId}" 
          action="view_profile_entry" 
          entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
        onClick="ss_loadEntry(this, '${entry1._docId}');return false;" 
      </c:if>
      <c:if test="${entry1._entityType == 'folder'}">
        href="<ssf:url     
          adapter="false" 
          portletName="ss_forum" 
          folderId="${entry1._docId}" 
          action="view_folder_listing"
          actionUrl="true" >
    	  <ssf:param name="binderId" value="${entry1._docId}"/>
    	  <ssf:param name="newTab" value="1"/>
    	  </ssf:url>" 
      </c:if>
      <c:if test="${entry1._entityType == 'workspace'}">
        href="<ssf:url     
          adapter="false" 
          portletName="ss_forum" 
          folderId="${entry1._docId}" 
          action="view_ws_listing"
          actionUrl="true" >
    	  <ssf:param name="binderId" value="${entry1._docId}"/>
    	  <ssf:param name="newTab" value="1"/>
    	  </ssf:url>" 
      </c:if>
      <c:if test="${entry1._entityType == 'group'}">
        href="<ssf:url     
          adapter="false" 
          portletName="ss_forum" 
          folderId="${entry1._binderId}" 
          entryId="${entry1._docId}" 
          action="view_group"
          actionUrl="true" />" 
        onClick="alert('This is a group'); return false;"
      </c:if>
    onMouseOver="ss_showTitleDropDown(this);"
    onMouseOut="ss_hideTitleDropDown(this);"
    ><img class="ss_title_menu"
    onClick="ss_showTitleOptions(this, '${entry1._docId}');"
    src="<html:imagesPath/>pics/downarrow_off.gif"/><c:if test="${empty entry1.title}"
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
	<ssf:param name="operation" value="configure_folder_columns" />
	<ssf:param name="operation2" value="search" />
	<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
	</ssf:url>" onClick="ss_createPopupDiv(this, 'ss_folder_column_menu');return false;">
    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
</div>
<%
	}
%>
<div id="ss_title_menu_div" style="position:absolute;border:1px solid black; visibility:hidden; 
  text-align:left; background:#ffffff;">
<ul>
<li>Pop-up in new window</li>
<li>Show in new tab</li>
<li>Show entry profile</li>
</ul>
</div>

