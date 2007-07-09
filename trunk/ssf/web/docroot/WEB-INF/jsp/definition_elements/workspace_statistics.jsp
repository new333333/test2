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
<% //View statistics for all subfolders (only 1. level) which have any one %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<fieldset class="ss_fieldset">
	<legend class="ss_legend"><ssf:nlt tag="project.statistics.title" /></legend>
		<ul class="ss_nobullet">
			<c:if test="${ssConfigJspStyle != 'template'}">		
				<c:forEach var="selection" items="${ssBinder.folders}" varStatus="status">
	  				<c:if test="${!empty selection &&
							!empty selection.id &&
							!empty selection.customAttributes['statistics'] &&
							!empty selection.customAttributes['statistics'].value &&
							!empty selection.customAttributes['statistics'].value.value}">		
			
						<li>
							<a href="<ssf:url 
			  				folderId="${selection.id}" 
			  				action="view_folder_listing">
			  				<ssf:param name="binderId" value="${selection.id}"/>
			  				<ssf:param name="newTab" value="1"/>
			  				</ssf:url>"><c:out value="${selection.title}" escapeXml="false"/></a>
		  				
			  				<c:forEach var="definition" items="${selection.customAttributes['statistics'].value.value}">
			  					<c:if test="${!empty definition.value}">
				  					<c:forEach var="attribute" items="${definition.value}">
				  						<c:if test="${!empty attribute.key && !empty attribute.value}">
					  						<c:if test="${attribute.key == 'status'}">
					  							<ssf:drawStatistic statistic="${attribute.value}" style="coloredBar ss_statusBar" showLabel="true" showLegend="true"/>
					  						</c:if>
				  						</c:if>
				  					</c:forEach>
			  					</c:if>
			  				</c:forEach>
			  			</li>
	  				</c:if>
				</c:forEach>
			</c:if>				
		</ul>
	</fieldset>
