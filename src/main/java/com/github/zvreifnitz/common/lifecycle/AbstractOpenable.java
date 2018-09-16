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

import com.github.zvreifnitz.common.utils.Preconditions;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public abstract class AbstractOpenable implements Openable {

    private final Openable[] dependencies;
    private final Map<Openable, Boolean> owners;
    private final EmptyOpenable self;

    private volatile boolean init;
    private volatile boolean open;

    protected AbstractOpenable(final Openable... dependencies) {
        this.dependencies = compact(dependencies);
        this.owners = new HashMap<>();
        this.self = new EmptyOpenable();
    }

    @Override
    public final boolean isInit() {
        return this.isInitAsDependency(this.self);
    }

    @Override
    public final boolean isOpen() {
        return this.isOpenAsDependency(this.self);
    }

    @Override
    public final void init() {
        this.initAsDependency(this.self);
    }

    @Override
    public final void open() {
        this.openAsDependency(this.self);
    }

    @Override
    public final void close() {
        this.closeAsDependency(this.self);
    }

    @Override
    public final boolean isInitAsDependency(final Openable owner) {
        Preconditions.checkNotNull(owner, "owner");
        synchronized (this.owners) {
            return this.owners.containsKey(owner);
        }
    }

    @Override
    public final boolean isOpenAsDependency(final Openable owner) {
        Preconditions.checkNotNull(owner, "owner");
        synchronized (this.owners) {
            return Boolean.TRUE.equals(this.owners.get(owner));
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
            if (!this.init) {
                this.performInit();
                this.init = true;
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
                this.throwNotInit();
            }
            if (Boolean.TRUE.equals(existing)) {
                return;
            }
            if (!this.open) {
                this.performOpen();
                this.open = true;
            }
            this.owners.put(owner, Boolean.TRUE);
        }
    }

    @Override
    public final void closeAsDependency(final Openable owner) {
        Preconditions.checkNotNull(owner, "owner");
        synchronized (this.owners) {
            if (!Boolean.TRUE.equals(this.owners.get(owner))) {
                return;
            }
            this.owners.put(owner, Boolean.FALSE);
            if (this.open && this.noneOpen()) {
                this.open = false;
                this.performClose();
            }
        }
        if (this.dependencies != null) {
            for (int i = (this.dependencies.length - 1); i >= 0; i--) {
                this.dependencies[i].closeAsDependency(owner);
            }
        }
    }

    private boolean noneOpen() {
        boolean anyOpen = false;
        for (final Boolean open : this.owners.values()) {
            anyOpen |= Boolean.TRUE.equals(open);
        }
        return !anyOpen;
    }

    protected final void checkInit() {
        if (!this.init) {
            throwNotInit();
        }
    }

    protected final void checkOpen() {
        if (!this.open) {
            this.throwNotOpen();
        }
    }

    protected final void throwNotInit() {
        Preconditions.checkState(false, "Openable is not initialised");
    }

    protected final void throwNotOpen() {
        Preconditions.checkState(false, "Openable is not open");
    }

    protected void performInit() {
    }

    protected void performOpen() {
    }

    protected void performClose() {
    }

    private static Openable[] compact(final Openable[] input) {
        if ((input == null) || (input.length == 0)) {
            return null;
        }
        final Openable[] result = Arrays.stream(input).filter(Objects::nonNull).toArray(Openable[]::new);
        return (((result == null) || (result.length == 0)) ? null : result);
    }
}
