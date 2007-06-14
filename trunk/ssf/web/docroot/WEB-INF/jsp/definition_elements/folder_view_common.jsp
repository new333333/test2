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
<%@ page import="com.sitescape.team.search.SearchFieldResult" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.team.domain.SeenMap" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%@ page import="com.sitescape.team.ssfs.util.SsfsUtil" %>
<%
boolean isIECheck = BrowserSniffer.is_ie(request);
String strBrowserType = "";
if (isIECheck) strBrowserType = "ie";
%>
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
</script>

<div id="ss_folder_table_parent" class="ss_folder">

<div style="margin:0px;">

<div align="right" class="ssPageNavi">
    
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
<div class="ss_folder_border">

	<% // Add the toolbar with the navigation widgets, commands and filter %>
	<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">
			
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
	</portlet:actionURL>"

	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.Number") %>" />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.Number") %>" />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	>
    	<ssf:nlt tag="folder.column.Number"/>
	    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Number") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
	    <c:if test="${ ssFolderSortBy == '_sortNum' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Number") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
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
	</portlet:actionURL>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>	
	 >
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Title"/> </div>
    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Title") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    <a/>
      
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['size']}">
    <ssf:slidingTableColumn width="12%">

    <a href="<portlet:actionURL windowState="maximized" portletMode="view">
		<portlet:param name="action" value="${action}"/>
		<portlet:param name="operation" value="save_folder_sort_info"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		<portlet:param name="ssFolderSortBy" value="_fileSize"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_fileSize' && ssFolderSortDescend == 'true'}">
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:otherwise>
		</c:choose>
		<portlet:param name="tabId" value="${tabId}"/>
	</portlet:actionURL>">
    	<ssf:nlt tag="folder.column.Size"/>
	    <c:if test="${ ssFolderSortBy == '_fileSize' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="alt.showMenu"/> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
	    <c:if test="${ ssFolderSortBy == '_fileSize' && ssFolderSortDescend == 'false' }">
			<img <ssf:alt tag="alt.hideThisMenu"/> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    <a/>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['download']}">
    <ssf:slidingTableColumn width="13%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Download"/> </div>
    </ssf:slidingTableColumn>
  </c:if>

  <c:if test="${!empty ssFolderColumns['html']}">
    <ssf:slidingTableColumn width="10%">
      <div class="ss_title_menu"><ssf:nlt tag="folder.column.Html"/> </div>
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
	</portlet:actionURL>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.State") %>" />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.State") %>" />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	>
    	<ssf:nlt tag="folder.column.State"/>
	    <c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.State") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_workflowState' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.State") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
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
	</portlet:actionURL>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.Author") %>" />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.Author") %>" />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	>
		<ssf:nlt tag="folder.column.Author"/>
	    <c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.Author") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.Author") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
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
		<portlet:param name="ssFolderSortBy" value="_lastActivity"/>
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
		  	<portlet:param name="ssFolderSortDescend" value="false"/>
		  </c:when>
		  <c:otherwise>
		  	<portlet:param name="ssFolderSortDescend" value="true"/>
		  </c:otherwise>
		</c:choose>
		<portlet:param name="tabId" value="${tabId}"/>
	</portlet:actionURL>"
	
	<c:choose>
	  <c:when test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
	  	<ssf:title tag="title.sort.by.column.asc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.LastActivity") %>" />
	  	</ssf:title>
	  </c:when>
	  <c:otherwise>
	  	<ssf:title tag="title.sort.by.column.desc">
	  		<ssf:param name="value" value="<%= NLT.get("folder.column.LastActivity") %>" />
	  	</ssf:title>
	  </c:otherwise>
	</c:choose>
	>
		<ssf:nlt tag="folder.column.LastActivity"/>
	    <c:if test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
			<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" value="<%= NLT.get("folder.column.LastActivity") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
		</c:if>
		<c:if test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'false'}">
			<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" value="<%= NLT.get("folder.column.LastActivity") %>" /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
		</c:if>
    <a/>
    </ssf:slidingTableColumn>
  </c:if>
  
  <c:forEach var="column" items="${ssFolderColumns}">
    <c:set var="colName" value="${column.key}"/>
    <c:set var="defId" value=""/>
    <c:set var="eleType" value=""/>
    <c:set var="eleName" value=""/>
    <c:set var="eleCaption" value=""/>
	<jsp:useBean id="colName" type="java.lang.String" scope="page"/>
	<jsp:useBean id="defId" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleType" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleName" type="java.lang.String" scope="page"/>
	<jsp:useBean id="eleCaption" type="java.lang.String" scope="page"/>
