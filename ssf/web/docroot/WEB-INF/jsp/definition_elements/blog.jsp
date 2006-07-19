<% // Blog view %>
<%@ include file="/WEB-INF/jsp/definition_elements/init.jsp" %>
<%@ page import="java.util.Date" %>
<jsp:useBean id="ssSeenMap" type="com.sitescape.ef.domain.SeenMap" scope="request" />
<style type="text/css">
	body {
		min-width: 730px;         /* 2 x (LC fullwidth + CC padding) + RC fullwidth */
	}

	.blog_container2 {
		padding-right: 200px;     /* RC fullwidth + CC padding */
		overflow: hidden;
	}
	
	.blog_column2 {
		position: relative;
		float: left;
		padding-top: 1em;
		text-align: justify;
		overflow: hidden;
	}
	
	.blog_center_column2 {
        padding: 10px 10px;       /* CC padding */
		width: 100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_right_column2 {
		width: 160px;             /* RC width */
		padding: 0 10px;          /* RC padding */
		margin-right: -100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_container3 {
        padding-left: 220px;      /* LC fullwidth */
		padding-right: 170px;     /* RC fullwidth + CC padding */
		overflow: hidden;
	}
	
	.blog_column3 {
		position: relative;
		float: left;
		padding-top: 1em;
		text-align: justify;
		overflow: hidden;
	}
	
	.blog_center_column3 {
        padding: 10px 10px;       /* CC padding */
		width: 100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_left_column3 {
		width: 200px;             /* LC width */
		padding: 0 10px;          /* LC padding */
		right: 240px;             /* LC fullwidth + CC padding */
		margin-left: -100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_right_column3 {
		width: 130px;             /* RC width */
		padding: 0 10px;          /* RC padding */
		margin-right: -100%;
		padding-bottom: 30001px;     /* X + padding-bottom */
		margin-bottom: -30000px;     /* X */
	}
	
	.blog_footer {
		clear: both;
	}
	
	/*** IE Fix ***/
	* html .blog_left_column3 {
		left: 210px;              /* RC fullwidth */
	}

	.blog_footer-wrapper {
		float: left;
		position: relative;
		width: 100%;
		padding-bottom: 31001px;
		margin-bottom: -31000px;
		background: #FFF;         /*** Same as body background ***/
		overflow: hidden;
	}

	/*** Just for Looks ***/

	.blog_header, .blog_footer {
		padding: 0.3em 0;
		overflow: hidden;
	}

</style>


<script type="text/javascript">
function ss_loadEntry(obj,id) {
	<c:out value="${showEntryMessageRoutine}"/>("<ssf:nlt tag="Loading" text="Loading..."/>");
	highlightLineById(id);
	ss_showForumEntry(obj.href, <c:out value="${showEntryCallbackRoutine}"/>);
	return false;
}

</script>
<div class="folder">
<% // First include the folder tree %>
<%@ include file="/WEB-INF/jsp/definition_elements/folder_list_folders.jsp" %>
</div>
<br>
<div class="folder">
  <table class="ss_blog" width="100%">
    <tr>
      <td class="ss_blog_content" width="80%" valign="top">
		  <c:forEach var="entry" items="${ssBlogEntries}" >
			<div class="ss_blog_content" style="margin:2px 8px 20px 2px;">
			  <ssf:displayConfiguration configDefinition="${entry.value.ssConfigDefinition}" 
			    configElement="${entry.value.ssConfigElement}" 
			    configJspStyle="view"
			    processThisItem="true" 
			    entry="${entry.value.entry}" />
			</div>
		  </c:forEach>
	  </td>
	  <td class="ss_blog_sidebar" width="20%" valign="top">
		<span class="ss_bold"><ssf:nlt tag="blog.calendar"/></span>
		<br>
		<ssf:datepicker id="ss_blog_sidebar_date" 
            popupDivId="ss_blog_sidebar_date_popup"
            formName="filterData" showSelectors="false" 
            initDate="<%= new Date() %>" />
        <br>
        <br>
		<span class="ss_bold"><ssf:nlt tag="blog.archives"/></span>
	  </td>
    </tr>
  </table>
</div>
