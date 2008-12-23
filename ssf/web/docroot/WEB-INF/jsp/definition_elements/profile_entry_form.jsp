<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String enctype = "multipart/form-data";
	String formName = (String) request.getAttribute("property_name");
	if (formName == null || formName.equals("")) {
		formName = WebKeys.DEFINITION_DEFAULT_FORM_NAME;
	}
	request.setAttribute("formName", formName);
	String methodName = (String) request.getAttribute("property_method");
	if (methodName == null || methodName.equals("")) {
		methodName = "post";
	}
%>
<c:set var="ss_profile_entry_form" value="true" scope="request" />
<form style="background: transparent;" method="<%= methodName %>" enctype="<%= enctype %>" name="<%= formName %>" 
  id="<%= formName %>" action="" onSubmit="return ss_onSubmit(this);">

<%-- Show the screen name --%>
<div class="ss_entryContent">
<div class="ss_labelAbove"><ssf:nlt tag="__profile_name"/></div>
<c:if test="${empty ssDefinitionEntry.name}">
  <input type="text" size="40" name="name" class="ss_text" />
</c:if>
<c:if test="${!empty ssDefinitionEntry.name}">
<c:out value="${ssDefinitionEntry.name}"/>
</c:if>
</div>

<div class="ss_entryContent">
  <c:if test="${empty ssReadOnlyFields['password'] || ss_isBinderAdmin}">
    <c:if test="${!empty ssDefinitionEntry.name && !ss_isBinderAdmin}">
		<div class="ss_labelAbove"><ssf:nlt tag="__profile_password_original"/></div>
		<input type="password" size="40" name="password_original" class="ss_text" />
	</c:if>
	<div class="ss_labelAbove"><ssf:nlt tag="__profile_password_new"/></div>
	<input type="password" size="40" name="password" class="ss_text" />
	<div class="ss_labelAbove"><ssf:nlt tag="__profile_password_again"/></div>
	<input type="password" size="40" name="password2" class="ss_text" />
  </c:if>
</div>

<%-- Show the first, middle and last names --%>
<div class="ss_entryContent">
<table>
<tr>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__firstName"/></span>
<c:if test="${empty ssReadOnlyFields['firstName']}">
	<input type="text" class="ss_text" name="firstName" value="${ssDefinitionEntry.firstName}">
</c:if>
<c:if test="${!empty ssReadOnlyFields['firstName']}">${ssDefinitionEntry.firstName}</c:if>
	</div>
  </td>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__middleName"/></span>
<c:if test="${empty ssReadOnlyFields['middleName']}">
	<input type="text" class="ss_text" name="middleName" value="${ssDefinitionEntry.middleName}">
</c:if>
<c:if test="${!empty ssReadOnlyFields['middleName']}">${ssDefinitionEntry.middleName}</c:if>
	</div>
  </td>
  <td>
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__lastName"/></span>
<c:if test="${empty ssReadOnlyFields['lastName']}">
	<input type="text" class="ss_text" name="lastName" value="${ssDefinitionEntry.lastName}">
</c:if>
<c:if test="${!empty ssReadOnlyFields['lastName']}">${ssDefinitionEntry.lastName}</c:if>
	</div>
  </td>
</tr>
<tr>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__emailAddress"/></span>
<c:if test="${empty ssReadOnlyFields['emailAddress']}">
	<input type="text" class="ss_text" name="emailAddress" value="${ssDefinitionEntry.emailAddress}">
</c:if>
<c:if test="${!empty ssReadOnlyFields['emailAddress']}">${ssDefinitionEntry.emailAddress}</c:if>
	</div>
  </td>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__mobileEmailAddress"/></span>
<c:if test="${empty ssReadOnlyFields['mobileEmailAddress']}">
	<input type="text" class="ss_text" name="mobileEmailAddress"  value="${ssDefinitionEntry.mobileEmailAddress}">
</c:if>
<c:if test="${!empty ssReadOnlyFields['mobileEmailAddress']}">${ssDefinitionEntry.mobileEmailAddress}</c:if>
	</div>
  </td>
  <td>
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__txtEmailAddress"/></span>
<c:if test="${empty ssReadOnlyFields['txtEmailAddress']}">
	<input type="text" class="ss_text" name="txtEmailAddress"  value="${ssDefinitionEntry.txtEmailAddress}">
</c:if>
<c:if test="${!empty ssReadOnlyFields['txtEmailAddress']}">${ssDefinitionEntry.txtEmailAddress}</c:if>
	</div>
  </td>
</tr>
<tr>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__zonName"/></span>
<c:if test="${empty ssReadOnlyFields['zonName']}">
	<input type="text" class="ss_text" name="zonName" value="${ssDefinitionEntry.zonName}">
</c:if>
<c:if test="${!empty ssReadOnlyFields['zonName']}">${ssDefinitionEntry.zonName}</c:if>
	</div>
  </td>
  <td>
  </td>
  <td>
  </td>
</tr>
</table>
</div>

<%-- Show all of the non-standard elements from the definition --%>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />
  
<%-- Show the photo upload browse button --%>
<c:set var="property_name" value="picture" scope="request"/>
<c:set var="property_caption" value='<%= NLT.get("__profile_entry_picture") %>' scope="request"/>
<c:set var="property_storage" value="" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/graphic_form.jsp" %>

<%-- Show the ok and cancel buttons --%>
<br/>
<div class="ss_buttonBarLeft">
  <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>
  <input type="submit" class="ss_submit" 
    name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" 
    onClick="self.window.close();return false;"/>
</div>

</form>  
