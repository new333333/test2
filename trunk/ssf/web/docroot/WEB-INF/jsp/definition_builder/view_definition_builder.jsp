<%
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
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="com.sitescape.team.domain.DefinitionInvalidOperation" %>
<%@ page import="com.sitescape.team.util.NLT" %>

<jsp:useBean id="definitionTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="data" type="java.util.Map" scope="request" />
<%@ page import="com.sitescape.team.domain.FolderEntry" %>
<%@ page import="com.sitescape.team.domain.User" %>
<%@ page import="com.sitescape.team.domain.Folder" %>
<%@ page import="com.sitescape.team.domain.Workspace" %>
<%
	boolean isIE = com.sitescape.util.BrowserSniffer.is_ie(request);

	String nodeOpen = " ";
	if (data.containsKey("nodeOpen")) {
		 nodeOpen = (String) data.get("nodeOpen");
	}
	if (nodeOpen.equals("")) {nodeOpen = " ";}
%>
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/single_user.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/jsp/tag_jsps/find/user_list.js"></script>
<script type="text/javascript">
dojo.require('dojo.widget.*');
dojo.require('sitescape.widget.SelectPageable');
dojo.require('sitescape.widget.MultiplePageableSelect');
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
<%
	if (!data.containsKey("selectedItem") || data.get("selectedItem").equals("")) {
%>
var sourceDefinitionId = '';
<%
	} else {
%>
var sourceDefinitionId = '<%= data.get("selectedItem") %>';
<%
	}

	String definitionType = "";
	if (data.containsKey("definitionType")) {
		definitionType = (String) data.get("definitionType");
	}
%>

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
}

function loadDiv(option, itemId, itemName, refItemId) {
	//alert("Load div: " + option + ", " + itemId + ", " + itemName + ", " + refItemId)
	ss_loadNextDiv(option, itemId, itemName, refItemId)
	return
	
	
	hideDisplayDiv();
	var url = "<ssf:url adapter="true" 
		portletName="ss_administration" 
		action="definition_builder" 
		actionUrl="true" />";
	if (sourceDefinitionId != "") {url += "\&sourceDefinitionId=" + sourceDefinitionId;}
	url += "\&option=" + option
	if (itemId != "") {url += "\&itemId=" + itemId;}
	if (itemName != "") {url += "\&itemName=" + itemName;}
	if (refItemId != "") {url += "\&refItemId=" + refItemId;}
	url += "\&rn=" + rn++
	//alert(url)
	ss_fetch_url(url, loadDivCallback)
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

	//alert(displaydivObj.innerHTML) 
	//alert("displaydiv: " + parseInt(ss_getDivHeight('displaydiv')) + ", displaydivbox: " + parseInt(ss_getDivHeight('displaydivbox')))

    ss_setObjectHeight(displaydiv0Obj, parseInt(ss_getObjectHeight(displaydivObj)));
    ss_setObjectHeight(displaydiv0Obj, parseInt(parseInt(ss_getObjectHeight(displaydivboxObj)) + parseInt(ss_getObjectHeight(displaydivButtonsObj))));

    //Position the div being displayed so it is in view
    var spacerTop = parseInt(ss_getDivTop('displaydiv_spacer'));
    var spacerBottom = parseInt(ss_getDivTop('displaydiv_spacer_bottom'));
    var divHeight = 0;
    if (spacerTop < parseInt(self.document.body.scrollTop) + ss_scrollTopOffset) {
    	divHeight = parseInt(self.document.body.scrollTop + ss_scrollTopOffset - spacerTop);
   	}
    ss_setObjectHeight(spacerObj, divHeight);
}

var ss_scrollTopOffset = 15;

