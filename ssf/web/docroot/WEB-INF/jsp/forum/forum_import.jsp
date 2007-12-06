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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body>
</ssf:ifadapter>

<table class="ss_style" cellpadding="10" width="100%"><tr><td>

<form class="ss_form" method="post" style="display:inline;" enctype="multipart/form-data"
	action="<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="forum_import" 
	actionUrl="true" ><ssf:param 
	name="binderId" value="${ssBinder.id}"/><ssf:param 
	name="binderType" value="${ssBinder.entityType}"/></ssf:url>">
	
<ssf:nlt tag="ihelp.forum_import.general"/><br/>

  <input type="file" class="ss_text" name="forumFile" id="forumFile"/><br/>

<c:if test="${!empty ssDefinitionChoices}">
Create imported entries as: <select name="entryType">
<c:forEach var="def" items="${ssDefinitionChoices}">
	<option value="${def.key}">${def.value}</option>
</c:forEach>
</select>
</li>
</c:if>
<c:if test="${empty ssDefinitionChoices}">
Imported entries will be created as: <ssf:nlt tag="${ssDefinition.title}"/> (${ssDefinition.name})
<input type="hidden" name="entryType" value="${ssDefinition.id}"/>
</c:if>
<br/>
<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok"/>"/>
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>" />

</form>
</td></tr></table>
<c:if test="${!empty ssErrorList}">
<span class="ss_bold"><ssf:nlt tag="administration.errors"/></span>
<br/>
<br/>
<ul>
<c:forEach var="err" items="${ssErrorList}">
	<li>${err}</li>
</c:forEach>
</ul>
</c:if>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
