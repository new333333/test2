<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<script type="text/javascript">
dojo.require('dojo.widget.ComboBox');

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
	   case "date" :
	      ss_addDate(ss_userOptionsCounter);
	      break;
	   default : alert("Unknown type: "+type);
	}
	ss_userOptionsCounter++;
}

function ss_addWorkflow(orderNo) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid yellow;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);
	div.appendChild(document.createTextNode(" Workflow"));
	document.getElementById('searchForm_additionalFilters').appendChild(div);
}
function ss_addEntry(orderNo) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid blue;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);
	div.appendChild(document.createTextNode(" Entry"));
	document.getElementById('searchForm_additionalFilters').appendChild(div);
}
function ss_addTag(orderNo) {
	
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
	document.getElementById('searchForm_additionalFilters').appendChild(div);
	
	var url = "<ssf:url adapter="true" portletName="ss_forum" action="__ajax_request" actionUrl="true"></ssf:url>";
	url += "&operation=get_tags_widget";
	var propertiesCommunity = {name:"communityTag"+orderNo+"", id:"communityTag"+orderNo+"", dataUrl:url+"&findType=communityTags"};
	var propertiesPersonal = {name:"personalTag"+orderNo+"", id:"personalTag"+orderNo+"", dataUrl:url+"&findType=personalTags"};
	dojo.widget.createWidget("ComboBox", propertiesCommunity, document.getElementById("placeholderCommunity"+orderNo+""));
	dojo.widget.createWidget("ComboBox", propertiesPersonal, document.getElementById("placeholderPersonal"+orderNo+""));
}

function ss_callRemove(orderNo) { 
	return function(evt) {ss_removeOption(orderNo);};
}

function ss_addDate(orderNo) {
	var div = document.createElement('div');
	div.id = "block"+ss_userOptionsCounter;
	div.setAttribute("style", "border: 1px solid green;padding:5px;margin:5px;");
	var removeLink = document.createElement('a');
	dojo.event.connect(removeLink, "onclick", ss_callRemove(orderNo));
	removeLink.appendChild(document.createTextNode("remove "));
	div.appendChild(removeLink);
	div.appendChild(document.createTextNode(" Date"));
	document.getElementById('searchForm_additionalFilters').appendChild(div);
}
function ss_removeOption(orderNo) {
	ss_optionsArray[orderNo]="";
	document.getElementById('searchForm_additionalFilters').removeChild(document.getElementById('block'+orderNo));
}

</script>

<form action="<portlet:actionURL windowState="maximized" portletMode="view">
			<portlet:param name="action" value="advanced_search"/>
			<portlet:param name="tabTitle" value=""/>
			<portlet:param name="newTab" value="1"/>
			</portlet:actionURL>" method="post">

	<div id="searchForm_main" style='border: 1px solid gray;padding:5px;margin:5px;'>
		<ssf:nlt tag="searchForm.searchText"/>: <input type="text" name="searchText"/>
		<ssf:nlt tag="searchForm.searchAuthor"/>: <input type="text" name="searchAuthors"/>
		<ssf:nlt tag="searchForm.searchTag"/>: <input type="text" name="searchTags"/>
		<ssf:nlt tag="searchForm.searchJoiner"/>: <input type="radio" name="searchJoiner" value="true"/><ssf:nlt tag="searchForm.searchJoiner.And"/>
			<input type="radio" name="searchJoiner" value="false"/><ssf:nlt tag="searchForm.searchJoiner.Or"/>
	</div>

	<div id="searchForm_options" style='border: 1px solid gray;padding:5px;margin:5px;'>
		<a href="#" onClick="ss_addOption('workflow');">+ workflows</a>
		<a href="#" onClick="ss_addOption('entry');">+ entry attributes</a>
		<a href="#" onClick="ss_addOption('tag');">+ tags filter</a>
		<a href="#" onClick="ss_addOption('date');">+ date filter</a>
	</div>
	
	<div id="searchForm_additionalFilters">
	</div>
	
	<div id="buttonBar">
		<input type="hidden" name="operation" value="ss_searchResults"/>
		<input type="submit" name="searchBtn" value="Submit"/>
	</div>
</form>