/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
//Common javascript functions for forum portlets
//
// browser-specific vars

var undefined;
var ss_declaredDivs;
if (!ss_common_loaded || ss_common_loaded == undefined || ss_common_loaded == "undefined" ) {
	var ss_isNSN = (navigator.appName == "Netscape");
	var ss_isNSN4 = ss_isNSN && ((navigator.userAgent.indexOf("Mozilla/4") > -1));
	var ss_isNSN6 = ((navigator.userAgent.indexOf("Netscape6") > -1));
	var ss_isMoz5 = ((navigator.userAgent.indexOf("Mozilla/5") > -1) && !ss_isNSN6);
	var ss_isMacIE = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("Mac") > -1));
	var ss_isIE = ((navigator.userAgent.indexOf("IE ") > -1));
	var ss_isIE6 = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("MSIE 6") > -1));
	
	
	//Random number seed (for building urls that are unique)
	var ss_now = new Date();
	var ss_random = Math.round(Math.random()*ss_now.getTime());
	
	//Files that don't pop-up in a new window when viewing them (space separated)
	var ss_files_that_do_not_pop_up = "doc xls";
	
	//zIndex map
	var ssPortletZ = 5
	var ssLightboxZ = 2000;
	var ssHelpZ = 2000;
	var ssHelpSpotZ = 2001;
	var ssHelpPanelZ = 2003;
	var ssHelpWelcomeZ = 2002;
	var ssMenuZ = 500;
	var ssPopupZ = 600;
	var ssDragOnTopZ = 100000;
	var ssEntryZ = 50;
	var ssDragEntryZ = 400;
	var ssSlidingTableInfoZ = 40;
	var ssDashboardTargetZ = 1000;
	
	//colors (defined at onLoad time by ss_defineColorValues)
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
	var ss_divToBeHidden = new Array;
	var ss_divToBeDelayHidden = new Array;
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
}
var ss_common_loaded = 1;

//Routine called by the body's onLoad event
function ss_onLoadInit() {
    //Call any routines that want to be called at onLoad time
    for (var i = 0; i < ss_onLoadList.length; i++) {
        if (ss_onLoadList[i].initRoutine) {
        	ss_onLoadList[i].initRoutine();
        }
    }
    if (ss_savedOnLoadRoutine != null) {
    	window.onload = ss_savedOnLoadRoutine;
    	if (window.onload != null) window.onload();
    }
    
	//Add the onResize routine to the onresize event
	if (!ss_onResizeRoutineLoaded) {
		ss_onResizeRoutineLoaded = 1;
		ss_savedOnResizeRoutine = window.onresize;
		window.onresize = ssf_onresize_event_handler;
	}
}

//Add the onLoadInit routine to the onload event
if (!ss_onLoadRoutineLoaded) {
	ss_onLoadRoutineLoaded = 1;
	ss_savedOnLoadRoutine = window.onload;
	window.onload = ss_onLoadInit;
}
//Routines to support Ajax
//suggest moving towards ss_get_url so errors can be handled consistantly
//Updated to dojo, result is text/plain
//used to fetch plain data and replace a div
function ss_fetch_div(url, divId, signal) {
	var bindArgs = {
	    	url: url,
			error: function(type, data, evt) {
				alert(ss_not_logged_in);
			},
			load: function(type, data, evt) {
	  		  try {
	  		  	dojo.byId(divId).innerHTML = data;
				//Signal that the layout changed
				if (signal) ssf_onLayoutChange();
		      } catch (e) {alert(e);}
			},
			preventCache: true
	};   
	dojo.io.bind(bindArgs);
}
//suggest moving towards ss_get_url so errors can be handled consistantly
//Updated to dojo, result is text/plain
//used to fetch plain data
function ss_fetch_url(url, callbackRoutine, callbackData, toggleCall) {
	ss_fetch_url_debug("Request to fetch url: " + url)
	eval(toggleCall);
	var bindArgs = {
	    	url: url,
			error: function(type, data, evt) {
				eval(toggleCall);
				alert(data.message);
			},
			load: function(type, data, evt) {
				eval(toggleCall);
			    try {
					ss_fetch_url_debug("received " + data);
					if (callbackRoutine) callbackRoutine(data, callbackData);
				} catch (e) {alert(e);}
				//Signal that the layout changed
				if (ssf_onLayoutChange) setTimeout("ssf_onLayoutChange();", 100);
				
			},
			preventCache: true
	};   
	dojo.io.bind(bindArgs);

}                

function ss_fetch_url_debug(str) {
    //ss_debug(str);
}
//Use dojo to post a form, results in text/json
//When result contains failure, message display
function ss_post(url, formId, callBackRoutine, callbackData, toggleCall) {
	eval(toggleCall);
	var bindArgs = {
    	url: url,
    	formNode: dojo.byId(formId),
		error: function(type, data, evt) {
			eval(toggleCall);
			alert(data.error);
		},
		load: function(type, data, evt) {
			eval(toggleCall);
			if (data.failure) {
				alert(data.failure);
			} else { 
				if (callBackRoutine) callBackRoutine(data, callbackData);
			}
		},
		preventCache: true,				
		mimetype: "text/json",
		method: "post"
	};   
	dojo.io.bind(bindArgs);
}     
//Use dojo to get a url.  Results in text/json. 
//When result contains failure, message display
function ss_get_url(url, callBackRoutine, callbackData, toggleCall) {
	eval(toggleCall);
	var bindArgs = {
    	url: url,
		error: function(type, data, evt) {
			eval(toggleCall);
			alert(data.error);
		},
		load: function(type, data, evt) {
			eval(toggleCall);
			if (data.failure) {
				alert(data.failure);
			} else { 
				if (callBackRoutine) callBackRoutine(data, callbackData);
			}
		},
		preventCache: true,				
		mimetype: "text/json"
	};   
	dojo.io.bind(bindArgs);
}     
function ss_buildAdapterUrl(base, paramMap, action) {
	var url = base;
	if (action && action != "") {
		url += "\&action=" + action;
	} else {
		url += "\&action=__ajax_request";
	}	
	for (var i in paramMap) {
		if (dojo.lang.isArray(paramMap[i])){
			for(var j=0,l=paramMap[i].length; j<l; j++){
				url += "\&" + i + "=" + encodeURIComponent(paramMap[i][j]);
			}
		} else {
			url += "\&" + i + "=" + encodeURIComponent(paramMap[i]);
		}
	}

	return url;
}

//use for callbacks into objects.  Keeps object references from hanging around.
function ss_createDelegate(object, method)
{
    var shim = function() {
          method.apply(object, arguments);
    }
    return shim;
}

//Routine to go to a permalink without actually using the permalink
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
			//See if this should be opened in ss_workarea
			if (typeof ss_workarea_showId != "undefined" && entityType == "workspace") {
				ss_workarea_showId(binderId, "view_ws_listing");
			} else if (typeof ss_workarea_showId != "undefined" && entityType == "user") {
				ss_workarea_showId(binderId, "view_ws_listing", entryId);
			} else if (typeof ss_workarea_showId != "undefined" && entityType == "folder") {
				ss_workarea_showId(binderId, "view_folder_listing");
			} else {
				self.location.href = url;
			}
		}
	} else {
		//See if this should be opened in ss_workarea
		if (typeof ss_workarea_showId != "undefined" && entityType == "workspace") {
			ss_workarea_showId(binderId, "view_ws_listing");
		} else if (typeof ss_workarea_showId != "undefined" && entityType == "user") {
			ss_workarea_showId(binderId, "view_ws_listing", entryId);
		} else if (typeof ss_workarea_showId != "undefined" && entityType == "folder") {
			ss_workarea_showId(binderId, "view_folder_listing");
		} else {
			self.location.href = url;
		}
	}
	return false;
}

//Routine to open a url in the workarea portlet if it exists
function ss_openUrlInWorkarea(url, id, action) {
	if (typeof ss_workarea_showId != "undefined") {
		ss_workarea_showId(id, action);
	} else {
		self.location.href = url;
	}
}

function ss_openUrlInParentWorkarea(url, id, action) {
	if (typeof self.parent.ss_workarea_showId != "undefined") {
		self.parent.ss_workarea_showId(id, action);
	} else {
		self.parent.location.href = url;
	}
}

//Routine to navigate to a point on the navigation list
function ss_navigation_goto(url) {
	if (self.window != self.top) {
		parent.location.reload(true);
		return false;
	} else {
		return(ss_openUrlInPortlet(url));
	}
}

//Routine to open a url in the portlet. This routine determines if the current code is
//  running inside an iframe. It it is, then the url is opened in the parent of the iframe.
//This routine returns "true" without opening the url if the caller is not inside a frame.
//If the caller is in a frame (or iframe), then the routine opens the url in the parent and returns false.
function ss_openUrlInPortlet(url, popup, width, height) {
	if (width == null) width = "";
	if (height == null) height = "";
	//Is this a request to pop up?
	ss_debug('popup = '+popup+', url = '+url)
	if (popup) {
		if (width == '' || height == '') {
			self.window.open(url, "_blank", "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no");
		} else {
			self.window.open(url, "_blank", "directories=no,location=no,menubar=yes,resizable=yes,width="+width+",height="+height+",scrollbars=yes,status=no,toolbar=no");
		}
		return false;
	}
	//Are we at the top window?
	if (self.window != self.top) {
		ss_debug('Not at top window')
		//See if we are in an iframe inside a portlet 
		var windowName = self.window.name    
		if (windowName.indexOf("ss_workareaIframe") == 0) {
			//This is inside the workarea iframe, just let the url be called
			return true;
		} else {
			//We are running inside a portlet iframe
			parent.location.href = url;
		}
		return false
	} else if (self.opener && self.opener != self.window) {
		try {
			self.opener.location.href = url
			setTimeout('self.window.close();', 200)
			return false;
		} catch (e) {
			ss_debug('opener is not addressable anymore, it must have been deleted.')
			return true;
		}
	} else {
		ss_debug('return true')
		return true;
	}
}


//Routine to open a page by following a "title" markup link
function ss_openTitleUrl(obj) {
	//Get the title text
	var spanObj = obj.getElementsByTagName('span').item(0);
	var titleText = ss_replaceSubStrAll(spanObj.innerHTML, "\"", "\&quot;");
	
	//See if the form exists already
	var formObj = document.getElementById('ss_title_url_form');
	if (formObj == null) {
		//Create the form
	    formObj = document.createElement("form");
	    formObj.setAttribute("id", "ss_title_url_form");
	    formObj.setAttribute("name", "ss_title_url_form");
	    var hiddenObj = document.createElement("input");
	    hiddenObj.setAttribute("type", "hidden");
	    hiddenObj.setAttribute("id", "ss_page_title");
	    hiddenObj.setAttribute("name", "page_title");
	    formObj.appendChild(hiddenObj);
		document.getElementsByTagName( "body" ).item(0).appendChild(formObj);
	}
	formObj.action = obj.href;
	formObj.method = "post"
	var pageTitleObj = document.getElementById('ss_page_title');
	pageTitleObj.value = titleText;
	formObj.submit();
	return false;
}

//Routine to open a url in a new window
function ss_openUrlInWindow(obj, windowName, width, height) {
	if (typeof width == "undefined") width = ss_getWindowWidth();
	if (typeof height == "undefined") height = ss_getWindowHeight();
	if (typeof windowName == "undefined" || windowName == "") {
		//There is no window, so open it in this window
		return true;
	} else {
		var url = obj.href
		var win = self.window.open(url, windowName, 'directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no,width='+width+',height='+height)
		if (win.focus) win.focus();
	}
	return false;
}

//Routine to show the permalink url so it can by cut/pasted
function ss_showPermalink(obj) {
	//See if the div exists already
	var divObj = document.getElementById('ss_permalink_display_div');
	var inputObj = document.getElementById('ss_permalink_display_input');
	if (divObj == null) {
		//Create the div
	    divObj = document.createElement("div");
	    divObj.setAttribute("id", "ss_permalink_display_div");
	    divObj.className = "ss_style ss_popupMenu ss_permalink";
	    divObj2 = document.createElement("div");
	    inputObj = document.createElement("input");
	    inputObj.setAttribute("id", "ss_permalink_display_input");
		inputObj.setAttribute("type", "text");
		inputObj.setAttribute("style", "margin: 2px;");
		dojo.event.connect(inputObj, "onclick", function(evt) {
			inputObj.select();
			return false;
	    });		
	    divObj2.setAttribute("align", "right");
	    divObj2.className = "ss_popupMenuClose";
	    aObj = document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDivNone('ss_permalink_display_div');");
	    imgObj = document.createElement("img");
	    imgObj.setAttribute("border", "0");
	    imgObj.setAttribute("src", ss_imagesPath + "pics/sym_s_delete.gif");
	    aObj.appendChild(imgObj);
	    divObj2.appendChild(aObj);
	    divObj.appendChild(divObj2)
	    divObj.appendChild(inputObj)
		document.getElementsByTagName( "body" ).item(0).appendChild(divObj);
	}
	var size = obj.href.length + obj.href.length / 15;
	inputObj.setAttribute("size", size);
	inputObj.setAttribute("maxlength", size);
	inputObj.value = ss_replaceSubStrAll(obj.href, "%20", " ");
	//ss_debug(parseInt(ss_getObjAbsY(obj) + 10) + "px")
	if (divObj.style && divObj.style.visibility && divObj.style.visibility == 'visible') {
		divObj.style.display = 'none';
		divObj.style.visibility = 'hidden';
	} else {
		divObj.style.display = 'block';
		divObj.style.visibility = 'visible';
		divObj.style.top = parseInt(ss_getClickPositionY() + 10) + "px";
		var x = parseInt(ss_getClickPositionX());
		x = x - parseInt((ss_getObjectWidth(divObj) / 3) * 2);
		if (x < 0) x = 0;
		divObj.style.left = x + "px";		
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	
}
//Routine to go to a binder when it is clicked
// id can be a number or a string ending in "_1234" where 1234 is the id
function ss_treeShowId(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = ss_baseBinderUrl;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	//console.log(url);
	self.location.href = url;
	return false;
}

function ss_treeShowIdNoWS(id, obj, action, namespace) {
	if (typeof namespace == "undefined" || namespace == null) namespace = "";
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Try to find the base urls from this namespace 
	var url = "";
	try {
		eval("url = ss_baseBinderUrlNoWS" + namespace)
	} catch(e) {}
	
	//Build a url to go to
	if (url == "") url = ss_baseBinderUrlNoWS;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	//console.log(url);
	self.location.href = url;
	return false;
}


//Routine to fetch a url in a iframe window (for accessibility mode)
function ss_fetchUrlInIframe(url, anchorDivName, width, height) {
    var iframeDivObj = self.document.getElementById("ss_reusableIframeDiv");
    var iframeObj = self.document.getElementById("ss_reusableIframe");
    var anchorDivObj = self.parent.document.getElementById(anchorDivName);
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
	var x = dojo.html.getAbsolutePosition(anchorDivObj, true).x
	var y = dojo.html.getAbsolutePosition(anchorDivObj, true).y
    ss_setObjectTop(iframeDivObj, y);
    ss_setObjectLeft(iframeDivObj, x);
	iframeObj.src = url;
}


//Routine to close a pop-up form window if the cancel button is clicked
//  This routine checks to see if it is in a pop-up or in an iframe
function ss_cancelButtonCloseWindow() {
	if (self == self.parent && self.opener) {
		//This looks like it is a pop-up form
		self.window.close();
		return
	} else if (self.parent) {
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

function ss_reloadOpenerParent(fallBackUrl) {
	//Are we at the top window?
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
	//Are we at the top window?
	if (self.window != self.top) {
		if (parent.ss_reloadUrl && parent.ss_reloadUrl != "") {
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
				self.opener.location.href = fallBackUrl;
				setTimeout('self.window.close();', 200)
			}
		} catch (e) {
			self.location.href = fallBackUrl;
		}
	} else {
		self.location.href = fallBackUrl;
	}
	return false;
}

//Routines to move an object (or a div) to the "body"
//  This is usefull for any absolutly positioned div.
//  The positioning of that div will work correctly when using absolute coordinates.
function ss_moveDivToBody(name) {
	if (document.getElementById(name)) ss_moveObjectToBody(document.getElementById(name));
}
function ss_moveObjectToBody(obj) {
    if (obj && obj.parentNode.tagName.toLowerCase() != 'body') {
    	//move the object to the body tag so it goes to the right x,y
    	var id = obj.id;
    	obj.parentNode.removeChild(obj);
    	document.getElementsByTagName("body").item(0).appendChild(obj);
    }
}

var ss_originalSSParentNodes = new Array();
var ss_originalSSChildNodeNumber = new Array();
function ss_moveDivToTopOfBody(divId) {
	var obj = document.getElementById(divId);
	if (obj == null) return;
    var bodyObj = document.getElementsByTagName("body").item(0);
    if (obj && obj.parentNode.tagName.toLowerCase() != 'body') {
    	//move the object to the body (at the top)
    	var startLeft = ss_getObjAbsX(obj)
    	var startTop = ss_getObjAbsY(obj)
    	ss_originalSSParentNodes[divId] = obj.parentNode;
		ss_originalSSChildNodeNumber[divId] = 0;
		for (var i = 0; i < obj.parentNode.childNodes.length; i++) {
			if (obj.parentNode.childNodes.item(i) == obj) break;
			ss_originalSSChildNodeNumber[divId]++;
		}
		obj.parentNode.removeChild(obj);
		obj.style.top = startTop;
		obj.style.left = startLeft;
		bodyObj.insertBefore(obj, bodyObj.childNodes.item(0));
		obj.style.zIndex = ssPortletZ;
		dojo.lfx.html.slideTo(divId, {top: 0, left:0}, 300, null, ssf_onLayoutChange).play();
    } else {
		if (ss_originalSSParentNodes[divId] != null) {
		
			bodyObj.removeChild(obj);
			if (ss_originalSSParentNodes[divId].childNodes.length <= ss_originalSSChildNodeNumber[divId]) {
				ss_originalSSParentNodes[divId].appendChild(obj);
			} else {
				ss_originalSSParentNodes[divId].insertBefore(obj, ss_originalSSParentNodes[divId].childNodes.item(parseInt(ss_originalSSChildNodeNumber[divId] + 1)))
			}
	    	var startLeft = parseInt(0 - parseInt(ss_getObjAbsX(obj)))
	    	var startTop = parseInt(0 - parseInt(ss_getObjAbsY(obj)))
	    	var endLeft = ss_getObjectLeft(obj)
	    	var endTop = ss_getObjectTop(obj)
	    	obj.style.top = startTop;
	    	obj.style.left = startLeft;
			dojo.lfx.html.slideTo(divId, {top: endTop, left: endLeft}, 300, null, ssf_onLayoutChange).play();
		}
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
	
	//Also signal that a resize might have been done
	ssf_onresize_event_handler();
}

//Functions to save the user status
function ss_updateStatusSoon(obj, evt) {
	if ((typeof evt.which == "undefined" || !evt.which) && typeof event == "undefined") return;
	
	ss_statusObj = obj;
	if (ss_statusTimer != null) {
		clearTimeout(ss_statusTimer)
		ss_statusTimer = null;
	}
    var charCode = (evt.which) ? evt.which : event.keyCode
    if (charCode == 10 || charCode == 13) {
    	ss_updateStatusNow(obj)
    } else {
		ss_setStatusBackground(obj, 'focus');
    }
}
function ss_updateStatusNowAccessible(id) {
	//This is the id of the text box
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
	    if (ss_statusCurrent != obj.value) {
			ss_statusCurrent = obj.value;
			var status = ss_replaceSubStrAll(obj.value, "\"", "&quot;");
			
			ss_setupStatusMessageDiv();
			var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_user_status", status:status}, "");
			var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
			ajaxRequest.setPostRequest(ss_postRequestAlertError);
			ajaxRequest.sendRequest();  //Send the request
			setTimeout('ss_flashStatus();', 200);
		}
		ss_setStatusBackground(obj, 'blur');
	}
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
		if (ss_statusObj != null) ss_statusTimer = setTimeout('ss_updateStatusNow(ss_statusObj);', 10000);
	}
	if (op == 'mouseOver') {
		obj.style.backgroundColor = '#ffffff';
		ss_statusOnMouseOver = true;
	}
	if (op == 'blur') {
		obj.style.backgroundColor = '#cccccc';
		ss_statusOnMouseOver = false;
	}
}
function ss_flashStatus() {
	ss_statusObj.style.backgroundColor = "#00ADEF";
	setTimeout("ss_setStatusBackground(ss_statusObj, 'blur');", 300);
}
function ss_setStatusBackgroundCheck(obj) {
	if (ss_statusOnMouseOver && ss_statusTimer == null) ss_setStatusBackground(obj, 'blur');
}

function ss_trackThisBinder(id, namespace) {
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"track_this_binder", binderId:id, namespace:namespace}, "__ajax_relevance");
	ss_fetch_url(url, ss_postRequestTrackThis, namespace)
}
function ss_postRequestTrackThis(s, namespace) {
	var divObjT = self.document.getElementById("ss_track_this_ok" + namespace);
	if (divObjT != null) {
		divObjT.innerHTML = s;
		divObjT.style.visibility = 'visible';
		divObjT.style.display = 'inline';
		setTimeout("ss_hideDivNone('ss_track_this_ok" + namespace + "');", 2000);
	}
}
function ss_trackedItemsDelete(obj, id) {
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"track_this_binder_delete", binderId:id}, "__ajax_relevance");
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  //Send the request
	
	//Delete the row from the parent table
	var trObj = obj.parentNode.parentNode
	trObj.parentNode.removeChild(trObj)
}

