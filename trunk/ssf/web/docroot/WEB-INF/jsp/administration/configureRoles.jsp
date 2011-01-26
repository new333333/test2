<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_roles") %>' scope="request"/>
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
			return false;
	<%	} %>
		
	}// end handleCloseBtn()

	var allowAccessText = "<ssf:nlt tag="administration.configure_roles.conditions.allow"/>";
	var denyAccessText = "<ssf:nlt tag="administration.configure_roles.conditions.deny"/>";
	
	var ss_nextIpAddressConditionId;
	function addIpAddressConditionRow() {
		var div;
		var span;
		var input;
		if (typeof ss_nextIpAddressConditionId == 'undefined') ss_nextIpAddressConditionId = 0;
		var tableObj = document.getElementById('ss_ip_addresses');
		var tableTbodyObj = document.getElementById('ss_ip_addresses_tbody');

		//Build the next row
		var conditionRow = document.createElement("tr");
		conditionRow.setAttribute("id", "trConditionRow"+ss_nextIpAddressConditionId)
		tableTbodyObj.appendChild(conditionRow)
		
		//Build the ip address input text box
		var conditionTd1 = document.createElement("td");
		conditionTd1.style.verticalAlign = "middle";
		conditionTd1.style.padding = "8px 20px 8px 0px";
		conditionRow.appendChild(conditionTd1);
		input = document.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("name", "ipAddressCondition"+ss_nextIpAddressConditionId);
		input.style.width = "200px";
		conditionTd1.appendChild(input);

		//Build the access options
		var conditionTd2 = document.createElement("td");
		conditionTd1.style.verticalAlign = "middle";
		conditionTd2.style.padding = "8px 0px";
		conditionRow.appendChild(conditionTd2);
		div = document.createElement("div");
		conditionTd2.appendChild(div);
		input = document.createElement("input");
		input.setAttribute("type", "radio");
		input.setAttribute("name", "ipAddressAccessCondition"+ss_nextIpAddressConditionId);
		input.setAttribute("value", "allow");
		input.setAttribute("checked", "checked");
		div.appendChild(input)
		span = document.createElement("span");
		span.appendChild(document.createTextNode(allowAccessText));
		div.appendChild(span);
		div = document.createElement("div");
		conditionTd2.appendChild(div);
		input = document.createElement("input");
		input.setAttribute("type", "radio");
		input.setAttribute("name", "ipAddressAccessCondition"+ss_nextIpAddressConditionId);
		input.setAttribute("value", "deny");
		div.appendChild(input)
		span = document.createElement("span");
		span.appendChild(document.createTextNode(denyAccessText));
		div.appendChild(span);
		
		ss_nextIpAddressConditionId++;
	}
	
	//Add the first row
	ss_createOnLoadObj('addIpAddressConditionRow', addIpAddressConditionRow);

</script>

<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<ssf:form titleTag="administration.configure_roles">
<br/>
<br/>

<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
  class="wg-tabs margintop3 marginbottom2">
  <table>
  <tr>
  <td><div class="wg-tab roundcornerSM on" >
	  <a href="<ssf:url action="configure_roles" actionUrl="true"/>"
	  ><ssf:nlt tag="administration.configure_roles"/></a>
  </div>
  </td>
  <td>
  <div class="wg-tab roundcornerSM">
	  <a href="<ssf:url action="configure_roles" actionUrl="true"><ssf:param
		name="operation" value="defineConditions"/></ssf:url>"
	  ><span style="color:#fff;"><ssf:nlt tag="administration.configure_roleCondition"/></span></a>
  </div>
  </td>
  </tr>
  </table>
</div>
<div class="ss_clear"></div>

<div style="display:block;" class="wg-tab-content">
<span class="ss_titlebold"><ssf:nlt tag="administration.configure_roles.configure" text="Configure SiteScape Forum Roles"/></span>
<c:if test="${!empty ssException}">
<font color="red">

<span class="ss_largerprint"><c:out value="${ssException}"/></span>
<br/>

<c:if test="${!empty ssRoleUsers}">
<span style="padding-left:20px;"><ssf:nlt tag="errorcode.role.inuse.by"/></span>
<br/>
</c:if>

<c:forEach var="user" items="${ssRoleUsers}">
<span style="padding-left:40px;">${user}</span>
<br/>
</c:forEach>

</font>
</c:if>

<ssf:expandableArea title='<%= NLT.get("administration.configure_roles.add") %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="configure_roles" actionUrl="true"/>">
		
	<label for="roleName"><span class="ss_bold"><ssf:nlt tag="administration.configure_roles.name" text="Name"/></span></label>
	<input type="text" class="ss_text" size="70" name="roleName" id="roleName" maxlength="64"><br/><br/>

	<label for="roleType"><span class="ss_bold"><ssf:nlt tag="administration.configure_roles.scope" text="Type"/></span></label>
	<select name="roleScope" id="roleScope">
	  <option value="binder" selected><ssf:nlt tag="administration.configure_role.type.binder"/></option>
	  <option value="entry"><ssf:nlt tag="administration.configure_role.type.entry"/></option>
	</select>
	<br/>
	<br/>		
	
	<div>
	  <a href="javascript: ;return false;" onClick="ss_toggleShowDiv('ss_conditionsForm');">
	    <span class="ss_bold"><ssf:nlt tag="administration.configure_roles.conditions"/></span>
	  </a>
	</div>
	<div id="ss_conditionsForm" style="display:none; padding:10px 0px 6px 20px;">
	<fieldset>
	<span class="ss_bold"><ssf:nlt tag="administration.configure_roles.conditions.ipAddresses"/></span>
	  <table id="ss_ip_addresses">
	    <tbody id="ss_ip_addresses_tbody">
	    </tbody>
	  </table>
	<a href="javascript: ;return false;" onClick="addIpAddressConditionRow();">
	  <span><ssf:nlt tag="administration.configure_roles.conditions.addIpAddresses"/></span>
	</a>
	</fieldset>
	</div>
	<br/>
	<br/>		
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<input type="checkbox" name="<c:out value="${operation.value}"/>"
			id="<c:out value="${operation.value}"/>">
		<label for="<c:out value="${operation.value}"/>">
			<c:out value="${operation.key}"/><br>
		</label>
	</c:forEach>		

	<input type="submit" class="ss_submit" name="addBtn" value="<ssf:nlt tag="button.add" text="Add"/>">
