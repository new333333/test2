<% // html %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<%@ page import="org.dom4j.Element" %>
<%
	//Get the html item being displayed
	Element item = (Element) request.getAttribute("item");
	String htmlTop = (String) request.getAttribute("property_htmlTop");
	String htmlBottom = (String) request.getAttribute("property_htmlBottom");
%>
<%= htmlTop %>
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
<%= htmlBottom %>
