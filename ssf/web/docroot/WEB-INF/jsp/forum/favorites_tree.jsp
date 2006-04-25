<% // Favorites tree %>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>

<jsp:useBean id="ss_ajaxStatus" type="java.util.Map" scope="request" />
<taconite-root xml:space="preserve">
<%
	if (ss_ajaxStatus.containsKey("ss_ajaxNotLoggedIn")) {
%>
	<taconite-replace contextNodeID="ss_favorites_status_message" parseInBrowser="true">
		<div id="ss_favorites_status_message" style="visibility:hidden; display:none;">error</div>
	</taconite-replace>
<%
	} else {
%>
	<taconite-replace contextNodeID="ss_favorites_status_message" parseInBrowser="true">
		<div id="ss_favorites_status_message" style="visibility:hidden; display:none;">ok</div>
	</taconite-replace>
<%
	}

%>
	<taconite-replace-children contextNodeID="ss_favorites" parseInBrowser="true">

<div style="margin:2px;">
<table id="ss_favorites_table" cellspacing="0" cellpadding="0" >
<tbody>
<tr>
  <td colspan="2" class="ss_bold ss_largerprint"><ssf:nlt tag="favorites" text="Favorites"/></td>
  <td align="right"><a onClick="ss_hideDivFadeOut('ss_favorites_pane', 100);return false;"
    ><img border="0" src="<html:imagesPath/>box/close_off.gif"/></a></td>
</tr>
<tr>
<td colspan="3">

<c:if test="${!empty ss_favoritesTree}">
<ssf:tree treeName="favTree" treeDocument="${ss_favoritesTree}"
  rootOpen="true" displayStyle="sortable" nowrap="true" />
</c:if>

</td>
</tr>

<tr>
<td colspan="3">
<br/>
<c:if test="${!empty ss_favoritesTree && !empty ss_favoritesTreeDelete}">
<ssf:tree treeName="favTreeDelete" treeDocument="${ss_favoritesTreeDelete}"
  rootOpen="true" displayStyle="sortable" nowrap="true" />
</c:if>

</td>
</tr>

</tbody>
</table>
</div>

	</taconite-replace-children>

</taconite-root>
