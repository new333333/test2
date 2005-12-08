<% //Add an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<div class="ss_portlet" width="100%">
<ssf:displayConfiguration configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" />
</div>
