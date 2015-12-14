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
<%@ page import="org.kablink.teaming.util.NLT" %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<c:set var="ss_windowTitle" value='<%= NLT.get("window.title.addEntry") %>' scope="request"/>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<ssf:ifadapter>
<body class="ss_style_body tundra">
</ssf:ifadapter>
<div class="ss_pseudoPortal">
<ssf:form titleTag="administration.userAccounts.addUserAccount">

<div class="ss_style ss_portlet">
	<div style="padding:10px;" id="ss_manageUserAccounts">
		<br>
		
		<c:if test="${!empty ssException}">
		  <font color="red">
		    <span class="ss_largerprint"><c:out value="${ssException}"/></span>
		  </font>
		  <br/>
		</c:if>
	
		<div class="wg-tabs margintop3" style="text-align: left; border: 0pt none;">
		  <table>
		    <tr>
			  <td>
				  <div class="wg-tab roundcornerSM on" >
					  <a href="<ssf:url action="add_profile_entry" actionUrl="true">
					    <ssf:param name="binderId" value="${ssFolder.id}" />
					    <ssf:param name="entryType" value="${ssEntryType}" />
					    <ssf:param name="context" value="adminMenu" />
					  </ssf:url>"
					  ><ssf:nlt tag="administration.userAccounts.addUserAccount"/></a>
				  </div>
			  </td>
			  <td>
				  <div class="wg-tab roundcornerSM" >
					  <a href="<ssf:url action="manage_user_accounts" actionUrl="true"/>"
					  ><ssf:nlt tag="administration.userAccounts.disableUserAccount"/></a>
				  </div>
			  </td>
			  <td>
				  <div class="wg-tab roundcornerSM" >
					  <a href="<ssf:url action="import_profiles" actionUrl="true"/>"
					  ><ssf:nlt tag="administration.import.profiles"/></a>
				  </div>
			  </td>
		    </tr>
		  </table>
		</div>
		<div class="ss_clear"></div>
	
		<div id="manageIndexDiv" style="display:block;" class="wg-tab-content marginbottom3">
		<c:set var="ss_do_not_show_form_wrapper" value="true" scope="request" />
		<script type="text/javascript" src="<html:rootPath />js/jsp/tag_jsps/find/find.js"></script>
		<ssf:displayConfiguration configDefinition="${ssConfigDefinition}" 
		  configElement="${ssConfigElement}" 
		  configJspStyle="${ssConfigJspStyle}"
		  processThisItem="true" />
		<c:set var="ss_do_not_show_form_wrapper" value="" scope="request" />
		</div>
	</div>
</div>
</ssf:form>
</div>
<ssf:ifadapter>
</body>
</html>
</ssf:ifadapter>