function ss_trackedPeopleDelete(obj, id) {
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"track_this_person_delete", binderId:id}, "__ajax_relevance");
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  //Send the request
	
	//Delete the row from the parent table
	var trObj = obj.parentNode.parentNode
	trObj.parentNode.removeChild(trObj)
}

function ss_showHideRelevanceCanvas(namespace) {
	var divObj = self.document.getElementById('relevanceCanvas_'+namespace)
	var showBtn = self.document.getElementById('ss_relevanceShowButton'+namespace)
	var hideBtn = self.document.getElementById('ss_relevanceHideButton'+namespace)
	if (typeof divObj.style.display != 'undefined' && divObj.style.display == 'none') {
		ss_showDiv('relevanceCanvas_'+namespace)
		showBtn.style.display = 'none';
		hideBtn.style.display = 'block';
	} else {
		ss_hideDivNone('relevanceCanvas_'+namespace)
		showBtn.style.display = 'block';
		hideBtn.style.display = 'none';
	}
}

function ss_selectRelevanceTab(obj, type, binderId, namespace) {
	//Clear "current" tab
	var currentTab = window["ss_relevanceTabCurrent_"+namespace];
	if (currentTab != null) {
		currentTab.parentNode.className = "";
	}
	if (obj != null) {
		window["ss_relevanceTabCurrent_"+namespace] = obj;
		obj.parentNode.className = "ss_tabsCCurrent";
	}
	//Switch to the new tab
	var url = window["ss_relevanceAjaxUrl"+namespace];
	url = ss_replaceSubStr(url, "ss_typePlaceHolder", type);
	url = ss_replaceSubStr(url, "ss_binderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ss_pagePlaceHolder", "0");
	url = ss_replaceSubStr(url, "ss_rnPlaceHolder", ss_random++);
	if (ss_userDisplayStyle == "accessible") {
		//If in accessible mode, just jump to the url directly
		self.location.href = url;
	} else {
		ss_fetch_url(url, ss_showRelevanceTab, namespace)
	}
}
function ss_showRelevanceTab(s, namespace) {
	var canvasObj = self.document.getElementById("relevanceCanvas_" + namespace);
	canvasObj.innerHTML = s;
	canvasObj.style.display = 'block'
	canvasObj.style.visibility = 'visible'
	//Make sure the hide button is visible
	var showBtn = self.document.getElementById('ss_relevanceShowButton'+namespace)
	var hideBtn = self.document.getElementById('ss_relevanceHideButton'+namespace)
	showBtn.style.display = 'none';
	hideBtn.style.display = 'block';
	ss_executeJavascript(canvasObj); // calendar view is generated in js
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_executeJavascript(xmlNode) {
    var scripts = xmlNode.getElementsByTagName("script");
    for (var i = 0; i < scripts.length; i++) {
        var script = scripts[i];
        if (script.getAttribute("type") == "text/javascript") {
           // var js = script.firstChild.nodeValue;
            eval(script.innerHTML);
        }
    }
}

function ss_showDashboardPage(binderId, type, op, currentPage, direction, divId, namespace) {
	if (currentPage == "") currentPage = "0";
	var page = parseInt(currentPage);
	if (direction == 'next') page = page + 1;
	if (direction == 'previous') page = page - 1;
	
	if (ss_userDisplayStyle == "accessible") {
		//In accessible mode, redraw the whole page
		var url = "";
		eval("url = ss_relevanceAjaxUrl"+namespace);
		url = ss_replaceSubStr(url, "ss_typePlaceHolder", type);
		url = ss_replaceSubStr(url, "ss_type2PlaceHolder", op);
		url = ss_replaceSubStr(url, "ss_binderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ss_pagePlaceHolder", page);
		url = ss_replaceSubStr(url, "ss_rnPlaceHolder", ss_random++);
		self.location.href = url;
	} else {
		ss_setupStatusMessageDiv();
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, operation:"get_dashboard_page", operation2:op, pageNumber:page, direction:direction}, "__ajax_relevance");
		ss_fetch_url(url, ss_showDashboardPageDiv, divId+namespace)
	}
}
function ss_showDashboardPageDiv(s, divId) {
	var divObj = self.document.getElementById(divId);
	divObj.innerHTML = s;
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

function ss_showWhatsNewPage(obj, binderId, type, currentPage, direction, divId, namespace) {
	if (currentPage == "") currentPage = "0";
	var page = parseInt(currentPage);
	if (direction == 'next') page = page + 1;
	if (direction == 'previous') page = page - 1;
	if (ss_userDisplayStyle == "accessible") {
		//In accessible mode, redraw the whole page
		var url = obj.href;
		self.location.href = url;
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
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Function to create a named div in the body
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

//Routines to show or hide a pop-up hover over div
function ss_showHoverOver(parentObj, divName) {
	ss_moveDivToBody(divName);
	ss_showHideObj(divName, 'visible', 'block');
	divObj = document.getElementById(divName)
	divObj.style.zIndex = '500';
	var x = dojo.html.getAbsolutePosition(parentObj, true).x
	var y = dojo.html.getAbsolutePosition(parentObj, true).y
	ss_setObjectTop(divObj, parseInt(parseInt(y) + dojo.html.getContentBoxHeight(parentObj)) + "px")
	ss_setObjectLeft(divObj, x + "px")
}
function ss_hideHoverOver(parentObj, divName) {
	ss_showHideObj(divName, 'hidden', 'none');
}

//Routines to show or hide an object
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
		}
		//Signal that the layout changed
		if (!obj.style.position || obj.style.position != "absolute") {
			ssf_onLayoutChange();
			//ss_debug("ss_showHideObj: " + objName + " = " + visibility)
		}
	} else {
		//ss_debug('Div "'+objName+'" does not exist. (ss_showHideObj)')
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
			return true;
		} else {	
			obj.style.visibility="hidden";
			obj.style.display="none";
			return false;
		} 
	}
	return false;
}

function ss_showHideSidebarBox(divId, imgObj, sticky, id) {
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
		dojo.html.hide("ss_smallBusinessCard");
		dojo.html.show("ss_largeBusinessCard");
		urlParams.operation="show_business_card";
	} else {
		dojo.html.hide("ss_largeBusinessCard");
		dojo.html.show("ss_smallBusinessCard");
		urlParams.operation="hide_business_card";
	}
	ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams));
}

//Routine to set the opacity of a div
//  (Note: this may not work if "width" is not explicitly set on the div)
function ss_setOpacity(obj, opacity) {
	dojo.html.setOpacity(obj, opacity);
}

//Routine to fade in a div
function ss_showDivFadeIn(id, ms) {
	if (ss_divFadeInArray[id] == null || ss_divFadeInArray[id] < 0) {
		ss_divFadeInArray[id] = 0;
	}
	ss_divFadeInArray[id]++;
    //Is this already being shown? If yes, return.
	if (ss_divFadeInArray[id] > 1) return;
    if (!ms || ms == undefined) ms = 300;
    if (document.getElementById(id).style.visibility == 'hidden') {
    	ss_setOpacity(document.getElementById(id),0.1);
    	ss_showDiv(id);
    }
    dojo.lfx.html.fadeIn(id, ms).play();
}

//Routine to fade out a div
function ss_hideDivFadeOut(id, ms) {
	if (ss_divFadeInArray[id] == null || ss_divFadeInArray[id] < 1) {
		ss_divFadeInArray[id] = 1;
	}
	ss_divFadeInArray[id]--;
    //Is this still being shown? If yes, return.
	if (ss_divFadeInArray[id] > 1) return;
    if (!ms || ms == undefined) ms = 300;
    dojo.lfx.html.fadeOut(id, ms, function(){
    	ss_hideDiv(id);
    	return true;
    }).play();
}

//Routine to add the innerHMTL of one div to another div
function ss_addToDiv(target, source) {
    var objTarget = self.document.getElementById(target)
    var objSource = self.document.getElementById(source)
    var targetHtml = ss_getDivHtml(target)
    var sourceHtml = ss_getDivHtml(source)
    ss_setDivHtml(target, targetHtml + sourceHtml)

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Routine to add html to a div
function ss_addHtmlToDiv(target, text) {
    var objTarget = self.document.getElementById(target)
    var targetHtml = ss_getDivHtml(target)
    ss_setDivHtml(target, targetHtml + text)

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Routines to get and set the html of an area
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

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Routines for the definition builder
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

//Routine to get the form object of the containing form
function ss_getContainingForm(obj) {
	var formObj = obj;
	while (formObj.nodeName.toLowerCase() != "body") {
		if (formObj.nodeName.toLowerCase() == "form") break;
		formObj = formObj.parentNode;
	}
	return formObj;
}

//Routine to create a new "ss_eventObj" object
//ss_eventObj objects are set up whenever you want to call a routine on an event.
//   event_name is the event name (e.g., "MOUSEDOWN")
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
    
    //Enable the event
    if (ss_isNSN) {
        //eval("self.document.captureEvents(Event."+event_name+")")
    }
    if (ss_eventList[fn].eventName.toLowerCase() == "unload") {
    	//Add the unload event to the body object
    	eval("self.document.body.on"+ss_eventList[fn].eventName.toLowerCase()+" = ssf_event_handler;")
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

//Common event handler
//  This function will call the desired routines on an event
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


//Routine to create a new "onLoadObj" object
//onLoadObj objects are set up whenever you want to call something at onLoad time.
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

//Routine to create a new "onSubmitObj" object
//onSubmitObj objects are set up whenever you want to call something at form submit time.
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

//Common onSubmit handler
//  This function will call the desired routines at form submit time
//  unless the onClick for the submit button called ss_selectButton('cancelBtn').
//  If any routine returns "false", then this routine returns false.
function ss_onSubmit(obj) {
    var result = true;
    if(ss_buttonSelected == "cancelBtn") {
    	return true;
    }
    for (var i = 0; i < ss_onSubmitList.length; i++) {
        if (ss_onSubmitList[i].formName == obj.name) {
            if (!ss_onSubmitList[i].submitRoutine()) {result = false;}
        }
    }
    return result && ss_validate(obj);
}

var ss_buttonSelected = "";
	
function ss_buttonSelect(btn) {
	ss_buttonSelected = btn
}
	

//Routine to create a new "onResizeObj" object
//onResizeObj objects are set up whenever you want to call something at onResize time.
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
    //Call any routines that want to be called at resize time
    for (var i = 0; i < ss_onResizeList.length; i++) {
        if (ss_onResizeList[i].resizeRoutine) {
        	ss_onResizeList[i].resizeRoutine();
        	//ss_debug("Resize event: " + ss_onResizeList[i].name + "\n");
        }
    }
    if (ss_savedOnResizeRoutine != null) {
    	window.onresize = ss_savedOnResizeRoutine;
    	if (window.onresize != null) window.onresize();
		window.onresize = ssf_onresize_event_handler;
    }
}

//Routine to create a new "onLayoutChangeObj" object
//onLayoutChangeObj objects are set up whenever you want to be called if the layout changes dynamically.
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

//Common onLayoutChange handler
//  This function will call the layout routines if the layout changes
function ssf_onLayoutChange(obj) {
    for (var i = 0; i < ss_onLayoutChangeList.length; i++) {
        if (ss_onLayoutChangeList[i].layoutRoutine) {
        	ss_debug("ssf_onLayoutChange executing routine: " + ss_onLayoutChangeList[i].name)
        	ss_onLayoutChangeList[i].layoutRoutine();
        }
    }
    return true;
}

function ss_getObjAbsX(obj) {
    var x = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        x += parentObj.offsetParent.offsetLeft
        parentObj = parentObj.offsetParent
    }
    return x
}

function ss_getObjAbsY(obj) {
    var y = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        y += parentObj.offsetParent.offsetTop
        parentObj = parentObj.offsetParent
    }
    return y
}

function ss_getDivTop(divName) {
    var top = 0;
    var obj = self.document.getElementById(divName)
    while (1) {
        if (!obj) {break}
        top += parseInt(obj.offsetTop)
        if (obj == obj.offsetParent) {break}
        obj = obj.offsetParent
    }
    return parseInt(top);
}

