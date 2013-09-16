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
<% //Checkbox form element %>
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
<div class="ss_entryContent">
<c:if test="${empty ssReadOnlyFields[property_name]}">
<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
<c:set var="cb_checked" value=""/>
  <input type="hidden" name="${property_name}" id="hidden_${property_name}" value="off"/> 
</c:if>
<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
  <c:set var="cb_checked" value=" checked "/>
  <input type="hidden" name="${property_name}" id="hidden_${property_name}" value="on"/> 
</c:if>
<c:set var="required" value=""/>
<div class="${ss_fieldModifyStyle}">
<input type="checkbox" name="${property_name}XXX" ${ss_fieldModifyInputAttribute}
  id="checkbox_${property_name}XXX" <c:out value="${cb_checked}"/> 
  onClick="ss_saveCheckBoxValue(this, 'hidden_${property_name}');"/> 
 <span class="ss_labelRight"><label for="checkbox_${property_name}XXX">${property_caption}</label></span>
</div>
  <c:if test="${property_userVersionAllowed == 'true'}">
    <c:set var="property_name_per_user" value="${property_name}.${ssUser.name}"/>
	<c:if test="${!ssDefinitionEntry.customAttributes[property_name_per_user].value}" >
	  <c:set var="cb_checked_per_user" value=""/>
  	  <input type="hidden" name="${property_name_per_user}" id="hidden_${property_name_per_user}" value="off"/> 
	</c:if>
	<c:if test="${ssDefinitionEntry.customAttributes[property_name_per_user].value}" >
  	  <c:set var="cb_checked_per_user" value=" checked "/>
  	  <input type="hidden" name="${property_name_per_user}" id="hidden_${property_name_per_user}" value="on"/> 
	</c:if>
    <div class="ss_perUserFormElement">
	  <input type="checkbox" name="${property_name_per_user}XXX" 
  	    id="checkbox_${property_name_per_user}XXX" <c:out value="${cb_checked_per_user}"/> 
  	    onClick="ss_saveCheckBoxValue(this, 'hidden_${property_name_per_user}');"/> 
 	  <span class="ss_labelRight"><label for="checkbox_${property_name_per_user}XXX">
 	    <ssf:nlt tag="element.perUser.yourVersion">
 	    <ssf:param name="value" value="${property_caption}"/>
 	    </ssf:nlt></label>
 	  </span>
    </div>
  </c:if>
</c:if>

<c:if test="${!empty ssReadOnlyFields[property_name] }">
<div class="${ss_fieldModifyStyle}">
	<c:if test="${ssDefinitionEntry.customAttributes[property_name].value}" >
	<input type="checkbox" checked DISABLED>&nbsp;
	</c:if>
	<c:if test="${!ssDefinitionEntry.customAttributes[property_name].value}" >
	<input type="checkbox" DISABLED>&nbsp;
	</c:if>
	 <span class="ss_labelRight">${property_caption}</span>
</div>
</c:if>
</div>