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
<% // Form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>

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

<script type="text/javascript">
	/**
	 * 
	 */
	function handleCloseBtn()
	{
	<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
			// Are we running from the administration page?
			if ( window.parent.ss_closeAdministrationContentPanel )
			{
				// Yes
				// Tell the Teaming GWT ui to close the administration content panel.
				window.parent.ss_closeAdministrationContentPanel();
			}
			else
			{
				ss_buttonSelect('cancelBtn'); 
				ss_cancelButtonCloseWindow();
			}
			return false;
	<% 	}
		else { %>
			ss_buttonSelect('cancelBtn'); 
			ss_cancelButtonCloseWindow();
			return true;
	<%	} %>
	
	}// end handleCloseBtn()
</script>

<script type="text/javascript">
var ss_checkTitleUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="false" >
	<ssf:param name="operation" value="check_binder_title" />
	</ssf:url>";
ss_addValidator("ss_titleCheck", ss_ajax_result_validator);
</script>

<c:set var="ss_profile_entry_form" value="true" scope="request" />
<form style="background: transparent;" method="<%= methodName %>" enctype="<%= enctype %>" name="<%= formName %>" 
  id="<%= formName %>" onSubmit="return ss_onSubmit(this);">


<%-- Show the ok and cancel buttons at top right --%>
<div class="ss_Tinybutton margintop2" align="right" style="width:100%; padding-right:10px;">
  <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>
  <input type="button" class="ss_submit" 
    name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" 
    onClick="ss_cancelButtonCloseWindow();return false;"/>
</div>


<%-- Show the screen name --%>
<div class="ss_entryContent">
<div class="needed-because-of-ie-bug"><div id="ss_titleCheck2" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
<div class="ss_labelAbove"><label for="name"><ssf:nlt tag="__profile_name"/></label></div>
<c:if test="${empty ssDefinitionEntry.name}">
  <input type="text" size="40" name="name" id="name" class="ss_text" autocomplete="off"
  onchange="ss_ajaxValidate(ss_checkTitleUrl, this, 'name', 'ss_titleCheck2');"/>
</c:if>
<c:if test="${!empty ssDefinitionEntry.name}">
<c:out value="${ssDefinitionEntry.name}"/>
</c:if>
</div>

<div class="ss_entryContent">
  <c:if test="${empty ssReadOnlyFields['password']}">
    <c:if test="${!empty ssDefinitionEntry.name && !ss_isBinderAdmin}">
		<div class="ss_labelAbove">
			<label for="password_original">
				<ssf:nlt tag="__profile_password_original"/>
			</label>
		</div>
		<input type="password" size="40" name="password_original" 
			id="password_original" class="ss_text" autocomplete="off" />
	</c:if>
	<div class="ss_labelAbove"><label for="password"><ssf:nlt tag="__profile_password_new"/></label></div>
	<input type="password" size="40" name="password" id="password" class="ss_text" autocomplete="off" 
	  <c:if test="${!empty ssDefinitionEntry.password}"> value="*****"</c:if> />
	<div class="ss_labelAbove"><label for="password2"><ssf:nlt tag="__profile_password_again"/></label></div>
	<input type="password" size="40" name="password2" id="password2" class="ss_text" autocomplete="off" 
	  <c:if test="${!empty ssDefinitionEntry.password}"> value="*****"</c:if> />
	<c:if test="${!empty ssDefinitionEntry.password}">
	  <input type="hidden" name="password3" id="password3" value="*****" />
	</c:if>
  </c:if>
</div>

<%-- Show the first, middle and last names --%>
<div class="ss_entryContent">
<div class="needed-because-of-ie-bug"><div id="ss_titleCheck" style="display:none; visibility:hidden;" 
      ss_ajaxResult="ok"><span class="ss_formError"></span></div></div>
<table>
<tr>
  <td style="padding-right:6px;">
	<div >
	<label for="firstName">
		<span class="ss_labelAbove"><ssf:nlt tag="__firstName"/></span>
	</label>
<c:if test="${empty ssReadOnlyFields['firstName']}">
	<input type="text" class="ss_text" name="firstName" id="firstName" 
		value="${ssDefinitionEntry.firstName}" 
		onchange="ss_ajaxValidate(ss_checkTitleUrl, this, 'firstName', 'ss_titleCheck');">
</c:if>
<c:if test="${!empty ssReadOnlyFields['firstName']}">${ssDefinitionEntry.firstName}&nbsp;</c:if>
	</div>
  </td>
  <td style="padding-right:6px;">
	<div >
	<label for="middleName">
		<span class="ss_labelAbove"><ssf:nlt tag="__middleName"/></span>
	</label>
