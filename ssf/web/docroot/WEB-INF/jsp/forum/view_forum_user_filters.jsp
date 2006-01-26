
<% // User filters %>

<span class="ss_bold"><ssf:nlt tag="filter.filter" text="Filter"/></span>&nbsp;<select>
<option value="">--none--</option>
</select>
<a href="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="build_filter"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:renderURL>"
><span class="ss_fineprint"><ssf:nlt tag="edit" text="edit"/></span></a>