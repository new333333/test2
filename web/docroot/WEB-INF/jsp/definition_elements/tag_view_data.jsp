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
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_data_pane_${ssTagsType}">
<c:if test="${!empty ssTags}">
<table style="background: transparent;">
<tbody>
<c:forEach var="ptag" items="${ssTags}">
<tr>
  <td style="width: 10px; padding-left:10px;">
    
    <c:choose>
    
    <c:when test="${ssTagsType == 'c'}">
    <ssf:ifAccessAllowed binder = "${ssBinder}" operation = "manageTag">    
    <a href="javascript:;"
      onClick="ss_tagDelete('${ss_tagViewNamespace}', '${ptag.id}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ssEntry.entityType}', '${ssEntry.id}');return false;"
    ><img border="0" src="<html:imagesPath/>pics/1pix.gif" class="ss_generic_close"/></a>
    </ssf:ifAccessAllowed>
    </c:when>
    
    <c:otherwise>    
    <a href="javascript:;"
      onClick="ss_tagDelete('${ss_tagViewNamespace}', '${ptag.id}', '${ss_tagDivNumber}', '${ssBinder.id}', '${ssEntry.entityType}', '${ssEntry.id}');return false;"
    ><img border="0" src="<html:imagesPath/>pics/1pix.gif" class="ss_generic_close"/></a>
    </c:otherwise>     
    </c:choose>
    
  </td>
  <td>
    <span class="ss_tags" style="padding-right:10px;"><c:out value="${ptag.name}"/></span>
  </td>
</tr>
</c:forEach>
</tbody>
</table>
</c:if>
<c:if test="${empty ssTags}">
<table style="background: transparent;">
<tbody>
<tr><td colspan="2"><ssf:nlt tag="tags.none" text="--none--"/></td></tr>
</tbody>
</table>
</c:if>
</div>
