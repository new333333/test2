<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_indent_small ss_border_light" style="width:100%;">
<table cellspacing="0" cellpadding="2" width="100%">
<tr>
  <td align="left" valign="top">
    <span class="ss_bold"><c:out value="${ssDashboard.title}"/> 
      <c:if test="${ssDashboard.includeBinderTitle}">
        <c:out value="${ssBinder.title}"/>
      </c:if>
    </span>
  </td>
  <td align="right" valign="top">
    <a href="<ssf:url 
      folderId="${ssDefinitionEntry.id}" 
      action="modify_dashboard"/>"><span class="ss_gray"><ssf:nlt tag="Edit"/></span></a>
  </td>
</tr>
</table>
</div>