function ss_getDivLeft(divName) {
    var left = 0;
    if (ss_isNSN || ss_isNSN6 || ss_isMoz5) {
        var obj = self.document.getElementById(divName)
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else {
        var obj = self.document.all[divName]
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
    return parseInt(obj.scrollTop);
}

function ss_getDivScrollLeft(divName) {
    var obj = self.document.getElementById(divName)
    if (!obj) return 0;
    return parseInt(obj.scrollLeft);
}

function ss_getDivHeight(divName) {
    var obj = self.document.getElementById(divName)
    if (!obj) return 0;
    return parseInt(obj.offsetHeight);
}

function ss_getDivWidth(divName) {
    var obj = self.document.getElementById(divName)
    if (!obj) return 0;
    return parseInt(obj.offsetWidth);
}

function ss_getAnchorTop(anchorName) {
    var top = 0;
    if (ss_isNSN6 || ss_isMoz5) {
        var obj = document.anchors[anchorName]
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (ss_isNSN) {
        top = document.anchors[anchorName].y
    } else {
        var obj = document.all[anchorName]
        while (1) {
            if (!obj) {break}
            top += obj.offsetTop
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(top);
}

function ss_getAnchorLeft(anchorName) {
    var left = 0;
    if (ss_isNSN6 || ss_isMoz5) {
        var obj = document.anchors[anchorName]
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (ss_isNSN) {
        left = document.anchors[anchorName].x
    } else {
        var obj = document.all[anchorName]
        while (1) {
            if (!obj) {break}
            left += obj.offsetLeft
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    }
    return parseInt(left);
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
    return dojo.html.getContentBoxWidth(obj)
}

function ss_getObjectHeight(obj) {
	return dojo.html.getContentBoxHeight(obj)
}

function ss_getObjectLeft(obj) {
    if (ss_isNSN6 || ss_isMoz5) {
        return parseInt(obj.style.left)
    } else if (ss_isNSN) {
        return parseInt(obj.style.left)
    } else {
        return parseInt(obj.style.pixelLeft)
    }
}

function ss_getObjectTop(obj) {
    if (ss_isNSN6 || ss_isMoz5) {
        return parseInt(obj.style.top)
    } else if (ss_isNSN) {
        return parseInt(obj.style.top)
    } else {
        return parseInt(obj.style.pixelTop)
    }
}

function ss_getObjectLeftAbs(obj) {
	return dojo.html.getAbsolutePosition(obj, true).x
}

function ss_getObjectTopAbs(obj) {
    return dojo.html.getAbsolutePosition(obj, true).y
}

function ss_setObjectWidth(obj, width) {
	if (obj && parseInt(width) > 0) obj.style.width = parseInt(width) + 'px';

    //Call the routines that want to be called on layout changes
    if (obj && obj.style && !obj.style.position || obj.style.position != "absolute") ssf_onLayoutChange();
}

function ss_setObjectHeight(obj, height) {
	if (obj == null) return;

    if (obj && parseInt(height) > 0) obj.style.height = parseInt(height) + 'px';
    
    //Call the routines that want to be called on layout changes
    if (obj && obj.style && !obj.style.position || obj.style.position != "absolute") ssf_onLayoutChange();
}

function ss_setObjectLeft(obj, value) {
    obj.style.left = parseInt(value) + "px";
    //Call the routines that want to be called on layout changes
    if (!obj.style.position || obj.style.position != "absolute") ssf_onLayoutChange();
}

function ss_setObjectTop(obj, value) {
    obj.style.top = parseInt(value) + "px";
    //Call the routines that want to be called on layout changes
    if (!obj.style.position || obj.style.position != "absolute") ssf_onLayoutChange();
}

function ss_getWindowWidth() {
	if( typeof( window.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if( document.documentElement &&
		( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		//IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	return myWidth;
}

function ss_getWindowHeight() {
	if( typeof( window.innerWidth ) == 'number' ) {
		//Non-IE
		myWidth = window.innerWidth;
		myHeight = window.innerHeight;
	} else if( document.documentElement &&
		( document.documentElement.clientWidth || document.documentElement.clientHeight ) ) {
		//IE 6+ in 'standards compliant mode'
		myWidth = document.documentElement.clientWidth;
		myHeight = document.documentElement.clientHeight;
	} else if( document.body && ( document.body.clientWidth || document.body.clientHeight ) ) {
		//IE 4 compatible
		myWidth = document.body.clientWidth;
		myHeight = document.body.clientHeight;
	}
	return myHeight;
}


function ss_getBodyHeight() {
    var h;
    if (window.innerHeight && window.scrollMaxY) {	
		h = window.innerHeight + window.scrollMaxY;
	} else {
		h = self.document.body.scrollHeight;
	}
    if (ss_getWindowHeight() > h) {
        h = ss_getWindowHeight();
    }
    return h
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
		//Netscape compliant
		scrOfY = window.pageYOffset;
		scrOfX = window.pageXOffset;
	} else if( document.body && ( document.body.scrollLeft || document.body.scrollTop ) ) {
		//DOM compliant
		scrOfY = document.body.scrollTop;
		scrOfX = document.body.scrollLeft;
	} else if( document.documentElement && ( document.documentElement.scrollLeft || document.documentElement.scrollTop ) ) {
		//IE6 standards compliant mode
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

function ss_hideAccessibleMenu(divId) {
	var divObj = document.getElementById(divId);
	ss_hideDivNone(divId);
}

// Pop-up menu support
// clicking anywhere will hide the div

//Create a clone of the menu before showing it; attach it to the "body" outside of any div
//  This makes sure that the z-index will be on top of everything else (IE fix)
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
    var menuObj = self.document.getElementById(divId);
    if (menuObj == null) {return}

	var x = 0;
	var y = 0;
    if (parentDivId != "") {
    	var pObj = document.getElementById(parentDivId);
    	x = dojo.html.getAbsolutePosition(pObj, true).x
    	y = dojo.html.getAbsolutePosition(pObj, true).y
	    //Add the offset to the x and y positions so the div isn't occluding too much
	    x = parseInt(parseInt(x) + parseInt(offsetLeft))
	    y = parseInt(parseInt(y) + ss_getDivHeight(parentDivId) + parseInt(offsetTop))
    } else {
	    x = ss_getClickPositionX();
	    y = ss_getClickPositionY();
	    //Add a little to the x and y positions so the div isn't occluding too much
	    x = parseInt(parseInt(x) + parseInt(offsetLeft));
	    y = parseInt(parseInt(y) + parseInt(offsetTop));
	}

    var maxWidth = 0;
    var divWidth = 0;

	document.getElementById(divId).style.display = "block";

    if (ss_isNSN6 || ss_isMoz5) {
        // need to bump layer an extra bit to the right to avoid horiz scrollbar
        divWidth = parseInt(document.getElementById(divId).offsetWidth) + 20;
        maxWidth = parseInt(window.innerWidth);
    } else {
        divWidth = parseInt(document.all[divId].clientWidth) + 20;
        maxWidth = parseInt(document.body.scrollWidth);
    }

	//console.log(divId, " dw ", divWidth, " mw ", maxWidth);
	
    if (x + divWidth > maxWidth) {
        x = maxWidth - divWidth;
    } 
  
    //alert('divId: ' + divId + ', x: ' + x + ', y: ' + y)
    //alert(document.getElementById(divId).innerHTML)
    if (!menuObj.style || !menuObj.style.zIndex || menuObj.style.zIndex == 0) {
    	menuObj.style.zIndex = ssMenuZ;
    }
    
    ss_ShowHideDivXY(divId, x, y);
    if (openStyle != "popup") ss_HideDivOnSecondClick(divId);
    ssf_onLayoutChange();
}

// activate_menulayer tests this flag to make sure the page is
// loaded before the pulldown menus are clicked.
function ss_setLayerFlag() {
    ss_layerFlag = 1;
}

ss_createOnLoadObj('ss_layerFlag', ss_setLayerFlag);



//Support for positioning divs at x,y 
//Enable the event handler
ss_createEventObj('captureXY', 'MOUSEUP')

//General routine to show a div given its name and coordinates
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
    }
}

/* IE6 workaround - divs under selectboxes */
var ss_showBackgroundIframeDivId = null;
function ss_showBackgroundIFrame(divId, frmId) {
	if (!ss_isIE6) {
		return;
	}
	if (ss_showBackgroundIframeDivId != null && ss_showBackgroundIframeDivId != divId) {
		//Delete the previous iframe if any
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
			frm.src = teaming_url;
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
	var top = dojo.html.getAbsolutePosition(div, true).y;
	var left = dojo.html.getAbsolutePosition(div, true).x;
    ss_setObjectTop(frm, top);
    ss_setObjectLeft(frm, left);

	frm.style.height = ss_getObjectHeight(div) + "px";
	frm.style.width = ss_getObjectWidth(div) + "px";
	
	frm.style.position = "absolute";
	frm.style.display = "block";
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

function ss_showDivActivate(divName) {
    if (ss_divBeingShown != null) {
        ss_hideDiv(ss_divBeingShown);
    }
    ss_divBeingShown = divName;
    ss_lastDivBeingShown = divName;
    ss_showDiv(divName);
	ss_HideDivOnSecondClick(divName);
}

//General routine to show a div given its name
function ss_HideDivIfActivated(divName) {
    if (ss_divBeingShown == divName) {
        ss_hideDiv(ss_divBeingShown);
        ss_divBeingShown = null;
        ss_lastDivBeingShown = null;
    }
}

//Routine to make div's be hidden on next click
function ss_HideDivOnSecondClick(divName) {
    ss_divToBeHidden[ss_divToBeHidden.length] = divName;
    ss_debug('ss_divToBeHidden length = '+ss_divToBeHidden.length)
}

//Routine to make div's be hidden on next click
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
	ss_showDiv(divName)
}
function ss_showDiv(divName, backgroundIframe) {
	if (document.getElementById(divName) == null) return;
    document.getElementById(divName).style.visibility = "visible";
    if (!document.getElementById(divName).style.display || document.getElementById(divName).style.display != 'inline') {
    	document.getElementById(divName).style.display = "block";
    }
	if (typeof backgroundIframe == 'undefined' || backgroundIframe != 'no') ss_showBackgroundIFrame(divName, "ss_background_iframe");
	//Signal that the layout changed
	if (!document.getElementById(divName) || 
	    	document.getElementById(divName).style.position != "absolute") {
		ssf_onLayoutChange();
		//ss_debug("ss_showDiv: " + divName)
	}
}

function ss_hideDiv(divName) {
	if (document.getElementById(divName))
			document.getElementById(divName).style.visibility = "hidden";
    ss_divToBeDelayHidden[divName] = null
    ss_divBeingShown = null;
    ss_hideBackgroundIFrame("ss_background_iframe");
	//Signal that the layout changed
	if (!document.getElementById(divName) || 
	    	document.getElementById(divName).style.position != "absolute") {
		ssf_onLayoutChange();
		//ss_debug("ss_hideDiv: " + divName)
	}
}

function ss_hideDivNone(divName) {
	if (document.getElementById(divName)) {
		document.getElementById(divName).style.display = "none";
		document.getElementById(divName).style.visibility = "hidden";
	}
    ss_divToBeDelayHidden[divName] = null
    ss_divBeingShown = null;
    
	//Signal that the layout changed
	if (!document.getElementById(divName) || 
	    	document.getElementById(divName).style.position != "absolute") {
		ssf_onLayoutChange();
		//ss_debug("ss_hideDiv: " + divName)
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

    //Is this click a "right click"? If yes, ignore it
    if (e && e.which && (e.which == 3 || e.which == 2)) {
        return false;
    } else if (e && e.button && (e.button == 2 || e.button == 3)) {
        return false;
    }

    //See if there is a div to be hidden
    ss_lastDivBeingShown = ss_divBeingShown;
    if (ss_divToBeHidden.length > 0) {
        for (var i = 0; i < ss_divToBeHidden.length; i++) {
	        if (ss_divToBeHidden[i] != '') {
	            if (ss_divToBeDelayHidden[ss_divToBeHidden[i]]) {
	                ss_divToBeDelayHidden[ss_divToBeHidden[i]] = null
	            } else {
	                ss_hideDiv(ss_divToBeHidden[i])
	    			if (ss_divToBeHidden[i] == ss_divBeingShown) ss_divBeingShown = null;
	                ss_divToBeHidden[i] = '';
	            }
	        }
	    }
	    ss_divToBeHidden = new Array();
    }
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
        //ss_mousePosX = event.x + ss_getScrollXY()[0]
        //ss_mousePosY = event.y + ss_getScrollXY()[1]
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

//Routines to get an object handle given the x,y coordinates of the image
function getNN4ImgObject(imgX, imgY) {
    var imgObj = getNN4ImgObjectObj(self, imgX, imgY)
    return imgObj
}

function getNN4ImgObjectObj(divObj, imgX, imgY) {
    //Look in this div for the image
    for (var i = 0; i < divObj.document.images.length; i++) {
        var testImgObj = divObj.document.images[i]
        if ( testImgObj && testImgObj.x &&   (imgX >= testImgObj.x) && 
                (imgX <= testImgObj.x + testImgObj.width) && 
                (imgY >= testImgObj.y) && 
                (imgY <= testImgObj.y + testImgObj.height)    ) {
                return(testImgObj)
        }
    }
    //The image isn't in this div, look in the children divs
    for (var n = 0; n < divObj.document.layers.length; n++) {
        var testObj = divObj.document.layers[n]
        var imgObj = getNN4ImgObjectObj(testObj, imgX, imgY)
        if (imgObj != null) {return imgObj}
    }
    return null
}

//Routines to get a div handle of the owner of an image
function getNN4ImgDivObject(imgObj) {
    var divObj = getNN4ImgDivObjectObj(self, imgObj)
    return divObj
}

function getNN4ImgDivObjectObj(divObj, imgObj) {
    //Look in this div for the image
    for (var i = 0; i < divObj.document.images.length; i++) {
        var testImgObj = divObj.document.images[i]
        if (testImgObj == imgObj) {
            return(divObj)
        }
    }
    //The image isn't in this div, look in the children divs
    for (var n = 0; n < divObj.document.layers.length; n++) {
        var testDivObj = divObj.document.layers[n]
        var testImgObj = getNN4ImgDivObjectObj(testDivObj, imgObj)
        if (testImgObj != null) {return testDivObj}
    }
    return null
}

//Routine to create a new "onErrorObj" object
//onErrorObj objects are set up whenever you want to call something at onError time.
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

//Routine to create a new "spannedArea" object
//spannedAreaObj objects are set up whenever you need some form elements to be 
//   blanked when showing the menus
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
    this.showRoutine = null;
    this.show = m_showSpannedArea;
    this.setShowRoutine = m_setShowRoutine;
    this.hideRoutine = null;
    this.hide = m_hideSpannedArea;
    this.setHideRoutine = m_setHideRoutine;
}
function m_setShowRoutine(showRoutine) {
    this.showRoutine = showRoutine;
    //See if there are any other arguments passed in
    //  These will get passed on to the show routine
    for (var i = 1; i < m_setShowRoutine.arguments.length; i++) {
        if (this.showArgumentString != '') {this.showArgumentString += ',';}
        this.showArgumentString += '"'+m_setShowRoutine.arguments[i]+'"';
    }
}
function m_setHideRoutine(hideRoutine) {
    this.hideRoutine = hideRoutine;
    //See if there are any other arguments passed in
    //  These will get passed on to the show routine
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
    //Hide any form elements that may be visible
    for (var i = 0; i < ss_spannedAreasList.length; i++) {
        ss_spannedAreasList[i].hide()
    }
}

function ss_showSpannedAreas() {
    //Show any form elements that should be returned to the visible state
    for (var i = 0; i < ss_spannedAreasList.length; i++) {
        ss_spannedAreasList[i].show()
    }
}

//Routine to pop up a window with a url (used in common toolbar code)
function ss_toolbarPopupUrl(url, windowName) {
	var width = ss_getWindowWidth();
	if (width < 600) width=600;
	var height = ss_getWindowHeight();
	if (height < 600) height=600;
	self.window.open(url?url:"", windowName?windowName:"_blank", "resizable=yes,scrollbars=yes,width="+width+",height="+height);
}

//Routine to show a div at the bottom of the highest size attained by the window
function setWindowHighWaterMark(divName) {
	var currentPageHeight = ss_getBodyHeight()
	if (parseInt(ss_forum_maxBodyWindowHeight) < parseInt(currentPageHeight)) {
		//Time to set a new high water mark
		ss_forum_maxBodyWindowHeight = currentPageHeight;
	}
	var dh = ss_getDivHeight(divName);
	var x = 0
	var y = parseInt(ss_forum_maxBodyWindowHeight - dh)
	ss_positionDiv(divName, x, y);
	ss_showDiv(divName)
}


//Routines to replace substrings in a string
function ss_replaceSubStr(str, subStr, newSubStrVal) {
    //ss_debug("ss_replaceSubStr: " + str + ", " + subStr + " ==> " + newSubStrVal)
    var newStr = str;
	var i = str.indexOf(subStr);
    var lenS = str.length;
    var lenSS = subStr.length;
    if (i >= 0) {
        newStr = str.substring(0, i) + newSubStrVal + str.substring(i+lenSS, lenS);
    }
    //ss_debug("   new str = " + newStr)
	return newStr;
}
function ss_replaceSubStrAll(str, subStr, newSubStrVal) {
    var newStr = str;
    var i = -1
    //Prevent a possible loop by only doing 1000 passes through this loop
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

//Routine to build a status_message div if one doesn't exist yet
function ss_setupStatusMessageDiv() {
	var smId = document.getElementById('ss_status_message');
	if (!smId) {
		//There isn't a status message div, so go build it
		var smDiv = document.createElement("div");
        smDiv.setAttribute("id", "ss_status_message");
        smDiv.style.visibility = "hidden";
        smDiv.style.display = "none";
    	document.getElementsByTagName("body").item(0).appendChild(smDiv);
	}
}
//common processing for callback after ajax call
function ss_postRequestAlertError(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
}
//Routine to write text to the debug window
function ss_debug(text) {
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

function ss_showNotLoggedInMsg() {
	alert(ss_not_logged_in);
}

function ss_showLightbox(id, zIndex, opacity, className) {
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
    lightBox.style.top = "0px";
    lightBox.style.left = "0px";
    dojo.html.setOpacity(lightBox, 0);
    lightBox.style.width = ss_getBodyWidth() + "px";
    lightBox.style.height = ss_getBodyHeight() + "px";
    lightBox.style.zIndex = zIndex;
    lightBox.style.visibility = "visible";
    dojo.lfx.html.fade(lightBox, {end:opacity}, 150).play();
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
}

//Support routines for the help system
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
		//ss_debug('ss_helpSystem');
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
	        //welcomeDiv.style.top = this.getPositionTop(welcomeDiv);
	        //welcomeDiv.style.left = this.getPositionLeft(welcomeDiv);
	        dojo.html.placeOnScreen(welcomeDiv,0,0,[5,5],false, ['TL'], false);
	        if (helpMenuAnchorDiv != null) {
	        	helpMenuAnchorDiv.style.visibility = "visible";
	        	helpMenuAnchorDiv.style.display = "block";
	        	//ss_setObjectHeight(helpMenuAnchorDiv, ss_getObjectHeight(welcomeDiv));
	        	var offsetT = -2;
	        	if (ss_isIE) offsetT = 6;
	        	//welcomeDiv.style.top = parseInt(ss_getObjectTopAbs(helpMenuAnchorDiv) - offsetT) + "px";
	        	var offsetL = parseInt((ss_getObjectWidth(helpMenuAnchorDiv) - ss_getObjectWidth(welcomeDiv)) / 2);
	        	//welcomeDiv.style.left = parseInt(ss_getObjectLeftAbs(helpMenuAnchorDiv) + offsetL) + "px";
	        }
	    	dojo.html.setOpacity(welcomeDiv, 0);
	    	dojo.lfx.html.fade(welcomeDiv, {start:0, end:1.0}, 150).play();
	    	dojo.event.connect(window, "onscroll", this, "moveWelcomeIntoView");
		}
	},

	moveWelcomeIntoView : function (e) {
		dojo.html.placeOnScreen(dojo.byId("ss_help_welcome"),0,0,[5,5],false, ['TL'], false);
	},
	
	hide : function() {
		var bodyObj = document.getElementsByTagName("body").item(0)
		var lightBox = document.getElementById('ss_help_light_box')
		if (!lightBox) return;
		var welcomeDiv = document.getElementById('ss_help_welcome');
		if (welcomeDiv) {
	    	welcomeDiv.style.visibility = "hidden";
	    	welcomeDiv.style.display = "none";
	    	dojo.event.disconnect(window, "onscroll", this, "moveWelcomeIntoView");
		    //Call the routines that want to be called on layout changes
		    ssf_onLayoutChange();
		}
		var helpMenuAnchorDiv = document.getElementById('ss_helpMenuAnchor');
        if (helpMenuAnchorDiv != null) {
        	helpMenuAnchorDiv.style.visibility = "hidden";
        	helpMenuAnchorDiv.style.display = "none";
        }
		if (lightBox.style.visibility && lightBox.style.visibility == 'visible') {
    		for (var i = 1; i < ss_helpSystemNextNodeId; i++) {
    			//Delete all of the help spots that were added during the help session
    			if (ss_helpSystemNodes[i] != null) {
    				bodyObj.removeChild(ss_helpSystemNodes[i]);
    				ss_helpSystemNodes[i] = null;
    			}
    		}
    		ss_helpSystemNextNodeId = 1;

    		//Delete all of the help panels that were added during the help session
			for (var i = ss_helpSystemPanels.length; --i >= 0;) {
				//ss_debug("panelObj = " + ss_helpSystemPanels[i])
				var pObj = document.getElementById(ss_helpSystemPanels[i]);
				if (pObj != null && pObj.parentNode != null) pObj.parentNode.removeChild(pObj);
			}
    		//Delete all of the highlighted nodes
    		this.clearHighlights();

    		dojo.lfx.html.fade(lightBox, {end: 0}, 150, '', function() {
    			var lightBox2 = document.getElementById('ss_help_light_box');
		    	lightBox.style.visibility = "hidden";
		    	lightBox.style.display = "none";
   		}).play();
    		return
		}
	},
	
	showPanel : function(id, location) {
	},
	
	showHelpSpots : function() {
		this.clearTOC()
		var bodyObj = document.getElementsByTagName("body").item(0)
		var nodes = new Array();
		//var time = new Date().getTime();
		nodes = document.getElementsByTagName("ssHelpSpot");
		//ss_debug('Time: '+ parseInt(new Date().getTime() - time))
		for (var i = 0; i < nodes.length; i++) {
			//ss_debug(nodes[i].getAttribute("helpId") + " = " + ss_helpSystemNextNodeId)
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
			
			//Build a table containing the image and the title
			helpSpotNode.appendChild(helpSpotA);
			helpSpotA.appendChild(helpSpotTable);
			helpSpotTable.appendChild(helpSpotTbody);
			helpSpotTbody.appendChild(helpSpotTr);
			helpSpotTr.appendChild(helpSpotTd1);
			helpSpotTr.appendChild(helpSpotTd2);
			var aObj = document.createElement("a");
			aObj.setAttribute("href", "javascript: ss_helpSystem.showHelpSpotInfo('" + helpSpotNodeId + "', '" + xAlignment + "', '" + yAlignment + "');");
			// Associate link either to the text ('show' text) or the icon ('hide' text)
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
	        var top = parseInt(dojo.html.getAbsolutePosition(nodes[i], true).y + offsetY);
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
	        var left = parseInt(dojo.html.getAbsolutePosition(nodes[i], true).x + offsetX);
	        if (nodes[i].getAttribute("align")) {
	        	if (nodes[i].getAttribute("align") == "center") {
	        		left += parseInt(dojo.html.getMarginBox(nodes[i]).width / 2);
	        	} else if (nodes[i].getAttribute("align") == "right") {
	        		left += dojo.html.getMarginBox(nodes[i]).width;
	        	}
	        }
	        helpSpotNode.style.top = top + "px";
	        helpSpotNode.style.left = left + "px";
	        bodyObj.appendChild(helpSpotNode);
			ss_helpSystemNextNodeId++;
	        var owningDiv = nodes[i].parentNode
	        var okToShow = 1
	        while (owningDiv != null && owningDiv.tagName != null && owningDiv.tagName.toLowerCase() != 'body') {
	        	if (owningDiv.tagName.toLowerCase() == 'div') {
	        		var displayStyle = dojo.html.getComputedStyle(owningDiv, 'display')
	        		var positionStyle = dojo.html.getComputedStyle(owningDiv, 'position')
	        		if (displayStyle.toLowerCase() == 'none' || positionStyle.toLowerCase() == 'absolute') {
	        			okToShow = 0
	        			break
	        		}
	        	}
	        	owningDiv = owningDiv.parentNode
	        }
	        if (okToShow == 1) helpSpotNode.style.visibility = "visible";
			//ss_debug("nodes[i] width = "+dojo.html.getMarginBox(nodes[i]).width)
		}
	},
	
	getPositionLeft : function(obj) {
		var x = 0;
		switch(obj.getAttribute("positionX")) {
			case "left" : 
				x = ss_help_position_leftOffset
				break
			case "center" :
				x = parseInt((ss_getWindowWidth() - dojo.html.getMarginBox(obj).width) / 2)
				if (x < 0) x = 0;
				break
			case "right" :
				x = parseInt(ss_getWindowWidth() - dojo.html.getMarginBox(obj).width - ss_help_position_rightOffset)
				if (x < 0) x = 0;
			 	break
			default :
				x = parseInt((ss_getWindowWidth() - dojo.html.getMarginBox(obj).width) / 2)
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
		//ss_debug("addToc " + id + ", " + title)
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
        //Hide moreinfo panel, if exists
        ss_hideDiv('ss_moreinfo_panel')
		if (xAlignment == null) xAlignment = "";
		if (yAlignment == null) yAlignment = "";
		//ss_debug('showHelpSpotInfo id = '+id)
		this.hideTOC();
		for (var i = 0; i < ss_helpSystemTOC.length; i++) {
			if (id == ss_helpSystemTOC[i].id) {
				ss_helpSystemTOCindex = i;
				break;
			}
		}
		//Find the help spot node
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
		//ss_debug("showHelpSpotInfo helpSpot: " + helpSpot)
		if (helpSpot != null) {
		    var top = parseInt(dojo.html.getAbsolutePosition(helpSpot, true).y);
		    var left = parseInt(dojo.html.getAbsolutePosition(helpSpot, true).x);
		    var width = parseInt(dojo.html.getContentBox(helpSpot).width);
		    var height = parseInt(dojo.html.getContentBox(helpSpot).height);
		    var x = parseInt(left + 3);
		    var y = parseInt(top + height - 8);
			this.showHelpPanel(id, "ss_help_panel", x, y, xAlignment, yAlignment)
			dojo.html.scrollIntoView(helpSpot);
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
		//ss_debug('showInlineHelpSpotInfo jspId = '+jspId)
		if (helpSpot != null) {
		    var top = parseInt(dojo.html.getAbsolutePosition(helpSpot, true).y);
		    var left = parseInt(dojo.html.getAbsolutePosition(helpSpot, true).x);
		    var x = parseInt(left + 3 + parseInt(dx));
		    var y = parseInt(top + 3 + parseInt(dy));
			this.showHelpPanel(jspId, "ss_help_panel", x, y, xAlignment, yAlignment, tagId)
		}
	},

	toggleShowHelpCPanel: function () {
		if (dojo.html.isDisplayed("ss_help_welcome_panel_body")) {
			this.recordShowHelpCPanel("hidden");
			dojo.html.setClass("ss_help_cpanel_show_control", "ss_help_cpanel_hide");
		} else {
			this.recordShowHelpCPanel("visible");
			dojo.html.setClass("ss_help_cpanel_show_control", "ss_help_cpanel_show");
		}		
		dojo.html.toggleDisplay("ss_help_welcome_panel_body");
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
		ss_debug('id='+id+', panelId='+panelId+', x = '+x+', y = '+y+', xAlign = '+xAlignment)
		if (ss_helpSystemRequestInProgress == 1) {
			ss_helpSystemQueuedId = id;
			ss_helpSystemQueuedPanelId = panelId;
			ss_helpSystemQueuedX = x;
			ss_helpSystemQueuedY = y;
			return;
		}
		ss_setupStatusMessageDiv()
		this.clearHighlights();
		//ss_debug("showHelpPanel " + id)
		var pObj = self.document.getElementById(panelId);
		var startTop = -1;
		var startLeft = -1;
		var startVisibility = "";
		if (!pObj) {
			//There is no help panel, so create it on-the-fly
			var bodyObj = document.getElementsByTagName("body").item(0)
			pObj = document.createElement("div");
	        pObj.setAttribute("id", panelId);
	        pObj.setAttribute("helpId", id);
	        pObj.className = "ss_helpPanel ss_popup_panel_outer";
	        pObj.style.zIndex = ssHelpPanelZ;
	        bodyObj.appendChild(pObj);
	        ss_helpSystemPanels[ss_helpSystemPanels.length] = panelId;
		} else {
			//See if this is a request for the same panel. If so, toggle it off.
			//ss_debug("id = " + pObj.getAttribute("id") + ", helpId = " + pObj.getAttribute("helpId"))
			if (pObj.getAttribute("helpId") == id && pObj.style.visibility == "visible") {
				//On the second click to the same help spot, turn the panel off
				pObj.style.visibility = "hidden"
				pObj.style.display = "none"
				return
			}
			startTop = parseInt(dojo.html.getAbsolutePosition(pObj, true).y);
			startLeft = parseInt(dojo.html.getAbsolutePosition(pObj, true).x);
			if (pObj.style && pObj.style.visibility) 
					startVisibility = pObj.style.visibility;
		}
		
		var orgHelpId = id;
		//See if this is the actual name of the help panel
		var i1 = id.indexOf("___");
		if (i1 >= 0) orgHelpId = id.substr(id.indexOf("___") + 3);
		var urlParams = {operation:"show_help_panel", operation2:orgHelpId, tagId:tagId}; 
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
		var callbackParams = {id: id, panelId:panelId, x:x, y:y, xAlignment:xAlignment,
								yAlignment:yAlignment, startTop:startTop, startLeft:startLeft,
								startVisibility: startVisibility};

		var bindArgs = {
	    	url: url,
			error: function(type, data, evt) {
				alert(ss_not_logged_in);
			},
			load: function(type, data, evt) {
	  		  try {
	  		  	dojo.byId(panelId).innerHTML = data;
	  		    ss_helpSystem.postShowPanel(callbackParams);
		      } catch (e) {alert(e);}
			},
			preventCache: true,				
			mimetype: "text/plain",
			method: "get"
		};   
		dojo.io.bind(bindArgs);

	},
	
	postShowPanel : function(data) {
		panelId = data.panelId;
		var pObj = self.document.getElementById(panelId);
		pObj.setAttribute("helpId", data.id);
		pObj.style.display = "block"
		var width = parseInt(dojo.html.getMarginBox(pObj).width);
		var height = parseInt(dojo.html.getMarginBox(pObj).height);
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
			dojo.lfx.html.slideTo(panelId, {top: top, left: left}, 300).play();
		} else {
			pObj.style.top = top + "px";
			pObj.style.left = left + "px";
		}
	    pObj.style.zIndex = ssHelpPanelZ;
		pObj.style.visibility = "visible";
		
		ss_helpSystemRequestInProgress = 0;
		
		//Is there another request queued?
		if (ss_helpSystemQueuedId != "") {
			//ss_debug("\nLaunching queued request to show " + ss_helpSystemQueuedId)
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
	    while (dojo.dom.hasParent(obj)) {
	        var n = obj.parentNode;
	    	if (dojo.html.hasClass(n, "ss_popup_panel_outer")) {
	    		if (n.id) {
					ss_hideDiv(n.id);
					break;
				}
			}
			obj = n;
		}
	},
	
	highlight : function(id) {
		//ss_debug("Highlight " + id)
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
			//ss_debug("highlightObj = " + ss_helpSystemHighlights[i])
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
		//ss_debug("blinkHighlight " + id)
		if (ss_helpSystemHighlightsBorderTimer[id] != null) {
			//ss_debug("clearTimeout " + id)
			clearTimeout(ss_helpSystemHighlightsBorderTimer[id])
			ss_helpSystemHighlightsBorderTimer[id] = null;
		} else {
			//There is no timer value. The user must have moved on. Don't blink any more.
			//ss_debug("Stopped blinking!")
			return;
		}
		var obj = document.getElementById(id);
		//ss_debug("  border color: " + obj.style.borderTopColor)
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

//Dashboard routines

function ss_addDashboardComponents(divId) {
	var panel = document.getElementById(divId);
	//undefined for some shared access
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
	}
	ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_dashboard"));
	//Signal that the layout changed
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
			if (toolbarOption) toolbarOption.innerHTML = ss_toolbarHideContent;
			//var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
			//lightBox.onclick = function(e) {ss_toggle_dashboard_toolbars(prefix);};
		} else {
			obj.style.visibility = 'hidden';
			obj.style.display = 'none';
			if (toolbarOption) toolbarOption.innerHTML = ss_toolbarAddContent;
			//ss_hideLightbox()
		}
	}
	
	//Signal that the layout changed
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
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

var ss_dashboardCallbacks = new Object();

function ss_addDashboardEvent(componentId, 
						when /* onBeforeShow, onAfterShow == onShow, onBeforeHide, onAfterHide == onHide */,
						routineToCall) {
	if (when == "onShow") {
		when = "onAfterShow";
	} else if (when == "onHide") {
		when = "onAfterHide";
	}
	
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
						when /* onBeforeShow, onAfterShow == onShow, onBeforeHide, onAfterHide == onHide */) {
	if (when == "onShow") {
		when = "onAfterShow";
	} else if (when == "onHide") {
		when = "onAfterHide";
	}	
	
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
	//ss_debug(obj.alt + ",    " + obj.src)
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
			//Signal that the layout changed 
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
			//Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
		ss_callDashboardEvent(componentId, "onBeforeHide");
	}
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "__ajax_dashboard");
	url += "\&" + idStr;
	if (callbackRoutine != "") ss_fetch_url(url, callbackRoutine, {divId:divId, componentId:componentId});
}
function ss_showComponentCallback(s, data) {
	// data = {"divId" : divId, "componentId" : componentId}
	ss_debug(s)
	var targetDiv = document.getElementById(data.divId);
	if (targetDiv) {
		targetDiv.innerHTML = s;
		targetDiv.style.visibility = "visible";
		targetDiv.style.display = "block";
		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
		ss_callDashboardEvent(data.componentId, "onAfterShow");
	}
}
function ss_hideComponentCallback(s, data) {
	// data = {"divId" : divId, "componentId" : componentId}
	ss_callDashboardEvent(data.componentId, "onAfterHide");
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


//Start: Add Attachment Related Functions

//Browse Related Functions


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
	//alert("Inside ss_showAddAttachmentBrowse...");

	ss_hideAddAttachmentDropbox(entryId, namespace);
	ss_hideAddAttachmentMeetingRecords(entryId, namespace);
	
	setURLInIFrame(binderId, entryId, namespace);
	
	var divId = 'ss_div_browse' + entryId + namespace;
	var divObj = document.getElementById(divId);

	var frameId = 'ss_iframe_browse' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	//alert("ss_showAddAttachmentBrowse: frameObj.src: "+frameObj.src);
	
	frameObj.src = ss_rootPath + "js/attachments/entry_attachment_browse.html";
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";
	
	divObj.style.width = "360px";
	divObj.style.height = "120px";
	
	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	if (parent.ss_setWikiIframeSize) parent.ss_setWikiIframeSize(namespace);
}

function ss_hideAddAttachmentBrowse(entryId, namespace) {
	var divId = 'ss_div_browse' + entryId + namespace;
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_hideAddAttachmentBrowseAndAJAXCall(binderId, entryId, namespace, strErrorMessage) {
	if (strErrorMessage != "") {
		alert(strErrorMessage);
	}

	ss_hideAddAttachmentBrowse(entryId, namespace);
	ss_selectEntryAttachmentAjax(binderId, entryId, namespace);
}

function ss_selectEntryAttachmentAjax(binderId, entryId, namespace) {
	ss_setupStatusMessageDiv();
	
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {binderId:binderId, entryId:entryId, operation:"reload_entry_attachments", namespace:namespace});
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  //Send the request
}


//Dropbox Functionality
function ss_hideAddAttachmentDropbox(entryId, namespace) {
	var divId = 'ss_div_dropbox' + entryId + namespace;
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_hideAddAttachmentDropboxAndAJAXCall(binderId, entryId, namespace) {
	ss_hideAddAttachmentDropbox(entryId, namespace);
	ss_selectEntryAttachmentAjax(binderId, entryId, namespace);
}

function ss_showAddAttachmentDropbox(binderId, entryId, namespace) {
	ss_hideAddAttachmentBrowse(entryId, namespace);
	ss_hideAddAttachmentMeetingRecords(entryId, namespace);

	var urlParams = {binderId:binderId, entryId:entryId, operation:"add_attachment_options", namespace:namespace};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
	
	var divId = 'ss_div_dropbox' + entryId + namespace;
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_dropbox' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	if (frameObj.src == "" || frameObj.src.indexOf("null.html") >= 0) {
		frameObj.src = url;
	}
	
	divObj.style.width = "300px";
	divObj.style.height = "100px";

	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	if (parent.ss_setWikiIframeSize) parent.ss_setWikiIframeSize(namespace);
}

function ss_showAttachMeetingRecords(binderId, entryId, namespace, held) {
	ss_setupStatusMessageDiv();
	ss_hideAddAttachmentDropbox(entryId, namespace);
	ss_hideAddAttachmentBrowse(entryId, namespace);
	
	var divId = 'ss_div_attach_meeting_records' + entryId + namespace;
	var divObj = document.getElementById(divId);
	ss_showDiv(divId);
	
	var contentDivId = 'ss_div_attach_meeting_records_content' + entryId + namespace;
	var contentDivObj = document.getElementById(divId);
	ss_toggleAjaxLoadingIndicator(contentDivId);

	// contentDivObj.style.height = "120px";
	var urlParams = {operation:"get_meeting_records", recordsDivId:contentDivId, 
					binderId:binderId, entryId:entryId,
					ssNamespace:namespace, randomNumber:ss_random++};
	if (held) {
		urlParams.ssHeld=held;
	}
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.sendRequest();  //Send the request
	
	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
	if (parent.ss_setWikiIframeSize) parent.ss_setWikiIframeSize(namespace);
}

function ss_hideAddAttachmentMeetingRecords(entryId, namespace) {
	var divId = 'ss_div_attach_meeting_records' + entryId + namespace;
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_attacheMeetingRecords(formId, binderId, entryId, namespace) {
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"attache_meeting_records"});
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(formId);
	ajaxRequest.setUsePOST();
	ajaxRequest.setData("binderId", binderId);
	ajaxRequest.setData("entryId", entryId);
	ajaxRequest.setData("namespace", namespace);
	ajaxRequest.setPostRequest(ss_hideAddAttachmentMeetingRecordsAndAJAXCall);	
	ajaxRequest.sendRequest();
	ss_toggleAjaxLoadingIndicator(formId, true);
}

function ss_hideAddAttachmentMeetingRecordsAndAJAXCall(obj) {
	var binderId = obj.getData("binderId");
	var entryId = obj.getData("entryId");
	var namespace = obj.getData("namespace");
	ss_hideAddAttachmentMeetingRecords(entryId, namespace);
	ss_selectEntryAttachmentAjax(binderId, entryId, namespace);
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

	var divId = "ss_div_fileopen" + entryId + namespace;
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_fileopen' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	editClicked = "true";
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	frameObj.src = url;
	
	divObj.style.width = "1px";
	divObj.style.height = "1px";
}

function ss_openWebDAVFile(binderId, entryId, namespace, OSInfo, fileId) {
	var urlParams ={binderId:binderId, entryId:entryId, operation:"open_webdav_file_by_fileid",
						namespace:namespace, ssOSInfo:OSInfo, fileId:fileId};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
    
	var divId = "ss_div_fileopen" + entryId + namespace;
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_fileopen' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	editClicked = "true";
	
	divObj.style.visibility = "visible";
	frameObj.style.visibility = "visible";

	frameObj.src = url;
	}

function ss_checkEditClicked(entryId, namespace) {
	return editClicked;
}

function ss_resetEditClicked(entryId, namespace) {
	editClicked = "false";
}


//End: Add Attachment Related Functions

//Title link Related Functions
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
	ss_highlightLine(id, namespace);
	if (isFile == "yes") {
		self.window.open(obj.href, '_blank');
		return false;
	}
	
	ss_showForumEntry(obj.href, isDashboard);
	
	return false;
}

//Initialize the ss_folder div
function ss_initShowFolderDiv(namespace) {
	var divObj = self.document.getElementById("ss_showfolder");
	if (divObj != null) return;
	//It hasn't been set up yet, use this first div to be defined
	divObj = self.document.getElementById("ss_showfolder"+namespace);
	if (divObj == null) return;
	divObj.id = "ss_showfolder";
}

//Initialize the floating div for viewing entries
function ss_showEntryDivInitialization(namespace) {
	var divObj = self.document.getElementById("ss_showentrydiv");
	if (divObj != null) return;
	//It hasn't been set up yet, use this prototype div
	divObj = self.document.getElementById("ss_showentrydiv"+namespace);
	var iframeObj = self.document.getElementById("ss_showentryframe"+namespace);
	var boxObj = self.document.getElementById("ss_iframe_box_div"+namespace);
	var formObj = self.document.getElementById("ss_saveEntryWidthForm"+namespace);
	if (divObj == null || iframeObj == null) return;
	iframeObj.id = "ss_showentryframe";
	iframeObj.name = "ss_showentryframe";
	divObj.id = "ss_showentrydiv";
	if (formObj != null) formObj.id = "ss_saveEntryWidthForm";
	boxObj.id = "ss_iframe_box_div";
}

function ss_loadEntry(obj, id, binderId, entityType, namespace, isDashboard) {
	if (ss_userDisplayStyle == "accessible") {
		self.location.href = obj.href;
		return false;
	}
	
	ss_highlightLine(id, namespace);

	ss_showForumEntry(obj.href, isDashboard);
	return false;
}
var ss_loadEntryInPlaceLastRowObj = null;
var ss_loadEntryInPlaceLastId = null;
function ss_loadEntryInPlace(obj, id, binderId, entityType, namespace, isDashboard) {
	if (ss_userDisplayStyle == "accessible") {
		self.location.href = obj.href;
		return false;
	}
	
	trObj = ss_findOwningElement(obj, "tr")
	tbodyObj = ss_findOwningElement(trObj, "tbody")
	tableObj = ss_findOwningElement(trObj, "table")
	tableDivObj = ss_findOwningElement(trObj, "div")
	if (ss_loadEntryInPlaceLastRowObj != null) {
		if (ss_loadEntryInPlaceLastId == binderId + ',' + id) {
			tbodyObj.removeChild(ss_loadEntryInPlaceLastRowObj);
			ss_loadEntryInPlaceLastRowObj = null;
			ss_loadEntryInPlaceLastId = null;
			return;
		} else {
			tbodyObj.removeChild(ss_loadEntryInPlaceLastRowObj);
			ss_loadEntryInPlaceLastRowObj = null;
			ss_loadEntryInPlaceLastId = null;
		}
	}
	tbodyObj.insertBefore(trObj.cloneNode(true), trObj)
	ss_loadEntryInPlaceLastId = binderId + ',' + id;
	
	//Count the number of "td" elements
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
	var random = ++ss_random;
	var iframeRow = document.createElement("tr");
	var iframeCol = document.createElement("td");
	iframeCol.setAttribute("colSpan", count);
	iframeRow.appendChild(iframeCol);
	//Draw Iframe for discussion thread
	iframeCol.innerHTML = '<div style="width:'+(ss_getObjectWidth(tableDivObj)-50)+'px;">' +
		'<iframe id="ss_entry_iframe'+id+random+'" name="ss_entry_iframe'+id+random+'"' +
    	' src="'+obj.href+'"' +
    	' style="width:'+(ss_getObjectWidth(tableDivObj)-50)+'px; margin:0px; padding:0px;" frameBorder="1"' +
    	' onLoad="ss_setIframeHeight(\'ss_entry_iframeDiv'+id+random+'\', \'ss_entry_iframe'+id+random+'\')"' +
    	'>xxx</iframe>' +
    	'</div>';
	
	tbodyObj.replaceChild(iframeRow, trObj)
	ss_loadEntryInPlaceLastRowObj = iframeRow;

	ss_highlightLine(id, namespace);
	return false;
}

var ss_entryInPlaceIframeOffset = 50;
function ss_setIframeHeight(divId, iframeId) {
	var targetDiv = document.getElementById(divId);
	var iframeDiv = document.getElementById(iframeId);
	if (window.frames[iframeId] != null) {
		eval("var iframeHeight = parseInt(window." + iframeId + ".document.body.scrollHeight);")
		if (iframeHeight > 0) {
			iframeDiv.style.height = iframeHeight + ss_entryInPlaceIframeOffset + "px"
			//Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	}
}

function ss_showForumEntry(url, isDashboard) {	
	if (ss_userDisplayStyle == "accessible") {
		self.location.href = obj.href;
		return false;
	}
	if (isDashboard == "yes") {
		if (ss_userDisplayStyle == 'popup') {
			return ss_showForumEntryInIframe_Popup(url);	
		} else {
			return ss_showForumEntryInIframe_Overlay(url);
		}
	} else {
		//redefined for displayType
		return ss_showForumEntryInIframe(url);
	}
}

function ss_showForumEntryInIframe_Overlay(url) {
	if (self.parent && self != self.parent && typeof self.parent.ss_showForumEntryInIframe != "undefined") {
		self.parent.ss_showForumEntryInIframe(url);
		return
	}
    var wObj = self.document.getElementById('ss_showentryframe')
    var wObj1 = self.document.getElementById('ss_showentrydiv')
	if (wObj1 == null){
		ss_showForumEntryInIframe_Popup(url);
		return true;
	}
	
    ss_hideSpannedAreas();
    wObj1.style.display = "block";
    wObj1.style.zIndex = ssEntryZ;
    wObj1.style.visibility = "visible";

    if (wObj.src && wObj.src == url) {
    	ss_nextUrl = url
    	wObj.src = ss_forumRefreshUrl;
    } else if (wObj.src && wObj.src == ss_forumRefreshUrl && ss_nextUrl == url) {
    	wObj.src = ss_forumRefreshUrl;
    } else {
    	wObj.src = url
    }

	if (self.ss_positionEntryDiv) ss_positionEntryDiv();
    
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();

    return false;
}

function ss_showForumEntryInIframe_Popup(url) {
    //ss_debug('popup width = ' + ss_viewEntryPopupWidth)
    //ss_debug('popup height = ' + ss_viewEntryPopupHeight)
    var wObj = self.document.getElementById('ss_showfolder')

	if (!wObj) {
		if (self.parent) {
			wObj = self.parent.document.getElementById('ss_showfolder')
		}
	}
	
	if (!wObj) {
		ss_viewEntryPopupWidth = 700;
		ss_viewEntryPopupHeight = 350;
	} else {
		if (ss_viewEntryPopupWidth == "0px") ss_viewEntryPopupWidth = ss_getObjectWidth(wObj);
		if (ss_viewEntryPopupHeight == "0px") ss_viewEntryPopupHeight = parseInt(ss_getWindowHeight()) - 50;
	}
	
    self.window.open(url, '_blank', 'width='+ss_viewEntryPopupWidth+',height='+ss_viewEntryPopupHeight+',resizable,scrollbars');
    return false;
}

function ss_postComment(replyStyle) {
	alert(replyStyle)
}

function ss_dummyMethodCall() {
}



function ss_getGeneratedURL(binderId, entryId, entityType, namespace, useNewTab) {
	var binderUrl = "";
	var entryUrl = "";
	//Try to find the base urls from this namespace or from the parent or the opener
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

	//Build a url to go to
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
		//url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_folder_listing');	
	} else if (entityType == 'workspace') {
		//url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", entryId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_ws_listing');	
	} else if (entityType == 'profiles') {
		//url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
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
		ss_moveObjectToBody(fObj);
		fObj.style.zIndex = ssMenuZ;
		fObj.style.visibility = "visible";
		ss_setOpacity(fObj, 100)
		//fObj.style.display = "none";
		fObj.style.display = "block";
		var w = ss_getObjectWidth(fObj)
		ss_setObjectTop(fObj, parseInt(ss_getDivTop("ss_navbar_favorites" + namespace) + ss_favoritesPaneTopOffset))
		ss_setObjectLeft(fObj, parseInt(ss_getDivLeft("ss_navbar_favorites" + namespace)))
		var leftEnd = parseInt(ss_getDivLeft("ss_navbar_bottom" + namespace) + ss_favoritesPaneLeftOffset);
		dojo.html.hide("ss_favorites_editor" + namespace);
    	dojo.html.show(fObj);
	    dojo.html.setVisibility(fObj, "visible");
	    dojo.html.setOpacity(fObj,0);
	    dojo.lfx.html.fadeIn(fObj, 100).play();
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
		var t = '<ul style="margin: 0px 0px 0px 1px; list-style-type: none;">';
		for (var i = 0; i < favList.length; i++) {
			var f = favList[i];
			if (f.eletype != 'favorite') continue
			t += '<li id ="ss_favorite_' + f.id + '">';
			t += '<input type="checkbox" style="display: none;" />';
			t += '<a href="javascript:;" ';
			if (1 == 1 || typeof ss_displayType != "undefined" && ss_displayType == "ss_workarea") {
				t += 'onClick="ss_treeShowIdNoWS(';
			} else {
				t += 'onClick="ss_treeShowId(';
			}
			t += "'" + f.value + "', this";
			if (typeof f.action == "undefined") {
				f.action = "view_ws_listing";
			}
			t += ", '" + f.action + "'";
			t += ');">' + f.name + '</a>';
			t += '</li>';
		}
		// Close the list and add a space so the div has something in it
		// even when empty so a floating div has something to float in.
		t += '</ul>&nbsp;';
		d.innerHTML = t;
	}



	this.saveFavorites = function() {
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_favorites", deletedIds:deletedFavorites.join(" "),
						 favorites: readFavoriteList()});
		ss_showDiv("ss_favorites_loading" + namespace);
		var callback = function(data) {
				setFavoritesList(data);
				ss_hideDiv("ss_favorites_loading" + namespace);
				dojo.lfx.html.fadeHide("ss_favorites_editor" + namespace, 100).play()
		}
		ss_get_url(url, ss_createDelegate(this, callback));
	}
	function readFavoriteList() {
		var container = dojo.byId("ss_favorites_list" + namespace);
		// Get the ul inside
	    var ul = dojo.dom.getFirstChildElement(container);
	    // Walk the list items
	    var li = dojo.dom.getFirstChildElement(ul);
	    var favs = new Array();
	    while (li) {
	    	// Ids = "ss_favorite_N"
	    	favs.push(li.id.substr(12));
		    li = dojo.dom.getNextSiblingElement(li);
	    }    
	    return favs.join(" ");
	}

	this.addBinderToFavorites = function(favoriteBinderUrl) {
		loadFavorites(favoriteBinderUrl);
	}

	this.showhideFavoritesEditor = function() {
	   var ebox = dojo.byId("ss_favorites_editor" + namespace);
		if (dojo.html.isDisplayed(ebox)) {
			dojo.lfx.html.fadeHide(ebox, 100).play()
			setFavoriteListEditable(false);
		} else {
		    dojo.html.show(ebox);
		    dojo.html.setVisibility(ebox, "visible");
		    dojo.html.setOpacity(ebox,0);
		    dojo.lfx.html.fadeIn(ebox, 300).play();
			setFavoriteListEditable(true);
		}
	}

	function setFavoriteListEditable(enable) {
		var container = dojo.byId("ss_favorites_list" + namespace);
		// Clear any prior activity
		while (deletedFavorites.length) {deletedFavorites.pop() };
		// Get the ul inside
	    var ul = dojo.dom.getFirstChildElement(container);
	    // Walk the list items
	    var li = dojo.dom.getFirstChildElement(ul);
	    while (li) {
	    	var cb = dojo.dom.getFirstChildElement(li);
	    	if (enable) {
		    	dojo.html.show(cb);
		    } else {
		    	dojo.html.hide(cb);
		    }
		    li = dojo.dom.getNextSiblingElement(li);
	    }    
	}

	function getSelectedFavorites() {
		var container = dojo.byId("ss_favorites_list" + namespace);
		// Get the ul inside
	    var ul = dojo.dom.getFirstChildElement(container);
	    // Walk the list items
	    var li = dojo.dom.getFirstChildElement(ul);
	    var selected = new Array();
	    while (li) {
	    	var cb = dojo.dom.getFirstChildElement(li);
	    	if (cb.checked) {
	    		selected.push(li);
	    	}
		    li = dojo.dom.getNextSiblingElement(li);
	    }    
	    return selected;
	}


	this.deleteSelectedFavorites = function() {
	    var toDelete = getSelectedFavorites();
	    dojo.lang.forEach(toDelete, recordDeletedFavorite) 
	    dojo.lang.forEach(toDelete, dojo.dom.removeNode)
	}

	function recordDeletedFavorite(node) {
	  	deletedFavorites.push(node.id.substr(12));
	}

	this.moveSelectedFavorites = function(upDown) {
	    var toMove = getSelectedFavorites();
	    if (upDown == 'up') {
		    dojo.lang.forEach(toMove, ss_moveElementUp);
		} else {
		    dojo.lang.forEach(toMove.reverse(), ss_moveElementDown);
		}
	}
	
	this.hideFavoritesPane = function() {
		ss_hideDivFadeOut('ss_favorites_pane'+namespace, 20);
	}

	
}


function ss_moveThisTableRow(objToMove, namespace, upDown) {
    var toMove = ss_findOwningElement(objToMove, "tr");
    if (upDown == 'up') {
	    ss_moveElementUp(toMove);
	} else {
	    ss_moveElementDown(toMove);
	}
}

function ss_findOwningElement(obj, eleName) {
	var node = obj;
	while (node != null && node.tagName.toLowerCase() != eleName.toLowerCase()) {
		node = node.parentNode;
		if (node == null || node.tagName == null) node = null;
		if (node == null || node.tagName == null || node.tagName.toLowerCase() == 'body') break;
	}
	return node;
}

function ss_moveElementUp(node) {
	var prior = dojo.dom.getPreviousSiblingElement(node);
	if (prior) {
		dojo.dom.insertBefore(dojo.dom.removeNode(node), prior);
	}
}
function ss_moveElementDown(node) {
	var next = dojo.dom.getNextSiblingElement(node);
	if (next) {
		dojo.dom.insertAfter(dojo.dom.removeNode(node), next);
	}
}

function ssTeams(namespace) {
	this.show = function() {
		var fObj = self.document.getElementById("ss_myteams_pane" + namespace);
		ss_moveObjectToBody(fObj);
		fObj.style.zIndex = ssMenuZ;
		fObj.style.visibility = "visible";
		ss_setOpacity(fObj, 100)
		//fObj.style.display = "none";
		fObj.style.display = "block";
		var w = ss_getObjectWidth(fObj)
		ss_setObjectTop(fObj, parseInt(ss_getDivTop("ss_navbar_myteams" + namespace) + ss_favoritesPaneTopOffset))
		ss_setObjectLeft(fObj, parseInt(ss_getDivLeft("ss_navbar_myteams" + namespace)))
		var leftEnd = parseInt(ss_getDivLeft("ss_navbar_bottom" + namespace) + ss_favoritesPaneLeftOffset);
	    dojo.html.show(fObj);
		dojo.html.setDisplay(fObj, "block");
	    dojo.html.setVisibility(fObj, "visible");
	    dojo.html.setOpacity(fObj,0);
	    dojo.lfx.html.fadeIn(fObj, 100).play();
	    ss_fetch_url(ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"show_my_teams", namespace:namespace}), ss_createDelegate(this, showCallback));
	}
	function showCallback(data) {
		ss_hideDiv("ss_myteams_loading" + namespace);
		var d = dojo.byId("ss_myteams_list" + namespace);
		d.innerHTML = data;
	}
	this.hide = function() {
		ss_hideDivFadeOut('ss_myteams_pane'+namespace, 20);
	}
	this.showAccessible = function() {
		var dObj = self.document.getElementById("ss_navbar_myteams" + namespace);
		var fObj = self.document.getElementById("ss_myTeamsIframe" + namespace);
		dObj.style.display = "block";
	    dojo.html.setVisibility(dObj, "visible");
	    dObj.style.zIndex = parseInt(ssMenuZ);
	    fObj.src = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"show_my_teams", namespace:namespace});
	}
	this.hideAccessible = function() {
		var dObj = self.document.getElementById("ss_navbar_myteams" + namespace);
		dojo.html.setDisplay(dObj, "none");
	    dojo.html.setVisibility(dObj, "hidden");
	}
}

//
//         Routine to show/hide portal
//


function ss_toggleShowHidePortal(obj) {
	ss_moveDivToTopOfBody('ss_portlet_content')
	var divObj = document.getElementById('ss_portlet_content');
	var spanObj = document.getElementById('ss_navbarHideShowPortalText');
    if (divObj && divObj.parentNode.tagName.toLowerCase() == 'body') {
    	obj.className = "ss_global_toolbar_show_portal";
    } else {
    	obj.className = "ss_global_toolbar_hide_portal";
    }
}
//show a div as a popup - no ajax
function ss_showPopupDiv(divId) {
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	lightBox.onclick = function(e) {ss_cancelPopupDiv(divId);};
	var divObj = document.getElementById(divId);
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	divObj.style.visibility = "visible";
	divObj.style.display= "block";
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Routine to configure the columns of a folder
function ss_createPopupDiv(obj, divId) {
	url = obj.href
	url = ss_replaceSubStr(url, 'ss_randomNumberPlaceholder', ss_random++)
	var divObj = ss_createDivInBody(divId, 'ss_popupMenu');
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	divObj.style.visibility = "hidden";
	
	ss_fetch_url(url, ss_callbackPopupDiv, divId);
}

// Lightbox a dialog centered.  Optionally take an id to set focus on.
function ss_showPopupDivCentered(divId, focusId) {
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	lightBox.onclick = function(e) {ss_cancelPopupDiv(divId);};
	var divObj = document.getElementById(divId);
    ss_moveObjectToBody(divObj); 
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	ss_setupPopupDiv(divObj);
	if (focusId && (focusId != '')) {
		document.getElementById(focusId).focus();
	}
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
		targetDiv.style.left = x + "px";
		targetDiv.style.top = y + "px";
}

function ss_setupPopupDiv(targetDiv) {
		targetDiv.style.display = "block";
		ss_centerPopupDiv(targetDiv);
		targetDiv.style.visibility = "visible";
		//Signal that the layout changed
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
	//Turn off ie's 3d table look
	var dashboardTable = document.getElementById(divId);
	dashboardTable.setAttribute('borderColorDark', ss_style_background_color);
	dashboardTable.setAttribute('borderColorLight', ss_style_background_color);

	var penlets = ss_getElementsByClass('ss_dashboard_component', dashboardTable, 'div')
	for (var i = 0; i < penlets.length; i++) {
		new dojo.dnd.ss_dashboard_source(penlets[i], "penlet");
	}

	var bodyObj = document.getElementsByTagName("body").item(0);
	var targets = ss_getElementsByClass('ss_dashboardProtoDropTarget.*', dashboardTable, 'div')
	for (var i = 0; i < targets.length; i++) {
		ss_dashboardClones[i] = targets[i].cloneNode(true);
		ss_dashboardClones[i].className = "ss_dashboardDropTarget";
		ss_dashboardClones[i].style.visibility = "hidden";
		bodyObj.appendChild(ss_dashboardClones[i])
		new dojo.dnd.ss_dashboard_target(ss_dashboardClones[i], ["penlet"]);
	}
}

function ss_clearDashboardSlider() {
	var bodyObj = document.getElementsByTagName("body").item(0);
	if (ss_dashboardSliderObj != null) {
		bodyObj.removeChild(ss_dashboardSliderObj);
		dojo.html.setOpacity(ss_dashboardSliderTargetObj, 1)
	}
	ss_dashboardSliderObj = null;
}

function ss_enableDashboardDropTargets() {
	//ss_debug('enable drop targets')
	var dashboardTable = document.getElementById('ss_dashboardTable');
	ss_dashboardTableBorderColor = dashboardTable.style.borderColor;
	//ss_debug('dashboardTable.borderColor = '+dashboardTable.style.borderColor)
	dashboardTable.className = "ss_dashboardTable_on";
	var tableElements = ss_getElementsByClass('ss_dashboardTable_.*', dashboardTable, 'td')
	for (var i = 0; i < tableElements.length; i++) tableElements[i].className = "ss_dashboardTable_on";

	var narrowFixedObj = document.getElementById('narrow_fixed')
	var narrowFixedHeight = parseInt(dojo.html.getContentBox(narrowFixedObj).height);
	var narrowVariableObj = document.getElementById('narrow_variable')
	var narrowVariableHeight = parseInt(dojo.html.getContentBox(narrowVariableObj).height);
	var targets = ss_getElementsByClass('ss_dashboardProtoDropTarget', null, 'div')
	for (var i = 0; i < targets.length; i++) {
		ss_dashboardClones[i].style.left = parseInt(dojo.html.getAbsolutePosition(targets[i], true).x) + "px";
		ss_dashboardClones[i].style.top = parseInt(dojo.html.getAbsolutePosition(targets[i], true).y) + "px";
		dojo.html.setContentBox(ss_dashboardClones[i], {width: dojo.html.getContentBox(targets[i]).width})
		ss_dashboardClones[i].className = "ss_dashboardDropTarget";
		ss_dashboardClones[i].style.height = ss_dashboardDropTargetHeight;
		ss_dashboardClones[i].style.visibility = "visible";
		ss_dashboardClones[i].style.zIndex = ssDashboardTargetZ;
		ss_setOpacity(ss_dashboardClones[i], .5);
		//ss_debug('  position: '+ss_dashboardClones[i].style.left+', '+ss_dashboardClones[i].style.top)
		
		//See if the drop target needs to be enlarged
		var sourceNode = targets[i];
		var children = sourceNode.parentNode.getElementsByTagName('div');
		//ss_debug('sourceNode parent id = '+sourceNode.parentNode.id)
		if (sourceNode.parentNode.id == "wide_top") {
			//The top target gets enlarged upward
			if (children[0] == sourceNode) {
				ss_dashboardClones[i].style.height = ss_dashboardTopDropTargetHeight;
				var top = parseInt(dojo.html.getAbsolutePosition(targets[i], true).y);
				top += parseInt(dojo.html.getContentBox(targets[i]).height);
				top = top - parseInt(ss_dashboardDropTargetTopOffset);
				top = top - parseInt(ss_dashboardDropTargetTopOffset);
				top = top - parseInt(ss_dashboardTopDropTargetHeight);
				ss_dashboardClones[i].style.top = top + "px";
			}
		
		} else if (sourceNode.parentNode.id == "wide_bottom") {
			if (children[children.length - 1] == sourceNode) {
				ss_dashboardClones[i].style.height = ss_dashboardTopDropTargetHeight;
				var top = parseInt(dojo.html.getAbsolutePosition(targets[i], true).y);
				ss_dashboardClones[i].style.top = top + "px";
			}

		} else if (sourceNode.parentNode.id == "narrow_fixed") {
			//See if this is the last target in this group
			if (children[children.length - 1] == sourceNode && narrowFixedHeight < narrowVariableHeight) {
				var height = parseInt(narrowVariableHeight - narrowFixedHeight);
				if (height < parseInt(ss_dashboardDropTargetHeight)) height = parseInt(ss_dashboardDropTargetHeight);
				ss_dashboardClones[i].style.height = height + "px";
			}
		
		} else if (sourceNode.parentNode.id == "narrow_variable") {
			//See if this is the last target in this group
			if (children[children.length - 1] == sourceNode && narrowVariableHeight < narrowFixedHeight) {
				var height = parseInt(narrowFixedHeight - narrowVariableHeight);
				if (height < parseInt(ss_dashboardDropTargetHeight)) height = parseInt(ss_dashboardDropTargetHeight);
				ss_dashboardClones[i].style.height = height + "px";
			}
		}
	}
}

function ss_disableDashboardDropTargets() {
	//ss_debug('disable drop targets')
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
	ss_debug('Save the penlet order:')

	var dashboardTable = document.getElementById('ss_dashboardTable');
	var dashboardLayoutForm = document.getElementById('ss_dashboard_layout_form');
	var layout = "";
	var penlets = ss_getElementsByClass('ss_dashboard_component', dashboardTable, 'div')
	for (var i = 0; i < penlets.length; i++) {
		layout += penlets[i].id + ',' + penlets[i].parentNode.id + ';';
	}
	ss_debug('Dashboard layout: ' + layout)
	dashboardLayoutForm.dashboard_layout.value = layout;

	ss_setupStatusMessageDiv()
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"save_dashboard_layout"}, "__ajax_dashboard");
	ss_post(url, "ss_dashboard_layout_form");
}



//Presence support
function ss_popupPresenceMenu(x, userId, userTitle, status, screenName, sweepTime, email, vcard, current, ssNamespace, ssPresenceZonBridge, skypeId) {
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
	ss_presenceMenu('', x, userId, userTitle, status, screenName, sweepTime, email, vcard, current, ssNamespace, ssPresenceZonBridge, skypeId);
}

function ss_presenceMenu(divId, x, userId, userTitle, status, screenName, sweepTime, email, vcard, current, ssNamespace, ssPresenceZonBridge, skypeId) {
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
    if (screenName != '') {
        if (current == '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgimsg'+ssNamespace+'"></td>';
            if (status == 0) {
                m += '<td class="ss_fineprint ss_gray">'+ss_ostatus_sendIm+'</td>';
            } else {
                m += '<td><a class="ss_graymenu" href="iic:im?screenName=' + screenName + '">'+ss_ostatus_sendIm+'</a></td>';
            }
            m += '</tr>';
        }
        var schedule_meeting_url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"schedule_meeting", users:userId});
        
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgimtg'+ssNamespace+'"></td>';
        m += '<td><a class="ss_graymenu" href="iic:meetone?screenName=' + screenName + '">'+ss_ostatus_startIm+'</a></td></tr>';
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgsched'+ssNamespace+'"></td>';
        m += '<td><a class="ss_graymenu" href="javascript:ss_startMeeting(\'' + schedule_meeting_url + '\');">'+ss_ostatus_schedIm+'</a></td></tr>';
        m += '<tr>';
        if (ssPresenceZonBridge == 'enabled') {
        	m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgphone'+ssNamespace+'"></td>';
        	m += '<td><a class="ss_graymenu" href="javascript:ss_startMeeting(\'' + schedule_meeting_url + '\');">'+ss_ostatus_call+'</a></td></tr>';
        }
	}
	if (userId != '' && current == '') {
        if (email != '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgmail'+ssNamespace+'"></td>';

			bodyText = ss_replaceSubStrAll(ss_pagePermalink, "&", "%26");
	        
            m += '<td><a class="ss_graymenu" href="mailto:' + email + '?body=' + bodyText +'">'+ss_ostatus_sendMail+' (' + email + ')...</a></td></tr>';
        }
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgvcard'+ssNamespace+'"></td>';
        m += '<td><a class="ss_graymenu" href="' + vcard + '">'+ss_ostatus_outlook+'</a></td></tr>';	
    }

	if (userId != '') {
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgclipboard'+ssNamespace+'"></td>';
        m += '<td id="addToClipboardTD' + screenName + '"><a class="ss_graymenu" href="javascript: // ;" onclick="ss_muster.addUsersToClipboard([' + userId + ']' + (divId != ''?', function () {$(\'addToClipboardTD'+screenName+'\').innerHTML=\'OK\'}':'') + ');return false;">'+ss_ostatus_clipboard+'</a></td></tr>';
	}	
	
    if (skypeId != '') {
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" id="ppgskype' +ssNamespace+'"></td>';
        m += '<td id="skypeId' + screenName + '"><a class="ss_graymenu" href="skype:' + skypeId + '?call">' +ss_ostatus_skype+'</a></td></tr>';
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
    if (self.document.images["ppgimtg"+ssNamespace]) {
        self.document.images["ppgimtg"+ssNamespace].src = ss_presencePopupGraphics["imtg"].src;
    }
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
	        //See if we need to make the portlet longer to hold the pop-up menu
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

//FOLDER_ENTRY=1;
//FOLDER_VIEW=5;
//PROFILE_VIEW=6;
//PROFILE_ENTRY_VIEW=7;
//WORKSPACE_VIEW=8;
//FILE_FOLDER_VIEW=9;
//FILE_ENTRY_VIEW=10;
//USER_WORKSPACE_VIEW=12;
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
		//IE doesn't work on the second attempt to open the same file.
		var docList = ss_files_that_do_not_pop_up.split(" ")
		for (var i = 0; i < docList.length; i++) {
			if (results[0] == docList[i] || results[0] == "."+docList[i]) {
				return true;
			}
		}
	}
	var w = window.open(obj.href, "_blank")
	w.focus();
	return false;
}

