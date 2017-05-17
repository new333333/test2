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
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_roleCondition") %>' scope="request"/>
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
	function addIpAddressConditionRow(id) {
		var div;
		var span;
		var input;
		if (typeof id == 'undefined') id = "";
		if (typeof ss_nextIpAddressConditionId == 'undefined') ss_nextIpAddressConditionId = 0;
		var tableObj = document.getElementById('ss_ip_addresses'+id);
		var tableTbodyObj = document.getElementById('ss_ip_addresses_tbody'+id);

		//Build the next row
		var conditionRow = document.createElement("tr");
		conditionRow.setAttribute("id", "trConditionRow"+ss_nextIpAddressConditionId)
		tableTbodyObj.appendChild(conditionRow)
		
		//Build the ip address input text box
		var conditionTd1 = document.createElement("td");
		conditionTd1.style.verticalAlign = "middle";
		conditionTd1.style.padding = "0px 20px 16px 0px";
		conditionRow.appendChild(conditionTd1);
		input = document.createElement("input");
		input.setAttribute("type", "text");
		input.setAttribute("name", "ipAddressCondition"+ss_nextIpAddressConditionId);
		input.style.width = "200px";
		conditionTd1.appendChild(input);

		//Build the access options
		var conditionTd2 = document.createElement("td");
		conditionTd1.style.verticalAlign = "middle";
		conditionTd2.style.padding = "0px 0px 16px 0px";
		conditionRow.appendChild(conditionTd2);
		div = document.createElement("div");
		conditionTd2.appendChild(div);
		//Due to a bug in IE7, we have to use innerHTML to add radio buttons
		var allowText = "<input type='radio' name='ipAddressAccessCondition"+ss_nextIpAddressConditionId+"'";
		allowText += " value='allow' checked='checked'/><span>"+allowAccessText+"</span>\n";
		div.innerHTML = allowText;
		div = document.createElement("div");
		conditionTd2.appendChild(div);
		var denyText = "<input type='radio' name='ipAddressAccessCondition"+ss_nextIpAddressConditionId+"'";
		denyText += " value='deny'/><span>"+denyAccessText+"</span>\n";
		div.innerHTML = denyText;
		
		ss_nextIpAddressConditionId++;
	}
	
	//Add the first row
	ss_createOnLoadObj('addIpAddressConditionRow', addIpAddressConditionRow);

	function setIdToBeDeleted(id) {
		var formObj = self.document.getElementById("conditionDeleteForm");
		formObj.conditionIdToBeDeleted.value = id;
	}

	function showReIndexWarning() {
		alert("<ssf:nlt tag="administration.configure_roles.warning2"/>");
		return true;
	}
	
</script>

<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<ssf:form titleTag="administration.configure_roleCondition">
<br/>
<br/>

<c:if test="${!empty ss_errorMessage}">
<div class="ss_labelLeftError">
<span><c:out value="${ss_errorMessage}"/></span>
</div>
<br/>
<br/>
</c:if>

<div style="text-align: left; margin: 0px 10px; border: 0pt none;" 
  class="wg-tabs margintop3 marginbottom2">
  <table>
  <tr>
  <td><div class="wg-tab roundcornerSM" >
	  <a href="<ssf:url action="configure_roles" actionUrl="true"/>"
	  ><span style="color:#fff;"><ssf:nlt tag="administration.configure_roles"/></span></a>
  </div></td><td>
  <div class="wg-tab roundcornerSM on">
	  <a href="<ssf:url action="configure_roles" actionUrl="true"><ssf:param
		name="operation" value="defineConditions"/></ssf:url>"
	  ><ssf:nlt tag="administration.configure_roleCondition"/></a>
  </div>
  </td>
  </tr>
  </table>
</div>
<div class="ss_clear"></div>

<div style="display:block;padding-top:10px;" class="wg-tab-content">
<span class="ss_titlebold"><ssf:nlt tag="administration.configure_roleCondition"/></span>
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

<br/>
<br/>

