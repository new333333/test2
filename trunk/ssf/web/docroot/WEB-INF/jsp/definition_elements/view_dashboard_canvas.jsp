<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%
	String ss_dashboardAddWide = NLT.get("dashboard.addComponents.wide");
	String ss_dashboardAddNarrow = NLT.get("dashboard.addComponents.narrow");
	String ss_dashboardAddMedium = NLT.get("dashboard.addComponents.medium");
%>
<div id="ss_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
  <form method="post" action="<portlet:actionURL>
	  <portlet:param name="action" value="modify_dashboard"/>
	  <portlet:param name="binderId" value="${ssBinder.id}"/>
	  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
      </portlet:actionURL>">
	<div style="margin:10px;">
      <span class="ss_bold"><ssf:nlt tag="dashboard.componentScope"/></span><br>
      <input type="radio" name="_scope" value="local" checked/><ssf:nlt tag="dashboard.componentScope.local"/><br>
      <input type="radio" name="_scope" value="global"/><ssf:nlt tag="dashboard.componentScope.global"/><br>
      <c:if test="${ssDashboard.sharedModificationAllowed}">
        <input type="radio" name="_scope" value="binder"/><ssf:nlt tag="dashboard.componentScope.binder"/><br>
      </c:if>
      <br/>
      <span class="ss_bold"><ssf:nlt tag="dashboard.componentType"/></span><br>
      <c:forEach var="component" items="${ssDashboard.components_wide}">
         <input type="radio" name="name" value="${component}">
           <ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/>
         <br>
      </c:forEach>
      <br>
	  <input class="ss_form" type="submit" name="add_wideTop" 
	    value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
	  <input class="ss_form" type="submit" name="cancel" 
	    value="<ssf:nlt tag="button.cancel"/>" 
	    onClick="ss_hideDashboardMenu(this);return false;">
	  <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	  <input type="hidden" name="_componentId" value="">
	  <input type="hidden" name="_returnView" value="binder"/>
	</div>
  </form>
</div>
</div>
<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

<c:set var="ss_toolbar_count" value="0"/>
<c:set var="ss_dashboard_control_count" value="0" scope="request"/>
<c:set var="ss_component_count" value="0" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>
<div style="width:100%;">
<table cellspacing="0" cellpadding="0" style="width:99%; margin-bottom:2px;">
<tr>
  <td valign="middle" nowrap width="2%">
      <form "style="display:inline;">
        <c:if test="${ss_show_all_dashboard_components}"><input 
	      type="image" 
      	  src="<html:imagesPath/>pics/sym_s_hide.gif"
          id="ss_showHideImg_all"
          alt="<ssf:nlt tag="button.hide"/>" 
          style="margin:2px;"
	      onClick="ss_showHideAllDashboardComponents(this);return false;"
	      ></c:if><c:if test="${!ss_show_all_dashboard_components}"><input type="image" 
          src="<html:imagesPath/>pics/sym_s_show.gif"
          id="ss_showHideImg_all"
          alt="<ssf:nlt tag="button.show"/>" 
          style="margin:2px;" 
          onClick="ss_showHideAllDashboardComponents(this);return false;"
          ></c:if></form>
  </td>
  <td align="left" valign="top" nowrap width="2%">
    <span class="ss_bold"><c:out value="${ssDashboard.title}"/> 
      <c:if test="${ssDashboard.includeBinderTitle}">
        <c:out value="${ssBinder.title}"/>
      </c:if>
    </span>
  </td>
  <td></td>
  <td align="right" valign="top">
	<div id="ss_addDashboardContent" style="display:inline;">
	<%
		String ss_dashboardTitle = NLT.get("dashboard.configure");
	%>
	<ssf:menu title="<%= ss_dashboardTitle %>" titleClass="ss_fineprint ss_light" 
	  titleId="ss_addDashboardContent" menuClass="ss_dashboard_menu" menuWidth="300px">
		<ul class="ss_dropdownmenu" 
		  style="list-style: outside; margin:2px 2px 2px 18px; padding:2px;">
		  <li><a href="#" onClick="ss_toggle_dashboard_hidden_controls();return false;"><span
		    id="ss_dashboard_menu_controls"><ssf:nlt 
		    tag="dashboard.showHiddenControls"/></span></a></li>
		  <li><a href="<portlet:renderURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		  <portlet:param name="_scope" value="local"/>
		  <portlet:param name="operation" value="set_dashboard_title"/>
		  </portlet:renderURL>"><ssf:nlt tag="dashboard.setTitle"/></a></li>
		  <li><a href="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		  <portlet:param name="_scope" value="global"/>
		  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.global"/></a></li>
		  <c:if test="${ssDashboard.sharedModificationAllowed}">
		    <li><a href="<portlet:actionURL>
		    <portlet:param name="action" value="modify_dashboard"/>
		    <portlet:param name="binderId" value="${ssBinder.id}"/>
		    <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		    <portlet:param name="_scope" value="binder"/>
		    </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.binder"/></a></li>
		  </c:if>

		  <li><a href="#" onClick="ss_changeStyles('debug');return false;"><span
		    id="ss_dashboard_menu_controls">Change color to 'debug' [test code]</span></a></li>

		  <li><a href="#" onClick="ss_changeStyles('blackandwhite');return false;"><span
		    id="ss_dashboard_menu_controls">Change color to 'blackandwhite' [test code]</span></a></li>

		</ul>
	</ssf:menu>
	</div>
  </td>
