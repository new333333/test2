<% //Date widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigElement" type="org.dom4j.Element" scope="request" />
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
<c:if test="${!empty ssFolderEntry.customAttributes[property_name].value}">
<c:set var="initDate" value="${ssFolderEntry.customAttributes[property_name].value}"/>
<jsp:useBean id="initDate" type="java.util.Date" />
<ssf:datepicker id="<%= elementName %>" 
  formName="<%= formName %>" 
  initDate="<%= initDate %>" />
</c:if>
<c:if test="${empty ssFolderEntry.customAttributes[property_name].value}">
<ssf:datepicker id="<%= elementName %>" 
  formName="<%= formName %>" 
  initDate="<%= new Date() %>" />
</c:if>
</div>
