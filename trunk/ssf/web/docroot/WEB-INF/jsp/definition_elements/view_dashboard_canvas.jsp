<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_toolbar_count" value="0"/>
<c:set var="ss_component_count" value="0"/>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>

<div class="ss_indent_medium" style="width:100%;">
<table cellspacing="0" cellpadding="0" style="width:99%; margin-bottom:2px;">
<tr>
  <td align="left" valign="top" nowrap width="2%">
    <span class="ss_bold"><c:out value="${ssDashboard.title}"/> 
      <c:if test="${ssDashboard.includeBinderTitle}">
        <c:out value="${ssBinder.title}"/>
      </c:if>
    </span>
  </td>
  <td></td>
  <td align="right" valign="top">
    <a href="javascript: ;" onClick="ss_toggle_toolbars();return false;"
	  title="<ssf:nlt tag="dashboard.configure"/>"
	  ><span class="ss_smallprint ss_gray"><ssf:nlt tag="Configure"/></span></a>
  </td>
</tr>
</table>
<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
   style="visibility:hidden; display:none;">
  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  <br/>
  <ul style="list-style: inside; margin:2px; padding:2px;">
  <span class="ss_bold"><ssf:nlt tag="dashboard.addModifyDelete"/></span>
  <li><a href="<portlet:actionURL>
  <portlet:param name="action" value="modify_dashboard"/>
  <portlet:param name="binderId" value="${ssBinder.id}"/>
  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
  <portlet:param name="_scope" value="local"/>
  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.local"/></a></li>
  <li><a href="<portlet:actionURL>
  <portlet:param name="action" value="modify_dashboard"/>
  <portlet:param name="binderId" value="${ssBinder.id}"/>
  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
  <portlet:param name="_scope" value="global"/>
  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.global"/></a></li>
  <li><a href="<portlet:actionURL>
  <portlet:param name="action" value="modify_dashboard"/>
  <portlet:param name="binderId" value="${ssBinder.id}"/>
  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
  <portlet:param name="_scope" value="binder"/>
  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.binder"/></a></li>
  </ul>
  <br/>
</div>

<table cellspacing="0" cellpadding="0" style="width:99%;">
<tr>
  <td colspan="3">
      <c:forEach var="component" items="${ssDashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="local"/>
		<c:set var="dashboardList" value="wide_top"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; width:1px; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

<tr>
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		<c:set var="dashboardList" value="narrow_fixed"/>
		<div style="margin:0px; padding:0px; 
		    width:${ssDashboard.narrowFixedWidth + 5}px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div style="width:${ssDashboard.narrowFixedWidth}px;"><img 
	  src="<html:imagesPath/>pics/1pix.gif" /></div>
	<div 
	  id="ss_dashboard_toolbar_${ss_toolbar_count}"
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  style="visibility:hidden; display:none; 
	  width:${ssDashboard.narrowFixedWidth}px; height:20px;"></div></td>
  
  <td valign="top" width="4%"><div style="width:10px;"><img 
	  src="<html:imagesPath/>pics/1pix.gif" /></div></td>
  
  <td valign="top" width="96%">
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		<c:set var="dashboardList" value="narrow_variable"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; width:1px; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

<tr>
  <td colspan="3">
      <c:forEach var="component" items="${ssDashboard.wide_bottom}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		<c:set var="dashboardList" value="wide_bottom"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; width:1px; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

</table>
</div>
<script type="text/javascript">
ss_toolbar_count = <c:out value="${ss_toolbar_count}"/>;
</script>
