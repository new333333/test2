<% // radio selection %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<c:set var="checked" value=""/>
<c:if test="${ssEntry.customAttributes[radioGroupName].value == property_name}">
  <c:set var="checked" value="checked"/>
</c:if>
<input type="radio" name="<c:out value="${radioGroupName}"/>" 
  value="<c:out value="${property_name}"/>" <c:out value="${checked}"/>
/>&nbsp;<span class="ss_bold"><c:out value="${property_caption}"/></span><ssf:displayConfiguration 
  configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
<br/>
