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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="org.kablink.teaming.util.NLT" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
<script type="text/javascript">
	var sentEmail=false;
	var width = ss_getWindowWidth()/2;
	if (width < 700) width=700;
	var height = ss_getWindowHeight();
	if (height < 600) height=600;
	self.window.resizeTo(width, height);

	function saveLocation() {
		document.getElementById("sendMailLocation").value = self.window.location;
	}
	ss_createOnLoadObj("saveLocation_sendMail", saveLocation);
</script>
<div class="ss_style ss_portlet ss_subsection2" style="padding: 0; margin: 0;">
  
<ssf:form title='<%= NLT.get("sendMail.title") %>'>

<form class="ss_style ss_form" method="post" 
  onSubmit="if (sentEmail) {return false;} else {sentEmail = true; return ss_onSubmit(this);}" name="${renderResponse.namespace}fm">

<input type="hidden" id="sendMailLocation" name="sendMailLocation" value=""                   />
<input type="hidden" id="ssUsersIdsToAdd"  name="ssUsersIdsToAdd"  value="${ssUsersIdsToAdd}" />

<div>
	<table class="ss_style margintop2" border="0" cellspacing="4" cellpadding="4">
		 <tr><td>
		   <span class="ss_bold"><ssf:nlt tag="sendMail.to"/>:</span><span class="marginleft1"><ssf:nlt tag="sendMail.addresses"/></span>
		   <div><input class="ss_style" type="text" name="addresses" id="addresses" size="86" value=""></div>
		 </td></tr>
		 <tr><td>
		 <input class="ss_style" type="checkbox" name="self" id="self" >&nbsp;<span class="ss_labelRight">
		  <label for="self"><ssf:userTitle user="${ssUser}"/> (${ssUser.emailAddress})</label></span>
		 </td></tr>
	</table>

	<table class="ss_style margintop2" style="margin-left: 30px;" border="0" cellspacing="4" cellpadding="4">
		<tr>
		<td class="ss_bold" valign="top" style="padding-top: 9px;"><ssf:nlt tag="general.users" text="Users"/></td>
		<td valign="top">
		  <ssf:find formName="${renderResponse.namespace}fm" formElement="users" 
			type="user" userList="${ssUsers}" binderId="${ssBinder.id}" sendingEmail="true" width="150px"/>
		</td>
		</tr>
		<tr>
		<td class="ss_bold" valign="top" style="padding-top: 9px;"><ssf:nlt tag="general.groups" text="Groups"/></td>
		<td valign="top">
		  <ssf:find formName="${renderResponse.namespace}fm" formElement="groups" 
			type="group" userList="${ssGroups}" sendingEmail="true" width="150px"/>
		</td>
		</tr>
	</table>

	<table class="ss_style margintop2" border ="0" cellspacing="4" cellpadding="4" width="95%">
		<tr><td>
			<ssf:clipboard type="user" formElement="users" />
			<c:if test="${!empty ssBinder}">
				<ssf:teamMembers binderId="${ssBinder.id}" formElement="users" appendAll="${appendTeamMembers}"/>
			</c:if>
		</td></tr>
	</table>

	<ssf:expandableArea title='<%= NLT.get("mail.cc") %>'>
		<table class="ss_style" style="margin-left: 30px;" border="0" cellspacing="4" cellpadding="4">
			<tr>
			<td class="ss_bold" valign="top" style="padding-top: 9px;"><ssf:nlt tag="general.users" text="Users"/></td>
			<td valign="top">
			  <ssf:find formName="${renderResponse.namespace}fm" formElement="ccusers" 
				type="user" sendingEmail="true" width="150px" />
			</td>
			</tr>
			<tr>
			<td class="ss_bold" valign="top" style="padding-top: 9px;"><ssf:nlt tag="general.groups" text="Groups"/></td>
			<td valign="top">
			  <ssf:find formName="${renderResponse.namespace}fm" formElement="ccgroups" 
				type="group" sendingEmail="true" width="150px"/>
			</td>
			</tr>
		</table>
	</ssf:expandableArea>

	<ssf:expandableArea title='<%= NLT.get("mail.bcc") %>'>
		<table class="ss_style" style="margin-left: 30px;" border="0" cellspacing="4" cellpadding="4">
			<tr>
			<td class="ss_bold" valign="top" style="padding-top: 9px;"><ssf:nlt tag="general.users" text="Users"/></td>
			<td valign="top">
			  <ssf:find formName="${renderResponse.namespace}fm" formElement="bccusers" 
				type="user" sendingEmail="true" width="150px" />
			</td>
			</tr>
			<tr>
			<td class="ss_bold" valign="top" style="padding-top: 9px;"><ssf:nlt tag="general.groups" text="Groups"/></td>
			<td valign="top">
			  <ssf:find formName="${renderResponse.namespace}fm" formElement="bccgroups" 
				type="group" sendingEmail="true" width="150px"/>
			</td>
			</tr>
		</table>
	</ssf:expandableArea>
</div>


<div class="ss_subsection"> 
	<table class="ss_style"  border ="0" cellspacing="4" cellpadding="4" width="95%">
	 <tr><td>
	   <span class="ss_labelAbove ss_bold"><ssf:nlt tag="sendMail.subject"/></span>
	   <!-- just use title, not docNumber so calendar entries contain summary -->
	   <input class="ss_style" type="text" name="subject" id="subject" size="86" 
		 <c:if test="${!empty ssEntry}">value="<ssf:escapeQuotes>${ssEntry.title}</ssf:escapeQuotes>" </c:if>
	   >
	</td></tr>
	<tr><td>
	   <span class="ss_labelAbove ss_bold"><c:if test="${!empty ssEntry}"><ssf:nlt tag="sendMail.message"/></c:if><c:if test="${empty ssEntry}"><ssf:nlt tag="sendMail.message"/></c:if></span>
		<div align="left">
	<%@ include file="/WEB-INF/jsp/binder/sendMail_htmlTextarea.jsp" %> 
	</td></tr>
	<c:if test="${!empty ssEntry}">
	
	<tr><td>
	 <input type="checkbox" name="attachments" id="attachments" class="ss_style" >&nbsp;<span class="ss_labelRight">
	  <ssf:nlt tag="sendMail.includeAttachments"/></span></td>
	</td></tr>
	</c:if>
	</table>
</div>
<div class="margintop3"
	<div class="ss_buttonBarRight">
		<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.send"/>">
		<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>" onClick="ss_cancelButtonCloseWindow();return false;">
	</div>
</div>
	<sec:csrfInput />
</form>
</ssf:form>

</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
