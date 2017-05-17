/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

var ss_optionsArray;
if (typeof ss_optionsArray == "undefined") {
	ss_optionsArray = new Array();
}
var ss_userOptionsCounter = ss_optionsArray.length
var ss_searchMoreInitialized = false;
function ss_addOption(type) {
	ss_optionsArray[ss_userOptionsCounter]=type;
	switch (type){
	   case "workflow" :
	      ss_addWorkflow(ss_userOptionsCounter);
	      break;
	   case "entry" :
	      ss_addEntry(ss_userOptionsCounter);
	      break;
	   case "tag" :
	      ss_addTag(ss_userOptionsCounter);
	      break;
	   case "creation_date" :
	      ss_addDate(ss_userOptionsCounter, "creation");
	      break;
	   case "modification_date" :
	      ss_addDate(ss_userOptionsCounter, "modification");
	      break;
	   case "creator_by_id" :
	      ss_addAuthor(ss_userOptionsCounter);
	      break;
	   case "last_activity" :
	      ss_addLastActivity(ss_userOptionsCounter);
	      break;
	   case "folder" :
	      ss_addFolder(ss_userOptionsCounter);
	      break;
	   default : alert("Unknown type: "+type);
	}
	ss_userOptionsCounter++;
}
function ss_callRemoveSearchOption(orderNo) { 
	return function(evt) {ss_removeOption(orderNo);};
}


function ss_addInitializedWorkflow(wfIdValue, wfTitle, stepsValue, stepTitles) {
	ss_optionsArray[ss_userOptionsCounter]='workflow';
	var wfWidget = ss_addWorkflow(ss_userOptionsCounter, wfIdValue, wfTitle, stepsValue, stepTitles);
	ss_userOptionsCounter++;
}

function ss_addInitializedEntry(entryId, fieldName, value, valueLabel, valueType, fieldNameTitle, entryType, entryTypeTitle) {
	ss_debug("ss_addInitializedEntry: " + entryType + ", " + entryTypeTitle + ', ' + value + ', ' + valueLabel)
	if (typeof fieldNameTitle == "undefined") {
		fieldNameTitle = fieldName;
	}
	ss_optionsArray[ss_userOptionsCounter]='entry';
	ss_debug("fieldName: "+fieldName+", value: "+value+", valueLabel: "+valueLabel+", valueType: "+valueType+", fieldNameTitle: "+fieldNameTitle)
	ss_addEntry(ss_userOptionsCounter, entryId, fieldName, value, valueLabel, valueType, fieldNameTitle, entryType, entryTypeTitle);
	ss_userOptionsCounter++;
}

function ss_addInitializedCreationDate(startDate, endDate) {
	ss_optionsArray[ss_userOptionsCounter]='creation_date';
	ss_addDate(ss_userOptionsCounter, 'creation', startDate, endDate);
	ss_userOptionsCounter++;
}
function ss_addInitializedLastActivity(daysNumber) {
	ss_optionsArray[ss_userOptionsCounter]='last_activity';
	ss_addLastActivity(ss_userOptionsCounter, daysNumber);
	ss_userOptionsCounter++;
}
function ss_addInitializedModificationDate(startDate, endDate) {
	ss_optionsArray[ss_userOptionsCounter]='modification_date';
	ss_addDate(ss_userOptionsCounter, 'modification', startDate, endDate);
	ss_userOptionsCounter++;
}
function ss_addInitializedTag(communityTag, personalTag) {
	ss_optionsArray[ss_userOptionsCounter]='tag';
	ss_addTag(ss_userOptionsCounter, communityTag, personalTag);
	ss_userOptionsCounter++;
}

function ss_addInitializedAuthor(userId, userName) {
	ss_optionsArray[ss_userOptionsCounter]='creator_by_id';
	ss_addAuthor(ss_userOptionsCounter, userId, userName);
	ss_userOptionsCounter++;
}

