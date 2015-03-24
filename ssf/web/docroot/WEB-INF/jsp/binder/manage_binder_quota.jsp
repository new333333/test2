<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="tag" value="quota.manageFolderQuota"/>
<c:if test="${ssBinder.entityType != 'folder'}">
  <c:set var="tag" value="quota.manageWorkspaceQuota"/>
</c:if>
<jsp:useBean id="tag" type="String" />
<c:set var="ss_windowTitle" value='<%= NLT.get(tag) %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<body class="ss_style_body tundra">

<script type="text/javascript">
function ss_treeShowIdConfig${renderResponse.namespace}(id, obj, action) {
	var binderId = id;
	//See if the id is formatted (e.g., "ss_favorites_xxx")
	if (binderId.indexOf("_") >= 0) {
		var binderData = id.substr(13).split("_");
		binderId = binderData[binderData.length - 1];
	}

	//Build a url to go to
	var url = "<ssf:url actionUrl="false" action="manage_binder_quota"><ssf:param 
		name="binderId" value="ssBinderIdPlaceHolder"/></ssf:url>";
	url = ss_replaceSubStr(url, "ssBinderIdPlaceHolder", binderId);
	self.location.href = url;
	return false;
}

function ss_checkIfQuotaValid(s) {
	if (ss_trim(s) == '') return true;   //Blank is ok
	
	var pattern1 = new RegExp("^[0-9]+$");
	if (pattern1.test(ss_trim(s))) {
		if (ss_trim(s).length >= 8) {
			alert("<ssf:escapeJavaScript><ssf:nlt tag="error.numberTooBig"/></ssf:escapeJavaScript>");
			return false;
		}
		return true;
	}
	alert("<ssf:escapeJavaScript><ssf:nlt tag="error.mustBeANumber"/></ssf:escapeJavaScript>");
	return false;
}

</script>

<div class="ss_style ss_portlet">
	<div style="padding:10px;">		
		<c:if test="${!empty ssException}">
		<br>
		  <font color="red">
		    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
		  </font>
		<br/>
		</c:if>
	
<c:set var="ss_tab_quota" value="on"/>
<%@ include file="/WEB-INF/jsp/binder/configure_tabs.jsp" %>

<div style="display:block;" class="wg-tab-content marginbottom3">

<c:if test="${ssBinder.entityType == 'folder'}">
  <span><ssf:nlt tag="access.currentFolder"/></span>
</c:if>
<c:if test="${ssBinder.entityType != 'folder'}">
  <span><ssf:nlt tag="access.currentWorkspace"/></span>
</c:if>
<% //need to check tags for templates %>
<span class="ss_bold ss_largestprint"><ssf:nlt tag="${ssBinder.title}" checkIfTag="true"/></span>

<c:set var="ss_breadcrumbsShowIdRoutine" 
  value="ss_treeShowIdConfig${renderResponse.namespace}" 
  scope="request" />
<jsp:include page="/WEB-INF/jsp/definition_elements/navigation_links.jsp" />

<form class="ss_form" method="post" style="display:inline;" 
	action="<ssf:url action="manage_binder_quota" actionUrl="true"><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>">
	
    <fieldset class="ss_fieldset">
      <legend class="ss_legend"><ssf:nlt tag="quota.setQuota"/></legend>
      <c:set var="type" value="workspace"/>
      <c:if test="${ssBinder.entityType == 'folder'}"><c:set var="type" value="folder"/></c:if>
      
      <div style="padding: 10px;">
        <span class="ss_normalprint"><ssf:nlt tag="quota.binder.diskSpaceUsed.${type}"/></span>
        <span class="ss_normalprint ss_bold">
          <fmt:setLocale value="${ssUser.locale}"/>
		  <fmt:formatNumber type="number" value="${(ss_binderQuota.diskSpaceUsed)/1048576}"
		  maxFractionDigits="2"/>&nbsp;<ssf:nlt tag="file.sizeMB"/>
		</span>
      </div>

      <div style="padding:0px 10px 10px;">
        <span class="ss_normalprint"><ssf:nlt tag="quota.binder.diskSpaceUsedCumulative.${type}"/></span>
        <span class="ss_normalprint ss_bold">
          <fmt:setLocale value="${ssUser.locale}"/>
		  <fmt:formatNumber type="number" value="${(ss_binderQuota.diskSpaceUsedCumulative)/1048576}"
		  maxFractionDigits="2"/>&nbsp;<ssf:nlt tag="file.sizeMB"/>
		</span>
      </div>


	  <div class="margintop2" style="padding:10px;">
        <span><ssf:nlt tag="quota.currentParentQuota"/></span>
        <span class="ss_bold">
          <c:if test="${empty ss_binderMinQuotaParentBinder}"><ssf:nlt tag="None"/></c:if>
          <c:if test="${!empty ss_binderMinQuotaParentBinder}">
          <fmt:setLocale value="${ssUser.locale}"/>
		  <fmt:formatNumber value="${(ss_binderMinQuotaParentBinder + 524287)/1048576}"
		  maxFractionDigits="0"/>&nbsp;<ssf:nlt tag="file.sizeMB"/></c:if>
        </span>
      </div>

      <c:set var="currentQuota" value=""/>
	  <div style="padding:0px 10px 10px;">
        <span><ssf:nlt tag="quota.currentQuota"/></span>
        <span class="ss_bold">
          <c:if test="${empty ss_binderQuota.diskQuota}"><ssf:nlt tag="None"/></c:if>
          <c:if test="${!empty ss_binderQuota.diskQuota}">
		      <c:set var="currentQuota"><fmt:formatNumber pattern="#######" 
		          value="${(ss_binderQuota.diskQuota + 524287)/1048576}"
				  maxFractionDigits="0"/></c:set>
			  <fmt:setLocale value="${ssUser.locale}"/>
		  	  <fmt:formatNumber value="${(ss_binderQuota.diskQuota + 524287)/1048576}"
		  	    maxFractionDigits="0"/>&nbsp;<ssf:nlt tag="file.sizeMB"/>
		  </c:if>
        </span>
      </div>

      <div class="margintop3" style="padding:10px;">
       <c:if test="${ss_binderQuotasAllowBinderOwnerEnabled || ss_binderQuotasAllowManageBinderQuotas}">
        <c:if test="${empty ss_binderQuota.diskQuota}">
          <span class="ss_bold"><ssf:nlt tag="quota.changeQuota"/></span>
          <input type="text" name="quota" value="" 
            style="width:40px; text-align:right;"
            onChange='if (!ss_checkIfQuotaValid(this.value)){this.value="";}'
          >&nbsp;<ssf:nlt tag="file.sizeMB"/>
        </c:if>
        <c:if test="${!empty ss_binderQuota.diskQuota}">
          <span class="ss_bold"><ssf:nlt tag="quota.changeQuota"/></span>
          <input type="text" name="quota" value="${currentQuota}" 
            style="width:40px; text-align:right;"
            onChange='if (!ss_checkIfQuotaValid(this.value)){this.value="";}'
            />&nbsp;<ssf:nlt tag="file.sizeMB"/>
        </c:if>
       </c:if>
       <c:if test="${!ss_binderQuotasAllowBinderOwnerEnabled && !ss_binderQuotasAllowManageBinderQuotas}">
       <span><ssf:nlt tag="quota.changeQuotaNotAllowed"/></span>
       </c:if>
	  </div>
    </fieldset>
	
	<div class="margintop3" style="text-align: right;">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />" >
		<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>"
		  onClick="ss_cancelButtonCloseWindow();return false;">
	</div>

</form>
</div>
</div>
</div>

</body>
</html>
