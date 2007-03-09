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
