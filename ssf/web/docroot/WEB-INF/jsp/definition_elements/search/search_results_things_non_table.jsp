<% // Search results listing of "things" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>

<script type="text/javascript">
var ss_placeholderEntryUrl = "<portlet:renderURL windowState="maximized"><portlet:param 
	name="action" value="ssActionPlaceHolder"/><portlet:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
	name="entryId" value="ssEntryIdPlaceHolder"/><portlet:param 
	name="operation" value="view_entry"/><portlet:param 
	name="newTab" value="ssNewTabPlaceHolder"/></portlet:renderURL>";
var ss_placeholderBinderUrl = "<portlet:renderURL windowState="maximized"><portlet:param 
	name="action" value="ssActionPlaceHolder"/><portlet:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><portlet:param 
	name="newTab" value="ssNewTabPlaceHolder"/></portlet:renderURL>";

//Check the Page Number Before Submission
function ss_goToPage_<portlet:namespace/>(formObj) {
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

function ss_submitPage_<portlet:namespace/>(formObj) {
	return (ss_goToPage_<portlet:namespace/>(formObj));
}

function ss_clickGoToPage_<portlet:namespace/>(strFormName) {
	var formObj = document.getElementById(strFormName);
	if (ss_goToPage_<portlet:namespace/>(formObj)) {
		formObj.submit();
	}
}

//Change the number of entries to be displayed in a page
function ss_changePageEntriesCount_<portlet:namespace/>(strFormName, pageCountValue) {
	var formObj = document.getElementById(strFormName);
	formObj.ssEntriesPerPage.value = pageCountValue;
	formObj.submit();
}
</script>
<div style="margin:0px;">
<div align="right" style="margin:0px 4px 0px 0px;">    

<table width="99%" border="0" cellspacing="0px" cellpadding="0px">
	<tr>
		<td align="left" width="55%">
		
			<table border="0" cellspacing="0px" cellpadding="0px">
			<tr>
				<td>
				    <span class="ss_light ss_fineprint">
					<c:choose>
					  <c:when test="${ssEntrySearchCount == '0'}">
						[<ssf:nlt tag="search.NoResults" />]
					  </c:when>
					  <c:otherwise>
						[<ssf:nlt tag="search.ResultsSimple">
						<ssf:param name="value" value="${ssPageStartIndex}"/>
						<ssf:param name="value" value="${ssPageEndIndex}"/>
						<ssf:param name="value" value="${ssEntrySearchCount}"/>
						</ssf:nlt>]
					  </c:otherwise>
					</c:choose>
					</span>
					&nbsp;&nbsp;
				</td>

			<form name="ss_recordsPerPage_<portlet:namespace/>" id="ss_recordsPerPage_<portlet:namespace/>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="view_search_results_listing"/><portlet:param 
				name="tabId" value="${tabId}"/><portlet:param 
				name="operation" value="change_entries_on_search_page"/></portlet:actionURL>">
			    
			    <input type="hidden" name="ssEntriesPerPage" />
				
				<td>
					<div style="position:relative; top:2; margin:2px; padding:2px; border-top:solid #666666 1px; border-bottom:solid #666666 1px;  border-right:solid #666666 1px;  border-left:solid #666666 1px;">
						<span class="ss_light ss_fineprint">
			
						<ssf:menu title="${ssPageMenuControlTitle}" titleId="ss_selectEntriesTitle" titleClass="ss_compact" menuClass="ss_actions_bar2 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
							<ul class="ss_actions_bar2 ss_actions_bar_submenu" style="width:250px;">
							<li>
								<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '5');return false;">
									<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
								</a>
							</li>
							<li>	
								<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '10');return false;">
									<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
								</a>
							</li>
							<li>
								<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '25');return false;">
									<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
								</a>
							</li>
							<li>
								<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '50');return false;">
									<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
								</a>
							</li>
							<li>
								<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '100');return false;">
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
				name="action" value="view_search_results_listing"/><portlet:param 
				name="tabId" value="${tabId}"/><portlet:param 
				name="operation" value="save_search_goto_page_info"/></portlet:actionURL>" onSubmit="return(ss_submitPage_<portlet:namespace/>(this))">
			
				<td>
					&nbsp;&nbsp;
				    <span class="ss_light ss_fineprint"><ssf:nlt tag="folder.GoToPage"/></span>
				    <input name="ssGoToPage" size="1" type="text" class="form-text" />
					<a class="ss_linkButton ss_smallprint" href="javascript: ;" onClick="ss_clickGoToPage_<portlet:namespace/>('ss_goToPageForm_<portlet:namespace/>'); return false;"><ssf:nlt tag="button.go"/></a>
				</td>
			</form>				
			
			</tr>
			</table>
			
		</td>
		
	</form>
		
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
							<portlet:param name="action" value="view_search_results_listing"/>
							<portlet:param name="operation" value="save_search_page_info"/>
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
								<portlet:param name="action" value="view_search_results_listing"/>
								<portlet:param name="operation" value="save_search_page_info"/>
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
							<portlet:param name="action" value="view_search_results_listing"/>
							<portlet:param name="operation" value="save_search_page_info"/>
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



