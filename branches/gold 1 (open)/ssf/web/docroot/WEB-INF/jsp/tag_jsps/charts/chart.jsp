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
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%
	Object percent = request.getAttribute("percent"); 
	String count = request.getAttribute("count").toString(); 
%>
<c:set var="percent" value="<%= percent %>" scope="request"/>

<div class="ss_chartContainer">
	<div class="ss_total">
		<c:if test="${percent > 0}">
		<div class="ss_bar" style="width:<%= percent %>%;">&nbsp;</div>
		</c:if>
	</div>
	<%= percent %>% <ssf:nlt tag="survey.xvotes"><ssf:param name="value" value="<%= count %>"/></ssf:nlt>
</div>


