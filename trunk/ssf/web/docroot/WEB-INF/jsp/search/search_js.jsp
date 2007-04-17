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
<script type="text/javascript">
dojo.require('dojo.widget.*');
</script>
<script type="text/javascript" src="<html:rootPath/>js/widget/WorkflowSelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/EntrySelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/FieldSelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/SelectPagable.js"></script>
<script type="text/javascript">



// TODO move this stuff to js file when ready
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
	   default : alert("Unknown type: "+type);
	}
	ss_userOptionsCounter++;
}
function ss_callRemoveSearchOption(orderNo) { 
	return function(evt) {ss_removeOption(orderNo);};
}

var ss_AjaxBaseUrl = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";

function ss_addInitializedWorkflow(wfIdValue, stepsValue) {
	ss_optionsArray[ss_userOptionsCounter]='workflow';
	var wfWidget = ss_addWorkflow(ss_userOptionsCounter, wfIdValue, stepsValue);
	ss_userOptionsCounter++;
}

function ss_addInitializedEntry(entryId, fieldName, value) {
	ss_optionsArray[ss_userOptionsCounter]='entry';
	ss_addEntry(ss_userOptionsCounter, entryId, fieldName, value);
	ss_userOptionsCounter++;
}

function ss_addInitializedCreationDate(startDate, endDate) {
	ss_optionsArray[ss_userOptionsCounter]='creation_date';
	ss_addDate(ss_userOptionsCounter, 'creation', startDate, endDate);
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
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", "<html:imagesPath/>pics/delete.gif");
	div.appendChild(remover);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="searchForm.label.workflow"/>: "));

	var wDiv = document.createElement('div');
	wDiv.id = "placeholderWorkflow"+orderNo;
	div.appendChild(wDiv);
	var sDiv = document.createElement('div');
	sDiv.id = "workflowSteps"+orderNo;
	sDiv.setAttribute("style", "display:inline;");
	div.appendChild(sDiv);
	document.getElementById('ss_workflows_options').appendChild(div);
		
	var properties = {name:"searchWorkflow"+orderNo+"", id:"searchWorkflow"+orderNo+"", dataUrl:ss_AjaxBaseUrl+"&operation=get_workflows_widget", nestedUrl:ss_AjaxBaseUrl+"&operation=get_workflow_step_widget", stepsWidget:sDiv, searchFieldName:"searchWorkflowStep"+orderNo, mode: "remote",
								maxListLength : 10,	autoComplete: false};
	var wfWidget = dojo.widget.createWidget("WorkflowSelect", properties, document.getElementById("placeholderWorkflow"+orderNo+""));

	if (wfIdValue!=null && wfIdValue!=""){
		wfWidget.setDefaultValue(wfIdValue, workflows[wfIdValue], stepsValue, steps[wfIdValue+"-"+stepsValue]);
	}
	return wfWidget;
}
function ss_addEntry(orderNo, entryId, fieldName, value) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", "<html:imagesPath/>pics/delete.gif");
	div.appendChild(remover);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="searchForm.label.entry"/>: "));
	
	var eDiv = document.createElement('div');
	eDiv.id = "placeholderEntry"+orderNo;
	div.appendChild(eDiv);
	var sDiv = document.createElement('div');
	sDiv.id = "entryFields"+orderNo;
	sDiv.setAttribute("style", "display:inline;");
	div.appendChild(sDiv);
	document.getElementById('ss_entries_options').appendChild(div);

	var properties = {name:"ss_entry_def_id"+orderNo+"", id:"ss_entry_def_id"+orderNo+"", dataUrl:ss_AjaxBaseUrl+"&operation=get_entry_types_widget", nestedUrl:ss_AjaxBaseUrl+"&operation=get_entry_fields_widget", widgetContainer:sDiv, searchFieldIndex:orderNo, mode: "remote",
								maxListLength : 10,	autoComplete: false};
	var entryWidget = dojo.widget.createWidget("EntrySelect", properties, document.getElementById("placeholderEntry"+orderNo+""));
	if (entryId && entryId != "") {
		entryWidget.setDefaultValue(entryId, entries[entryId], fieldName, fields[entryId+"-"+fieldName], value, fieldsTypes[entryId+"-"+fieldName]);
	}
}
function ss_addTag(orderNo, communityTagValue, personalTagValue) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", "<html:imagesPath/>pics/delete.gif");
	div.appendChild(remover);

	var pDiv = document.createElement('div');
	pDiv.id = "placeholderPersonal"+orderNo;
	var cDiv = document.createElement('div');
	cDiv.id = "placeholderCommunity"+orderNo;

	div.appendChild(document.createTextNode(" <ssf:nlt tag="tags.communityTags"/>: "));
	div.appendChild(cDiv);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="tags.personalTags"/>: "));
	div.appendChild(pDiv);
	document.getElementById('ss_tags_options').appendChild(div);
	
	var url = ss_AjaxBaseUrl + "&operation=get_tags_widget&searchText=%{searchString}&pager=%{pagerString}";
	var propertiesCommunity = {name:"searchCommunityTags"+orderNo+"", 
								id:"searchCommunityTags"+orderNo+"", 
								dataUrl:url+"&findType=communityTags", 								
								maxListLength : 12,	autoComplete: false};
	var propertiesPersonal = {name:"searchPersonalTags"+orderNo+"", 
								id:"searchPersonalTags"+orderNo+"", 
								dataUrl:url+"&findType=personalTags", 
								maxListLength : 12,	autoComplete: false};
	var communityTagWidget = dojo.widget.createWidget("SelectPagable", propertiesCommunity, document.getElementById("placeholderCommunity"+orderNo+""));
	var personalTagWidget = dojo.widget.createWidget("SelectPagable", propertiesPersonal, document.getElementById("placeholderPersonal"+orderNo+""));
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
	remover.setAttribute("src", "<html:imagesPath/>pics/delete.gif");
	div.appendChild(remover);

	var aDiv = document.createElement('div');
	aDiv.id = "placeholderAuthor"+orderNo;

	div.appendChild(document.createTextNode(" <ssf:nlt tag="searchForm.label.author"/>: "));
	div.appendChild(aDiv);
	document.getElementById('ss_authors_options').appendChild(div);
	
	var url = ss_AjaxBaseUrl + "&operation=get_users_widget&searchText=%{searchString}&pager=%{pagerString}";
	var props = {name : "searchAuthors"+orderNo+"", 
					id : "searchAuthors"+orderNo+"", 
					dataUrl:url,
					maxListLength : 12,
					autoComplete: false};
	var usersWidget = dojo.widget.createWidget("SelectPagable", props, document.getElementById("placeholderAuthor"+orderNo+""));
	if (authorId && authorName && authorId!="" && authorName!="") {
		usersWidget.setValue(authorId);
		usersWidget.setLabel(authorName);
	}
}

