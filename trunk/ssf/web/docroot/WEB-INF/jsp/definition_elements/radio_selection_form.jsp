<% // radio selection %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<c:set var="checked" value=""/>
<c:if test="${folderEntry.customAttributes[radioGroupName].value == property_name}">
  <c:set var="checked" value="checked"/>
</c:if>
<input type="radio" name="<c:out value="${radioGroupName}"/>" 
  value="<c:out value="${property_name}"/>" <c:out value="${checked}"/>
>&nbsp;<c:out value="${property_caption}"/><ssf:displayConfiguration 
  configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" /></input>
<br/>
