<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% //View dashboard canvas component %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="com.sitescape.team.domain.User" scope="request" />
<%
String displayStyle = ssUser.getDisplayStyle();
if (displayStyle == null || displayStyle.equals("")) {
	displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
}
%>
<c:set var="ss_displayStyle" value="<%= displayStyle %>"/>
<c:set var="hideDashboardControls" value="false"/>
<c:if test="${ssDashboard.dashboard.components[ssComponentId].displayStyle == 'none'}">
  <c:set var="hideDashboardControls" value="true"/>
</c:if>
<!-- Start of component -->
<div id="<portlet:namespace/>_component_${ss_component_count}"
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
	id="<portlet:namespace/>_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner1.jpg"></div></td>
<td valign="top" class="ss_dashboard_dragHandle">
  <div class="ss_title_bar"
  <c:if test="${hideDashboardControls}">
	id="<portlet:namespace/>_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
  >

 <c:if test="${!empty ssBinder}">
    <form 
      method="post" 
  	  action="<portlet:actionURL><portlet:param 
  		name="action" value="modify_dashboard"/><portlet:param 
  		name="binderId" value="${ssBinder.id}"/></portlet:actionURL>">
     <c:set var="myId" value="binderId=${ssBinder.id}"/> 
</c:if>
<c:if test="${empty ssBinder}">
    <form 
      method="post" 
  	  action="<portlet:actionURL><portlet:param 
  	  	name="action" value="modify_dashboard_portlet"/><portlet:param 
  	  	name="dashboardId" value="${ssDashboardId}"/></portlet:actionURL>">
     <c:set var="myId" value="dashboardId=${ssDashboardId}"/> 
</c:if>
	  <input type="hidden" name="_dashboardList" value="${ss_dashboard_dashboardList}"/>
	  <input type="hidden" name="_componentId" value="${ss_dashboard_id}"/>
	  <input type="hidden" name="_scope" value="${ss_dashboard_scope}"/>
	  <input type="hidden" name="_operation" value=""/>
	  <input type="hidden" name="_returnView" value="${ss_dashboard_returnView}"/>
			<ul class="ss_title_bar_icons">
			  <c:if test="${ss_dashboard_visible}">
				<li><a href="#"
				  onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', '<portlet:namespace/>_dashboard_component_${ss_component_count}', '${myId}', '<portlet:namespace/>_${ss_dashboard_id}');return false;"
				><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/iconset/hide.gif" 
				  alt="<ssf:nlt tag="button.hide"/>" /></a></li>
			  </c:if>
			  <c:if test="${!ss_dashboard_visible}">
				<li><a href="#"
				  onClick="ss_showHideDashboardComponent(this, '${ss_dashboard_id}', '<portlet:namespace/>_dashboard_component_${ss_component_count}', '${myId}', '<portlet:namespace/>_${ss_dashboard_id}');return false;"
				><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/iconset/show.gif" 
				  alt="<ssf:nlt tag="button.show"/>" /></a></li>
			  </c:if>
			  <c:if test="${ss_displayStyle == 'accessible'}">
				<li><a href="#"
				  onClick="ss_submitDashboardChange(this, '_moveUp');return false;"
				><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/iconset/up.gif" 
				  alt="<ssf:nlt tag="button.moveUp"/>" /></a></li>
				<li><a href="#"
				  onClick="ss_submitDashboardChange(this, '_moveDown');return false;"
				><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/iconset/down.gif" 
				  alt="<ssf:nlt tag="button.moveDown"/>" /></a></li>
			  </c:if>
				<li><a href="#" 
				    onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}');ss_submitDashboardChange(this, '_modifyComponentData');return false;"
				  ><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/iconset/modify.gif" 
				    alt="<ssf:nlt tag="button.modify"/>" /></a></li>
				<li><a href="#"
				  onClick="ss_modifyDashboardComponent(this, '${ss_dashboard_componentScope}'); ss_confirmDeleteComponent(this, '${ss_dashboard_id}', '<portlet:namespace/>_component_${ss_component_count}', '<portlet:namespace/>_component2_${ss_component_count}', '${myId}', '<portlet:namespace/>_${ss_dashboard_id}'); return false;"
				><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/iconset/delete-component.gif" 
				  alt="<ssf:nlt tag="button.delete"/>" /></a></li>
			</ul>
	</form>
	<strong>${ssDashboard.dashboard.components[ssComponentId].title}&nbsp;</strong>
  </div>
</td>
<td valign="top"><div
  <c:if test="${hideDashboardControls}">
	id="<portlet:namespace/>_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
><img border="0" src="<html:imagesPath/>skins/${ss_user_skin}/roundcorners3/corner2.jpg"></div></td>
</tr>
<tr>
<c:if test="${hideDashboardControls}">
<script type="text/javascript">
	<portlet:namespace/>_dashboard_border_classNames[${ss_dashboard_border_count}] = 'ss_decor-border7';
</script>
</c:if>
<td 
  <c:if test="${hideDashboardControls}">
    id="<portlet:namespace/>_dashboard_border_${ss_dashboard_border_count}"
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
	    id="<portlet:namespace/>_dashboard_border_${ss_dashboard_border_count}"
	      <c:set var="ss_dashboard_border_count" scope="request" 
	         value="${ss_dashboard_border_count + 1}"/>
      </c:if>
      <c:if test="${!hideDashboardControls && ss_dashboard_visible}">
        class="ss_content_window_content"
      </c:if>
    >
		<div id="<portlet:namespace/>_dashboard_component_${ss_component_count}" 
		    align="left" style="margin:0px; 
		    <c:if test="${!ss_dashboard_visible}">
		      visibility:hidden; display:none;
		    </c:if>
		    padding:2px;">
			<c:set var="ss_component_count" value="${ss_component_count + 1}" scope="request"/>
			<ssf:dashboard id="${ss_dashboard_id}"
			   type="viewData" configuration="${ssDashboard}"
			   initOnly="true" />
			<c:if test="${ss_dashboard_visible}">
			  <ssf:dashboard id="${ss_dashboard_id}"
			     type="viewData" configuration="${ssDashboard}"/>
			</c:if>
    	</div>
	</div>
</td>
<c:if test="${hideDashboardControls}">
<script type="text/javascript">
	<portlet:namespace/>_dashboard_border_classNames[${ss_dashboard_border_count}] = 'ss_decor-border8';
</script>
</c:if>
<td 
  <c:if test="${hideDashboardControls}">
    id="<portlet:namespace/>_dashboard_border_${ss_dashboard_border_count}"
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
	id="<portlet:namespace/>_dashboard_control_${ss_dashboard_control_count}"
    style="visibility:hidden; display:none;"
    <c:set var="ss_dashboard_control_count" scope="request" 
       value="${ss_dashboard_control_count + 1}"/>
  </c:if>
><div><div></div></div></div></td>
</tr>
</table>
</div>
<div style="margin:3px; padding:0px;"><img border="0"
  src="<html:imagesPath/>pics/1pix.gif"></div>
<!-- End of component -->

