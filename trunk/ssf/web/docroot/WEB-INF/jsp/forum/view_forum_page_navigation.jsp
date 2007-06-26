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
<% // Common folder page number navigation %>
<%@ page import="com.sitescape.team.util.NLT" %>

<ssf:skipLink tag="<%= NLT.get("skip.paging.links") %>" id="navigationLinks_${renderResponse.namespace}">

<c:if test="${ssConfigJspStyle != 'template'}">
<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>
<script type="text/javascript">
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
	  <ssHelpSpot helpId="workspaces_folders/menus_toolbars/more_folder_navigation" offsetX="-5" offsetY="3" 
	    title="<ssf:nlt tag="helpSpot.moreFolderNavigation"/>"></ssHelpSpot>

	<c:if test="${ssFolderViewType != 'blog'}">
		<table border="0" cellspacing="0px" cellpadding="0px">
		<tr>
			<td>
			    <span class="ssVisibleEntryNumbers">
					<c:choose>
					  <c:when test="${ssTotalRecords == '0'}">
						[<ssf:nlt tag="folder.NoResults" />]
					  </c:when>
					  <c:otherwise>
						[<ssf:nlt tag="folder.Results">
						<ssf:param name="value" value="${ssPageStartIndex}"/>
						<ssf:param name="value" value="${ssPageEndIndex}"/>
						<ssf:param name="value" value="${ssTotalRecords}"/>
						</ssf:nlt>]
					  </c:otherwise>
					</c:choose>
				</span>
				&nbsp;&nbsp;
			</td>

			<form name="ss_recordsPerPage_<portlet:namespace/>" id="ss_recordsPerPage_<portlet:namespace/>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><portlet:param 
				name="tabId" value="${tabId}"/><c:if test="${!empty cTag}"><portlet:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty day}"><portlet:param 
				name="day" value="${day}"/></c:if><c:if test="${!empty month}"><portlet:param 
				name="month" value="${month}"/></c:if><c:if test="${!empty year}"><portlet:param 
				name="year" value="${year}"/></c:if><portlet:param 
				name="operation" value="change_entries_on_page"/></portlet:actionURL>">
			    
			    <input type="hidden" name="ssEntriesPerPage" />
			
			<td>
				<div class="ss_results_pro_page" style="position:relative; top:2; margin:2px; padding:2px; border-top:solid #666666 1px; border-bottom:solid #666666 1px;  border-right:solid #666666 1px;  border-left:solid #666666 1px;">
				<span class="ss_light ss_fineprint">
	
				<ssf:menu title="${ssPageMenuControlTitle}" titleId="ss_selectEntriesTitle" titleClass="ss_compact" menuClass="ss_actions_bar4 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
				
				<ssf:ifnotaccessible>
				
					<ul class="ss_actions_bar4 ss_actions_bar_submenu" style="width:150px;">
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
					
				</ssf:ifnotaccessible>	
				
				<ssf:ifaccessible>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '5');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '10');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '25');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '50');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<portlet:namespace/>('ss_recordsPerPage_<portlet:namespace/>', '100');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
					</a><br/>

				</ssf:ifaccessible>
					
				</ssf:menu>

			    </span>
			    </div>
			</td>

			</form>
			
			<form name="ss_goToPageForm_<portlet:namespace/>" id="ss_goToPageForm_<portlet:namespace/>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><portlet:param 
				name="tabId" value="${tabId}"/><c:if test="${!empty cTag}"><portlet:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty day}"><portlet:param 
				name="day" value="${day}"/></c:if><c:if test="${!empty month}"><portlet:param 
				name="month" value="${month}"/></c:if><c:if test="${!empty year}"><portlet:param 
				name="year" value="${year}"/></c:if><portlet:param 
				name="operation" value="save_folder_goto_page_info"/></portlet:actionURL>" onSubmit="return(ss_submitPage_<portlet:namespace/>(this))">
			<td>
				&nbsp;&nbsp;
			<c:if test="${ssPageCount > '1.0'}">
				<ssf:ifnotaccessible>
			    	<span class="ss_go_to_page"><ssf:nlt tag="folder.GoToPage"/></span>
			    </ssf:ifnotaccessible>
			    
			    <ssf:ifaccessible>
			    	<span class="ss_go_to_page"><label for="ssGoToPage"><ssf:nlt tag="folder.GoToPage"/></label></span>
			    </ssf:ifaccessible>
			    <input name="ssGoToPage" id="ssGoToPage" size="1" type="text" class="form-text" />
				<a class="ss_linkButton" href="javascript: ;" 
				<ssf:title tag="title.goto.page" />
				onClick="ss_clickGoToPage_<portlet:namespace/>('ss_goToPageForm_<portlet:namespace/>'); return false;"><ssf:nlt tag="button.go"/></a>
			</c:if>
				
			</td>

			</form>
		
		</tr>
		</table>
		
		</td>
		
		<td align="center" width="25%">

		<table width="100%" border="0" cellspacing="0px" cellpadding="0px">
		<tr>
			<td width="10%">
				<c:choose>
				  <c:when test="${ssPagePrevious.ssPageNoLink == 'true'}">
					
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" portletMode="view">
							<portlet:param name="action" value="${action}"/>
							<portlet:param name="operation" value="save_folder_page_info"/>
							<portlet:param name="binderId" value="${ssFolder.id}"/>
							<portlet:param name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/>
							<portlet:param name="tabId" value="${tabId}"/>
							<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
							<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
							<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
							<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
							<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
							<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
					</portlet:actionURL>" <ssf:title tag="title.goto.prev.page" /> >&lt;&lt;
					</a>
				  </c:otherwise>
				</c:choose>

				<c:forEach var="entryPage" items="${ssPageNumbers}" >
				<jsp:useBean id="entryPage" type="java.util.HashMap" />
					<c:if test="${!empty entryPage.ssPageIsCurrent && entryPage.ssPageIsCurrent == 'true'}">
						<span class="ssCurrentPage">
							<c:if test="${ssPageCount > '1.0'}">
							<c:out value="${entryPage.ssPageDisplayValue}"/>
							</c:if>
						</span>
					</c:if>
					
					<c:if test="${empty entryPage.ssPageIsCurrent}">
						<a href="<portlet:actionURL windowState="maximized" portletMode="view">
								<portlet:param name="action" value="${action}"/>
								<portlet:param name="operation" value="save_folder_page_info"/>
								<portlet:param name="binderId" value="${ssFolder.id}"/>
								<portlet:param name="ssPageStartIndex" value="${entryPage.ssPageInternalValue}"/>
								<portlet:param name="tabId" value="${tabId}"/>
								<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
								<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
								<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
								<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
								<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
								<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
						</portlet:actionURL>" class="ssPageNumber" <ssf:title tag="title.goto.page.number"><ssf:param name="value" value="${entryPage.ssPageDisplayValue}" /></ssf:title> >
						<span><c:out value="${entryPage.ssPageDisplayValue}"/></span>
						</a>
					</c:if>
				</c:forEach>

				<c:choose>
				  <c:when test="${ssPageNext.ssPageNoLink == 'true'}">
					
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" portletMode="view">
							<portlet:param name="action" value="${action}"/>
							<portlet:param name="operation" value="save_folder_page_info"/>
							<portlet:param name="binderId" value="${ssFolder.id}"/>
							<portlet:param name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/>
							<portlet:param name="tabId" value="${tabId}"/>
							<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
							<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
							<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
							<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
							<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
							<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
					</portlet:actionURL>" <ssf:title tag="title.goto.next.page" />>&gt;&gt;
					</a>
				  </c:otherwise>
				</c:choose>
			</td>
		</tr>
		</table>
	</c:if>
</c:if>

</ssf:skipLink>