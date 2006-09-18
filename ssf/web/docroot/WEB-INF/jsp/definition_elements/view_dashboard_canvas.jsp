<% //View dashboard canvas %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
  
  <!-- Start of dashboard "Add penlet" form -->
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
  <!-- End of dashboard "add penlet" form -->
  
<c:if test="${!empty ssDashboard.wide_top || !empty ssDashboard.wide_bottom || !empty ssDashboard.narrow_fixed || !empty ssDashboard.narrow_variable}">

<!-- Start of dashboard canvas -->
<div class="ss_decor-round-corners-top2"><div><div></div></div></div>
<div class="ss_decor-border5">
  <div class="ss_decor-border6">
    <div class="ss_content_window">
		<table cellspacing="0" cellpadding="0" style="margin-bottom:2px;">
		<col width="20">
		<col width="100%">
		<col width="0*">
		<tr>
		  <td valign="middle" nowrap>
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
		  <td align="right" valign="top" nowrap>
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


		<!-- Start of dashboard components -->
		<div id="ss_dashboardComponentCanvas"
		<c:if test="${ss_show_all_dashboard_components}">
		  style="visibility:visible; display:block;"
		</c:if>
		<c:if test="${!ss_show_all_dashboard_components}">
		  style="visibility:hidden; display:none;"
		</c:if>
		>

<c:set var="ss_dashboard_table_scope" value="local" scope="request"/>
<%@ include file="/WEB-INF/jsp/definition_elements/view_dashboard_canvas_table.jsp" %>

	  	</div>
		<!-- End of dashboard components -->

    </div>
  </div>
</div>
<div class="ss_decor-round-corners-bottom2"><div><div></div></div></div>
<!-- End of dashboard canvas -->

</c:if>
<c:if test="${!ss_show_all_dashboard_components}">
<br/>
</c:if>
