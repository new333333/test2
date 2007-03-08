package com.sitescape.team.module.definition.notify;

import java.util.Map;
import org.dom4j.Element;

import com.sitescape.team.domain.DefinableEntity;

/**
 *
 * @author Janet Mccann
 */
public interface NotifyBuilder {
    
    public boolean buildElement(Element element, DefinableEntity entity, Notify notifyDef, String dataElemName, Map args);
}
