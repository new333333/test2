<% //box %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />

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
>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>
