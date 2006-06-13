package com.sitescape.ef.search.local;

import java.io.File;
import java.io.IOException;

import com.sitescape.ef.search.AbstractLuceneSessionFactory;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.util.FileHelper;

/**
 * @author Jong Kim
 *
 */
public class LocalLuceneSessionFactory extends AbstractLuceneSessionFactory {
    
	private String indexRootDir;

	public LuceneSession openSession(String indexName) {
		String indexDirPath = getIndexDirPath(indexName);
		
		try {
			FileHelper.mkdirsIfNecessary(indexDirPath);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		
        return new LocalLuceneSession(indexDirPath);
    }
	
	public void setIndexRootDir(String indexRootDir) {
		if(indexRootDir.endsWith("/"))
			this.indexRootDir = indexRootDir;
		else
			this.indexRootDir = indexRootDir + "/";
	}
	
	public String getIndexRootDir() {
		return indexRootDir;
	}
	
	private String getIndexDirPath(String indexName) {
		return indexRootDir + indexName;
	}
}
