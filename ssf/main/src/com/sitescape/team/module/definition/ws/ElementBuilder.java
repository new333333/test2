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
package com.sitescape.team.module.definition.ws;

import org.dom4j.Element;

import com.sitescape.team.domain.DefinableEntity;
import com.sitescape.team.util.AllModulesInjected;

/**
 *
 * @author Joe DeStefano
 */
public interface ElementBuilder {
    
    public boolean buildElement(Element element, DefinableEntity entity, String dataElemName, AllModulesInjected moduleSource);
}
