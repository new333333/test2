<%
/**
 * The contents of this file are subject to the Common Public Attribution License Version 1.0 (the "CPAL");
 * you may not use this file except in compliance with the CPAL. You may obtain a copy of the CPAL at
 * http://www.opensource.org/licenses/cpal_1.0. The CPAL is based on the Mozilla Public License Version 1.1
 * but Sections 14 and 15 have been added to cover use of software over a computer network and provide for
 * limited attribution for the Original Developer. In addition, Exhibit A has been modified to be
 * consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT WARRANTY OF ANY KIND,
 * either express or implied. See the CPAL for the specific language governing rights and limitations
 * under the CPAL.
 * 
 * The Original Code is ICEcore. The Original Developer is SiteScape, Inc. All portions of the code
 * written by SiteScape, Inc. are Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * 
 * 
 * Attribution Information
 * Attribution Copyright Notice: Copyright (c) 1998-2007 SiteScape, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by ICEcore]
 * Attribution URL: [www.icecore.com]
 * Graphic Image as provided in the Covered Code [web/docroot/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are defined in the CPAL as a
 * work which combines Covered Code or portions thereof with code not governed by the terms of the CPAL.
 * 
 * 
 * SITESCAPE and the SiteScape logo are registered trademarks and ICEcore and the ICEcore logos
 * are trademarks of SiteScape, Inc.
 */
