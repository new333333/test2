<% // Find a single user %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	String findUserGroupType = (String) request.getAttribute("list_type");
	String findUserFormName = (String) request.getAttribute("form_name");
	String findUserElementName = (String) request.getAttribute("form_element");
	String findUserElementWidth = (String) request.getAttribute("element_width");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
	String instanceCount = ((Integer) request.getAttribute("instanceCount")).toString();
	Boolean leaveResultsVisible = (Boolean) request.getAttribute("leaveResultsVisible");
%>
<c:set var="iCount" value="<%= instanceCount %>"/>
<c:set var="prefix" value="${renderResponse.namespace}_${iCount}" />
<script type="text/javascript">
var ss_findUser_searchText${prefix} = ""
var ss_findUser_pageNumber${prefix} = 0;
var ss_findUserDivTopOffset${prefix} = 2;

var ss_findUserSearchInProgress${prefix} = 0;
var ss_findUserSearchWaiting${prefix} = 0;
var ss_findUserSearchStartMs${prefix} = 0;
var ss_findUserSearchLastText${prefix} = "";
var ss_findUserSearchLastTextObjId${prefix} = "";
var ss_findUserSearchLastElement${prefix} = "";
var ss_findUserSearchLastfindUserGroupType${prefix} = "";
var ss_findUserClickRoutine${prefix} = "<%= clickRoutine %>";
function ss_findUserSearch_${prefix}(textObjId, elementName, findUserGroupType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text == '' || text != ss_findUserSearchLastText${prefix}) ss_findUser_pageNumber${prefix} = 0;
	ss_setupStatusMessageDiv()
	//ss_moveDivToBody('ss_findUserNavBarDiv${prefix}');
	//Are we already doing a search?
	if (ss_findUserSearchInProgress${prefix} == 1) {
		//Yes, hold this request until the current one finishes
		ss_findUserSearchLastText${prefix} = text;
		ss_findUserSearchLastTextObjId${prefix} = textObjId;
		ss_findUserSearchLastElement${prefix} = elementName;
		ss_findUserSearchLastfindUserGroupType${prefix} = findUserGroupType;
		ss_findUserSearchWaiting${prefix} = 1;
		var d = new Date();
		var curr_msec = d.getTime();
		if (ss_findUserSearchStartMs${prefix} == 0 || curr_msec < parseInt(ss_findUserSearchStartMs${prefix} + 1000)) {
			ss_debug('  hold search request...')
			if (ss_findUserSearchStartMs${prefix} == 0) ss_findUserSearchStartMs${prefix} = curr_msec;
			return;
		}
		//The user waited for over a second, let this request go through
		ss_findUserSearchStartMs${prefix} = 0;
		ss_debug('   Stopped waiting')
	}
	ss_findUserSearchInProgress${prefix} = 1;
	ss_findUserSearchWaiting${prefix} = 0;
	ss_findUserSearchLastTextObjId${prefix} = textObjId;
	ss_findUserSearchLastElement${prefix} = elementName;
	ss_findUserSearchLastText${prefix} = text;
	ss_findUserSearchLastfindUserGroupType${prefix} = findUserGroupType;
 	//Save the text in case the user changes the search type
 	ss_findUser_searchText${prefix} = text;
 	
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
			ss_findUserSelectItem${prefix}(liObjs[0]);
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
		<ssf:param name="operation" value="find_user_search" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	var searchText = text;
	if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findUser_pageNumber${prefix})
	ajaxRequest.addKeyValue("findType", findUserGroupType)
	ajaxRequest.addKeyValue("listDivId", "available_${prefix}")
	ajaxRequest.addKeyValue("namespace", "${prefix}");
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preFindUserRequest);
	ajaxRequest.setPostRequest(ss_postFindUserRequest${prefix});
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setData("crFound", crFound)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFindUserRequest${prefix}(obj) {
	ss_debug('ss_postFindUserRequest${prefix}')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findUserSearchInProgress${prefix} = 0;

	ss_showFindUserSelections${prefix}();
	
 	//Show this at full brightness
	var divObj = document.getElementById('ss_findUserNavBarDiv_${prefix}');
 	divObj = document.getElementById('available_${prefix}');
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
	
	//See if there is another search request to be done
	if (ss_findUserSearchWaiting${prefix} == 1) {
		setTimeout('ss_findUserSearch_${prefix}(ss_findUserSearchLastTextObjId${prefix}, ss_findUserSearchLastElement${prefix}, ss_findUserSearchLastfindUserGroupType${prefix})', 100)
	}

	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout("ss_findUserSelectItem0_${prefix}();", 100);
			return;
		}
	}
}
function ss_showFindUserSelections${prefix}() {
	var divObj = document.getElementById('ss_findUserNavBarDiv_${prefix}');
	ss_moveDivToBody('ss_findUserNavBarDiv_${prefix}');
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findUser_searchText_bottom_${prefix}") + ss_findUserDivTopOffset${prefix}))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findUser_searchText_bottom_${prefix}")))
	ss_showDivActivate('ss_findUserNavBarDiv_${prefix}');
}
function ss_findUserSelectItem0_${prefix}() {
	var ulObj = document.getElementById('available_${prefix}');
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findUserSelectItem${prefix}(liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findUserSelectItem${prefix}(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_ws_listing"/><portlet:param name="binderId" 
		value="${ssUser.parentBinder.id}"/><portlet:param name="entryId" 
		value="ss_entryIdPlaceholder"/><portlet:param name="newTab" value="1"/></portlet:renderURL>";
	var id = ss_replaceSubStr(obj.id, 'ss_findUser_id_', "");
	if (ss_findUserClickRoutine${prefix} != "") {
		eval(ss_findUserClickRoutine${prefix} + "('"+id+"');")
		<% if (leaveResultsVisible) { %>
		  setTimeout("ss_showFindUserSelections${prefix}();", 200)
		<% } %>
	} else {
		url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
		self.location.href = url;
	}
}

function ss_saveFindUserData_${prefix}() {
	ss_debug('ss_saveFindUserData')
	var ulObj = document.getElementById('available_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findUserSelectItem${prefix}(liObjs[0]);
	}
	return false;
}

function ss_findUserNextPage${prefix}() {
	ss_findUser_pageNumber${prefix}++;
	ss_findUserSearch_${prefix}(ss_findUserSearchLastTextObjId${prefix}, ss_findUserSearchLastElement${prefix}, ss_findUserSearchLastfindUserGroupType${prefix});
}

function ss_findUserPrevPage${prefix}() {
	ss_findUser_pageNumber${prefix}--;
	if (ss_findUser_pageNumber${prefix} < 0) ss_findUser_pageNumber${prefix} = 0;
	ss_findUserSearch_${prefix}(ss_findUserSearchLastTextObjId${prefix}, ss_findUserSearchLastElement${prefix}, ss_findUserSearchLastfindUserGroupType${prefix});
}

</script>

<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findUserElementWidth %>; overflow:hidden;" 
    name="ss_findUser_searchText_${prefix}" 
    id="ss_findUser_searchText_${prefix}"
    onKeyUp="ss_findUserSearch_${prefix}(this.id, '<%= findUserElementName %><%= instanceCount %>', '<%= findUserGroupType %>');"
    onBlur="setTimeout('ss_hideDiv(\'ss_findUserNavBarDiv_${prefix}\')', 200);"></textarea></div>
<div id="ss_findUser_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findUserNavBarDiv_${prefix}" 
    class="ss_findUserList" style="visibility:hidden;">
    <div id="available_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
<input type="hidden" name="<%= findUserElementName %><%= instanceCount %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findUserFormName %>', ss_saveFindUserData_${prefix});
</script>
