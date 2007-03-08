<%@ include file="/WEB-INF/jsp/common/common.jsp" %>
<div id="ss_tags${ss_tagViewNamespace}_${ss_tagDivNumber}" class="ss_muted_tag_cloud">
<c:forEach var="ptag" items="${ssPersonalTags}">
 <span class="ss_muted_cloud_tag"><c:out value="${ptag.name}"/></span>
</c:forEach>
<c:forEach var="tag" items="${ssCommunityTags}">
 <span class="ss_muted_cloud_tag"><c:out value="${tag.name}"/></span>
</c:forEach>
</div>
