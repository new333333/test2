package com.sitescape.ef.search.local;

import java.io.File;

import com.sitescape.ef.search.AbstractLuceneSessionFactory;
import com.sitescape.ef.search.LuceneSession;

/**
 * @author Jong Kim
 *
 */
public class LocalLuceneSessionFactory extends AbstractLuceneSessionFactory {
    
	private String rootDirPath;
	
    public void setRootDirPath(String rootDirPath) {
    	if(!rootDirPath.endsWith(File.separator))
    		rootDirPath += File.separator;
		this.rootDirPath = rootDirPath;
	}

	public LuceneSession openSession(String indexName) {
        return new LocalLuceneSession(rootDirPath + indexName + File.separator);
    }
}
