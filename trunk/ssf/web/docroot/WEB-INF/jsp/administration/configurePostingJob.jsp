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
<%@ page import="com.sitescape.ef.util.NLT" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">

var <portlet:namespace/>_alias_count=0;

function <portlet:namespace/>_addAlias(alias, forums) {
   var tbl = document.getElementById('<portlet:namespace/>_alias_table');
   var body = document.getElementById('<portlet:namespace/>_alias_body');
   var row = document.createElement("tr");
   body.appendChild(row);
    
    // delete cell
	var cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
    cellLeft.setAttribute("align", "center");
	var inner = document.createElement("input");
	inner.setAttribute("type", "checkbox");
    inner.setAttribute("name", "delete" + <portlet:namespace/>_alias_count);
    inner.setAttribute("id", "delete" + <portlet:namespace/>_alias_count);
    cellLeft.appendChild(inner);
  
	//alias name
	cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
	inner = document.createElement("input");
	inner.setAttribute("type", "text");
  	inner.setAttribute("size", "32");
	inner.setAttribute("name", "alias" + <portlet:namespace/>_alias_count);
	inner.setAttribute("id", "alias" + <portlet:namespace/>_alias_count);
	inner.setAttribute("value", alias);
    cellLeft.appendChild(inner);

	//mapped forums
	cellLeft = document.createElement("td");
	row.appendChild(cellLeft);
	inner = document.createTextNode(forums);
 	cellLeft.appendChild(inner);

	<portlet:namespace/>_alias_count++;

}

</script>

<div class="ss_style ss_portlet">
<form class="ss_style ss_form" name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_posting_job"/>
		</portlet:actionURL>">
<ssf:toolbar toolbar="${ss_toolbar}" style="ss_actions_bar" />
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="incoming.job_title"/></legend>

<br/>

<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" id="enabled" name="enabled" <c:if test="${ssScheduleInfo.enabled}">checked</c:if>/>
<ssf:nlt tag="incoming.enable.all"/><br/>
</td></tr></table>

<div class="ss_divider"></div>
<span class="ss_bold"><ssf:nlt tag="incoming.schedule_title" /></span>

<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</fieldset>
<br/>

<fieldset class="ss_fieldset">
<table class="ss_style" border="0" cellspacing="0" cellpadding="3">
<tr><td>
<div id="<portlet:namespace/>_alias_div" name="<portlet:namespace/>_alias_div">
<a class="ss_linkbutton" href="javascript:" onClick="<portlet:namespace/>_addAlias('','');"><ssf:nlt tag="button.add_alias" /></a>
</td></tr><tr><td>
<table border="0" cellspacing="0" cellpadding="3" class="ss_style ss_borderTable" name="<portlet:namespace/>_alias_table" id="<portlet:namespace/>_alias_table">
<tbody name="<portlet:namespace/>_alias_body" id="<portlet:namespace/>_alias_body">

  <tr class="ss_headerRow">
  <td class="ss_finestprintgray" align="center" width="5%" scope="col"><ssf:nlt tag="incoming.delete" /></td>
  <td class="ss_bold" scope="col"><ssf:nlt tag="incoming.alias" /></td>
  <td class="ss_bold" scope="col"><ssf:nlt tag="incoming.folder" /></td>
</tr>
</tBody>
</table>
</td></tr></table>

<c:forEach var="alias" varStatus="status" items="${ssPostings}" >
<c:set var="title" value=" " scope="request"/>
<c:if test="${!empty alias.binder}">
<c:set var="title" value="${alias.binder.title}" scope="request"/>
</c:if>
<input type="hidden" id="aliasId${status.index}" name="aliasId${status.index}" value="${alias.id}"/>
<script type="text/javascript">
<portlet:namespace/>_addAlias('<c:out value="${alias.emailAddress}"/>','<c:out value="${title}"/>');
</script>
</c:forEach>


</fieldset>


</div>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>

</div>
