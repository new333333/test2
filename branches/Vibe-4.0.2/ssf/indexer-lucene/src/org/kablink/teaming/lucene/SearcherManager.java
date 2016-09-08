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

import java.io.IOException;

import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.search.IndexSearcher;

public class SearcherManager extends IndexSupport {

	private IndexSearcher currentSearcher;
	private IndexWriter writer;
	
	public SearcherManager(String indexName, IndexWriter writer) throws IOException {
		super(indexName);
		this.writer = writer;
		currentSearcher = new IndexSearcher(writer.getReader());
		warm(currentSearcher);
		writer.setMergedSegmentWarmer(
				new IndexWriter.IndexReaderWarmer() {
					public void warm(IndexReader reader) throws IOException {
						SearcherManager.this.warm(new IndexSearcher(reader));
					}
				});
		logDebug("Searcher manager instantiated");
	}

	protected void warm(IndexSearcher searcher)
			throws IOException
	{
	}

	private boolean reopening;

	private synchronized void startReopen()
			throws InterruptedException {
		while (reopening) {
			wait();
		}
		reopening = true;
	}

	private synchronized void doneReopen() {
		reopening = false;
		notifyAll();
	}

	private IndexReader reopenReader(IndexReader reader) throws IOException {
		long startTime = System.nanoTime();

		IndexReader newReader = reader.reopen();

		end(startTime, "reopenReader");			
		
		return newReader;
	}
	
	public void maybeReopen() throws InterruptedException, IOException {
		ensureOpen();
		startReopen();
		try {
			final IndexSearcher searcher = getInternal();
			try {
				if(!currentSearcher.getIndexReader().isCurrent()) {
					IndexReader newReader = reopenReader(currentSearcher.getIndexReader());
					IndexSearcher newSearcher = new IndexSearcher(newReader);
					swapSearcher(newSearcher);
				}
			} finally {
				releaseInternal(searcher);
			}
		} finally {
			doneReopen();
		}
		
		logTrace("maybeReopen called");
	}

	public void forceReopen() throws InterruptedException, IOException {
		ensureOpen();
		startReopen();
		try {
			final IndexSearcher searcher = getInternal();
			try {
				IndexReader newReader = reopenReader(currentSearcher.getIndexReader());
				IndexSearcher newSearcher = new IndexSearcher(newReader);
				swapSearcher(newSearcher);
			} finally {
				releaseInternal(searcher);
			}
		} finally {
			doneReopen();
		}
		
		logTrace("forceReopen called");
	}

	public synchronized IndexSearcher get() {
		IndexSearcher searcher = getInternal();
		logTrace("Getting index searcher");
		return searcher;
	}

	private synchronized IndexSearcher getInternal() {
		ensureOpen();
		currentSearcher.getIndexReader().incRef();
		return currentSearcher;
	}

	public synchronized void release(IndexSearcher searcher)
			throws IOException {
		releaseInternal(searcher);
		logTrace("Releasing index searcher");
	}

	public synchronized void releaseInternal(IndexSearcher searcher)
			throws IOException {
		searcher.getIndexReader().decRef();
	}

	public synchronized void close() throws IOException {
		// Release current searcher. This will cause the current reader to be closed. 
		releaseInternal(currentSearcher);
		
		currentSearcher = null;
		
		// Don't close writer, since it is not owned by this class.
		
		logDebug("Searcher manager closed");
	}
	
	private synchronized void swapSearcher(IndexSearcher newSearcher)
			throws IOException {
		releaseInternal(currentSearcher);
		currentSearcher = newSearcher;
		logTrace("Swapping index searcher");
	}
	
	private void ensureOpen() throws IllegalStateException {
		if(currentSearcher == null)
			throw new IllegalStateException("this SearcherManager is closed");
	}
	
	private void end(long begin, String methodName) {
		if(logger.isDebugEnabled()) {
			logDebug((System.nanoTime()-begin)/1000000.0 + " ms, " + methodName);
		}
	}

}