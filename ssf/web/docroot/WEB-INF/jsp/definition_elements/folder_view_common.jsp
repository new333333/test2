<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />

<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>

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
var ss_saveFolderColumnsUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="${action}"/><portlet:param 
		name="binderId" value="${ssFolder.id}"/><portlet:param 
		name="operation" value="save_folder_columns"/></portlet:actionURL>";
var ss_saveSubscriptionUrl = "<portlet:actionURL windowState="maximized"><portlet:param 
		name="action" value="${action}"/><portlet:param 
		name="binderId" value="${ssBinder.id}"/><portlet:param 
		name="operation" value="subscribe"/></portlet:actionURL>";
var ss_confirmDeleteFolderText = "<ssf:nlt tag="folder.confirmDeleteFolder"/>";
</script>

<div id="ss_folder_table_parent" class="ss_folder">

<div style="margin:0px;">

<div align="right" style="margin:0px 4px 0px 0px;">
    
<table width="99%" border="0" cellspacing="0px" cellpadding="0px">

	<tr>
		<td align="left" width="55%">
			<%@ include file="/WEB-INF/jsp/forum/view_forum_page_navigation.jsp" %>
		</td>

		<td align="right" width="20%">
		  <a href="<ssf:url
			adapter="true" 
			portletName="ss_forum" 
			action="__ajax_request" 
			actionUrl="true" >
			<ssf:param name="operation" value="configure_folder_columns" />
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="rn" value="ss_randomNumberPlaceholder" />
			</ssf:url>" onClick="ss_createPopupDiv(this, 'ss_folder_column_menu');return false;">
		    <span class="ss_fineprint ss_light"><ssf:nlt tag="misc.configureColumns"/></span></a>
		</td>
	</tr>
</table>

</div>
<div class="ss_folder_border" style="position:relative; top:2; margin:2px; ">

	<% // Add the toolbar with the navigation widgets, commands and filter %>
	<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
	
		<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true">
			<c:set var="ss_history_bar_table_class" value="ss_actions_bar_background ss_actions_bar_history_bar" scope="request"/>
			<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
		</ssf:toolbar>
		
		<% // Entry toolbar %>
		<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
		
		<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true" >
			<%@ include file="/WEB-INF/jsp/forum/view_forum_user_filters.jsp" %>
		</ssf:toolbar>
	
	</ssf:toolbar>

</div>
</div>
<ssf:slidingTable id="ss_folder_table" parentId="ss_folder_table_parent" type="<%= slidingTableStyle %>" 
 height="<%= ssFolderTableHeight %>" folderId="${ssFolder.id}">

<ssf:slidingTableRow headerRow="true">
  <c:if test="${!empty ssFolderColumns['number']}">
    <ssf:slidingTableColumn width="12%">

    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_sortNum"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:otherwise>
		</c:choose>
		<portlet:param name="tabId" value="${tabId}"/>
	</portlet:actionURL>">
    	<ssf:nlt tag="folder.column.Number"/>
	    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
			<img border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
	    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'false' }">
			<img border="0" src="<html:imagesPath/>pics/sym_s_up.gif"/>
		</c:if>
    <a/>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['title']}">
    <ssf:slidingTableColumn width="28%">
    
    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_sortTitle"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:otherwise>
		</c:choose>
		<portlet:param name="tabId" value="${tabId}"/>
	</portlet:actionURL>">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Title"/> </div>
    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
			<img border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
			<img border="0" src="<html:imagesPath/>pics/sym_s_up.gif"/>
		</c:if>
    <a/>
      
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['state']}">
    <ssf:slidingTableColumn width="20%">

    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_workflowState"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:otherwise>
		</c:choose>
		<portlet:param name="tabId" value="${tabId}"/>
	</portlet:actionURL>">
    	<ssf:nlt tag="folder.column.State"/>
	    <c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'true'}">
			<img border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
			<img border="0" src="<html:imagesPath/>pics/sym_s_up.gif"/>
		</c:if>
    <a/>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['author']}">
    <ssf:slidingTableColumn width="20%">

    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_creatorTitle"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:otherwise>
		</c:choose>
		<portlet:param name="tabId" value="${tabId}"/>
	</portlet:actionURL>">
		<ssf:nlt tag="folder.column.Author"/>
	    <c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'true'}">
			<img border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
			<img border="0" src="<html:imagesPath/>pics/sym_s_up.gif"/>
		</c:if>
    <a/>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['date']}">
    <ssf:slidingTableColumn width="20%">
    
    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_modificationDate"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_modificationDate' && ssFolderSortDescend == 'true'}">
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:otherwise>
		</c:choose>
		<portlet:param name="tabId" value="${tabId}"/>
	</portlet:actionURL>">
		<ssf:nlt tag="folder.column.Date"/>
	    <c:if test="${ ssFolderSortBy == '_modificationDate' && ssFolderSortDescend == 'true'}">
			<img border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_modificationDate' && ssFolderSortDescend == 'false'}">
			<img border="0" src="<html:imagesPath/>pics/sym_s_up.gif"/>
		</c:if>
    <a/>
    </ssf:slidingTableColumn>
  </c:if>
</ssf:slidingTableRow>

<c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) entry1.get("_docId");
	String seenStyle = "";
	String seenStyleFine = "class=\"ss_finePrint\"";
	if (!ssSeenMap.checkIfSeen(entry1)) {
		seenStyle = "class=\"ss_unseen\"";
		seenStyleFine = "class=\"ss_unseen ss_fineprint\"";
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
	<ssf:menuLink 
		displayDiv="false" entryId="${entry1._docId}" 
		folderId="${ssFolder.id}" binderId="${ssBinder.id}" 
		entityType="${entry1._entityType}" imageId='menuimg_${entry1._docId}_${renderResponse.namespace}' 
		menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObj="ss_linkMenu${renderResponse.namespace}" 
		namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" isDashboard="no">
		
		<ssf:param name="url" useBody="true">
			<ssf:url adapter="true" portletName="ss_forum" folderId="${ssFolder.id}" 
			action="view_folder_entry" entryId="${entry1._docId}" actionUrl="true" />					
		</ssf:param>
		    <c:if test="${empty entry1.title}">
		    	(<ssf:nlt tag="entry.noTitle"/>)
		    </c:if>
	    	<c:out value="${entry1.title}"/>
	</ssf:menuLink>
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
	<ssf:showUser user="<%=(User)entry1.get("_principal")%>" titleStyle="<%= seenStyle %>"/> 
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
</div>


<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObj="ss_linkMenu${renderResponse.namespace}" 
	namespace="${renderResponse.namespace}">
</ssf:menuLink>

<script type="text/javascript">
var ss_linkMenu${renderResponse.namespace} = new ss_linkMenuObj();
</script>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>