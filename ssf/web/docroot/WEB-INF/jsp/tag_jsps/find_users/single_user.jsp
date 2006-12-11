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

var ss_findUserSearchInProgress = 0;
var ss_findUserSearchWaiting = 0;
var ss_findUserSearchLastText = "";
var ss_findUserSearchLastTextObjId = "";
var ss_findUserSearchLastElement = "";
var ss_findUserSearchLastfindUserGroupType = "";
function ss_findUserSearch(textObjId, elementName, findUserGroupType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text == '' || text != ss_findUserSearchLastText) ss_findUser_pageNumber = 0;
	ss_debug('ss_findUserSearch: '+text+', '+elementName+', '+findUserGroupType+', '+ss_findUser_pageNumber)
	ss_setupStatusMessageDiv()
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
 	if (text.match(/\n/)) {
 		textObj.value = text.replace(/\n/g, "");
 		text = textObj.value;
		var ulObj = document.getElementById('available_<%= findUserElementName %>_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findUserSelectItem(liObjs[0]);
			return;
		}
 	}
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
	ajaxRequest.addKeyValue("maxEntries", "3")
	ajaxRequest.addKeyValue("pageNumber", ss_findUser_pageNumber)
	ajaxRequest.addKeyValue("findUserGroupType", findUserGroupType)
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName+"_${prefix}")
	ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preFindUserRequest);
	ajaxRequest.setPostRequest(ss_postFindUserRequest);
	ajaxRequest.setData("elementName", elementName)
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

	ss_showDiv('ss_findUserNavBarDiv_<portlet:namespace/>');
		
	//See if there is another search request to be done
	if (ss_findUserSearchWaiting == 1) {
		document.getElementById('available_'+obj.getData('elementName')+'_${prefix}').innerHTML = "";
		setTimeout('ss_findUserSearch(ss_findUserSearchLastTextObjId, ss_findUserSearchLastElement, ss_findUserSearchLastfindUserGroupType)', 100)
	}
}
//Routine called when item is clicked
function ss_findUserSelectItem(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<portlet:renderURL windowState="maximized">
				<portlet:param name="action" value="view_ws_listing"/>
				<portlet:param name="binderId" value="${ssUser.parentBinder.id}"/>
				<portlet:param name="entryId" value="ss_entryIdPlaceholder"/>
				<portlet:param name="newTab" value="1"/>
				</portlet:renderURL>";
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
	ss_findUserSearch(ss_findUserSearchLastTextObjId, ss_findUserSearchLastElement, ss_findUserSearchLastfindUserGroupType);
}

</script>
<c:set var="ss_find_user_support_stuff_loaded" value="1" scope="request"/>
</c:if>

<textarea class="ss_text" style="height:17px; width:<%= findUserElementWidth %>;" 
  name="ss_findUser_searchText" 
  id="ss_findUser_searchText"
  onKeyUp="ss_findUserSearch(this.id, '<%= findUserElementName %>', '<%= findUserGroupType %>');"
  onBlur="setTimeout('ss_hideDiv(\'ss_findUserNavBarDiv_<portlet:namespace/>\')', 200);"></textarea>
<input type="hidden" name="<%= findUserElementName %>"/>

  <div id="ss_findUserNavBarDiv_<portlet:namespace/>"
    class="ss_findUserList" style="visibility:hidden;">
    <ul id="available_<%= findUserElementName %>_${prefix}">
    </ul>
  </div>	
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findUserFormName %>', ss_saveFindUserData_${prefix});
</script>
