package com.sitescape.ef.taglib;

import javax.portlet.PortletMode;
import javax.portlet.PortletURL;
import javax.portlet.RenderResponse;
import javax.portlet.WindowState;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.tagext.TagSupport;

import javax.servlet.http.HttpServletRequest;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.Attribute;
import java.util.List;
import java.util.ListIterator;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

import java.util.ArrayList;

import com.sitescape.ef.ObjectKeys;
import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.domain.User;
import com.sitescape.util.GetterUtil;




/**
 * @author Peter Hurley 
 *
 */
public class TreeTag extends TagSupport {
    private String treeName;
    private Document tree;
    private String contextPath;
    private boolean rootOpen = true;
    private boolean allOpen = false;
    private String nodeOpen = "";
    private String highlightNode = "";
    private List multiSelect;
    private String multiSelectPrefix;
    private boolean sortable = false;
    private String commonImg;
    private String className = "";
    private Map images;
    private Map imagesOpen;
	private String displayStyle;
	private int liCount;
    
    
	public int doStartTag() throws JspException {
	    if(treeName == null)
	        throw new JspException("Tree name must be specified");
	    
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

			User user = RequestContextHolder.getRequestContext().getUser();
			displayStyle = user.getDisplayStyle();
			this.contextPath = req.getContextPath();
			if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
		    setCommonImg(contextPath + "/images");
		    
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			sb.append("<script language=\"JavaScript\" src=\"").append(contextPath).append("/js/tree/tree_widget.js\"></script>\n");
			sb.append("<script language=\"JavaScript\">\n");
			sb.append("ssTree_defineBasicIcons('"+contextPath+"/images');\n");
			
			this.liCount = 0;
			int nodeId = 0;
			int parentId = 0;
			List recursedNodes = new ArrayList();

			//Get the starting point of the tree
			Element treeRoot = (Element)tree.getRootElement();
			treeRoot.addAttribute("treeNodeId",String.valueOf(++nodeId));
			treeRoot.addAttribute("treeParentId",String.valueOf(parentId));
			//Mark that the root is the last item in its list
			treeRoot.addAttribute("treeLS","1");
			if (this.rootOpen || this.allOpen || treeRoot.attributeValue("id", "-1").equals(this.nodeOpen)) {
				treeRoot.addAttribute("treeOpen","1");
			} else {
				treeRoot.addAttribute("treeOpen","0");
			}
			List treeRootElements = (List) treeRoot.elements("child");
			if (treeRootElements.size() > 0) {
				treeRoot.addAttribute("treeHasChildren","1");
			} else {
				treeRoot.addAttribute("treeHasChildren","0");
			}
			
			//Build a list of elements starting with the root
			List treeElements = new ArrayList();
			treeElements.add(0,treeRoot);
			
			//Process the tree by traversing each branch all the way to its tips
			//  remembering all elements seen along the way
			while (treeElements.size() > 0) {
				//Process the first branch on the list. Then process its children if any.
				Element currentTreeElement = (Element) treeElements.get(0);
				treeElements.remove(0);

				//Build the node list to be used later
				// nodeId | parentId | img | imgOpen

				//NodeId
				String s_nodeId = currentTreeElement.attributeValue("treeNodeId");
				
				//ParentId
				String s_parentId = currentTreeElement.attributeValue("treeParentId");

				//Image
				String s_imageName = currentTreeElement.attributeValue("image");
				s_imageName = (images.containsKey(s_imageName)) ? s_imageName : "page";
				String s_image = (String)images.get(s_imageName);
				String s_imageOpen = (imagesOpen.containsKey(s_imageName)) ? (String)imagesOpen.get(s_imageName): s_image;

				//If this branch has children, add those to the front of the processing list
				// Then, loop back to process the children befroe continuing with the other branches
				List treeElements2 = (List) currentTreeElement.elements("child");
				if (treeElements2.size() > 0) {
					ListIterator it = treeElements2.listIterator();
					while (it.hasNext()) {
						Element nextTreeElement2 = (Element) it.next();
						nextTreeElement2.addAttribute("treeNodeId",String.valueOf(++nodeId));
						nextTreeElement2.addAttribute("treeParentId",currentTreeElement.attributeValue("treeNodeId"));
						if (it.hasNext()) {
							nextTreeElement2.addAttribute("treeLS","0");
						} else {
							nextTreeElement2.addAttribute("treeLS","1");							
						}
						if (this.allOpen || nextTreeElement2.attributeValue("id", "-1").equals(this.nodeOpen)) {
							nextTreeElement2.addAttribute("treeOpen","1");
							
							//Make sure the parents are all open, too
							Element parentElement = nextTreeElement2.getParent();
							while (parentElement != null || !parentElement.isRootElement()) {
								parentElement.addAttribute("treeOpen", "1");
								if (parentElement.isRootElement()) {break;}
								parentElement = parentElement.getParent();
							}
						} else {
							nextTreeElement2.addAttribute("treeOpen","0");
						}
						List treeElements3 = (List) nextTreeElement2.elements("child");
						if (treeElements3.size() > 0) {
							nextTreeElement2.addAttribute("treeHasChildren","1");
						} else {
							nextTreeElement2.addAttribute("treeHasChildren","0");
						}
					}
					treeElements.addAll(0,treeElements2);
				}
			}
			if (this.sortable) {
				//This tree is sortable. Add the calls to enable this.
				//sb.append("function ss_setSortable_" + treeName + "() {ss_setSortable('" + treeName + "ul');}\n");
				//sb.append("ss_createOnLoadObj('ss_setSortable_" + treeName + "', ss_setSortable_" + treeName + ");\n");
			}
			sb.append("</script>\n\n\n");
			//sb.append("\n<ul class=\"ss_treeWidget_ul\" id=\"" + this.treeName + "ul\">\n");

			sb.append("<div class=\"ss_treeWidget\">\n");
			if (displayStyle != null && displayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				//This user is in accessibility mode, output a flat version of the tree
				//jspOut.print("\n<ul class=\"ss_treeWidget_ul\" id=\"" + this.treeName + "ul\">\n");
				outputTreeNodesFlat(treeRoot, recursedNodes);
				
			} else {
				jspOut.print(sb.toString());
				
				//Output the tree
				outputTreeNodes(treeRoot, recursedNodes);
			}
			sb.append("</div>\n");
			//jspOut.print("\n</ul>\n");
			
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
	    
		return SKIP_BODY;
	}
	
