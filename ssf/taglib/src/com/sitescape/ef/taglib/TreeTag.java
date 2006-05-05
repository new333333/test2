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
import com.sitescape.ef.portletadapter.AdaptedPortletURL;
import com.sitescape.ef.util.NLT;
import com.sitescape.ef.web.WebKeys;
import com.sitescape.util.GetterUtil;




/**
 * @author Peter Hurley 
 *
 */
public class TreeTag extends TagSupport {
    private String treeName;
    private String topId;
    private String startingId;
    private Document tree;
    private String contextPath;
    private boolean rootOpen = true;
    private boolean allOpen = false;
    private String nodeOpen = "";
    private String highlightNode = "";
    private List multiSelect;
    private String multiSelectPrefix;
    private String displayStyle;
    private boolean nowrap = false;
    private String commonImg;
    private String className = "";
    private Map images;
    private Map imagesOpen;
	private String userDisplayStyle;
	private boolean tableOpened = false;
	private boolean startingIdSeen = false;
	private boolean finished = false;
	private String lastListStyle = "";
    
    
	public int doStartTag() throws JspException {
	    if(treeName == null)
	        throw new JspException("Tree name must be specified");
	    this.finished = false;
	    this.tableOpened = false;
	    this.startingIdSeen = false;
	    this.lastListStyle = "";
	    if (this.displayStyle == null) this.displayStyle = "";
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

			User user = RequestContextHolder.getRequestContext().getUser();
			this.userDisplayStyle = user.getDisplayStyle();
			this.contextPath = req.getContextPath();
			if (contextPath.endsWith("/")) contextPath = contextPath.substring(0,contextPath.length()-1);
		    setCommonImg(contextPath + "/images");
			AdaptedPortletURL adapterUrl = new AdaptedPortletURL(req, "ss_forum", Boolean.parseBoolean("true"));
			adapterUrl.setParameter(WebKeys.ACTION, WebKeys.FORUM_AJAX_REQUEST);
			adapterUrl.setParameter(WebKeys.URL_OPERATION, WebKeys.FORUM_OPERATION_WORKSPACE_TREE);
			if (this.topId != null && !this.topId.equals("")) {
				adapterUrl.setParameter(WebKeys.URL_OPERATION2, this.topId);
			}
			String aUrl = adapterUrl.toString().replaceAll("&", "&amp;");
		    
			JspWriter jspOut = pageContext.getOut();
			StringBuffer sb = new StringBuffer();
			if (this.startingId == null || this.startingId.equals("")) {
				sb.append("<script type=\"text/javascript\" src=\"").append(contextPath).append("/js/tree/tree_widget.js\"></script>\n");
				sb.append("<script type=\"text/javascript\">\n");
				sb.append("ssTree_defineBasicIcons('"+contextPath+"/images');\n");
				sb.append("var ss_treeAjaxUrl_" + this.treeName + " = '" + aUrl + "';\n");
				sb.append("var ss_treeNotLoggedInMsg = '" + NLT.get("general.notLoggedIn") + "';\n");
			}
			
			List recursedNodes = new ArrayList();

			//Get the starting point of the tree
			Element treeRoot = (Element)tree.getRootElement();
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
				treeRoot.addAttribute("treeHasHiddenChildren","0");
			} else {
				treeRoot.addAttribute("treeHasChildren","0");
				if (treeRoot.attributeValue("hasChildren", "").equalsIgnoreCase("true")) {
					treeRoot.addAttribute("treeHasHiddenChildren","1");
				} else {
					treeRoot.addAttribute("treeHasHiddenChildren","0");
				}
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

				//If this branch has children, add those to the front of the processing list
				// Then, loop back to process the children befroe continuing with the other branches
				List treeElements2 = (List) currentTreeElement.elements("child");
				if (treeElements2.size() > 0) {
					ListIterator it = treeElements2.listIterator();
					while (it.hasNext()) {
						Element nextTreeElement2 = (Element) it.next();
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
							nextTreeElement2.addAttribute("treeHasHiddenChildren","0");
						} else {
							nextTreeElement2.addAttribute("treeHasChildren","0");
							if (nextTreeElement2.attributeValue("hasChildren", "").equalsIgnoreCase("true")) {
								nextTreeElement2.addAttribute("treeHasHiddenChildren","1");
							} else {
								nextTreeElement2.addAttribute("treeHasHiddenChildren","0");
							}
						}
					}
					treeElements.addAll(0,treeElements2);
				}
			}
			if (this.startingId == null || this.startingId.equals("")) {
				sb.append("</script>\n\n\n");
			}

			if (this.startingId == null || this.startingId.equals("")) {
				sb.append("<div class=\"ss_treeWidget\">\n");
			}
			if (userDisplayStyle != null && userDisplayStyle.equals(ObjectKeys.USER_DISPLAY_STYLE_ACCESSIBLE)) {
				//This user is in accessibility mode, output a flat version of the tree
				outputTreeNodesFlat(treeRoot, recursedNodes);
				
			} else {
				jspOut.print(sb.toString());
				
				//Output the tree
				outputTreeNodes(treeRoot, recursedNodes);
				
				//Close the sortable table if needed
				if (this.tableOpened) {
					jspOut.print("<li></li></ul>\n</td>\n</tr>\n</tbody>\n</table>\n");
					this.tableOpened = false;
				}
			}
			if (this.startingId == null || this.startingId.equals("")) {
				jspOut.print("</div>\n");
			}
		}
	    catch(Exception e) {
	        throw new JspException(e);
	    }
	    finally {
	    	this.nowrap = false;
	    }
	    
		return SKIP_BODY;
	}
	
	private void outputTreeNodes(Element e, List recursedNodes) throws JspException {
		//If processing is finished, just exit.
		if (this.finished) return;
		
		try {
			HttpServletRequest req = (HttpServletRequest) pageContext.getRequest();

			RenderResponse renderResponse = (RenderResponse) req.getAttribute("javax.portlet.response");

			//Output the element divs
			JspWriter jspOut = pageContext.getOut();
			
			//0 or 1 to indicate that there are no more elements in the list
			String s_ls = e.attributeValue("treeLS");
	
			//Text
			String s_text = e.attributeValue("title");
			if (this.nowrap) s_text = "<nobr>" + s_text + "</nobr>";
	
			//id
			String s_id = e.attributeValue("id", "");
			String s_parentId = e.attributeValue("parentId", "");
			String titleClass = "";
			if (!s_id.equals("") && s_id.equals(this.highlightNode)) {
				titleClass = "class=\"ss_tree_highlight\"";
			} else {
				if (!className.equals("")) titleClass = "class=\""+className+"\"";
			}
	
			//Image
			String s_image = getImage(e.attributeValue("image"));
			String s_imageOpen = getImageOpen(e.attributeValue("image"));
			String s_imageClass = e.attributeValue("imageClass", "ss_twImg");
			boolean displayOnly = GetterUtil.getBoolean((String)e.attributeValue("displayOnly"));
			
			//Url (if any)
			String s_url = (String) e.attributeValue("url");
			if (s_url == null) {
				//Look to see if there are url attributes to build the url
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
			s_url = s_url.replaceAll("&", "&amp;");
			
			//Write out the divs for this branch
			boolean ls = (s_ls == "1") ? true : false;
			boolean hcn = (e.attributeValue("treeHasChildren") == "1") ? true : false;
			boolean hhcn = (e.attributeValue("treeHasHiddenChildren") == "1") ? true : false;
			boolean ino = (e.attributeValue("treeOpen") == "1") ? true : false;

			//Show the tree in the requested style (sortable or normal)
			if (this.displayStyle.equals("sortable")) {
				//"sortable" style shows the tree in a series of lists
				String listStyle = e.attributeValue("listStyle", "ss_sortable");
				if (this.tableOpened && !this.lastListStyle.equals("") && !this.lastListStyle.equals(listStyle)) {
					//Switching styles, so close the table
					jspOut.print("</ul>\n</td>\n</tr>\n</tbody>\n</table>\n");
					this.tableOpened = false;
				}

				if (!this.tableOpened) {
					jspOut.print("<table class=\"ss_sortable\">\n<tbody>\n<tr>\n<td>\n");
					this.tableOpened = true;
				
					if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
						for (int j = recursedNodes.size() - 1; j >= 0; j--) {
							jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("spacer") + "\"/>");
						}
						jspOut.print("</td>\n");
						jspOut.print("<td>\n");
					}
					
					// Link
					jspOut.print("<ul id=\"ul_"+s_id+"\"");
					jspOut.print(" class=\"ss_dragableLink "+listStyle+"\">\n");
				}
				recursedNodes.add(0, "0");
				
				jspOut.print("<li id=\""+s_id+"\">");
				
				// Write out plus/minus icons
				if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
					if (hcn || hhcn) {
						String classField = "";
						if (!className.equals("")) classField = "class=\""+className+"\"";
						jspOut.print("<a "+classField+" href=\"javascript: ;\" ");
						jspOut.print("onClick=\"");
						jspOut.print("ss_treeToggle('" + this.treeName + "', '" + s_id + "', 2, '"+e.attributeValue("image")+"');\" ");
						jspOut.print("onDblClick=\"");
						jspOut.print("ss_treeToggleAll('" + this.treeName + "', '" + s_id + "', 2, '"+e.attributeValue("image")+"');\" ");
						jspOut.print("style=\"text-decoration: none;\">");
						jspOut.print("<img id=\"" + this.treeName + "join" + s_id + "\" class=\"");
		
						if (ino) {
							jspOut.print("ss_minus");	// minus.gif
						} else {
							jspOut.print("ss_plus");	// plus.gif
						}
		
						jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/></a>");
					}
				}
				if (!displayOnly) {
					String classField = "";
					if (!className.equals("")) classField = "class=\""+className+"\"";
					jspOut.print("<a "+classField+" href=\"" + s_url + "\" ");
					if (s_id != null && !s_id.equals("")) {
						jspOut.print("onClick=\"if (self."+this.treeName+"_showId) {return "+this.treeName+"_showId('"+s_id+"', this);}\" ");
					}
					jspOut.print(">");
				}
				jspOut.print("<span " + titleClass + ">");
				jspOut.print(s_text);
				jspOut.print("</span>");
				
				if (!displayOnly) jspOut.print("</a>");
				jspOut.print("</li>\n");
				
				//See if this is the starting id
				if (this.startingId != null && this.startingId.equals(s_id)) this.startingIdSeen = true;
				
				// Recurse if node has children
				if (hcn || hhcn || 
						(!this.lastListStyle.equals("") && !this.lastListStyle.equals(listStyle))) {
					//Close this table and reset listStyle so the next item is in its own table
					if (this.tableOpened) {
						jspOut.print("</ul>\n</td>\n</tr>\n</tbody>\n</table>\n");
						this.tableOpened = false;
					}
					listStyle = "";
				}
				this.lastListStyle = listStyle;
				if (hcn) {
					boolean divHasBeenOutput = false;
					if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
						jspOut.print("\n<div class=\"ss_twDiv\" id=\"" + this.treeName + "div" + s_id + "\"");
			
						if (!ino) {
							jspOut.print(" style=\"display: none;\"");
						}
			
						jspOut.print(">\n");
						divHasBeenOutput = true;
					}
		
					ListIterator it2 = e.elements("child").listIterator();
					while (it2.hasNext()) {
						outputTreeNodes((Element) it2.next(), recursedNodes);
					}
		
					if (this.tableOpened) {
						jspOut.print("<li></li></ul>\n</td>\n</tr>\n</tbody>\n</table>\n");
						this.tableOpened = false;
					}
					if (divHasBeenOutput) {
						jspOut.print("</div>\n");
					}
				} else if (hhcn) {
					if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
						jspOut.print("\n<div id=\"" + this.treeName + "temp" + s_id + "\"></div>\n");
					}
				}
		
				// Pop last line or empty icon
				recursedNodes.remove(0);

				// See if it is time to stop
				if (this.startingId != null && this.startingId.equals(s_id)) {
					this.finished = true;
				}
			
			
			} else {
				//The normal tree view
				if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
					if (this.multiSelect != null) {
						if (s_id.equals("")) {
							jspOut.print("<img src=\"" + this.commonImg + "/pics/1pix.gif\" width=\"15px\"/>");
						} else {
							String checked = "";
							if (this.multiSelect.contains(s_id)) checked = "checked=\"checked\"";
							jspOut.print("<input type=\"checkbox\" class=\"ss_text\"");
							jspOut.print(" style=\"margin:0px; padding:0px; width:15px;\" name=\"");
							jspOut.print(this.multiSelectPrefix + s_id + "\" " + checked + "/>");
						}
					}
					for (int j = recursedNodes.size() - 1; j >= 0; j--) {
						if ((String) recursedNodes.get(j) != "1") {
							jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("spacer") + "\"/>");
						} else {
							jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("line") + "\"/>");
						}
					}
				}
			
				// Line and empty icons
				if (ls) {
					recursedNodes.add(0, "0");
				} else {
					recursedNodes.add(0, "1");
				}
			
				if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
					// Write out join icons
					if (hcn || hhcn) {
						if (ls) {
							String classField = "";
							if (!className.equals("")) classField = "class=\""+className+"\"";
							jspOut.print("<a "+classField+" href=\"javascript: ;\" ");
							jspOut.print("onClick=\"");
							jspOut.print("ss_treeToggle('" + this.treeName + "', '" + s_id + "', 1, '"+e.attributeValue("image")+"');\" ");
							jspOut.print("onDblClick=\"");
							jspOut.print("ss_treeToggleAll('" + this.treeName + "', '" + s_id + "', 1, '"+e.attributeValue("image")+"');\" ");
							jspOut.print("style=\"text-decoration: none;\">");
							jspOut.print("<img id=\"" + this.treeName + "join" + s_id + "\" class=\"");
			
							if (ino) {
								jspOut.print("ss_twMinusBottom");	// minus_bottom.gif
							} else {
								jspOut.print("ss_twPlusBottom");    // plus_bottom.gif
							}
			
							jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/></a>");
						}
						else {
							String classField = "";
							if (!className.equals("")) classField = "class=\""+className+"\"";
							jspOut.print("<a "+classField+" href=\"javascript: ;\" ");
							jspOut.print("onClick=\"");
							jspOut.print("ss_treeToggle('" + this.treeName + "', '" + s_id + "', 0, '"+e.attributeValue("image")+"');\" ");
							jspOut.print("onDblClick=\"");
							jspOut.print("ss_treeToggleAll('" + this.treeName + "', '" + s_id + "', 0, '"+e.attributeValue("image")+"');\" ");
							jspOut.print("style=\"text-decoration: none;\">");
							jspOut.print("<img id=\"" + this.treeName + "join" + s_id + "\" class=\"");
			
							if (ino) {
								jspOut.print("ss_twMinus");	// minus.gif
							} else {
								jspOut.print("ss_twPlus");	// plus.gif
							}
			
							jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/></a>");
						}
					}
					else {
						if (ls) {
							jspOut.print("<img class=\"ss_twJoinBottom\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
						} else {
							jspOut.print("<img class=\"ss_twJoin\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
						}
					}
			
					// Link
					if (hcn || hhcn) {
						jspOut.print("<img class=\""+s_imageClass+"\" id=\"");
						jspOut.print(this.treeName);
						jspOut.print("icon" + s_id + "\" src=\"");
			
						if (ino) {
							jspOut.print(s_imageOpen); // e.g., folder_open.gif
						} else {
							jspOut.print(s_image);	// e.g., folder.gif
						}
			
						jspOut.print("\"/>");
					}
					else {
						jspOut.print("<img class=\""+s_imageClass+"\" id=\"");
						jspOut.print(this.treeName);
						jspOut.print("icon" + s_id + "\" src=\"" + s_image + "\"/>");
					}
			
					if (!displayOnly) {
						String classField = "";
						if (!className.equals("")) classField = "class=\""+className+"\"";
						jspOut.print("<a "+classField+" href=\"" + s_url + "\" ");
						if (s_id != null && !s_id.equals("")) {
							jspOut.print("onClick=\"if (self."+this.treeName+"_showId) {return "+this.treeName+"_showId('"+s_id+"', this);}\" ");
						}
						jspOut.print(">");
					}
					jspOut.print("<span " + titleClass + ">");
					jspOut.print(s_text);
					jspOut.print("</span>");
					
					if (!displayOnly) jspOut.print("</a>");

					jspOut.print("<br/>\n");
				}
				
				//See if this is the starting id
				if (this.startingId != null && this.startingId.equals(s_id)) this.startingIdSeen = true;
				
				// Recurse if node has children
				if (hcn) {
					boolean divHasBeenOutput = false;
					if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
						jspOut.print("\n<div class=\"ss_twDiv\" id=\"" + this.treeName + "div" + s_id + "\"");
			
						if (!ino) {
							jspOut.print(" style=\"display: none;\"");
						}
			
						jspOut.print(">\n");
						divHasBeenOutput = true;
					}
		
					ListIterator it2 = e.elements("child").listIterator();
					while (it2.hasNext()) {
						outputTreeNodes((Element) it2.next(), recursedNodes);
					}
		
					if (divHasBeenOutput) {
						jspOut.print("</div>\n");
					}
				} else if (hhcn) {
					if (this.startingId == null || this.startingId.equals("") || this.startingIdSeen) {
						jspOut.print("\n<div id=\"" + this.treeName + "temp" + s_id + "\"></div>\n");
					}
				}
		
				// Pop last line or empty icon
				recursedNodes.remove(0);

				// See if it is time to stop
				if (this.startingId != null && this.startingId.equals(s_id)) {
					this.finished = true;
				}
			}
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
			
			//0 or 1 to indicate that there are no more elements in the list
			String s_ls = e.attributeValue("treeLS");
	
			//Text
			String s_text = e.attributeValue("title");
			if (this.nowrap) s_text = "<nobr>" + s_text + "</nobr>";
	
			//id
			String s_id = e.attributeValue("id", "");
			String titleClass = className;
			if (!s_id.equals("") && s_id.equals(this.highlightNode)) titleClass = "ss_tree_highlight";
	
			//Image
			String s_image = getImage(e.attributeValue("image"));
			String s_imageOpen = getImageOpen(e.attributeValue("image"));
			String s_imageClass = e.attributeValue("imageClass", "ss_twImg");
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
			s_url = s_url.replaceAll("&", "&amp;");
			
			//Write out the divs for this branch
			boolean ls = (s_ls == "1") ? true : false;
			boolean hcn = (e.attributeValue("treeHasChildren") == "1") ? true : false;
			boolean hhcn = (e.attributeValue("treeHasHiddenChildren") == "1") ? true : false;
			boolean ino = (e.attributeValue("treeOpen") == "1") ? true : false;
	
			for (int j = recursedNodes.size() - 1; j >= 0; j--) {
				if ((String) recursedNodes.get(j) != "1") {
					jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("spacer") + "\"/>");
				} else {
					jspOut.print("<img class=\"ss_twImg\" src=\"" + getImage("line") + "\"/>");
				}
			}
	
			// Line and empty icons
			if (ls) {
				recursedNodes.add(0, "0");
			} else {
				recursedNodes.add(0, "1");
			}
	
			// Write out join icons
			if (hcn || hhcn) {
				if (ls) {
					jspOut.print("<img id=\"" + this.treeName + "join" + s_id + "\" class=\"");
	
					if (ino) {
						jspOut.print("ss_twMinusBottom");	// minus_bottom.gif
					} else {
						jspOut.print("ss_twPlusBottom");    // plus_bottom.gif
					}
	
					jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
				}
				else {
					jspOut.print("<img id=\"" + this.treeName + "join" + s_id + "\" class=\"");
	
					if (ino) {
						jspOut.print("ss_twMinus");	// minus.gif
					} else {
						jspOut.print("ss_twPlus");	// plus.gif
					}
	
					jspOut.print("\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
				}
			} else {
				if (ls) {
					jspOut.print("<img class=\"ss_twJoinBottom\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
				} else {
					jspOut.print("<img class=\"ss_twJoin\" src=\"" + this.commonImg + "/pics/1pix.gif\"/>");
				}
			}
	
			// Link
			if (hcn) {
				jspOut.print("<img class=\""+s_imageClass+"\" id=\"");
				jspOut.print(this.treeName);
				jspOut.print("icon" + s_id + "\" src=\"");
				jspOut.print(s_imageOpen); // e.g., folder_open.gif
				jspOut.print("\"/>");
			}
			else {
				jspOut.print("<img class=\""+s_imageClass+"\" id=\"");
				jspOut.print(this.treeName);
				jspOut.print("icon" + s_id + "\" src=\"" + s_image + "\"/>");
			}
	
			//jspOut.print("&nbsp;");
			if (!displayOnly) {
				String classField = "";
				if (!className.equals("")) classField = "class=\""+className+"\"";
				jspOut.print("<a "+classField+" href=\"" + s_url + "\" ");
				if (s_id != null && !s_id.equals("")) {
					jspOut.print("onClick=\"if (self."+this.treeName+"_showId) {return "+this.treeName+"_showId('"+s_id+"', this);}\" ");
				}
				jspOut.print(">");
			}
			jspOut.print("<span " + titleClass + ">");
			jspOut.print(s_text);
			jspOut.print("</span>");
			
			if (!displayOnly) jspOut.print("</a>");
			jspOut.print("<br/>\n");
			
			// Recurse if node has children
	
			if (hcn) {
				jspOut.print("\n<div id=\"" + this.treeName + "div" + s_id + "\">\n");
	
				ListIterator it2 = e.elements("child").listIterator();
				while (it2.hasNext()) {
					outputTreeNodesFlat((Element) it2.next(), recursedNodes);
				}
				jspOut.print("</div>\n");
			} else if (hhcn) {
				jspOut.print("\n<div id=\"" + this.treeName + "temp" + s_id + "\"></div>\n");
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
	    this.treeName = treeName;
	}
	
	public void setTopId(String topId) {
	    this.topId = topId;
	}
	
	public void setStartingId(String startingId) {
	    this.startingId = startingId;
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
	
	public void setDisplayStyle(String displayStyle) {
	    this.displayStyle = displayStyle;
	}
	
	public void setNowrap(boolean nowrap) {
	    this.nowrap = nowrap;
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
	    images.put("admin_tools", "/trees/admin_tools.gif");
	    images.put("calendar", "/trees/calendar.gif");
	    images.put("discussion", "/trees/discussion.gif");
	    images.put("folder", "/trees/folder.gif");
	    imagesOpen.put("folder", "/trees/folder_open.gif");
	    images.put("people", "/trees/people.gif");
	    images.put("tasks", "/trees/tasks.gif");
	    images.put("workspace", "/trees/workspace.gif");
	    
	    images.put("bullet", "/trees/bullet.gif");
	    images.put("contact", "/trees/contact.gif");
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
		if (image == null || image.equals("")) image = "page";
		if (image.contains(".")) {
			//This is a graphic, so just return it
			return this.commonImg + image;
		} else {
			return (this.images.containsKey(image)) ? this.commonImg + (String)this.images.get(image) : this.commonImg + (String) this.images.get("page");
		}
	}
	
	private String getImageOpen(String image) {
		if (image == null || image.equals("")) image = "page";
		if (image.contains(".")) {
			//This is a graphic, so just return it
			return this.commonImg + image;
		} else {
			return (this.imagesOpen.containsKey(image)) ? this.commonImg + (String)this.imagesOpen.get(image) : getImage(image);
		}
	}
}


