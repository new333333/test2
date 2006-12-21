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
<c:if test="${empty ss_find_places_support_stuff_loaded}">
<script type="text/javascript">
var ss_findPlaces_searchText = ""
var ss_findPlaces_pageNumber = 0;

var ss_findPlacesSearchInProgress = 0;
var ss_findPlacesSearchWaiting = 0;
var ss_findPlacesSearchLastText = "";
var ss_findPlacesSearchLastTextObjId = "";
var ss_findPlacesSearchLastElement = "";
var ss_findPlacesSearchLastfindPlacesType = "";
function ss_findPlacesSearch(textObjId, elementName, findPlacesType) {
	var textObj = document.getElementById(textObjId);
	var text = textObj.value;
	if (text == '' || text != ss_findPlacesSearchLastText) ss_findPlaces_pageNumber = 0;
	ss_debug('ss_findPlacesSearch: '+text+', '+elementName+', '+findPlacesType+', '+ss_findPlaces_pageNumber)
	ss_setupStatusMessageDiv()
	//Are we already doing a search?
	if (ss_findPlacesSearchInProgress == 1) {
		//Yes, hold this request until the current one finishes
		ss_findPlacesSearchLastText = text;
		ss_findPlacesSearchLastTextObjId = textObjId;
		ss_findPlacesSearchLastElement = elementName;
		ss_findPlacesSearchLastfindPlacesType = findPlacesType;
		ss_findPlacesSearchWaiting = 1;
		ss_debug('  hold search request...')
		return;
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
			ss_findPlacesSelectItem(liObjs[0]);
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
 	var url = "<ssf:url 
    	adapter="true" 
    	portletName="ss_forum" 
    	action="__ajax_request" 
    	actionUrl="false" >
		<ssf:param name="operation" value="find_places_search" />
    	</ssf:url>"
	var ajaxRequest = new ss_AjaxRequest(url); //Create AjaxRequest object
	var searchText = text;
	if (searchText.lastIndexOf("*") < parseInt(searchText.length - 1)) searchText += "*";
	ajaxRequest.addKeyValue("searchText", searchText)
	ajaxRequest.addKeyValue("maxEntries", "10")
	ajaxRequest.addKeyValue("pageNumber", ss_findPlaces_pageNumber)
	ajaxRequest.addKeyValue("findType", findPlacesType)
	ajaxRequest.addKeyValue("listDivId", "available_"+elementName+"_${prefix}")
	//ajaxRequest.setEchoDebugInfo();
	//ajaxRequest.setPreRequest(ss_prefindPlacesRequest);
	ajaxRequest.setPostRequest(ss_postfindPlacesRequest);
	ajaxRequest.setData("elementName", elementName)
	ajaxRequest.setData("savedColor", savedColor)
	ajaxRequest.setUseGET();
	ajaxRequest.sendRequest();  //Send the request
}
function ss_postfindPlacesRequest(obj) {
	ss_debug('ss_postfindPlacesRequest')
	//See if there was an error
	if (self.document.getElementById("ss_status_message").innerHTML == "error") {
		alert(ss_not_logged_in);
	}
	ss_findPlacesSearchInProgress = 0;

	ss_showDivActivate('ss_findPlacesNavBarDiv_<portlet:namespace/>');
	var divObj = document.getElementById('ss_findPlacesNavBarDiv_<portlet:namespace/>');
	if (divObj != null) divObj.style.zIndex = '500';
		
 	//Show this at full brightness
 	divObj = document.getElementById('available_' + obj.getData('elementName') + '_${prefix}');
 	if (divObj != null) divObj.style.color = obj.getData('savedColor');
		
	//See if there is another search request to be done
	if (ss_findPlacesSearchWaiting == 1) {
		document.getElementById('available_'+obj.getData('elementName')+'_${prefix}').innerHTML = "";
		setTimeout('ss_findPlacesSearch(ss_findPlacesSearchLastTextObjId, ss_findPlacesSearchLastElement, ss_findPlacesSearchLastfindPlacesType)', 100)
	}
}
//Routine called when item is clicked
function ss_findPlacesSelectItem(obj) {
	if (!obj || !obj.id ||obj.id == undefined) return false;
	var url = "<portlet:renderURL windowState="maximized"><portlet:param 
		name="action" value="view_permalink"/><portlet:param name="binderId" 
		value="ss_binderIdPlaceholder"/><portlet:param name="newTab" value="1"/></portlet:renderURL>";
	var id = ss_replaceSubStr(obj.id, 'ss_findPlaces_id_', "");
	url = ss_replaceSubStr(url, 'ss_binderIdPlaceholder', id);
	self.location.href = url;
}

function ss_savefindPlacesData_${prefix}() {
	ss_debug('ss_savefindPlacesData')
	var ulObj = document.getElementById('available_<%= findPlacesElementName %>_${prefix}')
	var liObjs = ulObj.getElementsByTagName('li');
	if (liObjs.length == 1) {
		ss_findPlacesSelectItem(liObjs[0]);
	}
	return false;
}

function ss_findPlacesNextPage() {
	ss_findPlaces_pageNumber++;
	setTimeout("ss_findPlacesSearch(ss_findPlacesSearchLastTextObjId, ss_findPlacesSearchLastElement, ss_findPlacesSearchLastfindPlacesType);", 100);
}

function ss_findPlacesPrevPage() {
	ss_findPlaces_pageNumber--;
	if (ss_findPlaces_pageNumber < 0) ss_findPlaces_pageNumber = 0;
	ss_findPlacesSearch(ss_findPlacesSearchLastTextObjId, ss_findPlacesSearchLastElement, ss_findPlacesSearchLastfindPlacesType);
}

</script>
<c:set var="ss_find_places_support_stuff_loaded" value="1" scope="request"/>
</c:if>

<div style="margin:0px; padding:0px;"><textarea 
    class="ss_text" style="height:17px; width:<%= findPlacesElementWidth %>; overflow:hidden;" 
    name="ss_findPlaces_searchText_<portlet:namespace/>" 
    id="ss_findPlaces_searchText_<portlet:namespace/>"
    onKeyUp="ss_findPlacesSearch(this.id, '<%= findPlacesElementName %>', '<%= findPlacesType %>');"
    onBlur="setTimeout('ss_hideDiv(\'ss_findPlacesNavBarDiv_<portlet:namespace/>\')', 200);"></textarea></div>
<div id="ss_findPlaces_searchText_bottom_<portlet:namespace/>" style="padding:0px; margin:0px;"></div>
<div id="ss_findPlacesNavBarDiv_<portlet:namespace/>"
    class="ss_findUserList" style="visibility:hidden;">
    <div id="available_<%= findPlacesElementName %>_${prefix}">
      <ul>
      </ul>
    </div>
</div>	
<input type="hidden" name="<%= findPlacesElementName %>"/>
  
<script type="text/javascript">
ss_createOnSubmitObj('${prefix}onSubmit', '<%= findPlacesFormName %>', ss_savefindPlacesData_${prefix});
</script>
