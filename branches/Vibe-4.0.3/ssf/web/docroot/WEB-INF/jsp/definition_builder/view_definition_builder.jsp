<%
/**
 * Copyright (c) 1998-2014 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2014 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2014 Novell, Inc. All Rights Reserved.
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
%>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("toolbar.menu.definition_builder") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="org.kablink.teaming.domain.DefinitionInvalidOperation" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ page import="org.kablink.teaming.domain.IdentityInfo" %>

<jsp:useBean id="definitionTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="data" type="java.util.Map" scope="request" />
<%@ page import="org.kablink.teaming.domain.FolderEntry" %>
<%@ page import="org.kablink.teaming.domain.User" %>
<%@ page import="org.kablink.teaming.domain.Folder" %>
<%@ page import="org.kablink.teaming.domain.Workspace" %>
<%
	boolean isIE = org.kablink.util.BrowserSniffer.is_ie(request);

	String nodeOpen = " ";
	if (data.containsKey("nodeOpen")) {
		 nodeOpen = (String) data.get("nodeOpen");
	}
	if (nodeOpen.equals("")) {nodeOpen = " ";}
%>
<script type="text/javascript">
	ss_loadJsFile(ss_rootPath, "js/jsp/tag_jsps/find/find.js");
	dojo.require("dojo.date");
	dojo.require("dojo.date.locale");
	dojo.require("dojo.date.stamp");
	dojo.require("dijit.form.DateTextBox");
	dojo.require("dijit.form.TimeTextBox");
</script> 
<script type="text/javascript">

var rn = Math.round(Math.random()*999999)
var selectedId = null;
var selectedIdMapped = null;
var lastSelectedId = null;
var operationSelection = null;
var operationSelectedItem = "";
var operationSelectedItemName = "";
var operationReferenceItem="";

var selectedIdText = null;
var selectedCaptionText = null;

var sourceDefinitionId = '${data.selectedItem}';
var binderId = '${ssBinderId}'
function initializeStateMachine() {
	//ss_hideAllDeclaredDivs()
	ss_setDivHtml("displaydiv", "")
	showDisplayDiv()
	ss_addToDiv("displaydiv", "info_select")
	var divObj = document.getElementById('displayDiv')
	if (divObj != null) divObj.className = "ss_definitionBuilder";
	hideDisplayButtons()
	showDisplayDiv()
	ss_showHideObj('definitionbuilder_tree_loading', 'hidden', 'none')
	ss_showHideObj('definitionbuilder_tree', 'visible', 'block')
	if (sourceDefinitionId != '') {
		operationSelection = "view_definition_options";
		operationSelectedItem = sourceDefinitionId;		
		selectedId = sourceDefinitionId;
		setStateMachine("view_definition_options")
	}
}

function loadDiv(option, itemId, itemName, refItemId) {
	//alert("Load div: " + option + ", " + itemId + ", " + itemName + ", " + refItemId)
	ss_loadNextDiv(option, itemId, itemName, refItemId)
	return

}

function hideDisplayDiv() {
	var displaydivObj = document.getElementById('displaydiv');
	var displaydiv0Obj = document.getElementById('displaydiv0');
	displaydiv0Obj.style.visibility = "hidden";
	displaydiv0Obj.style.display = "none";
}

function showDisplayButtons() {
	var displaydivButtonsObj = document.getElementById('displaydivButtons');
	displaydivButtonsObj.style.visibility = "visible";
	displaydivButtonsObj.style.display = "block";
	displaydivButtonsObj.focus();
}

function hideDisplayButtons() {
	var displaydivButtonsObj = document.getElementById('displaydivButtons');
	displaydivButtonsObj.style.visibility = "hidden";
	displaydivButtonsObj.style.display = "none";
}

function showDisplayDiv() {
	var displaydivObj = document.getElementById('displaydiv');
	var displaydivboxObj = document.getElementById('displaydivbox');
	var displaydiv0Obj = document.getElementById('displaydiv0');
	var displaydivButtonsObj = document.getElementById('displaydivButtons');
    var spacerObj = self.document.getElementById('displaydiv_spacer')
	displaydivObj.style.visibility = "visible";
	displaydivObj.style.display = "block";
	displaydiv0Obj.style.visibility = "visible";
	displaydiv0Obj.style.display = "block";
	displaydiv0Obj.focus();

	//alert(displaydivObj.innerHTML) 
	//alert("displaydiv: " + parseInt(ss_getDivHeight('displaydiv')) + ", displaydivbox: " + parseInt(ss_getDivHeight('displaydivbox')))

    //ss_setObjectHeight(displaydiv0Obj, parseInt(ss_getObjectHeight(displaydivObj)));
    //ss_setObjectHeight(displaydiv0Obj, parseInt(parseInt(ss_getObjectHeight(displaydivboxObj)) + parseInt(ss_getObjectHeight(displaydivButtonsObj))));

    //Position the div being displayed so it is in view
    var spacerTop = parseInt(ss_getDivTop('displaydiv_spacer'));
    var spacerBottom = parseInt(ss_getDivTop('displaydiv_spacer_bottom') + 50);
    var divHeight = 0;
    if (spacerTop < parseInt(ss_getScrollXY()[1]) + ss_scrollTopOffset) {
    	divHeight = parseInt(ss_getScrollXY()[1] + ss_scrollTopOffset - spacerTop);
   	}
    ss_setObjectHeight(spacerObj, divHeight);
}

var ss_scrollTopOffset = 15;



function definitionTree_showId(id, obj) {
	//User selected an item from the tree
	//See if this id has any info associated with it
	var mappedId = id;
	//alert('definitionTree_showId: ' + id + '--> '+mappedId+', state: '+state+ ', sourceDefinitionId: '+sourceDefinitionId)
	lastSelectedId = selectedId;
	selectedId = id;
	selectedIdMapped = mappedId;
	if (!idMapCaption[id]) {idMapCaption[id] = id;}
	selectedIdText = "";
	if (idNames[id]) {
		selectedIdText = idNames[id];
	} else if (obj.innerText) {
		selectedIdText = obj.innerText;
	}
	
	if (idCaptions[id]) {
		selectedCaptionText = idCaptions[id]
	} else {
		selectedCaptionText = "";
	}
	
	//See if waiting for an item to be selected
	if (state == "moveItem" || state == "copyItem") {
		//Make sure we aren't going back to the definition itself
		if (sourceDefinitionId != id) {
			setStateMachine(state + "Confirm")
			return false
		}
		//The user must have clicked on the definition id
		//Go back to square 1
		setStateMachine("view_definition_options")
		return false
	}
		
	//See if waiting for an operation to be submitted
	if (state == "deleteItem") {
		//The user selected something else while in the confirmation step.
		//Go back to square 1
		setStateMachine("definition_selected")
		return false
	}
		
	//See if in the confirmation state
	if (state == "deleteDefinition" || state == "moveItemConfirm" || state == "copyItemConfirm") {
		//The user selected something else while in the confirmation step.
		//Go back to square 1
		setStateMachine("definition_selected")
		return false
	}
		

	//Put up the standard "view" and "delete" options
	operationSelection = "view_definition_options";
	operationSelectedItem = "";		
	if (sourceDefinitionId == mappedId) {
		setStateMachine("view_definition_options")
		return false
	} else {
		setStateMachine("definition_selected");
		return false;
	}
	return true;
}

function addOption(id, name, item) {
	//alert("addOption: " + id + ", " + name + ", " + item)
	showOptions(id, name, item)
	return false;
}

function addDefinition(id, name, item) {
	//alert("addDefinition: " + id + ", " + name + ", " + item)
	showOptions(id, name, item)
	return false;
}


function modifyDefinition() {
	operationSelection = "modifyDefinition"
	operationSelectedItem = selectedId
	setStateMachine("modifyDefinition")
	return false;
}
function copyDefinition() {
	operationSelection = "copyDefinition"
	operationSelectedItem = selectedId
	setStateMachine("copyDefinition")
	return false;
}
function deleteDefinition() {
	operationSelection = "deleteDefinition"
	operationSelectedItem = selectedId
	setStateMachine("deleteDefinition")
	return false;
}
function moveDefinition() {
	var urlParams={option:'moveDefinition', sourceDefinitionId:sourceDefinitionId, binderId:binderId};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "definition_builder"); 
	self.location.href =url;
	return false;
}
function setVisibility(visible, toId) {
	operationSelection = "view_definition_options"
	operationSelectedItem = selectedId
	ss_setupStatusMessageDiv()
	//alert("load div: " + option + ", " + itemId + ", " + itemName + ", " + refItemId)
	hideDisplayDiv();
	var urlParams={operation:'setVisibility', visibility:visible, option:'view_definition_options', 
		sourceDefinitionId:sourceDefinitionId, binderId:binderId};

	if (toId != "") {urlParams['targetId'] = toId;}
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "definition_builder"); 
	
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	//ajaxRequest.setEchoDebugInfo()
	ajaxRequest.setPostRequest(ss_postVisibilityChange);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request

}
function ss_postVisibilityChange(obj) {
	// get trimmed content 

	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else {
		ss_doImgOnloadCalls()
		showDisplayDiv()
	}
}
function addItem(id, name, item) {
	//alert("addItem: " + id + ", " + name + ", " + item)
	showOptions(id, name, item)
	return false;
}

function deleteItem(id, name, item) {
	operationSelection = id;
	operationSelectedItem = selectedId;
	setStateMachine('deleteItem')
	return false;
}

function moveItem(id, name, item) {
	operationSelection = id;
	operationSelectedItem = selectedId;
	setStateMachine('moveItem')
	return false;
}

function modifyItem(id, name, item) {
	//alert('modifyItem: ' + id + ', ' + name + ', ' + item)
	//User selected an operation, show the operation options
	operationSelection = id;
	operationSelectedItem = selectedId;
	setStateMachine("modifyItem")
	return false;
}

function copyItem(id, name, item) {
	operationSelection = id;
	operationSelectedItem = selectedId;
	setStateMachine("copyItem")
	return false;
}

function getConditionSelectbox(obj, op, op2) {
	ss_setupStatusMessageDiv()
	var formObj = ss_getContainingForm(obj)
	var nameObj = obj.name
	if (!obj.name) nameObj = obj.id;
	var urlParams={operation:op, sourceDefinitionId:sourceDefinitionId, binderId:binderId};
    if (op2 != null && op2 != "") urlParams['operation']=op2;
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams); 
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(formObj.name);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postLoadGetConditionRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_createUserList(name) {
	var userListObj = ssFind.createMultiple({
							prefix: "definitionBuilder", 
							elementName: name,
							container: document.getElementById("conditionOperand"),
							listType: "user",
							searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_user_search"
						});
}

function ss_postLoadGetConditionRequest() {
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else {
		showDisplayDiv()
	}
}

function ss_setFolderSelectHeight() {
	setTimeout("ss_setFolderSelectHeight2();", 100);
}

function ss_setFolderSelectHeight2() {
	var obj = self.getElementById('ss_folderSelectIframe')
	if (obj != null) {
		obj.style.height = "400px"
	}
}

function showOptions(id, name, item) {
	//alert('showOptions: ' + id + ', ' + name + ', ' + item)
	//User selected an operation, show the operation options
	operationSelection = id;
	operationSelectedItem = item;
	setStateMachine("operation_selected")
	return false;
}

function showProperties(name, refItem) {
	//alert('showProperties: ' + name + ', ' + refItem)
	//User selected an option, show the properties
	operationSelection = "addItem";
	operationSelectedItem = name;
	operationSelectedItemName = name;
	operationReferenceItem = refItem;
	setStateMachine("addItem")
	return false;
}

var state = "";
function setStateMachine(newState) {
//	alert("Entering new state: " + newState + " selectedId:" + selectedId)
	state = newState
	if (state == "definition_selected") {
		//alert("info_"+selectedIdMapped)
		//Hide: selection instructions
		//Show: definition info, definition operations
		
		ss_setDivHtml("displaydiv", "")
		//ss_addToDiv("displaydiv", "info_"+selectedIdMapped)
		hideDisplayButtons()
		loadDiv('operations', selectedIdMapped, "")
		//ss_addToDiv("displaydiv", "operations_"+selectedIdMapped)
	} else if (state == "operation_selected") {
		//alert("operation_selected: " + operationSelection + ", info_"+selectedIdMapped+ ", operationSelectedItem: "+operationSelectedItem)
		if (operationSelection == "addDefinition" && operationSelectedItem != "") {
			ss_setDivHtml("displaydiv", "")
			ss_addToDiv("displaydiv", "info_"+selectedIdMapped)
			//ss_addToDiv("displaydiv", "operations_"+selectedIdMapped)
			//ss_addToDiv("displaydiv", "properties_"+operationSelectedItem)
			showDisplayButtons()
			loadDiv('properties', "", operationSelectedItem)
		} else if (operationSelection == "addOption") {
			ss_setDivHtml("displaydiv", "")
			//ss_addToDiv("displaydiv", "info_"+selectedIdMapped)
			//ss_addToDiv("displaydiv", "operations_"+selectedIdMapped)
			//ss_addToDiv("displaydiv", "options_"+selectedIdMapped)
			hideDisplayButtons()
			loadDiv('options', selectedIdMapped, "")
		} else {
			ss_setDivHtml("displaydiv", "")
			//ss_addToDiv("displaydiv", "info_select")
			hideDisplayButtons()
			loadDiv('info', '', 'select');
		}
	} else if (state == "view_definition_options") {
		//alert("view_definition_options")
		ss_setDivHtml("displaydiv", "")
		hideDisplayButtons();
		showDisplayButtons()
		loadDiv(state, "", "");
	} else if (state == "modifyDefinition") {
		ss_setDivHtml("displaydiv", "")
		showDisplayButtons()
		loadDiv('properties', "", "")
	} else if (state == "copyDefinition") {
		ss_setDivHtml("displaydiv", "")
		showDisplayButtons()
		loadDiv('copyDefinition', "", "")
	} else if (state == "deleteDefinition") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "delete_item")
		showDisplayButtons()
		loadDiv('deleteDefinition', "", "")
	} else if (state == "viewItem") {
		//alert("viewItem: "+operationSelectedItem)
		ss_setDivHtml("displaydiv", "")
		//ss_addToDiv("displaydiv", "info_"+operationSelectedItem)
		hideDisplayButtons()
		loadDiv('info', "", operationSelectedItem);
	} else if (state == "addItem") {
		ss_setDivHtml("displaydiv", "")
		//ss_addToDiv("displaydiv", "info_"+operationSelectedItem)
		//ss_addToDiv("displaydiv", "properties_"+operationSelectedItem)
		showDisplayButtons()
		loadDiv('properties', "", operationSelectedItem, operationReferenceItem);
		operationReferenceItem = "";
	} else if (state == "modifyItem") {
		ss_setDivHtml("displaydiv", "")
		showDisplayButtons()
		loadDiv('properties', selectedIdMapped, "")
	} else if (state == "deleteItem") {
		//alert("deleteItem: " + selectedId)
		ss_setDivHtml("displaydiv", "")
		//ss_addToDiv("displaydiv", "info_"+selectedId)
		ss_addToDiv("displaydiv", "delete_item")
		//loadDiv('info', '', 'delete_item');
		showDisplayButtons()
		showDisplayDiv();
	} else if (state == "moveItem") {
		ss_setDivHtml("displaydiv", "")
		ss_addHtmlToDivFront("displaydiv", "<div class='ss_titlebold designer_dialog_title'>"+idCaptions[selectedId]+"</div>")
		//ss_addToDiv("displaydiv", "info_"+selectedId)
		ss_addToDiv("displaydiv", "move_item")
		//loadDiv('info', '', 'move_item')
		hideDisplayButtons()
		showDisplayDiv();
	} else if (state == "moveItemConfirm") {
		ss_setDivHtml("displaydiv", "")
		//ss_addToDiv("displaydiv", "info_"+selectedId)
		//loadDiv('info', '', selectedId)
		var infoName = ""
		if (idCaptions[lastSelectedId]) {infoName = "<span class='ss_bold'>"+idCaptions[lastSelectedId]+"</span>"}
		ss_setDivHtml("moveItemSelection", infoName);
		ss_addToDiv("displaydiv", "move_item_confirm")
		showDisplayButtons()
		showDisplayDiv();
	} else if (state == "copyItem") {
		ss_setDivHtml("displaydiv", "")
		//ss_addToDiv("displaydiv", "info_"+selectedId)
		ss_addToDiv("displaydiv", "copy_item")
		//loadDiv('info', '', 'copyItem')
		hideDisplayButtons()
		showDisplayDiv();
	} else if (state == "copyItemConfirm") {
//		ss_addToDiv("displaydiv", "copy_item_confirm")
		showDisplayButtons()
		showDisplayDiv();
	} else {
		//alert("State: " + state)
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_select")
		hideDisplayButtons()
		loadDiv('info', '', 'select')
	}
}

function submitBuildForm(obj) {
	//alert(obj.form.name)
	setSubmitData(obj.form);
	obj.form.submit();
	return true;
}
function setSubmitData(formObj) {
	//alert('setSubmitData: ' + formObj.name)
	//alert('selectedId: '+selectedId+'\noperation: '+operationSelection+'\noperationItem: '+operationSelectedItem+'\noperationItemName: '+operationSelectedItemName)
	formObj.selectedId.value = selectedId;
	formObj.selectedIdMapped.value = selectedIdMapped;
	formObj.operation.value = operationSelection;
	formObj.operationItem.value = operationSelectedItem;
	formObj.operationItemName.value = operationSelectedItemName;
	formObj.sourceDefinitionId.value = sourceDefinitionId;
}

function ss_loadNextDiv(option, itemId, itemName, refItemId) {
	ss_setupStatusMessageDiv()
	//alert("load div: " + option + ", " + itemId + ", " + itemName + ", " + refItemId)
	hideDisplayDiv();
	var urlParams={option:option, binderId:binderId};
	if (sourceDefinitionId != "") urlParams['sourceDefinitionId'] = sourceDefinitionId;
	if (itemId != "")  urlParams['itemId'] = itemId;
	if (itemName != "") urlParams['itemName'] = itemName;
	if (refItemId != "") urlParams['refItemId'] = refItemId;
	
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams, "definition_builder"); 
	
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postLoadNextDivRequest);
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postLoadNextDivRequest(obj) {
	// get trimmed content 
	var trimmed = "xxx" ;
	//var trimmed = self.document.getElementById("displayDiv").innerHTML.replace(/^\s+|\s+$/g, '') ;
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	} else if (trimmed == "") {
	//don't display empty options
		var formObj = self.document.getElementById("definitionbuilder");
		setSubmitData(formObj)
		formObj.submit()
	} else {
		ss_doImgOnloadCalls()
		showDisplayDiv()
	}
}
function ss_doImgOnloadCalls() {
	var displaydivObj = document.getElementById('displaydiv');
	var imgObjs = displaydivObj.getElementsByTagName('img');
	for (var i=0; i<imgObjs.length; i++) {
		if (imgObjs[i].ssf_onload && imgObjs[i].ssf_onload != '') {
			//alert(imgObjs[i].ssf_onload)
			eval(imgObjs[i].ssf_onload)
		}
	}
}

ss_createOnLoadObj('initializeStateMachine', initializeStateMachine);

function ss_ug_saveResults() {
	var formName = "definitionbuilder"
	var formObj = document.getElementById(formName);
	var s = "";
	var items = formObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		s += items[i].id + " ";
	}
	ss_saveUserGroupResults(s);
}

function ss_saveUserGroupResults(s) {
	var formObj = document.getElementById('definitionbuilder');
	var eleObjs = ss_getElementsByClass('ss_user_group_results', formObj);
	eleObjs[0].value = s;
}

</script>
<div class="ss_style ss_portlet">
<div>
<ssf:form titleTag="toolbar.menu.definition_builder">
<br/>
<c:if test="${!empty ss_configErrorMessage}">
<div class="ss_labelLeftError">
<span><c:out value="${ss_configErrorMessage}"/></span>
</div>
<br/>
<br/>
</c:if>

<table cellpadding="0" cellspacing="0" width="100%">
<tr>
<td><span class="ss_titlebold">
<c:if test="${!empty ssBinder}">${ssBinder.title}<br/></c:if>
<ssf:nlt tag="definition.builder" text="Definition builder" />
 >> <c:out value="${data.selectedItemTitle}" escapeXml="true" />
</span>
</td>
<td align="right">

<form name="form1" action="<ssf:url actionUrl="false" binderId="${ssBinderId}" action="manage_definitions"/>" 
				method="post">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
	<sec:csrfInput />
</form>
</td>
</tr>
</table>
<br/>
<br/>

<div id="definitionbuilder_tree_loading">
<span><ssf:nlt tag="Loading" text="Loading..."/></span><br/>
</div>
<table width="100%"">
	<tr>
		<td width="50%" valign="top">
			<div id="definitionbuilder_tree" style="visibility:hidden;">
				<ssf:tree treeName="definitionTree" 
				 treeDocument="<%= definitionTree %>" 
				 rootOpen="true" 
				 onMouseover="ss_showTreeHover"
				 onMouseout="ss_hideTreeHover"
				 nodeOpen="<%= nodeOpen %>" />
			</div>
		</td>
		<td width="50%" valign="top">
			<div id="displaydiv_spacer" style="height:1px;">&nbsp;
			</div>
			<div id="displaydiv0" style="display:inline; visibility:hidden; z-index:100; position:relative;">
			<form class="ss_form" action="<ssf:url actionUrl="true" binderId="${ssBinderId}"><ssf:param 
				name="action" value="definition_builder" /></ssf:url>" 
				method="post" name="definitionbuilder" id="definitionbuilder" 
				onSubmit="setSubmitData(this)" style="display:inline;" >
			<ssf:box>
			  <ssf:param name="box_id" value="displaydivbox" />
			  <ssf:param name="box_width" value="350" />
			  <ssf:param name="box_show_close_icon" value="true" />
			  <ssf:param name="box_show_close_routine" value="hideDisplayDiv()" />
			  <ssf:param name="box_color" value="${ss_form_gray_color}" />
			  <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    		  <ssf:param name="box_title" useBody="true">&nbsp;</ssf:param>
			<div class="ss_form" id="displaydiv" 
			  style="margin:0px; padding:4px 14px;">&nbsp;</div>  
			<div class="ss_form" id="displaydivButtons" 
			  style="margin:0; padding:4px 14px; visibility:hidden;">
			<input class="ss_submit" type="submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
			&nbsp;&nbsp;&nbsp;
			<input class="ss_submit" type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
			</div>
			</ssf:box>
			<input type="hidden" name="selectedId" />
			<input type="hidden" name="selectedIdMapped" />
			<input type="hidden" name="operation" />
			<input type="hidden" name="operationItem" />
			<input type="hidden" name="operationItemName" />
			<input type='hidden' name='sourceDefinitionId'>
				<sec:csrfInput />
			</form>
			</div>
			<div id="displaydiv_spacer_bottom"></div>
		</td>
	</tr>
</table>
</ssf:form>

</div>

<div>

<ssf:buildDefinitionDivs title='<%= NLT.get("definition.select_item") %>'
  sourceDocument="${data.sourceDefinition}" 
  configDocument="${ssConfigDefinition}"
  />
</div>

<%
	//Show the preview area
		String selectedItem = (String)data.get("selectedItem");
		//See if this is an entry definition
		Element configElementEntry = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='entryForm']");
		Element configElementProfile = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='profileEntryForm']");
		Element configElementProfiles = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='profileForm']");
		Element configElementFolder = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='folderForm']");
		Element configElementWorkspace = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='workspaceForm']");
		Element configElement = null;
		if (configElementEntry != null || configElementProfile != null || configElementProfiles != null || 
				configElementFolder != null || configElementWorkspace != null) {
			//This definition has a form definition; so show the form preview
			if (configElementEntry != null) {
				configElement = configElementEntry;
				request.setAttribute("definitionEntry", new FolderEntry());
			} else if (configElementProfile != null) {
				configElement = configElementProfile;
				request.setAttribute("definitionEntry", new User(new IdentityInfo()));
			} else if (configElementProfiles != null) {
				configElement = configElementProfiles;
				request.setAttribute("definitionEntry", new User(new IdentityInfo()));
			} else if (configElementFolder != null) {
				configElement = configElementFolder;
				request.setAttribute("definitionEntry", new Folder());
			} else if (configElementWorkspace != null) {
				configElement = configElementWorkspace;
				request.setAttribute("definitionEntry", new Workspace());
			}
			request.setAttribute("ssConfigElement", configElement);
			String configJspStyle = "form";
			request.setAttribute("ssConfigJspStyle", "form");
%>

<br/>
<hr class="portlet-section-header">
<br/>

<div class="ss_style ss_portlet">
<div align="center" width="100%">
  <span class="ss_titlebold">
    <ssf:nlt tag="definition.form_preview" text="Form Preview"/><br/><c:out value="${data.selectedItemTitle}" escapeXml="true" />
  </span>
</div>
<br/>

<table cellpadding="10" width="100%"><tr><td>
<ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="<%= configElement %>" 
  configJspStyle="<%= configJspStyle %>" 
  processThisItem="true" />
</td></tr></table>
</div>

<%
		} else {
	 		//See if this is a workflow definition
	 		Element workflowElement = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='workflowProcess']");
			if (workflowElement != null) {
				//This is a workflow definition, show the applet
%>

<br/>
<hr class="portlet-section-header">
<br/>

<div class="ss_style ss_portlet">
<div align="center" width="100%">
  <span class="ss_titlebold">
    <ssf:nlt tag="definition.workflow_preview" text="Workflow Preview"/><br/><c:out value="${data.selectedItemTitle}" escapeXml="true" />
  </span>

<br/>
<!--NOVELL_REWRITE_ATTRIBUTE_ON='value'-->
<c:if test="<%= isIE %>">
<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH = "100%" HEIGHT = "600"  
  codebase="http://java.sun.com/update/1.7.0/jinstall-7u72-windows-i586.cab">
</c:if>
<c:if test="<%= !isIE %>">
<applet CODE = "org.kablink.teaming.applets.workflowviewer.WorkflowViewer" 
  JAVA_CODEBASE = "<html:appletPath/>applets" 
  ARCHIVE = "workflow-viewer/kablink-teaming-workflowviewer-applet.jar,lib/colt.jar,lib/commons-collections.jar,lib/jung.jar,lib/dom4j.jar,lib/jaxen.jar" 
  WIDTH = "100%" HEIGHT = "600">
</c:if>
    <PARAM NAME = CODE value = "org.kablink.teaming.applets.workflowviewer.WorkflowViewer" >
    <PARAM NAME = CODEBASE value = "<html:appletPath/>applets" >
    <PARAM NAME = ARCHIVE value = "workflow-viewer/kablink-teaming-workflowviewer-applet.jar,lib/colt.jar,lib/commons-collections.jar,lib/jung.jar,lib/dom4j.jar,lib/jaxen.jar" >
    <param name="type" value="application/x-java-applet;version=1.7">
    <param name="scriptable" value="false">
	<param name="xmlGetUrl" value="<ssf:url 
    		webPath="viewDefinitionXml" >
			<ssf:param name="id" value="<%= selectedItem %>" />
    		</ssf:url>&ss_random=<%= String.valueOf(new java.util.Date().getTime()) %>"/>
	<param name="xmlPostUrl" value="<ssf:url 
		    adapter="true" 
		    portletName="ss_administration" 
		    action="viewDefinitionXml" 
		    actionUrl="true" >
			<ssf:param name="id" value="<%= selectedItem %>" />
		    </ssf:url>"/>
	<param name="nltSaveLayout" value="<ssf:nlt tag="definition.workflow_save_layout" text="Save layout"/>"/>
<c:if test="<%= !isIE %>">
</applet>
</c:if>
<c:if test="<%= isIE %>">
</object>
</c:if>
<!--NOVELL_REWRITE_ATTRIBUTE_OFF='value'-->

</div>
<br/>
</div>
</div>
<%
			}
		}
%>

<script type="text/javascript">
var idNames = new Array();
var idCaptions = new Array();
var idReplyStyles = new Array();

<c:forEach var="item" items="${data.idData.names}">
idNames['<c:out value="${item.key}"/>'] = '<c:out value="${item.value}"/>';
</c:forEach>

<c:forEach var="item" items="${data.idData.captions}">
idCaptions['<c:out value="${item.key}"/>'] = '<c:out value="${item.value}"/>';
</c:forEach>

</script>
