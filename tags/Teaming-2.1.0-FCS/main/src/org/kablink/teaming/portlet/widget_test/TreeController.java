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
package org.kablink.teaming.portlet.widget_test;

import javax.portlet.RenderRequest;
import javax.portlet.RenderResponse;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.kablink.teaming.web.portlet.SAbstractController;
import org.springframework.web.portlet.ModelAndView;


public class TreeController extends SAbstractController {

	public ModelAndView handleRenderRequestAfterValidation(RenderRequest request, 
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
