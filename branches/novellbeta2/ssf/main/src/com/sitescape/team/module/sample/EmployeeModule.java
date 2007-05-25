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
package com.sitescape.team.module.sample;

import java.util.SortedSet;

public interface EmployeeModule {

    public Employee getEmployee (Integer key);
    
    public SortedSet getAllEmployees ();
    
    public int addEmployee (Employee employee);

    public int addEmployee (String author, String title, Integer count);

    public void incrementSalary(Integer key, Integer increment) throws SalaryMaxedOutException;
    
    public void updateEmployee (Employee employee);

    public void deleteEmployee (Integer key);

    public void deleteEmployee (Employee employee);
}
