package com.sitescape.ef.domain;


/**
 * @author Jong Kim
 *
 */
public abstract class Entry extends DefinableEntity  {

    protected Binder parentBinder;
 
    public Entry() {
    }
 
    /**
     * @hibernate.many-to-one
     * @return
     */
    public Binder getParentBinder() {
   	 return parentBinder;
    }
    public void setParentBinder(Binder parentBinder) {
   	 this.parentBinder = parentBinder;
    }
}
