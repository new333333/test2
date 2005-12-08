<% // submit button for the definition builder %>
<%@ include file="/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ss_forum_forum" type="com.sitescape.ef.domain.Binder" scope="request" />
<jsp:useBean id="ss_forum_config_definition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ss_forum_config" type="org.dom4j.Element" scope="request" />
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	String buttonName = (String) request.getAttribute("property_name");
	String buttonText = (String) request.getAttribute("property_caption");
	String buttonOnClick = (String) request.getAttribute("property_onClick");
%>
<input type="submit" name="<%= buttonName %>" value="<%= buttonText %>" onClick="<%= buttonOnClick %>">
