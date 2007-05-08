<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<% // Find a single place %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%
	String findPlacesType = (String) request.getAttribute("list_type");
	String findPlacesFormName = (String) request.getAttribute("form_name");
	String findPlacesElementName = (String) request.getAttribute("form_element");
	String findPlacesElementWidth = (String) request.getAttribute("element_width");
%>
<c:set var="prefix" value="<%= findPlacesFormName + "_" + findPlacesElementName %>" />
<c:set var="prefix" value="${prefix}_${renderResponse.namespace}"/>
<c:if test="${empty ss_find_places_support_stuff_loaded}">
<script type="text/javascript">
var ss_findPlaces_searchText = ""
var ss_findPlaces_pageNumber = 0;
var ss_findPlaces_pageNumberBefore = -1;
var ss_findPlacesDivTopOffset = 2;

var ss_findPlacesSearchInProgress = 0;
var ss_findPlacesSearchWaiting = 0;
var ss_findPlacesSearchStartMs = 0;
var ss_findPlacesSearchLastText = "";
var ss_findPlacesSearchLastTextObjId = "";
var ss_findPlacesSearchLastElement = "";
var ss_findPlacesSearchLastfindPlacesType = "";
var ss___findPlacesIsMouseOverList = false;
function ss_findPlacesSearch_${prefix}(textObjId, elementName, findPlacesType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text.trim() != ss_findPlacesSearchLastText.trim()) {
		ss_findPlaces_pageNumber = 0;
		ss_findPlaces_pageNumberBefore = 0;
	}
	ss_setupStatusMessageDiv()
	ss_moveDivToBody('ss_findPlacesNavBarDiv_${prefix}');
	//Are we already doing a search?
	if (ss_findPlacesSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_findPlacesSearchLastText = text;
		ss_findPlacesSearchLastTextObjId = textObjId;
		ss_findPlacesSearchLastElement = elementName;
		ss_findPlacesSearchLastfindPlacesType = findPlacesType;
		ss_findPlacesSearchWaiting = 1;
		var d = new Date();
		var curr_msec = d.getTime();
		if (ss_findPlacesSearchStartMs == 0 || curr_msec < ss_findPlacesSearchStartMs + 1000) {
			ss_debug('  hold search request...')
			if (ss_findPlacesSearchStartMs == 0) ss_findPlacesSearchStartMs = curr_msec;
			return;
		}
		//The user waited for over a second, let this request go through
		ss_findPlacesSearchStartMs = 0;
		ss_debug('   Stopped waiting')
	}
	ss_findPlacesSearchInProgress = 1;
	ss_findPlacesSearchWaiting = 0;
	ss_findPlacesSearchLastTextObjId = textObjId;
	ss_findPlacesSearchLastElement = elementName;
	ss_findPlacesSearchLastText = text;
	ss_findPlacesSearchLastfindPlacesType = findPlacesType;
 	//Save the text in case the user changes the search type
 	ss_findPlaces_searchText = text;
 	
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
		var ulObj = document.getElementById('available_<%= findPlacesElementName %>_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			var placeLink = liObjs[0].getElementsByTagName('a')[0];
			placeLink.onclick();

//  next function, called without type, uses permalink - loop problem
//	ss_findPlacesSelectItem${prefix}(liObjs[0]);
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

 	ss_debug("Page number: " + ss_findPlaces_pageNumber + ", //"+text+"//")
	var searchText = text;
	if (searchText.length > 0 && searchText.charAt(searchText.length-1) != " ") {
		if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	}
	if (ss_userDisplayStyle && ss_userDisplayStyle == 'accessible') {
		ss_findPlaceSearchAccessible_${prefix}(searchText, elementName, findPlacesType, crFound);
		ss_findPlacesSearchInProgress = 0;
		return;
	}
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="operation" value="find_places_search" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findPlaces_pageNumber)
	ajaxRequest.addKeyValue("findType", findPlacesType)
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName+"_${prefix}")
	ajaxRequest.addKeyValue("namespace", "${prefix}")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_prefindPlacesRequest);
	ajaxRequest.setPostRequest(ss_postfindPlacesRequest${prefix});
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setData("crFound", crFound)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postfindPlacesRequest${prefix}(obj) {
	ss_debug('ss_postfindPlacesRequest${prefix}')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findPlacesSearchInProgress = 0;

	var divObj = document.getElementById('ss_findPlacesNavBarDiv_${prefix}');
	ss_moveDivToBody('ss_findPlacesNavBarDiv_${prefix}');
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findPlaces_searchText_bottom_${prefix}") + ss_findPlacesDivTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findPlaces_searchText_bottom_${prefix}")))
	ss_showDivActivate('ss_findPlacesNavBarDiv_${prefix}');
		
 	//Show this at full brightness
 	divObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}');
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
		
	//See if there is another search request to be done
	if (ss_findPlacesSearchWaiting == 1) {
		setTimeout('ss_findPlacesSearch_${prefix}(ss_findPlacesSearchLastTextObjId, ss_findPlacesSearchLastElement, ss_findPlacesSearchLastfindPlacesType)', 100)
	}
	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout("ss_findPlacesSelectItem0_${prefix}();", 100);
			return;
		}
	}
}
function ss_findPlacesSelectItem0_${prefix}() {
	var ulObj = document.getElementById('available_<%= findPlacesElementName %>_${prefix}');
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findPlacesSelectItem${prefix}(liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findPlacesSelectItem${prefix}(obj, type) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url="<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="ssActionPlaceHolder"/><portlet:param name="binderId" 
		value="ss_binderIdPlaceholder"/><portlet:param name="newTab" value="1"/></portlet:renderURL>";
	var id = ss_replaceSubStr(obj.id, 'ss_findPlaces_id_', "");
	url = ss_replaceSubStr(url, 'ss_binderIdPlaceholder', id);
	if (type == 'folder') {
		url = ss_replaceSubStr(url, 'ssActionPlaceHolder', 'view_folder_listing');
	} else if (type == 'workspace') {
		url = ss_replaceSubStr(url, 'ssActionPlaceHolder', 'view_ws_listing');
	} else if (type == 'profiles') {
		url = ss_replaceSubStr(url, 'ssActionPlaceHolder', 'view_profile_listing');
	} else {
		url = ss_replaceSubStr(url, 'ssActionPlaceHolder', 'view_permalink');
	}
	self.location.href = url;	
	return false;
}

function ss_savefindPlacesData_${prefix}() {
	ss_debug('ss_savefindPlacesData')
	var ulObj = document.getElementById('available_<%= findPlacesElementName %>_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findPlacesSelectItem${prefix}(liObjs[0]);
	}
	return false;
}

function ss_findPlacesNextPage${prefix}() {
	ss_findPlaces_pageNumberBefore = ss_findPlaces_pageNumber;
	ss_findPlaces_pageNumber++;
	setTimeout("ss_findPlacesSearch_${prefix}(ss_findPlacesSearchLastTextObjId, ss_findPlacesSearchLastElement, ss_findPlacesSearchLastfindPlacesType);", 100);
}

function ss_findPlacesPrevPage${prefix}() {
	ss_findPlaces_pageNumberBefore = ss_findPlaces_pageNumber;
	ss_findPlaces_pageNumber--;
	if (ss_findPlaces_pageNumber < 0) ss_findPlaces_pageNumber = 0;
	ss_findPlacesSearch_${prefix}(ss_findPlacesSearchLastTextObjId, ss_findPlacesSearchLastElement, ss_findPlacesSearchLastfindPlacesType);
}

function ss_findPlacesClose${prefix}() {
	document.getElementById('ss_findPlaces_searchText_${prefix}').focus();
}


function ss_findPlacesBlurTextArea${prefix}() {
	if (!ss___findPlacesIsMouseOverList) {
		setTimeout(function() { ss_hideDiv('ss_findPlacesNavBarDiv_${prefix}') } , 200);
	}
}

function ss_findPlacesMouseOverList${prefix}() {
	ss___findPlacesIsMouseOverList = true;
}

function ss_findPlacesMouseOutList${prefix}() {
	ss___findPlacesIsMouseOverList = false;
}

function ss_findPlaceSearchAccessible_${prefix}(searchText, elementName, findPlacesType, crFound) {
	//In accessibility mode, wait for the user to type cr
	if (!crFound && parseInt(ss_findPlaces_pageNumber) == 0 && 
			parseInt(ss_findPlaces_pageNumberBefore) == 0) return;
	
    var iframeDivObj = self.document.getElementById("ss_findPlacesIframeDiv");
    var iframeObj = self.document.getElementById("ss_findPlacesIframe");
    var iframeDivObjParent = self.parent.document.getElementById("ss_findPlacesIframeDiv");
    var iframeObjParent = self.parent.document.getElementById("ss_findPlacesIframe");
    var textObj = self.document.getElementById('ss_findPlaces_searchText_bottom_${prefix}');
    if (iframeDivObjParent == null && iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
	    iframeDivObjParent = iframeDivObj;
        iframeDivObj.setAttribute("id", "ss_findPlacesIframeDiv");
		iframeDivObj.className = "ss_popupMenu";
		iframeDivObj.style.zIndex = ssPopupZ;
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_findPlacesIframe");
        iframeObj.style.width = "400px"
        iframeObj.style.height = "300px"
		iframeDivObj.appendChild(iframeObj);
	    var closeDivObj = self.document.createElement("div");
	    closeDivObj.style.border = "2px solid gray";
	    closeDivObj.style.marginTop = "1px";
	    closeDivObj.style.padding = "6px";
	    iframeDivObj.appendChild(closeDivObj);
	    var aObj = self.document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDiv('ss_findPlacesIframeDiv');ss_findPlacesClose${prefix}();");
	    aObj.style.border = "2px outset black";
	    aObj.style.padding = "2px";
	    aObj.appendChild(document.createTextNode(ss_findButtonClose));
	    closeDivObj.appendChild(aObj);
		self.document.getElementsByTagName( "body" ).item(0).appendChild(iframeDivObj);
    }
    if (iframeDivObj == null) iframeDivObj = iframeDivObjParent;
    if (iframeObj == null) iframeObj = iframeObjParent;
    if (self.parent == self && textObj != null) {
    	var x = dojo.html.getAbsolutePosition(textObj, true).x
    	var y = dojo.html.getAbsolutePosition(textObj, true).y
	    ss_setObjectTop(iframeDivObj, y + "px");
	    ss_setObjectLeft(iframeDivObj, x + "px");
	}
	ss_showDiv("ss_findPlacesIframeDiv");
	var url = ss_findAjaxUrl;
	url = ss_replaceSubStrAll(url, "&amp;", "&");
	url += "&operation=find_user_search";
	url += "&searchText=" + searchText;
	url += "&maxEntries=" + "10";
	url += "&pageNumber=" + ss_findPlaces_pageNumber;
	url += "&findType=" + findPlacesType;
	url += "&listDivId=" + "available_"+elementName+"_${prefix}";
	url += "&namespace=" + "${prefix}";
    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
		self.location.href = url;
	} else {
		iframeObj.src = url;
	}
}

</script>
<c:set var="ss_find_places_support_stuff_loaded" value="1" scope="request"/>
</c:if>

<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findPlacesElementWidth %>; overflow:hidden;" 
    name="ss_findPlaces_searchText_${prefix}" 
    id="ss_findPlaces_searchText_${prefix}"
    onKeyUp="ss_findPlacesSearch_${prefix}(this.id, '<%= findPlacesElementName %>', '<%= findPlacesType %>');"
    onBlur="ss_findPlacesBlurTextArea${prefix}();"></textarea></div>
<div id="ss_findPlaces_searchText_bottom_${prefix}" style="padding:0px; margin:0px;"></div>
<div id="ss_findPlacesNavBarDiv_${prefix}"
    class="ss_findUserList" style="visibility:hidden;"
    onmouseover="ss_findPlacesMouseOverList${prefix}()"
    onmouseout="ss_findPlacesMouseOutList${prefix}()">
    <div id="available_<%= findPlacesElementName %>_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
<input type="hidden" name="<%= findPlacesElementName %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findPlacesFormName %>', ss_savefindPlacesData_${prefix});
</script>
