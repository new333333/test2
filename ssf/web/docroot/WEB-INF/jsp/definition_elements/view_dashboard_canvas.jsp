<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_indent_small" style="width:100%;">
<table cellspacing="0" cellpadding="0" style="width:100%;">
<tr>
  <td align="left" valign="top">
    <span class="ss_bold"><c:out value="${ssDashboard.dashboard.title}"/> 
      <c:if test="${ssDashboard.dashboard.includeBinderTitle}">
        <c:out value="${ssBinder.title}"/>
      </c:if>
    </span>
  </td>
  <td align="right" valign="top">
    <a href="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		  </portlet:actionURL>"><span class="ss_gray"><ssf:nlt tag="Edit"/></span></a>
  </td>
</tr>

<tr>
  <td colspan="2">
      <c:forEach var="component" items="${ssDashboard.dashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
		<ssf:dashboard name="${ssDashboard.dashboard.components[id].name}" 
		  id="${id}"
		  type="view" configuration="${ssDashboard.dashboard}"/>
		</div>
	  </c:forEach>
  </td>
</tr>

<tr>
  <td valign="top" width="${ssDashboard.dashboard.narrowFixedWidth}">
      <c:forEach var="component" items="${ssDashboard.dashboard.narrow_fixed}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
		<ssf:dashboard name="${ssDashboard.dashboard.components[id].name}" 
		  id="${id}"
		  type="view" configuration="${ssDashboard.dashboard}"/>
		</div>
	  </c:forEach>
  </td>
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.dashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
		<ssf:dashboard name="${ssDashboard.dashboard.components[id].name}" 
		  id="${id}"
		  type="view" configuration="${ssDashboard.dashboard}"/>
		</div>
	  </c:forEach>
  </td>
</tr>

<tr>
  <td colspan="2">
      <c:forEach var="component" items="${ssDashboard.dashboard.wide_bottom}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
		<ssf:dashboard name="${ssDashboard.dashboard.components[id].name}" 
		  id="${id}"
		  type="view" configuration="${ssDashboard.dashboard}"/>
		</div>
	  </c:forEach>
  </td>
</tr>

</table>
</div>
