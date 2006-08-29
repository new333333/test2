/*
 * $Header$
 * $Revision: 208549 $
 * $Date: 2005-02-28 12:25:44 -0500 (Mon, 28 Feb 2005) $
 *
 * ====================================================================
 *
 * Copyright 1999-2002 The Apache Software Foundation 
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.apache.slide.macro;

/**
 * Macro parameters.
 *
 */
import java.util.HashMap;
import java.util.Map;

public class MacroParameters {
    
    private static final String RECURSIVE = "recursive";
    private static final String OVERWRITE = "overwrite";
    private static final String DELETE_CREATE = "deleteCreate";
    
    private Map parameters = new HashMap();
    
    /**
     * Constructor.
     */
    public MacroParameters() {
        this(true, false);
    }
    
    /**
     * Constructor.
     *
     * @param recursive True if the macro is recursive
     * @param overwrite True if we should try to overwrite stuff
     */
    public MacroParameters(boolean recursive, boolean overwrite) {
        this(recursive, overwrite, overwrite);
    }
    
    /**
     * Constructor.
     *
     * @param recursive True if the macro is recursive
     * @param overwrite True if we should try to overwrite stuff
     * @param deleteCreate if true, and if overwrite=true, resource at destination is
     *                     deleted first and a new resource is created at detination
     *
     */
    public MacroParameters(boolean recursive, boolean overwrite, boolean deleteCreate) {
        setBooleanParameter( RECURSIVE, recursive );
        setBooleanParameter( OVERWRITE, overwrite );
        setBooleanParameter( DELETE_CREATE, (overwrite && deleteCreate) );
    }
    
    /**
     * Recursive accessor.
     *
     * @return boolean True if the macro is recursive
     */
    public boolean isRecursive() {
        return getBooleanParameter( RECURSIVE );
    }
    
    /**
     * Overwrite accessor.
     *
     * @return boolean True if the macro will overwrite any items on the
     * destination (may not apply to all macros)
     */
    public boolean isOverwrite() {
        return getBooleanParameter( OVERWRITE );
    }
    
    /**
     * DeleteCreate accessor
     *
     * @return   True if the macro will overwrite any items on the
     * destination by 1st deleting resources and then creating new
     * resources at detination
     */
    public boolean isDeleteCreate() {
        return getBooleanParameter( DELETE_CREATE );
    }
    
    public void setParameter( String name, Object value ) {
        parameters.put( name, value );
    }
    
    public void setBooleanParameter( String name, boolean value ) {
        parameters.put( name, (value ? Boolean.TRUE : Boolean.FALSE) );
    }
    
    public Object getParameter( String name ) {
        return parameters.get( name );
    }
    
    public boolean getBooleanParameter( String name ) {
        return ((Boolean)parameters.get(name)).booleanValue();
    }
}
