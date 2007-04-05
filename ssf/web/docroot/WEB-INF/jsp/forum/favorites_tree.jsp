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
<% // Favorites tree %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<%@ page contentType="text/xml; charset=UTF-8" %>
<taconite-root xml:space="preserve">
<%@ include file="/WEB-INF/jsp/common/ajax_status.jsp" %>

<c:if test="${empty ss_ajaxStatus.ss_ajaxNotLoggedIn}">
	<taconite-replace-children contextNodeID="ss_favorites${ss_namespace}" parseInBrowser="true">

<div style="margin:2px;">
<table id="ss_favorites_table${ss_namespace}" cellspacing="0" cellpadding="0" >
<tbody>
<tr>
  <td colspan="2" ></td>
  <td align="right"><a onClick="ss_hideDivFadeOut('ss_favorites_pane${ss_namespace}', 0);return false;"
    ><img border="0" src="<html:imagesPath/>box/close_off.gif"/></a></td>
</tr>
<tr>
<td colspan="3">

<c:if test="${!empty ss_favoritesTree}">
<ssf:tree treeName="favTree${ss_namespace}" treeDocument="${ss_favoritesTree}"
  rootOpen="true" displayStyle="normal" nowrap="true" showIdRoutine="ss_treeShowId"/>
</c:if>

</td>
</tr>

<tr>
<td colspan="3">
<br/>
<c:if test="${!empty ss_favoritesTree && !empty ss_favoritesTreeDelete}">
<ssf:tree treeName="favTreeDelete${ss_namespace}" treeDocument="${ss_favoritesTreeDelete}"
  rootOpen="true" displayStyle="normal" nowrap="true" />
</c:if>

</td>
</tr>

</tbody>
</table>
</div>

	</taconite-replace-children>
</c:if>
</taconite-root>
