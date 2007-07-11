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
<% // Subscribe to entry on submit %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssConfigJspStyle != 'mail'}">
<div style="padding:15px 0px 4px 0px;">
<ssf:expandableArea title="${property_caption}">
<div class="ss_entryContent ss_indent_medium">
  <table>
  <tr>
    <td><input type="checkbox" name="_subscribe"/>
	</td>
	<td>
	  <span class="ss_labelRight"><ssf:nlt tag="toolbar.menu.subscribeToEntry"/></span>
	  <ssf:inlineHelp tag="ihelp.email.individual_notify_entry"/>
	</td>
  </tr>
  <tr>
    <td></td>
    <td>
	  <input type="checkbox" name="_subscribe_include_attachments"/>&nbsp;<span class="ss_labelRight">
	  <ssf:nlt tag="entry.sendMail.includeAttachments"/></span>
	</td>
  </tr>
  </table>
</div>
</ssf:expandableArea>
</div>
</c:if>
