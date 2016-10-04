<%
/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.fileUploadLimits") %>' scope="request"/>
<c:set var="helpGuideName" value="admin" scope="request" />
<c:set var="helpPageId" value="manage-file-upload-size" scope="request"/>
<c:set var="helpSectionId" value="dataquota_filelimit" scope="request" />
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
var groupUserFormsValid = true;
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
			return true;
		} else {
			// No, tell the user about the problem.
			var msg;
			
			msg = "<ssf:escapeQuotes><ssf:nlt tag="administration.quota.invalidDefaultQuotaSize" /></ssf:escapeQuotes>";
			window.setTimeout(function(){alert(msg);}, 100);
			obj.value="";
		}
	}
	return false;
}

function ss_validateSizeById( id ) {
	groupUserFormsValid = ss_validateSize( document.getElementById( id ) );
//	alert( "ss_validateSizeById( '" + id + "' ):  groupUserFormsValid" );
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
function showAddFSLUsersDiv() {
	hideAllDivs();
	var userDivObj = self.document.getElementById("addFSLUserDiv");
	userDivObj.style.display = "block";
}

function showAddFSLGroupsDiv() {
	hideAllDivs();
	var groupDivObj = self.document.getElementById("addFSLGroupDiv");
	groupDivObj.style.display = "block";
}

function hideAllDivs() {
	var userFSLDivObj = self.document.getElementById("addFSLUserDiv");
	var groupFSLDivObj = self.document.getElementById("addFSLGroupDiv");
	groupFSLDivObj.style.display = "none";
	userFSLDivObj.style.display = "none";
	if (ss_fileSizeLimitModifyDivBeingShown != null) {
		var divObj = self.document.getElementById("ss_modifyFSLDiv" + ss_fileSizeLimitModifyDivBeingShown);
		divObj.style.display = "none";
		ss_fileSizeLimitModifyDivBeingShown = null;
	}
	var modifyFSLIdObj = self.document.getElementById("modifyFSLId");
	if (modifyFSLIdObj != null) modifyFSLIdObj.value = "";
}

var ss_fileSizeLimitModifyDivBeingShown = null;

function ss_showModifyFSLDiv(id) {
	hideAllDivs();
	var modifyFSLIdObj = self.document.getElementById("modifyFSLId");
	if (modifyFSLIdObj != null) {
		modifyFSLIdObj.value = id;
		var divObj = self.document.getElementById("ss_modifyFSLDiv" + id);
		divObj.style.display = "block";
		ss_fileSizeLimitModifyDivBeingShown = id;
	}
}

function ss_checkForAllUsersGroup() {
	var formObj = self.document.getElementById("form1");
	if (formObj != null && typeof formObj.addFSLGroups.value != "undefined") {
		var groups = formObj.addFSLGroups.value.split(" ");
		for (var i = 0; i < groups.length; i++) {
			if (groups[i] == '${ssAllUsersGroupId}' || groups[i] == '${ssAllExtUsersGroupId}') {
				alert("<ssf:escapeQuotes><ssf:nlt tag="administration.quotas.allUsers.notAllowed"/></ssf:escapeQuotes>")
				return;
			}
		}
	}
}

</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.fileUploadLimits">

<div style="padding:10px;" id="ss_manageFileUploadLimits">

<c:if test="${!empty ssException}">
  <font color="red">
    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
  </font>
  <br/>
</c:if>

<form name="form1" id="form1" class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_file_upload_limits" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>"
	onSubmit="ss_checkForAllUsersGroup();return groupUserFormsValid;"
>
	<div align="right">
	  <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
	</div>

<fieldset class="ss_fieldset">
    <legend class="ss_legend">
      <span class="ss_bold"><ssf:nlt tag="administration.fileSizeLimit" /></span>
    </legend>
		
	<table style="margin: 10px">
	<tr>
	<td>
	  <ssf:nlt tag="administration.fileSizeLimit.default"/>
	</td>
	<td style="padding-left:4px;">
	  <input type="text" size="6" name="defaultFileSizeLimit" value="${ss_fileSizeLimitUserDefault}"
	    onblur="ss_validateSize(this);" style="text-align: right; font-weight: bold;"
	  />
	  <ssf:nlt tag="administration.quotas.mb"/>
	</td>
	</tr>
	</table>
			
	<div style="margin: 10px">
	<div style="margin: 15px 0 7px 0; padding-right: 40px;">
		<input type="button" class="ss_submit" name="addFSLGroupBtn" 
		  value="<ssf:escapeQuotes><ssf:nlt tag="administration.quotas.addGroupQuota"/></ssf:escapeQuotes>"
		  onClick="showAddFSLGroupsDiv();return false;"/>
	</div>  
	<!--Add Group DIV dialog-->
	<div class="ss_relDiv">
 	  	<div class="ss_diagSmallDiv teamingDlgBox" id="addFSLGroupDiv" style="display: none;">
			<div class="ss_diagDivTitle">
		  		<ssf:nlt tag="administration.quotas.addGroupQuota"/>
			</div>
			<div class="ss_diagDivContent">
				<table>
  					<tr>
						<td class="ss_cellvalign"><span class="ss_bold"><ssf:nlt tag="__definition_default_group"/>:&nbsp;</span></td>
						<td valign="top">
							<ssf:find formName="form1" formElement="addFSLGroups" type="group" width="150px" />
						</td>
					</tr>
					<tr>
						<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.fileSizeLimit"/>:&nbsp;</span></td>
						<td valign="top">
							<input class="ss_bold" type="text" name="addFSLGroupLimit" id="addFSLGroupLimit" size="6" style="width:50px; text-align: right;" 
							  style="text-align:right;"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
						</td>
					</tr>
				</table>
			</div>
			<div class="ss_diagDivFooter">
				<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_validateSizeById('addFSLGroupLimit')"; />
				<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
				  onClick="groupUserFormsValid=true;hideAllDivs();return false;"/>
			</div>
	    </div>
	</div>	
	<!--END-->

	<c:if test="${!empty ss_fileSizeLimitsGroups}">
	  <table class="objlist" width="100%">
		<tr class="title ends">
		  <td colspan="5"><ssf:nlt tag="administration.quotas.currentSettingsGroupFSL" /></td>
		</tr>  
	    <tr class="columnhead">
	      <td class="leftend"><ssf:nlt tag="button.delete"/></td>
	      <td><ssf:nlt tag="userlist.groupName"/></td>
	      <td><ssf:nlt tag="userlist.groupTitle"/></td>
	      <td align="center"><ssf:nlt tag="administration.quotas.fileSizeLimit"/></td>
	      <td class="rightend" width="100%">&nbsp;</td>
	    </tr>
	    <c:forEach var="group" items="${ss_fileSizeLimitsGroups}">
	      <tr class="regrow">
	        <td class="leftend">
	          <input type="checkbox" name="deleteFSLGroup_${group.id}" />
	        </td>
	        <td>
	          <a href="javascript: ;" onClick="ss_showModifyFSLDiv('${group.id}');return false;">${group.name}</a>
			  	<!--Edit Group DIV dialog-->
				<div class="ss_relDiv">
					<div class="ss_diagSmallDiv teamingDlgBox" id="ss_modifyFSLDiv${group.id}" style="display: none;">
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
									<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.fileSizeLimit"/>:&nbsp;</span></td>
									<td valign="top">
										<input class="ss_bold" type="text" name="newFSLGroupLimit_${group.id}" id="newFSLGroupLimit_${group.id}" size="6" style="width:50px; text-align: right;" 
										  style="text-align:right;" value="${group.fileSizeLimit}"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
									</td>
								</tr>
							</table>
						</div>
						<div class="ss_diagDivFooter">
							<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_validateSizeById('newFSLGroupLimit_${group.id}');" />
							<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
							  onClick="groupUserFormsValid=true;hideAllDivs();return false;"/>
						</div>
					</div>
				</div>	
				<!--END-->
	        </td>
	        <td>
	          ${group.title}
	        </td>
	        <td align="center">
	          ${group.fileSizeLimit}
	        </td>
	        <td class="rightend">&nbsp;
	        </td>
	      </tr>
	    </c:forEach>
		  <tr class="ends footrow">
		    <td colspan="5" style="padding: 3px;">
    <input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>"
		  title="<ssf:escapeQuotes><ssf:nlt tag="quota.select.itemToBeDeleteCheckedFSL"/></ssf:escapeQuotes>"/>
			  </td>
			</tr>
		  </table>
	</c:if>
	
	<div style="margin: 15px 0 7px 0; padding-right: 40px;">
		<input type="button" class="ss_submit" name="addFSLUserBtn" 
		  value="<ssf:escapeQuotes><ssf:nlt tag="administration.quotas.addUserQuota"/></ssf:escapeQuotes>"
	      onClick="showAddFSLUsersDiv();return false;"/>
	</div>  
	<!--Add User DIV dialog-->
	<div class="ss_relDiv">
	  	<div class="ss_diagSmallDiv teamingDlgBox" id="addFSLUserDiv" style="display: none;">
			<div class="ss_diagDivTitle">
		  		<ssf:nlt tag="administration.quotas.addUserQuota"/>
			</div>
			<div class="ss_diagDivContent">
				<table>
  					<tr>
						<td class="ss_cellvalign"><span class="ss_bold"><ssf:nlt tag="__definition_default_user"/>:&nbsp;</span></td>
						<td valign="top">
							<ssf:find formName="form1" formElement="addFSLUsers" type="user" width="150px" />
						</td>
					</tr>
					<tr>
						<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.fileSizeLimit"/>:&nbsp;</span></td>
						<td valign="top">
							<input class="ss_bold" type="text" name="addFSLUserLimit" id="addFSLUserLimit" size="6" style="width:50px; text-align: right;" 
							  style="text-align:right;"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
						</td>
					</tr>
				</table>
			</div>
			<div class="ss_diagDivFooter">
				<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_validateSizeById('addFSLUserLimit');" />
				<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
				  onClick="groupUserFormsValid=true;hideAllDivs();return false;"/>
			</div>
	    </div>
	</div>	
	<!--END-->
	<c:if test="${!empty ss_fileSizeLimitsUsers}">
	  <table class="objlist" width="100%">
	  	<tr class="title ends">
		  <td colspan="6"><ssf:nlt tag="administration.quotas.currentSettingsUserFSL" /></td>
	    <tr class="columnhead">
	      <td class="leftend"><ssf:nlt tag="button.delete"/></td>
	      <td><ssf:nlt tag="profile.element.title"/></td>
	      <td><ssf:nlt tag="profile.element.name"/></td>
	      <td align="center"><ssf:nlt tag="administration.quotas.fileSizeLimit"/></td>
	      <td class="rightend" width="100%">&nbsp;</td>
	    </tr>
	    <c:forEach var="user" items="${ss_fileSizeLimitsUsers}">
	      <tr class="regrow">
	        <td class="leftend">
	          <input type="checkbox" name="deleteFSLUser_${user.id}" />
	        </td>
	        <td>
	          <a href="javascript: ;" onClick="ss_showModifyFSLDiv('${user.id}');return false;">${user.title}</a>

			  	<!--Edit User DIV dialog-->
				<div class="ss_relDiv">
					<div class="ss_diagSmallDiv teamingDlgBox" id="ss_modifyFSLDiv${user.id}" style="display: none;">
						<div class="ss_diagDivTitle">
							<ssf:nlt tag="administration.quotas.quotaModifyFSL"/>
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
									<td><span class="ss_bold"><ssf:nlt tag="administration.quotas.fileSizeLimit"/>:&nbsp;</span></td>
									<td valign="top">
										<input class="ss_bold" type="text" name="newFSLUserLimit_${user.id}" id="newFSLUserLimit_${user.id}" size="6" style="width:50px; text-align: right;" 
										  style="text-align:right;" value="${user.fileSizeLimit}"/>&nbsp;<ssf:nlt tag="administration.quotas.mb" />
									</td>
								</tr>
							</table>
						</div>
						<div class="ss_diagDivFooter">
							<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>" onClick="ss_validateSizeById('newFSLUserLimit_${user.id}');" />
							<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
							  onClick="groupUserFormsValid=true;hideAllDivs();return false;"/>
						</div>
					</div>
				</div>	
				<!--END-->

	        </td>
	        <td>
	          <ssf:userName user="${user}"/>
	        </td>
	        <td align="center">
	          ${user.fileSizeLimit}
	        </td>
	        <td class="rightend">&nbsp;</td>
	      </tr>
	    </c:forEach>
		  <tr class="footrow ends">
		    <td colspan="6" style="padding: 3px;">
    <input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete"/>"
		  title="<ssf:escapeQuotes><ssf:nlt tag="quota.select.itemToBeDeleteCheckedFSL"/></ssf:escapeQuotes>"/>

			</td>
		  </tr>
	  </table>
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
  <input type="hidden" name="modifyFSLId" id="modifyFSLId" value="" />
</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