</tr>
</table>
<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
   style="visibility:hidden; display:none;">
  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
</div>

<div id="ss_dashboardComponentCanvas" style="
<c:if test="${ss_show_all_dashboard_components}">
  visibility:visible; display:block;
</c:if>
<c:if test="${!ss_show_all_dashboard_components}">
  visibility:hidden; display:none;
</c:if>
">
<table id="ss_dashboardTable" class="ss_dashboardTable_off" cellspacing="0" cellpadding="0" 
  frame="border" rules="all" width="100%">
<tr>
  <td colspan="2" class="ss_dashboardTable_off">
    <div id="wideTop">
    <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
    <c:forEach var="component" items="${ssDashboard.wide_top}">
  	  <c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
	  <c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
	  <c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
	  <c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
	  <c:set var="ss_dashboard_dashboardList" value="wide_top" scope="request"/>
	  <c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
	  <div id="ss_dashboard_component_${component.id}" 
	    class="ss_dashboard_component" style="margin:0px; padding:0px;">
	    <ssf:dashboard id="${component.id}" type="viewComponent" configuration="${ssDashboard}"/>
	  </div>
      <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	</c:forEach>
	</div>
  </td>
</tr>

<tr>
  <td valign="top" class="ss_dashboardTable_off">
	<div style="width:${ssDashboard.narrowFixedWidth}px;"><img 
	  src="<html:imagesPath/>pics/1pix.gif" /></div>
	
    <div id="narrowFixed">
    <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
      <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
		<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
		<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
		<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
		<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
		<c:set var="ss_dashboard_dashboardList" value="narrow_fixed" scope="request"/>
		<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
		<div id="ss_dashboard_component_${component.id}"
		  class="ss_dashboard_component" style="margin:0px; padding:0px;">
		  <ssf:dashboard id="${component.id}" type="viewComponent" configuration="${ssDashboard}"/>
		</div>
        <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	  </c:forEach>
	</div>

	<div style="visibility:hidden; display:none; 
	  width:${ssDashboard.narrowFixedWidth}px;"><img 
  src="<html:imagesPath/>pics/1pix.gif"></div></td>
  
  <td valign="top" width="98%" class="ss_dashboardTable_off" style="padding-left:15px;">
    <div id="narrowVariable">
    <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
		<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
		<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
		<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
		<c:set var="ss_dashboard_dashboardList" value="narrow_variable" scope="request"/>
		<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
		<div id="ss_dashboard_component_${component.id}"
		  class="ss_dashboard_component" style="margin:0px; padding:0px;">
		  <ssf:dashboard id="${component.id}" type="viewComponent" configuration="${ssDashboard}"/>
		</div>
        <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	  </c:forEach>
	</div>
  </td>
</tr>

<tr>
  <td colspan="2" class="ss_dashboardTable_off">
    <div id="wideBottom">
    <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
      <c:forEach var="component" items="${ssDashboard.wide_bottom}">
		<c:set var="ss_dashboard_id" value="${component.id}" scope="request"/>
		<c:set var="ss_dashboard_scope" value="${ssDashboard.scope}" scope="request"/>
		<c:set var="ss_dashboard_componentScope" value="${component.scope}" scope="request"/>
		<c:set var="ss_dashboard_visible" value="${component.visible}" scope="request"/>
		<c:set var="ss_dashboard_dashboardList" value="wide_bottom" scope="request"/>
		<c:set var="ss_dashboard_returnView" value="binder" scope="request"/>
		<div id="ss_dashboard_component_${component.id}"
		  class="ss_dashboard_component" style="margin:0px; padding:0px;">
		  <ssf:dashboard id="${component.id}" type="viewComponent" configuration="${ssDashboard}"/>
		</div>
        <div class="ss_dashboardProtoDropTarget"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	  </c:forEach>
	</div>
  </td>
</tr>

</tbody>
</table>
<form name="ss_dashboard_layout_form" id="ss_dashboard_layout_form" style="display:inline;">
<input type="hidden" name="binderId" value="${ssBinder.id}"/>
<input type="hidden" name="dashboard_layout"/>
</form>
</div>
</div>
<script type="text/javascript">
ss_toolbar_count = <c:out value="${ss_toolbar_count}"/>;
ss_dashboard_control_count = <c:out value="${ss_dashboard_control_count}"/>;
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
<c:if test="${!ss_show_all_dashboard_components}">
<br/>
</c:if>
