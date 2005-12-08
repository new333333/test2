package com.sitescape.ef.lucene.server;

import java.util.LinkedList;
import java.util.HashMap;


/**
 * Title: BatchQueue
 * Description: This class represents a queue of objects in which
 *              elements are removed in the same order they were
 *              entered.This is often referred to as first-in-first-out
 *              (FIFO).
 * Copyright:    Copyright (c) 2005
 * Company: SiteScape, Inc.
 * @author Roy Klein
 * @version 1.0
 */

public class BatchQueue extends LinkedList
{

    // This collection will be used to make sure there aren't any duplicates in the BatchQueue
    HashMap keymap;

    /**
     * Constructor - Set up a hashmap for fast lookup of keys already in the
     * linked list, and initialize the linked list.
     */
    public BatchQueue ()
    {
        super();
        keymap = new HashMap();
    }

    /**
     * Add the element to the linked list, making sure that there
     * isn't already an element with the same UID in the list.
     *
     * @param element
     */
    public synchronized Object enqueue (Object element)
    {
        if (element instanceof SsfDocument) {
            String uid = ((SsfDocument) element).getUID();
            if (keymap.containsKey(uid)) {
                Object remelem = keymap.remove(uid);
                remove(remelem);
            }
            keymap.put(uid, element);
            this.add(element);
        }
        return element;
    }

    /**
     * Take the first element off the front of the list. (This
     * is a FIFO queue.)
     */
    public synchronized Object dequeue ()
    {
        if (size()== 0)
            throw new EmptyQueueException() ;
        Object element = removeFirst();
        if (element instanceof SsfDocument) {
            String uid = ((SsfDocument) element).getUID();
            keymap.remove(uid);
        }
        return element;
    }

    /**
     * Remove the element off the list that matches
     * the uid.
     *
     * @param uid
     */
    public synchronized int dequeue (String uid)
    {
        if (size()== 0)
            return 0;
        Object entry = keymap.remove(uid);
        if (entry != null) {
            this.remove(entry);
            return 1;
        }
        return 0;
    }

    /**
     * Convenience function to check if a given uid is already
     * on the linked list.
     *
     * @param uid
     */
    public boolean inTheQueue(String uid) {
        return keymap.containsKey(uid);
    }


}