</form>
</ssf:expandableArea>

<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_roles.existing" text="Currently defined roles"/></h3>


<h4><ssf:nlt tag="administration.configure_roles.existing.binder" /></h4>
<c:forEach var="function2" items="${ssFunctions}">
<jsp:useBean id="function2" type="org.kablink.teaming.security.function.Function" />
<c:if test="${function2.scope == 'binder'}">
<ssf:expandableArea title='<%= NLT.getDef(function2.getName()) %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="configure_roles" actionUrl="true"/>">
	<label for="role_${function2.name}"><span class="ss_bold"><ssf:nlt tag="administration.configure_roles.name" text="Name"/></span></label>
	<input type="text" class="ss_text" size="70" name="roleName" 
		id="role_${function2.name}" value="${function2.name}"><br><br>
	<label for="scope_${function2.name}"><span class="ss_bold"><ssf:nlt tag="administration.configure_roles.scope" text="Scope"/></span></label>
	<select name="roleScope" id="scope_${function2.name}">
	  <option value="binder" selected><ssf:nlt tag="administration.configure_role.type.binder"/></option>
	  <option value="entry"><ssf:nlt tag="administration.configure_role.type.entry"/></option>
	</select><br><br>
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<c:set var="checked" value=""/>
		<c:forEach var="roleOperation" items="${function2.operations}">
			<c:if test="${roleOperation.name == operation.value}">
				<c:set var="checked" value="checked"/>
			</c:if>
		</c:forEach>
		<input type="checkbox" name="<c:out value="${operation.value}"/>"
			id="<c:out value="${function2.name}_${operation.value}"/>" <c:out value="${checked}"/>>
		<label for="<c:out value="${function2.name}_${operation.value}"/>"><c:out value="${operation.key}"/><br></label>
	</c:forEach>		
<input type="hidden" name="roleId" value="${function2.id}">
<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="modifyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
	<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete" text="Delete"/>">
</div>
</form>
<br/>
</ssf:expandableArea>
</c:if>
</c:forEach>
<br/>

<h4><ssf:nlt tag="administration.configure_roles.existing.entry" /></h4>
<c:forEach var="function3" items="${ssFunctions}">
<jsp:useBean id="function3" type="org.kablink.teaming.security.function.Function" />
<c:if test="${function3.scope == 'entry'}">
<ssf:expandableArea title='<%= NLT.getDef(function3.getName()) %>'>
<form class="ss_style ss_form" method="post" 
	action="<ssf:url action="configure_roles" actionUrl="true"/>">
	<label for="role_${function3.name}"><span class="ss_bold"><ssf:nlt tag="administration.configure_roles.name" text="Name"/></span></label>
	<input type="text" class="ss_text" size="70" name="roleName" 
		id="role_${function3.name}" value="${function3.name}"><br><br>
	<label for="scope_${function3.name}"><span class="ss_bold"><ssf:nlt tag="administration.configure_roles.scope" text="Scope"/></span></label>
	<select name="roleScope" id="scope_${function3.name}">
	  <option value="binder"><ssf:nlt tag="administration.configure_role.type.binder"/></option>
	  <option value="entry" selected><ssf:nlt tag="administration.configure_role.type.entry"/></option>
	</select><br><br>
	<c:forEach var="operation" items="${ssWorkAreaOperations}">
		<c:set var="checked" value=""/>
		<c:forEach var="roleOperation" items="${function3.operations}">
			<c:if test="${roleOperation.name == operation.value}">
				<c:set var="checked" value="checked"/>
			</c:if>
		</c:forEach>
		<input type="checkbox" name="<c:out value="${operation.value}"/>"
			id="<c:out value="${function3.name}_${operation.value}"/>" <c:out value="${checked}"/>>
		<label for="<c:out value="${function3.name}_${operation.value}"/>"><c:out value="${operation.key}"/><br></label>
	</c:forEach>		
<input type="hidden" name="roleId" value="${function3.id}">
<div class="ss_buttonBarLeft">
	<input type="submit" class="ss_submit" name="modifyBtn" value="<ssf:nlt tag="button.apply" text="Apply"/>">
	<input type="submit" class="ss_submit" name="deleteBtn" value="<ssf:nlt tag="button.delete" text="Delete"/>">
</div>
</form>
<br/>
</ssf:expandableArea>
</c:if>
</c:forEach>
<br/>

<form class="ss_style ss_form" name="${renderResponse.namespace}rolesForm" method="post" 
	action="<ssf:url action="site_administration" actionUrl="false"/>" >

	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
