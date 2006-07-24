<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<jsp:useBean id="ssDefinitionEntry" type="com.sitescape.ef.domain.DefinableEntity" 
  scope="request" />
<%
	int i_rating = 0;
	Float rating = ssDefinitionEntry.getRating();
	if (rating != null) i_rating = Math.round(rating);
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
  ><img id="ss_rating_img_${ssDefinitionEntry.id}_<%= i+1 %>" 
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
  ><img id="ss_rating_img_${ssDefinitionEntry.id}_<%= i+1 %>" 
    src="<html:imagesPath/>pics/star_gray.gif"/></a></td>
<script type="text/javascript">ss_ratingImages['ss_rating_img_${ssDefinitionEntry.id}_<%= i+1 %>'] = "<html:imagesPath/>pics/star_gray.gif";</script>
<%
	}
%>
<c:if test="${!empty ssDefinitionEntry.rating}">
<td><span class="ss_italic ss_smallprint"> (<ssf:nlt 
  tag="popularity.rating.average" 
  />: <c:out value="${ssDefinitionEntry.rating}"/>)</span></td>
</c:if>
<c:if test="${empty ssDefinitionEntry.rating}">
<td><span class="ss_italic ss_smallprint"> (<ssf:nlt 
  tag="popularity.rating.none" />)</span></td>
</c:if>
</tr></tbody></table>
</div>
