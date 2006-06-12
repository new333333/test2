<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<div class="ss_indent_small" style="width:98%;">
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
</table>

<table cellspacing="0" cellpadding="0" style="width:100%;">
<tr>
  <td colspan="2">
      <c:forEach var="component" items="${ssDashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
		<ssf:dashboard id="${id}"
		  type="view" configuration="${ssDashboard}"/>
		</div>
	  </c:forEach>
  </td>
</tr>

<tr>
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
	    <img src="<html:imagesPath/>pics/1pix.gif" 
	      hspace="${ssDashboard.narrowFixedWidth}px" vspace="0px"/><br/>
		<ssf:dashboard id="${id}"
		  type="view" configuration="${ssDashboard}"/>
		</div>
	  </c:forEach>
  </td>
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
		<ssf:dashboard id="${id}"
		  type="view" configuration="${ssDashboard}"/>
		</div>
	  </c:forEach>
  </td>
</tr>

<tr>
  <td colspan="2">
      <c:forEach var="component" items="${ssDashboard.wide_bottom}">
		<c:set var="id" value="${component.id}"/>
		<div class="ss_dashboard_view">
		<ssf:dashboard id="${id}"
		  type="view" configuration="${ssDashboard}"/>
		</div>
	  </c:forEach>
  </td>
</tr>

</table>
</div>
