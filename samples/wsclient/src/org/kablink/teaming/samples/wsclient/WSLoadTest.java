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
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;

import javax.activation.DataHandler;

import org.apache.axis.attachments.AttachmentPart;
import org.apache.axis.client.Call;

public class WSLoadTest extends WSClientBase
{
	static String loremIpsum = "Lorem ipsum dolor sit amet, consectetuer adipiscing elit. Phasellus commodo. Duis nibh mauris, venenatis et, nonummy vitae, rutrum quis, massa. Vestibulum rutrum. Maecenas arcu est, venenatis eget, pellentesque sit amet, dignissim fringilla, ipsum. Maecenas neque pede, accumsan a, pellentesque a, fringilla lobortis, felis. Sed dapibus nonummy lorem. Aliquam adipiscing. Etiam porttitor sagittis arcu. Integer laoreet ipsum vitae lacus. Duis tempor quam vel pede. Nam ornare pede vel turpis.\n\nNulla sapien felis, cursus commodo, elementum id, tristique vel, mauris. Quisque pede. Etiam sit amet turpis in ligula congue suscipit. Nulla venenatis, lectus vel ornare tincidunt, odio ipsum vehicula mi, in cursus felis odio vitae est. Nulla nibh tellus, dignissim vel, laoreet ac, elementum vel, diam. Curabitur non eros. Sed tempor felis non pede. Sed imperdiet fermentum ante. Nulla fermentum, eros ultrices pulvinar malesuada, velit massa luctus risus, id convallis mi tortor lobortis leo. Etiam congue semper ipsum. Mauris sodales libero ut erat. Proin tellus. Quisque ullamcorper mauris a dui. Duis commodo. Quisque ac quam. Ut magna. Maecenas a neque ac augue feugiat convallis. Sed a tortor at magna dapibus porttitor. Phasellus quam purus, sagittis et, hendrerit sit amet, lacinia ut, mauris. In luctus.\n\nMauris urna. Quisque ipsum. Curabitur non sapien non mi facilisis lobortis. Nam id nibh at tellus faucibus nonummy. Duis congue mollis risus. Etiam gravida sodales tortor. Aenean gravida dui id pede. Proin quis mauris non leo luctus tempus. Praesent non ante. Praesent tempus libero id purus. Quisque faucibus. Etiam vulputate ipsum eu elit. Nam eget ante. Nullam elementum semper libero.\n\nIn hac habitasse platea dictumst. Curabitur metus. Duis vestibulum massa ullamcorper sem mollis placerat. Fusce metus odio, feugiat ac, cursus at, faucibus a, eros. Phasellus ligula elit, venenatis a, venenatis eget, pretium vel, augue. Donec sit amet diam. Suspendisse sit amet metus quis lectus ornare consequat. Pellentesque condimentum accumsan velit. Donec in mauris. Mauris varius orci vel ipsum. Fusce viverra ante nec diam. Mauris massa massa, fringilla tempus, pellentesque at, lobortis ut, orci. Pellentesque cursus mi sed nisi. Aliquam vulputate ultrices neque. Vivamus condimentum.\n\nNulla lacinia velit at lectus. Suspendisse vel urna tristique purus feugiat gravida. Vestibulum ullamcorper. Suspendisse potenti. Morbi suscipit mi vel turpis. Nulla quis neque. Vestibulum ante ipsum primis in faucibus orci luctus et ultrices posuere cubilia Curae; Nam egestas elit in eros. Cras pede elit, rutrum et, pellentesque ut, euismod id, mauris. Morbi justo neque, accumsan non, suscipit et, consequat quis, sapien. Nulla egestas, turpis sit amet venenatis mattis, neque ante ultricies lacus, vel molestie orci purus ut justo. Cum sociis natoque penatibus et magnis dis parturient montes, nascetur ridiculus mus. Integer nibh enim, sollicitudin ac, vestibulum eget, mattis quis, est. In egestas scelerisque velit. Nulla accumsan mollis sem. Praesent tincidunt. Donec blandit. Integer lacinia ipsum vitae tortor. Aliquam ullamcorper placerat tellus.\n";
	static Date start = null;
	static Long templateId = null;
	static String definitionId = null;
	static String filename = null;
	static int completed = 0;
	static Date lastStatus = null;
	
