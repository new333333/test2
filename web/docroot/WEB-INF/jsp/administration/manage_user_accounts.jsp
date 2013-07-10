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

<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
function showAddUsersDiv() {
	var userDivObj = self.document.getElementById("addUserDiv");
	if (userDivObj.style.display != "block") {
		hideAllDivs();
		userDivObj.style.display = "block";
	} else {
		hideAllDivs();
	}
}

function showAllUsersDiv() {
	var userDivObj = self.document.getElementById("allUserDiv");
	if (userDivObj.style.display != "block") {
		hideAllDivs();
		userDivObj.style.display = "block";
	} else {
		hideAllDivs();
	}
}

function hideAllDivs() {
	var userDivObj = self.document.getElementById("addUserDiv");
	userDivObj.style.display = "none";
	
	userDivObj = self.document.getElementById("allUserDiv");
	userDivObj.style.display = "none";
}

function ss_confirmDelete(obj) {
	var formObj = ss_findOwningElement(obj, "form");
	//Count the number of selected names
	var count = 0;
	for (var i = 0; i < formObj.elements.length; i++) {
		var child = formObj.elements[i];
		if (child.name.indexOf("disableUser_") == 0) {
			// Is this checkbox checked?
			if ( child.type == 'checkbox' && child.checked )
			{
				count++;
			}
		} else if (child.name.indexOf("addUsers") == 0) {
			var values = child.value.split(" ");
			for (var j = 0; j < values.length; j++) {
				if (values[j] != '') count++;
			}
		}
	}
	if (confirm("<ssf:nlt tag="button.deleteSelectedAccountsConfirm"/> " + count)) {
		return true;
	} else {
		return false;
	}
}

</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.userAccounts.manageUserAccounts">

<div id="ss_manageUserAccounts">
<br>

<c:if test="${!empty ssException}">
  <font color="red">
    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
  </font>
  <br/>
</c:if>

<div class="wg-tabs margintop3" style="text-align: left; border: 0px none;">
  <table>
    <tr>
	  <td>
		  <div class="wg-tab roundcornerSM" >
			  <a href="<ssf:url action="add_profile_entry" actionUrl="true">
			    <ssf:param name="context" value="adminMenu" />
			  </ssf:url>"
			  ><ssf:nlt tag="administration.userAccounts.addUserAccount"/></a>
		  </div>
	  </td>
	  <td>
		  <div class="wg-tab roundcornerSM on" >
			  <a href="<ssf:url action="manage_user_accounts" actionUrl="true"/>"
			  ><ssf:nlt tag="administration.userAccounts.disableUserAccount"/></a>
		  </div>
	  </td>
	  <td>
		  <div class="wg-tab roundcornerSM" >
			  <a href="<ssf:url action="import_profiles" actionUrl="true"/>"
			  ><ssf:nlt tag="administration.import.profiles"/></a>
		  </div>
	  </td>
    </tr>
  </table>
</div>
<div class="ss_clear"></div>

<div id="manageIndexDiv" style="display:block;" class="wg-tab-content">
<form name="form1" class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_user_accounts" actionUrl="true"/>">
	
	<div align="right">
	  <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
	</div>
