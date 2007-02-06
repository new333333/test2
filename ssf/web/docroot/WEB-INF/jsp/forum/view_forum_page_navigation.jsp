<% // Common folder page number navigation %>
<c:if test="${ssConfigJspStyle != 'template'}">
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

		<table border="0" cellspacing="0px" cellpadding="0px">
		<tr>
			<td>
			    <span class="ss_light ss_fineprint">
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
				<div style="position:relative; top:2; margin:2px; padding:2px; border-top:solid #666666 1px; border-bottom:solid #666666 1px;  border-right:solid #666666 1px;  border-left:solid #666666 1px;">
				<span class="ss_light ss_fineprint">
	
				<ssf:menu title="${ssPageMenuControlTitle}" titleId="ss_selectEntriesTitle" titleClass="ss_compact" menuClass="ss_actions_bar_submenu" menuImage="pics/menudown.gif">
					<ul class="ss_actions_bar_submenu" style="width:250px;">
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
			    <span class="ss_light ss_fineprint"><ssf:nlt tag="folder.GoToPage"/></span>
			    <input name="ssGoToPage" size="1" type="text" class="form-text" />
				<a class="ss_linkButton ss_smallprint" href="javascript: ;" onClick="ss_clickGoToPage_<portlet:namespace/>('ss_goToPageForm_<portlet:namespace/>'); return false;"><ssf:nlt tag="button.go"/></a>
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
							<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
							<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
							<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
							<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
							<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
							<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
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
								<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
								<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
								<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
								<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
								<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
								<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
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
							<c:if test="${!empty cTag}"><portlet:param name="cTag" value="${cTag}"/></c:if>
							<c:if test="${!empty pTag}"><portlet:param name="pTag" value="${pTag}"/></c:if>
							<c:if test="${!empty yearMonth}"><portlet:param name="yearMonth" value="${yearMonth}"/></c:if>
							<c:if test="${!empty day}"><portlet:param name="day" value="${day}"/></c:if>
							<c:if test="${!empty month}"><portlet:param name="month" value="${month}"/></c:if>
							<c:if test="${!empty year}"><portlet:param name="year" value="${year}"/></c:if>
					</portlet:actionURL>"><img src="<html:imagesPath/>pics/sym_s_arrow_right.gif"/>
					</a>
				  </c:otherwise>
				</c:choose>
			</td>
		</tr>
		</table>
</c:if>
