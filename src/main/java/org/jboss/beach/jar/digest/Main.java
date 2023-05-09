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

import java.io.File;
import java.io.IOException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

/**
 * Create a digest for a jar ignoring any timestamped bits.
 *
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Main {
    private static Base64.Encoder encoder = Base64.getEncoder();

    private static byte[] digest(final String fileName, final boolean showEntries) throws NoSuchAlgorithmException, IOException {
        final FileProcessor processor = new DefaultFileProcessor();
        if (showEntries)
            processor.setPostProcessor((n, d) -> {
                System.out.println("Name: " + n);
                System.out.println("SHA1-Digest: " + encoder.encodeToString(d));
                System.out.println();
            });
        return processor.apply(new File(fileName));
    }

    public static void main(String[] args) throws Exception {
        if (args.length == 0) {
            System.err.println("Usage: Main [-s] <file>...");
            System.exit(1);
        }
        boolean showEntries = false;

        for (String fileName : args) {
            // TODO: make sensible options and process them properly
            if (fileName.equals("-s"))
                showEntries = true;
            else {
                // TODO: make output format configurable
                final byte[] digest = digest(fileName, showEntries);
                if (showEntries)
                    System.out.print("Jar-SHA1-Digest: ");
                else
                    System.out.print(fileName + ": ");
                System.out.println(encoder.encodeToString(digest));
            }
        }
    }
}
