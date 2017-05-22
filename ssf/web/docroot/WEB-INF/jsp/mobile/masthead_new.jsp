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
<%@ page import="org.kablink.teaming.ObjectKeys" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="html" tagdir="/WEB-INF/tags/html" %>

<div class="header">

	<script type="text/javascript">
	function ss_setMastheadTitle(title) {
		var titleElem = document.getElementById('mastHeadTitle');
		if ( titleElem != null ) {
			titleElem.innerHTML = title; 
		}
	}
	</script>

	<table cellspacing="0" cellpadding="0" width="100%">
		<tr>
			<td valign="top" align="left" width="1%">
			  <div class="homelink">
				<c:if test="${!ss_hideMiniBlog}">
					  <a href="javascript: ;" 
						onClick="ss_toggleDivVisibility('micro-blog-edit');ss_selectElement('micro-blog-text');return false;"><img src="<html:rootPath/>images/icons/miniblog_40.png" border="0"></a>
				</c:if>
			  </div>
			</td>
			<td valign="middle" align="center">
			  <div id="mastHeadTitle" class="masthead-title">
			  Micro Focus Vibe
			  </div>
			</td>
			<td valign="top" align="right" width="1%">
			  <div class="search">
				<a href="javascript: ;" 
				  onClick="ss_toggleDivVisibility('search-dialog');return false;"><img src="<html:rootPath/>images/icons/search_40.png" border="0"></a>
			  </div>
			</td>
		</tr>
	</table>

</div>

<%@ include file="/WEB-INF/jsp/mobile/search.jsp" %>
<c:if test="${!ss_hideMiniBlog}">
<%@ include file="/WEB-INF/jsp/mobile/miniblog.jsp" %>
</c:if>

<jsp:include page="/WEB-INF/jsp/mobile/logout.jsp" />