<% //Name form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String caption = (String) request.getAttribute("property_caption");
	if (caption == null) {caption = "";}
%>
<div class="ss_entryContent">
<div class="ss_labelAbove"><%= caption %></div>
<input type="text" size="40" name="name" value="<c:out value="${ssEntry.name}"/>"
	<c:if test="${empty ssEntry.name}">
	  class="ss_text"
	</c:if>
	<c:if test="${!empty ssEntry.name}">
	  class="ss_text ss_readonly" READONLY="true" 
	</c:if>
/>
</div>
