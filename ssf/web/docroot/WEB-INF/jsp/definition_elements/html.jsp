<% // html %>
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<%@ page import="org.dom4j.Element" %>
<%
	//Get the html item being displayed
	Element item = (Element) request.getAttribute("item");
	String htmlTop = (String) request.getAttribute("property_htmlTop");
	String htmlBottom = (String) request.getAttribute("property_htmlBottom");
%>
<%= htmlTop %>
<ssf:displayConfiguration configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ss_forum_configJspStyle %>" />
<%= htmlBottom %>
