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
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Random" %>
<%
Random rand = new Random();
int rn = rand.nextInt(999999);
%>
<c:if test="${!property_guestOnly || ssUser.shared}">
<div style="padding-bottom: .4em; padding-top: .25em;">
	<span class="ss_labelAbove"><label for="kaptcha-response"><ssf:nlt tag="captcha.label" /></label></span>
	<div style="padding-left: .5em;">
		<div><ssf:nlt tag="captcha.instructions" /></div>
		<table>
			<tr>
				<td>
					<div style="padding-top: .5em; padding-bottom: .5em;">
						<img src="Kaptcha.jpg?dummy=<%= rn %>" id="kaptcha-img">
					</div>
				</td>
			</tr>
			<tr>
				<td>
					<input id="kaptcha-response" name="${property_name}" class="ss_text" type="text" />
					<input id="kaptcha-exists" name="kaptcha-exists" value="true" type="hidden" />
				</td>
			</tr>
			<tr>
				<td>
					<span><ssf:nlt tag="captcha.caseinsensitive" /></span>
				</td>
			</tr>
		</table>
	</div>
</div>
</c:if>
