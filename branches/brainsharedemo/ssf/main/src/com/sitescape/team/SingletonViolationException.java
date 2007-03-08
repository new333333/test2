package com.sitescape.team;

import com.sitescape.team.exception.UncheckedException;

/**
 * @author Jong Kim
 *
 */
public class SingletonViolationException extends UncheckedException {
    public SingletonViolationException(Class clazz) {
        super("Could not instantiate " + clazz.getName() + " more than once");
    }
}
