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


var ss_userOptionsCounter = 0;
var ss_optionsArray = new Array();
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


function ss_addInitializedWorkflow(wfIdValue, stepsValue) {
	ss_optionsArray[ss_userOptionsCounter]='workflow';
	var wfWidget = ss_addWorkflow(ss_userOptionsCounter, wfIdValue, stepsValue);
	ss_userOptionsCounter++;
}

function ss_addInitializedEntry(entryId, fieldName, value, valueLabel) {
	ss_optionsArray[ss_userOptionsCounter]='entry';
	ss_addEntry(ss_userOptionsCounter, entryId, fieldName, value, valueLabel);
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

function ss_addWorkflow(orderNo, wfIdValue, stepsValue) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	
	var workflowsContainer = document.createElement('div');
	workflowsContainer.setAttribute("style", "display:inline; float: left;");
	div.appendChild(workflowsContainer);
	
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.gif");
	workflowsContainer.appendChild(remover);
	workflowsContainer.appendChild(document.createTextNode(" " + ss_nlt_searchFormLabelWorkflow + ": "));

	var wDiv = document.createElement('div');
	wDiv.id = "placeholderWorkflow"+orderNo;
	workflowsContainer.appendChild(wDiv);
	var sDiv = document.createElement('ul');
	sDiv.id = "workflowSteps"+orderNo;
	sDiv.setAttribute("style", "display:inline; float: left; padding-left: 5px; ");
	div.appendChild(sDiv);
	document.getElementById('ss_workflows_options').appendChild(div);
		
	var properties = {name:"searchWorkflow"+orderNo+"", id:"searchWorkflow"+orderNo+"", dataUrl:ss_AjaxBaseUrl+"&action=advanced_search&operation=get_workflows_widget&randomNumber="+ss_random++, nestedUrl:ss_AjaxBaseUrl+"&action=advanced_search&operation=get_workflow_step_widget&randomNumber="+ss_random++, stepsWidget:sDiv, searchFieldName:"searchWorkflowStep"+orderNo, mode: "remote",
								maxListLength : 10,	autoComplete: false};
	var wfWidget = dojo.widget.createWidget("WorkflowSelect", properties, document.getElementById("placeholderWorkflow"+orderNo+""));

	if (wfIdValue!=null && wfIdValue!=""){
		wfWidget.setDefaultValue(wfIdValue, ss_searchWorkflows[wfIdValue], stepsValue);
	}
	
	var brObj = document.createElement('br');
	brObj.setAttribute("style", "clear: both; ");
	div.appendChild(brObj);
	
	return wfWidget;
}

function ss_addEntry(orderNo, entryId, fieldName, value, valueLabel) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.gif");
	div.appendChild(remover);
	div.appendChild(document.createTextNode(" " + ss_nlt_searchFormLabelEntry + ": "));
	
	var eDiv = document.createElement('div');
	eDiv.id = "placeholderEntry"+orderNo;
	div.appendChild(eDiv);
	var sDiv = document.createElement('div');
	sDiv.id = "entryFields"+orderNo;
	sDiv.setAttribute("style", "display:inline;");
	div.appendChild(sDiv);
	document.getElementById('ss_entries_options').appendChild(div);

	var properties = {name:"ss_entry_def_id"+orderNo+"", id:"ss_entry_def_id"+orderNo+"", dataUrl:ss_AjaxBaseUrl+"&action=advanced_search&operation=get_entry_types_widget&randomNumber="+ss_random++, nestedUrl:ss_AjaxBaseUrl+"&action=advanced_search&operation=get_entry_fields_widget&randomNumber="+ss_random++, widgetContainer:sDiv, searchFieldIndex:orderNo, mode: "remote",
								maxListLength : 10,	autoComplete: false, weekStartsOn: ss_weekStartsOn};
	var entryWidget = dojo.widget.createWidget("EntrySelect", properties, document.getElementById("placeholderEntry"+orderNo+""));
	if (entryId && entryId != "") {
		entryWidget.setDefaultValue(entryId, ss_searchEntries[entryId], fieldName, ss_searchFields[entryId+"-"+fieldName], value, ss_searchFieldsTypes[entryId+"-"+fieldName], valueLabel);
	}
}