<%
	if (colName.contains(",")) {
		String[] temp = colName.split(",");
		if (temp.length == 4) {
			defId = temp[0];
			eleType = temp[1];
			eleName = temp[2];
			eleCaption = temp[3];
		}
	}
	if (!defId.equals("")) {
%>
	  <c:set var="eleName" value="<%= eleName %>"/>
	  <c:set var="eleCaption" value="<%= eleCaption %>"/>
	  <ssf:slidingTableColumn width="20%">
	    ${eleCaption}
	  </ssf:slidingTableColumn>
<%
	}
%>
  </c:forEach>
  
</ssf:slidingTableRow>

<c:forEach var="entry1" items="${ssFolderEntries}" >
<jsp:useBean id="entry1" type="java.util.HashMap" />
<%
	String folderLineId = "folderLine_" + (String) entry1.get("_docId");
	String seenStyle = "";
	String seenStyleAuthor = "";
	String seenStyleFine = "class=\"ss_fineprint\"";
	if (!ssSeenMap.checkIfSeen(entry1)) {
		seenStyle = "class=\"ss_unseen\"";
		seenStyleAuthor="ss_unseen";
		seenStyleFine = "class=\"ss_unseen ss_fineprint\"";
	}
	boolean hasFile = false;
	boolean oneFile = false;
	if (entry1.containsKey("_fileID")) {
		String srFileID = entry1.get("_fileID").toString();
		hasFile = true;
		if (!srFileID.contains(",")) oneFile = true;
	}
%>
<c:set var="hasFile2" value="<%= hasFile %>"/>
<c:set var="oneFile2" value="<%= oneFile %>"/>
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
		displayDiv="false" entryId="${entry1._docId}" binderId="${ssBinder.id}" 
		entityType="${entry1._entityType}" imageId='menuimg_${entry1._docId}_${renderResponse.namespace}' 
		menuDivId="ss_emd_${renderResponse.namespace}" linkMenuObjIdx="${renderResponse.namespace}" 
		namespace="${renderResponse.namespace}" entryCallbackRoutine="${showEntryCallbackRoutine}" isDashboard="no"
		seenStyle="<%= seenStyle %>" seenStyleFine="<%= seenStyleFine %>">
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
  
 <c:if test="${!empty ssFolderColumns['size']}">
  <ssf:slidingTableColumn>
    <c:if test="${hasFile2 && oneFile2 && !empty entry1._fileSize}">
      <span <%= seenStyle %>>${entry1._fileSize}KB</span>
    </c:if>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['download']}">
  <ssf:slidingTableColumn>
    <c:if test="${hasFile2 && oneFile2}">
      <a href="<ssf:url 
	    webPath="viewFile"
	    folderId="${entry1._binderId}"
	    entryId="${entry1._docId}" >
		<ssf:param name="entityType" value="${entry1._entityType}"/>
	    <ssf:param name="fileId" value="${entry1._fileID}"/>
	    </ssf:url>"  class="ss_download_link"
		onClick="return ss_openUrlInWindow(this, '_blank');"
	  ><span><ssf:nlt tag="entry.download"/></span></a>
    </c:if>
  </ssf:slidingTableColumn>
 </c:if>
  
 <c:if test="${!empty ssFolderColumns['html']}">
  <ssf:slidingTableColumn>
    <c:if test="${hasFile2 && oneFile2}">
		<ssf:ifSupportsViewAsHtml relativeFilePath="${entry1._fileName}" browserType="<%=strBrowserType%>">
			<a target="_blank" style="text-decoration: none;" href="<ssf:url 
				webPath="viewFile"
			    folderId="${entry1._binderId}"
			    entryId="${entry1._docId}" >
				<ssf:param name="entityType" value="${entry1._entityType}"/>
			    <ssf:param name="fileId" value="${entry1._fileID}"/>
			    <ssf:param name="viewType" value="html"/>
			    </ssf:url>" <ssf:title tag="title.open.file.in.html.format" /> 
			><span <%= seenStyle %>>[<ssf:nlt tag="entry.HTML" />]</span></a>
		</ssf:ifSupportsViewAsHtml>
    </c:if>
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
	<ssf:showUser user="<%=(User)entry1.get("_principal")%>" titleStyle="<%= seenStyleAuthor %>"/> 
  </ssf:slidingTableColumn>
 </c:if>
 
 <c:if test="${!empty ssFolderColumns['date']}">
  <ssf:slidingTableColumn>
    <span <%= seenStyle %>><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${entry1._lastActivity}" type="both" 
	 timeStyle="short" dateStyle="short" /></span>
  </ssf:slidingTableColumn>
 </c:if>
 
