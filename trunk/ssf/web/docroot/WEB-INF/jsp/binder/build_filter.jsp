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

<script language="JavaScript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<div class="ss_style ss_portlet" style="margin:6px;">
<script type="text/javascript" language="javascript">
function ss_getElements(obj) {
	//Set the term number
	var nameRoot = "ss_filter_entry_def_id"
	var termNumber = obj.name.substr(nameRoot.length, obj.name.length)
	obj.form.ss_filterTermNumber.value = parseInt(termNumber);
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="get_entry_elements" />
		<ssf:param name="binderId" value="${ssBinder.id}"/>
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(obj.form.name);
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_getElementValue(obj) {
	//Set the term number
	var nameRoot = "elementName"
	var termNumber = obj.name.substr(nameRoot.length, obj.name.length)
	obj.form.ss_filterTermNumber.value = parseInt(termNumber);
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="get_element_values" />
		<ssf:param name="binderId" value="${ssBinder.id}"/>
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(obj.form.name);
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

var ss_filterTermNumber = 1;
function ss_addFilterTerm() {
	ss_filterTermNumber++;
	var tableBody = document.getElementById('filterTermsBody');
	var row = document.createElement("tr");
	tableBody.appendChild(row);
	
	//Add the td cells 
	tdCell = document.createElement("td");
	var entryListSelect = document.getElementById('ss_filter_entry_def_id1');
	var newEntryListSelect = entryListSelect.cloneNode(true);
	newEntryListSelect.id = "ss_filter_entry_def_id" + parseInt(ss_filterTermNumber);
	newEntryListSelect.name = "ss_filter_entry_def_id" + parseInt(ss_filterTermNumber);

	var newEntryListDiv = document.createElement("div");
	newEntryListDiv.id = "entryList" + parseInt(ss_filterTermNumber);
	newEntryListDiv.appendChild(newEntryListSelect)
	
	tdCell.appendChild(newEntryListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	var elementListDiv = document.createElement("div");
	elementListDiv.id = "elementList" + parseInt(ss_filterTermNumber);
	tdCell.appendChild(elementListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	var valueListDiv = document.createElement("div");
	valueListDiv.id = "valueList" + parseInt(ss_filterTermNumber);
	tdCell.appendChild(valueListDiv)
	row.appendChild(tdCell);

	//alert(tableBody.innerHTML)
}

</script>

<form name="filterData" id="filterData" class="ss_style" method="post" 
    action="<portlet:actionURL>
	<portlet:param name="action" value="build_filter"/>
	<portlet:param name="binderId" value="${ssBinder.id}"/>
	</portlet:actionURL>" 
>

<div class="ss_buttonBarRight">
<input type="submit" name="closeBtn" class="ss_submit" 
  value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

  <fieldset class="fieldset">
    <legend class="legend"><ssf:nlt tag="filter.filterName" text="Filter name"/></legend>

	  <span class="ss_labelAbove"><ssf:nlt tag="filter.name" text="Name"/></span>
	  <input type="text" width="200px" name="filterName">
  </fieldset>

<div class="ss_formBreak"/>
  
  <fieldset class="fieldset">
    <legend class="legend"><ssf:nlt tag="filter.searchParameters" text="Search parameters"/></legend>

	  <span class="ss_labelAbove"><ssf:nlt tag="filter.searchTextIfAny" text="Search text (if any)"/></span>
	  <input type="text" width="300px" name="searchText">
	  
	  <br>
	  
	  <table id="filterTerms" class="ss_style">
	  <tbody id="filterTermsBody">
	  <tr>
	  <th align="left">Entry type</th>
	  <th align="left">Element</th>
	  <th align="left">Value(s)</th>
	  </tr>
	  <tr>
	  <td align="center" valign="top">
	    <div id="entryList1">
	    	<select name="ss_filter_entry_def_id1" id="ss_filter_entry_def_id1" 
	    	  onChange="ss_getElements(this)">
	    	  <option value="" selected>--select an entry type--</option>
			    <c:forEach var="item" items="${ssPublicEntryDefinitions}">
			      <option value="<c:out value="${item.value.id}"/>">
			      <c:out value="${item.value.title}"/></option>
			    </c:forEach>
	    	</select>
	    </div>
	  </td>
	  <td align="center" valign="top">
	    <div id="elementList1" style="visibility:hidden;">
	    	<select id="elementName1" name="elementName1" 
	    	  onChange="ss_getElementValue(this)">
	    	</select>
	    </div>
	  </td>
	  <td align="center" valign="top">
	    <div id="valueList1" style="visibility:hidden;">
	    	<select name="elementValue1">
	    	</select>
	    </div>
	  </td>
	  </tr>
	  </tbody>
	  </table>
	  <a href="javascript: ;" onClick="ss_addFilterTerm();return false;">Add another filter term</a>
	  
  </fieldset>
  
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" name="closeBtn" class="ss_submit" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>

<input type="hidden" name="ss_filterTermNumber" value="1"/>
</form>
<div id="ss_filter_status_message" style="display:none;"></div>
</div>

