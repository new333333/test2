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
<c:set var="ss_windowTitle" value='<%= NLT.get("administration.import.definitions") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<body class="ss_style_body tundra">
<div class="ss_pseudoPortal">
<ssf:form titleTag="administration.import.definitions">

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
<form class="ss_style ss_form" method="post" enctype="multipart/form-data" 
		  action="<ssf:url action="import_definition" 
			actionUrl="true"><ssf:param 
		  name="binderId" value="${ssBinder.id}"/></ssf:url>">
<c:if test="${empty ssBinder}">
<span class="ss_titlebold"><ssf:nlt tag="administration.import.definitions.public" /></span>
</c:if>
<c:if test="${!empty ssBinder}">
<span class="ss_titlebold"><ssf:nlt tag="administration.import.definitions.local"><ssf:param
	name="value" value="${ssBinder.pathName}"/></ssf:nlt></span>
</c:if>
<br>

<div class="ss_divider"></div>
<br>


<table class="ss_style" border="0" cellpadding="5" cellspacing="0" width="50%">
<thead><th><ssf:nlt tag="administration.import.replace"/></th>
<th><ssf:nlt tag="administration.selectFiles"/></th>
</thead>
<tbody>
<tr>
	<td>
		<input type="checkbox" name="definition1ck" id="definition1ck">
		<label for="definition1ck"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
	<td>
		<input type="file" size="80" class="ss_text" name="definition1" id="definition1">
		<label for="definition1"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
</tr>
<tr>
	<td>
		<input type="checkbox" name="definition2ck" id="definition2ck">
		<label for="definition2ck"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
	<td>
		<input type="file" size="80" class="ss_text" name="definition2" id="definition2">
		<label for="definition2"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
</tr>
<tr>
	<td>
		<input type="checkbox" name="definition3ck" id="definition3ck">
		<label for="definition3ck"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
	<td>
		<input type="file" size="80" class="ss_text" name="definition3" id="definition3">
		<label for="definition3"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
</tr>
<tr>
	<td>
		<input type="checkbox" name="definition4ck" id="definition4ck">
		<label for="definition4ck"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
	<td>
		<input type="file" size="80" class="ss_text" name="definition4" id="definition4">
		<label for="definition4"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
</tr>
<tr>
	<td>
		<input type="checkbox" name="definition5ck" id="definition5ck">
		<label for="definition5ck"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
	<td>
		<input type="file" size="80" class="ss_text" name="definition5" id="definition5">
		<label for="definition5"><span style="display:none;"><ssf:nlt tag="label.fileSelect"/></span></label>
	</td>
</tr>
</tr></tbody></table>
<div class="ss_divider"></div>

<br/>
<div class="ss_formBreak"></div>

<div class="ss_buttonBarLeft">

<input type="submit" class="ss_submit" name="okBtn" value="<ssf:nlt tag="button.ok" />"/>

<input type="submit" class="ss_submit" name="closeBtn" value="<ssf:nlt tag="button.close" text="Close"/>"/>

</div>
</div>
	<sec:csrfInput />
</form>
</div>

</ssf:form>
</div>
</body>
</html>