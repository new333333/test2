
<% // User filters %>

<select>
<option>filter 1</option>
<option>filter 2 filter 2 filter 2 </option>
<option>filter 3 filter 3 filter 3 filter 3 filter 3 </option>
</select>
<a href="<portlet:renderURL windowState="maximized">
		<portlet:param name="action" value="build_filter"/>
		<portlet:param name="binderId" value="${ssFolder.id}"/>
		</portlet:renderURL>"
><span class="ss_fintprint"><ssf:nlt tag="edit" text="edit"/></span></a>