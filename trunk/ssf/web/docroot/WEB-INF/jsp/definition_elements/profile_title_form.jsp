<% //Name form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
%>
<div class="ss_entryContent">
<div class="ss_labelAbove"><%= caption %></div>
<input type="text" class="ss_text" size="40" name="name" value="<c:out value="${ssEntry.name}"/>"
	<c:if test="${!empty ssEntry.name}">
	  READONLY style="background-color: infobackground;" 
	</c:if>
>
</div>