function ss_addWorkflow(orderNo, wfIdValue, wfTitle, stepsValue, stepTitles) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	
	var workflowsContainer = document.createElement('div');
	div.appendChild(workflowsContainer);
	
	var remover = document.createElement('img');
	dojo.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.png");
	workflowsContainer.appendChild(remover);
	workflowsContainer.appendChild(document.createTextNode(" " + ss_nlt_searchFormLabelWorkflow + " "));

	var wDiv = document.createElement('div');
	wDiv.id = "placeholderWorkflow"+orderNo;
	workflowsContainer.appendChild(wDiv);
	var stepsLabel = document.createElement('div');
	stepsLabel.id = "workflowSteps"+orderNo+"Label";
    div.appendChild(stepsLabel);
	var stepsContainer = document.createElement('ul');
	stepsContainer.id = "workflowSteps"+orderNo;
	stepsContainer.className = "ss_nobullet";
	div.appendChild(stepsContainer);

    if (typeof stepsValue != "undefined" && ("" + stepsValue) != "") {
		var steps = "" + stepsValue;
		var titles = "" + stepTitles;
		for (var i in steps.split(",")) {
			var step = steps.split(",")[i];
			var stepTitle = titles.split(",")[i];
			var liObj = document.createElement("li");
			stepsContainer.appendChild(liObj);
			var chckboxId = stepsContainer.id + wfIdValue + step;
			var chkbox = document.createElement("input");
			chkbox.type = "checkbox";
			chkbox.value = step;
			chkbox.id = chckboxId;
			chkbox.name = "searchWorkflowStep" + orderNo;
			liObj.appendChild(chkbox);
			chkbox.checked = true;
			var label = document.createElement("label");
			label.setAttribute("style", "padding-left: 5px;");
			label.appendChild(document.createTextNode(stepTitle));
			liObj.appendChild(label);
			label.htmlFor =  chckboxId;
		}
	}
	
	document.getElementById('ss_workflows_options').appendChild(div);

	var textAreaWorkflowsObj = document.createElement('textArea');
	textAreaWorkflowsObj.className = "ss_combobox_autocomplete";
    textAreaWorkflowsObj.name = "searchWorkflow" + orderNo;
    textAreaWorkflowsObj.id = "searchWorkflow" + orderNo;
    textAreaWorkflowsObj.style.width = "200px";
    if (typeof wfTitle != "undefined") {
    	textAreaWorkflowsObj.innerHTML = wfTitle;
    }
	
	wDiv.appendChild(textAreaWorkflowsObj);

    if (typeof wfIdValue != "undefined") {
    	var hiddenTypeObj = document.createElement('input');
    	hiddenTypeObj.type = "hidden";
    	hiddenTypeObj.name = "searchWorkflow" + orderNo + "_initialized";
    	hiddenTypeObj.value = wfIdValue;
    	wDiv.appendChild(hiddenTypeObj);
    }

	var findWorkflows = ssFind.configSingle({
				inputId: "searchWorkflow" + orderNo,
				prefix: "searchWorkflow" + orderNo, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_workflows_search",
	      		listType: "workflows",
				displayValue: true,
				searchOnInitialClick: true,
				appendToSearchUrlRoutine: ss_getSelectedBinders,
				clickRoutine: function () {
					var workflowId = findWorkflows.getSingleId();
					var defaultStepsIds = workflowId == wfIdValue ? stepsValue : [];
					var stepsS = "|" + defaultStepsIds.join("|") + "|";
					stepsContainer.innerHTML = "";
					stepsLabel.style.visibility = "visible";
					dojo.xhrGet({
						url: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_workflow_steps_search&workflowId=" + workflowId,
						load: function(data) { 
							for (var i in data) {
								var liObj = document.createElement("li");
								stepsContainer.appendChild(liObj);
								var chckboxId = stepsContainer.id + workflowId+i;
								var chkbox = document.createElement("input");
								chkbox.type = "checkbox";
								chkbox.value = i;
								chkbox.id = chckboxId;
								chkbox.name = "searchWorkflowStep" + orderNo;
								liObj.appendChild(chkbox);
								if (stepsS.indexOf("|" + i + "|") > -1) {
									chkbox.checked = true;
								}		
								var label = document.createElement("label");
								label.setAttribute("style", "padding-left: 5px;");
								label.appendChild(document.createTextNode(data[i]));
								liObj.appendChild(label);
								label.htmlFor =  chckboxId;
							}
						},
						handleAs: "json-comment-filtered",
						preventCache: true
					});
				},
				clearSubordinates: function () {
					stepsContainer.innerHTML = "";
				},
				displayArrow: true
		});
	
	if (wfIdValue!=null && wfIdValue!="" && typeof ss_searchWorkflows != "undefined"){
		findWorkflows.setValue(wfIdValue, ss_searchWorkflows[wfIdValue]);
		findWorkflows.selectItem({id: wfIdValue});
	}	
	
	var pulldown = document.createElement('img');
	pulldown.style.paddingLeft = "3px";
	pulldown.style.paddingBottom = "3px";
	dojo.connect(pulldown, "onclick", function(event) {
	    if (ss_isIE) {
		    textAreaWorkflowsObj.click();
	    } else {
		    var newEvent = document.createEvent("MouseEvents");
		    newEvent.initEvent("click",true,true);
		    textAreaWorkflowsObj.dispatchEvent(newEvent);
		}
	} );
	pulldown.setAttribute("src", ss_imagesPath + "pics/menudown.gif");
	wDiv.appendChild(pulldown);
	
	
	var brObj = document.createElement('br');
	brObj.setAttribute("style", "clear: both; ");
	div.appendChild(brObj);
	return findWorkflows;
}




