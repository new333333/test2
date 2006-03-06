<% // submit button for the definition builder %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String buttonName = (String) request.getAttribute("property_name");
	String buttonText = (String) request.getAttribute("property_caption");
	String buttonOnClick = (String) request.getAttribute("property_onClick");
%>
<input type="submit" class="ss_submit" name="<%= buttonName %>" 
  value="<%= buttonText %>" onClick="<%= buttonOnClick %>">
