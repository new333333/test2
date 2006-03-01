<% // Group list %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ArrayList" %>
<%
	List groupList = (List) request.getAttribute("group_list");
	String groupListFormName = (String) request.getAttribute("form_name");
	String groupListElementName = (String) request.getAttribute("form_element");
%>
<c:set var="prefix" value="<%= groupListFormName + "_" + groupListElementName %>" />
<c:if test="${empty ss_group_list_support_stuff_loaded}">
<c:if test="${empty ss_taconite_loaded}">
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script type="text/javascript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<c:set var="ss_taconite_loaded" value="1" scope="request"/>
</c:if>
<script type="text/javascript">
var ss_groupList_searchText = ""
var ss_groupList_searchType = "lastName"
function ss_groupListSetSearchType(type, elementName) {
	ss_groupList_searchType = type;
	if (ss_groupList_searchText != "") {
		//Re-do the search with the new type
		ss_groupListSearch(ss_groupList_searchText, elementName);
	}
}
var ss_groupListSearchInProgress = 0;
var ss_groupListSearchWaiting = 0;
var ss_groupListSearchLastText = "";
var ss_groupListSearchLastElement = "";
function ss_groupListSearch(text, elementName) {
	//Are we already doing a search?
	if (ss_groupListSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_groupListSearchLastText = text;
		ss_groupListSearchLastElement = elementName;
		ss_groupListSearchWaiting = 1;
		return;
	}
	ss_groupListSearchInProgress = 1;
	ss_groupListSearchWaiting = 0;
 	//Save the text in case the user changes the search type
 	ss_groupList_searchText = text;
 	
 	//Build a list of the groupIds already added
 	var addedObj = document.getElementById('added_<%= groupListElementName %>');
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
		<ssf:param name="operation" value="group_list_search" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("searchText", text + "*")
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("searchType", ss_groupList_searchType)
	ajaxRequest.addKeyValue("idsToSkip", addedIds)
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preGroupListRequest);
	ajaxRequest.setPostRequest(ss_postGroupListRequest);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postGroupListRequest(obj) {
	//See if there was an error
	if (self.document.getElementById("ss_search_status_message").innerHTML == "error") {
		alert("<ssf:nlt tag="forum.unseenCounts.notLoggedIn" text="Your session has timed out. Please log in again."/>");
	} else {
		//Re-enable the dragable lists
		ss_DragDrop.initializeListContainer();
		ss_DragDrop.makeListContainer( document.getElementById('added_'+obj.getData('elementName')));
		ss_DragDrop.makeListContainer( document.getElementById('available_'+obj.getData('elementName')));
	}
	ss_groupListSearchInProgress = 0;
	
	//See if there is another search request to be done
	if (ss_groupListSearchWaiting == 1) {
		setTimeout('ss_groupListSearch(ss_groupListSearchLastText, ss_groupListSearchLastElement)', 100)
	}
}
//Routine called when item is double clicked
//  Move the item from one list to the other
function ss_groupListMoveItem(obj) {
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
function ss_groupListInterceptCR(e) {
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
<c:set var="ss_group_list_support_stuff_loaded" value="1" scope="request"/>
</c:if>
<table class="ss_style" cellspacing="2px" cellpadding="5px">
<tr>
<td valign="top">
<div>
  <div style="border:solid #cecece 1px;">
    <ul id="added_<%= groupListElementName %>" class="ss_dragable ss_grouplist">
      <c:forEach var="item" items="${groupList}">
        <li id="<c:out value="${item.id}"/>" 
          class="ss_dragable ss_grouplist"><c:out value="${item.title}"/></li>
      </c:forEach>
    </ul>
  </div>
</div>
<input type="hidden" name="<%= groupListElementName %>">
</td>
<td valign="top">
  <div style="margin:0px; padding:0px;"><img 
    src="<html:imagesPath />pics/sym_s_arrow_left.gif" 
    alt="<ssf:nlt tag="grouplist.dragLeft" text="Drag to the left to add a name."/>"
    ><br><img 
    src="<html:imagesPath />pics/sym_s_arrow_right.gif" 
    alt="<ssf:nlt tag="grouplist.dragRight" 
      text="Drag to the right to delete a name."/>"
    ></div>
</td>
<td valign="top">
  <div>
    <b><ssf:nlt tag="grouplist.findName" text="Find name"/>:</b>
    <input type="text" size="15" name="ss_groupList_searchText" 
      onKeyUp="ss_groupListSearch(this.value, '<%= groupListElementName %>');">
    <div style="border:solid #cecece 1px;">
      <ul id="available_<%= groupListElementName %>" class="ss_dragable ss_grouplist">
      </ul>
    </div>
  </div>	
</td>
<td valign="top">
  <div>
	  <input type="radio" name="ss_groupList_searchType" value="firstName"
	    onClick="ss_groupListSetSearchType(this.value, '<%= groupListElementName %>');">
	  <ssf:nlt tag="grouplist.firstName" text="First name"/><br>
	  <input type="radio" name="ss_groupList_searchType" value="lastName" checked
	    onClick="ss_groupListSetSearchType(this.value, '<%= groupListElementName %>');">
	  <ssf:nlt tag="grouplist.lastName" text="Last name"/><br>
	  <input type="radio" name="ss_groupList_searchType" value="loginName"
	    onClick="ss_groupListSetSearchType(this.value, '<%= groupListElementName %>');">
	  <ssf:nlt tag="grouplist.loginName" text="Login name"/>
  </div>
<input type="hidden" name="<%= groupListElementName %>" id="<%= groupListElementName %>">
</td>
</tr>
</table>
  
<script type="text/javascript">
  ss_DragDrop.makeListContainer( document.getElementById('added_<%= groupListElementName %>'));
  ss_DragDrop.makeListContainer( document.getElementById('available_<%= groupListElementName %>'));

function ss_saveGroupListData_<portlet:namespace/>_${prefix}() {
	var elementObj = document.getElementById('<%= groupListElementName %>');
	var addedObj = document.getElementById('added_<%= groupListElementName %>');

	var s = "";
	var items = addedObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		s += items[i].id + " ";
	}
	elementObj.value = s;
	return true;
}

function ss_groupListKeyPress_<portlet:namespace/>_${prefix}(e) {
	//Save the current list of selected group ids
	ss_saveGroupListData_<portlet:namespace/>_${prefix}()
	//Then call the general keyPress handler
	ss_groupListInterceptCR(e)
}

ss_createOnSubmitObj('${prefix}onSubmit', '<%= groupListFormName %>', ss_saveGroupListData_<portlet:namespace/>_${prefix});
ss_createEventObj('ss_groupListKeyPress_<portlet:namespace/>_${prefix}', 'KEYPRESS');
</script>
