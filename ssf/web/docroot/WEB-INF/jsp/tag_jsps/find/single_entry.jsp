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
<% // Find a single entry %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.util.Set" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.sitescape.util.ParamUtil" %>
<%
	String findEntriesType = (String) request.getAttribute("list_type");
	String findEntriesFormName = (String) request.getAttribute("form_name");
	String findEntriesElementName = (String) request.getAttribute("form_element");
	String findEntriesElementWidth = (String) request.getAttribute("element_width");
	String findEntriesBinderId = (String) request.getAttribute("binderId");
	String findEntriesSearchSubFolders = (String) request.getAttribute("searchSubFolders");
	String clickRoutine = (String) request.getAttribute("clickRoutine");
	String instanceCount = (String) request.getAttribute("instanceCount");
	Boolean leaveResultsVisible = (Boolean) request.getAttribute("leaveResultsVisible");
	String label = ParamUtil.get(request, "label", "");
%>
<c:set var="prefix" value="<%= findEntriesFormName + "_" + findEntriesElementName %>" />
<c:set var="label" value="<%= label %>" />
<c:if test="${empty ss_find_entries_support_stuff_loaded}">
<script type="text/javascript">
var ss_findEntries_searchText = ""
var ss_findEntries_pageNumber = 0;
var ss_findEntries_pageNumberBefore = -1;
var ss_findEntriesDivTopOffset = 2;

var ss_findEntriesSearchInProgress = 0;
var ss_findEntriesSearchWaiting = 0;
var ss_findEntriesSearchStartMs = 0;
var ss_findEntriesSearchLastText = "";
var ss_findEntriesSearchLastTextObjId = "";
var ss_findEntriesSearchLastElement = "";
var ss_findEntriesSearchLastfindEntriesType = "";
var ss_findEntriesClickRoutine${prefix} = "<%= clickRoutine %>";
var ss___findEntriesIsMouseOverList = false;
function ss_findEntriesSearch_${prefix}(textObjId, elementName, findEntriesType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text.trim() != ss_findEntriesSearchLastText.trim()) {
		ss_findEntries_pageNumber = 0;
		ss_findEntries_pageNumberBefore = 0;
	}
	ss_setupStatusMessageDiv()
	ss_moveDivToBody('ss_findEntriesNavBarDiv_<portlet:namespace/>');
	//Are we already doing a search?
	if (ss_findEntriesSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_findEntriesSearchLastText = text;
		ss_findEntriesSearchLastTextObjId = textObjId;
		ss_findEntriesSearchLastElement = elementName;
		ss_findEntriesSearchLastfindEntriesType = findEntriesType;
		ss_findEntriesSearchWaiting = 1;
		var d = new Date();
		var curr_msec = d.getTime();
		if (ss_findEntriesSearchStartMs == 0 || curr_msec < ss_findEntriesSearchStartMs + 1000) {
			ss_debug('  hold search request...')
			if (ss_findEntriesSearchStartMs == 0) ss_findEntriesSearchStartMs = curr_msec;
			return;
		}
		//The user waited for over a second, let this request go through
		ss_findEntriesSearchStartMs = 0;
		ss_debug('   Stopped waiting')
	}
	ss_findEntriesSearchInProgress = 1;
	ss_findEntriesSearchWaiting = 0;
	ss_findEntriesSearchLastTextObjId = textObjId;
	ss_findEntriesSearchLastElement = elementName;
	ss_findEntriesSearchLastText = text;
	ss_findEntriesSearchLastfindEntriesType = findEntriesType;
 	//Save the text in case the user changes the search type
 	ss_findEntries_searchText = text;
 	
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
		var ulObj = document.getElementById('available_<%= findEntriesElementName %>_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			ss_findEntriesSelectItem<portlet:namespace/>(liObjs[0]);
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

 	ss_debug("Page number: " + ss_findEntries_pageNumber + ", //"+text+"//")
	var searchText = text;
	if (searchText.length > 0 && searchText.charAt(searchText.length-1) != " ") {
		if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	}
	if (ss_userDisplayStyle && ss_userDisplayStyle == 'accessible') {
		ss_findEntrySearchAccessible_${prefix}(searchText, elementName, findEntriesType, crFound);
		ss_findEntriesSearchInProgress = 0;
		return;
	}
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="operation" value="find_entries_search" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findEntries_pageNumber)
	ajaxRequest.addKeyValue("findType", findEntriesType)
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName+"_${prefix}")
	ajaxRequest.addKeyValue("binderId", "<%= findEntriesBinderId %>")
	ajaxRequest.addKeyValue("searchSubFolders", "<%= findEntriesSearchSubFolders %>")
	ajaxRequest.addKeyValue("namespace", "<portlet:namespace/>")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_prefindEntriesRequest);
	ajaxRequest.setPostRequest(ss_postfindEntriesRequest<portlet:namespace/>);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setData("crFound", crFound)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postfindEntriesRequest<portlet:namespace/>(obj) {
	ss_debug('ss_postfindEntriesRequest<portlet:namespace/>')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findEntriesSearchInProgress = 0;

	var divObj = document.getElementById('ss_findEntriesNavBarDiv_<portlet:namespace/>');
	ss_moveDivToBody('ss_findEntriesNavBarDiv_<portlet:namespace/>');
	ss_setObjectTop(divObj, parseInt(ss_getDivTop("ss_findEntries_searchText_bottom_<portlet:namespace/>") + ss_findEntriesDivTopOffset))
	ss_setObjectLeft(divObj, parseInt(ss_getDivLeft("ss_findEntries_searchText_bottom_<portlet:namespace/>")))
	ss_showDivActivate('ss_findEntriesNavBarDiv_<portlet:namespace/>');
		
 	//Show this at full brightness
 	divObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}');
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
		
	//See if there is another search request to be done
	if (ss_findEntriesSearchWaiting == 1) {
		setTimeout('ss_findEntriesSearch_${prefix}(ss_findEntriesSearchLastTextObjId, ss_findEntriesSearchLastElement, ss_findEntriesSearchLastfindEntriesType)', 100)
	}
	//See if the user typed a return. If so, see if there is a unique value to go to
	if (obj.getData('crFound') == 1) {
		var ulObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}')
		var liObjs = ulObj.getElementsByTagName('li');
		if (liObjs.length == 1) {
			setTimeout("ss_findEntriesSelectItem0_${prefix}();", 100);
			return;
		}
	}
}
function ss_findEntriesSelectItem0_${prefix}() {
	var ulObj = document.getElementById('available_<%= findEntriesElementName %>_${prefix}');
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findEntriesSelectItem<portlet:namespace/>(liObjs[0])
	}
}
//Routine called when item is clicked
function ss_findEntriesSelectItem<portlet:namespace/>(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<ssf:url adapter="true" portletName="ss_forum" 
		    action="view_permalink"
		    binderId="${ssBinder.id}"
		    entryId="ss_entryIdPlaceholder">
		    <ssf:param name="entityType" value="folderEntry" />
			</ssf:url>";
	var id = ss_replaceSubStr(obj.id, 'ss_findEntries_id_', "");
	if (ss_findEntriesClickRoutine${prefix} != "") {
		eval(ss_findEntriesClickRoutine${prefix} + "('"+id+"');")
		<% if (leaveResultsVisible) { %>
		  setTimeout("ss_showFindEntriesSelections${prefix}();", 200)
		<% } %>
	} else {
		url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', id);
		self.location.href = url;
	}
}

