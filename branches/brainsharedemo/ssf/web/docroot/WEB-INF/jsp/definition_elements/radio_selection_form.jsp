<% // radio selection %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="checked" value=""/>
<c:if test="${ssDefinitionEntry.customAttributes[radioGroupName].value == property_name}">
  <c:set var="checked" value="checked"/>
</c:if>
<input type="radio" name="<c:out value="${radioGroupName}"/>" 
  value="<c:out value="${property_name}"/>" <c:out value="${checked}"/>
/>&nbsp;<span class="ss_bold"><c:out value="${property_caption}"/></span><ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
<br/>
