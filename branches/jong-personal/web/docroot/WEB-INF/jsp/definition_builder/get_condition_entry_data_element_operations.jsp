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

	<taconite-replace contextNodeID="conditionOperations" 
	parseInBrowser="true"><div 
	   id="conditionOperations" >
       <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'event' || 
                     ssEntryDefinitionElementData[conditionElementName].type == 'selectbox' || 
                     ssEntryDefinitionElementData[conditionElementName].type == 'radio' || 
                     ssEntryDefinitionElementData[conditionElementName].type == 'checkbox' || 
       				 ssEntryDefinitionElementData[conditionElementName].type == 'date'  || 
       				 ssEntryDefinitionElementData[conditionElementName].type == 'date_time'  || 
       				 ssEntryDefinitionElementData[conditionElementName].type == 'text'  || 
       				 ssEntryDefinitionElementData[conditionElementName].type == 'number'}">
	   <span class="ss_bold"><ssf:nlt tag="definition.selectEntryDataOperation"/></span><br/>
	   <select
	   name="conditionElementOperation" 
	   onChange="getConditionSelectbox(this, 'get_condition_entry_data_element_values')"
	   >
	     <option value="" selected="selected"><ssf:nlt 
	       tag="filter.selectElementOperation"/></option>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'event'}">
		         <option value="setCurrentDateStart"><ssf:nlt tag="definition.operation_setCurrentDateStart"/></option>
		         <option value="incrementStart"><ssf:nlt tag="definition.operation_incrementStart"/></option>
		         <option value="decrementStart"><ssf:nlt tag="definition.operation_decrementStart"/></option>
		         <option value="setCurrentDateEnd"><ssf:nlt tag="definition.operation_setCurrentDateEnd"/></option>
		         <option value="incrementEnd"><ssf:nlt tag="definition.operation_incrementEnd"/></option>
		         <option value="decrementEnd"><ssf:nlt tag="definition.operation_decrementEnd"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'date'}">
		         <option value="setCurrentDate"><ssf:nlt tag="definition.operation_setCurrentDate"/></option>
		         <option value="increment"><ssf:nlt tag="definition.operation_increment"/></option>
		         <option value="decrement"><ssf:nlt tag="definition.operation_decrement"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'date_time'}">
		         <option value="setCurrentDate"><ssf:nlt tag="definition.operation_setCurrentDate"/></option>
		         <option value="increment"><ssf:nlt tag="definition.operation_increment"/></option>
		         <option value="decrement"><ssf:nlt tag="definition.operation_decrement"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'checkbox'}">
		       <option value="set"><ssf:nlt tag="definition.operation_set"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'selectbox'}">
		         <option value="set"><ssf:nlt tag="definition.operation_set"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'radio'}">
		         <option value="set"><ssf:nlt tag="definition.operation_set"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'text'}">
		         <option value="set"><ssf:nlt tag="definition.operation_set"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'number'}">
		         <option value="set"><ssf:nlt tag="definition.operation_set"/></option>
		         <option value="increment"><ssf:nlt tag="definition.operation_increment"/></option>
		         <option value="decrement"><ssf:nlt tag="definition.operation_decrement"/></option>
		     </c:if>
	   </select>
	   </c:if>
	   </div></taconite-replace>

	<taconite-replace contextNodeID="conditionOperand" 
	parseInBrowser="true"><div 
	  id="conditionOperand" 
	  style="visibility:visible; display:inline;"></div></taconite-replace>

</c:if>
</taconite-root>
