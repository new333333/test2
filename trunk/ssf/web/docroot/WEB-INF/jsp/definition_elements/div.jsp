<% //div %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String id = (String) request.getAttribute("property_id");
	String style = (String) request.getAttribute("property_style");
	
	if (!id.equals("")) {
		id = "id='"+id+"'";
	}
	if (!style.equals("")) {
		style = "style='"+style+"'";
	}
%>

<div <%= id %> <%= style %>>
<ssf:displayConfiguration configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" />
</div>