<c:set var="colName2" value=""/>
<c:set var="defId2" value=""/>
<c:set var="eleType2" value=""/>
<c:set var="eleName2" value=""/>
<c:set var="eleCaption2" value=""/>
<jsp:useBean id="colName2" type="java.lang.String"/>
<jsp:useBean id="defId2" type="java.lang.String"/>
<jsp:useBean id="eleType2" type="java.lang.String"/>
<jsp:useBean id="eleName2" type="java.lang.String"/>
<jsp:useBean id="eleCaption2" type="java.lang.String"/>
  <c:forEach var="column" items="${ssFolderColumns}">
	<jsp:useBean id="column" type="java.util.Map.Entry"/>
<%
	String[] temp = new String[] {};
	colName2 = column.getKey().toString();
	if (colName2.contains(",")) {
		temp = colName2.split(",");
		if (temp != null && temp.length == 4) {
			defId2 = temp[0];
			eleType2 = temp[1];
			eleName2 = temp[2];
			eleCaption2 = temp[3];
		} else {
			defId2 = "";
			eleType2 = "";
			eleName2 = "";
			eleCaption2 = "";
		}
	}
	if (defId2 != null && !defId2.equals("")) {
%>
	  <c:set var="eleType2" value="<%= eleType2 %>"/>
	  <c:set var="eleName2" value="<%= eleName2 %>"/>
	  <c:set var="eleCaption2" value="<%= eleCaption2 %>"/>
	  <ssf:slidingTableColumn>
         <span <%= seenStyle %>>
         <c:if test="${!empty eleName2 && !empty entry1[eleName2]}">
	       <c:if test="${eleType2 == 'selectbox' || 
	                     eleType2 == 'radio' || 
	                     eleType2 == 'checkbox'}">
	       	 <c:out value="${entry1[eleName2]}"/>
	       </c:if>
	       <c:if test="${eleType2 == 'date'}">
	       	 <c:if test="${!empty entry1[eleName2]}">
<%
	try {
		java.text.SimpleDateFormat formatter = new java.text.SimpleDateFormat("yyyy-MM-dd");
	    String year = ((String)entry1.get(eleName2)).substring(0,4);
		String month = ((String)entry1.get(eleName2)).substring(4,6);
		String day = ((String)entry1.get(eleName2)).substring(6,8);
		java.util.Date date = formatter.parse(year + "-" + month + "-" + day);
%>
	<fmt:formatDate value="<%= date %>" type="date" dateStyle="short" />
<%
	} catch(Exception e) {}
%>
	       	 </c:if>
	       </c:if>
	       <c:if test="${eleType2 == 'user_list' || 
	       				 eleType2 == 'userListSelectbox'}">
          	<c:set var="separator" value=""/>
<%
	try {
		SearchFieldResult sr = (SearchFieldResult)entry1.get(eleName2);
		String[] sIds = new String[0];
		java.util.Set ids = new java.util.HashSet();
		sIds = (String[]) sr.getValueSet().toArray(new String[0]);
		ids = com.sitescape.team.util.LongIdUtil.getIdsAsLongSet(sIds);
%>
          	<c:forEach var="user" 
          	  items="<%= com.sitescape.team.util.ResolveIds.getPrincipals(ids) %>"
          	>${separator}${user.title}<c:set var="separator" value=", "/>
          	</c:forEach>
<%
	} catch(Exception e) {}
%>
          </c:if>
         </c:if>
         </span>
	   </ssf:slidingTableColumn>
<%
	}
%>
  </c:forEach>

</ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>


<ssf:menuLink displayDiv="true" menuDivId="ss_emd_${renderResponse.namespace}" 
  linkMenuObjIdx="${renderResponse.namespace}" 
  namespace="${renderResponse.namespace}">
</ssf:menuLink>

<c:set var="ss_useDefaultViewEntryPopup" value="1" scope="request"/>