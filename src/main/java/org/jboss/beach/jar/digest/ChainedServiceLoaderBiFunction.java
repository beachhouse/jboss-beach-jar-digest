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

import java.util.ServiceLoader;
import java.util.function.BiFunction;

/**
 * @author <a href="mailto:cdewolf@redhat.com">Carlo de Wolf</a>
 */
public class ChainedServiceLoaderBiFunction<T, U, R> implements BiFunction<T, U, R> {
    private final Iterable<? extends BiFunction<T, U, R>> loader;

    public <X extends BiFunction<T, U, R>> ChainedServiceLoaderBiFunction(Class<X> service) {
        loader = ServiceLoader.load(service);
    }

    @Override
    public R apply(final T t, final U u) {
        for (BiFunction<T, U, R> f : loader) {
            final R result = f.apply(t, u);
            if (result != null)
                return result;
        }
        throw new IllegalStateException("No bi-function available for " + t + ", " + u);
    }
}
