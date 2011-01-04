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
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.userAccounts") %>' scope="request"/>
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

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js?<%= org.kablink.teaming.util.ReleaseInfo.getContentVersion() %>"></script>
<script type="text/javascript">
function showAddUsersDiv() {
	hideAllDivs();
	var userDivObj = self.document.getElementById("addUserDiv");
	userDivObj.style.display = "block";
}

function hideAllDivs() {
	var userDivObj = self.document.getElementById("addUserDiv");
	userDivObj.style.display = "none";
}
</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.userAccounts">

<div style="padding:10px;" id="ss_manageUserAccounts">
<br>

<c:if test="${!empty ssException}">
  <font color="red">
    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
  </font>
  <br/>
</c:if>

<form name="form1" class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_user_accounts" actionUrl="true"/>">
	
	<div align="right">
	  <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
	</div>
		
	<div style="margin: 20px 0 10px 0; padding-right: 50px;">
		<input type="button" class="ss_submit" name="addUserBtn" 
		  value="<ssf:nlt tag="administration.userAccounts.selectAccountsToDisable"/>"
	  onClick="showAddUsersDiv();return false;"/>
	</div>  
	<!--Add User DIV dialog-->
	<div class="ss_relDiv">
	  	<div class="ss_diagSmallDiv" id="addUserDiv" style="display: none;">
			<div class="ss_diagDivTitle">
		  		<ssf:nlt tag="administration.userAccounts.selectAccountsToDisable"/>
			</div>
			<div class="ss_diagDivContent">
				<table>
  					<tr>
						<td class="ss_cellvalign"><span class="ss_bold"><ssf:nlt tag="__definition_default_user"/>:&nbsp;</span></td>
						<td valign="top">
							<ssf:find formName="form1" formElement="addUsers" type="user" width="150px" />
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
	<c:if test="${!empty ss_disabledUserAccounts}">
	  <table class="objlist" width="100%">
	  	<tr class="title ends">
		  <td colspan="6"><ssf:nlt tag="administration.userAccounts.disabledAccounts" /></td>
	    <tr class="columnhead">
	      <td class="leftend"><ssf:nlt tag="button.enable"/></td>
	      <td><ssf:nlt tag="profile.element.title"/></td>
	      <td><ssf:nlt tag="profile.element.name"/></td>
	      <td class="rightend" width="100%">&nbsp;</td>
	    </tr>
	    <c:forEach var="user" items="${ss_disabledUserAccounts}">
	      <tr class="regrow">
	        <td class="leftend">
	          <input type="checkbox" name="enableUser_${user.id}" />
	        </td>
	        <td>
	          <ssf:userTitle user="${user}"/>
	        </td>
	        <td>
	          <ssf:userName user="${user}"/>
	        </td>
	        <td class="rightend">&nbsp;</td>
	      </tr>
	    </c:forEach>
		  <tr class="footrow ends">
		    <td colspan="6">
    <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.enable"/>"
		  title="<ssf:nlt tag="administration.userAccounts.enableSelectedAccounts"/>"/>

			</td>
		  </tr>
	  </table>
	</c:if>

  <div style="margin-top: 50px;">
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
