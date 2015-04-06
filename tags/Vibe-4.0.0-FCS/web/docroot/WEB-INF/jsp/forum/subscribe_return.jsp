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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style ss_popup teamingDlgBox" style="width: 350px" align="left">

<form method="post" id="${ss_namespace}subscription_form${ssBinder.id}">

<div class="popupContent">
<div class="teamingDlgBoxHeader"><ssf:nlt tag="toolbar.menu.subscriptionToFolder"/></div>
	<div class="margintop3 gray3" style="font-size: 12px !important; margin-left: 10px; padding-right: 10px;"><ssf:nlt tag="subscribe.select.type"/>&nbsp;&nbsp;<ssf:showHelp guideName="user" pageId="informed_notifications" /></div>

<c:set var="styles" value="${ssSubscription.styles}"/>
<c:if test="${!empty styles}">
<jsp:useBean id="styles" type="java.util.Map" />
<%
	java.util.Map currentStyles = new java.util.HashMap();
	for (int i=1; i<6; ++i) {
		String [] types = (String[])styles.get(Integer.valueOf(i));
		if (types == null) continue;
		if (types.length==0) {
			currentStyles.put(String.valueOf(i), "x");
		} else {
			for (int j=0; j<types.length; ++j) {
				currentStyles.put(i + types[j], "x");
			}
		}
	}
	request.setAttribute("currentStyles", currentStyles);
%>
</c:if>
<div class="ss_indent_medium  ss_valignTop" style="padding: 10px;">
<c:set var="nothing" value="${true}"/>

<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="1${email.key}"/>
	<c:if test="${!empty currentStyles[styleName]}"> 
		<c:set var="nothing" value="${false}"/>
	</c:if>
</c:forEach>

<span class="ss_labelAbove"><ssf:nlt tag="subscribe.digest"/>
<c:if test="${!ssScheduleInfo.enabled}">
<div>(<ssf:nlt tag="subscribe.select.disabled"/></div>
<div><ssf:nlt tag="administration.notify.nodefault.schedule"/></div>
)</c:if>
</span> 
  <select multiple="multiple" name="_subscribe1">
		<option value="" <c:if test="${nothing == 'true'}"> selected = "selected" </c:if>><ssf:nlt tag="definition.select_item_select"/></option>
	<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="1${email.key}"/>
		<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
		>${email.value.address}</option>
	</c:forEach>
   </select>

<c:set var="nothing" value="${true}"/>

<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="2${email.key}"/>
	<c:if test="${!empty currentStyles[styleName]}"> 
		<c:set var="nothing" value="${false}"/>
	</c:if>
</c:forEach>

	<div class="margintop3">
	  <span class="ss_labelAbove"><ssf:nlt tag="subscribe.message"/></span> 
	  <select multiple="multiple" name="_subscribe2">
			<option value="" <c:if test="${nothing == 'true'}"> selected = "selected" </c:if>><ssf:nlt tag="definition.select_item_select"/></option>
		<c:forEach var="email" items="${ssUser.emailAddresses}">
		<c:set var="styleName" value="2${email.key}"/>
			<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
			>${email.value.address}</option>
		</c:forEach>
	   </select>
	</div>   
<c:set var="nothing" value="${true}"/>

<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="3${email.key}"/>
	<c:if test="${!empty currentStyles[styleName]}"> 
		<c:set var="nothing" value="${false}"/>
	</c:if>
</c:forEach>

   	<div class="margintop3">
		<span class="ss_labelAbove"><ssf:nlt tag="subscribe.noattachments"/></span>
		<select multiple="multiple" name="_subscribe3">
			<option value="" <c:if test="${nothing == 'true'}"> selected = "selected" </c:if>><ssf:nlt tag="definition.select_item_select"/></option>
		<c:forEach var="email" items="${ssUser.emailAddresses}">
		<c:set var="styleName" value="3${email.key}"/>
			<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
			>${email.value.address}</option>
		</c:forEach>
	   </select>
		</div>  
<c:set var="nothing" value="${true}"/>

<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="5${email.key}"/>
	<c:if test="${!empty currentStyles[styleName]}"> 
		<c:set var="nothing" value="${false}"/>
	</c:if>
</c:forEach>
   
   	<div class="margintop3">
		<span class="ss_labelAbove"><ssf:nlt tag="subscribe.text"/></span>
		<select multiple="multiple" name="_subscribe5">
			<option value="" <c:if test="${nothing == 'true'}"> selected = "selected" </c:if>><ssf:nlt tag="definition.select_item_select"/></option>
		<c:forEach var="email" items="${ssUser.emailAddresses}">
		<c:set var="styleName" value="5${email.key}"/>
			<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
			>${email.value.address}</option>
		</c:forEach>
	   </select>
	</div>
	 
	<table class="margintop3" border="0" cellspacing="0" cellpadding="0">
		<tr>
			<td valign="top">
				<input type="checkbox" name="disable" id="notifyType_${ssSubscription.id.entityId}_4"
				<c:if test="${!empty currentStyles['4']}"> checked="checked"</c:if> />
			</td>
			<td>
				<label for="notifyType_${ssSubscription.id.entityId}_4"><ssf:nlt tag="subscribe.disable"/></label> <ssf:showHelp guideName="user" pageId="informed_notifications" sectionId="informed_notifications_override" />
			</td>
		</tr>
	</table>
  
	<div class="margintop3" style="text-align:right;">
    	<input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"
    		onClick="ss_post('<ssf:url adapter="true" action="__ajax_request" actionUrl="true" portletName="ss_forum" binderId="${ssBinder.id}" >
    		<ssf:param name="namespace" value="${ss_namespace}"/>
    		<ssf:param name="operation" value="subscribe"/>
     		<ssf:param name="okBtn" value="1"/>
    		</ssf:url>', '${ss_namespace}subscription_form${ssBinder.id}');ss_cancelPopupDiv('ss_subscription_menu');return false;">
  
  		<input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  			onClick="ss_cancelPopupDiv('ss_subscription_menu');return false;">
  </div>
  
</div>
</div>
</form>
</div>

