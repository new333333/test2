<%@ include file="/WEB-INF/jsp/common/snippet.include.jsp" %>

<c:choose>
<c:when test="${!empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
<span><ssf:nlt tag="general.notLoggedIn"/></span>
</c:when>
<c:otherwise>


<div class="ss_style" align="left">
<form method="post" onSubmit="ss_setActionUrl(this, ss_saveSubscriptionUrl);">
<span class="ss_largerprint ss_bold"><ssf:nlt tag="subscribe.select.type"/></span>
<br/>
<br/>
<div class="ss_indent_medium">
  <input type="radio" name="notifyType" value="1"/><ssf:nlt tag="subscribe.digest"/><br/>
  <input type="radio" name="notifyType" value="2"/><ssf:nlt tag="subscribe.message"/><br/>
  <input type="radio" name="notifyType" value="3"/><ssf:nlt tag="subscribe.noattachments"/><br/>
  
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
