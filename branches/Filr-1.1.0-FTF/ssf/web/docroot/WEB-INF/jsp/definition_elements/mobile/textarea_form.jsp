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
<% //Textarea form element %>
<%@ page import="org.kablink.util.BrowserSniffer" %>
<%@ page import="org.kablink.teaming.util.SPropsUtil" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	if (!BrowserSniffer.is_TinyMCECapable(request, SPropsUtil.getString("TinyMCE.notSupportedUserAgents", ""))) {
%>
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption});
	String width = (String) request.getAttribute("property_width");
	String rows = (String) request.getAttribute("property_rows");
	if (rows == null || rows.equals("")) {rows = "4";}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br/>";
	}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span id=\"ss_required_"+elementName+"\" title=\""+caption2+"\" class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<c:set var="textValue" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
<c:set var="textFormat" value="${ssDefinitionEntry.customAttributes[property_name].value.format}"/>
<c:if test="${property_name == 'description'}">
  <c:set var="textValue" value="${ssDefinitionEntry.description}"/>
  <c:set var="textFormat" value="${ssDefinitionEntry.description.format}"/>
</c:if>
<div class="ss_entryContent">
<span class="ss_labelAbove">
	<label for="<%= elementName %>">
		<%= caption %><%= required %>
	</label>
</span>
<textarea name="<%= elementName %>" id="<%= elementName %>" wrap="virtual"
  rows="<%= rows %>" cols="37"
><c:out value="${textValue}"/></textarea>
<c:if test="${empty textValue}">
<input type="hidden" name="<%= elementName %>.format" value="2">
</c:if>
<c:if test="${!empty textValue}">
<input type="hidden" name="<%= elementName %>.format" value="${textFormat}">
</c:if>
</div>
<%   } else {   %>
<%@ include file="/WEB-INF/jsp/definition_elements/html_editor_textarea_form.jsp" %>
<%   }   %>
