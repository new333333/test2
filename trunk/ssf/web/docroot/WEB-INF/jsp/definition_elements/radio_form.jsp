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
<% // The radio form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
	String elementName = (String) request.getAttribute("property_name");
	String orgRadioGroupName = (String) request.getAttribute("radioGroupName");
	request.setAttribute("radioGroupName", elementName);
	String caption = (String) request.getAttribute("property_caption");
	if (caption != null && !caption.equals("")) {
		caption = caption;
	}
	String checked = "";
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<c:set var="ss_radioButtonsLayout" value="${propertyValues_layout[0]}" scope="request"/>
<div class="ss_entryContent">
<span class="ss_labelAbove"><%= caption %><%= required %></span>
<c:if test="${ss_radioButtonsLayout == 'horizontal'}">
<table cellspacing="0" cellpadding="0" class="ss_radio_button_horizontal">
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
