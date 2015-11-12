/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0
 * (the "CPAL"); you may not use this file except in compliance with the CPAL.
 * You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla
 * Public License Version 1.1 but Sections 14 and 15 have been added to cover
 * use of software over a computer network and provide for limited attribution
 * for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis,
 * WITHOUT WARRANTY OF ANY KIND, either express or implied. See the CPAL for the
 * specific language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information: Attribution Copyright Notice: Copyright (c)
 * 1998-2015 Novell, Inc. All Rights Reserved. Attribution Phrase (not exceeding
 * 10 words): [Powered by Kablink] Attribution URL: [www.kablink.org] Graphic
 * Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png]. Display of Attribution Information
 * is required in Larger Works which are defined in the CPAL as a work which
 * combines Covered Code or portions thereof with code not governed by the terms
 * of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

//
// Common javascript functions for forum portlets
//

// browser-specific vars
var undefined;
var ss_declaredDivs;
if (typeof ss_common_loaded == "undefined" ) {
	var ss_isNSN = (navigator.appName == "Netscape");
	var ss_isNSN4 = ss_isNSN && ((navigator.userAgent.indexOf("Mozilla/4") > -1));
	var ss_isNSN6 = ((navigator.userAgent.indexOf("Netscape6") > -1));
	var ss_isMoz5 = ((navigator.userAgent.indexOf("Mozilla/5") > -1) && !ss_isNSN6);
	var ss_isMacIE = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("Mac") > -1));
	var ss_isIE = ((navigator.userAgent.indexOf("IE ") > -1));
	var ss_isIE6 = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("MSIE 6") > -1));
	var ss_isOpera = ((navigator.userAgent.indexOf("Opera/") > -1));
	
	// Random number seed (for building urls that are unique)
	var ss_now = new Date();
	var ss_random = Math.round(Math.random()*ss_now.getTime());
	
	// Files that don't pop-up in a new window when viewing them (space
	// separated)
	var ss_files_that_do_not_pop_up = "doc xls";
	
	// zIndex map
	var ssPortletZ = 5
	var ssLightboxZ = 2000;
	var ssHelpZ = 2000;
	var ssHelpSpotZ = 2001;
	var ssHelpPanelZ = 2003;
	var ssHelpWelcomeZ = 2002;
	var ssMenuZ = 500;
	var ssPopupZ = 600;
	var ssDragOnTopZ = 100000;
	var ssEntryZ = 350;
	var ssDragEntryZ = 400;
	var ssSlidingTableInfoZ = 40;
	var ssDashboardTargetZ = 1000;
	var ss_gwtLightboxZ = 200;  
	
	// colors (defined at onLoad time by ss_defineColorValues)
	var ss_style_background_color = "";
	var ss_dashboard_table_border_color = "";
	
	var ss_dashboardDropTargetHeight = "10px";
	var ss_dashboardDropTargetTopOffset = -4;
	var ss_dashboardTopDropTargetHeight = "20px";
	var ss_dashboardTableBorderColor = "";

	var ss_savedOnResizeRoutine = null;
	var ss_onResizeRoutineLoaded;
	var ss_savedOnLoadRoutine = null;
	var ss_onLoadRoutineLoaded;

	var ss_mouseX = 0;
	var ss_mouseY = 0;
	var ss_mousePosX = 0;
	var ss_mousePosY = 0;
	var ss_divBeingShown = null;
	var ss_lastDivBeingShown = null;
	
	var ss_declaredDivs = new Array();
	var ss_eventList = new Array();
	var ss_eventTypeList = new Array();
	var ss_onLoadList = new Array();
	var ss_onSubmitList = new Array();
	var ss_onResizeList = new Array();
	var ss_onLayoutChangeList = new Array();
	var ss_validatorList = new Array();
	var ss_menuDivClones = new Array();
	var ss_divToBeHidden = new Array();
	var ss_divToBeDelayHidden = new Array();
	var ss_onErrorList = new Array();
	var ss_spannedAreasList = new Array();
	var ss_active_menulayer = '';
	var ss_lastActive_menulayer = '';
	var ss_activateMenuOffsetTop = 0;
    var ss_activateMenuOffsetLeft = 0;
	var ss_layerFlag = 0;
	var ss_forum_maxBodyWindowHeight = 0;
	var ss_divFadeInArray = new Array();
	var ss_divFadeOutArray = new Array();
	var ss_helpSystemNextNodeId = 1;
	var ss_helpSystemNodes = new Array();
	var ss_helpSystemPanels = new Array();
	var ss_helpSystemHighlights = new Array();
	var ss_helpSystemHighlightsBorder = new Array();
	var ss_helpSystemHighlightsBorderTimer = new Array();
	var ss_helpSystemTOC = new Array();
	var ss_helpSystemTOCindex = -1;
	var ss_helpSystemQueuedId = "";
	var ss_helpSystemQueuedPanelId = "";
	var ss_helpSystemQueuedX = "";
	var ss_helpSystemQueuedY = "";
	var ss_helpSystemRequestInProgress = 0;
	var ss_helpSystemPanelMarginOffset = 4;
	
	var ss_favoritesPaneTopOffset = 5;
	var ss_favoritesPaneLeftOffset = 4;

	var ss_dashboardClones = new Array();
	var ss_dashboardSliderObj = null;
	var ss_dashboardSliderTargetObj = null;
	var ss_dashboardSliderObjEndCoords = null;
	
	var ss_currentTab = 0;
		
	var ss_statusCurrent = "";
	var ss_statusTimer = null;
	var ss_statusObj = null;
	var ss_statusOnMouseOver = false;
	
	//Dashboard variables
	var ss_dbrn;
	var ss_componentTextHide;
	var ss_componentTextShow;
	var ss_componentSrcHide;
	var ss_componentSrcShow;
	var ss_componentAltHide;
	var ss_componentAltShow;
	var ss_toolbarAddContent;
	var ss_toolbarHideContent;
	var ss_toolbarShowControls;
	var ss_toolbarHideControls;
	var ss_dashboardConfirmDelete;
	var ss_dashboardConfirmDeleteLocal;
	var ss_dashboardConfirmDeleteGlobal;
	var ss_dashboardConfirmDeleteBinder;
	var ss_dashboardConfirmDeleteUnknown;

}
var ss_common_loaded = 1;

// Function to load javascript files after the "head" has been output
// This routine prevents the file from being loaded twice
function ss_loadJsFile(rootPath, jsFile) {
	//alert( 'in ss_loadJsFile(), rootPath: ' + rootPath + '  jsFile: ' + jsFile );
	var spath = rootPath + jsFile;
	var scripts = document.getElementsByTagName("script");
	for (var i = 0; i < scripts.length; i++) {
		//alert( 'scripts[i].src: ' + scripts[i].src );
		if (scripts[i].src && scripts[i].src.indexOf( spath ) >= 0 ) return;
	}
	try {
		document.writeln("<scr"+"ipt type='text/javascript' src='"+spath+"'><"+"/scr"+"ipt>");
	} catch (e) {
		var script = document.createElement("script");
		script.src = spath;
		document.getElementsByTagName("head")[0].appendChild(script);
	}
}

// Routine called by the body's onLoad event
function ss_onLoadInit() {
    // Call any routines that want to be called at onLoad time
    for (var i = 0; i < ss_onLoadList.length; i++) {
        if (ss_onLoadList[i].initRoutine) {
        	ss_onLoadList[i].initRoutine();
        }
    }

    // Empty the on load list so that these don't get called again if
    // this method gets called again.
	ss_onLoadList = new Array();
	
    if (ss_savedOnLoadRoutine != null) {
    	window.onload = ss_savedOnLoadRoutine;
    	if (window.onload != null) window.onload();
    	ss_savedOnLoadRoutine = null;
    }
    
	// Add the onResize routine to the onresize event
	if (!ss_onResizeRoutineLoaded) {
		ss_onResizeRoutineLoaded = 1;
		ss_savedOnResizeRoutine = window.onresize;
		window.onresize = ssf_onresize_event_handler;
	}
}

// Add the onLoadInit routine to the onload event
if (!ss_onLoadRoutineLoaded) {
	ss_onLoadRoutineLoaded = 1;
	ss_savedOnLoadRoutine = window.onload;
	window.onload = ss_onLoadInit;
}

function ss_isInteger(val) {
	var digits="1234567890";
	for (var i=0; i < val.length; i++) {
		if (digits.indexOf(val.charAt(i))==-1) { return false; }
	}
	return true;
}

var ss_lastUserDisplayStyle = null;
function ss_getUserDisplayStyle() {
	if (self != self.parent && typeof self.parent.ss_getUserDisplayStyle != "undefined") {
		return self.parent.ss_getUserDisplayStyle();
	}
	if (ss_lastUserDisplayStyle != null && ss_userDisplayStyle != ss_lastUserDisplayStyle) {
		try {
			// When we have figured out how to dynamically update everything needed to view
			// an entry with a new entry display style, uncomment the if ( ss_isGwtUIActive == false ) statement.
			//!!!if ( ss_isGwtUIActive == false )
				self.location.reload();	  	
		} catch (e) {alert(e);}
	}
	ss_lastUserDisplayStyle = ss_userDisplayStyle;
	if (typeof ss_userDisplayStyle == "undefined" || ss_userDisplayStyle == "") {
		ss_userDisplayStyle = ss_defaultViewDisplayStyle;
	}
	return ss_userDisplayStyle;
}

// Routines to support Ajax
// suggest moving towards ss_get_url so errors can be handled consistantly
// Updated to dojo, result is text/plain
// used to fetch plain data and replace a div
function ss_fetch_div(url, divId, signal) {
	var bindArgs = {
	    	url: url,
			error: function(err) {
				alert(ss_not_logged_in);
			},
			load: function(data) {
	  		  try {
	  		  	dojo.byId(divId).innerHTML = data;
				// Signal that the layout changed
				if (signal) ssf_onLayoutChange();
		      } catch (e) {alert(e);}
			},
			preventCache: true
	};   
	dojo.xhrGet(bindArgs);
}
// suggest moving towards ss_get_url so errors can be handled consistantly
// Updated to dojo, result is text/plain
// used to fetch plain data
function ss_fetch_url(url, callbackRoutine, callbackData, toggleCall) {
	ss_fetch_url_debug("Request to fetch url: " + url)
	eval(toggleCall);
	var bindArgs = {
	    	url: url,
			error: function(err) {
				eval(toggleCall);
				alert(data.message);
			},
			load: function(data) {
				eval(toggleCall);
			    try {
					ss_fetch_url_debug("received " + data);
					if (callbackRoutine) callbackRoutine(data, callbackData);
				} catch (e) {alert(e);}
				// Signal that the layout changed
				if (ssf_onLayoutChange) setTimeout("ssf_onLayoutChange();", 100);
				
			},
			preventCache: true
	};   
	dojo.xhrGet(bindArgs);
}                
// Same as ss_fetch_url only do it as a post instead of a get
function ss_post_to_url(url, formName, callbackRoutine, callbackData, toggleCall) {
	ss_fetch_url_debug("Request to fetch url: " + url)
	eval(toggleCall);
	var bindArgs = {
	    	url: url,
	    	form: formName,
			error: function(err) {
				eval(toggleCall);
				alert(data.message);
			},
			load: function(data) {
				eval(toggleCall);
			    try {
					ss_fetch_url_debug("received " + data);
					if (callbackRoutine) callbackRoutine(data, callbackData);
				} catch (e) {alert(e);}
				// Signal that the layout changed
				if (ssf_onLayoutChange) setTimeout("ssf_onLayoutChange();", 100);
				
			},
			preventCache: true
	};   
	dojo.xhrPost(bindArgs);
}                

function ss_fetch_url_debug(str) {
    // ss_debug(str);
}
// Use dojo to post a form, results in text/json
// When result contains failure, message display
function ss_post(url, formId, callBackRoutine, callbackData, toggleCall) {
	eval(toggleCall);
	var bindArgs = {
    	url: url,
    	form: dojo.byId(formId),
		error: function(err) {
			eval(toggleCall);
			alert(err);
		},
		load: function(data) {
			eval(toggleCall);
			if (data.failure) {
				alert(data.failure);
			} else { 
				if (callBackRoutine) callBackRoutine(data, callbackData);
			}
		},
		preventCache: true,				
		handleAs: "json",
		method: "post"
	};   
	dojo.xhrPost(bindArgs);
}     
// Use dojo to get a url. Results in text/json.
// When result contains failure, message display
function ss_get_url(url, callBackRoutine, callbackData, toggleCall) {
	eval(toggleCall);
	var bindArgs = {
    	url: url,
		error: function(err) {
			eval(toggleCall);
			alert(err);
		},
		load: function(data) {
			eval(toggleCall);
			if (data.failure) {
				alert(data.failure);
			} else { 
				if (callBackRoutine) callBackRoutine(data, callbackData);
			}
		},
		preventCache: true,				
		handleAs: "json"
	};   
	dojo.xhrGet(bindArgs);
}     
function ss_buildAdapterUrl(base, paramMap, action) {
	var url = base;
	if (action && action != "") {
		url += "\&action=" + action;
	} else {
		url += "\&action=__ajax_request";
	}	
	for (var i in paramMap) {
		if (dojo.isArray(paramMap[i])){
			for(var j=0,l=paramMap[i].length; j<l; j++){
				url += "\&" + i + "=" + encodeURIComponent(paramMap[i][j]);
			}
		} else {
			url += "\&" + i + "=" + encodeURIComponent(paramMap[i]);
		}
	}

	return url;
}

// use for callbacks into objects. Keeps object references from hanging around.
function ss_createDelegate(object, method)
{
    var shim = function() {
          method.apply(object, arguments);
    }
    return shim;
}

// Routine to go to a permalink without actually using the permalink
function ss_gotoPermalink(binderId, entryId, entityType, namespace, useNewTab, useParentOrOpener) {

	var url = ss_getGeneratedURL(binderId, entryId, entityType, namespace, useNewTab);
	if (url == "") return true;

	if (typeof useParentOrOpener !== "undefined" && 
			useParentOrOpener) {
		if (self.opener) {
			self.opener.location.href = url;
			if (self != self.opener) {
				self.window.close();
			}
		} else if (self.parent) {
			self.parent.location.href = url;		
		} else {
			// See if this should be opened in ss_workarea
			if (typeof ss_workarea_showId != "undefined" && entityType == "workspace") {
				ss_workarea_showId(binderId, "view_ws_listing");
			} else if (typeof ss_workarea_showId != "undefined" && entityType == "user") {
				ss_workarea_showId(binderId, "view_ws_listing", entryId);
			} else if (typeof ss_workarea_showId != "undefined" && entityType == "folder") {
				ss_workarea_showId(binderId, "view_folder_listing");
			} else {
				ss_setSelfLocation(url);
			}
		}
	} else {
		// See if this should be opened in ss_workarea
		if (typeof ss_workarea_showId != "undefined" && entityType == "workspace") {
			ss_workarea_showId(binderId, "view_ws_listing");
		} else if (typeof ss_workarea_showId != "undefined" && entityType == "user") {
			ss_workarea_showId(binderId, "view_ws_listing", entryId);
		} else if (typeof ss_workarea_showId != "undefined" && entityType == "folder") {
			ss_workarea_showId(binderId, "view_folder_listing");
		} else {
			ss_setSelfLocation(url);
		}
	}
	return false;
}

// Routine to open a url in the workarea portlet if it exists
function ss_openUrlInWorkarea(url, id, action) {
	if (typeof ss_workarea_showId != "undefined" && id != '') {
		ss_workarea_showId(id, action);
	} else {
		ss_setSelfLocation(url);
	}
}

function ss_openUrlInParentWorkarea(url, id, action, target, close) {
	try {
		if (typeof self.parent.ss_workarea_showId != "undefined") {
			self.parent.ss_workarea_showId(id, action);
		} else {
			if (typeof target != 'undefined' && target != "") {
				var win = window.open(url, target);
				if (win != null && win.focus) win.focus();
				if (typeof close != 'undefined' && close == 'true') setTimeout("self.window.close()", 100);
			} else {
				self.parent.location.href = url;
			}
		}
	} catch(e) {
		if (typeof target != 'undefined' && target != "") {
			var win = window.open(url, target);
			if (win != null && win.focus) win.focus();
			if (typeof close != 'undefined' && close == 'true') setTimeout("self.window.close()", 100);
		} else {
			self.parent.location.href = url;
		}
	}
}

// Routine to navigate to a point on the navigation list
function ss_navigation_goto(url) {
	try {
		if (self.window != self.top) {
			parent.location.reload(true);
			return false;
		} else {
			return ss_openUrlInPortlet(url);
		}
	} catch(e) {
		return ss_openUrlInPortlet(url);
	}
}

// Routine to open a url in the portlet. This routine determines if the current
// code is
// running inside an iframe. It it is, then the url is opened in the parent of
// the iframe.
// This routine returns "true" without opening the url if the caller is not
// inside a frame.
// If the caller is in a frame (or iframe), then the routine opens the url in
// the parent and returns false.
function ss_openUrlInPortlet(url, popup, width, height) {
	if (width == null) width = "";
	if (height == null) height = "";
	// Is this a request to pop up?
	ss_debug('popup = '+popup+', url = '+url)
	if (popup) {
		ss_toolbarPopupUrl(url, "_blank", width, height);
		return false;
	}
	// Are we at the top window?
	try {
		if (self.window != self.top) {
			ss_debug('Not at top window')
			// See if we are in an iframe inside a portlet
			var windowName = self.window.name  
			if ((windowName.indexOf("ss_workareaIframe") == 0) ||
					(windowName.indexOf("gwtContentIframe")  == 0) ||
					(windowName.indexOf("adminContentControl")  == 0)) {
				// This is inside the workarea iframe, just let the url be
				// called
				return true;
			} else {
				// We are running inside a portlet iframe. Is the GWT
				// UI active?
				if (ss_isGwtUIActive) {
					// Yes! Then submit the url to the GWT UI content
					// frame.
					ss_setContentLocation(url);
				}
				else {
					// No, the GWT UI isn't active! Submit the URL
					// to the parent frame.
					parent.location.href = url;
				}
			}
			return false
		} else if ( self.opener !== undefined && self.opener != null ) {
			// If we are in a popup window.
			try {
				// Replace the contents of the current window with the new page.
				ss_setSelfLocation(url);
				
				// The following two lines of code were commented out as part of
				// the fix for bug 492902
// self.opener.location.href = url
// setTimeout('self.window.close();', 200)
				return false;
			} catch (e) {
				ss_debug('opener is not addressable anymore, it must have been deleted.')
				return true;
			}
		} else {
			ss_debug('return true')
			return true;
		}
	} catch(e) {
		return true;
	}
}


// Routine to open a page by following a "title" markup link
function ss_openTitleUrl(obj, showInParent) {
	if (typeof ss_showAsWiki != "undefined" && ss_showAsWiki) {
		ss_setSelfLocation(obj.href);
		return false;  //This is a wiki, just let the URL be executed in place
	}
	//ss_debug("**** ss_openTitleUrl - ss_showAsWiki: "+ss_showAsWiki)
	if (showInParent != null && showInParent) {
		try {
			// This is a request to just open the url in the parent (if it
			// exists and if not in the content frame)
			var windowName = self.window.name    
			//alert( 'windowName: ' + windowName );
			if ( windowName == 'ss_showentryframe' )
			{
				// We are opening the entry from within the "show entry" frame.  Just change
				// the url of the "show entry" frame to the new url.
				// Fix for bug 658648
				ss_setSelfLocation(obj.href);
				return false;
			}
			else if (windowName.indexOf("gwtContentIframe") == 0) {
				ss_setSelfLocation(obj.href);
				return false;
			} else if (typeof top.window.frames["gwtContentIframe"] != "undefined") {
				ss_setContentLocation(obj.href);
				return false;
			}
			if (self != self.parent) {
				self.parent.location.href = obj.href;
			} else {
				ss_setSelfLocation(obj.href);
			}
			return false;
		} catch(e) {
			ss_setSelfLocation(obj.href);
			return false;
		}
	}
	ss_showForumEntry(obj.href)
	return false;
}

function ss_postOpenTitleUrl(s) {
	alert('ss_postOpenTitleUrl: '+s)
}

// Routine to open a url in a new window
function ss_openUrlInWindow(obj, windowName, width, height) {
	if (typeof width == "undefined") width = ss_getWindowWidth();
	if (typeof height == "undefined") height = ss_getWindowHeight();
	if (typeof windowName == "undefined" || windowName == "") {
		// There is no window, so open it in this window
		return true;
	} else {
		var url = obj.href
		var win = self.window.open(url, windowName, 'directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width='+width+',height='+height)
		if (win != null && win.focus) win.focus();
	}
	return false;
}

// Routine to show the permalink url so it can by cut/pasted
function ss_showPermalink(obj, namespace) {
	if (typeof namespace == 'undefined' && typeof ss_namespace != 'undefined') namespace = ss_namespace;
	var divObj = document.getElementById('ss_permalink_display_div');
	var scrollIntoView = false;
	if (divObj != null && divObj.style && divObj.style.display && divObj.style.display == 'none') {
		scrollIntoView = true;
	}
	ss_toggleShowDiv('ss_permalink_display_div', namespace)	;
	if (scrollIntoView) {
		var x = 0;
		var y = parseInt(ss_mousePosY) + 100;
		smoothScroll(x, y);
	}
}
// Routine to go to a binder when it is clicked
// id can be a number or a string ending in "_1234" where 1234 is the id
function ss_treeShowId(id, obj, action, addParam) {
	var binderId = id;
	// See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	// Build a url to go to
	var url = ss_baseBinderUrl;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	if (addParam && ("&" == addParam.charAt(0))) {
		url += addParam;
	}

	// console.log(url);
	ss_setSelfLocation(url);
	return false;
}

function ss_treeShowIdNoWS(id, obj, action, namespace) {
	if (typeof namespace == "undefined" || namespace == null) namespace = "";
	var binderId = id;
	// See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	// Try to find the base urls from this namespace
	var url = "";
	try {
		eval("url = ss_baseBinderUrlNoWS" + namespace)
	} catch(e) {}
	
	// Build a url to go to
	if (url == "") url = ss_baseBinderUrlNoWS;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	// console.log(url);
	ss_setSelfLocation(url);
	return false;
}


// Routine to fetch a url in a iframe window (for accessibility mode)
function ss_fetchUrlInIframe(url, anchorDivName, width, height) {
    var iframeDivObj = self.document.getElementById("ss_reusableIframeDiv");
    var iframeObj = self.document.getElementById("ss_reusableIframe");
    var anchorDivObj = null;
    try { self.parent.document.getElementById(anchorDivName); } catch(e) {}
    if (iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
        iframeDivObj.setAttribute("id", "ss_reusableIframeDiv");
        iframeDivObj.name = "ss_reusableIframeDiv";
		iframeDivObj.className = "ss_popupMenu";
		iframeDivObj.style.zIndex = ssPopupZ;
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_reusableIframe");
		iframeDivObj.appendChild(iframeObj);
	    var closeDivObj = self.document.createElement("div");
	    closeDivObj.style.border = "2px solid gray";
	    closeDivObj.style.marginTop = "1px";
	    closeDivObj.style.padding = "6px";
	    iframeDivObj.appendChild(closeDivObj);
	    var aObj = self.document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDiv('ss_reusableIframeDiv');");
	    aObj.style.border = "2px outset black";
	    aObj.style.padding = "2px";
	    aObj.appendChild(document.createTextNode(ss_findButtonClose));
	    closeDivObj.appendChild(aObj);
		self.document.getElementsByTagName( "body" ).item(0).appendChild(iframeDivObj);
    }
    iframeObj.style.width = parseInt(width) + "px"
    iframeObj.style.height = parseInt(height) + "px"
	ss_showDiv("ss_reusableIframeDiv");
	if (anchorDivObj != null) {
		var x = dojo.coords(anchorDivObj, true).x
		var y = dojo.coords(anchorDivObj, true).y
	    ss_setObjectTop(iframeDivObj, y);
	    ss_setObjectLeft(iframeDivObj, x);
	}
//!	iframeObj.src = url;
	ss_setUrlInFrame(iframeObj, url);
}


// Routine to close a pop-up form window if the cancel button is clicked
// This routine checks to see if it is in a pop-up or in an iframe
function ss_cancelButtonCloseWindow() {
	if (ss_isGwtUIActive) {
		if ( self.window.name != "gwtContentIframe" && window.parent.ss_closeAdministrationContentPanel ) {
			// Tell the Teaming GWT ui to close the administration content
			// panel.
			window.parent.ss_closeAdministrationContentPanel();
			return;
		}
	}
	if (self == self.parent) {
		// This looks like it is a pop-up form
		self.window.close();
		return
	} else if (self != self.parent) {
		if (self.window.name == "ss_showpopupframe") {
			// This is in the popup iframe
			if (self.parent.ss_hidePopupDiv) self.parent.ss_hidePopupDiv();
			return;
		} else if (self.window.name == "gwtContentIframe") {
				// This is in the main content iframe
				self.history.go(-1);
				return;
		} else {
			iframeObj = self.parent.document.getElementById(self.name)
			if (iframeObj != null && iframeObj.tagName.toLowerCase() == 'iframe') {
				if (iframeObj.parentNode.tagName.toLowerCase() == 'div') {
					var divObj = self.parent.document.getElementById(iframeObj.parentNode.id);
					divObj.style.visibility = 'hidden';
					divObj.style.display = 'none';
					return
				}
			}
		}
	}
}

function ss_reloadOpenerParent(fallBackUrl) {
	// Are we at the top window?
	if (self.opener) {
		try {
			if (self.opener.window != self.opener.top) {
				if (self.opener.parent && self.opener.parent.ss_reloadUrl && self.opener.parent.ss_reloadUrl != "") {
					self.opener.parent.location.replace(self.opener.parent.ss_reloadUrl);
					setTimeout('self.window.close();', 200)
					return false;
				} else {
					self.opener.parent.location.href = fallBackUrl;
					setTimeout('self.window.close();', 200)
					return false;
				}
			}
			if (self.opener.ss_reloadUrl && self.opener.ss_reloadUrl != "") {
				self.opener.location.replace(self.opener.ss_reloadUrl);
				setTimeout('self.window.close();', 200)
			} else {
				self.opener.location.href = fallBackUrl;
				setTimeout('self.window.close();', 200)
			}
		} catch (e) {
			ss_reloadOpener(fallBackUrl);
		}
	} else {
		ss_reloadOpener(fallBackUrl);
	}
	return false;
}

function ss_reloadOpener(fallBackUrl) {
	// Are we at the top window?
	if (self.window != self.top) {
		// No! Are we running in the GWT UI?
		if (ss_isGwtUIActive) {
			// Yes! Then submit the fallBackUrl to the GWT UI
			// content frame.
			ss_setContentLocation(fallBackUrl);
		}
		
		// The remainder of this code is unchanged from what was
		// here BEFORE the GWT UI was implemented.
		else if (parent.ss_reloadUrl && parent.ss_reloadUrl != "") {
			parent.location.replace(parent.ss_reloadUrl);
		} else {
			parent.location.href = fallBackUrl;
		}
	} else if (self.opener) {
		try {
			if (self.opener.ss_reloadUrl && self.opener.ss_reloadUrl != "") {
				self.opener.location.replace(self.opener.ss_reloadUrl);
				setTimeout('self.window.close();', 200)
			} else {
				if (ss_isGwtUIActive) {
					self.opener.top.m_requestInfo.refreshSidebarTree = "true";
					ss_setOpenerLocation(fallBackUrl);
				}
				else {
					self.opener.location.href = fallBackUrl;
				}
				setTimeout('self.window.close();', 200)
			}
		} catch (e) {
			ss_setSelfLocation(fallBackUrl);
		}
	} else {
		ss_setSelfLocation(fallBackUrl);
	}
	return false;
}

// Routine to do a post to a URL
function ss_postToThisUrl(url) {
	formObj = document.createElement("form");
	formObj.method = "POST";
	formObj.action = url;
	document.getElementsByTagName( "body" ).item(0).appendChild(formObj);
	formObj.submit();
}

// Routines to move an object (or a div) to the "body"
// This is usefull for any absolutly positioned div.
// The positioning of that div will work correctly when using absolute
// coordinates.
function ss_moveDivToBody(name) {
	if (document.getElementById(name)) ss_moveObjectToBody(document.getElementById(name));
}
function ss_moveObjectToBody(obj) {
    if (obj && obj.parentNode.tagName.toLowerCase() != 'body') {
    	// move the object to the body tag so it goes to the right x,y
    	var id = obj.id;
    	obj.parentNode.removeChild(obj);
    	document.getElementsByTagName("body").item(0).appendChild(obj);
    }
}

// Function to resize the top div of the folder pages to get the data to display
// in sight
// This is the margin offset of the top div style (ss_pseudoPortal)
var ss_topDivMarginOffset = 32; 
var ss_origianalTopDivSize = 0;
var ss_origianalWindowSize = 0;
function ss_resizeTopDiv(namespace) {
	//This routine is no longer needed in the GWT UI
}

// Functions to save the user status
function ss_updateStatusSoon(obj, evt, maxLength) {
	if ((typeof evt == "undefined" || typeof evt.which == "undefined" || !evt.which) && typeof event == "undefined") return;
	
	ss_statusObj = obj;
	if (ss_statusTimer != null) {
		clearTimeout(ss_statusTimer)
		ss_statusTimer = null;
	}
	// If the string is too long to fit in the database, truncate it
	if (obj.value.length >= maxLength) {
		obj.value = obj.value.substr(0,maxLength-1);
		alert(ss_miniblogTextTooBigErrorMsg);
		return;
	}
	if (ss_isIE6 && obj.value.length > 200) {
		alert(ss_miniblogTextTooBigErrorMsg);
		return;
	}
	
    var charCode = (evt.which) ? evt.which : event.keyCode
    // check for tab or cr; tab or 2 cr's signals the end of the input
    if (charCode == 9) {
    	// ss_updateStatusNow(obj)
    } else if (charCode == 10 || charCode == 13) {
    	if (obj.value.length >= 2 && (obj.value.charCodeAt(obj.value.length - 1) == 10 || 
    			obj.value.charCodeAt(obj.value.length - 1) == 13)) {
    		// Double cr also ends new status
    		// ss_updateStatusNow(obj)
    	} else {
    		ss_setStatusBackground(obj, 'focus');
    	}
    } else {
		ss_setStatusBackground(obj, 'focus');
    }
}
function ss_updateStatusNowAccessible(id) {
	// This is the id of the text box
	var obj = document.getElementById(id);
	ss_updateStatusNow(obj);
}
function ss_updateStatusNowId(id) {
	var obj = document.getElementById(id);
	ss_updateStatusNow(obj);
}
function ss_updateStatusNow(obj) {
	ss_statusObj = obj;
	if (ss_statusTimer != null) {
		clearTimeout(ss_statusTimer)
		ss_statusTimer = null;
	}
	if (obj != null) {
	    if (ss_statusCurrent != escape(obj.value)) {
			ss_statusCurrent = escape(obj.value);
			var status = ss_replaceSubStrAll(obj.value, "\"", "&quot;");
			if (status.length > 255) {
				alert(ss_miniblogTextTooBigErrorMsg);
				return;
			}
			if (ss_isIE6 && status.length > 200) {
				alert(ss_miniblogTextTooBigErrorMsg);
				return;
			}

			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_user_status", status:status}, "");
			var divId = "ss_myStatusDiv";
			var textSrcId = "ss_myStatusTextSrc";
			var textDestId = "ss_myStatusTextDest";
			var textDest2Id = "ss_myStatusTextDest2";
			var titleSrcId = "ss_myStatusTitleSrc";
			var titleDestId = "ss_myStatusTitleDest";
			if (document.getElementById(divId) == null) {
				var divObj = ss_findOwningElement(obj, "div");
				if (divObj != null) divId = divObj.id;
			}
			ss_post_to_url(url, "", ss_postUpdateStatusNow, {divId:divId, textSrcId:textSrcId, textDestId:textDestId, textDest2Id:textDest2Id, titleSrcId:titleSrcId, titleDestId:titleDestId})
		}
		ss_setStatusBackground(obj, 'blur');
	}
}
function ss_postUpdateStatusNow(s, data) {
	var divObj = self.document.getElementById(data.divId);
	if (divObj != null) {
		divObj.innerHTML = s;
	}
	var textSrcObj = self.document.getElementById(data.textSrcId);
	var textDestObj = self.document.getElementById(data.textDestId);
	if (textSrcObj != null && textDestObj != null) {
		textDestObj.innerHTML = textSrcObj.innerHTML;
	}
	var textDest2Obj = self.document.getElementById(data.textDest2Id);
	if (textSrcObj != null && textDest2Obj != null) {
		textDest2Obj.innerHTML = textSrcObj.innerHTML;
	}
	var titleSrcObj = self.document.getElementById(data.titleSrcId);
	var titleDestObj = self.document.getElementById(data.titleDestId);
	if (titleSrcObj != null && titleDestObj != null) {
		titleDestObj.innerHTML = titleSrcObj.innerHTML;
	}
	ss_executeJavascript(divObj)
}

function ss_setStatusBackground(obj, op) {
	ss_statusObj = obj;
	if (op == 'focus') {
		obj.style.backgroundColor = '#ffffff';
		ss_statusOnMouseOver = true;
		if (ss_statusTimer != null) {
			clearTimeout(ss_statusTimer)
			ss_statusTimer = null;
		}
		// if (ss_statusObj != null) ss_statusTimer =
		// setTimeout('ss_updateStatusNow(ss_statusObj);', 10000);
	}
	if (op == 'mouseOver') {
		obj.style.backgroundColor = '#CCFFFF';
		ss_statusOnMouseOver = true;
	}
	if (op == 'blur') {
		obj.style.backgroundColor = '#FFFFFF';
		ss_statusOnMouseOver = false;
	}
}
function ss_flashStatus() {
	ss_statusObj.style.backgroundColor = "#F47400";
	setTimeout("ss_setStatusBackground(ss_statusObj, 'blur');", 300);
}
function ss_setStatusBackgroundCheck(obj) {
	if (ss_statusOnMouseOver && ss_statusTimer == null) ss_setStatusBackground(obj, 'blur');
}
function ss_clearStatus(textareaId) {
	var obj = document.getElementById(textareaId);
	if (obj && typeof obj.value != "undefined") {
		obj.value = "";
		try {obj.focus();} catch(e){}
    	ss_updateStatusNow(obj);
	}
}

function ss_trackThisBinder(id, namespace) {
	ss_setupStatusMessageDiv();
	ss_moveDivToBody("ss_track_this_ok" + namespace);
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"track_this_binder", binderId:id, namespace:namespace}, "__ajax_relevance");
	ss_fetch_url(url, ss_postRequestTrackThis, namespace)
}
function ss_postRequestTrackThis(s, namespace) {
	var divObjA = self.document.getElementById("ss_track_this_anchor_div" + namespace);
	var divObjT = self.document.getElementById("ss_track_this_ok" + namespace);
	if (divObjA != null && divObjT != null) {
		ss_setObjectLeft(divObjT, parseInt(parseInt(ss_getObjectLeft(divObjA)) + 30));
		ss_setObjectTop(divObjT, parseInt(parseInt(ss_getObjectTop(divObjA)) - 30));
		divObjT.innerHTML = s;
		divObjT.style.display = 'block';
		
		divObjT.style.visibility = 'visible';
		setTimeout("ss_hideDivNone('ss_track_this_ok" + namespace + "');", 2000);
	}
}
function ss_trackedItemsDelete(obj, id) {
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"track_this_binder_delete", binderId:id}, "__ajax_relevance");
	var ajaxRequest = new ss_AjaxRequest(url); // Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  // Send the request
	
	// Delete the row from the parent table
	var trObj = obj.parentNode;
	trObj.parentNode.removeChild(trObj)
}

function ss_trackedPeopleDelete(obj, id) {
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"track_this_person_delete", binderId:id}, "__ajax_relevance");
	var ajaxRequest = new ss_AjaxRequest(url); // Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  // Send the request
	
	// Delete the row from the parent table
	var trObj = obj.parentNode;
	trObj.parentNode.removeChild(trObj)
}

function ss_selectRelevanceTab(obj, type, type3, binderId, namespace) {
	// Clear "current" tab
	var currentTab = window["ss_relevanceTabCurrent_"+namespace];
	if (currentTab != null && obj != null) {
		currentTab.parentNode.className = "";
	}
	if (obj != null) {
		window["ss_relevanceTabCurrent_"+namespace] = obj;
		obj.parentNode.className = "ss_tabsCCurrent";
	}
	// Switch to the new tab
	var url = window["ss_relevanceAjaxUrl"+namespace];
	url = ss_replaceSubStr(url, "ss_typePlaceHolder", type);
	url = ss_replaceSubStr(url, "ss_type3PlaceHolder", type3);
	url = ss_replaceSubStr(url, "ss_binderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ss_pagePlaceHolder", "0");
	url = ss_replaceSubStr(url, "ss_rnPlaceHolder", ss_random++);
	if (ss_getUserDisplayStyle() == "accessible") {
		// If in accessible mode, just jump to the url directly
		ss_setSelfLocation(url);
	} else {
		if (type == 'profile' || type == 'overview' || type == 'tasks_and_calendars') {
			// Special case for the profile, overview and tasks_and_calendars
			// tabs; always refresh the whole page
			if ( type == 'profile' )
				url = window["ss_relevanceProfileUrl"+namespace];
			else if ( type == 'overview' )
				url = window["ss_relevanceOverviewUrl"+namespace];
			else
				url = window["ss_relevanceTasksAndCalendarsUrl"+namespace];
				
			url = ss_replaceSubStr(url, "ss_typePlaceHolder", type);
			url = ss_replaceSubStr(url, "ss_type3PlaceHolder", type3);
			url = ss_replaceSubStr(url, "ss_binderIdPlaceHolder", binderId);
			url = ss_replaceSubStr(url, "ss_pagePlaceHolder", "0");
			url = ss_replaceSubStr(url, "ss_rnPlaceHolder", ss_random++);
			ss_setSelfLocation(url);
		} else {
			ss_fetch_url(url, ss_showRelevanceTab, namespace)
		}
	}
}
function ss_showRelevanceTab(s, namespace) {
	var canvasObj = self.document.getElementById("relevanceCanvas_" + namespace);
	if (canvasObj == null) alert('relevance tab misconfigured on page');
	canvasObj.innerHTML = s;
	canvasObj.style.display = 'block'
	canvasObj.style.visibility = 'visible'
	canvasObj.focus();
	ss_executeJavascript(canvasObj); // calendar view is generated in js
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_setFileStatus(entityId, entityType, fileId, statusObjId, fileStatus) {
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"set_file_status", entityId:entityId, entityType:entityType, fileId:fileId, fileStatus:fileStatus});
	ss_post_to_url(url, "", ss_postRequestSetFileStatus, {fileId:fileId, statusObjId:statusObjId});
}
function ss_postRequestSetFileStatus(s, data) {
	var statusObj = self.document.getElementById("fileStatus"+data.statusObjId+"_" + data.fileId);
	if (statusObj != null) {
		statusObj.innerHTML = s;
	}
	ss_hideDiv("ss_fileStatusMenu"+data.statusObjId+"_" + data.fileId);
}

//Searches for <SCRIPT> elements in the given HTML element and
//executes the JavaScript.
//
//Only <SRCIPT> tags without a src="..." are executed.
function ss_executeJavascript(xmlNode, globalScope) {
 var scripts = xmlNode.getElementsByTagName("script");
	ss_executeJavascriptPhase2(scripts, xmlNode, globalScope);	// Phase 2 executes the <SCRIPT> tags without a src="..."
}

//Searches for <SCRIPT> elements in the given HTML element and
//executes the JavaScript.
//
//Executes <SCRIPT src="..."> tags first followed by <SRCIPT> tags
//without a src="...".
function ss_executePhasedJavascript(xmlNode) {
 var scripts = xmlNode.getElementsByTagName("script");
	var executedJSFromSrc = ss_executeJavascriptPhase1(scripts, xmlNode);
 if (!executedJSFromSrc) {
 	ss_executeJavascriptPhase2(scripts, xmlNode, true);	// true -> Always globalScope.
 }
}

//Executes <SCRIPT src="..."> tags first followed by <SRCIPT> tags
//without a src="...".
function ss_executeJavascriptPhase1(scripts, xmlNode) {
	// Scan the <script>'s in xmlNode.
 var executedJSFromSrc = false;
 for (var i = 0; i < scripts.length; i++) {
 	// Is this <script> JavaScript?
     var script = scripts[i];
     if (script.getAttribute("type") == "text/javascript") {
     	// Yes!  Does it have a 'src=...' setting?
     	var jsSrc = script.getAttribute("src");
     	if (jsSrc && (null != jsSrc) && (0 < jsSrc.length)) {
     		// Yes!  Force it's source to actually be loaded
     		// via the <HEAD> tag.
     		var tag = document.createElement("script");
             tag.setAttribute("type", "text/javascript");
     		tag.src = jsSrc;
     		tag.onload = function(){ss_executeJavascriptPhase2(scripts, xmlNode, true);}
     		document.getElementsByTagName("head")[0].appendChild(tag);
     		executedJSFromSrc = true;
     	}
     }
 }

 // Return true if we executed any JavaScript from a src="..." and
 // false otherwise.
 return executedJSFromSrc;
}

//Executes <SCRIPT> tags without a src="...".
function ss_executeJavascriptPhase2(scripts, xmlNode, globalScope) {
	// Scan the <script>'s in xmlNode again.
 for (var i = 0; i < scripts.length; i++) {
 	// Is this <script> JavaScript?
     var script = scripts[i];
     if (script.getAttribute("type") == "text/javascript") {
     	// Yes!  Does it contain anything to execute?
     	var js = script.innerHTML;
     	if ((null != js) && (0 < js.length)) {
     		// Yes!  Are we supposed to evaluate it at a global
     		// scope?
	        	if (globalScope) {
	        		// Yes!  Evaluate it.
		        	if (window.execScript)
		        	     window.execScript(js);	// Global scoped in IE.
		        	else window.eval(js);		// Global scoped everwhere else.
	        	}
	        	else {
	        		// No, we're not supposed to evaluate it at a
	        		// global scope.  Evaluate it at a local scope as
	        		// we've always done.
	        		eval(js);
	        	}
     	}
     }
 }
}

function ss_showFolderPageIndex(hrefUrl, binderId, currentPageIndex, divId, cTag, pTag, yearMonth, endDate) {
	if (currentPageIndex == "") currentPageIndex = "0";
	var page = parseInt(currentPageIndex);
	
	var divObj = self.document.getElementById(divId);
	if (divObj == null || ss_getUserDisplayStyle() == "accessible") {
		// In accessible mode, redraw the whole page
		ss_setSelfLocation(hrefUrl);
	} else {
		ss_setupStatusMessageDiv();
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, operation:"show_folder_page", ssPageStartIndex:page, cTag:cTag, pTag:pTag, yearMonth:yearMonth, endDate:endDate, random:ss_random++});
		ss_fetch_url(url, ss_showFolderPageDiv, divId)
	}
}
function ss_showFolderPageDiv(s, divId) {
	var divObj = self.document.getElementById(divId);
	divObj.innerHTML = s;
	ss_executeJavascript(divObj);
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_showWikiFolderPage(hrefUrl, binderId, currentPageIndex, divId, cTag, pTag, yearMonth, endDate) {
	if (currentPageIndex == "") currentPageIndex = "0";
	var page = parseInt(currentPageIndex);
	
	var divObj = self.document.getElementById(divId);
	if (divObj == null || ss_getUserDisplayStyle() == "accessible") {
		// In accessible mode, redraw the whole page
		ss_setSelfLocation(hrefUrl);
	} else {
		ss_setupStatusMessageDiv();
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, operation:"show_wiki_folder_page", ssPageStartIndex:page, cTag:cTag, pTag:pTag, yearMonth:yearMonth, endDate:endDate, random:ss_random++});
		ss_fetch_url(url, ss_showFolderPageDiv, divId)
	}
}

function ss_showDashboardPage(binderId, type, op, currentPage, direction, divId, namespace) {
	if (currentPage == "") currentPage = "0";
	var page = parseInt(currentPage);
	if (direction == 'next') page = page + 1;
	if (direction == 'previous') page = page - 1;
	
	if (ss_getUserDisplayStyle() == "accessible") {
		// In accessible mode, redraw the whole page
		var url = "";
		eval("url = ss_relevanceAjaxUrl"+namespace);
		url = ss_replaceSubStr(url, "ss_typePlaceHolder", type);
		url = ss_replaceSubStr(url, "ss_type2PlaceHolder", op);
		url = ss_replaceSubStr(url, "ss_binderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ss_pagePlaceHolder", page);
		url = ss_replaceSubStr(url, "ss_rnPlaceHolder", ss_random++);
		ss_setSelfLocation(url);
	} else {
		ss_setupStatusMessageDiv();
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, operation:"get_dashboard_page", operation2:op, pageNumber:page, direction:direction, namespace:namespace}, "__ajax_relevance");
		ss_fetch_url(url, ss_showDashboardPageDiv, divId+namespace)
	}
}
function ss_showDashboardPageDiv(s, divId) {
	var divObj = self.document.getElementById(divId);
	divObj.innerHTML = s;
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	ss_showEmailLinks();
}

function ss_showWhatsNewPage(obj, binderId, type, currentPage, direction, divId, namespace) {
	if (currentPage == "") currentPage = "0";
	var page = parseInt(currentPage);
	if (direction == 'next') page = page + 1;
	if (direction == 'previous') page = page - 1;
	if (ss_getUserDisplayStyle() == "accessible") {
		// In accessible mode, redraw the whole page
		var url = obj.href;
		ss_setSelfLocation(url);
	} else {
		ss_setupStatusMessageDiv();
		ss_random++;
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, operation:"get_whats_new_page", type:type, page:page, namespace:namespace, rn:ss_random}, "__ajax_relevance");
		ss_fetch_url(url, ss_showWhatsNewPageDiv, divId+namespace)
	}
}
function ss_showWhatsNewPageDiv(s, divId) {
	var divObj = self.document.getElementById(divId);
	divObj.innerHTML = s;
	divObj.style.display = 'block';
	divObj.style.visibility = 'visible';
	divObj.focus();
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}
function ss_clearWhatsUnseen(obj, binderId, ids, type, currentPage, direction, divId, namespace) {
	if (currentPage == "") currentPage = "0";
	var page = parseInt(currentPage);
	if (direction == 'next') page = page + 1;
	if (direction == 'previous') page = page - 1;
	if (ss_getUserDisplayStyle() == "accessible") {
		// In accessible mode, redraw the whole page
		var url = obj.href;
		ss_setSelfLocation(url);
	} else {
		ss_setupStatusMessageDiv();
		ss_random++;
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, ids:ids, operation:"clear_unseen", type:type, page:page, namespace:namespace, rn:ss_random}, "__ajax_relevance");
		ss_fetch_url(url, ss_showWhatsNewPageDiv, divId+namespace)
	}
}

// Function to create a named div in the body
function ss_createDivInBody(divId, className) {
	var divObj = document.getElementById(divId);
	if (divObj == null) {
		divObj = document.createElement("div");
		divObj.id = divId;
		divObj.className = className;
		document.getElementsByTagName( "body" ).item(0).appendChild(divObj);
	}
	return divObj;
}

// Routines to show or hide a pop-up hover over div
function ss_showHoverOver(parentObj, divName, event, offsetX, offsetY) {
	if (typeof offsetX == 'undefined') offsetX = 0;
	if (typeof offsetY == 'undefined') offsetY = 0;
	ss_moveDivToBody(divName);
	divObj = document.getElementById(divName)
	if (divObj == null) return;
	divObj.style.zIndex = '500';
	divObj.style.overflow = 'hidden';
	var x = dojo.coords(parentObj, true).x
	var y = dojo.coords(parentObj, true).y
	if (event != null && typeof event != 'undefined') {
		x = event.clientX;
	}
	var topOffset = parseInt(parseInt(y) + dojo.contentBox(parentObj).h)
	topOffset += offsetY;
	ss_setObjectTop(divObj, topOffset + "px")
	ss_setObjectLeft(divObj, parseInt(parseInt(x) + offsetX) + "px")
	divObj.style.visibility = "visible";
	divObj.style.display = "block";
	var h = dojo.coords(divObj, true).h;
	var wh = ss_getWindowHeight();
	var scrollTop = ss_getScrollXY()[1];
	if (wh < topOffset - scrollTop + h) {
		topOffset = parseInt(parseInt(y) - h - offsetY)
		ss_setObjectTop(divObj, topOffset + "px")
	}
}
function ss_hideHoverOver(divName) {
	ss_showHideObj(divName, 'hidden', 'none');
}

// Routines to show or hide an object
function ss_showObjBlock(objName) {
	ss_showHideObj(objName, 'visible', 'block')
}
function ss_showObjInline(objName) {
	ss_showHideObj(objName, 'visible', 'inline')
}
function ss_hideObjInline(objName) {
	ss_showHideObj(objName, 'hidden', 'inline')
}
function ss_hideObjBlock(objName) {
	ss_showHideObj(objName, 'hidden', 'block')
}
function ss_hideObj(objName) {
	ss_showHideObj(objName, 'hidden', 'none')
}
function ss_showHideObj(objName, visibility, displayStyle) {
    var obj = self.document.getElementById(objName)
    if (obj && obj.style) {
	    if (obj.style.visibility != visibility) {
		    obj.style.visibility = visibility;
		    obj.style.display = displayStyle;
		    if (displayStyle == "block" || displayStyle == "inline") {
		    	try{obj.focus()} catch(e){}
		    }
		}
		// Signal that the layout changed
		if (!obj.style.position || obj.style.position != "absolute") {
			ssf_onLayoutChange();
			// ss_debug("ss_showHideObj: " + objName + " = " + visibility)
		}
	} else {
		// ss_debug('Div "'+objName+'" does not exist. (ss_showHideObj)')
	}
}
function ss_toggleShowDiv(divName, namespace) {
	if (typeof namespace == 'undefined') namespace = "";
    var obj = self.document.getElementById(divName);
    if (obj && obj.style) {
	    if (!obj.style.display || obj.style.display != "none") {
		    obj.style.display = "none";
		} else {
			obj.style.visibility = "visible";
			obj.style.display = "block";
			obj.focus();
		}
		// Signal that the layout changed
		if (ssf_onLayoutChange) setTimeout("ssf_onLayoutChange();", 100);
		try {
			if (self != self.parent && parent.ss_positionEntryDiv) setTimeout("parent.ss_positionEntryDiv();", 100);
		} catch(e) {}
	} else {
		// ss_debug('Div "'+objName+'" does not exist. (ss_showHideObj)')
	}
	ssf_onLayoutChange();
}

// Routine to show and hide file action menus
function ss_showHideMenuDiv(divId) {
	var divObj = document.getElementById(divId);
	if (divObj != null && divObj.style.display == "block") {
		ss_HideDivIfActivated(divId);
	} else {
		ss_showDivActivate(divId);
		var divWidth = ss_getObjectWidth(divObj);
        // need to bump layer an extra bit to the right to avoid horiz scrollbar
        divWidth = parseInt(divWidth) + 35;
		var x = ss_getObjectLeftAbs(divObj);
		if (ss_isNSN6 || ss_isMoz5) {
	        maxWidth = parseInt(window.innerWidth);
	    } else {
	        maxWidth = parseInt(document.body.scrollWidth);
	    }
	    if (x + divWidth > maxWidth) {
	        x = maxWidth - divWidth;
	        ss_setObjectLeft(divObj, x);
	    }
	}
}

// Routine to hide or show a region using a collapse/expand button
function ss_toggleRegionInit(divId, imgId, ss_toggleRegionSize, initialRegionClass) {
	var divHeight = ss_getDivHeight(divId);
	if (parseInt(divHeight) >= ss_toggleRegionSize) {
		var imgObj = self.document.getElementById(imgId);
		if (imgObj != null) imgObj.style.display = "block";
		var divObj = self.document.getElementById(divId);
		divObj.className = initialRegionClass;
	}
}
function ss_toggleRegion(aObj, divId, regionId, baseClassName, ss_toggleRegionSize) {
	var urlParams = {operation:"save_region_view",id:regionId};
	var divObj = self.document.getElementById(divId);
	var buttonSrc = aObj.firstChild.src;
	var reExpand = /expand([^\/]*\.png)/
	var reCollapse = /collapse([^\/]*\.png)/
	if (buttonSrc.search(reExpand) >= 0) {
		divObj.className = baseClassName;
		aObj.firstChild.src = buttonSrc.replace(reExpand, "collapse$1")
		urlParams.state = "expanded";
	} else {
		var divHeight = ss_getDivHeight(divId);
		if (parseInt(divHeight) >= ss_toggleRegionSize) divObj.className = baseClassName+"-clipped";
		aObj.firstChild.src = buttonSrc.replace(reCollapse, "expand$1")
		urlParams.state = "collapsed";
	}
	// Remember this setting
	ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams));

	// Signal that the layout changed
	if (ssf_onLayoutChange) setTimeout("ssf_onLayoutChange();", 100);
}

// Routines to handle tabs
var ss_currentTabShowing = new Array();
var ss_currentHoverOverTab = new Array();
function ss_initTab(tabName, id) {
	ss_currentTabShowing["tab"+id] = tabName;
	ss_currentHoverOverTab["tab"+id] = null;
	ss_showTab(tabName, id);
}
function ss_showTab(tabName, id) {
	if (ss_currentTabShowing["tab"+id] != null) {
		var divObj = self.document.getElementById(ss_currentTabShowing["tab"+id] + "Div")
		divObj.style.display = "none";
		var tabObj = self.document.getElementById(ss_currentTabShowing["tab"+id] + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		ss_currentTabShowing["tab"+id] = null;
	}
	ss_currentTabShowing["tab"+id] = tabName;
	var divObj = self.document.getElementById(ss_currentTabShowing["tab"+id] + "Div");
	var tabObj = self.document.getElementById(ss_currentTabShowing["tab"+id] + "Tab");
	divObj.style.display = "block";
	tabObj.className = "wg-tab roundcornerSM on";
	
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_hoverOverTab(tabName, id) {
	if (ss_currentTabShowing["tab"+id] != null) {
		var tabObj = self.document.getElementById(ss_currentTabShowing["tab"+id] + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
	if (ss_currentHoverOverTab["tab"+id] != null && 
			ss_currentHoverOverTab["tab"+id] != ss_currentTabShowing["tab"+id]) {
		var tabObj = self.document.getElementById(ss_currentHoverOverTab["tab"+id] + "Tab");
		tabObj.className = "wg-tab roundcornerSM";
		ss_currentHoverOverTab["tab"+id] = null;
	}
	ss_currentHoverOverTab["tab"+id] = tabName;
	var tabObj = self.document.getElementById(ss_currentHoverOverTab["tab"+id] + "Tab");
	if (ss_currentHoverOverTab["tab"+id] == ss_currentTabShowing["tab"+id]) {
		tabObj.className = "wg-tab roundcornerSM selected-menu on";
	} else {
		tabObj.className = "wg-tab roundcornerSM selected-menu";
	}
}

function ss_hoverOverTabStopped(tabName, id) {
	if (ss_currentHoverOverTab["tab"+id] != null) {
		var tabObj = self.document.getElementById(ss_currentHoverOverTab["tab"+id] + "Tab");
		if (ss_currentHoverOverTab["tab"+id] == ss_currentTabShowing["tab"+id]) {
			tabObj.className = "wg-tab roundcornerSM on";
		} else {
			tabObj.className = "wg-tab roundcornerSM";
		}
		ss_currentHoverOverTab["tab"+id] = null;
	}
	if (ss_currentTabShowing["tab"+id] != null) {
		var tabObj = self.document.getElementById(ss_currentTabShowing["tab"+id] + "Tab");
		tabObj.className = "wg-tab roundcornerSM on";
	}
}


function ss_checkIfParentDivHidden(divId) {
    var obj = self.document.getElementById(divId)
    if (obj != null) {
    	while (obj.parentNode != null) {
    		obj = obj.parentNode;
    		if (obj.tagName && obj.tagName.toLowerCase() == "div") {
    			if (obj.style && obj.style.visibility && obj.style.visibility == "hidden") {
    				return true;
    			}
    		}
    	}
    }
    return false;
}

function ss_showHide(objId){
	var obj = dojo.byId(objId);
	if (obj && obj.style) {
	    if (obj.style.display == 'none' || 
	    		obj.style.visibility == 'hidden' || 
	    		obj.style.display == '') {
			obj.style.visibility="visible";
			obj.style.display="block";
			if (ss_checkIfVisible(obj)) obj.focus();
			
			// Signal that the layout changed
			if (!obj.style.position || obj.style.position != "absolute") {
				ssf_onLayoutChange();
			}
			return true;
		} else {	
			obj.style.visibility="hidden";
			obj.style.display="none";
			
			// Signal that the layout changed
			if (!obj.style.position || obj.style.position != "absolute") {
				ssf_onLayoutChange();
			}
			return false;
		} 
	}
	return false;
}

function ss_showHideTaskList(divId, imgObj, sticky, id) {
	var urlParams = {id:id};
	if (ss_showHide(divId)) {
		urlParams.operation="show_sidebar_panel";
	} else {
		urlParams.operation = "hide_sidebar_panel";
	}		
	ss_toggleImage(imgObj, "flip_up16H.gif", "flip_down16H.gif");
	if (sticky) {
		ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams));
	}
}

function ss_showHideSidebarBox(divId, divObj, sticky, id) {
	var urlParams = {id:id};
	if (ss_showHide(divId)) {
		urlParams.operation="show_sidebar_panel";
	} else {
		urlParams.operation = "hide_sidebar_panel";
	}		
	if (divObj.className == "ss_menuOpen") {
		divObj.className = "ss_menuClosed"
	} else {
		divObj.className = "ss_menuOpen"
	}
	if (sticky) {
		ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams));
	}
}


function ss_toggleImage(iconId, img1, img2) {
	var img = iconId;
	if (typeof iconId == "string")
		img = $(iconId);
	if (!img) return;
	if (img && (img.src.indexOf(img1) > -1)) {
		img.src = ss_imagesPath + "pics/" + img2;
	} else if (img && (img.src.indexOf(img1) == -1)) {
		img.src = ss_imagesPath + "pics/" + img1;
	}	
}
function ss_showHideBusinessCard(op, scope) {
	var urlParams = {scope:scope};
	if (op == "show") {
		ss_hideDivNone("ss_smallBusinessCard");
		ss_showDiv("ss_largeBusinessCard");
		urlParams.operation="show_business_card";
	} else {
		ss_hideDivNone("ss_largeBusinessCard");
		ss_showDiv("ss_smallBusinessCard");
		urlParams.operation="hide_business_card";
	}
	ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams));
}

// Routine to set the opacity of a div
// (Note: this may not work if "width" is not explicitly set on the div)
function ss_setOpacity(obj, opacity) {
	dojo.style(obj, "opacity", opacity);
}

// Routine to fade in a div
function ss_showDivFadeIn(id, ms) {
	if (ss_divFadeInArray[id] == null || ss_divFadeInArray[id] < 0) {
		ss_divFadeInArray[id] = 0;
	}
	ss_divFadeInArray[id]++;
    // Is this already being shown? If yes, return.
	if (ss_divFadeInArray[id] > 1) return;
    if (!ms || ms == undefined) ms = 300;
    if (document.getElementById(id).style.visibility == 'hidden') {
    	ss_setOpacity(document.getElementById(id),0.1);
    	ss_showDiv(id);
    }
    dojo.fadeIn({node:id, delay:ms}).play();
}

// Routine to fade out a div
function ss_hideDivFadeOut(id, ms) {
	if (ss_divFadeInArray[id] == null || ss_divFadeInArray[id] < 1) {
		ss_divFadeInArray[id] = 1;
	}
	ss_divFadeInArray[id]--;
    // Is this still being shown? If yes, return.
	if (ss_divFadeInArray[id] > 1) return;
    if (!ms || ms == undefined) ms = 300;
    dojo.fadeOut({node:id, delay:ms, onEnd:function(){
    	ss_hideDiv(id);
    	return true;
    }}).play();
}

// Routine to add the innerHMTL of one div to another div
function ss_addToDiv(target, source) {
    var objTarget = self.document.getElementById(target)
    var objSource = self.document.getElementById(source)
    var targetHtml = ss_getDivHtml(target)
    var sourceHtml = ss_getDivHtml(source)
    ss_setDivHtml(target, targetHtml + sourceHtml)

	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

// Routine to add html to a div
function ss_addHtmlToDiv(target, text) {
    var objTarget = self.document.getElementById(target)
    var targetHtml = ss_getDivHtml(target)
    ss_setDivHtml(target, targetHtml + text)

	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}
function ss_addHtmlToDivFront(target, text) {
    var objTarget = self.document.getElementById(target)
    var targetHtml = ss_getDivHtml(target)
    ss_setDivHtml(target, text + targetHtml)

	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

// Routines to get and set the html of an area
function ss_getDivHtml(divId) {
    var obj = self.document.getElementById(divId)
    var value = "";
    if (obj) {
    	value = obj.innerHTML;
    }
    return value;
}

function ss_setDivHtml(divId, value) {
    var obj = self.document.getElementById(divId)
    if (obj) {
    	obj.innerHTML = value
    }

	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

// Routines for the definition builder
function ss_setDeclaredDiv(id) {
	for (var i = 0; i < ss_declaredDivs.length; i++) {
		if (ss_declaredDivs[i] == id) return;
	}
	var next = ss_declaredDivs.length;
	ss_declaredDivs[next] = id;
}

function ss_hideAllDeclaredDivs() {
	for (var i = 0; i < ss_declaredDivs.length; i++) {
		ss_showHideObj(ss_declaredDivs[i], 'hidden', 'none')
	}
}

// Routine to get the form object of the containing form
function ss_getContainingForm(obj) {
	var formObj = obj;
	while (formObj.nodeName.toLowerCase() != "body") {
		if (formObj.nodeName.toLowerCase() == "form") break;
		formObj = formObj.parentNode;
	}
	return formObj;
}

// Routines to show and hide the hover over info
function ss_showTreeHover(obj, id) {
	var divObj = document.getElementById("info_div_" + id);
	if (divObj != null) {
		ss_showHoverOver(obj, "info_div_" + id, null, 100, 4)
	}
}
function ss_hideTreeHover(obj, id) {
	var divObj = document.getElementById("info_div_" + id);
	if (divObj != null) {
		ss_hideHoverOver("info_div_" + id)
	}
}

// Routine to create a new "ss_eventObj" object
// ss_eventObj objects are set up whenever you want to call a routine on an
// event.
// event_name is the event name (e.g., "MOUSEDOWN")
function ss_createEventObj(function_name, event_name, function_def) {
    var fn = -1;
    for (var i = 0; i < ss_eventList.length; i++) {
    	if (ss_eventList[i].functionName == function_name) {
    		fn = i;
    		break;
    	}
    }
    if (fn == -1) {
        fn = ss_eventList.length;
        ss_eventList[fn] = new ss_eventObj(function_name);
        ss_eventList[fn].setEventName(event_name);
        ss_eventList[fn].setFunctionDef(function_def);
    }
    for (var i = 0; i < ss_eventTypeList.length; i++) {
    	if (ss_eventTypeList[i] == event_name) return;
    }
    next = ss_eventTypeList.length;
    ss_eventTypeList[next] = event_name;
    
    // Enable the event
    if (ss_isNSN) {
        // eval("self.document.captureEvents(Event."+event_name+")")
    }
    if (ss_eventList[fn].eventName.toLowerCase() == "unload") {
    	// Add the unload event
    	if (typeof window.onunload == 'undefined' || window.onunload == '') {
    		window.onunload = ssf_event_handler;
    	} else {
    		dojo.addOnWindowUnload(ssf_event_handler);
    	}
    } else {
    	eval("self.document.on"+ss_eventList[fn].eventName.toLowerCase()+" = ssf_event_handler;")
    }
}
function ss_eventObj(function_name) {
    this.functionName = function_name;
    this.eventName = null;
    this.functionDef = null;
    this.setEventName = m_setEventName;
    this.setFunctionDef = m_setFunctionDef;
    this.callEventFunction = this.functionName;
}
function m_setEventName(event_name) {
    this.eventName = event_name.toLowerCase();
}
function m_setFunctionDef(function_def) {
    this.functionDef = function_def;
}

// Common event handler
// This function will call the desired routines on an event
function ssf_event_handler(e) {
    if (!ss_isNSN) {e = event}
    for (var i = 0; i < ss_eventList.length; i++) {
        if (e.type.toLowerCase() == ss_eventList[i].eventName) {
        	if (ss_eventList[i].functionDef != null && ss_eventList[i].functionDef != "undefined") {
        		ss_eventList[i].functionDef(e);
        	} else {
            	eval(ss_eventList[i].functionName+'(e)');
            }
        }
    }
}


// Routine to create a new "onLoadObj" object
// onLoadObj objects are set up whenever you want to call something at onLoad
// time.
function ss_createOnLoadObj(name, initName) {
    for (var i = 0; i < ss_onLoadList.length; i++) {
    	if (ss_onLoadList[i].name == name) return;
    }
    var next = ss_onLoadList.length;
    ss_onLoadList[next] = new onLoadObj(name);
    ss_onLoadList[next].setInitRoutine(initName);
}
function onLoadObj(name) {
    this.name = name;
    this.initRoutine = null;
    this.getInitRoutine = m_getInitRoutine;
    this.setInitRoutine = m_setInitRoutine;
}
function m_getInitRoutine() {
    return this.initRoutine;
}
function m_setInitRoutine(initRoutine) {
    this.initRoutine = initRoutine;
}

// Routine to create a new "onSubmitObj" object
// onSubmitObj objects are set up whenever you want to call something at form
// submit time.
function ss_createOnSubmitObj(name, formName, submitRoutine) {
    for (var i = 0; i < ss_onSubmitList.length; i++) {
    	if (ss_onSubmitList[i].name == name) return;
    }
    var next = ss_onSubmitList.length;
    ss_onSubmitList[next] = new onSubmitObj(name, formName);
    ss_onSubmitList[next].setSubmitRoutine(submitRoutine);
}
function onSubmitObj(name, formName) {
    this.name = name;
    this.formName = formName;
    this.submitRoutine = null;
    this.getSubmitRoutine = m_getSubmitRoutine;
    this.setSubmitRoutine = m_setSubmitRoutine;
}
function m_getSubmitRoutine() {
    return this.submitRoutine;
}
function m_setSubmitRoutine(submitRoutine) {
    this.submitRoutine = submitRoutine;
}

// Common onSubmit handler
// This function will call the desired routines at form submit time
// unless the onClick for the submit button called ss_selectButton('cancelBtn').
// If any routine returns "false", then this routine returns false.
function ss_onSubmit(obj, checkIfButtonClicked) {
	if (typeof checkIfButtonClicked == "undefined") {
		checkIfButtonClicked = false;
	}
    // Take this opportunity to fill in the action if blank. Some browsers want
	// this to be non-blank.
    if (typeof obj.action != 'undefined' && obj.action == '') obj.action = self.location.href;
    
    if (ss_buttonSelected == "" && checkIfButtonClicked) {
    	// This must be IE. Don't let them submit using the Enter key since it
		// doesn't submit the whole form that way.
    	alert(ss_clickOkToSubmit);
    	return false;
    }
    
	// If there's a GWT UI ss_dialogClosed() function defined, call it.
    if      (self.opener && (typeof self.opener.ss_dialogClosed != "undefined")) self.opener.ss_dialogClosed();	// Handles dialog in a popup window.
    else if (                typeof window.top.ss_dialogClosed  != "undefined")  window.top.ss_dialogClosed();	// Handles dialog being inline.
    
    if (ss_buttonSelected == "cancelBtn" || ss_buttonSelected == "closeBtn") {
		if (self != self.parent) {
			if (self.window.name == "ss_showpopupframe") {
				// This is in the popup iframe
				if (self.parent.ss_hidePopupDiv) {
					self.parent.ss_hidePopupDiv();
					if (typeof self.ss_setEntryDivHeight != 'undefined') ss_setEntryDivHeight();
					return false;
				}
			} else if (self.window.name == "gwtContentIframe") {
				// This is in the main content iframe
				if (window.top.ss_getUrlFromContentHistory) {
					var url = window.top.ss_getUrlFromContentHistory(-1);
					if (url && (0 < url.length)) {
						window.top.ss_gotoContentUrl(url);
						return false;
					}
				}
				self.history.back();
				return false;
			}
		}
    	return true;
    }
    var result = true;
    for (var i = 0; i < ss_onSubmitList.length; i++) {
        if (ss_onSubmitList[i].formName == obj.name) {
            if (!ss_onSubmitList[i].submitRoutine(obj)) {result = false;}
        }
    }
    if (!ss_validate(obj)) result = false;
    // After all of the other checks are done, and if the result is still true,
    // check if the required fields are filled in.
    // Do this last in case some fields get filled in by the other routines
    if (result && !ss_checkForRequiredFields(obj)) result = false;
    if (result) {
    	var els = obj.getElementsByTagName("input");
    	var elsLen = els.length;
     	for (i = 0, j = 0; i < elsLen; i++) {
    		if (els[i].type == "submit" && els[i].name == ss_buttonSelected) {
    			ss_startSpinner(els[i]);
    		}
    	}
    	
    }
    return result;
}

var ss_buttonSelected = "";
	
function ss_buttonSelect(btn) {
	ss_buttonSelected = btn
}
	

// Routine to create a new "onResizeObj" object
// onResizeObj objects are set up whenever you want to call something at
// onResize time.
function ss_createOnResizeObj(name, resizeName) {
    for (var i = 0; i < ss_onResizeList.length; i++) {
    	if (ss_onResizeList[i].name == name) return;
    }
    var next = ss_onResizeList.length;
    ss_onResizeList[next] = new onResizeObj(name);
    ss_onResizeList[next].setResizeRoutine(resizeName);
}
function onResizeObj(name) {
    this.name = name;
    this.resizeRoutine = null;
    this.getResizeRoutine = m_getResizeRoutine;
    this.setResizeRoutine = m_setResizeRoutine;
}
function m_getResizeRoutine() {
    return this.resizeRoutine;
}
function m_setResizeRoutine(resizeRoutine) {
    this.resizeRoutine = resizeRoutine;
}
function ssf_onresize_event_handler() {
    // Call any routines that want to be called at resize time
    for (var i = 0; i < ss_onResizeList.length; i++) {
        if (ss_onResizeList[i].resizeRoutine) {
        	ss_onResizeList[i].resizeRoutine();
        	// ss_debug("Resize event: " + ss_onResizeList[i].name + "\n");
        }
    }
    if (ss_savedOnResizeRoutine != null) {
    	window.onresize = ss_savedOnResizeRoutine;
    	if (window.onresize != null) window.onresize();
		window.onresize = ssf_onresize_event_handler;
    }
}

// Routine to create a new "onLayoutChangeObj" object
// onLayoutChangeObj objects are set up whenever you want to be called if the
// layout changes dynamically.
function ss_createOnLayoutChangeObj(name, layoutRoutine) {
    for (var i = 0; i < ss_onLayoutChangeList.length; i++) {
    	if (ss_onLayoutChangeList[i].name == name) return;
    }
    var next = ss_onLayoutChangeList.length;
    ss_onLayoutChangeList[next] = new onLayoutChangeObj(name);
    ss_onLayoutChangeList[next].setLayoutRoutine(layoutRoutine);
}
function onLayoutChangeObj(name) {
    this.name = name;
    this.layoutRoutine = null;
    this.getLayoutRoutine = m_getLayoutRoutine;
    this.setLayoutRoutine = m_setLayoutRoutine;
}
function m_getLayoutRoutine() {
    return this.layoutRoutine;
}
function m_setLayoutRoutine(layoutRoutine) {
    this.layoutRoutine = layoutRoutine;
}

// Common onLayoutChange handler
// This function will call the layout routines if the layout changes
function ssf_onLayoutChange(obj) {
    for (var i = 0; i < ss_onLayoutChangeList.length; i++) {
        if (ss_onLayoutChangeList[i].layoutRoutine) {
        	//ss_debug("ssf_onLayoutChange executing routine: " + ss_onLayoutChangeList[i].name)
        	ss_onLayoutChangeList[i].layoutRoutine();
        }
    }
    return true;
}

function ss_validateEntryTextFieldLength(sTitle) {
	var titleLength = 0;
	var count = 0;
	for (count = 0; count < sTitle.length; count++) {
		var s = sTitle.charAt(count);
		titleLength++;
		if (s.match("[\u0100-\uffff]") != null) titleLength++;
		if (titleLength > 255) break;
	}
	if (count < sTitle.length) return sTitle.substring(0, count);
	return sTitle;
}

function ss_getObjAbsX(obj) {
    return dojo.coords(obj, true).x;
    var x = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        x += parentObj.offsetParent.offsetLeft
        parentObj = parentObj.offsetParent
    }
    return x
}

function ss_getObjAbsY(obj) {
    return dojo.coords(obj, true).y;
    var y = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        y += parentObj.offsetParent.offsetTop
        parentObj = parentObj.offsetParent
    }
    return y
}

function ss_getDivTop(selector) {
	if (selector && selector.nodeType) {
		var obj = selector;
	} else {
		var obj = self.document.getElementById(selector);
	}
    if (!obj) return 0;
    return dojo.coords(obj, true).y;

    var top = 0;
    var obj = self.document.getElementById(selector)
    while (1) {
        if (!obj) {break}
        top += parseInt(obj.offsetTop)
        if (obj == obj.offsetParent) {break}
        obj = obj.offsetParent
    }
    return parseInt(top);
}

function ss_getDivLeft(selector) {
	if (selector && selector.nodeType) {
		var obj = selector;
	} else {
		var obj = self.document.getElementById(selector);
	}
    if (!obj) return 0;
    return dojo.coords(obj, true).x;
    
    var left = 0;
    if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
        var obj = self.document.getElementById(selector)
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else {
        var obj = self.document.all[selector]
        while (1) {
            if (!obj) {break}
            left += obj.offsetLeft
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(left);
}


function ss_getDivScrollTop(divName) {
    var obj = self.document.getElementById(divName)
    if (!obj) return 0;
    return dojo.coords(obj, true).y;
    // return parseInt(obj.scrollTop);
}

function ss_getDivScrollLeft(divName) {
    var obj = self.document.getElementById(divName)
    if (!obj) return 0;
    return dojo.coords(obj, true).x;
    // return parseInt(obj.scrollLeft);
}

function ss_getDivHeight(divName) {
    var obj = self.document.getElementById(divName)
    if (!obj) return 0;
    return parseInt(dojo.contentBox(obj).h);
    // return parseInt(obj.offsetHeight);
}

function ss_getDivWidth(divName) {
    var obj = self.document.getElementById(divName)
    if (!obj) return 0;
    return parseInt(dojo.contentBox(obj).w);
    // return parseInt(obj.offsetWidth);
}

function ss_getImageTop(imageName) {
    var top = 0;
    if (ss_isNSN6 || ss_isMoz5) {
        var obj = document.images[imageName]
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (ss_isNSN) {
        top = document.images[imageName].y
    } else {
        var obj = document.all[imageName]
        while (1) {
            if (!obj) {break}
            top += obj.offsetTop
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(top);
}

function ss_getImageLeft(imageName) {
    var left = 0;
    if (ss_isNSN6 || ss_isMoz5) {
        var obj = document.images[imageName]
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (ss_isNSN) {
        left = document.images[imageName].x
    } else {
        var obj = document.all[imageName]
        while (1) {
            if (!obj ) {break}
            left += obj.offsetLeft
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(left);
}

function ss_getObjectWidth(obj) {
    return dojo.contentBox(obj).w
}

function ss_getObjectHeight(obj) {
	return dojo.contentBox(obj).h
}

function ss_getObjectLeft(obj) {
    return dojo.coords(obj, true).x;
}

function ss_getObjectTop(obj) {
    return dojo.coords(obj, true).y;
}

function ss_getObjectLeftAbs(obj) {
	return dojo.coords(obj, true).x
}

function ss_getObjectTopAbs(obj) {
    return dojo.coords(obj, true).y
}

function ss_setObjectWidth(obj, width) {
	var oldWidth = 0;
	if (obj && parseInt(width) > 0) {
		oldWidth = obj.style.width;
		obj.style.width = parseInt(width) + 'px';
	}

    // Call the routines that want to be called on layout changes
    if (obj && obj.style && parseInt(oldWidth) != parseInt(width) &&
    		(!obj.style.position || obj.style.position != "absolute")) ssf_onLayoutChange();
}

function ss_setObjectHeight(obj, height) {
	//ss_debug("**** "+ss_debugTrace());
	if (obj == null) return;
	//ss_debug("   ss_setObjectHeight, "+obj.id+" height: "+height)
	var oldHeight = obj.style.height;
    if (obj && parseInt(height) > 0) obj.style.height = parseInt(height) + 'px';
    
    // Call the routines that want to be called on layout changes
    if (obj && obj.style && parseInt(oldHeight) != parseInt(height) && 
    		(!obj.style.position || obj.style.position != "absolute")) ssf_onLayoutChange();
}

function ss_setObjectLeft(obj, value) {
	var oldLeft = obj.style.left;
    obj.style.left = parseInt(value) + "px";
    // Call the routines that want to be called on layout changes
    if (parseInt(oldLeft) != parseInt(value) &&
    		(!obj.style.position || obj.style.position != "absolute")) ssf_onLayoutChange();
}

function ss_setObjectTop(obj, value) {
	var oldTop = obj.style.top;
    obj.style.top = parseInt(value) + "px";
    // Call the routines that want to be called on layout changes
    if (parseInt(oldTop) != parseInt(value) && (
    		!obj.style.position || obj.style.position != "absolute")) ssf_onLayoutChange();
}

function ss_getWindowWidth(windowObj) {
	if (typeof windowObj == "undefined") windowObj = self.window;
	if( typeof( windowObj.innerWidth ) == 'number' ) {
		// Non-IE
		myWidth = windowObj.innerWidth;
		myHeight = windowObj.innerHeight;
	} else if( windowObj.document.documentElement &&
		( windowObj.document.documentElement.clientWidth || windowObj.document.documentElement.clientHeight ) ) {
		// IE 6+ in 'standards compliant mode'
		myWidth = windowObj.document.documentElement.clientWidth;
		myHeight = windowObj.document.documentElement.clientHeight;
	} else if( windowObj.document.body && ( windowObj.document.body.clientWidth || windowObj.document.body.clientHeight ) ) {
		// IE 4 compatible
		myWidth = windowObj.document.body.clientWidth;
		myHeight = windowObj.document.body.clientHeight;
	} else {
		var winW = 630, winH = 460;
		if (parseInt(navigator.appVersion)>3) {
			 if (navigator.appName=="Netscape") {
				 winW = windowObj.innerWidth;
				 winH = windowObj.innerHeight;
			 }
			 if (navigator.appName.indexOf("Microsoft")!=-1) {
				 winW = windowObj.document.body.offsetWidth;
				 winH = windowObj.document.body.offsetHeight;
			 }
		}
		myWidth = winW;
	}
	return myWidth;
}

function ss_getWindowHeight(windowObj) {
	if (typeof windowObj == "undefined") windowObj = self.window;
	if( typeof( windowObj.innerWidth ) == 'number' ) {
		// Non-IE
		myWidth = windowObj.innerWidth;
		myHeight = windowObj.innerHeight;
	} else if( windowObj.document.documentElement &&
		( windowObj.document.documentElement.clientWidth || windowObj.document.documentElement.clientHeight ) ) {
		// IE 6+ in 'standards compliant mode'
		myWidth = windowObj.document.documentElement.clientWidth;
		myHeight = windowObj.document.documentElement.clientHeight;
	} else if( windowObj.document.body && ( windowObj.document.body.clientWidth || windowObj.document.body.clientHeight ) ) {
		// IE 4 compatible
		myWidth = windowObj.document.body.clientWidth;
		myHeight = windowObj.document.body.clientHeight;
	}
	return myHeight;
}

function ss_getBodyHeightWidth(windowObj) {
	if (typeof windowObj == "undefined") windowObj = self;
	var screenWidth, screenHeight;
	if (windowObj.innerHeight) // all except Explorer
	{
		screenWidth = windowObj.innerWidth;
		screenHeight = windowObj.innerHeight;
	}
	else if (windowObj.document.documentElement && windowObj.document.documentElement.clientHeight)
		// Explorer 6 Strict Mode
	{
		screenWidth = windowObj.document.documentElement.clientWidth;
		screenHeight = windowObj.document.documentElement.clientHeight;
	}
	else if (windowObj.document.body) // other Explorers
	{
		screenWidth = windowObj.document.body.clientWidth;
		screenHeight = windowObj.document.body.clientHeight;
	}
	return [screenWidth, screenHeight];
}

function ss_getBodyHeight() {
    var h;
    if (window.innerHeight && window.scrollMaxY) {	
		h = window.innerHeight + window.scrollMaxY;
	} else {
		//h = ss_getBodyHeightWidth()[1];
		h = self.document.body.scrollHeight;
	}
    if (ss_getWindowHeight() > h) {
        h = ss_getWindowHeight();
    }
    return h;
}

function ss_getBodyWidth() {
    var w = self.document.body.scrollWidth;
    if (ss_getWindowWidth() > w) {
        w = ss_getWindowWidth();
    }
    return w
}

function ss_getScrollXY() {
	var scrOfX = 0, scrOfY = 0;
	if( typeof( window.pageYOffset ) == 'number' ) {
		// Netscape compliant
		scrOfY = window.pageYOffset;
		scrOfX = window.pageXOffset;
	} else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
		// DOM compliant
		scrOfY = document.body.scrollTop;
		scrOfX = document.body.scrollLeft;
	} else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
		// IE6 standards compliant mode
		scrOfY = document.documentElement.scrollTop;
		scrOfX = document.documentElement.scrollLeft;
	}
	return [ scrOfX, scrOfY ];
}
function smoothScroll(x, y) {
	smoothScrollInTime(x,y,10)
}

function smoothScrollInTime(x, y, steps) {
    if (steps <= 1) {
		window.scroll(x,y)
    } else {
	    var bodyX = ss_getScrollXY()[0]
	    var bodyY = ss_getScrollXY()[1]
	    if (bodyX < x) {
	    	var newX = parseInt(bodyX + ((x - bodyX) / steps))
	    } else {
	    	var newX = parseInt(bodyX - ((bodyX - x) / steps))
	    }
	    if (bodyY < y) {
	    	var newY = parseInt(bodyY + ((y - bodyY) / steps))
	    } else {
	    	var newY = parseInt(bodyY - ((bodyY - y) / steps))
	    }
		window.scroll(newX, newY)
		steps = steps - 1
		eval("setTimeout('smoothScrollInTime("+x+", "+y+", "+steps+")', 8)")
    }
}

function ss_getElementsByClass(classPattern, node, tag) {
	var classElements = new Array();
	if (node == null)
		node = document;
	if (tag == null)
		tag = '*';
	var els = node.getElementsByTagName(tag);
	var elsLen = els.length;
	var pattern = new RegExp("(^|\\s)"+classPattern+"(\\s|$)");
	for (i = 0, j = 0; i < elsLen; i++) {
		if (pattern.test(els[i].className) ) {
			classElements[j] = els[i];
			j++;
		}
	}
	return classElements;
}

function ss_showAccessibleMenu(divId) {
	var divObj = document.getElementById(divId);
	ss_showDiv(divId);
}
function ss_putValueInto(objId, value) {
	document.getElementById(objId).value = value;
}


function ss_hideAccessibleMenu(divId) {
	var divObj = document.getElementById(divId);
	ss_hideDivNone(divId);
}

// Pop-up menu support
// clicking anywhere will hide the div

// Create a clone of the menu before showing it; attach it to the "body" outside
// of any div
// This makes sure that the z-index will be on top of everything else (IE fix)
function ss_activateMenuLayerClone(divId, parentDivId, offsetLeft, offsetTop, openStyle) {
	if (!parentDivId || parentDivId == null || parentDivId == 'undefined') {parentDivId=""}
	if (!offsetLeft || offsetLeft == null || offsetLeft == 'undefined') {offsetLeft=ss_activateMenuOffsetLeft}
	if (!offsetTop || offsetTop == null || offsetTop == 'undefined') {offsetTop=ss_activateMenuOffsetTop}
	if (!openStyle || openStyle == null || openStyle == 'undefined') {openStyle=""}
	var divObj = document.getElementById(divId);
	if (!ss_menuDivClones[divId]) {
		ss_menuDivClones[divId] = divId;
		var tempNode = divObj.cloneNode( true );
		tempNode.id = divId;
		tempNode.style.zIndex = ssMenuZ;
		divObj.parentNode.removeChild(divObj)
		document.getElementsByTagName( "body" ).item(0).appendChild( tempNode );
		divObj = document.getElementById(divId);
	}
	ss_activateMenuLayer(divId, parentDivId, offsetLeft, offsetTop, openStyle);
}
function ss_activateMenuLayerMove(divId, parentDivId, offsetLeft, offsetTop, openStyle) {
    ss_moveDivToBody(divId);
    ss_activateMenuLayer(divId, parentDivId, offsetLeft, offsetTop, openStyle)
}
function ss_activateMenuLayer(divId, parentDivId, offsetLeft, offsetTop, openStyle) {
	if (!parentDivId || parentDivId == null || parentDivId == 'undefined') {parentDivId=""}
	if (!offsetLeft || offsetLeft == null || offsetLeft == 'undefined') {offsetLeft="0"}
	if (!offsetTop || offsetTop == null || offsetTop == 'undefined') {offsetTop=ss_activateMenuOffsetTop}
	if (!openStyle || openStyle == null || openStyle == 'undefined') {openStyle=""}

    // don't do anything if the divs aren't loaded yet
	var parentDivObj = document.getElementById(parentDivId);
    var menuObj = self.document.getElementById(divId);
    if (menuObj == null) {return}

    // Put a lightbox under the menu
    ss_showHideMenuLightbox(divId, window.name);
    
	var x = 0;
	var y = 0;
    if (parentDivId != "") {
    	var pObj = document.getElementById(parentDivId);
    	x = dojo.coords(pObj, true).x
    	y = dojo.coords(pObj, true).y
	    // Add the offset to the x and y positions so the div isn't occluding
		// too much
	    x = parseInt(parseInt(x) + parseInt(offsetLeft))
	    y = parseInt(parseInt(y) + ss_getDivHeight(parentDivId) + parseInt(offsetTop))
    } else {
	    x = ss_getClickPositionX();
	    y = ss_getClickPositionY();
	    // Add a little to the x and y positions so the div isn't occluding too
		// much
	    x = parseInt(parseInt(x) + parseInt(offsetLeft));
	    y = parseInt(parseInt(y) + parseInt(offsetTop));
	}

	var savedDivDisplay = menuObj.style.display;
	menuObj.style.display = "block";
    var maxWidth = 0;
    var divWidth = ss_getObjectWidth(menuObj);
	menuObj.style.display = savedDivDisplay;

    if (ss_isNSN6 || ss_isMoz5) {
        // need to bump layer an extra bit to the right to avoid horiz scrollbar
        divWidth = parseInt(divWidth) + 20;
        maxWidth = parseInt(window.innerWidth);
    } else {
        divWidth = parseInt(divWidth) + 20;
        //maxWidth = parseInt(ss_getBodyHeightWidth()[0]);
        maxWidth = parseInt(document.body.scrollWidth);
    }

	// console.log(divId, " dw ", divWidth, " mw ", maxWidth);
	
    if (x + divWidth > maxWidth) {
        x = maxWidth - divWidth;
    } 
  
    // alert('divId: ' + divId + ', x: ' + x + ', y: ' + y)
    // alert(document.getElementById(divId).innerHTML)
    if (!menuObj.style || !menuObj.style.zIndex || menuObj.style.zIndex == 0) {
    	menuObj.style.zIndex = ssMenuZ;
    }

    // Put a lightbox under the menu
    ss_showHideMenuLightbox(divId, window.name);
    
    ss_ShowHideDivXY(divId, x, y);
    ss_setFocusToFirstA(divId)
    var scrollTop = parseInt(dojo.coords(menuObj, true).y - dojo.coords(menuObj, false).y);
    var menuBottom = parseInt(y + parseInt(ss_getObjectHeight(menuObj)));
    var screenBottom = parseInt(scrollTop + ss_getWindowHeight())
    if (menuBottom > screenBottom) {
    	// The menu is off the bottom of the screen, scroll until it is in view
    	var scrollAmount = parseInt(menuBottom - screenBottom + 10)
    	if (scrollAmount > parseInt(ss_getWindowHeight())) scrollAmount = parseInt(ss_getWindowHeight());
    	if (scrollAmount < 0) scrollAmount = 0;
    	window.scrollBy(0, scrollAmount);
    }
    
    if (openStyle != "popup") ss_HideDivOnSecondClick(divId);
    ssf_onLayoutChange();
	if (parentDivId != "") {
		if (parentDivObj != null) {
			document.getElementById(divId).tabIndex=document.getElementById(parentDivId).tabIndex;
		}
	}
}

// activate_menulayer tests this flag to make sure the page is
// loaded before the pulldown menus are clicked.
function ss_setLayerFlag() {
    ss_layerFlag = 1;
}

ss_createOnLoadObj('ss_layerFlag', ss_setLayerFlag);

// Routine to put a lightbox under the menu div
var ss_menuLightboxDivIds = new Array();
var ss_menuLightboxWindowNames = new Array();
function ss_showHideMenuLightbox(divId, windowName) {
	return;  // This has been turned off because it doesn't work for folder
				// "Add" menus
	if (window.name == windowName) {
		// If in the same window, do the check to see if the menu is being shown
		var divObj = self.document.getElementById(divId);
		if (divObj != null && divObj.style.display == 'block') {
			// The div is visible, so don't add the lightbox
			return;
		}
	}
	
	// Do this in the top most frame
	if (self != self.parent && typeof parent.ss_showHideMenuLightbox != 'undefined') {
		self.parent.ss_showHideMenuLightbox(divId, windowName);
		return;
	}
	var lightBox = document.getElementById('ss_entry_menu_light_box')
	if (!lightBox) {
		// Add the lightbox div onto this frame if it doesn't exist
		var bodyObj = document.getElementsByTagName("body").item(0)
		lightBox = document.createElement("div");
        lightBox.setAttribute("id", "ss_entry_menu_light_box");
        lightBox.style.position = "relative";
        bodyObj.appendChild(lightBox);
	}
	lightBox.style.backgroundColor = "#ffffff";
	ss_setOpacity(lightBox, .2);
    lightBox.style.display = "block";
    lightBox.style.top = 0 + "px";
    lightBox.style.left = 0 + "px";
    lightBox.style.width = ss_getBodyWidth() + "px";
    lightBox.style.height = ss_getBodyHeight()  + "px";
    lightBox.style.zIndex = parseInt(ss_gwtLightboxZ);
    lightBox.style.visibility = "visible";			
    
    // Save the divId
    var i = ss_menuLightboxDivIds.length;
    ss_menuLightboxDivIds[i] = divId;
    ss_menuLightboxWindowNames[i] = windowName;
}
function ss_hideMenuLightbox() {
	// Do this in the top most frame
	if (self != self.parent && typeof parent.ss_hideMenuLightbox != 'undefined') {
		self.parent.ss_hideMenuLightbox();
		return;
	}
	var lightBox = document.getElementById('ss_entry_menu_light_box')
	if (lightBox) {
	    lightBox.style.display = "none";
	}
	// Hide all of the menus
    if (ss_menuLightboxDivIds.length > 0) {
        for (var i = 0; i < ss_menuLightboxDivIds.length; i++) {
        	var windowName = ss_menuLightboxWindowNames[i];
        	var divId = ss_menuLightboxDivIds[i];
        	if (typeof window.frames[windowName].document != 'undefined') {
        		window.frames[windowName].ss_HideDivXY(divId);
        		window.frames[windowName].ss_hideAllDivsToBeHidden();
        	}
	    }
    }
    ss_menuLightboxDivIds = new Array();
    ss_menuLightboxWindowNames = new Array();
}

// Routine to set the focus onto the first anchor in a div (for accessibility)
function ss_setFocusToFirstA(divId) {
	var divObj = self.document.getElementById(divId);
	if (divObj != null) {
		var aElements = divObj.getElementsByTagName("a");
		if (aElements == null || aElements.length <= 0) return;
		for (var i = 0; i < aElements.length; i++) {
			var aObj = aElements.item(i);
			if (typeof aObj.className == "undefined" || aObj.className != "ss_skiplink") {
				try {aObj.focus();} catch(e){}
				return;
			}
		}
	}
}

// Support for positioning divs at x,y
// Enable the event handler
ss_createEventObj('captureXY', 'MOUSEUP')

// General routine to show a div given its name and coordinates
function ss_ShowHideDivXY(divName, x, y) {
    if (ss_divBeingShown == divName) {
        ss_hideDiv(ss_divBeingShown)
        ss_divBeingShown = null;
        ss_lastDivBeingShown = null;
    } else {
        if (ss_lastDivBeingShown == divName) {
            ss_lastDivBeingShown = null;
            return
        }
        ss_lastDivBeingShown = null;
        if (ss_divBeingShown != null) {
            ss_hideDiv(ss_divBeingShown)
        }
        ss_divBeingShown = divName;
        ss_lastDivBeingShown = divName;
        if (x != null && y != null && x != '' && y != '') ss_positionDiv(ss_divBeingShown, x, y);
        ss_showDiv(ss_divBeingShown)
        ss_hideSpannedAreas();
    }
}
// General routine to hide a div given its name and coordinates
function ss_HideDivXY(divName) {
    if (ss_divBeingShown == divName) {
        ss_hideDiv(ss_divBeingShown)
        ss_divBeingShown = null;
        ss_lastDivBeingShown = null;
    } else {
        if (ss_lastDivBeingShown == divName) {
            ss_hideDiv(divName)
            ss_divBeingShown = null;
            ss_lastDivBeingShown = null;
            return
        }
        ss_lastDivBeingShown = null;
        if (ss_divBeingShown != null) {
            ss_hideDiv(ss_divBeingShown)
        }
        ss_divBeingShown = null;
        ss_lastDivBeingShown = null;
    }
}

function ss_hideDivToBeHidden(i) {
	if (ss_divToBeHidden[i] != '') {
		if (ss_divToBeHidden[i] != ss_divBeingShown) {
			ss_hideDiv(ss_divToBeHidden[i]);
		}
	    ss_divToBeHidden[i] = '';
	    ss_showSpannedAreas();
	}
}

/* IE6 workaround - divs under selectboxes */
var ss_showBackgroundIframeDivId = null;
function ss_showBackgroundIFrame(divId, frmId) {
	return;  //This facility has been turned off because it doesn't work well with the GWT UI

	if (!ss_isIE6) {
		return;
	}
	if (ss_showBackgroundIframeDivId != null && ss_showBackgroundIframeDivId != divId) {
		// Delete the previous iframe if any
		var frm = document.getElementById(frmId);
		try {
			if (frm) {
				frm.parentNode.removeChild(frm);
			}
		} catch (e) {}
	}
	ss_showBackgroundIframeDivId = divId;
	var div = document.getElementById(divId);
	if (!div) {
		return;
	}
	if (div.getElementsByTagName('iframe').length > 0) return;
	if (!div.style.zIndex) {
		div.style.zIndex = ssLightboxZ - 1;
	}
	var frm = document.getElementById(frmId);
	if (frm == null) {
		frm = document.createElement("iframe");
		if (typeof ss_baseRootPathUrl != 'undefined') {
			var teaming_url = ss_baseRootPathUrl + 'js/forum/null.html';
//!			frm.src = teaming_url;
			ss_setUrlInFrame(frm, teaming_url);
		}
		frm.frameBorder = 0;
		frm.scrolling = "no";
		document.body.appendChild(frm);
		frm.id = frmId;
		frm.className = "ss_background_iframe";
	}
	if (div.style.zIndex) {
		frm.style.zIndex = div.style.zIndex * 1 - 1;
	} else {
		frm.style.zIndex = ssLightboxZ - 2;
	}
	var top = dojo.coords(div, true).y;
	var left = dojo.coords(div, true).x;
    ss_setObjectTop(frm, top);
    ss_setObjectLeft(frm, left);

	frm.style.height = ss_getObjectHeight(div) + "px";
	frm.style.width = ss_getObjectWidth(div) + "px";
	
	frm.style.position = "absolute";
	frm.style.display = "block";
	try {frm.focus();} catch(e){}
}

/* IE6 workaround - divs under selectboxes */
function ss_hideBackgroundIFrame(frmId) {
	if (!ss_isIE6) {
		return;
	}
	if (ss_showBackgroundIframeDivId != null) {
		var divObj = document.getElementById(ss_showBackgroundIframeDivId)
		if (divObj != null && divObj.style.visibility != 'hidden') return
	}
	var frm = document.getElementById(frmId);
	try {
		if (frm) {
			frm.parentNode.removeChild(frm);
		}
	} catch (e) {}
	ss_showBackgroundIframeDivId = null;
}

function ss_showDivActivate(divName, nofocus) {
	if (typeof nofocus == "undefined") nofocus = false;
    if (ss_divBeingShown != null) {
        ss_hideDiv(ss_divBeingShown);
    }
    ss_divBeingShown = divName;
    ss_lastDivBeingShown = divName;
    if (nofocus) {
    	// Show the div by hand and don't set focus to it.
    	var divObj = document.getElementById(divName);
    	divObj.style.display = "block";
    	divObj.style.visibility = "visible";
    } else {
    	ss_showDiv(divName);
    }
	ss_HideDivOnSecondClick(divName);
}

// General routine to show a div given its name
function ss_HideDivIfActivated(divName) {
    if (divName == ss_divBeingShown) {
        ss_hideDiv(ss_divBeingShown);
        ss_divBeingShown = null;
        ss_lastDivBeingShown = null;
        return true;
    } else {
    	return false;
    }
}

// Routine to make div's be hidden on next click
function ss_HideDivOnSecondClick(divName) {
    ss_divToBeHidden[ss_divToBeHidden.length] = divName;
    //ss_debug('ss_divToBeHidden length = '+ss_divToBeHidden.length)
}

// Routine to make div's be hidden on next click
function ss_NoHideDivOnNextClick(divName) {
    ss_divToBeDelayHidden[divName] = divName;
}

function ss_showDivAtXY(divName) {
	var divObj = document.getElementById(divName);
	ss_moveObjectToBody(divObj)
	var objTopOffset = 10;
	var objLeftOffset = -10;
	ss_setObjectTop(divObj, parseInt(ss_getClickPositionY() + objTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getClickPositionX() + objLeftOffset))
	ss_toggleShowDiv(divName, "")
}
function ss_showDiv(divName, backgroundIframe) {
	var divObj = document.getElementById(divName);
	if (divObj == null) return;
	ss_showDivObj(divObj, backgroundIframe)
}
function ss_showDivObj(divObj, backgroundIframe) {
    if (typeof divObj.style.display == 'undefined' || divObj.style.display == '' || divObj.style.display != 'inline') {
    	divObj.style.display = "block";
    }
    divObj.style.visibility = "visible";
    try {divObj.focus();} catch(e) {}
	if (typeof backgroundIframe == 'undefined' || backgroundIframe != 'no') 
		ss_showBackgroundIFrame(divObj.id, "ss_background_iframe");
	// Signal that the layout changed
	if (!divObj || divObj.style.position != "absolute") {
		ssf_onLayoutChange();
		// ss_debug("ss_showDivObj: " + divObj.id)
	}
}

function ss_hideDiv(divName) {
	var divObj = document.getElementById(divName);
	ss_hideDivObj(divObj);
}
function ss_hideDivObj(divObj) {
	var doLayoutChange = false;
	if (divObj != null) {
		if (typeof divObj.style.display == "undefined" || divObj.style.display != "none") {
			divObj.style.visibility = "hidden";
			divObj.style.display = "none";
			doLayoutChange = true;
		}
    	ss_divToBeDelayHidden[divObj.id] = null;
    }
    ss_divBeingShown = null;
    ss_hideBackgroundIFrame("ss_background_iframe");
	// Signal that the layout changed
	if (divObj != null && divObj.style.position != "absolute") {
		if (doLayoutChange) {
			ssf_onLayoutChange();
			// alert("ss_hideDiv: " + divObj.id)
		}
	}
}

function ss_hideDivNone(divName) {
	if (document.getElementById(divName) != null) {
		document.getElementById(divName).style.visibility = "hidden";
		document.getElementById(divName).style.display = "none";
	}
    ss_divToBeDelayHidden[divName] = null
    ss_divBeingShown = null;
    
	// Signal that the layout changed
	if (document.getElementById(divName) == null || 
	    	document.getElementById(divName).style.position != "absolute") {
		ssf_onLayoutChange();
		// ss_debug("ss_hideDiv: " + divName)
	}
}

function ss_hideAllDivsToBeHidden() {
	// hide any lightbox divs
	ss_hideMenuLightbox();
	
    if (ss_divToBeHidden.length > 0) {
        for (var i = 0; i < ss_divToBeHidden.length; i++) {
	        if (ss_divToBeHidden[i] != '') {
	            if (ss_divToBeDelayHidden[ss_divToBeHidden[i]]) {
	                ss_divToBeDelayHidden[ss_divToBeHidden[i]] = null
	            } else {
	                if (ss_divBeingShown == ss_divToBeHidden[i]) {
	                	ss_divBeingShown = '';
	                }
	                setTimeout("ss_hideDivToBeHidden('"+i+"');",100);
	            }
	        }
	    }
    }
}

function ss_positionDiv(divName, x, y) {
	if (self.document.getElementById(divName) && self.document.getElementById(divName).offsetParent) {
        self.document.getElementById(divName).style.left= (x - parseInt(self.document.getElementById(divName).offsetParent.offsetLeft)) + "px"
        self.document.getElementById(divName).style.top= (y - parseInt(self.document.getElementById(divName).offsetParent.offsetTop)) + "px"
    } else {
        self.document.getElementById(divName).style.left= x + "px"
        self.document.getElementById(divName).style.top= y + "px"
    }
}

function ss_getClickPositionX() {
    return ss_mousePosX
}

function ss_getClickPositionY() {
    return ss_mousePosY
}

function captureXY(e) {
    if (!e) e = window.contents.event;

    // Is this click a "right click"? If yes, ignore it
    if (e && e.which && (e.which == 3 || e.which == 2)) {
        return false;
    } else if (e && e.button && (e.button == 2 || e.button == 3)) {
        return false;
    }

    // See if there is a div to be hidden
    ss_lastDivBeingShown = ss_divBeingShown;
    ss_hideAllDivsToBeHidden();
    if (ss_isNSN6 || ss_isMoz5) {
        ss_mousePosX = e.pageX
        ss_mousePosY = e.pageY
        ss_mouseX = e.layerX
        ss_mouseY = e.layerY
        return(true)
    } else if (ss_isNSN) {
        ss_mousePosX = e.x
        ss_mousePosY = e.y
        ss_mouseX = e.layerX
        ss_mouseY = e.layerY
        var imgObj = getNN4ImgObject(e.layerX, e.layerY)
        if (imgObj != null) {
            ss_mouseX = imgObj.x
            ss_mouseY = imgObj.y
        }
        return(true)
    } else {
        // ss_mousePosX = event.x + ss_getScrollXY()[0]
        // ss_mousePosY = event.y + ss_getScrollXY()[1]
        ss_mousePosX = event.clientX + ss_getScrollXY()[0];
        ss_mousePosY = event.clientY + ss_getScrollXY()[1];
        ss_mouseX = event.clientX;
        ss_mouseY = event.clientY;
        var imgObj = window.event.srcElement
        if (imgObj.name != null && imgObj.name != "" && !ss_isMacIE) {
            ss_mouseX = ss_getImageLeft(imgObj.name)
            ss_mouseY = ss_getImageTop(imgObj.name)
        }
    }
}

// Routines to get an object handle given the x,y coordinates of the image
function getNN4ImgObject(imgX, imgY) {
    var imgObj = getNN4ImgObjectObj(self, imgX, imgY)
    return imgObj
}

function getNN4ImgObjectObj(divObj, imgX, imgY) {
    // Look in this div for the image
    for (var i = 0; i < divObj.document.images.length; i++) {
        var testImgObj = divObj.document.images[i]
        if ( testImgObj && testImgObj.x &&   (imgX >= testImgObj.x) && 
                (imgX <= testImgObj.x + testImgObj.width) && 
                (imgY >= testImgObj.y) && 
                (imgY <= testImgObj.y + testImgObj.height)    ) {
                return(testImgObj)
        }
    }
    // The image isn't in this div, look in the children divs
    for (var n = 0; n < divObj.document.layers.length; n++) {
        var testObj = divObj.document.layers[n]
        var imgObj = getNN4ImgObjectObj(testObj, imgX, imgY)
        if (imgObj != null) {return imgObj}
    }
    return null
}

// Routines to get a div handle of the owner of an image
function getNN4ImgDivObject(imgObj) {
    var divObj = getNN4ImgDivObjectObj(self, imgObj)
    return divObj
}

function getNN4ImgDivObjectObj(divObj, imgObj) {
    // Look in this div for the image
    for (var i = 0; i < divObj.document.images.length; i++) {
        var testImgObj = divObj.document.images[i]
        if (testImgObj == imgObj) {
            return(divObj)
        }
    }
    // The image isn't in this div, look in the children divs
    for (var n = 0; n < divObj.document.layers.length; n++) {
        var testDivObj = divObj.document.layers[n]
        var testImgObj = getNN4ImgDivObjectObj(testDivObj, imgObj)
        if (testImgObj != null) {return testDivObj}
    }
    return null
}

// Routine to create a new "onErrorObj" object
// onErrorObj objects are set up whenever you want to call something at onError
// time.
function ss_createOnErrorObj(name, onErrorName) {
    for (var i = 0; i < ss_onErrorList.length; i++) {
    	if (ss_onErrorList[i].name == name) return;
    }
    var next = ss_onErrorList.length;
    ss_onErrorList[next] = new onErrorObj(name);
    ss_onErrorList[next].setOnErrorRoutine(onErrorName);
    window.onerror = ssf__onError_event_handler
}
function onErrorObj(name) {
    this.name = name;
    this.onErrorRoutine = null;
    this.getOnErrorRoutine = m_getOnErrorRoutine;
    this.setOnErrorRoutine = m_setOnErrorRoutine;
}
function m_getOnErrorRoutine() {
    return this.onErrorRoutine;
}
function m_setOnErrorRoutine(onErrorRoutine) {
    this.onErrorRoutine = onErrorRoutine;
}
function ssf__onError_event_handler() {
    var ret = false
    for (var i = 0; i < ss_onErrorList.length; i++) {
        if (ss_onErrorList[i].onErrorRoutine()) {ret = true}
    }
    return ret
}

// Routine to create a new "spannedArea" object
// spannedAreaObj objects are set up whenever you need some form elements to be
// blanked when showing the menus
function ss_createSpannedAreaObj(name) {
	for (var i = 0; i < ss_spannedAreasList.length; i++) {
		if (ss_spannedAreasList[i].name == name) return ss_spannedAreasList[i];
	}
    var next = ss_spannedAreasList.length;
    ss_spannedAreasList[next] = new spannedAreaObj(name);
    return ss_spannedAreasList[next];
}
function spannedAreaObj(name) {
    this.name = name;
    this.showArgumentString = '';
    this.hideArgumentString = '';
    this.setHideRoutine = m_setHideRoutine;
    this.showRoutine = null;
    this.show = function() {ss_showDiv(this.name);}
    this.setShowRoutine = m_setShowRoutine;
    this.hideRoutine = null;
    this.hide = function() {ss_hideDiv(this.name);}
}
function m_setShowRoutine(showRoutine) {
    this.showRoutine = showRoutine;
    // See if there are any other arguments passed in
    // These will get passed on to the show routine
    for (var i = 1; i < m_setShowRoutine.arguments.length; i++) {
        if (this.showArgumentString != '') {this.showArgumentString += ',';}
        this.showArgumentString += '"'+m_setShowRoutine.arguments[i]+'"';
    }
}
function m_setHideRoutine(hideRoutine) {
    this.hideRoutine = hideRoutine;
    // See if there are any other arguments passed in
    // These will get passed on to the show routine
    for (var i = 1; i < m_setHideRoutine.arguments.length; i++) {
        if (this.hideArgumentString != '') {this.hideArgumentString += ',';}
        this.hideArgumentString += '"'+m_setHideRoutine.arguments[i]+'"';
    }
}
function m_showSpannedArea() {
    eval(this.showRoutine+'('+this.showArgumentString+');')
}
function m_hideSpannedArea() {
    eval(this.hideRoutine+'('+this.hideArgumentString+');')
}

function ss_toggleSpannedAreas(spanName,newValue) {
    if (self.document.layers && document.layers[spanName] != null) {
        self.document.layers[spanName].visibility = newValue;
    } else if (document.getElementById(spanName) != null) {
        document.getElementById(spanName).style.visibility = newValue;
    }
}

function ss_hideSpannedAreas() {
    // Hide any form elements that may be visible
    for (var i = 0; i < ss_spannedAreasList.length; i++) {
        var divObj = self.document.getElementById(ss_spannedAreasList[i].name);
        if (divObj != null) {
        	divObj.style.visibility = "hidden";
        	divObj.style.display = "none";
        }
    }
}

function ss_showSpannedAreas() {
    // Show any form elements that should be returned to the visible state
    if (typeof ss_spannedAreasList == 'undefined' || ss_spannedAreasList == null) return;
    for (var i = 0; i < ss_spannedAreasList.length; i++) {
        var divObj = self.document.getElementById(ss_spannedAreasList[i].name);
        if (divObj != null) {
        	divObj.style.display = "block";
        	divObj.style.visibility = "visible";
        }
    }
}

// Routine to pop up a window with a url (used in common toolbar code)
function ss_toolbarPopupUrl(url, windowName, width, height) {
	//ss_debug("**** "+ss_debugTrace());
	if (typeof width == "undefined") {
		width = ss_getWindowWidth();
		if (width < 600) width=600;
	}
	if (typeof height == "undefined") {
		var height = ss_getWindowHeight();
		if (height < 600) height=600;
	}
	var hw = "";
	if (width != "") hw += ",width="+parseInt(width) + "px";
	if (height != "") hw += ",height="+parseInt(height) + "px";
	var popupDiv = self.document.getElementById("ss_showpopupdiv");
	var popupIframe = self.document.getElementById("ss_showpopupframe");
	if (url != "" && popupDiv != null && popupIframe != null) {
		var blankhtml = "<div class=\"ss_loading\">" + ss_loadingMessage + "</div>";
		try {
			window.frames['ss_showpopupframe'].document.body.innerHTML = blankhtml;
		} catch(e) {}
		if (typeof popupDiv.style != "undefined" && 
				typeof popupDiv.style.display != "undefined" && popupDiv.style.display != "block") {
			popupDiv.style.display = "block";
			popupDiv.style.visibility = "visible";
		}
		var entryContentDiv = self.document.getElementById("ss_entryContentDiv");
		if (entryContentDiv != null) {
			entryContentDiv.style.display = "none";
		}
		ss_resizePopupDiv();
//!		popupIframe.src = url;
		ss_setUrlInFrame(popupIframe, url);
	} else if (url != "" && self.window.name == "ss_showentryframe") {
		// Instead of popping up into another window, we now use the current
		// showEntry frame
		ss_setSelfLocation(url);
	} else if (url != "" && self.window.name == "gwtContentIframe") {
		// Instead of popping up into another window, we now use the current
		// content frame
		ss_setSelfLocation(url);
	} else {
		self.window.open(url?url:"", windowName?windowName:"_blank", "resizable=yes,scrollbars=yes"+hw);
	}
	return false;
}
var ss_popupFrameWidthFudge = 0;
var ss_popupFrameHeightFudge = 0;
if (ss_isIE) {
	ss_popupFrameWidthFudge = 0;
	ss_popupFrameHeightFudge = 0;   // account for the potential horizontal scroll bar
}
var ss_popupFrameTimer = null;
function ss_resizePopupDiv() {
	//ss_debug("**** "+ss_debugTrace());
	var popupDiv = self.document.getElementById("ss_showpopupdiv");
	var popupIframe = self.document.getElementById("ss_showpopupframe");
	if (popupDiv != null && popupIframe != null && typeof popupDiv.style != "undefined" &&
			typeof popupDiv.style.display != "undefined" && popupDiv.style.display == "block" &&
			typeof window.frames['ss_showpopupframe'] != "undefined" && window.frames['ss_showpopupframe'] != null &&
			typeof window.frames['ss_showpopupframe'].document != "undefined" && window.frames['ss_showpopupframe'].document != null &&
			typeof window.frames['ss_showpopupframe'].document.body != "undefined" && window.frames['ss_showpopupframe'].document.body != null &&
			typeof window.frames['ss_showpopupframe'].document.body.scrollHeight != "undefined") {
		var scrollHeight = parseInt(window.frames['ss_showpopupframe'].document.body.scrollHeight);
		var scrollWidth = parseInt(ss_getBodyHeightWidth(top.window.frames['ss_showpopupframe'])[0]);
		//ss_debug("   ss_resizePopupDiv, scroll height: "+scrollHeight);
		//ss_debug("   ss_resizePopupDiv, scroll width: "+scrollWidth);
		var height = parseInt(scrollHeight);
		var width = parseInt(scrollWidth);
		var windowHeight = parseInt(parseInt(ss_getWindowHeight()) - ss_popupFrameHeightFudge);
		
		//ss_debug("   ss_resizePopupDiv, window height: "+windowHeight)
		//ss_debug("   ss_resizePopupDiv, parent window height: "+ss_getWindowHeight(self.parent))
		//ss_debug("   ss_resizePopupDiv, window width: "+ss_getWindowWidth())
		var windowWidth = ss_getWindowWidth();
		if (ss_getUserDisplayStyle() == "newpage") {
			if (parseInt(popupIframe.style.height) != windowHeight ||
					parseInt(popupIframe.style.width) != width) {
				popupIframe.style.height = parseInt(windowHeight) + "px";
				popupDiv.style.height = parseInt(windowHeight) + "px";
				popupIframe.style.width = parseInt(width) + "px";
			}

		} else {
			//See if the entry div needs to be resized first
			if (typeof ss_setEntryDivObjectHeight != "undefined") {
				var entryDivTop = ss_getEntryDivObjectTop();
				var entryDivMaxHeight = ss_getEntryDivMaxHeight(entryDivTop);
				//ss_debug("   ss_resizePopupDiv entryDivMaxHeight: "+entryDivMaxHeight)
				if (scrollHeight > ss_getWindowHeight()) {
					if (typeof entryDivMaxHeight != "undefined" && scrollHeight > entryDivMaxHeight) {
						ss_setEntryDivObjectHeight(entryDivMaxHeight);
					} else {
						ss_setEntryDivObjectHeight(scrollHeight);
					}
				}
			}
			if (parseInt(popupIframe.style.height) != parseInt(ss_getWindowHeight())) {
				//ss_debug("   ss_resizePopupDiv, set height to: "+ss_getWindowHeight())
				popupIframe.style.height = parseInt(ss_getWindowHeight()) + "px"
			}
			if (parseInt(popupIframe.style.width) != parseInt(ss_getWindowWidth() - 22)) {
				//ss_debug("   ss_resizePopupDiv, set width to: "+parseInt(ss_getWindowWidth() - 22))
				popupIframe.style.width = parseInt(ss_getWindowWidth() - 22) + "px"
			}
		}
	}
}

function ss_hidePopupDiv() {
	var entryContentDiv = self.document.getElementById("ss_entryContentDiv");
	if (entryContentDiv != null) entryContentDiv.style.display = "block";
	var popupDiv = self.document.getElementById("ss_showpopupdiv");
	if (popupDiv != null && popupDiv.style.display != "none") {
		popupDiv.style.display = "none";
		popupDiv.style.visibility = "hidden";
		// Signal that the layout changed
		if (ssf_onLayoutChange) {
			setTimeout("ssf_onLayoutChange();", 100);
		}
	}
}

// Routine to show a div at the bottom of the highest size attained by the
// window
function setWindowHighWaterMark(divName) {
	var currentPageHeight = ss_getBodyHeight()
	if (parseInt(ss_forum_maxBodyWindowHeight) < parseInt(currentPageHeight)) {
		// Time to set a new high water mark
		ss_forum_maxBodyWindowHeight = currentPageHeight;
	}
	var dh = ss_getDivHeight(divName);
	var x = 0
	var y = parseInt(ss_forum_maxBodyWindowHeight - dh)
	ss_positionDiv(divName, x, y);
	ss_showDiv(divName)
}


// Routines to replace substrings in a string
function ss_replaceSubStr(str, subStr, newSubStrVal) {
    // ss_debug("ss_replaceSubStr: " + str + ", " + subStr + " ==> " +
	// newSubStrVal)
    if (typeof str == 'undefined') return str;
    var newStr = str;
	var i = str.indexOf(subStr);
    var lenS = str.length;
    var lenSS = subStr.length;
    if (i >= 0) {
        newStr = str.substring(0, i) + newSubStrVal + str.substring(i+lenSS, lenS);
    }
    // ss_debug(" new str = " + newStr)
	return newStr;
}
function ss_replaceSubStrAll(str, subStr, newSubStrVal) {
    if (typeof str == 'undefined') return str;
    var newStr = str;
    var i = -1
    // Prevent a possible loop by only doing 1000 passes through this loop
    while (1000) {
        i = newStr.indexOf(subStr, i);
        var lenS = newStr.length;
        var lenSS = subStr.length;
        if (i >= 0) {
            newStr = newStr.substring(0, i) + newSubStrVal + newStr.substring(i+lenSS, lenS);
            i += newSubStrVal.length
        } else {
            break;
        }
    }
    return newStr;
}

// Routine to build a status_message div if one doesn't exist yet
function ss_setupStatusMessageDiv() {
	var smId = document.getElementById('ss_status_message');
	if (!smId) {
		// There isn't a status message div, so go build it
		var smDiv = document.createElement("div");
        smDiv.setAttribute("id", "ss_status_message");
        smDiv.style.visibility = "hidden";
        smDiv.style.display = "none";
    	document.getElementsByTagName("body").item(0).appendChild(smDiv);
	}
}
//Routine to get a status_message
function ss_getStatusMessage() {
	var value = '';
	var smId = document.getElementById('ss_status_message');
	if (smId != null) {
		// There is a status message
		value = smId.innerHTML;
	}
	return value;
}

// common processing for callback after ajax call
function ss_postRequestAlertError(obj) {
	// See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
}

// See if there is a javascript console for logging debug messages
if (!window.console) console = {};
console.log = console.log || function(){};

// Routine to write text to the debug window
function ss_debug(text) {
	console.log(text);
	return;
	if (typeof ss_debugTextareaId == "undefined") return;
	if (ss_debugTextareaId == '') return;
	var debugTextarea = document.getElementById(ss_debugTextareaId);
	if (debugTextarea != null) {
		var html = debugTextarea.value;
		if (html.length > 10000) html = html.substring(html.length - 6000, html.length);
		html +=  "\n";
		html += text;
		debugTextarea.value = html;
		debugTextarea.scrollTop = 1000;
	}
}
function ss_debugTrace(level) {
	if (typeof level == "undefined") level = 2;
	//Get the caller
	var text = "";
	var caller1 = arguments.callee.caller;
	if (caller1 != null) {
		text += caller1.name + " ";
		var c = caller1.caller;
		for (var i = 0; i < level; i++) {
			if (c == null || c.name == "") break;
			text += "<-- " + c.name + " ";
			c = c.caller;
		}
	}
	return text;
}

function ss_showNotLoggedInMsg() {
	alert(ss_not_logged_in);
}

function ss_showLightbox(id, zIndex, opacity, className, parentDiv) {
	ss_hideSpannedAreas();
	ss_hidePopupDivs();
	if (ss_isGwtUIActive && window.name != "ss_showentryframe") {
		ss_hideEntryDiv();
	}
	if (id == null) id = "ss_light_box";
	if (zIndex == null) zIndex = ssLightboxZ;
	if (opacity == null) opacity = .5;
	if (className == null) className = "ss_lightBox";
	var lightBox = document.getElementById(id)
	if (!lightBox) {
		var bodyObj = document.getElementsByTagName("body").item(0)
		lightBox = document.createElement("div");
        lightBox.setAttribute("id", id);
        lightBox.className = className;
        lightBox.innerHTML = "&nbsp;";
        bodyObj.appendChild(lightBox);
	}
    lightBox.style.visibility = "hidden";
    lightBox.className = className;
    lightBox.style.display = "block";
    if (typeof parentDiv == "undefined") {
	    lightBox.style.top = "0px";
	    lightBox.style.left = "0px";
	    ss_setOpacity(lightBox, 0);
	    lightBox.style.width = ss_getBodyWidth() + "px";
	    lightBox.style.height = ss_getBodyHeight() + "px";
    } else {
	    lightBox.style.top = ss_getDivTop(parentDiv.id) + "px";
	    lightBox.style.left = ss_getDivLeft(parentDiv.id) + "px";
	    ss_setOpacity(lightBox, 0);
	    lightBox.style.width = ss_getDivWidth(parentDiv.id) + "px";
	    lightBox.style.height = ss_getDivHeight(parentDiv.id) + "px";
    }
    lightBox.style.zIndex = zIndex;
    lightBox.style.visibility = "visible";
    lightBox.focus();
    dojo.fadeIn({node:lightBox, end:opacity, delay:150}).play();
    return lightBox;
}
function ss_hideLightbox(id) {
	if (id == null) id = "ss_light_box";
	var lightBox = document.getElementById(id)
	if (lightBox) {
		lightBox.style.visibility = "hidden";
		lightBox.style.display = "none";
		lightBox.parentNode.removeChild(lightBox);
	}
	ss_showSpannedAreas();
	ss_hidePopupDivs();
}

// Support routines for the help system
var ss_help_position_topOffset = 10;
var ss_help_position_bottomOffset = 20;
var ss_help_position_rightOffset = 20;
var ss_help_position_leftOffset = 10;
var ss_helpSystem = {

	tocBuilt:  false,
	
	run : function() {
		this.show();
		this.showHelpSpots()
	},
	
	show : function() {
		// ss_debug('ss_helpSystem');
		var lightBox = ss_showLightbox("ss_help_light_box", ssHelpZ)
	    lightBox.onclick = function(e) {if (ss_helpSystem) ss_helpSystem.hide();};
	    
		ss_moveDivToBody('ss_help_welcome');
		var helpUrl = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"show_help_panel", operation2:"welcome_panel"});
		ss_fetch_div(helpUrl, "ss_help_welcome", false);
		var welcomeDiv = document.getElementById('ss_help_welcome');
		var helpMenuAnchorDiv = document.getElementById('ss_helpMenuAnchor');
		if (welcomeDiv) {
	    	welcomeDiv.style.visibility = "visible";
	    	welcomeDiv.style.zIndex = ssHelpWelcomeZ;
	    	welcomeDiv.style.display = "block";
	        // welcomeDiv.style.top = this.getPositionTop(welcomeDiv);
	        // welcomeDiv.style.left = this.getPositionLeft(welcomeDiv);
	        dijit.placeOnScreen(welcomeDiv, {x:50, y:0}, "TL", false);
	        if (helpMenuAnchorDiv != null) {
	        	helpMenuAnchorDiv.style.visibility = "visible";
	        	helpMenuAnchorDiv.style.display = "block";
	        	// ss_setObjectHeight(helpMenuAnchorDiv,
				// ss_getObjectHeight(welcomeDiv));
	        	var offsetT = -2;
	        	if (ss_isIE) offsetT = 6;
	        	// welcomeDiv.style.top =
				// parseInt(ss_getObjectTopAbs(helpMenuAnchorDiv) - offsetT) +
				// "px";
	        	var offsetL = parseInt((ss_getObjectWidth(helpMenuAnchorDiv) - ss_getObjectWidth(welcomeDiv)) / 2);
	        	// welcomeDiv.style.left =
				// parseInt(ss_getObjectLeftAbs(helpMenuAnchorDiv) + offsetL) +
				// "px";
	        }
	    	ss_setOpacity(welcomeDiv, 0);
	    	dojo.fadeIn({node:welcomeDiv, start:0, end:1.0, delay:150}).play();
	    	dojo.connect(window, "onscroll", this, "moveWelcomeIntoView");
		}
	},

	moveWelcomeIntoView : function (e) {
		dijit.placeOnScreen(dojo.byId("ss_help_welcome"), {x:0, y:0}, "TL", false);
	},
	
	hide : function() {
		var bodyObj = document.getElementsByTagName("body").item(0)
		var lightBox = document.getElementById('ss_help_light_box')
		if (!lightBox) return;
		var welcomeDiv = document.getElementById('ss_help_welcome');
		if (welcomeDiv) {
	    	welcomeDiv.style.visibility = "hidden";
	    	welcomeDiv.style.display = "none";
	    	dojo.disconnect(dojo.body(), "onscroll", this, "moveWelcomeIntoView");
		    // Call the routines that want to be called on layout changes
		    ssf_onLayoutChange();
		}
		var helpMenuAnchorDiv = document.getElementById('ss_helpMenuAnchor');
        if (helpMenuAnchorDiv != null) {
        	helpMenuAnchorDiv.style.visibility = "hidden";
        	helpMenuAnchorDiv.style.display = "none";
        }
		if (lightBox.style.visibility && lightBox.style.visibility == 'visible') {
    		for (var i = 1; i < ss_helpSystemNextNodeId; i++) {
    			// Delete all of the help spots that were added during the help
				// session
    			if (ss_helpSystemNodes[i] != null) {
    				bodyObj.removeChild(ss_helpSystemNodes[i]);
    				ss_helpSystemNodes[i] = null;
    			}
    		}
    		ss_helpSystemNextNodeId = 1;

    		// Delete all of the help panels that were added during the help
			// session
			for (var i = ss_helpSystemPanels.length; --i >= 0;) {
				// ss_debug("panelObj = " + ss_helpSystemPanels[i])
				var pObj = document.getElementById(ss_helpSystemPanels[i]);
				if (pObj != null && pObj.parentNode != null) pObj.parentNode.removeChild(pObj);
			}
    		// Delete all of the highlighted nodes
    		this.clearHighlights();
    		ss_showSpannedAreas();

    		dojo.fadeOut({node:lightBox, end: 0, delay:150, onEnd: function() {
    			var lightBox2 = document.getElementById('ss_help_light_box');
		    	lightBox.style.visibility = "hidden";
		    	lightBox.style.display = "none";
   		}}).play();
    		return
		}
	},
	
	showPanel : function(id, location) {
	},
	
	showHelpSpots : function() {
		this.clearTOC()
		var bodyObj = document.getElementsByTagName("body").item(0)
		var nodes = new Array();
		// var time = new Date().getTime();
		nodes = document.getElementsByTagName("ssHelpSpot");
		// ss_debug('Time: '+ parseInt(new Date().getTime() - time))
		for (var i = 0; i < nodes.length; i++) {
			// ss_debug(nodes[i].getAttribute("helpId") + " = " +
			// ss_helpSystemNextNodeId)
			var helpSpotNodeId = nodes[i].getAttribute("helpId");
			helpSpotNodeId = ss_helpSystemNextNodeId + "___" + helpSpotNodeId;
			var helpSpotTitle = "";
			if (nodes[i].getAttribute("title")) {
				helpSpotTitle = nodes[i].getAttribute("title");
			}
			// Get alignment values for ToC link
			var xAlignment = nodes[i].getAttribute("xAlignment");
	        if (!xAlignment) xAlignment = "";
	        var yAlignment = nodes[i].getAttribute("yAlignment");
	        if (!yAlignment) yAlignment = "";
		    if (helpSpotTitle != "") this.addTOC(helpSpotNodeId, helpSpotTitle, xAlignment, yAlignment);
			var helpSpotNode = document.createElement("div");
	        helpSpotNode.className = "ss_helpSpot";
	        helpSpotNode.style.zIndex = ssHelpSpotZ;
	        helpSpotNode.style.display = "block";
	        try {helpSpotNode.focus()} catch(e){};
			
			var helpSpotA = document.createElement("a");
			var helpSpotTable = document.createElement("table");
			var helpSpotTbody = document.createElement("tbody");
			var helpSpotTr = document.createElement("tr");
			var helpSpotTd1 = document.createElement("td");
			var helpSpotTd2 = document.createElement("td");
			var helpSpotGif = document.createElement("img");
			helpSpotGif.src = ss_helpSpotGifSrc;
			helpSpotGif.setAttribute("border", "0");
            // Title can be 'show' or 'hide' (default)
			if (nodes[i].getAttribute("titleFlag")) {
			    helpSpotTitleFlag = nodes[i].getAttribute("titleFlag");
			    if (helpSpotTitleFlag != 'hide') {
			        helpSpotTd2.setAttribute("nowrap", "nowrap");
			        helpSpotTd2.className = "ss_helpSpotTitle";
			    }
			} else {
			    helpSpotTitleFlag = 'hide'; 
			}
			var helpSpotTitle = document.createElement("nobr");
			var helpSpotTitleText = "";
			if (nodes[i].getAttribute("title") && helpSpotTitleFlag != 'hide') {
				helpSpotTitleText = nodes[i].getAttribute("title");
			}
			helpSpotTitle.appendChild(document.createTextNode(helpSpotTitleText));
			
			// Build a table containing the image and the title
			helpSpotNode.appendChild(helpSpotA);
			helpSpotA.appendChild(helpSpotTable);
			helpSpotTable.appendChild(helpSpotTbody);
			helpSpotTbody.appendChild(helpSpotTr);
			helpSpotTr.appendChild(helpSpotTd1);
			helpSpotTr.appendChild(helpSpotTd2);
			var aObj = document.createElement("a");
			aObj.setAttribute("href", "javascript: ss_helpSystem.showHelpSpotInfo('" + helpSpotNodeId + "', '" + xAlignment + "', '" + yAlignment + "');");
			// Associate link either to the text ('show' text) or the icon
			// ('hide' text)
			if (helpSpotTitleFlag != 'hide') {
			    helpSpotTd1.appendChild(helpSpotGif);
			    aObj.appendChild(helpSpotTitle);
			    helpSpotTd2.appendChild(aObj);
			} else {
			    aObj.appendChild(helpSpotGif);
			    helpSpotTd1.appendChild(aObj);
			}
			
			ss_helpSystemNodes[ss_helpSystemNextNodeId] = helpSpotNode;
	        helpSpotNode.setAttribute("id", "ss_help_spot" + ss_helpSystemNextNodeId);
	        helpSpotNode.setAttribute("helpId", helpSpotNodeId);
	        
	        var offsetY = nodes[i].getAttribute("offsetY");
	        if (!offsetY) offsetY = 0;
	        offsetY = parseInt(offsetY);
	        var top = parseInt(dojo.coords(nodes[i].parentNode, true).y + offsetY);
	        if (nodes[i].getAttribute("valign")) {
	        	if (nodes[i].getAttribute("valign") == "middle") {
	        		top += parseInt(ss_getObjectHeight(nodes[i]) / 2);
	        	} else if (nodes[i].getAttribute("valign") == "bottom") {
	        		top += ss_getObjectHeight(nodes[i]);
	        	}
	        }
	        var offsetX = nodes[i].getAttribute("offsetX");
	        if (!offsetX) offsetX = 0;
	        offsetX = parseInt(offsetX);
	        var left = parseInt(dojo.coords(nodes[i].parentNode, true).x + offsetX);
	        if (nodes[i].getAttribute("align")) {
	        	if (nodes[i].getAttribute("align") == "center") {
	        		left += parseInt(dojo.marginBox(nodes[i]).w / 2);
	        	} else if (nodes[i].getAttribute("align") == "right") {
	        		left += dojo.marginBox(nodes[i]).w;
	        	}
	        }
	        if (top < 0) top = 0;
	        if (left < 0) left = 0;
	        helpSpotNode.style.top = top + "px";
	        helpSpotNode.style.left = left + "px";
	        bodyObj.appendChild(helpSpotNode);
			ss_helpSystemNextNodeId++;
	        var owningDiv = nodes[i].parentNode
	        var okToShow = 1
	        while (owningDiv != null && owningDiv.tagName != null && owningDiv.tagName.toLowerCase() != 'body') {
	        	if (owningDiv.tagName.toLowerCase() == 'div') {
	        		var displayStyle = dojo.style(owningDiv, 'display')
	        		var positionStyle = dojo.style(owningDiv, 'position')
	        		if (displayStyle.toLowerCase() == 'none' || positionStyle.toLowerCase() == 'absolute') {
	        			okToShow = 0
	        			break
	        		}
	        	}
	        	owningDiv = owningDiv.parentNode
	        }
	        if (okToShow == 1) helpSpotNode.style.visibility = "visible";
			// ss_debug("nodes[i] width = "+dojo.marginBox(nodes[i]).w)
		}
	},
	
	getPositionLeft : function(obj) {
		var x = 0;
		switch(obj.getAttribute("positionX")) {
			case "left" : 
				x = ss_help_position_leftOffset
				break
			case "center" :
				x = parseInt((ss_getWindowWidth() - dojo.marginBox(obj).w) / 2)
				if (x < 0) x = 0;
				break
			case "right" :
				x = parseInt(ss_getWindowWidth() - dojo.marginBox(obj).w - ss_help_position_rightOffset)
				if (x < 0) x = 0;
			 	break
			default :
				x = parseInt((ss_getWindowWidth() - dojo.marginBox(obj).w) / 2)
				if (x < 0) x = 0;
		}
		return x;
	},
	
	getPositionTop : function(obj) {
		var y = 0;
		switch(obj.getAttribute("positionY")) {
			case "top" : 
				y = ss_help_position_topOffset
				break
			case "middle" :
				y = parseInt((ss_getWindowHeight() - ss_getObjectHeight(obj)) / 2)
				if (y < 0) y = 0;
				break
			case "bottom" :
				y = parseInt(ss_getWindowHeight() - ss_getObjectHeight(obj) - ss_help_position_bottomOffset)
				if (y < 0) y = 0;
			 	break
			default :
				y = parseInt((ss_getWindowHeight() - ss_getObjectHeight(obj)) / 2)
				if (y < 0) y = 0;
		}
		return y;
	},
	
	addTOC : function(id, title, xAlignment, yAlignment) {
		// ss_debug("addToc " + id + ", " + title)
		// Don't add nodes with duplicate titles.
		for (var i=0; i<ss_helpSystemTOC.length; i++) {
		    if (ss_helpSystemTOC[i].title == title) {
		    	return;
		    }
		}
		ss_helpSystemTOC[ss_helpSystemTOC.length] = {id: id, title: title, x: xAlignment, y: yAlignment};
	},
	
	clearTOC : function() {
		var tocDiv = document.getElementById('ss_help_toc');
		if (tocDiv) {
			var uls = tocDiv.getElementsByTagName("ul");
			for (var i = uls.length; --i >= 0;) {
				tocDiv.removeChild(uls[i]);
			}
		}
		ss_helpSystemTOCindex = -1;
		ss_helpSystemTOC = new Array();
		this.tocBuilt = false;
	},
	
	toggleTOC : function() {
		var tocDiv = document.getElementById('ss_help_toc');
		if (!tocDiv) return;
    	if (tocDiv.style.visibility == "visible") {
    		this.hideTOC();
    	} else {
    		this.showTOC();
    	}
	},
	
	showTOC : function() {
		var tocDiv = document.getElementById('ss_help_toc');
		if (!tocDiv) return;

		if (!this.tocBuilt) {
			var uls = tocDiv.getElementsByTagName("ul");
			var ulObj = null;
			if (uls == null || uls.length <= 0) {
				ulObj = document.createElement("ul");
				tocDiv.appendChild(ulObj);
			} else {
				ulObj = uls.item(0);
			}
		
			for (var i=0; i<ss_helpSystemTOC.length; i++) {
			    var t = ss_helpSystemTOC[i];
				var liObj = document.createElement("li");
				var aObj = document.createElement("a");
				aObj.setAttribute("href", "javascript: ss_helpSystem.hideTOC();ss_helpSystem.showHelpSpotInfo('" + t.id + "', '" + t.x + "', '" + t.y + "');");
				aObj.appendChild(document.createTextNode(t.title));
				liObj.appendChild(aObj);
				ulObj.appendChild(liObj);
			}
			this.tocBuilt = true;		
		}

    	tocDiv.style.visibility = "visible";
    	tocDiv.style.display = "block";
    	tocDiv.style.zIndex = parseInt(ssHelpWelcomeZ + 1);
    	tocDiv.focus();
	},
	
	hideTOC : function() {
		var tocDiv = document.getElementById('ss_help_toc');
		if (!tocDiv) return;
    	tocDiv.style.visibility = "hidden";
    	tocDiv.style.display = "none";
	},
	
	showNextHelpSpot : function(id, xAlignment, yAlignment) {
		if (xAlignment == null) xAlignment = "";
		if (yAlignment == null) yAlignment = "";
		ss_helpSystemTOCindex++;
		if (ss_helpSystemTOCindex < 0) ss_helpSystemTOCindex = 0;
		if (ss_helpSystemTOCindex >= ss_helpSystemTOC.length) ss_helpSystemTOCindex = 0;
		if (ss_helpSystemTOCindex < ss_helpSystemTOC.length) {
			this.hideTOC();
			this.showHelpSpotInfo(ss_helpSystemTOC[ss_helpSystemTOCindex].id, xAlignment, yAlignment);
		}
	},
	
	showPreviousHelpSpot : function(id, xAlignment, yAlignment) {
		if (xAlignment == null) xAlignment = "";
		if (yAlignment == null) yAlignment = "";
		ss_helpSystemTOCindex--;
		if (ss_helpSystemTOCindex < 0) ss_helpSystemTOCindex = ss_helpSystemTOC.length-1;
		if (ss_helpSystemTOCindex >= ss_helpSystemTOC.length) ss_helpSystemTOCindex = 0;
		if (ss_helpSystemTOCindex >= 0 && ss_helpSystemTOCindex < ss_helpSystemTOC.length) {
			this.hideTOC();
			this.showHelpSpotInfo(ss_helpSystemTOC[ss_helpSystemTOCindex].id, xAlignment, yAlignment);
		}
	},
	
	showHelpSpotInfo : function(id, xAlignment, yAlignment) {
        // Hide moreinfo panel, if exists
        ss_hideDiv('ss_moreinfo_panel')
		if (xAlignment == null) xAlignment = "";
		if (yAlignment == null) yAlignment = "";
		// ss_debug('showHelpSpotInfo id = '+id)
		this.hideTOC();
		for (var i = 0; i < ss_helpSystemTOC.length; i++) {
			if (id == ss_helpSystemTOC[i].id) {
				ss_helpSystemTOCindex = i;
				break;
			}
		}
		// Find the help spot node
		var helpSpot = null;
		for (var i = 0; i < ss_helpSystemNodes.length; i++) {
			if (ss_helpSystemNodes[i] != null) {
				var helpId = ss_helpSystemNodes[i].getAttribute("helpId");
				var org_helpId = helpId;
				var i1 = helpId.indexOf("___");
				if (i1 >= 0) helpId = helpId.substr(helpId.indexOf("___") + 3);
				if (org_helpId == id || helpId == id) {
					helpSpot = ss_helpSystemNodes[i];
					break;
				}
			}
		}
		// ss_debug("showHelpSpotInfo helpSpot: " + helpSpot)
		if (helpSpot != null) {
		    var top = parseInt(dojo.coords(helpSpot, true).y);
		    var left = parseInt(dojo.coords(helpSpot, true).x);
		    var width = parseInt(dojo.contentBox(helpSpot).w);
		    var height = parseInt(dojo.contentBox(helpSpot).h);
		    var x = parseInt(left + 3);
		    var y = parseInt(top + height - 8);
			this.showHelpPanel(id, "ss_help_panel", x, y, xAlignment, yAlignment)
			dijit.scrollIntoView(helpSpot);
		}
	},
	
	showMoreInfoPanel : function(id, panelId) {
	    y = parseInt(ss_getDivTop('ss_help_panel') - 25);
	    x = parseInt(ss_getDivLeft('ss_help_panel') + 25);
	    this.showHelpPanel(id, panelId, x, y);
	},

	showInlineHelpSpotInfo : function(helpSpot, jspId, tagId, dx, dy, xAlignment, yAlignment) {
		if (dx == "") dx = 0;
		if (dy == "") dy = 0;
		if (xAlignment == null) xAlignment = "";
		if (yAlignment == null) yAlignment = "";
		// ss_debug('showInlineHelpSpotInfo jspId = '+jspId)
		if (helpSpot != null) {
		    var top = parseInt(dojo.coords(helpSpot, true).y);
		    var left = parseInt(dojo.coords(helpSpot, true).x);
		    var x = parseInt(left + 3 + parseInt(dx));
		    var y = parseInt(top + 3 + parseInt(dy));
			this.showHelpPanel(jspId, "ss_help_panel", x, y, xAlignment, yAlignment, tagId)
		}
	},

	showHelp : function( helpUrl )
	{
		window.open( helpUrl, "teaming_help_window", "resizeable,scrollbar" );
	},

	toggleShowHelpCPanel: function () {
		if (dojo.style(document.getElementById("ss_help_welcome_panel_body"), "display") != 'none') {
			this.recordShowHelpCPanel("hidden");
			ss_setClass("ss_help_cpanel_show_control", "ss_help_cpanel_hide");
			dojo.style(document.getElementById("ss_help_welcome_panel_body"), "display", "none")
		} else {
			this.recordShowHelpCPanel("visible");
			ss_setClass("ss_help_cpanel_show_control", "ss_help_cpanel_show");
			dojo.style(document.getElementById("ss_help_welcome_panel_body"), "display", "block")
		}		
	},

	recordShowHelpCPanel : function (visible) {
		var url;	
		if (visible == "visible") {
			url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"show_help_cpanel"});
		} else {
			url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"hide_help_cpanel"});
		}
		ss_fetch_url(url);	
	
	},
	
	showHelpPanel : function(id, panelId, x, y, xAlignment, yAlignment, tagId) {
		if (tagId == null) tagId = "";
		if (xAlignment == null) xAlignment = "";
		if (yAlignment == null) yAlignment = "";
		//ss_debug('id='+id+', panelId='+panelId+', x = '+x+', y = '+y+', xAlign = '+xAlignment)
		if (ss_helpSystemRequestInProgress == 1) {
			ss_helpSystemQueuedId = id;
			ss_helpSystemQueuedPanelId = panelId;
			ss_helpSystemQueuedX = x;
			ss_helpSystemQueuedY = y;
			return;
		}
		ss_setupStatusMessageDiv()
		this.clearHighlights();
		// ss_debug("showHelpPanel " + id)
		var pObj = self.document.getElementById(panelId);
		var startTop = -1;
		var startLeft = -1;
		var startVisibility = "";
		if (!pObj) {
			// There is no help panel, so create it on-the-fly
			var bodyObj = document.getElementsByTagName("body").item(0)
			pObj = document.createElement("div");
	        pObj.setAttribute("id", panelId);
	        pObj.setAttribute("helpId", id);
	        pObj.className = "ss_helpPanel ss_popup_panel_outer";
	        pObj.style.zIndex = ssHelpPanelZ;
	        bodyObj.appendChild(pObj);
	        ss_helpSystemPanels[ss_helpSystemPanels.length] = panelId;
		} else {
			// See if this is a request for the same panel. If so, toggle it
			// off.
			// ss_debug("id = " + pObj.getAttribute("id") + ", helpId = " +
			// pObj.getAttribute("helpId"))
			if (pObj.getAttribute("helpId") == id && pObj.style.visibility == "visible") {
				// On the second click to the same help spot, turn the panel off
				pObj.style.visibility = "hidden"
				pObj.style.display = "none"
				return
			}
			startTop = parseInt(dojo.coords(pObj, true).y);
			startLeft = parseInt(dojo.coords(pObj, true).x);
			if (pObj.style && pObj.style.visibility) 
					startVisibility = pObj.style.visibility;
		}
		
		var orgHelpId = id;
		// See if this is the actual name of the help panel
		var i1 = id.indexOf("___");
		if (i1 >= 0) orgHelpId = id.substr(id.indexOf("___") + 3);
		var urlParams = {operation:"show_help_panel", operation2:orgHelpId, tagId:tagId}; 
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
		var callbackParams = {id: id, panelId:panelId, x:x, y:y, xAlignment:xAlignment,
								yAlignment:yAlignment, startTop:startTop, startLeft:startLeft,
								startVisibility: startVisibility};

		var bindArgs = {
	    	url: url,
			error: function(err) {
				alert(ss_not_logged_in);
			},
			load: function(data) {
	  		  try {
	  		  	dojo.byId(panelId).innerHTML = data;
	  		    ss_helpSystem.postShowPanel(callbackParams);
		      } catch (e) {alert(e);}
			},
			preventCache: true,				
			handleAs: "text",
			method: "get"
		};   
		dojo.xhrGet(bindArgs);

	},
	
	postShowPanel : function(data) {
		panelId = data.panelId;
		var pObj = self.document.getElementById(panelId);
		pObj.setAttribute("helpId", data.id);
		pObj.style.display = "block"
		try {
			pObj.focus();
		} catch(e) {}
		var width = parseInt(dojo.marginBox(pObj).w);
		var height = parseInt(dojo.marginBox(pObj).h);
		var x = data.x;
		var y = data.y;
		var xAlignment = data.xAlignment;
		var yAlignment = data.yAlignment;
		var startTop = data.startTop;
		var startLeft = data.startLeft;
		var startVisibility = data.startVisibility;
		var top = 0;
		var left = 0;
		if (!isNaN(parseInt(x)) && !isNaN(parseInt(y))) {
			top = parseInt(y);
			left = parseInt(x);
		} else if (x == "" || y == "") {
		    top = startTop;
		    left = startLeft;
		} else {
			switch(x) {
				case "left" : 
					left = ss_help_position_leftOffset;
					break
				case "center" :
					left = parseInt((ss_getWindowWidth() - width) / 2)
					if (left < ss_help_position_leftOffset) left = ss_help_position_leftOffset;
					break
				case "right" :
					left = parseInt(ss_getWindowWidth() - width - ss_help_position_rightOffset)
				 	if (left < ss_help_position_leftOffset) left = ss_help_position_leftOffset;
				 	break
				default :
					left = parseInt((ss_getWindowWidth() - width) / 2)
					if (left < ss_help_position_leftOffset) left = ss_help_position_leftOffset;
			}
			switch(y) {
				case "top" : 
					top = ss_help_position_topOffset;
					break
				case "middle" :
					top = parseInt((ss_getWindowHeight() - height) / 2);
					if (top < ss_help_position_topOffset) top = ss_help_position_topOffset;
					break
				case "bottom" :
					top = parseInt(ss_getWindowHeight() - ss_help_position_bottomOffset - height)
				 	if (top < ss_help_position_topOffset) top = ss_help_position_topOffset;
				 	break
				default :
					top = parseInt(ss_getWindowHeight() - ss_help_position_bottomOffset - height)
					if (top < ss_help_position_topOffset) top = ss_help_position_topOffset;
			}
		}
		switch(xAlignment) {
			case "left" : 
				left = parseInt(left - width - ss_helpSystemPanelMarginOffset);
				break
			case "center" : 
				left = parseInt(left - width/2 - ss_helpSystemPanelMarginOffset);
				break
			case "right" : 
				left = parseInt(left - ss_helpSystemPanelMarginOffset);
				break
		}
		switch(yAlignment) {
			case "bottom" : 
				top = parseInt(top - height - ss_helpSystemPanelMarginOffset);
				break
			case "middle" : 
				top = parseInt(top - height/2 - ss_helpSystemPanelMarginOffset);
				break
			case "top" : 
				top = parseInt(top - ss_helpSystemPanelMarginOffset);
				break
		}
		var windowWidth = parseInt(ss_getWindowWidth());
		if (parseInt(left + width) > windowWidth - ss_help_position_rightOffset) {
			left = parseInt(windowWidth - width - ss_help_position_rightOffset);
		}
		if (left < ss_help_position_leftOffset) left = ss_help_position_leftOffset;
		
		if (startTop >= 0 && startLeft >= 0 && startVisibility == "visible") {
			pObj.style.top = startTop + "px";
			pObj.style.left = startLeft + "px";
			dojo.fx.slideTo({node:panelId, top: top, left: left, duration:300}).play();
		} else {
			pObj.style.top = top + "px";
			pObj.style.left = left + "px";
		}
	    pObj.style.zIndex = ssHelpPanelZ;
		pObj.style.visibility = "visible";
		
		ss_helpSystemRequestInProgress = 0;
		
		// Is there another request queued?
		if (ss_helpSystemQueuedId != "") {
			// ss_debug("\nLaunching queued request to show " +
			// ss_helpSystemQueuedId)
			var id = ss_helpSystemQueuedId;
			var panelId = ss_helpSystemQueuedPanelId;
			var x = ss_helpSystemQueuedX;
			var y = ss_helpSystemQueuedY;
			ss_helpSystemQueuedId = "";
			ss_helpSystemQueuedPanelId = "";
			ss_helpSystemQueuedX = "";
			ss_helpSystemQueuedY = "";
			ss_helpSystem.showHelpPanel(id, panelId, x, y);
		}
	},

	hideHelpPanel : function(obj) {
	    while (obj && obj.parentNode) {
	        var n = obj.parentNode;
	    	if (dojo.hasClass(n, "ss_popup_panel_outer")) {
	    		if (n.id) {
					ss_hideDiv(n.id);
					break;
				}
			}
			obj = n;
		}
	},
	
	highlight : function(id) {
		// ss_debug("Highlight " + id)
		var obj = document.getElementById(id);
		if (obj != null) {
			ss_helpSystemHighlights[ss_helpSystemHighlights.length] = id;
			ss_helpSystemHighlightsBorder[id] = obj.style.border;
			obj.style.border = "red 2px solid";
			if (ss_helpSystemHighlightsBorderTimer[id]) 
			    clearTimeout(ss_helpSystemHighlightsBorderTimer[id]);
			ss_helpSystemHighlightsBorderTimer[id] = setTimeout("ss_helpSystem.blinkHighlight('"+id+"', 4)", 200);
		}
	},
	
	clearHighlights : function() {
		for (var i = ss_helpSystemHighlights.length; --i >= 0;) {
			// ss_debug("highlightObj = " + ss_helpSystemHighlights[i])
			var id = ss_helpSystemHighlights[i];
			var hObj = document.getElementById(id);
		    if (hObj != null) {
			    hObj.style.border = ss_helpSystemHighlightsBorder[id];
			    ss_helpSystemHighlights[i] = null;
			    if (ss_helpSystemHighlightsBorderTimer[id]) {
				    clearTimeout(ss_helpSystemHighlightsBorderTimer[id])
				    ss_helpSystemHighlightsBorderTimer[id] = null;
			    }
			}
		}
		ss_helpSystemHighlights = new Array();
	},
	
	blinkHighlight : function(id, count) {
		// ss_debug("blinkHighlight " + id)
		if (ss_helpSystemHighlightsBorderTimer[id] != null) {
			// ss_debug("clearTimeout " + id)
			clearTimeout(ss_helpSystemHighlightsBorderTimer[id])
			ss_helpSystemHighlightsBorderTimer[id] = null;
		} else {
			// There is no timer value. The user must have moved on. Don't blink
			// any more.
			// ss_debug("Stopped blinking!")
			return;
		}
		var obj = document.getElementById(id);
		// ss_debug(" border color: " + obj.style.borderTopColor)
		if (obj != null) {
		    if (obj.style.borderTopColor == "red") {
			    obj.style.border = "white 2px solid";
		    } else {
			    obj.style.border = "red 2px solid";
		    }
		    if (count-- >= 0) {
			    ss_helpSystemHighlightsBorderTimer[id] = setTimeout("ss_helpSystem.blinkHighlight('"+id+"', "+count+")", 200);
		    }
		}
	},
	
	outputHelpWelcomeHtml : function() {
		var undefined;
		if (!ss_helpSpotGifSrc || ss_helpSpotGifSrc == undefined || ss_helpSpotGifSrc == "undefined") {
			var s = "";	
			s += "<div id=\"ss_help_welcome\" class=\"ss_style ss_helpWelcome ss_popup_panel_outer\" \n";
			s += "  positionX=\"center\" positionY=\"top\" align=\"center\"></div>\n";
			document.writeln(s);
		}
	}
}

// Dashboard routines

function ss_addDashboardComponents(divId) {
	var panel = document.getElementById(divId);
	// undefined for some shared access
	if (panel == null) return;
	ss_moveObjectToBody(panel);
	panel.style.zIndex = parseInt(ssLightboxZ + 1);
	ss_activateMenuLayer(divId, null, null, null, "popup")
}

function ss_showHideAllDashboardComponents(obj, divId, binderId) {
	var formObj = ss_getContainingForm(obj)
	var urlParams = {binderId:binderId};
	var canvas = document.getElementById(divId);
	if (canvas && canvas.style && canvas.style.visibility == 'visible') {
		urlParams.operation="hide_all_dashboard_components";
	    obj.innerHTML = "<span><img src='"+ss_imagesPath+"/icons/dashboard_show.gif' title='"+ss_componentTextShow+"'></span>";
		canvas.style.visibility = 'hidden';
		canvas.style.display = 'none';
	} else if (canvas && canvas.style) { 
		urlParams.operation="show_all_dashboard_components";
	    obj.innerHTML = "<span><img src='"+ss_imagesPath+"icons/dashboard_hide.gif' title='"+ss_componentTextHide+"'></span>";
		canvas.style.visibility = 'visible';
		canvas.style.display = 'block';
		canvas.focus();
	}
	ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_dashboard"));
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	
}

function ss_toggle_dashboard_toolbars(prefix) {
	var toolbarOption = document.getElementById(prefix + "_dashboard_menu_content");
	var count = 0;
	eval("count = " + prefix + "_toolbar_count;");
	for (var i = 0; i < count; i++) {
		var obj = document.getElementById(prefix + "_dashboard_toolbar_"+i)
		if (obj.style.visibility == 'hidden') {
			obj.style.visibility = 'visible';
			obj.style.display = 'inline';
			obj.style.zIndex = parseInt(ssLightboxZ + 1);
			obj.focus();
			if (toolbarOption) toolbarOption.innerHTML = ss_toolbarHideContent;
			// var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
			// lightBox.onclick = function(e)
			// {ss_toggle_dashboard_toolbars(prefix);};
		} else {
			obj.style.visibility = 'hidden';
			obj.style.display = 'none';
			if (toolbarOption) toolbarOption.innerHTML = ss_toolbarAddContent;
			// ss_hideLightbox()
		}
	}
	
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}
function ss_toggle_dashboard_hidden_controls(prefix) {
	var toolbarOption = document.getElementById(prefix + "_dashboard_menu_controls");
	var count = 0;
	eval("count = " + prefix + "_dashboard_control_count;");
	for (var i = 0; i < count; i++) {
		var obj = document.getElementById(prefix + "_dashboard_control_"+i);
		if (obj.style.visibility == 'hidden') {
			obj.style.visibility = 'visible';
			obj.style.display = 'block';
			obj.focus();
			if (toolbarOption) toolbarOption.innerHTML = ss_toolbarHideControls;
		} else {
			obj.style.visibility = 'hidden';
			obj.style.display = 'none';
			if (toolbarOption) toolbarOption.innerHTML = ss_toolbarShowControls;
		}
	}
	eval("count = " + prefix + "_dashboard_border_count");
	for (var i = 0; i < count; i++) {
		var obj = document.getElementById(prefix + "_dashboard_border_"+i);
		if (obj.className && obj.className != "") {
			eval (prefix + "_dashboard_border_classNames[i] = obj.className;");
			obj.className = "";
		} else {
			eval ("if (" + prefix + "_dashboard_border_classNames[i]) obj.className = " + prefix + "_dashboard_border_classNames[i];");
		}
	}
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

var ss_dashboardCallbacks = new Object();

function ss_addDashboardEvent(componentId, 
						when /*
								 * onBeforeShow, onAfterShow == onShow,
								 * onBeforeHide, onAfterHide == onHide
								 */,
						routineToCall) {
	if (when == "onShow") {
		when = "onAfterShow";
	} else if (when == "onHide") {
		when = "onAfterHide";
	}
	
	//alert("ss_addDashboardEvent(componentId=" + componentId + ", when=" + when);
	
	if (!window.ss_dashboardCallbacks) {
		ss_dashboardCallbacks = new Object();
	}
	
	if (!ss_dashboardCallbacks[componentId]) {
		ss_dashboardCallbacks[componentId] = new Object();
	}
	
	if (!ss_dashboardCallbacks[componentId][when]) {
		ss_dashboardCallbacks[componentId][when] = new Array();
	}

	ss_dashboardCallbacks[componentId][when].push(routineToCall);
}

function ss_callDashboardEvent(componentId, 
						when /*
								 * onBeforeShow, onAfterShow == onShow,
								 * onBeforeHide, onAfterHide == onHide
								 */) {
	if (when == "onShow") {
		when = "onAfterShow";
	} else if (when == "onHide") {
		when = "onAfterHide";
	}
	
	//alert("ss_callDashboardEvent(componentId=" + componentId + ", when=" + when);
	
	if (!window.ss_dashboardCallbacks) {
		return false;
	}
	
	if (!ss_dashboardCallbacks[componentId]) {
		return false;
	}
	
	if (!ss_dashboardCallbacks[componentId][when]) {
		return false;
	}

	for (var i = 0; i < ss_dashboardCallbacks[componentId][when].length; i++) {
		ss_dashboardCallbacks[componentId][when][i]();
	}
	
	return true;
}

function ss_showHideDashboardComponent(obj, componentId, divId, idStr, namespace, scope) {
	// ss_debug(obj.alt + ", " + obj.src)
	var formObj = ss_getContainingForm(obj);
	var urlParams = {namespace:namespace,rn:ss_dbrn++};
	
	if (componentId != "") {urlParams.operation2=componentId;}
	if (formObj._dashboardList && formObj._dashboardList.value != "") {
		urlParams._dashboardList=formObj._dashboardList.value;
	}
	if (scope != "") {
		urlParams._scope = scope;
	} else if (formObj._scope && formObj._scope.value != "") {
		urlParams._scope=formObj._scope.value;
	}
	var callbackRoutine = ""
	var imgObj = obj.getElementsByTagName('img').item(0);
	if (imgObj.src.match(/accessory_show.gif/)) {
		urlParams.operation="show_component";
	    callbackRoutine = ss_showComponentCallback;
	    imgObj.src = ss_componentSrcHide;
	    imgObj.alt = ss_componentAltHide;
		var targetDiv = document.getElementById(divId);
		if (targetDiv) {
			borderDiv = targetDiv.parentNode;
			if (borderDiv.className == 'ss_content_window_content_off') borderDiv.className = 'ss_content_window_content';
		}
	    ss_callDashboardEvent(componentId, "onBeforeShow");
	} else if (imgObj.src.match(/accessory_hide.gif/)) {
		urlParams.operation="hide_component";
	    callbackRoutine = ss_hideComponentCallback;
	    imgObj.src = ss_componentSrcShow;
	    imgObj.alt = ss_componentAltShow;
		var targetDiv = document.getElementById(divId);
		if (targetDiv) {
			targetDiv.innerHTML = "";
			targetDiv.style.visibility = "hidden";
			targetDiv.style.display = "none";
			borderDiv = targetDiv.parentNode;
			if (borderDiv.className == 'ss_content_window_content') borderDiv.className = 'ss_content_window_content_off';
			// Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
		ss_callDashboardEvent(componentId, "onBeforeHide");
	} else if (imgObj.className.match(/ss_accessory_delete/)) {
		urlParams.operation="delete_component";
	    callbackRoutine = ss_hideComponentCallback;
		var targetDiv = document.getElementById(divId);
		if (targetDiv) {
			targetDiv.innerHTML = "";
			targetDiv.style.visibility = "hidden";
			targetDiv.style.display = "none";
			// Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
		ss_addDashboardEvent(componentId, "onAfterHide", ss_deleteComponentCallback)
		ss_callDashboardEvent(componentId, "onBeforeHide");
	}
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_dashboard");
	url += "\&" + idStr;
	if (callbackRoutine != "") ss_post_to_url(url, "", callbackRoutine, {divId:divId, componentId:componentId});
}
function ss_showComponentCallback(s, data) {
	// data = {"divId" : divId, "componentId" : componentId}
	ss_debug(s)
	var targetDiv = document.getElementById(data.divId);
	if (targetDiv) {
		targetDiv.innerHTML = s;
		targetDiv.style.visibility = "visible";
		targetDiv.style.display = "block";
		targetDiv.focus();
		// Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
		ss_callDashboardEvent(data.componentId, "onAfterShow");
		ss_showEmailLinks();
		
		ss_executeJavascript(targetDiv, true);	// true -> Global scope the eval()'s.
	}
}
function ss_hideComponentCallback(s, data) {
	// data = {"divId" : divId, "componentId" : componentId}
	ss_callDashboardEvent(data.componentId, "onAfterHide");
}

function ss_deleteComponentCallback() {
	// When a dashboard compopnent is deleted, we now perform a full
	// reload of the topmost window location.
	//
	// In the JSP days, we simply refreshed the content area.  With
	// the GWT views, since they DON'T run in an <IFRAME>, we must make
	// sure every vestiage of the component gets removed, including
	// those calls added to the JavaScript onLoad handlers, ...
	setTimeout(window.top.ss_windowLocationReload(), 100);
}

function ss_confirmDeleteComponent(obj, componentId, divId, divId2, idStr, namespace, scope) {
	var formObj = ss_getContainingForm(obj)
	var confirmText = "";
	if (formObj._scope.value == "local") {
		confirmText = ss_dashboardConfirmDeleteLocal;
	} else if (formObj._scope.value == "global") {
		confirmText = ss_dashboardConfirmDeleteGlobal;
	} else if (formObj._scope.value == "binder") {
		confirmText = ss_dashboardConfirmDeleteBinder;
	} else {
		confirmText = ss_dashboardConfirmDeleteUnknown;
	}
	var confirmText2 = ss_dashboardConfirmDelete;
	if (!confirm(confirmText + "\n" + confirmText2)) return false;
	ss_showHideDashboardComponent(obj, componentId, divId, idStr, namespace, scope)
	if (divId2 && document.getElementById(divId2)) {
		ss_hideDiv(divId2)
	}
	return true;
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

function ss_submitDashboardChange(obj, op) {
	var formObj = ss_getContainingForm(obj)
	formObj._operation.value = op;
	formObj.submit();
}

function ss_hideDashboardMenu(obj) {
	var formObj = ss_getContainingForm(obj)
	ss_hideDiv(formObj.parentNode.id)
}

function ss_moreDashboardSearchResults(binderId, pageNumber, pageSize, namespace, divId, componentId, displayType) {
	var urlParams = {binderId:binderId, operation:"search_more", operation2:componentId, divId:divId, namespace:namespace,
						pageNumber:pageNumber, pageSize:pageSize, displayType:displayType};
	ss_fetch_div(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_dashboard"), divId, "true");
}

function ss_moreTeamMembers(binderId, pageNumber, pageSize, namespace, divId, componentId) {
	var urlParams = {binderId:binderId, operation:"team_more", operation2:componentId, divId:divId, namespace:namespace, pageNumber:pageNumber,
					pageSize:pageSize};
	ss_fetch_div(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_dashboard"), divId, "true");
}


// Start: Add Attachment Related Functions

// Browse Related Functions


var browseURL = "";
var browseHideAttachment = "";
var browseHideAttachmentAndAjax = "";


function setURLInIFrame(binderId, entryId, namespace) {
	var urlParams = {operation:"add_files_by_browse_for_entry", binderId:binderId, entryId:entryId};
	browseURL = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "add_entry_attachment");
	browseHideAttachment = "ss_hideAddAttachmentBrowse('"+ entryId + "', '" + namespace + "')";
	browseHideAttachmentAndAjax = 'ss_hideAddAttachmentBrowseAndAJAXCall("'+ binderId +'", "'+ entryId + '", "' + namespace + '", "strErrorMessage")';
}

function ss_showAddAttachmentBrowse(binderId, entryId, namespace) {
	// alert("Inside ss_showAddAttachmentBrowse...");

	ss_hideAddAttachmentDropbox(entryId, namespace);
	
	setURLInIFrame(binderId, entryId, namespace);
	
	var divId = 'ss_div_browse' + entryId + namespace;
	var divObj = document.getElementById(divId);

	var frameId = 'ss_iframe_browse' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	// alert("ss_showAddAttachmentBrowse: frameObj.src: "+frameObj.src);
	
//!	frameObj.src = ss_rootPath + "js/attachments/entry_attachment_browse.html";
	ss_setUrlInFrame(frameObj, ss_rootPath + "js/attachments/entry_attachment_browse.html");
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";
	
	// divObj.style.width = "360px";
	// divObj.style.height = "150px";
	
	try {
		if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	} catch(e) {}
}

function ss_hideAddAttachmentBrowse(entryId, namespace) {
	var divId = 'ss_div_browse' + entryId + namespace;
	var divObj = document.getElementById(divId);
	ss_hideDivNone(divId);
	divObj.style.display = "none";

	try {
		if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	} catch(e) {}
}

function ss_hideAddAttachmentBrowseAndAJAXCall(binderId, entryId, namespace, strErrorMessage) {
	if (strErrorMessage != "") {
		alert(strErrorMessage);
	}

	ss_hideAddAttachmentBrowse(entryId, namespace);
	ss_selectEntryAttachmentAjax(binderId, entryId, namespace);
}

function ss_selectEntryAttachmentAjax(binderId, entryId, namespace) {
	if (self.location.href.indexOf("/action/") > 0) {
		ss_setSelfLocation(self.location.href + "/ss_showCommentsAttachmentsTab/viewAttachments");
	} else {
		ss_setSelfLocation(self.location.href + "&ss_showCommentsAttachmentsTab=viewAttachments");
	}
	return;
}


// Dropbox Functionality
function ss_hideAddAttachmentDropbox(entryId, namespace) {
	var divId = 'ss_div_dropbox' + entryId + namespace;
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);

	try {
		if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	} catch(e) {}
}

function ss_hideAddAttachmentDropboxAndAJAXCall(binderId, entryId, namespace) {
	ss_hideAddAttachmentDropbox(entryId, namespace);
	//self.location.reload(true);
	ss_selectEntryAttachmentAjax(binderId, entryId, namespace);
}

function ss_showAddAttachmentDropbox(binderId, entryId, namespace) {
	ss_hideAddAttachmentBrowse(entryId, namespace);

	var urlParams = {binderId:binderId, entryId:entryId, operation:"add_attachment_options", namespace:namespace};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
	
	var divId = 'ss_div_dropbox' + entryId + namespace;
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_dropbox' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	if (frameObj.src == "" || frameObj.src.indexOf("null.html") >= 0) {
//!		frameObj.src = url;
		ss_setUrlInFrame(frameObj, url);
	}
	
	divObj.style.width = "400px";

	try {
		if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	} catch(e) {}
}

function escapeAppletFileURL(s) {
    var n = s;
    n = n.replace(/#/g, "%23");
    n = n.replace(/&/g, "%26");
    return n;
}

var editClicked = "false";
function ss_openWebDAVFileOld(binderId, entryId, namespace, OSInfo, strURLValue) {
	var escapedURL = escapeAppletFileURL(strURLValue);
	escapedURL = ss_replaceSubStrAll(escapedURL, "+", "%2B");

	var urlParams ={binderId:binderId, entryId:entryId, operation:"open_webdav_file",
						namespace:namespace, ssOSInfo:OSInfo, ssEntryAttachmentURL:escapedURL};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);

	var divId = "ss_div_fileopen" + namespace;
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_fileopen' + namespace;
	var frameObj = document.getElementById(frameId);
	
	editClicked = "true";
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

//!	frameObj.src = url;
	ss_setUrlInFrame(frameObj, url);
	
	if (divObj != null) {
		divObj.style.width = "1px";
		divObj.style.height = "1px";
	}
}

function ss_openWebDAVFile(binderId, entryId, namespace, OSInfo, fileId) {
	var urlParams ={binderId:binderId, entryId:entryId, operation:"open_webdav_file_by_fileid",
						namespace:namespace, ssOSInfo:OSInfo, fileId:fileId};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
    
	var divId = "ss_div_fileopen" + namespace;
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_fileopen' + namespace;
	var frameObj = document.getElementById(frameId);
	if (frameObj == null) {
		alert("The edit function is broken in ss_openWebDAVFile.  The iframe named ss_iframe_fileopen"+namespace+" is missing.");
		return;
	}
	
	editClicked = "true";
	
	if (divObj != null) divObj.style.visibility = "visible";
	frameObj.style.visibility = "visible";

//!	frameObj.src = url;
	ss_setUrlInFrame(frameObj, url);
}

function ss_checkEditClicked(entryId, namespace) {
	return editClicked;
}

function ss_resetEditClicked(entryId, namespace) {
	editClicked = "false";
}


// End: Add Attachment Related Functions

// Title link Related Functions
function ss_highlightLine(id, namespace) {
	if (id == "") return false;
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	if (window.ss_highlightLineById) {
		ss_highlightLineById(folderLine);
		if (window.swapImages && window.restoreImages) {
			restoreImages(id);
		}
	}
}
function ss_loadEntryFromMenu(obj, id, binderId, entityType, namespace, isDashboard, isFile) {
	if (typeof window.top.ss_clearEntryHeaderBar != "undefined") {
		window.top.ss_clearEntryHeaderBar();
	}
	ss_highlightLine(id, namespace);
	if (isFile == "yes") {
		self.window.open(obj.href, '_blank');
		return false;
	}
	
	ss_hideSunburst(id, binderId);
	ss_showForumEntry(obj.href, isDashboard);
	return false;
}

// Initialize the ss_folder div
function ss_initShowFolderDiv(namespace) {
	var divObj = self.document.getElementById("ss_showfolder");
	if (divObj != null) return;
	// It hasn't been set up yet, use this first div to be defined
	divObj = self.document.getElementById("ss_showfolder"+namespace);
	if (divObj == null) return;
	divObj.id = "ss_showfolder";
}

// Initialize the floating div for viewing entries
function ss_showEntryDivInitialization(namespace) {
	var divObj = self.document.getElementById("ss_showentrydiv");
	if (divObj != null) return;
	// See if there is a window iframe with this name already
	if (typeof window.ss_showentryframe != "undefined") return;
	
	// It hasn't been set up yet, use this prototype div
	divObj = self.document.getElementById("ss_showentrydiv"+namespace);
	var iframeObj = self.document.getElementById("ss_showentryframe"+namespace);
	var boxObj = self.document.getElementById("ss_iframe_box_div"+namespace);
	var holderObj = self.document.getElementById("ss_iframe_holder_div"+namespace);
	var formObj = self.document.getElementById("ss_saveEntryWidthForm"+namespace);
	if (divObj == null || iframeObj == null) return;
	iframeObj.id = "ss_showentryframe";
	iframeObj.name = "ss_showentryframe";
	divObj.id = "ss_showentrydiv";
	if (formObj != null) formObj.id = "ss_saveEntryWidthForm";
	if (boxObj != null) boxObj.id = "ss_iframe_box_div";
	if (holderObj != null) holderObj.id = "ss_iframe_holder_div";
}

function ss_hideEntryDivOnLoad() {
	// alert(window.name)
	if (window.name == '' || 
			window.name == 'gwtContentIframe' || 
			window.name == 'adminContentControl') {
		ss_hideEntryDiv();
		// Make sure the content div is visible
		ss_showDiv('contentControl');
	}
}
function ss_hideEntryDiv() {
	// Are we running in the GWT UI?
	if (ss_isGwtUIActive) {
		// Yes! Then we need see if the entry DIV is off window.top.
		// Note that there appear to be two of these now, one that's
		// in window.top.document and one that's in self.document
		// (which would be the GWT UI content IFRAME.)
	    ss_hideEntryDivImpl(window.top.document.getElementById('ss_showentrydiv'));
	    ss_showContentFrame();
	}
	
	// The remainder of this code is unchanged from what was here
	// BEFORE the GWT UI was implemented.
    var wObj1 = self.document.getElementById('ss_showentrydiv')
    if (wObj1 != null) {
    	ss_hideEntryDivImpl(wObj1);
    }
    ss_showSpannedAreas();
}

// Pops the View Details URL from the GWT ContentControl's content
// history.
function ss_popEntryUrl() {
	// If the history popping method is defined...
	if (window.top.ss_popUrlFromContentHistory) {
		// ...call it.
		window.top.ss_popUrlFromContentHistory();
	}
}

function ss_hideEntryDivImpl(eDIV) {
	if (eDIV != null) {
    	eDIV.style.visibility = "hidden";
    	eDIV.style.display = "none";
	}
}

// Routine to show the content frame
function ss_showContentFrame() {
	if (self != self.parent && typeof self.parent.ss_showContentFrame != "undefined") {
		self.parent.ss_showContentFrame();
		return;
	}
	ss_showDiv("contentControl", "no");
}

function ss_loadEntry(obj, id, binderId, entityType, namespace, isDashboard) {
	ss_hideSunburst(id, binderId);
	return ss_loadEntryUrl(obj.href, id, binderId, entityType, namespace, isDashboard);
}
function ss_loadEntryUrl(url, id, binderId, entityType, namespace, isDashboard) {
	ss_hideSunburst(id, binderId);
	if (ss_getUserDisplayStyle() == "accessible") {
		ss_setSelfLocation(url);
		return false;
	}
	
	ss_highlightLine(id, namespace);

	if (typeof ss_showAsWiki != "undefined" && ss_showAsWiki) {
		ss_setSelfLocation(url);  //This is a wiki, just let the URL be executed in place
	} else {
		ss_showForumEntry(url, isDashboard);
	}
	return false;
}

function ss_fadeOutTableRow(rowId, divId) {
	var rowObj = self.document.getElementById(rowId);
	var divObj = self.document.getElementById(divId);
	if (rowObj == null || divObj == null || typeof rowObj == 'undefined' || typeof divObj == 'undefined') return;
	if (divObj.visibility != 'hidden') {
		dojo.fadeOut({node:divId, delay:400, onEnd: function() {
			var divObj = self.document.getElementById(divId);
			var rowObj = ss_findOwningElement(divObj, "tr");
			var tbodyObj = ss_findOwningElement(rowObj, "tbody");
			tbodyObj.removeChild(rowObj);
			if (ss_loadEntryInPlaceLastRowObj != null) {
				var rowTop = parseInt(ss_getObjectTopAbs(ss_loadEntryInPlaceLastRowObj));
				var scrollTop = ss_getScrollXY()[1];
				var screenBottom = parseInt(scrollTop + ss_getWindowHeight());
				if (parseInt(rowTop + 200) > screenBottom || parseInt(rowTop - 100) < scrollTop) {
					window.scroll(0, rowTop - 100);
				}
			}
		}}).play();
		return;
	}
}

function ss_setWindowHighWaterMark(height) {
	var bodyObj = document.getElementsByTagName("body").item(0);
	var divObj = document.getElementById("ss_highwatermarkDiv");
	if (divObj == null) {
		divObj = document.createElement("div")
		divObj.setAttribute("id", "ss_highwatermarkDiv");
		bodyObj.appendChild(divObj);
		ss_setObjectHeight(divObj, height);
	}
	var divHeight = ss_getObjectHeight(divObj);
	if (divHeight < height) ss_setObjectHeight(divObj, height);
}

var ss_loadEntryInPlaceLastRowObj = null;
var ss_loadEntryInPlaceLastId = null;
var ss_loadEntryInPlaceNextId = 0;
// Note: this routine can be called (below) with obj = null
function ss_loadEntryInPlace(obj, id, binderId, entityType, namespace, viewType, isDashboard, hoverOverId) {
	if (ss_getUserDisplayStyle() == "accessible") {
		if (obj != null) ss_setSelfLocation(obj.href);
		return false;
	}
	var random = ++ss_loadEntryInPlaceNextId;
	if (typeof hoverOverId != "undefined" && hoverOverId != "") ss_hideHoverOver(hoverOverId);
	
	if (ss_loadEntryInPlaceLastRowObj != null) {
		ss_setWindowHighWaterMark(ss_getObjectHeight(ss_loadEntryInPlaceLastRowObj))
		var divId = 'ss_entry_iframeDiv'+ ss_loadEntryInPlaceLastId.substr(ss_loadEntryInPlaceLastId.indexOf(",")+1) + parseInt(random - 1);
		if (ss_loadEntryInPlaceLastId == binderId + ',' + id) {
			ss_fadeOutTableRow(ss_loadEntryInPlaceLastRowObj.id, divId);
			ss_loadEntryInPlaceLastRowObj = null;
			ss_loadEntryInPlaceLastId = null;
			return;
		} else {
			ss_fadeOutTableRow(ss_loadEntryInPlaceLastRowObj.id, divId);
			ss_loadEntryInPlaceLastRowObj = null;
			ss_loadEntryInPlaceLastId = null;
		}
	}
	if (obj == null) return;
	trObj = ss_findOwningElement(obj, "tr")
	tbodyObj = ss_findOwningElement(trObj, "tbody")
	tableObj = ss_findOwningElement(trObj, "table")
	tableDivObj = ss_findOwningElement(trObj, "div")
	tbodyObj.insertBefore(trObj.cloneNode(true), trObj)
	ss_loadEntryInPlaceLastId = binderId + ',' + id;
	
	// Count the number of "td" elements
	var count = 0
	var childObj = trObj.firstChild
	while (childObj != null) {
		if (typeof childObj.tagName != 'undefined') {
			if (childObj.tagName.toLowerCase() == 'td') {
				count++;
			}
		}
		if (childObj == trObj.lastChild) break;
		childObj = childObj.nextSibling
	}
	var iframeRow = document.createElement("tr");
	iframeRow.setAttribute("id", "ss_entry_rowId"+id+random);
	var iframeCol = document.createElement("td");
	iframeCol.className = "ss_fixed_TD_frame";
	iframeCol.setAttribute("colSpan", count);
	iframeRow.appendChild(iframeCol);
	// Draw Iframe for discussion thread
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, entryId:id, entityType:entityType, entryViewType:"entryView", entryViewStyle:"inline", entryViewStyle2:viewType, namespace:namespace}, "view_folder_entry");
	iframeCol.innerHTML = '<div id="ss_entry_iframeDiv'+id+random+'" ' +
		'style="width:'+(ss_getObjectWidth(tableDivObj)-50)+'px;">' +
		'<div align="right"><a href="javascript: ;" ' +
		'onClick="ss_loadEntryInPlace(null, \''+id+'\', \''+binderId+'\');return false;">' +
		'<span class="ss_smallprint ss_italic">['+ss_closeText+']</span></div>' +
		'<iframe id="ss_entry_iframe'+id+random+'" name="ss_entry_iframe'+id+random+'"' +
    	' src="'+url+'"' +
    	' style="height:300px;width:'+(ss_getObjectWidth(tableDivObj)-50)+'px; margin:10px 10px 10px 20px; padding:0px;" frameBorder="0"' +
    	' onLoad="ss_setIframeHeight(\'ss_entry_iframeDiv'+id+random+'\', \'ss_entry_iframe'+id+random+'\', \''+hoverOverId+'\')"' +
    	' title="'+ ss_entryFrameTitle +'">Novell Vibe</iframe>' +
    	'</div>';
	tbodyObj.replaceChild(iframeRow, trObj)
	ss_loadEntryInPlaceLastRowObj = iframeRow;
	var divId = "ss_entry_iframeDiv"+id+random;
	var divObj = document.getElementById(divId);
	ss_setOpacity(divObj, 0);
	dojo.fadeIn({node:divObj, delay:600}).play();
	ss_highlightLine(id, namespace);
	ss_hideSunburst(id, binderId);
	return false;
}

function ss_pinEntry(obj, binderId, entryId) {
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"pin_entry", binderId:binderId, entryId:entryId});
	var imgObj = obj.getElementsByTagName("img").item(0);
	if (imgObj.src.indexOf("pin_orange.png") > 0) {
		imgObj.src = ss_imagesPath + "pics/discussion/pin_gray.png";
	} else {
		imgObj.src = ss_imagesPath + "pics/discussion/pin_orange.png"
	}
	ss_post_to_url(url);
}

var ss_entryInPlaceIframeOffset = 0;
function ss_setIframeHeight(divId, iframeId, hoverOverId) {
	//ss_debug("**** "+ss_debugTrace());
	var targetDiv = document.getElementById(divId);
	var iframeDiv = document.getElementById(iframeId);	
	if (window.frames[iframeId] != null) {
		//var iframeHeight = parseInt(ss_getBodyHeightWidth(window.frames[iframeId])[1]);
		var iframeHeight = parseInt(window.frames[iframeId].document.body.scrollHeight);
		if (iframeHeight > 0) {
			//ss_debug("   Setting "+divId+" height to: "+parseInt(parseInt(iframeHeight) + ss_entryInPlaceIframeOffset) + "px")
			iframeDiv.style.height = parseInt(parseInt(iframeHeight) + ss_entryInPlaceIframeOffset) + "px"
			iframeDiv.style.width= parseInt(parseInt(ss_getObjectWidth(targetDiv)) - 6) + "px";
			// Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	}
	if (hoverOverId != "") ss_hideHoverOver(hoverOverId);
}

function ss_setCurrentIframeHeight() {
	//ss_debug("**** "+ss_debugTrace());
	if (self == self.parent) return;
	if (ss_getUserDisplayStyle() == "newpage") return;
	var iframeId = window.name;
	//var iframeHeight = parseInt(ss_getBodyHeightWidth()[1]);
	var iframeHeight = parseInt(document.body.scrollHeight);
	if (iframeHeight > 0) {
		try {
			var parentIframeObj = parent.document.getElementById(iframeId);
			if (parentIframeObj != null && 
					parseInt(parentIframeObj.style.height) != parseInt(iframeHeight + ss_entryInPlaceIframeOffset)) {
				//ss_debug("   Current height: "+parseInt(parentIframeObj.style.height) + ", new height: "+parseInt(iframeHeight + ss_entryInPlaceIframeOffset));
				var scrollLeft = ss_getScrollXY()[0];
				var scrollTop = ss_getScrollXY()[1];
				parentIframeObj.style.height = parseInt(iframeHeight + ss_entryInPlaceIframeOffset) + "px";
				//ss_debug("scroll left: "+scrollLeft+", scroll top: "+scrollTop+", current: "+document.body.scrollTop);
				//Put the scroll back to what it was before resizine the iframe
				setTimeout("parent.document.getElementById(window.name).contentWindow.scrollTo('"+scrollLeft+"','"+scrollTop+"');", 50)
				//ss_debug("   Setting "+iframeId+" height to: "+parentIframeObj.style.height)
				// Signal that the layout changed
				if (parent.ssf_onLayoutChange) parent.ssf_onLayoutChange();
			}
		} catch(e) {ss_debug("ss_setCurrentIframeHeight: "+e)}
	}
}

var ss_entryHistoryIframeOffset1 = 0;
if (ss_isIE) ss_entryHistoryIframeOffset1 = 51;
function ss_resizeEntryHistoryIframe(iframeId, loadingId) {
	//ss_debug("**** "+ss_debugTrace());
	var iframeDiv = document.getElementById(iframeId)
	if (typeof loadingId != "undefined" && iframeDiv.src.indexOf("null.html") < 0) {
		var spanObj = self.document.getElementById(loadingId);
		if (spanObj != null) spanObj.style.display = "none";
	}
	try {
		var frameWindow = window.frames[iframeId];
		//ss_debug("   frameWindow: "+frameWindow.name)
		if (frameWindow && frameWindow.document && frameWindow.document.body) {
			var iframeHeight = parseInt(frameWindow.document.body.scrollHeight);
			//ss_debug("   iframeHeight: "+iframeHeight)
			if (typeof iframeDiv.style.height == "undefined" || iframeDiv.style.height == "" || 
					(parseInt(iframeDiv.style.height) != parseInt(iframeHeight) + ss_entryHistoryIframeOffset1)) {
				//ss_debug("   Setting "+iframeId+" to height: "+parseInt(iframeHeight) + ss_entryHistoryIframeOffset1 + "px")
				iframeDiv.style.height = parseInt(iframeHeight) + ss_entryHistoryIframeOffset1 + "px"
				//Signal that the layout changed
				if (ssf_onLayoutChange) setTimeout("ssf_onLayoutChange();", 300);
				//if (self.parent.ssf_onLayoutChange) setTimeout("self.parent.ssf_onLayoutChange();", 100);
				//if (self.parent.parent.ssf_onLayoutChange) setTimeout("self.parent.parent.ssf_onLayoutChange();", 100);
			}
		}
	} catch(e) {ss_debug("Error: "+e)}
}

function ss_showForumEntry(url, isDashboard) {
	if (window.top.ss_showForumEntryGwt) {
		window.top.ss_showForumEntryGwt(url, isDashboard);
		return true;
	}
	
	return ss_showForumEntryJSP(url, isDashboard);
}

function ss_showForumEntryJSP(url, isDashboard) {
	//ss_debug("**** ss_showForumEntry - window name: "+window.name);
	if (typeof ss_showForumEntryOverride != "undefined") {
		ss_showForumEntryOverride(url, isDashboard);
		return false;
	}
	if (window.name == "ss_showentryframe") {
		ss_setSelfLocation(url);
		return false;    //This is already showing in the entry frame, just let the URL be executed in place
	}
	if (typeof ss_showAsWiki != "undefined" && ss_showAsWiki) {
		ss_setSelfLocation(url);
		return false;    //This is a wiki, just let the URL be executed in place
	}
	if (typeof isDashboard == 'undefined') isDashboard = "no";
	if (window.name != "ss_showentryframe" && window.name != "gwtContentIframe") {
		if ( url.indexOf("/action/view_permalink/") > 0 &&
				url.indexOf("/seen_by_gwt/1") < 0) {
			url = url + "/seen_by_gwt/1";
		}
		if ( url.indexOf("&action=view_permalink") > 0 &&
				url.indexOf("&seen_by_gwt=1") < 0) {
			url = url + "&seen_by_gwt=1";
		}
	}
	if (ss_getUserDisplayStyle() == "accessible") {
		ss_setSelfLocation(url);
		return false;
	}
	if (isDashboard == "yes") {
		if (ss_getUserDisplayStyle() == 'newpage') {
			if (ss_isGwtUIActive) {
				return ss_showForumEntryInIframe_Overlay(url);	
			} else {
				return ss_showForumEntryInIframe_Newpage(url);	
			}
		} else {
			return ss_showForumEntryInIframe_Overlay(url);
		}
	} else {
		// redefined for displayType
		if (typeof self.ss_showForumEntryInIframe != "undefined") {
			return ss_showForumEntryInIframe(url);
		} else {
			ss_setSelfLocation(url);
			return false;
		}
	}
}

function ss_showForumEntryInIframe_Overlay(url) {
	if (window.top.ss_showForumEntryGwt) {
		window.top.ss_showForumEntryGwt(url, 'no');
		return true;
	}
	
	try {
		if (self.parent && self != self.parent && typeof self.parent.ss_showForumEntryInIframe != "undefined") {
			self.parent.ss_showForumEntryInIframe(url);
			return false;
		}
	} catch(e) {
		// Most likely permission denied. Just return and let the url be shown
		// alert('overlay1')
		return true;
	}
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
	if (wObj1 == null){
		ss_showForumEntryInIframe_Popup(url);
		return true;
	}
    var wObj2 = self.document.getElementById('ss_iframe_holder_div')
    if (wObj2 == null && wObj == null) {
		ss_showForumEntryInIframe_Popup(url);
		return true;
    }
    if (wObj == null) {
    	// The iframe does not exist, create it
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_showentryframe");
        iframeObj.setAttribute("name", "ss_showentryframe");
        iframeObj.style.display = "block"
        iframeObj.style.position = "relative"
        // iframeObj.style.left = "5px"
        iframeObj.style.width = "99%"
        iframeObj.style.height = "99%"
        iframeObj.frameBorder = "0"
        iframeObj.onload = ss_iframeOnloadSetHeight;
		wObj2.appendChild(iframeObj);
    	wObj = self.document.getElementById('ss_showentryframe');
    }
	
    ss_hideSpannedAreas();
    wObj1.style.display = "block";
    wObj1.style.zIndex = ssEntryZ;
    wObj1.style.visibility = "visible";
    
    if (ss_getUserDisplayStyle() != "newpage") {
    	// Resize the popup down to a starting size
    	wObj.style.height = "301px";
    }

    if (wObj.src && wObj.src == url) {
    	ss_nextUrl = url
//!    	wObj.src = ss_forumRefreshUrl;
    	ss_setUrlInFrame(wObj, ss_forumRefreshUrl);
    } else if (wObj.src && wObj.src == ss_forumRefreshUrl && ss_nextUrl == url) {
//!    	wObj.src = ss_forumRefreshUrl;
    	ss_setUrlInFrame(wObj, ss_forumRefreshUrl);
    } else {
//!    	wObj.src = url
    	ss_setUrlInFrame(wObj, url);
    }
    try {wObj.focus();} catch(e){}

	if (self.ss_positionEntryDiv) ss_positionEntryDiv(true);
    
	// Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}

function ss_showForumEntryInIframe_Popup(url) {
	if (window.top.ss_showForumEntryGwt) {
		window.top.ss_showForumEntryGwt(url, 'no');
		return true;
	}
	
    // ss_debug('popup width = ' + ss_viewEntryPopupWidth)
    // ss_debug('popup height = ' + ss_viewEntryPopupHeight)
    var wObj = self.document.getElementById('ss_showfolder')

	if (wObj == null) {
		if (self.parent) {
			try {
				wObj = self.parent.document.getElementById('ss_showfolder');
			} catch(e) {}
		}
	}
	
	if (wObj == null) {
		ss_viewEntryPopupWidth = 700;
		ss_viewEntryPopupHeight = 350;
	} else {
		if (typeof ss_viewEntryPopupWidth == 'undefined' || ss_viewEntryPopupWidth == "0px") 
				ss_viewEntryPopupWidth = ss_getObjectWidth(wObj);
		if (typeof ss_viewEntryPopupHeight == 'undefined' || ss_viewEntryPopupHeight == "0px") 
				ss_viewEntryPopupHeight = parseInt(ss_getWindowHeight()) - 50;
	}
	
    self.window.open(url, '_blank', 'width='+ss_viewEntryPopupWidth+',height='+ss_viewEntryPopupHeight+',resizable,scrollbars');
    return false;
}

function ss_showForumEntryInIframe_Newpage(url) {
	if (window.top.ss_showForumEntryGwt) {
		window.top.ss_showForumEntryGwt(url, 'no');
		return true;
	}
	
	ss_setSelfLocation(url);
    return false;
}

function ss_dummyMethodCall() {
}



function ss_getGeneratedURL(binderId, entryId, entityType, namespace, useNewTab) {
	var binderUrl = "";
	var entryUrl = "";
	// Try to find the base urls from this namespace or from the parent or the
	// opener
	try {
		eval("binderUrl = ss_baseBinderUrl" + namespace)
		eval("entryUrl = ss_baseEntryUrl" + namespace)
	} catch(e) {}
	
	if (!binderUrl || !entryUrl || binderUrl == "" || entryUrl == "") {
		try {
			eval("binderUrl = self.parent.ss_baseBinderUrl" + namespace)
			eval("entryUrl = self.parent.ss_baseEntryUrl" + namespace)
		} catch(e) {}
	}

	if (!binderUrl || !entryUrl || binderUrl == "" || entryUrl == "") {
		try {
			eval("binderUrl = self.opener.ss_baseBinderUrl" + namespace)
			eval("entryUrl = self.opener.ss_baseEntryUrl" + namespace)
		} catch(e) {}
	}
	
	if (!binderUrl || !entryUrl || binderUrl == "" || entryUrl == "") {
		if (!ss_baseBinderUrl || !ss_baseEntryUrl) {
			if (!self.parent.ss_baseBinderUrl || !self.parent.ss_baseEntryUrl) {
				if (self.opener && self.opener.ss_baseBinderUrl && self.opener.ss_baseEntryUrl) {
					binderUrl = self.opener.ss_baseBinderUrl;
					entryUrl = self.opener.ss_baseEntryUrl;
				}
			} else {
				binderUrl = self.parent.ss_baseBinderUrl;
				entryUrl = self.parent.ss_baseEntryUrl;
			}
		} else {
			binderUrl = ss_baseBinderUrl;
			entryUrl = ss_baseEntryUrl;
		}
	}
	
	if (!binderUrl || !entryUrl) return "";

	if (binderUrl == "" || entryUrl == "") return "";

	// Build a url to go to
	var url;
	if (entityType == 'folderEntry') {
		url = ss_replaceSubStr(entryUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_folder_entry');
	} else if (entityType == 'user') {
		url = ss_replaceSubStr(entryUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_ws_listing');
	} else if (entityType == 'folder') {
		// url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_folder_listing');	
	} else if (entityType == 'workspace') {
		// url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_ws_listing');	
	} else if (entityType == 'profiles') {
		// url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_profile_listing');
	} 

	if (useNewTab && useNewTab == "yes") {
		url = ss_replaceSubStr(url, "ssNewTabPlaceHolder", "1");
	} else {
		url = ss_replaceSubStr(url, "ssNewTabPlaceHolder", "0");
	}
	return url;
}

function ss_saveDragId(id) {
    ss_lastDropped = id
    return false;
}

// Favorites Management

function ssFavorites(namespace) {
	var deletedFavorites = new Array();

	this.showFavoritesPane = function() {	
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_favorites_tree"});
		ss_setupStatusMessageDiv()
		var fObj = self.document.getElementById("ss_favorites_pane" + namespace);
		if (fObj == null) return;
		ss_moveObjectToBody(fObj);
		fObj.style.zIndex = ssMenuZ;
		fObj.style.visibility = "visible";
		ss_setOpacity(fObj, 100)
		// fObj.style.display = "none";
		fObj.style.display = "block";
		var w = ss_getObjectWidth(fObj)
		var navbarFavDivObj = document.getElementById("ss_navbar_favorites" + namespace);
		var parentTrObj = navbarFavDivObj.parentNode.parentNode;
		var parentTrObjHeight = parseInt(dojo.contentBox(parentTrObj).h);
		ss_setObjectTop(fObj, parseInt(dojo.coords(parentTrObj, true).y + parentTrObjHeight))
		ss_setObjectLeft(fObj, parseInt(dojo.coords(navbarFavDivObj, true).x))
		ss_hideDiv("ss_favorites_editor" + namespace);
    	ss_showDivObj(fObj);
	    dojo.style(fObj, "visibility", "visible");
	    ss_setOpacity(fObj,0);
	    dojo.fadeIn({node:fObj, delay:100}).play();
		loadFavorites(url);
	}
	
	function loadFavorites(url) {
		ss_showDiv("ss_favorites_loading" + namespace);
		ss_get_url(url, ss_createDelegate(this, loadFavoritesCallback));
	}
	function loadFavoritesCallback(data) {
		setFavoritesList(data);
		ss_hideDiv("ss_favorites_loading" + namespace);
	}

	function setFavoritesList(favList) {
		var d = dojo.byId("ss_favorites_list" + namespace);
		var t = '<ul class="ss_favoritesList">';
		var fid1 = null;
		for (var i = 0; i < favList.length; i++) {
			var f = favList[i];
			if (f.eletype != 'favorite') continue
			t += '<li id ="ss_favorite_' + f.id + '">';
			t += '<span style="white-space:nowrap"><input type="checkbox" style="display: none;"/>';
			t += '<a id ="ss_favorite_link_' + f.id + '" href="javascript:;" ';
			if (typeof f.hover != "undefined") t += 'title="'+f.hover+'" ';
			t += 'onClick="ss_treeShowIdNoWS(';
			t += "'" + f.value + "', this";
			if (typeof f.action == "undefined") {
				f.action = "view_ws_listing";
			}
			t += ", '" + f.action + "'";
			t += ');return false;">' + f.name + '</a></span>';
			t += '</li>';
			if (fid1 == null) fid1 = f.id;
		}
		// Close the list and add a space so the div has something in it
		// even when empty so a floating div has something to float in.
		t += '</ul>&nbsp;';
		d.innerHTML = t;
		if (fid1 != null) {
			var fObj = self.document.getElementById("ss_favorite_link_" + fid1);
			try {fObj.focus();} catch(e){}
		}
	}



	this.saveFavorites = function() {
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_favorites", deletedIds:deletedFavorites.join(" "),
						 favorites: readFavoriteList()});
		ss_showDiv("ss_favorites_loading" + namespace);
		var callback = function(data) {
				setFavoritesList(data);
				ss_hideDiv("ss_favorites_loading" + namespace);
				this.showhideFavoritesEditor();
		}
		ss_get_url(url, ss_createDelegate(this, callback));
	}
	function readFavoriteList() {
		var container = dojo.byId("ss_favorites_list" + namespace);
		// Get the ul inside
	    var ul = container.getElementsByTagName("ul")[0];
	    // Walk the list items
	    var li = ul.getElementsByTagName("li");
	    var favs = new Array();
	    for (var i = 0; i < li.length; i += 1) {
	    	// Ids = "ss_favorite_N"
	    	favs.push(li[i].id.substr(12));
	    }    
	    return favs.join(" ");
	}

	this.addBinderToFavorites = function(favoriteBinderUrl) {
		loadFavorites(favoriteBinderUrl);
	}

	this.showhideFavoritesEditor = function() {
	   var ebox = dojo.byId("ss_favorites_editor" + namespace);
		ebox.style.zIndex = ssMenuZ;
		if (dojo.style(ebox, "display") != "none") {
			dojo.fadeOut({node:ebox, delay:100}).play()
			setFavoriteListEditable(false);
			dojo.style(ebox, "display", "none");
		} else {
		    ss_showDivObj(ebox);
		    ss_setOpacity(ebox,0);
		    dojo.fadeIn({node:ebox, delay:300}).play();
		    setFavoriteListEditable(true);
		    dojo.style(ebox, "display", "");
		}
	}

	function setFavoriteListEditable(enable) {
		var container = dojo.byId("ss_favorites_list" + namespace);
		// Clear any prior activity
		while (deletedFavorites.length) {deletedFavorites.pop() };
		// Get the ul inside
	    var ul = container.getElementsByTagName("ul")[0];
	    // Walk the list items
	    var li = ul.getElementsByTagName("li");
	    for (var i = 0; i < li.length; i += 1) {
	    	var cb = li[i].getElementsByTagName("input")[0];
	    	if (enable) {
    			cb.style.display = "";
		    } else {
    			cb.style.display = "none";
		    }
	    }    
	}

	function getSelectedFavorites() {
		var container = dojo.byId("ss_favorites_list" + namespace);
		// Get the ul inside
	    var ul = container.getElementsByTagName("ul")[0];
	    // Walk the list items
	    var li = ul.getElementsByTagName("li");
	    var selected = new Array();
	    for (var i = 0; i < li.length; i += 1) {
	    	var cb = li[i].getElementsByTagName("input")[0];
	    	if (cb.checked) {
	    		selected.push(li[i]);
	    	}
	    }    
	    return selected;
	}


	this.deleteSelectedFavorites = function() {
	    var toDelete = getSelectedFavorites();
	    dojo.forEach(toDelete, recordDeletedFavorite) 
	    dojo.forEach(toDelete, function(node){node.parentNode.removeChild(node);});
	}

	function recordDeletedFavorite(node) {
	  	deletedFavorites.push(node.id.substr(12));
	}

	this.moveSelectedFavorites = function(upDown) {
	    var	toMove;
	    var	moves;
	    
		// If there aren't any items selected to move...
	    toMove	= getSelectedFavorites();
	    moves	= ((null == toMove) ? 0 : toMove.length);
	    if (0 == moves)
	    {
	    	// ...bail.
	    	return;
	    }
	    
	    if (upDown == 'up')
	    {
	    	// If the first selected item is other than the first item
	    	// in the list...
	    	if (null != toMove[0].previousSibling)
	    	{
	    		// ...move the selections up.
			    dojo.forEach(toMove, ss_moveElementUp);
	    	}
		}
		else
		{
			// If there's more than one item selected...
			if (1 < moves)
			{
				// ...reverse the list so that we move the last
				// ...selection down first, ...
				toMove = toMove.reverse();
			}
			
			// If the last selected item is other than the last item
			// in the list...
	    	if (null != toMove[0].nextSibling)
	    	{
	    		// ...move the selections down.
		    	dojo.forEach(toMove, ss_moveElementDown);
	    	}
		}
	}
	
	this.hideFavoritesPane = function() {
		var fObj = self.document.getElementById("ss_favorites_pane" + namespace);
		if (fObj == null) return;
		ss_hideDivFadeOut('ss_favorites_pane'+namespace, 20);
	}

	
}


function ss_moveThisTableRow(objToMove, namespace, upDown) {
    var toMove = ss_findOwningElement(objToMove, "tr");
    
    if ( ss_isTR( toMove ) )
    {
    	if (upDown == 'up')
    	{
    		var prevRow;
    		
    		// Do we have a previous row?
    		prevRow = ss_getPrevRow( toMove );
    		if ( prevRow != null )
    		{
    			//Yes, check to make sure this isn't the column header row "th"
    			if (prevRow.getElementsByTagName("th").length <= 0) {
	    			// Yes, move the given row above the previous row.
					toMove.parentNode.insertBefore( toMove, prevRow );
    			}
    		}
		}
		else
		{
			var nextRow;
			
			// Do we have a next row?
			nextRow = ss_getNextRow( toMove );
			if ( nextRow != null )
			{
				// Yes, is the next row the last row?
				nextRow = ss_getNextRow( nextRow );
				if ( nextRow != null )
				{
					// No
					toMove.parentNode.insertBefore( toMove, nextRow );
				}
				else
				{
					// Yes, move the given row after the last row.
					toMove.parentNode.appendChild( toMove );
				}
			}
		}
    }
}

/**
 * Return the
 * <tr> that is before the given
 * <tr>
 */
function ss_getPrevRow( tr )
{
	var prevRow = null;
	
	// Are we dealing with an HTMLTableRowElement?
	if ( ss_isTR( tr ) )
	{
		var prevSibling;
		
		// Yes
		prevSibling = tr.previousSibling;
		while ( prevSibling != null && prevRow == null )
		{
			if ( ss_isTR( prevSibling ) )
			{
				prevRow = prevSibling;
			}
			else
				prevSibling = prevSibling.previousSibling;
		}
	}
	
	return prevRow;
}// end ss_getPrevRow()


/**
 * Return the
 * <tr> that is after the given
 * <tr>
 */
function ss_getNextRow( tr )
{
	var nextRow = null;
	
	// Are we dealing with an HTMLTableRowElement?
	if ( ss_isTR( tr ) )
	{
		var nextSibling;
		
		// Yes
		nextSibling = tr.nextSibling;
		while ( nextSibling != null && nextRow == null )
		{
			if ( ss_isTR( nextSibling ) )
			{
				nextRow = nextSibling;
			}
			else
				nextSibling = nextSibling.nextSibling;
		}
	}
	
	return nextRow;
}// end ss_getNextRow()


/**
 * Is the given element a TR?
 */
 function ss_isTR( element )
 {
 	if ( element != null && element.tagName != null && element.tagName.toLowerCase() == 'tr' )
 		return true;
 		
 	return false;
 }// end ss_isTR()


function ss_findOwningElement(obj, eleName) {
	var node = obj;
	while (node != null && node.tagName.toLowerCase() != eleName.toLowerCase()) {
		node = node.parentNode;
		if (node == null || node.tagName == null) node = null;
		if (node == null || node.tagName == null || node.tagName.toLowerCase() == 'body') break;
	}
	return node;
}

function ss_moveElementUp(node, checkTheBox) {
	if (typeof checkTheBox == "undefined") checkTheBox = true;
	var prior = node.previousSibling;
	if (prior) {
		prior.parentNode.insertBefore(node, prior);
	}
	if (checkTheBox && node.getElementsByTagName("input").length > 0) 
		node.getElementsByTagName("input")[0].checked = true;
}
function ss_moveElementDown(node, checkTheBox) {
	if (typeof checkTheBox == "undefined") checkTheBox = true;
	var next = node.nextSibling;
	if (next) {
		next = next.nextSibling;
		if (next) {
			next.parentNode.insertBefore(node, next);
		}
		else {
			var	p = node.parentNode;
			p.removeChild(node);
			p.appendChild(node);
		}
	}
	if (checkTheBox && node.getElementsByTagName("input").length > 0) 
		node.getElementsByTagName("input")[0].checked = true;
}

function ssTeams(namespace) {
	this.show = function() {
		var fObj = self.document.getElementById("ss_myteams_pane" + namespace);
		if (fObj == null) return;
		ss_moveObjectToBody(fObj);
		fObj.style.zIndex = ssMenuZ;
		fObj.style.visibility = "visible";
		ss_setOpacity(fObj, 100)
		// fObj.style.display = "none";
		fObj.style.display = "block";
		var w = ss_getObjectWidth(fObj)
		var navbarTeamsDivObj = document.getElementById("ss_navbar_myteams" + namespace);
		var parentTrObj = navbarTeamsDivObj.parentNode.parentNode;
		var parentTrObjHeight = parseInt(dojo.contentBox(parentTrObj).h);
		ss_setObjectTop(fObj, parseInt(dojo.coords(parentTrObj, true).y + parentTrObjHeight))
		ss_setObjectLeft(fObj, parseInt(ss_getDivLeft("ss_navbar_myteams" + namespace)))
		var leftEnd = parseInt(ss_getDivLeft("ss_navbar_bottom" + namespace) + ss_favoritesPaneLeftOffset);
	    ss_showDivObj(fObj);
		dojo.style(fObj, "display", "block");
	    dojo.style(fObj, "visibility", "visible");
	    ss_setOpacity(fObj,0);
	    dojo.fadeIn({node:fObj, delay:100}).play();
	    ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"show_my_teams", namespace:namespace}), ss_createDelegate(this, showCallback));
	}
	function showCallback(data) {
		ss_hideDiv("ss_myteams_loading" + namespace);
		var d = dojo.byId("ss_myteams_list" + namespace);
		d.innerHTML = data;
		var fObj = self.document.getElementById("ss_myTeams_focusId" + namespace);
		try {fObj.focus();} catch(e){}
	}
	this.hide = function() {
		var fObj = self.document.getElementById("ss_myteams_pane" + namespace);
		if (fObj == null) return;
		ss_hideDivFadeOut('ss_myteams_pane'+namespace, 20);
	}
	this.showAccessible = function() {
		var dObj = self.document.getElementById("ss_navbar_myteams" + namespace);
		var fObj = self.document.getElementById("ss_myTeamsIframe" + namespace);
		dObj.style.display = "block";
	    dojo.style(dObj, "visibility", "visible");
	    dObj.style.zIndex = parseInt(ssMenuZ);
//!	    fObj.src = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"show_my_teams", namespace:namespace});
    	ss_setUrlInFrame(fObj, ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"show_my_teams", namespace:namespace}));
	    try {fObj.focus();} catch(e){}
	}
	this.hideAccessible = function() {
		var dObj = self.document.getElementById("ss_navbar_myteams" + namespace);
		dojo.style(dObj, "display", "none");
	    dojo.style(dObj, "visibility", "hidden");
	}
}

//Routine to show some text to be copied
function ss_popupUpText(text) {
	var divObj = document.getElementById("ss_popupTextDiv");
	if (divObj == null) {
		//Create the div
		divObj = document.createElement("div");
		divObj.setAttribute("id", 'ss_popupTextDiv');
		divObj.style.position = "absolute";
		divObj.style.visibility = "hidden";
		divObj.style.display = "none";
		divObj.style.backgroundColor = "#fff";
		divObj.style.padding = "10px";
    	document.getElementsByTagName("body").item(0).appendChild(divObj);
	}
	divObj.innerHTML = text;
	ss_showPopupDivCentered("ss_popupTextDiv", "ss_popupTextDiv", true);
}

// show a div as a popup - no ajax
function ss_showPopupDiv(divId) {
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	lightBox.onclick = function(e) {ss_cancelPopupDiv(divId);};
	var divObj = document.getElementById(divId);
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	divObj.style.visibility = "visible";
	divObj.style.display= "block";
	divObj.focus();
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

// Routine to configure the columns of a folder
function ss_createPopupDiv(obj, divId) {
	url = obj.href
	url = ss_replaceSubStr(url, 'ss_randomNumberPlaceholder', ss_random++)
	var divObj = ss_createDivInBody(divId, 'ss_popupDiv');
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	divObj.style.visibility = "hidden";
	
	ss_fetch_url(url, ss_callbackPopupDiv, divId);
}

// Lightbox a dialog centered. Optionally take an id to set focus on.
var ss_popupDivsBeingShown = new Array();
function ss_showPopupDivCentered(divId, focusId, cancelable) {
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	if (cancelable || typeof cancelable === undefined) {
		lightBox.onclick = function(e) {ss_cancelPopupDiv(divId);};
	}
	var divObj = document.getElementById(divId);
    ss_moveObjectToBody(divObj); 
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	ss_setupPopupDiv(divObj);
	ss_popupDivsBeingShown[ss_popupDivsBeingShown.length] = divId;
	if (ss_isGwtUIActive && window.name != "ss_showentryframe") {
		ss_hideEntryDiv();
	}
	if (focusId && (focusId != '')) {
		try {document.getElementById(focusId).focus();} catch(e){}
	}
}

function ss_hidePopupDivs() {
	for (var i = 0; i < ss_popupDivsBeingShown.length; i++) {
		var divId = ss_popupDivsBeingShown[i];
		ss_hideDiv(divId);
	}
	ss_popupDivsBeingShown = new Array();
}

function ss_centerPopupDiv(targetDiv, inContainer) {
	if(inContainer) {
		var x = inContainer.offsetWidth / 2;
		var y = inContainer.offsetHeight / 2;
    	var bodyX = inContainer.offsetLeft
    	var bodyY = inContainer.offsetTop
	} else {
		var x = parseInt(ss_getWindowWidth() / 2);
		var y = parseInt(ss_getWindowHeight() / 2);
    	var bodyX = ss_getScrollXY()[0]
    	var bodyY = ss_getScrollXY()[1]
    }
	x = parseInt(x + bodyX - ss_getObjectWidth(targetDiv) / 2)
	y = parseInt(y + bodyY - ss_getObjectHeight(targetDiv) / 2)
	if (x < 0) x = 0;
	if (y < 0) y = 0;
	targetDiv.style.left = x + "px";
	targetDiv.style.top = y + "px";
	ss_popupDivsBeingShown[ss_popupDivsBeingShown.length] = targetDiv.id;
}

function ss_setupPopupDiv(targetDiv) {
		targetDiv.style.display = "block";
		ss_centerPopupDiv(targetDiv);
		targetDiv.style.visibility = "visible";
		targetDiv.focus();
		ss_showBackgroundIFrame(targetDiv.id, "ss_background_iframe");
		// Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_callbackPopupDiv(s, divId) {
	var targetDiv = document.getElementById(divId);
	if (targetDiv) {
		var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
		lightBox.onclick = function(e) {ss_cancelPopupDiv(divId);};
		targetDiv.innerHTML = s;
		ss_setupPopupDiv(targetDiv);
	}
}

function ss_setActionUrl(formObj, url) {
	formObj.action = url;
}

function ss_cancelPopupDiv(divId) {
	ss_hideLightbox();
	ss_hideDiv(divId);
}

function ss_dashboardInitialization(divId) {
    return;
	// Turn off ie's 3d table look
	var dashboardTable = document.getElementById(divId);
	dashboardTable.setAttribute('borderColorDark', ss_style_background_color);
	dashboardTable.setAttribute('borderColorLight', ss_style_background_color);

	var penlets = ss_getElementsByClass('ss_dashboard_component', dashboardTable, 'div')
	for (var i = 0; i < penlets.length; i++) {
// dojo_xxx new dojo.dnd.ss_dashboard_source(penlets[i], "penlet");
	}

	var bodyObj = document.getElementsByTagName("body").item(0);
	var targets = ss_getElementsByClass('ss_dashboardProtoDropTarget.*', dashboardTable, 'div')
	for (var i = 0; i < targets.length; i++) {
		ss_dashboardClones[i] = targets[i].cloneNode(true);
		ss_dashboardClones[i].className = "ss_dashboardDropTarget";
		ss_dashboardClones[i].style.visibility = "hidden";
		bodyObj.appendChild(ss_dashboardClones[i])
// dojo_xxx new dojo.dnd.ss_dashboard_target(ss_dashboardClones[i], ["penlet"]);
	}
}

function ss_clearDashboardSlider() {
	var bodyObj = document.getElementsByTagName("body").item(0);
	if (ss_dashboardSliderObj != null) {
		bodyObj.removeChild(ss_dashboardSliderObj);
		ss_setOpacity(ss_dashboardSliderTargetObj, 1)
	}
	ss_dashboardSliderObj = null;
}

function ss_enableDashboardDropTargets() {
	return;
	// ss_debug('enable drop targets')
	var dashboardTable = document.getElementById('ss_dashboardTable');
	ss_dashboardTableBorderColor = dashboardTable.style.borderColor;
	// ss_debug('dashboardTable.borderColor =
	// '+dashboardTable.style.borderColor)
	dashboardTable.className = "ss_dashboardTable_on";
	var tableElements = ss_getElementsByClass('ss_dashboardTable_.*', dashboardTable, 'td')
	for (var i = 0; i < tableElements.length; i++) tableElements[i].className = "ss_dashboardTable_on";

	var narrowFixedObj = document.getElementById('narrow_fixed')
	var narrowFixedHeight = parseInt(dojo.contentBox(narrowFixedObj).h);
	var narrowVariableObj = document.getElementById('narrow_variable')
	var narrowVariableHeight = parseInt(dojo.contentBox(narrowVariableObj).h);
	var targets = ss_getElementsByClass('ss_dashboardProtoDropTarget', null, 'div')
	for (var i = 0; i < targets.length; i++) {
		ss_dashboardClones[i].style.left = parseInt(dojo.coords(targets[i], true).x) + "px";
		ss_dashboardClones[i].style.top = parseInt(dojo.coords(targets[i], true).y) + "px";
		dojo.contentBox(ss_dashboardClones[i], {w: dojo.contentBox(targets[i]).w})
		ss_dashboardClones[i].className = "ss_dashboardDropTarget";
		ss_dashboardClones[i].style.height = ss_dashboardDropTargetHeight;
		ss_dashboardClones[i].style.visibility = "visible";
		ss_dashboardClones[i].style.zIndex = ssDashboardTargetZ;
		ss_setOpacity(ss_dashboardClones[i], .5);
		// ss_debug(' position: '+ss_dashboardClones[i].style.left+',
		// '+ss_dashboardClones[i].style.top)
		
		// See if the drop target needs to be enlarged
		var sourceNode = targets[i];
		var children = sourceNode.parentNode.getElementsByTagName('div');
		// ss_debug('sourceNode parent id = '+sourceNode.parentNode.id)
		if (sourceNode.parentNode.id == "wide_top") {
			// The top target gets enlarged upward
			if (children[0] == sourceNode) {
				ss_dashboardClones[i].style.height = ss_dashboardTopDropTargetHeight;
				var top = parseInt(dojo.coords(targets[i], true).y);
				top += parseInt(dojo.contentBox(targets[i]).h);
				top = top - parseInt(ss_dashboardDropTargetTopOffset);
				top = top - parseInt(ss_dashboardDropTargetTopOffset);
				top = top - parseInt(ss_dashboardTopDropTargetHeight);
				ss_dashboardClones[i].style.top = top + "px";
			}
		
		} else if (sourceNode.parentNode.id == "wide_bottom") {
			if (children[children.length - 1] == sourceNode) {
				ss_dashboardClones[i].style.height = ss_dashboardTopDropTargetHeight;
				var top = parseInt(dojo.coords(targets[i], true).y);
				ss_dashboardClones[i].style.top = top + "px";
			}

		} else if (sourceNode.parentNode.id == "narrow_fixed") {
			// See if this is the last target in this group
			if (children[children.length - 1] == sourceNode && narrowFixedHeight < narrowVariableHeight) {
				var height = parseInt(narrowVariableHeight - narrowFixedHeight);
				if (height < parseInt(ss_dashboardDropTargetHeight)) height = parseInt(ss_dashboardDropTargetHeight);
				ss_dashboardClones[i].style.height = height + "px";
			}
		
		} else if (sourceNode.parentNode.id == "narrow_variable") {
			// See if this is the last target in this group
			if (children[children.length - 1] == sourceNode && narrowVariableHeight < narrowFixedHeight) {
				var height = parseInt(narrowFixedHeight - narrowVariableHeight);
				if (height < parseInt(ss_dashboardDropTargetHeight)) height = parseInt(ss_dashboardDropTargetHeight);
				ss_dashboardClones[i].style.height = height + "px";
			}
		}
	}
}

function ss_disableDashboardDropTargets() {
	// ss_debug('disable drop targets')
	var dashboardTable = document.getElementById('ss_dashboardTable');
	dashboardTable.className = "ss_dashboardTable_off";
	var tableElements = ss_getElementsByClass('ss_dashboardTable_.*', dashboardTable, 'td')
	for (var i = 0; i < tableElements.length; i++) tableElements[i].className = "ss_dashboardTable_off";

	var targets = ss_getElementsByClass('ss_dashboardProtoDropTarget', null, 'div')
	for (var i = 0; i < targets.length; i++) {
		ss_dashboardClones[i].style.visibility = "hidden"
	}
}

function ss_savePenletLayout() {
	//ss_debug('Save the penlet order:')

	var dashboardTable = document.getElementById('ss_dashboardTable');
	var dashboardLayoutForm = document.getElementById('ss_dashboard_layout_form');
	var layout = "";
	var penlets = ss_getElementsByClass('ss_dashboard_component', dashboardTable, 'div')
	for (var i = 0; i < penlets.length; i++) {
		layout += penlets[i].id + ',' + penlets[i].parentNode.id + ';';
	}
	//ss_debug('Dashboard layout: ' + layout)
	dashboardLayoutForm.dashboard_layout.value = layout;

	ss_setupStatusMessageDiv()
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_dashboard_layout"}, "__ajax_dashboard");
	ss_post(url, "ss_dashboard_layout_form");
}



// Presence support
function ss_popupPresenceMenu(x, userId, userTitle, status, imURL, sweepTime, emailName, emailHost, vcard, current, ssNamespace, ssPresenceZonBridge, skypeId, workspaceId) {
    obj = self.document.getElementById('ss_presencePopUp'+ssNamespace)
    if (obj == null) {
		obj = document.createElement("div");
        obj.setAttribute("id", 'ss_presencePopUp'+ssNamespace);
        obj.style.position = "absolute"
        obj.style.visibility = "hidden";
        obj.style.display = "none";
    	document.getElementsByTagName("body").item(0).appendChild(obj);
    }
    ss_moveObjectToBody(obj)
	ss_presenceMenu('', x, userId, userTitle, status, imURL, sweepTime, emailName, emailHost, vcard, current, ssNamespace, ssPresenceZonBridge, skypeId, workspaceId);
}

function ss_presenceMenu(divId, x, userId, userTitle, status, imURL, sweepTime, emailName, emailHost, vcard, current, ssNamespace, ssPresenceZonBridge, skypeId, workspaceId) {
    var obj;
    var objId = divId;
    if (objId == '') objId = 'ss_presencePopUp'+ssNamespace;
    
    var m = ''
    var imgid = "ppgpres"+ssNamespace
    var ostatus = ss_ostatus_none;
    obj = self.document.getElementById(objId)
    if (obj == null) alert('Could not find '+objId)
	if (divId == '') {
    m += '<div style="position: relative; opacity: 0.95; background: #666; margin: 4px;">'
    m += '<div style="position: relative; left: -2px; top: -2px; border-top-width:1px; border: 1px solid #666666; background-color:white">'
        m += '<table class="ss_style ss_graymenu ss_nowrap" border="0" style="border-spacing: 3px 1px;">';
	} else {
        m += '<table class="ss_nowrap ss_transparent" border="0" style="border-spacing: 3px 1px;">';
    }

    m += '<tr>';
    if (status >= 0) {
        if (status & 1) {
            if (status & 16) {
                ostatus = ss_ostatus_away;
                imgid = "ppgpresaway"+ssNamespace
            } else {
                ostatus = ss_ostatus_online;
                imgid = "ppgpreson"+ssNamespace
            }
        } else {
            ostatus = ss_ostatus_offline;
            imgid = "ppgpresoff"+ssNamespace
        }
    }
    m += '<td class="ss_bglightgray" valign=top><img border="0" alt="" id=' +imgid +'></td>';
    m += '<td><span>' + userTitle;
    m += ostatus;
    if (status >= 0) {
        m += '</span><br><span class="ss_fineprint ss_gray">(' + ss_ostatus_at + ' ' + sweepTime + ')</span>';
    }
    m += '</td></tr>';
    if (imURL != '') {
        if (current == '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgimsg'+ssNamespace+'"></td>';
            if (status == 0) {
                m += '<td class="ss_fineprint ss_gray">'+ss_ostatus_sendIm+'</td>';
            } else {
                m += '<td><a class="ss_graymenu" href="' + imURL + '">'+ss_ostatus_sendIm+'</a></td>';
            }
            m += '</tr>';
        }
        // var schedule_meeting_url = ss_buildAdapterUrl(ss_AjaxBaseUrl,
		// {operation:"schedule_meeting", users:userId});
        //
        // m += '<tr>';
        // m += '<td class="ss_bglightgray"><img border="0" alt=""
		// id="ppgimtg'+ssNamespace+'"></td>';
        // m += '<td><a class="ss_graymenu" href="iic:meetone?screenName=' +
		// ss_escapeSQ(screenName) + '">'+ss_ostatus_startIm+'</a></td></tr>';
        // m += '<tr>';
        // m += '<td class="ss_bglightgray"><img border="0" alt=""
		// id="ppgsched'+ssNamespace+'"></td>';
        // m += '<td><a class="ss_graymenu" href="javascript:ss_startMeeting(\''
		// + ss_escapeSQ(schedule_meeting_url) +
		// '\');">'+ss_ostatus_schedIm+'</a></td></tr>';
        // m += '<tr>';
        // if (ssPresenceZonBridge == 'enabled') {
        // m += '<td class="ss_bglightgray"><img border="0" alt=""
		// id="ppgphone'+ssNamespace+'"></td>';
        // m += '<td><a class="ss_graymenu" href="javascript:ss_startMeeting(\''
		// + ss_escapeSQ(schedule_meeting_url) +
		// '\');">'+ss_ostatus_call+'</a></td></tr>';
        // }
	}
	if (userId != '' && current == '') {
        if (emailName != '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgmail'+ssNamespace+'"></td>';

			bodyText = ss_replaceSubStrAll(ss_pagePermalink, "&", "%26");
			var emailAdr = emailName;
            if (emailHost != '') emailAdr += '@' + emailHost;
	        
            m += '<td><a class="ss_graymenu" href="mailto:' + emailAdr + '?body=' + bodyText +'">'+ss_ostatus_sendMail+' (' + emailAdr + ')...</a></td></tr>';
        }
        // Bugzilla 532282:
        // Commented out the "Add to Your E-Mail Contacts" option
        // from the popup menu.
        // m += '<tr>';
        // m += '<td class="ss_bglightgray"><img border="0" alt=""
		// id="ppgvcard'+ssNamespace+'"></td>';
        // m += '<td><a class="ss_graymenu" href="' + vcard +
		// '">'+ss_ostatus_outlook+'</a></td></tr>';
    }

	if (userId != '') {
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgclipboard'+ssNamespace+'"></td>';
        m += '<td id="addToClipboardTD' + userId + '"><a class="ss_graymenu" href="javascript: // ;" onclick="ss_muster.addUsersToClipboard([' + userId + ']' + (divId != ''?', function () {$(\'addToClipboardTD'+userId+'\').innerHTML=\'OK\'}':'') + ');return false;">'+ss_ostatus_clipboard+'</a></td></tr>';
	}	
	
    if (skypeId != '') {
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgskype' +ssNamespace+'"></td>';
        m += '<td id="skypeId' + userId + '"><a class="ss_graymenu" href="skype:' + skypeId + '?call">' +ss_ostatus_skype+'</a></td></tr>';
    }

    // View MiniBlog
    if (workspaceId != "") {
    	m += '<tr>';
    	m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgminiblog' +ssNamespace+'"></td>';
    	m += '<td id="miniblog' + ss_escapeSQ(userId) + '"><a class="ss_graymenu" href="javascript: ;" ';
    	m += 'onClick="ss_viewMiniBlog(\'' + ss_escapeSQ(userId) + '\', \'0\', true);return false;">' +ss_ostatus_miniblog+'</a></td></tr>';
    }

    m += '</table>'

	if (divId == '') {
    m += '</div>'
    m += '</div>'
	}
    obj.innerHTML = m;

    if (divId == '') ss_activateMenuLayer(objId);
    if (self.document.images["ppgpres"+ssNamespace]) {
        self.document.images["ppgpres"+ssNamespace].src = ss_presencePopupGraphics["pres"].src;
    }
    if (self.document.images["ppgpreson"+ssNamespace]) {
        self.document.images["ppgpreson"+ssNamespace].src = ss_presencePopupGraphics["preson"].src;
    }
    if (self.document.images["ppgpresoff"+ssNamespace]) {
        self.document.images["ppgpresoff"+ssNamespace].src = ss_presencePopupGraphics["presoff"].src;
    }
    if (self.document.images["ppgpresaway"+ssNamespace]) {
        self.document.images["ppgpresaway"+ssNamespace].src = ss_presencePopupGraphics["presaway"].src;
    }
    if (self.document.images["ppgimsg"+ssNamespace]) {
        self.document.images["ppgimsg"+ssNamespace].src = ss_presencePopupGraphics["imsg"].src;
    }
    //if (self.document.images["ppgimtg"+ssNamespace]) {
        //self.document.images["ppgimtg"+ssNamespace].src = ss_presencePopupGraphics["imtg"].src;
    //}
    if (self.document.images["ppgmail"+ssNamespace]) {
        self.document.images["ppgmail"+ssNamespace].src = ss_presencePopupGraphics["mail"].src;
    }
    if (self.document.images["ppgvcard"+ssNamespace]) {
        self.document.images["ppgvcard"+ssNamespace].src = ss_presencePopupGraphics["vcard"].src;
    }
    if (self.document.images["ppgphone"+ssNamespace]) {
        self.document.images["ppgphone"+ssNamespace].src = ss_presencePopupGraphics["phone"].src;
    }
    if (self.document.images["ppgsched"+ssNamespace]) {
        self.document.images["ppgsched"+ssNamespace].src = ss_presencePopupGraphics["sched"].src;
    }
    if (self.document.images["ppgclipboard"+ssNamespace]) {
        self.document.images["ppgclipboard"+ssNamespace].src = ss_presencePopupGraphics["clipboard"].src;
    }	
    if (self.document.images["ppgskype"+ssNamespace]) {
        self.document.images["ppgskype"+ssNamespace].src = ss_presencePopupGraphics["skype"].src;
    }	
    if (self.document.images["ppgminiblog"+ssNamespace]) {
        self.document.images["ppgminiblog"+ssNamespace].src = ss_presencePopupGraphics["miniblog"].src;
    }	
    if (divId == '') {
	    // move the div up if it scrolls off the bottom
	    var mousePosX = parseInt(ss_getClickPositionX());
	    var mousePosY = parseInt(ss_getClickPositionY());
	    if (mousePosY != 0) {
	        var divHt = parseInt(ss_getDivHeight(objId));
	        var windowHt = parseInt(ss_getWindowHeight());
	        var scrollHt = ss_getScrollXY()[1];
	        var diff = scrollHt + windowHt - mousePosY;
	        if (divHt > 0) {
	            if (diff <= divHt) {
	               ss_positionDiv(objId, mousePosX, mousePosY - divHt);
	            }
	        }
	        // See if we need to make the portlet longer to hold the pop-up menu
	        var sizerObj = document.getElementById('ss_presence_sizer_div'+ssNamespace);
	        if (sizerObj != null) {
	        	var menuTop = ss_getDivTop(objId);
	        	var menuHeight = ss_getDivHeight(objId);
	        	var sizerTop = ss_getDivTop('ss_presence_sizer_div'+ssNamespace);
	        	var sizerHeight = ss_getDivHeight('ss_presence_sizer_div'+ssNamespace);
	        	var deltaSizerHeight = parseInt((menuTop + menuHeight) - (sizerTop + sizerHeight));
	        	if (deltaSizerHeight > 0) {
	        		ss_setObjectHeight(sizerObj, parseInt(sizerHeight + deltaSizerHeight));
	        	}
	        }
	    }
	}
}

function ss_viewMiniBlog(userId, page, popup) {
	var now = new Date();
	var random = now.valueOf()
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"view_miniblog", userId:userId, page:page, randomNumber:random});
	if (popup) {
		self.window.open(url, "_blank", "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=500px,height=500px");
	} else {
		self.window.open(url, "_blank", "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width=500px,height=500px");
		setTimeout("self.window.close();", 100);
	}
}

function ss_getActionFromEntity(entityType) {
	if (entityType == 'folderEntry') return 'view_folder_entry';
	if (entityType == 'user') return 'view_profile_entry';
	if (entityType == 'group') return 'view_profile_entry';
	if (entityType == 'folder') return 'view_folder_listing';
	if (entityType == 'workspace') return 'view_ws_listing';
	if (entityType == 'userWorkspace') return 'view_ws_listing';
	if (entityType == 'profiles') return 'view_profile_listing';
	return 'view_folder_entry'
}

// FOLDER_ENTRY=1;
// FOLDER_VIEW=5;
// PROFILE_VIEW=6;
// PROFILE_ENTRY_VIEW=7;
// WORKSPACE_VIEW=8;
// FILE_FOLDER_VIEW=9;
// FILE_ENTRY_VIEW=10;
// USER_WORKSPACE_VIEW=12;
function ss_getActionFromDefinitionType(definitionType) {
	ss_debug("getActionFromDefinitionType: " + definitionType + this.type_workspace)
	if (definitionType == 'folderEntry') return 'view_folder_entry';
	if (definitionType == 'user') return 'view_profile_entry';
	if (definitionType == 'folder') return 'view_folder_listing';
	if (definitionType == 'workspace') return 'view_ws_listing';
	if (definitionType == 'profiles') return 'view_profile_listing';
	return 'view_folder_entry'
}

function ss_launchUrlInNewWindow(obj, fileName) {
	var pattern = /\.([^\/\.]*)$/
	var results = pattern.exec(fileName)
	if (!ss_isIE && results != null) {
		// IE doesn't work on the second attempt to open the same file.
		var docList = ss_files_that_do_not_pop_up.split(" ")
		for (var i = 0; i < docList.length; i++) {
			if (results[0] == docList[i] || results[0] == "."+docList[i]) {
				return true;
			}
		}
	}
	var w = window.open(obj.href, "_blank")
	try {w.focus();} catch(e){}
	return false;
}

// UI support

function ss_showSubmenu(obj) {
	//ss_debug('ss_showSubmenu')
	var ulElements = obj.parentNode.getElementsByTagName('ul')
	for (var i = 0; i < ulElements.length; i++) {
		if (ulElements[i].parentNode == obj.parentNode && 
		    	ulElements[i].className.indexOf('submenu') >= 0) {
			//ss_debug('  show ul '+i)
			ulElements[i].style.zIndex = parseInt(ssMenuZ)
			ulElements[i].style.display = 'block'
		}
	}
	try {obj.focus();} catch(e){}
}

function ss_hideSubmenu(obj) {
	//ss_debug('ss_hideSubmenu')
	var ulElements = obj.getElementsByTagName('ul')
	for (var i = 0; i < ulElements.length; i++) {
		if (ulElements[i].parentNode == obj) {
			ulElements[i].style.display = 'none'
		}
	}
}



// Profile functions
function ss_showProfileImg(obj, targetImgId) {
	// Get the url of the current image
	var imgObjs = obj.getElementsByTagName('img');
	if (imgObjs != null) {
		var imgObj = imgObjs.item(0);
		var targetImgObj = document.getElementById(targetImgId);
		if (targetImgObj != null) {
			var url = imgObj.src
			url = ss_replaceSubStr(url, "viewType=thumbnail", "viewType=scaled")
			url = ss_replaceSubStr(url, "/readThumbnail/", "/readScaledFile/")
			targetImgObj.src = url;
		}
	}
}

function ss_showThisImage(obj) {
	// Get the url of the current image
	var imgObjs = obj.getElementsByTagName('img');
	if (imgObjs != null) {
		var imgObj = imgObjs.item(0);
		var url = imgObj.src
		url = ss_replaceSubStr(url, "viewType=thumbnail", "viewType=normal")
		url = ss_replaceSubStr(url, "viewType=scaled", "viewType=normal")
		self.window.open(url, "_blank")
	}
}

// Mustering routines
function ss_Clipboard () {
	var usersCheckboxes = new Array();
	var contributorsIds = new Array();
	var sBinderId;
	
	this.showForm = function (musterClass, userIds, binderId) {
		contributorsIds = userIds;
		sBinderId = binderId;
		buildDiv(musterClass);
		showDiv();
	}

	this.cancel = function () {
		ss_cancelPopupDiv('ss_muster_div');
		return false;
	}
		
	function buildDiv (musterClass) {
		// Build the muster form
		var musterDiv = document.getElementById('ss_muster_div');
		if (musterDiv != null) musterDiv.parentNode.removeChild(musterDiv);
		
		// Build a new muster div
		musterDiv = document.createElement("div");
	    musterDiv.setAttribute("id", "ss_muster_div");
	    musterDiv.setAttribute("align", "left");
	    musterDiv.style.visibility = "hidden";
	    musterDiv.className = "ss_muster_div";
	    musterDiv.style.display = "none";
		musterDiv.innerHTML = '<table class="ss_popup" cellpadding="0" cellspacing="0" border="0">' +
         '<tbody>' +
         '<tr class="ss_base_title_bar">' +
         '<td><div class="ss_popup_topcenter"><div id="ss_muster_title"></div></div></td>' +
         '<td align="right" width="16"><div align="right" class="ss_popup_topright">' +
         '<div id="ss_muster_close" class="ss_popup_close" align="right"></div></div>' +
         '</td></tr>' +
         '<tr><td colspan="2"><div id="ss_muster_inner" style="padding: 10px;" class="ss_popup_body"></div>' +
         '</td></tr>' +
         '<tr><td><div class="ss_popup_bottomcenter"></div></td>' +
         '<td></td></tr></tbody></table>';

		// Link into the document tree
		document.getElementsByTagName("body").item(0).appendChild(musterDiv);
   
		dojo.byId("ss_muster_title").appendChild(document.createTextNode(ss_clipboardTitleText));
		
	    var formObj = document.createElement("form");
	    formObj.setAttribute("id", "ss_muster_form");
	    formObj.setAttribute("name", "ss_muster_form");
		dojo.byId("ss_muster_inner").appendChild(formObj);
		dojo.connect(formObj, "onsubmit", function(evt) {
			return dojoformfunction(this);
	    });
	    var hiddenObj = document.createElement("input");
	    hiddenObj.setAttribute("type", "hidden");
	    hiddenObj.setAttribute("name", "muster_class");
	    hiddenObj.setAttribute("value", musterClass);
	    formObj.appendChild(hiddenObj);
		
		var brObj = document.createElement("br");

		var addBtnDivObj = document.createElement("div");
		addBtnDivObj.style.whiteSpace = "nowrap";

		var addContrBtnObj = document.createElement("input");
		addContrBtnObj.setAttribute("type", "button");
		addContrBtnObj.setAttribute("name", "add");
		addContrBtnObj.setAttribute("value", ss_addContributesToClipboardText);
		dojo.connect(addContrBtnObj, "onclick", function(evt) {
			ss_muster.addContributesToClipboard();
			return false;
	    });

		var addTeamMembersBtnObj = document.createElement("input");
		addTeamMembersBtnObj.style.marginLeft = "10px";
		addTeamMembersBtnObj.setAttribute("type", "button");
		addTeamMembersBtnObj.setAttribute("name", "add");
		addTeamMembersBtnObj.setAttribute("value", ss_addTeamMembersToClipboardText);
		dojo.connect(addTeamMembersBtnObj, "onclick", function(evt) {
			ss_muster.addTeamMembersToClipboard();
			return false;
	    });

		addBtnDivObj.appendChild(addContrBtnObj);
		if (sBinderId)
			addBtnDivObj.appendChild(addTeamMembersBtnObj);
		
		formObj.appendChild(addBtnDivObj);
		formObj.appendChild(brObj);
		
		// Add list container
		var divObj = document.createElement("div");
		divObj.id = "ss_muster_list_container";
		formObj.appendChild(divObj);
		
		// Add the buttons
		var deleteBtnObj = document.createElement("input");
		deleteBtnObj.style.marginRight = "15px"
		deleteBtnObj.setAttribute("type", "button");
		deleteBtnObj.setAttribute("name", "clear");
		deleteBtnObj.setAttribute("value", ss_clearClipboardText);
		dojo.connect(deleteBtnObj, "onclick", function(evt) {
			ss_muster.removeFromClipboard('ss_muster_form');
			return false;
	    });

		var closeBtnObj = document.createElement("input");
		closeBtnObj.style.marginRight = "15px"
		closeBtnObj.setAttribute("type", "button");
		closeBtnObj.setAttribute("name", "close");
		closeBtnObj.setAttribute("value", ss_closeButtonText);
		dojo.connect(closeBtnObj, "onclick", function(evt) {
			ss_muster.cancel();
	    });

		dojo.connect(dojo.byId("ss_muster_close"), "onclick", function(evt) {
			ss_muster.cancel();
	    });

		formObj.appendChild(brObj.cloneNode(false));
		formObj.appendChild(deleteBtnObj);
		formObj.appendChild(closeBtnObj);
		

		loadUsers(divObj);
	}
	
	function loadUsers (divObj) {
		if (!divObj)
			return;
		ss_toggleAjaxLoadingIndicator(divObj, true);
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_clipboard_users", randomNumber:ss_random++}, "clipboard");
		
		var bindArgs = {
	    	url: url,
			error: function(err) {
				ss_toggleAjaxLoadingIndicator(divObj);
				alert(ss_not_logged_in);
			},
			load: function(data) {
				ss_toggleAjaxLoadingIndicator(divObj);
				displayUsers(data, divObj);			
			},
			handleAs: "json",
			transport: "XMLHTTPTransport",
			method: "get"
		};
	   
		dojo.xhrGet(bindArgs);
	}
	
	function displayUsers(data, containerObj) {
		var divObj = document.getElementById("ss_clipboard_users_div");
		if (divObj != null) divObj.parentNode.removeChild(divObj);
		divObj = document.createElement("div");
		divObj.setAttribute("id", "ss_clipboard_users_div");
		divObj.style.padding = "10px 10px 10px 20px";
		divObj.style.whiteSpace = "nowrap";
			containerObj.innerHTML = "";
			
		if (data.length > 0) {
			usersCheckboxes = new Array();

			var ulObj = document.createElement("ul");
			ulObj.className = 'ss_stylePopup';
			ulObj.style.listStyleType = "none";
			ulObj.style.listStylePosition = "inside";
			ulObj.style.padding = "0px 0px 10px 0px";
			ulObj.style.margin = "0px";

			var lastIndex = 0;
			for (var i = 0; i < data.length; i++) {
				ulObj.appendChild(createUserLI(i, data[i][0], data[i][1]));
				lastIndex = i;
			}
			
			var hrefSelectAllObj = document.createElement("a");
			hrefSelectAllObj.href = "javascript: //;";
			// hrefSelectAllObj.onclick = ss_muster.selectAll;
			dojo.connect(hrefSelectAllObj, "onclick", function(evt) {
				ss_muster.selectAll();
		    });

			hrefSelectAllObj.className = "ss_linkButton";
			hrefSelectAllObj.style.marginRight = "5px";
			hrefSelectAllObj.appendChild(document.createTextNode(ss_selectAllBtnText));

			var hrefDeselectAllObj = document.createElement("a");
			hrefDeselectAllObj.href = "javascript: //";
			// hrefDeselectAllObj.onclick = ss_muster.clearAll;
			dojo.connect(hrefDeselectAllObj, "onclick", function(evt) {
				ss_muster.clearAll();
		    });

			hrefDeselectAllObj.className = "ss_linkButton";
			hrefDeselectAllObj.style.marginRight = "5px";
			hrefDeselectAllObj.appendChild(document.createTextNode(ss_clearAllBtnText));

			divObj.appendChild(ulObj);
			divObj.appendChild(hrefSelectAllObj);
			divObj.appendChild(hrefDeselectAllObj);
		} else {
			var spanObj = document.createElement("span");
			spanObj.className = "ss_italic";
			spanObj.appendChild(document.createTextNode(ss_noUsersOnClipboardText));
			divObj.appendChild(spanObj);
		}
		containerObj.appendChild(divObj);
	}
	
	this.selectAll = function () {
		for (var i = 0; i < usersCheckboxes.length; i++) {
			usersCheckboxes[i].checked = true;
		}
	}
	
	this.clearAll = function () {
		for (var i = 0; i < usersCheckboxes.length; i++) {
			usersCheckboxes[i].checked = false;
		}
	}

	function createUserLI (index, userId, userTitle) {
		
		var liObj = document.createElement("li");
	
		var inputObj = document.createElement("input");
		inputObj.setAttribute("type", "checkbox");
		inputObj.setAttribute("name", "muster_ids");
		inputObj.setAttribute("value", userId);
		inputObj.setAttribute("id", "muster_ids_" + index);
		
		usersCheckboxes.push(inputObj);
		
		var labelObj = document.createElement("label");
		labelObj.htmlFor = "muster_ids_" + index;
		labelObj.appendChild(document.createTextNode(userTitle));
					
		liObj.appendChild(inputObj);
		liObj.appendChild(labelObj);
		
		return liObj;
	}
	
	function showDiv () {
		// Show the muster form
		ss_showPopupDivCentered('ss_muster_div');
	}
	
	/*
	 * afterPostRoutine - optional
	 */
	this.addContributesToClipboard = function (afterPostRoutine) {
		this.addUsersToClipboard(contributorsIds, afterPostRoutine);
	}

	/*
	 * afterPostRoutine - optional
	 */	
	this.addTeamMembersToClipboard = function (afterPostRoutine) {
		var urlParams = {operation:"add_to_clipboard", add_team_members:"true", binderId:sBinderId};
		if (afterPostRoutine) updateClipboard(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "clipboard"), afterPostRoutine);
		else updateClipboard(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "clipboard"), loadUsers, $("ss_muster_list_container"));
	}
	
	/*
	 * afterPostRoutine - optional
	 */
	this.addUsersToClipboard = function (userIds, afterPostRoutine) {
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"add_to_clipboard", muster_class:"ss_muster_users"}, "clipboard");
		for (var i = 0; i < userIds.length; i++) {
			url += "&muster_ids=" + userIds[i];
		}
		updateClipboard(url, afterPostRoutine);
		if (afterPostRoutine) updateClipboard(url, afterPostRoutine);
		else updateClipboard(url, loadUsers, $("ss_muster_list_container"));
	}

	function updateClipboard(url, afterPostRoutine, args) {
			var bindArgs = {
	    	url: url,
			error: function(err) {
				alert(err);
			},
			load: function(data) {
				if (data.failure) {
					alert(data.failure);
				} else { 
					if (afterPostRoutine) afterPostRoutine(args);
				}
			},
			preventCache: true,				
			handleAs: "json"
		};   
		dojo.xhrGet(bindArgs);	
	}
	
	this.removeFromClipboard = function (formId) {
		var bindArgs = {
	    	url: ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"remove_from_clipboard"}, "clipboard"),
	    	form: dojo.byId(formId),	    	
			error: function(err) {
				alert(err);
			},
			load: function(data) {
				if (data.failure) {
					alert(data.failure);
				} else {
					loadUsers($("ss_muster_list_container"));
				}
			},
			preventCache: true,				
			handleAs: "json",
			method: "post"			
		};   
		dojo.xhrGet(bindArgs);	
	}
}

var ss_muster = new ss_Clipboard();

/*
 * Launch the meeting
 */
function ss_launchMeeting(url) {
	var win = self.window.open(url, "_blank");
	if (win != null && win.focus) win.focus();
	
	return true;
}

/*
 * Creates a new meeting and launch it now.
 * 
 * ajaxLoadingIndicatorPane: add loading indicator as child of this HTML element
 * (if exists)
 */
function ss_startMeeting(url, formId, ajaxLoadingIndicatorPane) {
	ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane, true);
	
	url += "\&randomNumber="+ss_random++;
	
	var bindArgs = {
    	url: url,
		error: function(err) {
			ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
			alert(ss_not_logged_in);
		},
		load: function(data) {
			ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
			if (data.meetingError) {
				alert(data.meetingError);
			} else {
				if (data.meetingToken && data.meetingToken != "")  {
					ss_launchMeeting(data.meetingToken);
				}
				self.window.close();
			}
		},
		form: $(formId),
		handleAs: "json",
		method: "post"
	};
   
	dojo.xhrGet(bindArgs);
}

/*
 * Show/Hide ajax loading animated icon. The icon displays/disappears in given
 * HTML-Element as child.
 * 
 * objId: HTML element id OR HTML element append: true - append loading
 * indicator as child to objId false - replace objId content with loading
 * indicator
 */

function ss_toggleAjaxLoadingIndicator(obj, append) {
	var divObj = dojo.byId(obj);
	if (!divObj) return;
			
	var wasAjaxLoaderThere = false;
	for (var i = divObj.childNodes.length; i > 0; --i) {
		if (divObj.childNodes[i - 1] && divObj.childNodes[i - 1].src && divObj.childNodes[i - 1].src.indexOf("spinner_small.gif") > -1) {
			divObj.removeChild(divObj.childNodes[i - 1]);
			wasAjaxLoaderThere = true;
		}
	}
	
	if (!wasAjaxLoaderThere) {
		if (!append) {
			while(divObj.childNodes.length > 0){
				divObj.removeChild(divObj.childNodes[0]);
           	} 			
		}
		var imgObj = document.createElement("img");
		imgObj.setAttribute("src", ss_imagesPath + "pics/spinner_small.gif");
		imgObj.setAttribute("border", "0");
		imgObj.setAttribute("style" , "vertical-align: middle; ");
		divObj.appendChild(imgObj);
	}
}

/*
 * == document.getElementById(id)
 */
function $(id) {
	return document.getElementById(id);
}

/*
 * Summary:
 * 
 * Use this routine to create 'select all' checkbox.
 * 
 * Description:
 * 
 * Routine synchronize checbox 'selectAllCheckboxId' with group of checkboxes
 * with name 'checkboxesName'. If checkbox 'selectAllCheckboxId' is
 * checked/unchecked then all 'checkboxesName' checkboxes are checked/unchecked
 * too.
 * 
 * Example:
 * 
 * HTML: <input type="checkbox" name="team_member_ids" value="998" /> Joe Bloggs
 * <input type="checkbox" name="team_member_ids" value="999" /> Bob Dao
 * 
 * <input type="checkbox" id="team_member_all_ids" /> Select all
 * 
 * JavaScript: ss_synchronizeCheckboxes("team_member_all_ids",
 * "team_member_ids");
 * 
 */
function ss_synchronizeCheckboxes(selectAllCheckboxId, checkboxesName) {
    var selectAllCheckboxObj = $(selectAllCheckboxId);
    var synchronizedCheckboxesObjs = document.getElementsByName(checkboxesName);
	
	ss_synchronizeCheckboxes(selectAllCheckboxObj, synchronizedCheckboxesObjs);
}

function ss_synchronizeCheckboxes(selectAllCheckboxObj, synchronizedCheckboxesObjs) {

	if (selectAllCheckboxObj) {
		selectAllCheckboxObj.onchange = function() {
			var checked = selectAllCheckboxObj && selectAllCheckboxObj.checked;
			for (var i = 0; i < synchronizedCheckboxesObjs.length; i++) {
				synchronizedCheckboxesObjs[i].checked = checked;
			}
		}
	}
	
	for (var i = 0; i < synchronizedCheckboxesObjs.length; i++) {
		synchronizedCheckboxesObjs[i].onchange = function () {
			for (var i = 0; i < synchronizedCheckboxesObjs.length; i++) {
				if (!synchronizedCheckboxesObjs[i].checked) {
					selectAllCheckboxObj.checked = false;
					return;
				}
			}
			selectAllCheckboxObj.checked = true;
		}	
	}
}

// Routine to pop-up an edit window for editing an element
var ss_minEditWidth = 680;
var ss_minEditHeight = 580;
var ss_editWidthOffset = 20;
var ss_editHeightOffset = 100;
var ss_editScreenWidthOfset = 60;
var ss_editScreenHeightOfset = 60;
function ss_editablePopUp(url, sourceDivId, sectionNumber) {
	var width = parseInt(ss_getDivWidth(sourceDivId) + ss_editWidthOffset);
	var height = parseInt(ss_getDivHeight(sourceDivId) + ss_editHeightOffset);
	if (width > parseInt(screen.width - ss_editScreenWidthOfset)) width = parseInt(screen.width - ss_editScreenWidthOfset)
	if (height > parseInt(screen.height - ss_editScreenHeightOfset)) height = parseInt(screen.height - ss_editScreenHeightOfset)
	var cookieWidth = ss_getCookie("ss_editableWidth");
	var cookieHeight = ss_getCookie("ss_editableHeight");
	if (cookieWidth != null && parseInt(cookieWidth) > width) width = parseInt(cookieWidth);
	if (cookieHeight != null && parseInt(cookieHeight) > height) height = parseInt(cookieHeight);
	if (width < ss_minEditWidth) width = ss_minEditWidth;
	if (height < ss_minEditHeight) height = ss_minEditHeight;
	if (typeof sectionNumber != "undefined") {
		url = ss_replaceSubStr(url, "ss_sectionPlaceholder", sectionNumber);
	} else {
		url = ss_replaceSubStr(url, "ss_sectionPlaceholder", "");
	}
	self.window.open(url, '_blank', 'width='+width+'px,height='+height+'px,directories=no,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no');
}

function ss_editableHighlight(overOut, obj, divId) {
	if (overOut == 'over') {
		dojo.byId(divId).style.border = "dashed 1px #666666";
	} else {
		dojo.byId(divId).style.border = "solid 1px transparent";
	}
}

function ss_setEditableSize() {
	ss_setCookie("ss_editableWidth", ss_getWindowWidth());
	ss_setCookie("ss_editableHeight", ss_getWindowHeight());
}

function ss_submitParentForm(htmlObj) {
	if (htmlObj.submit) {
		htmlObj.submit();
	} else if (htmlObj.parentNode) {
		ss_submitParentForm(htmlObj.parentNode);
	}
}

function ss_ajaxValidate(url, obj, labelId, msgBoxId) {
	ss_setupStatusMessageDiv();
 	var ajaxRequest = new ss_AjaxRequest(url); // Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.addKeyValue("ss_ajaxId",obj.id);
	ajaxRequest.addKeyValue("ss_ajaxValue",obj.value);
	ajaxRequest.addKeyValue("ss_ajaxLabelId",labelId);
	ajaxRequest.addKeyValue("ss_ajaxMsgId",msgBoxId);
	// ajaxRequest.setEchoDebugInfo();
	ajaxRequest.sendRequest();  // Send the request
}

// Routine to pop-up a "find user" window
var ss_launchFindUserWindowElement = null;
function ss_launchFindUserWindow(elementId) {
	ss_launchFindUserWindowElement = elementId;
	alert('Not implemented yet. Entry user id directly into text box on the left.')
}

// Rountine submits form when ENTER pressed,
// Use: attache event onkeypress="return ss_submitViaEnter(event)" to form
// fields
function ss_submitViaEnter(evt) {
    evt = (evt) ? evt : event;
    var target = (evt.target) ? evt.target : evt.srcElement;
    var form = target.form;
    var charCode = (evt.charCode) ? evt.charCode :
        ((evt.which) ? evt.which : evt.keyCode);
    if (charCode == 13) {
            form.submit();
            return false;
    }
    return true;
}

function ss_addClassToElement(elt, clazz)
{
	ss_removeClassFromElement(elt, clazz);
	elt.className = elt.className + ' ' + clazz;
}

function ss_removeClassFromElement(elt, clazz)
{
	elt.className = elt.className.replace(clazz, "");
}

function ss_defaultValidationErrorHandler(vObj, isError)
{
  if(isError) {
  	if(vObj.messageId != null && vObj.messageId != '') {
  		var messageElt = document.getElementById(vObj.messageId);
  		messageElt.style.visibility='visible';
  		messageElt.style.display='block';
  		messageElt.focus();
  	}
  	if(vObj.labelId != null && vObj.labelId != '') {
  		var labelElt = document.getElementById(vObj.labelId);
  		ss_addClassToElement(labelElt, 'ss_errorLabel');
  	}
  } else {
  	if(vObj.messageId != null && vObj.messageId != '') {
  		var messageElt = document.getElementById(vObj.messageId);
  		messageElt.style.visibility='hidden';
  		messageElt.style.display='none';
  	}
  	if(vObj.labelId != null && vObj.labelId != '') {
  		var labelElt = document.getElementById(vObj.labelId);
  		ss_removeClassFromElement(labelElt, 'ss_errorLabel');
  	}
  }
}

function ss_addValidator(idOfEntryToValidate, validateRoutine, messageId, labelId, errorHandlerRoutine) {
    if(errorHandlerRoutine == null) {
    	errorHandlerRoutine = ss_defaultValidationErrorHandler;
    }
    ss_validatorList.push(new ss_validatorObj(idOfEntryToValidate, validateRoutine, messageId, labelId, errorHandlerRoutine));
}

function ss_validatorObj(id, validateRoutine, messageId, labelId, errorHandler) {
    this.id = id;
    this.validateRoutine = validateRoutine;
    this.messageId = messageId;
    this.labelId = labelId;
    this.errorHandler = errorHandler;
}


function ss_createValidationErrorsDiv()
{
	var vId = document.getElementById('ss_validation_errors_div');
	if (!vId) {
		var p = document.createElement("p");
		p.setAttribute("id", "ss_ved_text");
		var i = document.createElement("input");
		i.setAttribute("type", "button");
		// i.setAttribute("onclick",
		// "ss_cancelPopupDiv('ss_validation_errors_div')");
		dojo.connect(i, "onclick", function(evt) {
			ss_cancelPopupDiv('ss_validation_errors_div');
			return false;
	    });
		i.setAttribute("name", ss_findButtonClose);
		i.setAttribute("value", ss_findButtonClose);
		var vDiv = document.createElement("div");
        vDiv.setAttribute("id", "ss_validation_errors_div");
        vDiv.style.position = "absolute";
        vDiv.style.visibility = "hidden";
        vDiv.style.display = "none";
        vDiv.appendChild(p);
        vDiv.appendChild(i);
    	document.getElementsByTagName("body").item(0).appendChild(vDiv);
    	p = document.getElementById("ss_ved_text");
    	p.innerHTML = ss_validationErrorMessage;
	}
}

// Common validator handler
// This function will call the desired routines at form validate time
// If any routine returns "false", then this routine returns false.
function ss_validate(obj) {
	var errors = new Array();
    for (var i = 0; i < ss_validatorList.length; i++) {
		ss_validatorList[i].errorHandler(ss_validatorList[i], false);
    }
    for (var i = 0; i < ss_validatorList.length; i++) {
        if (!ss_validatorList[i].validateRoutine(ss_validatorList[i].id, obj)) {
        	errors[errors.length] = ss_validatorList[i];
			ss_validatorList[i].errorHandler(ss_validatorList[i], true);
		}
    }
    if(errors.length != 0) {
    	ss_createValidationErrorsDiv();
   		ss_showPopupDivCentered('ss_validation_errors_div');
    }

    return (errors.length == 0);
}

// Check for required fields
// Return false if there is a field left blank (after giving a alert)
function ss_checkForRequiredFields(obj) {
	if (typeof ss_isTemplateBinder != "undefined" && ss_isTemplateBinder) return true;
	if (typeof tinyMCE != "undefined" && tinyMCE.triggerSave) tinyMCE.triggerSave();
	var objs = ss_getElementsByClass("ss_required", obj, "span")
	for (var i_obj = 0; i_obj < objs.length; i_obj++) {
		var id = objs[i_obj].id.substring(12);
		var title = objs[i_obj].title;
		// See if the form element is empty
		eleObj = obj[id];
		eleObj0 = eleObj
		if (eleObj != null && typeof(eleObj) != 'undefined' && typeof(eleObj.length) != 'undefined') eleObj0 = eleObj[0];
		try {
			if (eleObj0 != null && typeof eleObj0 != 'undefined') {
				if (eleObj0.tagName.toLowerCase() == 'input' && eleObj0.type.toLowerCase() == 'radio') {
					for (var j = 0; j < eleObj.length; j++) {
						var radioClicked = false;
						if (eleObj[j].checked) {
							// alert('radio found: '+eleObj[j].value);
							radioClicked = true;
							break;
						}
					}
					if (radioClicked) continue;
				} else if (typeof(eleObj.tagName) != 'undefined' && eleObj.tagName.toLowerCase() == 'select') {
					if (typeof(eleObj.selectedIndex) != 'undefined') {
						//Check that something with a non-blank caption is selected
						if (eleObj.selectedIndex >= 0 && 
								(eleObj.multiple || eleObj.options[eleObj.selectedIndex].text != "")) {
							// alert('selection found: '+eleObj0.value);
							continue;
						}
					}
				} else if (eleObj0.tagName.toLowerCase() == 'input' && 
						(eleObj0.type.toLowerCase() == 'text' || eleObj0.type.toLowerCase() == 'hidden' ||
						 eleObj0.type.toLowerCase() == 'file')) {
					if (typeof(eleObj0.value) != 'undefined' && ss_trim(eleObj0.value) != "") {
						// alert('text found: xxx'+eleObj0.value+'xxx');
						continue;
					}
					var formObj = ss_findOwningElement(eleObj0, "form");
					
					//See if it is a file browse option and there is a request to create a file instead
					var fileNameId = "createFileName_" + id;
					var fileTypeId = "createFileType_" + id;
					var fnObj = null;
					for (var j = 0; j < formObj.elements.length; j++) {
						if (formObj.elements[j].name == fileNameId) fnObj = formObj.elements[j];
					}
					if (fnObj != null && typeof(fnObj) != 'undefined' && typeof(fnObj.length) != 'undefined') fnObj = fnObj[0];
					var ftObj = null;
					for (var j = 0; j < formObj.elements.length; j++) {
						if (formObj.elements[j].name == timeId) ftObj = formObj.elements[j];
					}
					if (ftObj != null && typeof(ftObj) != 'undefined' && typeof(ftObj.length) != 'undefined') ftObj = ftObj[0];
					if ((fnObj != null && fnObj.value != null && fnObj.value != '')) {
						// alert('create file found: '+fnObj.value);
						continue;
					}					
					
					// See if this is a date field.
					var dateId = id + "_fullDate";
					var timeId = id + "_0_fullTime";
					var dObj = null;
					for (var j = 0; j < formObj.elements.length; j++) {
						if (formObj.elements[j].name == dateId) dObj = formObj.elements[j];
					}
					if (dObj != null && typeof(dObj) != 'undefined' && typeof(dObj.length) != 'undefined') dObj = dObj[0];
					var tObj = null;
					for (var j = 0; j < formObj.elements.length; j++) {
						if (formObj.elements[j].name == timeId) tObj = formObj.elements[j];
					}
					if (tObj != null && typeof(tObj) != 'undefined' && typeof(tObj.length) != 'undefined') tObj = tObj[0];
					if ((dObj != null && dObj.value != '') || (tObj != null && tObj.value != '')) {
						// alert('date found: '+dObj.value);
						continue;
					}
	
					// See if this is an event
					var startId = "dp_" + id + "_";
					var endId = "dp2_" + id + "_";
					var sObj = obj[startId]
					if (typeof(sObj) != 'undefined' && typeof(sObj.length) != 'undefined') sObj = sObj[0];
					var eObj = obj[endId]
					if (typeof(eObj) != 'undefined' && typeof(eObj.length) != 'undefined') eObj = eObj[0];
					if ((sObj && sObj.value != '') || (eObj && eObj.value != '')) {
						// alert('event found: '+sObj.value);
						continue;
					}
				} else if (eleObj0.tagName.toLowerCase() == 'textarea') {
					if (typeof(eleObj0.value) != 'undefined') {
						var pattern = new RegExp("[\\s]*\\S");
						if (pattern.test(eleObj0.value) ) {
							// alert('textarea found: //'+eleObj0.value+'//');
							continue;
						}
					}
				}

				// No special cases, just tell the user what field has to be
				// filled in
				if (typeof ss_viewing_entry_history == "undefined") alert(title);
				return false;
			}
		} catch(e) {
			// alert('Error processing element: '+id + ', ' + e);
		}
	}
	return true;
}

function ss_ajax_result_validator(id, obj)
{
    var elt = document.getElementById(id);
	var result = elt.getAttribute("ss_ajaxResult");
	return result != "error";
}

function ss_date_validator(id, obj)
{
	var yearElt = document.getElementById(id + "_year");
	var year = yearElt.value;
	var monthSel = document.getElementById(id + "_month");
	var month = monthSel.options[monthSel.selectedIndex].value;
	var daySel = document.getElementById(id + "_date");
	var day = daySel.options[daySel.selectedIndex].value;

	return isDate(month+"/"+day+"/"+year, "M/d/y");
}

function ss_confirm(label, text) {
	if (text == null) text = "";
	if (text != "") text = " " + text;
	if (confirm(label + text)) {
		return true
	} else {
		return false
	}
}

function ss_confirmPost(label, text, url) {
	if (text == null) text = "";
	if (text != "") text = " " + text;
	if (confirm(label + text)) {
		ss_postToThisUrl(url);
		return false;
	} else {
		return false;
	}
}

function ss_confirmSetWikiHomepage(url) {
	if (confirm(ss_setWikiHomePageConfirmation)) {
		ss_postToThisUrl(url);
		return false
	} else {
		return false;
	}
}

function ss_startSpinner(btnObj)
{
	var spinner = document.getElementById("ss_spinner")
	var bodyObj = document.getElementsByTagName("body").item(0)
	if (!spinner) {
		spinner = document.createElement("div");
		spinner.className = "ss_style";
	    spinner.setAttribute("id", "ss_spinner");
        spinner.setAttribute("style", "width:300px;background-color:transparent;text-align:center;padding:10px;");
		var spinImgDiv = document.createElement("div");
		spinImgDiv.setAttribute("id", "ss_spinnerImgDiv");
		spinImgDiv.setAttribute("align", "center");
		var spinImg = document.createElement("img");
		spinImg.setAttribute("src", ss_rootPath + "images/pics/spinner.gif");
		spinImg.setAttribute("id", "ss_spinnerImg");
		spinImgDiv.appendChild(spinImg);
		spinner.appendChild(spinImgDiv);
		var status = document.createElement("div");
		status.setAttribute("id", "ss_operation_status");
		spinner.appendChild(status);
		
        bodyObj.appendChild(spinner);
		spinner.style.position='absolute';
	    spinner.style.zIndex = 1000;
		spinner.style.display='block';
		if (typeof btnObj != "undefined") {
			ss_centerPopupDiv(spinner, btnObj);
			return false;
		} else {
			ss_centerPopupDiv(spinner);
		}
	}
	spinner.style.display='block';
}

function ss_stopSpinner()
{
	var spinner = document.getElementById("ss_spinner")
	if (spinner) {
	  spinner.style.display='none';
	}
}
function ss_hideSpinnerImg() {
	var spinnerImgDiv = document.getElementById("ss_spinnerImgDiv")
	if (spinnerImgDiv) {
		spinnerImgDiv.style.visibility = "hidden";
		spinnerImgDiv.style.display = "none";
	}
}
function ss_showSpinnerImg() {
	var spinner = document.getElementById("ss_spinner");
	if (spinner) spinner.style.display='block';
	var spinnerImgDiv = document.getElementById("ss_spinnerImgDiv")
	if (spinnerImgDiv) {
		spinnerImgDiv.style.display = "block";
		spinnerImgDiv.style.visibility = "visible";
	}
}

function ss_showSavedQueriesList(relObj, divId, resultUrl) {

	if (dojo.style(dojo.byId(divId), "display") != "none") {
		dojo.fadeOut({node:divId, delay:100, onEnd: function() {
		ss_hideDiv(divId);}}).play();
		return false;
	}
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"list_saved_queries"});
	
	var bindArgs = {
    	url: url,
		error: function(err) {
			alert(ss_not_logged_in);
		},
		load: function(data) {
			try {
				var divObj = document.getElementById(divId);
			
				var ss_savedSearchLinkId = null;
				var txt = '<div class="ss_popupMenuClose" align="right" style="width:250px;">';
				txt += '<a href="javascript: ;" ';
				txt += 'onClick="ss_hideDivNone(this.parentNode.parentNode.id);">'
				txt += '<img src="' + ss_imagesPath + 'pics/delete.png" border="0"/></a></div>'
				txt += "<h1>" + ss_savedSearchTitle + "</h1><ul>";
				for (var queryNo = 0; queryNo < data.length; queryNo++) {
					txt += "<li><a href=\"" + resultUrl + "&operation=ss_savedQuery&newTab=1&ss_queryName=" + data[queryNo] + "\""
					if (ss_savedSearchLinkId == null) {
						txt += " id=\"ss_savedSearchLinkId1\" ";
						ss_savedSearchLinkId = "1";
					}
					txt += ">"+data[queryNo]+"</a></li>";
				}
				txt += "</ul>";
				divObj.innerHTML = txt;

				ss_placeOnScreen(divId, relObj, 12, 12);
				dojo.style(divId, "display", "inline");
				dojo.style(divId, "visibility", "visible");
				var fObj = document.getElementById("ss_savedSearchLinkId1");
				if (fObj != null) fObj.focus();
	            ss_setOpacity(divId,0);
	            dojo.fadeIn({node:divId, delay:50}).play();
				// Signal that the layout changed
				if (ssf_onLayoutChange) ssf_onLayoutChange();
			} catch (e) {alert(e)}
		},
		preventCache: true,
		handleAs: "json",
		method: "get"
	};   
	dojo.xhrGet(bindArgs);	
}

function ss_placeOnScreen(divId, rel, offsetTop, offsetLeft) {
	var box = dojo.coords(rel, true);
	ss_moveDivToBody(divId);
	var divObj = document.getElementById(divId);
	dijit.placeOnScreen(divObj, {x: (box.x + offsetLeft), y: (box.y + offsetTop)}, "TL", false);
	try {divObj.focus();} catch(e){}
}


function ss_submitFormViaAjax(formName, doneRoutine) {
	ss_setupStatusMessageDiv()
	var formObj = document.forms[formName];
	var ajaxRequest = new ss_AjaxRequest(formObj.action); // Create
															// AjaxRequest
															// object
	ajaxRequest.addFormElements(formName);
	// ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setData("doneRoutine", doneRoutine)
	ajaxRequest.setPostRequest(ss_postSubmitFormViaAjax);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  // Send the request
	return false;
}

function ss_postSubmitFormViaAjax(obj) {
	// See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else {
		var doneRoutine = obj.getData("doneRoutine");
		eval("setTimeout('"+doneRoutine+"();', 100)");
	}
}

// Edit Application Configuration Management

function ss_editAppConfig() {
	// Create the DIV to contain the menu...
	var menuDIV = ss_createDivInBody('ss_appConfigurator', 'ss_appConfigMenu');
	menuDIV.style.visibility = "hidden";
	menuDIV.style.zIndex = parseInt(ssLightboxZ + 1);

	// ...and run the dialog in it.
	var editAppConfig = new ssEditAppConfig(menuDIV);
	editAppConfig.showDialog();	
}


// Object to manage the Edit Application Configuration dialog.
function ssEditAppConfig(menuDIV) {
	// Store the data members...
	this.menuDIV = menuDIV;
	this.menuDIV.n_editAppConfig = this;

	// ...initialize the constants...
	this.NBSP			= "\u00A0";
	this.NBSP2			= (this.NBSP + this.NBSP);
	this.NON_DATA_ROWS	= 2;
	this.MENU_SPLIT	= (this.NBSP + "|" + this.NBSP);

	// ...define the extension/application mappings for Microsoft
	// ...Office...
	this.msoMap          = new Array();
	this.msoMap['.doc']  = "winword.exe";
	this.msoMap['.xls']  = "excel.exe";
	this.msoMap['.ppt']  = "powerpnt.exe";
	this.msoMap['.docx'] = "winword.exe";
	this.msoMap['.xlsx'] = "excel.exe";
	this.msoMap['.pptx'] = "powerpnt.exe";
	this.msoMap['.rtf']  = "winword.exe";
	this.msoMap['.txt']  = "winword.exe";
	
	// ...define the extension/application mappings for Open Office...
	this.ooMap			= new Array();
	this.ooMap['.odf']	= "soffice";
	this.ooMap['.odg']	= "soffice";
	this.ooMap['.odp']	= "soffice";
	this.ooMap['.ods']	= "soffice";
	this.ooMap['.odt']	= "soffice";
	this.ooMap['.sxw']	= "soffice";
	this.ooMap['.doc']	= "soffice";
	this.ooMap['.xls']	= "soffice";
	this.ooMap['.ppt']	= "soffice";
	this.ooMap['.rtf']	= "soffice";
	this.ooMap['.txt']	= "soffice";
	
	// ...define the extension/application mappings for Star Office...
	this.soMap			= new Array();
	this.soMap['.odf']	= "soffice";
	this.soMap['.odg']	= "soffice";
	this.soMap['.odp']	= "soffice";
	this.soMap['.ods']	= "soffice";
	this.soMap['.odt']	= "soffice";
	this.soMap['.sxw']	= "soffice";
	this.soMap['.doc']	= "soffice";
	this.soMap['.xls']	= "soffice";
	this.soMap['.ppt']	= "soffice";
	this.soMap['.rtf']	= "soffice";
	this.soMap['.txt']	= "soffice";
	
	// ...and initialize everything else.
	this.dataTABLE = null;
	this.idBase    = 0;	
	this.strings   = g_appConfigStrings;
	
	
	// Called to add a row to the data table with the given
	// extension and application.
	this.addRow = function(extension, application) {
		var	bHasEXT;
		var	eCBOX;
		var	eINPUT;
		var	eSELECT;
		var	eTD;
		var	i;
		var	iSEL;
		var	sEXT;
		var	sID;
		

		// Construct an ID for this row...
		this.idBase += 1;
		sID           = String(this.idBase);
		
		// ...and create it.
		eTR      = this.dataTABLE.insertRow(this.dataTABLE.rows.length);
		eTR.id   = ("dataTR_" + sID);
		eTR.n_id = sID;
		
		eTD        = eTR.insertCell(eTR.cells.length);
		eTD.width  = "1%";
		eCBOX      = document.createElement("input");
		eCBOX.id   = ("overrideCBOX_" + sID);
		eCBOX.type = "checkbox"
		eTD.appendChild(eCBOX);
		
		eTD       = eTR.insertCell(eTR.cells.length);
		eTD.width = "1%";
		eTD.appendChild(document.createTextNode(this.NBSP));
		
		eTD              = eTR.insertCell(eTR.cells.length);
		eSELECT          = document.createElement("select");
		eSELECT.id       = ("extensionSELECT_" + sID);
		eSELECT.onchange = function(e) {this.n_editAppConfig.extensionSELChanged(this);};
		eSELECT.n_editAppConfig = this;
		bHasEXT          = ((null != extension) && (0 < extension.length));
		if (!bHasEXT) {
			eSELECT.options[eSELECT.length] = new Option(this.strings['sidebar.appConfig.SelectAnExtension']);
		}
		iSEL = (-1);
		for (i = 0; i < g_appEditInPlaceExtensions.length; i += 1) {
			sEXT = g_appEditInPlaceExtensions[i];
			eSELECT.options[eSELECT.length] = new Option(sEXT);
			if (this.extensionsEqual(extension, sEXT)) {
				iSEL = (eSELECT.length - 1);
			}
		}
		if (((-1) == iSEL) && bHasEXT) {
			eSELECT.options[eSELECT.length] = new Option(extension);
			iSEL = (eSELECT.length - 1);
		}
		if ((-1) != iSEL) {
			eSELECT.selectedIndex = iSEL;
		}
		eTD.appendChild(eSELECT);

		eTD       = eTR.insertCell(eTR.cells.length);
		eTD.width = "1%";
		eTD.appendChild(document.createTextNode(this.NBSP));
		
		eTD = eTR.insertCell(eTR.cells.length);
		eINPUT           = document.createElement("input");
		eINPUT.id        = ("applicationINPUT_" + sID);
		eINPUT.value     = application;
		eINPUT.size      = 20;
		eINPUT.maxLength = 512;
		eTD.appendChild(eINPUT);
		
		eTD       = eTR.insertCell(eTR.cells.length);
		eTD.width = "100%";
		eTD.appendChild(document.createTextNode(this.NBSP));
		
		
		// Finally, remove any empty message that's displayed.
		this.updateEmptyMessage();
	}


	// Called to apply a use menu selection (e.g., use OpenOffice
	// applications.)
	this.applyUse = function(useWhat) {
		var	i;
				

		// Are there any overrides defined?
		if (this.hasData())
		{
			var	inUse;
			var	iR;
			
			
			// Yes! Are any those being 'used in' already defined?
			for (i in useWhat) {
				if ((-1) != this.findRowByExtension(i)) {
					// Yes! Does the user want to overwite them?
					if (!(confirm(this.strings['sidebar.appConfig.Confirm.Overwrite']))) {
						// No! Bail.
						return;
					}
					
					
					// Yes! Break out of the scan loop since once the
					// user says ok, we don't want to ask again.
					break;
				}
			}


			// Yes, the user wants to overwrite them. Delete the
			// existing rows.
			for (i in useWhat) {
				iR = this.findRowByExtension(i);
				while ((-1) != iR) {
					// Note that we use a while here instead of an if
					// for the cause where they may have multiple rows
					// defining the same extension.
					this.dataTABLE.deleteRow(iR);
					iR = this.findRowByExtension(i);
				}
			}
		}


		// Scan the overrides being 'used in'...
		for (i in useWhat) {
			// ...adding a row for each.
			this.addRow(i, useWhat[i]);
		}
	}
	
	
	// Bundles the override data for pushing back to the server.
	this.bundleData = function() {
		var	eSELECT;
		var	eTR;
		var	sID;
		var	dataA;
		var	dataS;
		

		// Allocate an Array to hold the bundled data.
		dataA = new Array();
		
		
		// Scan the rows containing data in the data table...
		for (var i = this.NON_DATA_ROWS; i< this.dataTABLE.rows.length; i += 1) {
			// ...adding data for each row to the data Array.
			eTR = this.dataTABLE.rows[i];
			sID = String( eTR.n_id );
			
			eSELECT             = document.getElementById("extensionSELECT_" + sID);
			dataA[dataA.length] = ss_trim(eSELECT.options[eSELECT.selectedIndex].text);
			dataA[dataA.length] = ss_trim(document.getElementById("applicationINPUT_" + sID).value);
		}
		
		
		// Finally, return the data Array.
		dataS = dataA.join("]-$-[");
		return dataS;
	}	
	
	
	// Called to close the Edit Application Configuration dialog.
	this.closeDialog = function() {
		this.hidePopups();
		
		ss_hideLightbox();
		ss_hideDivObj(this.menuDIV);

		this.release();		
	}
	
	
	// Called to create the data table structure in the DOM.
	this.createDataTable = function() {
		var	eA;
		var	eCBOX;
		var	eDIV;
		var	eIMG;
		var	eSPAN;
		var	eTABLE;
		var	eTD;
		var	eTR;
		

		// Define the data table's header.
		eDIV    = document.createElement("div");
		eDIV.id = "tableHeaderDIV";
		this.menuDIV.appendChild(eDIV);

		eTABLE             = document.createElement("table");
		eTABLE.border      = "0";
		eTABLE.cellSpacing = "0";
		eTABLE.cellPadding = "3";
		eTABLE.className   = "ss_objlist_table_top";
		eTABLE.width       = "100%";
		eDIV.appendChild(eTABLE);
		
		eTR           = eTABLE.insertRow(eTABLE.rows.length);
		eTR.className = "ss_objlist_table_tablehead";
	
		eTD = eTR.insertCell(eTR.cells.length);
		eTD.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Banner'] + this.NBSP2));
		eA         = document.createElement("a");
		eA.href    = "#";
		eA.onclick = function(e) {ss_helpSystem.showHelp( g_appConfigStrings['sidebar.appConfig.helpUrl'] );return false;};
		eIMG       = document.createElement("img");
 		eIMG.alt   =
 		eIMG.title = this.strings['sidebar.appConfig.Banner.Alt.Help'];
 		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("border", "0");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/sym_s_help.gif"));
 		eA.appendChild(eIMG);
 		eTD.appendChild(eA);
		eTD = eTR.insertCell(eTR.cells.length);
		eTD.appendChild(document.createTextNode(this.NBSP));
		
		
		// Define the data table's menu.
		eDIV    = document.createElement("div");
		eDIV.id = "menu";
		this.menuDIV.appendChild(eDIV);

		eTABLE             = document.createElement("table");
		eTABLE.border      = "0";
		eTABLE.cellSpacing = "0";
		eTABLE.cellPadding = "3";
		eTABLE.width       = "100%";
		eDIV.appendChild(eTABLE);
		
		eTR = eTABLE.insertRow(eTABLE.rows.length);
	
		eTD           = eTR.insertCell(eTR.cells.length);
		eTD.className = "ss_objlist_table_smalltext";
		eTD.vAlign    = "middle";
		eTD.width     = "90%";
		eTD.bgColor   = "#efeeec";
		
		eDIV = document.createElement("div");
		eTD.appendChild(eDIV);
		
		eA = document.createElement("a");
		eA.n_editAppConfig      = this;
		eA.style.textDecoration = "none";
		eA.style.paddingTop     = eA.style.paddingBottom = "1px";
		eA.style.paddingRight   = eA.style.paddingLeft   = "5px";
		eA.href                 = "#";
		eA.onclick     = function(e) {this.n_editAppConfig.handleMenu_Add(); return false;};
		eA.onmouseover = function(e) {this.n_editAppConfig.mouseOver(this);};
		eA.onmouseout  = function(e) {this.n_editAppConfig.mouseOut(this);};
		eA.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Menu.Add']));
		eDIV.appendChild(eA);

		eDIV.appendChild(document.createTextNode(this.MENU_SPLIT));
		eA = document.createElement("a");
		eA.n_editAppConfig = this;
		eA.style.textDecoration = "none";
		eA.style.paddingTop     = eA.style.paddingBottom = "1px";
		eA.style.paddingRight   = eA.style.paddingLeft   = "5px";
		eA.href                 = "#";
		eA.onclick     = function(e) {this.n_editAppConfig.handleMenu_Use(); return false;};
		eA.onmouseover = function(e) {this.n_editAppConfig.mouseOver(this);};
		eA.onmouseout  = function(e) {this.n_editAppConfig.mouseOut(this);};
		eA.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Menu.Use']));
		eIMG           = document.createElement("img");
 		eIMG.id        = "useMenuArrow";
 		eIMG.className = "ss_objlist_menu_margin";
 		eIMG.alt       =
 		eIMG.title     = this.strings['sidebar.appConfig.Menu.Alt.Open'];
 		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("border", "0");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/menudown.gif"));
		eA.appendChild(eIMG);
		eDIV.appendChild(eA);
		eDIV.appendChild(this.createUseMenuSPAN());		

		eDIV.appendChild(document.createTextNode(this.MENU_SPLIT));
		eA = document.createElement("a");
		eA.n_editAppConfig = this;
		eA.style.textDecoration = "none";
		eA.style.paddingTop     = eA.style.paddingBottom = "1px";
		eA.style.paddingRight   = eA.style.paddingLeft   = "5px";
		eA.href                 = "#";
		eA.onclick     = function(e) {this.n_editAppConfig.handleMenu_Delete(); return false;};
		eA.onmouseover = function(e) {this.n_editAppConfig.mouseOver(this);};
		eA.onmouseout  = function(e) {this.n_editAppConfig.mouseOut(this);};
		eA.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Menu.Delete']));
		eDIV.appendChild(eA);
		
		eTD         = eTR.insertCell(eTR.cells.length);
		eTD.vAlign  = "middle";
		eTD.width   = "100%";
		eTD.height  = "26";
		eTD.bgColor = "#efeeec";

		
		// Define the data table's data.
		eDIV                = document.createElement("div");
		eDIV.id             = "appConfigListDiv";
		eDIV.style.width    = "600px";
		eDIV.style.overflow = "auto";
		this.menuDIV.appendChild(eDIV);
		
		eTABLE             = document.createElement("table");
		eTABLE.id          = "appConfigTABLE";
		eTABLE.border      = "0";
		eTABLE.cellSpacing = "0";
		eTABLE.cellPadding = "3";
		eTABLE.width       = "100%";
		eDIV.appendChild(eTABLE);
		this.dataTABLE = eTABLE;
		
		eTR = eTABLE.insertRow(eTABLE.rows.length);
		
		eTD           = eTR.insertCell(eTR.cells.length);
		eTD.className = "ss_objlist_table_columnhead";
		eTD.width     = "1%";
		eCBOX         = document.createElement("input");
		eCBOX.n_editAppConfig = this;
		eCBOX.type    = "checkbox"
		eCBOX.id      = "selectAllCheckbox";
		eCBOX.onclick = function(e) {this.n_editAppConfig.handleSelectAll(this);};
		eTD.appendChild(eCBOX);
		
		eTD           = eTR.insertCell(eTR.cells.length);
		eTD.className = "ss_objlist_table_columnhead";
		eTD.width     = "1%";
		eTD.appendChild(document.createTextNode(this.NBSP));
		
		eTD           = eTR.insertCell(eTR.cells.length);
		eTD.className = "ss_objlist_table_columnhead";
		eSPAN         = document.createElement("span");
		eSPAN.style.whiteSpace = "nowrap";
		eSPAN.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Column.Extension']));
		eTD.appendChild(eSPAN);
		eTD.setAttribute("nowrap", "nowrap");

		eTD           = eTR.insertCell(eTR.cells.length);
		eTD.className = "ss_objlist_table_columnhead";
		eTD.width     = "1%";
		eTD.appendChild(document.createTextNode(this.NBSP));
		
		eTD           = eTR.insertCell(eTR.cells.length);
		eTD.className = "ss_objlist_table_columnhead";
		eSPAN         = document.createElement("span");
		eSPAN.style.whiteSpace = "nowrap";
		eSPAN.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Column.Application']));
		eTD.appendChild(eSPAN);
		eTD.setAttribute("nowrap", "nowrap");
		
		eTD           = eTR.insertCell(eTR.cells.length);
		eTD.className = "ss_objlist_table_columnhead";
		eTD.width     = "100%";
		eTD.appendChild(document.createTextNode(this.NBSP));
		
		eTR                   = eTABLE.insertRow(eTABLE.rows.length);
		eTD                   = eTR.insertCell(eTR.cells.length);
		eTD.id                = "emptyRowTD";
		eTD.className         = "ss_objlist_table_mediumtext";
		eTD.colSpan           = "6";
		eTD.style.paddingLeft = "2em";
		eTD.style.paddingTop  = "2em";
		
		
		// Display a message stating there's no overrides defined.
		this.updateEmptyMessage();
	}
	

	// Called to create the Use menu popup for the data table in the
	// DOM.
	this.createUseMenuSPAN = function() {
		var	eA;
		var	eIMG;
		var	eDIV;
		var	eUseMenu_DIV;
		var	eUseMenu_SPAN;
		

		// Create a <SPAN><DIV> to contain the entire Use menu...
		eUseMenu_SPAN                = document.createElement("span");
		eUseMenu_SPAN.id             = "usePopupMenuSPAN";
		eUseMenu_SPAN.style.position = "relative";
		eUseMenu_SPAN.style.display  = "none";
		eUseMenu_DIV                 = document.createElement("div");
		eUseMenu_DIV.className       = "ss_objlist_menu_popupDIV";
		eUseMenu_SPAN.appendChild(eUseMenu_DIV);

		// ...create the popup menu's title bar...
		eDIV = document.createElement("div");
		eDIV.className = "ss_objlist_menu_titleDIV";
		eA = document.createElement("a");
		eA.onclick = function(e){this.n_editAppConfig.hideMenu_Use(); return false;}
		eA.href = "#";
		eA.alt = eA.title = "";
		eA.n_editAppConfig = this;
		eIMG = document.createElement("img");
 		eIMG.className = "ss_objlist_menu_titleIMG";
 		eIMG.alt       =
 		eIMG.title     = this.strings['sidebar.appConfig.Menu.Alt.Close'];
 		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("border", "0");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/popup_close_box.gif"));
 		eA.appendChild(eIMG);
		eDIV.appendChild(eA);
		eDIV.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Menu.Use']));
		eUseMenu_DIV.appendChild(eDIV);

		// ...create the Open Office menu item...
		eDIV           = document.createElement("div");
		eDIV.className = "ss_objlist_menu_itemDIV";
		eIMG           = document.createElement("img");
		eIMG.id        = "useOOMenuItemIMG";
		eIMG.height    =
		eIMG.width     = "14px";
		eIMG.setAttribute("border", "0");
		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/onePXSpacer.gif"));
		eDIV.appendChild(eIMG);
		eA                      = document.createElement("a");
		eA.id                   = "useOOMenuItemA";
		eA.href                 = "#";
		eA.style.textDecoration = "none";
		eA.style.paddingTop     =
		eA.style.paddingBottom  = "1px";
		eA.style.paddingLeft    =
		eA.style.paddingRight   = "5px";
		eA.n_editAppConfig      = this;
		eA.onclick              = function(e) {this.n_editAppConfig.handleMenu_UseOO(); return false;};
		eA.onmouseover          = function(e) {this.n_editAppConfig.mouseOver(this);};
		eA.onmouseout           = function(e) {this.n_editAppConfig.mouseOut(this);};
		eA.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Menu.Use.OO']));
		eDIV.appendChild(eA);
		eIMG        = document.createElement("img");
		eIMG.height =
		eIMG.width  = "14px";
		eIMG.setAttribute("border", "0");
		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/onePXSpacer.gif"));
		eDIV.appendChild(eIMG);
		eUseMenu_DIV.appendChild(eDIV);
		
		// ...create the Star Office menu item...
		eDIV           = document.createElement("div");
		eDIV.className = "ss_objlist_menu_itemDIV";
		eIMG           = document.createElement("img");
		eIMG.id        = "useSOMenuItemIMG";
		eIMG.height    =
		eIMG.width     = "14px";
		eIMG.setAttribute("border", "0");
		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/onePXSpacer.gif"));
		eDIV.appendChild(eIMG);
		eA                      = document.createElement("a");
		eA.id                   = "useSOMenuItemA";
		eA.href                 = "#";
		eA.style.textDecoration = "none";
		eA.style.paddingTop     =
		eA.style.paddingBottom  = "1px";
		eA.style.paddingLeft    =
		eA.style.paddingRight   = "5px";
		eA.n_editAppConfig      = this;
		eA.onclick              = function(e) {this.n_editAppConfig.handleMenu_UseSO(); return false;};
		eA.onmouseover          = function(e) {this.n_editAppConfig.mouseOver(this);};
		eA.onmouseout           = function(e) {this.n_editAppConfig.mouseOut(this);};
		eA.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Menu.Use.SO']));
		eDIV.appendChild(eA);
		eIMG        = document.createElement("img");
		eIMG.height =
		eIMG.width  = "14px";
		eIMG.setAttribute("border", "0");
		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/onePXSpacer.gif"));
		eDIV.appendChild(eIMG);
		eUseMenu_DIV.appendChild(eDIV);

		// ...create the Microsoft Office menu item...
		eDIV           = document.createElement("div");
		eDIV.className = "ss_objlist_menu_itemDIV";
		eIMG           = document.createElement("img");
		eIMG.id        = "useMSOMenuItemIMG";
		eIMG.height    =
		eIMG.width     = "14px";
		eIMG.setAttribute("border", "0");
		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/onePXSpacer.gif"));
		eDIV.appendChild(eIMG);
		eA                      = document.createElement("a");
		eA.id                   = "useMSOMenuItemA";
		eA.href                 = "#";
		eA.style.textDecoration = "none";
		eA.style.paddingTop     =
		eA.style.paddingBottom  = "1px";
		eA.style.paddingLeft    =
		eA.style.paddingRight   = "5px";
		eA.n_editAppConfig      = this;
		eA.onclick              = function(e) {this.n_editAppConfig.handleMenu_UseMSO(); return false;};
		eA.onmouseover          = function(e) {this.n_editAppConfig.mouseOver(this);};
		eA.onmouseout           = function(e) {this.n_editAppConfig.mouseOut(this);};
		eA.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Menu.Use.MSO']));
		eDIV.appendChild(eA);
		eIMG        = document.createElement("img");
		eIMG.height =
		eIMG.width  = "14px";
		eIMG.setAttribute("border", "0");
		eIMG.setAttribute("align", "absmiddle");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/onePXSpacer.gif"));
		eDIV.appendChild(eIMG);
		eUseMenu_DIV.appendChild(eDIV);

		// ...create the popup menu's footer...
		eDIV = document.createElement("div");
		eDIV.className = "ss_objlist_menu_bottomDIV";
		eIMG        = document.createElement("img");
		eIMG.height =
		eIMG.width  = "4";
 		eIMG.setAttribute("border", "0");
 		eIMG.setAttribute("src", (ss_imagesPath + "pics/onePXSpacer.gif"));
 		eDIV.appendChild(eIMG);
		eUseMenu_DIV.appendChild(eDIV);
		
		// ...and return the SPAN.
		return eUseMenu_SPAN;
	}
	
		
	// Called to add a footer containing the push buttons to the
	// dialog.
	this.createFooter = function() {
		var	eBUTTON;
		var	eDIV;
		
		
		// Create a footer div to contain the push buttons...
		eDIV = document.createElement("div");
		eDIV.className = "ss_objlist_table_footer";
		this.menuDIV.appendChild(eDIV);

		// ...create the OK push button...
		eBUTTON = document.createElement("input");
		eBUTTON.className = "ss_submit";
		eBUTTON.type = "button";
		eBUTTON.value = this.strings['sidebar.appConfig.Button.OK'];
		eBUTTON.onclick = function(e) {
			if (this.n_editAppConfig.handleSave()) {
				this.n_editAppConfig.closeDialog();
			}
			return false;
		};
		eBUTTON.n_editAppConfig = this;
		eDIV.appendChild(eBUTTON);

		// ...and create the Cancel push button.
		eDIV.appendChild(document.createTextNode(this.NBSP2));		
		eBUTTON = document.createElement("input");
		eBUTTON.className = "ss_submit";
		eBUTTON.type = "button";
		eBUTTON.value = this.strings['sidebar.appConfig.Button.Cancel'];
		eBUTTON.onclick = function(e){this.n_editAppConfig.closeDialog(); return false;};
		eBUTTON.n_editAppConfig = this;
		eDIV.appendChild(eBUTTON);
	}


	// Called when the selection in the eSEL SELECT widget changes.
	this.extensionSELChanged = function(eSEL) {
		// Is other than the first item selected?
		var	iSEL = eSEL.selectedIndex;
		if (0 < iSEL) {
			// Yes! Is the first item the Select an Extension string?
			if (eSEL.options[0].text == this.strings['sidebar.appConfig.SelectAnExtension']) {
				// Yes! Remove it.
				iSEL              -= 1;
				eSEL.options[0]    = null;
				eSEL.selectedIndex = (-1);
				eSEL.selectedIndex = iSEL;
			}
		}
		

		// Is there a row besides this one that's already using this
		// extension?
		if ((-1) != this.findRowByExtension(eSEL.options[iSEL].text, eSEL.id)) {
			// Yes! Warn the user.
			alert(this.strings['sidebar.appConfig.Warning.DuplicateExtension']);
		}
	}
	
	
	// Returns true if ext1 and ext2 are the same file extensions and
	// false otherwise.
	this.extensionsEqual = function(ext1, ext2) {
		if ((null == ext1) || (null == ext2)) {
			return(ext1 == ext2);
		}
		ext1 = ss_trim(ext1);
		ext2 = ss_trim(ext2);
		if ((0 == ext1.length) || (0 == ext2.length)) {
			return(ext1 == ext2);
		}
		if (0 == ext1.indexOf('.')) ext1 = ext1.substring(1);
		if (0 == ext2.indexOf('.')) ext2 = ext2.substring(1);
		return(ext1.toLowerCase() == ext2.toLowerCase());
	}
	
	
	// Returns the index of a row using ext as its extension. Returns
	// -1 if such a row is not found. Any row whose extension SELECT
	// widget's ID is sSkipSELID is skipped.
	this.findRowByExtension = function(ext, sSkipSELID) {
		var	reply = (-1);
		

		// Do we have any overrides defined?
		if (this.hasData()) {
			var	eSEL;
			var	eTR;
			var	sSEL;
			var	sSELID;
		

			// Yes! If we're not skipping any of them...
			if (!sSkipSELID) {
				// ...make sure we have a defined string for the skip
				// ...ID compare.
				sSkipSELID = "";
			}
		
		
			// Scan the defined overrides.
			for (i = this.NON_DATA_ROWS; i < this.dataTABLE.rows.length; i += 1) {
				// Are we supposed to skip this row?
				eTR    = this.dataTABLE.rows[i];
				sSELID = ("extensionSELECT_" + String(eTR.n_id));
				if (sSkipSELID != sSELID) {
					// No! Does it correspond to the extension in
					// question?
					eSEL = document.getElementById(sSELID);
					sSEL = eSEL.options[eSEL.selectedIndex].text;
					if (this.extensionsEqual(ext, sSEL)) {
						// Yes! Return its index.
						reply = i;
						break;
					}
				}
			}
		}
		
		
		// If we get here, reply contains the index of the row
		// containing ext of -1. Return it.
		return reply;	
	}
		
	// Called when the user clicks the dialog's Add menu item.
	this.handleMenu_Add = function() {
		// Add a row...
		this.hidePopups();
		this.addRow('', '');

		// ...and put the input focus in its extension SELECT widget.
		var	eTR = this.dataTABLE.rows[this.dataTABLE.rows.length - 1];
		try {document.getElementById("extensionSELECT_" + String(eTR.n_id)).focus();} catch(e){}
	}	
	
	
	// Called when the user clicks the dialog's Delete menu item.
	this.handleMenu_Delete = function() {
		var	count;
		
		
		// Scan the rows in the data table... (Note that the row at
		// index 0 is for the column headers and the row at index 1 is
		// for spacing above the data and we skip those.)
		this.hidePopups();
		count = 0;
		for (var i = (this.dataTABLE.rows.length - 1); i >= this.NON_DATA_ROWS ; i -= 1) {
			// ...and check/uncheck each row.
			eTR = this.dataTABLE.rows[i];
			if (document.getElementById("overrideCBOX_" + String(eTR.n_id)).checked) {
				count += 1;
				this.dataTABLE.deleteRow(i);
			}
		}
		
		
		// Did we delete anything?
		if (0 == count) {
			// No! Tell the user.
			alert(this.strings['sidebar.appConfig.Error.NoDelete']);
		}

		
		// Yes, we deleted something! Are there any data rows left?
		else if (!(this.hasData())) {
			// No! Put the No Data string back.
			this.updateEmptyMessage();
		}
	}	
	
	
	// Called when the user clicks the dialog's Use menu item.
	this.handleMenu_Use = function() {
		this.hidePopups();
		document.getElementById( "usePopupMenuSPAN" ).style.display = "";
	}	
	

	// Called when the user clicks the dialog's Use menu item.
	this.handleMenu_UseMSO = function() {
		this.hidePopups();
		this.applyUse(this.msoMap);
	}	
	

	// Called when the user clicks the dialog's Use menu item.
	this.handleMenu_UseOO = function() {
		this.hidePopups();
		this.applyUse(this.ooMap);
	}	
	

	// Called when the user clicks the dialog's Use menu item.
	this.handleMenu_UseSO = function() {
		this.hidePopups();
		this.applyUse(this.soMap);
	}	
	

	// Called when the user clicks the dialog's OK push button.
	this.handleSave = function() {
		var	eINPUT;
		

		// Is the information in the data table valid?
		this.hidePopups();
		eINPUT = this.isDataValid();
		if (null != eINPUT) {
			// No! isDataValid() will have told the user about the
			// problem. Set the focus into the widget in error and
			// bail.
			window.setTimeout(function(){eINPUT.focus();}, 100);
			return false;
		}
		
		
		// The data is valid. Issue an AJAX request to save it...
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_user_appconfig", appConfigs:this.bundleData()});
		ss_get_url(url, ss_createDelegate(this, this.saveResults));

		// ...and return false. saveResults() will close the dialog
		// ...AFTER a successful save.
		return false;
	}	
	

	// Called when the user toggles the state of the select all
	// checkbox.
	this.handleSelectAll = function(eCBOX) {
		var	check;
		var	eTR;
		

		// Is the select all checkbox checked or unchecked?
		this.hidePopups();
		check = eCBOX.checked;
		
		
		// Scan the rows in the data table... (Note that the row at
		// index 0 is for the column headers and the row at index 1 is
		// for spacing above the data and we skip those.)
		for (var i = this.NON_DATA_ROWS; i < this.dataTABLE.rows.length; i += 1) {
			// ...and check/uncheck each row.
			eTR = this.dataTABLE.rows[i];
			document.getElementById("overrideCBOX_" + String(eTR.n_id)).checked = check;
		}
	}
	
	
	// Returns true if there are any rows defined in the data table
	// and false otherwise.
	this.hasData = function() {
		return (this.dataTABLE && (this.NON_DATA_ROWS < this.dataTABLE.rows.length));
	}
	

	// Hides all the popups (menus, ...)
	this.hidePopups = function() {
		this.hideMenu_Use();
	}


	// Called when the user closes the Use menu.
	this.hideMenu_Use = function() {
		document.getElementById( "usePopupMenuSPAN" ).style.display = "none";
	}


	// Returns null if the information in the data table is valid or
	// the object containing invalid data otherwise.
	this.isDataValid = function() {
		var	eINPUT1;
		var	eSELECT1;
		var	eSELECT2;
		var	eTR1;
		var	eTR2;
		var	sID1;
		var	sID2;
		var	sAPP1;
		var	sEXT1;
		var	sEXT2;
		

		// Scan rows containing data in the data table.
		for (var i = this.NON_DATA_ROWS; i < this.dataTABLE.rows.length; i += 1) {
			// Does this row contain an extension selection?
			eTR1     = this.dataTABLE.rows[i];
			sID1     = String(eTR1.n_id);
			eSELECT1 = document.getElementById("extensionSELECT_" + sID1);
			sEXT1    = eSELECT1.options[eSELECT1.selectedIndex].text;
			if (sEXT1 == this.strings['sidebar.appConfig.SelectAnExtension']) {
				// No! Tell the user about the problem and bail.
				alert(this.strings['sidebar.appConfig.Error.SelectAnExtension']);
				return eSELECT1;
			}


			// Scan the rows below the one that we're looking at.
			for (var j = (i + 1); j < this.dataTABLE.rows.length; j += 1) {
				// Does this row contain the same extension as the row
				// that we're looking at above?
				eTR2     = this.dataTABLE.rows[j];
				sID2     = String(eTR2.n_id);
				eSELECT2 = document.getElementById("extensionSELECT_" + sID2);
				sEXT2    = eSELECT2.options[eSELECT2.selectedIndex].text;
				if (this.extensionsEqual(sEXT1, sEXT2)) {
					// Yes! Tell the user about the problem and bail.
					alert(this.strings['sidebar.appConfig.Error.DuplicateExtension']);
					return eSELECT2;
				}
			}


			// Does this row contain an application?
			eINPUT1 = document.getElementById("applicationINPUT_" + sID1);
			sAPP1   = ss_trim(eINPUT1.value);
			if ((null == sAPP1) || (0 == sAPP1.length)) {
				// No! Tell the user about the problem and bail.
				alert(this.strings['sidebar.appConfig.Error.ApplicationMissing']);
				return eINPUT1;
			}
		}
		

		// If we get here, the data in data table is valid. Return it.
		return null;		
	}	
	
	
	// Called when the mouse enters an object to highlight it.
	this.mouseOver = function(eOBJ) {
		eOBJ.style.backgroundColor = '#458AB9';
		eOBJ.style.color = '#ffffff';
	}
	
		
	// Called when the mouse leaves an object to remove the highlight.
	this.mouseOut = function(eOBJ) {
		eOBJ.style.backgroundColor = '';
		eOBJ.style.color = '';
	}
	
		
	// Called to release the resources held by this object.
	this.release = function() {
		// Forget the links back to this object...
		this.lightBox.n_editAppConfig =
		this.menuDIV.n_editAppConfig = null;

		// ...the object constants...
		this.NBSP =
		this.NBSP2 =
		this.NON_DATA_ROWS =
		this.MENU_SPLIT = null;
		
		// ...the objects data maps...
		this.msoMap =
		this.ooMap =
		this.soMap = null;
		
		// ...the object's data members...
		this.dataTABLE =	
		this.dlgDIV =
		this.idBase =	
		this.lightBox =
		this.menuDIV =
		this.strings = null;

		// ...and the object's methods.
		this.addRow =
		this.applyUse =
		this.bundleData =		
		this.closeDialog =
		this.createDataTable =
		this.createUseMenuSPAN =
		this.createFooter =
		this.extensionSELChanged =
		this.extensionsEqual =
		this.findRowByExtension =
		this.handleMenu_Add =
		this.handleMenu_Delete =
		this.handleMenu_Use =
		this.handleMenu_UseMSO =
		this.handleMenu_UseOO =
		this.handleMenu_UseSO =
		this.handleSave =
		this.handleSelectAll =
		this.hasData =
		this.hideMenu_Use =
		this.hidePopups =
		this.isDataValid =
		this.mouseOut =
		this.mouseOver =
		this.release =
		this.runDialog =
		this.saveResults =
		this.showDialog =
		this.updateEmptyMessage = null;
	}


	// Given user application configuration JSON data in jsonData, runs
	// the Edit Application Configuration dialog.
	this.runDialog = function(jsonData) {
		var	eDIV;
		var	eSPAN;
		var	uac;
		
		
		// Create the dialog's caption bar.
		this.menuDIV.innerHTML = '<div class="ss_popup_top ss_themeMenu_top ss_popup_title" id="ss_appConfigDlg" />';
		this.dlgDIV = document.getElementById('ss_appConfigDlg');
	    this.dlgDIV.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Caption']));
	    

		// Create some instructions at the top of the dialog...
	    eDIV = document.createElement("div");
	    eDIV.style.width = "400px";
	    eDIV.style.padding = "10px";
	    eSPAN = document.createElement("span");
	    eSPAN.className = "ss_objlist_table_instructions";
	    eSPAN.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Info']));
	    eDIV.appendChild(eSPAN);
	    this.menuDIV.appendChild(eDIV);
	    
	    // ...create and populate the data table...
	    this.createDataTable();
		for (var i = 0; i < jsonData.length; i += 1) {
			uac = jsonData[i];
			this.addRow(uac.extension, uac.application);
		}
		
		// ...create a footer containing the dialog's push buttons...
		this.createFooter();

		// ...and show the dialog.
		var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
		lightBox.n_editAppConfig = this;
		this.lightBox = lightBox;
		lightBox.onclick = function(e) {return false;};
		this.menuDIV.style.visibility = "visible";
		this.menuDIV.style.display= "block";	
		this.menuDIV.focus();
		ss_centerPopupDiv(this.menuDIV);
	}
	

	// Callback from the request to save the Edit Application
	// Configuration information.
	this.saveResults = function() {
		// Nothing to do but close the dialog.
		this.closeDialog();
	}	
	
	
	// Starts the processing to run the Edit Application Configuration
	// dialog. Simply submits an AJAX request for the data.
	this.showDialog = function() {
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_user_appconfig"});
		ss_get_url(url, ss_createDelegate(this, this.runDialog));
	}


	// Sets or clears the in the data table about their being no
	// overrides defined.
	this.updateEmptyMessage = function() {
		document.getElementById("emptyRowTD").innerHTML = '<i id="emptyRowI" />';
		if (!(this.hasData())) {
			var	eI = document.getElementById("emptyRowI");
			eI.appendChild(document.createTextNode(this.strings['sidebar.appConfig.Message.NoData']));
		}
	}
}

function ss_changeUITheme(idListText, nameListText, divCaption) {
	var idList = idListText.split(",");
	var nameList = nameListText.split(",");
	var divObj = ss_createDivInBody('ss_uiThemeSelector', 'ss_themeMenu');
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	
	var txt = '<div class="ss_popup_top ss_themeMenu_top ss_popup_title">';
	txt += (divCaption ? divCaption : '');
	txt += '</div>';
	
	var divHtml = '<ul>';	
	for (var t=0; t<idList.length; t++) {
		var link = '<li><a class="" href="javascript: ;" onclick="ss_changeUIThemeRequest(';
		link += "'" + idList[t] + "'" + ');">';
		link += nameList[t] + '</a></li>';
		divHtml += link;
	}
	divHtml += '</ul>';
	divObj.innerHTML = txt + divHtml;
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	lightBox.onclick = function(e) {ss_cancelUITheme();};
	divObj.style.visibility = "visible";
	divObj.style.display= "block";	
	divObj.focus();
	ss_centerPopupDiv(divObj);
}

function ss_changeUIThemeRequest(themeId) {
	var setUrl = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"set_ui_theme", theme:themeId});

	var bindArgs = {
    	url: setUrl,
		error: function(err) {
			alert(ss_not_logged_in);
		},
		load: function(data) {
  		  try {
		  	document.location.reload();	  	
	      } catch (e) {alert(e);}
		},
		preventCache: true,				
		handleAs: "text",
		method: "get"
	};   
	dojo.xhrGet(bindArgs);
	    
}

function ss_cancelUITheme() {
	ss_hideLightbox();
	ss_hideDiv('ss_uiThemeSelector');
}



function ss_showAbout(divId) {
	ss_moveDivToBody(divId);
	divObj = dojo.byId(divId);
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	lightBox.onclick = function(e) {ss_cancelAbout(divId);};
	divObj.style.visibility = "visible";
	divObj.style.display= "block";	
	divObj.focus();
	ss_centerPopupDiv(divObj);
}


function ss_cancelAbout(divId) {
	ss_hideLightbox();
	ss_hideDiv(divId);
}




// Hemanth: This method will be called by the links that get created in tiny MCE
// Refer to link.js insertLink() method.
function ss_checkTypeOfLink(linkObj) {
	var targetValue = linkObj.target;
	var url = linkObj.href;
	
	if (targetValue == '_blank') return true;
	else {
		try {
			if (self.window != self.top) {
				parent.location.href = url;
			} else {
				return true;
			}
		} catch(e) {
			return true;
		}
	}
	return false;
}

function ss_buddyPhotoLoadError (imgObj, src) {
	imgObj.src = src;
}

function ss_tagSearchObj(obj) {
    var tag = dojox.data.dom.textContent(obj);
	var searchUrl = "";
   	try { searchUrl = ss_tagSearchResultUrl; } catch(e) {searchUrl=""}
	if (searchUrl == "") { try { searchUrl = self.parent.ss_tagSearchResultUrl } catch(e) {searchUrl=""} }
	if (searchUrl == "") { try { searchUrl = self.opener.ss_tagSearchResultUrl } catch(e) {searchUrl=""} }
	var url = ss_replaceSubStrAll(searchUrl, 'ss_tagPlaceHolder', tag);
	if (ss_openUrlInPortlet(url)) {
		ss_setSelfLocation(url);
	}
	return false;
}

function ss_scrollOuter() {
	window.scrollTo(0,0);
}

/* TREE WIDGET */

// Routines to display an expandable/contractable tree
//
var ss_treeIds;
if (ss_treeIds == null) ss_treeIds = new Array();
function ss_treeToggle(treeName, id, parentId, bottom, type, page, indentKey, showFullLineOnHover) {
	ss_hideBucketText()
	if (page == null) page = "";
	if (showFullLineOnHover == null) showFullLineOnHover = "false";
	if (window["ss_treeDisplayStyle"] && ss_treeDisplayStyle == 'accessible') {
		return ss_treeToggleAccessible(treeName, id, parentId, bottom, type, page, indentKey, showFullLineOnHover);
	}
	ss_setupStatusMessageDiv()
    var tObj = self.document.getElementById(treeName + "div" + id);
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    var showTreeIdRoutine = window["ss_treeShowIdRoutine_"+treeName];
    if (tObj == null) {
        // See if the tree is in the process of being loaded
        if (ss_treeIds[treeName + "div" + id] != null) return;
        ss_treeIds[treeName + "div" + id] = "1";
        // The div hasn't been loaded yet. Go get the div via ajax
		var url = window["ss_treeAjaxUrl_" + treeName];
		url = ss_replaceSubStrAll(url, "&amp;", "&");
		var ajaxRequest = new ss_AjaxRequest(url); // Create AjaxRequest object
		ajaxRequest.addKeyValue("binderId", id)
		ajaxRequest.addKeyValue("treeName", treeName)
		ajaxRequest.addKeyValue("page", page)
		ajaxRequest.addKeyValue("indentKey", indentKey)
		ajaxRequest.addKeyValue("showIdRoutine", showTreeIdRoutine)
		ajaxRequest.addKeyValue("showFullLineOnHover", showFullLineOnHover)
 		var treeKey = window["ss_treeKey_"+treeName];
		if (treeKey != null)
			ajaxRequest.addKeyValue("treeKey", treeKey);
	    var seObj = window["ss_treeSelected_"+treeName];
	    // add single select id
	    if (seObj != null) {
	    	ajaxRequest.addKeyValue("select", seObj);
	    }
		ajaxRequest.setData("treeName", treeName)
		ajaxRequest.setData("id", id)
		ajaxRequest.setData("page", page)
		ajaxRequest.setData("indentKey", indentKey)
		ajaxRequest.setData("parentId", parentId)
		ajaxRequest.setData("bottom", bottom)
		ajaxRequest.setData("type", type)
		// ajaxRequest.setEchoDebugInfo();
		// ajaxRequest.setPreRequest(ss_preRequest);
		ajaxRequest.setPostRequest(ss_postTreeDivRequest);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  // Send the request
    } else {
	    if (tObj.style.display == "none" || tObj.style.visibility == 'hidden') {
	        tObj.style.display = "block";
	        tObj.style.visibility = 'visible';
	        tObj.focus();
			if (bottom == 0) {
				if (parentId == "") {
					jObj.className = "ss_twMinusTop";	   // minus_top.gif
				} else {
					jObj.className = "ss_twMinus";	       // minus.gif
				}
			} else if (bottom == 1) {
				if (parentId == "") {
					jObj.className = "ss_twMinusTopBottom";   // minus_top_bottom.gif
				} else {
					jObj.className = "ss_twMinusBottom";      // minus_bottom.gif
				}
			} else {
				if (parentId == "") {
					jObj.className = "ss_minusTop";        // minus_top.gif (no
															// join lines)
				} else {
					jObj.className = "ss_minus";           // minus.gif (no
															// join lines)
				}
			}
			if (iObj != null && ss_treeIconsOpen[type]) iObj.src = ss_treeIconsOpen[type];
	    } else {
	        tObj.style.display = "none";
	        tObj.style.visibility = 'hidden';
			if (bottom == 0) {
				if (parentId == "") {
					jObj.className = "ss_twPlusTop";	  // plus_top.gif
				} else {
					jObj.className = "ss_twPlus";	      // plus.gif
				}
			} else if (bottom == 1) {
				if (parentId == "") {
					jObj.className = "ss_twPlusTopBottom";	  // plus_top_bottom.gif
				} else {
					jObj.className = "ss_twPlusBottom";	      // plus_bottom.gif
				}
			} else {
				if (parentId == "") {
					jObj.className = "ss_plusTop";        // plus_top.gif (no
															// join lines)
				} else {
					jObj.className = "ss_plus";           // plus.gif (no join
															// lines)
				}
			}
			if (iObj != null && ss_treeIconsClosed[type]) iObj.src = ss_treeIconsClosed[type];
	    }
		// Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
		
		self.focus();
	}
}

function ss_treeToggleAccessible(treeName, id, parentId, bottom, type, page, indentKey, showFullLineOnHover) {
	if (page == null) page = "";
    var tempObj = self.document.getElementById(treeName + "temp" + id);
    var tempObjParent = self.parent.document.getElementById(treeName + "temp" + id);
    var iframeDivObj = self.document.getElementById("ss_treeIframeDiv");
    var iframeObj = self.document.getElementById("ss_treeIframe");
    var iframeDivObjParent = self.parent.document.getElementById("ss_treeIframeDiv");
    var iframeObjParent = self.parent.document.getElementById("ss_treeIframe");
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    var showTreeIdRoutine = window["ss_treeShowIdRoutine_"+treeName];
    if (iframeDivObjParent == null && iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
	    iframeDivObjParent = iframeDivObj;
        iframeDivObj.setAttribute("id", "ss_treeIframeDiv");
        iframeDivObj.setAttribute("name", "ss_treeIframeDiv");
		iframeDivObj.className = "ss_treeIframeDiv";
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_treeIframe");
        iframeObj.setAttribute("name", "ss_treeIframe");
        iframeObj.style.width = "400px"
        iframeObj.style.height = "250px"
		iframeDivObj.appendChild(iframeObj);
	    var closeDivObj = self.document.createElement("div");
	    closeDivObj.style.border = "2px solid gray";
	    closeDivObj.style.marginTop = "1px";
	    closeDivObj.style.padding = "6px";
	    iframeDivObj.appendChild(closeDivObj);
	    var aObj = self.document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDiv('ss_treeIframeDiv');");
	    aObj.style.border = "2px outset black";
	    aObj.style.padding = "2px";
	    aObj.appendChild(document.createTextNode(ss_treeButtonClose));
	    closeDivObj.appendChild(aObj);
		self.document.getElementsByTagName( "body" ).item(0).appendChild(iframeDivObj);
		dojo.connect(iframeObj, "onload", function(evt) {
			var iframeDiv = document.getElementById('ss_treeIframe');
			if (window.frames['ss_treeIframe'] != null) {
				//var iframeHeight = parseInt(ss_getBodyHeightWidth(window.frames['ss_treeIframe'])[1]);
				eval("var iframeHeight = parseInt(window.ss_treeIframe" + ".document.body.scrollHeight);");
				if (iframeHeight > 0) {
					iframeDiv.style.height = iframeHeight + 51 + "px";
				}
			}
			return false;
	    });		
    }
    if (iframeDivObj == null) iframeDivObj = iframeDivObjParent;
    if (iframeObj == null) iframeObj = iframeObjParent;
	var x = dojo.coords(tempObj, true).x
	var y = dojo.coords(tempObj, true).y
    ss_setObjectTop(iframeDivObj, y + "px");
    ss_setObjectLeft(iframeDivObj, x + "px");
	ss_showDiv("ss_treeIframeDiv");
	var url = window["ss_treeAjaxUrl_" + treeName];
	url = ss_replaceSubStrAll(url, "&amp;", "&");
	url += "&binderId=" + id;
	url += "&treeName=" + treeName;
	if (showTreeIdRoutine != '') url += "&showIdRoutine=" + showTreeIdRoutine;
	url += "&parentId=" + parentId;
	url += "&bottom=" + bottom;
	url += "&type=" + type;
	url += "&page=" + page;
	url += "&indentKey=" + indentKey;
	var seObj = window["ss_treeSelected_"+treeName];
	if (parent) {
		var selectedIds = parent.window["ss_treeCurrentChoosen_" + treeName];
	}
	// add single select id
	if (seObj != null && !selectedIds) {
	   	url += "&select=" + seObj;
	}

	if (selectedIds && selectedIds.constructor.toString().indexOf("Array") > -1) {
		for (var i = 0; i < selectedIds.length; i++) {
			url += "&ss_tree_select=" + selectedIds[i];
		}
	} else if (selectedIds) {// it's single id
		url += "&select=" + selectedIds;
	}
	var treeKey = window["ss_treeKey_"+treeName];
	if (treeKey != null) {
		url += "&treeKey=" + treeKey;
	}
	
    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
    	ss_setSelfLocation(url);
	} else {
//!		iframeObj.src = url;
    	ss_setUrlInFrame(iframeObj, url);
	}
}

function ss_createTreeCheckbox(treeName, prefix, id) {
	var divObj = document.getElementById("ss_hiddenTreeDiv"+treeName);
	var cbObj = document.getElementById("ss_tree_checkbox" + treeName + prefix + id)
	if (cbObj == null) {
		// alert("null: ss_tree_checkbox" + treeName + prefix + id)
	} else {
		// alert("Not null: ss_tree_checkbox" + treeName + prefix + id)
	}
}

function ss_positionAccessibleIframe(treeName, id) {
	//ss_debug('position: '+ parent.ss_getDivTop(treeName + "temp" + id))
    var iframeDivObj = self.document.getElementById("ss_treeIframeDiv");
	ss_setObjectTop(iframeDivObj, ss_getDivTop(treeName + "temp" + id));
	ss_setObjectLeft(iframeDivObj, ss_getDivLeft(treeName + "temp" + id));
	ss_showDiv(iframeDivObj);
}

function ss_postTreeDivRequest(obj) {
	ss_hideBucketText()
	// See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_treeNotLoggedInMsg); 
	} else {
		ss_treeOpen(obj.getData('treeName'), obj.getData('id'), obj.getData('parentId'), obj.getData('bottom'), obj.getData('type'));
	}
	
}

function ss_treeOpen(treeName, id, parentId, bottom, type) {
    ss_hideBucketText()
    var tObj = self.document.getElementById(treeName + "div" + id);
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    if (tObj == null) {
    	// alert("ss_treeOpen div obj = null: " + treeName + "div" + id)
    } else {
    	// alert("ss_treeOpen id: " + tObj.id)
    }
    if (tObj == null) {
		// nothing came back treat as empty
		// this happens when don't have access or binder type when go to get
		// data
		if (jObj != null) {
			if (bottom == '0') {
				jObj.className = "ss_twJoin";
			} else  {
				jObj.className = "ss_twJoinBottom";	
			} 
		}
    } else {
        tObj.style.display = "block";
        tObj.style.visibility = 'visible';
        tObj.focus();
		if (jObj != null) {
			if (bottom == '0') {
				if (parentId == "") {
					jObj.className = "ss_twMinusTop";	     // minus_top.gif
				} else {
					jObj.className = "ss_twMinus";	         // minus.gif
				}
			} else if (bottom == '1') {
				if (parentId == "") {
					jObj.className = "ss_twMinusTopBottom";	 // minus_top_bottom.gif
				} else {
					jObj.className = "ss_twMinusBottom";	 // minus_bottom.gif
				}
			} else {
				if (parentId == "") {
					jObj.className = "ss_minus_top";         // minus_top.gif
																// (no join
																// lines)
				} else {
					jObj.className = "ss_minus";             // minus.gif (no
																// join lines)
				}
			}
		}
		if (iObj != null && ss_treeIconsOpen[type]) iObj.src = ss_treeIconsOpen[type];

		// Signal that the layout changed
		if (ssf_onLayoutChange) setTimeout('ssf_onLayoutChange();', 100);
		
		self.focus();
	}
}

function ss_treeToggleAll(treeName, id, parentId, bottom, type, page, indentKey, showFullLineOnHover) {
	ss_hideBucketText()
	if (page == null) page = "";
	if (showFullLineOnHover == null) showFullLineOnHover = "false";
    var tObj = self.document.getElementById(treeName + "div" + id);
    if (tObj == null) {
    	// The div hasn't been loaded yet. Only load one div at a time
    	ss_treeToggle(treeName, id, parentId, bottom, type, page, indentKey, showFullLineOnHover)
    	return
    }
	if (tObj.style.display == "none") {
		ss_treeToggle(treeName, id, parentId, bottom, type, page, indentKey, showFullLineOnHover)
	}
    var children = tObj.childNodes;
    for (var i = 0; i < children.length; i++) {
    	if (children[i].id && children[i].id.indexOf(treeName + "div") == 0) {
			var nodeRoot = treeName + "div";
			var childnode = children[i].id.substr(nodeRoot.length)
    		if (children[i].style.display == "none") {
    			ss_treeToggle(treeName, childnode, id, bottom, type, "", indentKey, showFullLineOnHover)
    		}
    		ss_treeToggleAll(treeName, childnode, id, bottom, type, "", indentKey, showFullLineOnHover)
    	}
    }
}

function ss_getNodeById(obj, id) {
	for( var i = 0; obj.childNodes[i]; i++ ) {
		if (obj.childNodes[i].getAttribute("id") == id) return obj.childNodes[i];
	}
	return null;
}

var ss_treeIcons = new Array();
var ss_treeIconsClosed = new Array();
var ss_treeIconsOpen = new Array();
function ssTree_defineBasicIcons(imageBase) {
	// Basic icons
	
	ss_treeIcons['root'] = imageBase + "/trees/root.gif";
	ss_treeIcons['spacer'] = imageBase + "/trees/spacer.gif";
	ss_treeIcons['line'] = imageBase + "/trees/line.gif";
	ss_treeIcons['join'] = imageBase + "/trees/join.gif";
	ss_treeIcons['join_bottom'] = imageBase + "/trees/join_bottom.gif";
	ss_treeIcons['minus'] = imageBase + "/trees/minus.gif";
	ss_treeIcons['minus_bottom'] = imageBase + "/trees/minus_bottom.gif";
	ss_treeIcons['plus'] = imageBase + "/trees/plus.gif";
	ss_treeIcons['plus_bottom'] = imageBase + "/trees/plus_bottom.gif";
	ss_treeIconsClosed['folder'] = imageBase + "/trees/folder.gif";
	ss_treeIconsOpen['folder'] = imageBase + "/trees/folder_open.gif";
	ss_treeIconsClosed['page'] = imageBase + "/trees/page.gif";
	
	// More icons
	
	ss_treeIconsClosed['doc'] = imageBase + "/trees/file_types/doc.gif";
	ss_treeIconsClosed['pdf'] = imageBase + "/trees/file_types/pdf.gif";
	ss_treeIconsClosed['ppt'] = imageBase + "/trees/file_types/ppt.gif";
	ss_treeIconsClosed['rtf'] = imageBase + "/trees/file_types/rtf.gif";
	ss_treeIconsClosed['sxc'] = imageBase + "/trees/file_types/sxc.gif";
	ss_treeIconsClosed['sxi'] = imageBase + "/trees/file_types/sxi.gif";
	ss_treeIconsClosed['sxw'] = imageBase + "/trees/file_types/sxw.gif";
	ss_treeIconsClosed['txt'] = imageBase + "/trees/file_types/txt.gif";
	ss_treeIconsClosed['xls'] = imageBase + "/trees/file_types/xls.gif";
}

// Routines to show and hide a tool tip at an object
function ss_showBucketText(obj, text) {
	// ss_debug('ss_showTip: '+text)
	var tipObj = document.getElementById('ss_treeBucketTextDiv')
	if (tipObj == null) {
		// Build a new tip div
		tipObj = document.createElement("div");
	    tipObj.setAttribute("id", "ss_treeBucketTextDiv");
	    tipObj.style.visibility = "hidden";
	    tipObj.className = "ss_style ss_tree_bucket_text_div";
	    tipObj.style.display = "none";

		// Link into the document tree
		document.getElementsByTagName("body").item(0).appendChild(tipObj);
	}
	tipObj.innerHTML = text;
	tipObj.style.visibility = "visible";
	tipObj.style.display = "block";
	tipObj.focus();
	tipObj.style.fontSize = obj.style.fontSize;
	tipObj.style.fontFamily = obj.style.fontFamily;
	var x = dojo.coords(obj, true).x
	var y = dojo.coords(obj, true).y
    ss_setObjectTop(tipObj, y + 16 + "px");
    ss_setObjectLeft(tipObj, x + 16 + "px");
}
function ss_hideBucketText() {
	var tipObj = document.getElementById('ss_treeBucketTextDiv')
	if (tipObj != null) {
	    tipObj.style.visibility = "hidden";
	    tipObj.style.display = "none";
	}
}


function ss_checkTree(obj, elementId) {
	if (obj.ownerDocument) {
		var cDocument = obj.ownerDocument;
	} else if (obj.document) {
		cDocument = obj.document;
	}
	if (cDocument) {
		var r = cDocument.getElementById(elementId);
		if (r) {
			if (r.type == 'radio') {
				if (r.checked !== undefined) {
					r.checked = true;
				}
			} else {
				if (r.checked !== undefined) {
					r.checked = !r.checked;
				}
			}
			if (r.onclick !== undefined) {
				r.onclick();
			}
		}
	}
	return false;
}

function ss_saveTreeId(obj, treeName, placeId, idChoicesInputId) {
	var idChoices = null;
	var choicesAreFromParent = false;

	idChoices = document.getElementById(idChoicesInputId);
	if (!idChoices) {
		idChoices = parent.document.getElementById(idChoicesInputId);
		choicesAreFromParent = true;
	}
		
	if (obj.type == 'radio') {
		if (idChoices != null && typeof idChoices !== "undefined") {
			if (idChoices.value && idChoices.value != (obj.name + "_" + obj.value)) {
				selected = parent.document.getElementById("ss_tree_radio" + treeName + idChoices.value);
				if (selected && selected.checked) {
					selected.checked = false;
				}
			}
		
			idChoices.value = obj.name + "_" + obj.value;
			if (treeName) {

				// accessible mode only - unselect last choice if visible
				var treeIframe = document.getElementById("ss_treeIframe");
				if (treeIframe && window["ss_treeCurrentChoosen_" + treeName] && 
					window["ss_treeCurrentChoosen_" + treeName] != placeId &&
					window["ss_treeSelectId"]) {
					var doc = treeIframe.document ? treeIframe.document : treeIframe.contentDocument;
					var selected = doc.getElementById("ss_tree_radio" + treeName + window["ss_treeSelectId"] + window["ss_treeCurrentChoosen_" + treeName]);
					if (selected && selected.checked) {
						selected.checked = false;
					}
				}

				if (choicesAreFromParent) {
					parent.window["ss_treeCurrentChoosen_" + treeName] = placeId;
				} else {
					window["ss_treeCurrentChoosen_" + treeName] = placeId;
				}
			}
		}
	} else {
		if (idChoices != null && typeof idChoices !== "undefined") {
			var re = new RegExp(" " + obj.name + " ", "g");
			idChoices.value = idChoices.value.replace(re, " ");
			re = new RegExp(" " + obj.name + "$", "g");
			idChoices.value = idChoices.value.replace(re, "");
			
			if (!obj.checked) {
				var uncheckIdsInUrl = function(urlToFix) {
					var partFirst = urlToFix.substring(0, urlToFix.indexOf("ss_tree_select=") + 15);
					var partToFix = urlToFix.substring(urlToFix.indexOf("ss_tree_select=") + 15);
					var partLast = partToFix.substring(partToFix.indexOf("&amp;"));
					partToFix = partToFix.substring(0, partToFix.indexOf("&amp;"));
					
					var re = new RegExp(placeId + "%2C", "");
					partToFix = partToFix.replace(re, "");
					re = new RegExp(placeId + "$", "");
					partToFix = partToFix.replace(re, "");
					re = new RegExp("%2C$", "");
					partToFix = partToFix.replace(re, "");					
					return partFirst + partToFix + partLast;
				}
				window["ss_treeAjaxUrl_" + treeName] = uncheckIdsInUrl(window["ss_treeAjaxUrl_" + treeName]);
				if (parent.window["ss_treeAjaxUrl_" + treeName]) {
					parent.window["ss_treeAjaxUrl_" + treeName] = uncheckIdsInUrl(parent.window["ss_treeAjaxUrl_" + treeName]);
				}
	  		}
	  
			if (obj.checked) {
				idChoices.value += " " + obj.name;
			}
			if (treeName && choicesAreFromParent) {
				if (typeof parent.window["ss_treeCurrentChoosen_" + treeName] === "undefined") {
					parent.window["ss_treeCurrentChoosen_" + treeName] = new Array();
				}
				
				var idsList = parent.window["ss_treeCurrentChoosen_" + treeName];
				if (obj.checked) {
						// add new id to list
					idsList.push(placeId);
				} else {
						// remove id from list
			    	for (var i = 0; i < idsList.length; i++) {
			    		if (idsList[i] == placeId) {
			    			idsList.splice(i, 1);
			    		}
			    	}					
				}
			}
		}
	}
}

// checkboxes on designer forms need to be present even if they are unchecked.
// Use hidden field.
function ss_saveCheckBoxValue(box, hiddenFieldId) {
	var hiddenField = document.getElementById(hiddenFieldId);
	var cbChecked = box.checked;
	if (cbChecked) hiddenField.value="true";
	else hiddenField.value="false";

	// Did the user just check the notify assignee/attendee checkbox on
	// a task/calendar entry?
	if (cbChecked &&
			((hiddenFieldId == "hidden_attendee_notify") ||		// Appointment.
			 (hiddenFieldId == "hidden_assignment_notify"))) {	// Task.
		// Yes! Can we access the subject, its default and the title?
		var eSubject        = document.getElementById("_sendMail_subject");
		var eSubjectDefault = document.getElementById("_sendMail_subject_default");
		var eTitle          = document.getElementById("title");
		var sTitle          = ((null == eTitle) ? "" : eTitle.value);
		if (eSubject && eSubjectDefault && eTitle && sTitle.length) {
			// Yes! Does the subject still contain the default?
			if (eSubject.value == eSubjectDefault.value) {
				// Yes! Default the email subject to the title.
				eSubject.value = sTitle;
			}
		}
	}
}
function ss_showAttachmentVersions(prefix, start, end) {
	if (!document.getElementById) {
		return;
	}
	var rowObj = document.getElementById(prefix + start + "n");
	if (rowObj) {
		rowObj.style.display = 'none';
		rowObj.style.visibility = 'hidden';
	}
	var more = start < end;
	var count = start;
	while (more) {
		var rowObj = document.getElementById(prefix + count);
		if (!rowObj) {
			// Call the routines that want to be called on layout changes
		    ssf_onLayoutChange();
			return;
		}
		rowObj.style.display = 'block';
		rowObj.style.visibility = 'visible';
		more = count < end;
		count++;
	}
	var rowObj = document.getElementById(prefix + count + "n");
	if (rowObj) {
		rowObj.style.display = 'block';
		rowObj.style.visibility = 'visible';
	}	
	// Call the routines that want to be called on layout changes
    ssf_onLayoutChange();
}



// Routine to show or hide the sidebar
function ss_showHideSidebar(namespace) {
	var divObj = self.document.getElementById('ss_sidebarDiv'+namespace);
	if (divObj == null) return;
	var tdObj = self.document.getElementById('ss_sidebarTd'+namespace);
	var sidebarHide = self.document.getElementById('ss_sidebarHide'+namespace);
	var sidebarShow = self.document.getElementById('ss_sidebarShow'+namespace);
	var sidebarVisibility = "";
	if (divObj.style.display == 'block') {
		// Hide it
   		dojo.fadeOut({node: divObj, end: 0, delay: 400, onEnd: function() {
		    	divObj.style.visibility = "hidden";
		    	divObj.style.display = "none";
				tdObj.className = '';
				sidebarShow.style.display = 'none';
				sidebarHide.style.display = 'block';
				ssf_onLayoutChange();
   		}}).play();
   		sidebarVisibility = "none";
	} else {
		// Show it
		tdObj.className = 'ss_view_sidebar';
    	ss_setOpacity(divObj, 0);
		divObj.style.display = 'block';
    	divObj.style.visibility = "visible";
    	divObj.focus();
	    dojo.fadeIn({node: divObj, start:0, end:1.0, delay:400}).play();
		sidebarShow.style.display = 'block'
		sidebarHide.style.display = 'none'
		sidebarVisibility = "block"
	}
	ssf_onLayoutChange()
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"set_sidebar_visibility", visibility:sidebarVisibility}, "");
	var ajaxRequest = new ss_AjaxRequest(url); // Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  // Send the request
}

// Routine to hide the sunburst
function ss_hideSunburst(s_id, s_binderId) {
	var divObj = self.document.getElementById('ss_sunburstDiv'+s_binderId+'_'+s_id);
	var titleObj = self.document.getElementById('folderLineSeen_' + s_id);
	if (titleObj != null) {
		var cn = titleObj.className;
		titleObj.className = ss_replaceSubStrAll(cn, "ss_unseen", "")
	}

	// Hide it
	if (divObj != null) {
		dojo.fadeOut({node: divObj, end: 0, delay: 400, onEnd: function() {
			    	divObj.style.visibility = "hidden";
			    	divObj.style.display = "none";
		}}).play();
	}

	ssf_onLayoutChange()
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, 
		{operation:"set_sunburst_visibility", 
		entryId:s_id,
		binderId:s_binderId}, "");
	var ajaxRequest = new ss_AjaxRequest(url); // Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  // Send the request
}

// Routine to get the current url and post it to the login controller so we can
// return here after logging in
function ss_requestLogin(obj, binderId, userWorkspaceId, userName) {
	// If we are looking at the guest user workspace, don't return here. Go to
	// the new user's workspace
	if (userName == "guest" && binderId == userWorkspaceId) return true;
	var formObj = ss_findOwningElement(obj, "form");
	if (formObj == null) return true;
	formObj.url.value = self.location.href;
	formObj.action = obj.href; 
	formObj.submit();
	return false;
}

function ss_setClass(selector, className) {
	if (selector && selector.nodeType) {
		var obj = selector;
	} else {
		var obj = document.getElementById(selector);
	}
    if (!obj) return;
	if (obj != null) obj.className = className;
}

function ss_getTimeZoneDate(date, timeZoneOffset) {
	var u = date.getTime();
	var o = 0 - (date.getTimezoneOffset() * 60 * 1000);
	var timeZoneTime = u - (o - timeZoneOffset);
	return new Date(timeZoneTime);
}

function ss_getTimeZoneTime(hours, minutes, timeZoneOffset) {
	var date = new Date();
	date.setHours(hours);
	date.setMinutes(minutes);
	date = ss_getTimeZoneDate(date, timeZoneOffset);
	return {hours: date.getHours(), minutes: date.getMinutes()};
}

function ss_printSchedulerTime(hoursObjId, minutesObjId, paneObjId, timeZoneOffset, locale) {
	if (!document.getElementById || !hoursObjId || !minutesObjId || !paneObjId || typeof timeZoneOffset === undefined) {
		return;
	}
	var hoursObj = document.getElementById(hoursObjId);
	var minutesObj = document.getElementById(minutesObjId);	
	var paneObj = document.getElementById(paneObjId);
	if (!hoursObj|| !minutesObj || !paneObj) {
		return;
	}
	var hours = hoursObj.options[hoursObj.selectedIndex].value;
	var minutes = minutesObj.options[minutesObj.selectedIndex].value;
	
	var date = new Date();
	// It is assumed that the hours and minutes obtained from the select
	// controls are in GMT.
	// That is why we call date.setUTCxxx()
	date.setUTCHours(hours);
	date.setUTCMinutes(minutes);
	date = ss_getTimeZoneDate(date, timeZoneOffset);
	
	paneObj.innerHTML = dojo.date.locale.format(date, {formatLength:"short", timePattern:"", selector:"time", locale: locale});
}

function ss_FileUploadProgressBar(container) {

	var _MAX_PROGRESS_TO_SHOW_BAR = 20;

	var _containerId = "ss_uploadFileProgressBar";

	var _container = false;
	
	if (container) {
		_container = container;
	}
	
	var _progressBar = false;
	
	var _timeAndSpeed= false;
	
	var _initialized = false;
	
	var _progress = false;
	
	/* Returns true if needs more updates or false otherwise. */
	/*
	 * Parameters: progress - how many % done mbytes_read - bytes already read
	 * (in MB) content_length - bytes total to transfer (in MB) speed - upload
	 * speed (in kB/sec) left_time - approximated left time (in sec)
	 * running_time - time already gone (in sec) legendProgress - example: "0:01
	 * (at 120kB/sec)" legendTimeAndSpeed - example: "100 MB / 1000 MB ( 10% )"
	 */
	this.update = function(data) {
		if (!_initialized && data && data.progress > _MAX_PROGRESS_TO_SHOW_BAR) {
			// it's fast upload, don't show progress bar at all
			return false;
		}
		
		if (!_initialized && data && data.progress <= _MAX_PROGRESS_TO_SHOW_BAR) {
			_initProgressBar();
		}
		
		if (data) {
			_showProgress(data.progress, data.legendTimeAndSpeed, data.legendProgress);
		}
		if (data && data.progress < 100) {
			return true;
		}

		return false;
	}
	
	function _showProgress(progress, legendTimeAndSpeed, legendProgress) {
		if (_progressBar && typeof progress !== undefined) {
			_progressBar.style.width = progress + "%";
		}
		if (_timeAndSpeed && legendTimeAndSpeed) {
			_timeAndSpeed.innerHTML = legendTimeAndSpeed;
		}
		if (_progress && legendProgress) {
			_progress.innerHTML = legendProgress;
		}
	}
	
	function _initProgressBar() {
		if (!document.createElement || !document.getElementById) {
			return;
		}
		
		var newContainer = false;
		if (!_container) {
			_container = document.createElement("div");
			document.getElementsByTagName("body").item(0).appendChild(_container);
					
			_container.className = "ss_fileUploadProgressBarContainer";
			_container.style.position="absolute";
			newContainer = true;
		}
		
		if (!_container.id) {
			_container.id = _containerId;
		}

		var progressBar = document.createElement("div");
		progressBar.className = "ss_progressBar";

		_progressBar = document.createElement("div");

		progressBar.appendChild(_progressBar);
		_container.appendChild(progressBar);

		var table = document.createElement("table");
		var tableBody = document.createElement("tbody");
		var row = document.createElement("tr");
		_timeAndSpeed = document.createElement("td");
		_timeAndSpeed.className = "ss_progressTimeSpeed";
		_progress = document.createElement("td");
		_progress.className = "ss_progress";

		table.appendChild(tableBody);
		tableBody.appendChild(row);
		row.appendChild(_timeAndSpeed);
		row.appendChild(_progress);
		_container.appendChild(table);
	
		
		if (newContainer) {
			ss_showPopupDivCentered(_container.id, null, false);
		} else {
			_container.style.display = "";
		}
		_initialized = true;
	}

}

ss_FileUploadProgressBar.reloadProgressStatus = function(progressBar, url) { 
	dojo.xhrGet({
    	url: url,
		error: function(err) {
		},
		load: function(data) {
					if (progressBar.update(data)) {
			  		  	setTimeout(function() {
			  		  		if (window.ss_FileUploadProgressBar.reloadProgressStatus) {// prevent
																						// error
																						// if
																						// window
																						// already
																						// unloaded
				  		  		ss_FileUploadProgressBar.reloadProgressStatus(progressBar, url);
				  		  	}
			  		  	}, 1200);
		  		  	}
				},
				handleAs: "json-comment-filtered",
				preventCache: true
	});
}

function ss_saveWindowHeightInServer(height, communicationIframeName) {
	var urlParams = {operation:"set_session_data", operation2:windowHeight, operation3:height};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);

	dojo.xhrGet({
    	url: url,
		error: function(err) {
			alert(ss_not_logged_in);
		},
		load: function(data) {
			// Signal that the iframe height in the portlet needs to be changed
			parent.frames[communicationIframeName].location.href = ss_rootPath + 'js/forum/null.html?'+ ss_random++;
		},
		preventCache: true
	});
}

// Attributes support
function ss_deleteAttributeSet(obj, id) {
	if (obj.tagName != 'input') return false;
	// Find the form then specify the set to be deleted
	var formObj = ss_findOwningElement(obj, "form");
	if (formObj != null) {
		formObj[id].value = 'on';
	}
	return true;
}
function ss_deleteAttribute(obj, id) {
	// Find the row and delete it from the table
	var rowObj = document.getElementById("row_"+id);
	if (rowObj != null) rowObj.parentNode.removeChild(rowObj);
	return true;
}

//Function used in ordering folder columns
function ss_saveColumnOrder(obj, name) {
	var formObj = ss_getContainingForm(obj);
	var hiddenObj = formObj[name];
	hiddenObj.value = "";
	var tableNode = ss_findOwningElement(obj, 'tbody')
	for (var i = 0; i < tableNode.childNodes.length; i++) {
		var node = tableNode.childNodes[i]
		if (node.tagName && node.tagName.toLowerCase() == 'tr') {
			if (hiddenObj.value != '') hiddenObj.value += '|';
			hiddenObj.value += node.id;
		}
	}
}


/**
 * This function will update the given element's text node with the given text.
 */
function updateElementsTextNode(
	element,	// The Element whose text Node is to be updated.
	newText)	// The text to update the Node with.
{
	var	found;
	var	i;
	var kids;
	var	numKids;

	if (null == element)
	{
		return;
	}

	// Find the text node for this element.
	kids    = element.childNodes;
	numKids = kids.length;
	found   = false;
	for (i = 0; ((i < numKids) && (!found)); i += 1)
	{
		// Is this child a text node?
		if (3 == kids[i].nodeType)
		{
			// Yes! Replace its text with the new text.
			kids[i].data = newText;
			found        = true;
		}
	}

	// Did we find a text node?
	if ( !found )
	{
		var	textNode;

		// No! Create one and add it to the element.
		textNode = element.ownerDocument.createTextNode( newText );
		element.appendChild( textNode );
	}
}// end updateElementsTextNode()

// General purpose <INPUT> widget utilities.
function input_setSelectionRange(input, selectionStart, selectionEnd) {
  if (input.setSelectionRange) {
    input.focus();
    input.setSelectionRange(selectionStart, selectionEnd);
  }
  else if (input.createTextRange) {
    var range = input.createTextRange();
    range.collapse(true);
    range.moveEnd('character', selectionEnd);
    range.moveStart('character', selectionStart);
    range.select();
  }
}
function input_setCaretToEnd(input) {
  input_setSelectionRange(input, input.value.length, input.value.length);
}
function input_setCaretToBegin(input) {
  input_setSelectionRange(input, 0, 0);
}
function input_setCaretToPos(input, pos) {
  input_setSelectionRange(input, pos, pos);
}

// Mailto: replacement routines
function ss_showEmailLinks() {
	var mailtoElements = document.getElementsByTagName('ssmailto')
	while (mailtoElements != null && mailtoElements.length > 0) {
		var mailtoName = mailtoElements[0].getAttribute("name");
		mailtoName = ss_replaceSubStrAll(mailtoName, "<", "&lt;")
		mailtoName = ss_replaceSubStrAll(mailtoName, ">", "&gt;")
		var mailtoHost = mailtoElements[0].getAttribute("host");
		mailtoHost = ss_replaceSubStrAll(mailtoHost, "<", "&lt;")
		mailtoHost = ss_replaceSubStrAll(mailtoHost, ">", "&gt;")
		var mailtoNoLink = mailtoElements[0].getAttribute("noLink");
		var aNode = mailtoElements[0].parentNode;
		if (mailtoNoLink == "true") {
			aNode.innerHTML = mailtoName+"@"+mailtoHost;
		} else {
			aNode.setAttribute("href", "mailto:"+mailtoName+"@"+mailtoHost);
			aNode.removeChild(mailtoElements[0]);
			spanObj = self.document.createElement("span");
			spanObj.innerHTML = mailtoName+"@"+mailtoHost;
			aNode.appendChild(spanObj);
		}
		mailtoElements = document.getElementsByTagName('ssmailto')
	}
}

// Session timeout
function ss_startSessionTimoutTimer(maxInactiveInterval) {
	// If we're running in the GWT UI...
	if (ss_isGwtUIActive) {
		// ...these session timeout timers are meaningless because of
		// ...the extensive use of AJAX calls.  Ignore them.
		return;
	}
	
	if (typeof maxInactiveInterval == 'undefined' || maxInactiveInterval == '') return;
	var maxInt = parseInt(maxInactiveInterval);
	
	if (maxInt > 2000*60 || maxInt <= 0) return;
	var timeToWarn = parseInt((maxInt - 5*60)*1000);
	setTimeout("ss_resetSessionTimeoutTimer('"+maxInactiveInterval+"');", timeToWarn)
}
function ss_resetSessionTimeoutTimer(maxInactiveInterval) {
	// If we're running in the GWT UI...
	if (ss_isGwtUIActive) {
		// ...these session timeout timers are meaningless because of
		// ...the extensive use of AJAX calls.  Ignore them.
		return;
	}
	
	var now = new Date();
	if (confirm(ss_sessionTimeoutText + "\n  (" + now.toLocaleString() + ")")) {
		ss_setupStatusMessageDiv();
		ss_random++;
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"check_if_logged_in", rn:ss_random});
		var ajaxRequest = new ss_AjaxRequest(url); // Create AjaxRequest object
		ajaxRequest.setData("maxInactiveInterval", maxInactiveInterval);
		ajaxRequest.setPostRequest(ss_resetSessionTimeoutTimer2);	
		ajaxRequest.sendRequest();
	}
}
function ss_resetSessionTimeoutTimer2(obj) {
	// If we're running in the GWT UI...
	if (ss_isGwtUIActive) {
		// ...these session timeout timers are meaningless because of
		// ...the extensive use of AJAX calls.  Ignore them.
		return;
	}
	
	var maxInactiveInterval = obj.getData("maxInactiveInterval");
	// See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else {
		ss_startSessionTimoutTimer(maxInactiveInterval);
	}
}
function ss_trim(str) {
    // skip leading and trailing whitespace
    // and return everything in between
    var x=str;
    x=x.replace(/^\s*(.*)/, "$1");
    x=x.replace(/(.*?)\s*$/, "$1");
    return x;
};


function ss_urlEncode(str) {
	var	r;
	if (encodeURIComponent) {
		r = encodeURIComponent(str);
	}
	else {
		r = escape(str,1);
		r = ss_replaceSubStrAll(r, "+", "%2B");
		r = ss_replaceSubStrAll(r, " ", "+");
	}
	
	return r;
}
   
function ss_urlDecode(str) {
	str = ss_replaceSubStrAll(str, "+", " ");
	if (decodeURIComponent) {
		return decodeURIComponent(str);
	}
	else {
		return unescape(str);
	}
}
   
function ss_pack(list) {
	if(!list || !list.length || list.length<1) {
		return "PP";
	}
   
	var s = "P:" + ss_urlEncode(list[0]);
	for(var i=1; i<list.length; i++) {
		s += ":" + ss_urlEncode(list[i]);
	}
   
	return s + "P";
}
   
function ss_unpack(s) {
	if(!s) {
		return null;
	}
   
	if(s == "PP") {
		return new Array();
	}
   
	if(s.length < 3 || s.charAt(0) != 'P' || s.charAt(1) != ':' || s.charAt(s.length - 1) != 'P') {
		var list = new Array();
		list[0] = s;
		return list;
	}
   
	s = s.substring(2, s.length-1);
   
	var tmplist = s.split(":");
	var list = new Array();
	for(var i=0; i<tmplist.length; i++) {
		list[i] = ss_urlDecode(tmplist[i]);
	}
   
	return list;
}

function ss_checkIfNumber(s) {
	var pattern1 = new RegExp("^-?[0-9]*\\.?[0-9]*$");
	var pattern2 = new RegExp("^-?\\.$");
	if (pattern2.test(ss_trim(s))) return false;
	return pattern1.test(ss_trim(s));
}

function ss_checkIfInteger(s) {
	var pattern1 = new RegExp("^[0-9]+$");
	return pattern1.test(ss_trim(s));
}

function ss_setCookie ( name, value, exp_y, exp_m, exp_d, path, domain, secure ) {
	var cookie_string = name + "=" + escape ( value );

	if ( exp_y ) {
		var expires = new Date ( exp_y, exp_m, exp_d );
		cookie_string += "; expires=" + expires.toGMTString();
	}
	if ( path )
		cookie_string += "; path=" + escape ( path );
	if ( domain )
		cookie_string += "; domain=" + escape ( domain );  
	if ( secure )
		cookie_string += "; secure";  
	document.cookie = cookie_string;
}
function ss_deleteCookie ( cookie_name ) {
	var cookie_date = new Date ( );  // current date & time
	cookie_date.setTime ( cookie_date.getTime() - 1 );
	document.cookie = cookie_name += "=; expires=" + cookie_date.toGMTString();
}
function ss_getCookie ( cookie_name ) {
	var results = document.cookie.match ( '(^|;) ?' + cookie_name + '=([^;]*)(;|$)' );
	if ( results )
		return ( unescape ( results[2] ) );
	else
		return null;
}

function ss_escapeSQ(s) {
	return ss_replaceSubStrAll(s, "'", "\\'");
}
function ss_checkIfVisible(obj) {
	// Make sure this element is visible
	var loopCounter = 0;
	while (obj != null) {
		if (obj.style && obj.style.display && obj.style.display == 'none' || 
				obj.style && obj.style.visibility && obj.style.visibility == 'hidden') return false;
		obj = obj.parentNode;
		loopCounter++;
		if (loopCounter > 1000) return true;
	}
	return true;
}

/*
 * Called to force the GWT content IFRAME to resize itself based on it's
 * contents.
 */
function resizeGwtContent(reason) {
	if (ss_isGwtUIActive) {
		if ("function" == typeof window.top.ss_setWorkareaIframeSize) {
			//ss_debug("resizeGwtContent( reason:  '" + reason + "' )");
			window.top.ss_setWorkareaIframeSize();
		}
		ss_showContentFrame();
	}
}

/*
 * Called to allow the GWT UI to do whatever needs to be done from a UI
 * perspective to prepare for an pending context switch.
 */
function preContextSwitch() {
	// If there's a GWT UI ss_preContextSwitch() function defined...
	if (typeof window.top.ss_preContextSwitch != "undefined") {
		// ...call it.
		window.top.ss_preContextSwitch();
	}
}

// Routine to confirm the deletion of multiple file versions
function ss_deleteMultipleFileVersions(formId, confirmText) {
	var formObj = self.document.getElementById(formId);
	if (confirm(ss_deleteFileVersionsConfirmText)) {
		formObj.submit();
	} else {
		return false;
	}
}

function ss_launchSimpleProfile(element, workspaceId, userName, errorText) {
	if(window.top.ss_invokeSimpleProfile != null ) {
		window.top.ss_invokeSimpleProfile(element, workspaceId, userName);
	} else {
		alert(errorText);
	}
	return;
}

/*
 * Deletes the character at index i from String s.
 */
function delCFromS(s, i) {
	// Can we delete the i'th character from s?
	if ((null != s) && (i < s.length)) {
		var	sA;
		var	sB;


		// Yes! Delete it.
		sB = s.substring(0, i );	// Substring before the i'th character.
		sA = s.substring(i + 1);	// Substring after the i'th character.
		return (sB + sA);
	}


	// If we get here, we couldn't delete the i'th character from s.
	// Return null.
	return null;
}

/*
 * Selects the text in and gives the focus to a widget.
 */
function focusAndSelect(eElement) {
	// Were we given an Element to select and give the focus to?
	if (eElement) {
		// Do it.
		if (eElement.focus)  eElement.focus();
		if (eElement.select) eElement.select();
	}
}

/*
 * Selects the text in and gives the focus to a widget.
 */
function focusAndSelectByID(sID) {
	focusAndSelect(document.getElementById(sID));
}

/*
 * Selects the text in and gives the focus to a widget.
 */
function focusAndSelectByID_Delayed(eID) {
	window.setTimeout("focusAndSelectByID('" + eID + "' )", 500);
}

/*
 * Does a string contain a value?
 */
function hasString(sValue) {
	return((null != sValue) && (0 < sValue.length));
}

/*
 * Is an element hidden?
 */
function isHidden(e) {
	return(e.type && ("hidden" == e.type));
}

// Modes handled by intRequiredBlur().
var	INT_MODE_ALL		= 0;		// Any integer, position or negative.
var	INT_MODE_GE_ZERO	= 1;		// Any integer >= 0.
var	INT_MODE_GT_ZERO	= 2;		// Any integer > 0.
var	INT_MODE_LE_ZERO	= 3;		// Any integer <= 0.
var	INT_MODE_LT_ZERO	= 4;		// Any integer < 0.
var	INT_MODE_NE_ZERO	= 5;		// Any integer != 0.

/*
 * Set in a widget's onBlur event handler to mark it as requiring an integer
 * value.
 * 
 * Note that we brute force this validation because building a validator around
 * isNaN allows numbers to be in scientific notation, which are invalid as
 * integers.
 */
function intRequiredBlur(eWidget, mode, sFixupMsg) {
	var	aM;
	var	c;
	var	i;
	var	l;
	var	s;
	var	sIn;


	// Validate the mode that this integer can be.
	if (!mode) mode = INT_MODE_ALL;
	else       mode = Number(mode);
	switch (mode) {
	default:                mode = INT_MODE_ALL;	// Fall through.
	case INT_MODE_ALL:      aM   = true;  break;
	case INT_MODE_GE_ZERO:  aM   = false; break;
	case INT_MODE_GT_ZERO:  aM   = false; break;
	case INT_MODE_LE_ZERO:  aM   = true;  break;
	case INT_MODE_LT_ZERO:  aM   = true;  break;
	case INT_MODE_NE_ZERO:  aM   = true;  break;
	}


	// Scan the widget's value.
	s   =
	sIn = eWidget.value;
	if (null == s) s = "";
	l = s.length;
	for (i = 0; i < l; i += 1) {
		// What character are we on?
		c = s.charAt(i);
		switch (c) {
		case '0':  case '1':  case '2':  case '3':  case '4':
		case '5':  case '6':  case '7':  case '8':  case '9':
			// A digit. Leave it alone.
			break;


		case '-':
			// A minus sign. Is this the first character when a minus
			// sign is allowed?
			if ((0 == i) && aM) {
				// Yes! Leave it alone.
				break;
			}

			// * * * * * * * * * * * * * * * * * * * * * * * * * * * //
			// Fall through and handle with the default case. This //
			// will delete this invalid minus sign. //
			// * * * * * * * * * * * * * * * * * * * * * * * * * * * //


		default:
			// This character isn't part of an integer. Delete it...
			s = delCFromS(s, i);

			// ...adjust the index and length to account for the
			// ...deletion...
			i -= 1; l -= 1;

			// ...and continue on.
			break;
		}
	}


	// After validating, do we still have a value?
	if (hasString(s)) {
		// Yes! Is it valid for the INT_MODE_... that was specified?
		i = Number(s);
		switch (mode) {
		case INT_MODE_ALL:                          break;	// Valid.
		case INT_MODE_GE_ZERO:  if (0 >  i) s = ""; break;	// Invalid.
		case INT_MODE_GT_ZERO:  if (0 >= i) s = ""; break;	// Invalid.
		case INT_MODE_LE_ZERO:  if (0 <  i) s = ""; break;	// Invalid.
		case INT_MODE_LT_ZERO:  if (0 <= i) s = ""; break;	// Invalid.
		case INT_MODE_NE_ZERO:  if (0 == i) s = ""; break;	// Invalid.
		}
	}


	// Did we change the value of the string we pulled from eWidget?
	if (s != sIn) {
		var	eWidgetHidden;
		
		
		// Yes! Store it...
		eWidget.value = s;
		eWidgetHidden = isHidden(eWidget);
		if (!eWidgetHidden) focusAndSelect(eWidget);

		// ...and if we supposed to tell the user that we changed
		// ...the value...
		if (sFixupMsg && hasString(sFixupMsg)) {
			// ...tell them...
			alert(sFixupMsg);

			// ...and give the widget the focus again AFTER having
			// ...displayed the fixup message.
			if (!eWidgetHidden) {
				var	sID;


				sID = eWidget.getAttribute("id");
				if (hasString(sID))
					 focusAndSelectByID_Delayed(sID    );
				else focusAndSelect(            eWidget);
			}
		}
	}
}

/*
 * If a checkbox element is checked, confirms with the user if they
 * want to proceed.
 */
function ss_confirmIfCBChecked(cbId, confirmation) {
	var reply;
	if (document.getElementById(cbId).checked)
         reply = confirm(confirmation);
	else reply = true;
	return reply;
}

/*
 * WebKit Pasting Bug Cleanup.
 * 
 * The problem is that when pasting plain text into a WebKit based
 * browser, EOL characters are NOT being handled correctly.
 * 
 * This is part of the fix for Bugzilla bug#625658 against Teaming.
 * 
 * As a reference as to how this issue is being addressed here, see
 * bug#2866317 against TinyMCE at:
 * 
 * 		http://sourceforge.net/tracker/index.php?func=detail&aid=2866317&group_id=103281&atid=635682
 */
function TinyMCEWebKitPasteFixup(t, v) {
	switch (t)
	{
	case "paste_postprocess":
		var fixThis = /<div id="_mcePaste[^>]*>(?!<div>)([\s\S]*)<\/div>([\s\S]*)$/i;
		v = v.replace(fixThis, '<p>$1</p>');
		
		fixThis = /<div id="_mcePaste[^>]*>/gi;
		v = v.replace(fixThis, '<p>');
		break;
	}
	return v;
}

/*
 * Sets the content to a specific URL.
 */
function ss_setContentLocation(url) {
	if (window.top.ss_gotoContentUrl) {
		window.top.ss_gotoContentUrl(url);
	}
	else {
		window.top.gwtContentIframe.location.href = url;		
	}
}

/*
 * Sets the opener's content to a specific URL.
 */
function ss_setOpenerLocation(url) {
	if (self.opener.top.ss_gotoContentUrl) {
		self.opener.top.ss_gotoContentUrl(url);
	}
	else {
		self.opener.top.gwtContentIframe.location.href = url;
	}
}

/*
 * Sets the content to a specific URL.
 */
function ss_setSelfLocation(url) {
	if (window.top.ss_gotoContentUrl) {
		window.top.ss_gotoContentUrl(url);
	}
	else {
		self.location.href = url;
	}
}

/*
 * Sets a URL into a frame with the frame disconnected from the DOM.
 * 
 * The logic here detaches the IFRAME from the DOM before setting the
 * URL.   This is done to address a problem with IFRAME's and GWT
 * history as per the following web site:
 *  - - - - - - - - - - - - - - - - - - -
 * http://owenrh.blogspot.com/2011/04/gwt-iframes-and-history.html
 * - - - - - Copied From There - - - - -
 * So I ran into this problem the other day, and thought I'd 
 * document the solution I found for it.
 *  
 * We've got a scenario on a project where we embed an IFRAME into
 * the page and load content into it. The problem is that the every
 * time the src attribute on the IFRAME was set, the browser
 * created a non-GWT history event.  This meant that when the user
 * pressed back there would be spurious history events, stopping
 * anything from happening, or they'd have to press back multiple
 * times instead of once.
 *  
 * After some investigation I discovered the solution. Basically,
 * creating a GWT Frame widget, setting the URL in the constructor,
 * and adding it to the page each time the content changed didn't
 * create these history events.  The history events seem to be
 * related to call the Frame.setSrc( ) method, or setting the src
 * attribute on the IFRAME element in the HTML.
 *  - - - - - - - - - - - - - - - - - - -
 */
function ss_setUrlInFrame(frame, url) {
	var frame_Parent = frame.parentNode;
	if (null != frame_Parent) {
		frame_Parent.removeChild(frame);
	}
	
	frame.src = url
	
	if (null != frame_Parent) {
		frame_Parent.appendChild(frame);
	}
}

/*
 * Called to fixup any <A>'s in a listing frame so that they go through
 * the top document's load handler (instead of the <IFRAME>'s.)  This
 * is necessary to ensure the GWT code 'sees' the URLs that flow
 * through these frames.
 */
function ss_ensureAnchorsTargetTopFrame() {
	var anchors     = self.document.getElementsByTagName("a");
	var count       = anchors.length;
	var patched     = 0;
	var skipHref    = 0;
	var skipNohref  = 0;
	var skipOnclick = 0;
	var skipTarget  = 0;
	for (var i = 0; i < count; i += 1) {
		// If the <A> already has a 'target="..."'...
		var anchor = anchors[i];
		var target = anchor.getAttribute("target");
		if (target && (0 < target.length)) {
			// ...skip it.
			skipTarget += 1;
			continue;
		}
		
		// If the <A> has an onclick handler...
		var onclick = anchor.getAttribute("onclick");
		if (onclick && (0 < onclick.length)) {
			// ...skip it.
			skipOnclick += 1;
			continue;
		}
		
		// Does the <A> have a link?
		var href = anchor.getAttribute("href");
		if (href && (0 < href.length)) {
			// Yes!  If it doesn't need patching...
			if (0 != href.indexOf("http")) {
				// ...skip it.
				skipHref += 1;
				continue;
			}

			// If it's a build_filter URL...
			if (0 < href.indexOf("action=build_filter")) {
				// ...skip it.
				//
				// DRF (20150108):  This is a special case to address
				//    bug#912155.  Without this fix, in the photo album
				//    (which is still JSP), the build filter dialog
				//    would come up in the top frame instead of in the
				//    iframe containing the photo album view.
				continue;
			}

			// If it's a survey navigation URL...
			if (0 < href.indexOf("feature=survey")) {
				// ...skip it.
				//
				// DRF (20150108):  This is a special case to address
				//    bug#910705.  Without this fix, viewing survey
				//    results or returning from viewing them did not
				//    work.
				continue;
			}
			
			// If it's a page navigation URL...
			if (0 < href.indexOf("operation=save_folder_page_info")) {
				// ...skip it.
				//
				// DRF (20150706):  This is a special case to address
				//    bug#929716.  Without this fix, paging through a
				//    photo album did not work.
				continue;
			}
			
			// If it's a change vote URL...
			if ((0 < href.indexOf("operation=changeVote")) || (0 < href.indexOf("operation=viewResults"))) {
				// ...skip it.
				//
				// DRF (20151112):  This is a special case to address
				//    bug#939975.  Without this fix, changing votes or
				//    viewing how others voted in a survey did not
				//    work.
				continue;
			}
			
			// Add a 'target="_top"' to it.
			patched += 1;
			anchor.setAttribute("target", "_top");
		}
		
		else {
			// No!
			skipNohref += 1;
		}
	}

/*
	// Do our counts align?
	var processed = (patched + skipHref + skipNohref + skipOnclick + skipTarget);
	if (count != processed) {
		// No!  Tell the user about the problem.
		alert("ss_ensureAnchorsTargetTopFrame( *Internal Error*):  Counts don't match!");
	}

	// Tell the user what we did.
	alert(
		"ss_ensureAnchorsTargetTopFrame():  added a 'target=\"_top\"' to " + patched + " out of " + count + " <A>'s in the document.  " +
		"Skips:  target: "  + skipTarget  +
		      ", onclick: " + skipOnclick +
		      ", href: "    + skipHref    +
		      ", nohref: "  + skipNohref );
*/
}


dojo.require("dijit.dijit");
dojo.require("dojo.fx");
dojo.require("dojo.io.iframe");
dojo.require("dojox.data.dom");
dojo.require("dojox.fx");
dojo.require("dojox.uuid.generateRandomUuid");