function ss_addTag(orderNo, communityTagValue, personalTagValue) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.gif");
	div.appendChild(remover);

	var pDiv = document.createElement('div');
	pDiv.id = "placeholderPersonal"+orderNo;
	var cDiv = document.createElement('div');
	cDiv.id = "placeholderCommunity"+orderNo;

	div.appendChild(document.createTextNode(" " + ss_nlt_tagsCommunityTags + ": "));
	div.appendChild(cDiv);
	div.appendChild(document.createTextNode(" " + ss_nlt_tagsPersonalTags + ": "));
	div.appendChild(pDiv);
	document.getElementById('ss_tags_options').appendChild(div);
	
	var url = ss_AjaxBaseUrl + "&action=advanced_search&operation=get_tags_widget&searchText=%{searchString}&pager=%{pagerString}&randomNumber="+ss_random++;
	var propertiesCommunity = {name:"searchCommunityTags"+orderNo+"", 
								id:"searchCommunityTags"+orderNo+"", 
								dataUrl:url+"&findType=communityTags", 								
								maxListLength : 12,	autoComplete: false};
	var propertiesPersonal = {name:"searchPersonalTags"+orderNo+"", 
								id:"searchPersonalTags"+orderNo+"", 
								dataUrl:url+"&findType=personalTags", 
								maxListLength : 12,	autoComplete: false};
	var communityTagWidget = dojo.widget.createWidget("SelectPageable", propertiesCommunity, document.getElementById("placeholderCommunity"+orderNo+""));
	var personalTagWidget = dojo.widget.createWidget("SelectPageable", propertiesPersonal, document.getElementById("placeholderPersonal"+orderNo+""));
	if (communityTagValue && communityTagValue != "") {
		communityTagWidget.setValue(communityTagValue);
		communityTagWidget.setLabel(communityTagValue);
	}
	if (personalTagValue && personalTagValue != "") {
		personalTagWidget.setValue(personalTagValue);
		personalTagWidget.setLabel(personalTagValue);
	}
}

function ss_addAuthor(orderNo, authorId, authorName) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.gif");
	div.appendChild(remover);

	var aDiv = document.createElement('div');
	aDiv.id = "placeholderAuthor"+orderNo;

	div.appendChild(document.createTextNode(" " + ss_searchFormLabelAuthor + ": "));
	div.appendChild(aDiv);
	document.getElementById('ss_authors_options').appendChild(div);
	
	var url = ss_AjaxBaseUrl + "&action=advanced_search&operation=get_users_widget&searchText=%{searchString}&pager=%{pagerString}&randomNumber="+ss_random++;
	var props = {name : "searchAuthors"+orderNo+"", 
					id : "searchAuthors"+orderNo+"", 
					dataUrl:url,
					maxListLength : 12,
					autoComplete: false};
	var usersWidget = dojo.widget.createWidget("SelectPageable", props, document.getElementById("placeholderAuthor"+orderNo+""));
	if (authorId && authorName && authorId!="" && authorName!="") {
		usersWidget.setValue(authorId);
		usersWidget.setLabel(authorName);
	}
}

