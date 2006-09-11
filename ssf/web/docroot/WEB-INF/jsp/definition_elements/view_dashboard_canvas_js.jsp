<% //View dashboard canvas (javascript) %>
<script type="text/javascript">
//Initialize the variables only once
if (!ss_dbrn) {
	var ss_dbrn = Math.round(Math.random()*999999)
	var ss_toolbar_count = 0;
	var ss_dashboard_control_count = 0;
	var ss_dashboard_border_count = 0;
	var ss_dashboard_border_classNames = new Array();
	var ss_componentSrcHide = "<html:imagesPath/>skins/${ss_user_skin}/iconset/hide.gif"
	var ss_componentSrcShow = "<html:imagesPath/>skins/${ss_user_skin}/iconset/show.gif"
	var ss_componentAltHide = "<ssf:nlt tag="button.hide"/>"
	var ss_componentAltShow = "<ssf:nlt tag="button.show"/>"
	var ss_toolbarAddContent = "<ssf:nlt tag="dashboard.addContent"/>"
	var ss_toolbarHideContent = "<ssf:nlt tag="dashboard.addContentOff"/>"
	var ss_toolbarShowControls = "<ssf:nlt tag="dashboard.showHiddenControls"/>"
	var ss_toolbarHideControls = "<ssf:nlt tag="dashboard.showHiddenControlsOff"/>"
	var ss_dashboardConfirmDelete = "<ssf:nlt tag="dashboard.confirmDelete"/>";
	var ss_dashboardConfirmDeleteLocal = "<ssf:nlt tag="dashboard.confirmDeleteLocal"/>";
	var ss_dashboardConfirmDeleteGlobal = "<ssf:nlt tag="dashboard.confirmDeleteGlobal"/>";
	var ss_dashboardConfirmDeleteBinder = "<ssf:nlt tag="dashboard.confirmDeleteBinder"/>";
	var ss_dashboardConfirmDeleteUnknown = "<ssf:nlt tag="dashboard.confirmDeleteUnknown"/>";

	var ss_showDashboardComponentUrl = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssBinder.id}" />
		<ssf:param name="operation" value="show_component" />
    	</ssf:url>";

	var ss_hideDashboardComponentUrl = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssBinder.id}" />
		<ssf:param name="operation" value="hide_component" />
    	</ssf:url>";

	var ss_deleteDashboardComponentUrl = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="binderId" value="${ssBinder.id}" />
		<ssf:param name="operation" value="delete_component" />
    	</ssf:url>";
}

</script>
