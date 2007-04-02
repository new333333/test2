<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
dojo.require('dojo.widget.*');

</script>
<script type="text/javascript" src="<html:rootPath/>js/widget/WorkflowSelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/EntrySelect.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/widget/FieldSelect.js"></script>
<script type="text/javascript">

// TODO move this stuff to js file when ready
var ss_userOptionsCounter = 0;
var ss_optionsArray = new Array();
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
function ss_callRemove(orderNo) { 
	return function(evt) {ss_removeOption(orderNo);};
}
function ss_addWorkflow(orderNo, wfIdValue, stepsValue) {
//	alert(wfIdValue+" steps:"+stepsValue);
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid yellow;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="filter.workflows"/>: "));

	var wDiv = document.createElement('div');
	wDiv.id = "placeholderWorkflow"+orderNo;
	div.appendChild(wDiv);
	var sDiv = document.createElement('div');
	sDiv.id = "workflowSteps"+orderNo;
	sDiv.setAttribute("style", "display:inline;");
	div.appendChild(sDiv);
	document.getElementById('ss_searchForm_additionalFilters').appendChild(div);
		
	var baseUrl = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
	var properties = {name:"searchWorkflow"+orderNo+"", id:"searchWorkflow"+orderNo+"", dataUrl:baseUrl+"&operation=get_workflows_widget", nestedUrl:baseUrl+"&operation=get_workflow_step_widget", stepsWidget:sDiv, searchFieldName:"searchWorkflowStep"+orderNo};
	var wfWidget = dojo.widget.createWidget("WorkflowSelect", properties, document.getElementById("placeholderWorkflow"+orderNo+""));

//	alert(wfWidget);
	if (wfIdValue!=null && wfIdValue!="")
		wfWidget.setWorkflowValue(wfIdValue, wfIdValue);
	return wfWidget;
}
function ss_addEntry(orderNo) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid blue;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="label.entry"/>: "));
	
	var eDiv = document.createElement('div');
	eDiv.id = "placeholderEntry"+orderNo;
	div.appendChild(eDiv);
	var sDiv = document.createElement('div');
	sDiv.id = "entryFields"+orderNo;
	sDiv.setAttribute("style", "display:inline;");
	div.appendChild(sDiv);
	document.getElementById('ss_searchForm_additionalFilters').appendChild(div);
		
	var baseUrl = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
	var properties = {name:"ss_entry_def_id"+orderNo+"", id:"ss_entry_def_id"+orderNo+"", dataUrl:baseUrl+"&operation=get_entry_types_widget", nestedUrl:baseUrl+"&operation=get_entry_fields_widget", widgetContainer:sDiv, searchFieldIndex:orderNo};
	dojo.widget.createWidget("EntrySelect", properties, document.getElementById("placeholderEntry"+orderNo+""));
	
}
function ss_addTag(orderNo, communityTagValue, personalTagValue) {
	
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid red;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);

	var pDiv = document.createElement('div');
	pDiv.id = "placeholderPersonal"+orderNo;
	var cDiv = document.createElement('div');
	cDiv.id = "placeholderCommunity"+orderNo;

	div.appendChild(document.createTextNode(" <ssf:nlt tag="tags.communityTags"/>: "));
	div.appendChild(cDiv);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="tags.personalTags"/>: "));
	div.appendChild(pDiv);
	document.getElementById('ss_searchForm_additionalFilters').appendChild(div);
	
	var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
	url += "&operation=get_tags_widget";
	var propertiesCommunity = {name:"searchCommunityTags"+orderNo+"", id:"searchCommunityTags"+orderNo+"", dataUrl:url+"&findType=communityTags"};
	var propertiesPersonal = {name:"searchPersonalTags"+orderNo+"", id:"searchPersonalTags"+orderNo+"", dataUrl:url+"&findType=personalTags"};
	dojo.widget.createWidget("ComboBox", propertiesCommunity, document.getElementById("placeholderCommunity"+orderNo+""));
	dojo.widget.createWidget("ComboBox", propertiesPersonal, document.getElementById("placeholderPersonal"+orderNo+""));
	
}