function ss_addLastActivity(orderNo, initialDaysNumber) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.appendChild(document.createTextNode(" " + ss_searchFormLabelLastActivity + ": "));
	
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
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", ss_imagesPath + "pics/delete.gif");
	div.appendChild(remover);
	div.appendChild(document.createTextNode(" " + ss_searchFormLabelDate + ": "));
	
	var sdDiv = document.createElement('div');
	sdDiv.id = "placeholderStartDate"+orderNo;
	div.appendChild(sdDiv);
	var edDiv = document.createElement('div');
	edDiv.id = "placeholderEndDate"+orderNo;
	div.appendChild(edDiv);
	
	if (type == 'creation')	document.getElementById('ss_creationDates_options').appendChild(div);
	else document.getElementById('ss_modificationDates_options').appendChild(div);
	if (!startDate)
		startDate = ''; 

	if (!endDate)
		endDate = '';

	dojo.widget.createWidget("DropdownDatePickerActivateByInput", 
								{value:startDate, 
								lang: djConfig&&djConfig["locale"]?djConfig["locale"]:"en", 
								weekStartsOn: ss_weekStartsOn,
								id:'searchStartDate'+orderNo, 
								name:'searchStartDate'+orderNo,
								maxListLength : 10,	
								autoComplete: false}, 
							document.getElementById("placeholderStartDate"+orderNo+""));

	dojo.widget.createWidget("DropdownDatePickerActivateByInput", {value:endDate, lang: djConfig&&djConfig["locale"]?djConfig["locale"]:"en", weekStartsOn: ss_weekStartsOn, id:'searchEndDate'+orderNo, name:'searchEndDate'+orderNo,
								maxListLength : 10,	autoComplete: false}, document.getElementById("placeholderEndDate"+orderNo+""));
}

/* check/uncheck checkboxes in tree on click in the place name */
function t_advSearchForm_wsTree_showId(forum, obj) {
	if (obj.ownerDocument) {
		var cDocument = obj.ownerDocument;
	} else if (obj.document) {
		cDocument = obj.document;
	}
	if (cDocument) {
		var r = cDocument.getElementById("ss_tree_checkboxt_searchForm_wsTreesearchFolders" + forum);
		if (r) {
			if (r.checked !== undefined) {
				r.checked = !r.checked;
			}
			if (r.onclick !== undefined) {
				r.onclick();
			}
		}
	}
	return false;
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
	ss_prepareAdditionalSearchOptions();
	document.getElementById('ss_advSearchForm').submit();
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
	var txtContainerObj = document.getElementById(txtContainerId);
	if (txtContainerObj) {
		if (txtContainerObj.innerHTML == ss_searchFormMoreOptionsHideLabel) {
			txtContainerObj.innerHTML = ss_searchFormMoreOptionsShowLabel;
		} else {
			txtContainerObj.innerHTML = ss_searchFormMoreOptionsHideLabel;
		}
	}
	
	var ssSearchParseAdvancedFormInputObj = document.getElementById("ssSearchParseAdvancedForm" + namespace);
	if (ssSearchParseAdvancedFormInputObj) {
		ssSearchParseAdvancedFormInputObj.value = "true";
	// <input type="hidden" name="ssSearchParseAdvancedForm" value="true" />
	}
	
	ss_showHide(objId);
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

function ss_prepareAdditionalSearchOptions() {
	var numbers = new Array();
	var types = new Array();
	for (var i=0; i<ss_userOptionsCounter; i++) {
		if (ss_optionsArray[i] != "") {
			numbers[numbers.length] = i;
			types[types.length] = ss_optionsArray[i];
		}
	}
	document.getElementById("searchNumbers").value = numbers.join(" ");
	document.getElementById("searchTypes").value = types.join(" ");
	return true;
}

function ss_saveSearchQuery(inputId, errMsgBoxId) {
	var inputObj = document.getElementById(inputId);
	if (!inputObj) {
		return;
	}
	var queryName = inputObj.value;
	if (!queryName || queryName.trim() == "" || queryName == window.ss_searchResultSavedSearchInputLegend) {
		var errMsgBoxObj = document.getElementById(errMsgBoxId);		
		errMsgBoxObj.innerHTML = ss_noNameMsg;
		ss_showDiv(errMsgBoxId);
		inputObj.focus();
		return;
	} else {
		ss_hideDiv(errMsgBoxId);
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
		dojo.event.connect(removerLink, "onclick", ss_callRemoveSavedQuery(data.savedQueryName,'ss_saveQueryErrMsg', newLi));
		var removerImg = document.createElement('img');
		removerImg.setAttribute("src", ss_imagesPath + "pics/delete.gif");
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



