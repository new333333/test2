<% // History and entry navigation bar %>
<script language="javascript">
var ss_entryList = new Array();
var ss_entryCount = 0;
<c:forEach var="entry" items="${ssFolderEntries}" >
ss_entryList[ss_entryCount++] = '<c:out value="${entry.id}"/>';
</c:forEach>

var ss_baseHistoryUrl = '<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="view_entry"
          actionUrl="false"
          />';
function ss_getNextEntryId() {
	var nextEntry = "";
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
	}
	return false;
}

function ss_getPreviousEntryId() {
	var nextEntry = "";
	if (ss_currentEntryId && ss_currentEntryId != "") {
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
	}
	return false;
}

</script>
<div>
      <span class="ss_buttonBarRight"><a  
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"        
          operation="entry_previous"
          actionUrl="false"
          />"
          onClick="ss_getNextEntryId();return false;" ><img 
          border="0" src="<html:imagesPath/>pics/sym_s_prev.gif"></a><span>&nbsp;Entries&nbsp;</span><a href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="entry_next"
          actionUrl="false"
          />"
          onClick="ss_getPreviousEntryId(ss_currentEntryId);return false;" ><img 
          border="0" src="<html:imagesPath/>pics/sym_s_next.gif"></a>&nbsp;</span>
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
