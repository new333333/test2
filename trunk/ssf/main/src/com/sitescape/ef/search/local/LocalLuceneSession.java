package com.sitescape.ef.search.local;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Iterator;
import java.util.TreeSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.DocumentSelection;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexUpdater;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.apache.lucene.index.TermEnum;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Sort;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import com.sitescape.ef.context.request.RequestContextHolder;
import com.sitescape.ef.lucene.SsfQueryAnalyzer;
import com.sitescape.ef.search.BasicIndexUtils;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.util.LuceneUtil;

/**
 * This implementation provides access to local Lucene index.
 * 
 * @author Jong Kim
 * 
 */
public class LocalLuceneSession implements LuceneSession {
	Object SyncObj = new Object();
	// Note: I'm not convinced that this implementation makes good use of
	// Lucene,
	// primarily due to my lack of intimiate knowledge of Lucene.
	// Two major implementation questions:
	// 1) Is it allowed to open more than one IndexWriter (using multiple
	// instances of sessions in multiple threads) in a program?
	// What are the ramifications when we do that?
	// 2) What is the best practise around using IndexSearcher? Specifically,
	// should it be opened and closed for each query or re-used for multiple
	// queries? If re-used for extended period of time, then I assume that
	// the index snapshot represented by the handle won't incorporate the
	// changes that may have been made to the index since the handle was
	// obtained. Is that right?

	// Updated Note: This implementation is rewritten to simply use Liferay's
	// local index support, which is not very scalable. Since local index
	// configuration should only be used for testing and/or demo installation,
	// I wouldn't bother with making this a production quality service.

	private static final Log logger = LogFactory
			.getLog(LocalLuceneSession.class);

	private String indexPath;

	public LocalLuceneSession(String indexPath) {
		this.indexPath = indexPath;
	}

	public void addDocument(Document doc) {
		if (doc.getField(BasicIndexUtils.UID_FIELD) == null)
			throw new LuceneException(
					"Document must contain a UID with field name "
							+ BasicIndexUtils.UID_FIELD);

		IndexWriter indexWriter = null;

		try {
			synchronized (SyncObj) {
				indexWriter = LuceneUtil.getWriter(indexPath);
			}
		} catch (IOException e) {
			throw new LuceneException("Could not open writer on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			indexWriter.addDocument(doc);
		} catch (IOException e) {
			throw new LuceneException("Could not add document to the index ["
					+ indexPath + "]", e);
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
			}
		}
	}

