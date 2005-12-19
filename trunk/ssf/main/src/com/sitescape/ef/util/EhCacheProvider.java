package com.sitescape.ef.util;

import java.util.Properties;
import java.io.FileInputStream;
import java.io.File;
import java.net.URL;
import net.sf.ehcache.CacheManager;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hibernate.cache.CacheException;

public class EhCacheProvider extends org.hibernate.cache.EhCacheProvider {
	/**
	 * Callback to perform any necessary initialization of the underlying cache implementation
	 * during SessionFactory construction.
	 *
	 * @param properties current configuration settings.
	 */
	public void start(Properties properties) throws CacheException {
		try {
			//load singleton with our config file
			URL url = this.getClass().getResource("/config/ehcache.xml");
			FileInputStream fs = new FileInputStream(new File(url.getFile()));
			CacheManager.create(fs);
		}
        catch (Exception e) {
        	Log logger = LogFactory.getLog(getClass());
        	logger.error("cache exception " + e.getLocalizedMessage());
        }
        super.start(properties);
 	}
}