function ss_getSelectedBinders(url) {
	var value = "";
	var contextBinderId = "";
	var binderIdObj = document.getElementById('contextBinderId');
	if (binderIdObj !== undefined && binderIdObj != null) {
		contextBinderId = binderIdObj.value;
	}
	var searchScopeCurrent = "";
	var searchScopeCurrentObj = document.getElementById('search_scope_current');
	if (searchScopeCurrentObj !== undefined && searchScopeCurrentObj != null) {
		if (searchScopeCurrentObj.checked) {
			searchScopeCurrent = "&scope=current";
		}	
	} else {
		searchScopeCurrentObj = document.getElementById('search_scope_current_filter');
		if (searchScopeCurrentObj !== undefined && searchScopeCurrentObj != null) {
			//This is a filter form, so set the scope to be current
			searchScopeCurrent = "&scope=current";
		}
	}
	var obj = document.getElementById('search_currentFolder');
	if (obj !== undefined && obj != null && obj.checked) {
		//get current folder
		obj = document.getElementById('search_dashboardFolders');
		return url += "&idChoices=" + encodeURIComponent(" searchFolders_" + obj.value) + "&contextBinderId=" + encodeURIComponent(contextBinderId) + searchScopeCurrent;
	}
	obj = document.getElementById('t_searchForm_wsTreesearchFolders_idChoices');				
	if (obj !== undefined && obj != null) value = obj.value;
	obj = document.getElementById('search_currentAndSubfolders');
	if (obj !== undefined && obj != null && obj.checked) {
		//don't allow duplicates
		var id = " searchFolders_" + obj.name.substr(13);
		var re = new RegExp(id + " ", "g");
		value = value.replace(re, " ");
		re = new RegExp(id + "$", "g");
		value = value.replace(re, "");
		value += id;
	}
	return url += "&idChoices=" + encodeURIComponent(value) + "&contextBinderId=" + encodeURIComponent(contextBinderId) + searchScopeCurrent;
 
}
function ss_addEntry(orderNo, entryId, fieldName, value, valueLabel, valueType, fieldNameTitle, entryType, entryTypeTitle) {
	ss_debug("ss_addEntry: " + fieldName + ", " + value + ", " + valueLabel + ", " + valueType + ", " + fieldNameTitle + ", " + entryType + ", " + entryTypeTitle)
	if (typeof fieldNameTitle == "undefined") {
		fieldNameTitle = fieldName;
	}
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.style.marginBottom = "3px";
	var remover = document.createElement('img');
	dojo.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.png");
	div.appendChild(remover);
	div.appendChild(document.createTextNode(" " + ss_nlt_searchFormLabelEntry + " "));
	
	var entryTypeDiv = document.createElement('div');
	entryTypeDiv.id = "placeholderEntry"+orderNo;
	entryTypeDiv.style.display = "block";
	div.appendChild(entryTypeDiv);

    var fieldsLabelDiv = document.createElement('div');
	fieldsLabelDiv.id = "entryFields"+orderNo+"Label";
	fieldsLabelDiv.setAttribute("style", "display:none;");
	div.appendChild(fieldsLabelDiv);
    	
	var fieldsDiv = document.createElement('div');
	fieldsDiv.id = "entryFields"+orderNo;
	div.appendChild(fieldsDiv);
	
    var valueLabelDiv = document.createElement('div');
	valueLabelDiv.setAttribute("style", "display:none;");
	div.appendChild(valueLabelDiv);


	var fieldValueDiv = document.createElement('div');
	fieldValueDiv.id = "entryFieldsValue"+orderNo;
	div.appendChild(fieldValueDiv);
	
	var fieldValue2Div = document.createElement('div');
	fieldValue2Div.id = "entryFieldsValue2"+orderNo;
	div.appendChild(fieldValue2Div);
		
	var fieldValue3Div = document.createElement('div');
	fieldValue3Div.id = "entryFieldsValue3"+orderNo;
	div.appendChild(fieldValue3Div);

	var valueOptionValue = "";
	if (typeof valueType != "undefined" && (valueType == "date" || valueType == "event")) {
		valueOptionValue = valueLabel;
	} else {
		valueOptionValue = value;
	}
	if (typeof fieldName != "undefined" && fieldName != "") {
		var selectObj = document.createElement("select");
		selectObj.name = "elementName" + orderNo + "_selected";
		selectObj.id = "elementName" + orderNo + "_selected";
		var optionObj = document.createElement("option");
		optionObj.value = fieldName;
		optionObj.selected = true;
		optionObj.innerHTML = fieldNameTitle;
		selectObj.appendChild(optionObj);
		fieldValue2Div.appendChild(selectObj);
		
		if (typeof value != "undefined" && valueOptionValue != "") {
			if (typeof valueType != "undefined" && valueType == "text") {
				// - - - - - - - - - - - - - - - - - - - - - - - - - -
				// Bugzilla 924275 (20150519):  When the value type is
				// 'text', use an <input> instead of a <select>
				// for specifying the value.
				// - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - - -
				// Sample data from the ss_debug(...) on method entry:
				//    fieldName,   value,        valueLabel,    valueType, fieldNameTitle, entryType,                        entryTypeTitle
				//    drfTestText, My Test Text, DRF Test Text, text,      DRF Test Text,  8a80814d4d6d3b6a014d6d7cf4ff0004, DRF Discussion Entry
				// - - - - - - - - - - - - - - - - - - - - - - - - - -
				var inpt = document.createElement('input');
				inpt.type = "text";
				inpt.id = "elementValue" + orderNo + "_selected";
				inpt.name = "elementValue" + orderNo + "_selected";
				if (valueOptionValue) {
					inpt.value = valueOptionValue;
				}
				fieldValue3Div.appendChild(inpt);
			}
			else {
				var selectObj = document.createElement("select");
				selectObj.name = "elementValue" + orderNo + "_selected";
				selectObj.id = "elementValue" + orderNo + "_selected";
				var optionObj = document.createElement("option");
				optionObj.value = valueOptionValue;
				optionObj.selected = true;
				if (typeof valueLabel != "undefined" && valueLabel != "") {
					optionObj.innerHTML = valueLabel;
				} else {
					optionObj.innerHTML = valueOptionValue;
				}
				selectObj.appendChild(optionObj);
				fieldValue3Div.appendChild(selectObj);
			}
		}
	}
	document.getElementById('ss_entries_options').appendChild(div);
	
	var entryInputId = "ss_entry_def_id" + orderNo;
	
	var textAreaEntriesObj = document.createElement('textArea');
	textAreaEntriesObj.className = "ss_combobox_autocomplete";
    textAreaEntriesObj.name = entryInputId;
    textAreaEntriesObj.id = entryInputId;
    textAreaEntriesObj.style.width = "200px";
    if (typeof entryTypeTitle != "undefined") {
    	textAreaEntriesObj.innerHTML = entryTypeTitle;
    }
	entryTypeDiv.appendChild(textAreaEntriesObj);
    if (typeof entryType != "undefined") {
    	var hiddenTypeObj = document.createElement('input');
    	hiddenTypeObj.type = "hidden";
    	hiddenTypeObj.name = entryInputId + "_initialized";
    	hiddenTypeObj.value = entryType;
    	entryTypeDiv.appendChild(hiddenTypeObj);
    }
	
	var findEntries = ssFind.configSingle({
		inputId: entryInputId,
		prefix: entryInputId, 
		searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_entry_types_search",
  		listType: "entry_fields",
		displayValue: true,
		displayArrow: true,
		searchOnInitialClick: true,
		appendToSearchUrlRoutine: ss_getSelectedBinders,
		clickRoutine: function () {
			ss_removeAllChildren(fieldsDiv);
			ss_removeAllChildren(fieldValueDiv);
			ss_removeAllChildren(fieldValue2Div);
			ss_removeAllChildren(fieldValue3Div);

            //fieldsLabelDiv.style.display = "inline";
			
			var entryTypeId = findEntries.getSingleId();
			var fieldsInputId = "elementName" + orderNo;
			
			var textAreaFieldsObj = document.createElement('textArea');
			textAreaFieldsObj.className = "ss_combobox_autocomplete";
		    textAreaFieldsObj.name = fieldsInputId;
		    textAreaFieldsObj.id = fieldsInputId;
		    textAreaFieldsObj.style.width = "200px";
			
			fieldsDiv.appendChild(textAreaFieldsObj);

			var findEntryFields = ssFind.configSingle({
				inputId: fieldsInputId,
				prefix: fieldsInputId, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_entry_fields_search&ss_entry_def_id=" + entryTypeId,
	      		listType: "entry_fields",
				displayValue: true,
				displayArrow: true,
				searchOnInitialClick: true,
				clickRoutine: function () {
				    //valueLabelDiv.style.display = "inline";
					var fieldValueWidget = dijit.byId("elementValue" + orderNo + "_selected");
					if (fieldValueWidget && fieldValueWidget.destroy) {
						fieldValueWidget.destroy();
					}
					var fieldValue2Widget = dijit.byId("elementValue" + orderNo + "_selected" + "0");
					if (fieldValue2Widget && fieldValue2Widget.destroy) {
						fieldValue2Widget.destroy();
					}

					var fieldValue3Widget = dijit.byId("elementValue" + orderNo + "_selected" + "0");
					if (fieldValue3Widget && fieldValue3Widget.destroy) {
						fieldValue3Widget.destroy();
					}

					ss_removeAllChildren(fieldValueDiv);
					ss_removeAllChildren(fieldValue2Div);
					ss_removeAllChildren(fieldValue3Div);
			
					var currentFieldId = findEntryFields.getSingleId();
					var currentFieldName = findEntryFields.getSingleValue();
					var fieldType = findEntryFields.getSingleType();
					
					if (fieldType == "date" || fieldType == "event") {
						var dateValue = "";
						if (entryId && fieldName && entryTypeId == entryId && fieldName == currentFieldId) {
							dateValue = new Date(value);
						}
						addDateField(orderNo, fieldValueDiv, currentFieldId, fieldName, dateValue);
					} else if (fieldType == "date_time") {
						var dateValue = "";
						if (entryId && fieldName && entryTypeId == entryId && fieldName == currentFieldId) {
							dateValue = new Date(value);
						}
						addDateTimeField(orderNo, fieldValueDiv, fieldValue2Div, currentFieldId, fieldName, dateValue);
					} else if (fieldType == "user_list" || fieldType == "userListSelectbox") {
						var idToSet = false;
						var labelToSet = false;
						if (entryId && fieldName && entryTypeId == entryId && fieldName == currentFieldId) {
							idToSet = value;
							labelToSet= valueLabel;
						}
						addUserListField(orderNo, fieldValueDiv, currentFieldId, fieldName, idToSet, labelToSet);					
					} else if (fieldType == "group_list") {
						var idToSet = false;
						var labelToSet = false;
						if (entryId && fieldName && entryTypeId == entryId && fieldName == currentFieldId) {
							idToSet = value;
							labelToSet= valueLabel;
						}
						addGroupListField(orderNo, fieldValueDiv, currentFieldId, fieldName, idToSet, labelToSet);					
					} else if (fieldType == "team_list") {
						var idToSet = false;
						var labelToSet = false;
						if (entryId && fieldName && entryTypeId == entryId && fieldName == currentFieldId) {
							idToSet = value;
							labelToSet= valueLabel;
						}
						addTeamListField(orderNo, fieldValueDiv, currentFieldId, fieldName, idToSet, labelToSet);					
					} else if (fieldType == "checkbox" || fieldType == "radio" || fieldType == "selectbox") {
						var idToSet = false;
						var labelToSet = false;
						if (entryId && fieldName && entryTypeId == entryId && fieldName == currentFieldId) {
							idToSet = value;
							labelToSet= valueLabel;
						}
						dojo.xhrGet({
					    	url: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_entry_fields_search&ss_entry_def_id=" + entryTypeId + "&elementName=" + currentFieldId,
							load: function (data) {
								var selectObj = document.createElement("select");
								selectObj.name = "elementValue" + orderNo + "_selected";
								selectObj.id = "elementValue" + orderNo + "_selected";
								for (var i in data) {
									var optionObj = document.createElement("option");
									optionObj.value = i;
									optionObj.selected = (idToSet == i);
									optionObj.innerHTML = data[i];
									selectObj.appendChild(optionObj);
								}
								fieldValue2Div.appendChild(selectObj);
							},
							handleAs: "json-comment-filtered",
							preventCache: true
						});
					} else if (fieldType == "entryAttributes") {
						var textAreaAttributesFieldsObj = document.createElement('textArea');
						textAreaAttributesFieldsObj.className = "ss_combobox_autocomplete";
					    textAreaAttributesFieldsObj.name = "elementValue" + orderNo;
					    textAreaAttributesFieldsObj.id = "elementValue" + orderNo;
					    textAreaAttributesFieldsObj.style.width = "150px";
						
						fieldValue2Div.appendChild(textAreaAttributesFieldsObj);
						
						var findAttributeFields = ssFind.configSingle({
							inputId: "elementValue" + orderNo,
							prefix: "elementValue" + orderNo, 
							searchUrl: ss_AjaxBaseUrl + "&action=advanced_search&operation=get_entry_attributes_widget&elementName="+currentFieldId+"&ss_entry_def_id="+entryTypeId,
				      		listType: "entry_fields",
							displayValue: true,
							displayArrow: true,
							searchOnInitialClick: true,
							clickRoutine: function () {
							    //valueLabelDiv.style.display = "inline";
								var fieldValueWidget = dijit.byId("elementValue" + orderNo + "_selected");
								if (fieldValueWidget && fieldValueWidget.destroy) {
									fieldValueWidget.destroy();
								}
								var fieldValue2Widget = dijit.byId("elementValue" + orderNo + "_selected" + "0");
								if (fieldValue2Widget && fieldValue2Widget.destroy) {
									fieldValue2Widget.destroy();
								}
			
								var fieldValue3Widget = dijit.byId("elementValue" + orderNo + "_selected" + "0");
								if (fieldValue3Widget && fieldValue3Widget.destroy) {
									fieldValue3Widget.destroy();
								}
			
								ss_removeAllChildren(fieldValueDiv);
								ss_removeAllChildren(fieldValue2Div);
								ss_removeAllChildren(fieldValue3Div);
						
								var currentFieldId = findEntryFields.getSingleId();
								var currentFieldName = findEntryFields.getSingleValue();
								var fieldType = findEntryFields.getSingleType();
					
								var textAreaAttributesFieldsObj = document.createElement('textArea');
								textAreaAttributesFieldsObj.className = "ss_combobox_autocomplete";
							    textAreaAttributesFieldsObj.name = "elementValue" + orderNo;
							    textAreaAttributesFieldsObj.id = "elementValue" + orderNo;
							    textAreaAttributesFieldsObj.style.width = "150px";
								
								fieldValue3Div.appendChild(textAreaAttributesFieldsObj);
								
								dojo.xhrGet({
							    	url: ss_AjaxBaseUrl + "&action=advanced_search&operation=get_entry_attributes_value_widget&ss_entry_def_id=" + entryTypeId + "&elementName=" + currentFieldId,
									load: function (data) {
										var selectObj = document.createElement("select");
										selectObj.name = "elementValue" + orderNo + "_selected";
										selectObj.id = "elementValue" + orderNo + "_selected";
										for (var i in data) {
											var optionObj = document.createElement("option");
											optionObj.value = i;
											optionObj.selected = (idToSet == i);
											optionObj.innerHTML = data[i];
											selectObj.appendChild(optionObj);
										}
										fieldValue3Div.appendChild(selectObj);
									},
									handleAs: "json-comment-filtered",
									preventCache: true
								});
							}
						});
							
					} else {// TODO: entryAttributes field!!
						var idToSet = false;
						var labelToSet = false;					
						if (entryId && fieldName && entryTypeId == entryId && fieldName == currentFieldId) {
							idToSet = value;
							labelToSet= valueLabel;
						}
						var inpt = document.createElement('input');
						inpt.type = "text";
						inpt.id = "elementValue" + orderNo + "_selected";
						inpt.name = "elementValue" + orderNo + "_selected";
						if (labelToSet) {
							inpt.value = labelToSet;
						}
						fieldValue2Div.appendChild(inpt);
					}
					
				},
				clearSubordinates: function() {
					ss_removeAllChildren(fieldValueDiv);
					ss_removeAllChildren(fieldValue2Div);
					ss_removeAllChildren(fieldValue3Div);
				    valueLabelDiv.style.display = "none";
				}
			});
			
			if (entryId && fieldName && entryTypeId == entryId) {
				findEntryFields.setValue(fieldName, ss_searchFields[entryId+"-"+fieldName], ss_searchFieldsTypes[entryId+"-"+fieldName]);
				findEntryFields.selectItem({id: fieldName});
			}


			var pulldown = document.createElement('img');
			pulldown.style.paddingLeft = "3px";
			pulldown.style.paddingBottom = "3px";
			dojo.connect(pulldown, "onclick", function(event) {
			    if (ss_isIE) {
				    textAreaFieldsObj.click();
			    } else {
				    var newEvent = document.createEvent("MouseEvents");
				    newEvent.initEvent("click",true,true);
				    textAreaFieldsObj.dispatchEvent(newEvent);
				}
			} );
			pulldown.setAttribute("src", ss_imagesPath + "pics/menudown.gif");
			fieldsDiv.appendChild(pulldown);



		},
		clearSubordinates: function() {
			ss_removeAllChildren(fieldsDiv);
			ss_removeAllChildren(fieldValueDiv);
			ss_removeAllChildren(fieldValue2Div);
			ss_removeAllChildren(fieldValue3Div);
            fieldsLabelDiv.style.display = "none";
		    valueLabelDiv.style.display = "none";
		}		
	});
	
	if (entryId && typeof ss_searchEntries != "undefined") {
		findEntries.setValue(entryId, ss_searchEntries[entryId]);
		findEntries.selectItem({id: entryId});
		// , fieldName, ss_searchFields[entryId+"-"+fieldName], 
		// value, ss_searchFieldsTypes[entryId+"-"+fieldName], valueLabel);
	}	


	var pulldown = document.createElement('img');
	pulldown.style.paddingLeft = "3px";
	pulldown.style.paddingBottom = "3px";
	dojo.connect(pulldown, "onclick", function(event) {
	    if (ss_isIE) {
		    textAreaEntriesObj.click();
	    } else {
		    var newEvent = document.createEvent("MouseEvents");
		    newEvent.initEvent("click",true,true);
		    textAreaEntriesObj.dispatchEvent(newEvent);
		}
	} );
	pulldown.setAttribute("src", ss_imagesPath + "pics/menudown.gif");
	entryTypeDiv.appendChild(pulldown);

	
/*
	var properties = {name:"ss_entry_def_id"+orderNo+"", id:"ss_entry_def_id"+orderNo+"", 
		getSubSearchString:ss_getSelectedBinders,
		dataUrl:ss_AjaxBaseUrl+"&action=advanced_search&operation=get_entry_types_widget&idChoices=%{searchString}&randomNumber="+ss_random++, 
		nestedUrl:ss_AjaxBaseUrl+"&action=advanced_search&operation=get_entry_fields_widget&randomNumber="+ss_random++, 
		widgetContainer:sDiv, widgetContainer2:sDiv2, searchFieldIndex:orderNo, mode: "remote",
		maxListLength : 10,	autoComplete: false, weekStartsOn: ss_weekStartsOn};
	var entryWidget = dojox.widget.createWidget("EntrySelect", properties, document.getElementById("placeholderEntry"+orderNo+""));
	if (entryId && entryId != "") {
		entryWidget.setDefaultValue(entryId, ss_searchEntries[entryId], fieldName, ss_searchFields[entryId+"-"+fieldName], value, ss_searchFieldsTypes[entryId+"-"+fieldName], valueLabel);
	}
*/	
}

