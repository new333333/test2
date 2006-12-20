<% // Folder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />

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
var ss_placeholderEntryUrl = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_folder_entry"/><portlet:param 
		name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
		name="entryId" value="ssEntryIdPlaceHolder"/><portlet:param 
		name="newTab" value="ssNewTabPlaceHolder"/></portlet:renderURL>";
var ss_placeholderFileUrl = "<ssf:url 
    	webPath="viewFile"
    	folderId="ssBinderIdPlaceHolder"
    	entryId="ssEntryIdPlaceHolder" >
    	</ssf:url>";
var ss_confirmDeleteFolderText = "<ssf:nlt tag="folder.confirmDeleteFolder"/>";

//Check the Page Number Before Submission
function goToPage_<portlet:namespace/>(formObj) {
	var strGoToPage = formObj.ssGoToPage.value;
	var pageCount = <c:out value="${ssPageCount}"/>;
	
	if (strGoToPage == "") {
		alert("<ssf:nlt tag="folder.enterPage" />");
		return false;	
	}
	if (strGoToPage == "0") {
		alert("<ssf:nlt tag="folder.enterValidPage" />");
		return false;
	}
	var blnValueCheck = _isInteger(strGoToPage);
	if (!blnValueCheck) {
		alert("<ssf:nlt tag="folder.enterValidPage" />");
		return false;
	}
	if (strGoToPage > pageCount) {
		formObj.ssGoToPage.value = pageCount;
	}
	return true;
}

function submitPage_<portlet:namespace/>(formObj) {
	return (goToPage_<portlet:namespace/>(formObj));
}

function clickGoToPage_<portlet:namespace/>(strFormName) {
	var formObj = document.getElementById(strFormName);
	if (goToPage_<portlet:namespace/>(formObj)) {
		formObj.submit();
	}
}

//Change the number of entries to be displayed in a page
function changePageEntriesCount_<portlet:namespace/>(strFormName, pageCountValue) {
	var formObj = document.getElementById(strFormName);
	formObj.ssEntriesPerPage.value = pageCountValue;
	formObj.submit();
}
</script>

<div id="ss_folder_table_parent" class="ss_folder">

<div style="margin:0px;">

<div align="right" style="margin:0px 4px 0px 0px;">
    
<table width="99%" border="0" cellspacing="0px" cellpadding="0px">

	<tr>
		<td align="left" width="55%">
		
		<table border="0" cellspacing="0px" cellpadding="0px">
		<tr>
			<td>
			    <span class="ss_light ss_fineprint">
				[<ssf:nlt tag="folder.Results">
				<ssf:param name="value" value="${ssPageStartIndex}"/>
				<ssf:param name="value" value="${ssPageEndIndex}"/>
				<ssf:param name="value" value="${ssTotalRecords}"/>
				</ssf:nlt>]
			    </span>
				&nbsp;&nbsp;
			</td>

			<form name="ss_recordsPerPage_<portlet:namespace/>" id="ss_recordsPerPage_<portlet:namespace/>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><portlet:param 
				name="tabId" value="${tabId}"/><portlet:param 
				name="operation" value="change_entries_on_page"/></portlet:actionURL>">
			    
			    <input type="hidden" name="ssEntriesPerPage" />
			
			<td>
				<div style="position:relative; top:2; margin:2px; padding:2px; border-top:solid #666666 1px; border-bottom:solid #666666 1px;  border-right:solid #666666 1px;  border-left:solid #666666 1px;">
				<span class="ss_light ss_fineprint">
	
				<ssf:menu title="${ssPageMenuControlTitle}" titleId="ss_selectEntriesTitle" titleClass="ss_compact" menuClass="ss_actions_bar_submenu" menuImage="pics/sym_s_down.gif">
					<ul class="ss_actions_bar_submenu" style="width:250px;">
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '5');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
						</a>
					</li>
					<li>	
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '10');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '25');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '50');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '100');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
						</a>
					</li>
					</ul>
				</ssf:menu>

			    </span>
			    </div>
			</td>

			</form>
			
			<form name="ss_goToPageForm_<portlet:namespace/>" id="ss_goToPageForm_<portlet:namespace/>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><portlet:param 
				name="tabId" value="${tabId}"/><portlet:param 
				name="operation" value="save_folder_goto_page_info"/></portlet:actionURL>" onSubmit="return(submitPage_<portlet:namespace/>(this))">

			<td>
				&nbsp;&nbsp;
			    <span class="ss_light ss_fineprint"><ssf:nlt tag="folder.GoToPage"/></span>
			    <input name="ssGoToPage" size="1" type="text" class="form-text" />
				<a class="ss_linkButton ss_smallprint" href="javascript: ;" onClick="clickGoToPage_<portlet:namespace/>('ss_goToPageForm_<portlet:namespace/>'); return false;"><ssf:nlt tag="button.go"/></a>
			</td>

			</form>
		
		</tr>
		</table>
		
		</td>
		
		<td align="center" width="25%">

		<table width="100%" border="0" cellspacing="0px" cellpadding="0px">
		<tr>
			<td width="15%">
				<c:choose>
				  <c:when test="${ssPagePrevious.ssPageNoLink == 'true'}">
					<img src="<html:imagesPath/>pics/sym_s_arrow_left.gif"/>
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" portletMode="view">
							<portlet:param name="action" value="${action}"/>
							<portlet:param name="operation" value="save_folder_page_info"/>
							<portlet:param name="binderId" value="${ssFolder.id}"/>
							<portlet:param name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/>
							<portlet:param name="tabId" value="${tabId}"/>
					</portlet:actionURL>"><img src="<html:imagesPath/>pics/sym_s_arrow_left.gif"/>
					</a>
				  </c:otherwise>
				</c:choose>
			</td>
			<td width="70%" align="center">
				<c:forEach var="entryPage" items="${ssPageNumbers}" >
				<jsp:useBean id="entryPage" type="java.util.HashMap" />
					<c:if test="${!empty entryPage.ssPageIsCurrent && entryPage.ssPageIsCurrent == 'true'}">
						<span class="font-small">
							<c:out value="${entryPage.ssPageDisplayValue}"/>
						</span>
					</c:if>
					
					<c:if test="${empty entryPage.ssPageIsCurrent}">
						<a href="<portlet:actionURL windowState="maximized" portletMode="view">
								<portlet:param name="action" value="${action}"/>
								<portlet:param name="operation" value="save_folder_page_info"/>
								<portlet:param name="binderId" value="${ssFolder.id}"/>
								<portlet:param name="ssPageStartIndex" value="${entryPage.ssPageInternalValue}"/>
								<portlet:param name="tabId" value="${tabId}"/>
						</portlet:actionURL>">
						<span class="ss_fineprint ss_light"><c:out value="${entryPage.ssPageDisplayValue}"/></span>
						</a>
					</c:if>
				</c:forEach>
			</td>
			<td width="15%" align="right">
				<c:choose>
				  <c:when test="${ssPageNext.ssPageNoLink == 'true'}">
					<img src="<html:imagesPath/>pics/sym_s_arrow_right.gif"/>
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" portletMode="view">
							<portlet:param name="action" value="${action}"/>
							<portlet:param name="operation" value="save_folder_page_info"/>
							<portlet:param name="binderId" value="${ssFolder.id}"/>
							<portlet:param name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/>
							<portlet:param name="tabId" value="${tabId}"/>
					</portlet:actionURL>"><img src="<html:imagesPath/>pics/sym_s_arrow_right.gif"/>
					</a>
				  </c:otherwise>
				</c:choose>
			</td>
		</tr>
		</table>

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

