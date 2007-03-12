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
<%@ page contentType="text/html; charset=UTF-8" %>

<ssf:ifadapter>
<body class="ss_style_body" onLoad="init();">
</ssf:ifadapter>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>

<%@ include file="/WEB-INF/jsp/forum/init.jsp" %>

<script type="text/javascript">
function init() {
	if (self.parent.ss_resizeUserGroupsIFrame) {
		self.parent.ss_resizeUserGroupsIFrame(self.document.body.scrollHeight);
	}
}
function saveResults() {
	var formObj = document.getElementById('userGroupsForm');
	var s = "";
	var items = formObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		s += items[i].id + " ";
	}
	if (self.parent.ss_saveUserGroupResults) {
		self.parent.ss_saveUserGroupResults(s, '${ssElementName}');
	}
	
	var height = self.document.body.scrollHeight;
	var divs = document.getElementsByTagName('div');
	for (var i = 0; i < divs.length; i++) {
		var divBottom = parseInt(ss_getObjectTopAbs(divs[i]) + ss_getObjectHeight(divs[i]));
		if (divBottom > height) height = divBottom;
	}
	
	if (self.parent.ss_resizeUserGroupsIFrame) {
		self.parent.ss_resizeUserGroupsIFrame(height);
	}
}
</script>

<form id="userGroupsForm" name="userGroupsForm">
<table cellspacing="10px" cellpadding="10px" width="100%">
	<tr>
		<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
	</tr>
	<tr>
		<td valign="top">
			<ssf:find formName="userGroupsForm" formElement="data_users" 
				type="user" userList="${ss_userList}"
				clickRoutine="saveResults();"/>
		</td>
	</tr>
	<tr>
		<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
	</tr>
	<tr>
		<td valign="top">
			<ssf:find formName="userGroupsForm" formElement="data_groups" 
				type="group" userList="${ss_userList}"
				clickRoutine="saveResults();"/>
		</td>
	</tr>
</table>
</form>


<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>





