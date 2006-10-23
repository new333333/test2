<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
  <ssf:toolbar toolbar="${ss_toolbar}" style="ss_actions_bar" />
  
  <!-- Start of dashboard "Add penlet" form -->
  <div id="<portlet:namespace/>_dashboardAddContentPanel" class="ss_dashboard_menu" align="left">
    <form method="post" action="<portlet:actionURL>
	    <portlet:param name="action" value="modify_dashboard_portlet"/>
	    <portlet:param name="dashboardId" value="${ssDashboardId}"/>
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
 <div id="<portlet:namespace/>_dashboardComponentCanvas"
	<c:if test="${ss_show_all_dashboard_components}">
	  style="visibility:visible; display:block;"
	</c:if>
	<c:if test="${!ss_show_all_dashboard_components}">
	  style="visibility:hidden; display:none;"
	</c:if>
 >
 <div class="ss_decor-round-corners-top2 ss_innerContentBegins" ><div><div>
 </div></div></div>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">
		    <span class="ss_bold"><c:out value="${ssDashboard.title}"/> 
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
</div>
<div class="ss_decor-round-corners-bottom2"><div><div></div></div></div>
</div>
<!-- End of dashboard canvas -->

</c:if>
<c:if test="${!ss_show_all_dashboard_components}">
<br/>
</c:if>
