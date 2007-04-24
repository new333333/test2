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

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div class="ss_style ss_portlet">
<div style="margin:10px;">
<script type="text/javascript">

var ss_buttonSelected = "";
function ss_buttonSelect(btn) {
	ss_buttonSelected = btn
}
function checkFilterForm(obj) {
	if (ss_buttonSelected == 'delete' && 
			document.forms.filterData.selectedSearchFilter.value != "") {
		if (!confirm("<ssf:nlt tag="filter.confirmDelete"
		/>\n" + document.forms.filterData.selectedSearchFilter.value)) return false;
	}
	return true;
}

</script>

<form name="filterData" id="filterData" method="post" 
    action="<portlet:actionURL><portlet:param 
    	name="action" value="build_filter"/><portlet:param 
    	name="binderId" value="${ssBinder.id}"/><portlet:param 
    	name="binderType" value="${ssBinder.entityType}"/></portlet:actionURL>" 
	onSubmit="return(checkFilterForm(this))"
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
  </fieldset>

<div class="ss_formBreak"></div>

<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" 
  onClick="ss_buttonSelect('close');"
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

</form>

</div>
</div>