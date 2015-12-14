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
<c:set var="slidingTableTableStyle" value=""/>
<c:set var="slidingTableRowStyle" value="ss_table_oddRow"/>
<c:set var="slidingTableRowOddStyle" value="ss_table_oddRow"/>
<c:set var="slidingTableRowEvenStyle" value="ss_table_evenRow"/>
<c:set var="slidingTableColStyle" value=""/>

<div id="ss_folder_table_parent" class="ss_folder">
<ssf:slidingTable id="ss_folder_table" parentId="ss_folder_table_parent" type="sliding" 
	  height="400" folderId="${ssBinder.id}" tableStyle="${slidingTableTableStyle}">
<ssf:slidingTableRow style="${slidingTableRowStyle}" headerRow="true">
  <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="40%">
	    <a href="<ssf:url binderId="${ssBinder.id}" action="${action}" actionUrl="true"><ssf:param 
	    	name="operation" value="save_folder_sort_info"/><ssf:param 
	    	name="ssFolderSortBy" value="_sortTitle"/><c:choose><c:when 
	    	test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}"><ssf:param 
	    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
	    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param
	    	name="wiki_folder_list" value="1"/></ssf:url>"
		
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
		  	<ssf:title tag="title.sort.by.column.desc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.Title") %>' />
		  	</ssf:title>
		  </c:when>
		  <c:otherwise>
		  	<ssf:title tag="title.sort.by.column.asc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.Title") %>' />
		  	</ssf:title>
		  </c:otherwise>
		</c:choose>	
		 ><ssf:nlt tag="folder.column.Title"/>
	    	<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'true'}">
				<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" 
				style="height:8px !important; width:10px !important; line-height:8px !important;"
				src="<html:imagesPath/>pics/menudown.gif"/>
			</c:if>
			<c:if test="${ ssFolderSortBy == '_sortTitle' && ssFolderSortDescend == 'false'}">
				<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.Title") %>' /></ssf:alt> border="0" 
				style="height:8px !important; width:10px !important; line-height:8px !important;"
				src="<html:imagesPath/>pics/menuup.gif"/>
			</c:if>
	    </a>
    </ssf:slidingTableColumn>
    
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="20%">
	    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
	    	name="operation" value="save_folder_sort_info"/><ssf:param 
	    	name="binderId" value="${ssBinder.id}"/><ssf:param 
	    	name="ssFolderSortBy" value="_creatorTitle"/><c:choose><c:when 
	    	test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}"><ssf:param 
	    	name="ssFolderSortDescend" value="true"/></c:when><c:otherwise><ssf:param 
	    	name="ssFolderSortDescend" value="false"/></c:otherwise></c:choose><ssf:param
	    	name="wiki_folder_list" value="1"/></ssf:url>"
		
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
		  	<ssf:title tag="title.sort.by.column.desc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.Author") %>' />
		  	</ssf:title>
		  </c:when>
		  <c:otherwise>
		  	<ssf:title tag="title.sort.by.column.asc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.Author") %>' />
		  	</ssf:title>
		  </c:otherwise>
		</c:choose>
		>
			<ssf:nlt tag="folder.column.Author"/>
		    <c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'true'}">
				<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.Author") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
			</c:if>
			<c:if test="${ ssFolderSortBy == '_creatorTitle' && ssFolderSortDescend == 'false'}">
				<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.Author") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
			</c:if>
	    </a>
    </ssf:slidingTableColumn>
    
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="10%">
        <ssf:nlt tag="folder.column.Comments"/>
    </ssf:slidingTableColumn>
    
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="17%">
	    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
	    	name="operation" value="save_folder_sort_info"/><ssf:param 
	    	name="binderId" value="${ssBinder.id}"/><ssf:param 
	    	name="ssFolderSortBy" value="_lastActivity"/><c:choose><c:when 
	    	test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}"><ssf:param 
	    	name="ssFolderSortDescend" value="false"/></c:when><c:otherwise><ssf:param 
	    	name="ssFolderSortDescend" value="true"/></c:otherwise></c:choose><ssf:param
	    	name="wiki_folder_list" value="1"/></ssf:url>"
		
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
		  	<ssf:title tag="title.sort.by.column.asc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.LastActivity") %>' />
		  	</ssf:title>
		  </c:when>
		  <c:otherwise>
		  	<ssf:title tag="title.sort.by.column.desc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.LastActivity") %>' />
		  	</ssf:title>
		  </c:otherwise>
		</c:choose>
		>
			<ssf:nlt tag="folder.column.LastActivity"/>
		    <c:if test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'true'}">
				<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.LastActivity") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
			</c:if>
			<c:if test="${ ssFolderSortBy == '_lastActivity' && ssFolderSortDescend == 'false'}">
				<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.LastActivity") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
			</c:if>
	    </a>
    </ssf:slidingTableColumn>
    
    <ssf:slidingTableColumn  style="${slidingTableColStyle}" width="13%">
	    <a href="<ssf:url action="${action}" actionUrl="true"><ssf:param 
	    	name="operation" value="save_folder_sort_info"/><ssf:param 
	    	name="binderId" value="${ssBinder.id}"/><ssf:param 
	    	name="ssFolderSortBy" value="_rating"/><c:choose><c:when 
	    	test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'true'}"><ssf:param 
	    	name="ssFolderSortDescend" value="false"/></c:when><c:otherwise><ssf:param 
	    	name="ssFolderSortDescend" value="true"/></c:otherwise></c:choose><ssf:param
	    	name="wiki_folder_list" value="1"/></ssf:url>"
		
		<c:choose>
		  <c:when test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'false'}">
		  	<ssf:title tag="title.sort.by.column.desc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.Rating") %>' />
		  	</ssf:title>
		  </c:when>
		  <c:otherwise>
		  	<ssf:title tag="title.sort.by.column.asc">
		  		<ssf:param name="value" value='<%= NLT.get("folder.column.Rating") %>' />
		  	</ssf:title>
		  </c:otherwise>
		</c:choose>
		><ssf:nlt tag="folder.column.Rating"/>
		    <c:if test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'true'}">
				<img <ssf:alt tag="title.sorted.by.column.desc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.Rating") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menudown.gif"/>
			</c:if>
			<c:if test="${ ssFolderSortBy == '_rating' && ssFolderSortDescend == 'false'}">
				<img <ssf:alt tag="title.sorted.by.column.asc"><ssf:param name="value" 
				value='<%= NLT.get("folder.column.Rating") %>' /></ssf:alt> border="0" src="<html:imagesPath/>pics/menuup.gif"/>
			</c:if>
	    </a>
    </ssf:slidingTableColumn>
  </ssf:slidingTableRow>