function loadDivCallback(s) {
	//alert("s: " +s)
	ss_addHtmlToDiv("displaydiv", s)
	showDisplayDiv();
}

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
	if (state == "moveItem" || state == "cloneItem") {
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
	if (state == "deleteDefinitionConfirm" || state == "moveItemConfirm" || state == "cloneItemConfirm") {
		//The user selected something else while in the confirmation step.
		//Go back to square 1
		setStateMachine("definition_selected")
		return false
	}
		
	if (sourceDefinitionId == "" && !idMap[id]) {
		//This is a request to view a definition
		return viewDefinition();
	}

	//Put up the standard "view" and "delete" options
	operationSelection = "viewDefinitionOptions";
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

function viewDefinition() {
	operationSelection = "selectId";
	operationSelectedItem = "selectedId";
	ss_setDivHtml("displaydiv", "");
	ss_setDivHtml("displaydiv", ss_getDivHtml("definitionbuilder_tree_loading"));
	var formObj = self.document.forms['definitionbuilder']
	setSubmitData(formObj)
	formObj.submit()
	return false
}

function modifyDefinition() {
	operationSelection = "modifyDefinition"
	operationSelectedItem = selectedId
	setStateMachine("modifyDefinition")
	return false;
}

function deleteDefinition() {
	operationSelection = "deleteDefinition"
	operationSelectedItem = selectedId
	setStateMachine("deleteDefinitionConfirm")
	return false;
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

function cloneItem(id, name, item) {
}

function getConditionSelectbox(obj, op, op2) {
	ss_setupStatusMessageDiv()
	var formObj = ss_getContainingForm(obj)
	var nameObj = obj.name
	if (!obj.name) nameObj = obj.id;
	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
    	</ssf:url>"
    url += "&operation=" + op;
    if (op2 != null && op2 != "") url += "&operation2=" + op2;
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addFormElements(formObj.name);
	//ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postLoadGetConditionRequest);
	ajaxRequest.setUsePOST();
	ajaxRequest.sendRequest();  //Send the request
}

function ss_createUserList(name) {
	if (!name || name == "") name = 'conditionElementValue';
	
	var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>&operation=get_users_widget&searchText=%{searchString}&pager=%{pagerString}";
	var props = {name : "select_"+name, 
				 id : "select_"+name, 
				 dataUrl:url,
				 maxListLength : 12,
				 autoComplete: false,
				 hiddenFormElementName: name,
				 imgRootPath: "<html:imagesPath/>"};
	var usersWidget = dojo.widget.createWidget("MultiplePageableSelect", props, document.getElementById("conditionOperand"), "last");
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
	//alert("Entering new state: " + newState)
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
		var selectedIdNameText = "<span class='ss_bold'>"+selectedCaptionText + " (" + selectedIdText + ")</span>";
		ss_setDivHtml("infoDefinitionOptionsDefinitionName", selectedIdNameText)
		ss_addToDiv("displaydiv", "infoDefinitionOptions")
		hideDisplayButtons()
		showDisplayDiv();
	} else if (state == "modifyDefinition") {
		ss_setDivHtml("displaydiv", "")
		showDisplayButtons()
		loadDiv('properties', "", "")
	} else if (state == "deleteDefinitionConfirm") {
		//alert('deleteDefinitionConfirm')
		ss_setDivHtml("displaydiv", "")
		var selectedIdNameText = "<span class='ss_bold'>"+selectedCaptionText + " (" + selectedIdText + ")</span>";
		ss_setDivHtml("deleteDefinitionSelection", selectedIdNameText)
		ss_addToDiv("displaydiv", "delete_definition_confirm")
		showDisplayButtons()
		showDisplayDiv();
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
	} else if (state == "cloneItemConfirm") {
		ss_addToDiv("displaydiv", "clone_item_confirm")
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
	var url = "<ssf:url adapter="true" 
		portletName="ss_administration" 
		action="definition_builder" 
		actionUrl="true" />";
	if (sourceDefinitionId != "") {url += "\&sourceDefinitionId=" + sourceDefinitionId;}
	url += "\&option=" + option
	if (itemId != "") {url += "\&itemId=" + itemId;}
	if (itemName != "") {url += "\&itemName=" + itemName;}
	if (refItemId != "") {url += "\&refItemId=" + refItemId;}
	url += "\&rn=" + rn++
	//alert(url)
	
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

<c:if test="${!empty ss_configErrorMessage}">
<div class="ss_labelLeftError">
<span><c:out value="${ss_configErrorMessage}"/></span>
</div>
<br/>
<br/>
</c:if>

<span class="ss_titlebold">
<a href="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="definition_builder" /><portlet:param 
	name="definition_type" value="<%= definitionType %>" /></portlet:actionURL>">
<ssf:nlt tag="definition.builder" text="Definition builder" />
</a>
<%
	//See if there is a selected item
	if (data.containsKey("selectedItemTitle") && !data.get("selectedItemTitle").equals("")) {
%>
>> <c:out value="${data.selectedItemTitle}" />
<%
	}
%>
</span>
<br/>
<br/>

<div id="definitionbuilder_tree_loading">
<span><ssf:nlt tag="Loading" text="Loading..."/></span><br/>
</div>
<table width="100%">
	<tr>
		<td width="50%" valign="top">
			<div id="definitionbuilder_tree" style="visibility:hidden;">
				<ssf:tree treeName="definitionTree" 
				 treeDocument="<%= definitionTree %>" 
				 rootOpen="true" 
				 nodeOpen="<%= nodeOpen %>" />
			</div>
		</td>
		<td width="50%" valign="top">
			<div id="displaydiv_spacer" style="height:1px;">&nbsp;
			</div>
			<div id="displaydiv0" style="display:inline; visibility:hidden;">
			<form class="ss_form" action="<portlet:actionURL windowState="maximized"><portlet:param 
				name="action" value="definition_builder" /><portlet:param 
				name="definition_type" value="<%= definitionType %>" /></portlet:actionURL>" 
				method="post" name="definitionbuilder" id="definitionbuilder" 
				onSubmit="setSubmitData(this)" style="display:inline;" >
			<ssf:box>
			  <ssf:param name="box_id" value="displaydivbox" />
			  <ssf:param name="box_width" value="300" />
			  <ssf:param name="box_show_close_icon" value="true" />
			  <ssf:param name="box_show_close_routine" value="hideDisplayDiv()" />
			  <ssf:param name="box_color" value="${ss_form_gray_color}" />
			  <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
    		  <ssf:param name="box_title" useBody="true">&nbsp;</ssf:param>
			<div class="ss_form" id="displaydiv" 
			  style="margin:0px; padding:4px;">&nbsp;</div>  
			<div class="ss_form" id="displaydivButtons" 
			  style="margin:0; padding:4px; visibility:hidden;">
			<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
			&nbsp;&nbsp;&nbsp;
			<input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
			</div>
			</ssf:box>
			<input type="hidden" name="selectedId" />
			<input type="hidden" name="selectedIdMapped" />
			<input type="hidden" name="operation" />
			<input type="hidden" name="operationItem" />
			<input type="hidden" name="operationItemName" />
			<input type='hidden' name='sourceDefinitionId'>
			</form>
			</div>
			<div id="displaydiv_spacer_bottom"></div>
		</td>
	</tr>
</table>

</div>

<div>
<%
	String ssSelectItemText = NLT.get("definition.select_item");
	//Build the divs
	if (!data.containsKey("selectedItem") || data.get("selectedItem").equals("")) {
%>
<ssf:buildDefinitionDivs title="<%= ssSelectItemText %>" 
  sourceDocument="${ssConfigDefinition}" configDocument="${ssConfigDefinition}"/>
<%
	
	} else {
		//A definition type was selected. Build the page to edit that definition type
%>
<ssf:buildDefinitionDivs title="<%= ssSelectItemText %>" 
  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
  configDocument="${ssConfigDefinition}" />
<%
	}
%>
</div>

<%
	//Show the preview area
	if (data.containsKey("selectedItem") && !data.get("selectedItem").equals("")) {
		String selectedItem = (String)data.get("selectedItem");
		//See if this is an entry definition
		Element configElementEntry = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='entryForm']");
		Element configElementFileEntry = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='fileEntryForm']");
		Element configElementProfile = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='profileEntryForm']");
		Element configElementFolder = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='folderForm']");
		Element configElementFileFolder = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='fileFolderForm']");
		Element configElementWorkspace = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='workspaceForm']");
		Element configElement = null;
		if (configElementEntry != null || configElementProfile != null || 
				configElementFileEntry != null || configElementFileFolder != null ||
				configElementFolder != null || configElementWorkspace != null) {
			//This definition has a form definition; so show the form preview
			String definitionName = (String) ((Document) data.get("sourceDefinition")).getRootElement().attributeValue("caption","");
			if (configElementEntry != null) {
				configElement = configElementEntry;
				request.setAttribute("definitionEntry", new FolderEntry());
			} else if (configElementFileEntry != null) {
				configElement = configElementFileEntry;
				request.setAttribute("definitionEntry", new FolderEntry());
			} else if (configElementProfile != null) {
				configElement = configElementProfile;
				request.setAttribute("definitionEntry", new User());
			} else if (configElementFolder != null) {
				configElement = configElementFolder;
				request.setAttribute("definitionEntry", new Folder());
			} else if (configElementFileFolder != null) {
				configElement = configElementFileFolder;
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
    <ssf:nlt tag="definition.form_preview" text="Form Preview"/><br/><%= definitionName %>
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
				String definitionName = (String) ((Document) data.get("sourceDefinition")).getRootElement().attributeValue("caption","");
%>

<br/>
<hr class="portlet-section-header">
<br/>

<div class="ss_style ss_portlet">
<div align="center" width="100%">
  <span class="ss_titlebold">
    <ssf:nlt tag="definition.workflow_preview" text="Workflow Preview"/><br/><%= definitionName %>
  </span>

<br/>
<c:if test="<%= isIE %>">
<object classid="clsid:8AD9C840-044E-11D1-B3E9-00805F499D93" WIDTH = "100%" HEIGHT = "600"  
  codebase="http://java.sun.com/update/1.5.0/jinstall-1_5-windows-i586.cab#Version=5,0,0,3">
</c:if>
<c:if test="<%= !isIE %>">
<applet CODE = "com.sitescape.team.applets.workflowviewer.WorkflowViewer" 
  JAVA_CODEBASE = "<html:rootPath/>applets" 
  ARCHIVE = "workflow-viewer/ssf-workflowviewer-applet.jar,lib/colt.jar,lib/commons-collections-3.1.jar,lib/jung-1.7.6.jar,lib/dom4j.jar,lib/jaxen.jar" 
  WIDTH = "100%" HEIGHT = "600">
</c:if>
    <PARAM NAME = CODE VALUE = "com.sitescape.team.applets.workflowviewer.WorkflowViewer" >
    <PARAM NAME = CODEBASE VALUE = "<html:rootPath/>applets" >
    <PARAM NAME = ARCHIVE VALUE = "workflow-viewer/ssf-workflowviewer-applet.jar,lib/colt.jar,lib/commons-collections-3.1.jar,lib/jung-1.7.6.jar,lib/dom4j.jar,lib/jaxen.jar" >
    <param name="type" value="application/x-java-applet;version=1.5">
    <param name="scriptable" value="false">
	<param name="xmlGetUrl" value="<ssf:url 
    		webPath="viewDefinitionXml" >
			<ssf:param name="id" value="<%= selectedItem %>" />
    		</ssf:url>"/>
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

</div>
<br/>
</div>
</div>
<%
			}
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
