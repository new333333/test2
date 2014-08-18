<%
/**
 * Copyright (c) 1998-2013 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2013 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2013 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.Utils" %>

<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<%@ page import="org.kablink.teaming.web.util.GwtUIHelper" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_mail") %>' scope="request"/>
<c:set var="helpGuideName" value="admin" scope="request" />
<c:set var="helpPageId" value="emailintegration_configure" scope="request" />
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<ssf:form titleTag="administration.configure_mail">
<br/>

<script type="text/javascript">
var ${renderResponse.namespace}_savedIndex;

/**
 * 
 */
function handleCloseBtn()
{
<% 	if ( GwtUIHelper.isGwtUIActive( request ) ) { %>
		// Tell the Teaming GWT ui to close the administration content panel.
		if ( window.parent.ss_closeAdministrationContentPanel ) {
			window.parent.ss_closeAdministrationContentPanel();
		} else {
			ss_cancelButtonCloseWindow();
		}

		return false;
<% 	}
	else { %>
		ss_cancelButtonCloseWindow();
		return false;
<%	} %>
	
}// end handleCloseBtn()

function ${renderResponse.namespace}_showAliasDiv(index) {
	
	self.document.${renderResponse.namespace}_modifyAliasFm.alias.value=eval('self.document.${renderResponse.namespace}fm.alias' + index + '.value');
	${renderResponse.namespace}_savedIndex=index;
	ss_showPopupDivCentered('${renderResponse.namespace}_modifyAliasDiv');	
}
function ${renderResponse.namespace}_modifyAlias() {
	var param = eval('self.document.${renderResponse.namespace}fm.alias' + ${renderResponse.namespace}_savedIndex);
	param.value = self.document.${renderResponse.namespace}_modifyAliasFm.alias.value;
<c:if test="${mail_posting_use_aliases == 'false'}">
	var param = eval('self.document.${renderResponse.namespace}fm.password' + ${renderResponse.namespace}_savedIndex);
	param.value = self.document.${renderResponse.namespace}_modifyAliasFm.password.value;
</c:if>
	var param = document.getElementById('aliasSpan' + ${renderResponse.namespace}_savedIndex);
	param.innerHTML = self.document.${renderResponse.namespace}_modifyAliasFm.alias.value;
}

function ss_checkIfNumberValid(s) {
	if (ss_trim(s) == '') return true;   //Blank is ok
	
	var pattern1 = new RegExp("^[0-9]+$");
	if (pattern1.test(ss_trim(s))) {
		if (ss_trim(s).length > 8) {
			alert("<ssf:escapeJavaScript><ssf:nlt tag="error.numberTooBig"/></ssf:escapeJavaScript>");
			return false;
		}
		return true;
	}
	alert("<ssf:escapeJavaScript><ssf:nlt tag="error.mustBeAPositiveNumber"/></ssf:escapeJavaScript>");
	return false;
}

</script>

<form class="ss_style ss_form" name="${renderResponse.namespace}fm" id="${renderResponse.namespace}fm" method="post" 
	action="<ssf:url action="configure_posting_job" actionUrl="true"/>">

<div class="gray3"><ssf:nlt tag="notify.description"/></div>

<div class="margintop2 marginbottom3">
	<input type="checkbox" class="ss_style" id="notifyenabled" name="notifyenabled" <c:if test="${ssScheduleInfonotify.enabled}">checked</c:if> />
	<label for="notifyenabled"><span class="ss_labelRight"><ssf:nlt tag="notify.schedule.enable"/> </span></label>
</div>

<fieldset class="ss_fieldset marginleft2 marginbottom3">
  <legend class="ss_legend"><ssf:nlt tag="schedule.digestSchedule" /></legend>	
<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
<tr>
<td valign="top">

<c:set var="schedule" value="${ssScheduleInfonotify.schedule}" scope="request"/>
<c:set var="schedPrefix" value="notify" scope="request"/>
<jsp:include page="/WEB-INF/jsp/administration/schedule.jsp" />
</td></tr></table>
</fieldset>

<fieldset class="ss_fieldset marginleft2">
	<legend class="ss_legend"><ssf:nlt tag="administration.configure.schedule.legend.outgoingAttachmentQuotas" /></legend>
	<div class="marginleft2">
		<label for="outgoingAttachmentSumLimit">
		  <span class="ss_labelAbove ss_normal"><ssf:nlt tag="administration.configure.schedule.quotaSum"/></span>
		</label>
		<input size="8" maxlength="8" type="textbox" style="text-align:right;"
		  id="outgoingAttachmentSumLimit" 
		  name="outgoingAttachmentSumLimit" 
		  value="${ssMailConfig.outgoingAttachmentSumLimitKb}" 
		  onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}' />
		<span><ssf:nlt tag="file.sizeKB"/></span>
	</div>
	<div class="margintop3 marginleft2">
		<label for="outgoingAttachmentSizeLimit">
		  <span class="ss_labelAbove ss_normal"><ssf:nlt tag="administration.configure.schedule.quotaFile"/></span>
		</label>
		<input size="8" maxlength="8" type="textbox" style="text-align:right;"
		  id= "outgoingAttachmentSizeLimit" 
		  name="outgoingAttachmentSizeLimit" 
		  value="${ssMailConfig.outgoingAttachmentSizeLimitKb}" 
		  onChange='if (!ss_checkIfNumberValid(this.value)){this.value="";}' />
		<span><ssf:nlt tag="file.sizeKB"/></span>
		<div class="ss_normal margintop3"><ssf:nlt tag="administration.configure.schedule.quotaUnlimited"/></div>
	</div>
