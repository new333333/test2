<% // Find a single tag %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	String findTagType = (String) request.getAttribute("list_type");
	String findTagFormName = (String) request.getAttribute("form_name");
	String findTagElementName = (String) request.getAttribute("form_element");
	String findTagElementWidth = (String) request.getAttribute("element_width");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	Boolean leaveResultsVisible = (Boolean) request.getAttribute("leaveResultsVisible");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="prefix" value="${renderResponse.namespace}_${iCount}" />
<script type="text/javascript">
var ss_findTag_searchText${prefix} = ""
var ss_findTag_pageNumber${prefix} = 0;
var ss_findTagDivTopOffset${prefix} = 2;

var ss_findTagSearchInProgress${prefix} = 0;
var ss_findTagSearchWaiting${prefix} = 0;
var ss_findTagSearchStartMs${prefix} = 0;
var ss_findTagSearchLastText${prefix} = "";
var ss_findTagSearchLastTextObjId${prefix} = "";
var ss_findTagSearchLastElement${prefix} = "";
var ss_findTagSearchLastfindTagType${prefix} = "";
var ss_findTagClickRoutine${prefix} = "<%= clickRoutine %>";
function ss_findTagSearch_${prefix}(textObjId, elementName, findTagType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text == '' || text != ss_findTagSearchLastText${prefix}) ss_findTag_pageNumber${prefix} = 0;
	ss_setupStatusMessageDiv()
	//ss_moveDivToBody('ss_findTagNavBarDiv${prefix}');
	//Are we already doing a search?
	if (ss_findTagSearchInProgress${prefix} == 1) {
		//Yes, hold this request until the current one finishes
		ss_findTagSearchLastText${prefix} = text;
		ss_findTagSearchLastTextObjId${prefix} = textObjId;
		ss_findTagSearchLastElement${prefix} = elementName;
		ss_findTagSearchLastfindTagType${prefix} = findTagType;
		ss_findTagSearchWaiting${prefix} = 1;
		var d = new Date();
		var curr_msec = d.getTime();
		if (ss_findTagSearchStartMs${prefix} == 0 || curr_msec < parseInt(ss_findTagSearchStartMs${prefix} + 1000)) {
			ss_debug('  hold search request...')
			if (ss_findTagSearchStartMs${prefix} == 0) ss_findTagSearchStartMs${prefix} = curr_msec;
			return;
		}
		//The user waited for over a second, let this request go through
		ss_findTagSearchStartMs${prefix} = 0;
		ss_debug('   Stopped waiting')
	}
	ss_findTagSearchInProgress${prefix} = 1;
	ss_findTagSearchWaiting${prefix} = 0;
	ss_findTagSearchLastTextObjId${prefix} = textObjId;
	ss_findTagSearchLastElement${prefix} = elementName;
	ss_findTagSearchLastText${prefix} = text;
	ss_findTagSearchLastfindTagType${prefix} = findTagType;
 	//Save the text in case the user changes the search type
 	ss_findTag_searchText${prefix} = text;
 	
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
		var ulObj = document.getElementById('available_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findTagSelectItem${prefix}(liObjs[0]);
			return;
		}
 	}
 	//Fade the previous selections
 	var savedColor = "#000000";
 	var divObj = document.getElementById('available_${prefix}');
 	if (divObj != null && divObj.style && divObj.style.color) {
 		savedColor = divObj.style.color;
 	}
 	if (divObj != null) divObj.style.color = "#cccccc";

 	ss_debug("//"+text+"//")
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="operation" value="find_tag_search" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	var searchText = text;
	if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findTag_pageNumber${prefix})
	ajaxRequest.addKeyValue("findType", findTagType)
	ajaxRequest.addKeyValue("listDivId", "available_${prefix}")
	ajaxRequest.addKeyValue("namespace", "${prefix}");
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preFindTagRequest);
	ajaxRequest.setPostRequest(ss_postFindTagRequest${prefix});
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setData("crFound", crFound)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFindTagRequest${prefix}(obj) {
	ss_debug('ss_postFindTagRequest${prefix}')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findTagSearchInProgress${prefix} = 0;

	ss_showFindTagSelections${prefix}();
	
 	//Show this at full brightness
	var divObj = document.getElementById('ss_findTagNavBarDiv_${prefix}');
 	divObj = document.getElementById('available_${prefix}');
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
	
	//See if there is another search request to be done
	if (ss_findTagSearchWaiting${prefix} == 1) {
		setTimeout('ss_findTagSearch_${prefix}(ss_findTagSearchLastTextObjId${prefix}, ss_findTagSearchLastElement${prefix}, ss_findTagSearchLastfindTagType${prefix})', 100)
	}

	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout("ss_findTagSelectItem0_${prefix}();", 100);
			return;
		}
	}
}
function ss_showFindTagSelections${prefix}() {
	var divObj = document.getElementById('ss_findTagNavBarDiv_${prefix}');
	ss_moveDivToBody('ss_findTagNavBarDiv_${prefix}');
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findTag_searchText_bottom_${prefix}") + ss_findTagDivTopOffset${prefix}))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findTag_searchText_bottom_${prefix}")))
	ss_showDivActivate('ss_findTagNavBarDiv_${prefix}');
}
function ss_findTagSelectItem0_${prefix}() {
	var ulObj = document.getElementById('available_${prefix}');
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findTagSelectItem${prefix}(liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findTagSelectItem${prefix}(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<portlet:actionURL windowState="maximized" portletMode="view"><portlet:param 
		name="action" value="search"/><portlet:param 
		name="searchCommunityTags" value="ss_tagPlaceHolder"/><portlet:param 
		name="searchPersonalTags" value="ss_tagPlaceHolder"/><portlet:param 
		name="searchTags" value="tagOnlySearch"/><portlet:param 
		name="tabId" value="${tabId}"/></portlet:actionURL>";
	var id = ss_replaceSubStr(obj.id, 'ss_findTag_id_', "");
	if (ss_findTagClickRoutine${prefix} != "") {
		eval(ss_findTagClickRoutine${prefix} + "('"+id+"');")
		<% if (leaveResultsVisible) { %>
		  setTimeout("ss_showFindTagSelections${prefix}();", 200)
		<% } %>
	} else {
		url = ss_replaceSubStrAll(url, 'ss_tagPlaceHolder', id);
		self.location.href = url;
	}
}

function ss_saveFindTagData_${prefix}() {
	ss_debug('ss_saveFindTagData')
	var ulObj = document.getElementById('available_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findTagSelectItem${prefix}(liObjs[0]);
	}
	return false;
}

function ss_findTagNextPage${prefix}() {
	ss_findTag_pageNumber${prefix}++;
	ss_findTagSearch_${prefix}(ss_findTagSearchLastTextObjId${prefix}, ss_findTagSearchLastElement${prefix}, ss_findTagSearchLastfindTagType${prefix});
}

function ss_findTagPrevPage${prefix}() {
	ss_findTag_pageNumber${prefix}--;
	if (ss_findTag_pageNumber${prefix} < 0) ss_findTag_pageNumber${prefix} = 0;
	ss_findTagSearch_${prefix}(ss_findTagSearchLastTextObjId${prefix}, ss_findTagSearchLastElement${prefix}, ss_findTagSearchLastfindTagType${prefix});
}

</script>

<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findTagElementWidth %>; overflow:hidden;" 
    name="ss_findTag_searchText_${prefix}" 
    id="ss_findTag_searchText_${prefix}"
    onKeyUp="ss_findTagSearch_${prefix}(this.id, '<%= findTagElementName %><%= instanceCount %>', '<%= findTagType %>');"
    onBlur="setTimeout('ss_hideDiv(\'ss_findTagNavBarDiv_${prefix}\')', 200);"></textarea></div>
<div id="ss_findTag_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findTagNavBarDiv_${prefix}" 
    class="ss_findUserList" style="visibility:hidden;">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
<input type="hidden" name="<%= findTagElementName %><%= instanceCount %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findTagFormName %>', ss_saveFindTagData_${prefix});
</script>
