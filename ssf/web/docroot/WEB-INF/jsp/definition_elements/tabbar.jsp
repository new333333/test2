<!-- Start of tabs -->

<script type="text/javascript">
var ss_tabs_delete_icon = "<html:imagesPath/>skins/${ss_user_skin}/iconset/delete.gif";
</script>
<div id="ss_tabbar" class="ss_tabs">
<table cellspacing="0" cellpadding="0" style="background:transparent;">
<tbody>
<tr id="ss_tabbar_tr">

<td>
  <table cellspacing="0" cellpadding="0" style="background:transparent;">
  <tbody>
  <tr>
  <td valign="middle" class="ss_tabs_td_left_active"><img 
    src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner"></td>
  <td valign="middle" class="ss_tabs_td_active" nowrap>
	<a href="" onClick="return ss_showTab(this);">
<c:if test="${!empty ssBinder.iconName}">
	   <img src="<html:imagesPath/>${ssBinder.iconName}">
</c:if>
	   <span>${ssBinder.title}</span></a>
  </td>
  <td valign="middle" class="ss_tabs_td_right_active"><img 
    src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner"></td>
  </tr>
  </tbody>
  </table>
</td>

<td>
  <table cellspacing="0" cellpadding="0" style="background:transparent;">
  <tbody>
  <tr>
  <td valign="middle" class="ss_tabs_td_left"><img 
    src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner"></td>
  <td valign="middle" class="ss_tabs_td" nowrap>
	<a href="" onClick="return ss_showTab(this);">
<c:if test="${!empty ssBinder.iconName}">
	   <img src="<html:imagesPath/>${ssBinder.iconName}">
</c:if>
	   <span>Sample inactive tab</span></a>
	<a href="#" onClick="ss_deleteTab(this);return false;">
	  <img src="<html:imagesPath/>skins/${ss_user_skin}/iconset/delete.gif"/>
	</a>
  </td>
  <td valign="middle" class="ss_tabs_td_right"><img 
    src="<html:imagesPath/>pics/1pix.gif" class="ss_tabs_corner"></td>
  </tr>
  </tbody>
  </table>
</td>

</tr>
</tbody>
</table>
</div>
<div class="ss_clear"></div>

<!-- End of tabs -->

