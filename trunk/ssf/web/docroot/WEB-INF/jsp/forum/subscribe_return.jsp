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
<form method="post" onSubmit="ss_setActionUrl(this, ss_saveSubscriptionUrl);">
<c:if test="${ssScheduleInfo.enabled}">
<span class="ss_largerprint ss_bold"><ssf:nlt tag="subscribe.select.type"/></span>
</c:if>
<c:if test="${!ssScheduleInfo.enabled}">
<span class="ss_largerprint ss_bold"><ssf:nlt tag="subscribe.select.disabled"/>
<br><ssf:nlt tag="subscribe.contact.admin">
<ssf:param name="value" value="${ssBinder.owner.emailAddress}"/>
<ssf:param name="value" value="${ssBinder.owner.title}"/>
</ssf:nlt></span>
</c:if>
<br/>
<br/>
<div class="ss_indent_medium">
  <input type="radio" name="notifyType" value="1" id="notifyType_${ssSubscription.id.entityId}_1"
  <c:if test="${ssSubscription.style=='1'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssSubscription.id.entityId}_1"><ssf:nlt tag="subscribe.digest"/></label><br/>
  <input type="radio" name="notifyType" value="2" id="notifyType_${ssSubscription.id.entityId}_2"
  <c:if test="${ssSubscription.style=='2'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssSubscription.id.entityId}_2"><ssf:nlt tag="subscribe.message"/></label><br/>
  <input type="radio" name="notifyType" value="3" id="notifyType_${ssSubscription.id.entityId}_3"
  <c:if test="${ssSubscription.style=='3'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssSubscription.id.entityId}_3"><ssf:nlt tag="subscribe.noattachments"/></label><br/>
  <input type="radio" name="notifyType" value="-1" id="notifyType_${ssSubscription.id.entityId}_delete"/><label for="notifyType_${ssSubscription.id.entityId}_delete"><ssf:nlt tag="subscribe.delete"/></label><br/>
<br/>
  <input type="radio" name="notifyType" value="4" id="notifyType_${ssSubscription.id.entityId}_4"
  <c:if test="${ssSubscription.style=='4'}"> checked="checked"</c:if>
  /><label for="notifyType_${ssSubscription.id.entityId}_4"><ssf:nlt tag="subscribe.disable"/></label><br/>
  
  <br/>
  <input type="submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>">
  &nbsp;&nbsp;&nbsp;
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('ss_subscription_menu');return false;">
  
</div>
</form>
</div>
</c:otherwise>
</c:choose>