</fieldset>

<c:if test="${ssSMTPEnabled}">
<% if (Utils.checkIfFilr()) { %>
	<div style="display: none;">
		<input type="checkbox" class="ss_style" id="simplepostenabled" name="simplepostenabled" <c:if test="${ssMailConfig.simpleUrlPostingEnabled}">checked</c:if>/>
	</div>
<% } else { %>
	<br/>
	<br/>
	<input type="checkbox" class="ss_style" id="simplepostenabled" name="simplepostenabled" <c:if test="${ssMailConfig.simpleUrlPostingEnabled}">checked</c:if>/>
	<span class="ss_labelRight"><ssf:nlt tag="incoming.enable.simple"/></span>
	<br/>
<% } %>
</c:if>
<c:if test="${!empty ssScheduleInfopost}">
<br/>
<input type="checkbox" class="ss_style" id="postenabled" name="postenabled" <c:if test="${ssScheduleInfopost.enabled}">checked</c:if>/>
<span class="ss_labelRight"><ssf:nlt tag="incoming.enable.all"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_enable_posting" /></span>
<br/>
<fieldset class="ss_fieldset">
  <legend class="ss_legend"><ssf:nlt tag="incoming.header" /></legend>	
<table class="ss_style" border="0" cellspacing="3" cellpadding="3">
<tr><td valign="top"> 
<c:set var="schedule" value="${ssScheduleInfopost.schedule}" scope="request"/>
<c:set var="schedPrefix" value="post" scope="request"/>
<jsp:include page="/WEB-INF/jsp/administration/schedule.jsp" />
</td></tr>
<c:if test="${!empty ssPostings}"> 
<tr>
<td valign="top">
<span class="ss_labelAbove"> <ssf:nlt tag="incoming.aliases"/><ssf:inlineHelp jsp="workspaces_folders/misc_tools/incomingAddresses" /></span>
<table border="0" cellspacing="0" cellpadding="0" class="ss_style ss_borderTable" >
  <tr class="ss_headerRow">
  <td class="ss_bold" align="center" width="5%" scope="col"><ssf:nlt tag="incoming.delete"/></td>
  <td class="ss_bold" align="left" colspan="2" scope="col"><ssf:nlt tag="incoming.alias" /></td>
  <td class="ss_bold" align="left" scope="col"><ssf:nlt tag="incoming.folder" /></td>
</tr>

<c:forEach var="alias" varStatus="status" items="${ssPostings}" >
<input type="hidden" id="aliasId${status.index}" name="aliasId${status.index}" value="${alias.id}"/>
<input type="hidden" id="alias${status.index}" name="alias${status.index}" value="${alias.emailAddress}"/>
<c:if test="${mail_posting_use_aliases == 'false'}">
  <c:set var="emailPassword" value=""/>
  <input type="hidden" id="password${status.index}" name="password${status.index}" value="${emailPassword}"/>
</c:if>
<tr><td>
<input type="checkbox" class="ss_normal" id="delete${status.index}" name="delete${status.index}">
</td><td>
<span class="ss_normal" id="aliasSpan${status.index}" name="aliasSpan${status.index}">${alias.emailAddress}</span>
</td><td>
<input type="button" value="Edit" onClick="${renderResponse.namespace}_showAliasDiv('${status.index}'); return false"/>
</td><td>
<c:if test="${!empty alias.binder}">
<a href="<ssf:permalink entity="${alias.binder}" />">
${alias.binder.title}&nbsp;&nbsp<span  class="ss_smallprint ss_light">(${alias.binder.parentBinder.title})</span>
</a>
</c:if>
</td></tr>
</c:forEach>
</table>
</td></tr>
</c:if>
</table>
</fieldset>
</c:if>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="button" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"
		onClick="return handleCloseBtn();" />
</div>
</form>
<div class="ss_style ss_popupMenu" style="visibility:hidden; display:block" name="${renderResponse.namespace}_modifyAliasDiv" id="${renderResponse.namespace}_modifyAliasDiv">
<form class="ss_style ss_form" 
  name="${renderResponse.namespace}_modifyAliasFm" 
  id="${renderResponse.namespace}_modifyAliasFm"
  onSubmit="${renderResponse.namespace}_modifyAlias(); ss_cancelPopupDiv('${renderResponse.namespace}_modifyAliasDiv'); return false;">
<br/><br/>
<label for="alias"><span class="ss_labelRight"><ssf:nlt tag="incoming.modifyAlias"/></span></label>
 <input type="text" name="alias" id="alias" value="" size="32"/> 
<c:if test="${mail_posting_use_aliases == 'false'}">
<br/>
<span class="ss_labelRight"><ssf:nlt tag="incoming.password"/></span>
 <input type="password" name="password" id="password" value="" size="32"/> 
</c:if>
<br/><br/>
<div class="ss_buttonBarLeft">
  <input type="submit" value="<ssf:nlt tag="button.ok"/>" 
  onClick="${renderResponse.namespace}_modifyAlias(); ss_cancelPopupDiv('${renderResponse.namespace}_modifyAliasDiv'); return false;">
  <input type="submit" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('${renderResponse.namespace}_modifyAliasDiv');return false;">  
</div>
</form>
</div>
</ssf:form>
</div>

</div>
</body>
</html>
