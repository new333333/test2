<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<ssf:ifNotFilr>
<c:if test="${!ss_pseudoEntity}">
<c:set var="i_rating" value="0"/>
<c:if test="${!empty ssDefinitionEntry.averageRating.average}">
	<c:set var="i_rating" value="${ssDefinitionEntry.averageRating.average}"/>
</c:if>
<div id="${ss_ratingDivId}" style="margin:0px; padding:0px;">
<table style="border-spacing:0px; border-width:thin;"><tbody><tr>

	<c:if test="${i_rating > 0}">
		<c:forEach var="i" begin="0" end="${i_rating - 1}" step="1">
	
		  <td><a style="text-decoration: none;" 
		    onMouseover="ss_showRating('${i + 1}', '${ssDefinitionEntry.id}', '${ss_namespace}');" 
		    onMouseout="ss_clearRating('${i + 1}', '${ssDefinitionEntry.id}', '${ss_namespace}');"
		    onClick="ss_saveRating('${i + 1}', '${ssDefinitionEntry.id}', '${ss_namespace}');return false;"
		  ><img border="0" id="ss_rating_img_${ssDefinitionEntry.id}_${i + 1}" 
		    <ssf:alt tag="alt.goldStar"/> src="<html:imagesPath/>pics/star_gold.png"
		    onLoad="ss_ratingImages['ss_rating_img_${ssDefinitionEntry.id}_${i + 1}'] = '<html:imagesPath/>pics/star_gold.png';" /></a></td>
	
		</c:forEach>
	</c:if>
	
	<c:if test="${i_rating < 5}">
		<c:forEach var="i" begin="${i_rating}" end="4" step="1">
		  <td><a style="text-decoration: none;" 
			    onMouseover="ss_showRating('${i + 1}', '${ssDefinitionEntry.id}', '${ss_namespace}');" 
			    onMouseout="ss_clearRating('${i + 1}', '${ssDefinitionEntry.id}', '${ss_namespace}');"
			    onClick="ss_saveRating('${i + 1}', '${ssDefinitionEntry.id}', '${ss_namespace}');return false;"
			  ><img <ssf:alt tag="alt.grayStar"/> border="0" id="ss_rating_img_${ssDefinitionEntry.id}_${i + 1}" 
			    src="<html:imagesPath/>pics/star_gray.png"
			    onLoad="ss_ratingImages['ss_rating_img_${ssDefinitionEntry.id}_${i + 1}'] = '<html:imagesPath/>pics/star_gray.png';" /></a></td>
		</c:forEach>
	</c:if>

<c:if test="${!empty ssDefinitionEntry.averageRating}">
  <td class="ss_nowrap">
	<c:if test="${ssDefinitionEntry.averageRating.count == 1}">
  	  <span class="ss_muted_label_small"> <ssf:nlt tag="popularity.rating.average">
    	<ssf:param name="value" value="${ssDefinitionEntry.averageRating.average}"/>
    	<ssf:param name="value" value="${ssDefinitionEntry.averageRating.count}"/>
    	</ssf:nlt>
  	  </span>
	</c:if>
	<c:if test="${ssDefinitionEntry.averageRating.count != 1}">
  	  <span class="ss_muted_label_small"> <ssf:nlt tag="popularity.rating.averages">
    	<ssf:param name="value" value="${ssDefinitionEntry.averageRating.average}"/>
    	<ssf:param name="value" value="${ssDefinitionEntry.averageRating.count}"/>
    	</ssf:nlt>
  	  </span>
	</c:if>
  </td>
</c:if>
<c:if test="${empty ssDefinitionEntry.averageRating}">
  <td><span class="ss_muted_label_small"> <ssf:nlt 
  	tag="popularity.rating.none" /></span></td>
</c:if>
</tr></tbody></table>
</div>
</c:if>
</ssf:ifNotFilr>
