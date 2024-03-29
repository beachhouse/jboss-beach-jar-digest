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
import java.util.Base64;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class Mockup {
    private static <T> T[] array(final T... t) {
        return t;
    }
    private static Base64.Encoder encoder = Base64.getEncoder();

    public static void main(String[] args) throws Exception {
        //Main.main(array("target/jboss-beach-jar-digest-0.1.0-SNAPSHOT.jar"));
        final File file = new File("target/jboss-beach-jar-digest-0.1.0-SNAPSHOT.jar");
        final FileProcessor processor = new DefaultFileProcessor();
        final byte[] result = processor.apply(file);
        System.out.println(encoder.encodeToString(result));
    }
}