//Routine called when item is clicked in accessible mode
function ss_findEntriesSelectItemAccessible<portlet:namespace/>(obj, entryId) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<ssf:url adapter="false" portletName="ss_forum" 
		    folderId="${ssFolder.id}" action="view_folder_entry" 
		    entryId="ss_entryIdPlaceholder" actionUrl="true">
		    <ssf:param name="newTab" value="1" /></ssf:url>";
	url = ss_replaceSubStr(url, 'ss_entryIdPlaceholder', entryId);
	self.location.href = url;
}

function ss_savefindEntriesData_${prefix}() {
	ss_debug('ss_savefindEntriesData')
	var ulObj = document.getElementById('available_<%= findEntriesElementName %>_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findEntriesSelectItem<portlet:namespace/>(liObjs[0]);
	}
	return false;
}

function ss_findEntriesNextPage<portlet:namespace/>() {
	ss_findEntries_pageNumberBefore = ss_findEntries_pageNumber;
	ss_findEntries_pageNumber++;
	setTimeout("ss_findEntriesSearch_${prefix}(ss_findEntriesSearchLastTextObjId, ss_findEntriesSearchLastElement, ss_findEntriesSearchLastfindEntriesType);", 100);
}

function ss_findEntriesPrevPage<portlet:namespace/>() {
	ss_findEntries_pageNumberBefore = ss_findEntries_pageNumber;
	ss_findEntries_pageNumber--;
	if (ss_findEntries_pageNumber < 0) ss_findEntries_pageNumber = 0;
	ss_findEntriesSearch_${prefix}(ss_findEntriesSearchLastTextObjId, ss_findEntriesSearchLastElement, ss_findEntriesSearchLastfindEntriesType);
}

function ss_findEntriesClose<portlet:namespace/>() {
	document.getElementById('ss_findEntries_searchText_<portlet:namespace/>').focus();
}

