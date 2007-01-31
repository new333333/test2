<% // Find a single entry %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	String findEntriesType = (String) request.getAttribute("list_type");
	String findEntriesFormName = (String) request.getAttribute("form_name");
	String findEntriesElementName = (String) request.getAttribute("form_element");
	String findEntriesElementWidth = (String) request.getAttribute("element_width");
	String findEntriesBinderId = (String) request.getAttribute("binderId");
	String findEntriesSearchSubFolders = (String) request.getAttribute("searchSubFolders");
%>
<c:set var="prefix" value="<%= findEntriesFormName + "_" + findEntriesElementName %>" />
<c:if test="${empty ss_find_entries_support_stuff_loaded}">
<script type="text/javascript">
var ss_findEntries_searchText = ""
var ss_findEntries_pageNumber = 0;
var ss_findEntriesDivTopOffset = 2;

var ss_findEntriesSearchInProgress = 0;
var ss_findEntriesSearchWaiting = 0;
var ss_findEntriesSearchStartMs = 0;
var ss_findEntriesSearchLastText = "";
var ss_findEntriesSearchLastTextObjId = "";
var ss_findEntriesSearchLastElement = "";
var ss_findEntriesSearchLastfindEntriesType = "";
function ss_findEntriesSearch_${prefix}(textObjId, elementName, findEntriesType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text == '' || text != ss_findEntriesSearchLastText) ss_findEntries_pageNumber = 0;
	ss_setupStatusMessageDiv()
	ss_moveDivToBody('ss_findEntriesNavBarDiv_<portlet:namespace/>');
	//Are we already doing a search?
	if (ss_findEntriesSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_findEntriesSearchLastText = text;
		ss_findEntriesSearchLastTextObjId = textObjId;
		ss_findEntriesSearchLastElement = elementName;
		ss_findEntriesSearchLastfindEntriesType = findEntriesType;
		ss_findEntriesSearchWaiting = 1;
		var d = new Date();
		var curr_msec = d.getTime();
		if (ss_findEntriesSearchStartMs == 0 || curr_msec < ss_findEntriesSearchStartMs + 1000) {
			ss_debug('  hold search request...')
			if (ss_findEntriesSearchStartMs == 0) ss_findEntriesSearchStartMs = curr_msec;
			return;
		}
		//The user waited for over a second, let this request go through
		ss_findEntriesSearchStartMs = 0;
		ss_debug('   Stopped waiting')
	}
	ss_findEntriesSearchInProgress = 1;
	ss_findEntriesSearchWaiting = 0;
	ss_findEntriesSearchLastTextObjId = textObjId;
	ss_findEntriesSearchLastElement = elementName;
	ss_findEntriesSearchLastText = text;
	ss_findEntriesSearchLastfindEntriesType = findEntriesType;
 	//Save the text in case the user changes the search type
 	ss_findEntries_searchText = text;
 	
 	//See if the user ended the string with a CR. If so, then try to launch.
 	var newText = "";
 	var crFound = 0;
 	for (var i = 0; i < text.length; i++) {
 		if (text.charCodeAt(i) == 10 || text.charCodeAt(i) == 13) {
 			crFound = 1;
 			break;
 		} else {
 			newText += text.charAt(i);
 		}
 	}
 	if (crFound == 1) {
 		textObj.value = newText;
 		text = textObj.value;
		var ulObj = document.getElementById('available_<%= findEntriesElementName %>_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findEntriesSelectItem<portlet:namespace/>(liObjs[0]);
			return;
		}
 	}
 	//Fade the previous selections
 	var savedColor = "#000000";
 	var divObj = document.getElementById('available_'+elementName+'_${prefix}');
 	if (divObj != null && divObj.style && divObj.style.color) {
 		savedColor = divObj.style.color;
 	}
 	if (divObj != null) divObj.style.color = "#cccccc";

 	ss_debug("Page number: " + ss_findEntries_pageNumber + ", //"+text+"//")
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="operation" value="find_entries_search" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	var searchText = text;
	if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findEntries_pageNumber)
	ajaxRequest.addKeyValue("findType", findEntriesType)
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName+"_${prefix}")
	ajaxRequest.addKeyValue("binderId", "<%= findEntriesBinderId %>")
	ajaxRequest.addKeyValue("searchSubFolders", "<%= findEntriesSearchSubFolders %>")
	ajaxRequest.addKeyValue("namespace", "<portlet:namespace/>")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_prefindEntriesRequest);
	ajaxRequest.setPostRequest(ss_postfindEntriesRequest<portlet:namespace/>);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setData("crFound", crFound)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postfindEntriesRequest<portlet:namespace/>(obj) {
	ss_debug('ss_postfindEntriesRequest<portlet:namespace/>')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findEntriesSearchInProgress = 0;

	var divObj = document.getElementById('ss_findEntriesNavBarDiv_<portlet:namespace/>');
	ss_moveDivToBody('ss_findEntriesNavBarDiv_<portlet:namespace/>');
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findEntries_searchText_bottom_<portlet:namespace/>") + ss_findEntriesDivTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findEntries_searchText_bottom_<portlet:namespace/>")))
	ss_showDivActivate('ss_findEntriesNavBarDiv_<portlet:namespace/>');
		
 	//Show this at full brightness
 	divObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}');
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
		
	//See if there is another search request to be done
	if (ss_findEntriesSearchWaiting == 1) {
		setTimeout('ss_findEntriesSearch_${prefix}(ss_findEntriesSearchLastTextObjId, ss_findEntriesSearchLastElement, ss_findEntriesSearchLastfindEntriesType)', 100)
	}
	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout("ss_findEntriesSelectItem0_${prefix}();", 100);
			return;
		}
	}
}
function ss_findEntriesSelectItem0_${prefix}() {
	var ulObj = document.getElementById('available_<%= findEntriesElementName %>_${prefix}');
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findEntriesSelectItem<portlet:namespace/>(liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findEntriesSelectItem<portlet:namespace/>(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${ssBinder.id}"
		    entryId="ss_entryIdPlaceholder">
		    <ssf:param name="entityType" value="folderEntry" />
			</ssf:url>";
	var id = ss_replaceSubStr(obj.id, 'ss_findEntries_id_', "");
	url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
	self.location.href = url;
}

function ss_savefindEntriesData_${prefix}() {
	ss_debug('ss_savefindEntriesData')
	var ulObj = document.getElementById('available_<%= findEntriesElementName %>_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findEntriesSelectItem<portlet:namespace/>(liObjs[0]);
	}
	return false;
}

function ss_findEntriesNextPage<portlet:namespace/>() {
	ss_findEntries_pageNumber++;
	setTimeout("ss_findEntriesSearch_${prefix}(ss_findEntriesSearchLastTextObjId, ss_findEntriesSearchLastElement, ss_findEntriesSearchLastfindEntriesType);", 100);
}

function ss_findEntriesPrevPage<portlet:namespace/>() {
	ss_findEntries_pageNumber--;
	if (ss_findEntries_pageNumber < 0) ss_findEntries_pageNumber = 0;
	ss_findEntriesSearch_${prefix}(ss_findEntriesSearchLastTextObjId, ss_findEntriesSearchLastElement, ss_findEntriesSearchLastfindEntriesType);
}

</script>
<c:set var="ss_find_entries_support_stuff_loaded" value="1" scope="request"/>
</c:if>

<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findEntriesElementWidth %>; overflow:hidden;" 
    name="ss_findEntries_searchText_<portlet:namespace/>" 
    id="ss_findEntries_searchText_<portlet:namespace/>"
    onKeyUp="ss_findEntriesSearch_${prefix}(this.id, '<%= findEntriesElementName %>', '<%= findEntriesType %>');"
    onBlur="setTimeout('ss_hideDiv(\'ss_findEntriesNavBarDiv_<portlet:namespace/>\')', 200);"></textarea></div>
<div id="ss_findEntries_searchText_bottom_<portlet:namespace/>" style="padding:0px; margin:0px;"></div>
<div id="ss_findEntriesNavBarDiv_<portlet:namespace/>"
    class="ss_findUserList" style="visibility:hidden;">
    <div id="available_<%= findEntriesElementName %>_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
<input type="hidden" name="<%= findEntriesElementName %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findEntriesFormName %>', ss_savefindEntriesData_${prefix});
</script>
