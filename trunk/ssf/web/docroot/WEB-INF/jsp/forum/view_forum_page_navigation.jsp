<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Common folder page number navigation %>
<%@ page import="com.sitescape.team.util.NLT" %>

<ssf:skipLink tag="<%= NLT.get("skip.paging.links") %>" id="navigationLinks_${renderResponse.namespace}">

<c:if test="${ssConfigJspStyle != 'template'}">
<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>
<script type="text/javascript">
//Check the Page Number Before Submission
function ss_goToPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(formObj) {
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

function ss_submitPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(formObj) {
	return (ss_goToPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(formObj));
}

function ss_clickGoToPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(strFormName) {
	var formObj = document.getElementById(strFormName);
	if (ss_goToPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(formObj)) {
		formObj.submit();
	}
}

//Change the number of entries to be displayed in a page
function ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(strFormName, pageCountValue) {
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

			<form name="ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" id="ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/>
				<c:if test="${!empty cTag}"><portlet:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><portlet:param 
				name="endDate" value="${endDate}"/></c:if><portlet:param 
				name="operation" value="change_entries_on_page"/></portlet:actionURL>">
			    
			    <input type="hidden" name="ssEntriesPerPage" />
			
			<td>
				<div class="ss_results_pro_page">
				<span class="ss_light ss_fineprint">
	
				<ssf:menu title="${ssPageMenuControlTitle}" titleId="ss_selectEntriesTitle" titleClass="ss_compact" menuClass="ss_actions_bar4 ss_actions_bar_submenu" menuImage="pics/menudown.gif">
				
				<ssf:ifnotaccessible>
				
					<ul class="ss_actions_bar4 ss_actions_bar_submenu" style="width:150px;">
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '5');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
						</a>
					</li>
					<li>	
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '10');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '25');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '50');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
						</a>
					</li>
					<li>
						<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '100');return false;">
							<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
						</a>
					</li>
					</ul>
					
				</ssf:ifnotaccessible>	
				
				<ssf:ifaccessible>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '5');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="5"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '10');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="10"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '25');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="25"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '50');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="50"/></ssf:nlt>
					</a><br/>

					<a href="javascript: ;" onClick="ss_changePageEntriesCount_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_recordsPerPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>', '100');return false;"
					title="<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>">
						<ssf:nlt tag="folder.Page"><ssf:param name="value" value="100"/></ssf:nlt>
					</a><br/>

				</ssf:ifaccessible>
					
				</ssf:menu>

			    </span>
			    </div>
			</td>

			</form>
			
			<form name="ss_goToPageForm_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" id="ss_goToPageForm_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" method="post" 
			    action="<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
				name="action" value="${action}"/><portlet:param 
				name="binderId" value="${ssFolder.id}"/><c:if test="${!empty cTag}"><portlet:param 
				name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
				name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
				name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><portlet:param 
				name="endDate" value="${endDate}"/></c:if><portlet:param 
				name="operation" value="save_folder_goto_page_info"/></portlet:actionURL>" onSubmit="return(ss_submitPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(this))">
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
				onClick="ss_clickGoToPage_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>('ss_goToPageForm_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>'); return false;"><ssf:nlt tag="button.go"/></a>
			</c:if>
				
			</td>

			</form>
		
		</tr>
		</table>
		
		</td>
		
		<td align="center" width="25%">

				<c:choose>
				  <c:when test="${ssPagePrevious.ssPageNoLink == 'true'}">
					
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" 
						portletMode="view"><portlet:param 
						name="action" value="${action}"/><portlet:param 
						name="operation" value="save_folder_page_info"/><portlet:param 
						name="binderId" value="${ssFolder.id}"/><portlet:param 
						name="ssPageStartIndex" value="${ssPagePrevious.ssPageInternalValue}"/><c:if test="${!empty cTag}"><portlet:param 
						name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
						name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
						name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><portlet:param 
						name="endDate" value="${endDate}"/></c:if></portlet:actionURL>" 
						<ssf:title tag="title.goto.prev.page" /> >&lt;&lt;
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
						<a href="<portlet:actionURL windowState="maximized" 
							portletMode="view"><portlet:param 
							name="action" value="${action}"/><portlet:param 
							name="operation" value="save_folder_page_info"/><portlet:param 
							name="binderId" value="${ssFolder.id}"/><portlet:param 
							name="ssPageStartIndex" value="${entryPage.ssPageInternalValue}"/><c:if test="${!empty cTag}"><portlet:param 
							name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
							name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
							name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><portlet:param 
							name="endDate" value="${endDate}"/></c:if></portlet:actionURL>" 
							class="ssPageNumber" <ssf:title tag="title.goto.page.number"
						><ssf:param name="value" value="${entryPage.ssPageDisplayValue}" /></ssf:title> >
						<span><c:out value="${entryPage.ssPageDisplayValue}"/></span><%--
						--%></a>
					</c:if>
				</c:forEach>

				<c:choose>
				  <c:when test="${ssPageNext.ssPageNoLink == 'true'}">
					
				  </c:when>
				  <c:otherwise>
					<a href="<portlet:actionURL windowState="maximized" 
						portletMode="view"><portlet:param 
						name="action" value="${action}"/><portlet:param 
						name="operation" value="save_folder_page_info"/><portlet:param 
						name="binderId" value="${ssFolder.id}"/><portlet:param 
						name="ssPageStartIndex" value="${ssPageNext.ssPageInternalValue}"/><c:if test="${!empty cTag}"><portlet:param 
						name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><portlet:param 
						name="pTag" value="${pTag}"/></c:if><c:if test="${!empty yearMonth}"><portlet:param 
						name="yearMonth" value="${yearMonth}"/></c:if><c:if test="${!empty endDate}"><portlet:param 
						name="endDate" value="${endDate}"/></c:if></portlet:actionURL>" 
						<ssf:title tag="title.goto.next.page" />>&gt;&gt;
					</a>
				  </c:otherwise>
				</c:choose>
	</c:if>
</c:if>

</ssf:skipLink>