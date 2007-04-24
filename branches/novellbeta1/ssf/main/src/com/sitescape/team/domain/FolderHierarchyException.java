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
 * Created on Feb 8, 2005
 *
 * TODO To change the template for this generated file go to
 * Window - Preferences - Java - Code Style - Code Templates
 */
package com.sitescape.team.domain;
import com.sitescape.team.exception.UncheckedCodedException;
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
