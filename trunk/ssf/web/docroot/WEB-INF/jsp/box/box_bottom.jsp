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
<%@ include file="/WEB-INF/jsp/box/init.jsp" %>
<c:if test="${empty ss_box_color}"><c:set var="ss_box_color" value="#CCCCCC" scope="request"/></c:if>
<c:if test="${empty ss_box_canvas_color}"><c:set var="ss_box_canvas_color" value="#FFFFAA" scope="request"/></c:if>
<jsp:useBean id="ss_box_color" type="String" scope="request" />
<jsp:useBean id="ss_box_canvas_color" type="String" scope="request" />
<%
boolean brWrapContent = ParamUtil.get(request, "box_br_wrap_content", true);
%>
<c:set var="boxColor" value='<%= ParamUtil.get(request, "box_color", ss_box_color) %>' />
<c:set var="boxBgColor" value='<%= ParamUtil.get(request, "box_canvas_color", ss_box_canvas_color) %>' />

</div>
<c:if test="<%= brWrapContent %>">
  <br>
</c:if>
	  </td>
	  <td class="ss_decor-border8" style="background-color:${boxBgColor};"></td>
	  </tr>


<%@ include file="/WEB-INF/jsp/box/box_bottom-ext.jsp" %>
	  <tr>
	  <td colspan="${ss_boxColCount + 1}" style=" background-color:${boxBgColor};"><div 
	    class="ss_decor-round-corners-bottom3"><div><div></div></div></div></td>
	  </tr>
	</table>
</div>


