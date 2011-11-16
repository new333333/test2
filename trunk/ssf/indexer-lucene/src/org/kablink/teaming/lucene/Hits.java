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
import java.io.Serializable;

import org.apache.lucene.document.Document;
import org.apache.lucene.search.ScoreDoc;


/**
 *
 * @author Jong Kim
 *
 */
public class Hits implements Serializable {

    private int size;
    private Document[] documents;
    private float[] scores;
    private int totalHits = 0;

    public Hits(int length) {
        this.size = length;
        documents = new Document[length];
        scores = new float[length];
    }

    public Document doc(int n) {
        return documents[n];
    }

    public int length() {
        return this.size;
    }

    public float score(int n) {
        return scores[n];
    }

    public static Hits transfer(org.apache.lucene.search.IndexSearcher searcher, org.apache.lucene.search.TopDocs topDocs,
            int offset, int maxSize) throws IOException {
        if (topDocs == null) return new Hits(0);
    	int length = topDocs.totalHits;
        if (maxSize > 0) {
          length = Math.min(length - offset, maxSize);
        }
        if (length <= 0) return new Hits(0);
        Hits ss_hits = new Hits(length);
        ScoreDoc[] hits = topDocs.scoreDocs;
        int docId;
        for(int i = 0; i < length; i++) {
            ss_hits.setDoc(searcher.doc(hits[offset + i].doc), i);
            ss_hits.setScore(hits[offset + i].score, i);
        }
        ss_hits.setTotalHits(length);
        return ss_hits;
    }

    public void setDoc(Document doc, int n) {
        documents[n] = doc;
    }

    public void setScore(float score, int n) {
        scores[n] = score;
    }

	/**
	 * @return Returns the totalHits.
	 */
	public int getTotalHits() {
		return totalHits;
	}

	/**
	 * @param totalHits The totalHits to set.
	 */
	public void setTotalHits(int totalHits) {
		this.totalHits = totalHits;
	}
}