<% // User list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<ssf:findUsers formName="${formName}" formElement="${property_name}" 
  type="user" userList="${ssDefinitionEntry.customAttributes[property_name].valueSet}"/>
