<!-- Start of tabs -->

<div id="ss_tabbar">
<div class="ss_tabs">
<table cellspacing="0" cellpadding="0" style="background:transparent;">
<tbody>
<tr>

<td>
  <table cellspacing="0" cellpadding="0" style="background:transparent;">
  <tbody>
  <tr>
  <td valign="middle" class="ss_tabs_td_left_active"></td>
  <td valign="middle" class="ss_tabs_td_active" nowrap>
	<a href="" onClick="return ss_showTab(this);">
<c:if test="${!empty ssBinder.iconName}">
	   <img src="<html:imagesPath/>${ssBinder.iconName}">
</c:if>
	   <span>${ssBinder.title}</span></a>
  </td>
  <td valign="middle" class="ss_tabs_td_right_active"></td>
  </tr>
  </tbody>
  </table>
</td>

<td>
  <table cellspacing="0" cellpadding="0" style="background:transparent;">
  <tbody>
  <tr>
  <td valign="middle" class="ss_tabs_td_left"></td>
  <td valign="middle" class="ss_tabs_td" nowrap>
	<a href="" onClick="return ss_showTab(this);">
<c:if test="${!empty ssBinder.iconName}">
	   <img src="<html:imagesPath/>${ssBinder.iconName}">
</c:if>
	   <span>Sample inactive tab</span></a>
  </td>
  <td valign="middle" class="ss_tabs_td_right"></td>
  </tr>
  </tbody>
  </table>
</td>

</tr>
</tbody>
</table>
</div>

<div class="ss_decor-round-corners-top1"><div><div></div></div></div>
</div>
<!-- End of tabs -->

