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
<%@ page import="com.sitescape.team.util.NLT" %>

<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
var <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex;

function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_showAliasDiv(index) {
	
	self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.alias.value=eval('self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.alias' + index + '.value');
	<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex=index;
	ss_showPopupDivCentered('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv');	
}
function <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAlias() {
	var param = eval('self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm.alias' + <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex);
	param.value = self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.alias.value;
	var param = document.getElementById('aliasSpan' + <ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_savedIndex);
	param.innerHTML = self.document.<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm.alias.value;
}
</script>
<div class="ss_style ss_portlet">
<form class="ss_style ss_form" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>fm" method="post" 
	action="<portlet:actionURL windowState="maximized"><portlet:param 
	name="action" value="configure_posting_job"/></portlet:actionURL>">

<table class="ss_style"  border="1" cellspacing="0" cellpadding="3" width="100%">
<tr>
<th><ssf:nlt tag="incoming.job_title"/></th>
<th><ssf:nlt tag="incoming.aliases"/> <ssf:inlineHelp tag="ihelp.email.incomingAddresses" /></th>
</tr>
<tr><td valign="top">
<table class="ss_style" border ="0" cellspacing="0" cellpadding="3">
<tr><td> 
<input type="checkbox" class="ss_labelRight" id="enabled" name="enabled" <c:if test="${ssScheduleInfo.enabled}">checked</c:if>/>
<ssf:nlt tag="incoming.enable.all"/> <ssf:inlineHelp jsp="workspaces_folders/misc_tools/email_enable_posting" />
<br/>

<c:set var="schedule" value="${ssScheduleInfo.schedule}"/>
<%@ include file="/WEB-INF/jsp/administration/schedule.jsp" %>
</td></tr></table>
</td><td valign="top">
 <br/>
<table border="0" cellspacing="0" cellpadding="0" class="ss_style ss_borderTable" >
  <tr class="ss_headerRow">
  <td class="ss_bold" align="center" width="5%" scope="col"><ssf:nlt tag="incoming.delete"/></td>
  <td class="ss_bold" align="left" colspan="2" scope="col"><ssf:nlt tag="incoming.alias" /></td>
  <td class="ss_bold" align="left" scope="col"><ssf:nlt tag="incoming.folder" /></td>
</tr>

<c:forEach var="alias" varStatus="status" items="${ssPostings}" >
<input type="hidden" id="aliasId${status.index}" name="aliasId${status.index}" value="${alias.id}"/>
<input type="hidden" id="alias${status.index}" name="alias${status.index}" value="${alias.emailAddress}"/>
<tr><td>
<input type="checkbox" class="ss_normal" id="delete${status.index}" name="delete${status.index}">
</td><td>
<span class="ss_normal" id="aliasSpan${status.index}" name="aliasSpan${status.index}">${alias.emailAddress}</span>
</td><td>
<input type="button" value="Edit" onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_showAliasDiv('${status.index}'); return false"/>
</td><td>
<c:if test="${!empty alias.binder}">
${alias.binder.title}&nbsp;&nbsp<span  class="ss_smallprint ss_light">(${alias.binder.parentBinder.title})</span>
</c:if>
</td></tr>
</c:forEach>
</table>
</table>
<br/>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.apply" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>">
</div>
</form>
<div class="ss_style ss_popupMenu" style="visibility:hidden; display:block" name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv" id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv">
<form class="ss_style ss_form" 
  name="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm" 
  id="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasFm"
  onSubmit="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAlias(); ss_cancelPopupDiv('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv'); return false;">
<br/><br/>
<span class="ss_labelRight"><ssf:nlt tag="incoming.modifyAlias"/></span>
 <input type="text" name="alias" id="alias" value="" size="32"/> 
<br/><br/>
<div class="ss_buttonBarLeft">
  <input type="submit" value="<ssf:nlt tag="button.ok"/>" 
  onClick="<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAlias(); ss_cancelPopupDiv('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv'); return false;">
  <input type="submit" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('<ssf:ifadapter><portletadapter:namespace/></ssf:ifadapter><ssf:ifnotadapter><portlet:namespace/></ssf:ifnotadapter>_modifyAliasDiv');return false;">  
</div>
</form>
</div>
</div>
