<% // History and entry navigation bar %>
<script language="javascript">
var ss_entryList = new Array();
var ss_entryCount = 0;
<c:forEach var="entry" items="${ssFolderEntries}" >
ss_entryList[ss_entryCount++] = '<c:out value="${entry.id}"/>';
</c:forEach>

var left_end = "<html:imagesPath/>pics/sym_s_left_end.gif";
var left = "<html:imagesPath/>pics/sym_s_left.gif";
var right_end = "<html:imagesPath/>pics/sym_s_right_end.gif";
var right = "<html:imagesPath/>pics/sym_s_right.gif";
var left_end_g = "<html:imagesPath/>pics/sym_s_left_end_g.gif";
var left_g = "<html:imagesPath/>pics/sym_s_left_g.gif";
var right_end_g = "<html:imagesPath/>pics/sym_s_right_end_g.gif";
var right_g = "<html:imagesPath/>pics/sym_s_right_g.gif";
var g_alt = "no more entries";
var left_alt = "previous entry"
var left_end_alt = "first entry"
var right_alt = "next entry"
var right_end_alt = "last entry"

function swapImages (id, img, alt) {
		document.getElementById(id).src = img;
		document.getElementById(id).alt = alt;
}

function swapPrevFirst () {
		swapImages('first', left_end_g, g_alt)
		swapImages('prev', left_g, g_alt)
}

function swapNextLast () {
		swapImages('last', right_end_g, g_alt)
		swapImages('next', right_g, g_alt)
		return false;
}

function restoreImages (currentEntry) {
		swapImages('last', right_end, right_end_alt)
		swapImages('next', right, right_alt)
		swapImages('first', left_end, left_end_alt)
		swapImages('prev', left, left_alt)
		if (currentEntry != null) {
			if (currentEntry == ss_entryList[ss_entryCount - 1]) {				
				swapPrevFirst();
			} else if (currentEntry == ss_entryList[0]) {
				swapNextLast();
			}
		}
		return false;
}

var nextEntry = "";
var ss_baseHistoryUrl = '<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="view_entry"
          actionUrl="false"
          />';
          
function ss_getNextEntryId() {
	if (!ss_currentEntryId || ss_currentEntryId == "") {
		if (ss_entryCount > 0) {nextEntry = ss_entryList[0];}
	} else {
		for (var i = 0; i < ss_entryCount; i++) {
			if (ss_entryList[i] == ss_currentEntryId) {
				i++;
				if (i < ss_entryCount) {nextEntry = ss_entryList[i];}
				break;
			}
		}
	}
	restoreImages();
	if (nextEntry != "") {
		var url = ss_baseHistoryUrl + '&entryId=' + nextEntry;
		ss_loadEntryUrl(url, nextEntry);
		if (nextEntry == ss_entryList[ss_entryCount - 1]) {
			swapPrevFirst();
		} 
	} else {
		alert("There are no more entries to view.")
		swapPrevFirst();
	}
	return false;
}

var prevEntry = "";
function ss_getPreviousEntryId() {
    if (!ss_currentEntryId || ss_currentEntryId == "") {
		if (ss_entryCount > 0) {nextEntry = ss_entryList[0];}
	} else {
		for (var i = 0; i < ss_entryCount; i++) {
			if (ss_entryList[i] == ss_currentEntryId) {
				i--;
				if (i >= 0) {nextEntry = ss_entryList[i];}
				break;
			}
		}
	}
	restoreImages();
	if (nextEntry != "") {
		var url = ss_baseHistoryUrl + '&entryId=' + nextEntry;
		ss_loadEntryUrl(url, nextEntry);
		if (nextEntry == ss_entryList[0]) {
			swapNextLast();
		}
	} else {
		alert("There are no more entries to view.")
		swapNextLast()
	}
	return false;
}

var firstEntry = "";
function ss_getFirstEntryId() {
    if (ss_entryCount > 0) {firstEntry = ss_entryList[0];}
    restoreImages();
    if (ss_currentEntryId == firstEntry) {
    	alert("You are already viewing the last entry.")
    	swapNextLast()
    } else {
        var url = ss_baseHistoryUrl + '&entryId=' + firstEntry;
		ss_loadEntryUrl(url, firstEntry);
		swapNextLast()
    }
	return false;
}

var lastEntry = "";
function ss_getLastEntryId() {
    if (ss_entryCount > 0) {lastEntry = ss_entryList[ss_entryCount - 1];}
    restoreImages();
    if (ss_currentEntryId == lastEntry) {
    	alert("You are already viewing the first entry.")
    	swapPrevFirst()
    } else {
        var url = ss_baseHistoryUrl + '&entryId=' + lastEntry;
		ss_loadEntryUrl(url, lastEntry);
		swapPrevFirst()
    }
	return false;
}
</script>

<div>
      <span class="ss_buttonBarRight">

     <a  
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"        
          operation="entry_previous"
          actionUrl="false"
          />"
          onClick="ss_getLastEntryId();return false;" ><img alt="first entry" id="first"
          border="0" src="<html:imagesPath/>pics/sym_s_left_end.gif"></a><a  
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"        
          operation="entry_previous"
          actionUrl="false"
          />"
          onClick="ss_getNextEntryId();return false;" ><img alt="previous entry" id="prev"
          border="0" src="<html:imagesPath/>pics/sym_s_left.gif"></a><span>Entries</span><a href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="entry_next"
          actionUrl="false"
          />"
          onClick="ss_getPreviousEntryId();return false;" ><img alt="next entry" id="next"
          border="0" src="<html:imagesPath/>pics/sym_s_right.gif"></a><a 
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="entry_next"
          actionUrl="false"
          />"
          onClick="ss_getFirstEntryId();return false;" ><img alt="last entry" id="last"
          border="0" src="<html:imagesPath/>pics/sym_s_right_end.gif"></a>&nbsp;
          </span>
</div>
<br />
<% // Debugging code (turned off) %>
<c:if test="">
<div> 
<b>Dump of history map</b>
<br>
<%
	if (ssHistoryMap != null) {
		Iterator it = ssHistoryMap.getHistoryMap().entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry me = (Map.Entry) it.next();
%>
key=<%= me.getKey() %>, value=<%= me.getValue() %><br>
<%
		}
	}
%>
<br>
</div>
</c:if>
