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
package org.kablink.teaming.search;

import java.util.ArrayList;

import org.apache.commons.logging.Log;
import org.apache.lucene.index.Term;
import org.kablink.teaming.lucene.LuceneException;
import org.kablink.teaming.util.SimpleProfiler;

public abstract class AbstractLuceneWriteSession extends AbstractLuceneSession implements LuceneWriteSession {

	protected AbstractLuceneWriteSession(Log logger) {
		super(logger);
	}

	public void addDocuments(ArrayList docs) throws LuceneException {
		SimpleProfiler.start("addDocuments()");
		long begin = System.nanoTime();
		invokeAddDocuments(docs);
		SimpleProfiler.stop("addDocuments()");
		endWrite(begin, "addDocuments");
	}

	protected abstract void invokeAddDocuments(ArrayList docs) throws LuceneException;
	
	public void deleteDocuments(Term term) throws LuceneException {
		SimpleProfiler.start("deleteDocuments()");
		long begin = System.nanoTime();
		invokeDeleteDocuments(term);
		SimpleProfiler.stop("deleteDocuments()");
		endWrite(begin, "deleteDocuments");
	}

	protected abstract void invokeDeleteDocuments(Term term) throws LuceneException;
	
	public void addDeleteDocuments(ArrayList docsToAddOrDelete) throws LuceneException {
		SimpleProfiler.start("addDeleteDocuments()");
		long begin = System.nanoTime();
		invokeAddDeleteDocuments(docsToAddOrDelete);
		SimpleProfiler.stop("addDeleteDocuments()");
		endWrite(begin, "addDeleteDocuments");
	}

	protected abstract void invokeAddDeleteDocuments(ArrayList docsToAddOrDelete) throws LuceneException;
	
	public void optimize() throws LuceneException {
		SimpleProfiler.start("optimize()");
		long begin = System.nanoTime();
		invokeOptimize();
		SimpleProfiler.stop("optimize()");
		endWrite(begin, "optimize");
	}
	
	protected abstract void invokeOptimize() throws LuceneException;

	public void clearIndex() throws LuceneException {
		SimpleProfiler.start("clearIndex()");
		long begin = System.nanoTime();
		invokeClearIndex();
		SimpleProfiler.stop("clearIndex()");
		endWrite(begin, "clearIndex");
	}

	protected abstract void invokeClearIndex() throws LuceneException;
}
