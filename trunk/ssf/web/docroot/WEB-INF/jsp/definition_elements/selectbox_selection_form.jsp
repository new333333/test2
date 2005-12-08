<% // selectbox option %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<jsp:useBean id="ss_forum_configJspStyle" type="String" scope="request" />
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
%>
<c:set var="checked" value=""/>
<c:forEach var="selection" items="${ss_forum_entry.customAttributes[selectboxName].valueSet}" >
  <c:if test="${selection == property_name}">
    <c:set var="checked" value="selected"/>
  </c:if>
</c:forEach>
<option value="<c:out value="${property_name}"/>" 
  <c:out value="${checked}"/>><c:out value="${property_caption}"/><sitescape:displayConfiguration 
  configDefinition="<%= ss_forum_config_definition %>" 
  configElement="<%= item %>" 
  configJspStyle="<%= ss_forum_configJspStyle %>" /></option>
