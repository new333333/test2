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

import org.dom4j.Document;
import org.dom4j.DocumentException;

/**
 *
 * @author Jong Kim
 */
public class DefaultMergeableXmlClassPathConfigFiles extends MergeableXmlClassPathConfigFiles {

    /**
     * This method overrides {@link MergeableXmlClassPathConfigFile#mergeIntoSingleDom4jDocument}
     */
    protected Document mergeIntoSingleDom4jDocument() throws DocumentException, IOException {
        // TODO This must be implemented propertly - For now, it simply returns 
        // the very first (and the only) config file.
        return getAsDom4jDocument();
    }
}