//UI support

function ss_showSubmenu(obj) {
	ss_debug('ss_showSubmenu')
	var ulElements = obj.parentNode.getElementsByTagName('ul')
	for (var i = 0; i < ulElements.length; i++) {
		if (ulElements[i].parentNode == obj.parentNode && 
		    	ulElements[i].className.indexOf('submenu') >= 0) {
			ss_debug('  show ul '+i)
			ulElements[i].style.zIndex = parseInt(ssMenuZ)
			ulElements[i].style.display = 'block'
		}
	}
}

function ss_hideSubmenu(obj) {
	ss_debug('ss_hideSubmenu')
	var ulElements = obj.getElementsByTagName('ul')
	for (var i = 0; i < ulElements.length; i++) {
		if (ulElements[i].parentNode == obj) {
			ulElements[i].style.display = 'none'
		}
	}
}



//Profile functions
function ss_showProfileImg(obj, targetImgId) {
	//Get the url of the current image
	var imgObjs = obj.getElementsByTagName('img');
	if (imgObjs != null) {
		var imgObj = imgObjs.item(0);
		var targetImgObj = document.getElementById(targetImgId);
		if (targetImgObj != null) {
			var url = imgObj.src
			url = ss_replaceSubStr(url, "viewType=thumbnail", "viewType=scaled")
			targetImgObj.src = url;
		}
	}
}

