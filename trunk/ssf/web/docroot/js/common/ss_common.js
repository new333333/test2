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

var jsDebug = 0;

var isNSN = (navigator.appName == "Netscape");
var isNSN4 = isNSN && ((navigator.userAgent.indexOf("Mozilla/4") > -1));
var isNSN6 = ((navigator.userAgent.indexOf("Netscape6") > -1));
var isMoz5 = ((navigator.userAgent.indexOf("Mozilla/5") > -1) && !isNSN6);
var isMacIE = ((navigator.userAgent.indexOf("IE ") > -1) && (navigator.userAgent.indexOf("Mac") > -1));
var isIE = ((navigator.userAgent.indexOf("IE ") > -1));

var ss_savedOnResizeRoutine = null;
var ss_onResizeRoutineLoaded;

//SiteScape's array function (use this instead of "new Array()" to avoid problems with extending the js Array)
function ssArray() {}
function ssArrayLength(obj) {
	var length = 0
	for (var i in obj) length++;
	return length;
}

//Routine called by the body's onLoad event
var ss_savedOnLoadRoutine = null;
var ss_onLoadRoutineLoaded;
function ss_onLoadInit() {
    //Call any routines that want to be called at onLoad time
    for (var name in ss_onLoadList) {
        if (ss_onLoadList[name].initRoutine) {
        	ss_onLoadList[name].initRoutine();
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
		if (jsDebug) {alert('Div "'+objName+'" does not exist. (ss_showHideObj)')}
	}
	//Signal that the layout changed
	if (ssf_onLayoutChange) ssf_onLayoutChange();
}

// Function by Simon Willison from sitepoint.com
function ss_setOpacity(obj, opacity) {
  opacity = (opacity == 100)?99.999:opacity;

  // IE/Win
  obj.style.filter = "alpha(opacity:"+opacity+")";

  // Safari<1.2, Konqueror
  obj.style.KHTMLOpacity = opacity/100;

  // Older Mozilla and Firefox
  obj.style.MozOpacity = opacity/100;

  // Safari 1.2, newer Firefox and Mozilla, CSS3
  obj.style.opacity = opacity/100;
}

//Routine to fade in a div
function ss_showDivFadeIn(id, ms) {
    var now = new Date();
    var endTime = parseInt(now.getTime() + ms);
    ss_setOpacity(document.getElementById(id),0);
    ss_showDivFader(id, 0, endTime, 20)
}

function ss_showDivFader(id, opacity, endTime, count) {
    count--
    var now = new Date();
    var incTime = parseInt(endTime - now.getTime());
    if (count <= 0 || incTime <= 0) {
        ss_setOpacity(document.getElementById(id),100);
    } else {
        var incOpacity = parseInt((100 - opacity) / count)
        if (incOpacity < 5) incOpacity = 5;
        opacity = parseInt(opacity + incOpacity)
        if (opacity > 100) opacity = 100;
        ss_setOpacity(document.getElementById(id), opacity);
        var sleepTime = parseInt(incTime/count);
        setTimeout("ss_showDivFader('"+id+"', "+opacity+", "+endTime+", "+count+");", sleepTime);
    }
}

//Routine to fade out a div
function ss_hideDivFadeOut(id, ms) {
    var now = new Date();
    var endTime = parseInt(now.getTime() + parseInt(ms));
    ss_hideDivFader(id, 100, endTime, 20)
}

function ss_hideDivFader(id, opacity, endTime, count) {
    count--
    var now = new Date();
    var incTime = parseInt(endTime - now.getTime());
    if (count <= 0 || incTime <= 0) {
        ss_setOpacity(document.getElementById(id), 0);
        ss_hideObj(id);
    } else {
        var incOpacity = parseInt(opacity / count)
        if (incOpacity < 5) incOpacity = 5;
        opacity = parseInt(opacity - incOpacity)
        if (opacity < 0) opacity = 0;
        ss_setOpacity(document.getElementById(id), opacity);
        var sleepTime = parseInt(incTime/count);
        setTimeout("ss_hideDivFader('"+id+"', "+opacity+", "+endTime+", "+count+");", sleepTime);
    }
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
var ss_declaredDivs = new ssArray();
function ss_setDeclaredDiv(id) {
	ss_declaredDivs[id] = id;
}

function ss_hideAllDeclaredDivs() {
	for (var i in ss_declaredDivs) {
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

var ss_eventList = new ssArray();
var ss_eventTypeList = new ssArray();
//Routine to create a new "ss_eventObj" object
//ss_eventObj objects are set up whenever you want to call a routine on an event.
//   event_name is the event name (e.g., "MOUSEDOWN")
function ss_createEventObj(function_name, event_name, function_def) {
    if (ss_eventList[function_name] == null) {
        ss_eventList[function_name] = new ss_eventObj(function_name);
        ss_eventList[function_name].setEventName(event_name);
        ss_eventList[function_name].setFunctionDef(function_def);
    }
    if (ss_eventTypeList[event_name] == null) {
        ss_eventTypeList[event_name] = event_name
        //Enable the event
        if (isNSN) {
            eval("self.document.captureEvents(Event."+event_name+")")
        }
        if (ss_eventList[function_name].eventName.toLowerCase() == "unload") {
        	//Add the unload event to the body object
        	eval("self.document.body.on"+ss_eventList[function_name].eventName.toLowerCase()+" = ssf_event_handler;")
        } else {
        	eval("self.document.on"+ss_eventList[function_name].eventName.toLowerCase()+" = ssf_event_handler;")
        }
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
    for (var n in ss_eventList) {
        if (e.type.toLowerCase() == ss_eventList[n].eventName) {
        	if (ss_eventList[n].functionDef != null && ss_eventList[n].functionDef != "undefined") {
        		ss_eventList[n].functionDef(e);
        	} else {
            	eval(ss_eventList[n].functionName+'(e)');
            }
        }
    }
}


var ss_onLoadList = new ssArray();
//Routine to create a new "onLoadObj" object
//onLoadObj objects are set up whenever you want to call something at onLoad time.
function ss_createOnLoadObj(name, initName) {
    if (ss_onLoadList[name] == null) {
        ss_onLoadList[name] = new onLoadObj(name);
        ss_onLoadList[name].setInitRoutine(initName);
    }
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

var ss_onSubmitList = new ssArray();
//Routine to create a new "onSubmitObj" object
//onSubmitObj objects are set up whenever you want to call something at form submit time.
function ss_createOnSubmitObj(name, formName, submitRoutine) {
    if (ss_onSubmitList[name] == null) {
        ss_onSubmitList[name] = new onSubmitObj(name, formName);
        ss_onSubmitList[name].setSubmitRoutine(submitRoutine);
    }
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
    for (var i in ss_onSubmitList) {
        if (ss_onSubmitList[i].formName == obj.name) {
            if (!ss_onSubmitList[i].submitRoutine()) {return false;}
        }
    }
    return true;
}


var ss_onResizeList = new ssArray();
//Routine to create a new "onResizeObj" object
//onResizeObj objects are set up whenever you want to call something at onResize time.
function ss_createOnResizeObj(name, resizeName) {
    if (ss_onResizeList[name] == null) {
        ss_onResizeList[name] = new onResizeObj(name);
        ss_onResizeList[name].setResizeRoutine(resizeName);
    }
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
    for (var name in ss_onResizeList) {
        if (ss_onResizeList[name].resizeRoutine) {
        	ss_onResizeList[name].resizeRoutine();
        }
    }
    if (ss_savedOnResizeRoutine != null) {
    	window.onresize = ss_savedOnResizeRoutine;
    	if (window.onresize != null) window.onresize();
		window.onresize = ss_onResize;
    }
}

var ss_onLayoutChangeList = new ssArray();
//Routine to create a new "onLayoutChangeObj" object
//onLayoutChangeObj objects are set up whenever you want to be called if the layout changes dynamically.
function ss_createOnLayoutChangeObj(name, layoutRoutine) {
    if (ss_onLayoutChangeList[name] == null) {
        ss_onLayoutChangeList[name] = new onLayoutChangeObj(name);
        ss_onLayoutChangeList[name].setLayoutRoutine(layoutRoutine);
    }
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
    for (var i in ss_onLayoutChangeList) {
        ss_onLayoutChangeList[i].layoutRoutine()
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
    return parseInt(obj.scrollTop);
}

function ss_getDivScrollLeft(divName) {
    var obj = self.document.getElementById(divName)
    return parseInt(obj.scrollLeft);
}

function ss_getDivHeight(divName) {
    if (isNSN || isNSN6 || isMoz5) {
        var obj = self.document.getElementById(divName)
    } else {
        var obj = self.document.all[divName]
    }
    return parseInt(obj.offsetHeight);
}

function ss_getDivWidth(divName) {
    if (isNSN || isNSN6 || isMoz5) {
        var obj = self.document.getElementById(divName)
    } else {
        var obj = self.document.all[divName]
    }
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
    ssf_onLayoutChange();
	return

    if (isNSN6 || isMoz5) {
        obj.offsetWidth = width;
    } else if (isNSN) {
        obj.clip.width = width;
    } else {
        obj.clientWidth = width;
    }
}

function ss_setObjectHeight(obj, height) {
    obj.style.height = height;
    
    //Call the routines that want to be called on layout changes
    ssf_onLayoutChange();
    return
    
    if (isNSN6 || isMoz5) {
        obj.offsetHeight = height;
    } else if (isNSN) {
        obj.clip.height = height;
    } else {
        obj.clientHeight = height;
    }
    //Call the routines that want to be called on layout changes
    ssf_onLayoutChange();
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
    ssf_onLayoutChange();
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
    ssf_onLayoutChange();
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
var ss_active_menulayer = '';
var ss_lastActive_menulayer = '';
var ss_active_menulayer_form = 0;
var ss_activateMenuOffsetTop = 6;
var ss_menuDivClones = new Array();

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
    ss_HideDivOnSecondClick(divId);
}

// activate_menulayer tests this flag to make sure the page is
// loaded before the pulldown menus are clicked.
var layerFlag = 0;
function setLayerFlag() {
    layerFlag = 1;
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

ss_createOnLoadObj('layerFlag', setLayerFlag);



//Support for positioning divs at x,y 
var ss_mouseX = 0;
var ss_mouseY = 0;
var ss_mousePosX = 0;
var ss_mousePosY = 0;
var divBeingShown = null;
var lastDivBeingShown = null;
var divToBeHidden = new Array;
var divToBeDelayHidden = new Array;

//Enable the event handler
ss_createEventObj('captureXY', 'MOUSEUP')

//General routine to show a div given its name and coordinates
function ss_ShowHideDivXY(divName, x, y) {
    if (divBeingShown == divName) {
        ss_hideDiv(divBeingShown)
        divBeingShown = null;
        lastDivBeingShown = null;
    } else {
        if (lastDivBeingShown == divName) {
            lastDivBeingShown = null;
            return
        }
        lastDivBeingShown = null;
        if (divBeingShown != null) {
            ss_hideDiv(divBeingShown)
        }
        divBeingShown = divName;
        lastDivBeingShown = divName;
        ss_positionDiv(divBeingShown, x, y)
        ss_showDiv(divBeingShown)
    }
}

//General routine to show a div given its name
function HideDivIfActivated(divName) {
    if (divBeingShown == divName) {
        ss_hideDiv(divBeingShown)
        divBeingShown = null;
        lastDivBeingShown = null;
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
}

function ss_hideDiv(divName) {
    document.getElementById(divName).style.visibility = "hidden";
    divToBeDelayHidden[divBeingShown] = null
    divBeingShown = null;
    
    //Show any spanned areas that may have been turned off
    ss_showSpannedAreas()
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
    lastDivBeingShown = divBeingShown;
    if (divBeingShown != null) {
        if (divToBeHidden[divBeingShown]) {
            if (divToBeDelayHidden[divBeingShown]) {
                divToBeDelayHidden[divBeingShown] = null
            } else {
                ss_hideDiv(divBeingShown)
                divBeingShown = null;
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

var ss_onErrorList = new ssArray();
//Routine to create a new "onErrorObj" object
//onErrorObj objects are set up whenever you want to call something at onError time.
function ss_createOnErrorObj(name, onErrorName) {
    if (ss_onErrorList[name] == null) {
        ss_onErrorList[name] = new onErrorObj(name);
        ss_onErrorList[name].setOnErrorRoutine(onErrorName);
        window.onerror = ssf__onError_event_handler
    }
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
    for (var n in ss_onErrorList) {
        if (ss_onErrorList[n].onErrorRoutine()) {ret = true}
    }
    return ret
}

var ss_spannedAreasList = new ssArray();
//Routine to create a new "spannedArea" object
//spannedAreaObj objects are set up whenever you need some form elements to be 
//   blanked when showing the menus
function ss_createSpannedAreaObj(name) {
    if (ss_spannedAreasList[name] == null) {
        ss_spannedAreasList[name] = new spannedAreaObj(name);
    }
    return ss_spannedAreasList[name];
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
    for (var name in ss_spannedAreasList) {
        ss_spannedAreasList[name].hide()
    }
}

function ss_showSpannedAreas() {
    //Show any form elements that should be returned to the visible state
    for (var name in ss_spannedAreasList) {
        ss_spannedAreasList[name].show()
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
var ss_forum_maxBodyWindowHeight = 0;
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

function fetch_url(url, callbackRoutine) {
	if (url == undefined) {return}
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
        	callbackRoutine(x.responseText)        	
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
    //alert(str);
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

