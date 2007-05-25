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
<c:if test="${empty ss_ratingSupportLoaded}">
<script type="text/javascript">
var ss_ratingImages = new Array();
var ss_ratingRedStar = "<html:imagesPath/>pics/star_red.gif";
var ss_ratings_info = new Array();
var ss_currentRatingInfoId = "";
var ss_saveRatingUrl = "<ssf:url 
	adapter="true" 
	portletName="ss_forum" 
	action="__ajax_request" 
	actionUrl="true" >
	<ssf:param name="operation" value="save_rating" />
	</ssf:url>";
var ss_binderId = "${ssBinder.id}";
var ss_confirmDeleteEntryText = "<ssf:nlt tag="entry.confirmDeleteEntry"/>";
var ss_confirmUnlockEntryText = "<ssf:nlt tag="entry.confirmUnlockEntry"/>";

ss_ratings_info[1] = "<ssf:nlt tag="popularity.rating.1star" />"
ss_ratings_info[2] = "<ssf:nlt tag="popularity.rating.2stars" />"
ss_ratings_info[3] = "<ssf:nlt tag="popularity.rating.3stars" />"
ss_ratings_info[4] = "<ssf:nlt tag="popularity.rating.4stars" />"
ss_ratings_info[5] = "<ssf:nlt tag="popularity.rating.5stars" />"
</script>
<script type="text/javascript" src="<html:rootPath/>js/forum/ss_entry.js"></script>
<div id="ss_rating_info_div" 
  style="position:absolute; display:none; visibility:hidden;
  border:1px solid black; padding:4px; background-color:#ffffff;">
<span><ssf:nlt tag="popularity.rating.register"/></span>:
<span id="ss_rating_info"></span>
</div>
<c:set var="ss_ratingSupportLoaded" value="1" scope="request"/>
</c:if>
<c:set var="ss_ratingDivId" value="ss_rating_div_${ssDefinitionEntry.id}" 
  scope="request"/>
<table cellspacing="0" cellpadding="0">
<tr>
<td valign="middle" nowrap>
<ssHelpSpot helpId="tools/rating" offsetX="0" 
  title="<ssf:nlt tag="helpSpot.rating"/>"></ssHelpSpot>
<%@ include file="/WEB-INF/jsp/forum/rating.jsp" %>
</td>
<td>&nbsp;&nbsp;&nbsp;</td>
<td valign="middle" nowrap>
<c:if test="${!empty ssDefinitionEntry.popularity}">
<span class="ss_muted_label_small"><c:out value="${ssDefinitionEntry.popularity}"/> 
<ssf:nlt tag="popularity.visits" text="visits"/></span>
</c:if>
<c:if test="${empty ssDefinitionEntry.popularity}">
<span class="ss_muted_label_small"><ssf:nlt tag="popularity.visits.none" /></span>
</c:if>
</td>
</tr>
</table>
