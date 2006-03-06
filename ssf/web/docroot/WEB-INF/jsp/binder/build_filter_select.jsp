<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<c:if test="${empty ss_taconite_loaded}">
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<c:set var="ss_taconite_loaded" value="1" scope="request"/>
</c:if>
<div class="ss_style ss_portlet" style="margin:6px;">
<script type="text/javascript">

var ss_buttonSelected = "";
function ss_buttonSelect(btn) {
	ss_buttonSelected = btn
}
function checkFilterForm(obj) {
	if (ss_buttonSelected == 'delete' && 
			document.forms.filterData.selectedSearchFilter.value != "") {
		if (!confirm("<ssf:nlt tag="filter.confirmDelete" 
		text="Please confirm that you want to delete the filter named:"
		/>\n" + document.forms.filterData.selectedSearchFilter.value)) return false;
	}
	return true;
}

</script>

<form name="filterData" id="filterData" class="ss_style ss_form" method="post" 
    action="<portlet:actionURL>
	<portlet:param name="action" value="build_filter"/>
	<portlet:param name="binderId" value="${ssBinder.id}"/>
	</portlet:actionURL>" 
	onSubmit="return(checkFilterForm(this))"
>

<div class="ss_buttonBarRight">
<input 
  type="submit" class="ss_submit" name="closeBtn" onClick="ss_buttonSelect('close');"
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

  <fieldset class="ss_fieldset">
    <legend class="ss_legend"><ssf:nlt tag="filter.filterAddModDelete" text="Add, modify or delete filters"/></legend>

	<br>
	<input type="submit" class="ss_submit" name="addBtn"  onClick="ss_buttonSelect('add');"
	  value="<ssf:nlt tag="filter.add" text="Add a new filter"/>">
	<br>
	<br>
	<c:if test="${!empty ss_searchFilters}">
	  <table class="ss_style">
	  <tr>
	  <td valign="top">
	    <select name="selectedSearchFilter">
	      <option value=""><ssf:nlt tag="filter.selectFilter" 
	        text="--select the filter to be modified or deleted--"/></option>
	      <c:forEach var="searchFilter" items="${ss_searchFilters}">
	        <option value="<c:out value="${searchFilter.key}" escapeXml="true"/>"><c:out value="${searchFilter.key}" escapeXml="true"/></option>
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
  </fieldset>

<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" 
  onClick="ss_buttonSelect('close');"
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

</form>

