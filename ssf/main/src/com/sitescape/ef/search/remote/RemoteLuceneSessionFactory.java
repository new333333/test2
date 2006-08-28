package com.sitescape.ef.search.remote;

import java.io.File;

import com.sitescape.ef.search.AbstractLuceneSessionFactory;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.util.Constants;

/**
 *
 * @author Jong Kim
 */
public class RemoteLuceneSessionFactory extends AbstractLuceneSessionFactory {

	private String rootDirPath;		// unused, but needed by the local session...
	private String indexRootDir;
	
    public void setRootDirPath(String rootDirPath) {
    	if(!rootDirPath.endsWith(File.separator))
    		rootDirPath += File.separator;
		this.rootDirPath = rootDirPath;
	}
    
    public LuceneSession openSession(String indexName) throws LuceneException {
        return new RemoteLuceneSession(indexName);
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
}
