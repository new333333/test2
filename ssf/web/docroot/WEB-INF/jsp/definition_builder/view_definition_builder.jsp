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

<jsp:useBean id="definitionTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="data" type="java.util.Map" scope="request" />
<jsp:useBean id="ssPublicEntryDefinitions" type="java.util.Map" scope="request" />
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ssConfigJspStyle" type="String" scope="request" />
<%@ page import="com.sitescape.ef.domain.FolderEntry" %>
<%
	String nodeOpen = " ";
	if (data.containsKey("nodeOpen")) {
		 nodeOpen = (String) data.get("nodeOpen");
	}
	if (nodeOpen.equals("")) {nodeOpen = " ";}
%>
<script language="javascript">

var selectedId = null;
var selectedIdMapped = null;
var lastSelectedId = null;
var operationSelection = null;
var operationSelectedItem = "";
var operationSelectedItemName = "";

var selectedIdText = null;
var selectedCaptionText = null;
var selectedReplyStyle = null;
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
%>

function initializeStateMachine() {
	ss_hideAllDeclaredDivs()
	ss_setDivHtml("displaydiv", "")
	ss_addToDiv("displaydiv", "info_select")
}

function t_<portlet:namespace/>_definitionTree_showId(id, obj) {
	//User selected an item from the tree
	//See if this id has any info associated with it
	var mappedId = id;
	//alert('t_<portlet:namespace/>_definitionTree_showId: ' + id + '--> '+mappedId+', state: '+state)
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
	
	if (idReplyStyles[id]) {
		selectedReplyStyle = idReplyStyles[id]
	} else {
		selectedReplyStyle = "";
	}
	
	//See if waiting for an item to be selected
	if (state == "deleteItem" || state == "moveItem" || state == "cloneItem") {
		setStateMachine(state + "Confirm")
		return false
	}
		
	//See if waiting for an operation to be submitted
	if (state == "deleteItem") {
		//The user selected something else while in the confirmation step.
		//Go back to square 1
		setStateMachine("viewItem")
		return false
	}
		
	//See if in the confirmation state
	if (state == "deleteDefinitionConfirm" || state == "deleteItemConfirm" || state == "moveItemConfirm" || state == "cloneItemConfirm") {
		//The user selected something else while in the confirmation step.
		//Go back to square 1
		state = "";
		setStateMachine("")
		return false
	}
		
	if (checkForInfo(mappedId)) {
		setStateMachine("definition_selected")
		return false
	} else {
		//This id has no info div. Put up the standard "view" and "delete" options
		operationSelection = "viewDefinitionOptions";
		operationSelectedItem = "";
		setStateMachine("view_definition_options")
		return false
	}
	return true;
}

function checkForInfo(id) {
    //alert('checkForInfo: info_'+id)
    var objName = "info_"+id;
    var obj
    if (isNSN || isNSN6 || isMoz5) {
        obj = self.document.getElementById(objName)
    } else {
        obj = self.document.all[objName]
    }
    if (obj) {
    	return true;
    } else {
    	return false;
    }
}

function addOption(id, name, item) {
	showOptions(id, name, item)
	return false;
}

function addDefinition(id, name, item) {
	showOptions(id, name, item)
	return false;
}

function viewDefinition() {
	operationSelection = "selectId";
	operationSelectedItem = "selectedId";
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
	state = newState
	//alert('setStateMachine: ' + state)
	if (state == "definition_selected") {
		//Hide: selection instructions
		//Show: definition info, definition operations
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_"+selectedIdMapped)
		ss_addToDiv("displaydiv", "operations_"+selectedIdMapped)
	} else if (state == "operation_selected") {
		if (operationSelection == "addDefinition" && operationSelectedItem != "") {
			ss_setDivHtml("displaydiv", "")
			ss_addToDiv("displaydiv", "info_"+operationSelectedItem)
			ss_addToDiv("displaydiv", "properties_"+operationSelectedItem)
		} else if (operationSelection == "addOption") {
			ss_addToDiv("displaydiv", "options_"+selectedIdMapped)
		} else {
			ss_setDivHtml("displaydiv", "")
			ss_addToDiv("displaydiv", "info_select")
		}
	} else if (state == "view_definition_options") {
		ss_setDivHtml("displaydiv", "")
		var selectedIdNameText = "<span class='ss_contentbold'>"+selectedCaptionText + " (" + selectedIdText + ")</span>";
		ss_setDivHtml("infoDefinitionOptionsDefinitionName", selectedIdNameText)
		ss_addToDiv("displaydiv", "infoDefinitionOptions")
	} else if (state == "modifyDefinition") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "modify_definition")		
		self.document.forms['definitionbuilder'].modifyDefinitionName.value = selectedIdText;
		self.document.forms['definitionbuilder'].modifyDefinitionCaption.value = selectedCaptionText;
		if (self.document.forms['definitionbuilder'].modifyDefinitionReplyStyle) {
			var selObj = self.document.forms['definitionbuilder'].modifyDefinitionReplyStyle
			for (var i = 0; i < selObj.length; i++) {
				if (selObj.options[i].value == selectedReplyStyle) {
					selObj.options[i].selected = true;
				}
			}
		}
	} else if (state == "deleteDefinitionConfirm") {
		ss_setDivHtml("displaydiv", "")
		var selectedIdNameText = "<span class='ss_contentbold'>"+selectedCaptionText + " (" + selectedIdText + ")</span>";
		ss_setDivHtml("deleteDefinitionSelection", selectedIdNameText)
		ss_addToDiv("displaydiv", "delete_definition_confirm")
	} else if (state == "viewItem") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_"+operationSelectedItem)
	} else if (state == "addItem") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_"+operationSelectedItem)
		ss_addToDiv("displaydiv", "properties_"+operationSelectedItem)
	} else if (state == "modifyItem") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_"+selectedIdMapped)
		ss_addToDiv("displaydiv", "properties_"+selectedIdMapped)
	} else if (state == "deleteItem") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_"+selectedId)
		ss_addToDiv("displaydiv", "delete_item")
	} else if (state == "moveItem") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_"+selectedId)
		ss_addToDiv("displaydiv", "move_item")
	} else if (state == "moveItemConfirm") {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_"+selectedId)
		var infoName = ""
		if (idMapCaption[lastSelectedId]) {infoName = "<span class='ss_contentbold'>"+idMapCaption[lastSelectedId]+"</span>"}
		ss_setDivHtml("moveItemSelection", infoName);
		ss_addToDiv("displaydiv", "move_item_confirm")
	} else if (state == "cloneItemConfirm") {
		ss_addToDiv("displaydiv", "clone_item_confirm")
	} else {
		ss_setDivHtml("displaydiv", "")
		ss_addToDiv("displaydiv", "info_select")
	}
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

