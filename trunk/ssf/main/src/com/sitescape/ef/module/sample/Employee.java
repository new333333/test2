package com.sitescape.ef.module.sample;

public class Employee implements Comparable {

	private String firstName;
	private String lastName;
	private Integer salary;
	private Integer key;
	
	private int hashCode = Integer.MIN_VALUE;
	
	public Employee() {}
	public Employee(String firstName, String lastName, Integer salary) {
		this.firstName = firstName;
		this.lastName = lastName;
		this.salary = salary;
	}
	public String getFirstName() {
		return firstName;
	}
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	public String getLastName() {
		return lastName;
	}
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	public Integer getSalary() {
		return salary;
	}
	public void setSalary(Integer salary) {
		this.salary = salary;
	}
	public Integer getKey() {
		return key;
	}
	public void setKey(Integer key) {
		this.key = key;
	}
	public void incrementSalary(Integer increment) {
		int salary = this.salary.intValue() + increment.intValue();
		if(salary < 0) salary = 0;
		this.salary = new Integer(salary);
	}
	
    public int compareTo(Object obj) {
        if (obj == null) throw new NullPointerException("Cannot compare to null object");
        if (!(obj instanceof Employee)) throw new ClassCastException("Can only compare to class" + this.getClass().getName());
        if (this.firstName == null || this.lastName == null) throw new NullPointerException("This object is not initialized yet");
        if (this.equals(obj)) return 0;
        Employee employee = (Employee)obj;
        int res = getFirstName().compareTo(employee.getFirstName());
        if (res != 0) return res;
        return getLastName().compareTo(employee.getLastName());
    }

    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (!(obj instanceof Employee)) return false;
        if (this.firstName == null || this.lastName == null) return false;
        Employee employee = (Employee)obj;
        return (this.firstName.equals(employee.getFirstName()) &&
                 this.lastName.equals(employee.getLastName()));
    }
    
    public int hashCode() {
		if (Integer.MIN_VALUE == this.hashCode) {
			String hashStr = this.getClass().getName() + ":" + this.toString();
			this.hashCode = hashStr.hashCode();
		}
		return this.hashCode;
    }
}
