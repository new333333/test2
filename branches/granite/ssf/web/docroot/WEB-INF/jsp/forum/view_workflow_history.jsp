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
<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="org.kablink.teaming.util.ResolveIds" %>
<%@ page import="org.kablink.teaming.domain.Principal" %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ include file="/WEB-INF/jsp/common/view_css.jsp" %>
<ssf:ifadapter>
<body class="tundra">
</ssf:ifadapter>

<div class="ss_style ss_portlet diag_modal2">
<ssf:form titleTag="entry.workflowHistory">
<form class="ss_form" method="post" action="<ssf:url     
		adapter="true" 
		portletName="ss_forum" 
		action="view_workflow_history" 
		actionUrl="true">
		<ssf:param name="entityId" value="${ss_entityId}" />
		<ssf:param name="operation" value="modifyEntry" />
		</ssf:url>"
>
	 <div class="ss_formButtonRight">
	 	<input type="button" name="Button" value="<ssf:nlt tag="button.close"/>" 
	 	  onClick="ss_cancelButtonCloseWindow();return false;"/>
	 </div>
	 <c:if test="${!empty ssEntry}">
	   <div class="ss_bold ss_largerprint">${ssEntry.title}</div>
	 </c:if>
	 <br/>
    	<table width="100%" align="center" border="0" cellpadding="0" cellspacing="0">
    		<tr class="ss_tab_table_columnhead">
				<th><ssf:nlt tag="entry.eventNumber"/></th>
				<th><ssf:nlt tag="entry.modifiedOn"/></th>
				<th><ssf:nlt tag="entry.modifiedBy"/></th>
				<th><ssf:nlt tag="entry.operation"/></th>
				<th><ssf:nlt tag="entry.processName"/></th>
				<th><ssf:nlt tag="entry.threadName"/></th>
				<th><ssf:nlt tag="entry.state"/></th>
			</tr>
			
			<c:set var="lastLogVersion" value="0"/>
			<c:forEach var="change" items="${ss_changeLogList}">
			  <c:set var="changeLog" value="${change.changeLog}"/>
			  <jsp:useBean id="changeLog" type="org.kablink.teaming.domain.ChangeLog" />
			  <tr class="ss_tab_table_row">
				<td>
				  <c:if test="${changeLog.version != lastLogVersion}">
				    ${changeLog.version}
				  </c:if>
				  &nbsp
				</td>
				
				<td class="ss_table_data_TD" valign="top">
				  <c:if test="${changeLog.version != lastLogVersion}">
				    <fmt:formatDate timeZone="${ssUser.timeZone.ID}"
      				  value="${changeLog.operationDate}"  type="both" 
	  				  timeStyle="short" dateStyle="short" />
	  			  </c:if>
	  			  &nbsp
				</td>
				
				<td>
				  <c:if test="${changeLog.version != lastLogVersion}">
				    <%
				  	  String fullName = changeLog.getUserName();
				  	  java.util.List ps = ResolveIds.getPrincipals(changeLog.getUserId().toString(), false);
				  	  if (!ps.isEmpty()) fullName = ((Principal)ps.get(0)).getTitle();
				    %>
				    <%= fullName %> (${changeLog.userName})
				  </c:if>
				  &nbsp
				</td>
				
				<td>
				  <ssf:nlt tag="workflow.${changeLog.operation}"/>&nbsp
				</td>
				
				<td>			
				<%
					Document doc = changeLog.getDocument();
					Element root = doc.getRootElement();
					java.util.List<Element> workflowStates = root.selectNodes("//folderEntry/workflowState");
					if (workflowStates != null) {
						for (Element workflowState : workflowStates) {
							String stateName = workflowState.attributeValue("name", "???");
							String stateCaption = workflowState.attributeValue("stateCaption", stateName);
							String processName = workflowState.attributeValue("process", "???");
							String processId = "";
							Element property = (Element)workflowState.selectSingleNode("./property[@name='definition']");
							if (property != null) {
								processId = property.getText();
							    %>
								  <a target="_blank" title="<ssf:nlt tag="workflow.viewProcess"/>"
								    href="<ssf:url 
									  adapter="true" 
									  portletName="ss_forum" 
									  action="__ajax_request" 
									  actionUrl="false" ><ssf:param 
									  name="operation" value="get_workflow_applet" /><ssf:param 
									  name="workflowProcessId" value="<%= processId %>" /></ssf:url>"
									><%= processName %><img border="0" alt="<ssf:nlt tag="workflow.viewProcess"/>"
									src="<html:rootPath/>images/pics/sym_s_popup.gif"></a>&nbsp
								  <br>
							    <%
							} else {
							    %>
								  <%= processName %>&nbsp
								  <br>
							    <%
							}
						}
					}
				%>
				</td>
				
				<td>
				  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
					  <c:if test="${!empty workflow.value.attributes.threadCaption}">
					    ${workflow.value.attributes.threadCaption}&nbsp
					  </c:if>
					  <c:if test="${empty workflow.value.attributes.threadCaption}">
					    ${workflow.value.attributes.thread}&nbsp
					  </c:if>
					<br>
				  </c:forEach>
				</td>
				
				<td>
				  <c:forEach var="workflow" items="${change.folderEntry.workflowState}">
					  <c:if test="${!empty workflow.value.attributes.stateCaption}">
					    ${workflow.value.attributes.stateCaption}&nbsp
					  </c:if>
					  <c:if test="${empty workflow.value.attributes.stateCaption}">
					    ${workflow.value.attributes.name}&nbsp
					  </c:if>
					<br>
				  </c:forEach>
				</td>
			  </tr>
			  <c:set var="lastLogVersion" value="${changeLog.version}"/>
			</c:forEach>
    	</table> 
	 <br />
	 
	 <div class="ss_formButtonLeft">
	 	<input type="button" name="Button" value="<ssf:nlt tag="button.close"/>" 
	 	onClick="ss_cancelButtonCloseWindow();return false;"/>
	 </div>	 
</form>
</ssf:form>

</div>

<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
