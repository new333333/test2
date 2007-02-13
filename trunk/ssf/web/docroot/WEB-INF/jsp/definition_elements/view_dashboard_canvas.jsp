<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>
  
  <!-- Start of dashboard "Add penlet" form -->
  <c:if test="${empty ssBinderConfig}">
  <div id="<portlet:namespace/>_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
    <form method="post" action="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard"/>
	    <portlet:param name="binderId" value="${ssBinder.id}"/>
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
        <c:forEach var="component" items="${ssDashboard.components_list}">
           <input type="radio" name="componentName" value="${component}">
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
  </c:if>
  <c:if test="${!empty ssBinderConfig}">
  <div id="<portlet:namespace/>_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
    <form method="post" action="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard"/>
	    <portlet:param name="binderId" value="${ssBinder.id}"/>
        </portlet:actionURL>">
         <input type="hidden" name="_scope" value="binder"/>
 	  <div style="margin:10px;">
        <span class="ss_bold"><ssf:nlt tag="dashboard.componentType"/></span><br>
        <c:forEach var="component" items="${ssDashboard.components_list}">
           <input type="radio" name="componentName" value="${component}">
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
  </c:if>
  <!-- End of dashboard "add penlet" form -->
<c:if test="${!empty ssDashboardToolbar}">  
<div class="ss_dashboardContainer" >
	<div class="ss_utils_bar" >
	<ssf:toolbar toolbar="${ssDashboardToolbar}" style="ss_utils_bar" />
	</div>
	<div class="ss_clear"></div>
</c:if>

<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

  <div id="<portlet:namespace/>_dashboardConfigurationMenu" class="ss_dashboard_menu" align="left">
	<ul class="ss_dropdownmenu" 
	  style="list-style: outside; margin:2px 2px 2px 18px; padding:2px;">
	  <li><a href="<portlet:renderURL>
	  <portlet:param name="action" value="modify_dashboard"/>
	  <portlet:param name="binderId" value="${ssBinder.id}"/>
	  <portlet:param name="_scope" value="local"/>
	  <portlet:param name="operation" value="set_dashboard_title"/>
	  </portlet:renderURL>"><ssf:nlt tag="dashboard.setTitle"/></a></li>
	  <li><a href="<portlet:actionURL>
	  <portlet:param name="action" value="modify_dashboard"/>
	  <portlet:param name="binderId" value="${ssBinder.id}"/>
	  <portlet:param name="_scope" value="global"/>
	  </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.global"/></a></li>
	  <c:if test="${ssDashboard.sharedModificationAllowed}">
	    <li><a href="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard"/>
	    <portlet:param name="binderId" value="${ssBinder.id}"/>
	    <portlet:param name="_scope" value="binder"/>
	    </portlet:actionURL>"><ssf:nlt tag="dashboard.configure.binder"/></a></li>
	  </c:if>
	</ul>
  </div>

 <!-- Start of dashboard canvas -->
  <div id="<portlet:namespace/>_dashboardComponentCanvas"
	<c:if test="${ss_show_all_dashboard_components}">
	  style="visibility:visible; display:block;"
	</c:if>
	<c:if test="${!ss_show_all_dashboard_components}">
	  style="visibility:hidden; display:none;"
	</c:if>
 >

	<div class="ss_content_window">
		<span class="ss_bold"><c:out value="${ssDashboard.title}"/> 
		  <c:if test="${ssDashboard.includeBinderTitle}">
		    <c:out value="${ssBinder.title}"/>
		  </c:if>
		</span>

		<div id="<portlet:namespace/>_dashboard_toolbar_${ss_toolbar_count}"
		   style="visibility:hidden; display:none;">
		  <c:set var="ss_toolbar_count" value="${ss_toolbar_count + 1}"/>
		</div>


		<!-- Start of dashboard components -->
		<div>

<c:set var="ss_dashboard_table_scope" value="local" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_table.jsp" %>

	  	</div>
		<!-- End of dashboard components -->
	</div>
  </div>
<!-- End of dashboard canvas -->

</c:if>
<c:if test="${!empty ssDashboardToolbar}">
</div>
</c:if>