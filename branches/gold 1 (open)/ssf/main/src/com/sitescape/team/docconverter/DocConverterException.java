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
/*
 * Created on Jun 30, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.docconverter;

/**
 * @author Roy Klein
 *
 */

public class DocConverterException extends RuntimeException {
    public DocConverterException() {
        super();
    }
    public DocConverterException(String message) {
        super(message);
    }
    public DocConverterException(String message, Throwable cause) {
        super(message, cause);
    }
    public DocConverterException(Throwable cause) {
        super(cause);
    }
}
