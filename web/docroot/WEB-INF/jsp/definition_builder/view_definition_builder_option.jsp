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
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>

<%@ page import="org.kablink.teaming.util.NLT" %>

<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>
<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="displaydiv" parseInBrowser="true">
	  <div id="displaydiv" style="margin:0px; padding:4px 14px;"> 
	  <c:choose>
	  <c:when test="${data['option'] == 'copyDefinition'}">
	  		<div class="ss_titlebold designer_dialog_title"><ssf:nlt tag="administration.copy.definition.rename"/></div>
			<span><ssf:nlt tag="definition.name"/></span>
			<input type="text" class="ss_text" name="propertyId_name" size="40" value="<c:out value="${ssDefinition.name}-2" escapeXml="true"/>"/>
			<span><ssf:nlt tag="definition.caption"/></span>
			<input type="text" class="ss_text" name="propertyId_caption" size="40" value="<c:out value="${ssDefinition.title}-2" escapeXml="true"/>"/>
		</c:when>	  
	  <c:when test="${data['option'] == 'deleteDefinition'}">
			<div class="ss_titlebold designer_dialog_title"><ssf:nlt tag="definition.delete"/></span><span><c:out value="${data.selectedItemTitle}" escapeXml="true"/></div>
	  </c:when>	  
	  <c:when test="${data['option'] == 'view_definition_options'}">
			<div class="ss_titlebold designer_dialog_title" title="${data.selectedItemTitle}"
			><c:out value="${data.selectedItemTitle}" escapeXml="true"/></div>
			
		<table><tbody>
		<tr><td><a href="javascript: ;" onClick="return modifyDefinition();"><ssf:nlt tag="definition.modifyProperties"/></a></td></tr>
		<tr><td><a href="javascript: ;" onClick="return copyDefinition();"><ssf:nlt tag="definition.copyDefinition"/></a></td></tr>
		<tr><td><a href="javascript: ;" onClick="return deleteDefinition();"><ssf:nlt tag="definition.deleteDefinition"/></a></td></tr>
		<tr><td><a href="<ssf:url webPath="definitionDownload"><ssf:param
				name="id_${ssDefinition.id}" value="on"/></ssf:url>"><ssf:nlt tag="definition.exportDefinition"/></a></td></tr>
		<c:set var="defBinderId" value=""/>
		<c:if test="${ssDefinition.binderId != -1}">
			<c:set var="defBinderId" value="${ssDefinition.binderId}"/>
		</c:if>
		<c:if test="${ssDefinition.binderId != -1}">
		<tr><td><a href="javascript: ;" onClick="return moveDefinition();"><ssf:nlt tag="definition.moveDefinition"/></a></td></tr>
		</c:if>
		<c:if test="${ssDefinition.binderId != -1  && ssIsAdmin}">
		<tr><td><a href="javascript: ;" onClick="return setVisibility(${ssDefinition.visibility}, '');"><ssf:nlt tag="definition.setGlobal"/></a></td></tr>
		</c:if>
		<c:if test="${ssDefinition.visibility == 1}">
		<tr><td><a href="javascript: ;" onClick="return setVisibility(3, '${defBinderId}');"><ssf:nlt tag="definition.setDeprecated"/></a></td></tr>
		</c:if>
		<c:if test="${ssDefinition.visibility == 3}">
		<tr><td><a href="javascript: ;" onClick="return setVisibility(1, '${defBinderId}');"><ssf:nlt tag="definition.setNotDeprecated"/></a></td></tr>
		</c:if>
		</tbody></table>
	  </c:when>
	  <c:otherwise>
	  <c:if test="${ssDefinition.binderId == -1}">
		<ssf:buildDefinitionDivs title='<%= NLT.get("definition.select_item") %>'
		  sourceDocument="${data.sourceDefinition}" 
		  configDocument="${ssConfigDefinition}"
		  option="${data.option}" 
		  itemId="${data.itemId}" 
		  itemName="${data.itemName}" 
		  refItemId="${data.refItemId}" 
		/>
		</c:if>
	  <c:if test="${ssDefinition.binderId != -1}">
		<ssf:buildDefinitionDivs title='<%= NLT.get("definition.select_item") %>'
		  sourceDocument="${data.sourceDefinition}" 
		  configDocument="${ssConfigDefinition}"
		  option="${data.option}" 
		  itemId="${data.itemId}" 
		  itemName="${data.itemName}" 
		  refItemId="${data.refItemId}" 
		  owningBinderId="${ssDefinition.binderId}"
		/>
		</c:if>
		
		</c:otherwise>
		</c:choose>
	  </div>
	</taconite-replace>
</c:if>
</taconite-root>

