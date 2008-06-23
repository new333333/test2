<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>

<%@ page import="com.sitescape.team.util.NLT" %>

<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>
<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="displaydiv" parseInBrowser="true">
	  <div id="displaydiv" style="margin:0px; padding:4px;"> 
	  <c:choose>
	  <c:when test="${data['option'] == 'copyDefinition'}">
	  		<span class="ss_titlebold"><ssf:nlt tag="administration.copy.definition.rename"/></span><br/><br/>
			<span><ssf:nlt tag="definition.name"/></span><br/>
			<input type="text" class="ss_text" name="propertyId_name" size="40" value="<c:out value="${ssDefinition.name}-2" escapeXml="true"/>"/><br/>
			<span><ssf:nlt tag="definition.caption"/></span><br/>
			<input type="text" class="ss_text" name="propertyId_caption" size="40" value="<c:out value="${ssDefinition.title}-2" escapeXml="true"/>"/><br/><br/>
		</c:when>	  
	  <c:when test="${data['option'] == 'deleteDefinition'}">
			<span class="ss_titlebold"><ssf:nlt tag="definition.delete"/></span><span><c:out value="${data.selectedItemTitle}" escapeXml="true"/></span>
	  </c:when>	  
	  <c:when test="${data['option'] == 'view_definition_options'}">
		<span class="ss_titlebold"><c:out value="${data.selectedItemTitle}" escapeXml="true"/></span>
			
		<table><tbody>
		<tr><td><a href="javascript: ;" onClick="return modifyDefinition();"><ssf:nlt tag="definition.modifyProperties"/></a></td></tr>
		<tr><td><a href="javascript: ;\" onClick="return copyDefinition();"><ssf:nlt tag="definition.copyDefinition"/></a></td></tr>
		<tr><td><a href="javascript: ;\" onClick="return deleteDefinition();"><ssf:nlt tag="definition.deleteDefinition"/></a></td></tr>
			
		<c:if test="${!empty ssDefinition && ssDefinition.visibility != 1 && ssIsAdmin}">
		<tr><td><a href="javascript: ;\" onClick="return setVisibility(1);"><ssf:nlt tag="definition.setPublic"/></a></td></tr>
		</c:if>
		</tbody></table>
	  </c:when>
	  <c:otherwise>
		<ssf:buildDefinitionDivs title="<%= NLT.get("definition.select_item") %>"
		  sourceDocument="${data.sourceDefinition}" 
		  configDocument="${ssConfigDefinition}"
		  option="${data.option}" 
		  itemId="${data.itemId}" 
		  itemName="${data.itemName}" 
		  refItemId="${data.refItemId}" 
		/>
		</c:otherwise>
		</c:choose>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>

