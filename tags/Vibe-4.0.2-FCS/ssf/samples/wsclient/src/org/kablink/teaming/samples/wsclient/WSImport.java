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
package org.kablink.teaming.samples.wsclient;

import java.io.File;
import java.io.FilenameFilter;
import java.util.Date;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

/*
 * Import workspaces, folders, entries and attachments exported by WsExport.  Each binder is a Windows
 *  folder, with an Attributes.xml file in it.  Attributes.xml contains XML whose root element has at
 *  least an 'id', a 'type', and a 'title' element.  The id is just used for displaying status, but the
 *  type and title are used to create the binder.
 * Within a binder's Windows folder, there can be entries in XML files.  Any .xml file that begins with a
 *  number will be taken as an entry.  The contents of the XML file will be sent as-is in the addFolderEntry
 *  call.  The definitionId in that XML will be sent as the definition ID, so it must exist on the target
 *  system before importing.
 * If a folder exists whose name matches the name of the entry XML file, but with "Attach-" at the beginning
 *  and with no ".xml" extension, each of the files within it will be uploaded as an attachment to that
 *  entry.
 *  
 * You may need to change three numbers in this file and recompile to use it on your system.  They are
 *  the values of the global variables GlobalWorkspacesId, WorkspaceConfigId, and DiscussionFolderConfigId.
 *  GlobalWorkspacesId is the binder ID of the "Global Workspaces" folder, under which a workspace to hold
 *  the imported data will be created.  You can set this to anything you like, not just the Global Workspaces,
 *  but it must be a workspace.  If you don't want to create a new workspace to hold the imported data,
 *  you can pass a binder ID on the command line, and the data will be imported there.  It must be a
 *  workspace ID, if the data to be imported has any workspaces in it.
 * The other two numbers are the "config IDs" for a vanilla workspace and a discussion folder.  You can
 *  find these numbers on your system by going to any workspace, and choosing "Add a workspace" or
 *  "Add a folder" from the Manage menu.  Viewing the source of this page, you can find the number
 *  associated with the radio button for each of the two binder types (workspace and discussion folder).
 *  You choose any of the folder types, not just Discussion Folder, but all imported folders will be of
 *  that type.
 *  
 * Besides the all-folders-are-of-one-type limitation, the other big limitation of this process is that
 *  all binders and entries created will be owned by a single user (admin, unless you change the log-in
 *  information in this file and the PasswordCallback).  Also, unlike WsExport, there is no capability for
 *  resuming an import that gets interrupted.  You'll have to just delete the partially imported stuff
 *  and start again.
 */
public class WSImport extends WSClientBase
{
	static final String PAD = "                                                                     ";
	
	
	static Long GlobalWorkspacesId = new Long(28);
	static Long WorkspaceConfigId = new Long(24);
	static Long DiscussionFolderConfigId = new Long(5);
	
	static Long targetId = null;
	public static void main(String[] args) {
		WSImport wsImport = new WSImport();
		
		if(args.length < 1 || args.length > 2) {
			System.err.println("Usage: WSImport <export folder> [<target binder id>]");
			return;
		}
		File importRoot = new File(args[0]);
		if(args.length == 2) {
			targetId = Long.valueOf(args[1]);
		} else {
			try {
				targetId = (Long) wsImport.invokeWithCall("Facade", "addFolder", new Object[] {GlobalWorkspacesId, WorkspaceConfigId, safeName("Imported data " + (new Date()).toString())}, null, null);
			} catch(Exception e) {
				System.err.println("Could not create new import folder.");
				e.printStackTrace();
				System.exit(-1);
			}
		}
		try {
			wsImport.createWorkspaceTree(importRoot, null, 0);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	static String safeName(String name)
	{
		return name.replaceAll("[\\p{Punct}\\p{Space}]", "_");
	}
	
	void createWorkspaceTree(File root, Long parentId, int depth)
	throws Exception
	{
		Long myId = null;
		File myAttributes = new File(root, "Attributes.xml");
		if(myAttributes.exists()) {
			String xml = readText(myAttributes);
			Document document = DocumentHelper.parseText(xml);
			System.err.println(PAD.substring(0, depth) + "Processing " + document.getRootElement().attributeValue("id") + " - " + document.getRootElement().attributeValue("title"));
			if(parentId == null) {
				myId = targetId;
			} else {
				Element binder = document.getRootElement();
				Long configId = null;
				if("workspace".equals(binder.attributeValue("type"))) {
					configId = WorkspaceConfigId;
				} else {
					configId = DiscussionFolderConfigId;
				}
				String title = safeName(binder.attributeValue("title"));
				myId = (Long) invokeWithCall("Facade", "addFolder", new Object[] {parentId, configId, title}, null, null);
			}
		}
		// Build tree recursively
		File[] children = root.listFiles(
				new FilenameFilter() {
					public boolean accept(File dir, String name) {
						return name.matches("\\p{Digit}+-.*");
					}
				});
		if(children != null) {
			for(File child : children) {
				if(child.isDirectory()) {
					createWorkspaceTree(child, myId, depth + 1);
				} else {
					createEntry(child, root, myId, depth + 1);
				}
			}
		}
	}

	void createEntry(File entryFile, File parentFolder, Long parentId, int depth)
	throws Exception
	{
		String xml = readText(entryFile);
		Document document = DocumentHelper.parseText(xml);
		Element root = document.getRootElement();
		String definition = root.attributeValue("definitionId");
		System.err.println(PAD.substring(0, depth) + "Entry " + root.attributeValue("id") + " - " + root.attributeValue("title"));
		Long myId = (Long) invokeWithCall("Facade", "addFolderEntry", new Object[] {parentId, definition, xml}, null, null);
		File attachmentsFolder = new File(parentFolder, "Attach-"+entryFile.getName().substring(0, entryFile.getName().length()-4));
		if(attachmentsFolder.exists() && attachmentsFolder.isDirectory()) {
			createEntryAttachments(attachmentsFolder, myId, parentId, depth + 1);
		}
	}
	
	void createEntryAttachments(File attachmentsFolder, Long entryId, Long folderId, int depth)
	throws Exception
	{
		File[] attachments = attachmentsFolder.listFiles();
		for(File f : attachments) {
			System.out.println(PAD.substring(0, depth) + "Attachment: " + f.getName());
			invokeWithCall("Facade", "uploadFolderFile", new Object[] {folderId, entryId, "ss_attachFile1", f.getName()}, f, null);
		}
	}
}