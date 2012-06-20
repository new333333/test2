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
<%@ page import="org.kablink.teaming.domain.ResourceDriverConfig" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div style="padding:0px 20px;">
<fieldset>
<form class="ss_style ss_form" method="post" 
	action="${formAction}">
	<table cellspacing="6" cellpadding="4">
	<tr>
	<td valign="middle">
	  <label for="driverName">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.name"/></span>
	  </label>
	</td>
	<td valign="middle">
	  <input type="text" class="ss_text" size="70" name="driverName" id="driverName" maxlength="64"
	  <c:if test="${!empty fsr}"> value="${fsr.name}" disabled="disabled"</c:if>
	  >
	</td>
	</tr>

	<tr>
	<td valign="middle">
	  <label for="driverType">
	    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.type"/></span>
	  </label>
	</td>
	<td valign="middle">
	  <c:set var="selected" value="filesystem"/>
	  <c:if test="${!empty fsr}">
	    <c:set var="selected">${fsr.driverType}</c:set>
	  </c:if>
	  <select name="driverType" id="driverType">
	    <option value="<%= ResourceDriverConfig.DriverType.filesystem %>" 
	      <c:if test="${selected == 'filesystem'}">selected</c:if>
	    ><ssf:nlt tag="administration.resourceDrivers.type.filesystem"/></option>
	    <option value="<%= ResourceDriverConfig.DriverType.webdav %>"
	      <c:if test="${selected == 'webdav'}">selected</c:if>
	    ><ssf:nlt tag="administration.resourceDrivers.type.webdav"/></option>
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
	  <input type="text" class="ss_text" size="70" name="rootPath" id="rootPath" maxlength="64"
	  <c:if test="${!empty fsr}"> value="${fsr.rootPath}" </c:if>
	  >
	</td>
	</tr>		

	<tr>
	<td valign="middle" colspan="2">
	  <input type="checkbox" class="ss_text" size="70" name="readonly" id="readonly"
	    <c:if test="${fsr.readOnly}"> checked="checked"</c:if>
	  >
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
	  <input type="text" class="ss_text" size="70" name="hostUrl" id="hostUrl" maxlength="64"
	  <c:if test="${!empty fsr}"> value="${fsr.hostUrl}" </c:if>
	  >
	</td>
	</tr>

	<tr>
	<td valign="middle" colspan="2">
	  <input type="checkbox" class="ss_text" size="70" name="allowSelfSignedCertificate" 
	    id="allowSelfSignedCertificate"
	    <c:if test="${fsr.allowSelfSignedCertificate}"> checked="checked"</c:if>
	  >
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

	<input type="submit" class="ss_submit" name="${buttonName}" value="${buttonText}">
	<c:if test="${!empty deleteButtonName}">
	  <input type="submit" class="ss_submit" name="${deleteButtonName}" style="margin-left:20px;"
	    value="<ssf:nlt tag="button.delete"/>" onClick="return confirmDelete();">
	</c:if>
</form>
</fieldset>
</div>