function ss_addAuthor(orderNo, author) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid pink;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);

	var aDiv = document.createElement('div');
	aDiv.id = "placeholderAuthor"+orderNo;

	div.appendChild(document.createTextNode(" <ssf:nlt tag="label.author"/>: "));
	div.appendChild(aDiv);
	document.getElementById('ss_searchForm_additionalFilters').appendChild(div);
	
	var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
	url += "&operation=get_users_widget";
	var props = {name:"searchAuthors"+orderNo+"", id:"searchAuthors"+orderNo+"", dataUrl:url};
	dojo.widget.createWidget("ComboBox", props, document.getElementById("placeholderAuthor"+orderNo+""));
}

function ss_addDate(orderNo, type) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid green;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);
	div.appendChild(document.createTextNode(" <ssf:nlt tag="label.Date"/>: "));
	
	var sdDiv = document.createElement('div');
	sdDiv.id = "placeholderStartDate"+orderNo;
	div.appendChild(sdDiv);
	var edDiv = document.createElement('div');
	edDiv.id = "placeholderEndDate"+orderNo;
	div.appendChild(edDiv);
	
	document.getElementById('ss_searchForm_additionalFilters').appendChild(div);
	
	dojo.widget.createWidget("DropDownDatePicker", {value:'', id:'searchStartDate'+orderNo, name:'searchStartDate'+orderNo}, document.getElementById("placeholderStartDate"+orderNo+""));
	dojo.widget.createWidget("DropDownDatePicker", {value:'today', id:'searchEndDate'+orderNo, name:'searchEndDate'+orderNo}, document.getElementById("placeholderEndDate"+orderNo+""));
}

function ss_removeOption(orderNo) {
	ss_optionsArray[orderNo]="";
	document.getElementById('ss_searchForm_additionalFilters').removeChild(document.getElementById('block'+orderNo));
}

function prepareAdditionalOptions() {
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

</script>
<div id="ss_searchForm" style="background-color:#e8eff7;">
<form action="<portlet:actionURL windowState="maximized" portletMode="view">
			<portlet:param name="action" value="advanced_search"/>
			<portlet:param name="tabTitle" value=""/>
			<portlet:param name="newTab" value="1"/>
			</portlet:actionURL>" method="post" onSubmit="return prepareAdditionalOptions();">

	<div id="ss_searchForm_main" style='border: 1px solid gray;padding:5px;margin:0px 5px 5px 5px;'>
		<ssf:nlt tag="searchForm.searchText"/>: <input type="text" name="searchText" id="searchText"/>
		<ssf:nlt tag="searchForm.searchAuthor"/>: <input type="text" name="searchAuthors" id="searchAuthors"/>
		<ssf:nlt tag="searchForm.searchTag"/>: <input type="text" name="searchTags" id="searchTags"/>
		<ssf:nlt tag="searchForm.searchJoiner"/>: <input type="radio" name="searchJoinerAnd" value="true" id="searchJoinerAnd" checked="true"/><ssf:nlt tag="searchForm.searchJoiner.And"/>
			<input type="radio" name="searchJoinerAnd" id="searchJoinerOr" value="false"/><ssf:nlt tag="searchForm.searchJoiner.Or"/>
	</div>

	<div id="ss_searchForm_options" style='border: 1px solid gray;padding:5px;margin:5px;'>
		<a href="#" onClick="ss_addOption('workflow');">+ workflows</a>
		<a href="#" onClick="ss_addOption('entry');">+ entry attributes</a>
		<a href="#" onClick="ss_addOption('tag');">+ tags filter</a>
		<a href="#" onClick="ss_addOption('creation_date');">+ creation date filter</a>
		<a href="#" onClick="ss_addOption('modification_date');">+ modification date filter</a>
		<a href="#" onClick="ss_addOption('creator_by_id');">+ author</a>
	</div>
	
	<div id="ss_searchForm_additionalFilters">
	</div>
	
	<div id="ss_buttonBar">
		<input type="hidden" name="operation" value="ss_searchResults"/>
		<input type="hidden" name="searchNumbers" id="searchNumbers" value=""/>		
		<input type="hidden" name="searchTypes" id="searchTypes" value=""/>		
		<input type="submit" name="searchBtn" value="Submit"/>
	</div>
</form>
</div>