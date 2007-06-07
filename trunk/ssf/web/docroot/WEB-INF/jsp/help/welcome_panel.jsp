<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<table class="ss_popup" cellpadding="0" cellspacing="0" border="0" style="width: 275px;"><tbody>
<tr>
 <td width="40"><div class="ss_popup_topleft"></div></td>
 <td width="100%"><div class="ss_popup_topcenter"><span class="ss_bold"><ssf:nlt tag="help.welcome"><ssf:param name="value" value="${ssProductName}"/></ssf:nlt></span>
  <a href="javascript:;" onClick="ss_helpSystem.showPreviousHelpSpot();return false;"
  ><img border="0" style="margin-left: 20px;" src="<html:imagesPath/>pics/sym_arrow_left_.gif"/></a>
   <a href="javascript:;" onClick="ss_helpSystem.showNextHelpSpot();return false;"
  ><img border="0" src="<html:imagesPath/>pics/sym_arrow_right_.gif"/></a>   
 
 </div></td>
 <td width="40">
   <div class="ss_popup_topright">
   <div class="ss_popup_close" onClick="ss_helpSystem.hide();return false;"><img border="0" src="<html:imagesPath/>pics/1pix.gif"/></div>
 </td>
</tr>
<tr><td colspan="3"><div id="ss_help_welcome_panel_body" class="ss_popup_body" style="padding-top: 2px; padding-left: 10px; padding-right: 10px; padding-bottom: 1px;">

<table align="center">
<tr>
  <td align="center" colspan="3"><span style="font-size:10px;"  class="ss_titlebold"><a href="javascript:;" 
    onClick="ss_helpSystem.showHelpPanel('help_on_help','ss_help_on_help','right','bottom'); return false;"><ssf:nlt tag="help.instructions"/></a></span></td>
</tr>
<tr>
<td align="center" colspan="3"><a href="javascript:;" 
  onClick="ss_helpSystem.toggleTOC();return false;"><ssf:nlt tag="help.toc"/></a></td>
</tr>
<tr>
<td align="center" colspan="3">
  <a class="ss_linkButton ss_smallprint" href="javascript:;" 
    onClick="ss_helpSystem.showHelpPanel('print_manuals','ss_help_print_manuals','right','bottom'); return false;"><ssf:nlt tag="help.button.viewBooks"/></a>
  <a class="ss_linkButton ss_smallprint" href="javascript:;" 
    onClick="ss_helpSystem.hide(); return false;"><ssf:nlt tag="help.button.exit.help"/></a>
</td>
</tr>
</table>
<table align="center">
<tr>
<td>&nbsp;</td>
<td align="center"><div id="ss_help_toc" class="ss_helpToc" align="left"></td>
<td>&nbsp;</td>
</tr>
</table>

</div></td></tr>
<tr>
 <td width="40"><div class="ss_popup_bottomleft" style="height: 5px;"></div></td>
 <td width="100%"><div class="ss_popup_bottomcenter" style="height: 5px;"></div></td>
 <td width="40"><div class="ss_popup_bottomright" style="height: 5px;"></div></td>
</tr>
</tbody></table>
