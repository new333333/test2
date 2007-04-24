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
package com.sitescape.team.security.function;

import java.util.List;

import com.sitescape.team.NotSupportedException;
import com.sitescape.team.NoObjectByTheIdException;

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
