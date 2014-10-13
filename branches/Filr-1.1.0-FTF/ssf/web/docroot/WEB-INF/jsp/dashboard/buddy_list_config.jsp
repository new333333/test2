<%
/**
 * Copyright (c) 1998-2010 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2010 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2010 Novell, Inc. All Rights Reserved.
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
<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>

<div style="margin:6px; width:100%;">
	<h3><ssf:nlt tag="presence.configure" text="Configure buddy list"/></h3>
	<fieldset class="ss_fieldset">
		<legend class="ss_legend"><ssf:nlt tag="presence.configure.buddies" text="Buddies"/></legend>

		<table cellpadding="10px" width="100%" style="border-spacing: 10px;">
			<tr>
				<td class="ss_bold" valign="top"><ssf:nlt tag="general.users" text="Users"/></td>
				<td valign="top">
					<ssf:find formName="${ss_dashboard_config_form_name}" formElement="data_users" 
    					type="user" userList="${ssDashboard.beans[ssComponentId].ssUsers}" 
    					binderId="${ssBinder.id}" width="200px"/>
				</td>
			</tr>
			<tr>
				<td class="ss_bold" valign="top"><ssf:nlt tag="general.groups" text="Groups"/></td>
				<td valign="top">
					<ssf:find formName="${ss_dashboard_config_form_name}" formElement="data_groups" 
						type="group" userList="${ssDashboard.beans[ssComponentId].ssGroups}" width="200px"/>
				</td>
			</tr>
			<tr>
				<td colspan="2">
					<ssf:clipboard type="user" formElement="data_users" />
					<c:if test="${!empty ssBinder}">
						<ssf:teamMembers binderId="${ssBinder.id}" formElement="data_users" checkOnLoad="true"/>
						<br/>
						<input type="checkbox" name="data_teamOn" id="data_teamOn" <c:if test="${!empty ssDashboard.beans[ssComponentId].data.teamOn}"> checked="checked" </c:if>/>
						<label for="data_teamOn"><span class="ss_labelLeft"><ssf:nlt tag="presense.include.teamMembers"/></span></label>
					</c:if>						
				</td>
			</tr>
		</table>

	</fieldset>
	
</div>
