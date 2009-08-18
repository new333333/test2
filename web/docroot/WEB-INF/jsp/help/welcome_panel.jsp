<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<table class="ss_popup" cellpadding="0" cellspacing="0" border="0" style="width: 300px;"><tbody>
<tr>
 <td width="100%"><div class="ss_popup_top"><div class="ss_popup_title"><ssf:nlt tag="help.welcome"><ssf:param name="value" value="${ssProductTitle}"/></ssf:nlt>
	<img id="ss_help_cpanel_show_control" border="0" src="<html:imagesPath/>pics/1pix.gif"
   <c:if test="${help_cpanel_show}">
   class="ss_help_cpanel_show"
   </c:if>
   <c:if test="${!help_cpanel_show}">
   class="ss_help_cpanel_hide"
   </c:if>
   onClick="javascript: ss_helpSystem.toggleShowHelpCPanel(); return false;"/></div>
 </div></td>
 <td width="40">
  <div class="ss_popup_top"><div class="ss_popup_close" onClick="ss_helpSystem.hide();return false;"><img border="0" src="<html:imagesPath/>pics/1pix.gif"/></div></div>
 </td>
</tr>
<tr id="ss_help_welcome_panel_body" style="<c:if test="${!help_cpanel_show}"> display: none;</c:if>">
<td colspan="2"><div class="ss_popup_body" style="padding-top: 2px; padding-left: 10px; padding-right: 10px; padding-bottom: 1px;">
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
   <table>
   <tr>
   <td><a class="ss_linkButton ss_smallprint" href="javascript: ;" 
     onClick="ss_helpSystem.showInlineHelpSpotInfo(this, 'print_manuals', '', 0, 0, '', '');return false;"
   ><ssf:nlt tag="help.button.viewBooks"/></a></td>
   <td><a class="ss_linkButton ss_smallprint" href="javascript:;" 
    onClick="ss_helpSystem.hide(); return false;"><ssf:nlt tag="help.button.exit.help"/></a></td>
   </td>
   </tr>
   </table>
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

</div>
</td></tr>
</tbody></table>
