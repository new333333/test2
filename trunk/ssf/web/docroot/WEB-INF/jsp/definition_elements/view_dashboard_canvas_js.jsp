<% //View dashboard canvas (javascript) %>
<script type="text/javascript">
var ss_dbrn = Math.round(Math.random()*999999)
var ss_toolbar_count = 0;
var ss_componentSrcHide = "<html:imagesPath/>pics/sym_s_hide.gif"
var ss_componentSrcShow = "<html:imagesPath/>pics/sym_s_show.gif"
var ss_componentAltHide = "<ssf:nlt tag="button.hide"/>"
var ss_componentAltShow = "<ssf:nlt tag="button.show"/>"

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
function ss_showHideDashboardComponent(obj, componentId, divId) {
	ss_debug(obj.alt + ",    " + obj.src)
	var formObj = ss_getContainingForm(obj)
	var url = "";
	var callbackRoutine = ""
	if (obj.src.match(/sym_s_show.gif/)) {
		url = "<ssf:url 
	    	adapter="true" 
	    	portletName="ss_forum" 
	    	action="__ajax_request" 
	    	actionUrl="true" >
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="operation" value="show_component" />
	    	</ssf:url>"
	    callbackRoutine = ss_showComponentCallback;
	    obj.src = ss_componentSrcHide;
	    obj.alt = ss_componentAltHide;
	} else if (obj.src.match(/sym_s_hide.gif/)) {
		url = "<ssf:url 
	    	adapter="true" 
	    	portletName="ss_forum" 
	    	action="__ajax_request" 
	    	actionUrl="true" >
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="operation" value="hide_component" />
	    	</ssf:url>"
	    callbackRoutine = ss_hideComponentCallback;
	    obj.src = ss_componentSrcShow;
	    obj.alt = ss_componentAltShow;
		var targetDiv = document.getElementById(divId);
		if (targetDiv) {
			targetDiv.innerHTML = "";
			//Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	}
	if (componentId != "") {url += "\&operation2=" + componentId;}
	if (formObj._dashboardList.value != "") {url += "\&_dashboardList=" + formObj._dashboardList.value;}
	if (formObj._scope.value != "") {url += "\&_scope=" + formObj._scope.value;}
	url += "\&rn=" + ss_dbrn++
	if (callbackRoutine != "") fetch_url(url, callbackRoutine, divId);
}
function ss_showComponentCallback(s, divId) {
	var targetDiv = document.getElementById(divId);
	if (targetDiv) {
		targetDiv.innerHTML = s;
		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
	}
}
function ss_hideComponentCallback(s, divId) {
}
function ss_confirmDeleteComponent(obj, componentId) {
	var formObj = ss_getContainingForm(obj)
	var confirmText = "";
	if (formObj._scope.value == "local") {
		confirmText = "<ssf:nlt tag="dashboard.confirmDeleteLocal"/>";
	} else if (formObj._scope.value == "global") {
		confirmText = "<ssf:nlt tag="dashboard.confirmDeleteGlobal"/>";
	} else if (formObj._scope.value == "binder") {
		confirmText = "<ssf:nlt tag="dashboard.confirmDeleteBinder"/>";
	} else {
		confirmText = "<ssf:nlt tag="dashboard.confirmDeleteUnknown"/>";
	}
	var confirmText2 = "<ssf:nlt tag="dashboard.confirmDelete"/>";
	if (confirm(confirmText + "\n" + confirmText2)) return true;
	return false;
}

function ss_addDashboardComponent(obj, component) {
	var formObj = ss_getContainingForm(obj)
	formObj.name.value = component;
	formObj.submit();
}

function ss_modifyDashboardComponent(obj, componentScope) {
	var formObj = ss_getContainingForm(obj)
	formObj._scope.value = componentScope;
}

function ss_hideDashboardMenu(obj) {
	var formObj = ss_getContainingForm(obj)
	ss_hideDiv(formObj.parentNode.id)
}
</script>
