package com.sitescape.ef.util;

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
