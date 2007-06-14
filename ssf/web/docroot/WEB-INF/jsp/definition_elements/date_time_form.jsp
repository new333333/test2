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
	String elementName = "date_time";
	if (nameProperty != null) {
		elementName = nameProperty.attributeValue("value", "date_time");
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
<script type="text/javascript">
	ss_addValidator("<%= elementName %>", ss_date_validator, '<%= elementName %>_error', '<%= elementName %>_label');
</script>
<div class="ss_entryContent">
	<span class="ss_labelAbove" id='<%= elementName %>_label'><%= caption %><%= required %></span>
	<div id="<%= elementName %>_error" style="visibility:hidden; display:none;"><span class="ss_formError">Please enter a valid date.</span></div>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
		<jsp:useBean id="initDate" type="java.util.Date" />
		<table width="300px" class="ss_style" cellpadding="0" border="0">
			<tr>
				<td>
					<ssf:datepicker id="<%= elementName %>" 
					  formName="<%= formName %>" 
					  initDate="<%= initDate %>" />
				</td>
			</tr>
			<tr>
				<td>
					<ssf:timepicker 
						formName="<%= formName %>"
						initDate="<%= initDate %>"
						id="<%= elementName %>" />
				</td>
			</tr>
		</table>
	</c:if>
	<c:if test="${empty ssDefinitionEntry.customAttributes[property_name].value}">
		<table class="ss_style" cellpadding="0" border="0">
			<tr>
				<td>
					<ssf:datepicker id="<%= elementName %>" 
					  formName="<%= formName %>" 
					  initDate="<%= new Date() %>" />
				</td>
			</tr>
			<tr>
				<td>					  
					<ssf:timepicker 
						formName="<%= formName %>"
						initDate="<%= new Date() %>"
						id="<%= elementName %>" />
				</td>
			</tr>
		</table>						
	</c:if>
</div>