function addDateField(orderNo, container, fieldId, fieldName, value) {
	var localContainer = document.createElement("input");
	localContainer.type = "text";
	container.appendChild(localContainer);
	
	return [new dijit.form.DateTextBox({value: value, 
								id: "elementValue" + orderNo + "_selected", 
								name: "elementValue" + orderNo + "_selected",
								autoComplete: false}, 
							localContainer)];
}

function addDateTimeField(orderNo, dateContainer, timeContainer, fieldId, fieldName, value) {
	var widgets = [];
	widgets.push(addDateField(orderNo, dateContainer, fieldId, fieldName, value));
	
	var localContainer = document.createElement("input");
	localContainer.type = "text";
	timeContainer.appendChild(localContainer);
	
	widgets.push(new dijit.form.TimeTextBox({value: value, 
								id: "elementValue" + orderNo + "_selected" + "0",
								name: "elementValue" + orderNo + "_selected" + "0",
								autoComplete: false}, 
							localContainer));
	return widgets;
}

function addUserListField(orderNo, container, fieldId, fieldName, id, name) {
	var textAreaUserListObj = document.createElement('textArea');
	textAreaUserListObj.className = "ss_combobox_autocomplete";
    textAreaUserListObj.name = "elementValue" + orderNo;
    textAreaUserListObj.id = "elementValue" + orderNo;
    textAreaUserListObj.style.width = "150px";
	
	container.appendChild(textAreaUserListObj);
	
	var findUsers = ssFind.configSingle({
				inputId: "elementValue" + orderNo,
				prefix: "elementValue" + orderNo, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_user_search",
	      		listType: "user",
				displayValue: true,
				displayArrow: true,
				addCurrentUserToResult: true
		});
	
	if (id && name) {
		findUsers.setValue(id, name);
	}	
}

