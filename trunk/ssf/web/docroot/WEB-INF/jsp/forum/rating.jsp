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

<c:set var="i_rating" value="0"/>
<c:if test="${!empty ssDefinitionEntry.averageRating.average}">
	<c:set var="i_rating" value="${ssDefinitionEntry.averageRating.average}"/>
</c:if>

<div id="${ss_ratingDivId}" style="margin:0px; padding:0px;">
<table style="border-spacing:0px; border-width:thin;"><tbody><tr>

	<c:if test="${i_rating > 0}">
		<c:forEach var="i" begin="0" end="${i_rating - 1}" step="1">
	
		  <td><a style="text-decoration: none;" 
		    onMouseover="ss_showRating('${i + 1}', '${ssDefinitionEntry.id}');" 
		    onMouseout="ss_clearRating('${i + 1}', '${ssDefinitionEntry.id}');"
		    onClick="ss_saveRating('${i + 1}', '${ssDefinitionEntry.id}');return false;"
		  ><img border="0" id="ss_rating_img_${ssDefinitionEntry.id}_${i + 1}" 
		    <ssf:alt tag="alt.goldStar"/> src="<html:imagesPath/>pics/star_gold.gif"/></a></td>
			<script type="text/javascript">ss_ratingImages['ss_rating_img_${ssDefinitionEntry.id}_${i + 1}'] = "<html:imagesPath/>pics/star_gold.gif";</script>
	
		</c:forEach>
	</c:if>
	
	<c:if test="${i_rating < 5}">
		<c:forEach var="i" begin="${i_rating}" end="4" step="1">
		  <td><a style="text-decoration: none;" 
			    onMouseover="ss_showRating('${i + 1}', '${ssDefinitionEntry.id}');" 
			    onMouseout="ss_clearRating('${i + 1}', '${ssDefinitionEntry.id}');"
			    onClick="ss_saveRating('${i + 1}', '${ssDefinitionEntry.id}');return false;"
			  ><img <ssf:alt tag="alt.grayStar"/> border="0" id="ss_rating_img_${ssDefinitionEntry.id}_${i + 1}" 
			    src="<html:imagesPath/>pics/star_gray.gif"/></a></td>
			<script type="text/javascript">ss_ratingImages['ss_rating_img_${ssDefinitionEntry.id}_${i + 1}'] = "<html:imagesPath/>pics/star_gray.gif";</script>
		</c:forEach>
	</c:if>

<c:if test="${!empty ssDefinitionEntry.averageRating}">
<td class="ss_nowrap">
<c:if test="${ssDefinitionEntry.averageRating.count == 1}">
  <span class="ss_muted_label_small"> (<ssf:nlt tag="popularity.rating.average">
    <ssf:param name="value" value="${ssDefinitionEntry.averageRating.average}"/>
    <ssf:param name="value" value="${ssDefinitionEntry.averageRating.count}"/>
    </ssf:nlt>)
  </span>
</c:if>
<c:if test="${ssDefinitionEntry.averageRating.count != 1}">
  <span class="ss_muted_label_small"> (<ssf:nlt tag="popularity.rating.averages">
    <ssf:param name="value" value="${ssDefinitionEntry.averageRating.average}"/>
    <ssf:param name="value" value="${ssDefinitionEntry.averageRating.count}"/>
    </ssf:nlt>)
  </span>
</c:if>
</td>
</c:if>
<c:if test="${empty ssDefinitionEntry.averageRating}">
<td><span class="ss_muted_label_small"> (<ssf:nlt 
  tag="popularity.rating.none" />)</span></td>
</c:if>
</tr></tbody></table>
</div>
