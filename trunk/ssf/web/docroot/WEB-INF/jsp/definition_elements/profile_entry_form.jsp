<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
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
<c:out value="${ssDefinitionEntry.name}"/>
</div>

<%-- Show the first, middle and last names --%>
<div class="ss_entryContent">
<table>
<tr>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__firstName"/></span>
	<input type="text" class="ss_text" name="firstName" value="${ssDefinitionEntry.firstName}">
	</div>
  </td>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__middleName"/></span>
	<input type="text" class="ss_text" name="middleName" value="${ssDefinitionEntry.middleName}">
	</div>
  </td>
  <td>
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__lastName"/></span>
	<input type="text" class="ss_text" name="lastName" value="${ssDefinitionEntry.lastName}">
	</div>
  </td>
</tr>
<tr>
  <td style="padding-right:6px;">
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__emailAddress"/></span>
	<input type="text" class="ss_text" name="emailAddress" value="${ssDefinitionEntry.emailAddress}">
	</div>
  </td>
  <td>
	<div >
	<span class="ss_labelAbove"><ssf:nlt tag="__zonName"/></span>
	<input type="text" class="ss_text" name="zonName" value="${ssDefinitionEntry.zonName}">
	</div>
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
<c:set var="property_caption" value="<%= NLT.get("__profile_entry_picture") %>" scope="request"/>
<c:set var="property_storage" value="" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/graphic_form.jsp" %>

<%-- Show the ok and cancel buttons --%>
<br/>
<div class="ss_buttonBarLeft">
  <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>
  <input type="submit" class="ss_submit" style="padding-left:15px;"
    name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" 
    onClick="self.window.close();return false;"/>
</div>

</form>  