function ss_showThisImage(obj) {
	//Get the url of the current image
	var imgObjs = obj.getElementsByTagName('img');
	if (imgObjs != null) {
		var imgObj = imgObjs.item(0);
		var url = imgObj.src
		url = ss_replaceSubStr(url, "viewType=thumbnail", "viewType=normal")
		url = ss_replaceSubStr(url, "viewType=scaled", "viewType=normal")
		self.window.open(url, "_blank")
	}
}

//Mustering routines
function ss_Clipboard () {
	var usersCheckboxes = new Array();
	var contributorsIds = new Array();
	var sBinderId;
	
	this.showForm = function (musterClass, userIds, binderId) {
		contributorsIds = userIds;
		sBinderId = binderId;
		buildDiv(musterClass, userIds);
		showDiv();
	}

	this.cancel = function () {
		ss_cancelPopupDiv('ss_muster_div');
		return false;
	}
		
	function buildDiv (musterClass) {
		//Build the muster form
		var musterDiv = document.getElementById('ss_muster_div');
		if (musterDiv != null) musterDiv.parentNode.removeChild(musterDiv);
		
		//Build a new muster div
		musterDiv = document.createElement("div");
	    musterDiv.setAttribute("id", "ss_muster_div");
	    musterDiv.setAttribute("align", "left");
	    musterDiv.style.visibility = "hidden";
	    musterDiv.className = "ss_muster_div";
	    musterDiv.style.display = "none";
		musterDiv.innerHTML = '<table class="ss_popup" cellpadding="0" cellspacing="0" border="0" style="width: 220px;">' +
         '<tbody><tr class="ss_base_title_bar"><td width="30px"><div class="ss_popup_topleft"></div></td><td width="100%"><div class="ss_popup_topcenter"><div id="ss_muster_title"></div></div></td><td width="40px"><div class="ss_popup_topright"><div id="ss_muster_close" class="ss_popup_close"></div></div>' +
         '</td></tr><tr><td colspan="3"><div id="ss_muster_inner" style="padding: 3px 10px;" class="ss_popup_body"></div></td></tr><tr><td width="30px"><div class="ss_popup_bottomleft"></div></td><td width="100%"><div class="ss_popup_bottomcenter"></div></td>' +
         '<td width="40px"><div class="ss_popup_bottomright"></div></td></tr></tbody></table>';

		// Link into the document tree
		document.getElementsByTagName("body").item(0).appendChild(musterDiv);
   
		dojo.byId("ss_muster_title").appendChild(document.createTextNode(ss_clipboardTitleText));
		
	    var formObj = document.createElement("form");
	    formObj.setAttribute("id", "ss_muster_form");
	    formObj.setAttribute("name", "ss_muster_form");
		dojo.byId("ss_muster_inner").appendChild(formObj);
		dojo.event.connect(formObj, "onsubmit", function(evt) {
			return dojoformfunction(this);
	    });
	    var hiddenObj = document.createElement("input");
	    hiddenObj.setAttribute("type", "hidden");
	    hiddenObj.setAttribute("name", "muster_class");
	    hiddenObj.setAttribute("value", musterClass);
	    formObj.appendChild(hiddenObj);
		
		var brObj = document.createElement("br");

		var addBtnDivObj = document.createElement("div");
		addBtnDivObj.style.textAlign = "right";
		addBtnDivObj.style.width = "100%";

		var addContrBtnObj = document.createElement("input");
		addContrBtnObj.setAttribute("type", "button");
		addContrBtnObj.setAttribute("name", "add");
		addContrBtnObj.setAttribute("value", ss_addContributesToClipboardText);
		dojo.event.connect(addContrBtnObj, "onclick", function(evt) {
			ss_muster.addContributesToClipboard();
			return false;
	    });

		var addTeamMembersBtnObj = document.createElement("input");
		addTeamMembersBtnObj.setAttribute("type", "button");
		addTeamMembersBtnObj.setAttribute("name", "add");
		addTeamMembersBtnObj.setAttribute("value", ss_addTeamMembersToClipboardText);
		dojo.event.connect(addTeamMembersBtnObj, "onclick", function(evt) {
			ss_muster.addTeamMembersToClipboard();
			return false;
	    });

		addBtnDivObj.appendChild(addContrBtnObj);
		addBtnDivObj.appendChild(brObj.cloneNode(false));
		if (sBinderId)
			addBtnDivObj.appendChild(addTeamMembersBtnObj);
		
		formObj.appendChild(addBtnDivObj);
		formObj.appendChild(brObj);
		
		// Add list container
		var divObj = document.createElement("div");
		divObj.id = "ss_muster_list_container";
		formObj.appendChild(divObj);
		
		//Add the buttons 		
		var deleteBtnObj = document.createElement("input");
		deleteBtnObj.setAttribute("type", "button");
		deleteBtnObj.setAttribute("name", "clear");
		deleteBtnObj.setAttribute("value", ss_clearClipboardText);
		dojo.event.connect(deleteBtnObj, "onclick", function(evt) {
			ss_muster.removeFromClipboard('ss_muster_form');
			return false;
	    });

		deleteBtnObj.style.marginRight = "15px"

		dojo.event.connect(dojo.byId("ss_muster_close"), "onclick", function(evt) {
			ss_muster.cancel();
	    });

		formObj.appendChild(brObj.cloneNode(false));
		formObj.appendChild(deleteBtnObj);
		

		loadUsers(divObj);
	}
	
	function loadUsers (divObj) {
		if (!divObj)
			return;
		ss_toggleAjaxLoadingIndicator(divObj, true);
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"get_clipboard_users", randomNumber:ss_random++}, "clipboard");
		
		var bindArgs = {
	    	url: url,
			error: function(type, data, evt) {
				ss_toggleAjaxLoadingIndicator(divObj);
				alert(ss_not_logged_in);
			},
			load: function(type, data, evt) {
				ss_toggleAjaxLoadingIndicator(divObj);
				displayUsers(data, divObj);			
			},
			mimetype: "text/json",
			transport: "XMLHTTPTransport",
			method: "get"
		};
	   
		dojo.io.bind(bindArgs);
	}
	
	function displayUsers(data, containerObj) {
		if (data.length > 0) {
			usersCheckboxes = new Array();

			var ulObj = document.createElement("ul");
			ulObj.style.marginLeft = "0";
			ulObj.style.marginTop = "5px";
			ulObj.style.marginBottom = "15px";
			ulObj.style.marginRight = "0";
			ulObj.style.padding = "0";

			var lastIndex = 0;
			for (var i = 0; i < data.length; i++) {
				ulObj.appendChild(createUserLI(i, data[i][0], data[i][1]));
				lastIndex = i;
			}
			
			var hrefSelectAllObj = document.createElement("a");
			hrefSelectAllObj.href = "javascript: //;";
			// hrefSelectAllObj.onclick = ss_muster.selectAll;
			dojo.event.connect(hrefSelectAllObj, "onclick", function(evt) {
				ss_muster.selectAll();
		    });

			hrefSelectAllObj.className = "ss_linkButton";
			hrefSelectAllObj.style.marginRight = "5px";
			hrefSelectAllObj.appendChild(document.createTextNode(ss_selectAllBtnText));

			var hrefDeselectAllObj = document.createElement("a");
			hrefDeselectAllObj.href = "javascript: //";
			// hrefDeselectAllObj.onclick = ss_muster.clearAll;
			dojo.event.connect(hrefDeselectAllObj, "onclick", function(evt) {
				ss_muster.clearAll();
		    });

			hrefDeselectAllObj.className = "ss_linkButton";
			hrefDeselectAllObj.style.marginRight = "5px";
			hrefDeselectAllObj.appendChild(document.createTextNode(ss_clearAllBtnText));

			containerObj.innerHTML = "";
			containerObj.appendChild(ulObj);
			containerObj.appendChild(hrefSelectAllObj);
			containerObj.appendChild(hrefDeselectAllObj);
		}
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
		//Show the muster form
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
			error: function(type, data, evt) {
				alert(data.message);
			},
			load: function(type, data, evt) {
				if (data.failure) {
					alert(data.failure);
				} else { 
					if (afterPostRoutine) afterPostRoutine(args);
				}
			},
			preventCache: true,				
			mimetype: "text/json"
		};   
		dojo.io.bind(bindArgs);	
	}
	
	this.removeFromClipboard = function (formId) {
		var bindArgs = {
	    	url: ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"remove_from_clipboard"}, "clipboard"),
	    	formNode: dojo.byId(formId),	    	
			error: function(type, data, evt) {
				alert(data.message);
			},
			load: function(type, data, evt) {
				if (data.failure) {
					alert(data.failure);
				} else { 
					ss_cancelPopupDiv('ss_muster_div');
				}
			},
			preventCache: true,				
			mimetype: "text/json",
			method: "post"			
		};   
		dojo.io.bind(bindArgs);	
	}
}

