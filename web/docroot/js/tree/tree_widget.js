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
//Routines to display an expandable/contractable tree
//
var ss_treeIds;
if (ss_treeIds == null) ss_treeIds = new Array();
function ss_treeToggle(treeName, id, parentId, bottom, type, page, indentKey) {
	alert('zzz')
	ss_hideBucketText()
	if (page == null) page = "";
	if (ss_treeDisplayStyle && ss_treeDisplayStyle == 'accessible') {
		return ss_treeToggleAccessible(treeName, id, parentId, bottom, type, page, indentKey);
	}
	ss_setupStatusMessageDiv()
    var tObj = self.document.getElementById(treeName + "div" + id);
    var jObj = self.document.getElementById(treeName + "join" + id);
    var iObj = self.document.getElementById(treeName + "icon" + id);
    eval("var showTreeIdRoutine = ss_treeShowIdRoutine_"+treeName+";");
    if (tObj == null) {
        //See if the tree is in the process of being loaded
        if (ss_treeIds[treeName + "div" + id] != null) return;
        ss_treeIds[treeName + "div" + id] = "1";
        //The div hasn't been loaded yet. Go get the div via ajax
		eval("var url = ss_treeAjaxUrl_" + treeName);
		url = ss_replaceSubStrAll(url, "&amp;", "&");
		var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
		ajaxRequest.addKeyValue("binderId", id)
		ajaxRequest.addKeyValue("treeName", treeName)
		ajaxRequest.addKeyValue("page", page)
		ajaxRequest.addKeyValue("indentKey", indentKey)
		ajaxRequest.addKeyValue("showIdRoutine", showTreeIdRoutine)
 		eval("var treeKey = ss_treeKey_"+treeName);
		if (treeKey != null)
			ajaxRequest.addKeyValue("treeKey", treeKey);
	    eval("var seObj = ss_treeSelected_"+treeName); 	    
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
    eval("var showTreeIdRoutine = ss_treeShowIdRoutine_"+treeName+";");
    if (iframeDivObjParent == null && iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
	    iframeDivObjParent = iframeDivObj;
        iframeDivObj.setAttribute("id", "ss_treeIframeDiv");
		iframeDivObj.className = "ss_treeIframeDiv";
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_treeIframe");
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
    }
    if (iframeDivObj == null) iframeDivObj = iframeDivObjParent;
    if (iframeObj == null) iframeObj = iframeObjParent;
    if (self.parent == self) {
    	var x = dojo.html.getAbsolutePosition(tempObj, true).x
    	var y = dojo.html.getAbsolutePosition(tempObj, true).y
	    ss_setObjectTop(iframeDivObj, y + "px");
	    ss_setObjectLeft(iframeDivObj, x + "px");
	}
	ss_showDiv("ss_treeIframeDiv");
	eval("var url = ss_treeAjaxUrl_" + treeName);
	url = ss_replaceSubStrAll(url, "&amp;", "&");
	url += "&binderId=" + id;
	url += "&treeName=" + treeName;
	if (showTreeIdRoutine != '') url += "&showIdRoutine=" + showTreeIdRoutine;
	url += "&parentId=" + parentId;
	url += "&bottom=" + bottom;
	url += "&type=" + type;
	url += "&page=" + page;
	url += "&indentKey=" + indentKey;
	eval("var seObj = ss_treeSelected_"+treeName); 	    
	//add single select id
	if (seObj != null) {
	   	url += "&select=" + seObj;
	}
	eval("var treeKey = ss_treeKey_"+treeName);
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
		if (ssf_onLayoutChange) ssf_onLayoutChange();
		
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

function ss_clearSingleSelect(treeName) {
	eval("ss_treeSelected_" + treeName + "=null;");
	
	var inputHiddenObj = document.getElementById(treeName + "_lastChoice");
	if (inputHiddenObj) {
		inputHiddenObj.parentNode.removeChild(inputHiddenObj);
	}
	
	return true;
}

function ss_clearMultiSelect(id) {
	var inputHiddenObj = document.getElementById(id + "_lastChoice");
	if (inputHiddenObj) {
		inputHiddenObj.parentNode.removeChild(inputHiddenObj);
	}
}

function ss_saveTreeId(obj) {
	var formObj = null;
	if (obj.type == 'radio') {
		//Strip off the leading "ss_tree_radio"
		var prefix = obj.id.substr(13)
		prefix = ss_replaceSubStr(prefix, obj.name + obj.value, "");
		for (var i = 0; i < parent.document.forms.length; i++) {
			if (parent.document.forms[i].name && parent.document.forms[i].name.indexOf(prefix) >= 0) {
				formObj = parent.document.forms[i];
				break;
			}
		}
		if (formObj != null && typeof formObj.idChoices !== "undefined") {
			formObj.idChoices.value = obj.name + "_" + obj.value;
		}
	} else {
		//Strip off the leading "ss_tree_checkbox"
		var prefix = obj.id.substr(16)
		prefix = ss_replaceSubStr(prefix, obj.name, "");
		for (var i = 0; i < parent.document.forms.length; i++) {
			if (parent.document.forms[i].name && parent.document.forms[i].name.indexOf(prefix) >= 0) {
				formObj = parent.document.forms[i];
				break;
			}
		}
		if (formObj != null && typeof formObj.idChoices !== "undefined") {
			if (typeof formObj.idChoices.value === "undefined")  formObj.idChoices.value = "";
			formObj.idChoices.value = ss_replaceSubStrAll(formObj.idChoices.value, " " + obj.name, "");
			if (obj.checked) formObj.idChoices.value += " " + obj.name;
		}
	}
}
