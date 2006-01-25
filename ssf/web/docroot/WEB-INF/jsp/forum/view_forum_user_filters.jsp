
<% // User filters %>

<select>
<option value="">--none--</option>
<c:forEach var="filter" items="${ssUserFolderProperties}">
</c:forEach>
</select>
<a href="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="build_filter"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:renderURL>"
><span class="ss_fintprint"><ssf:nlt tag="edit" text="edit"/></span></a>