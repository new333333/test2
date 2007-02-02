<% //Title form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:choose>
  <c:when test="${ss_formViewStyle == 'guestbook'}">
		<input type="hidden" name="title" value="<c:out value="${ssUser.firstName}"/> <c:out value="${ssUser.lastName}"/> wrote" />
  </c:when>

  <c:otherwise>
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
		 <c:if test="${property_generated}">&nbsp;(<ssf:nlt tag="entry.titleGenerated"/>)</c:if>
		</div>
  </c:otherwise>
</c:choose>

