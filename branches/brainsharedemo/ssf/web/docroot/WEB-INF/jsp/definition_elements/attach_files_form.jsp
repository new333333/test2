<% //File form for attaching files %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:if test="${empty property_hide || !property_hide}">
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
		caption = caption;
	}
%>
<div class="ss_entryContent" >
<span class="ss_labelAbove">${property_caption}</span>
<%
	for (int i = 1; i <= count; i++) {
%>
<input type="file" class="ss_text" name="<%= elementName + Integer.toString(i) %>" <%= width %> ><br>
<%
	}
%>
</div>
</c:if>
