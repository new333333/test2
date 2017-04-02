<%
/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<div class="ss_style ss_portlet">
<div style="margin:10px;">
<script type="text/javascript">

var ss_buttonSelected = "";
function ss_buttonSelect(btn) {
	ss_buttonSelected = btn
}
function checkFilterForm(obj) {
	if (ss_buttonSelected == 'delete') {
		var delPersonalFilter = document.forms.filterData.selectedSearchFilter.value;
		if (delPersonalFilter == "") {
			ss_buttonSelected = 'close';
		}
		else {
			if (!confirm("<ssf:nlt tag="filter.confirmDelete"
				/>\n" + document.forms.filterData.selectedSearchFilter.value)) return false;
		}
	}
	if (ss_buttonSelected == 'deleteGlobal') {
		var delGlobalFilter = document.forms.filterData.selectedSearchFilterGlobal.value;
		if (delGlobalFilter == "") {
			ss_buttonSelected = 'close';
		}
		else {
			if (!confirm("<ssf:nlt tag="filter.confirmDelete"
				/>\n" + document.forms.filterData.selectedSearchFilterGlobal.value)) return false;
		}
	}
	
	// Is this a 'close' and we have JSP history to draw from?
	if ((ss_buttonSelected == 'close') && window.top.ss_getUrlFromContentHistory) {
		// Yes!  Is the top URL in the history a view permalink?
		var url = window.top.ss_getUrlFromContentHistory(0);
		if (url && (0 > url.indexOf("view_permalink"))) {
			// No!  Then we'll use the next one.
			url = window.top.ss_getUrlFromContentHistory(-1);
		}
		
		// If we have a URL to navigate to...
		if (url && (0 < url.length)) {
			// ...go there.
			window.top.ss_gotoContentUrl(url);
			return false;
		}
	}
	
	return true;
}

</script>

<form name="filterData" id="filterData" method="post" 
    action="<ssf:url action="build_filter" actionUrl="true"><ssf:param 
    	name="binderId" value="${ssBinder.id}"/><ssf:param 
    	name="binderType" value="${ssBinder.entityType}"/></ssf:url>" 
	onSubmit="return(checkFilterForm(this));"
>

<div class="ss_buttonBarRight">
<input 
  type="submit" class="ss_submit" name="closeBtn" onClick="ss_buttonSelect('close');"
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="filter.filterAddModDelete"/></legend>

	<br>
	<input type="submit" class="ss_submit" name="addBtn"  onClick="ss_buttonSelect('add');"
	  value="<ssf:nlt tag="filter.add" text="Add a new filter"/>">
	<br>
	<br>
	<c:if test="${!empty ss_searchFilters}">
	  <table class="ss_style">
	  <tr>
	  <td valign="top">
	    <span class="ss_labelAbove"><ssf:nlt tag="filter.personalFilters"/></span>
	    <select name="selectedSearchFilter">
	      <option value=""><ssf:nlt tag="filter.selectFilter"/></option>
	      <c:forEach var="searchFilter" items="${ss_searchFilters}">
	        <option value="<c:out value="${searchFilter.key}" 
	          escapeXml="true"/>"><c:out value="${searchFilter.key}" escapeXml="true"/></option>
	      </c:forEach>
	    </select>
	  </td>
	  <td valign="top">
	    <input type="submit" class="ss_submit" name="modifyBtn"  onClick="ss_buttonSelect('modify');"
	      value="<ssf:nlt tag="button.modify" text="Modify"/>">
	    <br>
	    <input type="submit" class="ss_submit" name="deleteBtn"  onClick="ss_buttonSelect('delete');"
	      value="<ssf:nlt tag="button.delete" text="Delete"/>">
	  </td>
	  </tr>
	  </table>
	</c:if>
	<br>
	<br>
	<c:if test="${!empty ss_searchFiltersGlobal}">
	  <table class="ss_style">
	  <tr>
	  <td valign="top">
	    <span class="ss_labelAbove"><ssf:nlt tag="filter.globalFilters"/></span>
	    <select name="selectedSearchFilterGlobal">
	      <option value=""><ssf:nlt tag="filter.selectFilter"/></option>
	      <c:forEach var="searchFilter" items="${ss_searchFiltersGlobal}">
	        <option value="<c:out value="${searchFilter.key}" 
	          escapeXml="true"/>"><c:out value="${searchFilter.key}" escapeXml="true"/></option>
	      </c:forEach>
	    </select>
	  </td>
	  <td valign="top">
	    <input type="submit" class="ss_submit" name="modifyBtnGlobal"  onClick="ss_buttonSelect('modify');"
	      value="<ssf:nlt tag="button.modify" text="Modify"/>">
	    <br>
	    <input type="submit" class="ss_submit" name="deleteBtnGlobal"  onClick="ss_buttonSelect('deleteGlobal');"
	      value="<ssf:nlt tag="button.delete" text="Delete"/>">
	  </td>
	  </tr>
	  </table>
	</c:if>
  </fieldset>

<div class="ss_formBreak"></div>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" 
  onClick="ss_buttonSelect('close');"
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
	<sec:csrfInput />

</form>

</div>
</div>
</body>
