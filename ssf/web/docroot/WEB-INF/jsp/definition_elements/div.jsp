<% //div %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
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
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />
</div>