function addGroupListField(orderNo, container, fieldId, fieldName, id, name) {
	var textAreaUserListObj = document.createElement('textArea');
	textAreaUserListObj.className = "ss_combobox_autocomplete";
    textAreaUserListObj.name = "elementValue" + orderNo;
    textAreaUserListObj.id = "elementValue" + orderNo;
	
	container.appendChild(textAreaUserListObj);
	
	var findGroups = ssFind.configSingle({
				inputId: "elementValue" + orderNo,
				prefix: "elementValue" + orderNo, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_user_search",
	      		listType: "group",
				displayValue: true,
				displayArrow: true
		});
	
	if (id && name) {
		findGroups.setValue(id, name);
	}	
}

function addTeamListField(orderNo, container, fieldId, fieldName, id, name) {
	var textAreaUserListObj = document.createElement('textArea');
	textAreaUserListObj.className = "ss_combobox_autocomplete";
    textAreaUserListObj.name = "elementValue" + orderNo;
    textAreaUserListObj.id = "elementValue" + orderNo;
	
	container.appendChild(textAreaUserListObj);
	
	var findTeams = ssFind.configSingle({
				inputId: "elementValue" + orderNo,
				prefix: "elementValue" + orderNo, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_user_search",
	      		listType: "teams",
				displayValue: true,
				displayArrow: true
		});
	
	if (id && name) {
		findTeams.setValue(id, name);
	}	
}

