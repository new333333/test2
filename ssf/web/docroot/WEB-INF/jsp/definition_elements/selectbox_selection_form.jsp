<% // selectbox option %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="configJspStyle" type="String" scope="request" />
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<c:set var="checked" value=""/>
<c:forEach var="selection" items="${folderEntry.customAttributes[selectboxName].valueSet}" >
  <c:if test="${selection == property_name}">
    <c:set var="checked" value="selected"/>
  </c:if>
</c:forEach>
<option value="<c:out value="${property_name}"/>" 
  <c:out value="${checked}"/>><c:out value="${property_caption}"/><ssf:displayConfiguration 
  configDefinition="<%= configDefinition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= configJspStyle %>" /></option>
