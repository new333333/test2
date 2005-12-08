<% //Title form element %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
%>
<div class="formBreak">
<div class="labelAbove"><%= caption %></div>
<input type="text" size="40" name="title" value="<c:out value="${ss_forum_entry.title}"/>">
</div>
