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
function ss_getFilterSelectionBox(obj, nameRoot, op, op2) {
	var formObj = ss_getContainingForm(obj)
	//Set the term number
	var nameObj = obj.name
	if (!obj.name) nameObj = obj.id;
	var termNumber = nameObj.substr(nameRoot.length, nameObj.length)
	formObj.ss_filterTermNumber.value = parseInt(termNumber);
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssBinder.id}"/>
    	</ssf:url>"
    url += "&operation=" + op;
    if (op2 != null && op2 != "") url += "&operation2=" + op2;
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(formObj.name);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

var ss_filterTermNumber = 1;
function ss_addFilterTerm() {
	ss_filterTermNumber++;
	var tableDiv = document.getElementById('filterTerms');
	var tbl = document.createElement("table");
	tbl.className = "ss_style";
	var tableBody = document.createElement("tbody");
	tbl.appendChild(tableBody);
	var row = document.createElement("tr");
	tableBody.appendChild(row);
	
	//Add the td cells 
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var typeListDiv = document.getElementById('typeList1');
	var newTypeListDiv = typeListDiv.cloneNode(true);
	newTypeListDiv.id = "typeList" + parseInt(ss_filterTermNumber);
	tdCell.appendChild(newTypeListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var newEntryListDiv = document.createElement("div");
	newEntryListDiv.id = "entryList" + parseInt(ss_filterTermNumber);
	tdCell.appendChild(newEntryListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var elementListDiv = document.createElement("div");
	elementListDiv.id = "elementList" + parseInt(ss_filterTermNumber);
	tdCell.appendChild(elementListDiv)
	row.appendChild(tdCell);
	
	tdCell = document.createElement("td");
	tdCell.vAlign = "top"
	var valueListDiv = document.createElement("div");
	valueListDiv.id = "valueList" + parseInt(ss_filterTermNumber);
	tdCell.appendChild(valueListDiv)
	row.appendChild(tdCell);

	var br = document.createElement("br");
	tableDiv.appendChild(br);
	tableDiv.appendChild(tbl);
	//alert(tbl.innerHTML)
}

</script>

<form name="filterData" id="filterData" class="ss_style" method="post" 
    action="<portlet:actionURL>
	<portlet:param name="action" value="build_filter"/>
	<portlet:param name="binderId" value="${ssBinder.id}"/>
	</portlet:actionURL>" 
>

<div class="ss_buttonBarRight">
<input type="submit" name="okBtn" class="ss_submit" 
  value="<ssf:nlt tag="button.ok" text="OK"/>">&nbsp;<input 
  type="submit" name="cancelBtn" class="ss_submit" 
  value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
</div>

  <fieldset class="fieldset">
    <legend class="legend"><ssf:nlt tag="filter.filterName" text="Filter name"/></legend>

	  <span class="ss_labelAbove"><ssf:nlt tag="filter.filterName" text="Filter name"/></span>
	  <input type="text" style="width:400px;" name="filterName">
  </fieldset>

<div class="ss_formBreak"/>
  
  <fieldset class="fieldset">
    <legend class="legend"><ssf:nlt tag="filter.terms" text="Filter terms"/></legend>
	<div id="filterTerms">
	  <table class="ss_style">
	  <tbody>
	  <tr>
	  <td valign="top">
	    <div id="typeList1" style="display:inline;">
	      <a href="javascript: ;" 
	        onClick="ss_getFilterSelectionBox(this.parentNode, 'typeList', 'get_filter_type', 'text');return false;">
	          <ssf:nlt tag="filter.searchText" text="Search text"/>
	      </a>
	      <br/>
	      <a href="javascript: ;" 
	        onClick="ss_getFilterSelectionBox(this.parentNode, 'typeList', 'get_filter_type', 'entry');return false;">
	          <ssf:nlt tag="filter.entryAttributes" text="Entry attributes"/>
	      </a>
	      <br/>
	      <a href="javascript: ;" 
	        onClick="ss_getFilterSelectionBox(this.parentNode, 'typeList', 'get_filter_type', 'workflow');return false;">
	          <ssf:nlt tag="filter.workflowStates" text="Workflow states"/>
	      </a>
	      <br/>
	    </div>
	  </td>
	  <td valign="top">
	    <div id="entryList1" style="display:inline;">
	    </div>
	  </td>
	  <td valign="top">
	    <div id="elementList1" style="visibility:hidden; display:inline;">
	    </div>
	  </td>
	  <td valign="top">
	    <div id="valueList1" style="visibility:hidden; display:inline;">
	    </div>
	  </td>
	  </tr>
	  </tbody>
	  </table>
	</div>
	<br/>
	<a class="ss_linkButton" href="javascript: ;" onClick="ss_addFilterTerm();return false;">Add another filter term</a>
	  
  </fieldset>
  
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">
<input type="submit" name="okBtn" class="ss_submit" 
  value="<ssf:nlt tag="button.ok" text="OK"/>">&nbsp;<input 
  type="submit" name="cancelBtn" class="ss_submit" 
  value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
</div>

<input type="hidden" name="ss_filterTermNumber" value="1"/>
</form>
<div id="ss_filter_status_message" style="display:none;"></div>
</div>

