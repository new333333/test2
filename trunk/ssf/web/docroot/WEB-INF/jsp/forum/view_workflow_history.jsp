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
<%@ page import="org.dom4j.Element" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<ssf:ifadapter>
<body>
</ssf:ifadapter>

<div class="ss_style ss_portlet">
<h3><ssf:nlt tag="entry.workflowHistory"/></h3>
<form class="ss_form" method="post" action="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_workflow_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="operation" value="modifyEntry" />
		</ssf:url>"
>
<table class="ss_style" cellpadding="10" width="100%">
<th><center><ssf:nlt tag="entry.Version"/></center></th>
<th><center><ssf:nlt tag="entry.modifiedOn"/></center></th>
<th><center><ssf:nlt tag="entry.modifiedBy"/></center></th>
<th><center><ssf:nlt tag="entry.operation"/></center></th>
<th><center><ssf:nlt tag="entry.processName"/></center></th>
<th><center><ssf:nlt tag="entry.threadName"/></center></th>
<th><center><ssf:nlt tag="entry.state"/></center></th>

<c:forEach var="change" items="${ss_changeLogList}">
<tr>
<td valign="top" width="05%" nowrap>
  <span style="padding-left:10px;">${change.folderEntry.attributes.logVersion}</span>
</td>
<td valign="top" width="10%" nowrap>
  <span style="padding-left:10px;">"${change.folderEntry.attributes.modifiedOn}"</span>
</td>
<td valign="top" width="10%" nowrap>
  <span style="padding-left:10px;">${change.folderEntry.attributes.modifiedBy}</span>
</td>
<td valign="top" width="10%" nowrap>
  <span style="padding-left:10px;"><ssf:nlt tag="workflow.${change.folderEntry.attributes.operation}"/></span>
</td>
<td valign="top" width="10%" nowrap>
  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
	  <span style="padding-left:10px;">${workflow.value.attributes.process}</span>
	<br>
  </c:forEach>
</td>
<td valign="top" width="10%" nowrap>
  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
	  <span style="padding-left:10px;">${workflow.value.attributes.thread}</span>
	<br>
  </c:forEach>
</td>
<td valign="top" width="10%" nowrap>
  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
	  <span style="padding-left:10px;">${workflow.value.attributes.name}</span>
	<br>
  </c:forEach>
</td>
<td valign="top" width="30%" nowrap/>
</tr>
</c:forEach>
<tr>
<td valign="top" nowrap>
  <input type="button" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;"/>
</td>
<td></td>
</tr>
</table>
</form>

<br/>
<br/>

</div>

<div id ="diff" style="display:none">
<h3 id="diff-header"><ssf:nlt tag="entry.comparison">
  <ssf:param name="value" value="<span id=\"versionNumberA\">x</span>"/>
  <ssf:param name="value" value="<span id=\"versionNumberB\">x</span>"/>
  </ssf:nlt>
</h3>
<h4 id="diff-key"><ssf:nlt tag="entry.comparison.key"/></h4>
<div id="diff-title" class="ss_largeprint"></div>
<div id="diff-desc" class="ss_entryContent ss_entryDescription"></div>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