	private void outputTreeNodes(Element e, List recursedNodes) throws JspException {
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

			//Output the element divs
			JspWriter jspOut = pageContext.getOut();
			
			//NodeId
			String s_nodeId = e.attributeValue("treeNodeId");
			
			//ParentId
			String s_parentId = e.attributeValue("treeParentId");
	
			//0 or 1 to indicate that there are no more elements in the list
			String s_ls = e.attributeValue("treeLS");
	
			//Text
			String s_text = e.attributeValue("title");
	
			//id
			String s_id = e.attributeValue("id");
			String titleClass = "";
			if ((s_id != null) && s_id.equals(this.highlightNode)) {
				titleClass = "class='ss_tree_highlight'";
			} else {
				if (!className.equals("")) titleClass = "class='"+className+"'";
			}
	
			//Image
			String s_image = getImage(e.attributeValue("image"));
			String s_imageOpen = getImageOpen(e.attributeValue("image"));
			boolean displayOnly = GetterUtil.getBoolean((String)e.attributeValue("displayOnly"));
			
			//Url = null value means 
			String s_url = (String) e.attributeValue("url");
			if (s_url == null) {
				Element url = e.element("url");
				s_url = "";
				if (url != null && url.attributes().size() > 0) {
					PortletURL portletURL = renderResponse.createActionURL();
					Iterator attrs = url.attributeIterator();
					while (attrs.hasNext()) {
						Attribute attr = (Attribute) attrs.next();
						portletURL.setParameter(attr.getName(), attr.getValue());
					}
					portletURL.setWindowState(WindowState.MAXIMIZED);
					portletURL.setPortletMode(PortletMode.VIEW);
					s_url = portletURL.toString();
				} 
			}
			
			//Write out the divs for this branch
			boolean ls = (s_ls == "1") ? true : false;
			boolean hcn = (e.attributeValue("treeHasChildren") == "1") ? true : false;
			boolean ino = (e.attributeValue("treeOpen") == "1") ? true : false;
	
			//jspOut.print("<li class='ss_treeWidget_li' id='" + this.treeName + "_li" + String.valueOf(this.liCount) + "'>");
			this.liCount++;
			//jspOut.print("<table cellspacing='0' cellpadding='0' style='display:inline;'>\n");
			//jspOut.print("<tr>\n");
			//jspOut.print("<div>");
			if (this.multiSelect != null) {
				if (s_id.equals("")) {
					//jspOut.print("<td><img src='" + this.commonImg + "/pics/1pix.gif' width='10px'></td>\n");
					jspOut.print("<img src='" + this.commonImg + "/pics/1pix.gif' width='15px'>");
				} else {
					String checked = "";
					if (this.multiSelect.contains(s_id)) checked = "checked";
					//jspOut.print("<td><input type='checkbox' class='ss_text' name='" + this.multiSelectPrefix + s_id + "' " + checked + " style='width:15px;'></td>\n");
					jspOut.print("<input type='checkbox' class='ss_text' style='margin:0px; padding:0px; width:15px;' name='" + this.multiSelectPrefix + s_id + "' " + checked + ">");
				}
			}
			//jspOut.print("<td valign='top' nowrap>");
			for (int j = recursedNodes.size() - 1; j >= 0; j--) {
				if ((String) recursedNodes.get(j) != "1") {
					jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("spacer") + "\">");
				} else {
					jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("line") + "\">");
				}
			}
	
			// Line and empty icons
			if (ls) {
				recursedNodes.add(0, "0");
			} else {
				recursedNodes.add(0, "1");
			}
	
			// Write out join icons
			if (hcn) {
				if (ls) {
					String classField = "";
					if (!className.equals("")) classField = "class='"+className+"'";
					jspOut.print("<a "+classField+" href=\"javascript: ;\" ");
					jspOut.print("onClick=\"");
					//jspOut.print(this.treeName);
					jspOut.print("ss_treeToggle('" + this.treeName + "', '" + s_nodeId + "', 1, '"+e.attributeValue("image")+"');\" ");
					jspOut.print("onDblClick=\"");
					//jspOut.print(this.treeName);
					jspOut.print("ss_treeToggleAll('" + this.treeName + "', '" + s_nodeId + "', 1, '"+e.attributeValue("image")+"');\" ");
					jspOut.print("style='text-decoration: none;'>");
					jspOut.print("<img id='" + this.treeName + "join" + s_nodeId + "' class='");
	
					if (ino) {
						jspOut.print("ss_twMinusBottom");	// minus_bottom.gif
					} else {
						jspOut.print("ss_twPlusBottom");    // plus_bottom.gif
					}
	
					jspOut.print("' src='" + this.commonImg + "/pics/1pix.gif'></a>");
				}
				else {
					String classField = "";
					if (!className.equals("")) classField = "class='"+className+"'";
					jspOut.print("<a "+classField+" href=\"javascript: ;\" ");
					jspOut.print("onClick=\"");
					//jspOut.print(this.treeName);
					jspOut.print("ss_treeToggle('" + this.treeName + "', '" + s_nodeId + "', 0, '"+e.attributeValue("image")+"');\" ");
					jspOut.print("onDblClick=\"");
					//jspOut.print(this.treeName);
					jspOut.print("ss_treeToggleAll('" + this.treeName + "', '" + s_nodeId + "', 0, '"+e.attributeValue("image")+"');\" ");
					jspOut.print("style='text-decoration: none;'>");
					jspOut.print("<img id='" + this.treeName + "join" + s_nodeId + "' class='");
	
					if (ino) {
						jspOut.print("ss_twMinus");	// minus.gif
					} else {
						jspOut.print("ss_twPlus");	// plus.gif
					}
	
					jspOut.print("' src='" + this.commonImg + "/pics/1pix.gif'></a>");
				}
			}
			else {
				if (ls) {
					jspOut.print("<img class='ss_twJoinBottom' src='" + this.commonImg + "/pics/1pix.gif'>");
				} else {
					jspOut.print("<img class='ss_twJoin' src='" + this.commonImg + "/pics/1pix.gif'>");
				}
			}
	
			// Link
			if (hcn) {
				jspOut.print("<img class=\"ss_twImg\" id=\"");
				jspOut.print(this.treeName);
				jspOut.print("icon" + s_nodeId + "\" src=\"");
	
				if (ino) {
					jspOut.print(s_imageOpen); // e.g., folder_open.gif
				} else {
					jspOut.print(s_image);	// e.g., folder.gif
				}
	
				jspOut.print("\">");
			}
			else {
				jspOut.print("<img class=\"ss_twImg\" id=\"");
				jspOut.print(this.treeName);
				jspOut.print("icon" + s_nodeId + "\" src=\"" + s_image + "\">");
			}
	
			jspOut.print("&nbsp;");
			if (!displayOnly) {
				String classField = "";
				if (!className.equals("")) classField = "class='"+className+"'";
				jspOut.print("<a "+classField+" href=\"" + s_url + "\" ");
				if (s_id != null && !s_id.equals("")) {
					jspOut.print("onClick=\"if (self."+this.treeName+"_showId) {return "+this.treeName+"_showId('"+s_id+"', this);}\" ");
				}
				jspOut.print(">");
			}
			jspOut.print(s_text);
			
			if (!displayOnly) jspOut.print("</a>");
			jspOut.print("<br>\n");
			
			//jspOut.print("</li>");
	
			// Recurse if node has children
	
			if (hcn) {
				jspOut.print("\n<div class=\"ss_twDiv\" id=\"" + this.treeName + "div" + s_nodeId + "\"");
	
				if (!ino) {
					jspOut.print(" style=\"display: none;\"");
				}
	
				jspOut.print(">\n");
				//jspOut.print("\n<ul class=\"ss_treeWidget_ul\" id=\"" + this.treeName + "ul" + s_nodeId + "\">\n");
	
				ListIterator it2 = e.elements("child").listIterator();
				while (it2.hasNext()) {
					outputTreeNodes((Element) it2.next(), recursedNodes);
				}
	
				//jspOut.print("</ul>\n");
				jspOut.print("</div>\n");
				if (this.sortable) {
					//This tree is sortable. Add the calls to enable this.
					jspOut.print("<script language=\"JavaScript\">\n");
					jspOut.print("function ss_setSortable_" + treeName + "ul" + s_nodeId + "() {ss_setSortable('" + treeName + "ul" + s_nodeId + "');}\n");
					jspOut.print("ss_createOnLoadObj('ss_setSortable_" + treeName + "ul" + s_nodeId + "', ss_setSortable_" + treeName + "ul" + s_nodeId + ");\n");
					jspOut.print("</script>\n");
				}
			}
	
			// Pop last line or empty icon
			recursedNodes.remove(0);
		}
	    catch(Exception ex) {
	        throw new JspException(ex);
	    }
	}

	private void outputTreeNodesFlat(Element e, List recursedNodes) throws JspException {
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();
			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

			//Output the element divs
			JspWriter jspOut = pageContext.getOut();
			
			//NodeId
			String s_nodeId = e.attributeValue("treeNodeId");
			
			//ParentId
			String s_parentId = e.attributeValue("treeParentId");
	
			//0 or 1 to indicate that there are no more elements in the list
			String s_ls = e.attributeValue("treeLS");
	
			//Text
			String s_text = e.attributeValue("title");
	
			//id
			String s_id = e.attributeValue("id");
			String titleClass = className;
			if ((s_id != null) && s_id.equals(this.highlightNode)) titleClass = "ss_tree_highlight";
	
			//Image
			String s_image = getImage(e.attributeValue("image"));
			String s_imageOpen = getImageOpen(e.attributeValue("image"));
			boolean displayOnly = GetterUtil.getBoolean((String)e.attributeValue("displayOnly"));
			
			//Url = null value means 
			String s_url = (String) e.attributeValue("url");
			if (s_url == null) {
				Element url = e.element("url");
				s_url = "";
				if (url != null && url.attributes().size() > 0) {
					PortletURL portletURL = renderResponse.createActionURL();
					Iterator attrs = url.attributeIterator();
					while (attrs.hasNext()) {
						Attribute attr = (Attribute) attrs.next();
						portletURL.setParameter(attr.getName(), attr.getValue());
					}
					portletURL.setWindowState(WindowState.MAXIMIZED);
					portletURL.setPortletMode(PortletMode.VIEW);
					s_url = portletURL.toString();
				} 
			}
			
			//Write out the divs for this branch
			boolean ls = (s_ls == "1") ? true : false;
			boolean hcn = (e.attributeValue("treeHasChildren") == "1") ? true : false;
	
			for (int j = recursedNodes.size() - 1; j >= 0; j--) {
				if ((String) recursedNodes.get(j) != "1") {
					jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("spacer") + "\">");
				} else {
					jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("line") + "\">");
				}
			}
	
			// Line and empty icons
			if (ls) {
				recursedNodes.add(0, "0");
			} else {
				recursedNodes.add(0, "1");
			}
	
			// Write out join icons
			if (ls) {
				jspOut.print("<img class=\"ss_twJoinBottom\"> src=\"" + this.commonImg + "/pics/1pix.gif\"");
			} else {
				jspOut.print("<img class=\"ss_twJoin\"> src=\"" + this.commonImg + "/pics/1pix.gif\"");
			}
	
			// Link
			if (hcn) {
				jspOut.print("<img class=\"ss_twImg\" id=\"");
				jspOut.print(this.treeName);
				jspOut.print("icon" + s_nodeId + "\" src=\"");
				jspOut.print(s_imageOpen); // e.g., folder_open.gif
				jspOut.print("\">");
			}
			else {
				jspOut.print("<img class=\"ss_twImg\" id=\"");
				jspOut.print(this.treeName);
				jspOut.print("icon" + s_nodeId + "\" src=\"" + s_image + "\">");
			}
	
			jspOut.print("&nbsp;");
			if (!displayOnly) {
				String classField = "";
				if (!className.equals("")) classField = "class='"+className+"'";
				jspOut.print("<a "+classField+" href=\"" + s_url + "\" ");
				if (s_id != null && !s_id.equals("")) {
					jspOut.print("onClick=\"if (self."+this.treeName+"_showId) {return "+this.treeName+"_showId('"+s_id+"', this);}\" ");
				}
				jspOut.print(">");
			}
			jspOut.print(s_text);
			
			if (!displayOnly) jspOut.print("</a>");
			jspOut.print("<br>\n");
			
			jspOut.print("</div>\n");
	
			// Recurse if node has children
	
			if (hcn) {
				jspOut.print("\n<div id=\"" + this.treeName + "div" + s_nodeId + "\">\n");
				//jspOut.print("\n<ul class=\"ss_treeWidget_ul\" id=\"" + this.treeName + "ul" + s_nodeId + "\">\n");
	
				ListIterator it2 = e.elements("child").listIterator();
				while (it2.hasNext()) {
					outputTreeNodesFlat((Element) it2.next(), recursedNodes);
				}
				//jspOut.print("</ul>\n");
				jspOut.print("</div>\n");
				if (this.sortable) {
					//This tree is sortable. Add the calls to enable this.
					jspOut.print("<script language=\"JavaScript\">\n");
					jspOut.print("function ss_setSortable_" + treeName + "ul" + s_nodeId + "() {ss_setSortable('" + treeName + "ul" + s_nodeId + "');}\n");
					jspOut.print("ss_createOnLoadObj('ss_setSortable_" + treeName + "ul" + s_nodeId + "', ss_setSortable_" + treeName + "ul" + s_nodeId + ");\n");
					jspOut.print("</script>\n");
				}
			}
	
			// Pop last line or empty icon
			recursedNodes.remove(0);
		}
	    catch(Exception ex) {
	        throw new JspException(ex);
	    }
	}

	public int doEndTag() throws JspException {
		return EVAL_PAGE;
	}
	
	public void setTreeName(String treeName) {
		HttpServletRequest req =
			(HttpServletRequest)pageContext.getRequest();

		RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

		String portletName = renderResponse.getNamespace();
	    this.treeName = "t_" + portletName + "_" + treeName;
	}
	
	public void setTreeDocument(Document tree) {
	    this.tree = tree;
	}
	
	public void setRootOpen(boolean rootOpen) {
	    this.rootOpen = rootOpen;
	}
	
	public void setAllOpen(boolean allOpen) {
	    this.allOpen = allOpen;
	}
	
	public void setNodeOpen(String nodeOpen) {
	    this.nodeOpen = nodeOpen;
	}
	
	public void setHighlightNode(String highlightNode) {
	    this.highlightNode = highlightNode;
	}
	
	public void setMultiSelect(List multiSelect) {
	    this.multiSelect = multiSelect;
	}
	
	public void setMultiSelectPrefix(String multiSelectPrefix) {
	    this.multiSelectPrefix = multiSelectPrefix;
	}
	
	public void setSortable(boolean sortable) {
	    this.sortable = sortable;
	}
	
	public void setCommonImg(String commonImg) {
	    this.commonImg = commonImg;
	    this.images = new HashMap();
	    this.imagesOpen = new HashMap();
	    
	    //Common tree icons
	    images.put("root", "/trees/root.gif");
	    images.put("spacer", "/trees/spacer.gif");
	    images.put("line", "/trees/line.gif");
	    images.put("join", "/trees/join.gif");
	    images.put("join_bottom", "/trees/join_bottom.gif");
	    images.put("minus", "/trees/minus.gif");
	    images.put("minus_bottom", "/trees/minus_bottom.gif");
	    images.put("plus", "/trees/plus.gif");
	    images.put("plus_bottom", "/trees/plus_bottom.gif");
	    
	    //Container icons
	    images.put("folder", "/trees/folder.gif");
	    imagesOpen.put("folder", "/trees/folder_open.gif");
	    images.put("page", "/trees/page.gif");
		
		// File icons		
	    images.put("doc", "/document_library/doc.gif");
	    images.put("pdf", "/document_library/pdf.gif");
	    images.put("ppt", "/document_library/ppt.gif");
	    images.put("rtf", "/document_library/rtf.gif");
	    images.put("sxc", "/document_library/sxc.gif");
	    images.put("sxi", "/document_library/sxi.gif");
	    images.put("sxw", "/document_library/sxw.gif");
	    images.put("txt", "/document_library/txt.gif");
	    images.put("xls", "/document_library/xls.gif");
	    
	}
	
	private String getImage(String image) {
		return (this.images.containsKey(image)) ? this.commonImg + (String)this.images.get(image) : this.commonImg + (String) this.images.get("page");
	}
	
	private String getImageOpen(String image) {
		return (this.imagesOpen.containsKey(image)) ? this.commonImg + (String)this.imagesOpen.get(image) : getImage(image);
	}
}


