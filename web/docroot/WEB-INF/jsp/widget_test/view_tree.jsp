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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<%@ page import="org.dom4j.Document" %>
<%@ page import="org.dom4j.DocumentHelper" %>
<%@ page import="org.dom4j.DocumentType" %>
<%@ page import="org.dom4j.Element" %>
<%@ page import="java.util.List" %>
<%@ page import="java.util.ListIterator" %>


<jsp:useBean id="wsTree" type="org.dom4j.Document" scope="request" />
<jsp:useBean id="ws2Tree" type="org.dom4j.Document" scope="request" />

<script type="text/javascript">

function t_${renderResponse.namespace}_wsTree_showId(id, obj) {
	alert('You selected "'+obj.innerText+' (id: '+id+') from the first widget.')
	return false
}

function t_${renderResponse.namespace}_ws2Tree_showId(id, obj) {
	alert('You selected "'+obj.innerText+' (id: '+id+') from the second widget.')
	return false
}

</script>

<span align="left">SiteScape Forum Widget Tester -  Tree Widget</span>
<br />
<br />

	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table class="ss_style" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					First tree:
					<div>
						<ssf:tree treeName="wsTree" treeDocument="<%= wsTree %>" />
					</div>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	</table>
	<br>
	<br>
	<table class="ss_style" border="0" cellpadding="0" cellspacing="0" width="95%">
	<tr>
		<td>
			<table class="ss_style" border="0" cellpadding="0" cellspacing="0">
			<tr>
				<td>
					Second tree:
					<div>
						<c:if test="true">
						<ssf:tree treeName="ws2Tree" treeDocument="<%= ws2Tree %>" rootOpen="false" />
						</c:if>
					</div>
				</td>
			</tr>
			</table>
		</td>
	</tr>
	</table>
<br />
<br />
End of page

