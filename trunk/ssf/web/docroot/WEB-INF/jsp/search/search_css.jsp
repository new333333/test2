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
<style type="text/css">
#ss_tab_content {padding:6px 6px 12px 6px;}
#ss_tabs_container {border-left:1px solid #cccccc;border-right:1px solid #cccccc;border-bottom:1px solid #cccccc;}

#ss_rankings { float: left; width: 215px;}
#ss_content_container {
    float: right;
    width: 508px;
    padding:0px;
    margin:0px;
}
div.ss_searchContainer {background: transparent url(<html:imagesPath/>pics/top_left.gif) no-repeat top left;}
#ss_content { 
	border-left:1px solid #afc8e3; 
	border-right:1px solid #afc8e3;
    border-bottom:1px solid #afc8e3; 
	margin:0px; padding:0px;
}
#ss_searchForm_container {margin:0px; padding:0px;	}
#ss_searchForm_spacer {
	background: #e8eff7 url(<html:imagesPath/>pics/top_border.gif) repeat-x top left;
	margin:0px 0px 0px 5px;
	padding:0px;
	height:5px;
	line-height:1px; 
	font-size:0px;
	border-right:1px solid #afc8e3;
}
#ss_searchForm {
	background: #e8eff7 url(<html:imagesPath/>pics/left_border.gif) repeat-y top left;
	padding:6px;
	border-bottom:1px solid #afc8e3;
}
#ss_searchForm input { width: 150px;}
#ss_searchForm th {text-align:left;padding: 0px 0px 0px 12px;}
#ss_searchForm td {text-align:left;padding: 0px 12px 0px 12px;}
#ss_searchForm h4 {margin:0px 0px 6px 0px;padding:0px;}
#ss_searchResult li {
	border-bottom: 1px solid #cccccc;
	display:block;
	margin:12px 12px 12px 12px;
}
#ss_searchResult {
	margin:12px 0px 0px 0px;
	padding:0px;
}
#ss_searchResult_header {
	border-bottom: 1px solid #afc8e3;	
	margin-bottom:24px;
	padding:6px 24px 6px 12px;
}
#ss_searchResult_numbers {float:left;}
#ss_paginator {float:right;}

div.ss_thumbnail {float: left; width:62px; text-align:center;vertical-align:left;}
div.ss_thumbnail img {width:50px;height:50px;padding:0px margin:0px;}
div.ss_entry {float: left; width:418px;}
.ss_entryTitle {float:left;margin:0px;}
div.ss_more {float: right; width:72px; text-align:right;}
div.ss_entryDetails {  padding:6px;}
div.ss_entryDetails p {margin:0px 0px 6px 0px;}
img.ss_attachment_thumbnail {width:80px;height:74px;padding:0px margin:0px;}
.ss_label {}

.ss_rating_box {
	margin:0px 0px 24px 0px;
	padding:0px;
	width: 215px;
}
.ss_rating_box_title {
	background: transparent url(<html:imagesPath/>pics/sidemenuhead.gif) no-repeat top left;
	margin:0px;
	padding:0px 5px 0px 5px;
	height: 16px;
}
.ss_rating_box_content {
	margin:0px;
	padding: 6px;
	border-left: 1px solid #cccccc;
	border-right: 1px solid #cccccc;
	border-bottom: 1px solid #cccccc;
	background-color: #dbe6f2;
}
img.ss_toogler {float:right;}
div.ss_rating_box_title h4 {float:left;margin:0px;}
div.ss_rating_box_content table {width:200px; border-collapse: collapse; border-spacing: 0;}
div.ss_rating_box_content th {border-bottom: 1px solid #afc8e3;text-align:left;}
div.ss_rating_box_content td {text-align:left;}
div.ss_rating_box_content td.ss_star {padding-left:10px;}
div.ss_rating_box_content h5 {margin:0px; border-bottom: 1px solid #afc8e3; }
div.ss_rating_box_content p {margin:6px 0px 12px 0px; }
span.ss_pageNumber{margin:0px 24px 0px 24px;}
#ss_searchForm_additionalFilters {background-color:#ffffff;}

div.ss_options_container {border-bottom:1px solid #afc8e3; margin:0px 0px 0px 0px;padding:0px 12px 12px 12px;}
div.ss_options {padding: 6px 0px 12px 0px;}
div.ss_options_container h4 {margin:6px 0px 6px 0px;}
a.ss_button {
	background-color: #dbe6f2;
	border: 1px solid #afc8e3;
	padding: 0px 6px 0px 12px;
}
</style>
