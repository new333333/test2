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
<%@ include file="/WEB-INF/jsp/box/init.jsp" %>
<c:if test="${empty ss_box_color}"><c:set var="ss_box_color" value="#CCCCCC" scope="request"/></c:if>
<c:if test="${empty ss_box_canvas_color}"><c:set var="ss_box_canvas_color" value="#FFFFAA" scope="request"/></c:if>
<jsp:useBean id="ss_box_color" type="String" scope="request" />
<jsp:useBean id="ss_box_canvas_color" type="String" scope="request" />
<%
boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", false);
%>
<c:set var="boxColor" value='<%= ParamUtil.get(request, "box_color", ss_box_color) %>' />
<c:set var="boxBgColor" value='<%= ParamUtil.get(request, "box_canvas_color", ss_box_canvas_color) %>' />

</div>
<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
	  </td>
	  <td class="ss_decor-border8"></td>
	  </tr>


<%@ include file="/WEB-INF/jsp/box/box_bottom-ext.jsp" %>
	  <tr>
	  	<td colspan="${ss_boxColCount + 1}">
			<table border="0" cellpadding="0" cellspacing="0" width="100%">
				<tr>
					<td><div class="ss_decor-round-corners-bottom-left"></div></td>
					<td width="100%"><div class="ss_decor-round-corners-bottom-center"></div></td>
					<td><div class="ss_decor-round-corners-bottom-right"></div></td>
				</tr>
			</table>
		</td>
	  </tr>
	</table>
</div>


