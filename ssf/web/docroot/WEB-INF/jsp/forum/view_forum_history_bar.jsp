<% // History and entry navigation bar %>
<script language="javascript">
var ss_entryList = new Array();
var ss_entryCount = 0;
<c:forEach var="entry" items="${ssFolderEntries}" >
ss_entryList[ss_entryCount++] = '<c:out value="${entry.id}"/>';
</c:forEach>

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
	if (nextEntry != "") {
		var url = ss_baseHistoryUrl + '&entryId=' + nextEntry;
		ss_loadEntryUrl(url, nextEntry);
	} else {
		alert("There are no more entries to view.")
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
	if (nextEntry != "") {
		var url = ss_baseHistoryUrl + '&entryId=' + nextEntry;
		ss_loadEntryUrl(url, nextEntry);
	} else {
		alert("There are no more entries to view.")
	}
	return false;
}

var firstEntry = "";
function ss_getFirstEntryId() {
    if (ss_entryCount > 0) {firstEntry = ss_entryList[0];}
    if (ss_currentEntryId == firstEntry) {
    	alert("You are already viewing the last entry.")
    } else {
        var url = ss_baseHistoryUrl + '&entryId=' + firstEntry;
		ss_loadEntryUrl(url, firstEntry);
    }
	return false;
}

var lastEntry = "";
function ss_getLastEntryId() {
    if (ss_entryCount > 0) {lastEntry = ss_entryList[ss_entryCount - 1];}
    if (ss_currentEntryId == lastEntry) {
    	alert("You are already viewing the first entry.")
    } else {
        var url = ss_baseHistoryUrl + '&entryId=' + lastEntry;
		ss_loadEntryUrl(url, lastEntry);
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
          onClick="ss_getLastEntryId();return false;" ><img alt="first entry" name="first"
          border="0" src="<html:imagesPath/>pics/sym_s_left_end.gif"></a><a  
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"        
          operation="entry_previous"
          actionUrl="false"
          />"
          onClick="ss_getNextEntryId();return false;" ><img alt="previous entry" name="prev"
          border="0" src="<html:imagesPath/>pics/sym_s_left.gif"></a><span>Entries</span><a href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="entry_next"
          actionUrl="false"
          />"
          onClick="ss_getPreviousEntryId();return false;" ><img alt="next entry" name="next"
          border="0" src="<html:imagesPath/>pics/sym_s_right.gif"></a><a 
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="entry_next"
          actionUrl="false"
          />"
          onClick="ss_getFirstEntryId();return false;" ><img alt="last entry" name="last"
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
