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
	var ss_active_menulayer_form = 0;
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

//Routine to open a url in the portlet. This routine determines if the current code is
//  running inside an iframe. It it is, then the url is opened in the parent of the iframe.
//This routine returns "true" without opening the url if the caller is not inside a frame.
//If the caller is in a frame (or iframe), then the routine opens the url in the parent and returns false.
function ss_openUrlInPortlet(url, popup) {
	//Is this a request to pop up?
	if (popup) {
		self.window.open(url, "_blank", "directories=no,location=no,menubar=yes,resizable=yes,scrollbars=yes,status=no,toolbar=no");
		return false;
	}
	//Are we at the top window?
	if (self.window != self.top) {
		parent.location.href = url;
		return false
	} else if (self.opener) {
		self.opener.location.href = url
		setTimeout('self.window.close();', 200)
		return false
	} else {
		return true
	}
}

function ss_reloadOpener(fallBackUrl) {
	//Are we at the top window?
	if (self.window != self.top) {
		if (parent.ss_reloadUrl && parent.ss_reloadUrl != "") {
			parent.location.replace(parent.ss_reloadUrl);
		}
	} else if (self.opener) {
		if (self.opener.ss_reloadUrl && self.opener.ss_reloadUrl != "") {
			self.opener.location.replace(self.opener.ss_reloadUrl);
			setTimeout('self.window.close();', 200)
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
	} else {
		ss_debug('Div "'+objName+'" does not exist. (ss_showHideObj)')
	}
	//Signal that the layout changed
	if (!obj.style.position || obj.style.position != "absolute") {
		ssf_onLayoutChange();
		ss_debug("ss_showHideObj: " + objName + " = " + visibility)
	}
}

//Routine to set the opacity of a div
//  (Note: this may not work if "width" is not explicitly set on the div)
function ss_setOpacity(obj, opacity) {
	dojo.style.setOpacity(obj, opacity);
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
    dojo.lfx.html.fadeOut(id, ms, null, function(){
    	ss_hideDiv(id);
    	return true;
    }).play();
}

//Routine to add the innerHMTL of one div to another div
function ss_addToDiv(target, source) {
    var objTarget
    var objSource
    if (isNSN || isNSN6 || isMoz5) {
        objTarget = self.document.getElementById(target)
        objSource = self.document.getElementById(source)
    } else {
        objTarget = self.document.all[target]
        objSource = self.document.all[source]
    }
    var targetHtml = ss_getDivHtml(target)
    var sourceHtml = ss_getDivHtml(source)
    ss_setDivHtml(target, targetHtml + sourceHtml)

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Routine to add html to a div
function ss_addHtmlToDiv(target, text) {
    var objTarget
    if (isNSN || isNSN6 || isMoz5) {
        objTarget = self.document.getElementById(target)
    } else {
        objTarget = self.document.all[target]
    }
    var targetHtml = ss_getDivHtml(target)
    ss_setDivHtml(target, targetHtml + text)

	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

//Routines to get and set the html of an area
function ss_getDivHtml(divId) {
    var obj
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(divId)
    } else {
        obj = self.document.all[divId]
    }
    var value = "";
    if (obj) {
    	value = obj.innerHTML;
    }
    return value;
}

function ss_setDivHtml(divId, value) {
    var obj
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(divId)
    } else {
        obj = self.document.all[divId]
    }
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
    for (var i = 0; i < ss_onSubmitList.length; i++) {
        if (ss_onSubmitList[i].formName == obj.name) {
            if (!ss_onSubmitList[i].submitRoutine()) {return false;}
        }
    }
    return true;
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
        	ss_debug("Resize event: " + ss_onResizeList[i].name + "\n");
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
        	ss_debug("ssf_onLayoutChange: " + ss_onLayoutChangeList[i].name)
        	ss_onLayoutChangeList[i].layoutRoutine();
        }
    }
    return true;
}

