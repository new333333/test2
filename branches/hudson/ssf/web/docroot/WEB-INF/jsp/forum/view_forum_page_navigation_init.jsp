<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<% // Common folder page number navigation %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<c:if test="${!empty ssPageCount}">

<script type="text/javascript" src="<html:rootPath/>js/datepicker/date.js"></script>
<script type="text/javascript">
var ss_baseBinderPageUrl${renderResponse.namespace} = '<ssf:url><ssf:param 
	name="action" value="ssActionPlaceHolder"/><ssf:param 
	name="binderId" value="ssBinderIdPlaceHolder"/><ssf:param 
	name="page" value="ssPagePlaceHolder"/><ssf:param 
	name="cTag" value="ssCTagPlaceHolder"/><ssf:param 
	name="pTag" value="ssPTagPlaceHolder"/><ssf:param 
	name="yearMonth" value="ssYearMonthPlaceHolder"/><ssf:param 
	name="endDate" value="ssEndDatePlaceHolder"/><ssf:param 
	name="newTab" value="ssNewTabPlaceHolder"/></ssf:url>';

var ss_pageNavigationSliderUrl${renderResponse.namespace} = '<ssf:url action="${action}" actionUrl="true"><ssf:param 
	name="operation" value="save_folder_page_info"/> 
	<c:if test="${!empty ssForumPageNav_ShowTrash && 'true' == ssForumPageNav_ShowTrash}">
		<ssf:param name="showTrash" value="true"/> 
	</c:if>
	<ssf:param name="binderId" value="${ssFolder.id}"/><ssf:param 
	name="ssPageStartIndex" value="ss_pageIndexPlaceHolder"/><c:if test="${!empty cTag}"><ssf:param 
	name="cTag" value="${cTag}"/></c:if><c:if test="${!empty pTag}"><ssf:param 
	name="pTag" value="${pTag}"/></c:if><c:if test="${!empty ss_yearMonth}"><ssf:param 
	name="yearMonth" value="${ss_yearMonth}"/></c:if><c:if test="${!empty endDate}"><ssf:param 
	name="endDate" value="${endDate}"/></c:if></ssf:url>';
	
//Check the Page Number Before Submission
function ss_goToPage_${renderResponse.namespace}(formObj) {
	var strGoToPage = formObj.ssGoToPage${renderResponse.namespace}.value;
	var pageCount = <c:out value="${ssPageCount}"/>;
	
	if (strGoToPage == "") {
		alert('<ssf:escapeJavaScript value='<%= NLT.get("folder.enterPage") %>'/>');
		return false;	
	}
	if (strGoToPage == "0") {
		alert('<ssf:escapeJavaScript value='<%= NLT.get("folder.enterValidPage") %>'/>');
		return false;
	}
	var blnValueCheck = _isInteger(strGoToPage);
	if (!blnValueCheck) {
		alert('<ssf:escapeJavaScript value='<%= NLT.get("folder.enterValidPage") %>'/>');
		return false;
	}
	if (strGoToPage > pageCount) {
		formObj.ssGoToPage${renderResponse.namespace}.value = pageCount;
	}
	return true;
}

function ss_submitPage_${renderResponse.namespace}(formObj) {
	return (ss_goToPage_${renderResponse.namespace}(formObj));
}

function ss_clickGoToPage_${renderResponse.namespace}(strFormName) {
	var formObj = document.getElementById(strFormName);
	if (ss_goToPage_${renderResponse.namespace}(formObj)) {
		formObj.submit();
	}
}

function ss_autoGoToPage${renderResponse.namespace}(formId, page) {
	var formObj = document.getElementById(formId);
	if (ss_goToPage_${renderResponse.namespace}(formObj)) {
		formObj.submit();
	}
}

function ss_clickGoToEntry_${renderResponse.namespace}(strFormName) {
	var formObj = document.getElementById(strFormName);
	var strGoToEntry = formObj.ssGoToEntry${renderResponse.namespace}.value;

	if (strGoToEntry == "") {
		alert("<ssf:nlt tag="folder.enterEntryNumber" />");
		return false;	
	}
	if (strGoToEntry == "0") {
		alert("<ssf:nlt tag="folder.enterValidEntryNumber" />");
		return false;
	}
	var blnValueCheck = _isInteger(strGoToEntry);
	if (!blnValueCheck) {
		alert("<ssf:nlt tag="folder.enterValidEntryNumber" />");
		return false;
	}
	formObj.submit();
}

//Change the number of entries to be displayed in a page
function ss_changePageEntriesCount_${renderResponse.namespace}(strFormName, pageCountValue) {
	var formObj = document.getElementById(strFormName);
	formObj.ssEntriesPerPage.value = pageCountValue;
	formObj.submit();
}
</script>

</c:if>
