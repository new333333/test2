<% //Title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}

	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
%>
<div class="ss_entryContent">
<div class="ss_labelAbove"><c:out value="${property_caption}"/></div>
<input type="text" class="ss_text" name="title" <%= width %>
 value="<c:out value="${ssDefinitionEntry.title}"/>" />
</div>
