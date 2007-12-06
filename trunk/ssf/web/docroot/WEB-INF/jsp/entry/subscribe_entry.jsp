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
<c:set var="styles" value="${ssSubscription.styles}"/>
<c:if test="${!empty styles}">
<jsp:useBean id="styles" type="java.util.Map" />
<%
	java.util.Map currentStyles = new java.util.HashMap();
	for (int i=2; i<6; ++i) {
		if (i == 4) continue;
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
    <c:if test="${!empty currentStyles}"> <input type="hidden" name="_subscribe_element_present" value="1"/> </c:if>
	
  <span class="ss_labelAbove"><ssf:nlt tag="subscribe.message"/><ssf:inlineHelp tag="ihelp.email.individual_notify_entry"/></span> 
  <select multiple="multiple" name="_subscribe2" >
		<option value="" ><ssf:nlt tag="__none"/></option>
	<c:forEach var="email" items="${ssUser.emailAddresses}">
	<c:set var="styleName" value="2${email.key}"/>
		<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
		>${email.value.address}</option>
	</c:forEach>
   </select>
	<span class="ss_labelAbove"><ssf:nlt tag="subscribe.noattachments"/></span>
     <select multiple="multiple" name="_subscribe3">
		<option value="" ><ssf:nlt tag="__none"/></option>
	<c:forEach var="email" items="${ssUser.emailAddresses}">
	<c:set var="styleName" value="3${email.key}"/>
		<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
		>${email.value.address}</option>
	</c:forEach>
   </select>
   
 	<span class="ss_labelAbove"><ssf:nlt tag="subscribe.text"/></span>
   <select multiple="multiple"  name="_subscribe5">
		<option value="" ><ssf:nlt tag="__none"/></option>
	<c:forEach var="email" items="${ssUser.emailAddresses}">
	<c:set var="styleName" value="5${email.key}"/>
		<option value="${email.key}" <c:if test="${!empty currentStyles[styleName]}"> selected="selected" </c:if>
		>${email.value.address}</option>
	</c:forEach>
   </select>