<% //Cancel button form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String buttonText = (String) request.getAttribute("property_caption");
	String buttonOnClick = (String) request.getAttribute("property_onClick");
%>
<input type="submit" class="ss_submit" name="cancelBtn" value="<%= buttonText %>" 
  onClick="<%= buttonOnClick %>">
