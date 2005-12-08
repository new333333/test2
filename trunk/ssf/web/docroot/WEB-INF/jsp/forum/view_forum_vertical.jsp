<% //view a folder forum with folder on top and entry below %>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>

<div class="ss_portlet">
<ssf:displayConfiguration configDefinition="<%= ssConfigDefinition %>" 
  configElement="<%= ssConfigElement %>" 
  configJspStyle="<%= ssConfigJspStyle %>" />
</div>

<div id="showentrydiv" style="visibility:hidden; position:relative; display:none; width:<%= ss_entryWindowWidth %>;">
<a href="#return_to_folder_list" onClick="scrollToSavedLocation();return false;">
Scroll up to the folder listing...
</a>

<%@ include file="/WEB-INF/jsp/forum/view_forum_history_bar.jsp" %>
<div id="showentry" class="ss_portlet" >
</div>
<a href="#return_to_folder_list" onClick="scrollToSavedLocation();return false;">
Scroll up to the folder listing...
</a>
<br>
</div>
