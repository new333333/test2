<% //Business card elements %>
<%@ page import="java.lang.reflect.Method" %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ include file="/WEB-INF/jsp/common/presence_support.jsp" %>


<table>
	<tr>
		<td style="padding: 10px 0 10px 0;"> 
		  <div class="ss_thumbnail_gallery ss_thumbnail_small_no_text"><div>
			  <c:set var="selections" value="${ssDefinitionEntry.customAttributes['picture'].value}" />
			  <c:set var="pictureCount" value="0"/>
			  <c:forEach var="selection" items="${selections}">
			  	<c:if test="${pictureCount == 0}">
					<img border="0" src="<ssf:url 
					    webPath="viewFile"
					    folderId="${ssDefinitionEntry.parentBinder.id}"
					    entryId="${ssDefinitionEntry.id}" >
					    <ssf:param name="fileId" value="${selection.id}"/>
					    <ssf:param name="viewType" value="scaled"/>
					    </ssf:url>" />
				</c:if>
				<c:set var="pictureCount" value="${pictureCount + 1}"/>
			  </c:forEach>
		  </div></div>
		</td>

		<c:if test="${!empty propertyValues__elements}">
			<td style="padding: 10px 0 10px 0;" valign="top">
			
				<c:set var="ss_element_display_style" value="tableAlignLeft" scope="request"/>
				<table>
				
				<c:forEach var="element" items="${propertyValues__elements}">
				
					<c:if test="${!empty ssDefinitionEntry[element]}">
						<tr>
							<td nowrap="nowrap">
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
							<td>
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

<%
//Get the form item being displayed
	request.removeAttribute("propertyValues__elements");
%>