function getObjAbsX(obj) {
    var x = 0
    var parentObj = obj
    while (parentObj.offsetParent && parentObj.offsetParent != '') {
        x += parentObj.offsetParent.offsetLeft
        parentObj = parentObj.offsetParent
    }
    return x
}

function getObjAbsY(obj) {
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
    if (isNSN || isNSN6 || isMoz5) {
        var obj = self.document.getElementById(divName)
        while (1) {
            if (!obj) {break}
            top += parseInt(obj.offsetTop)
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
    } else {
        var obj = self.document.all[divName]
        while (1) {
            if (!obj) {break}
            top += obj.offsetTop
            if (obj == obj.offsetParent) {break}
            obj = obj.offsetParent
        }
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
	obj.style.width = width;

    //Call the routines that want to be called on layout changes
    if (!obj.style.position || obj.style.position != "absolute") ssf_onLayoutChange();
}

function ss_setObjectHeight(obj, height) {
    obj.style.height = height;
    
    //Call the routines that want to be called on layout changes
    if (!obj.style.position || obj.style.position != "absolute") ssf_onLayoutChange();
}

function ss_setObjectLeft(obj, value) {
    if (isNSN6 || isMoz5) {
        obj.style.left = value;
    } else if (isNSN) {
        obj.style.left = value;
    } else {
        obj.style.pixelLeft = value;
    }
    //Call the routines that want to be called on layout changes
    if (!obj.style.position || obj.style.position != "absolute") ssf_onLayoutChange();
}

function ss_setObjectTop(obj, value) {
    if (isNSN6 || isMoz5) {
        obj.style.top = value;
    } else if (isNSN) {
        obj.style.top = value;
    } else {
        obj.style.pixelTop = value;
    }
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
		divObj.parentNode.removeChild(divObj)
		document.getElementsByTagName( "body" ).item(0).appendChild( tempNode );
		divObj = document.getElementById(divId);
	}
	ss_activateMenuLayer(divId, parentDivId, offsetLeft, offsetTop, openStyle);
}
function ss_activateMenuLayer(divId, parentDivId, offsetLeft, offsetTop, openStyle) {
	if (!parentDivId || parentDivId == null || parentDivId == 'undefined') {parentDivId=""}
	if (!offsetLeft || offsetLeft == null || offsetLeft == 'undefined') {offsetLeft="0"}
	if (!offsetTop || offsetTop == null || offsetTop == 'undefined') {offsetTop=ss_activateMenuOffsetTop}
	if (!openStyle || openStyle == null || openStyle == 'undefined') {openStyle=""}

    // don't do anything if the divs aren't loaded yet
    if (self.document.getElementById(divId) == null) {return}

	var x = 0;
	var y = 0;
    if (parentDivId != "") {
    	x = ss_getDivLeft(parentDivId)
    	y = ss_getDivTop(parentDivId)
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
    ss_ShowHideDivXY(divId, x, y);
    if (openStyle != "popup") ss_HideDivOnSecondClick(divId);
}

// activate_menulayer tests this flag to make sure the page is
// loaded before the pulldown menus are clicked.
function ss_setLayerFlag() {
    ss_layerFlag = 1;
}

// Clears (hides) the active menulayer (if any)
function ss_clearActive_menulayer() {
    if (ss_active_menulayer_form) {return}
    ss_active_menulayer_form = 0;

    ss_lastActive_menulayer = ss_active_menulayer;
    if (ss_active_menulayer != '') {
        menulayerId = ss_active_menulayer;
        hideMenu(menulayerId);
        ss_active_menulayer = '';
    }     
    if (self.clearActiveMenu) {self.clearActiveMenu()}
}

//Enable the event handler
ss_createEventObj('ss_clearActive_menulayer', 'MOUSEUP')

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
        ss_positionDiv(ss_divBeingShown, x, y)
        ss_showDiv(ss_divBeingShown)
    }
}

//General routine to show a div given its name
function HideDivIfActivated(divName) {
    if (ss_divBeingShown == divName) {
        ss_hideDiv(ss_divBeingShown)
        ss_divBeingShown = null;
        ss_lastDivBeingShown = null;
    }
}

