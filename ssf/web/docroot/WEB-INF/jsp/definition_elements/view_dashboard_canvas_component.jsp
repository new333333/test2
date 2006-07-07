<% //View dashboard canvas component %>

<div class="ss_shadowbox">
  <div class="ss_shadowbox2 ss_dashboard_view">
    <div class="ss_dashboard_toolbar ss_dashboard_toolbar_color">
      <table class="ss_dashboard_toolbar_color" 
		  cellspacing="0" cellpadding="1" style="width:100%;">
		<tr>
		  <td><span class="ss_bold"><ssf:dashboard id="${id}"
		    type="title" configuration="${ssDashboard}"/></span></td>
		  <td align="right">
		    <form class="ss_dashboard_toolbar_color" method="post" style="display:inline;"
		      action="<portlet:actionURL>
		      <portlet:param name="action" value="modify_dashboard"/>
		      <portlet:param name="binderId" value="${ssBinder.id}"/>
		      <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		      </portlet:actionURL>">
			  <input type="hidden" name="_dashboardList" value="${dashboardList}">
			  <input type="hidden" name="_componentId" value="${id}">
			  <input type="hidden" name="_scope" value="${scope}">
			  <input type="hidden" name="_returnView" value="binder"/>
			
		      <c:if test="${component.visible}"><input type="image" 
		      	  src="<html:imagesPath/>pics/sym_s_hide.gif"
		          id="ss_showHideImg_${ss_component_count}"
		          alt="<ssf:nlt tag="button.hide"/>" 
		          style="margin-right:2px;"
			      onClick="ss_showHideDashboardComponent(this, '${id}', 'ss_dashboard_component_${ss_component_count}');return false;"
			      ></c:if><c:if test="${!component.visible}"><input type="image" 
		          src="<html:imagesPath/>pics/sym_s_show.gif"
		          id="ss_showHideImg_${ss_component_count}"
		          alt="<ssf:nlt tag="button.show"/>" 
		          style="margin-right:2px;" 
		          onClick="ss_showHideDashboardComponent(this, '${id}', 'ss_dashboard_component_${ss_component_count}');return false;"
		          ></c:if>&nbsp;<input 
		      type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
		        name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>" 
		        style="margin-right:2px;">&nbsp;<input 
		      type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
		        name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>" 
		        style="margin-right:2px;">&nbsp;<input 
		      type="image" src="<html:imagesPath/>pics/sym_s_modify.gif"
		        name="_modifyComponentData" alt="<ssf:nlt tag="button.modify"/>" 
		        style="margin-right:2px;">&nbsp;<input 
		      type="image" src="<html:imagesPath/>pics/sym_s_delete.gif"
		        name="_deleteComponent" alt="<ssf:nlt tag="button.delete"/>" 
		        style="margin-right:2px;">
		    </form>
		  </td>
		</tr>
      </table>
	</div>
	<div id="ss_dashboard_component_${ss_component_count}" 
	   align="left" style="margin:0px; padding:2px;">
	<c:set var="ss_component_count" value="${ss_component_count + 1}"/>
	<c:if test="${component.visible}">
	  <ssf:dashboard id="${id}"
	     type="view" configuration="${ssDashboard}"/>
	</c:if>
	</div>
  </div>
</div>
<div style="margin:6px; padding:0px;"><img 
  src="<html:imagesPath/>pics/1pix.gif"></div>
</div>
