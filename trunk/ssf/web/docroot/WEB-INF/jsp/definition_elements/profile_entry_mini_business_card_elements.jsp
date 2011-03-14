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
<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />


<div class="ss_smallRBoxTop2 ss_profileBox2"></div><div class="ss_smallRBoxTop1 ss_profileBox2"></div>
<div class="ss_profileBox2" style="padding: 3px 5px;"><div class="ss_profileBox2" style="">
<table class="ss_minicard_interior">
	<tr>
		<td style="padding: 5px; vertical-align:top;">
		<a href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
			value="${ssDefinitionEntry.creation.principal.parentBinder.id}"/><ssf:param name="entryId" 
			value="${ssDefinitionEntry.creation.principal.id}"/><ssf:param name="newTab" 
			value="1" /></ssf:url>" <ssf:title tag="title.goto.profile.page" />>
			<ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_small" 
				user="${ssDefinitionEntry}" 
				folderId="${ssDefinitionEntry.parentBinder.id}" entryId="${ssDefinitionEntry.id}" />
			</a>
		</td>

		<c:if test="${!empty propertyValues__elements}">
			<td style="padding: 5px 5px 5px 0; vertical-align:top;">
			
				<c:set var="ss_element_display_style_saved" value="${ss_element_display_style}"/>
				<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
				<c:set var="ss_element_display_style_caption" value="ss_light" scope="request"/>
				<c:set var="ss_element_display_style_item" value="ss_bold" scope="request"/>
				<table cellpadding="0" cellspacing="0" border="0">
				
				<c:forEach var="element" items="${propertyValues__elements}">
				
					<c:if test="${!empty ssDefinitionEntry[element]}">
						<tr>
							<td nowrap="nowrap" valign="top">
								<c:if test="${element == 'name'}">
									  <div id="ss_presenceOptions_${renderResponse.namespace}"></div>
										  <ssf:presenceInfo user="${ssDefinitionEntry}" 
										    componentId="${renderResponse.namespace}"
										    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
													
								</c:if>
							    <span class="ss_fineprint"><c:out value="${ssDefinitionEntry[element]}" escapeXml="true"/></span>
							</td>
						</tr>
					</c:if>
					
					<c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
						<tr>
							<td valign="top">
							    <span class="${ss_element_display_style_item}"><c:out value="${ssDefinitionEntry[element]}" escapeXml="true"/></span>
							</td>
						</tr>
					</c:if>
				
				</c:forEach>
				
				</table>
				<c:set var="ss_element_display_style" value="${ss_element_display_style_saved}" scope="request"/>
			
			</td>
		</c:if>
	</tr>
</table>
</div></div>
<div class="ss_smallRBoxBtm1 ss_profileBox2"></div><div class="ss_smallRBoxBtm2 ss_profileBox2"></div>
