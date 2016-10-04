<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.quotas") %>' scope="request"/>
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
var ss_validateStatusTicket = "validate"+ss_random++;
var MAX_QUOTA_SIZE = 2147483647;

/**
 * Validate the value entered for the quota size.
 */
function ss_validateSize( obj ) {
	// Did the user enter a valid number?
			
	if ( obj.value != "" && ss_checkIfNumber( obj ) ) {
		// Yes
		// Is the number greater than 0 and less than the max quota size?
		if ( obj.value > 0 && obj.value < MAX_QUOTA_SIZE ) {
			// Yes, nothing to do.
		} else {
			// No, tell the user about the problem.
			var msg;
			
			msg = "<ssf:escapeQuotes><ssf:nlt tag="administration.quota.invalidDefaultQuotaSize" /></ssf:escapeQuotes>";
			alert(msg);
			obj.value="";
		}
	}
}

function ss_checkIfNumber(obj) {
	if (!ss_isInteger(obj.value)) {
		var msg = "<ssf:escapeQuotes><ssf:nlt tag="definition.error.invalidCharacter"><ssf:param name="value" value="xxxxxx"/></ssf:nlt></ssf:escapeQuotes>";
		msg = ss_replaceSubStr(msg, "xxxxxx", obj.value);
		alert(msg);
		obj.value="";
		return false;
	}
	
	return true;
}
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
	if (ss_quotaModifyDivBeingShown != null) {
		var divObj = self.document.getElementById("ss_modifyQuotaDiv" + ss_quotaModifyDivBeingShown);
		divObj.style.display = "none";
		ss_quotaModifyDivBeingShown = null;
	}
	var modifyIdObj = self.document.getElementById("modifyId");
	if (modifyIdObj != null) modifyIdObj.value = "";
	
}

var ss_quotaModifyDivBeingShown = null;

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

var ss_validationRunning = false;
function ss_validateBinderQuotas() {
	ss_setupStatusMessageDiv();
	var statusDiv = document.getElementById("ss_operation_status");
	statusDiv.innerHTML = "<span class='ss_bold'><ssf:escapeQuotes><ssf:nlt tag="validate.binderQuota.starting"/></ssf:escapeQuotes></span>";
	var urlParams = {operation:"validate_binder_quotas", ss_statusId:ss_validateStatusTicket};
	ss_get_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams), ss_validationComplete);
	ss_validationRunning = true;
	ss_indexTimeout = setTimeout(ss_getOperationStatus, 1000);
}

function ss_validationComplete(data) {
	if (ss_indexTimeout != null) {
		clearTimeout(ss_indexTimeout);
		ss_indexTimeout = null;
	}
	ss_validationRunning = false;
	var statusDiv = document.getElementById("ss_operation_status");
	statusDiv.innerHTML = "<span class='ss_bold'><ssf:escapeQuotes><ssf:nlt tag="validate.binderQuota.completed"/></ssf:escapeQuotes></span><br/>\n";
	statusDiv.innerHTML += "<span class='ss_bold'><ssf:escapeQuotes><ssf:nlt tag="validate.binderQuota.completedScanned"/></ssf:escapeQuotes> " + data.ids + "</span><br/>\n";
	statusDiv.innerHTML += "<span class='ss_bold'><ssf:escapeQuotes><ssf:nlt tag="validate.binderQuota.completedCorrections"/></ssf:escapeQuotes> " + data.errors + "</span><br/>\n";
	<c:if test="${!ss_binderQuotasInitialized}">
	  var url = "<ssf:url action="manage_quotas" actionUrl="true"><ssf:param 
		name="binderId" value="${ssBinder.id}"/></ssf:url>";
	  self.location.href = url;
	</c:if>
}

var ss_checkStatusUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_status" />
	</ssf:url>";
	
function ss_getOperationStatus() {
	if (!ss_validationRunning) return;
	var ajaxRequest = new ss_AjaxRequest(ss_checkStatusUrl); //Create AjaxRequest object
	ajaxRequest.addKeyValue("ss_statusId",ss_validateStatusTicket);
	ajaxRequest.sendRequest();  //Send the request
	ss_indexTimeout = setTimeout(ss_getOperationStatus, 1000);
}

function ss_checkForAllUsersGroup() {
	var formObj = self.document.getElementById("form1");
	if (formObj != null && typeof formObj.addGroups.value != "undefined") {
		var groups = formObj.addGroups.value.split(" ");
		for (var i = 0; i < groups.length; i++) {
			if (groups[i] == '${ssAllUsersGroupId}' || groups[i] == '${ssAllExtUsersGroupId}') {
				alert("<ssf:escapeQuotes><ssf:nlt tag="administration.quotas.allUsers.notAllowed"/></ssf:escapeQuotes>")
				return;
			}
		}
	}
	
	// If there's a GWT UI ss_dialogClosed() function defined, call it.
    if      (typeof self.opener.ss_dialogClosed != "undefined") self.opener.ss_dialogClosed();	// Handles dialog in a popup window.
    else if (typeof window.top.ss_dialogClosed  != "undefined") window.top.ss_dialogClosed();	// Handles dialog being inline.
}