createOnLoadObj('initializeStateMachine', initializeStateMachine);

</script>


<div class="ss_portlet">

<form action="" method="post" name="definitionbuilder" onSubmit="setSubmitData(this)" >
<table width="100%">
	<tr>
		<td width="50%" valign="top">
			<div>
				<ssf:tree treeName="definitionTree" 
				 treeDocument="<%= definitionTree %>" 
				 rootOpen="true" 
				 nodeOpen="<%= nodeOpen %>" />
			</div>
		</td>
		<td width="50%" valign="top">
			<div id="displaydiv">
			</div>
		</td>
	</tr>
</table>

<input type="hidden" name="selectedId" />
<input type="hidden" name="selectedIdMapped" />
<input type="hidden" name="operation" />
<input type="hidden" name="operationItem" />
<input type="hidden" name="operationItemName" />
<input type='hidden' name='sourceDefinitionId'>
</form>

</div>

<%
	//Show the preview area
	if (data.containsKey("selectedItem") && !data.get("selectedItem").equals("")) {
		Element configElement = (Element) ((Document) data.get("sourceDefinition")).getRootElement().selectSingleNode("//item[@name='entryForm']");
		if (configElement != null) {
			String definitionName = (String) ((Document) data.get("sourceDefinition")).getRootElement().attributeValue("caption","");
			request.setAttribute("definitionEntry", new FolderEntry());
			request.setAttribute("configElement", configElement);
			ssConfigJspStyle = "form";
			request.setAttribute("ssConfigJspStyle", "form");
%>

<br>
<hr class="portlet-section-header">
<br>

<div class="ss_portlet">
<div align="center" width="100%">
  <span class="ss_titlebold">
    <ssf:nlt tag="definition.form_preview" text="Form Preview"/><br><%= definitionName %>
  </span>
</div>
<br>

<table cellpadding="10" width="100%"><tr><td>
<ssf:displayConfiguration 
  configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= configElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" 
  processThisItem="true" />
</td></tr></table>
</div>

<%
		}
	}
%>

<div>
<%
	//Build the divs
	if (!data.containsKey("selectedItem") || data.get("selectedItem").equals("")) {
%>
<ssf:buildDefinitionDivs title="Select the type of definition you want to work on..." 
  sourceDocument="<%= ssConfigDefinition %>" configDocument="<%= ssConfigDefinition %>"
  entryDefinitions="<%= ssPublicEntryDefinitions %>"/>
<%
	
	} else {
		//A definition type was selected. Build the page to edit that definition type
%>
<ssf:buildDefinitionDivs title="Select the item that you want to work on..." 
  sourceDocument="<%= (Document) data.get("sourceDefinition") %>" 
  configDocument="<%= ssConfigDefinition %>"
  entryDefinitions="<%= ssPublicEntryDefinitions %>"/>
<%
	}
%>
</div>

<script language="javascript">
var idNames = new Array();
var idCaptions = new Array();
var idReplyStyles = new Array();

<c:forEach var="item" items="${data.idData.names}">
idNames['<c:out value="${item.key}"/>'] = '<c:out value="${item.value}"/>';
</c:forEach>

<c:forEach var="item" items="${data.idData.captions}">
idCaptions['<c:out value="${item.key}"/>'] = '<c:out value="${item.value}"/>';
</c:forEach>

<c:forEach var="item" items="${data.idData.replyStyles}">
idReplyStyles['<c:out value="${item.key}"/>'] = '<c:out value="${item.value}"/>';
</c:forEach>

</script>
