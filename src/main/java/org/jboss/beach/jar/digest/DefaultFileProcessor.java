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

import sun.misc.BASE64Encoder;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

import static org.jboss.beach.jar.digest.MessageDigestHelper.createMessageDigest;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class DefaultFileProcessor implements FileProcessor {
    private static BASE64Encoder encoder = new BASE64Encoder();

    private final BiFunction<JarEntry, JarInputStream, byte[]> jarEntryProcessor;
    private BiConsumer<String, byte[]> postProcessor;

    public DefaultFileProcessor() {
        this(new ChainedServiceLoaderBiFunction<>(JarEntryProcessor.class));
    }

    public DefaultFileProcessor(final BiFunction<JarEntry, JarInputStream, byte[]> jarEntryProcessor) {
        this.jarEntryProcessor = jarEntryProcessor;
    }

    @Override
    public byte[] apply(final File file) {
        try {
            // TODO: make the algorithm choice configurable
            final MessageDigest jarDigest = createMessageDigest();
            final JarInputStream in = new JarInputStream(new BufferedInputStream(new FileInputStream(file)));
            int numEntries = 0;
            try {
                JarEntry entry;
                final SortedMap<String, byte[]> digests = new TreeMap<>();
                while ((entry = in.getNextJarEntry()) != null) {
                    numEntries++;

                    // do not hash directories
                    if (entry.isDirectory())
                        continue;

                    final String name = entry.getName();
                    final byte[] d = jarEntryProcessor.apply(entry, in);
                    if (d.length == 0)
                        continue;
                    digests.put(name, d);
                }
                for (SortedMap.Entry<String, byte[]> digestEntry : digests.entrySet()) {
                    final byte[] d = digestEntry.getValue();
                    if (postProcessor != null)
                        postProcessor.accept(digestEntry.getKey(), d);
                    jarDigest.update(d);
                }
            } finally {
                in.close();
            }

            if (numEntries == 0) {
                // did not find any entries? then its not a jar file probably (or a zero size one). In any case digest it.
                final InputStream fin = new BufferedInputStream(new FileInputStream(file));
                try {
                    final byte[] buf = new byte[4096];
                    int l;
                    while ((l = fin.read(buf)) > 0)
                        jarDigest.update(buf, 0, l);
                } finally {
                    fin.close();
                }
            }
            return jarDigest.digest();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void setPostProcessor(final BiConsumer<String, byte[]> postProcessor) {
        this.postProcessor = postProcessor;
    }
}
