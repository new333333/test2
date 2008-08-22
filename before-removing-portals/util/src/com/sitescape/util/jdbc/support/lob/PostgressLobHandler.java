/*
 * Copyright 2002-2008 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.sitescape.util.jdbc.support.lob;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
/**
 * Handle postgres where sql clobs/blobs are stored as type oid.  
 * OIDs require a transaction which doesn't work with our use of hibernate lazy loading.
 * Additionally OIDs only work with clob/blob interfaces, not getBytes or getStream used by DefaultLobHandler.
 * Deleteing rows with OIDs does not delete the clob/blob objects and I have no idea how to handle that.
 * So, instead we use the postgress text/bytea types to represent large objects.  We supply a postgress hibernate 
 * dialect to do this mapping.  This is acceptable, cause we don't store 'reallybig' files in these fields,
 * and most of them are added to the secondary cache so they have to be read anyway.
 * So, this class is a marker class that is needed by the custom types which try to assume clobs/blobs can
 * be read when needed.
 * @author Janet
 *
 */

public class PostgressLobHandler extends DefaultLobHandler implements NoLazyLobs {

}
