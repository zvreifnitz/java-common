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

public final class EmptyOpenable implements Openable {
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
    public boolean isInitAsDependency(final Openable owner) {
        return false;
    }

    @Override
    public boolean isOpenAsDependency(final Openable owner) {
        return false;
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
