<% // Default view if the forum has not been set up yet %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>

<div class="ss_style ss_portlet">
<span><i>[This forum has not been configured yet.]</i></span>
<br/>
<br/>
<table class="ss_style" border="0" cellpadding="4" cellspacing="0" width="100%">
<th align="left">Forum administration menu</th>
<tr><td></td></tr>
<tr>
	<td>
		<ul>
			<li>
				<a class="gamma" 
				  href="<portlet:renderURL windowState="maximized">
				  	<portlet:param name="action" value="configure_forum" />
				  	<portlet:param name="binderId" value="${ssBinder.id}" />
		    		</portlet:renderURL>">Configure</a>
			</li>
			<li>
				<a class="gamma" 
				  href="<portlet:actionURL windowState="maximized">
				  <portlet:param name="action" value="definition_builder" />
				  	<portlet:param name="binderId" value="${ssBinder.id}"/>
		    		</portlet:actionURL>">Definition builder</a>
			</li>
		</ul>
	</td>
</tr>
</table>

</div>


