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
