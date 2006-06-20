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
<table cellspacing="0" cellpadding="0" style="width:99%;">
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

<table cellspacing="0" cellpadding="0" style="width:99%;">
<tr>
  <td colspan="3">
      <c:forEach var="component" items="${ssDashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		  <div 
		   <c:if test="${!component.visible}">
		    style="visibility:hidden; display:none; margin:0px; padding:0px;"
		    id="ss_dashboard_toolbar_${ss_toolbar_count}"
			<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		   </c:if>
		   <c:if test="${component.visible}">
		    style="margin:0px; padding:0px;"
		   </c:if>
		  >
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
			<input type="hidden" name="_dashboardList" value="wide_top">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>
			
		    <c:if test="${component.visible}">
		      <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
		        name="_hide" alt="<ssf:nlt tag="button.hide"/>" 
		        style="width:13px; height:13px; hspace:2px;">
		    </c:if>
		    <c:if test="${!component.visible}">
		      <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
		        name="_show" alt="<ssf:nlt tag="button.show"/>" 
		        style="width:13px; height:13px; hspace:2px;">
		    </c:if>
		    <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
		      name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>" 
		      style="width:13px; height:13px; hspace:2px;">
		    <input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
		      name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>" 
		      style="width:13px; height:13px; hspace:2px;">
		  </form></td></tr></table>
		 </div>
		 <c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;">
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		 </c:if>
		</div>
		</div>
		<div style="margin:3px; padding:0px;"><img 
		  src="<html:imagesPath/>pics/1pix.gif"></div>
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
		  <div 
		   <c:if test="${!component.visible}">
		    style="visibility:hidden; display:none; 
		    margin:0px; padding:0px; width:${ssDashboard.narrowFixedWidth + 5}px;"
		    id="ss_dashboard_toolbar_${ss_toolbar_count}"
			<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		   </c:if>
		   <c:if test="${component.visible}">
		    style="margin:0px; padding:0px; width:${ssDashboard.narrowFixedWidth + 5}px;"
		   </c:if>
		  >
		  <div class="ss_shadowbox">
		  <div class="ss_shadowbox2 ss_dashboard_view">
		  <div class="ss_dashboard_toolbar ss_dashboard_toolbar_color">
		  <table class="ss_dashboard_toolbar_color" 
		    cellspacing="0" cellpadding="2" style="width:100%;">
		  <tr>
		  <td><span class="ss_bold"><ssf:dashboard id="${id}"
		    type="title" configuration="${ssDashboard}"/></span></td>
		  <td align="right">
		  <form class="ss_dashboard_toolbar_color" method="post" style="display:inline;"
		    method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
		  </portlet:actionURL>">
			<input type="hidden" name="_dashboardList" value="narrow_fixed">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>

			<c:if test="${component.visible}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
			    name="_hide" alt="<ssf:nlt tag="button.hide"/>" 
			    style="width:13px; height:13px; hspace:2px;">
			</c:if>
			<c:if test="${!component.visible}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
			    name="_show" alt="<ssf:nlt tag="button.show"/>" 
			    style="width:13px; height:13px; hspace:2px;">
			</c:if>
		    <input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			  name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>" 
			  style="width:13px; height:13px; hspace:2px;">
			<input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			  name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>" 
			  style="width:13px; height:13px; hspace:2px;">
		  
		  </form>
		  </td></tr></table>
		 </div>
		 <c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;"><img 
		  src="<html:imagesPath/>pics/1pix.gif" 
	        hspace="${ssDashboard.narrowFixedWidth2}" vspace="0"/><br/>
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		 </c:if>
		</div>
		</div>
		<div style="margin:3px; padding:0px;"><img 
		  src="<html:imagesPath/>pics/1pix.gif"></div>
		</div>
	  </c:forEach>

	<div style="width:${ssDashboard.narrowFixedWidth}px;"><img 
	  src="<html:imagesPath/>pics/1pix.gif" /></div>
	<div 
	  id="ss_dashboard_toolbar_${ss_toolbar_count}"
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  style="visibility:hidden; display:none; 
	  width:${ssDashboard.narrowFixedWidth}px; height:20px;"></div></td>
  
  <td valign="top"><div style="width:10px;"><img 
	  src="<html:imagesPath/>pics/1pix.gif" /></div></td>
  
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${component.scope}"/>
		  <div 
		   <c:if test="${!component.visible}">
		    style="visibility:hidden; display:none; margin:0px; padding:0px;"
		    id="ss_dashboard_toolbar_${ss_toolbar_count}"
			<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		   </c:if>
		   <c:if test="${component.visible}">
		    style="margin:0px; padding:0px;"
		   </c:if>
		  >
		  <div class="ss_shadowbox">
		  <div class="ss_shadowbox2 ss_dashboard_view">
		  <div class="ss_dashboard_toolbar ss_dashboard_toolbar_color">
		  <table class="ss_dashboard_toolbar_color" 
		    cellspacing="0" cellpadding="2" style="width:100%;">
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
			<input type="hidden" name="_dashboardList" value="narrow_variable">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>

			<c:if test="${component.visible}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
			    name="_hide" alt="<ssf:nlt tag="button.hide"/>" 
			    style="width:13px; height:13px; hspace:2px;">
			</c:if>
			<c:if test="${!component.visible}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
			    name="_show" alt="<ssf:nlt tag="button.show"/>" 
			    style="width:13px; height:13px; hspace:2px;">
			</c:if>
			<input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			  name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>" 
			  style="width:13px; height:13px; hspace:2px;">
			<input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			  name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>" 
			  style="width:13px; height:13px; hspace:2px;">
		
		  </form>
		  </td></tr></table>
		 </div>
		 <c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;">
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		 </c:if>
		</div>
		</div>
		<div style="margin:3px; padding:0px;"><img 
		  src="<html:imagesPath/>pics/1pix.gif"></div>
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
		  <div 
		   <c:if test="${!component.visible}">
		    style="visibility:hidden; display:none; margin:0px; padding:0px;"
		    id="ss_dashboard_toolbar_${ss_toolbar_count}"
			<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		   </c:if>
		   <c:if test="${component.visible}">
		    style="margin:0px; padding:0px;"
		   </c:if>
		  >
		  <div class="ss_shadowbox">
		  <div class="ss_shadowbox2 ss_dashboard_view">
		  <div class="ss_dashboard_toolbar ss_dashboard_toolbar_color">
		  <table class="ss_dashboard_toolbar_color" 
		    cellspacing="0" cellpadding="2" style="width:100%;">
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
			<input type="hidden" name="_dashboardList" value="wide_bottom">
			<input type="hidden" name="_componentId" value="${id}">
			<input type="hidden" name="_returnView" value="binder"/>

			<c:if test="${component.visible}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_hide.gif"
			    name="_hide" alt="<ssf:nlt tag="button.hide"/>" 
			    style="width:13px; height:13px; hspace:2px;">
			</c:if>
			<c:if test="${!component.visible}">
			  <input type="image" src="<html:imagesPath/>pics/sym_s_show.gif"
			    name="_show" alt="<ssf:nlt tag="button.show"/>" 
			    style="width:13px; height:13px; hspace:2px;">
			</c:if>
			<input type="image" src="<html:imagesPath/>pics/sym_s_move_up.gif"
			  name="_moveUp" alt="<ssf:nlt tag="button.moveUp"/>" 
			  style="width:13px; height:13px; hspace:2px;">
			<input type="image" src="<html:imagesPath/>pics/sym_s_move_down.gif"
			  name="_moveDown" alt="<ssf:nlt tag="button.moveDown"/>" 
			  style="width:13px; height:13px; hspace:2px;">

		  </form>
		  </td></tr></table>
		 </div>
		 <c:if test="${component.visible}">
		  <div align="left" style="margin:0px; padding:2px;">
		  <ssf:dashboard id="${id}"
		    type="view" configuration="${ssDashboard}"/>
		  </div>
		 </c:if>
		</div>
		</div>
		<div style="margin:3px; padding:0px;"><img 
		  src="<html:imagesPath/>pics/1pix.gif"></div>
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
