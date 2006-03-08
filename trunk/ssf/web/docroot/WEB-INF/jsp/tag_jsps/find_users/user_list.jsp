<% // User/Group list widget %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	Set userList = (Set) request.getAttribute("user_list");
	String userGroupType = (String) request.getAttribute("list_type");
	String userListFormName = (String) request.getAttribute("form_name");
	String userListElementName = (String) request.getAttribute("form_element");
%>
<c:set var="userList" value="<%= userList %>"/>
<c:set var="prefix" value="<%= userListFormName + "_" + userListElementName %>" />
<c:if test="${empty ss_user_list_support_stuff_loaded}">
<c:if test="${empty ss_taconite_loaded}">
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<c:set var="ss_taconite_loaded" value="1" scope="request"/>
</c:if>
<script type="text/javascript">
var ss_userList_searchText = ""
var ss_userList_searchType = "lastName"
var ss_groupList_searchType = "title"
function ss_userListSetSearchType(type, elementName, userGroupType) {
	if (userGroupType == 'group') {
		ss_groupList_searchType = type;
	} else {
		ss_userList_searchType = type;
	}
	if (ss_userList_searchText != "") {
		//Re-do the search with the new type
		ss_userListSearch(ss_userList_searchText, elementName, userGroupType);
	}
}
var ss_userListSearchInProgress = 0;
var ss_userListSearchWaiting = 0;
var ss_userListSearchLastText = "";
var ss_userListSearchLastElement = "";
var ss_userListSearchLastUserGroupType = "";
function ss_userListSearch(text, elementName, userGroupType) {
	//Are we already doing a search?
	if (ss_userListSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_userListSearchLastText = text;
		ss_userListSearchLastElement = elementName;
		ss_userListSearchLastUserGroupType = userGroupType;
		ss_userListSearchWaiting = 1;
		return;
	}
	ss_userListSearchInProgress = 1;
	ss_userListSearchWaiting = 0;
 	//Save the text in case the user changes the search type
 	ss_userList_searchText = text;
 	
 	//Build a list of the userIds already added
 	var addedObj = document.getElementById('added_'+elementName);
	var addedIds = "";
	var items = addedObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		addedIds += items[i].id + " ";
	}
 	
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="operation" value="user_list_search" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("searchText", text + "*")
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName)
	ajaxRequest.addKeyValue("maxEntries", "10")
	if (userGroupType == 'group') {
		ajaxRequest.addKeyValue("searchType", ss_groupList_searchType)
	} else {
		ajaxRequest.addKeyValue("searchType", ss_userList_searchType)
	}
	ajaxRequest.addKeyValue("userGroupType", userGroupType)
	ajaxRequest.addKeyValue("idsToSkip", addedIds)
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preUserListRequest);
	ajaxRequest.setPostRequest(ss_postUserListRequest);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postUserListRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_search_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="forum.unseenCounts.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	} else {
		//Re-enable the dragable lists
		ss_DragDrop.initializeListContainer();
		ss_DragDrop.makeListContainer( document.getElementById('added_'+obj.getData('elementName')));
		ss_DragDrop.makeListContainer( document.getElementById('available_'+obj.getData('elementName')));
	}
	ss_userListSearchInProgress = 0;
	
	//See if there is another search request to be done
	if (ss_userListSearchWaiting == 1) {
		setTimeout('ss_userListSearch(ss_userListSearchLastText, ss_userListSearchLastElement, ss_userListSearchLastUserGroupType)', 100)
	}
}
//Routine called when item is double clicked
//  Move the item from one list to the other
function ss_userListMoveItem(obj) {
	//Get the names of the source and target lists
	var elementName = obj.offsetParent.id.substr(obj.offsetParent.id.indexOf("_")+1);
	var sourceList = obj.offsetParent.id.substr(0, obj.offsetParent.id.indexOf("_"));
	var targetList = "added_" + elementName;
	if (sourceList == "added") targetList = "available_" + elementName;
	
	//Now move the list item to the target list
	var item = document.getElementById(obj.id);
	var sourceListNode = item.parentNode;
	var targetListNode = document.getElementById(targetList);
	sourceListNode.removeChild( item );
	targetListNode.appendChild( item );
}

//Routine to prevent early form submission
function ss_userListInterceptCR(e) {
	var key = "";
	if (window.event) {
		e = window.event;
		if (e.keyCode == 13 || e.keyCode == 9) {e.keyCode = 0;return true}
	} else {
		if (e.which == 13 || e.which == 9) {e.which = 0; return true}
	}
	return false
}

