package com.sitescape.ef.security.function;

import java.util.List;

import com.sitescape.ef.NotSupportedException;
import com.sitescape.ef.NoObjectByTheIdException;

/**
 *
 * @author Jong Kim
 */
public interface FunctionManager {
    
    public void addFunction(Function function);
    
    public void deleteFunction(Function function) throws NotSupportedException;
    
    public void updateFunction(Function function);
    
    public Function getFunction(Long zoneId, Long id) throws NoObjectByTheIdException;
   
    public List findFunctions(Long zoneId);
    
    public List findFunctions(Long zoneId, WorkAreaOperation workAreaOperation);
}
