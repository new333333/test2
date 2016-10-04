
<%
	/**
	 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
	 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
	 * 
	 * Attribution Information:
	 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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
<%@ include file="/WEB-INF/jsp/common/common.jsp"%>
<c:set var="presenceAltText" value="${ss_presence_text}" />
<c:if test="${ss_presence_show_hint}">
  <c:set var="presenceAltText" >${ss_presence_user.title}</c:set>
</c:if>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet"%>
<%@ taglib prefix="portletadapter"
	uri="http://www.sitescape.com/tags-portletadapter"%>
<script type="text/javascript">
var noProfileErrorText = "<ssf:nlt tag="errorcode.noProfileQuickView"/>";
</script>


<portletadapter:defineObjects1 />
<ssf:ifadapter>
	<portletadapter:defineObjects2 />
</ssf:ifadapter>
<ssf:ifnotadapter>
	<portlet:defineObjects />
</ssf:ifnotadapter>

<table cellspacing="0" cellpadding="0" style="display: inline-table">
<tr>
<c:if test="${empty ss_presence_user}">
  <td>
	<a href="javascript: ;"
		onClick="ss_popupPresenceMenu(this, '', '', '-1', '', '', '', '', '', '', '${ss_presence_component_id}', '${ss_presence_zonBridge}', '', '');return false;"><img
		border="0" align="absmiddle"
		style="height:14px !important; width:15px !important; line-height:14px !important;" 
		src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
		alt="<c:out value="${presenceAltText}"/>" /></a>
  </td>
</c:if>

<c:if test="${!empty ss_presence_user}">
	<c:set var="presence_user_title" value="" />
	<c:if test="${!empty ss_presence_user.title}">
		<c:set var="presence_user_title">
			<ssf:userTitle user="${ss_presence_user}" />
		</c:set>
	</c:if>
	<c:set var="presence_user_zonName" value="" />
	<c:if test="${!empty ss_presence_user.zonName}">
		<c:set var="presence_user_zonName" value="${ss_presence_user.zonName}" />
	</c:if>
	<c:set var="presence_user_emailAddress" value="" />
	<c:if test="${!empty ss_presence_user.emailAddress}">
		<c:set var="presence_user_emailAddress"
			value="${ss_presence_user.emailAddress}" />
	</c:if>
	<c:set var="presence_user_skypeId" value="" />
	<c:if test="${!empty ss_presence_user.skypeId}">
		<c:set var="presence_user_skypeId" value="${ss_presence_user.skypeId}" />
	</c:if>
	<jsp:useBean id="presence_user_skypeId" type="java.lang.String" />
	<jsp:useBean id="presence_user_title" type="java.lang.String" />
	<jsp:useBean id="presence_user_zonName" type="java.lang.String" />
	<jsp:useBean id="presence_user_emailAddress" type="java.lang.String" />

	<%
		String presenceUserTitle = presence_user_title;
			presenceUserTitle = presenceUserTitle.replaceAll("\\\\", "")
					.replaceAll("'", "\\\\'");
			String presenceUserZonName = presence_user_zonName;
			presenceUserZonName = presenceUserZonName
					.replaceAll("\\\\", "").replaceAll("'", "\\\\'");
			String presenceUserEmailAddress = presence_user_emailAddress;
			presenceUserEmailAddress = presenceUserEmailAddress.replaceAll(
					"\\\\", "").replaceAll("'", "\\\\'");
			String presenceUserSkypeId = presence_user_skypeId;
			presenceUserSkypeId = presenceUserSkypeId
					.replaceAll("\\\\", "").replaceAll("'", "\\\\'");
			String presenceUserEmailAddressName = "";
			String presenceUserEmailAddressHost = "";
			if (!presenceUserEmailAddress.equals("")
					&& presenceUserEmailAddress.indexOf("@") > 0) {
				presenceUserEmailAddressName = presenceUserEmailAddress
						.substring(0, presenceUserEmailAddress.indexOf("@"));
				presenceUserEmailAddressHost = presenceUserEmailAddress
						.substring(presenceUserEmailAddress.indexOf("@") + 1);
			} else {
				presenceUserEmailAddressName = presenceUserEmailAddress;
			}
	%>


	<c:set var="current" value="" />
	<c:if test="${ssUser.zonName == ss_presence_user.zonName}">
		<c:set var="current" value="current" />
	</c:if>
	<td>
	<a href="javascript: ;" title="<c:out value="${presenceAltText}"/>"
		class="ss_presence_dude"
		onClick="ss_launchSimpleProfile( this,'${ss_presence_user.id}','${ss_presence_user.workspaceId}','<ssf:escapeJavaScript>${presence_user_title}</ssf:escapeJavaScript>', noProfileErrorText);return false;"><img
		border="0" align="absmiddle"
		style="height:14px !important; width:15px !important; line-height:14px !important;" 
		src="<html:imagesPath/>pics/<c:out value="${ss_presence_dude}"/>"
		alt="<c:out value="${presenceAltText}"/>" /></a>
	</td>
	<c:if test="${ss_presence_show_title}">
		<ssf:ifadapter>
		  <td>
			<c:if test="${ss_presence_workspace_predeleted}">
				<span id="${ss_presence_user.id}"
					class="ss_presence_title_style ss_muster_users"><ssf:userTitle
					user="${ss_presence_user}" /></span>
			</c:if>
			<c:if test="${!ss_presence_workspace_predeleted}">
				<c:if test="${!empty ss_presence_user.workspaceId}">
					<a
						<c:if test="${!empty ss_presence_target}">target="${ss_presence_target}"</c:if>
						href="<ssf:permalink entity="${ss_presence_user}"/>"
						onClick="ss_launchSimpleProfile( this,'${ss_presence_user.id}','${ss_presence_user.workspaceId}','<ssf:escapeJavaScript>${presence_user_title}</ssf:escapeJavaScript>', noProfileErrorText);return false;"><span
						id="${ss_presence_user.id}"
						class="ss_presence_title_style ss_muster_users"
						title="<c:out value="${presenceAltText}"/>"
				    ><ssf:userTitle user="${ss_presence_user}" /></span></a>
				</c:if>
				<c:if test="${empty ss_presence_user.workspaceId}">
					<c:if test="${!empty ss_presence_user.parentBinder.id && ss_canAccessProfilesBinder}">
						<a
							<c:if test="${!empty ss_presence_target}">target="${ss_presence_target}"</c:if>
							href="<ssf:url     
						      binderId="${ss_presence_user.parentBinder.id}" 
						      action="view_profile_entry" 
						      entryId="${ss_presence_user.id}"><ssf:param 
					  	      name="newTab" value="1"/><ssf:param name="entryViewStyle" value="full"/></ssf:url>"
					  	    title="<c:out value="${presenceAltText}"/>"
					  	>
						<span id="${ss_presence_user.id}"
							class="ss_presence_title_style ss_muster_users"><ssf:userTitle
							user="${ss_presence_user}" /></span> </a>
					</c:if>
					<c:if test="${empty ss_presence_user.parentBinder.id || !ss_canAccessProfilesBinder}">
						<span id="${ss_presence_user.id}"
							class="ss_presence_title_style ss_muster_users"><ssf:userTitle
							user="${ss_presence_user}" /></span>
					</c:if>
				</c:if>
			</c:if>
		  </td>
		</ssf:ifadapter>
		<ssf:ifnotadapter>
		  <td>
			<c:if test="${!empty ss_presence_user.workspaceId}">
				<a
					href="<ssf:url windowState="maximized"><ssf:param 
					  	name="action" value="view_ws_listing"/><ssf:param 
					  	name="binderId" value="${ss_presence_user.workspaceId}"/></ssf:url>"
					onClick="ss_launchSimpleProfile( this,'${ss_presence_user.id}','${ss_presence_user.workspaceId}','<ssf:escapeJavaScript>${presence_user_title}</ssf:escapeJavaScript>', noProfileErrorText);return false;"><span
					id="${ss_presence_user.id}"
					class="ss_presence_title_style ss_muster_users"
					title="<c:out value="${presenceAltText}"/>"
				><ssf:userTitle user="${ss_presence_user}" /></span></a>
			</c:if>
			<c:if test="${empty ss_presence_user.workspaceId}">
				<span id="${ss_presence_user.id}"
					class="ss_presence_title_style ss_muster_users"><ssf:userTitle
					user="${ss_presence_user}" /></span>
			</c:if>
		  </td>
		</ssf:ifnotadapter>

	</c:if>

</c:if>
</tr>
</table>

