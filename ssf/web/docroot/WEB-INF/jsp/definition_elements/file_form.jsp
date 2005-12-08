<% //File form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String width = (String) request.getAttribute("property_width");
	String inline = (String) request.getAttribute("property_inline");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}
	if (inline == null) {inline = "block";}
	if (inline.equals("true")) {
		inline = "inline";
	} else {
		inline = "block";
	}
%>
<div style="display:<%= inline %>;"><%= caption %>
<input type="file" name="<%= elementName %>" <%= width %> >
</div>
