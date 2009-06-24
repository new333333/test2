<%
/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
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
