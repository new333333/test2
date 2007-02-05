<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ taglib prefix="portlet" uri="http://java.sun.com/portlet" %>
<%@ taglib prefix="portletadapter" uri="http://www.sitescape.com/tags-portletadapter" %>
<portletadapter:defineObjects1/>
<ssf:ifadapter><portletadapter:defineObjects2/></ssf:ifadapter>
<ssf:ifnotadapter><portlet:defineObjects/></ssf:ifnotadapter>

<c:if test="${ss_showUserShowPresence}">
  <ssf:presenceInfo user="${ss_showUserUser}" showTitle="true" titleStyle="${ss_showUserTitleStyle}" /> 
</c:if>
<c:if test="${!ss_showUserShowPresence}">
	<ssf:ifadapter>
	  <a href="<ssf:url adapter="true" portletName="ss_forum" 
	    action="view_permalink"
	    binderId="${ss_showUserUser.parentBinder.id}"
	    entryId="${ss_showUserUser.id}">
	    <ssf:param name="entityType" value="workspace" />
	    <ssf:param name="newTab" value="1" />
		</ssf:url>"
		onClick="self.parent.location.href = this.href;return false;"
	  ><span id="${ss_showUserUser.id}" 
	    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span></a>
	</ssf:ifadapter>
	<ssf:ifnotadapter>
	  <a href="<portlet:renderURL windowState="maximized"><portlet:param 
	  	name="action" value="view_ws_listing"/><portlet:param 
	  	name="binderId" value="${ss_showUserUser.parentBinder.id}"/><portlet:param 
	  	name="entryId" value="${ss_showUserUser.id}"/><portlet:param 
	  	name="newTab" value="1"/></portlet:renderURL>"
	  ><span id="${ss_showUserUser.id}"  
	    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span></a>
	</ssf:ifnotadapter>
  <span id="${ss_showUserUser.id}" 
    class="${ss_showUserTitleStyle} ss_muster_users">${ss_showUserUser.title}</span>
</c:if>
