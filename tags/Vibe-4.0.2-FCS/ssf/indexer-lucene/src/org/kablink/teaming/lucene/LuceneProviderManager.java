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
package org.kablink.teaming.lucene;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.BooleanQuery;

import org.kablink.util.MBeanUtil;
import org.kablink.util.PropsUtil;

/**
 * This class manages <code>LuceneProvider</code> instances.  
 * 
 * IMPORTANT: Typically, a spring bean is shut down by the application context invoking a method
 * on the bean that was registered as a shutdown method, for example, by implementing
 * <code>org.springframework.beans.factory.DisposableBean</code> interface. Although the normal 
 * mechanism is sufficient for most beans, it isn't so for this particular one. 
 * Graciously closing this bean under all circumstances is critical to ensuring that all pending 
 * changes are flushed/committed to disk and that all indexes are closed properly. Not doing so 
 * can result in loss of data or corrupt index, although the latter is rare. To meet that more
 * stringent requirement, the cleanup procedure of this class is also registered with the JVM 
 * itself as a shutdown hook. The cleanup method is idempotent, so it is ok if it is called more 
 * than once.
 *
 */
public class LuceneProviderManager implements LuceneProviderManagerMBean {

	private static final int DEFAULT_MAX_BOOLEAN_CLAUSES = 10000;
	private static final int DEFAULT_COMMIT_NUMBER_OPS = 10000;
	private static final int DEFAULT_COMMIT_TIME_INTERVAL = 600; // = 10 minutes
	private static final boolean DEFAULT_CHECK_FOR_WORK_IN_CALLING_THREAD = false;
	
	
	private static final String SLASH = "/";

	private String indexRootDirPath;
	private String mbeanNamePrefix;
	private String mbeanObjectName;
	
    private volatile int commitNumberOps;
    private volatile int commitTimeInterval; // in second
    private volatile boolean commitCheckForWorkInCallingThread;

	// Access to this map is protected by "this"
	private Map<String,Object> lockMap = new HashMap<String,Object>();
	
	// Access to a particular entry in this map is protected by the corresponding lock object stored in the lockMap above.
	private ConcurrentMap<String,LuceneProvider> providerMap = new ConcurrentHashMap<String,LuceneProvider>();
	
	private Log logger = LogFactory.getLog(getClass());
	
	private volatile boolean closed = false;
	
	public void setIndexRootDirPath(String indexRootDirPath) {
		if(indexRootDirPath == null)
			indexRootDirPath = "";
		if(!indexRootDirPath.endsWith(SLASH))
			indexRootDirPath = indexRootDirPath + SLASH;	
		if(indexRootDirPath.equals(SLASH))
			throw new IllegalArgumentException("Index root directory path must be specified");
		else
			this.indexRootDirPath = indexRootDirPath;
		logger.info("Index root directory path is set to [" + indexRootDirPath + "]");
	}
	
	public String getIndexRootDirPath() {
		return indexRootDirPath;
	}
	
	public void setServerType(String serverType) {
		
	}
	public void setMbeanNamePrefix(String mbeanNamePrefix) {
		this.mbeanNamePrefix = mbeanNamePrefix;
		this.mbeanObjectName = mbeanNamePrefix + "name=luceneProviderManager";
	}
	
	public void initialize() throws LuceneException {
		File indexRootDir = new File(indexRootDirPath);
		if (indexRootDir.exists()) {
			if (!indexRootDir.isDirectory()) {
				throw new LuceneException("The specified index root directory path [" + indexRootDirPath + "] exists, but is not a directory");
			}
			else {
				logger.info("The index root directory exists and is a directory");
			}
		} else {
			// Create the directory
			if(!indexRootDir.mkdirs()) {
				throw new LuceneException("Can not create index root directory [" + indexRootDirPath + "]");
			}
			else {
				logger.info("The index root directory is created");
			}
		}

		this.commitNumberOps = PropsUtil.getInt("lucene.index.commit.number.ops", DEFAULT_COMMIT_NUMBER_OPS);
		this.commitTimeInterval = PropsUtil.getInt("lucene.index.commit.time.interval", DEFAULT_COMMIT_TIME_INTERVAL);
		this.commitCheckForWorkInCallingThread = PropsUtil.getBoolean("lucene.index.commit.check.for.work.in.calling.thread", DEFAULT_CHECK_FOR_WORK_IN_CALLING_THREAD);

		int maxBooleans = PropsUtil.getInt("lucene.max.booleans", DEFAULT_MAX_BOOLEAN_CLAUSES);
		BooleanQuery.setMaxClauseCount(maxBooleans);
		
		boolean enableInfoStream = PropsUtil.getBoolean("lucene.enable.info.stream", false);
		
		logger.info("commitNumberOps=" + commitNumberOps + 
				", commitTimeInterval=" + commitTimeInterval + 
				", commitCheckForWorkInCallingThread=" + commitCheckForWorkInCallingThread + 
				", maxBooleanClauseCount=" + maxBooleans +
				", enableInfoStream=" + enableInfoStream);
		
		if(enableInfoStream)
			IndexWriter.setDefaultInfoStream(System.out);
		
		// Register MBean
		try {
			MBeanUtil.register(this, mbeanObjectName);
			if(logger.isDebugEnabled())
				logger.debug("MBean with name " + mbeanObjectName + " is registered");
		} catch (Exception e) {
			logger.warn("Error registering MBean with name " + mbeanObjectName, e);
		}
		
		// Register JVM shutdown hook
		Runtime.getRuntime().addShutdownHook(
				new Thread("LuceneProviderManagerShutdownHook") {
					public void run() {
						logger.info("Shutdown hook thread running");
						LuceneProviderManager.this.close();
					}
				});
		if(logger.isDebugEnabled())
			logger.debug("Shutdown hook thread is registered");
	}
	
