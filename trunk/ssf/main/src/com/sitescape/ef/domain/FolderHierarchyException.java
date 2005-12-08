/*
 * Created on Feb 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.ef.domain;
import com.sitescape.ef.exception.UncheckedCodedException;
/**
 * @author Janet McCann
 *
 */
public class FolderHierarchyException extends UncheckedCodedException {
    private static final String DocshareHierarchyException_ErrorCode = "errorcode.folder.hierarchy.invalid";
    
    public FolderHierarchyException(Long docshareId) {
        super(DocshareHierarchyException_ErrorCode, new Object[] {docshareId});
   }
    public FolderHierarchyException(Long docshareId, String message) {
        super(DocshareHierarchyException_ErrorCode, new Object[] {docshareId}, message);
   }
    public FolderHierarchyException(Long docshareId, String message, Throwable cause) {
        super(DocshareHierarchyException_ErrorCode, new Object[] {docshareId}, message, cause);
   }
    public FolderHierarchyException(Long docshareId, Throwable cause) {
        super(DocshareHierarchyException_ErrorCode, new Object[] {docshareId}, cause);
   }
}
