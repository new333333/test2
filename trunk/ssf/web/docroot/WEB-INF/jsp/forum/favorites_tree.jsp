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
	<taconite-replace-children contextNodeID="ss_favorites" parseInBrowser="true">

<div style="margin:2px;">
<form name="ss_favorites_form" class="ss_style" method="post" onSubmit="return false;" >
<table id="ss_favorites_table" cellspacing="0" cellpadding="0" >
<tbody>
<tr>
  <td colspan="2" class="ss_bold ss_largerprint"><ssf:nlt tag="favorites" text="Favorites"/></td>
  <td align="right"><a onClick="ss_hideDiv('ss_favorites_pane');return false;"
    ><img border="0" src="<html:imagesPath/>box/close_off.gif"/></a></td>
</tr>
<tr>
<td colspan="3">

<table class="ss_sortableList">
<tbody>
<tr>
<td nowrap="nowrap">
</td><td>
<ul id="ss_favorites_1aa" class="ss_dragableLink ss_sortableListCircle">
<li id="li_1aa_0" ><a href="#" onClick="ss_favorite_clicked(this);return false;">
<span class="ss_bold">Category 1</span></a></li>
</ul>
</td>
</tr>
</tbody>
</table>
<table class="ss_sortableList">
<tbody>
<tr>
<td nowrap="nowrap"><img src="<html:imagesPath/>trees/spacer.gif" /></td><td>
<ul id="ss_favorites_1a" class="ss_dragableLink ss_sortableList"> 
<li 
id="li_1a_0" ><a href="#" onClick="ss_favorite_clicked(this);return false;">yada yada link</a></li>
<li id="li_1a_1" >yada yada</li>
<li id="li_1a_2" nowrap="nowrap">yada yada yada yada</li>
<li id="li_1a_3">foo</li>
<li id="li_1a_4">yada yada</li>
<li id="li_1a_5">yada yada</li>
<li id="li_1a_6">yada yada</li>
</ul>
</td>
</tr>
</tbody>
</table>

<table class="ss_sortableList">
<tbody>
<tr>
<td nowrap="nowrap"><img src="<html:imagesPath/>trees/spacer.gif" /><img 
src="<html:imagesPath/>trees/spacer.gif" /></td><td>
<ul id="ss_favorites_1b" class="ss_dragableLink ss_sortableListCircle">
<li id="li_1b_0" ><a href="#" onClick="alert('click');return false;">
<span class="ss_bold">Category 2</span></a></li>
</ul>
</td>
</tr>
</tbody>
</table>
<table class="ss_sortableList">
<tbody>
<tr>
<td nowrap="nowrap"><img src="<html:imagesPath/>trees/spacer.gif" /><img 
src="<html:imagesPath/>trees/spacer.gif" /><img 
src="<html:imagesPath/>trees/spacer.gif" /></td>
<td>
<ul id="ss_favorites_1b2" class="ss_dragableLink ss_sortableList">
<li id="li_1b_0" ><a href="#" onClick="alert('click');return false;">yada yada link</a></li>
<li id="li_1b_1" >yada yada</li>
<li id="li_1b_2" >yada yada yada yada</li>
<li id="li_1b_3">foo</li>
<li id="li_1b_4">yada yada</li>
<li id="li_1b_5">yada yada</li>
<li id="li_1b_6">yada yada</li>
</ul>
</td>
</tr>
</tbody>
</table>

<table cellspacing="0" cellpadding="0"><tbody><tr>
<td><img src="<html:imagesPath/>trees/spacer.gif" 
/><img src="<html:imagesPath/>trees/spacer.gif" /></td>
<td>
<ul id="ss_favorites_1" class="ss_dragableLink ss_sortableList">
<li id="li_1_0"><a>yada yada</a></li>
<li id="li_1_1">yada yada</li>
<li id="li_1_2">yada yada yada yada</li>
<li id="li_1_3">foo</li>
<li id="li_1_4">yada yada</li>
<li id="li_1_5">yada yada</li>
<li id="li_1_6">yada yada</li>
</ul>
</td>
</tr></tbody></table>

<table cellspacing="0" cellpadding="0"><tbody><tr>
<td>
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" /></td>
<td>
	<ul id="ss_favorites_2" class="ss_dragableLink ss_sortableList">
	<li id="li_2_0">yada yada</li>
	<li id="li_2_1">yada yada</li>
	<li id="li_2_2">yada yada yada yada</li>
	<li id="li_2_3">yada yada</li>
	<li id="li_2_4">yada yada</li>
	<li id="li_2_5">yada yada</li>
	<li id="li_2_6">yada yada</li>
	</ul>
</td>
</tr></tbody></table>

<table cellspacing="0" cellpadding="0"><tbody><tr>
<td nowrap="nowrap">
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" />
<img src="<html:imagesPath/>trees/spacer.gif" /></td>
<td nowrap="nowrap">
<ul id="ss_favorites_3" class="ss_dragableLink ss_sortableList">
<li id="li_3_0">yada yada</li>
<li id="li_3_1">yada yada</li>
<li id="li_3_2">yada yada yada yada</li>
<li id="li_32_3">yada yada</li>
<li id="li_3_4">yada yada</li>
<li id="li_3_5">yada yada</li>
<li id="li_3_6">yada yada</li>
</ul>
</td>
</tr></tbody></table>
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

	</taconite-replace-children>

</taconite-root>
