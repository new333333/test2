package com.sitescape.ef.domain;

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