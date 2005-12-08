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
<div class="ss_portlet">
<span class="ss_titlebold">Configure incoming e-mail</span><br/>
<br/>
<c:choose>
<c:when test="${!empty ssWsDomTree}">
<jsp:useBean id="ssWsDomTree" type="org.dom4j.Document" scope="request" />
	<table border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr align="left"><td>Choose a folder</td></tr>
	<tr>
		<td>
			<div>
				<ssf:tree treeName="ssWsDomTree" treeDocument="<%= ssWsDomTree %>" rootOpen="true" />
			</div>
		</td>
	</tr>
	</table>
	<br/>
</c:when>
<c:otherwise>
<form name="<portlet:namespace/>fm" method="post" action="<portlet:actionURL>
			<portlet:param name="action" value="configure_posting"/>
		</portlet:actionURL>">
<script language="javascript" type="text/javascript">
function <portlet:namespace/>setEnable() {
	if (document.<portlet:namespace/>fm.disabled.checked) {
		document.<portlet:namespace/>fm.enabled.value = "false";
	} else {
		document.<portlet:namespace/>fm.enabled.value = "true";
	}
}
function <portlet:namespace/>addRow(alias, subject) {
  var tbl = document.getElementById('aliasTable');
  var lastRow = tbl.rows.length;
  // if there's no header row in the table, then iteration = lastRow + 1
  var iteration = lastRow;
  alert('have' + lastRow);
  var row = tbl.insertRow(lastRow);
    // alias cell
  var cellLeft = row.insertCell(0);
  var textNode = document.createTextNode(alias);
  cellLeft.appendChild(textNode);
  
    // subject cell
  var cellLeft = row.insertCell(1);
  var textNode = document.createTextNode(subject);
  cellLeft.appendChild(textNode);
  var div = document.getElementById('aliasDiv');
  div.style.visibility="visible";
}
</script>
<c:if test="${!enabled}">Incoming e-mail is disabled for this site</c:if><br/>
<span class="ss_contentbold">Folder: ${ssFolder.title}</span>

<div class="ss_divider"></div>
<br/>
<div id="aliasDiv" style="visibility:hidden">
<table id="aliasTable" border ="0" cellspacing="0" cellpadding="3">
<tr><th>Alias</th>
<th>Subject</th>
</tr>
</table>
</div>
<script language="javascript" type="text/javascript">
<c:forEach var="post" items="${ssFolder.postings}">
<portlet:namespace/>addRow(<c:out value="${post.emailAddress}"/>, <c:out value="${post.subject}"/>)
</c:forEach>
</script>
<input type="button" value="addRow" onClick="<portlet:namespace/>addRow('kkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkkme', 'metoo');"/>
<br/>
<div class="ss_divider"></div>
<br/>
<input class="ss_submit" type="submit" name="okBtn" value="Ok">
<input class="ss_submit" type="submit" name="cancelBtn" value="Cancel">
</form>

</c:otherwise>
</c:choose>
</div>
		