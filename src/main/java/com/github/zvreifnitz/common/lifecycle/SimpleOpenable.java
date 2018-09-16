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

public final class SimpleOpenable extends AbstractOpenable {
    private final Runnable initAction;
    private final Runnable openAction;
    private final Runnable closeAction;

    public SimpleOpenable(final Runnable closeAction) {
        this(null, null, closeAction);
    }

    public SimpleOpenable(final Runnable openAction, final Runnable closeAction) {
        this(null, openAction, closeAction);
    }

    public SimpleOpenable(final Runnable initAction, final Runnable openAction, final Runnable closeAction) {
        this.initAction = initAction;
        this.openAction = openAction;
        this.closeAction = closeAction;
    }

    @Override
    protected final void performInit() {
        if (this.initAction == null) {
            return;
        }
        this.initAction.run();
    }

    @Override
    protected final void performOpen() {
        if (this.openAction == null) {
            return;
        }
        this.openAction.run();
    }

    @Override
    protected final void performClose() {
        if (this.closeAction == null) {
            return;
        }
        this.closeAction.run();
    }
}
