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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="helpGuideName" value="admin" scope="request" />
<c:set var="helpPageId" value="nodes" scope="request" />
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

<ssf:form titleTag="administration.configure_search_nodes">
<form class="ss_style ss_form" name="${renderResponse.namespace}fm" id="${renderResponse.namespace}fm" method="post" 
	action="<ssf:url action="configure_search_nodes" actionUrl="true"/>">
<div class="ss_buttonBarRight">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="ss_cancelButtonCloseWindow();return false;"/>
</div>

<c:if test="${!empty ssSearchNodes}">
  <c:forEach var="node" items="${ssSearchNodes}">
    <c:set var="disabled" value=""/>
    <c:if test="$!empty node.deferredUpdateLogApplyingIpv4Address}">
      <c:set var="disabled" value=" disabled"/>
    </c:if>
	<fieldset class="ss_fieldset">
	  <legend class="ss_legend ss_labelLeft">${node.title} (${node.nodeName})</legend>	
	  
	  <c:if test="${!empty node.deferredUpdateLogApplyingIpv4Address}">
	    <div>
	      <span class="ss_errorLabel"><ssf:nlt tag="index.currentlyApplyingUpdateLogs1"/></span>
	      <br/>
	      <span class="ss_errorLabel"><ssf:nlt tag="index.currentlyApplyingUpdateLogs2"><ssf:param
            name="value" value="${node.deferredUpdateLogApplyingIpv4Address}"/></ssf:nlt></span>
	    </div>
	  </c:if>
	  
	<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
	<tr><td valign="top">
	<c:set var="properties" value="${node.displayProperties}"/>
	<c:if test="${!empty properties}">
		<c:forEach var="property" items="${properties}">
			<c:out value="${property.key}"/>: <c:out value="${property.value}"/><br/>
		</c:forEach>
	</c:if>
	<table class="ss_style" border ="0" cellspacing="0" cellpadding="0" width="100%">
	<tr><td valign="top">
	<hr shade=noshade size=1/>
	<span class="ss_labelLeft"><ssf:nlt tag="administration.search.node.usermodeaccess"/></span><br>
		<input type="radio" name="userModeAccess${node.nodeName}" value="readwrite" <c:if test="${node.userModeAccess == 'readwrite'}">checked</c:if>><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.usermodeaccess.readwrite" /></span><br>
		<input type="radio" name="userModeAccess${node.nodeName}" value="writeonly" <c:if test="${node.userModeAccess == 'writeonly'}">checked</c:if>><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.usermodeaccess.writeonly" /></span><br>
		<input type="radio" name="userModeAccess${node.nodeName}" value="noaccess" <c:if test="${node.userModeAccess == 'noaccess'}">checked</c:if>><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.usermodeaccess.noaccess" /></span><br>
	</td></tr>
	</table>
	<table class="ss_style" border="0" cellspacing="3" cellpadding="3" width="100%">
	<tr><td valign="top">
	<hr shade=noshade size=1/>
		<input type="checkbox" name="enableDeferredUpdateLog${node.nodeName}" <c:if test="${node.enableDeferredUpdateLog}">checked</c:if> ${disabled} /> <span class="ss_labelLeft"><ssf:nlt tag="administration.search.node.deferredupdatelog.enable"/></span>
	<br/>
	</td></tr>
	</table>
	<table class="ss_style" border="0" cellspacing="3" cellpadding="3" width="100%">
	<tr><td valign="top">
	<hr shade=noshade size=1/>
	<c:if test="${!node.noDeferredUpdateLogRecords}">
		<span class="ss_labelLeft ss_errorLabel"><ssf:nlt tag="administration.search.node.nodeferredupdatelogrecords.false" /></span>
		<c:if test="${empty node.deferredUpdateLogApplyingIpv4Address}">
		   <br/><input type="radio" name="synchronize${node.nodeName}" value="apply"><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.deferredupdatelogrecords.apply"/></span>
		</c:if>
		<br/><input type="radio" name="synchronize${node.nodeName}" value="discard"><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.deferredupdatelogrecords.discard"/></span>
		<br/><input type="radio" name="synchronize${node.nodeName}" value="donothing" checked><span class="ss_labelRight ss_normal"><ssf:nlt tag="administration.search.node.deferredupdatelogrecords.donothing"/></span>
	</c:if>
	<c:if test="${node.noDeferredUpdateLogRecords}">
		<span class="ss_labelLeft"><ssf:nlt tag="administration.search.node.nodeferredupdatelogrecords.true" /></span>
	</c:if>
	<br/>
	</td></tr>
	</table>
	</td></tr>
	</table>
	</fieldset>
  </c:forEach>
</c:if>

<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		  onClick="ss_cancelButtonCloseWindow();return false;"/>
</div>
	<sec:csrfInput />
</form>
</ssf:form>

</div>
</div>
</body>
</html>
