<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>


<div class="ss_smallRBoxTop2 ss_profileBox2"></div><div class="ss_smallRBoxTop1 ss_profileBox2"></div>
<div class="ss_profileBox2" style="padding: 10px;"><div class="ss_profileBox2" style="">
<table class="ss_minicard_interior">
	<tr>
		<td valign="center" style="padding-left: 3px; padding-top: 3px; padding-bottom: 3px;">
		<a href="<ssf:url action="view_ws_listing"><ssf:param name="binderId" 
			value="${ssDefinitionEntry.creation.principal.parentBinder.id}"/><ssf:param name="entryId" 
			value="${ssDefinitionEntry.creation.principal.id}"/>
	    <ssf:param name="newTab" value="1" />
		</ssf:url>">
			<ssf:buddyPhoto style="ss_thumbnail_standalone ss_thumbnail_standalone_small" 
				photos="${ssDefinitionEntry.customAttributes['picture'].value}" 
				folderId="${ssDefinitionEntry.parentBinder.id}" entryId="${ssDefinitionEntry.id}" />
			</a>
		</td>

		<c:if test="${!empty propertyValues__elements}">
			<td valign="center">
			
				<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
				<table cellpadding="0" cellspacing="0" border="0">
				
				<c:forEach var="element" items="${propertyValues__elements}">
				
					<c:if test="${!empty ssDefinitionEntry[element]}">
						<tr>
							<td nowrap="nowrap" valign="top">
								<c:if test="${element == 'name'}">
									  <div id="ss_presenceOptions_${renderResponse.namespace}"></div>
										  <ssf:presenceInfo user="${ssDefinitionEntry}" 
										    showOptionsInline="false" componentId="${renderResponse.namespace}"
										    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
													
								</c:if>
							    <span class="ss_fineprint"><c:out value="${ssDefinitionEntry[element]}"/></span>
							</td>
						</tr>
					</c:if>
					
					<c:if test="${!empty ssDefinitionEntry.customAttributes[element]}">
						<tr>
							<td valign="top">
							    <span class="ss_bold"><c:out value="${ssDefinitionEntry[element]}"/></span>
							</td>
						</tr>
					</c:if>
				
				</c:forEach>
				
				</table>
				<c:set var="ss_element_display_style" value="" scope="request"/>
			
			</td>
		</c:if>
	</tr>
</table>
</div></div>
<div class="ss_smallRBoxBtm1 ss_profileBox2"></div><div class="ss_smallRBoxBtm2 ss_profileBox2"></div>
