<% //Title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
%>
<div class="ss_entryContent">
<div class="ss_labelAbove"><%= caption %></div>
<input type="text" size="40" name="title" value="<c:out value="${ssFolderEntry.title}"/>">
</div>
<div class="ss_divider"></div>