<ssf:expandableArea title='<%= NLT.get("administration.configure_roleConditionIpAddress.add") %>'>
<form class="ss_style ss_form" method="post" onSubmit="return ss_onSubmit(this, false);"
	action="<ssf:url action="configure_roles" actionUrl="true"/>">
		
	<div id="ss_conditionsForm" style="padding:10px 0px 6px 20px;">
	<fieldset>
	<div >
	  <span class="ss_bold ss_required" id="ss_required_title" 
	    title="<ssf:nlt tag="administration.configure_roleCondition.titleRequired"/>">
	    <ssf:nlt tag="administration.configure_roleCondition.title"/>
	  </span><br/>
	  <input type="text" name="title" id="title" style="width:400px;" >
	</div>
	
	<div style="padding:10px 0px 0px 0px;">
	  <span class="ss_bold"><ssf:nlt tag="administration.configure_roleCondition.description"/></span><br/>
	  <textarea rows="4" Cols="80" name="description"></textarea>
	</div>
	
	<div style="padding:10px 0px 0px 0px;">
		<span class="ss_bold"><ssf:nlt tag="administration.configure_roles.conditions.ipAddresses"/></span>
		<table id="ss_ip_addresses">
		  <tbody id="ss_ip_addresses_tbody">
		  </tbody>
		</table>
		<a href="javascript: ;" onClick="addIpAddressConditionRow('');return false;">
		  <span><ssf:nlt tag="administration.configure_roles.conditions.addIpAddresses"/></span>
		</a>
	</div>
	</fieldset>
	</div>
	<br/>
	<br/>		
	<input type="submit" class="ss_submit" name="addCondition" value="<ssf:nlt tag="button.add" text="Add"/>">
	<sec:csrfInput />
</form>
</ssf:expandableArea>
<br>
<hr>
<br>
<h3><ssf:nlt tag="administration.configure_roleCondition.existing" /></h3>

<fieldset>
<table width="100%">
<tr>
  <th><ssf:nlt tag="administration.configure_roleCondition.title"/></th>
  <th><ssf:nlt tag="administration.configure_roleCondition.description"/></th>
  <th><ssf:nlt tag="administration.configure_roles.conditions.ipAddresses"/></th>
  <th></th>
</tr>
<c:forEach var="condition" items="${ssConditions}">
<jsp:useBean id="condition" type="org.kablink.teaming.security.function.Condition" />
<tr>
	<td valign="top">
	  <span>${condition.title}</span>
	</td>
	<td valign="top">
	  <span>${condition.description}</span>
	</td>
	<td valign="top">
		<c:forEach var="exp" items="${condition.includeAddressExpressions}">
		  <ssf:nlt tag="administration.configure_roles.conditions.include">
		    <ssf:param name="value" useBody="true">${exp}</ssf:param>
		  </ssf:nlt><br/>
		</c:forEach>
		<c:forEach var="exp" items="${condition.excludeAddressExpressions}">
		  <ssf:nlt tag="administration.configure_roles.conditions.exclude">
		    <ssf:param name="value" useBody="true">${exp}</ssf:param>
		  </ssf:nlt><br/>
		</c:forEach>
	</td>
	<td valign="top">
	  <form class="ss_style ss_form" method="post"
		action="<ssf:url action="configure_roles" actionUrl="true"/>">
		  <input type="submit" class="ss_submit" name="modifyCondition" 
		    value="<ssf:nlt tag="button.modify" />" 
		    onClick="ss_toggleShowDiv('modifyConditionDiv${condition.id}');return false;">
		  <input type="submit" class="ss_submit" name="deleteCondition" 
		    value="<ssf:nlt tag="button.delete" />">
		  <input type="hidden" name="conditionIdToBeDeleted" value="${condition.id}"/>
			<sec:csrfInput />
	  </form>
	</td>
</tr>
<tr>
<td colspan="4">
	<div id="modifyConditionDiv${condition.id}" style="display:none;">
