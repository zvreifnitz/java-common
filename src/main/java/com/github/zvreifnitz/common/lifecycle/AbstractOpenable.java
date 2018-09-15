/*
 * (C) Copyright 2017 zvreifnitz
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.github.zvreifnitz.common.lifecycle;

import com.github.zvreifnitz.common.utils.Exceptions;
import com.github.zvreifnitz.common.utils.Preconditions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractOpenable implements Openable {

    private final Openable[] dependencies;
    private final Map<Openable, Boolean> owners;
    private final SelfOpenable self;

    protected AbstractOpenable(final Openable... dependencies) {
        this.dependencies = compact(dependencies);
        this.owners = new HashMap<>();
        this.self = new SelfOpenable();
    }

    @Override
    public final void init() {
        this.initAsDependency(self);
    }

    @Override
    public final void open() {
        this.openAsDependency(self);
    }

    @Override
    public final void close() {
        this.closeAsDependency(self);
    }

    @Override
    public final boolean isInit() {
        synchronized (this.owners) {
            return this.owners.containsKey(self);
        }
    }

    @Override
    public final boolean isOpen() {
        synchronized (this.owners) {
            return Boolean.TRUE.equals(this.owners.get(self));
        }
    }

    @Override
    public final void initAsDependency(final Openable owner) {
        Preconditions.checkNotNull(owner, "owner");
        if (this.dependencies != null) {
            for (int i = 0; i < this.dependencies.length; i++) {
                this.dependencies[i].initAsDependency(owner);
            }
        }
        synchronized (this.owners) {
            if (this.owners.containsKey(owner)) {
                return;
            }
            if (this.owners.size() == 0) {
                try {
                    this.performInit();
                } catch (final Exception exc) {
                    Exceptions.throwUnchecked(exc);
                }
            }
            this.owners.put(owner, Boolean.FALSE);
        }
    }

    @Override
    public final void openAsDependency(final Openable owner) {
        Preconditions.checkNotNull(owner, "owner");
        if (this.dependencies != null) {
            for (int i = 0; i < this.dependencies.length; i++) {
                this.dependencies[i].openAsDependency(owner);
            }
        }
        synchronized (this.owners) {
            final Boolean existing = this.owners.get(owner);
            if (existing == null) {
                throw new RuntimeException("Openable is not initialised");
            }
            if (Boolean.TRUE.equals(existing)) {
                return;
            }
            if (this.noneOpen()) {
                try {
                    this.performOpen();
                } catch (final Exception exc) {
                    Exceptions.throwUnchecked(exc);
                }
            }
            this.owners.put(owner, Boolean.TRUE);
        }
    }

    @Override
    public final void closeAsDependency(final Openable owner) {
        Preconditions.checkNotNull(owner, "owner");
        Exception firstException = null;
        synchronized (this.owners) {
            if (!Boolean.TRUE.equals(this.owners.get(owner))) {
                return;
            }
            this.owners.put(owner, Boolean.FALSE);
            if (this.noneOpen()) {
                try {
                    this.performClose();
                } catch (final Exception exc) {
                    firstException = exc;
                }
            }
        }
        if (this.dependencies != null) {
            for (int i = (this.dependencies.length - 1); i >= 0; i--) {
                try {
                    this.dependencies[i].closeAsDependency(owner);
                } catch (final Exception exc) {
                    firstException = ((firstException != null) ? firstException : exc);
                }
            }
        }
        if (firstException != null) {
            Exceptions.throwUnchecked(firstException);
        }
    }

    private boolean noneOpen() {
        boolean result = false;
        for (final Boolean open : this.owners.values()) {
            result |= Boolean.TRUE.equals(open);
        }
        return result;
    }

    protected void performInit() throws Exception {
    }

    protected void performOpen() throws Exception {
    }

    protected void performClose() throws Exception {
    }

    private static Openable[] compact(final Openable[] input) {
        if ((input == null) || (input.length == 0)) {
            return null;
        }
        final Openable[] result = Arrays.stream(input).filter(Objects::nonNull).toArray(Openable[]::new);
        return (((result == null) || (result.length == 0)) ? null : result);
    }

    private final static class SelfOpenable implements Openable {

        @Override
        public boolean isInit() {
            return false;
        }

        @Override
        public boolean isOpen() {
            return false;
        }

        @Override
        public void init() {
        }

        @Override
        public void open() {
        }

        @Override
        public void close() {
        }

        @Override
        public void initAsDependency(final Openable owner) {
        }

        @Override
        public void openAsDependency(final Openable owner) {
        }

        @Override
        public void closeAsDependency(final Openable owner) {
        }
    }
}
