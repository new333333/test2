<%
/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}_data_pane_${ssTagsType}">
<c:if test="${!empty ssTags}">
<table style="background: transparent;">
<tbody>
<c:forEach var="ptag" items="${ssTags}">
<tr>
  <td style="padding-left:10px;">
    <span class="ss_tags" style="padding-right:10px;"><c:out value="${ptag.name}"/></span>
  </td>
  <td>
    <a class="ss_fineprint ss_linkButton" href="#"
      onClick="ss_deleteTag${ss_tagViewNamespace}('${ptag.id}', '${ss_tagDivNumber}', '${ssEntryId}');return false;"
    ><ssf:nlt tag="button.delete"/></a>
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