//Routine to make div's be hidden on next click
function ss_HideDivOnSecondClick(divName) {
    divToBeHidden[divName] = true;
}

//Routine to make div's be hidden on next click
function ss_NoHideDivOnNextClick(divName) {
    divToBeDelayHidden[divName] = true;
}

function ss_showDiv(divName) {
    //Hide any area that has elements that might bleed through
    ss_hideSpannedAreas()
     
    document.getElementById(divName).style.visibility = "visible";
    if (!document.getElementById(divName).style.display || document.getElementById(divName).style.display != 'inline') {
    	document.getElementById(divName).style.display = "block";
    }

	//Signal that the layout changed
	if (!document.getElementById(divName) || 
	    	document.getElementById(divName).style.position != "absolute") {
		ssf_onLayoutChange();
		ss_debug("ss_showDiv: " + divName)
	}
}

function ss_hideDiv(divName) {
    document.getElementById(divName).style.visibility = "hidden";
    divToBeDelayHidden[ss_divBeingShown] = null
    ss_divBeingShown = null;
    
    //Show any spanned areas that may have been turned off
    ss_showSpannedAreas()

	//Signal that the layout changed
	if (!document.getElementById(divName) || 
	    	document.getElementById(divName).style.position != "absolute") {
		ssf_onLayoutChange();
		ss_debug("ss_hideDiv: " + divName)
	}
}

