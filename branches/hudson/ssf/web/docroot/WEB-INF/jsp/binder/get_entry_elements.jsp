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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	<taconite-replace contextNodeID="elementList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true">
	 <c:if test="${ss_filterType == 'entry'}">
	   <div id="elementList<c:out value="${ss_filterTermNumber}"/>" 
	   style="visibility:visible; display:inline;"><select
	   name="elementName<c:out value="${ss_filterTermNumber}"/>" 
	   onChange="ss_getFilterSelectionBox(this, 'elementName', 'get_element_values')">
	     <option value="" selected="selected"><ssf:nlt 
	       tag="filter.selectElement" text="--select an element--"/></option>
	     <option value="_all_entries" ><ssf:nlt 
	       tag="filter.selectEntryAny"/></option>
	     <c:forEach var="element" items="${ssEntryDefinitionElementData}">
	       <c:if test="${element.value.type == 'title' || element.value.type == 'event' || 
	                     element.value.type == 'text'  || element.value.type == 'selectbox' || 
	                     element.value.type == 'radio' || element.value.type == 'checkbox' || 
	       				 element.value.type == 'date'  || element.value.type == 'user_list' ||
	       				 element.value.type == 'userListSelectbox'}">
	         <option value="<c:out value="${element.key}"/>"><c:out value="${element.value.caption}"/></option>
	       </c:if>
	     </c:forEach>
	   </select></div>
	 </c:if>
	 
	 <c:if test="${ss_filterType == 'workflow'}">
	   <c:set var="workflowSelectBoxSize" value="1"/>
	   <c:forEach var="state" items="${ssWorkflowDefinitionStateData}">
	     <c:set var="workflowSelectBoxSize" value="${workflowSelectBoxSize + 1}"/>
	   </c:forEach>
	   <div id="elementList<c:out value="${ss_filterTermNumber}"/>" 
	   style="visibility:visible; display:inline;"><select
	   name="ss_stateNameData<c:out value="${ss_filterTermNumber}"/>" multiple="multiple" 
	   size="${workflowSelectBoxSize}">
	     <option value="" selected="selected"><ssf:nlt 
	       tag="filter.selectState"/></option>
	     <c:forEach var="state" items="${ssWorkflowDefinitionStateData}">
	       <option value="<c:out value="${state.key}"/>"><c:out value="${state.value.caption}"/></option>
	     </c:forEach>
	   </select></div>
	 </c:if>
	</taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;"></div></taconite-replace>

	<taconite-replace contextNodeID="valueData<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	  id="valueData<c:out value="${ss_filterTermNumber}"/>" 
	  style="visibility:visible; display:inline;"></div></taconite-replace>

</c:if>
</taconite-root>
