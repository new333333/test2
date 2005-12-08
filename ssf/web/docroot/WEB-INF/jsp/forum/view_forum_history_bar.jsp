<% // History and entry navigation bar %>
<div class="portlet-section-header" 
  style="margin-top: 8x; margin-bottom: 8px; width:<c:if 
  test="${empty ss_toolbarWidth}"><c:out value="100%"/></c:if><c:if 
  test="${!empty ss_toolbarWidth}"><c:out value="${ss_toolbarWidth}"/></c:if>;">
  <table cellpadding="0" cellspacing="0" style="display:inline;">
    <tr>
      <td nowrap align="left" width="10%">
        <a href="<ssf:url
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="history_previous"
          actionUrl="false"
          popup="<%= popupValue %>" />"
          onClick="loadEntry(this,'');return false;" ><img 
          border="0" src="<html:imagesPath/>pics/sym_s_prev.gif"></a>
        <span class="portlet-font">History</span>
        <a href="<ssf:url
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry" 
          operation="history_next"
          actionUrl="false"
 		  popup="<%= popupValue %>" />"
          onClick="loadEntry(this,'');return false;" ><img 
          border="0" src="<html:imagesPath/>pics/sym_s_next.gif"></a>
      </td>
      <td width="80%">&nbsp;</td>
      <td nowrap align="right" width="10%">
        <a href="<ssf:url
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="entry_previous"
          actionUrl="false"
          popup="<%= popupValue %>" />"
          onClick="loadEntry(this,'');return false;" ><img 
          border="0" src="<html:imagesPath/>pics/sym_s_prev.gif"></a>
        <span class="portlet-font">Entries</span>
        <a href="<ssf:url
          folderId="<%= ssFolder.getId().toString() %>"
          action="view_entry"
          operation="entry_next"
          actionUrl="false"
           popup="<%= popupValue %>" />"
          onClick="loadEntry(this,'');return false;" ><img 
          border="0" src="<html:imagesPath/>pics/sym_s_next.gif"></a>
      </td>
    </tr>
  </table>
</div>

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
