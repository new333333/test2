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
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<div class="ss_style" align="left">
<form method="post" id="${ss_namespace}subscription_form${ssBinder.id}">
<span class="ss_largerprint ss_bold"><ssf:nlt tag="subscribe.select.type"/></span><ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_notifications_explained"/>

<br/>
<br/>
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
<div class="ss_indent_medium">
<c:set var="nothing" value="${true}"/>

<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="1${email.key}"/>
	<c:if test="${!empty currentStyles[styleName]}"> 
		<c:set var="nothing" value="${false}"/>
	</c:if>
</c:forEach>

   <span class="ss_labelAbove"><ssf:nlt tag="subscribe.digest"/>
<c:if test="${!ssScheduleInfo.enabled}">
<br/>(<ssf:nlt tag="subscribe.select.disabled"/>
<c:if test="${!empty ssBinder && !empty ssBinder.owner}">
<br/><ssf:nlt tag="subscribe.contact.admin">
<ssf:param name="value" value="${ssBinder.owner.emailAddress}"/>
<ssf:param name="value" value="${ssBinder.owner.title}"/>
</ssf:nlt>
</c:if>
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

   <span class="ss_labelAbove"><ssf:nlt tag="subscribe.message"/></span> 
  <select multiple="multiple" name="_subscribe2">
		<option value="" <c:if test="${nothing == 'true'}"> selected = "selected" </c:if>><ssf:nlt tag="definition.select_item_select"/></option>
	<c:forEach var="email" items="${ssUser.emailAddresses}">
	<c:set var="styleName" value="2${email.key}"/>
		<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
		>${email.value.address}</option>
	</c:forEach>
   </select>
   
<c:set var="nothing" value="${true}"/>

<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="3${email.key}"/>
	<c:if test="${!empty currentStyles[styleName]}"> 
		<c:set var="nothing" value="${false}"/>
	</c:if>
</c:forEach>

	<span class="ss_labelAbove"><ssf:nlt tag="subscribe.noattachments"/></span>
    <select multiple="multiple" name="_subscribe3">
		<option value="" <c:if test="${nothing == 'true'}"> selected = "selected" </c:if>><ssf:nlt tag="definition.select_item_select"/></option>
	<c:forEach var="email" items="${ssUser.emailAddresses}">
	<c:set var="styleName" value="3${email.key}"/>
		<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
		>${email.value.address}</option>
	</c:forEach>
   </select>
  
<c:set var="nothing" value="${true}"/>

<c:forEach var="email" items="${ssUser.emailAddresses}"> 	
	<c:set var="styleName" value="5${email.key}"/>
	<c:if test="${!empty currentStyles[styleName]}"> 
		<c:set var="nothing" value="${false}"/>
	</c:if>
</c:forEach>
   
	<span class="ss_labelAbove"><ssf:nlt tag="subscribe.text"/></span>
    <select multiple="multiple" name="_subscribe5">
		<option value="" <c:if test="${nothing == 'true'}"> selected = "selected" </c:if>><ssf:nlt tag="definition.select_item_select"/></option>
	<c:forEach var="email" items="${ssUser.emailAddresses}">
	<c:set var="styleName" value="5${email.key}"/>
		<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
		>${email.value.address}</option>
	</c:forEach>
   </select>
 
<br/>
  <input type="checkbox" name="disable" id="notifyType_${ssSubscription.id.entityId}_4"
  <c:if test="${!empty currentStyles['4']}"> checked="checked"</c:if>
  /><label for="notifyType_${ssSubscription.id.entityId}_4"><ssf:nlt tag="subscribe.disable"/></label> <ssf:inlineHelp tag="ihelp.email.disable_admin_notify"/><br/>
  
  <br/>
    <input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"
    onClick="ss_post('<ssf:url adapter="true" action="__ajax_request" actionUrl="true" portletName="ss_forum" binderId="${ssBinder.id}" >
    <ssf:param name="namespace" value="${ss_namespace}"/>
    <ssf:param name="operation" value="subscribe"/>
     <ssf:param name="okBtn" value="1"/>
    </ssf:url>', '${ss_namespace}subscription_form${ssBinder.id}');ss_cancelPopupDiv('ss_subscription_menu');return false;">
 &nbsp;&nbsp;&nbsp;
  
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('ss_subscription_menu');return false;">
  
</div>
</form>
</div>

