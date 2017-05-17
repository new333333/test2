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
<%@ page import="org.kablink.teaming.domain.Principal" %>
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
	
	//Routine to show the right options
	var ss_resourceDriverCount = 0;
	function showOptions(selectObj, formNumber) {
		var type, obj;
		<c:forEach var="driverType" items='<%= new java.lang.String[] {"filesystem", "webdav", "cifs", "ncp_netware", "ncp_oes"} %>'>
			type = "${driverType}";
			obj = document.getElementById("options_" + formNumber + "_" + type);
			obj.style.display = "none";
		</c:forEach>
		type = selectObj.options[selectObj.selectedIndex].value;
		obj = document.getElementById("options_" + formNumber + "_" + type);
		obj.style.display = "block";
	}
	
	function confirmDelete() {
		return confirm("<ssf:nlt tag="administration.resourceDrivers.confirmDelete"/>");
	}
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

<c:if test="${!empty ss_errorMessage}">
<br/>
<div class="ss_labelLeftError">
<span><c:out value="${ss_errorMessage}"/></span>
</div>
<br/>
</c:if>

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
<c:set var="buttonName" value="addBtn"/>
<c:set var="buttonText"><ssf:nlt tag="button.add"/></c:set>
<c:set var="deleteButtonName" value=""/>
<c:set var="formAction"><ssf:url action="manage_resource_drivers" actionUrl="true"/></c:set>
<c:set var="resourceDriverGroups" value=""/>
<c:set var="resourceDriverUsers" value=""/>
<%@ include file="/WEB-INF/jsp/administration/manage_resource_drivers_form.jsp" %>
</ssf:expandableArea>
		

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.resourceDrivers.currentResourceDrivers"/></h3>

<c:set var="formNumber" value="0"/>
<c:forEach var="fsr" items="${ss_filespaceRoots}">
    <ssf:expandableArea title="${fsr.name}">
	  <c:set var="buttonName" value="modifyBtn"/>
	  <c:set var="buttonText"><ssf:nlt tag="button.modify"/></c:set>
	  <c:set var="deleteButtonName" value="deleteBtn"/>
	  <c:set var="formAction"><ssf:url action="manage_resource_drivers" actionUrl="true"><ssf:param
	    name="nameToModify" value="${fsr.name}"/></ssf:url></c:set>
	  <c:set var="resourceDriverGroups" value="${ssFunctionMap[fsr.name]['groups']}"/>
	  <c:set var="resourceDriverUsers" value="${ssFunctionMap[fsr.name]['users']}"/>
      <%@ include file="/WEB-INF/jsp/administration/manage_resource_drivers_form.jsp" %>	
    </ssf:expandableArea>
    <c:set var="formNumber" value="${formNumber + 1}"/>
</c:forEach>
	


  <div style="padding: 10px 0px;">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
  </div>
	<sec:csrfInput />

</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
