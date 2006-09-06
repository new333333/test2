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
		!displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_VERTICAL)) {
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
	<c:choose>
	<c:when test="${empty ssSubscription}">
	<input type="submit" class="ss_submit" name="subscribeBtn" 
		     value="<ssf:nlt tag="button.subscribe"/>">
	</c:when>
	<c:otherwise>
	<input type="submit" class="ss_submit" name="unsubscribeBtn" 
		     value="<ssf:nlt tag="button.unsubscribe"/>">
	</c:otherwise>
	</c:choose>
	
	
  </form>
</div>
