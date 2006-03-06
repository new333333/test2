<% //Graphic form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = caption;
	}
	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
%>
<div class="ss_entryContent" >
<span class="ss_labelAbove"><%= caption %></span>
<input type="file" class="ss_text" name="<%= elementName %>" <%= width %> >
</div>
<div class="ss_divider"></div>