function ss_addTag(orderNo, communityTagValue, personalTagValue) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.png");
	div.appendChild(remover);

	var pDiv = document.createElement('div');
	pDiv.id = "placeholderPersonal"+orderNo;
	pDiv.style.display = "inline";
	
	var cDiv = document.createElement('div');
	cDiv.id = "placeholderCommunity"+orderNo;
	cDiv.style.display = "inline";

	div.appendChild(document.createTextNode(" " + ss_nlt_tagsCommunityTags + ": "));
	div.appendChild(cDiv);
	div.appendChild(document.createTextNode(" " + ss_nlt_tagsPersonalTags + ": "));
	div.appendChild(pDiv);
	document.getElementById('ss_tags_options').appendChild(div);

	var textAreaCommunityTagsObj = document.createElement('textArea');
	textAreaCommunityTagsObj.className = "ss_combobox_autocomplete";
    textAreaCommunityTagsObj.name = "searchCommunityTags" + orderNo;
    textAreaCommunityTagsObj.id = "searchCommunityTags" + orderNo;
	
	cDiv.appendChild(textAreaCommunityTagsObj);
	
	var findCommunityTags = ssFind.configSingle({
				inputId: "searchCommunityTags" + orderNo,
				prefix: "searchCommunityTags" + orderNo, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_tag_search", 
	      		listType: "communityTags",
				displayValueOnly: true,
				displayArrow: true
		});
	if (communityTagValue && communityTagValue != "") {
		findCommunityTags.setValue(communityTagValue, communityTagValue);
	}
	
	var textAreaPersonalTagsObj = document.createElement('textArea');
	textAreaPersonalTagsObj.className = "ss_combobox_autocomplete";
    textAreaPersonalTagsObj.name = "searchPersonalTags" + orderNo;
    textAreaPersonalTagsObj.id = "searchPersonalTags" + orderNo;
	
	pDiv.appendChild(textAreaPersonalTagsObj);
	
	var findPersonalTags = ssFind.configSingle({
				inputId: "searchPersonalTags" + orderNo,
				prefix: "searchPersonalTags" + orderNo, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_tag_search", 
	      		listType: "personalTags",
				displayValueOnly: true,
				displayArrow: true
		});
	if (personalTagValue && personalTagValue != "") {
		findPersonalTags.setValue(personalTagValue, personalTagValue);
	}
}

function ss_addAuthor(orderNo, authorId, authorName) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.png");
	div.appendChild(remover);

	var aDiv = document.createElement('div');
	aDiv.id = "placeholderAuthor"+orderNo;
	aDiv.style.display = "inline";

	div.appendChild(document.createTextNode(" " + ss_searchFormLabelAuthor + " "));
	div.appendChild(aDiv);
	document.getElementById('ss_authors_options').appendChild(div);
	
	
	var textAreaAuthorObj = document.createElement('textArea');
	textAreaAuthorObj.className = "ss_combobox_autocomplete";
    textAreaAuthorObj.name = "searchAuthors" + orderNo;
    textAreaAuthorObj.id = "searchAuthors" + orderNo;
    textAreaAuthorObj.style.width = "150px";
	
	aDiv.appendChild(textAreaAuthorObj);
	
	var findAuthors = ssFind.configSingle({
				inputId: "searchAuthors" + orderNo,
				prefix: "searchAuthors" + orderNo, 
				searchUrl: ss_AjaxBaseUrl + "&action=__ajax_find&operation=find_user_search",
	      		listType: "user",
				displayValue: true,
				displayArrow: true,
				addCurrentUserToResult: true
		});
	
	if (authorId && authorName) {
		findAuthors.setValue(authorId, authorName);
	}	
}

