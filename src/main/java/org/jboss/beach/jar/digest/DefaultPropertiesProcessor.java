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

import java.security.MessageDigest;
import java.util.Properties;

import static org.jboss.beach.jar.digest.MessageDigestHelper.createMessageDigest;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class DefaultPropertiesProcessor implements PropertiesProcessor {
    @Override
    public byte[] apply(Properties properties) {
        final MessageDigest digest = createMessageDigest();
        properties.forEach((k, v) -> {
            digest.update(((String) k).getBytes());
            digest.update(((String) v).getBytes());
        });
        return digest.digest();
    }
}
