<% //File form for attaching files %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String number = (String) request.getAttribute("property_number");
	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "size='"+width+"'";
	}
	if (number == null || number.equals("")) {
		number = "1";
	}
	int count = Integer.parseInt(number);
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}
%>
<div class="entryContent" ><%= caption %>
<%
	for (int i = 1; i <= count; i++) {
%>
<input type="file" name="<%= elementName + Integer.toString(i) %>" <%= width %> ><br>
<%
	}
%>
</div>
