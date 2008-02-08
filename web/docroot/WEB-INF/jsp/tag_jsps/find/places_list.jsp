<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Find a single user %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	String findPlacesType = (String) request.getAttribute("list_type");
	String findUserFormName = (String) request.getAttribute("form_name");
	String findUserElementName = (String) request.getAttribute("form_element");
	String findUserElementWidth = (String) request.getAttribute("element_width");
%>
<c:set var="prefix" value="<%= findUserFormName + "_" + findUserElementName %>" />
<c:if test="${empty ss_find_user_support_stuff_loaded}">
<script type="text/javascript">
var ss_findUser_searchText_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter> = ""
var ss_findUser_pageNumber = 0;

var ss_findUserSearchInProgress = 0;
var ss_findUserSearchWaiting = 0;
var ss_findUserSearchLastText = "";
var ss_findUserSearchLastTextObjId = "";
var ss_findUserSearchLastElement = "";
var ss_findUserSearchLastfindPlacesType = "";
function ss_findUserSearch(textObjId, elementName, findPlacesType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text != ss_findUserSearchLastText) ss_findUser_pageNumber = 0;
	ss_debug('ss_findUserSearch: '+text+', '+elementName+', '+findPlacesType+', '+ss_findUser_pageNumber)
	ss_setupStatusMessageDiv()
	//ss_moveDivToBody('ss_findUserNavBarDiv_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>');
	//Are we already doing a search?
	if (ss_findUserSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_findUserSearchLastText = text;
		ss_findUserSearchLastTextObjId = textObjId;
		ss_findUserSearchLastElement = elementName;
		ss_findUserSearchLastfindPlacesType = findPlacesType;
		ss_findUserSearchWaiting = 1;
		ss_debug('  hold search request...')
		return;
	}
	ss_findUserSearchInProgress = 1;
	ss_findUserSearchWaiting = 0;
	ss_findUserSearchLastTextObjId = textObjId;
	ss_findUserSearchLastElement = elementName;
	ss_findUserSearchLastText = text;
	ss_findUserSearchLastfindPlacesType = findPlacesType;
 	//Save the text in case the user changes the search type
 	ss_findUser_searchText_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter> = text;
 	
 	//See if the user ended the string with a CR. If so, then try to launch.
 	if (text.match(/\n/)) {
 		textObj.value = text.replace(/\n/g, "");
 		text = textObj.value;
		var ulObj = document.getElementById('available_<%= findUserElementName %>_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findUserSelectItem<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(liObjs[0]);
			return;
		}
 	}
 	ss_debug("//"+text+"//")
 	var url = ss_buildAdapterUrl(ss_AjaxBaseUrl, {operation:"find_user_search"}, "__ajax_find");
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	var searchText = text;
	if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findUser_pageNumber)
	ajaxRequest.addKeyValue("findType", findPlacesType)
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName+"_${prefix}")
	ajaxRequest.addKeyValue("namespace", "<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>");
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_preFindUserRequest);
	ajaxRequest.setPostRequest(ss_postFindUserRequest<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postFindUserRequest<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(obj) {
	ss_debug('ss_postFindUserRequest<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findUserSearchInProgress = 0;

	ss_showDiv('ss_findUserNavBarDiv_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>');
		
	//See if there is another search request to be done
	if (ss_findUserSearchWaiting == 1) {
		document.getElementById('available_'+obj.getData('elementName')+'_${prefix}').innerHTML = "";
		setTimeout('ss_findUserSearch(ss_findUserSearchLastTextObjId, ss_findUserSearchLastElement, ss_findUserSearchLastfindPlacesType)', 100)
	}
}
//Routine called when item is clicked
function ss_findUserSelectItem<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_ws_listing"/><portlet:param name="binderId" 
		value="${ssUser.parentBinder.id}"/><portlet:param name="entryId" 
		value="ss_entryIdPlaceholder"/><portlet:param name="newTab" value="1"/></portlet:renderURL>";
	var id = ss_replaceSubStr(obj.id, 'ss_findPlaces_id_', "");
	url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
	self.location.href = url;
}

function ss_saveFindUserData_${prefix}() {
	ss_debug('ss_saveFindUserData')
	var ulObj = document.getElementById('available_<%= findUserElementName %>_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findUserSelectItem<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>(liObjs[0]);
	}
	return false;
}

function ss_findUserNextPage() {
	ss_findUser_pageNumber++;
	ss_findUserSearch(ss_findUserSearchLastTextObjId, ss_findUserSearchLastElement, ss_findUserSearchLastfindPlacesType);
}

</script>
<c:set var="ss_find_user_support_stuff_loaded" value="1" scope="request"/>
</c:if>

<div style="margin:0px; padding:0px;">
	<ssf:ifaccessible>
 		<label for="ss_findUser_searchText_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>"><ssf:nlt tag="${accessibilityText}" /></label>
 	</ssf:ifaccessible>

	<textarea 
	    class="ss_text" style="height:14px; width:<%= findUserElementWidth %>; overflow:hidden;" 
	    name="ss_findUser_searchText_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" 
	    id="ss_findUser_searchText_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>" 
	    onKeyUp="ss_findUserSearch(this.id, '<%= findUserElementName %>', '<%= findPlacesType %>');" 
	    onBlur="setTimeout('ss_hideDiv(\'ss_findUserNavBarDiv_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>\')', 200);">
	</textarea>
</div>

<div id="ss_findUserNavBarDiv_<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>"
    class="ss_typeToFindResults" style="visibility:hidden;">
    <ul id="available_<%= findUserElementName %>_${prefix}">
    </ul>
</div>	
<input type="hidden" name="<%= findUserElementName %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findUserFormName %>', ss_saveFindUserData_${prefix});
</script>
