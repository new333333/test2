<%
/**
 * Copyright (c) 2005 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="com.sitescape.ef.domain.DefinitionInvalidOperation" %>
<%@ page import="com.sitescape.ef.util.NLT" %>

<jsp:useBean id="definitionTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="data" type="java.util.Map" scope="request" />
<jsp:useBean id="ssPublicEntryDefinitions" type="java.util.Map" scope="request" />
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<%@ page import="com.sitescape.ef.domain.FolderEntry" %>
<%@ page import="com.sitescape.ef.domain.User" %>
<%
	String nodeOpen = " ";
	if (data.containsKey("nodeOpen")) {
		 nodeOpen = (String) data.get("nodeOpen");
	}
	if (nodeOpen.equals("")) {nodeOpen = " ";}
%>
<c:if test="${empty ss_taconite_loaded}">
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<c:set var="ss_taconite_loaded" value="1" scope="request"/>
</c:if>
<script type="text/javascript">

var rn = Math.round(Math.random()*999999)
var selectedId = null;
var selectedIdMapped = null;
var lastSelectedId = null;
var operationSelection = null;
var operationSelectedItem = "";
var operationSelectedItemName = "";

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
	ss_addToDiv("displaydiv", "info_select")
	hideDisplayButtons()
	showDisplayDiv()
	ss_showHideObj('definitionbuilder_tree_loading', 'hidden', 'none')
	ss_showHideObj('definitionbuilder_tree', 'visible', 'block')
}

function loadDiv(option, itemId, itemName) {
	//alert("Load div: " + option + ", " + itemId + ", " + itemName)
	//ss_loadNextDiv(option, itemId, itemName)
	//return
	
	
	hideDisplayDiv();
	var url = "<ssf:url adapter="true" 
		portletName="ss_administration" 
		action="definition_builder" 
		actionUrl="true" />";
	if (sourceDefinitionId != "") {url += "\&sourceDefinitionId=" + sourceDefinitionId;}
	url += "\&option=" + option
	if (itemId != "") {url += "\&itemId=" + itemId;}
	if (itemName != "") {url += "\&itemName=" + itemName;}
	url += "\&rn=" + rn++
	//alert(url)
	fetch_url(url, loadDivCallback)
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

function t_<portlet:namespace/>_definitionTree_showId(id, obj) {
	//User selected an item from the tree
	//See if this id has any info associated with it
	var mappedId = id;
	//alert('t_<portlet:namespace/>_definitionTree_showId: ' + id + '--> '+mappedId+', state: '+state+ ', sourceDefinitionId: '+sourceDefinitionId)
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

function showOptions(id, name, item) {
	//alert('showOptions: ' + id + ', ' + name + ', ' + item)
	//User selected an operation, show the operation options
	operationSelection = id;
	operationSelectedItem = item;
	setStateMachine("operation_selected")
	return false;
}

function showProperties(id, name, item) {
	//alert('showProperties: ' + id + ', ' + name + ', ' + item)
	//User selected an option, show the properties
	operationSelection = "addItem";
	operationSelectedItem = id;
	operationSelectedItemName = name;
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
		//alert("operation_selected: " + operationSelection + ", info_"+selectedIdMapped)
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
		loadDiv('properties', "", operationSelectedItem)
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

function ss_loadNextDiv(option, itemId, itemName) {
	//alert("load div: " + option + ", " + itemId + ", " + itemName)
	hideDisplayDiv();
	var url = "<ssf:url adapter="true" 
		portletName="ss_administration" 
		action="definition_builder" 
		actionUrl="true" />";
	if (sourceDefinitionId != "") {url += "\&sourceDefinitionId=" + sourceDefinitionId;}
	url += "\&option=" + option
	if (itemId != "") {url += "\&itemId=" + itemId;}
	if (itemName != "") {url += "\&itemName=" + itemName;}
	url += "\&rn=" + rn++
	//alert(url)
	
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.setEchoDebugInfo();
	ajaxRequest.setPostRequest(ss_postLoadNextDivRequest);
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postLoadNextDivRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_load_div_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="forum.unseenCounts.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	} else {
		showDisplayDiv()
	}
}

ss_createOnLoadObj('initializeStateMachine', initializeStateMachine);

</script>
<div class="ss_style ss_portlet">
<div id="ss_load_div_status_message"></div>
<div>

<c:if test="${!empty ss_configErrorMessage}">
<div class="ss_labelLeftError">
<span><c:out value="${ss_configErrorMessage}"/></span>
</div>
<br/>
<br/>
</c:if>

<span class="ss_titlebold">
<a href="<portlet:actionURL windowState="maximized">
	<portlet:param name="action" value="definition_builder" />
	<portlet:param name="definition_type" value="<%= definitionType %>" />
	</portlet:actionURL>">
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
			<form class="ss_form" action="<portlet:actionURL windowState="maximized">
				<portlet:param name="action" value="definition_builder" />
				<portlet:param name="definition_type" value="<%= definitionType %>" />
				</portlet:actionURL>" method="post" name="definitionbuilder" 
				onSubmit="setSubmitData(this)" style="display:inline;" >
			<ssf:box>
			  <ssf:param name="box_id" value="displaydivbox" />
			  <ssf:param name="box_width" value="300" />
			  <ssf:param name="box_show_close_icon" value="true" />
			  <ssf:param name="box_show_close_routine" value="hideDisplayDiv()" />
			  <ssf:param name="box_color" value="${ss_form_background_color}" />
			  <ssf:param name="box_canvas_color" value="${ss_style_background_color}" />
			<div class="ss_form" id="displaydiv" 
			  style="margin:0px; padding:4px;">&nbsp;</div>  
			<div class="ss_form" id="displaydivButtons" 
			  style="margin:0; padding:4px; visibility:hidden;">
			<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok" text="OK"/>">
			&nbsp;&nbsp;&nbsp;
			<input type="submit" name="okBtn" value="<ssf:nlt tag="button.cancel" text="Cancel"/>">
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
  sourceDocument="<%= ssConfigDefinition %>" configDocument="<%= ssConfigDefinition %>"
  entryDefinitions="<%= ssPublicEntryDefinitions %>"/>
<%
	
	} else {
		//A definition type was selected. Build the page to edit that definition type
%>
<ssf:buildDefinitionDivs title="<%= ssSelectItemText %>" 
  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
  configDocument="<%= ssConfigDefinition %>"
  entryDefinitions="<%= ssPublicEntryDefinitions %>"/>
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
		Element configElementProfile = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='profileEntryForm']");
		Element configElement = null;
		if (configElementEntry != null || configElementProfile != null) {
			//This is an entry or profile definition; so show the preview of the form
			String definitionName = (String) ((Document) data.get("sourceDefinition")).getRootElement().attributeValue("caption","");
			if (configElementEntry != null) {
				configElement = configElementEntry;
				request.setAttribute("definitionEntry", new FolderEntry());
			} else {
				configElement = configElementProfile;
				request.setAttribute("definitionEntry", new User());
			}
			request.setAttribute("configElement", configElement);
			ssConfigJspStyle = "form";
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
  configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= configElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" 
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
	<applet archive="workflow-viewer/workflow-viewer.jar,lib/colt.jar,lib/commons-collections-3.1.jar,lib/jung-1.7.0.jar,lib/dom4j.jar,lib/jaxen.jar" 
	  code="com.sitescape.ef.applets.workflowviewer.WorkflowViewer" 
	  codebase="<html:rootPath/>applets" height="600" width="100%" >
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
	</applet>
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
