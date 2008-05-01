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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">

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
			 <span class="ss_bold"><ssf:nlt tag="definition.select_user_names"/></span><br/>
			 <input type="text" name="conditionElementValue" id="conditionElementValue" size="30"/>
			 <a style="padding-left:15px;" class="ss_fineprint" href="javascript: ;"
			   onClick="ss_launchFindUserWindow('conditionElementValue');return false;">
			     <ssf:nlt tag="definition.find_user"/></a>
		   </c:if>
		</c:if>
		</div></taconite-replace>

</c:if>
</taconite-root>
