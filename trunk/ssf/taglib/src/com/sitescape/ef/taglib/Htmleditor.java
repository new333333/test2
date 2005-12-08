/*
 * Created on Apr 7, 2005
 *
 *	Stuff for the HTML editor tag
 * 
 */
package com.sitescape.ef.taglib;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.tagext.TagSupport;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;


/**
 * @author billmers
 *;
 */

// This is a stub so far; it is just a 
// gutted version of datepicker.

public class Htmleditor extends TagSupport {
  private String id;
  private String formName;
  private String initText;
  private String height = null;
  private String color = "ButtonFace";
  private String contextPath;
        
  public int doStartTag() throws JspException {
    JspWriter jspOut = pageContext.getOut(); 
    if (id == null) {
      throw new JspException("You must provide an element name"); 
    }
	    
    try {
      HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
      contextPath = req.getContextPath();
      
      if (height == null) {
          height = "250";
      }
			
      if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);

      StringBuffer sb = new StringBuffer();
      //Set the styles needed for the toolbar and buttons
      sb.append("<style>\n");
      sb.append(".htmlarea .toolbar {\n");
      sb.append("cursor: default;");
      sb.append("background: " + this.color + ";\n");
      sb.append("padding: 1px 1px 2px 1px;\n");
      sb.append("border: 1px solid;\n");
      sb.append("border-color: ButtonHighlight ButtonShadow ButtonShadow ButtonHighlight;\n");
      sb.append("}\n");
      sb.append(".htmlarea .toolbar .button {\n");
      sb.append("background: " + this.color + ";\n");
      sb.append("color: ButtonText;");
      sb.append("border: 1px solid " + this.color + ";\n");
      sb.append("padding: 1px;\n");
      sb.append("margin: 0px;\n");
      sb.append("width: 18px;\n");
      sb.append("height: 18px;\n");
      sb.append("}\n");
      sb.append(".htmlarea .statusBar {\n");
      sb.append("border: 1px solid;\n");
      sb.append("border-color: ButtonShadow ButtonHighlight ButtonHighlight ButtonShadow;\n");
      sb.append("padding: 2px 4px;\n");
      sb.append("background-color: " + this.color + ";\n");
      sb.append("color: ButtonText;\n");
      sb.append("font: 11px Tahoma,Verdana,sans-serif;\n");
      sb.append("}\n");
      sb.append("</style>\n");

      // load support for the html editor
      sb.append("<script type=\"text/javascript\">\n");

      // one-time stuff (once per page, not once per tag instance)
      sb.append("if (!editorInitialized) {\n")
	.append("var editorInitialized = 1;\n");
	
      // one-time stuff goes here
      sb.append("var editorFormArray = new Array;\n");
      sb.append("var editorElementArray = new Array;\n");
      sb.append("var editorIndex = 0;\n");
	

      // end of one-time stuff
      sb.append("}\n");
	
      sb.append("function err() {\n")
	.append("return true;\n")
	.append("}\n");
      sb.append("createOnErrorObj('err', err);\n");

      sb.append("_editor_url = \"")
	.append(contextPath)
	.append("/js/htmleditor/\";\n");
      sb.append("_editor_lang = \"en\";\n");
      sb.append("var editorCallerForm = '")
	.append(formName)
	.append("';\n");
      sb.append("var editorCallerElement = '")
	.append(id)
	.append("';\n");
      sb.append("editorFormArray[editorIndex] = '")
	.append(formName)
	.append("';\n");
      sb.append("editorElementArray[editorIndex++] = '")
	.append(id)
	.append("';\n");
      sb.append("</script>\n");

      sb.append("<div style=\"width: 500px; height: ")
	.append(height)
	.append("px; \">\n");
      sb.append("<textarea name=\"")
	.append(id)
	.append("\" id=\"")
	.append(id)
	.append("\" style=\"width: 100%; height: 100%;\">")
	.append(initText)
	.append("</textarea>\n")
	.append("</div>\n");
	

      sb.append("<script type=\"text/javascript\" src=\"")
	.append(contextPath)
	.append("/js/htmleditor/htmlarea.js\">;\n");
      sb.append("</script>\n");

      String suffix = "_" + formName + "_" + id;
      sb.append("<script type=\"text/javascript\" defer=\"1\">\n");
      sb.append("  var config")
	.append(suffix)
	.append(" = new HTMLArea.Config();\n");
      sb.append("config")
	.append(suffix)
	.append(".width=\"500px\";\n");
      sb.append("config")
	.append(suffix)
	.append(".height=\"")
	.append(height)
	.append("px\";\n");
      sb.append("config")
	.append(suffix)
	.append(".statusBar = false;\n");
      sb.append("config")
	.append(suffix)
	.append(".pageStyle = \"body {font-family: arial; font-size: 12px;}\"\n");
      sb.append("config")
	.append(suffix)
	.append(".toolbar = [\n");
      sb.append("  ['fontname', 'space',\n");
      sb.append("   'fontsize', 'space',\n");
      sb.append("   'formatblock', 'space',\n");
      sb.append("   'bold', 'italic', 'underline',\n");
      sb.append("   'strikethrough'],\n");
      sb.append("  ['justifyleft', 'justifycenter', 'justifyright', 'justifyfull',\n");
      sb.append("   \"insertorderedlist\", \"insertunorderedlist\", \"outdent\", \"indent\", \"separator\",\n");
      sb.append("   \"forecolor\", \"hilitecolor\", \"separator\",\n");
      sb.append("   \"inserthorizontalrule\", \"createlink\", \"insertimage\", \"inserttable\", \n");
      sb.append("   \"separator\", \"htmlmode\", 'undo', 'redo'\n");
      sb.append("  ]\n");
      sb.append("];\n");

      sb.append("config")
	.append(suffix)
	.append(".fontsize = {\n");
      sb.append("    \"Default size\": \"2\",\n");
      sb.append("    \"1 (8 pt)\":  \"1\",\n");
      sb.append("    \"2 (10 pt)\": \"2\",\n");
      sb.append("    \"3 (12 pt)\": \"3\",\n");
      sb.append("    \"4 (14 pt)\": \"4\",\n");
      sb.append("    \"5 (18 pt)\": \"5\",\n");
      sb.append("    \"6 (24 pt)\": \"6\",\n");
      sb.append("    \"7 (36 pt)\": \"7\"\n");
      sb.append("};\n");

      sb.append("config")
	.append(suffix)
	.append(".formatblock = {\n");
      sb.append("    \"Normal\": \"p\",\n");
      sb.append("    \"Heading 1\": \"h1\",\n");
      sb.append("    \"Heading 2\": \"h2\",\n");
      sb.append("    \"Heading 3\": \"h3\",\n");
      sb.append("    \"Heading 4\": \"h4\",\n");
      sb.append("    \"Heading 5\": \"h5\",\n");
      sb.append("    \"Heading 6\": \"h6\",\n");
      sb.append("    \"Address\": \"address\",\n");
      sb.append("    \"Formatted\": \"pre\"\n");
      sb.append("};\n");


      sb.append("config")
	.append(suffix)
	.append(".fontname = {\n");
      sb.append("    \"Default font\": \"arial,helvetica,sans-serif\", \n");
      sb.append("    \"Arial\":	   'arial,helvetica,sans-serif',\n");
      sb.append("    \"Courier New\":	   'courier new,courier,monospace',\n");
      sb.append("    \"Georgia\":	   'georgia,times new roman,times,serif',\n");
      sb.append("    \"Tahoma\":	   'tahoma,arial,helvetica,sans-serif',\n");
      sb.append("    \"Times New Roman\": 'times new roman,times,serif',\n");
      sb.append("    \"Verdana\":	   'verdana,arial,helvetica,sans-serif',\n");
      sb.append("    \"impact\":	   'impact',\n");
      sb.append("    \"WingDings\":	   'wingdings'\n");
      sb.append("};\n");

      sb.append("var ta")
	.append(suffix)
	.append(" = HTMLArea.getElementById(\"textarea\", '")
	.append(id)
	.append("');\n");
      sb.append("var htmlEditor")
	.append(suffix)
	.append(" = new HTMLArea(ta")
	.append(suffix)
	.append(", config")
	.append(suffix)
	.append(");\n");
      sb.append("htmlEditor")
	.append(suffix)
	.append(".generate();\n");
      sb.append("htmlEditorUsed = 1;\n");
      sb.append("</script>\n");

      jspOut.print(sb.toString());
    }
    catch (Exception e) {
      throw new JspException(e);
    }
    return SKIP_BODY;
  }

  public int doEndTag() throws JspException {
      this.initText = null;
      return SKIP_BODY;
  }

  
  public void setId(String id) {
    this.id = id;
  }

  public void setInitText(String initText) {
      this.initText = initText;
    }

  public void setHeight(String height) {
    this.height = height;
  }

  public void setColor(String color) {
    this.color = color;
  }

  public void setFormName(String formName) {
    this.formName = formName;
  }

}
