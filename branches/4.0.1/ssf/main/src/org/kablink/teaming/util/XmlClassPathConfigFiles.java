/**
 * Copyright (c) 1998-2015 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2015 Novell, Inc. All Rights Reserved.
 * 
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2015 Novell, Inc. All Rights Reserved.
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

import org.dom4j.io.SAXReader;

import org.kablink.teaming.util.EntityResolver;
import org.kablink.teaming.util.XmlUtil;

import org.springframework.beans.factory.InitializingBean;

/**
 * ?
 * 
 * @author Jong Kim
 */
public class XmlClassPathConfigFiles extends ClassPathConfigFiles 
	implements InitializingBean {
    
    protected boolean validating = true;
    protected org.dom4j.Document[] docs; 
    
    public boolean isValidating() {
        return validating;
    }
    public void setValidating(boolean validating) {
        this.validating = validating;
    }
    
    @Override
	public void afterPropertiesSet() throws Exception {
        int size = size();
        docs = new org.dom4j.Document[size];
        for(int i = 0; i < size; i++) {
            SAXReader reader = XmlUtil.getSAXReader(validating);  
            if(validating) {
                // The following code turns on XML schema-based validation
                // features specific to Apache Xerces2 parser. Therefore it
                // will not work when a different parser is used. 
                reader.setFeature("http://apache.org/xml/features/validation/schema", true); // Enables XML Schema validation
                reader.setFeature("http://apache.org/xml/features/validation/schema-full-checking",true); // Enables full (if slow) schema checking
            }
            reader.setEntityResolver(new EntityResolver());
            docs[i] = reader.read(this.getAsInputStream(i));
        }
    }
    
    /**
     * Returns the first file as <code>org.dom4j.Document</code>. 
     * 
     * @return
     */
    public org.dom4j.Document getAsDom4jDocument() {
        return getAsDom4jDocument(0);
    }
    
    /**
     * Returns the specified file as <code>org.dom4j.Document</code>. 
     * 
     * @param index
     * @return
     */
    public org.dom4j.Document getAsDom4jDocument(int index) {
        return docs[index];
    }
}
