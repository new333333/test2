/**
 * Copyright (c) 1998-2011 Novell, Inc. and its licensors. All rights reserved.
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

package org.kablink.teaming.samples.client.rest.v1;

import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.kablink.teaming.client.rest.v1.Api;
import org.kablink.teaming.client.rest.v1.ApiClient;
import org.kablink.teaming.rest.v1.model.FileProperties;

/**
 * @author jong
 *
 */
public class ApiTest {

	private ApiClient client;
	private Api api;
	
	@Before
	public void setUp() throws Exception {
		client = ApiClient.create("http://localhost:8080", "user1", "test");
		api = client.getApi();
	}
	
	@After
	public void tearDown() throws Exception {
		client.destroy();
	}
	
	@Test
	public void testReadingFilePropertiesByName() throws Exception {
		System.out.println("Invoking testReadingFilePropertiesByName");
		FileProperties fp = api.readFileProperties("folderEntry", 750, "test.txt");
		Assert.assertEquals("test.txt", fp.getName());
	}
	
	@Test
	public void testReadingFilePropertiesById() throws Exception {
		System.out.println("Invoking testReadingFilePropertiesById");
		FileProperties fp = api.readFileProperties("24e3531933933d270133934991be0011");
		Assert.assertEquals("test.txt", fp.getName());
	}
	
	@Test
	public void testFileDownload() throws Exception {
		System.out.println("Start time: " + new Date());
		for(int i = 0; i < 2; i++) {
			//File file = api.readFileAsFile("folderEntry", 749, "catalina.out");
			//System.out.println("File path: " + file.getAbsolutePath());
			//System.out.println("File length: " + file.length());
			//InputStream is = api.readFile("folderEntry", 749, "catalina.out");
			//InputStream is = api.readFile("24e350ea3383b149013383e583af0030");
			//File file = new File("C:/temp/rest/file_" + i + ".out");
			//OutputStream os = new FileOutputStream(file);
			//FileUtil.copy(is, os);
			//os.close();
			//is.close();
			//System.out.println("(" + i + ") file downloaded");
		}
		System.out.println("End time: " + new Date());
	}
}
