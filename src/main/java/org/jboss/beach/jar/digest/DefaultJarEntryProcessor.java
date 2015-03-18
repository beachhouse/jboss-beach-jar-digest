/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package org.jboss.beach.jar.digest;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.jboss.beach.jar.digest.MessageDigestHelper.createMessageDigest;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class DefaultJarEntryProcessor implements JarEntryProcessor {
    @Override
    public byte[] apply(final JarEntry entry, final JarInputStream in) {
        final MessageDigest digest = createMessageDigest();
        try {
            final byte[] buf = new byte[4096];
            int l;
            while ((l = in.read(buf)) > 0)
                digest.update(buf, 0, l);
            return digest.digest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
