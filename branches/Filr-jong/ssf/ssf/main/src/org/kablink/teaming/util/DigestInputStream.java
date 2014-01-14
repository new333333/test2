/**
 * Copyright (c) 1998-2009 Novell, Inc. and its licensors. All rights reserved.
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
 * (c) 1998-2009 Novell, Inc. All Rights Reserved.
 *
 * Attribution Information:
 * Attribution Copyright Notice: Copyright (c) 1998-2009 Novell, Inc. All Rights Reserved.
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
package org.kablink.teaming.util;

import org.apache.commons.codec.binary.Hex;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * User: david
 * Date: 10/16/12
 * Time: 12:29 PM
 */
public class DigestInputStream extends FilterInputStream {
    private MessageDigest digester;

    public DigestInputStream(InputStream in) {
        this(in, buildDigest("MD5"));
    }

    public DigestInputStream(InputStream in, String algorithm) throws NoSuchAlgorithmException {
        this(in, MessageDigest.getInstance(algorithm));
    }

    public DigestInputStream(InputStream in, MessageDigest digester) {
        super(in);
        this.digester = digester;
    }

    @Override
    public int read() throws IOException {
        int b = in.read();
        if (b>=0) {
            digester.update((byte) b);
        }
        return b;
    }

    @Override
    public int read(byte[] b) throws IOException {
        int ret = in.read(b);
        if (ret>0) {
            digester.update(b, 0, ret);
        }
        return ret;
    }

    @Override
    public int read(byte[] b, int off, int len) throws IOException {
        int ret = in.read(b, off, len);
        if (ret>0) {
            digester.update(b, off, ret);
        }
        return ret;
    }

    public byte [] getDigestBytes() {
        return digester.digest();
    }

    public String getDigest() {
        return new String(Hex.encodeHex(getDigestBytes()));
    }

    private static MessageDigest buildDigest(String algorithm) {
        try {
            return MessageDigest.getInstance(algorithm);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