</script>
<div id="ss_search_status_message"></div>
<c:set var="ss_user_list_support_stuff_loaded" value="1" scope="request"/>
</c:if>
<table class="ss_style" cellspacing="2px" cellpadding="5px">
<tr>
<td valign="top">
<div>
  <div style="border:solid ${ss_form_element_border_color} 1px;">
    <ul id="added_<%= userListElementName %>" class="ss_dragable ss_userlist">
      <c:forEach var="item" items="${userList}">
        <li id="<c:out value="${item.id}"/>" 
          onDblClick="ss_userListMoveItem(this);" 
          class="ss_dragable ss_userlist"><c:out value="${item.title}"/></li>
      </c:forEach>
    </ul>
  </div>
</div>
<input type="hidden" name="<%= userListElementName %>">
</td>
<td valign="top">
  <div style="margin:0px; padding:0px;"><img 
    src="<html:imagesPath />pics/sym_s_arrow_left.gif" 
    alt="<ssf:nlt tag="userlist.dragLeft" text="Drag to the left to add a name."/>"
    ><br><img 
    src="<html:imagesPath />pics/sym_s_arrow_right.gif" 
    alt="<ssf:nlt tag="userlist.dragRight" 
      text="Drag to the right to delete a name."/>"
    ></div>
</td>
<td valign="top">
  <div>
    <b><ssf:nlt tag="userlist.findName" text="Find name"/>:</b>
    <input type="text" class="ss_text" size="15" name="ss_userList_searchText" 
      onKeyUp="ss_userListSearch(this.value, '<%= userListElementName %>', '<%= userGroupType %>');">
    <div style="border:solid ${ss_form_element_border_color} 1px;">
      <ul id="available_<%= userListElementName %>" class="ss_dragable ss_userlist">
      </ul>
    </div>
  </div>	
</td>
<td valign="top">
  <div>
<%
	if (userGroupType.equals("group")) {
%>
	  <input type="radio" name="ss_groupList_searchType" value="title" checked
	    onClick="ss_userListSetSearchType(this.value, '<%= userListElementName %>', '<%= userGroupType %>');">
	  <ssf:nlt tag="userlist.groupTitle" text="Group title"/><br>
	  <input type="radio" name="ss_groupList_searchType" value="groupName"
	    onClick="ss_userListSetSearchType(this.value, '<%= userListElementName %>', '<%= userGroupType %>');">
	  <ssf:nlt tag="userlist.groupName" text="Group name"/><br>
<%
	} else {
%>
	  <input type="radio" name="ss_userList_searchType" value="firstName"
	    onClick="ss_userListSetSearchType(this.value, '<%= userListElementName %>', '<%= userGroupType %>');">
	  <ssf:nlt tag="userlist.firstName" text="First name"/><br>
	  <input type="radio" name="ss_userList_searchType" value="lastName" checked
	    onClick="ss_userListSetSearchType(this.value, '<%= userListElementName %>', '<%= userGroupType %>');">
	  <ssf:nlt tag="userlist.lastName" text="Last name"/><br>
	  <input type="radio" name="ss_userList_searchType" value="loginName"
	    onClick="ss_userListSetSearchType(this.value, '<%= userListElementName %>', '<%= userGroupType %>');">
	  <ssf:nlt tag="userlist.loginName" text="Login name"/>
<%
	}
%>
  </div>
<input type="hidden" name="<%= userListElementName %>" id="<%= userListElementName %>">
</td>
</tr>
</table>
  
<script type="text/javascript">
  ss_DragDrop.makeListContainer( document.getElementById('added_<%= userListElementName %>'));
  ss_DragDrop.makeListContainer( document.getElementById('available_<%= userListElementName %>'));

function ss_saveUserListData_<portlet:namespace/>_${prefix}() {
	var elementObj = document.getElementById('<%= userListElementName %>');
	var addedObj = document.getElementById('added_<%= userListElementName %>');

	var s = "";
	var items = addedObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		s += items[i].id + " ";
	}
	elementObj.value = s;
	return true;
}

function ss_userListKeyPress_<portlet:namespace/>_${prefix}(e) {
	//Save the current list of selected user ids
	ss_saveUserListData_<portlet:namespace/>_${prefix}()
	//Then call the general keyPress handler
	ss_userListInterceptCR(e)
}

ss_createOnSubmitObj('${prefix}onSubmit', '<%= userListFormName %>', ss_saveUserListData_<portlet:namespace/>_${prefix});
ss_createEventObj('ss_userListKeyPress_<portlet:namespace/>_${prefix}', 'KEYPRESS');
</script>
