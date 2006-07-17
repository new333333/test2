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
		
		// component has been removed.  exit.
		if (bar == null)
			return;
			
		if (bar.startOut) {
			// stop fadeOut prematurely
			clearTimeout(bar.timerOut);
			//debug_div.innerHTML += bar.timerOut + " stop OUT prematurely<br/>";
			bar.timerOut = 0;
		}
		bar.startOut = false;		
		bar.startIn = true;		

		bar.opac += 20;
		//debug_div.innerHTML += "IN "+bar.opac+"<br/>";
		ss_setOpacity(bar, bar.opac);
		bar.style.display = "block";
		
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
			//debug_div.innerHTML += + bar.timerIn + " stop IN prematurely<br/>";
			bar.timerIn = 0;
		}
		bar.startIn = false;
		bar.startOut = true;		
		
		bar.opac -= 20;
		//debug_div.innerHTML += "OUT "+bar.opac+"<br/>";
		ss_setOpacity(bar, bar.opac);
		bar.style.display = "block";
		if (bar.opac > 0) {
			bar.timerOut = setTimeout("ss_dashboardComponentToolbar.fadeOut(\"" + id + "\")", 50);
		}
		else {
			bar.style.display = "none";
			bar.timerOut = 0;
			bar.startOut = false;
		}
	},
	
	init : function (bar) {
	},
	
	hide : function (id) {
		var bar = document.getElementById(id);
		//debug_div.innerHTML += "<br/>hide " + bar.timerIn + " " + bar.startIn + " <br/>";
		
		// If fadeIn timer has been set, but hasn't started, cancel it
		if (bar.timerIn && !bar.startIn) {
			// cancel unstarted fadeIn
			//debug_div.innerHTML +=  "cancel unstarted IN<br/>";
			clearTimeout(bar.timerIn);
			bar.timerIn = 0;
		}	
		
		if (!bar.startOut && bar.opac > 0) {
			if (bar.timerOut) {
				// reset unstarted fadeOut timer
				clearTimeout(bar.timerOut);
				//debug_div.innerHTML += "Out restarted<br/>";
				bar.timerOut = 0;
			}

			this.init(bar);
			bar.timerOut = setTimeout("ss_dashboardComponentToolbar.fadeOut(\"" + id + "\")", 150);
			//debug_div.innerHTML += bar.timerOut + " hide OUT<br/>";
		}
	},
	
	show : function (id) {
		//debug_div.innerHTML += "<br/>show<br/>";
		var bar = document.getElementById(id);
		
		// If fadeOut timer has been set, but hasn't started, cancel it
		if (bar.timerOut && !bar.startOut) {
			// cancel unstarted fadeOut
			//debug_div.innerHTML +=  "cancel unstarted OUT<br/>";
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
				//debug_div.innerHTML += "In restarted<br/>";
				bar.timerIn = 0;
			}

			this.init(bar);
			bar.timerIn = setTimeout("ss_dashboardComponentToolbar.fadeIn(\"" + id + "\")", 150);
			//debug_div.innerHTML += bar.timerIn + " show IN<br/>";
		}
	}
}

</script>