</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.quotas">

<div style="padding:10px;" id="ss_manageQuotas">

<c:if test="${!empty ssException}">
  <font color="red">
    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
  </font>
  <br/>
</c:if>

<form name="form1" id="form1" class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_quotas" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>"
	onSubmit="ss_checkForAllUsersGroup();return true;"
>
	
	<div align="right">
	  <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
	</div>

	<c:if test="${isFilr}">
		<legend class="ss_legend">
		    <input type="checkbox" name="allowPersonalStorage" 
			  <c:if test="${ss_allowPersonalStorage}">checked=checked</c:if>
			/>
			<span class="ss_bold"><ssf:nlt tag="administration.quotas.allowPersonalStorage" /></span>
			<ssf:showHelp guideName="admin" pageId="personalfolders" />
		</legend>
		<br/>
	</c:if>
			
	<c:if test="${!isFilr}">
		<fieldset class="ss_fieldset">
		  <legend class="ss_legend">
		    <input type="checkbox" name="enableBinderQuotas" 
			  <c:if test="${ss_binderQuotasEnabled}">checked=checked</c:if>
			  <c:if test="${!ss_binderQuotasInitialized}">disabled=disabled</c:if>
			/>
			<span class="ss_bold"><ssf:nlt tag="administration.quotas.binder.enable" /></span></legend>
			
			<div style="margin: 10px">
			  <c:if test="${ss_binderQuotasInitialized}">
			    <input type="checkbox" name="allowBinderQuotasByOwner" 
			    <c:if test="${ss_binderQuotasAllowBinderOwnerEnabled}">checked=checked</c:if>
			    /><ssf:nlt tag="administration.quotas.binder.allowBinderOwners"/>
			  </c:if>
			  <c:if test="${!ss_binderQuotasInitialized}">
			    <span class="ss_bold"><ssf:nlt tag="administration.quotas.binder.mustInitialize"/></span>
			  </c:if>
			</div>
		    <div style="margin: 10px; padding-top:16px;">
		      <a class="ss_button ss_bold" href="javascript: ;" onClick="ss_validateBinderQuotas();return false;"
		        title="<ssf:escapeQuotes><ssf:nlt tag="administration.quotas.binder.validateHint"/></ssf:escapeQuotes>"
		      >
		        <c:if test="${ss_binderQuotasInitialized}">
		          <span><ssf:nlt tag="administration.quotas.binder.validate"/></span>
		        </c:if>
		        <c:if test="${!ss_binderQuotasInitialized}">
		          <span><ssf:nlt tag="administration.quotas.binder.initialize"/></span>
		        </c:if>
		      </a>
		    </div>
		    <div style="padding-left:100px;">
		      <div id="ss_operation_status">
		      </div>
		    </div>
	
		  <div style="padding: 10px 0px;">
			<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
		  </div>
		</fieldset>
		
		<br/>
		<br/>
	</c:if>
		
