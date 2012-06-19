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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.resourceDrivers") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Tell the Teaming GWT ui to close the administration content panel.
			if ( window.parent.ss_closeAdministrationContentPanel ) {
				window.parent.ss_closeAdministrationContentPanel();
			} else {
				ss_cancelButtonCloseWindow();
			}

			return false;
	<% 	}
		else { %>
			ss_cancelButtonCloseWindow();
			return true;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
function showAddUsersDiv() {
	hideAllDivs();
	var userDivObj = self.document.getElementById("addUserDiv");
	userDivObj.style.display = "block";
}

function showAddGroupsDiv() {
	hideAllDivs();
	var groupDivObj = self.document.getElementById("addGroupDiv");
	groupDivObj.style.display = "block";
}

function hideAllDivs() {
	var userDivObj = self.document.getElementById("addUserDiv");
	var groupDivObj = self.document.getElementById("addGroupDiv");
	groupDivObj.style.display = "none";
	userDivObj.style.display = "none";
}

function ss_showModifyDiv(id) {
	hideAllDivs();
	var modifyIdObj = self.document.getElementById("modifyId");
	if (modifyIdObj != null) {
		modifyIdObj.value = id;
		var divObj = self.document.getElementById("ss_modifyQuotaDiv" + id);
		divObj.style.display = "block";
		ss_quotaModifyDivBeingShown = id;
	}
}

</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.resourceDrivers">

<div style="padding:10px;" id="ss_manageResourceDrivers">

<c:if test="${!empty ssException}">
  <font color="red">
    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
  </font>
  <br/>
</c:if>

<form name="form1" id="form1" class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_resource_drivers" actionUrl="true"/>"
	onSubmit="ss_checkForAllUsersGroup();return true;"
>
<input type="hidden" class="ss_user_group_results" id="addedUsers" 
  name="addedUsers"/>
<input type="hidden" class="ss_user_group_results" id="addedGroups" 
  name="addedGroups"/>
	
	<div align="right">
	  <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
	</div>
		
<ssf:expandableArea title='<%= NLT.get("administration.resourceDrivers.add") %>'>
<div style="padding:0px 20px;">
<fieldset>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_resource_drivers" actionUrl="true"/>">
	<table cellspacing="6" cellpadding="4">
	<tr>
	<td valign="middle">
	  <label for="driverName">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.name"/></span>
	  </label>
	</td>
	<td valign="middle">
	  <input type="text" class="ss_text" size="70" name="driverName" id="driverName" maxlength="64">
	</td>
	</tr>

	<tr>
	<td valign="middle">
	  <label for="driverType">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.type"/></span>
	  </label>
	</td>
	<td valign="middle">
	  <select name="driverType" id="driverType">
	    <option value="filesystem" selected><ssf:nlt tag="administration.resourceDrivers.type.filesystem"/></option>
	    <option value="slide"><ssf:nlt tag="administration.resourceDrivers.type.slide"/></option>
	  </select>
	</td>
	</tr>		

	<tr>
	<td valign="middle">
	  <label for="rootPath">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.rootpath"/></span>
	  </label>
	</td>
	<td valign="middle">
	  <input type="text" class="ss_text" size="70" name="rootPath" id="rootPath" maxlength="64">
	</td>
	</tr>		

	<tr>
	<td valign="middle" colspan="2">
	  <input type="checkbox" class="ss_text" size="70" name="readonly" id="readonly">
	  <label for="readonly">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.readonly"/></span>
	  </label>
	</td>
	</tr>		

	<tr>
	<td valign="middle" style="padding-top:20px;">
	  <label for="hostUrl">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.hostUrl"/></span>
	    <span class="ss_smallprint">(<ssf:nlt tag="administration.resourceDrivers.webdavOnly"/>)</span>
	  </label>
	</td>
	<td valign="middle" style="padding-top:20px;">
	  <input type="text" class="ss_text" size="70" name="hostUrl" id="hostUrl" maxlength="64">
	</td>
	</tr>

	<tr>
	<td valign="middle" colspan="2">
	  <input type="checkbox" class="ss_text" size="70" name="allowSelfSignedCertificate" id="allowSelfSignedCertificate">
	  <label for="allowSelfSignedCertificate">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.allowSelfSignedCertificate"/></span>
	    <span class="ss_smallprint">(<ssf:nlt tag="administration.resourceDrivers.webdavOnly"/>)</span>
	  </label>
	</td>
	</tr>		
	</table>
	
	<div style="margin:10px; padding-top:20px;">
	  <span><ssf:nlt tag="administration.resourceDrivers.allowedUsersAndGroups"/></span>
	
	<div style="padding-left:20px;">
	<% /* Group selection. */ %>
	<div class="ss_entryContent">
	 	<span class="ss_labelAbove"><ssf:nlt tag="administration.resourceDrivers.addGroup" /></span>
		<ssf:find formName="${formName}" formElement="addedGroups" 
		type="group" width="150px" />
	</div>
	
	<% /* User selection. */ %>
	<div class="ss_entryContent">
		<span class="ss_labelAbove"><ssf:nlt tag="administration.resourceDrivers.addUser" /></span>
		<ssf:find formName="${formName}" formElement="addedUsers" type="user" 
		userList="<%= new java.util.HashSet() %>" width="150px" />
	</div>


	</div>
	</div>
	
	<br/>
	<br/>
	
	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add"/>">
</form>
</fieldset>
</div>
</ssf:expandableArea>
		
<br/>			

	<c:if test="${!empty ss_resourceDriverGroups}">
	  <table class="objlist" width="100%">
		<tr class="title ends">
		  <td colspan="5"><ssf:nlt tag="administration.resourceDrivers.currentSettingsGroups" /></td>
		</tr>  
	    <tr class="columnhead">
	      <td class="leftend"><ssf:nlt tag="button.delete"/></td>
	      <td><ssf:nlt tag="userlist.groupName"/></td>
	      <td><ssf:nlt tag="userlist.groupTitle"/></td>
	      <td class="rightend" width="100%">&nbsp;</td>
	    </tr>
	    <c:forEach var="group" items="${ss_resourceDriverGroups}">
	      <tr class="regrow">
	        <td class="leftend">
	          <input type="checkbox" name="deleteGroup_${group.id}" />
	        </td>
	        <td>
	          ${group.name}
	        </td>
	        <td>
	          ${group.title}
	        </td>
	        <td class="rightend">&nbsp;
	        </td>
	      </tr>
	    </c:forEach>
		  <tr class="footrow ends">
		    <td colspan="6" style="padding: 3px;">
				<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>"
		  			title="<ssf:escapeQuotes><ssf:nlt tag="administration.resourceDrivers.deleteSelectedGroups"/></ssf:escapeQuotes>"/>
			</td>
		  </tr>		
	  </table>
	</c:if>

	<c:if test="${!empty ss_resourceDriverUsers}">
	  <table class="objlist" width="100%">
	  	<tr class="title ends">
		  <td colspan="6"><ssf:nlt tag="administration.quotas.currentSettingsUser" /></td>
	    <tr class="columnhead">
	      <td class="leftend"><ssf:nlt tag="button.delete"/></td>
	      <td><ssf:nlt tag="profile.element.title"/></td>
	      <td><ssf:nlt tag="profile.element.name"/></td>
	      <td class="rightend" width="100%">&nbsp;</td>
	    </tr>
	    <c:forEach var="user" items="${ss_resourceDriverUsers}">
	      <tr class="regrow">
	        <td class="leftend">
	          <input type="checkbox" name="deleteUser_${user.id}" />
	        </td>
	        <td>
	          ${user.title}
	        </td>
	        <td>
	          <ssf:userName user="${user}"/>
	        </td>
	        <td class="rightend">&nbsp;</td>
	      </tr>
	    </c:forEach>
		  <tr class="footrow ends">
		    <td colspan="6" style="padding: 3px;">
    <input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>"
		  title="<ssf:escapeQuotes><ssf:nlt tag="administration.resourceDrivers.deleteSelectedUsers"/></ssf:escapeQuotes>"/>

			</td>
		  </tr>
	  </table>
	</c:if>


  <div style="padding: 10px 0px;">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
  </div>

</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
