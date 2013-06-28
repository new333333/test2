/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.runasync;

import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import junit.framework.TestCase;

/**
 * @author jong
 *
 */
public class RunAsyncManagerTests extends TestCase {

	public void testCapacity1() throws Exception {
		ThreadPoolExecutor service = new ThreadPoolExecutor(3, 
				3,
                20L, 
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>());
		service.allowCoreThreadTimeOut(true);
		

		Future<String> f1 = null, 
				f2 = null, 
				f3 = null, 
				f4 = null;
		
		try {
			f1 = service.submit(new MyCallable("task-1"));
			System.out.println("First task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f2 = service.submit(new MyCallable("task-2"));
			System.out.println("Second task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f3 = service.submit(new MyCallable("task-3"));
			System.out.println("Third task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f4 = service.submit(new MyCallable("task-4"));
			System.out.println("Fourth task submitted");			
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		if(f1 != null)
			System.out.println("Result from first task: " + f1.get());
		if(f2 != null)
			System.out.println("Result from second task: " + f2.get());
		if(f3 != null)
			System.out.println("Result from third task: " + f3.get());
		if(f4 != null)
			System.out.println("Result from fourth task: " + f4.get());
		
		int poolSize = Integer.MAX_VALUE;
		for(int i = 0; i < 10; i++) {
			poolSize = service.getPoolSize();
			System.out.println("Current pool size = " + poolSize);
			if(poolSize == 0)
				break;
			Thread.sleep(5000);
		}
		
		System.out.println("Bye!");
	}
	
	static class MyCallable implements Callable<String> {
		String taskName;
		public MyCallable(String taskName) {
			this.taskName = taskName;
		}
		public String call() throws Exception {
			System.out.println("[" + Thread.currentThread().getName() + "] start executing " + taskName);
			Thread.sleep(10000);
			return ("[" + Thread.currentThread().getName() + "] finish executing " + taskName);
		}
	}
	
	/*
	public void testCapacity2() throws Exception {
		ThreadPoolExecutor service = new ThreadPoolExecutor(1, 
				2,
                60L, 
                TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(10));

		Callable<String> task = new Callable<String>() {
			public String call() throws Exception {
				long time = System.nanoTime();
				System.out.println("[" + Thread.currentThread().getName() + "] start executing - " + time);
				Thread.sleep(10000);
				return ("[" + Thread.currentThread().getName() + "] finish executing - " + time);
			}
		};

		Future<String> f1 = null, 
				f2 = null, 
				f3 = null, 
				f4 = null;
		
		try {
			f1 = service.submit(task);
			System.out.println("First task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f2 = service.submit(task);
			System.out.println("Second task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f3 = service.submit(task);
			System.out.println("Third task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f4 = service.submit(task);
			System.out.println("Fourth task submitted");			
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		if(f1 != null)
			System.out.println("Result from first task: " + f1.get());
		if(f2 != null)
			System.out.println("Result from second task: " + f2.get());
		if(f3 != null)
			System.out.println("Result from third task: " + f3.get());
		if(f4 != null)
			System.out.println("Result from fourth task: " + f4.get());
	}
	
	public void testHandoff() throws Exception {
		ExecutorService service = new ThreadPoolExecutor(0, 
				1,
                60L, 
                TimeUnit.SECONDS,
                new SynchronousQueue<Runnable>());

		Callable<String> task = new Callable<String>() {
			public String call() throws Exception {
				Thread.sleep(10000);
				return "Nice run! - " + System.nanoTime();
			}
		};

		Future<String> f1 = null, 
				f2 = null, 
				f3 = null, 
				f4 = null;
		
		try {
			f1 = service.submit(task);
			System.out.println("First task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f2 = service.submit(task);
			System.out.println("Second task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f3 = service.submit(task);
			System.out.println("Third task submitted");
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		try {
			f4 = service.submit(task);
			System.out.println("Fourth task submitted");			
		}
		catch(Exception e) {
			System.out.println(e);
		}
		
		if(f1 != null)
			System.out.println("Result from first task: " + f1.get());
		if(f2 != null)
			System.out.println("Result from second task: " + f2.get());
		if(f3 != null)
			System.out.println("Result from third task: " + f3.get());
		if(f4 != null)
			System.out.println("Result from fourth task: " + f4.get());
	}
	*/
}