function ss_addDate(orderNo, type, startDate, endDate) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemoveSearchOption(orderNo));
	remover.setAttribute("src", "<html:imagesPath/>pics/delete.gif");
	div.appendChild(remover);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="searchForm.label.date"/>: "));
	
	var sdDiv = document.createElement('div');
	sdDiv.id = "placeholderStartDate"+orderNo;
	div.appendChild(sdDiv);
	var edDiv = document.createElement('div');
	edDiv.id = "placeholderEndDate"+orderNo;
	div.appendChild(edDiv);
	
	if (type == 'creation')	document.getElementById('ss_creationDates_options').appendChild(div);
	else document.getElementById('ss_modificationDates_options').appendChild(div);
	if (startDate) 
		dojo.widget.createWidget("DropDownDatePicker", {value:startDate, lang: ss_user_locale, id:'searchStartDate'+orderNo, name:'searchStartDate'+orderNo,
								maxListLength : 10,	autoComplete: false}, document.getElementById("placeholderStartDate"+orderNo+""));
	else 
		dojo.widget.createWidget("DropDownDatePicker", {value:'', lang: ss_user_locale, id:'searchStartDate'+orderNo, name:'searchStartDate'+orderNo,
								maxListLength : 10,	autoComplete: false}, document.getElementById("placeholderStartDate"+orderNo+""));

	if (endDate)
		dojo.widget.createWidget("DropDownDatePicker", {value:endDate, lang: ss_user_locale, id:'searchEndDate'+orderNo, name:'searchEndDate'+orderNo,
								maxListLength : 10,	autoComplete: false}, document.getElementById("placeholderEndDate"+orderNo+""));
	else 
		dojo.widget.createWidget("DropDownDatePicker", {value:'', lang: ss_user_locale, id:'searchEndDate'+orderNo, name:'searchEndDate'+orderNo,
								maxListLength : 10,	autoComplete: false}, document.getElementById("placeholderEndDate"+orderNo+""));
	
}

function ss_removeOption(orderNo) {
	ss_optionsArray[orderNo]="";
	var parent = document.getElementById('block'+orderNo).parentNode;
	parent.removeChild(document.getElementById('block'+orderNo));
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
function ss_search() {
	ss_prepareAdditionalSearchOptions();
	document.getElementById('advSearchForm').submit();
}

// TODO find the same method somewhere in common....
function ss_showHide(objId){
	var obj = document.getElementById(objId);
	if (obj && obj.style) {
		if (obj.style.visibility == "visible") {
			obj.style.visibility="hidden";
			obj.style.display="none";
		} else {
			obj.style.visibility="visible";
			obj.style.display="block";
		}
	}
}

function ss_showAdditionalOptions(objId) {
	ss_showHide(objId);
	if (!ss_searchMoreInitialized) {
		ss_initSearchOptions();
	}
}

function ss_showHideDetails(ind){
	ss_showHide("summary_"+ind);
	ss_showHide("details_"+ind);
}
var ss_opendBoxTooglerSrc = "<html:imagesPath/>pics/flip_down16H.gif";
var ss_closedBoxTooglerSrc = "<html:imagesPath/>pics/flip_up16H.gif";
function ss_showHideRatingBox(id, imgObj) {
	ss_showHide(id);
	if (imgObj.src.indexOf("flip_down16H.gif") > -1) {
		imgObj.src=ss_closedBoxTooglerSrc;
	} else {
		imgObj.src=ss_opendBoxTooglerSrc;
	}
}

function ss_goToSearchResultPage(ind) {
	var url="<portlet:actionURL windowState="maximized" portletMode="view">
				<portlet:param name="action" value="advanced_search"/>
				<portlet:param name="tabId" value="${tabId}"/>
				<portlet:param name="operation" value="viewPage"/>
		</portlet:actionURL>";
	url = url + "&pageNumber=" + ind;
	window.location.assign(url);
}

</script>