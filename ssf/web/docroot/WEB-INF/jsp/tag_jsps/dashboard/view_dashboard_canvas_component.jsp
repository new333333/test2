<% //View dashboard canvas component %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="hideDashboardControls" value="false"/>
<c:if test="${ssDashboard.dashboard.components[ssDashboardId].displayStyle == 'none'}">
  <c:set var="hideDashboardControls" value="true"/>
</c:if>
<!-- Start of component -->
<div id="ss_component_${ss_component_count}"
  <c:if test="${hideDashboardControls}">
    class="ss_content_window_compact" 
  </c:if>
  <c:if test="${!hideDashboardControls}">
    class="ss_content_window" 
  </c:if>
>
<table cellspacing="0" cellpadding="0">
<col width="8"/>
<col width="100%"/>
<col width="8"/>
<tr>
<td valign="top"><div
  <c:if test="${hideDashboardControls}">
	id="ss_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
><img src="<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner1.gif"><div></td>
<td valign="top" class="ss_dashboard_dragHandle">
  <div class="ss_title_bar"
  <c:if test="${hideDashboardControls}">
	id="ss_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
  >
    <form 
      method="post" 
      action="<portlet:actionURL>
      <portlet:param name="action" value="modify_dashboard"/>
      <portlet:param name="binderId" value="${ssBinder.id}"/>
      <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
      </portlet:actionURL>">
	  <input type="hidden" name="_dashboardList" value="${ss_dashboard_dashboardList}">
	  <input type="hidden" name="_componentId" value="${ss_dashboard_id}">
	  <input type="hidden" name="_scope" value="${ss_dashboard_scope}">
	  <input type="hidden" name="_operation" value="">
	  <input type="hidden" name="_returnView" value="${ss_dashboard_returnView}"/>
			<ul class="ss_title_bar_icons">
			  <c:if test="${ss_dashboard_visible}">
				<li><a href="#"
				  onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', 'ss_dashboard_component_${ss_component_count}');return false;"
				><img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/hide.gif" 
				  alt="<ssf:nlt tag="button.hide"/>" /></a></li>
			  </c:if>
			  <c:if test="${!ss_dashboard_visible}">
				<li><a href="#"
				  onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', 'ss_dashboard_component_${ss_component_count}');return false;"
				><img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/show.gif" 
				  alt="<ssf:nlt tag="button.show"/>" /></a></li>
			  </c:if>
				<li><a href="#"
				  onClick="ss_submitDashboardChange(this, '_moveUp');return false;"
				><img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/up.gif" 
				  alt="<ssf:nlt tag="button.moveUp"/>" /></a></li>
				<li><a href="#"
				  onClick="ss_submitDashboardChange(this, '_moveDown');return false;"
				><img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/down.gif" 
				  alt="<ssf:nlt tag="button.moveDown"/>" /></a></li>
				<li><a href="#" 
				    onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}');ss_submitDashboardChange(this, '_modifyComponentData');return false;"
				  ><img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/modify.gif" 
				    alt="<ssf:nlt tag="button.modify"/>" /></a></li>
				<li><a href="#"
				  onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}'); ss_confirmDeleteComponent(this, '${ss_dashboard_id}', 'ss_component_${ss_component_count}', 'ss_component2_${ss_component_count}'); return false;"
				><img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/delete.gif" 
				  alt="<ssf:nlt tag="button.delete"/>" /></a></li>
			</ul>
	</form>
	<strong>${ssDashboard.dashboard.components[ssDashboardId].title}&nbsp;</strong>
  </div>
</td>
<td valign="top"><div
  <c:if test="${hideDashboardControls}">
	id="ss_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
><img src="<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner2.gif"></div></td>
</tr>
<tr>
<c:if test="${hideDashboardControls}">
<script type="text/javascript">
	ss_dashboard_border_classNames[${ss_dashboard_border_count}] = 'ss_decor-border7';
</script>
</c:if>
<td 
  <c:if test="${hideDashboardControls}">
    id="ss_dashboard_border_${ss_dashboard_border_count}"
    <c:set var="ss_dashboard_border_count" scope="request" 
      value="${ss_dashboard_border_count + 1}"/>
  </c:if>
  <c:if test="${!hideDashboardControls}">
    class="ss_decor-border7"
  </c:if>
></td>
<td>
	<div 
      <c:if test="${hideDashboardControls}">
	    id="ss_dashboard_border_${ss_dashboard_border_count}"
	      <c:set var="ss_dashboard_border_count" scope="request" 
	         value="${ss_dashboard_border_count + 1}"/>
      </c:if>
      <c:if test="${!hideDashboardControls && ss_dashboard_visible}">
        class="ss_content_window_content"
      </c:if>
    >
		<div id="ss_dashboard_component_${ss_component_count}" 
		    align="left" style="margin:0px; 
		    <c:if test="${!ss_dashboard_visible}">
		      visibility:hidden; display:none;
		    </c:if>
		    padding:2px;">
			<c:set var="ss_component_count" value="${ss_component_count + 1}" scope="request"/>
			<c:if test="${ss_dashboard_visible}">
			  <ssf:dashboard id="${ss_dashboard_id}"
			     type="viewData" configuration="${ssDashboard}"/>
			</c:if>
    	</div>
	</div>
</td>
<c:if test="${hideDashboardControls}">
<script type="text/javascript">
	ss_dashboard_border_classNames[${ss_dashboard_border_count}] = 'ss_decor-border8';
</script>
</c:if>
<td 
  <c:if test="${hideDashboardControls}">
    id="ss_dashboard_border_${ss_dashboard_border_count}"
    <c:set var="ss_dashboard_border_count" scope="request" 
       value="${ss_dashboard_border_count + 1}"/>
  </c:if>
  <c:if test="${!hideDashboardControls}">
    class="ss_decor-border8"
  </c:if>
></td>
</tr>
<tr>
<td colspan="3"><div class="ss_decor-round-corners-bottom3"
  <c:if test="${hideDashboardControls}">
	id="ss_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
><div><div></div></div></div></td>
</tr>
</table>
</div>
<div style="margin:3px; padding:0px;"><img 
  src="<html:imagesPath/>pics/1pix.gif"></div>
<!-- End of component -->