function ss_addLastActivity(orderNo, initialDaysNumber) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.appendChild(document.createTextNode(" " + ss_searchFormLabelLastActivity + " "));
	
	var selectBox = document.createElement('select');
	selectBox.name="searchDaysNumber"+orderNo;
	selectBox.id="searchDaysNumber"+orderNo;	
	var option = document.createElement('option');
	option.value = 0;
	option.appendChild(document.createTextNode(ss_days_0));
	selectBox.appendChild(option);
	option = document.createElement('option');
	option.value = 1;
	if (initialDaysNumber && initialDaysNumber==1) option.selected=true; 
	option.appendChild(document.createTextNode(ss_days_1));
	selectBox.appendChild(option);
	option = document.createElement('option');
	option.value = 3;
	if (initialDaysNumber && initialDaysNumber==3) option.selected=true; 
	option.appendChild(document.createTextNode(ss_days_3));
	selectBox.appendChild(option);
	option = document.createElement('option');
	option.value = 7;
	if (initialDaysNumber && initialDaysNumber==7) option.selected=true; 
	option.appendChild(document.createTextNode(ss_days_7));
	selectBox.appendChild(option);
	option = document.createElement('option');
	option.value = 30;
	if (initialDaysNumber && initialDaysNumber==30) option.selected=true; 
	option.appendChild(document.createTextNode(ss_days_30));
	selectBox.appendChild(option);
	option = document.createElement('option');
	option.value = 90;
	if (initialDaysNumber && initialDaysNumber==90) option.selected=true; 
	option.appendChild(document.createTextNode(ss_days_90));
	selectBox.appendChild(option);
	
	div.appendChild(selectBox);
	document.getElementById('ss_lastActivities_options').appendChild(div);	
}

function ss_addDate(orderNo, type, startDate, endDate) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.png");
	div.appendChild(remover);
	div.appendChild(document.createTextNode(" " + ss_searchFormLabelDate + ": "));
	
	var placeholderStartDateObj = document.createElement('input');
	placeholderStartDateObj.id = "placeholderStartDate"+orderNo;
	placeholderStartDateObj.type = "text"
	div.appendChild(placeholderStartDateObj);
	
	var placeholderEndDateObj = document.createElement('input');
	placeholderEndDateObj.type = "text";
	placeholderEndDateObj.id = "placeholderEndDate"+orderNo;
	div.appendChild(placeholderEndDateObj);
	
	if (type == 'creation')	document.getElementById('ss_creationDates_options').appendChild(div);
	else document.getElementById('ss_modificationDates_options').appendChild(div);
	if (!startDate)
		startDate = null; 

	if (!endDate)
		endDate = null;
	
	var startDateWidget = new dijit.form.DateTextBox({value: ss_parseSimpleDate(startDate), 
								weekStartsOn: ss_weekStartsOn,
								id:'searchStartDate'+orderNo, 
								name:'searchStartDate'+orderNo,
								maxListLength : 10,	
								autoComplete: false}, 
							placeholderStartDateObj);
							
	var endDateWidget = new dijit.form.DateTextBox({value: ss_parseSimpleDate(endDate), 
								id:'searchEndDate'+orderNo, 
								name:'searchEndDate'+orderNo,
								maxListLength : 10,	autoComplete: false}, 
							placeholderEndDateObj);
}

/* check/uncheck checkboxes in tree on click in the place name */
function t_advSearchForm_wsTree_showId(id, obj) {
	return ss_checkTree(obj, "ss_tree_checkboxt_searchForm_wsTreesearchFolders" + id)
}

function ss_addFolderAfterPost(response, bindObjId) {
	var bindObj = document.getElementById(bindObjId);
	if (bindObj) {
		bindObj.innerHTML = response;
	}
}

function ss_removeOption(orderNo) {
	ss_optionsArray[orderNo]="";
	var parent = document.getElementById('block'+orderNo).parentNode;
	parent.removeChild(document.getElementById('block'+orderNo));
}

function ss_search() {
	ss_prepareAdditionalSearchOptions(document.getElementById('ss_advSearchForm'));
	document.getElementById('ss_advSearchForm').submit();
}

function ss_removeAllChildren(domObj) {
	if (domObj && domObj.hasChildNodes()) {
	    while (domObj.childNodes.length >= 1 ) {
	        domObj.removeChild( domObj.firstChild );       
	    } 
	}
}



function ss_searchToggleFolders(objId, selection) {
	var obj = document.getElementById(objId);
	if (obj && obj.style) {
		if (selection == "dashboard") {
			obj.style.visibility="hidden";
			obj.style.display="none";
		} else {
			obj.style.visibility="visible";
			obj.style.display="block";
		}
	}
}

function ss_searchSetCheckbox(obj, name) {
	if (obj.checked) {
		var formObj = ss_getContainingForm(obj)
		formObj[name].checked = true;
	}
}

function ss_showAdditionalOptions(objId, txtContainerId, namespace) {
	console.log("ss_showAdditionalOptions");
}

function ss_loadSearchOptions(namespace) {
	var ssSearchParseAdvancedFormInputObj = document.getElementById("ssSearchParseAdvancedForm" + namespace);
	if (ssSearchParseAdvancedFormInputObj) {
		ssSearchParseAdvancedFormInputObj.value = "true";
	}
	if (!ss_searchMoreInitialized) {
		ss_initSearchOptions();
	}
}


function ss_showHideDetails(ind){
	ss_showHide("summary_"+ind);
	ss_showHide("details_"+ind);
}



