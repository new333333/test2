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
<% // The radio form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="org.kablink.teaming.domain.User" scope="request" />
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:set var="ss_radioFieldModificationAllowed" value="${ss_fieldModificationsAllowed}" scope="request"/>
<c:set var="original_property_name" value="${property_name}"/>
<c:set var="original_property_caption" value="${property_caption}"/>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	String orgRadioGroupName = (String) request.getAttribute("radioGroupName");
	request.setAttribute("radioGroupName", elementName);
	String caption = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption});
	if (caption != null && !caption.equals("")) {
		caption = caption;
	}
	String checked = "";
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span id=\"ss_required_"+elementName+"\" title=\""+caption2+"\" class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<c:set var="ss_radioButtonsLayout" value="${propertyValues_layout[0]}" scope="request"/>
<div class="ss_entryContent ${ss_fieldModifyStyle}">
<span class="ss_labelAbove"><%= caption %><%= required %></span>
<c:if test="${ss_radioButtonsLayout == 'horizontal'}">
<table cellspacing="0" cellpadding="0" class="ss_radio_button_horizontal ${ss_fieldModifyStyle}">
<tr>
</c:if>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
<%
	request.setAttribute("radioGroupName", orgRadioGroupName);
%>
<c:if test="${ss_radioButtonsLayout == 'horizontal'}">
</tr>
</table>
</c:if>
</div>
  <c:if test="${property_userVersionAllowed == 'true'}">
	<c:set var="ss_radioFieldModificationAllowed" value="true" scope="request"/>
<%
	request.setAttribute("radioGroupName", elementName + "." + ssUser.getName().toString());
%>
	<div class="ss_entryContent">
	<c:set var="property_name_per_user" value="${original_property_name}.${ssUser.name}"/>
	<c:if test="${ss_radioButtonsLayout == 'horizontal'}">
		<table cellspacing="0" cellpadding="0" class="ss_radio_button_horizontal ${ss_fieldModifyStyle}">
		<tr>
	</c:if>
    	<div class="ss_labelAbove">
    	<ssf:nlt tag="element.perUser.yourVersion"><ssf:param name="value" value="${original_property_caption}"/></ssf:nlt>
    	</div>
		<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  		configElement="<%= item %>" 
  		configJspStyle="${ssConfigJspStyle}" />
<%
	request.setAttribute("radioGroupName", orgRadioGroupName);
%>
	<c:if test="${ss_radioButtonsLayout == 'horizontal'}">
		</tr>
		</table>
	</c:if>
	</div>
  </c:if>