<ssf:toolbar style="ss_actions_bar" item="true" >
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
			<img border="0" src="<html:imagesPath/>pics/sym_s_down.gif"/>
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
			<img border="0" src="<html:imagesPath/>pics/sym_s_down.gif"/>
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
			<img border="0" src="<html:imagesPath/>pics/sym_s_down.gif"/>
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
			<img border="0" src="<html:imagesPath/>pics/sym_s_down.gif"/>
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
			<img border="0" src="<html:imagesPath/>pics/sym_s_down.gif"/>
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
    <a 
    class="ss_title_menu" 
    href="<ssf:url     
    adapter="<%= useAdaptor %>" 
    portletName="ss_forum" 
    folderId="${ssFolder.id}" 
    action="view_folder_entry" 
    entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
    onClick="ss_loadEntry(this, '${entry1._docId}');return false;" 
    onMouseOver="ss_linkMenu.showButton(this);"
    onMouseOut="ss_linkMenu.hideButton(this);"
    ><img border="0" class="ss_title_menu"
    onClick="ss_linkMenu.showMenu(this, '${entry1._docId}', '${ssBinder.id}', '${entry1._definitionType}');"
    src="<html:imagesPath/>pics/downarrow_off.gif"/><c:if test="${empty entry1.title}"
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
    <span <%= seenStyle %>><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${entry1._modificationDate}" type="both" 
	 timeStyle="short" dateStyle="short" /></span>
  </ssf:slidingTableColumn>
 </c:if>
</ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>
<div id="ss_emd" class="ss_link_menu">
<ul id="ss_folderMenuShowFileLink" class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.showFile(); return false;"><ssf:nlt 
  tag="linkMenu.showFile"/></a></li></ul>
<ul id="ss_folderMenuShowEntryLink" class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.showEntry(); return false;"><ssf:nlt 
  tag="linkMenu.showEntry"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.currentTab(); return false;"><ssf:nlt tag="linkMenu.currentTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.newTab(); return false;"><ssf:nlt tag="linkMenu.newTab"/></a></li></ul>
<ul class="ss_title_menu"><li><a href="#" 
  onClick="ss_linkMenu.newWindow(); return false;"><ssf:nlt tag="linkMenu.newWindow"/></a></li></ul>
</div>
<script type="text/javascript">
function ss_initLinkMenu() {
	ss_linkMenu.menuDiv = "ss_emd";
	ss_linkMenu.binderId = "${ssBinder.id}";
	ss_linkMenu.entityType = "folderEntry";
	ss_linkMenu.binderDefinitionType = "${ssBinder.definitionType}";
	ss_linkMenu.entryUrl = ss_placeholderEntryUrl;
	ss_linkMenu.fileUrl = ss_placeholderFileUrl;
	ss_linkMenu.menuLinkShowEntry = 'ss_folderMenuShowEntryLink';
	ss_linkMenu.menuLinkShowFile = 'ss_folderMenuShowFileLink';
}
ss_createOnLoadObj('ss_initLinkMenu', ss_initLinkMenu);
</script>



