<% // User list %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssConfigDefinition" type="org.dom4j.Document" scope="request" />
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
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-client.js"></script>
<script language="JavaScript" src="<html:rootPath/>js/common/taconite-parser.js"></script>
<style type="text/css">
ul.ss_dragable {
    position: relative;
    list-style-type: none;
    margin: 0px;
    padding: 0px;
    width: 200px;
}

ul.ss_dragable li {
    position: relative;
    cursor: move;
    margin: 0px;
    text-align: left;
}
</style>
<table>
  <tr>
    <td valign="top">
		<div ><%= caption %>
		<ul id="added_<%= elementName %>" class="ss_dragable">
			<li id="user1" class="sortList">user 1</li>
			<li id="user2"  class="sortList">user 2</li>
			<li id="user3"  class="sortList">user 3</li>
		</ul>
		</div>
	</td>
	<td valign="top">
	    <input type="text" size="40" name="ss_userList_searchText" onKeyUp="ss_userListSearch(this.value, 'available_<%= elementName %>');">
	    <br>
	    <input type="radio" name="ss_userList_searchType" value="firstName">
	    First name,
	    <input type="radio" name="ss_userList_searchType" value="lastName" checked>
	    Last name,
	    <input type="radio" name="ss_userList_searchType" value="loginName">
	    Login name
	  <br>
	  <ul id="available_<%= elementName %>" class="ss_dragable">
			<li id="avuser1" class="sortList">available user 1</li>
			<li id="avuser2"  class="sortList">available user 2</li>
			<li id="avuser3"  class="sortList">available user 3</li>
	  </ul>
	</td>
  </tr>
</table>
<script language="JavaScript" type="text/javascript">
function ss_userListSearch(text, listDivId) {
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="true" >
		<ssf:param name="operation" value="user_list_search" />
    	</ssf:url>"
	var ajaxRequest = new AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("searchText", text)
	ajaxRequest.addKeyValue("listDivId", listDivId)
	ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preUserListRequest);
	//ajaxRequest.setPostRequest(ss_postUserListRequest);
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}

  ss_DragDrop.makeListContainer( document.getElementById('added_<%= elementName %>'));
  ss_DragDrop.makeListContainer( document.getElementById('available_<%= elementName %>'));
</script>

