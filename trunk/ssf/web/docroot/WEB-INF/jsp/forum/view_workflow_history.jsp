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
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>

<ssf:ifadapter>
<body>
</ssf:ifadapter>

<div class="ss_style ss_portlet">
<form class="ss_form" method="post" action="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_workflow_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="operation" value="modifyEntry" />
		</ssf:url>"
>
<table width="75%" border="0" align="center" cellpadding="0" cellspacing="0" class="ss_table_wrap">
  <tr>
    <td class="ss_form_header"><div height="267"><ssf:nlt tag="entry.workflowHistory"/></div>
	 <div class="ss_formButton">
	 	<input type="button" name="Button" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;"/>
	 </div>
	 <br/>
    	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
    		<tr>
				<th width="4.75%" class="ss_table_heading"><ssf:nlt tag="entry.eventNumber"/></th>
				<th width="14%" class="ss_table_heading"><ssf:nlt tag="entry.modifiedOn"/></th>
				<th width="10.5%" class="ss_table_heading"><ssf:nlt tag="entry.modifiedBy"/></th>
				<th width="10%" class="ss_table_heading"><ssf:nlt tag="entry.operation"/></th>
				<th width="13%" class="ss_table_heading"><ssf:nlt tag="entry.processName"/></th>
				<th width="13.25%" class="ss_table_heading"><p><ssf:nlt tag="entry.threadName"/></p></th>
				<th width="7.5%" class="ss_table_heading"><ssf:nlt tag="entry.state"/></th>
			</tr>
			
			<c:set var="odd" value="${false}"/>
			
			<c:forEach var="change" items="${ss_changeLogList}">
				<c:set var="odd" value= "${not odd}"/>
				
				<tr <c:if test="${odd == 'true'}">class="ss_table_oddRow "</c:if>>		
					<td width="4.75%" class="ss_table_data_mid">
					  ${change.folderEntry.attributes.logVersion}&nbsp
					</td>
					
					<td width="14%" class="ss_table_data_TD">
					 ${change.folderEntry.attributes.modifiedOn}&nbsp
					</td>
					
					<td width="10.5%" class="ss_table_data_TD">
					 ${change.folderEntry.attributes.modifiedBy}&nbsp
					</td>
					
					<td width="10%" class="ss_table_data_TD">
					  <ssf:nlt tag="workflow.${change.folderEntry.attributes.operation}"/>&nbsp
					</td>
					
					<td width="13%"class="ss_table_data_TD">			
					  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
						  ${workflow.value.attributes.process}&nbsp
						<br>
					  </c:forEach>
					</td>
					
					<td width="13.25" class="ss_table_data_TD">
					  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
						  ${workflow.value.attributes.threadCaption}&nbsp
						<br>
					  </c:forEach>
					</td>
					
					<td width="7.5" class="ss_table_data_TD">
					  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
						  ${workflow.value.attributes.stateCaption}&nbsp
						<br>
					  </c:forEach>
					</td>
				</tr>
			</c:forEach>
		</table> 
	 <br />
	 
	 <div class="ss_formButton">
	 	<input type="button" name="Button" value="<ssf:nlt tag="button.close"/>" onClick="self.window.close();return false;"/>
	 </div>	 
<tr>
<td valign="top" nowrap>
</td>
<td></td>
</tr>
</table>
</body>
</form>

<br/>
<br/>

</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
