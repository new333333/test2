/**
 * The contents of this file are governed by the terms of your license
 * with SiteScape, Inc., which includes disclaimers of warranties and
 * limitations on liability. You may not use this file except in accordance
 * with the terms of that license. See the license for the specific language
 * governing your rights and limitations under the license.
 *
 * Copyright (c) 2007 SiteScape, Inc.
 *
 */
package com.sitescape.team.util;

import java.io.IOException;

/**
 *
 * @author Jong Kim
 */
public abstract class MergeableXmlClassPathConfigFiles extends XmlClassPathConfigFiles {
    
    private org.dom4j.Document mergedDoc;
    
    public void afterPropertiesSet() throws Exception {
        super.afterPropertiesSet();
        this.mergedDoc = mergeIntoSingleDom4jDocument();
    }
    
    /**
     * Returns the single merged <code>org.dom4j.Document</code>.
     * 
     * @return
     */
    public org.dom4j.Document getAsMergedDom4jDocument() {
        return mergedDoc;
    }
    
    /**
     * Merges the contents of files into a single <code>org.dom4j.Document</code>.
     * There is no universally agreed-upon algorithm for merging multiple DOM
     * trees into a single tree. The manner in which merge occurs is rather 
     * application specific, and therefore the subclass must implement this 
     * method to provide a specific behavior. 
     * 
     * @return
     * @throws org.dom4j.DocumentException
     * @throws IOException
     */
    protected abstract org.dom4j.Document mergeIntoSingleDom4jDocument()
		throws org.dom4j.DocumentException, IOException;
}
