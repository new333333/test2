/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */

//These routines are a copy of routines in ss_common
//We use ss_mobile to minimize the size of the common javascript routines loaded

//Routine to create a new "onLoadObj" object
//onLoadObj objects are set up whenever you want to call something at onLoad time.
var ss_onLoadList = new Array();
var ss_onLoadRoutineLoaded;
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
}

//Add the onLoadInit routine to the onload event
if (!ss_onLoadRoutineLoaded) {
	ss_onLoadRoutineLoaded = 1;
	ss_savedOnLoadRoutine = window.onload;
	window.onload = ss_onLoadInit;
}

//Mailto: replacement routines
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

function ss_replaceSubStrAll(str, subStr, newSubStrVal) {
    if (typeof str == 'undefined') return str;
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

function ss_findOwningElement(obj, eleName) {
	var node = obj;
	while (node != null && node.tagName.toLowerCase() != eleName.toLowerCase()) {
		node = node.parentNode;
		if (node == null || node.tagName == null) node = null;
		if (node == null || node.tagName == null || node.tagName.toLowerCase() == 'body') break;
	}
	return node;
}


//Micro Bolg routines
function ss_clearStatusMobile(textareaId) {
	var obj = document.getElementById(textareaId);
	if (obj && typeof obj.value != "undefined") {
		obj.value = "";
		try {obj.focus();} catch(e){}
	}
}

var ss_activeMenu = null;
function ss_showMenu(divId) {
	if (ss_activeMenu != null) {
		var divObj = self.document.getElementById(ss_activeMenu);
		divObj.style.visibility = "hidden";
		divObj.style.display = "none";
		ss_activeMenu = null;
	}
	var divObj = self.document.getElementById(divId);
	divObj.style.display = "block";
	divObj.style.visibility = "visible";
	ss_activeMenu = divId;
}

function ss_hideMenu(divId) {
	if (ss_activeMenu != null) {
		var divObj = self.document.getElementById(ss_activeMenu);
		divObj.style.visibility = "hidden";
		divObj.style.display = "none";
		ss_activeMenu = null;
	}
	var divObj = self.document.getElementById(divId);
	if (divObj.style.visibility != "hidden") divObj.style.visibility = "hidden";
	if (divObj.style.display != "none") divObj.style.display = "none";
	ss_activeMenu = null;
}

function ss_toggleDivVisibility(id) {
	var divObj = self.document.getElementById(id);
	if (divObj.style.display != "none") {
		divObj.style.display = "none";
		divObj.style.visibility = "hidden";
	} else {
		divObj.style.display = "block";
		divObj.style.visibility = "visible";
	}
}

//Routine to select an element
function ss_selectElement(id) {
	var eleObj = self.document.getElementById(id);
	eleObj.select();
}

//Routine to clear the value of a hidden field in a form and blank the owning div
function ss_delete_hidden_field(obj, formName, elementName, value) {
	var formObj = self.document.getElementById(formName);
	if (formObj != null) {
		var inputs = document.getElementsByTagName("input");
		for (var i=0; i < inputs.length; i++) {
			if (inputs[i].getAttribute('name') == elementName && 
					inputs[i].getAttribute('value') == value) {
				inputs[i].setAttribute('value', '');
			}
		}
	}
	var divObj = ss_findOwningElement(obj, "div");
	if (divObj != null) {
		divObj.style.display = "none";
	}
}

function ss_setUGT(formName, elementName, type) {
	var formObj = self.document.getElementById(formName);
	var found = false;
	if (formObj != null) {
		var inputs = document.getElementsByTagName("input");
		for (var i=0; i < inputs.length; i++) {
			if (inputs[i].getAttribute('name') == "entryUGT") {
				inputs[i].setAttribute('value', type + "," + elementName);
				found = true;
				break;
			}
		}
		if (!found) {
			var ele = document.createElement("input");
			ele.setAttribute("name", "entryUGT");
			ele.setAttribute("value", type + "," + elementName);
			formObj.appendChild(ele);
		}
	}
}

//checkboxes on designer forms need to be present even if they are unchecked.
//Use hidden field.
function ss_saveCheckBoxValue(box, hiddenFieldId) {
	var hiddenField = document.getElementById(hiddenFieldId);
	var cbChecked = box.checked;
	if (cbChecked) hiddenField.value="true";
	else hiddenField.value="false";

	// Did the user just check the notify assignee/attendee checkbox on
	// a task/calendar entry?
	if (cbChecked &&
			((hiddenFieldId == "hidden_attendee_notify") ||		// Calendar
																// entry.
			 (hiddenFieldId == "hidden_assignment_notify"))) {	// Task entry.
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
