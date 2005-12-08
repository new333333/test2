package com.sitescape.ef.domain;

/**
 * Attachability of a class is enabled by the class implementing the
 * <code>Attachable</code> interface. Classes that do not implement this
 * interface will not be allowed to be attached to another object. 
 * All subtypes of a attachable class are themselves attachable. 
 * 
 * @author Jong Kim
 *
 */
public interface Attachable {
    /**
     * The item can be attached to a single object only and the lifecycle
     * of the item is dependent upon the object to which it is attached. 
     */
    public static final int NON_SHARED = 1;// default
    /**
     * The item can be attached to multiple objects and the lifecycle of
     * attached item is dependent upon the objects to which it is attached.
     * Once the number of objects having the object as an attachment goes down 
     * to zero, the attached object is automatically removed from the system.
     */
    public static final int SHARABLE_DEPENDENT = 2; 
    /**
     * The item can be attached to multiple objects and the lifecycle of 
     * attached item is not dependent upon the objects to which it is attached. 
     * It is expected that the application explicitly manages the lifecycle of 
     * the object. 
     */
    public static final int SHARABLE_INDEPENDENT = 3;
    
    /**
     * Returns one of the following:
     * <p>
     * {@link #NON_SHARED}<br>
     * {@link #SHARABLE_DEPENDENT}<br>
     * {@link #SHARABLE_INDEPENDENT}
     * 
     * @return
     */
    public int getKind();
    public void setKind(int kind);
}
