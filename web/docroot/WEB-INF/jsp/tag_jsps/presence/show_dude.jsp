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
<%@ page import="com.sitescape.util.BrowserSniffer" %>
<%
boolean isIE6 = BrowserSniffer.is_ie_6(request);
%>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<c:if test="${empty ss_presence_user && !ss_presence_show_options_inline}">
<a href="javascript: ;"
 onClick="ss_popupPresenceMenu(this, '', '', '-1', '', '', '', '', '', '${ss_presence_component_id}', '${ss_presence_zonBridge}');return false;"
><img border="0" 
<% if (isIE6) { %> style="padding-top:1px;" <% } %>
src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>"/></a>
</c:if>
<c:if test="${empty ss_presence_user && ss_presence_show_options_inline}">
</c:if>

<c:if test="${!empty ss_presence_user}">
<c:set var="presence_user_title" value=""/>
<c:if test="${!empty ss_presence_user.title}">
<c:set var="presence_user_title" value="${ss_presence_user.title}"/>
</c:if>
<c:set var="presence_user_zonName" value=""/>
<c:if test="${!empty ss_presence_user.zonName}">
<c:set var="presence_user_zonName" value="${ss_presence_user.zonName}"/>
</c:if>
<c:set var="presence_user_emailAddress" value=""/>
<c:if test="${!empty ss_presence_user.emailAddress}">
<c:set var="presence_user_emailAddress" value="${ss_presence_user.emailAddress}"/>
</c:if>
<jsp:useBean id="presence_user_title" type="java.lang.String" />
<jsp:useBean id="presence_user_zonName" type="java.lang.String" />
<jsp:useBean id="presence_user_emailAddress" type="java.lang.String" />

<%
	String presenceUserTitle = presence_user_title;
	presenceUserTitle = presenceUserTitle.replaceAll("'", "\\\\'");
	String presenceUserZonName = presence_user_zonName;
	presenceUserZonName.replaceAll("'", "\\\\'");
	String presenceUserEmailAddress = presence_user_emailAddress;
	presenceUserEmailAddress.replaceAll("'", "\\\\'");
%>
<c:set var="current" value=""/>
<c:if test="${ssUser.zonName == ss_presence_user.zonName}">
<c:set var="current" value="current"/>
</c:if>
<c:if test="${!ss_presence_show_options_inline}">
<a href="javascript: ;"
 style="padding-right:2px; text-decoration:none;"
 onClick="ss_popupPresenceMenu(this, 
    '${ss_presence_user.id}', 
    '<%= presenceUserTitle %>', 
    '${ss_presence_userStatus}', 
    '<%= presenceUserZonName %>', 
    '<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${ss_presence_sweep_time}" type="both" timeStyle="short" dateStyle="medium" />', 
    '<%= presenceUserEmailAddress %>', 
    '${ss_presence_vcard}', 
    '${current}', '${ss_presence_component_id}', '${ss_presence_zonBridge}');return false;"
><img border="0" 
<% if (isIE6) { %> style="padding-top:1px;" <% } %>
src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
 alt="<c:out value="${ss_presence_text}"/>"/></a>
  <c:if test="${ss_presence_show_title}">
	<ssf:ifadapter>
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ss_presence_user.parentBinder.id}"
	    entryId="${ss_presence_user.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"
		onClick="self.parent.location.href = this.href;return false;"
	  ><span id="${ss_presence_user.id}" 
	    class="${ss_presence_title_style} ss_muster_users">${ss_presence_user.title}</span></a>
	</ssf:ifadapter>
	<ssf:ifnotadapter>
	  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
	  	name="action" value="view_ws_listing"/><portlet:param 
	  	name="binderId" value="${ss_presence_user.parentBinder.id}"/><portlet:param 
	  	name="entryId" value="${ss_presence_user.id}"/><portlet:param 
	  	name="newTab" value="1"/></portlet:renderURL>"
	  ><span id="${ss_presence_user.id}"  
	    class="${ss_presence_title_style} ss_muster_users">${ss_presence_user.title}</span></a>
	</ssf:ifnotadapter>
 
  </c:if>
</c:if>
<c:if test="${ss_presence_show_options_inline}">
<script type="text/javascript">
function ss_showPresenceInline_${ss_presence_div_id}() {
	ss_presenceMenu('${ss_presence_div_id}', this, 
	    '${ss_presence_user.id}', 
	    '<%= presenceUserTitle %>', 
	    '${ss_presence_userStatus}', 
	    '<%= presenceUserZonName %>', 
	    '<fmt:formatDate timeZone="${ssUser.timeZone.ID}" value="${ss_presence_sweep_time}" type="both" timeStyle="short" dateStyle="medium" />', 
	    '<%= presenceUserEmailAddress %>', 
	    '${ss_presence_vcard}', 
	    '${current}', 
	    '${ss_presence_component_id}', 
	    '${ss_presence_zonBridge}');
}
ss_createOnLoadObj("ss_showPresenceInline_${ss_presence_div_id}", ss_showPresenceInline_${ss_presence_div_id});

</script>
</c:if>

</c:if>
