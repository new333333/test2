package com.sitescape.ef.module.definition.notify;

import java.util.Map;
import org.dom4j.Element;
import com.sitescape.ef.domain.Entry;

/**
 *
 * @author Janet Mccann
 */
public interface NotifyBuilder {
    
    public boolean buildElement(Element element, Entry entry, Notify notifyDef, String dataElemName, Map args);
}
