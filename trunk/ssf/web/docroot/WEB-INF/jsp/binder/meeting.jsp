<%
/**
 * Copyright (c) 2007 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="com.sitescape.team.util.NLT" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>
<script type="text/javascript">
	var width = ss_getWindowWidth()/2;
	if (width < 700) width=700;
	var height = ss_getWindowHeight();
	if (height < 600) height=600;
self.window.resizeTo(width, height);

	var rn = Math.round(Math.random()*999999);
	var meetingToken;

	function ss_startMeeting() {
		var a = 1;
		ss_onSubmit(document.getElementById("startMeetingForm"));
		var url = "<ssf:url 
	    	adapter="true" 
	    	portletName="ss_forum" 
	    	action="__ajax_request" 
	    	actionUrl="true" >
			<ssf:param name="operation" value="start_meeting" />
	    	</ssf:url>";
		var ajaxRequest = new ss_AjaxRequest(url);
		ajaxRequest.addFormElements("startMeetingForm");
		ajaxRequest.setPostRequest(ss_launchMeeting);
		ajaxRequest.setUsePOST();
		ajaxRequest.sendRequest();  //Send the request
	}	

	function ss_launchMeeting() {
		if (document.getElementById("meetingToken"))
			self.location.href = 'iic:meetmany?meetingtoken=' + document.getElementById("meetingToken").value;
		return false;
	}	

</script>


<div class="ss_style ss_portlet" style="padding:10px;">
<c:choose>
<c:when test="${!empty ssErrorList}">
<form class="ss_style ss_form" method="post">
<span class="ss_titlebold"><ssf:nlt tag="meeting.status"/></span><br/>
<br/>
<ul>
<c:forEach var="item" items="${ssErrorList}">
	<li>${item}</li>
</c:forEach>
</ul>
<div class="ss_buttonBarLeft">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;">
</div>
</form>
</c:when>
<c:otherwise>

<form class="ss_style ss_form" method="post" id="startMeetingForm" name="startMeetingForm">
	<input type="hidden" id="meetingToken" value="" />
	<input type="hidden" name="binderId" value="${ssBinder.id}" />
	<c:if test="${!empty ssEntry}">
		<input type="hidden" name="entryId" value="${ssEntry.id}" />	
	</c:if>

	<span class="ss_bold"><c:if test="${action == 'start_meeting'}"><ssf:nlt tag="meeting.start.title"/></c:if><c:if test="${action == 'schedule_meeting'}"><ssf:nlt tag="meeting.schedule.title"/></c:if></span>
	<table class="ss_style"  border="0" cellspacing="0" cellpadding="0" width="95%">
	 <tr><td>
	<fieldset class="ss_fieldset">
	  <legend class="ss_legend"><ssf:nlt tag="meeting.invitees" /></legend>

	
	<table class="ss_style"  border ="0" cellspacing="0" cellpadding="3" width="95%">
	<tr>
	<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
	<td valign="top">
	  <ssf:find formName="startMeetingForm" formElement="users" 
	    type="user" userList="${ssUsers}" clipboardUserList="${ssClipboardPrincipals}"/>
	</td>
	</tr>
	</table>
	</fieldset>
	</td></tr>
	
	</table>

</form>

<a class="ss_linkButton ss_bold ss_smallprint" href="#"
  onClick="ss_startMeeting();"
><c:if test="${action == 'start_meeting'}"><ssf:nlt tag="meeting.start"/></c:if><c:if test="${action == 'schedule_meeting'}"><ssf:nlt tag="meeting.schedule"/></c:if></a>

<br/>

<div class="ss_buttonBarLeft">
	<form class="ss_style ss_form" method="post" 
	  onSubmit="return ss_onSubmit(this);" name="<portlet:namespace />fm">
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;">
	</form>
</div>


</c:otherwise>
</c:choose>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