function ss_findEntriesBlurTextArea<portlet:namespace/>() {
	if (!ss___findEntriesIsMouseOverList) {
		setTimeout(function() { ss_hideDiv('ss_findEntriesNavBarDiv_<portlet:namespace/>') } , 200);
	}
}

function ss_findEntriesMouseOverList<portlet:namespace/>() {
	ss___findEntriesIsMouseOverList = true;
}

function ss_findEntriesMouseOutList<portlet:namespace/>() {
	ss___findEntriesIsMouseOverList = false;
}

function ss_findEntrySearchAccessible_${prefix}(searchText, elementName, findEntriesType, crFound) {
	//In accessibility mode, wait for the user to type cr
	if (!crFound && parseInt(ss_findEntries_pageNumber) == 0 && 
			parseInt(ss_findEntries_pageNumberBefore) == 0) return;
	
    var iframeDivObj = self.document.getElementById("ss_findEntriesIframeDiv");
    var iframeObj = self.document.getElementById("ss_findEntriesIframe");
    var iframeDivObjParent = self.parent.document.getElementById("ss_findEntriesIframeDiv");
    var iframeObjParent = self.parent.document.getElementById("ss_findEntriesIframe");
    var textObj = self.document.getElementById('ss_findEntries_searchText_<portlet:namespace/>');
    if (iframeDivObjParent == null && iframeDivObj == null) {
	    iframeDivObj = self.document.createElement("div");
	    iframeDivObjParent = iframeDivObj;
        iframeDivObj.setAttribute("id", "ss_findEntriesIframeDiv");
		iframeDivObj.className = "ss_popupMenu";
		iframeDivObj.style.zIndex = ssPopupZ;
        iframeObj = self.document.createElement("iframe");
        iframeObj.setAttribute("id", "ss_findEntriesIframe");
        iframeObj.style.width = "400px"
        iframeObj.style.height = "300px"
		iframeDivObj.appendChild(iframeObj);
	    var closeDivObj = self.document.createElement("div");
	    closeDivObj.style.border = "2px solid gray";
	    closeDivObj.style.marginTop = "1px";
	    closeDivObj.style.padding = "6px";
	    iframeDivObj.appendChild(closeDivObj);
	    var aObj = self.document.createElement("a");
	    aObj.setAttribute("href", "javascript: ss_hideDiv('ss_findEntriesIframeDiv');ss_findEntriesClose<portlet:namespace/>();");
	    aObj.setAttribute("title", "<ssf:nlt tag="title.close.searchResults" />");
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
	    ss_setObjectTop(iframeDivObj, (y + 15) + "px");
	    ss_setObjectLeft(iframeDivObj, x + "px");
	}
	ss_showDiv("ss_findEntriesIframeDiv");
	
	var url = ss_AjaxBaseUrl;
	url = ss_replaceSubStrAll(url, "&amp;", "&");
	url += "&operation=find_user_search";
	url += "&searchText=" + searchText;
	url += "&binderId=" + "<%= findEntriesBinderId %>";
	url += "&maxEntries=" + "10";
	url += "&pageNumber=" + ss_findEntries_pageNumber;
	url += "&findType=" + findEntriesType;
	url += "&listDivId=" + "available_"+elementName+"_${prefix}";
	url += "&namespace=" + "<portlet:namespace/>";
	
    if (iframeDivObjParent != null && iframeDivObjParent != iframeDivObj) {
		self.location.href = url;
	} else {
		iframeObj.src = url;
	}
}

</script>
<c:set var="ss_find_entries_support_stuff_loaded" value="1" scope="request"/>
</c:if>

<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findEntriesElementWidth %>; overflow:hidden;" 
    name="ss_findEntries_searchText_<portlet:namespace/>" 
    id="ss_findEntries_searchText_<portlet:namespace/>"
    onKeyUp="ss_findEntriesSearch_${prefix}(this.id, '<%= findEntriesElementName %>', '<%= findEntriesType %>');"
    onBlur="ss_findEntriesBlurTextArea<portlet:namespace/>();"
    <c:if test="${!empty label}">
    	title="${label}"
    </c:if>
    ></textarea></div>
<div id="ss_findEntries_searchText_bottom_<portlet:namespace/>" style="padding:0px; margin:0px;"></div>
<div id="ss_findEntriesNavBarDiv_<portlet:namespace/>"
    class="ss_findUserList" style="visibility:hidden;"
    onmouseover="ss_findEntriesMouseOverList<portlet:namespace/>()"
    onmouseout="ss_findEntriesMouseOutList<portlet:namespace/>()">
    <div id="available_<%= findEntriesElementName %>_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
<input type="hidden" name="<%= findEntriesElementName %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findEntriesFormName %>', ss_savefindEntriesData_${prefix});
</script>
