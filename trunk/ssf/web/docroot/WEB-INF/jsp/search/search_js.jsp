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
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemove(orderNo));
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
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemove(orderNo));
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
		
	var baseUrl = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
	var properties = {name:"ss_entry_def_id"+orderNo+"", id:"ss_entry_def_id"+orderNo+"", dataUrl:baseUrl+"&operation=get_entry_types_widget", nestedUrl:baseUrl+"&operation=get_entry_fields_widget", widgetContainer:sDiv, searchFieldIndex:orderNo};
	dojo.widget.createWidget("EntrySelect", properties, document.getElementById("placeholderEntry"+orderNo+""));
	
}
function ss_addTag(orderNo, communityTagValue, personalTagValue) {
	
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemove(orderNo));
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
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemove(orderNo));
	remover.setAttribute("src", "<html:imagesPath/>pics/delete.gif");
	div.appendChild(remover);

	var aDiv = document.createElement('div');
	aDiv.id = "placeholderAuthor"+orderNo;

	div.appendChild(document.createTextNode(" <ssf:nlt tag="searchForm.label.author"/>: "));
	div.appendChild(aDiv);
	document.getElementById('ss_authors_options').appendChild(div);
	
	var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
	url += "&operation=get_users_widget";
	var props = {name:"searchAuthors"+orderNo+"", id:"searchAuthors"+orderNo+"", dataUrl:url};
	dojo.widget.createWidget("ComboBox", props, document.getElementById("placeholderAuthor"+orderNo+""));
}

function ss_addDate(orderNo, type) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	var remover = document.createElement('img');
	dojo.event.connect(remover, "onclick", ss_callRemove(orderNo));
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
		
	dojo.widget.createWidget("DropDownDatePicker", {value:'', id:'searchStartDate'+orderNo, name:'searchStartDate'+orderNo}, document.getElementById("placeholderStartDate"+orderNo+""));
	dojo.widget.createWidget("DropDownDatePicker", {value:'today', id:'searchEndDate'+orderNo, name:'searchEndDate'+orderNo}, document.getElementById("placeholderEndDate"+orderNo+""));
}

function ss_removeOption(orderNo) {
	ss_optionsArray[orderNo]="";
	var parent = document.getElementById('block'+orderNo).parentNode;
	parent.removeChild(document.getElementById('block'+orderNo));
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
function ss_search() {
	prepareAdditionalOptions();
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

function goToPage(ind) {
	url="<portlet:actionURL windowState="maximized" portletMode="view">
				<portlet:param name="action" value="advanced_search"/>
				<portlet:param name="tabId" value="${tabId}"/>
				<portlet:param name="operation" value="viewPage"/>
		</portlet:actionURL>";
	url = url + "&pageNumber=" + ind;
	window.location.assign(url);
}

</script>