<div class="ss_folder_border" style="position:relative; top:2; 
  margin:0px; padding:2px 0px;">

<% // Add the toolbar with the navigation widgets, commands and filter %>
<ssf:toolbar style="ss_actions_bar2 ss_actions_bar">

<ssf:toolbar style="ss_actions_bar2 ss_actions_bar" item="true">
<c:set var="ss_history_bar_table_class" value="ss_actions_bar_background ss_actions_bar_history_bar" scope="request"/>
<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
</ssf:toolbar>

<% // Entry toolbar %>
<c:if test="${!empty ssEntryToolbar}">
<ssf:toolbar toolbar="${ssEntryToolbar}" style="ss_actions_bar2 ss_actions_bar" item="true" />
</c:if>

</ssf:toolbar>

</div>
</div>

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

<div class="ss_blog_summary_title">
	<table class="ss_searchviewDashboardContainer" cellspacing="0" cellpadding="0" width="100%" border="0" align="center">
  	<tr>
		<td valign="top" class="ss_searchviewDashboardContainer">
			
		   	
		  <c:if test="${entry1._entityType == 'folderEntry' || 
		  		entry1._entityType == 'reply'}">
		    <a href="<ssf:url     
		      adapter="<%= useAdaptor2 %>" 
		      portletName="ss_forum" 
		      folderId="${entry1._binderId}" 
		      action="view_folder_entry" 
		      entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
		    onClick="ss_loadEntry(this, '${entry1._docId}');return false;">
		  </c:if>
		  
		  <c:if test="${entry1._entityType == 'user'}">
		  	<img border="0" src="<html:imagesPath/>icons/user_profile.png" alt="<ssf:nlt tag="general.users" />" />
		    <a href="<ssf:url     
		      adapter="<%= useAdaptor2 %>" 
		      portletName="ss_forum" 
		      folderId="${entry1._binderId}" 
		      action="view_profile_entry" 
		      entryId="<%= entry1.get("_docId").toString() %>" actionUrl="true" />" 
		    onClick="ss_loadEntry(this, '${entry1._docId}');return false;"> 
		  </c:if>
		  
		  <c:if test="${entry1._entityType == 'folder'}">
		    <c:if test="${entry1._entityType == 'folder'}">
		    	<c:if test="${!empty ssBinderData[entry1._docId].iconName}">
		    		<img border="0" src="<html:imagesPath/>${ssBinderData[entry1._docId].iconName}" alt="<ssf:nlt tag="general.type.folder" />"/>
		    	</c:if>
		    	<c:if test="${empty ssBinderData[entry1._docId].iconName}">
		    		<img border="0" src="<html:imagesPath/>icons/folder.gif" alt="<ssf:nlt tag="general.type.folder" />"/>
		    	</c:if>
		    </c:if>
		  
		    <a href="<ssf:url     
		      adapter="false" 
		      portletName="ss_forum" 
		      folderId="${entry1._docId}" 
		      action="view_folder_listing"
		      actionUrl="true" >
			  <ssf:param name="binderId" value="${entry1._docId}"/>
			  <ssf:param name="newTab" value="1"/>
			  </ssf:url>" 
		    onClick="return ss_loadBinder(this, '${entry1._docId}', '${entry1._entityType}');"> 
		  </c:if>
		  
		  <c:if test="${entry1._entityType == 'workspace' && entry1._definitionType != 12}">
		    <c:if test="${entry1._entityType == 'workspace'}">
		    	<c:if test="${!empty ssBinderData[entry1._docId].iconName}">
		    		<img border="0" src="<html:imagesPath/>${ssBinderData[entry1._docId].iconName}" alt="<ssf:nlt tag="general.type.workspace" />"/>
		    	</c:if>
		    	<c:if test="${empty ssBinderData[entry1._docId].iconName}">
		    		<img border="0" src="<html:imagesPath/>icons/workspace.gif" alt="<ssf:nlt tag="general.type.workspace" />"/>
		    	</c:if>
		    </c:if>
		    <a href="<ssf:url     
		      adapter="false" 
		      portletName="ss_forum" 
		      folderId="${entry1._docId}" 
		      action="view_ws_listing"
		      actionUrl="true" >
			  <ssf:param name="binderId" value="${entry1._docId}"/>
			  <ssf:param name="newTab" value="1"/>
			  </ssf:url>" 
		    onClick="return ss_loadBinder(this, '${entry1._docId}', '${entry1._entityType}');"> 
		  </c:if>
		  
		  <c:if test="${entry1._entityType == 'workspace' && entry1._definitionType == 12}">
		    <c:if test="${entry1._entityType == 'workspace'}">
		    	<c:if test="${!empty ssBinderData[entry1._docId].iconName}">
		    		<img border="0" src="<html:imagesPath/>${ssBinderData[entry1._docId].iconName}" alt="<ssf:nlt tag="general.type.workspace" />"/>
		    	</c:if>
		    	<c:if test="${empty ssBinderData[entry1._docId].iconName}">
		    		<img border="0" src="<html:imagesPath/>icons/workspace.gif" alt="<ssf:nlt tag="general.type.workspace" />"/>
		    	</c:if>
		    </c:if>		  
		    <a href="<ssf:url     
		      adapter="false" 
		      portletName="ss_forum" 
		      folderId="${entry1._docId}" 
		      action="view_ws_listing"
		      actionUrl="true" >
			  <ssf:param name="binderId" value="${entry1._docId}"/>
			  <ssf:param name="newTab" value="1"/>
			  </ssf:url>" 
		    onClick="return ss_loadBinder(this, '${entry1._docId}', '${entry1._entityType}');"> 
		  </c:if>
		  
		  <c:if test="${entry1._entityType == 'group'}">
		    <a href="<ssf:url     
		      adapter="false" 
		      portletName="ss_forum" 
		      folderId="${entry1._binderId}" 
		      entryId="${entry1._docId}" 
		      action="view_group"
		      actionUrl="true" />" 
		    onClick="alert('This is a group'); //ss_loadGroup(this, '${entry1._docId}');return false;">
		  </c:if>
		  
		    <c:if test="${empty entry1.title}">
		    	<span class="ss_fineprint"><i>(<ssf:nlt tag="entry.noTitle"/>)</i></span>
		    </c:if>
	    
	    	<span class="ss_entryTitle ss_underline"><c:out value="${entry1.title}"/></span></a>
		</td>
	</tr>
	
	<c:set var="entryBinderId" value="${entry1._binderId}"/>
	<c:set var="entryDocId" value="${entry1._docId}"/>	
	<c:if test="${entry1._entityType == 'folder' || entry1._entityType == 'workspace'}">
	  <c:set var="entryBinderId" value="${entry1._docId}"/>
	  <c:set var="entryDocId" value=""/>
	</c:if>
	<tr>
		<td class="ss_searchviewDashboardContainer">
			<span class="ss_smallprint">
				<ssf:markup type="view" binderId="${entryBinderId}" entryId="${entryDocId}">
					<ssf:textFormat textContent="${entry1._desc}" formatAction="limitedDescription" textMaxWords="30" />
					<c:if test="${entry1._entityType == 'user'}">
						<ssf:textFormat textContent="${entry1._comments}" formatAction="limitedDescription" textMaxWords="30" />
					</c:if>
				</ssf:markup>
			</span>	
		</td>
	</tr>
	
	<tr>
		<td class="ss_smallprint">
			<table width="100%">
			<tr>
				<td width="50%" class="ss_smallprint">
					<c:if test="${entry1._entityType == 'folderEntry' || entry1._entityType == 'reply'}">
						<ssf:nlt tag="entry.Folder" />: 
						<a href="<ssf:url adapter="true" portletName="ss_forum" 
					    	action="view_permalink"
					    	binderId="${entry1._binderId}">
					    	<ssf:param name="entityType" value="folder" />
			    	    	<ssf:param name="newTab" value="1"/>
							</ssf:url>" 
							onClick="if (${ss_divId}_searchurl) ${ss_divId}_searchurl('${entry1._binderId}','', 'folder'); return false;">
							<span class="ss_underline">${ssBinderData[entry1._binderId].title}</span>
							</a>
					</c:if>
				</td>
				<td width="50%" class="ss_smallprint">
					<c:if test="${!empty entry1._workflowStateCaption}">
						<ssf:nlt tag="entry.workflowState" />: <c:out value="${entry1._workflowStateCaption}" />
					</c:if>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	
	<tr>
		<td>
			<table width="100%">
			<tr>
				<td width="50%" class="ss_smallprint">
					<ssf:nlt tag="entry.createdBy" />: <ssf:showUser user="${entry1._principal}" />
				</td>
				<td width="50%" class="ss_smallprint">
					<ssf:nlt tag="entry.modified" />: <fmt:formatDate timeZone="${entry1._principal.timeZone.ID}"
				 		value="${entry1._modificationDate}" type="both" 
				 		timeStyle="short" dateStyle="medium" />
				</td>
			</tr>
			</table>
		</td>
	</tr>
	
	<tr>
		<td><div class="ss_line"></div></td>
	</tr>
	
  </table>
</div>

</c:forEach>


<div id="ss_tmd" class="ss_link_menu">
<ul class="ss_dropdownmenu">
<li><a href="#" onClick="ss_linkMenu.newTab(); return false;"><ssf:nlt tag="linkMenu.newTab"/></a></li>
<li><a href="#" onClick="ss_linkMenu.newWindow(); return false;"><ssf:nlt tag="linkMenu.newWindow"/></a></li>
</ul>
</div>
<script type="text/javascript">
function ss_initLinkMenu() {
	ss_linkMenu.menuDiv = "ss_tmd";
	ss_linkMenu.binderUrl = ss_placeholderBinderUrl;
	ss_linkMenu.entryUrl = ss_placeholderEntryUrl;
}
ss_createOnLoadObj('ss_initLinkMenu', ss_initLinkMenu);
</script>

