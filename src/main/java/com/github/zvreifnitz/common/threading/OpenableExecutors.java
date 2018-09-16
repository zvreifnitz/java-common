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

package com.github.zvreifnitz.common.threading;

import com.github.zvreifnitz.common.utils.Preconditions;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.ScheduledExecutorService;
import java.util.function.Supplier;

public final class OpenableExecutors {

    public static OpenableExecutorService openableExecutorService(final Supplier<ExecutorService> executorSupplier) {
        return new OpenableExecutorService(Preconditions.checkNotNull(executorSupplier, "executorSupplier"));
    }

    public static ScheduledExecutorService openableScheduledExecutorService(final Supplier<ScheduledExecutorService> executorSupplier) {
        return new OpenableScheduledExecutorService(Preconditions.checkNotNull(executorSupplier, "executorSupplier"));
    }
}
