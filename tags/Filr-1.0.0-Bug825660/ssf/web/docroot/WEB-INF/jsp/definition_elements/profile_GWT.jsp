<%
/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
<% //Business card view %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/initializeGWT.jsp"     %>
<%
	//Get the form item being displayed
	Element item = (Element) request.getAttribute("item");
%>

<ssf:ifLoggedIn>
  <c:if test="${ssUser == ssDefinitionEntry}">
	<c:if test="${ss_quotasEnabled}">
	    <c:set var="ssDiskQuota" ><fmt:formatNumber value="${ss_diskQuotaUserMaximum/1048576}" maxFractionDigits="0"/></c:set>
	    <c:set var="ssDiskSpaceUsed" ><fmt:formatNumber value="${ssUser.diskSpaceUsed/1048576}" maxFractionDigits="2"/></c:set>
	</c:if>
	<c:set var="ss_quotaMessage" value="" />
	<c:if test="${ss_diskQuotaHighWaterMarkExceeded && !ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
	<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.nearLimit"><ssf:param name="value" useBody="true"
		    ><fmt:formatNumber value="${(ss_diskQuotaUserMaximum - ssUser.diskSpaceUsed)/1048576}" 
		    maxFractionDigits="2"/></ssf:param></ssf:nlt></c:set>
	</c:if>
	<c:if test="${ss_diskQuotaExceeded && !ss_isBinderMirroredFolder}">
	<c:set var="ss_quotaMessage" ><ssf:nlt tag="quota.diskQuotaExceeded"/></c:set>
	</c:if> 
 </c:if>
</ssf:ifLoggedIn>

<c:set var="gwtPage" value="profile" scope="request"/>	
<%@ include file="/WEB-INF/jsp/common/GwtRequestInfo.jsp" %>
	
<% if (GwtUIHelper.isGwtUIActive(request)) { %>
	<script type="text/javascript">
		/*
		 * onload event handler.
		 *
		 * Calls into the GWT code to notify it that a new context has
		 * been loaded into the content frame.
		 */
		function notifyGwtUI_ProfileLoaded() {
			if ((typeof window.top.ss_gwtRelayoutPage != "undefined") &&
					((window.name == "gwtContentIframe") || (window.name == "ss_showentryframe"))) {
				window.top.ss_gwtRelayoutPage();
			}
		}

		ss_createOnLoadObj("notifyGwtUI_ProfileLoaded", notifyGwtUI_ProfileLoaded);
	</script>
<% } %>

<script type="text/javascript" src="<html:rootPath />js/gwt/gwtteaming/gwtteaming.nocache.js"></script>
<div id="gwtProfileDiv">
	<div id="profilePhoto" style="display:none;">
	  <div class="ss_thumbnail_standalone ss_thumbnail_profile">
		<div>
			<a onclick="ss_showThisImage(this);return false;" href="javascript:;">
				<c:if test="${empty ssDefinitionEntry.customAttributes['picture']}">
					<img src="<html:imagesPath/>pics/UserPhoto.png" alt="${ssDefinitionEntry.title}" />
	     		</c:if>
				<c:if test="${!empty ssDefinitionEntry.customAttributes['picture']}">
				  <c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
				  <c:set var="pictureCount" value="0"/>
				  <c:forEach var="selection" items="${selections}">
				    <c:if test="${pictureCount == 0}">
					   <img  src="<ssf:fileUrl webPath="readFile" file="${selection}"/>" alt="${ssDefinitionEntry.title}" />
				    </c:if>
				    <c:set var="pictureCount" value="${pictureCount + 1}"/>
				  </c:forEach>
				</c:if>
			</a>
		</div>
	  </div>
	</div>
</div>


<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  		configElement="<%= item %>" 
  		configJspStyle="${ssConfigJspStyle}" 
  		entry="${ssDefinitionEntry}" />

