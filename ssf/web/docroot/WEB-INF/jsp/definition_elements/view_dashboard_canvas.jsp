<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_toolbar_count" value="0"/>
<c:set var="ss_component_count" value="0"/>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>
<%
	String ss_dashboardAddWide = NLT.get("dashboard.addComponents.wide");
	String ss_dashboardAddNarrow = NLT.get("dashboard.addComponents.narrow");
	String ss_dashboardAddMedium = NLT.get("dashboard.addComponents.medium");
%>
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
	<div id="ss_addDashboardContent" style="display:inline;">
	<%
		String ss_dashboardTitle = NLT.get("dashboard.configure");
	%>
	<ssf:menu title="<%= ss_dashboardTitle %>" titleClass="ss_smallprint ss_gray" 
	  titleId="ss_addDashboardContent" menuWidth="350px">
		<ul class="ss_dropdownmenu" 
		  style="list-style: outside; margin:2px 2px 2px 18px; padding:2px;">
		  <li><a href="" onClick="ss_toggle_toolbars();return false;"><ssf:nlt 
		    tag="dashboard.addContent"/></a></li>
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

<table cellspacing="0" cellpadding="0" style="width:99%;">
<tr>
  <td colspan="3">
      <c:forEach var="component" items="${ssDashboard.wide_top}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="wide_top"/>
		<c:set var="returnView" value="binder"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	   style="visibility:hidden; display:none;">
	  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  <ssf:menu title="<%= ss_dashboardAddWide %>" titleClass="ss_linkButton" 
	    titleId="ss_addDashboardContentWideTop" menuWidth="500px" offsetTop="2"
	    openStyle="popup">
	  <form method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
	      </portlet:actionURL>">
		<div class="ss_form">
	      <span><ssf:nlt tag="dashboard.componentScope"/></span><br>
	      <input type="radio" name="_scope" value="local" checked/><ssf:nlt tag="dashboard.componentScope.local"/><br>
	      <input type="radio" name="_scope" value="global"/><ssf:nlt tag="dashboard.componentScope.global"/><br>
	      <c:if test="${ssDashboard.sharedModificationAllowed}">
	        <input type="radio" name="_scope" value="binder"/><ssf:nlt tag="dashboard.componentScope.binder"/><br>
	      </c:if>
	      <span><ssf:nlt tag="dashboard.componentType"/></span><br>
	      <c:forEach var="component" items="${ssDashboard.components_wide}">
	         <input type="radio" name="name" value="${component}">
	           <ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/>
	         <br>
	      </c:forEach>
	    </div>
	  <input class="ss_form" type="submit" name="add_wideTop" 
	    value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
	  <input class="ss_form" type="submit" name="cancel" 
	    value="<ssf:nlt tag="button.cancel"/>" 
	    onClick="ss_hideDashboardMenu(this);return false;">
	  <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	  <input type="hidden" name="_componentId" value="">
	  <input type="hidden" name="_returnView" value="binder"/>
      </form>
      </ssf:menu>
      <br>
	</div>
	
	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; width:1px; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