	public static void main(String[] args) {
		WSLoadTest wsLoadTest = new WSLoadTest();
		
		if(args.length < 3 || args.length > 4) {
			System.err.println("Usage: WSLoadTest <count> <commaSeparatedFolderIdList> <definitionId> [<attachmentFilename>]");
			System.err.println("           folder id's can be ranges, e.g. 3-15:4 is the same as 3,7,11,15");
			System.err.println("           or you can specify 'auto:nnn:mmm' (without quotes), where 'nnn' is a folder number");
			System.err.println("              and 'mmm' is a template id.  In this case, a new folder will be created for each");
			System.err.println("              entry, with parent folder 'nnn' using template 'mmm'.  Also in this case, the count");
			System.err.println("              can be of the form N1:N2:...:M, which will make a tree of N1 folders, each");
			System.err.println("              containing N2 sub-folders, etc, etc, finally each containing M entries");
			return;
		}
		Integer count = null;
		Long[] folderIds = null;
		String ids[] = null;
		boolean autoFolder = false;
		templateId = null;
		definitionId = args[2];
		filename = null;
		if(args.length == 4) { filename = args[3]; }
		start = lastStatus = new Date();

		if(args[1].startsWith("auto")) {
			autoFolder = true;
			ids = args[1].split(":");
			Long parentFolder = Long.parseLong(ids[1]);
			templateId = Long.parseLong(ids[2]);
			String[] foo = args[0].split(":");
			int[] counts = new int[foo.length];
			int product = 1;
			for(int i = 0; i < foo.length; i++) {
				counts[i] = Integer.parseInt(foo[i]);
				product = product * counts[i];
			}
			System.out.println("Creating " + product + " entries, total.");
			wsLoadTest.createFolders(counts, 0, parentFolder, "");
		} else {
			count = Integer.parseInt(args[0]);
			if(args[1].contains(",")) {
				ids = args[1].split(",");
			} else {
				ids = new String[] {args[1]};
			}
			long startId, stopId, step;
			LinkedList<Long> generatedIds = new LinkedList<Long>();
			for(int i = 0; i < ids.length; i++) {
				if(ids[i].contains("-")) {
					String[] range = ids[i].split("-");
					if(range[1].contains(":")) {
						String parts[] = range[1].split(":");
						range[1] = parts[0];
						step = Long.parseLong(parts[1]);
					} else {
						step = 1;
					}
					startId = Long.parseLong(range[0]);
					stopId = Long.parseLong(range[1]);
				} else {
					startId = stopId = Long.parseLong(ids[i]);
					step = 1;
				}
				for(long id = startId; id <= stopId; id += step) {
					generatedIds.add(new Long(id));
				}
			}
			folderIds = generatedIds.toArray(new Long[0]);
			wsLoadTest.createEntries(count, folderIds, "");
		}

		System.err.println("Total time: " + ((new Date()).getTime() - start.getTime()) + "ms");
	}

	void createEntries(int count, Long[] folderIds, String prefix) {
		try {
			for(int j = 0; j < folderIds.length; j++) {
				for(int i = 0; i < count; i++) {
					String s = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><entry>  <attribute name=\"title\" type=\"title\">Load Test Entry " + prefix + i + " " + start.toString()  + "</attribute><attribute name=\"description\" type=\"description\">" + loremIpsum + "</attribute></entry>";
					Long entryId = (Long) invokeWithCall("Facade", "addFolderEntry", 
							new Object[] {folderIds[j], definitionId, s, filename},
							((filename != null)? new File(filename) : null),
							null);
					completed++;
					Date now = new Date();
					if(now.getTime() - lastStatus.getTime() > 60000) {
						System.err.println(completed+1);
						lastStatus = now;
					}
				}
			}
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	void createFolders(int[] folderCounts, int depth, Long parentFolder, String prefix)
	{
		if(depth == folderCounts.length - 1) {
			createEntries(folderCounts[depth], new Long[] {parentFolder}, prefix);
		} else {
			for(int i = 0; i < folderCounts[depth]; i++) {
				try {
					Long newFolder = (Long) invokeWithCall("Facade", "addFolder", 
							new Object[] {parentFolder, templateId, "Generated folder " + prefix + (i+1)  + safeName(start.toString())}, 
							null, null);
					createFolders(folderCounts, depth + 1, newFolder, prefix + (i+1) + "-");
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	static String safeName(String name)
	{
		return name.replaceAll("[\\p{Punct}\\p{Space}]", "_");
	}

	/*
	 * (non-Javadoc)
	 * @see org.kablink.teaming.samples.wsclient.WSClientBase#extractOutputFiles(org.apache.axis.client.Call, java.io.File)
	 */
	@Override
	protected void extractOutputFiles(Call call, File outputAttachmentsDirectory) throws Exception {
		org.apache.axis.MessageContext messageContext = call.getMessageContext();
		org.apache.axis.Message returnedMessage = messageContext.getResponseMessage();
		Iterator iteAtta = returnedMessage.getAttachments();
		DataHandler[] dhTab = new DataHandler[returnedMessage.countAttachments()];
		for (int i=0;iteAtta.hasNext();i++) {
			AttachmentPart ap = (AttachmentPart) iteAtta.next();
			dhTab[i] = ap.getDataHandler();
			System.out.println("Filename=" + dhTab[i].getName());
		}
	}

}