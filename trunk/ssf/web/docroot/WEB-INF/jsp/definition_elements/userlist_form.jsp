<% // User list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="formName" type="String" scope="request" />
<jsp:useBean id="property_name" type="String" scope="request" />
<%
	String elementName = (String) request.getAttribute("property_name");
	String caption = (String) request.getAttribute("property_caption");
	String width = (String) request.getAttribute("property_width");
	if (width == null || width.equals("")) {
		width = "";
	} else {
		width = "width='"+width+"'";
	}
	if (caption == null || caption.equals("")) {
		caption = "";
	} else {
		caption = "<b>"+caption+"</b><br>";
	}
%>
<c:set var="prefix" value="${formName}_${property_name}" />
<c:if test="${empty ss_user_list_support_stuff_loaded}">
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<script language="JavaScript" type="text/javascript">
var ss_userList_searchText = ""
var ss_userList_searchType = "lastName"
function ss_userListSetSearchType(type, elementName) {
	ss_userList_searchType = type;
	if (ss_userList_searchText != "") {
		//Re-do the search with the new type
		ss_userListSearch(ss_userList_searchText, elementName);
	}
}
function ss_userListSearch(text, elementName) {
 	//Save the text in case the user changes the search type
 	ss_userList_searchText = text;
 	
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
	ajaxRequest.addKeyValue("searchType", ss_userList_searchType)
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
}
</script>
<div id="ss_search_status_message"></div>
</c:if>
<c:set var="ss_user_list_support_stuff_loaded" value="1" scope="request"/>
<table>
  <tr>
    <td valign="top">
		<div><%= caption %>
		  <div style="border:solid #cecece 1px;">
		    <ul id="added_<%= elementName %>" class="ss_dragable ss_userlist">
		    </ul>
		  </div>
		</div>
		<input type="hidden" name="<%= elementName %>">
	</td>
	<td valign="middle">
	  <div style="margin:0px; padding:0px;"><img 
	    src="<html:imagesPath />pics/sym_s_arrow_left.gif" 
	    alt="<ssf:nlt tag="userlist.dragLeft" text="Drag to the left to add a name."/>"
	    ></div>
	  <div style="margin:0px; padding:0px;"><img 
	    src="<html:imagesPath />pics/sym_s_arrow_right.gif" 
	    alt="<ssf:nlt tag="userlist.dragRight" text="Drag to the right to delete a name."/>"
	    ></div>
	</td>
	<td valign="top">
	  <table cellspacing="0" cellpadding="0">
	  <tr>
	    <td colspan="2" valign="top" nowrap>
	      <b><ssf:nlt tag="userlist.findName" text="Find name"/>:</b>
	      <input type="text" size="15" name="ss_userList_searchText" 
	        onKeyUp="ss_userListSearch(this.value, '<%= elementName %>');">
	    </td>
	  </tr>
	  <tr>
	    <td valign="top">
		  <div style="border:solid #cecece 1px;">
		    <ul id="available_<%= elementName %>" class="ss_dragable ss_userlist">
		    </ul>
		  </div>
	    </td>
	    <td valign="top">
	      <input type="radio" name="ss_userList_searchType" value="firstName"
	        onClick="ss_userListSetSearchType(this.value, '<%= elementName %>');">
	      <ssf:nlt tag="userlist.firstName" text="First name"/><br>
	      <input type="radio" name="ss_userList_searchType" value="lastName" checked
	        onClick="ss_userListSetSearchType(this.value, '<%= elementName %>');">
	      <ssf:nlt tag="userlist.lastName" text="Last name"/><br>
	      <input type="radio" name="ss_userList_searchType" value="loginName"
	        onClick="ss_userListSetSearchType(this.value, '<%= elementName %>');">
	      <ssf:nlt tag="userlist.loginName" text="Login name"/>
	    </td>
	  </tr>
	  </table>
	</td>
  </tr>
</table>
<script language="JavaScript" type="text/javascript">
  ss_DragDrop.makeListContainer( document.getElementById('added_<%= elementName %>'));
  ss_DragDrop.makeListContainer( document.getElementById('available_<%= elementName %>'));

function ss_saveUserListData_<portlet:namespace/>_${prefix}() {
	var elementObj = document.getElementById('<%= elementName %>');
	var addedObj = document.getElementById('added_<%= elementName %>');

	var s = "";
	var items = addedObj.getElementsByTagName( "li" );
	for (var i = 0; i < items.length; i++) {
		s += items[i].id + " ";
	}
	elementObj.value = s;
	return true;
}
createOnSubmitObj('${prefix}onSubmit', '${formName}', ss_saveUserListData_<portlet:namespace/>_${prefix});
</script>

<div id="debugLog">
</div>
