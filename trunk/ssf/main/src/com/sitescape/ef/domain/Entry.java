package com.sitescape.ef.domain;


/**
 * @author Jong Kim
 * Mostly a marker class
 */
public abstract class Entry extends DefinableEntity  {
 
    public Entry() {
    }

    public Definition getEntryDef() {
    	if (entryDef != null) return entryDef;
    	return getParentBinder().getDefaultEntryDef();
    }

}
