<% //View dashboard canvas component %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>

<c:set var="hideDashboardControls" value="false"/>
<c:if test="${ssDashboard.dashboard.components[ssDashboardId].controlStyle == 'hide'}">
  <c:set var="hideDashboardControls" value="true"/>
</c:if>
<c:if test="${empty ssDashboard.dashboard.components[ssDashboardId].displayStyle ||
              ssDashboard.dashboard.components[ssDashboardId].displayStyle == 'shadow'}">
<div class="ss_shadowbox" id="ss_component2_${ss_component_count}">
  <div class="ss_shadowbox2 ss_dashboard_view" id="ss_component_${ss_component_count}">
    <c:set var="dashboardToolbarColor" value="ss_dashboard_toolbar_color"/>
    <div class="ss_dashboard_toolbar ${dashboardToolbarColor}"
</c:if>
<c:if test="${ssDashboard.dashboard.components[ssDashboardId].displayStyle == 'simple'}">
<div id="ss_component_${ss_component_count}" style="margin:0px; padding:0px;">
  <div class="ss_dashboard_display_simple ss_dashboard_view">
    <c:set var="dashboardToolbarColor" value="ss_dashboard_display_simple_toolbar_color"/>
    <div class="ss_dashboard_display_simple_toolbar ${dashboardToolbarColor}"
</c:if>
<c:if test="${ssDashboard.dashboard.components[ssDashboardId].displayStyle == 'none'}">
<div id="ss_component_${ss_component_count}" style="margin:0px; padding:0px;">
  <div class="ss_dashboard_display_none">
    <c:set var="dashboardToolbarColor" value="ss_dashboard_display_none_toolbar_color"/>
    <div class="ss_dashboard_display_none_toolbar ${dashboardToolbarColor}"
</c:if>
      >
    <form 
      <c:if test="${!empty ssDashboard.dashboard.components[ssDashboardId].title}">
        class="${dashboardToolbarColor}"
      </c:if>
      method="post" style="display:inline;"
      action="<portlet:actionURL>
      <portlet:param name="action" value="modify_dashboard"/>
      <portlet:param name="binderId" value="${ssBinder.id}"/>
      <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
      </portlet:actionURL>">
	  <input type="hidden" name="_dashboardList" value="${ss_dashboard_dashboardList}">
	  <input type="hidden" name="_componentId" value="${ss_dashboard_id}">
	  <input type="hidden" name="_scope" value="${ss_dashboard_scope}">
	  <input type="hidden" name="_returnView" value="${ss_dashboard_returnView}"/>
      <table cellspacing="0" cellpadding="0" style="width:98%;">
	    <tr
		  onMouseOver="ss_dashboardComponentToolbar.fadeIn('ss_component_toolbuttons_${ss_component_count}');"
		  onMouseOut="ss_dashboardComponentToolbar.fadeOut('ss_component_toolbuttons_${ss_component_count}');"
	    >
	      <td nowrap valign="middle"><c:if 
	        test="${hideDashboardControls}"><div 
	          id="ss_dashboard_control_${ss_dashboard_control_count}"
              style="visibility:hidden; display:none;"
              <c:set var="ss_dashboard_control_count" scope="request" 
                value="${ss_dashboard_control_count + 1}"/>
              ></c:if><div style="margin:1px; padding:0px;"
	        ><div style="padding:1px;
			<c:if test="${!empty ssDashboard.dashboard.components[ssDashboardId].title}">
			 <c:if test="${ss_dashboard_componentScope == 'local'}">
		      background-color:red !important;
			 </c:if>
			 <c:if test="${ss_dashboard_componentScope == 'global'}">
		      background-color:blue !important;
			 </c:if>
			 <c:if test="${ss_dashboard_componentScope == 'binder'}">
		      background-color:green !important;
			 </c:if>
		    </c:if>
		    margin:0px 2px 0px 0px;"><div 
		      class="${dashboardToolbarColor}"><c:if test="${ss_dashboard_visible}"><input 
		      type="image" 
	      	  src="<html:imagesPath/>pics/sym_s_hide.gif"
	          id="ss_showHideImg_${ss_component_count}"
	          alt="<ssf:nlt tag="button.hide"/>" 
	          style="margin:2px;"
		      onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', 'ss_dashboard_component_${ss_component_count}');return false;"
		      ></c:if><c:if test="${!ss_dashboard_visible}"><input type="image" 
	          src="<html:imagesPath/>pics/sym_s_show.gif"
	          id="ss_showHideImg_${ss_component_count}"
	          alt="<ssf:nlt tag="button.show"/>" 
	          style="margin:2px;" 
	          onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', 'ss_dashboard_component_${ss_component_count}');return false;"
	          ></c:if></div></div></div><c:if 
	          test="${hideDashboardControls}"></div></c:if>
	      </td>
		  <td valign="middle" width="100%">
		    <span class="ss_bold">${ssDashboard.dashboard.components[ssDashboardId].title}</span>
		  </td>
		  <td align="right" valign="middle">
		    <c:if test="${hideDashboardControls}">
	          <div id="ss_dashboard_control_${ss_dashboard_control_count}"
              style="visibility:hidden; display:none;">
              <c:set var="ss_dashboard_control_count" scope="request" 
                value="${ss_dashboard_control_count + 1}"/>
		    </c:if>
	        <div width="60" style="display:inline; margin:0px; visibility:hidden; width:60px;" 
		      id="ss_component_toolbuttons_${ss_component_count}">
			
		      <table class="ss_dashboard_toolbar_color" cellspacing="0" cellpadding="0">
		      <tr>
		      <td nowrap>
			      <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			        name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>" 
			        style="margin-right:2px;">
		      </td>
		      <td nowrap>
			      <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			        name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>" 
			        style="margin-right:2px;">
		      </td>
		      <c:if test="${ssDashboard.sharedModificationAllowed}">
		        <td nowrap>
			      <input type="image" src="<html:imagesPath/>pics/sym_s_modify.gif"
			        name="_modifyComponentData" alt="<ssf:nlt tag="button.modify"/>" 
			        style="margin-right:2px;" 
			        onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}');">
		        </td>
		        <td nowrap>
			      <input type="image" src="<html:imagesPath/>pics/sym_s_delete.gif"
			        name="_deleteComponent" alt="<ssf:nlt tag="button.delete"/>" 
			        style="margin-right:2px;"
			        onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}'); ss_confirmDeleteComponent(this, '${ss_dashboard_id}', 'ss_component_${ss_component_count}', 'ss_component2_${ss_component_count}'); return false">
		        </td>
		      </c:if>
		      </tr>
		      </table>
		    </div>
		    <c:if test="${hideDashboardControls}">
		    </div>
		    </c:if>
		  </td>
		</tr>
      </table>
	 </form>
	</div>
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
</div>
<div style="margin:6px; padding:0px;"><img 
  src="<html:imagesPath/>pics/1pix.gif"></div>
</div>
