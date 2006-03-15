<% // html %>
<c:out value="${property_htmlTop}"/>
<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
<c:out value="${property_htmlBottom}"/>
