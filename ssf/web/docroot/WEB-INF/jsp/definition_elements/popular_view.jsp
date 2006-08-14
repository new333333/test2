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
<%@ include file="/WEB-INF/jsp/forum/rating.jsp" %>
</td>
<td>&nbsp;&nbsp;&nbsp;</td>
<td valign="middle" nowrap>
<span class="ss_bold ss_smallprint">
<ssf:nlt tag="popularity.visits" text="Visits"/>:</span>
<c:if test="${!empty ssDefinitionEntry.popularity}">
<span class="ss_smallprint"><c:out value="${ssDefinitionEntry.popularity}"/></span>
</c:if>
<c:if test="${empty ssDefinitionEntry.popularity}">
<span class="ss_smallprint"><ssf:nlt tag="popularity.visits.none" /></span>
</c:if>
</td>
</tr>
</table>