var ss_muster = new ss_Clipboard();

/*
	Starts a Zon meeting with given id;
*/
function ss_launchMeeting(id) {
	try {
		self.location.href = 'iic:meetmany?meetingtoken=' + id;
	} catch (e) {
		alert(ss_rtc_not_configured);
		// iic protocol unknown
	}
	return false;
}

/*
	Creates a new Zon meeting and launch it now.
	
	ajaxLoadingIndicatorPane: add loading indicator as child of this HTML element (if exists)
*/
function ss_startMeeting(url, formId, ajaxLoadingIndicatorPane) {
	ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane, true);
	
	url += "\&randomNumber="+ss_random++;
	
	var bindArgs = {
    	url: url,
		error: function(type, data, evt) {
			ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
			alert(ss_not_logged_in);
		},
		load: function(type, data, evt) {
			ss_toggleAjaxLoadingIndicator(ajaxLoadingIndicatorPane);
			if ((!data.meetingToken || data.meetingToken == "") && data.meetingError) {
				alert(data.meetingError);
			} else {
				ss_launchMeeting(data.meetingToken);
			}
			
		},
		formNode: $(formId),
		mimetype: "text/json",
		method: "post"
	};
   
	dojo.io.bind(bindArgs);
}

/*
	Show/Hide ajax loading animated icon. The icon displays/disappears in given HTML-Element as child.
	
	objId: HTML element id OR HTML element
	append: 
		true - append loading indicator as child to objId
		false - replace objId content with loading indicator
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
	== document.getElementById(id) 
*/
function $(id) {
	return document.getElementById(id);
}