function ss_fillSearchMask(id, value) { 
	if (document.getElementById(id)) document.getElementById(id).value = value;
}

function ss_goToSearchResultPage(ind) {
	var url=ss_AdvancedSearchURL;
	url = url + "&pageNumber=" + ind;
	document.location.href = url;
}

function ss_goToSearchResultPageByInputValue(inputId) {
	var inputObj = document.getElementById(inputId);
	if (!inputObj) {
		return;
	}
	var ind = inputObj.value;
	var url=ss_AdvancedSearchURL;
	url = url + "&pageNumber=" + ind;
	document.location.href = url;
}

function ss_prepareAdditionalSearchOptions(formObj) {
	ss_debug("ss_prepareAdditionalSearchOptions ss_userOptionsCounter = " + ss_userOptionsCounter)
	var numbers = new Array();
	var types = new Array();
	for (var i=0; i<ss_userOptionsCounter; i++) {
		if (ss_optionsArray[i] != "") {
			numbers[numbers.length] = i;
			types[types.length] = ss_optionsArray[i];
			ss_debug("types: " +ss_optionsArray[i])
		}
	}
	document.getElementById("searchNumbers").value = numbers.join(" ");
	document.getElementById("searchTypes").value = types.join(" ");
	if (typeof formObj != "undefined") {
		formObj.action = ss_getSelectedBinders(formObj.action);
	}
	return true;
}

function ss_saveSearchQuery(inputId, errMsgBoxId) {
	var inputObj = document.getElementById(inputId);
	if (!inputObj) {
		return;
	}
	var queryName = inputObj.value;
	if (typeof queryName == "undefined" || !queryName || ss_trim(queryName) == "" || 
			queryName == ss_searchResultSavedSearchInputLegend) {
		var errMsgBoxObj = document.getElementById(errMsgBoxId);		
		errMsgBoxObj.innerHTML = ss_noNameMsg;
		ss_showDiv(errMsgBoxId);
		inputObj.focus();
		return;
	} else {
		ss_hideDiv(errMsgBoxId);
	}
	var pattern = new RegExp("[^\\w \\.]");
	if (pattern.test(queryName) ) {
		var errMsgBoxObj = document.getElementById(errMsgBoxId);		
		errMsgBoxObj.innerHTML = ss_invalidNameMsg;
		ss_showDiv(errMsgBoxId);
		inputObj.focus();
		return;
	}

	if (!ss_nameAlreadyInUse(queryName) || (ss_overwrite(queryName))) {
		var urlParams = {operation:"save_search_query", queryName:queryName, tabId:ss_currentTabId};
		var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
		ss_get_url(url, ss_addSavedSearchToView);
	}
}
function ss_callRemoveSavedQuery(queryName, errMsg, objToRemove) { 
	return function(evt) {ss_removeSavedSearchQuery(queryName, errMsg, objToRemove);};
}

function ss_addSavedSearchToView(data) {
	if (data.savedQueryName && !ss_nameAlreadyInUse(data.savedQueryName)) {
		var savedQueriesList = document.getElementById("ss_savedQueriesList");	
		
		if (savedQueriesList && !hasListElements(savedQueriesList)) {
			savedQueriesList.innerHTML = "";
		}
		
		var newLi = document.createElement("li");
	
		var removerLink = document.createElement('a');
		removerLink.href = "javascript: //;";
		dojo.connect(removerLink, "onclick", ss_callRemoveSavedQuery(data.savedQueryName,'ss_saveQueryErrMsg', newLi));
		var removerImg = document.createElement('img');
		removerImg.setAttribute("src", ss_imagesPath + "pics/delete.png");
		removerLink.appendChild(removerImg);
		
		var queryLink = document.createElement("a");
		queryLink.href = ss_AdvancedSearchURLNoOperation + "&operation=ss_savedQuery&ss_queryName=" + data.savedQueryName;
		queryLink.innerHTML = data.savedQueryName;
		
		newLi.appendChild(removerLink);
		newLi.appendChild(document.createTextNode(" "))
		newLi.appendChild(queryLink);
		
		savedQueriesList.appendChild(newLi);
		ss_addToSaved(data.savedQueryName);
	}
}

function hasListElements(htmlObj) {
	if (!htmlObj) {
		return false;
	}
	for (var i = 0; i < htmlObj.childNodes.length; i++) {
		if (htmlObj.childNodes[i].tagName == "LI") {
			return true;
		}
	}
	return false;
}


function ss_removeSavedSearchQuery(queryName, errMsgBoxId, objToRemove) {
	if (!queryName) {
		return;
	}
	var urlParams = {operation:"remove_search_query", queryName:queryName};
	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, urlParams);
	ss_get_url(url, ss_removeSavedSearchFromView, objToRemove);
}

function ss_removeSavedSearchFromView(data, objToRemove) {
	if (data.removedQueryName) {
		ss_removeFromSaved(data.removedQueryName);
		objToRemove.parentNode.removeChild(objToRemove);
	}
}

var ss_savedQueries = "|";

function ss_addToSaved(queryName) {
	if (!ss_nameAlreadyInUse(queryName)) {
		ss_savedQueries = ss_savedQueries+queryName+"|";
	}
}
function ss_overwrite(queryName) {
	var answer = confirm(ss_overwriteQuestion);
	if (answer)	return true;
	else return false;
}
function ss_removeFromSaved(queryName){
	var extendedName = "|"+queryName+"|";
	ss_savedQueries = ss_savedQueries.replace(extendedName, "|");
}
function ss_nameAlreadyInUse(queryName) {
	if (ss_savedQueries.indexOf("|"+queryName+"|")>-1) {
		return true;
	} else {
		return false;
	}
}

function ss_parseSimpleDate(date) {
	if (!date) {
		return date;
	}
	
	if (date.length != 10) {
		return date;
	}
	var y = date.substr(0, 4) * 1;
	var m = date.substr(5, 2) * 1 - 1;
	var d = date.substr(8, 2) * 1;
	return new Date(y, m, d);
}
