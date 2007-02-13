<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.team.domain.DefinableEntity" 
  scope="request" />
<%
	int i_rating = 0;
	if (ssDefinitionEntry.getAverageRating() != null) {
		Float rating = ssDefinitionEntry.getAverageRating().getAverage();
		if (rating != null) i_rating = Math.round(rating);
	}
%>
<div id="${ss_ratingDivId}" style="margin:0px; padding:0px;">
<table style="border-spacing:0px; border-width:thin;"><tbody><tr>
<%
	for (int i = 0; i < i_rating; i++) {
%>
  <td><a style="text-decoration: none;" 
    onMouseover="ss_showRating('<%= i+1 %>', '${ssDefinitionEntry.id}');" 
    onMouseout="ss_clearRating('<%= i+1 %>', '${ssDefinitionEntry.id}');"
    onClick="ss_saveRating('<%= i+1 %>', '${ssDefinitionEntry.id}');return false;"
  ><img border="0" id="ss_rating_img_${ssDefinitionEntry.id}_<%= i+1 %>" 
    src="<html:imagesPath/>pics/star_gold.gif"/></a></td>
<script type="text/javascript">ss_ratingImages['ss_rating_img_${ssDefinitionEntry.id}_<%= i+1 %>'] = "<html:imagesPath/>pics/star_gold.gif";</script>
<%
	}
	for (int i = i_rating; i < 5; i++) {
%>
  <td><a style="text-decoration: none;" 
    onMouseover="ss_showRating('<%= i+1 %>', '${ssDefinitionEntry.id}');" 
    onMouseout="ss_clearRating('<%= i+1 %>', '${ssDefinitionEntry.id}');"
    onClick="ss_saveRating('<%= i+1 %>', '${ssDefinitionEntry.id}');return false;"
  ><img border="0" id="ss_rating_img_${ssDefinitionEntry.id}_<%= i+1 %>" 
    src="<html:imagesPath/>pics/star_gray.gif"/></a></td>
<script type="text/javascript">ss_ratingImages['ss_rating_img_${ssDefinitionEntry.id}_<%= i+1 %>'] = "<html:imagesPath/>pics/star_gray.gif";</script>
<%
	}
%>
<c:if test="${!empty ssDefinitionEntry.averageRating}">
<td>
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