<tr>
  <td valign="top">
      <c:forEach var="component" items="${ssDashboard.narrow_fixed}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="narrow_fixed"/>
		<c:set var="returnView" value="binder"/>
		<div style="margin:0px; padding:0px; 
		    width:${ssDashboard.narrowFixedWidth + 5}px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div style="width:${ssDashboard.narrowFixedWidth}px;"><img 
	  src="<html:imagesPath/>pics/1pix.gif" /></div>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	   style="visibility:hidden; display:none;">
	  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  <ssf:menu title="<%= ss_dashboardAddNarrow %>" titleClass="ss_linkButton" 
	    titleId="ss_addDashboardContentNarrow" menuWidth="400px" offsetTop="2"
	    openStyle="popup">
	  <form method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
	      </portlet:actionURL>">
		<div class="ss_form">
	      <span><ssf:nlt tag="dashboard.componentScope"/></span><br>
	      <input type="radio" name="_scope" value="local" checked/><ssf:nlt tag="dashboard.componentScope.local"/><br>
	      <input type="radio" name="_scope" value="global"/><ssf:nlt tag="dashboard.componentScope.global"/><br>
	      <c:if test="${ssDashboard.sharedModificationAllowed}">
	        <input type="radio" name="_scope" value="binder"/><ssf:nlt tag="dashboard.componentScope.binder"/><br>
	      </c:if>
	      <span><ssf:nlt tag="dashboard.componentType"/></span><br>
	      <c:forEach var="component" items="${ssDashboard.components_narrow_fixed}">
	         <input type="radio" name="name" value="${component}">
	           <ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/>
	         <br>
	      </c:forEach>
	    </div>
	  <input class="ss_form" type="submit" name="add_narrowFixed" 
	    value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
	  <input class="ss_form" type="submit" name="cancel" 
	    value="<ssf:nlt tag="button.cancel"/>" 
	    onClick="ss_hideDashboardMenu(this);return false;">
	  <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	  <input type="hidden" name="_componentId" value="">
	  <input type="hidden" name="_returnView" value="binder"/>
      </form>
      </ssf:menu>
      <br>
	</div>
	
	<div 
	  id="ss_dashboard_toolbar_${ss_toolbar_count}"
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  style="visibility:hidden; display:none; 
	  width:${ssDashboard.narrowFixedWidth}px; height:20px;"></div></td>
  
  <td valign="top" width="2%"><div><img style="margin:0px 0px 0px 15px;"
	  src="<html:imagesPath/>pics/1pix.gif" /></div></td>
  
  <td valign="top" width="98%">
      <c:forEach var="component" items="${ssDashboard.narrow_variable}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="narrow_variable"/>
		<c:set var="returnView" value="binder"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	   style="visibility:hidden; display:none;">
	  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  <ssf:menu title="<%= ss_dashboardAddMedium %>" titleClass="ss_linkButton" 
	    titleId="ss_addDashboardContentNarrowVariable" menuWidth="400px" offsetTop="2"
	    openStyle="popup">
	  <form method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
	      </portlet:actionURL>">
		<div class="ss_form">
	      <span><ssf:nlt tag="dashboard.componentScope"/></span><br>
	      <input type="radio" name="_scope" value="local" checked/><ssf:nlt tag="dashboard.componentScope.local"/><br>
	      <input type="radio" name="_scope" value="global"/><ssf:nlt tag="dashboard.componentScope.global"/><br>
	      <c:if test="${ssDashboard.sharedModificationAllowed}">
	        <input type="radio" name="_scope" value="binder"/><ssf:nlt tag="dashboard.componentScope.binder"/><br>
	      </c:if>
	      <span><ssf:nlt tag="dashboard.componentType"/></span><br>
	      <c:forEach var="component" items="${ssDashboard.components_narrow_variable}">
	         <input type="radio" name="name" value="${component}">
	           <ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/>
	         <br>
	      </c:forEach>
	    </div>
	  <input class="ss_form" type="submit" name="add_narrowVariable" 
	    value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
	  <input class="ss_form" type="submit" name="cancel" 
	    value="<ssf:nlt tag="button.cancel"/>" 
	    onClick="ss_hideDashboardMenu(this);return false;">
	  <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	  <input type="hidden" name="_componentId" value="">
	  <input type="hidden" name="_returnView" value="binder"/>
      </form>
      </ssf:menu>
      <br>
	</div>
	
	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	  style="visibility:hidden; display:none; width:1px; height:20px;"></div>
	<c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
  </td>
</tr>

<tr>
  <td colspan="3">
      <c:forEach var="component" items="${ssDashboard.wide_bottom}">
		<c:set var="id" value="${component.id}"/>
		<c:set var="scope" value="${ssDashboard.scope}"/>
		<c:set var="componentScope" value="${component.scope}"/>
		<c:set var="dashboardList" value="wide_bottom"/>
		<c:set var="returnView" value="binder"/>
		<div style="margin:0px; padding:0px;">
		  <%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_component.jsp" %>
		</div>
	  </c:forEach>

	<div id="ss_dashboard_toolbar_${ss_toolbar_count}"
	   style="visibility:hidden; display:none;">
	  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
	  <ssf:menu title="<%= ss_dashboardAddWide %>" titleClass="ss_linkButton" 
	    titleId="ss_addDashboardContentWideBottom" menuWidth="400px" offsetTop="2"
	    openStyle="popup">
	  <form method="post" action="<portlet:actionURL>
		  <portlet:param name="action" value="modify_dashboard"/>
		  <portlet:param name="binderId" value="${ssBinder.id}"/>
		  <portlet:param name="binderType" value="${ssBinder.entityIdentifier.entityType}"/>
	      </portlet:actionURL>">
		<div class="ss_form">
	      <span><ssf:nlt tag="dashboard.componentScope"/></span><br>
	      <input type="radio" name="_scope" value="local" checked/><ssf:nlt tag="dashboard.componentScope.local"/><br>
	      <input type="radio" name="_scope" value="global"/><ssf:nlt tag="dashboard.componentScope.global"/><br>
	      <c:if test="${ssDashboard.sharedModificationAllowed}">
	        <input type="radio" name="_scope" value="binder"/><ssf:nlt tag="dashboard.componentScope.binder"/><br>
	      </c:if>
	      <span><ssf:nlt tag="dashboard.componentType"/></span><br>
	      <c:forEach var="component" items="${ssDashboard.components_wide}">
	         <input type="radio" name="name" value="${component}">
	           <ssf:nlt checkIfTag="true" tag="${ssDashboard.component_titles[component]}"/>
	         <br>
	      </c:forEach>
	    </div>
	  <input class="ss_form" type="submit" name="add_wideBottom" 
	    value="<ssf:nlt tag="button.ok"/>">&nbsp;&nbsp;
	  <input class="ss_form" type="submit" name="cancel" 
	    value="<ssf:nlt tag="button.cancel"/>" 
	    onClick="ss_hideDashboardMenu(this);return false;">
	  <input type="hidden" name="_dashboardList" value="${ssDashboard.dashboardList}">
	  <input type="hidden" name="_componentId" value="">
	  <input type="hidden" name="_returnView" value="binder"/>
      </form>
      </ssf:menu>
      <br>
	</div>
	
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
