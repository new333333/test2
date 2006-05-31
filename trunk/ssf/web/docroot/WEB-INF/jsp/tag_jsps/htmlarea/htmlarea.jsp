<% // htmlarea editor %>
<%@ include file="/WEB-INF/jsp/common/include.jsp" %>
<%@ page import="java.lang.String" %>
<%
	String id = (String) request.getAttribute("element_id");
	String formName = (String) request.getAttribute("form_name");
	String initText = (String) request.getAttribute("init_text");
	String element_height = (String) request.getAttribute("element_height");
	String element_color = (String) request.getAttribute("element_color");

	String suffix = "_" + formName + "_" + id;
%>
<% //Set the styles needed for the toolbar and buttons %>
<% // one-time stuff (once per page, not once per tag instance) %>
<c:if test="${empty ss_htmlarea_initialized}">
<c:set var="ss_htmlarea_initialized" value="1" scope="request"/>

<% // load support for the html editor %>
<script type="text/javascript">	
var _editor_url = "<html:rootPath />js/htmleditor/";
var _editor_lang = "en";
var _editor_style_sheet = "<html:rootPath/>css/ss_color_theme_${ss_color_theme}.css";
</script>

<style>
.htmlarea .toolbar {
cursor: default;
background: <%= element_color %>;
padding: 1px 1px 2px 1px;
border: 1px solid;
border-color: ButtonHighlight ButtonShadow ButtonShadow ButtonHighlight;
}
.htmlarea .toolbar .button {
background: <%= element_color %>;
color: ButtonText;
border: 1px solid <%= element_color %>;
padding: 1px;
margin: 0px;
width: 18px;
height: 18px;
}
.htmlarea .statusBar {
border: 1px solid;
border-color: ButtonShadow ButtonHighlight ButtonHighlight ButtonShadow;
padding: 2px 4px;
background-color: <%= element_color %>;
color: ButtonText;
font: 11px Tahoma,Verdana,sans-serif;
}
</style>

<script type="text/javascript">
var editorFormArray = new Array;
var editorElementArray = new Array;
var editorIndex = 0;

function ss_htmlarea_err() {
	return true;
}
ss_createOnErrorObj('ss_htmlarea_err', ss_htmlarea_err);
</script>
<script type="text/javascript" src="<html:rootPath />js/htmleditor/htmlarea.js">;
</script>
</c:if>

<script type="text/javascript">	
var editorCallerForm = '<%= formName %>';
var editorCallerElement = '<%= id %>';
editorFormArray[editorIndex] = '<%= formName %>';
editorElementArray[editorIndex++] = '<%= id %>';
</script>

<div style="width: 500px; height: <%= element_height %>px; ">
<textarea name="<%= id %>" id="<%= id %>" 
  style="width: 100%; height: 100%;"><%= initText %></textarea></div>

<script type="text/javascript" defer="1">
var config<%= suffix %> = new HTMLArea.Config();
config<%= suffix %>.width="500px";
config<%= suffix %>.height="<%= element_height %>px";
config<%= suffix %>.statusBar = false;
config<%= suffix %>.pageStyle = "body {font-family: arial; font-size: 12px;}"
config<%= suffix %>.toolbar = [
  ['fontname', 'space',
   'fontsize', 'space',
   'formatblock', 'space',
   'bold', 'italic', 'underline',
   'strikethrough'],
  ['justifyleft', 'justifycenter', 'justifyright', 'justifyfull',
   "insertorderedlist", "insertunorderedlist", "outdent", "indent", "separator",
   "forecolor", "hilitecolor", "separator",
   "inserthorizontalrule", "createlink", "insertimage", "inserttable", 
   "separator", "htmlmode", 'undo', 'redo'
  ]
];

config<%= suffix %>.fontsize = {
    "Default size": "2",
    "1 (8 pt)":  "1",
    "2 (10 pt)": "2",
    "3 (12 pt)": "3",
    "4 (14 pt)": "4",
    "5 (18 pt)": "5",
    "6 (24 pt)": "6",
    "7 (36 pt)": "7"
};

config<%= suffix %>.formatblock = {
    "Normal": "p",
    "Heading 1": "h1",
    "Heading 2": "h2",
    "Heading 3": "h3",
    "Heading 4": "h4",
    "Heading 5": "h5",
    "Heading 6": "h6",
    "Address": "address",
    "Formatted": "pre"
};


config<%= suffix %>.fontname = {
    "Default font": "arial,helvetica,sans-serif", 
    "Arial":		'arial,helvetica,sans-serif',
    "Courier New":	'courier new,courier,monospace',
    "Georgia":		'georgia,times new roman,times,serif',
    "Tahoma":		'tahoma,arial,helvetica,sans-serif',
    "Times New Roman": 'times new roman,times,serif',
    "Verdana":		'verdana,arial,helvetica,sans-serif',
    "impact":		'impact',
    "WingDings":	'wingdings'
};

var ta<%= suffix %> = HTMLArea.getElementById("textarea", '<%= id %>');
var htmlEditor<%= suffix %> = new HTMLArea(ta<%= suffix %>, config<%= suffix %>);
function htmlEditor<%= suffix %>generate() {
    htmlEditor<%= suffix %>.generate();
}
ss_createOnLoadObj('htmlEditor<%= suffix %>generate', htmlEditor<%= suffix %>generate);
htmlEditorUsed = 1;
</script>

