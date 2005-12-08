<% //Textarea form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="configDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="configElement" type="org.dom4j.Element" scope="request" />
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String width = (String) request.getAttribute("property_width");
	String rows = (String) request.getAttribute("property_rows");
	if (rows == null || rows.equals("")) {rows = "4";}
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "width='"+width+"'";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}
%>
<div class="formBreak">
<div style="display:inline;"><%= caption %>
<textarea name="<%= elementName %>" wrap="virtual"
  rows="<%= rows %>" <%= width %> 
><c:out value="${folderEntry.customAttributes[property_name].value}"/></textarea>
</div>
</div>
