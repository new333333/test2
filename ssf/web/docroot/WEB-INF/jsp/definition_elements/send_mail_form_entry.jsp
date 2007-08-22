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
<% // Send mail on submit %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:if test="${ssConfigJspStyle != 'mail'}">
<div class="ss_entryContent">
<div style="padding:15px 0px 15px 0px;">
<ssf:expandableArea title="${property_caption}">
<div class="ss_entryContent">
  <span class="ss_labelAbove"><ssf:nlt tag="entry.sendMail.toList" /></span>
  <ssf:find formName="${formName}" formElement="_sendMail_toList" type="user" />

<c:if test="${!empty ssFolder.teamMemberIds}">
  <input type="checkbox" name="_sendMail_toTeam" />
  <span class="ss_labelAfter"><label for="_sendMail_toTeam">
    <ssf:nlt tag="entry.sendMail.toTeam"/>
  </label></span>
  <br/>
</c:if>
  <br/>

  <span class="ss_labelAbove"><label for="_sendMail_subject">
    <ssf:nlt tag="entry.sendMail.subject"/>
  </label></span>
  <input type="text" size="80" name="_sendMail_subject" />
  <br/>

  <span class="ss_labelAbove"><label for="_sendMail_body">
    <ssf:nlt tag="entry.sendMail.body"/>
  </label></span>
  <ssf:htmleditor name="_sendMail_body" height="200" />

  <br/>
  <input type="checkbox" name="_sendMail_includeAttachments" />
  <span class="ss_labelAfter"><label for="_sendMail_includeAttachments">
    <ssf:nlt tag="entry.sendMail.includeAttachments"/>
  </label></span>
</div>
</ssf:expandableArea>
</div>
</div>
</c:if>
