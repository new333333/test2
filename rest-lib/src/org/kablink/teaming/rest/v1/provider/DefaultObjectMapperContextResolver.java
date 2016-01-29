/**
 * Copyright (c) 1998-2012 Novell, Inc. and its licensors. All rights reserved.
 *
 * This work is governed by the Common Public Attribution License Version 1.0 (the
 * "CPAL"); you may not use this file except in compliance with the CPAL. You may
 * obtain a copy of the CPAL at http://www.opensource.org/licenses/cpal_1.0. The
 * CPAL is based on the Mozilla Public License Version 1.1 but Sections 14 and 15
 * have been added to cover use of software over a computer network and provide
 * for limited attribution for the Original Developer. In addition, Exhibit A has
 * been modified to be consistent with Exhibit B.
 *
 * Software distributed under the CPAL is distributed on an "AS IS" basis, WITHOUT
 * WARRANTY OF ANY KIND, either express or implied. See the CPAL for the specific
 * language governing rights and limitations under the CPAL.
 *
 * The Original Code is ICEcore, now called Kablink. The Original Developer is
 * Novell, Inc. All portions of the code written by Novell, Inc. are Copyright
 * (c) 1998-2012 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2012 Novell, Inc. All Rights Reserved.
 * Attribution Phrase (not exceeding 10 words): [Powered by Kablink]
 * Attribution URL: [www.kablink.org]
 * Graphic Image as provided in the Covered Code
 * [ssf/images/pics/powered_by_icecore.png].
 * Display of Attribution Information is required in Larger Works which are
 * defined in the CPAL as a work which combines Covered Code or portions thereof
 * with code not governed by the terms of the CPAL.
 *
 * NOVELL and the Novell logo are registered trademarks and Kablink and the
 * Kablink logos are trademarks of Novell, Inc.
 */
package org.kablink.teaming.rest.v1.provider;

import org.codehaus.jackson.map.AnnotationIntrospector;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.map.annotate.JsonSerialize;
import org.codehaus.jackson.map.introspect.JacksonAnnotationIntrospector;
import org.codehaus.jackson.xc.JaxbAnnotationIntrospector;

import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;
import java.text.SimpleDateFormat;
import java.util.Locale;

/**
 * User: david
 * Date: 5/22/12
 * Time: 5:00 PM
 */
@Provider
public class DefaultObjectMapperContextResolver implements ContextResolver<ObjectMapper> {
    private ObjectMapper objectMapper;

    public DefaultObjectMapperContextResolver() throws Exception {
        this.objectMapper = new ObjectMapper();
        AnnotationIntrospector jaxb = new JaxbAnnotationIntrospector();
        AnnotationIntrospector jackson = new JacksonAnnotationIntrospector();

        // make de/serializer use JAXB annotations first, then jackson ones
        AnnotationIntrospector pair = new AnnotationIntrospector.Pair(jaxb, jackson);
        this.objectMapper.setSerializationConfig(
                this.objectMapper.getSerializationConfig()
                        .withSerializationInclusion(JsonSerialize.Inclusion.NON_NULL)
                        .withDateFormat(new CustomDateFormat())
                        .withAnnotationIntrospector(pair));
        this.objectMapper.setDeserializationConfig(
                this.objectMapper.getDeserializationConfig()
                        .withDateFormat(new CustomDateFormat())
                        .withAnnotationIntrospector(pair));
    }

    public ObjectMapper getContext(Class<?> aClass) {
        return objectMapper;
    }
}

class CustomDateFormat extends SimpleDateFormat {

    public CustomDateFormat() {
        super("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
    }

    @Override
    public Object clone() {
        return new CustomDateFormat();
    }
}