%>
<% // Discussion Workspace binder listing %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<jsp:include page="/WEB-INF/jsp/common/presence_support.jsp" />
<div id="ss_dashboard_content" class="ss_doublecolumn">
<div align="center">
 <div id="ss_folder_inset">
  <div id="ss_column_L" class="ss_dblcolleft">
      <div class="ss_dblcol1">
      
      <!-- Start Left Column -->

		<div id="ss_topic">
			<c:set var="binderCounter" value="0"/> <% // set binder counter to zero (first pass) %>
   				<c:forEach var="binder" items="${ss_binders}">
  					<c:set var="binderCounter" value="${binderCounter + 1}"/>
  					<c:if test="${binder._entityType == 'workspace' || binder._entityType == 'profiles'}">
    				  <c:forEach var="subBinder" items="${ss_bindersSubBinders[binder._docId]}">
      				    <c:set var="binderCounter" value="${binderCounter + 1}"/>
    				  </c:forEach>
  					</c:if>
  
   				</c:forEach>

			<c:set var="binderCounter2" value="0"/>  <% // set binder counter to zero (second pass) %>
			<c:set var="column2Seen" value="0"/>
  			<c:forEach var="binder" items="${ss_binders}">
  			  <c:if test="${binderCounter2 >= (binderCounter/2) && column2Seen == '0'}">
    			<c:set var="column2Seen" value="1"/>
    			</div><!-- end of topic -->
        </div><!-- end of ss_col 1 -->
        <!-- Start Right Column -->
      	<div id="ss_column_R">
      	<div class="ss_dblcol2">
      			<div id="ss_topic">
  			  </c:if>

  			  <c:if test="${binder._entityType == 'workspace'}"><% // Discussion Workspace if statement %>
    			<div id="ss_topic_title" class="ss_disc_th1">
   
    			  <span class="ss_title_th1">
    				<a 
    				  <c:if test="${binder._entityType == 'workspace'}">
      				    href="<ssf:url 
        			    action="view_ws_listing"
        			    binderId="${binder._docId}"/>"
			  		  </c:if>
			  		  <c:if test="${binder._entityType == 'profiles'}">
      					href="<ssf:url 
        				action="view_ws_listing"
        				binderId="${binder._docId}"/>"
    				  </c:if>
    				>
      				${binder.title}
    				</a> 
    			  </span>
    			  <c:set var="ss_binderNumbers1" value="${ss_binderUnseenCounts[binder._docId].count}"/>
		 		  <c:choose>
					<c:when test="${ss_binderNumbers1 == '0'}">
					  <span class="ss_title_count">(<ssf:nlt tag="New"><ssf:param name="value" 
				  		value="${ss_binderUnseenCounts[binder._docId].count}"/></ssf:nlt>) </span>
					</c:when>
					<c:otherwise>
					  <span class="ss_title_count"><a href="">(<ssf:nlt tag="New"><ssf:param name="value" 
				  		value="${ss_binderUnseenCounts[binder._docId].count}"/></ssf:nlt>) </a></span>
					</c:otherwise>
		 		  </c:choose>
    				<br/>
    			</div><!-- end of ss_topic_title -->
    			<div id="ss_topic_desc">
					<ssf:textFormat formatAction="limitedDescription" textMaxWords="50">
		  				<ssf:markup search="${binder}">${binder._desc}</ssf:markup>
		  			</ssf:textFormat>
		  		</div>
    			<c:forEach var="subBinder" items="${ss_bindersSubBinders[binder._docId]}">
      				<c:if test="${subBinder._entityType == 'workspace' || subBinder._entityType == 'profiles'}">
      					<div id="ss_topic_thread" class="ss_disc_sub_th1">
        				  <div style="padding-left:10px;">
        					<span class="ss_title_th1">
        					<a 
          						<c:if test="${subBinder._entityType == 'workspace'}">
            					  href="<ssf:url action="view_ws_listing" binderId="${subBinder._docId}"/>"
          						</c:if>
          						<c:if test="${subBinder._entityType == 'profiles'}">
            					  href="<ssf:url action="view_profile_listing" binderId="${subBinder._docId}"/>"
          						</c:if>
        					>${subBinder.title}
        					</a>
        					</span>
         				    <c:set var="ss_binderNumbers2" value="${ss_binderUnseenCounts[subBinder._docId].count}"/>
		 				  	<c:choose>
							  <c:when test="${ss_binderNumbers2 == '0'}">
							    <span class="ss_title_count">(<ssf:nlt tag="New"><ssf:param name="value" 
				  				  value="${ss_binderUnseenCounts[subBinder._docId].count}"/></ssf:nlt>) </span>
							  </c:when>
							  <c:otherwise>
							    <span class="ss_title_count"><a href="">(<ssf:nlt tag="New"><ssf:param name="value" 
				  				  value="${ss_binderUnseenCounts[subBinder._docId].count}"/></ssf:nlt>) </a></span>
							  </c:otherwise>
		 				    </c:choose>
        				    <br/>
        				    <div><ssf:textFormat formatAction="limitedDescription" textMaxWords="50">
		  							<ssf:markup search="${subBinder}">${subBinder._desc}</ssf:markup>
		  						</ssf:textFormat>
		  					</div>
        				  </div><!-- end of left padding -->
        				</div><!-- end of title -->
      				</c:if>
      				<c:if test="${subBinder._entityType == 'profiles'}">
        				<div style="padding-left:10px;">
        				  <span style="padding-right:10px;">profiles</span>
         
        				  <a href="<ssf:url action="view_profile_listing" binderId="${subBinder._docId}"/>">
          					<span>${subBinder.title}</span>
        				  </a>
        				  <c:set var="ss_binderNumbers3" value="${ss_binderUnseenCounts[subBinder._docId].count}"/>
		 			  	  <c:choose>
							<c:when test="${ss_binderNumbers3 == '0'}">
							  <span class="ss_title_count">(<ssf:nlt tag="New"><ssf:param name="value" 
				  				value="${ss_binderUnseenCounts[subBinder._docId].count}"/></ssf:nlt>) </span>
							</c:when>
						    <c:otherwise>
							  <span class="ss_title_count"><a href="">(<ssf:nlt tag="New"><ssf:param name="value" 
				  				value="${ss_binderUnseenCounts[subBinder._docId].count}"/></ssf:nlt>) </a></span>
						    </c:otherwise>
		 				  </c:choose>
        				  <br/>
        			    </div>
      				</c:if>
     				<c:if test="${subBinder._entityType == 'folder'}">
      				  <div id="ss_topic_thread">
        				<div style="padding-left:10px;">
       						<img src="<html:rootPath/>images/pics/discussion/folder_orange.png" align="absmiddle" <ssf:alt tag="folder"/>/> 
        					<a href="<ssf:url action="view_folder_listing" binderId="${subBinder._docId}"/>">
          					  <span>${subBinder.title}</span>
        					</a>
        					<c:set var="ss_binderNumbers4" value="${ss_binderUnseenCounts[subBinder._docId].count}"/>
		 					<c:choose>
							  <c:when test="${ss_binderNumbers4 == '0'}">
								<span class="ss_title_count">(<ssf:nlt tag="New"><ssf:param name="value" 
				  					value="${ss_binderUnseenCounts[subBinder._docId].count}"/></ssf:nlt>) </span>
							  </c:when>
							  <c:otherwise>
								<span class="ss_title_count"><a href="">(<ssf:nlt tag="New"><ssf:param name="value" 
				  					value="${ss_binderUnseenCounts[subBinder._docId].count}"/></ssf:nlt>) </a></span>
							  </c:otherwise>
		 					</c:choose>
        	
        					<br/>
        			    </div><!-- end of left padding -->
       				  </div><!-- end of topic thread -->
      				</c:if>
      				<c:set var="binderCounter2" value="${binderCounter2 + 1}"/>
    			</c:forEach>
  		</c:if><% // Discussion Workspace if statement end %>
  
  		<c:if test="${binder._entityType == 'folder'}">
  			<div id="ss_topic_title" class="ss_disc_folder_th1">
    			<span class="ss_title_th1">
    			  <a href="<ssf:url action="view_folder_listing" binderId="${binder._docId}"/>">
      				${binder.title}
    			  </a>
    			</span> 
    			<c:set var="ss_binderNumbers5" value="${ss_binderUnseenCounts[binder._docId].count}"/>
		 		<c:choose>
				  <c:when test="${ss_binderNumbers5 == '0'}">
					<span class="ss_title_count">(<ssf:nlt tag="New"><ssf:param name="value" 
				  	  value="${ss_binderUnseenCounts[binder._docId].count}"/></ssf:nlt>) 
				  	</span>
				  </c:when>
				  <c:otherwise>
					<span class="ss_title_count">
						<a href="">(<ssf:nlt tag="New"><ssf:param name="value" 
				  	  		value="${ss_binderUnseenCounts[binder._docId].count}"/></ssf:nlt>) 
				  	  	</a>
				  	</span>
				  </c:otherwise>
		 		</c:choose>
    
    			<br/>
    		</div><!-- end of ss_topic_title -->
    		  <div id="ss_topic_desc"><ssf:textFormat formatAction="limitedDescription" 
		  			textMaxWords="50">
		  				<ssf:markup search="${binder}">${binder._desc}</ssf:markup>
		  			</ssf:textFormat>
		  	  </div>
  		</c:if>
  
  		<br/>
  		<c:set var="binderCounter2" value="${binderCounter2 + 1}"/>


</c:forEach>
</div><!-- end of ss_topic (see top) -->
      </div><!-- end of col2 (right column) -->
      </div><!-- end of ss_col_R (right column placeholder) -->
    </div><!-- end of col left -->
</div><!-- end of inset -->
</div><!-- end of center -->
</div><!-- end of content -->
<div class="ss_clear_float"></div>
