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
package com.sitescape.team.domain;

import java.util.List;
import java.util.Map;


public interface DefinitionArea {
    public Long getDefinitionAreaId();
    
    /**
     * The type of the Definition area. 
     * The value must be between 1 and 16 characters long.
     * 
     * @return
     */
    public String getDefinitionAreaType();
    
    public DefinitionArea getParentDefinitionArea();
    
    public boolean isDefinitionsInherited();
    
    public void setDefinitionsInherited(boolean definitionsInherited);
    public boolean isDefinitionInheritanceSupported();

    public List getViewDefinitions();
    public List getEntryDefinitions();
    public List getDefinitions();
    public void setDefinitions(List definitions);
    public Map getWorkflowAssociations();
    public void setWorkflowAssociations(Map workflowAssociations);
        
   
}