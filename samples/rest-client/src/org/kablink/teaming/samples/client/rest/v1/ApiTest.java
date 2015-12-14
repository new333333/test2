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

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Date;

import junit.framework.Assert;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.kablink.teaming.client.rest.v1.Api;
import org.kablink.teaming.client.rest.v1.ApiClient;
import org.kablink.teaming.rest.v1.model.FileProperties;
import org.kablink.util.FileUtil;

/**
 * @author jong
 *
 */
public class ApiTest {

	private ApiClient client;
	private Api api;
	
	/*
    @BeforeClass
    public static void oneTimeSetUp() {
        // one-time initialization code        
    }

    @AfterClass
    public static void oneTimeTearDown() {
        // one-time cleanup code
    }
    */

	@Before
	public void setUp() throws Exception {
		client = ApiClient.create("http://localhost:8080", "admin", "admin");
		api = client.getApi();
	}
	
	@After
	public void tearDown() throws Exception {
		client.destroy();
	}
	
	/*
	//@Test
	public void testReadFilePropertiesByName() throws Exception {
		System.out.println("Invoking testReadFilePropertiesByName");
		long entityId = 13;
		String fileName = "debug5.txt";
		FileProperties fp = api.readFileProperties("folderEntry", entityId, fileName);
		Assert.assertEquals(fileName, fp.getName());
	}
	
	//@Test
	public void testReadFilePropertiesById() throws Exception {
		System.out.println("Invoking testReadFilePropertiesById");
		String fileId = "24e3531933933d270133934991be0011";
		FileProperties fp = api.readFileProperties(fileId);
		Assert.assertEquals(fileId, fp.getId());
	}
	
	//@Test
	public void testDownloadFileByName() throws Exception {
		long entityId = 47;
		String fileName = "debug.doc";
		InputStream is = api.readFile("folderEntry", entityId, fileName);
		File outFile = new File("/temp/rest/" + fileName);
		OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
		FileUtil.copy(is, os);
		os.close();
		is.close();
	}
	
	//@Test
	public void testDownloadFileById() throws Exception {
		String fileId = "4028818a34f29ea50134f2e3a00e0011";
		InputStream is = api.readFile(fileId);
		File outFile = new File("/temp/rest/" + fileId);
		OutputStream os = new BufferedOutputStream(new FileOutputStream(outFile));
		FileUtil.copy(is, os);
		os.close();
		is.close();
	}
	
	//@Test
	public void testUploadFileByName() throws Exception {
		long entityId = 43;
		String fileName = "debug.doc";
		File inFile = new File("/temp/rest/" + fileName);
		api.writeFile("folderEntry", entityId, fileName, inFile);
	}
	
	//@Test
	public void testUploadFileByName2() throws Exception {
		long entityId = 1;
		String fileName = "debug5.txt";
		File inFile = new File("/temp/rest/" + fileName);
		Date modDate = new Date(System.currentTimeMillis() - 86400000); // yesterday
		api.writeFile("folderEntry", entityId, fileName, inFile, null, modDate, null, null, null);
	}

	//@Test
	public void testUploadFileById() throws Exception {
		String fileId = "24e3531933933d270133934991be0011";
		String fileName = "debug5.txt";
		File inFile = new File("/temp/rest/" + fileName);
		api.writeFile(fileId, inFile);
	}
	
	//@Test
	public void testDownloadFileRepeatedly() throws Exception {
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
	
	//@Test
	public void testReadFilePropertiesRepeatedlyAsynchronously() throws Exception {
		int threadCount = 30;
		Thread[] threads = new Thread[threadCount];
		final int repeatCount = 30;
		final long entityId = 13;
		final String fileName = "debug5.txt";
		for(int i = 0; i < threadCount; i++) {
			threads[i] = new Thread("ApiTestThread"+i) {
				public void run() {
					for(int j = 0; j < repeatCount; j++)
						api.readFileProperties("folderEntry", entityId, fileName);
					System.out.println("Thread [" + Thread.currentThread().getName() + "] completing normally");
				}
			};
		}
		for(int i = 0; i < threadCount; i++)
			threads[i].start();
		for(int i = 0; i < threadCount; i++)
			threads[i].join();
		System.out.println("testReadFilePropertiesRepeatedlyAsynchronously completed");
	}
	*/
}
