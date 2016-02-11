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
<% // The html editor widget %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
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
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%

// TinyMCE options not current used:
//
//  plugins 
//  spellchecker_url 
//  external_image_list_url 


	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption});
	String height = (String) request.getAttribute("property_height");
	if (height == null || height.equals("")) {
		height = "300";
	}
	height = height.replaceAll("px", "").trim();
	try {
		//Don't crash if someone put in a bogus value for the height
		if (Integer.valueOf(height).intValue() < 100) height = "100";
	}
	catch(Exception e) {}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b>";
	}

	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span id=\"ss_required_"+elementName+"\" title=\""+caption2+"\" class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
	
%>
<c:set var="textValue" value=""/>
<c:set var="textFormat" value="1"/>
<c:if test="${!empty ssDefinitionEntry}">
  <c:if test="${property_name == 'description'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.description.text}"/>
    <c:set var="textFormat" value="${ssDefinitionEntry.description.format}"/>
  </c:if>
  <c:if test="${property_name == 'branding'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.branding}"/>
  </c:if>
  <c:if test="${property_name != 'description' && property_name != 'branding'}" >
    <c:set var="textValue" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
  </c:if>
  <c:if test="${!empty ss_sectionText}"><c:set var="textValue" value="${ss_sectionText}"/></c:if>
</c:if>
<c:if test="${empty ssDefinitionEntry && ssDefinitionFamily == 'miniblog'}">
  <c:set var="textFormat" value="2"/>
</c:if>
<div class="ss_entryContent ${ss_fieldModifyStyle}">
<c:if test="${empty ssReadOnlyFields[property_name] && (empty ss_fieldModifyDisabled || ss_fieldModificationsAllowed)}">

  <span class="ss_labelLeft"><%= caption %><%= required %>   </span>
  	<div class="ss_editorHints" style="padding-left:10px;">
  	<c:if test="${!empty ss_html_editor_textarea_form_helpicon}" >
  		<c:if test="${!empty ss_html_editor_textarea_form_helpicon_prefix}">
  			<em><ssf:nlt tag="${ss_html_editor_textarea_form_helpicon_prefix}"/> </em>
  		</c:if>	
  		<ssf:inlineHelp jsp="${ss_html_editor_textarea_form_helpicon}"/>
  	</c:if>
  	</div>

  <div>
  <c:if test="${textFormat != '2'}">
  <ssf:htmleditor name="${property_name}" id="ss_htmleditor_${property_name}" toolbar="${propertyValues_toolbarType[0]}"
    height="<%= height %>"><ssf:markup type="form" leaveSectionsUnchanged="true"
    entity="${ssDefinitionEntry}">${textValue}</ssf:markup></ssf:htmleditor>
  </c:if>
  <c:if test="${textFormat == '2'}">
  <textarea name="${property_name}" id="ss_htmleditor_${property_name}" 
    rows="8" cols="80"><ssf:markup type="form" leaveSectionsUnchanged="true"
    entity="${ssDefinitionEntry}">${textValue}</ssf:markup></textarea>
    <input type="hidden" name="${property_name}.format" value="2"/>
  </c:if>
  </div>
 </c:if>
 <c:if test="${!empty ssReadOnlyFields[property_name] || (!empty ss_fieldModifyDisabled && !ss_fieldModificationsAllowed)}">
 <span class="ss_labelLeft"><%= caption %><c:if test="${!empty ssReadOnlyFields[property_name]}"> &nbsp; </c:if></span>
 <ssf:markup leaveSectionsUnchanged="true"
    entity="${ssDefinitionEntry}">${textValue}</ssf:markup>
 </c:if>
</div>
<c:set var="ss_html_editor_textarea_form_helpicon" value="" scope="request" />
<c:set var="ss_html_editor_textarea_form_helpicon_prefix" value="" scope="request" />