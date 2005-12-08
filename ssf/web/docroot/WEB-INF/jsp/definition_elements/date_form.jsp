<% //Date widget form element %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
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
	if (caption == null) {caption = "";}
%>
<div class="formBreak">
<div class="labelAbove"><%= caption %></div>
<c:if test="${!empty ss_forum_entry.customAttributes[property_name].value}">
<c:set var="initDate" value="${ss_forum_entry.customAttributes[property_name].value}"/>
<jsp:useBean id="initDate" type="java.util.Date" />
<sitescape:datepicker id="<%= elementName %>" 
  formName="<%= formName %>" 
  initDate="<%= initDate %>" />
</c:if>
<c:if test="${empty ss_forum_entry.customAttributes[property_name].value}">
<sitescape:datepicker id="<%= elementName %>" 
  formName="<%= formName %>" 
  initDate="<%= new Date() %>" />
</c:if>
</div>