<form class="ss_style ss_form" method="post" onSubmit="return ss_onSubmit(this, false);"
	action="<ssf:url action="configure_roles" actionUrl="true"/>">
		
	<div id="ss_conditionsForm" style="padding:10px 0px 6px 6px;">
	<fieldset>
	<div >
	  <span class="ss_bold ss_required" id="ss_required_title" 
	    title="<ssf:nlt tag="administration.configure_roleCondition.titleRequired"/>">
	    <ssf:nlt tag="administration.configure_roleCondition.title"/>
	  </span><br/>
	  <input type="text" name="title" id="title" style="width:400px;" value="${condition.title}">
	</div>
	
	<div style="padding:10px 0px 0px 0px;">
	  <span class="ss_bold"><ssf:nlt tag="administration.configure_roleCondition.description"/></span><br/>
	  <textarea rows="4" Cols="80" name="description">${condition.description}</textarea>
	</div>
	
	<div style="padding:10px 0px 0px 0px;">
		<span class="ss_bold"><ssf:nlt tag="administration.configure_roles.conditions.ipAddresses"/></span>
		<table id="ss_ip_addresses${condition.id}">
		  <tbody id="ss_ip_addresses_tbody${condition.id}">
		    <c:set var="counter" value="0"/>
			<c:forEach var="exp" items="${condition.includeAddressExpressions}">
			  <tr>
			  <td style="padding:0px 20px 16px 0px;">
			    <input type="text" style="width:200px;" name="ipAddressCondition_m${counter}" value="${exp}"/>
			  </td>
			  <td valign="middle" style="padding:0px 0px 16px 0px;">
			    <input type="radio" name="ipAddressAccessCondition_m${counter}" value="allow" checked="checked"/>
			    <ssf:nlt tag="administration.configure_roles.conditions.allow"/><br/>
			    <input type="radio" name="ipAddressAccessCondition_m${counter}" value="deny"/>
			    <ssf:nlt tag="administration.configure_roles.conditions.deny"/>
			  </td>
			  </tr>
			  <c:set var="counter" value="${counter + 1}"/>
			</c:forEach>
			<c:forEach var="exp" items="${condition.excludeAddressExpressions}">
			  <tr>
			  <td style="padding:0px 20px 16px 0px;">
			    <input type="text" style="width:200px;" name="ipAddressCondition_m${counter}" value="${exp}"/>
			  </td>
			  <td valign="middle" style="padding:0px 0px 16px 0px;">
			    <input type="radio" name="ipAddressAccessCondition_m${counter}" value="allow">
			    <ssf:nlt tag="administration.configure_roles.conditions.allow"/><br/>
			    <input type="radio" name="ipAddressAccessCondition_m${counter}" value="deny" checked="checked">
			    <ssf:nlt tag="administration.configure_roles.conditions.deny"/>
			  </td>
			  </tr>
			  <c:set var="counter" value="${counter + 1}"/>
			</c:forEach>
		  </tbody>
		</table>
		<a href="javascript: ;" onClick="addIpAddressConditionRow('${condition.id}');return false;">
		  <span><ssf:nlt tag="administration.configure_roles.conditions.addIpAddresses"/></span>
		</a>
	</div>
	</fieldset>
	</div>
	<br/>
	<br/>
	<input type="hidden" name="id" value="${condition.id}"/>
	<input type="submit" class="ss_submit" name="modifyCondition" 
	  onClick="showReIndexWarning();" value="<ssf:nlt tag="button.apply"/>">
	<input type="submit" class="ss_submit" onClick="ss_toggleShowDiv('modifyConditionDiv${condition.id}');return false;" 
	  value="<ssf:nlt tag="button.cancel"/>">
	<sec:csrfInput />
</form>
	</div>
</td>
</tr>

</c:forEach>
</table>
</fieldset>
<br/>


<form class="ss_style ss_form" name="${renderResponse.namespace}rolesForm" method="post" 
	action="<ssf:url action="site_administration" actionUrl="false"/>" >

	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="return handleCloseBtn();"/>
	<sec:csrfInput />
</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
