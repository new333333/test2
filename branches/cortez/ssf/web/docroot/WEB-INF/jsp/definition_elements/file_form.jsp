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
<% //File form element %>
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
<div class="ss_entryContent ${ss_fieldModifyStyle}">

<c:set var="count" value="0"/>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<c:forEach var="selection" items="${selections}">
  <c:set var="count" value="${count + 1}"/>
</c:forEach>
</c:if>
<c:if test="${count > 0}"><c:set var="property_required" value="" scope="request"/></c:if>
<c:set var="ss_fileBrowseOfferMoreFiles" value="false"/>
<%@ include file="/WEB-INF/jsp/definition_elements/file_browse.jsp" %>

<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name]}">
<c:set var="selections" value="${ssDefinitionEntry.customAttributes[property_name].value}" />
<span class="ss_bold"><ssf:nlt tag="form.file.currentFiles"><ssf:param name="value" value="${property_caption}"/></ssf:nlt></span>
<br/>
<table cellspacing="0" cellpadding="0" border="0"><tbody>
<c:forEach var="selection" items="${selections}">
<tr id="${selection.id}">
<c:if test="${count > 0}">
<td><a class="ss_inlineButton" 
<c:if test="${empty ss_fieldModifyDisabled || ss_fieldModificationsAllowed}">
  onClick="ss_moveThisTableRow(this, '${renderResponse.namespace}', 'down');ss_saveFileOrder(this, '${property_name}__order');"
</c:if>
><img alt="<ssf:nlt tag="favorites.movedown"/>" title="<ssf:nlt tag="favorites.movedown"/>" 
src="<html:imagesPath/>icons/button_move_down.gif" 
/></a></td>

<td><a class="ss_inlineButton" 
<c:if test="${empty ss_fieldModifyDisabled || ss_fieldModificationsAllowed}">
  onClick="ss_moveThisTableRow(this, '${renderResponse.namespace}', 'up');ss_saveFileOrder(this, '${property_name}__order');"
</c:if>
><img alt="<ssf:nlt tag="favorites.moveup"/>" title="<ssf:nlt tag="favorites.moveup"/>" 
src="<html:imagesPath/>icons/button_move_up.gif" 
/></a></td>
</c:if>

<td><input type="checkbox" name="_delete_${selection.id}"
>&nbsp;${selection.fileItem.name}</td>

</tr>
</c:forEach>
</tbody></table>	
<span class="ss_small">(<ssf:nlt tag="form.file.selectForDelete"/>)</span>
<br/>
<br/>
  <c:if test="${count > 0}">
    <input type="hidden" name="${property_name}__order"/>
    <script type="text/javascript">
function ss_saveFileOrder(obj, name) {
	var formObj = ss_getContainingForm(obj);
	var hiddenObj = formObj[name]
	hiddenObj.value = "";
	var tableNode = ss_findOwningElement(obj, 'tbody')
	for (var i = 0; i < tableNode.childNodes.length; i++) {
		var node = tableNode.childNodes[i]
		if (node.tagName && node.tagName.toLowerCase() == 'tr') {
			if (hiddenObj.value != '') hiddenObj.value += ' ';
			hiddenObj.value += node.id;
		}
	}
	//alert('hiddenObj.value = '+hiddenObj.value)
}
    </script>
  </c:if>
</c:if>

</div>
