/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
 * 
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 * 
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 * 
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 * 
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.util;

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
    
    /*
    public void setConfigFiles(String[] cFiles) {
    	if(cFiles.length > 1)
    		throw new IllegalArgumentException("Current implementation does not support multiple config files");
    	else
    		super.setConfigFiles(cFiles);
    }
    */
    
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
