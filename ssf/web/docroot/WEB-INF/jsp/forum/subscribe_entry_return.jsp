<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>

<c:choose>
<c:when test="${!empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<span><ssf:nlt tag="general.notLoggedIn"/></span>
</c:when>
<c:otherwise>

<div class="ss_style" align="left">

<c:choose>
	<c:when test="${empty ss_namespace}">
		<form method="post">
	</c:when>
	<c:otherwise>
		<form method="post" onSubmit="ss_setActionUrl(this, ss_saveSubscriptionUrl);">
	</c:otherwise>
</c:choose>

  <span class="ss_bold"><ssf:nlt tag="subscribe.select.type"/></span><br/><br/>
  <input type="radio" name="notifyType" value="2" id="notifyType_${ssEntry.id}_2"
  <c:if test="${ssSubscription.style=='2'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssEntry.id}_2"><ssf:nlt tag="subscribe.message"/></label> <ssf:inlineHelp tag="ihelp.email.individual_notify"/><br/>
  <input type="radio" name="notifyType" value="3" id="notifyType_${ssEntry.id}_3"
  <c:if test="${ssSubscription.style=='3'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssEntry.id}_3"><ssf:nlt tag="subscribe.noattachments"/></label><br/>
<c:if test="${!empty ssSubscription}">
  <input type="radio" name="notifyType" id="notifyType_${ssEntry.id}_delete" value="-1"/><label for="notifyType_${ssEntry.id}_delete"><ssf:nlt tag="subscribe.delete"/></label><br/>
</c:if>
  <br/>
  <input type="hidden" name="subscribeEntryId" value="${ssEntry.id}" />
  <input type="submit" name="subscribeBtn" value="<ssf:nlt tag="button.ok"/>">
 &nbsp;&nbsp;&nbsp;
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('${ss_namespace}ss_subscription_entry${ssEntry.id}');return false;">
</form>
</div>
</c:otherwise>
</c:choose>