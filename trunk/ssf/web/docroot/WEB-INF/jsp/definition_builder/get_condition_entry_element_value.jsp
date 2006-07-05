<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml" %>
<%@ page import="java.util.Map" %>
<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<jsp:useBean id="ssEntryDefinitionElementData" type="java.util.Map" scope="request" />

<taconite-root>
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message">
		  <script type="text/javascript">
		    if (self.ss_notLoggedIn) self.ss_notLoggedIn();
		  </script>
		</div
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_status_message" parseInBrowser="true">
		<div id="ss_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>

	<taconite-replace contextNodeID="conditionOperand" 
	parseInBrowser="true"><div 
	   id="conditionOperand" >
       <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'event' || 
                     ssEntryDefinitionElementData[conditionElementName].type == 'selectbox' || 
                     ssEntryDefinitionElementData[conditionElementName].type == 'radio' || 
       				 ssEntryDefinitionElementData[conditionElementName].type == 'date'  || 
       				 ssEntryDefinitionElementData[conditionElementName].type == 'user_list'}">
		   
		   <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'event'}">
		     <c:if test="${conditionElementOperation == 'beforeStart' || 
		                   conditionElementOperation == 'afterStart' || 
		                   conditionElementOperation == 'beforeEnd' || 
		                   conditionElementOperation == 'afterEnd'}">
		       <table>
		        <tbody>
		         <tr>
		           <td valign="top">
		             <input type="text" size="3" name="operationDuration"/>
		           </td>
		           <td valign="top">
		             <input type="radio" name="operationDurationType" value="minutes" /><ssf:nlt tag="minutes" text="minutes"/><br/>
		             <input type="radio" name="operationDurationType" value="hours" /><ssf:nlt tag="hours" text="hours"/><br/>
		             <input type="radio" name="operationDurationType" value="days" checked="checked" /><ssf:nlt tag="days" text="days"/>
		           </td>
		         </tr>
		        </tbody>
		       </table>
		     </c:if>
		   </c:if>
		   
		   <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'date'}">
		     <c:if test="${conditionElementOperation == 'beforeDate' || 
		                   conditionElementOperation == 'afterDate'}">
		       <table>
		        <tbody>
		         <tr>
		           <td valign="top">
		             <input type="text" size="3" name="operationDuration"/>
		           </td>
		           <td valign="top">
		             <input type="radio" name="operationDurationType" value="minutes" /><ssf:nlt tag="minutes" text="minutes"/><br/>
		             <input type="radio" name="operationDurationType" value="hours" /><ssf:nlt tag="hours" text="hours"/><br/>
		             <input type="radio" name="operationDurationType" value="days" checked="checked" /><ssf:nlt tag="days" text="days"/>
		           </td>
		         </tr>
		        </tbody>
		       </table>
		     </c:if>
		   </c:if>
		   
		   <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'selectbox'}">
	   		 <span class="ss_bold"><ssf:nlt tag="definition.selectEntryValue"/></span><br/>
		     <select
		     name="conditionElementValue">
		     <option value="" selected="selected"><ssf:nlt 
		       tag="condition.selectValue" text="--select a value--"/></option>
		       <c:forEach var="elementValue" items="${ssEntryDefinitionElementData[conditionElementName].values}">
		         <option value="<c:out value="${elementValue.key}"/>"><c:out value="${elementValue.value}"/></option>
		       </c:forEach>
		     </select>
		   </c:if>
		   
		   <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'radio'}">
	   		<span class="ss_bold"><ssf:nlt tag="definition.selectEntryValue"/></span><br/>
		     <select
		     name="conditionElementValue">
		     <option value="" selected="selected"><ssf:nlt 
		       tag="condition.selectValue" text="--select a value--"/></option>
		       <c:forEach var="elementValue" items="${ssEntryDefinitionElementData[conditionElementName].values}">
		         <option value="<c:out value="${elementValue.key}"/>"><c:out value="${elementValue.value}"/></option>
		       </c:forEach>
		     </select>
		   </c:if>
		   <c:if test="${ssEntryDefinitionElementData[conditionElementName].type == 'user_list'}">
		       -- user and group selection goes here --<br/>
		   </c:if>
		</c:if>
		</div></taconite-replace>

<%
	}
%>	
</taconite-root>
