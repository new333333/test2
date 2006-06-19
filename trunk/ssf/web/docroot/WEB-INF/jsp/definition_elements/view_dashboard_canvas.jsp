<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_toolbar_count" value="0"/>

<script type="text/javascript">
var ss_toolbar_count = 0;
function ss_toggle_toolbars() {
	for (var i = 0; i < ss_toolbar_count; i++) {
		var obj = document.getElementById("ss_dashboard_toolbar_"+i)
		if (obj.style.visibility == 'hidden') {
			obj.style.visibility = 'visible';
			obj.style.display = 'inline';
		} else {
			obj.style.visibility = 'hidden';
			obj.style.display = 'none';
		}
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}
</script>
<div class="ss_indent_medium" style="width:100%;">
<table cellspacing="0" cellpadding="0" style="width:100%;">
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
	 <br/>
	 <div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	   align="left" style="visibility:hidden; display:none;">
	  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  <table><tr><td>
	  <ul>
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
	  </td></tr></table>
	 </div>
  </td>
</tr>
</table>

<table cellspacing="0" cellpadding="0" style="width:100%;">
<tr>
  <td colspan="3">
      <c:forEach var="component" items="${ssDashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		<c:set var="scopeTitle" value="dashboard.local"/>
		<c:if test="${scope == 'global'}">
		  <c:set var="scopeTitle" value="dashboard.global"/>
		</c:if>
		<c:if test="${scope == 'binder'}">
		  <c:set var="scopeTitle" value="dashboard.binder"/>
		</c:if>
		<c:if test="${component.visible}">
		<div class="ss_shadowbox">
		<div class="ss_shadowbox2 ss_dashboard_view">
		</c:if>
		<c:if test="${!component.visible}">
		<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
		  class="ss_dashboard_view" style="visibility:hidden; display:none;">
		<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		</c:if>
		 <div id="ss_dashboard_toolbar_${ss_toolbar_count}"
		   class="ss_dashboard_view_toolbar" 
		   style="visibility:hidden; display:none;">
		  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		  <div>
		  <table class="ss_dashboard_view_toolbar" 
		    cellspacing="0" cellpadding="0" style="width:100%;">
		  <tr>
		  <td><span class="ss_bold"><ssf:nlt tag="${scopeTitle}"/></span></td>
		  <td align="right">
		  <form class="ss_dashboard_view_toolbar" method="post" style="display:inline;"
		    action="<portlet:actionURL>
		    <portlet:param name="action" value="modify_dashboard"/>
		    <portlet:param name="binderId" value="${ssBinder.id}"/>
		    <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		    </portlet:actionURL>">
			<input type="hidden" name="_dashboardList" value="wide_top">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>
			
			<c:if test="${scope == 'local'}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_modify.gif"
			    name="_modifyComponentData" alt="<ssf:nlt tag="button.modify"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_delete.gif"
			    name="_deleteComponent" alt="<ssf:nlt tag="button.delete"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>

			<c:if test="${scope != 'local'}">
			  <c:if test="${component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
			      name="_hide" alt="<ssf:nlt tag="button.hide"/>">
			  </c:if>
			  <c:if test="${!component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
			      name="_show" alt="<ssf:nlt tag="button.show"/>">
			  </c:if>
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>
			
		  </form>
		  </td></tr></table>
		 </div>
		 </div>
		<c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;">
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		</div>
		</c:if>
		</div>
	    <c:if test="${component.visible}">
		<div style="margin:3px; padding:0px;"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	    </c:if>
	    <c:if test="${!component.visible}">
		<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
		  style="visibility:hidden; display:none; 
		  margin:3px; padding:0px;"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	    <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	    </c:if>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

<tr>
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		<c:set var="scopeTitle" value="dashboard.local"/>
		<c:if test="${scope == 'global'}">
		  <c:set var="scopeTitle" value="dashboard.global"/>
		</c:if>
		<c:if test="${scope == 'binder'}">
		  <c:set var="scopeTitle" value="dashboard.binder"/>
		</c:if>
		<div class="ss_shadowbox" style="width:${ssDashboard.narrowFixedWidth}px;">
		<div class="ss_shadowbox2 ss_dashboard_view" style="width:${ssDashboard.narrowFixedWidth}px;">
		 <div id="ss_dashboard_toolbar_${ss_toolbar_count}"
		   class="ss_dashboard_view_toolbar" 
		   style="visibility:hidden; display:none; width:${ssDashboard.narrowFixedWidth}px;">
		  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		  <div>
		  <table class="ss_dashboard_view_toolbar" 
		    cellspacing="0" cellpadding="0" style="width:100%;">
		  <tr>
		  <td><span class="ss_bold"><ssf:nlt tag="${scopeTitle}"/></span></td>
		  <td align="right">
		  <form class="ss_dashboard_view_toolbar" style="display:inline;"
		    method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		  </portlet:actionURL>">
			<input type="hidden" name="_dashboardList" value="narrow_fixed">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>

			<c:if test="${scope == 'local'}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_modify.gif"
			    name="_modifyComponentData" alt="<ssf:nlt tag="button.modify"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_delete.gif"
			    name="_deleteComponent" alt="<ssf:nlt tag="button.delete"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>

			<c:if test="${scope != 'local'}">
			  <c:if test="${component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
			      name="_hide" alt="<ssf:nlt tag="button.hide"/>">
			  </c:if>
			  <c:if test="${!component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
			      name="_show" alt="<ssf:nlt tag="button.show"/>">
			  </c:if>
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>
		  </form>
		  </td></tr></table>
		 </div>
		 </div>
		<c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;">
	      <img src="<html:imagesPath/>pics/1pix.gif" 
	        hspace="${ssDashboard.narrowFixedWidth2}px" vspace="0px"/><br/>
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		</c:if>
		</div>
		</div>
		<div style="margin:3px; padding:0px;"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	<img src="<html:imagesPath/>pics/1pix.gif" 
	        hspace="${ssDashboard.narrowFixedWidth2}px" vspace="0px"/>
  </td>
  <td>&nbsp;&nbsp;</td>
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		<c:set var="scopeTitle" value="dashboard.local"/>
		<c:if test="${scope == 'global'}">
		  <c:set var="scopeTitle" value="dashboard.global"/>
		</c:if>
		<c:if test="${scope == 'binder'}">
		  <c:set var="scopeTitle" value="dashboard.binder"/>
		</c:if>
		<div class="ss_shadowbox">
		<div class="ss_shadowbox2 ss_dashboard_view" align="left">
		 <div id="ss_dashboard_toolbar_${ss_toolbar_count}"
		   class="ss_dashboard_view_toolbar" 
		   style="visibility:hidden; display:none;">
		  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		  <div>
		  <table class="ss_dashboard_view_toolbar" 
		    cellspacing="0" cellpadding="0" style="width:100%;">
		  <tr>
		  <td><span class="ss_bold"><ssf:nlt tag="${scopeTitle}"/></span></td>
		  <td align="right">
		  <form class="ss_dashboard_view_toolbar" method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		  </portlet:actionURL>">
			<input type="hidden" name="_dashboardList" value="narrow_variable">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>

			<c:if test="${scope == 'local'}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_modify.gif"
			    name="_modifyComponentData" alt="<ssf:nlt tag="button.modify"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_delete.gif"
			    name="_deleteComponent" alt="<ssf:nlt tag="button.delete"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>

			<c:if test="${scope != 'local'}">
			  <c:if test="${component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
			      name="_hide" alt="<ssf:nlt tag="button.hide"/>">
			  </c:if>
			  <c:if test="${!component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
			      name="_show" alt="<ssf:nlt tag="button.show"/>">
			  </c:if>
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>
		  </form>
		  </td></tr></table>
		 </div>
		 </div>
		<c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;">
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		</c:if>
		</div>
		</div>
		<div style="margin:3px; padding:0px;"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

<tr>
  <td colspan="3">
      <c:forEach var="component" items="${ssDashboard.wide_bottom}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		<c:set var="scopeTitle" value="dashboard.local"/>
		<c:if test="${scope == 'global'}">
		  <c:set var="scopeTitle" value="dashboard.global"/>
		</c:if>
		<c:if test="${scope == 'binder'}">
		  <c:set var="scopeTitle" value="dashboard.binder"/>
		</c:if>
		<div class="ss_shadowbox">
		<div class="ss_shadowbox2 ss_dashboard_view">
		 <div id="ss_dashboard_toolbar_${ss_toolbar_count}"
		   class="ss_dashboard_view_toolbar" align="right" 
		   style="visibility:hidden; display:none;">
		  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		  <div>
		  <table class="ss_dashboard_view_toolbar" 
		    cellspacing="0" cellpadding="0" style="width:100%;">
		  <tr>
		  <td><span class="ss_bold"><ssf:nlt tag="${scopeTitle}"/></span></td>
		  <td align="right">
		  <form class="ss_dashboard_view_toolbar" method="post"
		    action="<portlet:actionURL>
		    <portlet:param name="action" value="modify_dashboard"/>
		    <portlet:param name="binderId" value="${ssBinder.id}"/>
		    <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		    </portlet:actionURL>">
			<input type="hidden" name="_dashboardList" value="wide_bottom">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>

			<c:if test="${scope == 'local'}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_modify.gif"
			    name="_modifyComponentData" alt="<ssf:nlt tag="button.modify"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_delete.gif"
			    name="_deleteComponent" alt="<ssf:nlt tag="button.delete"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>

			<c:if test="${scope != 'local'}">
			  <c:if test="${component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
			      name="_hide" alt="<ssf:nlt tag="button.hide"/>">
			  </c:if>
			  <c:if test="${!component.visible}">
			    <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
			      name="_show" alt="<ssf:nlt tag="button.show"/>">
			  </c:if>
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			    name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>">
			  &nbsp;
			  <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			    name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>">
			</c:if>
		  </form>
		  </td></tr></table>
		 </div>
		 </div>
		<c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;">
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		</c:if>
		</div>
		</div>
		<div style="margin:3px; padding:0px;"><img src="<html:imagesPath/>pics/1pix.gif"></div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

</table>
</div>
<script type="text/javascript">
ss_toolbar_count = <c:out value="${ss_toolbar_count}"/>;
</script>
