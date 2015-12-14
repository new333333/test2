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

	<taconite-replace contextNodeID="valueList<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	id="valueList<c:out value="${ss_filterTermNumber}"/>" 
	style="visibility:visible; display:inline;">
	  <c:forEach var="element" items="${ssEntryDefinitionElementData}">
		<c:if test="${element.key == ss_filter_entry_element_name}">
	       
	       <c:if test="${element.value.type == 'title' || element.value.type == 'text'}">
	         <ssf:nlt tag="filter.searchText" text="Search text"/>: <input 
	         type="text" class="ss_text" style="width:150px;" 
	         name="elementValue<c:out value="${ss_filterTermNumber}"/>" />
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'event'}">
	         <ssf:nlt tag="filter.date" text="Date"/>: ...
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'date'}">
	         <select name="elementValueDateType<c:out value="${ss_filterTermNumber}"/>" 
	         onChange="ss_getFilterSelectionBox(this, 'elementValueDateType', 'get_element_value_data', 'date')" >
	           <option value=""><ssf:nlt tag="filter.selectDateOption"/></option>
	           <option value="onOrBefore"><ssf:nlt tag="filter.onOrBefore"/></option>
	           <option value="onOrAfter"><ssf:nlt tag="filter.onOrAfter"/></option>
	           <option value="withinNextFewDays"><ssf:nlt tag="filter.withinNextFewDays"/></option>
	           <option value="withinPastFewDays"><ssf:nlt tag="filter.withinPastFewDays"/></option>
	         </select>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'selectbox' || element.value.type == 'radio'}">
			 <select
		       name="elementValue<c:out value="${ss_filterTermNumber}"/>" 
		       multiple="multiple" 
		       size="<c:out value="${element.value.length}"/>"
		     >
				<c:forEach var="option" items="${element.value.values}">
				  <option value="<c:out value="${option.key}"/>"><c:out value="${option.value}"/></option>
				</c:forEach>
		       </select>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'checkbox'}">
	         <input type="checkbox" 
	         name="elementValue<c:out value="${ss_filterTermNumber}"/>" 
	         checked="checked"/> <c:out value="${element.value.caption}"/>
	         <input type="hidden" 
	         name="elementValueType<c:out value="${ss_filterTermNumber}"/>" 
	         value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'entryAttributes'}">
			 <select
		       name="elementValue${ss_filterTermNumber}" 
		       multiple="multiple" 
		       size="${element.value.length}"
		     >
				<c:forEach var="option" items="${element.value.values}">
				  <option value="${option.key}">${option.value}</option>
				</c:forEach>
		       </select>
	         <input type="hidden" name="elementValueType${ss_filterTermNumber}" value="${element.value.type}"/>
	       </c:if>
	       
	       <c:if test="${element.value.type == 'user_list' || element.value.type == 'userListSelectbox'}">
	         <ssf:nlt tag="filter.users" text="Users"/>: ...
	       </c:if>
		</c:if>
	  </c:forEach>
	 </div></taconite-replace>
       
	<taconite-replace contextNodeID="valueData<c:out value="${ss_filterTermNumber}"/>" 
	parseInBrowser="true"><div 
	  id="valueData<c:out value="${ss_filterTermNumber}"/>" 
	  style="visibility:visible; display:inline;">
	</div></taconite-replace>

</c:if>
</taconite-root>
