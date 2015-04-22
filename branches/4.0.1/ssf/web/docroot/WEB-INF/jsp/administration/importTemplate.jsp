<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<c:choose>
	<c:when test="${ssOperation == 'import'}">
		<c:set var="ss_windowTitle" value='<%= NLT.get("administration.configure_cfg.import") %>' scope="request"/>
		<c:set var="ss_windowTitleTag" value="administration.configure_cfg.import" scope="request"/>
	</c:when>
	<c:otherwise>
		<c:set var="ss_windowTitle" value='<%= NLT.get("administration.reload.templates") %>' scope="request"/>
		<c:set var="ss_windowTitleTag" value="administration.reload.templates" scope="request"/>
	</c:otherwise>
</c:choose>


<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">
<ssf:form titleTag="${ss_windowTitleTag}">

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

<c:if test="${ssOperation == 'import'}">
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url adapter="true" action="configure_configuration" 
			actionUrl="true" ><ssf:param name="operation" value="import"/><ssf:param 
			name="binderParentId" value="${binderParentId}"/></ssf:url>">

<div class="margintop2">&nbsp;</div>

<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="50%">
<thead><th><ssf:nlt tag="administration.import.replace"/></th>
<th><ssf:nlt tag="administration.selectFiles"/></th>
</thead>
<tbody>
<tr><td><input type="checkbox" name="template1ck"></td>
<td><input type="file" size="80" class="ss_text" name="template1" ></td></tr>
<tr><td><input type="checkbox" name="template2ck"></td>
<td><input type="file" size="80" class="ss_text" name="template2" ></td></tr>
<tr><td><input type="checkbox" name="template3ck"></td>
<td><input type="file" size="80" class="ss_text" name="template3" ></td></tr>
<tr><td><input type="checkbox" name="template4ck"></td>
<td><input type="file" size="80" class="ss_text" name="template4" ></td></tr>
<tr><td><input type="checkbox" name="template5ck"></td>
<td><input type="file" size="80" class="ss_text" name="template5ck" ></td></tr>
</tr></tbody></table>
<div class="margintop2"></div>

<br/>
<div class="ss_formBreak"></div>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</div>
</form>
</c:if>

<c:if test="${ssOperation == 'reset'}">
<div class="margintop2">&nbsp;</div>
<br>

<form class="ss_style ss_form" method="post" 
		  action="<ssf:url action="configure_configuration" actionUrl="true"><ssf:param 
		  name="operation" value="reset"/><ssf:param 
		  name="binderParentId" value="${binderParentId}"/></ssf:url>" 
		  name="${renderResponse.namespace}fm">

<span>
<ssf:nlt tag="administration.reload.templates.warning"/>
</span>
<br/>
<br/>

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>
&nbsp;&nbsp;&nbsp;
<input type="submit" class="ss_submit" name="cancelBtn" value="<ssf:nlt tag="button.cancel"/>"/>

</form>
</c:if>
</ssf:form>
</div>
</div>
</body>
</html>