<c:if test="${empty ssReadOnlyFields['middleName']}">
	<input type="text" class="ss_text" name="middleName" id="middleName"
		value="${ssDefinitionEntry.middleName}" 
		onchange="ss_ajaxValidate(ss_checkTitleUrl, this, 'middleName', 'ss_titleCheck');">
</c:if>
<c:if test="${!empty ssReadOnlyFields['middleName']}">${ssDefinitionEntry.middleName}&nbsp;</c:if>
	</div>
  </td>
  <td>
	<div >
	<label for="lastName">
		<span class="ss_labelAbove"><ssf:nlt tag="__lastName"/></span>
	</label>
<c:if test="${empty ssReadOnlyFields['lastName']}">
	<input type="text" class="ss_text" name="lastName" id="lastName"
		value="${ssDefinitionEntry.lastName}"
		onchange="ss_ajaxValidate(ss_checkTitleUrl, this, 'lastName', 'ss_titleCheck');">
</c:if>
<c:if test="${!empty ssReadOnlyFields['lastName']}">${ssDefinitionEntry.lastName}&nbsp;</c:if>
	</div>
  </td>
</tr>
<tr>
  <td style="padding-right:6px;">
	<div >
	<label for="emailAddress">
		<span class="ss_labelAbove"><ssf:nlt tag="__emailAddress"/></span>
	</label>
<c:if test="${empty ssReadOnlyFields['emailAddress']}">
	<input type="text" class="ss_text" name="emailAddress" id="emailAddress"
		value="${ssDefinitionEntry.emailAddress}" 
		onBlur="validateEmailAddress(this, '<ssf:escapeJavaScript><ssf:nlt tag="email.apparentInvalidEmailFormat" /></ssf:escapeJavaScript>');">
</c:if>
<c:if test="${!empty ssReadOnlyFields['emailAddress']}">${ssDefinitionEntry.emailAddress}&nbsp;</c:if>
	</div>
  </td>
  <td style="padding-right:6px;">
	<div >
	<label for="mobileEmailAddress">
		<span class="ss_labelAbove"><ssf:nlt tag="__mobileEmailAddress"/></span>
	</label>
<c:if test="${empty ssReadOnlyFields['mobileEmailAddress']}">
	<input type="text" class="ss_text" name="mobileEmailAddress" id="mobileEmailAddress"
		value="${ssDefinitionEntry.mobileEmailAddress}" 
		onBlur="validateEmailAddress(this, '<ssf:escapeJavaScript><ssf:nlt tag="email.apparentInvalidEmailFormat" /></ssf:escapeJavaScript>');">
</c:if>
<c:if test="${!empty ssReadOnlyFields['mobileEmailAddress']}">${ssDefinitionEntry.mobileEmailAddress}&nbsp;</c:if>
	</div>
  </td>
  <td>
	<div >
	<label for="txtEmailAddress">
		<span class="ss_labelAbove"><ssf:nlt tag="__txtEmailAddress"/></span>
	</label>
<c:if test="${empty ssReadOnlyFields['txtEmailAddress']}">
	<input type="text" class="ss_text" name="txtEmailAddress" id="txtEmailAddress"
		value="${ssDefinitionEntry.txtEmailAddress}" 
		onBlur="validateEmailAddress(this, '<ssf:escapeJavaScript><ssf:nlt tag="email.apparentInvalidEmailFormat" /></ssf:escapeJavaScript>');">
</c:if>
<c:if test="${!empty ssReadOnlyFields['txtEmailAddress']}">${ssDefinitionEntry.txtEmailAddress}&nbsp;</c:if>
	</div>
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

<!-- If needed, show the Text Verification controls. -->
<%@ include file="/WEB-INF/jsp/definition_elements/textVerification.jsp" %>

<%-- Show the ok and cancel buttons --%>
<div class="ss_buttonBarRight" style="margin-top: 15px;">
  <input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>
  <input type="button" class="ss_submit" 
    name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" 
    onClick="ss_cancelButtonCloseWindow();return false;"/>
</div>
	<sec:csrfInput />

</form>  

<c:if test="${!empty ssReadOnlyFields && empty ss_readOnlyFieldsFootnoteOutput}">
<br/>
<br/>
<span class="ss_smallprint"><ssf:nlt tag="profile.fieldsSynced"/></span>
<c:set var="ss_readOnlyFieldsFootnoteOutput" value="1" scope="request"/>
</c:if>

