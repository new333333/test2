package com.sitescape.ef.search.local;

import java.io.File;
import java.io.IOException;

import com.sitescape.ef.search.AbstractLuceneSessionFactory;
import com.sitescape.ef.search.LuceneException;
import com.sitescape.ef.search.LuceneSession;
import com.sitescape.ef.util.ConfigPropertyNotFoundException;
import com.sitescape.ef.util.FileHelper;
import com.sitescape.ef.util.SPropsUtil;

/**
 * @author Jong Kim
 *
 */
public class LocalLuceneSessionFactory extends AbstractLuceneSessionFactory {
    
	private String dataRootDir;
	private String subDirName;
	
	public void setDataRootDirProperty(String dataRootDirProperty) 
		throws ConfigPropertyNotFoundException, IOException {
		this.dataRootDir = SPropsUtil.getDirPath(dataRootDirProperty);
	}
	
	public void setSubDirName(String subDirName) {
		this.subDirName = subDirName;
	}

	public LuceneSession openSession(String indexName) {
		String indexDirPath = getIndexDirPath(indexName);
		
		try {
			FileHelper.mkdirsIfNecessary(indexDirPath);
		} catch (IOException e) {
			throw new LuceneException(e);
		}
		
        return new LocalLuceneSession(indexDirPath);
    }
	
	private String getIndexDirPath(String indexName) {
		return dataRootDir + indexName + File.separator + subDirName;
	}
}
