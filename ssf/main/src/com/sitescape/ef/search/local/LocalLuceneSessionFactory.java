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
    
	private String rootDirPath;
	
    public void setRootDirPath(String rootPath) throws IOException {
		this.rootDirPath = new File(rootPath).getCanonicalPath();
		
		if(!rootDirPath.endsWith(File.separator))
			rootDirPath += File.separator;
		
		FileHelper.mkdirsIfNecessary(rootDirPath);
	}

	public LuceneSession openSession(String indexName) {
		String indexDirPath = rootDirPath + indexName + File.separator;
		
		try {
			FileHelper.mkdirsIfNecessary(indexDirPath);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		
        return new LocalLuceneSession(indexDirPath);
    }
}
