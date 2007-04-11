/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
//Common javascript functions for forum portlets

//   Copyright (c) 2005 / SiteScape, Inc.  All Rights Reserved.
//
//  This information in this document is subject to change without notice 
//  and should not be construed as a commitment by SiteScape, Inc.  
//  SiteScape, Inc. assumes no responsibility for any errors that may appear 
//  in this document.
//
//  Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
//  is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
//  Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
//
//  SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
//
//
// browser-specific vars

var undefined;
var ss_declaredDivs;
if (!ss_common_loaded || ss_common_loaded == undefined || ss_common_loaded == "undefined" ) {
	var isNSN = (navigator.appName == "Netscape");
	var isNSN4 = isNSN && ((navigator.userAgent.indexOf("Mozilla/4") > -1));
	var isNSN6 = ((navigator.userAgent.indexOf("Netscape6") > -1));
	var isMoz5 = ((navigator.userAgent.indexOf("Mozilla/5") > -1) && !isNSN6);
	var isMacIE = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("Mac") > -1));
	var isIE = ((navigator.userAgent.indexOf("IE ") > -1));
	
	//Random number seed (for building urls that are unique)
	var ss_random = Math.round(Math.random()*999999);
	
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
	var ss_menuDivClones = new Array();
	var divToBeHidden = new Array;
	var divToBeDelayHidden = new Array;
	var ss_onErrorList = new Array();
	var ss_spannedAreasList = new Array();
	var ss_active_menulayer = '';
	var ss_lastActive_menulayer = '';
	var ss_activateMenuOffsetTop = 6;
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
	
	var ss_favoritesPaneTopOffset = 0;
	var ss_favoritesPaneLeftOffset = 4;
	var ss_favoritesMarginW = 4;
	var ss_favoritesMarginH = 6;

	var ss_dashboardClones = new Array();
	var ss_dashboardSliderObj = null;
	var ss_dashboardSliderTargetObj = null;
	var ss_dashboardSliderObjEndCoords = null;
	
	var ss_currentTab = 0;
		
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
function ss_openUrlInPortlet(url, popup) {
	//Is this a request to pop up?
	ss_debug('popup = '+popup+', url = '+url)
	if (popup) {
		self.window.open(url, "_blank", "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no");
		return false;
	}
	//Are we at the top window?
	if (self.window != self.top) {
		ss_debug('Not at top window')
		parent.location.href = url;
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

function ss_openUrlInNewTab(url) {
	self.location.href = url + "&newTab=1";
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
function ss_openUrlInWindow(obj,windowName) {
	if (windowName == "") {
		//There is no window, so open it in this window
		return true;
	} else {
		var url = obj.href
		var win = self.window.open(url, windowName, 'directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no')
		if (win.focus) win.focus();
	}
	return false;
}

//Routine to show the permalink url so it can by cut/pasted
function ss_showPermalink(obj) {
	//See if the div exists already
	var divObj = document.getElementById('ss_permalink_display_div');
	var divObj3 = document.getElementById('ss_permalink_display_div3');
	if (divObj == null) {
		//Create the div
	    divObj = document.createElement("div");
	    divObj.setAttribute("id", "ss_permalink_display_div");
	    divObj.className = "ss_style ss_popupMenu";
	    divObj2 = document.createElement("div");
	    divObj3 = document.createElement("div");
	    divObj3.setAttribute("id", "ss_permalink_display_div3");
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
	    divObj.appendChild(divObj3)
		document.getElementsByTagName( "body" ).item(0).appendChild(divObj);
	}
	divObj3.innerHTML = obj.href;
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
}

//Routine to close a pop-up form window if the cancel button is clicked
//  This routine checks to see if it is in a pop-up or in an iframe
function ss_cancelButtonCloseWindow() {
	if (self == self.parent && self.opener) {
		//This looks like it is a pop-up form
		self.window.close();
		return
	} else if (self != self.parent) {
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

// Replace an image (e.g. expand/collapse arrows)
function ss_replaceImage(imgName, imgPath) {
    if (document.images) {
        eval('if (document.images[\''+imgName+'\']) {document.images[\''+imgName+'\'].src = imgPath}');
    }
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
    if (isNSN) {
        eval("self.document.captureEvents(Event."+event_name+")")
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
    if (!isNSN) {e = event}
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
//  If any routine returns "false", then this routine returns false.
function ss_onSubmit(obj) {
    var result = true;
    for (var i = 0; i < ss_onSubmitList.length; i++) {
        if (ss_onSubmitList[i].formName == obj.name) {
            if (!ss_onSubmitList[i].submitRoutine()) {result = false;}
        }
    }
    return result;
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
        	//ss_debug("ssf_onLayoutChange: " + ss_onLayoutChangeList[i].name)
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
    if (isNSN || isNSN6 || isMoz5) {
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
    if (isNSN6 || isMoz5) {
        var obj = document.anchors[anchorName]
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
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
    if (isNSN6 || isMoz5) {
        var obj = document.anchors[anchorName]
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
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
    if (isNSN6 || isMoz5) {
        var obj = document.images[imageName]
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
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
    if (isNSN6 || isMoz5) {
        var obj = document.images[imageName]
        while (1) {
            if (!obj) {break}
            left += parseInt(obj.offsetLeft)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else if (isNSN) {
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
    if (isNSN6 || isMoz5) {
        return parseInt(obj.offsetWidth)
    } else if (isNSN) {
        return parseInt(obj.clip.width)
    } else {
        return parseInt(obj.clientWidth)
    }
}

function ss_getObjectHeight(obj) {
    if (isNSN6 || isMoz5) {
        return parseInt(obj.offsetHeight)
    } else if (isNSN) {
        return parseInt(obj.clip.height)
    } else {
        return parseInt(obj.clientHeight)
    }
}

function ss_getObjectLeft(obj) {
    if (isNSN6 || isMoz5) {
        return parseInt(obj.style.left)
    } else if (isNSN) {
        return parseInt(obj.style.left)
    } else {
        return parseInt(obj.style.pixelLeft)
    }
}

function ss_getObjectTop(obj) {
    if (isNSN6 || isMoz5) {
        return parseInt(obj.style.top)
    } else if (isNSN) {
        return parseInt(obj.style.top)
    } else {
        return parseInt(obj.style.pixelTop)
    }
}

function ss_getObjectLeftAbs(obj) {
    var left = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        left += parentObj.offsetParent.offsetLeft
        parentObj = parentObj.offsetParent
    }
    return left
}

function ss_getObjectTopAbs(obj) {
    var top = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        top += parentObj.offsetParent.offsetTop
        parentObj = parentObj.offsetParent
    }
    return top
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
    var h = self.document.body.scrollHeight;
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

function smoothScroll(x, y) {
	smoothScrollInTime(x,y,10)
}

function smoothScrollInTime(x, y, steps) {
    if (steps <= 1) {
		window.scroll(x,y)
    } else {
	    var bodyX = self.document.body.scrollLeft
	    var bodyY = self.document.body.scrollTop
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


// Pop-up menu support
// clicking anywhere will hide the div

//Create a clone of the menu before showing it; attach it to the "body" outside of any div
//  This makes sure that the z-index will be on top of everything else (IE fix)
function ss_activateMenuLayerClone(divId, parentDivId, offsetLeft, offsetTop, openStyle) {
	if (!parentDivId || parentDivId == null || parentDivId == 'undefined') {parentDivId=""}
	if (!offsetLeft || offsetLeft == null || offsetLeft == 'undefined') {offsetLeft="0"}
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

    if (isNSN6 || isMoz5) {
        // need to bump layer an extra bit to the right to avoid horiz scrollbar
        divWidth = parseInt(self.document.getElementById(divId).offsetWidth) + 25;
        maxWidth = parseInt(window.innerWidth);
    } else {
        divWidth = parseInt(self.document.all[divId].clientWidth) + 25;
        maxWidth = parseInt(document.body.scrollWidth);
    }

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

function ss_showDivActivate(divName) {
    if (ss_divBeingShown != null) {
        ss_hideDiv(ss_divBeingShown)
    }
    ss_divBeingShown = divName;
    ss_lastDivBeingShown = divName;
    ss_showDiv(divName)
	ss_HideDivOnSecondClick(divName)
}

//General routine to show a div given its name
function ss_HideDivIfActivated(divName) {
    if (ss_divBeingShown == divName) {
        ss_hideDiv(ss_divBeingShown)
        ss_divBeingShown = null;
        ss_lastDivBeingShown = null;
    }
}

//Routine to make div's be hidden on next click
function ss_HideDivOnSecondClick(divName) {
    divToBeHidden[divToBeHidden.length] = divName;
    ss_debug('divToBeHidden length = '+divToBeHidden.length)
}

//Routine to make div's be hidden on next click
function ss_NoHideDivOnNextClick(divName) {
    divToBeDelayHidden[divName] = divName;
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
function ss_showDiv(divName) {
	if (document.getElementById(divName) == null) return;
    document.getElementById(divName).style.visibility = "visible";
    if (!document.getElementById(divName).style.display || document.getElementById(divName).style.display != 'inline') {
    	document.getElementById(divName).style.display = "block";
    }

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
    divToBeDelayHidden[divName] = null
    ss_divBeingShown = null;
    
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
    divToBeDelayHidden[divName] = null
    ss_divBeingShown = null;
    
	//Signal that the layout changed
	if (!document.getElementById(divName) || 
	    	document.getElementById(divName).style.position != "absolute") {
		ssf_onLayoutChange();
		//ss_debug("ss_hideDiv: " + divName)
	}
}

function ss_toggleDivWipe(divName) {
	var divObj = self.document.getElementById(divName);
	if (divObj.style.display == "block" || divObj.style.display == "inline") {
		ss_hideDivWipe(divName);
	} else {
		ss_showDivWipe(divName);
	}
}
function ss_showDivWipe(divName) {
	var divObj = self.document.getElementById(divName);
	divObj.style.display = "block"
	divObj.style.visibility = "visible"
	//setTimeout("dojo.lfx.html.wipeIn('" + divName + "', 400).play();", 100);
}
function ss_hideDivWipe(divName) {
	var divObj = self.document.getElementById(divName);
	divObj.style.visibility = "hidden"
	divObj.style.display = "none"
	//setTimeout("dojo.lfx.html.wipeOut('" + divName + "', 400).play();", 100);
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

//Routines to get an object handle given the id name of a div
function getNN4DivObject(divName) {
    for (var n = 0; n < self.document.layers.length; n++) {
        var obj = getNN4DivObjectObj(self.document.layers[n], divName)
        if (obj != null) {return obj}
    }
    alert('getNN4DivObject error: unknown div id - '+divName)
    return self.document
}

function getNN4DivObjectObj(obj, divName) {
    if (obj.name == divName) {return obj}
    for (var n = 0; n < obj.document.layers.length; n++) {
        var obj1 = getNN4DivObjectObj(obj.document.layers[n], divName)
        if (obj1 != null) {return obj1}
    }
    return null
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
    if (divToBeHidden.length > 0) {
        for (var i = 0; i < divToBeHidden.length; i++) {
	        if (divToBeHidden[i] != '') {
	            if (divToBeDelayHidden[divToBeHidden[i]]) {
	                divToBeDelayHidden[divToBeHidden[i]] = null
	            } else {
	                ss_hideDiv(divToBeHidden[i])
	    			if (divToBeHidden[i] == ss_divBeingShown) ss_divBeingShown = null;
	                divToBeHidden[i] = '';
	            }
	        }
	    }
	    divToBeHidden = new Array();
    }
    if (isNSN6 || isMoz5) {
        ss_mousePosX = e.pageX
        ss_mousePosY = e.pageY
        ss_mouseX = e.layerX
        ss_mouseY = e.layerY
        return(true)
    } else if (isNSN) {
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
        //ss_mousePosX = event.x + self.document.body.scrollLeft
        //ss_mousePosY = event.y + self.document.body.scrollTop
        ss_mousePosX = event.clientX + self.document.body.scrollLeft;
        ss_mousePosY = event.clientY + self.document.body.scrollTop;
        ss_mouseX = event.clientX;
        ss_mouseY = event.clientY;
        var imgObj = window.event.srcElement
        if (imgObj.name != null && imgObj.name != "" && !isMacIE) {
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

//Routines to support getting stuff from the server without reloading the page
function ss_getXMLObj() {
	var req;
    // branch for native XMLHttpRequest object
    if (window.XMLHttpRequest) {
        req = new XMLHttpRequest();
    // branch for IE/Windows ActiveX version
    } else if (window.ActiveXObject) {
        req = new ActiveXObject("Microsoft.XMLHTTP");
    }
    return req;
}

function ss_fetch_url(url, callbackRoutine, callbackData) {
	if (url == undefined) {return}
	if (callbackData == undefined) callbackData = "";
	ss_fetch_url_debug("Request to fetch url: " + url)
	var x;
	x = ss_getXMLObj();
	x.open("GET", url, true);
	x.onreadystatechange = function() {
		if (x.readyState != 4) {
			return;
		}
		ss_fetch_url_debug("status: " + x.status + ", received " + x.responseText);
		ss_fetch_url_debug("callbackRoutine " + callbackRoutine);
        if (x.status == 200) {
        	callbackRoutine(x.responseText, callbackData)        	
        } else {
        	alert(x.status + "  \n" + x.statusText)
        	callbackRoutine(x.statusText)
        }
	}
	x.send(null);
	ss_fetch_url_debug(" waiting... url = " + url);
	delete x;
}                

function ss_fetch_url_debug(str) {
    //ss_debug(str);
}

//if (!Array.prototype.push) {
	function array_push() {
		for (var i = 0; i < arguments.length; i++) {
			this[this.length] = arguments[i];
		}
	}

	Array.prototype.push = array_push;
//}

//if (!Array.prototype.pop) {
	function array_pop() {
		if (this.length <= 0) {return ""}
		lastElement = this[this.length - 1];
		this.length = Math.max(this.length - 1, 0);

		if (lastElement == undefined) {lastElement = ""}
		return lastElement;
	}

	Array.prototype.pop = array_pop;
//}

//Routines to replace substrings in a string
function ss_replaceSubStr(str, subStr, newSubStrVal) {
    ss_debug("ss_replaceSubStr: " + str + ", " + subStr + " ==> " + newSubStrVal)
    var newStr = str;
	var i = str.indexOf(subStr);
    var lenS = str.length;
    var lenSS = subStr.length;
    if (i >= 0) {
        newStr = str.substring(0, i) + newSubStrVal + str.substring(i+lenSS, lenS);
    }
    ss_debug("   new str = " + newStr)
	return newStr;
}
function ss_replaceSubStrAll(str, subStr, newSubStrVal) {
    var newStr = str;
    while (1) {
        var i = newStr.indexOf(subStr);
        var lenS = newStr.length;
        var lenSS = subStr.length;
        if (i >= 0) {
            newStr = newStr.substring(0, i) + newSubStrVal + newStr.substring(i+lenSS, lenS);
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

//Routine to write text to the debug window
function ss_debug(text) {
	try {
		var temp = ss_debugTextareaId;
		if (temp == '') return;
	} catch(e) {return}
	var debugTextarea = document.getElementById(ss_debugTextareaId);
	if (debugTextarea) {
		var html = debugTextarea.value;
		if (html.length > 10000) html = html.substring(html.length - 6000, html.length);
		html +=  "\n";
		html += text;
		debugTextarea.value = html;
		debugTextarea.scrollTop = 1000;
	}
}
//common processing for callback after ajax call
function ss_postRequest(obj) {
	// alert('postRequest: ' + obj.getXMLHttpRequestObject().responseText);
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		if (obj.getData('timeout') != "timeout") {
			//This call wasn't made from a timeout. So, give error message
			ss_showNotLoggedInMsg();
		}
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
        bodyObj.appendChild(lightBox);
	}
    lightBox.style.visibility = "hidden";
    lightBox.className = className;
    lightBox.style.display = "block";
    lightBox.style.top = 0;
    lightBox.style.left = 0;
    dojo.html.setOpacity(lightBox, 0);
    lightBox.style.width = ss_getBodyWidth();
    lightBox.style.height = ss_getBodyHeight();
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

	run : function() {
		this.show();
		this.showHelpSpots()
	},
	
	show : function() {
		//ss_debug('ss_helpSystem');
		var lightBox = ss_showLightbox("ss_help_light_box", ssHelpZ)
	    lightBox.onclick = function(e) {if (ss_helpSystem) ss_helpSystem.hide();};
	    
		ss_moveDivToBody('ss_help_welcome');
		var welcomeDiv = document.getElementById('ss_help_welcome');
		if (welcomeDiv) {
	    	welcomeDiv.style.visibility = "visible";
	    	welcomeDiv.style.zIndex = ssHelpWelcomeZ;
	    	welcomeDiv.style.display = "block";
	        welcomeDiv.style.top = this.getPositionTop(welcomeDiv);
	        welcomeDiv.style.left = this.getPositionLeft(welcomeDiv);
	    	dojo.html.setOpacity(welcomeDiv, 0);
	    	dojo.lfx.html.fade(welcomeDiv, {start:0, end:1.0}, 150).play();
		}
	},
	
	hide : function() {
		var bodyObj = document.getElementsByTagName("body").item(0)
		var lightBox = document.getElementById('ss_help_light_box')
		if (!lightBox) return;
		var welcomeDiv = document.getElementById('ss_help_welcome');
		if (welcomeDiv) {
	    	welcomeDiv.style.visibility = "hidden";
	    	welcomeDiv.style.display = "none";
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
	        helpSpotNode.style.visibility = "visible";
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
		var tocDiv = document.getElementById('ss_help_toc');
		if (!tocDiv) return;
		var uls = tocDiv.getElementsByTagName("ul");
		var ulObj = null;
		if (uls == null || uls.length <= 0) {
			ulObj = document.createElement("ul");
			tocDiv.appendChild(ulObj);
		} else {
			ulObj = uls.item(0);
		}
		var liObj = document.createElement("li");
		var aObj = document.createElement("a");
		aObj.setAttribute("href", "javascript: ss_helpSystem.hideTOC();ss_helpSystem.showHelpSpotInfo('" + id + "', '" + xAlignment + "', '" + yAlignment + "');");
		aObj.appendChild(document.createTextNode(title));
		liObj.appendChild(aObj);
		ulObj.appendChild(liObj);
		ss_helpSystemTOC[ss_helpSystemTOC.length] = id;
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
			this.showHelpSpotInfo(ss_helpSystemTOC[ss_helpSystemTOCindex], xAlignment, yAlignment);
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
			this.showHelpSpotInfo(ss_helpSystemTOC[ss_helpSystemTOCindex], xAlignment, yAlignment);
		}
	},
	
	showHelpSpotInfo : function(id, xAlignment, yAlignment) {
		if (xAlignment == null) xAlignment = "";
		if (yAlignment == null) yAlignment = "";
		//ss_debug('showHelpSpotInfo id = '+id)
		this.hideTOC();
		for (var i = 0; i < ss_helpSystemTOC.length; i++) {
			if (id == ss_helpSystemTOC[i]) {
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
		}
	},
	
	showHelpPanel : function(id, panelId, x, y, xAlignment, yAlignment) {
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
	        pObj.className = "ss_helpPanel";
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
		var url = ss_helpSystemUrl;
		var orgHelpId = id;
		//See if this is the actual name of the help panel
		var i1 = id.indexOf("___");
		if (i1 >= 0) orgHelpId = id.substr(id.indexOf("___") + 3);
		url = ss_replaceSubStr(url, "ss_help_panel_id_place_holder",  orgHelpId);
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addKeyValue("operation2", id)
		ajaxRequest.addKeyValue("ss_help_panel_id", panelId)
		ajaxRequest.setData("id", id)
		ajaxRequest.setData("panelId", panelId)
		ajaxRequest.setData("x", x)
		ajaxRequest.setData("y", y)
		ajaxRequest.setData("xAlignment", xAlignment)
		ajaxRequest.setData("yAlignment", yAlignment)
		ajaxRequest.setData("startTop", startTop)
		ajaxRequest.setData("startLeft", startLeft)
		ajaxRequest.setData("startVisibility", startVisibility)
		//ajaxRequest.setEchoDebugInfo();
		ajaxRequest.setPostRequest(ss_helpSystem.postShowPanel);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
	},
	
	postShowPanel : function(obj) {
		//See if there was an error
		if (self.document.getElementById("ss_status_message").innerHTML == "error") {
			alert(ss_not_logged_in);
		}
		var panelId = obj.getData("panelId");
		var pObj = self.document.getElementById(panelId);
		pObj.setAttribute("helpId", obj.getData("id"));
		pObj.style.display = "block"
		var width = parseInt(dojo.html.getMarginBox(pObj).width);
		var height = parseInt(dojo.html.getMarginBox(pObj).height);
		var x = obj.getData("x");
		var y = obj.getData("y");
		var xAlignment = obj.getData("xAlignment");
		var yAlignment = obj.getData("yAlignment");
		var startTop = obj.getData("startTop");
		var startLeft = obj.getData("startLeft");
		var startVisibility = obj.getData("startVisibility");
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
		}
		switch(yAlignment) {
			case "bottom" : 
				top = parseInt(top - height - ss_helpSystemPanelMarginOffset);
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
			s += "<div id=\"ss_help_welcome\" class=\"ss_style ss_helpWelcome\" \n";
			s += "  positionX=\"center\" positionY=\"top\" align=\"center\">\n";
			s += "  <table width=\"400\">\n";
			s += "  <tr>\n";
			s += "  <td><a href=\"#\" onClick=\"ss_helpSystem.showPreviousHelpSpot();return false;\"\n";
			s += "    >&lt;&lt;&lt; "+ss_helpPreviousText+"</a></td>\n";
			s += "  <td><span class=\"ss_style ss_bold ss_largestprint\">"+ss_helpWelcomeText+"</span></td>\n";
			s += "  <td align=\"right\"><a href=\"#\" onClick=\"ss_helpSystem.showNextHelpSpot();return false;\"\n";
			s += "    >"+ss_helpNextText+" &gt;&gt;&gt;</a></td>\n";
			s += "  </tr>\n";
			s += "  <tr>\n";
			s += "    <td align=\"center\" colspan=\"3\"><span style=\"font-size:10px;\"  class=\"ss_titlebold\"><a href=\"#\" \n";
			s += "      onClick=\"ss_helpSystem.showHelpPanel('help_on_help','ss_help_on_help','right','bottom'); return false;\">"+ss_helpInstructions+"</a></span></td>\n";
			s += "  </tr>\n";
			s += "  <tr>\n";
			s += "  <td align=\"center\" colspan=\"3\"><a href=\"#\" \n";
			s += "    onClick=\"ss_helpSystem.toggleTOC();return false;\">"+ss_helpTocText+"</a></td>\n";
			s += "  </tr>\n"
			s += "  <tr>\n";
			s += "  <td align=\"center\" colspan=\"3\">\n";
			s += "    <a class=\"ss_linkButton ss_smallprint\" href=\"#\" \n";
			s += "      onClick=\"ss_helpSystem.showHelpPanel('print_manuals','ss_help_print_manuals','right','bottom'); return false;\">"+ss_helpManualsButtonText+"</a>\n";
			s += "    <a class=\"ss_linkButton ss_smallprint\" href=\"#\" \n";
			s += "      onClick=\"ss_helpSystem.hide(); return false;\">"+ss_helpCloseButtonText+"</a>\n";
			s += "  </td>\n";
			s += "  </tr>\n";
			s += "  </table>\n";
			s += "  <table>\n";
			s += "  <tr>\n";
			s += "  <td>&nbsp;</td>\n";
			s += "  <td align=\"center\"><div id=\"ss_help_toc\" class=\"ss_helpToc\" align=\"left\"></td>\n";
			s += "  <td>&nbsp;</td>\n";
			s += "  </tr>\n";
			s += "  </table>\n";
			s += "</div>\n";
			//alert(s)
			document.writeln(s);
		}
	}
}

//Dashboard routines

function ss_addDashboardComponents(divId) {
	var panel = document.getElementById(divId);
	ss_moveObjectToBody(panel);
	panel.style.zIndex = parseInt(ssLightboxZ + 1);
	ss_activateMenuLayer(divId, null, null, null, "popup")
}

function ss_showHideAllDashboardComponents(obj, divId, idStr) {
	var formObj = ss_getContainingForm(obj)
	var url = "";
	var canvas = document.getElementById(divId);
	if (canvas && canvas.style && canvas.style.visibility == 'visible') {
		url = ss_dashboardAjaxUrl + "\&operation=hide_all_dashboard_components\&" + idStr;
	    obj.innerHTML = "<span><img src='"+ss_imagesPath+"skins/"+ss_userSkin+"/iconset/showDashboard.gif' alt='"+ss_componentTextShow+"'></span>";
		canvas.style.visibility = 'hidden';
		canvas.style.display = 'none';
	} else if (canvas && canvas.style) { 
		url = ss_dashboardAjaxUrl + "\&operation=show_all_dashboard_components\&" + idStr;
	    obj.innerHTML = "<span><img src='"+ss_imagesPath+"skins/"+ss_userSkin+"/iconset/hideDashboard.gif' alt='"+ss_componentTextHide+"'></span>";
		canvas.style.visibility = 'visible';
		canvas.style.display = 'block';
	}
	
	ss_setupStatusMessageDiv()
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request

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
function ss_showHideDashboardComponent(obj, componentId, divId, idStr, namespace) {
	//ss_debug(obj.alt + ",    " + obj.src)
	var formObj = ss_getContainingForm(obj)
	var url = ss_dashboardAjaxUrl;
	if (componentId != "") {url += "\&operation2=" + componentId;}
	if (formObj._dashboardList && formObj._dashboardList.value != "") {
		url += "\&_dashboardList=" + formObj._dashboardList.value;
	}
	if (formObj._scope && formObj._scope.value != "") {
		url += "\&_scope=" + formObj._scope.value;
	}
	var callbackRoutine = ""
	var imgObj = obj.getElementsByTagName('img').item(0);
	if (imgObj.src.match(/show.gif/)) {
		url += "\&operation=show_component";
	    callbackRoutine = ss_showComponentCallback;
	    imgObj.src = ss_componentSrcHide;
	    imgObj.alt = ss_componentAltHide;
	} else if (imgObj.src.match(/hide.gif/)) {
		url += "\&operation=hide_component";
	    callbackRoutine = ss_hideComponentCallback;
	    imgObj.src = ss_componentSrcShow;
	    imgObj.alt = ss_componentAltShow;
		var targetDiv = document.getElementById(divId);
		if (targetDiv) {
			targetDiv.innerHTML = "";
			targetDiv.style.visibility = "hidden";
			targetDiv.style.display = "none";
			borderDiv = targetDiv.parentNode;
			if (borderDiv.className == 'ss_content_window_content') borderDiv.className = '';
			//Signal that the layout changed 
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	} else if (imgObj.src.match(/delete-component.gif/)) {
		url += "\&operation=delete_component";
	    callbackRoutine = ss_hideComponentCallback;
		var targetDiv = document.getElementById(divId);
		if (targetDiv) {
			targetDiv.innerHTML = "";
			targetDiv.style.visibility = "hidden";
			targetDiv.style.display = "none";
			//Signal that the layout changed
			if (ssf_onLayoutChange) ssf_onLayoutChange();
		}
	}
	url += "\&namespace="+namespace;
	url += "\&" + idStr;
	url += "\&rn=" + ss_dbrn++;
	if (callbackRoutine != "") ss_fetch_url(url, callbackRoutine, divId);
}
function ss_showComponentCallback(s, divId) {
	ss_debug(s)
	var targetDiv = document.getElementById(divId);
	if (targetDiv) {
		targetDiv.innerHTML = s;
		targetDiv.style.visibility = "visible";
		targetDiv.style.display = "block";
		//Signal that the layout changed
		if (ssf_onLayoutChange) ssf_onLayoutChange();
	}
}
function ss_hideComponentCallback(s, divId) {
}
function ss_confirmDeleteComponent(obj, componentId, divId, divId2, idStr, namespace) {
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
	ss_showHideDashboardComponent(obj, componentId, divId, idStr, namespace)
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

function ss_moreDashboardSearchResults(binderId, pageNumber, pageSize, divId, componentId, displayType) {
	var url = ss_dashboardAjaxUrl + "\&binderId="+binderId;
	url += "\&operation=search_more";
	url += "\&operation2="+componentId;
	url += "\&divId="+divId;
	url += "\&pageNumber="+pageNumber;
	url += "\&pageSize="+pageSize;
	url += "\&displayType="+displayType;
	url += "\&randomNumber="+ss_random++;
	ss_fetch_url(url, ss_moreDashboardSearchResultsCallback, divId);
}

function ss_moreDashboardSearchResultsCallback(s, divId) {
	var divObj = document.getElementById(divId);
	divObj.innerHTML = s;
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Start: Add Attachment Related Functions

//Browse Related Functions


var browseURL = "";
var browseHideAttachment = "";
var browseHideAttachmentAndAjax = "";


function setURLInIFrame(binderId, entryId, namespace) {
	var url = ss_baseAppletFileUploadURL;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);

	browseURL = url;
	browseHideAttachment = "ss_hideAddAttachmentBrowse('"+ entryId + "', '" + namespace + "')";
	browseHideAttachmentAndAjax = "ss_hideAddAttachmentBrowseAndAJAXCall('"+ binderId +"', '"+ entryId + "', '" + namespace + "')";
	//this.frames['ss_iframe_browse'+ entryId + namespace].setURL(url, ss_labelButtonOK, ss_labelButtonCancel, ss_labelEntryChooseFileWarning, "ss_hideAddAttachmentBrowse('"+ entryId + "', '" + namespace + "')", "ss_hideAddAttachmentBrowseAndAJAXCall('"+ binderId +"', '"+ entryId + "', '" + namespace + "')", ss_labelEntryBrowseAddAttachmentHelpText);
}

function ss_showAddAttachmentBrowse(binderId, entryId, namespace) {
	//alert("Inside ss_showAddAttachmentBrowse...");

	ss_hideAddAttachmentDropbox(entryId, namespace);
	
	setURLInIFrame(binderId, entryId, namespace);
	
	var divId = 'ss_div_browse' + entryId + namespace;
	var divObj = document.getElementById(divId);

	var frameId = 'ss_iframe_browse' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	//alert("ss_showAddAttachmentBrowse: frameObj.src: "+frameObj.src);
	
	frameObj.src = ss_htmlRootPath + "js/attachments/entry_attachment_browse.html";
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";
	
	divObj.style.width = "360px";
	divObj.style.height = "120px";
}

function ss_hideAddAttachmentBrowse(entryId, namespace) {
	var divId = 'ss_div_browse' + entryId + namespace;
	var divObj = document.getElementById(divId);
	divObj.style.display = "none";
	ss_hideDiv(divId);
}

function ss_hideAddAttachmentBrowseAndAJAXCall(binderId, entryId, namespace) {
	ss_hideAddAttachmentBrowse(entryId, namespace);
	ss_selectEntryAttachmentAjax(binderId, entryId, namespace);
}

function ss_selectEntryAttachmentAjax(binderId, entryId, namespace) {
	ss_setupStatusMessageDiv();
	
	var url = ss_baseAjaxRequest;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssOperationPlaceHolder", "reload_entry_attachments");
	url = ss_replaceSubStr(url, "ssNameSpacePlaceHolder", namespace);

	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object

	//These two values have been set to identify the entryid and namespace combination for which we need to throw the sucess/failure message
	ajaxRequest.setData("ss_IDF_entryId", entryId);
	ajaxRequest.setData("ss_IDF_namespace", namespace);

	ajaxRequest.setPostRequest(ss_postSelectEntryAttachment);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_postSelectEntryAttachment(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	
	//the objReturned is nothing but the ajaxRequest object
	//we are getting the entryId and namespace we set to idenity the correct
	//object to throw the correct success/failure message
	var entryId = obj.getData("ss_IDF_entryId");
	var namespace = obj.getData("ss_IDF_namespace");
	
	var divObj = document.getElementById('ss_divAttachmentList' + entryId + namespace);
	var s = divObj.innerHTML;
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

	var url = ss_baseAjaxRequest;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssOperationPlaceHolder", "add_attachment_options");
	url = ss_replaceSubStr(url, "ssNameSpacePlaceHolder", namespace);
	
	var divId = 'ss_div_dropbox' + entryId + namespace;
	var divObj = document.getElementById(divId);
	
	var frameId = 'ss_iframe_dropbox' + entryId + namespace;
	var frameObj = document.getElementById(frameId);
	
	if (frameObj.src == "") {
		frameObj.src = url;
	}
	
	ss_showDiv(divId);
	frameObj.style.visibility = "visible";

	divObj.style.width = "300px";
	divObj.style.height = "75px";

	if (parent.ss_positionEntryDiv) parent.ss_positionEntryDiv();
}

var editClicked = "false";
function ss_openWebDAVFile(binderId, entryId, namespace, OSInfo, strURLValue) {

	var url = ss_baseAjaxRequestWithOS;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", entryId);
	url = ss_replaceSubStr(url, "ssOperationPlaceHolder", "open_webdav_file");
	url = ss_replaceSubStr(url, "ssNameSpacePlaceHolder", namespace);
	url = ss_replaceSubStr(url, "ssOSPlaceHolder", OSInfo);
    url = url + "&ssEntryAttachmentURL="+strURLValue;

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

function ss_checkEditClicked(entryId, namespace) {
	return editClicked;
}

function ss_resetEditClicked(entryId, namespace) {
	editClicked = "false";
}

var ss_linkMenu_arr = new Array();

function checkAndCreateMenuObject(linkObj) {
	var checkObj = ss_linkMenu_arr[linkObj];
	
	if (checkObj) {
		//do nothing as object already exists
	} else {
		//create a new object
		ss_linkMenu_arr[linkObj] = new ss_linkMenuObj();
	}
}

//End: Add Attachment Related Functions


//Link Menu Related Functions
function ss_loadBinderFromMenu(obj, linkMenu, id, entityType) {
	//var linkMenuObj = eval(linkMenu+"");
	var linkMenuObj = ss_linkMenu_arr[linkMenu];
	
	if (linkMenuObj.showingMenu && linkMenuObj.showingMenu == 1) {
		//The user wants to see the drop down options, don't show the binder
		linkMenuObj.showingMenu = 0;
		return false;
	}
	self.location.href = obj.href;
}

function ss_loadEntryFromMenu(obj, linkMenu, id, binderId, entityType, entryCallBackRoutine, isDashboard) {
	//var linkMenuObj = eval(linkMenu+"");
	var linkMenuObj = ss_linkMenu_arr[linkMenu];
	
	if (ss_displayStyle == "accessible") {
		self.location.href = obj.href;
		return false;
	}
	if (linkMenuObj.showingMenu && linkMenuObj.showingMenu == 1) {
		//The user wants to see the drop down options, don't show the entry
		if (binderId != null && binderId != "") linkMenuObj.binderId = binderId;
		if (entityType != null && entityType != "") linkMenuObj.entityType = entityType;
		linkMenuObj.showingMenu = 0;
		return false;
	}
	
	linkMenuObj.showingMenu = 0;
	if (id == "") return false;
	var folderLine = 'folderLine_'+id;
	ss_currentEntryId = id;
	if (window.ss_highlightLineById) {
		ss_highlightLineById(folderLine);
		if (window.swapImages && window.restoreImages) {
			restoreImages(id);
		}
	}
	
	ss_showForumEntry(obj.href, eval(entryCallBackRoutine+""), isDashboard);
	
	return false;
}

function ss_loadPermaLinkFromMenu(linkMenu, binderId, entryId, entityType, namespace) {
	//var linkMenuObj = eval(linkMenu+"");
	var linkMenuObj = ss_linkMenu_arr[linkMenu];
	
	if (linkMenuObj.showingMenu && linkMenuObj.showingMenu == 1) {
		//The user wants to see the drop down options, don't show the binder
		linkMenuObj.showingMenu = 0;
		return false;
	}
	
	ss_gotoPermalink(binderId, entryId, entityType, namespace, "yes");
}

var menuLinkAdapterURL = "";

function setMenuGenericLinks(linkMenu, menuDivId, namespace, adapterURL, isDashboard) {

	if (adapterURL) menuLinkAdapterURL = adapterURL;
	//var linkMenuObj = eval(linkMenu+"");
	var linkMenuObj = ss_linkMenu_arr[linkMenu];
	
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

	linkMenuObj.menuDiv = menuDivId;
	linkMenuObj.binderUrl = binderUrl;
	linkMenuObj.entryUrl = entryUrl;
	linkMenuObj.menuLinkShowEntry = 'ss_folderMenuShowEntryLink_' + namespace;
	linkMenuObj.menuLinkShowFile = 'ss_folderMenuShowFileLink_' + namespace;
	linkMenuObj.menuLinkShowNewWindow = 'ss_folderMenuShowNewWindow_' + namespace;
	linkMenuObj.isDashboardLink = isDashboard;

}

//Routine to go to a permalink without actually using the permalink
function ss_gotoPermalink(binderId, entryId, entityType, namespace, useNewTab) {

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
	
	if (binderUrl == "" || entryUrl == "") return true;

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
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_folder_listing');	
	} else if (entityType == 'workspace') {
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_ws_listing');	
	} else if (entityType == 'profiles') {
		url = ss_replaceSubStr(binderUrl, "ssBinderIdPlaceHolder", binderId);
		url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_profile_listing');
	} 

	if (useNewTab && useNewTab == "yes") {
		url = ss_replaceSubStr(url, "ssNewTabPlaceHolder", "1");
	}
	
	self.location.href = url;
	return false;
}

function ss_saveDragId(id) {
    ss_lastDropped = id
    return false;
}

// Favorites Management

var ss_deletedFavorites = new Array();

function ss_saveFavorites(namespace) {
	var url = ss_saveFavoritesUrl;
	var saveArgs = new Array();
	saveArgs["deletedIds"] = ss_deletedFavorites.join(" ");
	saveArgs["favorites"] = ss_readFavoriteList(namespace);
	ss_showDiv("ss_favorites_loading" + namespace);
	var bindArgs = {
    	url: url,
		error: function(type, data, evt) {
			alert(ss_not_logged_in);
		},
		content: saveArgs,
		load: function(type, data, evt) {
  		  try {
			ss_setFavoritesList(data, namespace);
			ss_hideDiv("ss_favorites_loading" + namespace);
	      } catch (e) {alert(e);}
		},					
		mimetype: "text/json",
		method: "post"
	};   
	dojo.io.bind(bindArgs);
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
	var url = ss_treeShowIdUrl;
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	url = ss_replaceSubStr(url, "ssActionPlaceHolder", action);
	//console.log(url);
	self.location.href = url;
	return false;
}

function ss_addBinderToFavorites(namespace) {
	ss_loadFavorites(namespace, ss_addFavoriteBinderUrl);
}

function ss_showFavoritesPane(namespace) {
	ss_setupStatusMessageDiv()
	var fObj = self.document.getElementById("ss_favorites_pane" + namespace);
	ss_moveObjectToBody(fObj);
	fObj.style.zIndex = ssMenuZ;
	fObj.style.visibility = "visible";
	ss_setOpacity(fObj, 100)
	//fObj.style.display = "none";
	fObj.style.display = "block";
	var fObj2 = self.document.getElementById("ss_favorites_table" + namespace)
	var w = ss_getObjectWidth(fObj)
	ss_setObjectTop(fObj, parseInt(ss_getDivTop("ss_navbar_bottom" + namespace) + ss_favoritesPaneTopOffset))
	ss_setObjectLeft(fObj, parseInt(ss_getDivLeft("ss_navbar_favorites" + namespace)))
	var leftEnd = parseInt(ss_getDivLeft("ss_navbar_bottom" + namespace) + ss_favoritesPaneLeftOffset);
	ss_showDiv("ss_favorites_pane" + namespace);
	ss_hideObj("ss_favorites_form_div" + namespace);
	ss_loadFavorites(namespace, ss_getFavoritesTreeUrl);
}

function ss_loadFavorites(namespace, url) {
	ss_showDiv("ss_favorites_loading" + namespace);
	var postArgs = new Array();
	postArgs["IEcacheBuster"] = Math.random();
	var bindArgs = {
    	url: url,
		content: postArgs,
		error: function(type, data, evt) {
			alert(ss_not_logged_in);
		},
		load: function(type, data, evt) {
  		  try {
			ss_setFavoritesList(data, namespace);
			ss_hideDiv("ss_favorites_loading" + namespace);
	      } catch (e) {alert(e);}
		},
		preventCache: true,				
		mimetype: "text/json",
		method: "post"
	};   
	dojo.io.bind(bindArgs);
}

function ss_setFavoritesList(favList, namespace) {
	var d = dojo.byId("ss_favorites_list" + namespace);
	var t = '<ul style="margin: 0px 0px 0px 5px; list-style-type: none;">';
	for (var i = 0; i < favList.length; i++) {
		var f = favList[i];
		if (f.eletype != 'favorite') continue
		t += '<li id ="ss_favorite_' + f.id + '">';
		t += '<input type="checkbox" style="display: none;" />';
		t += '<a href="javascript:;" onClick="ss_treeShowId(' + "'" + f.value + "', this";
		if (typeof f.action == "undefined") {
			f.action = "view_ws_listing";
		}
		t += ", '" + f.action + "'";
		t += ');">' + f.name + '</a>';
		t += '</li>';
	}
	t += '</ul>';
	d.innerHTML = t;
}


function ss_setFavoriteListEditable(namespace, enable) {
	var container = dojo.byId("ss_favorites_list" + namespace);
	// Clear any prior activity
	while (ss_deletedFavorites.length) { ss_deletedFavorites.pop() };
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

function ss_getSelectedFavorites(namespace) {
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


function ss_deleteSelectedFavorites(namespace) {
    var toDelete = ss_getSelectedFavorites(namespace);
    dojo.lang.forEach(toDelete, ss_recordDeletedFavorite) 
    dojo.lang.forEach(toDelete, dojo.dom.removeNode)
}

function ss_recordDeletedFavorite(node) {
  	ss_deletedFavorites.push(node.id.substr(12));
}

function ss_moveSelectedFavorites(namespace, upDown) {
    var toMove = ss_getSelectedFavorites(namespace);
    if (upDown == 'up') {
	    dojo.lang.forEach(toMove, ss_moveElementUp);
	} else {
	    dojo.lang.forEach(toMove.reverse(), ss_moveElementDown);
	}
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

function ss_readFavoriteList(namespace) {
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


function ss_hideFavoritesPane(namespace) {
	ss_hideDivFadeOut('ss_favorites_pane'+namespace, 20);
}


//Routine to show/hide portal
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

function ss_setupPopupDiv(targetDiv) {
		targetDiv.style.display = "block";
		var x = parseInt(ss_getWindowWidth() / 2);
		var y = parseInt(ss_getWindowHeight() / 2);
	    var bodyX = self.document.body.scrollLeft
	    var bodyY = self.document.body.scrollTop
		x = parseInt(x + bodyX - ss_getObjectWidth(targetDiv) / 2)
		y = parseInt(y + bodyY - ss_getObjectHeight(targetDiv) / 2)
		targetDiv.style.left = x;
		targetDiv.style.top = y;
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

	var url = ss_saveDashboardLayoutUrl;
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements("ss_dashboard_layout_form");
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_postRequestAlertError(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
}

//Presence support
function ss_popupPresenceMenu(x, userId, userTitle, status, screenName, sweepTime, email, vcard, current, ssNamespace, ssPresenceZonBridge) {
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
	ss_presenceMenu('', x, userId, userTitle, status, screenName, sweepTime, email, vcard, current, ssNamespace, ssPresenceZonBridge);
}

function ss_presenceMenu(divId, x, userId, userTitle, status, screenName, sweepTime, email, vcard, current, ssNamespace, ssPresenceZonBridge) {
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
        m += '<table class="ss_style ss_graymenu ss_nowrap" border="0" cellspacing="0" cellpadding="3">';
	} else {
        m += '<table class="ss_nowrap ss_transparent" border="0" cellspacing="0" cellpadding="3">';
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
    m += '<td class="ss_bglightgray" valign=top><img border="0" src="" alt="" id=' +imgid +'></td>';
    m += '<td><span>' + userTitle;
    m += ostatus;
    if (status >= 0) {
        m += '</span><br><span class="ss_fineprint ss_gray">(' + ss_ostatus_at + ' ' + sweepTime + ')</span>';
    }
    m += '</td></tr>';
    if (screenName != '') {
        if (current == '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img border="0" alt="" src="" id="ppgimsg'+ssNamespace+'"></td>';
            if (status == 0) {
                m += '<td class="ss_fineprint ss_gray">'+ss_ostatus_sendIm+'</td>';
            } else {
                m += '<td><a class="ss_graymenu" href="iic:im?screenName=' + screenName + '">'+ss_ostatus_sendIm+'</a></td>';
            }
            m += '</tr>';
        }
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" src="" id="ppgimtg'+ssNamespace+'"></td>';
        m += '<td><a class="ss_graymenu" href="iic:meetone?screenName=' + screenName + '">'+ss_ostatus_startIm+'</a></td></tr>';
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" src="" id="ppgsched'+ssNamespace+'"></td>';
        m += '<td><a class="ss_graymenu" href="javascript:ss_startMeeting(\'' + ss_ostatus_schedule_meeting_url + '&users=' + userId + '\');">'+ss_ostatus_schedIm+'</a></td></tr>';
        m += '<tr>';
        if (ssPresenceZonBridge == 'enabled') {
        	m += '<td class="ss_bglightgray"><img border="0" alt="" src="" id="ppgphone'+ssNamespace+'"></td>';
        	m += '<td><a class="ss_graymenu" href="javascript:ss_startMeeting(\'' + ss_ostatus_schedule_meeting_url + '&users=' + userId + '\');">'+ss_ostatus_call+'</a></td></tr>';
        }
	}
	if (userId != '' && current == '') {
        if (email != '') {
            m += '<tr>';
            m += '<td class="ss_bglightgray"><img border="0" alt="" src="" id="ppgmail'+ssNamespace+'"></td>';
            bodyText = escape(window.location.href);
            m += '<td><a class="ss_graymenu" href="mailto:' + email + '?body=' + bodyText +'">'+ss_ostatus_sendMail+' (' + email + ')...</a></td></tr>';
        }
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" src="" id="ppgvcard'+ssNamespace+'"></td>';
        m += '<td><a class="ss_graymenu" href="' + vcard + '">'+ss_ostatus_outlook+'</a></td></tr>';	
    }

	if (userId != '') {
        m += '<tr>';
        m += '<td class="ss_bglightgray"><img border="0" alt="" src="" id="ppgclipboard'+ssNamespace+'"></td>';
        m += '<td id="addToClipboardTD' + screenName + '"><a class="ss_graymenu" href="javascript: // ;" onclick="ss_muster.addUsersToClipboard([' + userId + ']' + (divId != ''?', function () {$(\'addToClipboardTD'+screenName+'\').innerHTML=\'OK\'}':'') + ');return false;">'+ss_ostatus_clipboard+'</a></td></tr>';
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
    if (divId == '') {
	    // move the div up if it scrolls off the bottom
	    var mousePosX = parseInt(ss_getClickPositionX());
	    var mousePosY = parseInt(ss_getClickPositionY());
	    if (mousePosY != 0) {
	        var divHt = parseInt(ss_getDivHeight(objId));
	        var windowHt = parseInt(ss_getWindowHeight());
	        var scrollHt = self.document.body.scrollTop;
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

//Routines that support the link dropdown menu concept
var ss_linkMenu = new ss_linkMenuObj();

function ss_linkMenuObj() {
	this.menuDiv;	           //Div id of the menu div
	this.binderId;             //Binder id of the current folder
	this.definitionType;       //Definition type default of the current entry
	this.binderDefinitionType; //DeifinitionTypeType of current folder (9 = file folder)
	this.showingMenu;          //0 = not showing a menu; 1 = showing a menu
	this.linkObj;              //The link object that is active
	this.binderUrl;
	this.entryUrl;
	this.fileUrl;
	this.currentId;
	this.currentBinderId;
	this.currentDefinitionType;
	this.lastShownButton;       
	this.menuLinkShowEntry;
	this.menuLinkShowFile;
	this.menuLinkShowNewWindow;
	this.isDashboardLink;
	
	this.type_folderEntry = 'folderEntry';
	this.type_folder = 'folder';
	this.type_profileFolder = 'profiles';
	this.type_profileEntry = 'user';
	this.type_group = 'group';
	this.type_workspace = 'workspace';
	
	this.showButton = function(obj, imgid) {
		if (imgid != null && imgid != "") {
			var imgObj = document.getElementById(imgid);
			if (imgObj != null) {
				imgObj.src = ss_imagesPath + "pics/downarrow.gif";
				this.lastShownButton = obj;
				return;
			}
		} 
		if (this.lastShownButton && this.lastShownButton != obj) this.hideMenu(obj);
		obj.parentNode.getElementsByTagName("img").item(0).src = ss_imagesPath + "pics/downarrow.gif";
		this.lastShownButton = obj;
	}	
	
	this.showMenu = function(obj, id, binderId, definitionType) {
		ss_debug('show menu: id = ' + id + ', binderId = '+binderId + ', definition = '+definitionType)
		if (binderId != null) this.binderId = binderId;
		
		if (definitionType != null) this.definitionType = definitionType;
		
		
		//alert("id: "+ id + ", binderId: " + binderId);
		
		if (this.showingMenu) {
			ss_hideDiv(this.menuDiv)
			this.showingMenu = 0;
		}
		this.showingMenu = 1;
		this.currentId = id;
		this.currentBinderId = this.binderId;
		this.currentDefinitionType = this.definitionType;
		
		if (this.definitionType == this.type_folder || this.definitionType == this.type_workspace) {
			this.currentBinderId = id;
		}
		
		this.linkObj = obj;
		if (this.menuDiv != "") {
			var menuObj = document.getElementById(this.menuDiv);
			ss_moveObjectToBody(menuObj)
			
	    	var x = dojo.html.getAbsolutePosition(obj, true).x + 1;
	    	var y = dojo.html.getAbsolutePosition(obj, true).y	+ 16;
			
			menuObj.style.top = y + "px";
			menuObj.style.left = x + "px";
			menuObj.style.zIndex = ssMenuZ;
			
			if (this.menuLinkShowEntry != null) {
				var menuLinkObj = document.getElementById(this.menuLinkShowEntry);
				if (menuLinkObj != null) {
					menuLinkObj.style.display = 'none';
					if (this.currentDefinitionType == this.type_folderEntry || this.currentDefinitionType == this.type_profileFolder) 
						menuLinkObj.style.display = 'block';
				}
			}
			
			if (this.menuLinkShowFile != null) {
				var menuLinkObj = document.getElementById(this.menuLinkShowFile);
				if (menuLinkObj != null) {
					menuLinkObj.style.display = 'none';
					if (this.binderDefinitionType == this.type_folder) 
						menuLinkObj.style.display = 'block';
				}
			}

			if (this.menuLinkShowNewWindow != null) {
				var menuLinkObj = document.getElementById(this.menuLinkShowNewWindow);
				if (menuLinkObj != null) {
					menuLinkObj.style.display = 'none';
					if (this.currentDefinitionType == this.type_folderEntry) 
						menuLinkObj.style.display = 'block';
				}
			}
			
			ss_ShowHideDivXY(this.menuDiv, x, y)
			ss_HideDivOnSecondClick(this.menuDiv)
		}
	}

	this.hideButton = function(obj, imgid) {
		if (imgid != null && imgid != "") {
			var imgObj = document.getElementById(imgid);
			if (imgObj != null) {
				imgObj.src = ss_imagesPath + "pics/downarrow_off.gif";
				return;
			}
		} 
		obj.parentNode.getElementsByTagName("img").item(0).src = ss_imagesPath + "pics/downarrow_off.gif";
	}
	
	this.hideMenu = function(obj) {
		if (this.linkObj == obj) return;
		ss_hideDiv(this.menuDiv)
		this.showingMenu = 0;
		this.linkObj = null;
		this.currentId = "";
		this.currentBinderId = "";
		this.currentDefinitionType = "";
	}
	
	this.currentTab = function() {
		ss_debug('current tab: id = ' + this.currentId + ', binderId = '+this.currentBinderId + ', definition = '+this.currentDefinitionType)
		var url = this.buildBaseUrl();
		//url = ss_replaceSubStr(url, "ssNewTabPlaceHolder", "0");
		//Hemanth: New Code 3 has been introduced for opening in the current tab.
		//This is used for overcoming the search tab check
		url = ss_replaceSubStr(url, "ssNewTabPlaceHolder", "3");
		
		if (self.opener) {
			self.opener.location.href = url;
			self.opener.focus();
		} else if (self.parent) {
			self.parent.location.href = url;
			self.parent.focus();
		} else {
			self.location.href = url;
		}
	}
	
	this.newTab = function() {
		ss_debug('new tab: id = ' + this.currentId + ', binderId = '+this.currentBinderId + ', definition = '+this.currentDefinitionType)
		var url = this.buildBaseUrl();
		url = ss_replaceSubStr(url, "ssNewTabPlaceHolder", "1");
		if (self.opener) {
			self.opener.location.href = url;
			self.opener.focus();
		} else if (self.parent) {
			self.parent.location.href = url;
			self.parent.focus();
		} else {
			self.location.href = url;
		}
	}

	this.newWindow = function() {
		ss_debug('new window: id = ' + this.currentId + ', binderId = '+this.currentBinderId + ', definition = '+this.currentDefinitionType)
		ss_showForumEntryInPopupWindow(this.currentDefinitionType);
		return false;
	}
	
	this.newWindowOld = function() {
		ss_debug('new window: id = ' + this.currentId + ', binderId = '+this.currentBinderId + ', definition = '+this.currentDefinitionType)
		var url = this.buildBaseUrl();
		url = ss_replaceSubStr(url, "ssNewTabPlaceHolder", "0");
		self.window.open(url, '_blank');
		return false;
	}
	
	this.showEntry = function() {
		ss_loadEntry(this.lastShownButton, this.currentId, "", "", this.isDashboardLink)
		return false;
	}
	
	this.showFile = function() {
		var url = this.fileUrl;
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", this.currentBinderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", this.currentId);
		self.window.open(url, '_blank');
		return false;
	}
	
	this.buildBaseUrl = function() {
		var url;
		if (this.currentDefinitionType == this.type_folderEntry || this.currentDefinitionType == this.type_profileFolder) {
			url = this.entryUrl;
		} else if (this.currentDefinitionType == this.type_folder) {
			url = this.binderUrl;
		} else if (this.currentDefinitionType == this.type_workspace) {
			url = this.binderUrl;
		} else if (this.currentDefinitionType == this.type_profileEntry) {
			url = this.entryUrl;
		}
		url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", this.currentBinderId);
		url = ss_replaceSubStr(url, "ssEntryIdPlaceHolder", this.currentId);
		
		if (this.currentDefinitionType == this.type_profileEntry) {
			url = ss_replaceSubStr(url, "ssActionPlaceHolder", 'view_ws_listing');
		} else {
			url = ss_replaceSubStr(url, "ssActionPlaceHolder", ss_getActionFromDefinitionType(this.currentDefinitionType));
		}
		
		ss_debug('buildBaseUrl - '+ url);
		return url;
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
	var pattern = /\.([^/\.]*)$/
	var results = pattern.exec(fileName)
	if (!isIE && results != null) {
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

// Tabs
function ss_addTab(obj, type, binderId, entryId) {
	if (binderId == null) binderId = "";
	if (entryId == null) entryId = "";
	ss_setupStatusMessageDiv();
	var tabId = ss_nextTabNumber;
	ss_nextTabNumber++;
	//Create the data div
	var dataDivId = "ss_tabDataDiv" + tabId;
	ss_debug(dataDivId)
	dataDivObj = document.createElement("div");
	dataDivObj.id = dataDivId;
	var dataDiv0 = document.getElementById("ss_tabDataDiv0");
	dataDiv0.parentNode.insertBefore(dataDivObj, dataDiv0);
	
	var formObj = ss_getContainingForm(obj);
	var url = ss_addTabUrl;
	if (binderId != "") url = ss_replaceSubStr(url, "ss_binderid_place_holder",  binderId);
	if (entryId != "") url = ss_replaceSubStr(url, "ss_entryid_place_holder",  entryId);
	url = ss_replaceSubStr(url, "ss_tabid_place_holder",  tabId);
	url = ss_replaceSubStr(url, "ss_tab_type_place_holder",  type);
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(formObj.id);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_changeTabDone);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_deleteTab(obj, tabId) {
	ss_setupStatusMessageDiv();
	//Check if this is pointing to a tab
	var tabTdObject = obj.parentNode;
	if (tabTdObject.className && tabTdObject.className.indexOf("ss_tabs_td") >= 0) {
		//Get the outer td parent in the tab table
		var tabParentTdObject = tabTdObject.parentNode.parentNode.parentNode.parentNode;
		if (tabParentTdObject.tagName.toLowerCase() == "td") {
			//Make sure this isn't the last tab
			var tabs = tabParentTdObject.parentNode.getElementsByTagName("td");
			var tabCount = 0;
			for (var i = 0; i < tabs.length; i++) {
				if (tabs[i].parentNode == tabParentTdObject.parentNode) tabCount++;
			}
			if (tabCount > 1) {
				//Ok, delete this td from the tab table
				tabParentTdObject.parentNode.removeChild(tabParentTdObject)
				//Hide the tab contents
				var tabDataObj = document.getElementById("ss_tab_data_" + tabId);
				if (tabDataObj != null) tabDataObj.style.display = "none";
				
				//Now tell the server which tab got deleted
				var url = ss_deleteTabUrl;
				url = ss_replaceSubStr(url, "ss_tabid_place_holder",  tabId);
				var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
				ajaxRequest.setData("tabId", tabId)
				//ajaxRequest.setEchoDebugInfo();
				ajaxRequest.setPostRequest(ss_changeTabDone);
				ajaxRequest.setUsePOST();
				ajaxRequest.sendRequest();  //Send the request
			} else {
				if (ss_tabs_no_delete_last_tab) alert(ss_tabs_no_delete_last_tab);
			}
		}
	}
}

function ss_changeTabDone(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Search functions from the navbar
function ss_doSearch(obj, title) {
	ss_addTab(obj, "query");
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
         '<tbody><tr><td width="30px"><div class="ss_popup_topleft"></td><td width="100%"><div class="ss_popup_topright"><div id="ss_muster_close" class="ss_popup_close"></div><div id="ss_muster_title" class="ss_popup_title"></div></div>' +
         '</td></tr><tr><td colspan="2"><div id="ss_muster_inner" style="padding: 3px 10px;" class="ss_popup_body"></div></td></tr><tr><td width="30px"><div class="ss_popup_bottomleft"></div>' +
         '<td width="100%"><div class="ss_popup_bottomright"></div></tr></tbody></table>';

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
		// addContrBtnObj.onclick = ss_muster.addContributesToClipboard;
		dojo.event.connect(addContrBtnObj, "onclick", function(evt) {
			ss_muster.addContributesToClipboard();
			return false;
	    });

		var addTeamMembersBtnObj = document.createElement("input");
		addTeamMembersBtnObj.setAttribute("type", "button");
		addTeamMembersBtnObj.setAttribute("name", "add");
		addTeamMembersBtnObj.setAttribute("value", ss_addTeamMembersToClipboardText);
		// addTeamMembersBtnObj.onclick = ss_muster.addTeamMembersToClipboard;
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
		// deleteBtnObj.onclick = function () { ss_muster.removeFromClipboard('ss_muster_form'); };
		dojo.event.connect(deleteBtnObj, "onclick", function(evt) {
			ss_muster.removeFromClipboard('ss_muster_form');
			return false;
	    });

		deleteBtnObj.style.marginRight = "15px"

//		dojo.byId("ss_muster_close").onclick = ss_muster.cancel;
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
		var url = ss_musterUrl;
		var url = ss_replaceSubStr(url, "ss_operation_place_holder",  "get_clipboard_users");
		url += "\&randomNumber="+ss_random++;
		
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
		labelObj.setAttribute("for", "muster_ids_" + index);
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
		ss_setupStatusMessageDiv()
		var url = ss_musterUrl;
		url = ss_replaceSubStr(url, "ss_operation_place_holder",  "add_to_clipboard");
		url += "&add_team_members=true";
		url += "&binderId=" + sBinderId;
	
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object		
		//ajaxRequest.setEchoDebugInfo();
		if (afterPostRoutine)
			ajaxRequest.setPostRequest(afterPostRoutine);
		else
			ajaxRequest.setPostRequest(postAddToClipboard);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
	}
	
	/*
	 * afterPostRoutine - optional
	 */
	this.addUsersToClipboard = function (userIds, afterPostRoutine) {
		ss_setupStatusMessageDiv()
		var url = ss_musterUrl;
		url = ss_replaceSubStr(url, "ss_operation_place_holder",  "add_to_clipboard");
		
		
		url += "&muster_class=ss_muster_users";
		for (var i = 0; i < userIds.length; i++) {
			url += "&muster_ids=" + userIds[i];
		}

		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object		
		//ajaxRequest.setEchoDebugInfo();
		if (afterPostRoutine)
			ajaxRequest.setPostRequest(afterPostRoutine);
		else
			ajaxRequest.setPostRequest(postAddToClipboard);			
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
	}
	
	function postAddToClipboard (obj) {
		//See if there was an error
		if (self.document.getElementById("ss_status_message").innerHTML == "error") {
			alert(ss_not_logged_in);
		} else {
			loadUsers ($("ss_muster_list_container"));
		}
	}
	
	this.removeFromClipboard = function (formName) {
		ss_setupStatusMessageDiv();
		var url = ss_musterUrl;
		url = ss_replaceSubStr(url, "ss_operation_place_holder",  "remove_from_clipboard");
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		//ajaxRequest.setEchoDebugInfo();
		ajaxRequest.addFormElements(formName);
		ajaxRequest.setPostRequest(postRemoveFromClipboard);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
	}
	
	function postRemoveFromClipboard (obj) {
		//See if there was an error
		if (self.document.getElementById("ss_status_message").innerHTML == "error") {
			alert(ss_not_logged_in);
		}
		ss_cancelPopupDiv('ss_muster_div');
	}

}

var ss_muster = new ss_Clipboard();

/*
	Starts a Zon meeting with given id;
*/
function ss_launchMeeting(id) {
	self.location.href = 'iic:meetmany?meetingtoken=' + id;
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
			if (!data.meetingToken || data.meetingToken == "") {
				alert(ss_not_logged_in);
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
	var divObj = obj;
	if (typeof obj == "string")
		divObj = $(obj);
	if (!divObj) return;
			
	var imgObj = document.createElement("img");
	imgObj.setAttribute("src", "/ssf/images/pics/ajax-loader.gif");
	imgObj.setAttribute("border", "0");
	imgObj.setAttribute("style" , "vertical-align: middle; ");

	var wasAjaxLoaderThere = false;
	for (var i = divObj.childNodes.length; i > 0; --i) {
		if (divObj.childNodes[i - 1] && divObj.childNodes[i - 1].src && divObj.childNodes[i - 1].src.indexOf("ajax-loader.gif") > -1) {
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

function ss_checkForDuplicateFileAjax(obj) {
	ss_setupStatusMessageDiv();
 	var url = ss_findEntryForFileUrl; 
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setPostRequest(ss_postRequestAlertError);
	ajaxRequest.addKeyValue("file",obj.value);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.sendRequest();  //Send the request
}


//Routine to pop-up a "find user" window
var ss_launchFindUserWindowElement = null;
function ss_launchFindUserWindow(elementId) {
	ss_launchFindUserWindowElement = elementId;
	alert('Not implemented yet. Entry user id directly into text box on the left.')
}
