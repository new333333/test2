
<% // History and entry navigation bar %>
<c:if test="${empty ss_history_bar_loaded}">
<c:set var="ss_history_bar_imageId" value="0" scope="request"/>
<c:set var="ss_history_bar_loaded" value="1" scope="request"/>

<script type="text/javascript">
if (!ss_history_bar_loaded || ss_history_bar_loaded == "undefined" ) {
var ss_entryList = new Array();
var ss_entryCount = 0;
<c:forEach var="entry" items="${ssFolderEntries}" >
ss_entryList[ss_entryCount++] = '<c:out value="${entry._docId}"/>';
</c:forEach>

var left_end = "<html:imagesPath/>pics/sym_s_left_end.gif";
var left = "<html:imagesPath/>pics/sym_s_arrow_down.gif";
var right_end = "<html:imagesPath/>pics/sym_s_right_end.gif";
var right = "<html:imagesPath/>pics/sym_s_arrow_up.gif";
var left_end_g = "<html:imagesPath/>pics/sym_s_left_end_g.gif";
var left_g = "<html:imagesPath/>pics/sym_s_arrow_down_g.gif";
var right_end_g = "<html:imagesPath/>pics/sym_s_right_end_g.gif";
var right_g = "<html:imagesPath/>pics/sym_s_arrow_up_g.gif";
var g_alt = "<ssf:nlt tag="nav.noEntries" text="No more entries"/>";
var left_alt = "<ssf:nlt tag="nav.prevEntry" text="Previous entry"/>"
var left_end_alt = "<ssf:nlt tag="nav.firstEntry" text="First entry"/>"
var right_alt = "<ssf:nlt tag="nav.nextEntry" text="Next entry"/>"
var right_end_alt = "<ssf:nlt tag="nav.lastEntry" text="Last entry"/>"

var ss_baseHistoryUrl = '<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="${ssFolder.id}"
          action="view_folder_entry"
          operation="view_entry"
          actionUrl="false"
          />';
          
}
var ss_history_bar_loaded = 1;

</script>
</c:if>
<c:set var="ss_history_bar_imageId" value="${ss_history_bar_imageId + 1}" scope="request"/>

<table cellspacing="0" cellpadding="0" style="display: inline;">
  <tr>

<%
if (false) {
%>
     <td><a  
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="${ssFolder.id}"
          action="view_folder_entry"        
          operation="entry_previous"
          actionUrl="false"
          />"
          onClick="ss_getLastEntryId('${ss_history_bar_imageId}');return false;" ><img 
          alt="<ssf:nlt tag="nav.firstEntry" text="First entry"/>" id="ss_first"
          border="0" src="<html:imagesPath/>pics/sym_s_left_end.gif"></a></td>
<%
}
%>
     <td><a  
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="${ssFolder.id}"
          action="view_folder_entry"        
          operation="entry_previous"
          actionUrl="false"
          />"
          onClick="ss_getNextEntryId('${ss_history_bar_imageId}');return false;" ><img 
          alt="<ssf:nlt tag="nav.prevEntry" text="Previous entry"/>" id="ss_prev"
          border="0" src="<html:imagesPath/>pics/sym_s_arrow_down.gif"></a></td>
          
     <td><span><ssf:nlt tag="nav.view"/></span></td>
     
     <td><a href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="${ssFolder.id}"
          action="view_folder_entry"
          operation="entry_next"
          actionUrl="false"
          />"
          onClick="ss_getPreviousEntryId('${ss_history_bar_imageId}');return false;" ><img 
          alt="<ssf:nlt tag="nav.nextEntry" text="Next entry"/>" id="ss_next"
          border="0" src="<html:imagesPath/>pics/sym_s_arrow_up.gif"></a></td>
          
<%
if (false) {
%>
      <td><a 
          href="<ssf:url
          adapter="true"
          portletName="ss_forum" 
          folderId="${ssFolder.id}"
          action="view_folder_entry"
          operation="entry_next"
          actionUrl="false"
          />"
          onClick="ss_getFirstEntryId('${ss_history_bar_imageId}');return false;" ><img 
          alt="<ssf:nlt tag="nav.lastEntry" text="Last entry"/>" id="ss_last"
          border="0" src="<html:imagesPath/>pics/sym_s_right_end.gif"></a></td>
<%
}
%>
      <td>&nbsp;</td>
  </tr>
</table>

