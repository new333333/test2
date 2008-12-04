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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<div class="ss_style ss_portlet">

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
			actionUrl="true" ><ssf:param name="operation" value="import"/></ssf:url>">
<span class="ss_titlebold"><ssf:nlt tag="administration.configure_cfg.import" /></span>
<br>
<div class="ss_divider"></div>
<br>

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
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"/>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />">
<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close"/>">
</div>
</div>
</form>
</c:if>
<c:if test="${ssOperation == 'reset'}">
<span class="ss_titlebold"><ssf:nlt tag="administration.reload.templates" /></span>
<br>
<br>

<form class="ss_style ss_form" method="post" 
		  action="<ssf:url action="configure_configuration" actionUrl="true"><ssf:param 
		  name="operation" value="reset"/></ssf:url>" 
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
</div>
</div>
</body>
</html>
