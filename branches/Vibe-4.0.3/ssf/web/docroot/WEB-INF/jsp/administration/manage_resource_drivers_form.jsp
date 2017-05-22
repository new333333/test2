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
	action="${formAction}" id="form${fsr.name}">
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
	  <c:set var="selectedType" value="filesystem"/>
	  <c:if test="${!empty fsr}">
	    <c:set var="selectedType">${fsr.driverType}</c:set>
	  </c:if>
	  <select name="driverType" id="driverType" onChange="showOptions(this, '${formNumber}');">
	    <option value="<%= ResourceDriverConfig.DriverType.filesystem %>" 
	      <c:if test="${selectedType == 'filesystem'}">selected</c:if>
	    ><ssf:nlt tag="administration.resourceDrivers.type.filesystem"/></option>
	    <option value="<%= ResourceDriverConfig.DriverType.webdav %>"
	      <c:if test="${selectedType == 'webdav'}">selected</c:if>
	    ><ssf:nlt tag="administration.resourceDrivers.type.webdav"/></option>
	    <ssf:ifAuthorizedByLicense featureName="com.novell.teaming.Filr">
		    <option value="<%= ResourceDriverConfig.DriverType.windows_server %>"
		      <c:if test="${selectedType == 'cifs'}">selected</c:if>
		    ><ssf:nlt tag="administration.resourceDrivers.type.cifs"/></option>
		    <option value="<%= ResourceDriverConfig.DriverType.netware %>"
		      <c:if test="${selectedType == 'ncp_netware'}">selected</c:if>
		    ><ssf:nlt tag="administration.resourceDrivers.type.ncp_netware"/></option>
		    <option value="<%= ResourceDriverConfig.DriverType.oes %>"
		      <c:if test="${selectedType == 'ncp_oes'}">selected</c:if>
		    ><ssf:nlt tag="administration.resourceDrivers.type.ncp_oes"/></option>
		    <option value="<%= ResourceDriverConfig.DriverType.famt %>"
		      <c:if test="${selectedType == 'famt'}">selected</c:if>
		    ><ssf:nlt tag="administration.resourceDrivers.type.famt"/></option>
	    </ssf:ifAuthorizedByLicense>
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
	</table>
	
	<div style="margin:10px; padding-top:20px;">
	  <span><ssf:nlt tag="administration.resourceDrivers.otherOptions"/></span>
	  
	  <c:set var="displayStyle" value="none"/>
	  <c:if test="${selectedType == 'filesystem'}"><c:set var="displayStyle" value="block"/></c:if>
	  <div style="padding-left:20px; display:${displayStyle};" id="options_${formNumber}_filesystem">
		<table cellspacing="6" cellpadding="4">		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="accountName_filesystem">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="accountName_filesystem" 
		    id="accountName_filesystem" maxlength="64"
		  <c:if test="${!empty fsr.accountName}"> value="${fsr.accountName}" </c:if>
		  >
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="password_filesystem">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountPassword"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="password" class="ss_text" size="70" name="password_filesystem" 
		    id="password_filesystem" maxlength="64">
		  <br/>
		  <input type="checkbox" name="changePassword_filesystem">
		  <span><ssf:nlt tag="administration.resourceDrivers.changePassword"/></span>
		</td>
		</tr>
		
		</table>
	  </div>
	  <c:set var="displayStyle" value="none"/>
	  <c:if test="${selectedType == 'webdav'}"><c:set var="displayStyle" value="block"/></c:if>
	  <div style="padding-left:20px; display:${displayStyle}; " id="options_${formNumber}_webdav">
		<table cellspacing="6" cellpadding="4">		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="hostUrl_webdav">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.hostUrl"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="hostUrl_webdav" id="hostUrl_webdav" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.hostUrl}" </c:if>
		  >
		</td>
		</tr>
	
		<tr>
		<td valign="middle" colspan="2">
		  <input type="checkbox" class="ss_text" size="70" name="allowSelfSignedCertificate_webdav" 
		    id="allowSelfSignedCertificate_webdav"
		    <c:if test="${fsr.allowSelfSignedCertificate}"> checked="checked"</c:if>
		  >
		  <label for="allowSelfSignedCertificate_webdav">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.allowSelfSignedCertificate"/></span>
		  </label>
		</td>
		</tr>		
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="accountName_webdav">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="accountName_webdav" id="accountName_webdav" maxlength="64"
		  <c:if test="${!empty fsr.accountName}"> value="${fsr.accountName}" </c:if>
		  >
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="password_webdav">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountPassword"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="password" class="ss_text" size="70" name="password_webdav" id="password_webdav" maxlength="64">
		  <br/>
		  <input type="checkbox" name="changePassword_webdav">
		  <span><ssf:nlt tag="administration.resourceDrivers.changePassword"/></span>
		</td>
		</tr>
		
		</table>
	  </div>

	  <div style="padding-left:20px; <c:if test="${selectedType != 'cifs'}"> display:none; </c:if>" 
	    id="options_${formNumber}_cifs"
	  >
		<table cellspacing="6" cellpadding="4">		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="accountName_cifs">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="accountName_cifs" id="accountName_cifs" maxlength="64"
		  <c:if test="${!empty fsr.accountName}"> value="${fsr.accountName}" </c:if>
		  >
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="password_cifs">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountPassword"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="password" class="ss_text" size="70" name="password_cifs" id="password_cifs" maxlength="64">
		  <br/>
		  <input type="checkbox" name="changePassword_cifs">
		  <span><ssf:nlt tag="administration.resourceDrivers.changePassword"/></span>
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="shareName_cifs">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.shareName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="shareName_cifs" id="shareName_cifs" maxlength="64"
		  <c:if test="${!empty fsr.shareName}"> value="${fsr.shareName}" </c:if>
		  >
		</td>
		</tr>
		
		</table>
	  </div>

	  <div style="padding-left:20px; <c:if test="${selectedType != 'ncp_netware'}"> display:none; </c:if>" 
	    id="options_${formNumber}_ncp_netware"
	  >
		<table cellspacing="6" cellpadding="4">		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="accountName_ncp_netware">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="accountName_ncp_netware" id="accountName_ncp_netware" maxlength="64"
		  <c:if test="${!empty fsr.accountName}"> value="${fsr.accountName}" </c:if>
		  >
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="password_ncp_netware">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountPassword"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="password" class="ss_text" size="70" name="password_ncp_netware" id="password_ncp_netware" maxlength="64">
		  <br/>
		  <input type="checkbox" name="changePassword_ncp_netware">
		  <span><ssf:nlt tag="administration.resourceDrivers.changePassword"/></span>
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="serverName_ncp_netware">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.serverName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="serverName_ncp_netware" id="serverName_ncp_netware" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.serverName}" </c:if>
		  >
		</td>
		</tr>
	
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="serverIP_ncp_netware">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.serverIP"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="serverIP_ncp_netware" id="serverIP_ncp_netware" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.serverIP}" </c:if>
		  >
		</td>
		</tr>
	
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="volume_ncp_netware">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.volume"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="volume_ncp_netware" id="volume_ncp_netware" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.volume}" </c:if>
		  >
		</td>
		</tr>
	
		</table>
	  </div>

	  <div style="padding-left:20px; <c:if test="${selectedType != 'ncp_oes'}"> display:none; </c:if>" 
	    id="options_${formNumber}_ncp_oes"
	  >
		<table cellspacing="6" cellpadding="4">		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="accountName_ncp_oes">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="accountName_ncp_oes" id="accountName_ncp_oes" maxlength="64"
		  <c:if test="${!empty fsr.accountName}"> value="${fsr.accountName}" </c:if>
		  >
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="password_ncp_oes">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountPassword"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="password" class="ss_text" size="70" name="password_ncp_oes" id="password_ncp_oes" maxlength="64">
		  <br/>
		  <input type="checkbox" name="changePassword_ncp_oes">
		  <span><ssf:nlt tag="administration.resourceDrivers.changePassword"/></span>
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="serverName_ncp_oes">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.serverDnsName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="serverName_ncp_oes" id="serverName_ncp_oes" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.serverName}" </c:if>
		  >
		</td>
		</tr>
	
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="serverIP_ncp_oes">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.serverIP"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="serverIP_ncp_oes" id="serverIP_ncp_oes" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.serverIP}" </c:if>
		  >
		</td>
		</tr>
	
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="volume_ncp_oes">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.volume"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="volume_ncp_oes" id="volume_ncp_oes" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.volume}" </c:if>
		  >
		</td>
		</tr>
	
		</table>
	  </div>

	  <div style="padding-left:20px; <c:if test="${selectedType != 'famt'}"> display:none; </c:if>" 
	    id="options_${formNumber}_famt"
	  >
		<table cellspacing="6" cellpadding="4">		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="accountName_famt">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="accountName_famt" id="accountName_famt" maxlength="64"
		  <c:if test="${!empty fsr.accountName}"> value="${fsr.accountName}" </c:if>
		  >
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="password_famt">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.accountPassword"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="password" class="ss_text" size="70" name="password_famt" id="password_famt" maxlength="64">
		  <br/>
		  <input type="checkbox" name="changePassword_famt">
		  <span><ssf:nlt tag="administration.resourceDrivers.changePassword"/></span>
		</td>
		</tr>
		
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="serverName_famt">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.serverDnsName"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="serverName_famt" id="serverName_famt" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.serverName}" </c:if>
		  >
		</td>
		</tr>
	
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="serverIP_famt">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.serverIP"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="serverIP_famt" id="serverIP_famt" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.serverIP}" </c:if>
		  >
		</td>
		</tr>
	
		<tr>
		<td valign="middle" style="padding-top:20px;">
		  <label for="volume_famt">
		    <span class="ss_bold"><ssf:nlt tag="administration.resourceDrivers.volume"/></span>
		  </label>
		</td>
		<td valign="middle" style="padding-top:20px;">
		  <input type="text" class="ss_text" size="70" name="volume_famt" id="volume_famt" maxlength="64"
		  <c:if test="${!empty fsr}"> value="${fsr.volume}" </c:if>
		  >
		</td>
		</tr>
	
		</table>
	  </div>

	</div>
	
	<div style="margin:10px; padding-top:20px;">
	  <span><ssf:nlt tag="administration.resourceDrivers.allowedUsersAndGroups"/></span>
	
	<div style="padding-left:20px;">
	<% /* Group selection. */ %>
	<div class="ss_entryContent">
	 	<span class="ss_labelAbove"><ssf:nlt tag="administration.resourceDrivers.addGroup" /></span>
		<ssf:find formName="${formName}" formElement="addedGroups" 
		type="group" userList="${resourceDriverGroups}" width="150px" />
	</div>
	
	<% /* User selection. */ %>
	<div class="ss_entryContent">
		<span class="ss_labelAbove"><ssf:nlt tag="administration.resourceDrivers.addUser" /></span>
		<ssf:find formName="${formName}" formElement="addedUsers" type="user" 
		userList="${resourceDriverUsers}" width="150px" />
	</div>

	</div>
	</div>
	
	<input type="submit" class="ss_submit" name="${buttonName}" value="${buttonText}">
	<c:if test="${!empty deleteButtonName}">
	  <input type="submit" class="ss_submit" name="${deleteButtonName}" style="margin-left:20px;"
	    value="<ssf:nlt tag="button.delete"/>" onClick="return confirmDelete();">
	</c:if>
	<sec:csrfInput />
</form>
</fieldset>
</div>
