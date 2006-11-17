<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_portlet_style ss_portlet">
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_js.jsp" %>

<% // Toolbar %>
<c:if test="${!empty ss_toolbar}">
<ssf:toolbar toolbar="${ss_toolbar}" style="ss_actions_bar" />
</c:if>
  
  <!-- Start of dashboard "Add penlet" form -->
  <div id="<portlet:namespace/>_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
    <form method="post" action="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard_portlet"/>
<c:if test="${!empty ssDashboardId}">
	    <portlet:param name="dashboardId" value="${ssDashboardId}"/>
</c:if>
	    <portlet:param name="scope" value="${ssDashboard.scope}"/>
        </portlet:actionURL>">
	  <div style="margin:10px;">
        <br/>
        <span class="ss_bold"><ssf:nlt tag="dashboard.componentType"/></span><br>
        <c:forEach var="component" items="${ssDashboard.components_list}">
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
	    <input type="hidden" name="_returnView" value="dashboard"/>
	  </div>
    </form>
  </div>
  <!-- End of dashboard "add penlet" form -->
  
<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

<!-- Start of dashboard canvas -->
<div style="padding:0px 4px 4px 4px;">
 <div id="<portlet:namespace/>_dashboardComponentCanvas"
	<c:if test="${ss_show_all_dashboard_components}">
	  style="visibility:visible; display:block;"
	</c:if>
	<c:if test="${!ss_show_all_dashboard_components}">
	  style="visibility:hidden; display:none;"
	</c:if>
 >
<div class="ss_decor-round-corners-top2 ss_innerContentBegins" ><div><div></div></div></div>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window" style="margin:0px 4px;">
		<span class="ss_bold"><c:out value="${ssDashboard.title}"/> </span>

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
</div>
<div class="ss_decor-round-corners-bottom2"><div><div></div></div></div>
</div>
<!-- End of dashboard canvas -->

</c:if>
<c:if test="${!ss_show_all_dashboard_components}">
<br/>
</c:if>
</div>
</div>
