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
<% //Date widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%
	//Get the formName of date being displayed
	Element item = (Element) request.getAttribute("item");
	Element nameProperty = (Element) item.selectSingleNode("properties/property[@name='name']");
	String elementName = "date";
	if (nameProperty != null) {
		elementName = nameProperty.attributeValue("value", "date");
	}
	String formName = (String) request.getAttribute("formName");
	String name = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	if (caption != null && !caption.equals("")) {
		caption = caption;
	}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<c:set var="elementName" value="<%= elementName %>" />
<div class="ss_entryContent">
<span class="ss_labelAbove" id='<%= elementName %>_label'><%= caption %><%= required %></span>
<div id="<%= elementName %>_error" style="visibility:hidden; display:none;"><span class="ss_formError">Please enter a valid date.</span></div>

	
	
	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
	</c:if>

	<div dojoType="DropdownDatePickerActivateByInput" 
		widgetId="date_${elementName}_${prefix}" 
		id="date_${elementName}_${prefix}"
		name="${elementName}_fullDate" 
		lang="${ssUser.locale.language}" 
		value="<fmt:formatDate value="${initDate}" pattern="yyyy-MM-dd" timeZone="${ssUser.timeZone.ID}"/>"></div>
	</div>
	
	<script type="text/javascript">
		dojo.require("sitescape.widget.DropdownDatePickerActivateByInput");
		djConfig.searchIds.push("date_${elementName}_${prefix}");
	</script>
</div>
