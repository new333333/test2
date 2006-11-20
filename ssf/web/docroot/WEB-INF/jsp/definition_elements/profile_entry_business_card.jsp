<% //Business card view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
		//Get the form item being displayed
		Element item = (Element) request.getAttribute("item");
%>
<br>
<div class="ss_entryContent">
<h1 class="ss_entryTitle">
<c:if test="${empty ssProfileConfigEntry.title}">
    <c:out value="${ssProfileConfigEntry.name}"/>
</c:if>
<c:if test="${!empty ssProfileConfigEntry.title}">
	<c:out value="${ssProfileConfigEntry.title}"/> 
	<span class="ss_light">(<c:out value="${ssProfileConfigEntry.name}"/>)</span>
</c:if>
</h1>
</div>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="<%= item %>" configJspStyle="${ssConfigJspStyle}" 
  processThisItem="false" />
