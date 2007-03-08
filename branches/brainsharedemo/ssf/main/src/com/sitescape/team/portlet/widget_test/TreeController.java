package com.sitescape.team.portlet.widget_test;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.servlet.ModelAndView;

import com.sitescape.team.web.portlet.SAbstractController;

public class TreeController extends SAbstractController {

	public ModelAndView handleRenderRequestInternal(RenderRequest request, 
			RenderResponse response) throws Exception {
		
		int id = 1;
		
		//Build the tree
		Document wsTree = DocumentHelper.createDocument();
		Element rootElement = wsTree.addElement("root");
		rootElement.addAttribute("title", "This is the root");
		rootElement.addAttribute("image", "folder");
		rootElement.addAttribute("id", Integer.toString(id++));
		String title = rootElement.attributeValue("title");
		
		Element b1 = rootElement.addElement("child");
		b1.addAttribute("title", "this is workspace 1");
		b1.addAttribute("image", "folder");
		b1.addAttribute("id", Integer.toString(id++));
		
		Element b1a = b1.addElement("child");
		b1a.addAttribute("title", "This is forum 1/1");
		b1a.addAttribute("image", "page");
		b1a.addAttribute("id", Integer.toString(id++));
		
		Element b1b = b1.addElement("child");
		b1b.addAttribute("title", "This is forum 1/2");
		b1b.addAttribute("image", "page");
		b1b.addAttribute("id", Integer.toString(id++));
		
		//Build the second tree
		Document ws2Tree = DocumentHelper.createDocument();
		rootElement = ws2Tree.addElement("root");
		rootElement.addAttribute("title", "This is a different root");
		rootElement.addAttribute("image", "folder");
		rootElement.addAttribute("id", Integer.toString(id++));
		
		for (int i = 1; i <= 5; i++){
			//Add some folders to the root
			String nodeId = "f" + String.valueOf(i);
			Element rf1 = rootElement.addElement("child");
			title = "Folder " + String.valueOf(i);
			rf1.addAttribute("title", title);
			rf1.addAttribute("image", "folder");
			rf1.addAttribute("id", Integer.toString(id++));
			
			//Add some sub-folders to the folders
			for (int j = 1; j <= 20; j++){
				//Add some folders to the root
				nodeId = "f_" + String.valueOf(i) + "_" + String.valueOf(j);
				Element f1 = rf1.addElement("child");
				title = "SubFolder " + String.valueOf(j);
				f1.addAttribute("title", title);
				f1.addAttribute("image", "folder");
				f1.addAttribute("id", Integer.toString(id++));
				
				//Add some files to the folder
				for (int k = 1; k <= 20; k++){
					//Add some folders to the root
					nodeId = "f_" + String.valueOf(i) + "_" + String.valueOf(j) + "_" + String.valueOf(k);
					Element file = f1.addElement("child");
					title = "File " + String.valueOf(k);
					file.addAttribute("title", title);
					file.addAttribute("image", "page");
					file.addAttribute("id", Integer.toString(id++));
				}
			}
		}
		
		
		//Make the trees available to the jsp
		request.setAttribute("wsTree", wsTree);
		request.setAttribute("ws2Tree", ws2Tree);
		
		String path = "widget_test/view_tree";
		return new ModelAndView(path);
	}

}