<c:forEach var="entry1" items="${ssFolderEntries}" varStatus="status" >
  <jsp:useBean id="entry1" type="java.util.HashMap" />
  <jsp:useBean id="status" type="javax.servlet.jsp.jstl.core.LoopTagStatus" />

<ssf:slidingTableRow style="${slidingTableRowStyle}" 
  oddStyle="${slidingTableRowOddStyle}" evenStyle="${slidingTableRowEvenStyle}" id="${folderLineId}" >

  <ssf:slidingTableColumn style="${slidingTableColStyle}">
	<c:set var="seenStyleburst" value=""/>
	<%
		String folderLineId = "folderLine_" + (String) entry1.get("_docId");
		String seenStyle = " ";
		String seenStyleFine = "ss_finePrint ";
		String seenStyleAuthor = " ";
		if (!ssSeenMap.checkIfSeen(entry1)) {
			seenStyle = "ss_unseen ";
			seenStyleFine = "ss_unseen ss_fineprint ";
			seenStyleAuthor="ss_unseen";
			%><c:set var="seenStyleburst" value="1"/><%
		}
	%>
	  <!-- Sunburst -->
	  <c:if test="${!empty seenStyleburst}">
	  	<a id="ss_sunburstDiv${ssBinder.id}_${entry1._docId}" href="javascript: ;" 
	  		title="<ssf:nlt tag="sunburst.click"/>"
	  		onClick="ss_hideSunburst('${entry1._docId}', '${ssBinder.id}');return false;"
		><img src="<html:rootPath/>images/pics/discussion/sunburst.png" 
		  style="height:12px !important; width:12px !important; line-height:12px !important;"
	  	  align="absmiddle" border="0" <ssf:alt tag="sunburst.click"/> />
	    </a>
	  </c:if>
      <a class="wiki-page-a <c:if test="${entry1._docId == ss_wikiHomepageEntryId}"> wiki-homepage-a</c:if>"
        href="<ssf:url     
          adapter="true" 
          portletName="ss_forum" 
          folderId="${ssFolder.id}" 
          action="view_folder_entry" 
          entryId='<%= entry1.get("_docId").toString() %>' actionUrl="true"><ssf:param
          name="entryViewStyle" value="popup"/><ssf:param
          name="namespace" value="${renderResponse.namespace}"/></ssf:url>" 
    	<c:if test="${!empty entry1._desc}">
    	  onMouseOver="ss_showHoverOver(this, 'ss_folderEntryTitle_${entry1._docId}', event, 20, 4);"
    	  onMouseOut="ss_hideHoverOver('ss_folderEntryTitle_${entry1._docId}');"
    	</c:if>
        onClick="ss_hideSunburst('${entry1._docId}', '${ssBinder.id}');return true;" 		    	
      ><c:if test="${empty entry1.title}"
      ><span id="folderLineSeen_${entry1._docId}" class="<%= seenStyleFine %>"
        >--<ssf:nlt tag="entry.noTitle"/>--</span
      ></c:if><span id="folderLineSeen_${entry1._docId}" class="<%= seenStyle %>"
        ><ssf:textFormat formatAction="limitedCharacters" 
    		textMaxChars="folder.title.charCount"><c:out value="${entry1.title}"/></ssf:textFormat>
        <c:if test="${entry1._docId == ss_wikiHomepageEntryId}">
          <img border="0" align="absmiddle" 
            style="height:16px !important; width:16px !important; line-height:16px !important;" 
            src="<html:rootPath/>images/pics/wiki/home16.png" 
            alt="<ssf:nlt tag="wiki.homePage"/>" style="padding-bottom:4px;">
        </c:if>
      </span></a>
  </ssf:slidingTableColumn>
  <ssf:slidingTableColumn style="${slidingTableColStyle}">
    <ssf:showUser user='<%=(User)entry1.get("_principal")%>' /> 
  </ssf:slidingTableColumn>
  <ssf:slidingTableColumn style="${slidingTableColStyle}">
      <span >${entry1._totalReplyCount}</span>
  </ssf:slidingTableColumn>
  <ssf:slidingTableColumn style="${slidingTableColStyle}">
    <span ><fmt:formatDate timeZone="${ssUser.timeZone.ID}"
     value="${entry1._lastActivity}" type="both" 
	 timeStyle="short" dateStyle="short" /></span>
  </ssf:slidingTableColumn>
  <ssf:slidingTableColumn style="${slidingTableColStyle}">
     <c:if test="${!empty entry1._rating}">
		<span class="ss_nowrap">
			<%
				String iRating = String.valueOf(java.lang.Math.round(Float.valueOf(entry1.get("_rating").toString())));
			%>
			<c:set var="sRating" value="<%= iRating %>"/>
			<c:if test="${sRating > 0}">
				<c:forEach var="i" begin="0" end="${sRating - 1}" step="1">
			
				  <img border="0" 
				    <ssf:alt tag="alt.goldStar"/>
				    src="<html:imagesPath/>pics/star_gold.png"/>
				 
			
				</c:forEach>
			</c:if>
			
			<c:if test="${sRating < 5}">
				<c:forEach var="i" begin="${sRating}" end="4" step="1">
				  <img <ssf:alt tag="alt.grayStar"/> border="0" 
					    src="<html:imagesPath/>pics/star_gray.png" />
				  
				</c:forEach>
			</c:if>
		</span>
     </c:if>&nbsp;
  </ssf:slidingTableColumn>
</ssf:slidingTableRow>
</c:forEach>
</ssf:slidingTable>
</div>

<c:if test="${empty ssFolderEntries}">
  <div style="margin:20px 30px;"><span class="wiki-noentries-panel"><ssf:nlt tag="wiki.no_entries"/></span></div>
</c:if>

<c:if test="${!empty ssFolderEntries}">
	<c:forEach var="entry2" items="${ssFolderEntries}" >
	  <c:if test="${!empty entry2._desc}">
	  <div id="ss_folderEntryTitle_${entry2._docId}" class="ss_hover_over" 
	    style="visibility:hidden; display:none;">
	      <span class="ss_style" >
			  <ssf:textFormat formatAction="limitedDescription" textMaxWords="folder.preview.wordCount">
			    <ssf:markup search="${entry2}">${entry2._desc}</ssf:markup>
			    </ssf:textFormat>
	      </span>
	      <div class="ss_clear"></div>
	  </div>
	  </c:if>
	</c:forEach>
</c:if>
