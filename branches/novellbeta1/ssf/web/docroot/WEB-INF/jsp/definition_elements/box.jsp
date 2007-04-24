<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //box %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<!-- Start of box -->
<div
<c:if test="${!empty property_id}">
  id="${property_id}"
</c:if>
<c:if test="${propertyValues_style[0] == 'rounded'}">
  <c:if test="${ssConfigJspStyle == 'form'}">
    class="ss_rounded_border_form"
  </c:if>
  <c:if test="${ssConfigJspStyle != 'form'}">
    class="ss_rounded_border"
  </c:if>
</c:if>
<c:if test="${propertyValues_style[0] == 'square'}">
  style="border:solid black 1px; padding:2px; margin-bottom:2px;"
</c:if>
>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
</div>
<!-- End of box -->
