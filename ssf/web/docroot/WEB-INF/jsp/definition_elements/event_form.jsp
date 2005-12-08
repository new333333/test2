<% //Event widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
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
	String hD = (String) request.getAttribute("property_hasDur");
	String hR = (String) request.getAttribute("property_hasRecur");
	Boolean hasDur = new Boolean(hD);
	Boolean hasRecur = new Boolean(hR);
	if (caption == null) {caption = "";}
%>
<div class="formBreak">
<div class="labelAbove"><%= caption %></div>

<c:if test="${!empty ss_forum_entry.customAttributes[property_name]}" >
<c:set var="ev" value="${ss_forum_entry.customAttributes[property_name].value}" />
</c:if>
<jsp:useBean id="ev" type="com.sitescape.ef.domain.Event" class="com.sitescape.ef.domain.Event" />

<ssf:eventeditor id="<%= elementName %>" 
         formName="<%= formName %>" 
         initEvent="<%= ev %>"
         hasDuration="<%= hasDur %>"
         hasRecurrence="<%= hasRecur %>" />
</div>
