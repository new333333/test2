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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.manage.quotas") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
function ss_checkIfNumber(obj) {
	if (!ss_isInteger(obj.value)) {
		var msg = "<ssf:nlt tag="definition.error.invalidCharacter"><ssf:param name="value" value="xxxxxx"/></ssf:nlt>";
		msg = ss_replaceSubStr(msg, "xxxxxx", obj.value);
		alert(msg);
		obj.value="";
	}
}
</script>

<div class="ss_pseudoPortal">

<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.manage.quotas">

<div style="padding:10px;" id="ss_manageQuotas">
<span class="ss_titlebold"><ssf:nlt tag="administration.manage.quotas" /></span>
<br>
<br>

<c:if test="${!empty ssException}">
  <font color="red">
    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
  </font>
  <br/>
</c:if>

<form name="form1" class="ss_style ss_form" method="post" 
	action="<ssf:url action="manage_quotas" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/></ssf:url>">
	
	<div align="right">
	  <input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="self.window.close();return false;"/>
	</div>
		
	<fieldset class="ss_fieldset">
	  <legend class="ss_legend"><ssf:nlt tag="administration.quotas.enable" /></legend>
	<div>
	  <input type="checkbox" name="enableQuotas" 
	  <c:if test="${ss_quotasEnabled}">checked=checked</c:if>
	  />
	  <ssf:nlt tag="administration.quotas.enable"/>
	</div>
		
	<table>
	<tr>
	<td style="padding-left:20px;" valign="top">
	  <ssf:nlt tag="administration.quotas.default"/>
	</td>
	<td style="padding-left:4px;" valign="top">
	  <input type="text" size="6" name="defaultQuota" value="${ss_quotasDefault}"
	    onblur="ss_checkIfNumber(this);"
	  />
	  <ssf:nlt tag="administration.quotas.mb"/>
	</td>
	</tr>
	<tr>
	<td style="padding-left:20px;" valign="top">
	  <ssf:nlt tag="administration.quotas.highWaterMark"/>
	</td>
	<td style="padding-left:4px;" valign="top">
	  <input type="text" size="6" name="highWaterMark" value="${ss_quotasHighWaterMark}"
	  	onblur="ss_checkIfNumber(this);"
	  />%
	</td>
	</tr>
	</table>
	</fieldset>
			
	<br/>
	
	<fieldset class="ss_fieldset">
	  <legend class="ss_legend"><ssf:nlt tag="administration.quotas.addUsersAndGroups" /></legend>
  <table >
  <tr>
  <td valign="top" style="padding-right:50px;">
    <div class="ss_bold"><ssf:nlt tag="administration.quotas.quota"/></div>
    <input type="text" name="addGroupQuota" size="6" style="width:40px;"
	  onblur="ss_checkIfNumber(this);"
    />
    <ssf:nlt tag="administration.quotas.mb" />
  </td>
  <td valign="top">
  <div class="ss_bold"><ssf:nlt tag="administration.quotas.addGroupQuota"/></div>
  <ssf:find formName="form1" formElement="addGroups" 
    type="group" />
  </td>
  </tr>
  
  <tr><td colspan="2">&nbsp;</td></tr>
	
  <tr>
  <td valign="top" style="padding-right:50px;">
    <div class="ss_bold"><ssf:nlt tag="administration.quotas.quota"/></div>
    <input type="text" name="addUserQuota" size="6" style="width:40px;"
	  onblur="ss_checkIfNumber(this);"
    />
    <ssf:nlt tag="administration.quotas.mb" />
  </td>
  <td valign="top">
  <div class="ss_bold"><ssf:nlt tag="administration.quotas.addUserQuota"/></div>
  <ssf:find formName="form1" formElement="addUsers" 
    type="user" />
  </td>
  </tr>
  </table>
    </fieldset>

	<br/>
	
	<c:if test="${!empty ss_quotasGroups}">
	<fieldset class="ss_fieldset">
	  <legend class="ss_legend"><ssf:nlt tag="administration.quotas.currentSettingsGroup" /></legend>
	  <table class="ss_table_data" width="100%">
	    <tr>
	      <th class="ss_table_data_TD"><ssf:nlt tag="administration.quotas.quotaChange"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="userlist.groupName"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="userlist.groupTitle"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="administration.quotas.quota"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="administration.quotas.newQuota"/></th>
	    </tr>
	    <c:forEach var="group" items="${ss_quotasGroups}">
	      <tr>
	        <td valign="top" class="ss_table_data_TD">
	          <input type="checkbox" name="changeGroup_${group.id}" />
	        </td>
	        <td valign="top" class="ss_table_data_TD">
	          ${group.name}
	        </td>
	        <td valign="top" class="ss_table_data_TD">
	          ${group.title}
	        </td>
	        <td valign="top" class="ss_table_data_TD">
	          ${group.diskQuota}
	        </td>
	        <td valign="top" class="ss_table_data_TD" nowrap>
	          <input type="text" name="newGroupQuota_${group.id}" size="6" style="width:40px;" 
	            onblur="ss_checkIfNumber(this);"
	          />
	          <ssf:nlt tag="administration.quotas.mb" />
	        </td>
	      </tr>
	    </c:forEach>
	  </table>
	  <div><span class="ss_fineprint"><ssf:nlt tag="quota.select.itemToBeModified"/></span></div>
	</fieldset>
	<br/>
	</c:if>
	
	<c:if test="${!empty ss_quotasUsers}">
	<fieldset class="ss_fieldset">
	  <legend class="ss_legend"><ssf:nlt tag="administration.quotas.currentSettingsUser" /></legend>
	  <table class="ss_table_data" width="100%">
	    <tr>
	      <th class="ss_table_data_TD"><ssf:nlt tag="administration.quotas.quotaChange"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="profile.element.name"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="profile.element.title"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="administration.quotas.quota"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="administration.quotas.diskSpaceUsed"/></th>
	      <th class="ss_table_data_TD"><ssf:nlt tag="administration.quotas.newQuota"/></th>
	    </tr>
	    <c:forEach var="user" items="${ss_quotasUsers}">
	      <tr>
	        <td valign="top" class="ss_table_data_TD">
	          <input type="checkbox" name="changeUser_${user.id}" />
	        </td>
	        <td valign="top" class="ss_table_data_TD">
	          ${user.name}
	        </td>
	        <td valign="top" class="ss_table_data_TD">
	          ${user.title}
	        </td>
	        <td valign="top" class="ss_table_data_TD">
	          ${user.diskQuota}
	        </td>
	        <td valign="top" class="ss_table_data_TD">
	          <fmt:formatNumber value="${user.diskSpaceUsed/1048576}" maxFractionDigits="2"/>
	        </td>
	        <td valign="top" class="ss_table_data_TD" nowrap>
	          <input type="text" name="newUserQuota_${user.id}" size="6" style="width:40px;" 
	            onblur="ss_checkIfNumber(this);"
	          />
	          <ssf:nlt tag="administration.quotas.mb" />
	        </td>
	      </tr>
	    </c:forEach>
	  </table>
	  <div><span class="ss_fineprint"><ssf:nlt tag="quota.select.itemToBeModified"/></span></div>
	</fieldset>
	<br/>
	</c:if>
	
	<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply"/>">
	<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="self.window.close();return false;"/>
</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
