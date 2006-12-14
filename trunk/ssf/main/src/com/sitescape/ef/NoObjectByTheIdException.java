package com.sitescape.ef;

import com.sitescape.ef.exception.UncheckedCodedException;

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
