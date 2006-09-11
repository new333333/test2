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
       				 ssEntryDefinitionElementData[conditionElementName].type == 'user_list'}">
	   <span class="ss_bold"><ssf:nlt tag="definition.selectEntryOperation"/></span><br/>
	   <select
	   name="conditionElementOperation" 
	   onChange="getConditionSelectbox(this, 'get_condition_entry_element_values')">
	     <option value="" selected="selected"><ssf:nlt 
	       tag="filter.selectElementOperation" text="--select an operation--"/></option>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'event'}">
		       <option value="started"><ssf:nlt tag="definition.operation_started" text="Start date passed"/></option>
		       <option value="ended"><ssf:nlt tag="definition.operation_ended" text="End date passed"/></option>
		       <option value="beforeStart"><ssf:nlt tag="definition.operation_beforeStart" text="Before the start date"/></option>
		       <option value="afterStart"><ssf:nlt tag="definition.operation_afterStart" text="After the start date"/></option>
		       <option value="beforeEnd"><ssf:nlt tag="definition.operation_beforeEnd" text="Before the end date"/></option>
		       <option value="afterEnd"><ssf:nlt tag="definition.operation_afterEnd" text="After the end date"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'date'}">
		       <option value="datePassed"><ssf:nlt tag="definition.operation_datePassed" text="Date passed"/></option>
		       <option value="beforeDate"><ssf:nlt tag="definition.operation_beforeDate" text="Before the date"/></option>
		       <option value="afterDate"><ssf:nlt tag="definition.operation_afterDate" text="After the date"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'checkbox'}">
		       <option value="checked"><ssf:nlt tag="definition.operation_checked" text="Checked"/></option>
		       <option value="checkedNot"><ssf:nlt tag="definition.operation_checkedNot" text="Not checked"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'selectbox'}">
		         <option value="equals"><ssf:nlt tag="definition.operation_equals" text="Equals"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'radio'}">
		         <option value="equals"><ssf:nlt tag="definition.operation_equals" text="Equals"/></option>
		     </c:if>
		     <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'user_list'}">
		         <option value="equals"><ssf:nlt tag="definition.operation_equals" text="Equals"/></option>
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
