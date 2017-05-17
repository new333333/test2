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

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

	  <c:if test="${ss_filterType == 'entry'}">
    	<select name="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	   id="ss_entry_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	  onChange="ss_getFilterSelectionBox(this, 'ss_entry_def_id', 'get_entry_elements')">
    	  <option value="" selected="selected"><ssf:nlt tag="filter.selectElement"/></option>
    	  <option value="_common"><ssf:nlt tag="filter.commonElements"/></option>
		    <c:forEach var="item" items="${ssEntryDefinitionMap}">
		        <option value="<c:out value="${item.value.id}"/>"><c:out 
		          value="${item.value.title}"/></option>
		    </c:forEach>
		    <c:forEach var="item" items="${ssPublicProfileEntryDefinitions}">
		      <c:if test="${!empty ssEntryDefinitionMap[item.key]}">
		        <option value="<c:out value="${item.value.id}"/>"><c:out 
		          value="${item.value.title}"/></option>
		      </c:if>
		    </c:forEach>
    	</select>
	  </c:if>


<c:if test="${ss_filterType != 'entry'}">
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

	<taconite-replace contextNodeID="entryList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div id="entryList<c:out value="${ss_filterTermNumber}"/>" 
	style="display:inline;">
	  <c:if test="${ss_filterType == 'text'}">
         <ssf:nlt tag="filter.searchText" text="Search text"/>: <input 
         type="text" class="ss_text" style="width:200px;" 
         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	  </c:if>
	  <c:if test="${ss_filterType == 'workflow'}">
    	<select name="ss_workflow_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	   id="ss_workflow_def_id<c:out value="${ss_filterTermNumber}"/>" 
    	  onChange="ss_getFilterSelectionBox(this, 'ss_workflow_def_id', 'get_workflow_states')">
    	  <option value="" selected="selected"><ssf:nlt tag="filter.selectWorkflow"/></option>
		  <c:forEach var="item" items="${ssWorkflowDefinitionMap}">
		    <option value="<c:out value="${item.value.id}"/>"><c:out 
		      value="${item.value.title}"/></option>
		  </c:forEach>
    	</select>
	  </c:if>
	 <c:if test="${ss_filterType == 'folders'}">
	 </c:if>
	  <input type="hidden" name="filterType<c:out value="${ss_filterTermNumber}"/>"
	    value="<c:out value="${ss_filterType}"/>"/>
	</div></taconite-replace>

	<taconite-replace contextNodeID="elementList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="elementList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	 </div></taconite-replace>

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	 </div></taconite-replace>
</taconite-root>
</c:if>
</c:if>