	public void addDocuments(Collection docs) {

		IndexWriter indexWriter = null;

		try {
			synchronized (SyncObj) {
				indexWriter = LuceneUtil.getWriter(indexPath);
			}
		} catch (IOException e) {
			throw new LuceneException("Could not open writer on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			for (Iterator iter = docs.iterator(); iter.hasNext();) {
				Document doc = (Document) iter.next();
				if (doc.getField(BasicIndexUtils.UID_FIELD) == null)
					throw new LuceneException(
							"Document must contain a UID with field name "
									+ BasicIndexUtils.UID_FIELD);
				indexWriter.addDocument(doc);
			}
		} catch (IOException e) {
			throw new LuceneException("Could not add document to the index ["
					+ indexPath + "]", e);
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
			}
		}
	}

	public void deleteDocument(String uid) {
		deleteDocuments(new Term(BasicIndexUtils.UID_FIELD, uid));
	}

	public void deleteDocuments(Term term) {
		IndexReader indexReader = null;

		try {
			indexReader = LuceneUtil.getReader(indexPath);
		} catch (IOException e) {
			throw new LuceneException("Could not open reader on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			indexReader.deleteDocuments(term);
		} catch (IOException e) {
			throw new LuceneException(
					"Could not delete documents from the index [" + indexPath
							+ "]", e);
		} finally {
			try {
				indexReader.close();
			} catch (IOException e) {
			}
		}
	}

	public void deleteDocuments(Query query) {
		IndexSearcher indexSearcher = null;

		try {
			indexSearcher = LuceneUtil.getSearcher(indexPath);
		} catch (IOException e) {
			throw new LuceneException("Could not open searcher on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			deleteDocs(indexSearcher.search(query));
		} catch (IOException e) {
			throw new LuceneException("Error searching index [" + indexPath
					+ "]", e);
		} finally {
			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
		}
	}

	public void updateDocument(String uid, String fieldname, String fieldvalue) {
		// build the query
		Query q = null;
		QueryParser qp = new QueryParser(BasicIndexUtils.ALL_TEXT_FIELD,
				new SsfQueryAnalyzer());
		try {
			q = qp.parse(BasicIndexUtils.UID_FIELD + ":" + uid);
		} catch (ParseException pe) {
			throw new LuceneException(pe.toString());
		}
		updateDocuments(q, fieldname, fieldvalue);
	}

	public void updateDocuments(Query query, String fieldname, String fieldvalue) {

		try {
			updateDocs(query, fieldname, fieldvalue);
		} catch (Exception e) {
			throw new LuceneException("Error updating index [" + indexPath
					+ "]", e);
		}
	}

	public com.sitescape.ef.lucene.Hits search(Query query) {
		return this.search(query, 0, -1);
	}

	public com.sitescape.ef.lucene.Hits search(Query query, int offset, int size) {
		IndexSearcher indexSearcher = null;

		try {
			indexSearcher = LuceneUtil.getSearcher(indexPath);
		} catch (IOException e) {
			throw new LuceneException("Could not open searcher on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			org.apache.lucene.search.Hits hits = indexSearcher.search(query);
			if (size < 0)
				size = hits.length();
			com.sitescape.ef.lucene.Hits tempHits = com.sitescape.ef.lucene.Hits
					.transfer(hits, offset, size);
			tempHits.setTotalHits(hits.length());
			return tempHits;
		} catch (IOException e) {
			throw new LuceneException("Error searching index [" + indexPath
					+ "]", e);
		} finally {
			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
		}
	}

	public com.sitescape.ef.lucene.Hits search(Query query, Sort sort) {
		return this.search(query, sort, 0, -1);
	}

	public com.sitescape.ef.lucene.Hits search(Query query, Sort sort,
			int offset, int size) {
		Hits hits = null;
		IndexSearcher indexSearcher = null;

		try {
			indexSearcher = LuceneUtil.getSearcher(indexPath);
		} catch (IOException e) {
			throw new LuceneException("Could not open searcher on the index ["
					+ this.indexPath + "]", e);
		}

		try {
			if (sort == null)
				hits = indexSearcher.search(query);
			else
				try {
					hits = indexSearcher.search(query, sort);
				} catch (Exception ex) {
					hits = indexSearcher.search(query);
				}
			if (size < 0)
				size = hits.length();
			com.sitescape.ef.lucene.Hits tempHits = com.sitescape.ef.lucene.Hits
					.transfer(hits, offset, size);
			tempHits.setTotalHits(hits.length());
			return tempHits;
		} catch (IOException e) {
			throw new LuceneException("Error searching index [" + indexPath
					+ "]", e);
		} finally {
			try {
				indexSearcher.close();
			} catch (IOException e) {
			}
		}
	}
	/**
	 * Get all the unique tags that this user can see, based on the wordroot passed in.
	 * 
	 * @param query
	 * @param tag
	 * @return
	 * @throws LuceneException
	 */
	public ArrayList getTags(Query query, String tag) throws LuceneException {
		IndexReader indexReader = null;
		IndexSearcher indexSearcher = null;
		TreeSet results = new TreeSet();
		ArrayList resultTags = new ArrayList();

		try {
			indexReader = LuceneUtil.getReader(indexPath);
			indexSearcher = LuceneUtil.getSearcher(indexReader);
		} catch (IOException e) {
			throw new LuceneException("Could not open reader on the index ["
					+ this.indexPath + "]", e);
		}
		try {
			final BitSet userDocIds = new BitSet(indexReader.maxDoc());
			indexSearcher.search(query, new HitCollector() {
				public void collect(int doc, float score) {
					userDocIds.set(doc);
				}
			});

			String[] fields = { BasicIndexUtils.TAG_FIELD,
					BasicIndexUtils.ACL_TAG_FIELD };
			int preTagLength = 0;
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].equalsIgnoreCase("_aclTagField")) {
					String preTag = BasicIndexUtils.TAG_ACL_PRE
							+ RequestContextHolder.getRequestContext()
									.getUserId().toString() + BasicIndexUtils.TAG;
					preTagLength = preTag.length();
					tag = preTag + tag;
				}
				TermEnum enumerator = indexReader
						.terms(new Term(fields[i], tag));

				TermDocs termDocs = indexReader.termDocs();
				if (enumerator.term() == null) {
					// no matches
					return null;
				}
				do {
					Term term = enumerator.term();
					// stop when the field is no longer the field we're looking
					// for, or, when
					// the term doesn't startwith the string we're matching.
					if (term.field().compareTo(fields[i]) != 0
							|| !term.text().startsWith(tag)) {
						break; // no longer in '_tagField' field
					}
					termDocs.seek(enumerator);
					while (termDocs.next()) {
						if (userDocIds.get((termDocs.doc()))) {
							// Add term.text to results
							String matchingTerm = term.text();
							results.add(term.text().substring(preTagLength));
							break;
						}
					}
				} while (enumerator.next());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}

		Iterator iter = results.iterator();
		while (iter.hasNext())
			resultTags.add(iter.next());

		return resultTags;

	}
	
	
	public void flush() {
		// Because Liferay's Lucene functions (on which this implementation
		// is based) are atomic in that it flushes out after each operation,
		// there is no separate flush to perform. Nothing to do.
	}

	public void optimize() {
		IndexWriter indexWriter = null;
		synchronized (SyncObj) {
			try {
				indexWriter = LuceneUtil.getWriter(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				indexWriter.optimize();
			} catch (IOException e) {
				throw new LuceneException(
						"Could not add document to the index [" + indexPath
								+ "]", e);
			} finally {
				try {
					indexWriter.close();
				} catch (IOException e) {
				}
			}
		}
	}

	public void close() {
		// Nothing to do
	}

	private int deleteDocs(org.apache.lucene.search.Hits hits) {
		int length = hits.length();

		if (length > 0) {
			IndexReader indexReader = null;
			try {
				indexReader = LuceneUtil.getReader(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open reader on the index [" + this.indexPath
								+ "]", e);
			}

			try {
				for (int i = 0; i < length; i++) {
					int docId = hits.id(i);
					indexReader.deleteDocument(docId);
				}
			} catch (IOException e) {
				throw new LuceneException(
						"Could not delete documents from the index ["
								+ indexPath + "]", e);
			} finally {
				try {
					indexReader.close();
				} catch (IOException e) {
				}
			}
		}

		return length;
	}

	private void updateDocs(Query q, String fieldname, String fieldvalue) {
		synchronized (LocalLuceneSession.class) {
			IndexUpdater updater = null;
			// first Optimize the index.
			IndexWriter indexWriter = null;

			try {
				synchronized (SyncObj) {
					indexWriter = LuceneUtil.getWriter(indexPath);
				}
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open writer on the index [" + this.indexPath
								+ "]", e);
			}
			try {
				indexWriter.optimize();
				indexWriter.close();

				Directory indDir = FSDirectory.getDirectory(indexPath, false);
				updater = new IndexUpdater(indDir);
				DocumentSelection docsel = updater.createDocSelection(q);
				updater.updateField(new Field(fieldname, fieldvalue,
						Field.Store.NO, Field.Index.TOKENIZED),
						new SsfQueryAnalyzer(), docsel);
				updater.close();
			} catch (IOException ioe) {
				throw new LuceneException(
						"Could not update fields on the index ["
								+ this.indexPath + " ], query is: "
								+ q.toString() + " field: " + fieldname);
			} finally {
				try {
					updater.close();
				} catch (Exception e) {}
			}
		}
	}

	private ArrayList getUserUniqueTags(Query aclQuery, String tag, int id) {
		ArrayList resultTags = new ArrayList();
		TreeSet results = new TreeSet();
		try {
			IndexReader reader = null;
			try {
				reader = LuceneUtil.getReader(indexPath);
			} catch (IOException e) {
				throw new LuceneException(
						"Could not open reader on the index [" + this.indexPath
								+ "]", e);
			}
			IndexSearcher searcher = new IndexSearcher(reader);
			Query query = null;

			try {
				QueryParser qp = new QueryParser("text",
						new WhitespaceAnalyzer());
				qp.setDefaultOperator(QueryParser.OR_OPERATOR);
				query = qp.parse(BasicIndexUtils.ENTRY_ACL_FIELD + ":" + id + " OR " + BasicIndexUtils.FOLDER_ACL_FIELD +":" + id);
			} catch (Exception qe) {
				System.out.println(qe.toString());
			}

			final BitSet userDocIds = new BitSet(reader.maxDoc());

			searcher.search(query, new HitCollector() {
				public void collect(int doc, float score) {
					userDocIds.set(doc);
				}
			});

			String[] fields = { BasicIndexUtils.TAG_FIELD, BasicIndexUtils.ACL_TAG_FIELD };
			int preTagLength = 0;
			for (int i = 0; i < fields.length; i++) {
				if (fields[i].equalsIgnoreCase("_aclTagField")) {
					String preTag = "ACL" + id + "TAG";
					preTagLength = preTag.length();
					tag = preTag + tag;
				}
				TermEnum enumerator = reader.terms(new Term(fields[i], tag));

				TermDocs termDocs = reader.termDocs();
				if (enumerator.term() == null) {
					// no matches
					return null;
				}
				do {
					Term term = enumerator.term();
					// stop when the field is no longer the field we're looking
					// for, or, when
					// the term doesn't startwith the string we're matching.
					if (term.field().compareTo(fields[i]) != 0
							|| !term.text().startsWith(tag)) {
						break; // no longer in '_tagField' field
					}
					termDocs.seek(enumerator);
					while (termDocs.next()) {
						if (userDocIds.get((termDocs.doc()))) {
							// Add term.text to results
							String matchingTerm = term.text();
							results.add(term.text().substring(preTagLength));
							break;
						}
					}
				} while (enumerator.next());
			}
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		Iterator iter = results.iterator();
        while (iter.hasNext())
           resultTags.add(iter.next());
		return resultTags;
	}
}
