<% //Title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
%>
<div class="formBreak">
<div class="labelAbove"><%= caption %></div>
<input type="text" size="40" name="title" value="<c:out value="${folderEntry.title}"/>">
</div>
