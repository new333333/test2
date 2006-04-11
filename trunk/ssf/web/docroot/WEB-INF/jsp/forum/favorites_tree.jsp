<% // Favorites tree %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<taconite-root xml:space="preserve">
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_favorites_status_message" parseInBrowser="true">
		<div id="ss_favorites_status_message" style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_favorites_status_message" parseInBrowser="true">
		<div id="ss_favorites_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
<%
	}

%>
	<taconite-replace contextNodeID="ss_favorites" parseInBrowser="true">

<div class="ss_style" id="ss_favorites" 
  style="border:solid 1px black;">
<div style="margin:2px;">
<form name="ss_favorites_form" class="ss_style" method="post" onSubmit="return false;" >
<table id="ss_favorites_table" cellspacing="0" cellpadding="0" >
<tbody>
<tr>
  <td colspan="2" class="ss_bold"><ssf:nlt tag="favorites" text="Favorites"/></td>
  <td align="right"><a onClick="ss_hideDiv('ss_favorites_pane');return false;"
    ><img border="0" src="<html:imagesPath/>box/close_off.gif"/></a></td>
</tr>
<tr>
<td colspan="3">
<ul id="ss_favorites_1" class="ss_dragable ss_userlist">
<li id="li_1_0" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_1" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_2" class="ss_dragable ss_userlist">yada yada yada yada</li>
<li id="li_1_3" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_4" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_5" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_1_6" class="ss_dragable ss_userlist">yada yada</li>
</ul>
</td>
</tr>
<tr>
<td colspan="3">
<ul id="ss_favorites_2" class="ss_dragable ss_userlist">
<li id="li_2_0" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_1" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_2" class="ss_dragable ss_userlist">yada yada yada yada</li>
<li id="li_2_3" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_4" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_5" class="ss_dragable ss_userlist">yada yada</li>
<li id="li_2_6" class="ss_dragable ss_userlist">yada yada</li>
</ul>
</td>
</tr>
<tr><td colspan="3"> </td></tr>
<tr>
<td colspan="3">
<a href="javascript: ;" 
 onClick="ss_addForumToFavorites();return false;"
><span class="ss_bold">Add the current page to the favorites list...</span></a>
</td>
</tr>
<tr><td colspan="3"> </td></tr>
<tr>
<td colspan="3">
<span class="ss_bold">Add a new favorites category:</span><br />
<input type="text" size="20" name="new_favorites_category" />
<input type="submit" name="add_favorites_category" 
 value="<ssf:nlt tag="button.ok" text="OK"/>" 
 onClick="ss_addFavoriteCategory();return false;" />
</td>
</tr>

</tbody>
</table>
</form>
</div>
</div>

	</taconite-replace>

</taconite-root>
