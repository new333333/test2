<% // Find a single user %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	String findUserGroupType = (String) request.getAttribute("list_type");
	String findUserFormName = (String) request.getAttribute("form_name");
	String findUserElementName = (String) request.getAttribute("form_element");
	String findUserElementWidth = (String) request.getAttribute("element_width");
%>
<c:set var="prefix" value="<%= findUserFormName + "_" + findUserElementName %>" />
<c:if test="${empty ss_find_user_support_stuff_loaded}">
<script type="text/javascript">
var ss_findUser_searchText = ""
var ss_findUser_pageNumber = 0;
var ss_findUserDivTopOffset = 2;

var ss_findUserSearchInProgress = 0;
var ss_findUserSearchWaiting = 0;
var ss_findUserSearchLastText = "";
var ss_findUserSearchLastTextObjId = "";
var ss_findUserSearchLastElement = "";
var ss_findUserSearchLastfindUserGroupType = "";
function ss_findUserSearch_${prefix}(textObjId, elementName, findUserGroupType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text == '' || text != ss_findUserSearchLastText) ss_findUser_pageNumber = 0;
	ss_setupStatusMessageDiv()
	ss_moveDivToBody('ss_findUserNavBarDiv_<portlet:namespace/>');
	//Are we already doing a search?
	if (ss_findUserSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_findUserSearchLastText = text;
		ss_findUserSearchLastTextObjId = textObjId;
		ss_findUserSearchLastElement = elementName;
		ss_findUserSearchLastfindUserGroupType = findUserGroupType;
		ss_findUserSearchWaiting = 1;
		ss_debug('  hold search request...')
		return;
	}
	ss_findUserSearchInProgress = 1;
	ss_findUserSearchWaiting = 0;
	ss_findUserSearchLastTextObjId = textObjId;
	ss_findUserSearchLastElement = elementName;
	ss_findUserSearchLastText = text;
	ss_findUserSearchLastfindUserGroupType = findUserGroupType;
 	//Save the text in case the user changes the search type
 	ss_findUser_searchText = text;
 	
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
		var ulObj = document.getElementById('available_'+elementName+'_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findUserSelectItem(liObjs[0]);
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
	ajaxRequest.addKeyValue("pageNumber", ss_findUser_pageNumber)
	ajaxRequest.addKeyValue("findType", findUserGroupType)
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName+"_${prefix}")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preFindUserRequest);
	ajaxRequest.setPostRequest(ss_postFindUserRequest);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setData("crFound", crFound)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFindUserRequest(obj) {
	ss_debug('ss_postFindUserRequest')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findUserSearchInProgress = 0;

	var divObj = document.getElementById('ss_findUserNavBarDiv_<portlet:namespace/>');
	ss_moveDivToBody('ss_findUserNavBarDiv_<portlet:namespace/>');
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findUser_searchText_bottom_<portlet:namespace/>") + ss_findUserDivTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findUser_searchText_bottom_<portlet:namespace/>")))
	ss_showDivActivate('ss_findUserNavBarDiv_<portlet:namespace/>');

 	//Show this at full brightness
 	divObj = document.getElementById('available_'+obj.getData('elementName')+'_${prefix}');
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
	
	//See if there is another search request to be done
	if (ss_findUserSearchWaiting == 1) {
		setTimeout('ss_findUserSearch_${prefix}(ss_findUserSearchLastTextObjId, ss_findUserSearchLastElement, ss_findUserSearchLastfindUserGroupType)', 100)
	}

	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout("ss_findUserSelectItem0_${prefix}();", 100);
			return;
		}
	}
}
function ss_findUserSelectItem0_${prefix}() {
	var ulObj = document.getElementById('available_<%= findUserElementName %>_${prefix}');
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findUserSelectItem(liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findUserSelectItem(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_ws_listing"/><portlet:param name="binderId" 
		value="${ssUser.parentBinder.id}"/><portlet:param name="entryId" 
		value="ss_entryIdPlaceholder"/><portlet:param name="newTab" value="1"/></portlet:renderURL>";
	var id = ss_replaceSubStr(obj.id, 'ss_findUser_id_', "");
	url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
	self.location.href = url;
}

function ss_saveFindUserData_${prefix}() {
	ss_debug('ss_saveFindUserData')
	var ulObj = document.getElementById('available_<%= findUserElementName %>_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findUserSelectItem(liObjs[0]);
	}
	return false;
}

function ss_findUserNextPage() {
	ss_findUser_pageNumber++;
	ss_findUserSearch_${prefix}(ss_findUserSearchLastTextObjId, ss_findUserSearchLastElement, ss_findUserSearchLastfindUserGroupType);
}

function ss_findUserPrevPage() {
	ss_findUser_pageNumber--;
	if (ss_findUser_pageNumber < 0) ss_findUser_pageNumber = 0;
	ss_findUserSearch_${prefix}(ss_findUserSearchLastTextObjId, ss_findUserSearchLastElement, ss_findUserSearchLastfindUserGroupType);
}

</script>
<c:set var="ss_find_user_support_stuff_loaded" value="1" scope="request"/>
</c:if>

<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findUserElementWidth %>; overflow:hidden;" 
    name="ss_findUser_searchText_<portlet:namespace/>" 
    id="ss_findUser_searchText_<portlet:namespace/>"
    onKeyUp="ss_findUserSearch_${prefix}(this.id, '<%= findUserElementName %>', '<%= findUserGroupType %>');"
    onBlur="setTimeout('ss_hideDiv(\'ss_findUserNavBarDiv_<portlet:namespace/>\')', 200);"></textarea></div>
<div id="ss_findUser_searchText_bottom_<portlet:namespace/>" style="padding:0px; margin:0px;"></div>
<div id="ss_findUserNavBarDiv_<portlet:namespace/>" 
    class="ss_findUserList" style="visibility:hidden;">
    <div id="available_<%= findUserElementName %>_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
<input type="hidden" name="<%= findUserElementName %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findUserFormName %>', ss_saveFindUserData_${prefix});
</script>
