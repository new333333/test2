<% //fieldset %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<div class="formBreak">
<fieldset class="fieldset">
<c:if test="${!empty property_legend}">
<legend class="legend"><c:out value="${property_legend}"/></legend>
</c:if>
<ssf:displayConfiguration configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" />
</fieldset>
</div>
