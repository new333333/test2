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
<% // radio selection %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<c:set var="ss_fieldModifyDisabled" value=""/>
<c:set var="ss_fieldModifyStyle" value=""/>
<c:if test="${ss_accessControlMap['ss_modifyEntryRightsSet']}">
  <c:if test="${(!ss_accessControlMap['ss_modifyEntryFieldsAllowed'] && !ss_accessControlMap['ss_modifyEntryAllowed']) || 
			(!ss_accessControlMap['ss_modifyEntryAllowed'] && !ss_radioFieldModificationAllowed == 'true')}">
    <c:set var="ss_fieldModifyStyle" value="ss_modifyDisabled"/>
    <c:set var="ss_fieldModifyInputAttribute" value=" disabled='disabled' "/>
    <c:set var="ss_fieldModifyDisabled" value="true"/>
  </c:if>
</c:if>
<c:set var="radio_property_name" value="${property_name}"/>
<c:if test="${empty ssReadOnlyFields[radioGroupName]}">
<c:set var="checked" value=""/>
<c:if test="${ssDefinitionEntry.customAttributes[radioGroupName].value == radio_property_name}">
  <c:set var="checked" value="checked"/>
</c:if>
<c:if test="${empty ssDefinitionEntry.customAttributes[radioGroupName].value && property_selected}">
  <c:set var="checked" value="checked"/>
</c:if>
<c:if test="${ss_radioButtonsLayout == 'horizontal'}">
<td>
</c:if>
<input type="radio" name="<c:out value="${radioGroupName}"/>" ${ss_fieldModifyInputAttribute}
  value="<c:out value="${radio_property_name}"/>" <c:out value="${checked}"/>
/>&nbsp;<span class="ss_bold ${ss_fieldModifyStyle}"><c:out value="${property_caption}"/></span>
 <ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
<c:if test="${ss_radioButtonsLayout == 'horizontal'}">
</td>
</c:if>
<c:if test="${ss_radioButtonsLayout != 'horizontal'}">
<br/>
</c:if>
</c:if>
<c:if test="${!empty ssReadOnlyFields[radioGroupName]}">
<c:if test="${ssDefinitionEntry.customAttributes[radioGroupName].value == radio_property_name}">
<c:out value="${property_caption}"/> &nbsp;
</c:if>
 <ssf:displayConfiguration 
  configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" />
</c:if>
