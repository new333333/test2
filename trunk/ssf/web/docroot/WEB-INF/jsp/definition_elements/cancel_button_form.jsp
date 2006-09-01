<% //Cancel button form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssOperation == 'add_folder_reply' && empty property_onClick}">
  <c:set var="property_onClick" value="self.window.close();return false;"/>
</c:if>

<input type="submit" class="ss_submit" name="cancelBtn" value="${property_caption}" 
  onClick="${property_onClick}">
