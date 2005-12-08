<% //Textarea form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
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
	String required = (String) request.getAttribute("property_required");
	if (required == null) {required = "";}
	if (required.equals("true")) {
		required = "<span class=\"ss_required\">*</span>";
	} else {
		required = "";
	}
%>
<div class="ss_entryContent">
<span class="ss_labelAbove"><%= caption %><%= required %></span>
<textarea name="<%= elementName %>" wrap="virtual"
  rows="<%= rows %>" <%= width %> 
><c:out value="${ssFolderEntry.customAttributes[property_name].value}"/></textarea>
</div>
<div class="ss_divider"></div>
