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
<% //Event widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%
	Event initEvent = new Event();

	//Get the formName of event being displayed
	Element item = (Element) request.getAttribute("item");
	Element nameProperty = (Element) item.selectSingleNode("properties/property[@name='name']");
	String elementName = "event";
	if (nameProperty != null) {
		elementName = nameProperty.attributeValue("value", "event");
	}
	String formName = (String) request.getAttribute("formName");
	String caption = (String) request.getAttribute("property_caption");
	String hD = (String) request.getAttribute("property_hasDuration");
	String hR = (String) request.getAttribute("property_hasRecurrence");
	Boolean hasDur = new Boolean(hD);
	Boolean hasRecur = new Boolean(hR);
	if (caption == null) {caption = "";}
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<script type="text/javascript">
ss_addValidator("dp_<%= elementName %>", ss_date_validator, '<%= elementName %>_startError', '<%= elementName %>_label');
ss_addValidator("dp2_<%= elementName %>", ss_date_validator, '<%= elementName %>_endError', '<%= elementName %>_label');
</script>
<span class="ss_labelAbove" id='<%= elementName %>_label'><%= caption %><%= required %></span>
<div id="<%= elementName %>_startError" style="visibility:hidden; display:none;"><span class="ss_formError"><ssf:nlt tag="validation.startDateError"/></span></div>
<div id="<%= elementName %>_endError" style="visibility:hidden; display:none;"><span class="ss_formError"><ssf:nlt tag="validation.endDateError"/></span></div>

<c:choose>
	<c:when test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
		<c:set var="ev" value="${ssDefinitionEntry.customAttributes[property_name].value}" />	
	</c:when>
	<c:when test="${!empty ssInitialEvent}">
		<c:set var="ev" value="${ssInitialEvent}" />	
	</c:when>	
</c:choose>

<jsp:useBean id="ev" type="com.sitescape.team.domain.Event" class="com.sitescape.team.domain.Event" />

<ssf:eventeditor id="<%= elementName %>" 
         formName="<%= formName %>" 
         initEvent="<%= ev %>"
         hasDuration="<%= hasDur %>"
         hasRecurrence="<%= hasRecur %>" />
</div>
