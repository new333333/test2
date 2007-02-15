<% //expandable div %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	//Get the item being displayed
	Element item = (Element) request.getAttribute("item");
	
%>
<c:if test="${ssConfigJspStyle != 'mail'}">
<ssf:expandableArea title="${property_caption}">
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />
</ssf:expandableArea>
</c:if>

<c:if test="${ssConfigJspStyle == 'mail'}">
<div>
<c:if test="!empty property_caption}">
<span class="ss_bold">${property_caption}</span>
<br/>
</c:if>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" 
  configJspStyle="${ssConfigJspStyle}" />
</div>
</c:if>
