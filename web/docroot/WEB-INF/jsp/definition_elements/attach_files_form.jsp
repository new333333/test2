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
<% //File form for attaching files %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_fieldModificationsAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:if test="${empty ss_fieldModifyDisabled || ss_fieldModificationsAllowed}">
<c:if test="${empty property_hide || !property_hide || (!empty ssDefinitionEntry && !empty ssDefinitionEntry.fileAttachments)}">
 <c:if test='${empty property_number}'>
  <c:set var="property_number" value="1"/>
 </c:if>
<div class="ss_entryContent">
<c:set var="ss_fileBrowseOfferMoreFiles" value="true"/>
<%@ include file="/WEB-INF/jsp/definition_elements/file_browse.jsp" %>
<c:if test="${!empty ssDefinitionEntry.fileAttachments}">
<span class="ss_bold"><ssf:nlt tag="form.attachments.currentFiles" /></span>
<br/>
<table cellspacing="2" cellpadding="2">
<c:forEach var="selection" items="${ssDefinitionEntry.fileAttachments}">
<tr>
<td valign="top" style="padding-left:16px;"><input type="checkbox" name="_delete_${selection.id}"></td>
<td valign="top" style="padding-left:10px;">${selection.fileItem.name}</td>
<td valign="top" style="padding-left:16px;">
  <a href="javascript: ;" onClick="ss_showHide('editCommentDiv_${selection.id}');return false;">
    <span class="ss_fineprint"><ssf:nlt tag="file.addComment"/></span>
  </a>
  <div id="editCommentDiv_${selection.id}" style="padding:4px; display:none;">
    <ssf:htmleditor name="ss_attachFile${selection.id}.description" height="100" 
      toolbar="minimal">${selection.fileItem.description.text}</ssf:htmleditor>
  </div>
</td>
</tr>
</c:forEach>
</table>
<span class="ss_small">(<ssf:nlt tag="form.atachments.selectForDelete" />)</span>
<br/>
</c:if>
<br/>
</div>
</c:if>
</c:if>
