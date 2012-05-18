/*
 * JBoss, Home of Professional Open Source.
 * Copyright (c) 2012, Red Hat, Inc., and individual contributors
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
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.util.jar.JarEntry;
import java.util.jar.JarInputStream;

/**
 * Create a digest for a jar ignoring any timestamped bits.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Main {
    private static BASE64Encoder encoder = new BASE64Encoder();

    public static void main(String[] args) throws Exception {
        // TODO: make the algorithm choice configurable
        final MessageDigest jarDigest = MessageDigest.getInstance("SHA1");
        final MessageDigest digest = MessageDigest.getInstance("SHA1");
        final JarInputStream in = new JarInputStream(new BufferedInputStream(new FileInputStream(args[0])));
        try {
            JarEntry entry;
            while ((entry = in.getNextJarEntry()) != null) {
                // do not hash directories
                if (entry.isDirectory())
                    continue;

                final String name = entry.getName();
                // do not hash information added by jarsigner
                if (name.startsWith("META-INF/")) {
                    if (name.endsWith(".SF") || name.endsWith(".DSA"))
                        continue;
                }
                if (name.equals("META-INF/INDEX.LIST"))
                    continue;

                // do not hash timestamped maven artifacts
                // TODO: make this optional, enabled by default
                if (name.startsWith("META-INF/maven/") && name.endsWith("/pom.properties"))
                    continue;

                System.out.println("Name: " + name);
                digest.reset();
                final byte[] buf = new byte[4096];
                int l;
                while ((l = in.read(buf)) > 0)
                    digest.update(buf, 0, l);
                final byte[] d = digest.digest();
                System.out.println("SHA1-Digest: " + encoder.encode(d));
                System.out.println();
                jarDigest.update(d);
            }
        } finally {
            in.close();
        }
        // TODO: make output format configurable
        System.out.println("Jar-SHA1-Digest: " + encoder.encode(jarDigest.digest()));
    }
}
