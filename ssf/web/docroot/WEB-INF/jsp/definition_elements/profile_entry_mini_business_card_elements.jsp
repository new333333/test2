<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>


<table>
	<tr>
		<td style="padding: 10px 0 10px 0;" valign="top"> 
			<ssf:buddyPhoto style="ss_thumbnail_gallery ss_thumbnail_small_no_text" 
				photos="${ssDefinitionEntry.customAttributes['picture'].value}" 
				folderId="${ssDefinitionEntry.parentBinder.id}" entryId="${ssDefinitionEntry.id}" />
		</td>

		<c:if test="${!empty propertyValues__elements}">
			<td style="padding: 10px 0 10px 0;" valign="top">
			
				<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
				<table>
				
				<c:forEach var="element" items="${propertyValues__elements}">
				
					<c:if test="${!empty ssDefinitionEntry[element]}">
						<tr>
							<td nowrap="nowrap" valign="top">
								<c:if test="${element == 'name'}">
									  <div id="ss_presenceOptions_${renderResponse.namespace}"></div>
										  <ssf:presenceInfo user="${ssDefinitionEntry}" 
										    showOptionsInline="false" 
										    optionsDivId="ss_presenceOptions_${renderResponse.namespace}"/>
													
								</c:if>
							    <span class="ss_bold"><c:out value="${ssDefinitionEntry[element]}"/></span>
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