<fieldset class="ss_fieldset">
    <legend class="ss_legend"><input type="checkbox" name="enableQuotas" 
	  <c:if test="${ss_quotasEnabled}">checked=checked</c:if>
	  />
	  <b><ssf:nlt tag="administration.quotas.enable" /></b>
	  <ssf:showHelp guideName="admin" pageId="manage-user-data-quotas" sectionId="dataquota"/>
	  </legend>
		
	<table style="margin: 10px">
	<tr>
	<td>
	  <ssf:nlt tag="administration.quotas.default"/>
	</td>
	<td style="padding-left:4px;">
	  <input type="text" size="6" name="defaultQuota" value="${ss_quotasDefault}"
	    onblur="ss_validateSize(this);" style="text-align: right; font-weight: bold;"
	  />
	  <ssf:nlt tag="administration.quotas.mb"/>
	</td>
	</tr>
	<tr>
	<td>
	  <ssf:nlt tag="administration.quotas.highWaterMark"/>
	</td>
	<td style="padding-left:4px;">
	  <input type="text" size="6" name="highWaterMark" value="${ss_quotasHighWaterMark}"
	  	onblur="ss_checkIfNumber(this);" style="text-align: right; font-weight: bold;"
	  />&nbsp;%
	</td>
	</tr>
	</table>
			
	<div style="margin: 10px">
	<div style="margin: 15px 0 7px 0; padding-right: 40px;">
		<span style="paddong-right: 5px;"><input type="button" class="ss_submit" name="addGroupBtn" 
		  value="<ssf:escapeQuotes><ssf:nlt tag="administration.quotas.addGroupQuota"/></ssf:escapeQuotes>"
		  onClick="showAddGroupsDiv();return false;"/></span>
	</div>  
	<!--Add Group DIV dialog-->
	<div class="ss_relDiv">
 	  	<div class="ss_diagSmallDiv teamingDlgBox" id="addGroupDiv" style="display: none;">
			<div class="ss_diagDivTitle">
		  		<ssf:nlt tag="administration.quotas.addGroupQuota"/>
			</div>
			<div class="ss_diagDivContent">
				<table>
  					<tr>
						<td class="ss_cellvalign"><span class="ss_bold"><ssf:nlt tag="__definition_default_group"/>:&nbsp;</span></td>
						<td valign="top">
							<ssf:find formName="form1" formElement="addGroups" type="group" width="150px" />
						</td>
					</tr>
					<tr>
						<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.quota"/>:&nbsp;</span></td>
						<td valign="top">
							<input class="ss_bold" type="text" name="addGroupQuota" size="6" style="width:50px; text-align: right;" 
							  onblur="ss_validateSize(this);" style="text-align:right;"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
						</td>
					</tr>
				</table>
			</div>
			<div class="ss_diagDivFooter">
				<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
				<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
				  onClick="hideAllDivs();return false;"/>
			</div>
	    </div>
	</div>	
	<!--END-->

	<c:if test="${!empty ss_quotasGroups}">
	  <table class="objlist" width="100%">
		<tr class="title ends">
		  <td colspan="5"><ssf:nlt tag="administration.quotas.currentSettingsGroup" /></td>
		</tr>  
	    <tr class="columnhead">
	      <td class="leftend"><ssf:nlt tag="button.delete"/></td>
	      <td><ssf:nlt tag="userlist.groupName"/></td>
	      <td><ssf:nlt tag="userlist.groupTitle"/></td>
	      <td align="center"><ssf:nlt tag="administration.quotas.quota"/></td>
	      <td class="rightend" width="100%">&nbsp;</td>
	    </tr>
	    <c:forEach var="group" items="${ss_quotasGroups}">
	      <tr class="regrow">
	        <td class="leftend">
	          <input type="checkbox" name="deleteGroup_${group.id}" />
	        </td>
	        <td>
	          <a href="javascript: ;" onClick="ss_showModifyDiv('${group.id}');return false;">${group.name}</a>
			  	<!--Edit Group DIV dialog-->
				<div class="ss_relDiv">
					<div class="ss_diagSmallDiv teamingDlgBox" id="ss_modifyQuotaDiv${group.id}" style="display: none;">
						<div class="ss_diagDivTitle">
							<ssf:nlt tag="administration.quotas.quotaModify"/>
						</div>
						<div class="ss_diagDivContent">
							<table>
								<tr class="no-regrow">
									<td class="ss_cellvalign"><span class="ss_bold"><ssf:nlt tag="__definition_default_group"/>:&nbsp;</span></td>
									<td style="white-space: nowrap">
										${group.name}&nbsp;(${group.title})
									</td>
								</tr>
								<tr class="no-regrow">
									<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.quota"/>:&nbsp;</span></td>
									<td valign="top">
										<input class="ss_bold" type="text" name="newGroupQuota_${group.id}" size="6" style="width:50px; text-align: right;" 
										  onblur="ss_validateSize(this);" style="text-align:right;" value="${group.diskQuota}"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
									</td>
								</tr>
							</table>
						</div>
						<div class="ss_diagDivFooter">
							<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
							<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
							  onClick="hideAllDivs();return false;"/>
						</div>
					</div>
				</div>	
				<!--END-->
	        </td>
	        <td>
	          ${group.title}
	        </td>
	        <td align="center">
	          ${group.diskQuota}
	        </td>
	        <td class="rightend">&nbsp;
	        </td>
	      </tr>
	    </c:forEach>
		  <tr class="footrow ends">
		    <td colspan="6" style="padding: 3px;">
				<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>"
		  			title="<ssf:escapeQuotes><ssf:nlt tag="quota.select.itemToBeDeleteChecked"/></ssf:escapeQuotes>"/>
			</td>
		  </tr>		
	  </table>
	</c:if>
	
	<div style="margin: 15px 0 7px 0; padding-right: 40px;">
		<input type="button" class="ss_submit" name="addUserBtn" 
		  value="<ssf:escapeQuotes><ssf:nlt tag="administration.quotas.addUserQuota"/></ssf:escapeQuotes>"
	      onClick="showAddUsersDiv();return false;"/>
	</div>  
	<!--Add User DIV dialog-->
	<div class="ss_relDiv">
	  	<div class="ss_diagSmallDiv teamingDlgBox" id="addUserDiv" style="display: none;">
			<div class="ss_diagDivTitle">
		  		<ssf:nlt tag="administration.quotas.addUserQuota"/>
			</div>
			<div class="ss_diagDivContent">
				<table>
  					<tr>
						<td class="ss_cellvalign"><span class="ss_bold"><ssf:nlt tag="__definition_default_user"/>:&nbsp;</span></td>
						<td valign="top">
							<ssf:find formName="form1" formElement="addUsers" type="user" width="150px" />
						</td>
					</tr>
					<tr>
						<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.quota"/>:&nbsp;</span></td>
						<td valign="top">
							<input class="ss_bold" type="text" name="addUserQuota" size="6" style="width:50px; text-align: right;" 
							  onblur="ss_validateSize(this);" style="text-align:right;"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
						</td>
					</tr>
				</table>
			</div>
			<div class="ss_diagDivFooter">
				<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
				<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
				  onClick="hideAllDivs();return false;"/>
			</div>
	    </div>
	</div>	
	<!--END-->
	<c:if test="${!empty ss_quotasUsers}">
	  <table class="objlist" width="100%">
	  	<tr class="title ends">
		  <td colspan="6"><ssf:nlt tag="administration.quotas.currentSettingsUser" /></td>
	    <tr class="columnhead">
	      <td class="leftend"><ssf:nlt tag="button.delete"/></td>
	      <td><ssf:nlt tag="profile.element.title"/></td>
	      <td><ssf:nlt tag="profile.element.name"/></td>
	      <td align="center"><ssf:nlt tag="administration.quotas.quota"/></td>
	      <td align="center"><ssf:nlt tag="administration.quotas.diskSpaceUsed"/></td>
	      <td class="rightend" width="100%">&nbsp;</td>
	    </tr>
	    <c:forEach var="user" items="${ss_quotasUsers}">
	      <tr class="regrow">
	        <td class="leftend">
	          <input type="checkbox" name="deleteUser_${user.id}" />
	        </td>
	        <td>
	          <a href="javascript: ;" onClick="ss_showModifyDiv('${user.id}');return false;">${user.title}</a>

			  	<!--Edit User DIV dialog-->
				<div class="ss_relDiv">
					<div class="ss_diagSmallDiv teamingDlgBox" id="ss_modifyQuotaDiv${user.id}" style="display: none;">
						<div class="ss_diagDivTitle">
							<ssf:nlt tag="administration.quotas.quotaModify"/>
						</div>
						<div class="ss_diagDivContent">
							<table>
								<tr class="no-regrow">
									<td class="ss_cellvalign"><span class="ss_bold"><ssf:nlt tag="__definition_default_user"/>:&nbsp;</span></td>
									<td style="white-space: nowrap">
										${user.title}&nbsp;(<ssf:userName user="${user}"/>)
									</td>
								</tr>
								<tr class="no-regrow">
									<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.quota"/>:&nbsp;</span></td>
									<td valign="top">
										<input class="ss_bold" type="text" name="newUserQuota_${user.id}" size="6" style="width:50px; text-align: right;" 
										  onblur="ss_validateSize(this);" style="text-align:right;" value="${user.diskQuota}"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
									</td>
								</tr>
							</table>
						</div>
						<div class="ss_diagDivFooter">
							<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
							<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
							  onClick="hideAllDivs();return false;"/>
						</div>
					</div>
				</div>	
				<!--END-->

	        </td>
	        <td>
	          <ssf:userName user="${user}"/>
	        </td>
	        <td align="center">
	          ${user.diskQuota}
	        </td>
	        <td align="center">
	          <fmt:formatNumber value="${user.diskSpaceUsed/1048576}" maxFractionDigits="2"/>
	        </td>
	        <td class="rightend">&nbsp;</td>
	      </tr>
	    </c:forEach>
		  <tr class="footrow ends">
		    <td colspan="6" style="padding: 3px;">
    <input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>"
		  title="<ssf:escapeQuotes><ssf:nlt tag="quota.select.itemToBeDeleteChecked"/></ssf:escapeQuotes>"/>

			</td>
		  </tr>
	  </table>
	</c:if>

	<c:if test="${!ss_quotasEnabled}">
	  <span class="ss_bold ss_errorLabel"><ssf:nlt tag="administration.quotas.notEnabled"/></span>
	    <br/>
	    <br/>
	</c:if>
	</div>
</fieldset>

<br/>

  <div style="padding: 10px 0px;">
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
  </div>
  
  <input type="hidden" name="modifyId" id="modifyId" value="" />

</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
