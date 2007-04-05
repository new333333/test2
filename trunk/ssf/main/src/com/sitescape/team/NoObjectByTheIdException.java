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
package com.sitescape.team;

import com.sitescape.team.exception.UncheckedCodedException;

/**
 * @author Jong Kim
 *
 */
public class NoObjectByTheIdException extends UncheckedCodedException {
    public NoObjectByTheIdException(String errorCode, String objId) {
        super(errorCode, new Object[] {objId});
    }
    public NoObjectByTheIdException(String errorCode, String objId, String message) {
        super(errorCode, new Object[] {objId}, message);
    }
    public NoObjectByTheIdException(String errorCode, String objId, String message, Throwable cause) {
        super(errorCode, new Object[] {objId}, message, cause);
    }
    public NoObjectByTheIdException(String errorCode, String objId, Throwable cause) {
        super(errorCode, new Object[] {objId}, cause);
    }
    public NoObjectByTheIdException(String errorCode, Long objId) {
        super(errorCode, new Object[] {objId});
    }
    public NoObjectByTheIdException(String errorCode, Long objId, String message) {
        super(errorCode, new Object[] {objId}, message);
    }
    public NoObjectByTheIdException(String errorCode, Long objId, String message, Throwable cause) {
        super(errorCode, new Object[] {objId}, message, cause);
    }
    public NoObjectByTheIdException(String errorCode, Long objId, Throwable cause) {
        super(errorCode, new Object[] {objId}, cause);
    }
    public NoObjectByTheIdException(String errorCode, Object[] ids) {
        super(errorCode, ids);
    }
    public NoObjectByTheIdException(String errorCode, Object[] ids, String message) {
        super(errorCode, ids, message);
    }
    public NoObjectByTheIdException(String errorCode, Object[] ids, String message, Throwable cause) {
        super(errorCode, ids, message, cause);
    }
    public NoObjectByTheIdException(String errorCode, Object[] ids, Throwable cause) {
        super(errorCode, ids, cause);
    }
 }
