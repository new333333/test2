<% // html %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<%@ page import="org.dom4j.Element" %>
<%
	//Get the html item being displayed
	Element item = (Element) request.getAttribute("item");
	String htmlTop = (String) request.getAttribute("property_htmlTop");
	String htmlBottom = (String) request.getAttribute("property_htmlBottom");
%>
<%= htmlTop %>
<ssf:displayConfiguration configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" />
<%= htmlBottom %>
