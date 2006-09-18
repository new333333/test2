<% //View dashboard canvas table %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>  
  
<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

<c:set var="ss_toolbar_count" value="0"/>
<c:set var="ss_dashboard_control_count" value="0" scope="request"/>
<c:set var="ss_dashboard_border_count" value="0" scope="request"/>
<c:set var="ss_component_count" value="0" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>

<table id="ss_dashboardTable" class="ss_dashboardTable_off" cellspacing="0" cellpadding="0" 
  frame="border" rules="all">
<col width="${ssDashboard.narrowFixedWidth}">
<col width="100%">
<tr>
  <td colspan="2" class="ss_dashboardTable_off">
			<div id="wide_top">
			  <div class="ss_dashboardProtoDropTarget"><img 
			    src="<html:imagesPath/>pics/1pix.gif"></div>
			  <c:forEach var="component" items="${ssDashboard.wide_top}">
			  	  <c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				  <c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
				  <c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				  <c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				  <c:set var="ss_dashboard_dashboardList" value="wide_top" scope="request"/>
				  <c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				  <div id="ss_dashboard_component_${component.id}" 
				    class="ss_dashboard_component" style="margin:0px; padding:0px;">
				    <ssf:dashboard id="${component.id}" 
				      type="viewComponent" configuration="${ssDashboard}"/>
				  </div>
			      <div class="ss_dashboardProtoDropTarget"><img 
			        src="<html:imagesPath/>pics/1pix.gif"></div>
			  </c:forEach>
			</div>
			
			<div style="width:${ssDashboard.narrowFixedWidth}px;"><img 
			  src="<html:imagesPath/>pics/1pix.gif" /></div>
	
  </td>
</tr>

<tr>
  <td valign="top" class="ss_dashboardTable_off">
			<div id="narrow_fixed">
			  <div class="ss_dashboardProtoDropTarget"><img 
			    src="<html:imagesPath/>pics/1pix.gif"></div>
			  <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
				<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
				<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				<c:set var="ss_dashboard_dashboardList" value="narrow_fixed" scope="request"/>
				<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				<div id="ss_dashboard_component_${component.id}"
				  class="ss_dashboard_component" style="margin:0px; padding:0px;">
				  <ssf:dashboard id="${component.id}" 
				    type="viewComponent" configuration="${ssDashboard}"/>
				</div>
			    <div class="ss_dashboardProtoDropTarget"><img 
			    src="<html:imagesPath/>pics/1pix.gif"></div>
			  </c:forEach>
			</div>

			<div style="visibility:hidden; 
			  width:${ssDashboard.narrowFixedWidth}px;"><img 
			  src="<html:imagesPath/>pics/1pix.gif"></div></td>
  
  
  <td valign="top" class="ss_dashboardTable_off" style="padding-left:15px;">
			<div id="narrow_variable">
			  <div class="ss_dashboardProtoDropTarget"><img 
			    src="<html:imagesPath/>pics/1pix.gif"></div>
			  <c:forEach var="component" items="${ssDashboard.narrow_variable}">
				<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
				<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				<c:set var="ss_dashboard_dashboardList" value="narrow_variable" scope="request"/>
				<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				<div id="ss_dashboard_component_${component.id}"
				  class="ss_dashboard_component" style="margin:0px; padding:0px;">
				  <ssf:dashboard id="${component.id}" 
				    type="viewComponent" configuration="${ssDashboard}"/>
				</div>
			    <div class="ss_dashboardProtoDropTarget"><img 
			      src="<html:imagesPath/>pics/1pix.gif"></div>
			  </c:forEach>
			</div>
  </td>
</tr>

<tr>
  <td colspan="2" class="ss_dashboardTable_off">
			<div id="wide_bottom">
			  <div class="ss_dashboardProtoDropTarget"><img 
			    src="<html:imagesPath/>pics/1pix.gif"></div>
			  <c:forEach var="component" items="${ssDashboard.wide_bottom}">
				<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
				<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
				<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
				<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
				<c:set var="ss_dashboard_dashboardList" value="wide_bottom" scope="request"/>
				<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
				<div id="ss_dashboard_component_${component.id}"
				  class="ss_dashboard_component" style="margin:0px; padding:0px;">
				  <ssf:dashboard id="${component.id}" 
				    type="viewComponent" configuration="${ssDashboard}"/>
				</div>
			    <div class="ss_dashboardProtoDropTarget"><img 
			      src="<html:imagesPath/>pics/1pix.gif"></div>
			  </c:forEach>
			</div>
  </td>
</tr>

</tbody>
</table>

<form name="ss_dashboard_layout_form" id="ss_dashboard_layout_form" 
  style="display:inline;">
<input type="hidden" name="binderId" value="${ssBinder.id}"/>
<input type="hidden" name="scope" value="${ss_dashboard_table_scope}"/>
<input type="hidden" name="dashboard_layout"/>
</form>

<script type="text/javascript">
ss_toolbar_count = <c:out value="${ss_toolbar_count}"/>;
ss_dashboard_control_count = <c:out value="${ss_dashboard_control_count}"/>;
ss_dashboard_border_count = <c:out value="${ss_dashboard_border_count}"/>;
ss_createOnLoadObj("ss_dashboardInitialization", ss_dashboardInitialization)
var ss_saveDashboardLayoutUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_dashboard_layout" />
	</ssf:url>";
var ss_showHideAllDashboardComponentsUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="binderId" value="${ssBinder.id}" />
	</ssf:url>";
</script>

</c:if>
