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
	} else if (obj.src.match(/sym_s_delete.gif/)) {
		url = "<ssf:url 
	    	adapter="true" 
	    	portletName="ss_forum" 
	    	action="__ajax_request" 
	    	actionUrl="true" >
			<ssf:param name="binderId" value="${ssBinder.id}" />
			<ssf:param name="operation" value="delete_component" />
	    	</ssf:url>"
	    callbackRoutine = ss_hideComponentCallback;
		var targetDiv = document.getElementById(divId);
		if (targetDiv) {
			targetDiv.innerHTML = "";
			//Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	}
	if (componentId != "") {url += "\&operation2=" + componentId;}
	if (formObj._dashboardList && formObj._dashboardList.value != "") {
		url += "\&_dashboardList=" + formObj._dashboardList.value;
	}
	if (formObj._scope && formObj._scope.value != "") {
		url += "\&_scope=" + formObj._scope.value;
	}
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
function ss_confirmDeleteComponent(obj, componentId, divId, divId2) {
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
	if (!confirm(confirmText + "\n" + confirmText2)) return;
	ss_showHideDashboardComponent(obj, componentId, divId)
	if (divId2 && document.getElementById(divId2)) {
		ss_hideDiv(divId2)
	}
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

//This routine was ported from Liferay portal.js
var ss_dashboardComponentToolbar = {

	fadeIn : function (id) {
		var bar = document.getElementById(id);
	    if (bar.style.visibility == 'hidden') {
	    	//ss_setOpacity(document.getElementById(id),0.1);
	    	//ss_setOpacity(bar, 0.01)
	    	ss_dashboardComponentToolbar.changeOpacity(bar, 0.01)
	    	ss_showDiv(id);
			if (!bar.opac || bar.opac < 0) {
				bar.opac = 0;
			}
	    }
		
		// component has been removed.  exit.
		if (bar == null)
			return;
			
		if (bar.startOut) {
			// stop fadeOut prematurely
			clearTimeout(bar.timerOut);
			ss_debug(bar.timerOut + " stop OUT prematurely");
			bar.timerOut = 0;
		}
		bar.startOut = false;		
		bar.startIn = true;		

		bar.opac += 20;
		ss_debug("IN "+parseFloat(parseFloat(bar.opac) / 100.0));
		ss_dashboardComponentToolbar.changeOpacity(bar, parseFloat(parseFloat(bar.opac) / 100.0));
		
		if (bar.opac < 100) {
			bar.timerIn = setTimeout("ss_dashboardComponentToolbar.fadeIn(\"" + id + "\")", 50);
		}
		else {
			bar.timerIn = 0;
			bar.startIn = false;
		}
	},
	
	fadeOut : function (id) {
		var bar = document.getElementById(id);
		
		// component has been removed.  exit.
		if (bar == null)
			return;
		
		if (bar.startIn) {
			// stop fadeIn prematurely
			clearTimeout(bar.timerIn);
			ss_debug(bar.timerIn + " stop IN prematurely");
			bar.timerIn = 0;
		}
		bar.startIn = false;
		bar.startOut = true;		
		
		bar.opac -= 20;
		ss_debug("OUT "+parseFloat(parseFloat(bar.opac) / 100.0));
		ss_dashboardComponentToolbar.changeOpacity(bar, parseFloat(parseFloat(bar.opac) / 100.0));
		if (bar.opac > 0) {
			bar.timerOut = setTimeout("ss_dashboardComponentToolbar.fadeOut(\"" + id + "\")", 50);
		}
		else {
			bar.style.visibility = "hidden";
			bar.timerOut = 0;
			bar.startOut = false;
		}
	},
	
	init : function (bar) {
	},
	
	hide : function (id) {
		var bar = document.getElementById(id);
		ss_debug("hide " + bar.timerIn + " " + bar.startIn);
		
		// If fadeIn timer has been set, but hasn't started, cancel it
		if (bar.timerIn && !bar.startIn) {
			// cancel unstarted fadeIn
			ss_debug("cancel unstarted IN");
			clearTimeout(bar.timerIn);
			bar.timerIn = 0;
		}	
		
		if (!bar.startOut && bar.opac > 0) {
			if (bar.timerOut) {
				// reset unstarted fadeOut timer
				clearTimeout(bar.timerOut);
				ss_debug("Out restarted");
				bar.timerOut = 0;
			}

			this.init(bar);
			bar.timerOut = setTimeout("ss_dashboardComponentToolbar.fadeOut(\"" + id + "\")", 150);
			ss_debug(bar.timerOut + " hide OUT");
		}
	},
	
	show : function (id) {
		ss_debug("show");
		var bar = document.getElementById(id);
		
		// If fadeOut timer has been set, but hasn't started, cancel it
		if (bar.timerOut && !bar.startOut) {
			// cancel unstarted fadeOut
			ss_debug("cancel unstarted OUT");
			clearTimeout(bar.timerOut);
			bar.timerOut = 0;
		}
		
		if (!bar.startIn && (!bar.opac || bar.opac < 100)){
			if (!bar.opac) {
				bar.opac = 0;
			}

			if (bar.timerIn) {
				// reset unstarted fadeIn timer
				clearTimeout(bar.timerIn);
				ss_debug("In restarted");
				bar.timerIn = 0;
			}

			this.init(bar);
			bar.timerIn = setTimeout("ss_dashboardComponentToolbar.fadeIn(\"" + id + "\")", 150);
			ss_debug(bar.timerIn + " show IN");
		}
	},
	
	changeOpacity : function (object, opacity) {
		opacity = (opacity >= 1.0) ? 0.999 : opacity;
		opacity = (opacity < 0) ? 0 : opacity;
	    
		ss_debug("change opacity = " + opacity)
		object.style.opacity = (opacity);
		object.style.MozOpacity = (opacity);
		object.style.KhtmlOpacity = (opacity);
		object.style.filter = "alpha(opacity=" + opacity * 100.0 + ")";
	}
	
}

</script>
