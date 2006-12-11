<%
// The dashboard "comments" component
/**
 * Copyright (c) 2006 SiteScape, Inc. All rights reserved.
 *
 * The information in this document is subject to change without notice 
 * and should not be construed as a commitment by SiteScape, Inc.  
 * SiteScape, Inc. assumes no responsibility for any errors that may appear 
 * in this document.
 *
 * Restricted Rights:  Use, duplication, or disclosure by the U.S. Government 
 * is subject to restrictions as set forth in subparagraph (c)(1)(ii) of the
 * Rights in Technical Data and Computer Software clause at DFARS 252.227-7013.
 *
 * SiteScape and SiteScape Forum are trademarks of SiteScape, Inc.
 */
%>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<script type="text/javascript">
function fakePost() {
	document.getElementById('currentComments').innerHTML += 
	  document.getElementById('mycomment').value + '<p>';
	  document.getElementById('mycomment').value = '';
}
</script>
<span class="ss_fineprint ss_light">Displaying 0 of 0 comments<br/>( View All | <a href="#" onClick="ss_showPopupDivCentered('commentForm_${ssComponentId}_<portlet:namespace/>', 'mycomment'); return false;">Add Comment</a> )</span>
<div id="currentComments" style="border-style: ridge; border-color: #cccccc; border-width: 2px; padding: 5px; background-color: #eeeeee;">
<c:out value="${ssDashboard.dashboard.components[ssComponentId].data.comment[0]}" escapeXml="false"/>
</div>
<a class="ss_fineprint" href="#" onClick="ss_showPopupDivCentered('commentForm_${ssComponentId}_<portlet:namespace/>', 'mycomment'); return false;">Add Comment</a>
<div id="commentForm_${ssComponentId}_<portlet:namespace/>" style="display: none; visibility: hidden; position: absolute;">

     <div  style="width: 300px; height: 200px; background: #eeeeee; border: 1px solid #cccccc; padding: 3px;">
	<form onsubmit="fakePost(); ss_cancelPopupDiv('commentForm_${ssComponentId}_<portlet:namespace/>'); return false;">
 
		<table>
			<tr>
				<td colspan="2" align="center">
					Post a comment
			</tr>
			<tr>
				<td>Your comment:</td>
				<td><textarea id="mycomment" cols="30" rows="8"></textarea></td>
			</tr>
			<tr>
				<td colspan="2" align="center">
					<input type="submit" value="Post this comment"/></td>
			</tr>
	</table>
	</form>

     </div>
  </div>