/*
 	Summary:
 	
 	Use this routine to create 'select all' checkbox.
 	
 	Description:
 	
 	Routine synchronize checbox 'selectAllCheckboxId' with group of checkboxes with name 'checkboxesName'. If checkbox 'selectAllCheckboxId' is
 	checked/unchecked then all 'checkboxesName' checkboxes are checked/unchecked too.
 	
 	Example:
 	 
 	 	HTML:
 	 		<input type="checkbox" name="team_member_ids" value="998" /> Joe Bloggs
 	 		<input type="checkbox" name="team_member_ids" value="999" /> Bob Dao
 	 		
 	 		<input type="checkbox" id="team_member_all_ids" /> Select all 
 	 		
 	 	JavaScript:
 			ss_synchronizeCheckboxes("team_member_all_ids", "team_member_ids");
  
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

//Routine to pop-up an edit window for editing an element
var ss_minEditWidth = 300;
var ss_minEditHeight = 480;
var ss_editWidthOffset = 20;
var ss_editHeightOffset = 100;
var ss_editScreenWidthOfset = 60;
var ss_editScreenHeightOfset = 60;
function ss_editablePopUp(url, sourceDivId) {
	var width = parseInt(ss_getDivWidth(sourceDivId) + ss_editWidthOffset);
	var height = parseInt(ss_getDivHeight(sourceDivId) + ss_editHeightOffset);
	if (width < ss_minEditWidth) width = ss_minEditWidth;
	if (height < ss_minEditHeight) height = ss_minEditHeight;
	if (width > parseInt(screen.width - ss_editScreenWidthOfset)) width = parseInt(screen.width - ss_editScreenWidthOfset)
	if (height > parseInt(screen.height - ss_editScreenHeightOfset)) height = parseInt(screen.height - ss_editScreenHeightOfset)
	self.window.open(url, '_blank', 'width='+width+',height='+height+',directories=no,location=no,menubar=no,resizable=yes,scrollbars=yes,status=no,toolbar=no');
}

function ss_editableHighlight(overOut, obj, divId) {
	if (overOut == 'over') {
		dojo.byId(divId).style.border = "dashed 1px #666666";
	} else {
		dojo.byId(divId).style.border = "solid 1px transparent";
	}
}


function ss_submitParentForm(htmlObj) {
	if (htmlObj.submit) {
		htmlObj.submit();
	} else if (htmlObj.parentNode) {
		ss_submitParentForm(htmlObj.parentNode);
	}
}

function ss_putValueInto(objId, value) {
	document.getElementById(objId).value = value;
}

function ss_ajaxValidate(url, obj, labelId, msgBoxId) {
	ss_setupStatusMessageDiv();
 	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.addKeyValue("ss_ajaxId",obj.id);
	ajaxRequest.addKeyValue("ss_ajaxValue",obj.value);
	ajaxRequest.addKeyValue("ss_ajaxLabelId",labelId);
	ajaxRequest.addKeyValue("ss_ajaxMsgId",msgBoxId);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.sendRequest();  //Send the request
}

//Routine to pop-up a "find user" window
var ss_launchFindUserWindowElement = null;
function ss_launchFindUserWindow(elementId) {
	ss_launchFindUserWindowElement = elementId;
	alert('Not implemented yet. Entry user id directly into text box on the left.')
}

// Rountine submits form when ENTER pressed, 
// Use: attache event onkeypress="return ss_submitViaEnter(event)" to form fields
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
		// i.setAttribute("onclick", "ss_cancelPopupDiv('ss_validation_errors_div')");
		dojo.event.connect(i, "onclick", function(evt) {
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

//Common validator handler
//  This function will call the desired routines at form validate time
//  If any routine returns "false", then this routine returns false.
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

function ss_startSpinner()
{
	var spinner = document.getElementById("ss_spinner")
	var bodyObj = document.getElementsByTagName("body").item(0)
	if (!spinner) {
		spinner = document.createElement("div");
        spinner.setAttribute("id", "ss_spinner");
		var spinImg = document.createElement("img");
		spinImg.setAttribute("src", ss_imagesPath + "pics/spinner.gif");
		spinner.appendChild(spinImg);
		var status = document.createElement("div");
		status.setAttribute("id", "ss_operation_status");
		spinner.appendChild(status);
		
        bodyObj.appendChild(spinner);
	}
	spinner.style.position='absolute';
    spinner.style.zIndex = 1000;
	spinner.style.display='block';
	ss_centerPopupDiv(spinner);
}

function ss_stopSpinner()
{
	var spinner = document.getElementById("ss_spinner")
	if (spinner) {
	  spinner.style.display='none';
	}
}

dojo.require("dojo.html.iframe");

function ss_showSavedQueriesList(relObj, divId, resultUrl) {

	if (dojo.html.isDisplayed(divId)) {
		dojo.lfx.html.fadeHide(divId, 100).play();
		//dojo.html.hide(divId);
		return false;
	}
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"list_saved_queries"});
	
	var bindArgs = {
    	url: url,
		error: function(type, data, evt) {
			alert(ss_not_logged_in);
		},
		load: function(type, data, evt) {
			try {
				var divObj = document.getElementById(divId);
			
				
				var txt = '<div class="ss_popupMenuClose" align="right">';
				txt += '<a href="javascript: ;" ';
				txt += 'onClick="ss_hideDivNone(this.parentNode.parentNode.id);">'
				txt += '<img src="' + ss_imagesPath + 'pics/sym_s_delete.gif" border="0"/></a></div>'
				txt += "<h1>" + ss_savedSearchTitle + "</h1><ul>";
				for (var queryNo = 0; queryNo < data.length; queryNo++) {
					txt += "<li><a href=\"" + resultUrl + "&operation=ss_savedQuery&newTab=1&ss_queryName=" + data[queryNo] + "\">"+data[queryNo]+"</a></li>";
				}
				txt += "</ul>";
				divObj.innerHTML = txt;

				ss_placeOnScreen(divId, relObj, 12, -128);
				dojo.html.setDisplay(divId, "block");
				dojo.html.setVisibility(divId, "visible");
	            dojo.html.setOpacity(divId,0);
	            dojo.lfx.html.fadeIn(divId, 200).play();
				//Signal that the layout changed
				if (ssf_onLayoutChange) ssf_onLayoutChange();
			} catch (e) {alert(e)}
		},
		preventCache: true,
		mimetype: "text/json",
		method: "get"
	};   
	dojo.io.bind(bindArgs);	
}

function ss_placeOnScreen(div, rel, offsetTop, offsetLeft) {
	var box = dojo.html.abs(rel);
	ss_moveDivToBody(div);
	dojo.html.placeOnScreen(div, box.left + offsetLeft, box.top + offsetTop, 0, false, "TL");
}


function ss_submitFormViaAjax(formName, doneRoutine) {
	ss_setupStatusMessageDiv()
	var formObj = document.forms[formName];
	var ajaxRequest = new ss_AjaxRequest(formObj.action); //Create AjaxRequest object
	ajaxRequest.addFormElements(formName);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setData("doneRoutine", doneRoutine)
	ajaxRequest.setPostRequest(ss_postSubmitFormViaAjax);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
	return false;
}

function ss_postSubmitFormViaAjax(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else {
		var doneRoutine = obj.getData("doneRoutine");
		eval("setTimeout('"+doneRoutine+"();', 100)");
	}
}

function ss_changeUITheme(idListText, nameListText) {
	var idList = idListText.split(",");
	var nameList = nameListText.split(",");
	var divObj = ss_createDivInBody('ss_uiThemeSelector', 'ss_themeMenu');
	divObj.style.zIndex = parseInt(ssLightboxZ + 1);
	var divHtml = '<ul>';	
	for (var t=0; t<idList.length; t++) {
		var link = '<li><a href="javascript: ;" onclick="ss_changeUIThemeRequest(';
		link += "'" + idList[t] + "'" + ');">';
		link += nameList[t] + '</a></li>';
		divHtml += link;
	}
	divHtml += '</ul>';
	divObj.innerHTML = divHtml;
	var lightBox = ss_showLightbox(null, ssLightboxZ, .5);
	lightBox.onclick = function(e) {ss_cancelUITheme();};
	divObj.style.visibility = "visible";
	divObj.style.display= "block";	
	ss_centerPopupDiv(divObj);
}

function ss_changeUIThemeRequest(themeId) {
	var setUrl = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"set_ui_theme", theme:themeId});

	var bindArgs = {
    	url: setUrl,
		error: function(type, data, evt) {
			alert(ss_not_logged_in);
		},
		load: function(type, data, evt) {
  		  try {
		  	document.location.reload();	  	
	      } catch (e) {alert(e);}
		},
		preventCache: true,				
		mimetype: "text/plain",
		method: "get"
	};   
	dojo.io.bind(bindArgs);
	    
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
	ss_centerPopupDiv(divObj);
}


function ss_cancelAbout(divId) {
	ss_hideLightbox();
	ss_hideDiv(divId);
}




//Hemanth: This method will be called by the links that get created in tiny MCE
//Refer to link.js insertLink() method.
function ss_checkTypeOfLink(linkObj) {
	var targetValue = linkObj.target;
	var url = linkObj.href;
	
	if (targetValue == '_blank') return true;
	else {
		if (self.window != self.top) {
			parent.location.href = url;
		} else {
			return true;
		}
	}
	return false;
}

function ss_buddyPhotoLoadError (imgObj, src) {
	imgObj.src = src;
}

function ss_tagSearchObj(obj) {
    var tag = dojo.dom.textContent(obj);
	var searchUrl = "";
   	try { searchUrl = ss_tagSearchResultUrl; } catch(e) {searchUrl=""}
	if (searchUrl == "") { try { searchUrl = self.parent.ss_tagSearchResultUrl } catch(e) {searchUrl=""} }
	if (searchUrl == "") { try { searchUrl = self.opener.ss_tagSearchResultUrl } catch(e) {searchUrl=""} }
	var url = ss_replaceSubStrAll(searchUrl, 'ss_tagPlaceHolder', tag);
	if (ss_openUrlInPortlet(url)) {
		self.location.href = url;
	}
	return false;
}

function ss_scrollOuter() {
	window.scrollTo(0,0);
}

/* TREE WIDGET */

