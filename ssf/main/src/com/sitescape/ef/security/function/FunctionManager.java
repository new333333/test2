package com.sitescape.ef.security.function;

import java.util.List;

/**
 *
 * @author Jong Kim
 */
public interface FunctionManager {
    
    public void addFunction(Function function);
    
    public void deleteFunction(Function function);
    
    public void updateFunction(Function function);
    
    public List findFunctions(String zoneId);
    
    public List findFunctions(String zoneId, WorkAreaOperation workAreaOperation);
}