function ss_positionDiv(divName, x, y) {
    if (isNSN6 || isMoz5) {
    	if (self.document.getElementById(divName) && self.document.getElementById(divName).offsetParent) {
	        self.document.getElementById(divName).style.left= (x - parseInt(self.document.getElementById(divName).offsetParent.offsetLeft)) + "px"
	        self.document.getElementById(divName).style.top= (y - parseInt(self.document.getElementById(divName).offsetParent.offsetTop)) + "px"
	    } else {
	        self.document.getElementById(divName).style.left= x + "px"
	        self.document.getElementById(divName).style.top= y + "px"
	    }
    } else if (isNSN) {
        var nn4obj = getNN4DivObject(divName)
        nn4obj.left=x
        nn4obj.top=y
    } else {
        if (self.document.all[divName] && self.document.all[divName].offsetParent) {
	        self.document.all[divName].style.left=x - self.document.all[divName].offsetParent.offsetLeft
	        self.document.all[divName].style.top=y - self.document.all[divName].offsetParent.offsetTop
    	} else {
	        self.document.all[divName].style.left=x
	        self.document.all[divName].style.top=y
    	}
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
    if (ss_divBeingShown != null) {
        if (divToBeHidden[ss_divBeingShown]) {
            if (divToBeDelayHidden[ss_divBeingShown]) {
                divToBeDelayHidden[ss_divBeingShown] = null
            } else {
                ss_hideDiv(ss_divBeingShown)
                ss_divBeingShown = null;
            }
        }
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
    if (isNSN6 || isMoz5) {
        if (document.getElementById(spanName) != null) {
            document.getElementById(spanName).style.visibility = newValue;
        }    
    } else {  
        if (self.document.layers && document.layers[spanName] != null) {
            self.document.layers[spanName].visibility = newValue;
        } else if (self.document.all && document.all[spanName] != null) {
            self.document.all[spanName].style.visibility = newValue;
        }
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
function getXMLObj() {
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

function fetch_url(url, callbackRoutine, callbackData) {
	if (url == undefined) {return}
	if (callbackData == undefined) callbackData = "";
	fetch_url_debug("Request to fetch url: " + url)
	var x;
	x = getXMLObj();
	x.open("GET", url, true);
	x.onreadystatechange = function() {
		if (x.readyState != 4) {
			return;
		}
		fetch_url_debug("status: " + x.status + ", received " + x.responseText);
		fetch_url_debug("callbackRoutine " + callbackRoutine);
        if (x.status == 200) {
        	callbackRoutine(x.responseText, callbackData)        	
        } else {
        	alert(x.status)
        	alert(x.statusText)
        	callbackRoutine(x.statusText)
        }
	}
	x.send(null);
	fetch_url_debug(" waiting... url = " + url);
	delete x;
}                

function fetch_url_debug(str) {
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
    var newStr = str;
	var i = str.indexOf(subStr);
    var lenS = str.length;
    var lenSS = subStr.length;
    if (i >= 0) {
        newStr = str.substring(0, i) + newSubStrVal + str.substring(i+lenSS, lenS);
    }
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
	var debugTextarea = document.getElementById('debugTextarea');
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
			if (self.ss_showNotLoggedInMsg) self.ss_showNotLoggedInMsg();
		}
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
		ss_debug('ss_helpSystem');
		var lightBox = document.getElementById('ss_help_light_box')
		if (!lightBox) {
			var bodyObj = document.getElementsByTagName("body").item(0)
			lightBox = document.createElement("div");
	        lightBox.setAttribute("id", "ss_help_light_box");
	        lightBox.className = "ss_helpLightBox";
	        bodyObj.appendChild(lightBox);
		}
	    ss_debug(lightBox.id)
	    lightBox.style.visibility = "hidden";
	    lightBox.style.display = "none";
	    lightBox.onclick = function(e) {if (ss_helpSystem) ss_helpSystem.hide();};
	    lightBox.style.top = 0;
	    lightBox.style.left = 0;
	    lightBox.style.width = ss_getBodyWidth();
	    lightBox.style.height = ss_getBodyHeight();
	    dojo.style.setOpacity(lightBox, 0);
	    lightBox.className = "ss_helpLightBox";
	    lightBox.style.visibility = "visible";
	    lightBox.style.display = "block";
	    dojo.fx.html.fade(lightBox, 150, 0, .5)
	    
		ss_moveDivToBody('ss_help_welcome');
		var welcomeDiv = document.getElementById('ss_help_welcome');
		if (welcomeDiv) {
	    	welcomeDiv.style.visibility = "visible";
	    	welcomeDiv.style.display = "block";
	    	welcomeDiv.style.zIndex = 2001;
	        welcomeDiv.style.top = this.getPositionTop(welcomeDiv);
	        welcomeDiv.style.left = this.getPositionLeft(welcomeDiv);
	    	dojo.style.setOpacity(welcomeDiv, 0);
	    	dojo.fx.html.fade(welcomeDiv, 150, 0, 1.0)
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
		ss_debug(lightBox.style.visibility)
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
				ss_debug("panelObj = " + ss_helpSystemPanels[i])
				var pObj = document.getElementById(ss_helpSystemPanels[i]);
				if (pObj != null && pObj.parentNode != null) pObj.parentNode.removeChild(pObj);
			}
    		//Delete all of the highlighted nodes
    		this.clearHighlights();

    		dojo.fx.html.fade(lightBox, 150, .5, 0, function() {
    			var lightBox2 = document.getElementById('ss_help_light_box');
		    	lightBox.style.visibility = "hidden";
		    	lightBox.style.display = "none";
		    	ss_debug('lightBox hidden\n');
    		})
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
			ss_debug(nodes[i].getAttribute("helpId") + " = " + ss_helpSystemNextNodeId)
			var helpSpotNodeId = nodes[i].getAttribute("helpId");
			var helpSpotTitle = "";
			if (nodes[i].getAttribute("title")) {
				helpSpotTitle = nodes[i].getAttribute("title");
			}
			if (helpSpotTitle != "") this.addTOC(helpSpotNodeId, helpSpotTitle);
			var helpSpotNode = document.createElement("div");
	        helpSpotNode.className = "ss_helpSpot";
	        helpSpotNode.style.display = "block";
			
			var helpSpotA = document.createElement("a");
			var helpSpotTable = document.createElement("table");
			var helpSpotTbody = document.createElement("tbody");
			var helpSpotTr = document.createElement("tr");
			var helpSpotTd1 = document.createElement("td");
			var helpSpotTd2 = document.createElement("td");
			helpSpotTd2.setAttribute("nowrap", "nowrap");
			helpSpotTd2.className = "ss_helpSpotTitle";
			var helpSpotGif = document.createElement("img");
			helpSpotGif.src = ss_helpSpotGifSrc;
			var helpSpotTitle = "";
			if (nodes[i].getAttribute("title")) {
				helpSpotTitle = nodes[i].getAttribute("title");
			}
			
			//Build a table containing the image and the title
			helpSpotNode.appendChild(helpSpotA);
			helpSpotA.appendChild(helpSpotTable);
			helpSpotTable.appendChild(helpSpotTbody);
			helpSpotTbody.appendChild(helpSpotTr);
			helpSpotTr.appendChild(helpSpotTd1);
			helpSpotTr.appendChild(helpSpotTd2);
			helpSpotTd1.appendChild(helpSpotGif);
			var aObj = document.createElement("a");
			aObj.setAttribute("href", "javascript: ss_helpSystem.showHelpSpotInfo('" + helpSpotNodeId + "');");
			aObj.appendChild(document.createTextNode(helpSpotTitle));
			helpSpotTd2.appendChild(aObj);
			
			ss_helpSystemNodes[ss_helpSystemNextNodeId] = helpSpotNode;
	        helpSpotNode.setAttribute("id", "ss_help_spot" + ss_helpSystemNextNodeId);
	        helpSpotNode.setAttribute("helpId", helpSpotNodeId);
	        
	        var offsetY = nodes[i].getAttribute("offsetY");
	        if (!offsetY) offsetY = 0;
	        offsetY = parseInt(offsetY);
	        var top = parseInt(dojo.style.getAbsolutePosition(nodes[i], true).y + offsetY);
	        if (nodes[i].getAttribute("align")) {
	        	if (nodes[i].getAttribute("valign") == "middle") {
	        		top += parseInt(ss_getObjectHeight(nodes[i]) / 2);
	        	} else if (nodes[i].getAttribute("valign") == "bottom") {
	        		top += ss_getObjectHeight(nodes[i]);
	        	}
	        }
	        var offsetX = nodes[i].getAttribute("offsetX");
	        if (!offsetX) offsetX = 0;
	        offsetX = parseInt(offsetX);
	        var left = parseInt(dojo.style.getAbsolutePosition(nodes[i], true).x + offsetX);
	        if (nodes[i].getAttribute("align")) {
	        	if (nodes[i].getAttribute("align") == "center") {
	        		left += parseInt(dojo.style.getMarginBoxWidth(nodes[i]) / 2);
	        	} else if (nodes[i].getAttribute("align") == "right") {
	        		left += dojo.style.getMarginBoxWidth(nodes[i]);
	        	}
	        }
	        helpSpotNode.style.top = top + "px";
	        helpSpotNode.style.left = left + "px";
	        bodyObj.appendChild(helpSpotNode);
			ss_helpSystemNextNodeId++;
	        helpSpotNode.style.visibility = "visible";
			ss_debug("nodes[i] width = "+dojo.style.getMarginBoxWidth(nodes[i]))
		}
	},
	
	getPositionLeft : function(obj) {
		var x = 0;
		switch(obj.getAttribute("positionX")) {
			case "left" : 
				x = ss_help_position_leftOffset
				break
			case "center" :
				x = parseInt((ss_getWindowWidth() - dojo.style.getMarginBoxWidth(obj)) / 2)
				if (x < 0) x = 0;
				break
			case "right" :
				x = parseInt(ss_getWindowWidth() - dojo.style.getMarginBoxWidth(obj) - ss_help_position_rightOffset)
				if (x < 0) x = 0;
			 	break
			default :
				x = parseInt((ss_getWindowWidth() - dojo.style.getMarginBoxWidth(obj)) / 2)
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
	
	addTOC : function(id, title) {
		ss_debug("addToc " + id + ", " + title)
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
		aObj.setAttribute("href", "javascript: ss_helpSystem.hideTOC();ss_helpSystem.showHelpSpotInfo('" + id + "');");
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
    	tocDiv.style.zIndex = 2001;
	},
	
	hideTOC : function() {
		var tocDiv = document.getElementById('ss_help_toc');
		if (!tocDiv) return;
    	tocDiv.style.visibility = "hidden";
    	tocDiv.style.display = "none";
	},
	
	showNextHelpSpot : function(id) {
		ss_helpSystemTOCindex++;
		if (ss_helpSystemTOCindex < 0) ss_helpSystemTOCindex = 0;
		if (ss_helpSystemTOCindex >= ss_helpSystemTOC.length) ss_helpSystemTOCindex = 0;
		if (ss_helpSystemTOCindex < ss_helpSystemTOC.length) {
			this.hideTOC();
			this.showHelpSpotInfo(ss_helpSystemTOC[ss_helpSystemTOCindex]);
		}
	},
	
	showPreviousHelpSpot : function(id) {
		ss_helpSystemTOCindex--;
		if (ss_helpSystemTOCindex < 0) ss_helpSystemTOCindex = ss_helpSystemTOC.length-1;
		if (ss_helpSystemTOCindex >= ss_helpSystemTOC.length) ss_helpSystemTOCindex = 0;
		if (ss_helpSystemTOCindex >= 0 && ss_helpSystemTOCindex < ss_helpSystemTOC.length) {
			this.hideTOC();
			this.showHelpSpotInfo(ss_helpSystemTOC[ss_helpSystemTOCindex]);
		}
	},
	
	showHelpSpotInfo : function(id) {
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
			if (ss_helpSystemNodes[i] != null && ss_helpSystemNodes[i].getAttribute("helpId") == id) {
				helpSpot = ss_helpSystemNodes[i];
				break;
			}
		}
		ss_debug("showHelpSpotInfo helpSpot: " + helpSpot)
		if (helpSpot != null) {
		    var top = parseInt(dojo.style.getAbsolutePosition(helpSpot, true).y);
		    var left = parseInt(dojo.style.getAbsolutePosition(helpSpot, true).x);
		    var width = parseInt(dojo.style.getContentBoxWidth(helpSpot));
		    var height = parseInt(dojo.style.getContentBoxHeight(helpSpot));
			this.showHelpPanel(id, "ss_help_panel", parseInt(left + width/2), parseInt(top + height + 5))
		}
	},
	
	showHelpPanel : function(id, panelId, x, y) {
		if (ss_helpSystemRequestInProgress == 1) {
			ss_helpSystemQueuedId = id;
			ss_helpSystemQueuedPanelId = panelId;
			ss_helpSystemQueuedX = x;
			ss_helpSystemQueuedY = y;
			return;
		}
		ss_setupStatusMessageDiv()
		this.clearHighlights();
		ss_debug("showHelpPanel " + id)
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
	        bodyObj.appendChild(pObj);
	        ss_helpSystemPanels[ss_helpSystemPanels.length] = panelId;
		} else {
			//See if this is a request for the same panel. If so, toggle it off.
			ss_debug("id = " + pObj.getAttribute("id") + ", helpId = " + pObj.getAttribute("helpId"))
			if (pObj.getAttribute("helpId") == id && pObj.style.visibility == "visible") {
				//On the second click to the same help spot, turn the panel off
				pObj.style.visibility = "hidden"
				pObj.style.display = "none"
				return
			}
			startTop = parseInt(dojo.style.getAbsolutePosition(pObj, true).y);
			startLeft = parseInt(dojo.style.getAbsolutePosition(pObj, true).x);
			if (pObj.style && pObj.style.visibility) 
					startVisibility = pObj.style.visibility;
		}
		var url = ss_helpSystemUrl;
		url = ss_replaceSubStr(url, "ss_help_panel_id_place_holder",  id);
		var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addKeyValue("operation2", id)
		ajaxRequest.addKeyValue("ss_help_panel_id", panelId)
		ajaxRequest.setData("id", id)
		ajaxRequest.setData("panelId", panelId)
		ajaxRequest.setData("x", x)
		ajaxRequest.setData("y", y)
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
		var width = parseInt(dojo.style.getMarginBoxWidth(pObj));
		var height = parseInt(dojo.style.getMarginBoxHeight(pObj));
		var x = obj.getData("x");
		var y = obj.getData("y");
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
			ss_debug("top = " + top);
			ss_debug("left = " + left);
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
		var windowWidth = parseInt(ss_getWindowWidth());
		if (parseInt(left + width) > windowWidth - ss_help_position_rightOffset) {
			left = parseInt(windowWidth - width - ss_help_position_rightOffset);
		}
		if (left < ss_help_position_leftOffset) left = ss_help_position_leftOffset;
		
		ss_debug("["+startLeft+", "+startTop+"], ["+left+", "+top+"]")
		if (startTop >= 0 && startLeft >= 0 && startVisibility == "visible") {
			dojo.fx.html.slide(panelId, 300, [startLeft, startTop], [left, top]);
		} else {
			pObj.style.top = top + "px";
			pObj.style.left = left + "px";
		}
		pObj.style.visibility = "visible";
		
		ss_helpSystemRequestInProgress = 0;
		
		//Is there another request queued?
		if (ss_helpSystemQueuedId != "") {
			ss_debug("\nLaunching queued request to show " + ss_helpSystemQueuedId)
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
		ss_debug("Highlight " + id)
		var obj = document.getElementById(id);
		if (obj != null) {
			ss_helpSystemHighlights[ss_helpSystemHighlights.length] = id;
			ss_helpSystemHighlightsBorder[id] = obj.style.border;
			obj.style.border = "red 3px solid";
			if (ss_helpSystemHighlightsBorderTimer[id]) 
			    clearTimeout(ss_helpSystemHighlightsBorderTimer[id]);
			ss_helpSystemHighlightsBorderTimer[id] = setTimeout("ss_helpSystem.blinkHighlight('"+id+"', 4)", 200);
		}
	},
	
	clearHighlights : function() {
		for (var i = ss_helpSystemHighlights.length; --i >= 0;) {
			ss_debug("highlightObj = " + ss_helpSystemHighlights[i])
			var id = ss_helpSystemHighlights[i];
			var hObj = document.getElementById(id);
			hObj.style.border = ss_helpSystemHighlightsBorder[id];
			ss_helpSystemHighlights[i] = null;
			if (ss_helpSystemHighlightsBorderTimer[id]) {
				clearTimeout(ss_helpSystemHighlightsBorderTimer[id])
				ss_helpSystemHighlightsBorderTimer[id] = null;
			}
		}
		ss_helpSystemHighlights = new Array();
	},
	
	blinkHighlight : function(id, count) {
		ss_debug("blinkHighlight " + id)
		if (ss_helpSystemHighlightsBorderTimer[id] != null) {
			ss_debug("clearTimeout " + id)
			clearTimeout(ss_helpSystemHighlightsBorderTimer[id])
			ss_helpSystemHighlightsBorderTimer[id] = null;
		} else {
			//There is no timer value. The user must have moved on. Don't blink any more.
			ss_debug("Stopped blinking!")
			return;
		}
		var obj = document.getElementById(id);
		ss_debug("  border color: " + obj.style.borderTopColor)
		if (obj.style.borderTopColor == "red") {
			obj.style.border = "white 3px solid";
		} else {
			obj.style.border = "red 3px solid";
		}
		if (count-- >= 0) {
			ss_helpSystemHighlightsBorderTimer[id] = setTimeout("ss_helpSystem.blinkHighlight('"+id+"', "+count+")", 200);
		}
	}
	
}

