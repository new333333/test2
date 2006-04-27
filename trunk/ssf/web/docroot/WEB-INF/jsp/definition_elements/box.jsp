<% //box %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div
<c:if test="${!empty property_id}">
  id="${property_id}"
</c:if>
<c:if test="${property_style == 'rounded'}">
  <c:if test="${ssConfigJspStyle == 'form'}">
    class="ss_rounded_border_form"
  </c:if>
  <c:if test="${ssConfigJspStyle != 'form'}">
    class="ss_rounded_border"
  </c:if>
</c:if>
<c:if test="${property_style == 'square'}">
  style="border:solid black 1px;"
</c:if>
>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>