//Routines to display an expandable/contractable tree
//
var ss_treeIds;
if (ss_treeIds == null) ss_treeIds = new Array();
function ss_treeToggle(treeName, id, parentId, bottom, type, page, indentKey) {
	ss_hideBucketText()
	if (page == null) page = "";
	if (window["ss_treeDisplayStyle"] && ss_treeDisplayStyle == 'accessible') {
		return ss_treeToggleAccessible(treeName, id, parentId, bottom, type, page, indentKey);
	}
	ss_setupStatusMessageDiv()
    var tObj = self.document.getElementById(treeName + "div" + id);
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    var showTreeIdRoutine = window["ss_treeShowIdRoutine_"+treeName];
    if (tObj == null) {
        //See if the tree is in the process of being loaded
        if (ss_treeIds[treeName + "div" + id] != null) return;
        ss_treeIds[treeName + "div" + id] = "1";
        //The div hasn't been loaded yet. Go get the div via ajax
		var url = window["ss_treeAjaxUrl_" + treeName];
		url = ss_replaceSubStrAll(url, "&amp;", "&");
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addKeyValue("binderId", id)
		ajaxRequest.addKeyValue("treeName", treeName)
		ajaxRequest.addKeyValue("page", page)
		ajaxRequest.addKeyValue("indentKey", indentKey)
		ajaxRequest.addKeyValue("showIdRoutine", showTreeIdRoutine)
 		var treeKey = window["ss_treeKey_"+treeName];
		if (treeKey != null)
			ajaxRequest.addKeyValue("treeKey", treeKey);
	    var seObj = window["ss_treeSelected_"+treeName];
	    //add single select id
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
		//ajaxRequest.setEchoDebugInfo();
		//ajaxRequest.setPreRequest(ss_preRequest);
		ajaxRequest.setPostRequest(ss_postTreeDivRequest);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
    } else {
	    if (tObj.style.display == "none" || tObj.style.visibility == 'hidden') {
	        tObj.style.display = "block";
	        tObj.style.visibility = 'visible';
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
					jObj.className = "ss_minusTop";        // minus_top.gif (no join lines)
				} else {
					jObj.className = "ss_minus";           // minus.gif (no join lines)
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
					jObj.className = "ss_plusTop";        // plus_top.gif (no join lines)
				} else {
					jObj.className = "ss_plus";           // plus.gif (no join lines)
				}
			}
			if (iObj != null && ss_treeIconsClosed[type]) iObj.src = ss_treeIconsClosed[type];
	    }
		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
		
		self.focus();
	}
}

function ss_treeToggleAccessible(treeName, id, parentId, bottom, type, page, indentKey) {
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
		dojo.event.connect(iframeObj, "onload", function(evt) {
			var iframeDiv = document.getElementById('ss_treeIframe');
			if (window.frames['ss_treeIframe'] != null) {
				eval("var iframeHeight = parseInt(window.ss_treeIframe" + ".document.body.scrollHeight);");
				if (iframeHeight > 0) {
					iframeDiv.style.height = iframeHeight + 50 + "px";
				}
			}
			return false;
	    });		
    }
    if (iframeDivObj == null) iframeDivObj = iframeDivObjParent;
    if (iframeObj == null) iframeObj = iframeObjParent;
	var x = dojo.html.getAbsolutePosition(tempObj, true).x
	var y = dojo.html.getAbsolutePosition(tempObj, true).y
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
	//add single select id
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
		self.location.href = url;
	} else {
		iframeObj.src = url;
	}
}

function ss_createTreeCheckbox(treeName, prefix, id) {
	var divObj = document.getElementById("ss_hiddenTreeDiv"+treeName);
	var cbObj = document.getElementById("ss_tree_checkbox" + treeName + prefix + id)
	if (cbObj == null) {
		//alert("null: ss_tree_checkbox" + treeName + prefix + id)
	} else {
		//alert("Not null: ss_tree_checkbox" + treeName + prefix + id)
	}
}

function ss_positionAccessibleIframe(treeName, id) {
	ss_debug('position: '+ parent.ss_getDivTop(treeName + "temp" + id))
    var iframeDivObj = self.document.getElementById("ss_treeIframeDiv");
	ss_setObjectTop(iframeDivObj, ss_getDivTop(treeName + "temp" + id));
	ss_setObjectLeft(iframeDivObj, ss_getDivLeft(treeName + "temp" + id));
	ss_showDiv(iframeDivObj);
}

function ss_postTreeDivRequest(obj) {
	ss_hideBucketText()
	//See if there was an error
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
    	//alert("ss_treeOpen div obj = null: " + treeName + "div" + id)
    } else {
    	//alert("ss_treeOpen id: " + tObj.id)
    }
    if (tObj == null) {
		//nothing came back treat as empty
		//this happens when don't have access or binder type when go to get data
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
					jObj.className = "ss_minus_top";         // minus_top.gif (no join lines)
				} else {
					jObj.className = "ss_minus";             // minus.gif (no join lines)
				}
			}
		}
		if (iObj != null && ss_treeIconsOpen[type]) iObj.src = ss_treeIconsOpen[type];

		//Signal that the layout changed
		if (ssf_onLayoutChange) setTimeout('ssf_onLayoutChange();', 100);
		
		self.focus();
	}
}

function ss_treeToggleAll(treeName, id, parentId, bottom, type, page, indentKey) {
	ss_hideBucketText()
	if (page == null) page = "";
    var tObj = self.document.getElementById(treeName + "div" + id);
    if (tObj == null) {
    	//The div hasn't been loaded yet. Only load one div at a time
    	ss_treeToggle(treeName, id, parentId, bottom, type, page, indentKey)
    	return
    }
	if (tObj.style.display == "none") {
		ss_treeToggle(treeName, id, parentId, bottom, type, page, indentKey)
	}
    var children = tObj.childNodes;
    for (var i = 0; i < children.length; i++) {
    	if (children[i].id && children[i].id.indexOf(treeName + "div") == 0) {
			var nodeRoot = treeName + "div";
			var childnode = children[i].id.substr(nodeRoot.length)
    		if (children[i].style.display == "none") {
    			ss_treeToggle(treeName, childnode, id, bottom, type, "", indentKey)
    		}
    		ss_treeToggleAll(treeName, childnode, id, bottom, type, "", indentKey)
    	}
    }
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

//Routines to show and hide a tool tip at an object
function ss_showBucketText(obj, text) {
	//ss_debug('ss_showTip: '+text)
	var tipObj = document.getElementById('ss_treeBucketTextDiv')
	if (tipObj == null) {
		//Build a new tip div
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
	tipObj.style.fontSize = obj.style.fontSize;
	tipObj.style.fontFamily = obj.style.fontFamily;
	var x = dojo.html.getAbsolutePosition(obj, true).x
	var y = dojo.html.getAbsolutePosition(obj, true).y
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

function ss_clearSingleSelect(treeName, idChoicesInputId) {
	window["ss_treeSelected_" + treeName] = null;
	if (parent) {
		// in accessible mode
		parent.window["ss_treeSelected_" + treeName] = null;
	}
	var inputHiddenObj = document.getElementById(treeName + "_lastChoice");
	if (!inputHiddenObj && parent) {
		inputHiddenObj = parent.document.getElementById(treeName + "_lastChoice");
	}
	
	if (parent) {
		// in accessible mode only - unselect radio
		if (inputHiddenObj) {
			var selected = parent.document.getElementById("ss_tree_radio" + treeName + inputHiddenObj.name + inputHiddenObj.value);
			if (!selected) {
				var treeIframe = document.getElementById("ss_treeIframe");
				if (treeIframe) {
					var doc = treeIframe.document ? treeIframe.document : treeIframe.contentDocument;
					var selected = doc.getElementById("ss_tree_radio" + treeName + inputHiddenObj.name + inputHiddenObj.value);
				}
			}
			if (selected && selected.checked) {
				selected.checked = false;
			}			
		}
	}
		
	if (inputHiddenObj) {
		inputHiddenObj.parentNode.removeChild(inputHiddenObj);
	}
	
	return true;
}

function ss_clearMultiSelect(id) {
	var inputHiddenObj = document.getElementById(id + "_lastChoice");
	if (!inputHiddenObj && parent) {
		// in accessible mode
		inputHiddenObj = parent.document.getElementById(id + "_lastChoice");
	}
	if (inputHiddenObj) {
		inputHiddenObj.parentNode.removeChild(inputHiddenObj);
	}
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
			if (idChoices.value && idChoices.value != (obj.name + "%" + obj.value)) {
				selected = parent.document.getElementById("ss_tree_radio" + treeName + idChoices.value);
				if (selected && selected.checked) {
					selected.checked = false;
				}
			}
		
			idChoices.value = obj.name + "%" + obj.value;
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
			return;
		}
		rowObj.style.display = 'table-row';
		rowObj.style.visibility = 'visible';
		more = count < end;
		count++;
	}
	var rowObj = document.getElementById(prefix + count + "n");
	if (rowObj) {
		rowObj.style.display = 'table-row';
		rowObj.style.visibility = 'visible';
	}	
}



//Routine to show or hide the sidebar
function ss_showHideSidebar(namespace) {
	var divObj = self.document.getElementById('ss_sidebarDiv'+namespace);
	var tdObj = self.document.getElementById('ss_sidebarTd'+namespace);
	var sidebarHide = self.document.getElementById('ss_sidebarHide'+namespace);
	var sidebarShow = self.document.getElementById('ss_sidebarShow'+namespace);
	var sidebarVisibility = "";
	if (divObj.style.display == 'block') {
		//Hide it
   		dojo.lfx.html.fade(divObj, {end: 0}, 400, '', function() {
		    	divObj.style.visibility = "hidden";
		    	divObj.style.display = "none";
				tdObj.className = '';
				sidebarShow.style.display = 'none'
				sidebarHide.style.display = 'block'
   		}).play();
   		sidebarVisibility = "none";
	} else {
		//Show it
		tdObj.className = 'ss_view_sidebar';
    	dojo.html.setOpacity(divObj, 0);
		divObj.style.display = 'block';
    	divObj.style.visibility = "visible";
	    dojo.lfx.html.fade(divObj, {start:0, end:1.0}, 400).play();
		sidebarShow.style.display = 'block'
		sidebarHide.style.display = 'none'
		sidebarVisibility = "block"
	}
	ssf_onLayoutChange()
	ss_setupStatusMessageDiv();
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"set_sidebar_visibility", visibility:sidebarVisibility}, "");
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.sendRequest();  //Send the request
}


