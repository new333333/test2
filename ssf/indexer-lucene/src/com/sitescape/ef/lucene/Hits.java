package com.sitescape.ef.lucene;

import java.io.IOException;
import java.io.Serializable;

import org.apache.lucene.document.Document;


/**
 *
 * @author Jong Kim
 *
 */
public class Hits implements Serializable {

    private int size;
    private Document[] documents;
    private float[] scores;

    private Hits(int length) {
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

    public static Hits transfer(org.apache.lucene.search.Hits hits,
            int offset, int maxSize) throws IOException {
        int length = hits.length();
        if (maxSize > 0) {
          length = Math.min(hits.length() - offset, maxSize);
        }
        Hits ss_hits = new Hits(length);
        for(int i = 0; i < length; i++) {
            ss_hits.setDoc(hits.doc(offset + i), i);
            ss_hits.setScore(hits.score(offset + i), i);
        }
        return ss_hits;
    }

    private void setDoc(Document doc, int n) {
        documents[n] = doc;
    }

    private void setScore(float score, int n) {
        scores[n] = score;
    }
}