</form>

	<div style="margin: 20px 0 10px 0; padding-right: 50px;">
		<span>
		  <input type="button" class="ss_submit" name="addUserBtn" 
		    value="<ssf:nlt tag="administration.userAccounts.selectIndividualAccountsToDisable"/>"
	        onClick="showAddUsersDiv();return false;"/>
	    </span>
		<span style="padding-left:20px;">
		  <input type="button" class="ss_submit" name="addUserBtn" 
		    value="<ssf:nlt tag="administration.userAccounts.selectAllAccountsToDisable"/>"
	        onClick="showAllUsersDiv();return false;"/>
	    </span>
	</div>  
	<!--Add User DIV dialog-->
	<div>
	  	<div id="addUserDiv" style="border:1px solid #babdb6; margin-bottom:20px; display: none;">
		  <form name="form1" class="ss_style ss_form" method="post" 
			action="<ssf:url action="manage_user_accounts" actionUrl="true"/>">
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
			<div>
				<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.disableSelectedAccounts"/>">
				<input type="submit" class="ss_submit" name="deleteBtn" 
				  value="<ssf:nlt tag="button.deleteSelectedAccounts"/>"
				  onClick="return ss_confirmDelete(this);"
				>
				<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
				  onClick="hideAllDivs();return false;"/>
			</div>
		  </form>
	    </div>
	</div>	
	<!--END-->
	<!--All User DIV dialog-->
	<div>
	  	<div id="allUserDiv" style="border:1px solid #babdb6; margin-bottom:20px; 
	  	  <c:if test="${!ss_showUserList}"> display: none; </c:if>">
		  <form name="form1" class="ss_style ss_form" method="post" 
			  action="<ssf:url action="manage_user_accounts" actionUrl="true"/>">
			<div class="ss_diagDivTitle">
		  		<ssf:nlt tag="administration.userAccounts.selectAccountsToDisable"/>
			</div>
			
			<c:if test="${ss_searchTotalHits > ss_pageSize}">
			  <table>
			  <tr>
			    <th colspan="2" align="center">
				  <div>
				    <span><ssf:nlt tag="title.page.n_of_m"><ssf:param 
				    	name="value" value="${ss_pageNumber}"/><ssf:param
				    	name="value" useBody="true"><fmt:formatNumber value="${ss_searchTotalHits / ss_pageSize}" 
		    				maxFractionDigits="0"/></ssf:param></ssf:nlt></span>
				  </div>
			    </th>
			  </tr>
			  <tr>
			    <td>
			      <c:if test="${ss_pageNumber <= 0}">
				    <span class="ss_light"><ssf:nlt tag="general.previousPage"/></span>
				  </c:if>
			      <c:if test="${ss_pageNumber > 0}">
				    <a href="<ssf:url action="manage_user_accounts" actionUrl="true">
				        <ssf:param name="page" value="${ss_pageNumber - 1}" />
				        </ssf:url>"
				    ><span><ssf:nlt tag="general.previousPage"/></span></a>
				  </c:if>
				</td>
				<td style="padding-left:20px;">
				  <c:if test="${(ss_pageNumber + 1) * ss_pageSize >= ss_searchTotalHits}">
					    <ssf:nlt tag="general.nextPage"/>
				  </c:if>
				  <c:if test="${(ss_pageNumber + 1) * ss_pageSize < ss_searchTotalHits}">
					  <a href="<ssf:url action="manage_user_accounts" actionUrl="true">
					      <ssf:param name="page" value="${ss_pageNumber + 1}" />
					    </ssf:url>"
					  >
					    <ssf:nlt tag="general.nextPage"/>
					  </a>
				  </c:if>
				</td>
			  </tr>
			  </table>
			</c:if>
			
			<div class="ss_diagDivContent">
				<table>
			    <c:forEach var="user" items="${ss_activeUserAccounts}">
			      <c:if test="${user['_loginName'] != 'guest' && user['_loginName'] != 'admin'}">
			      <tr>
			        <td class="leftend">
			          <input type="checkbox" name="disableUser_${user['_docId']}" />
			        </td>
			        <td style="padding-left:6px;">
			          ${user['title']}
			        </td>
			        <td style="padding-left:20px;">
			          ${user['_loginName']}
			        </td>
			      </tr>
			      </c:if>
			    </c:forEach>
				</table>
			</div>
			
			<div>
				<input type="submit" class="ss_submit" name="okBtn" 
				  value="<ssf:nlt tag="button.disableSelectedAccounts"/>">
				<input type="submit" class="ss_submit" name="deleteBtn" 
				  value="<ssf:nlt tag="button.deleteSelectedAccounts"/>"
				  onClick="return ss_confirmDelete(this);"
				>
				<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.cancel"/>"
				  onClick="hideAllDivs();return false;"/>
			</div>
		  </form>
	    </div>
	</div>	
	<!--END-->
	<c:if test="${!empty ss_disabledUserAccounts}">
	<form name="form1" class="ss_style ss_form" method="post" 
		action="<ssf:url action="manage_user_accounts" actionUrl="true"/>">
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
    <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.enableSelectedAccounts"/>"
		  title="<ssf:nlt tag="administration.userAccounts.enableSelectedAccounts"/>"/>

			</td>
		  </tr>
	  </table>
	</form>
	</c:if>

  <div style="margin-top: 50px;">
	<form name="form1" class="ss_style ss_form" method="post" 
		action="<ssf:url action="manage_user_accounts" actionUrl="true"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="return handleCloseBtn();"/>
	</form>
  </div>		  
</form>
</div>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