	public LuceneProvider getProvider(String indexName) throws LuceneException {
		// We control accesses to the global provider map such that only accesses to the "same" index are synchronized.
		// That is, we prevent more than one provider from being created for the same index. But between different indexes,
		// accesses to providers (and related creation process) can proceed in parallel.
		Object lock = obtainLock(indexName);
		synchronized(lock) {
			LuceneProvider provider = providerMap.get(indexName);
			if(provider == null) {
				provider = createProvider(indexName);
				providerMap.put(indexName, provider);
			}
			return provider;
		}
	}
	
	public void close() {
		if(!closed) {
			// Set this flag immediately before beginning the actual rundown process. 
			// It helps to prevent other thread from entering this block of code simultaneously.
			closed = true;
			for(LuceneProvider provider: providerMap.values()) {
				provider.close();
			}
		}
	}
	
	private synchronized Object obtainLock(String indexName) {
		Object lock = lockMap.get(indexName);
		if(lock == null) {
			lock = new Object();
			lockMap.put(indexName, lock);
		}
		return lock;
	}
	
	private LuceneProvider createProvider(String indexName) throws LuceneException {
		LuceneProvider provider = new LuceneProvider(indexName, getIndexDirPath(indexName), this);
		provider.initialize();
		
		String mbeanName = mbeanNamePrefix + "name=luceneProvider-" + indexName;
		try {
			MBeanUtil.register(provider, mbeanName);
			if(logger.isDebugEnabled())
				logger.debug("MBean with name " + mbeanName + " is registered");
		} catch (Exception e) {
			logger.warn("Error registering MBean with name " + mbeanName, e);
		}

		return provider;
	}
	
	private String getIndexDirPath(String indexName) {
		return indexRootDirPath + indexName;
	}

	public int getCommitNumberOps() {
		return commitNumberOps;
	}

	public int getCommitTimeInterval() {
		return commitTimeInterval;
	}

	public boolean getCommitCheckForWorkInCallingThread() {
		return commitCheckForWorkInCallingThread;
	}
	
	public void setCommitNumberOps(int commitNumberOps) {
		logger.info("Setting commitNumberOps to " + commitNumberOps);
		this.commitNumberOps = commitNumberOps;
	}

	public void setCommitTimeInterval(int commitTimeInterval) {
		logger.info("Setting commitTimeInterval to " + commitTimeInterval);
		this.commitTimeInterval = commitTimeInterval;
	}
	
	public void setCommitCheckForWorkInCallingThread(boolean commitCheckForWorkInCallingThread) {
		logger.info("Setting commitCheckForWorkInCallingThread to " + commitCheckForWorkInCallingThread);
		this.commitCheckForWorkInCallingThread = commitCheckForWorkInCallingThread;
	}

	public void optimize() {
		logSystemResource();
		for(LuceneProvider provider: providerMap.values()) {
			// Optimize the index merging segments
			provider.optimize();
			// Call commit to release resources and free up disk spaces.
			provider.commit();
		}
		logSystemResource();
	}
	
	public void optimize(int maxNumSegments) {
		logSystemResource();
		for(LuceneProvider provider: providerMap.values()) {
			// Optimize the index merging segments
			provider.optimize(maxNumSegments);
			// Call commit to release resources and free up disk spaces.
			provider.commit();
		}
		logSystemResource();
	}	

	public void expungeDeletes() {
		logSystemResource();
		for(LuceneProvider provider: providerMap.values()) {
			// Expunge deletes from the index
			provider.expungeDeletes();
			// Call commit to release resources and free up disk spaces.
			provider.commit();
		}
		logSystemResource();
	}
	
	private void logSystemResource() {
		if(logger.isDebugEnabled())
			logger.debug("free memory=" + Runtime.getRuntime().freeMemory() + ", max memory=" + Runtime.getRuntime().maxMemory() + ", total memory=" + Runtime.getRuntime().totalMemory());
	}
	
	public boolean getInfoStreamEnabled() {
		return (IndexWriter.getDefaultInfoStream() != null);
	}

	public void setInfoStreamEnabled(boolean infoStreamEnabled) {
		IndexWriter.setDefaultInfoStream(((infoStreamEnabled)? System.out : null));
	}
}
