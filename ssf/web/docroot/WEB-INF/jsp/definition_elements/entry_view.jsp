<% //View an entry %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:useBean id="ssUser" type="com.sitescape.ef.domain.User" scope="request" />

<div class="ss_style ss_portlet">
<%
	String displayStyle = ssUser.getDisplayStyle();
	if (displayStyle == null || displayStyle.equals("")) {
		displayStyle = ObjectKeys.USER_DISPLAY_STYLE_IFRAME;
	}
	if (!displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_IFRAME) && 
		!displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL) &&
		!displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_POPUP)) {
%>
<%@ include file="/WEB-INF/jsp/definition_elements/navigation_links.jsp" %>
<%
	}
%>
<ssf:toolbar toolbar="${ssFolderEntryToolbar}" style="ss_actions_bar" />
<%@ include file="/WEB-INF/jsp/definition_elements/popular_view.jsp" %>
<%@ include file="/WEB-INF/jsp/definition_elements/tag_view.jsp" %>

<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
  configElement="${item}" 
  configJspStyle="${ssConfigJspStyle}" 
  entry="${ssDefinitionEntry}" />
  
<br>

<form class="ss_style ss_form" method="post" action="" style="display:inline;">

	<input type="submit" class="ss_submit" name="subscribe" 
	<c:if test="${empty ssSubscription}">value="<ssf:nlt tag="button.subscribe"/>" </c:if>
	<c:if test="${!empty ssSubscription}">value="<ssf:nlt tag="button.subscription"/>" </c:if>
	onClick="ss_showPopupDiv('ss_subscription_entry'); return false;">

<div name="ss_subscription_entry" id="ss_subscription_entry" style="display:none; visibility:hidden" class="ss_popupMenu ss_indent_medium">
  <input type="radio" name="notifyType" value="2"
  <c:if test="${ssSubscription.style=='2'}"> checked="checked"</c:if>
  /><ssf:nlt tag="subscribe.message"/><br/>
  <input type="radio" name="notifyType" value="3"
  <c:if test="${ssSubscription.style=='3'}"> checked="checked"</c:if>
  /><ssf:nlt tag="subscribe.noattachments"/><br/>
<c:if test="${!empty ssSubscription}">
  <input type="radio" name="notifyType" value="-1"/><ssf:nlt tag="subscribe.delete"/><br/>
</c:if>
  <br/>
  <input type="submit" name="subscribeBtn" value="<ssf:nlt tag="button.ok"/>">
 &nbsp;&nbsp;&nbsp;
  <input type="submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"
  onClick="ss_cancelPopupDiv('ss_subscription_entry');return false;">
</div>	
</form>
</div>
