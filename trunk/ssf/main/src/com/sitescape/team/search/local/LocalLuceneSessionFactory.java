package com.sitescape.team.search.local;

import java.io.File;
import java.io.IOException;

import com.sitescape.team.search.AbstractLuceneSessionFactory;
import com.sitescape.team.search.LuceneException;
import com.sitescape.team.search.LuceneSession;
import com.sitescape.team.util.Constants;
import com.sitescape.team.util.FileHelper;

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
		if(indexRootDir.endsWith(Constants.SLASH))
			this.indexRootDir = indexRootDir;
		else
			this.indexRootDir = indexRootDir + Constants.SLASH;
	}
	
	public String getIndexRootDir() {
		return indexRootDir;
	}
	
	private String getIndexDirPath(String indexName) {
		return indexRootDir + indexName;
	}
}
