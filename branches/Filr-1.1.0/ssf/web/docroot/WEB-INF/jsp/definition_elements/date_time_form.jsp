<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<% //Date widget form element %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<%@ page import="org.kablink.teaming.util.CalendarHelper" %>
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
<c:if test="${empty ssReadOnlyFields[property_name]}">
<c:if test="${property_required}"><c:set var="ss_someFieldsRequired" value="true" scope="request"/></c:if>
<%
	String caption1 = (String) request.getAttribute("property_caption");
	String caption2 = NLT.get("general.required.caption", new Object[]{caption1});
%>

<style type="text/css">
        @import "<html:rootPath />js/dojo/dijit/themes/tundra/tundra.css";
        @import "<html:rootPath />js/dojo/dojo/resources/dojo.css"
</style>

<script type="text/javascript">
	dojo.addOnLoad(function() {
			dojo.addClass(document.body, "tundra");
		}
	);	
	dojo.require("dijit.form.DateTextBox");
	dojo.require("dijit.form.TimeTextBox");
</script>
	
<div class="ss_entryContent tundra ${ss_fieldModifyStyle}">
	<span class="ss_labelAbove" id='${property_name}_label'>
	${property_caption}<c:if test="${property_required}"><span 
	  id="ss_required_${property_name}" title="<%= caption2 %>" class="ss_required">*</span></c:if></span>
	<div id="${property_name}_error" style="visibility:hidden; display:none;"><span 
	  class="ss_formError"><ssf:nlt tag="date.validate.error"/></span></div>
	

	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name].value}"/>
		<c:set var="property_initialSetting" value="entry"/>
	</c:if>

	<table class="ss_style" cellpadding="0" border="0">
		<tr>
			<td>
				<input type="text" dojoType="dijit.form.DateTextBox" ${ss_fieldModifyInputAttribute}
					id="date_${property_name}_${prefix}" 
					name="${property_name}_fullDate" 
					lang="<ssf:convertLocaleToDojoStyle />" 
					<c:if test="${property_initialSetting != 'none'}">
					  value="<fmt:formatDate value="${initDate}" 
					    pattern="yyyy-MM-dd" timeZone="${ssUser.timeZone.ID}"/>"
					</c:if>
					<c:if test="${property_initialSetting == 'none'}">
					  value=""
					</c:if>
				/>
			</td>
			<td>
				<input type="text" dojoType="dijit.form.TimeTextBox" ${ss_fieldModifyInputAttribute}
					id="date_time_${property_name}_${prefix}" 
					name="${property_name}_0_fullTime" 
					lang="<ssf:convertLocaleToDojoStyle />" 
					<c:if test="${property_initialSetting != 'none'}">
					  value="T<fmt:formatDate value="${initDate}" 
					    pattern="HH:mm:ss" timeZone="${ssUser.timeZone.ID}"/>"
					</c:if>
					<c:if test="${property_initialSetting == 'none'}">
					  value=""
					</c:if>
				></div>
				<input type="hidden" name="${property_name}_timezoneid" value="${ssUser.timeZone.ID}" />
			</td>
		</tr>
	</table>
	
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
		<input type="hidden" name="${property_name}_dateExistedBefore" value="true" />
	</c:if>
	<input type="hidden" name="${property_name}" value="" />
	
</div>
<c:if test="${property_userVersionAllowed == 'true'}">
    <c:set var="property_name_per_user" value="${property_name}.${ssUser.name}"/>
<div class="ss_entryContent tundra">
	<span class="ss_labelAbove" id='${property_name_per_user}_label'>
	<ssf:nlt tag="element.perUser.yourVersion"><ssf:param name="value" value="${property_caption}"/></ssf:nlt>
	</span>
	<div id="${property_name_per_user}_error" style="visibility:hidden; display:none;"><span 
	  class="ss_formError"><ssf:nlt tag="date.validate.error"/></span></div>
	

	<c:set var="initDate" value="<%= new Date() %>"/>
	<c:set var="property_initialSetting" value="none"/>
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name_per_user].value}">
		<c:set var="initDate" value="${ssDefinitionEntry.customAttributes[property_name_per_user].value}"/>
		<c:set var="property_initialSetting" value="entry"/>
	</c:if>

	<table class="ss_style" cellpadding="0" border="0">
		<tr>
			<td>
				<input type="text" dojoType="dijit.form.DateTextBox" 
					id="date_${property_name_per_user}_${prefix}" 
					name="${property_name_per_user}_fullDate" 
					lang="<ssf:convertLocaleToDojoStyle />" 
					<c:if test="${property_initialSetting != 'none'}">
					  value="<fmt:formatDate value="${initDate}" 
					    pattern="yyyy-MM-dd" timeZone="${ssUser.timeZone.ID}"/>"
					</c:if>
					<c:if test="${property_initialSetting == 'none'}">
					  value=""
					</c:if>
				/>
			</td>
			<td>
				<input type="text" dojoType="dijit.form.TimeTextBox"
					id="date_time_${property_name_per_user}_${prefix}" 
					name="${property_name_per_user}_0_fullTime" 
					lang="<ssf:convertLocaleToDojoStyle />" 
					<c:if test="${property_initialSetting != 'none'}">
					  value="T<fmt:formatDate value="${initDate}" 
					    pattern="HH:mm:ss" timeZone="${ssUser.timeZone.ID}"/>"
					</c:if>
					<c:if test="${property_initialSetting == 'none'}">
					  value=""
					</c:if>
				></div>
				<input type="hidden" name="${property_name_per_user}_timezoneid" value="${ssUser.timeZone.ID}" />
			</td>
		</tr>
	</table>
	
	<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name_per_user].value}">
		<input type="hidden" name="${property_name_per_user}_dateExistedBefore" value="true" />
	</c:if>
	<input type="hidden" name="${property_name_per_user}" value="" />
	
</div>
</c:if>
</c:if>

<c:if test="${!empty ssReadOnlyFields[property_name]}">
<div class="ss_entryContent ${ss_fieldModifyStyle}">
<span class="ss_labelAbove">${property_caption}</span>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name].value}">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${ssDefinitionEntry.customAttributes[property_name].value}" 
				      type="both" dateStyle="medium" timeStyle="short" /> &nbsp;
</c:if>
</div>

<c:if test="${property_userVersionAllowed == 'true'}">
    <c:set var="property_name_per_user" value="${property_name}.${ssUser.name}"/>
<div class="ss_entryContent">
<span class="ss_labelAbove"><ssf:nlt tag="element.perUser.yourVersion">
 	    <ssf:param name="value" value="${property_caption}"/>
 	    </ssf:nlt></span>
<c:if test="${!empty ssDefinitionEntry.customAttributes[property_name_per_user].value}">
<fmt:formatDate timeZone="${ssUser.timeZone.ID}"
				      value="${ssDefinitionEntry.customAttributes[property_name_per_user].value}" 
				      type="both" dateStyle="medium" timeStyle="short" /> &nbsp;
</c:if>
</div>
</c:if>

</c:if>