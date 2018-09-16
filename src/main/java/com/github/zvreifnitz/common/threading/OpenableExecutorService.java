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

import com.github.zvreifnitz.common.lifecycle.AbstractOpenable;
import com.github.zvreifnitz.common.lifecycle.EmptyOpenable;
import com.github.zvreifnitz.common.lifecycle.Openable;
import com.github.zvreifnitz.common.utils.Exceptions;
import com.github.zvreifnitz.common.utils.Openables;
import com.github.zvreifnitz.common.utils.Preconditions;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.*;
import java.util.function.Supplier;

public final class OpenableExecutorService extends AbstractOpenable implements ExecutorService {

    private final Supplier<ExecutorService> executorSupplier;
    private final EmptyOpenable self;

    private volatile ExecutorService executor;

    OpenableExecutorService(final Supplier<ExecutorService> executorSupplier) {
        this.executorSupplier = Preconditions.checkNotNull(executorSupplier, "executorSupplier");
        this.self = new EmptyOpenable();
    }

    @Override
    protected void performInit() {
        final ExecutorService executorService = Preconditions.checkNotNull(this.executorSupplier.get(), "executor");
        Openables.initAsDependency(executorService, this.self);
        this.executor = executorService;
    }

    @Override
    protected void performOpen() {
        Openables.openAsDependency(this.executor, this.self);
    }

    @Override
    protected void performClose() {
        final ExecutorService executorService = this.executor;
        this.executor = null;
        if (executorService == null) {
            return;
        }
        if (Openables.isOpenable(executorService)) {
            Openables.closeAsDependency(executorService, this.self);
        } else {
            executorService.shutdown();
            while (!executorService.isTerminated()) {
                try {
                    executorService.awaitTermination(1, TimeUnit.SECONDS);
                } catch (final Exception ignored) {
                }
            }
        }
    }

    @Override
    public void shutdown() {
        Exceptions.thrownUnsupportedMethodException("shutdown");
    }

    @Override
    public List<Runnable> shutdownNow() {
        return Exceptions.thrownUnsupportedMethodException("shutdownNow");
    }

    @Override
    public boolean isShutdown() {
        return Exceptions.thrownUnsupportedMethodException("isShutdown");
    }

    @Override
    public boolean isTerminated() {
        return Exceptions.thrownUnsupportedMethodException("isTerminated");
    }

    @Override
    public boolean awaitTermination(final long timeout, final TimeUnit unit) throws InterruptedException {
        return Exceptions.thrownUnsupportedMethodException("awaitTermination");
    }

    @Override
    public <T> Future<T> submit(final Callable<T> task) {
        return this.getExecutor().submit(task);
    }

    @Override
    public <T> Future<T> submit(final Runnable task, final T result) {
        return this.getExecutor().submit(task, result);
    }

    @Override
    public Future<?> submit(final Runnable task) {
        return this.getExecutor().submit(task);
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks) throws InterruptedException {
        return this.getExecutor().invokeAll(tasks);
    }

    @Override
    public <T> List<Future<T>> invokeAll(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException {
        return this.getExecutor().invokeAll(tasks, timeout, unit);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks) throws InterruptedException, ExecutionException {
        return this.getExecutor().invokeAny(tasks);
    }

    @Override
    public <T> T invokeAny(final Collection<? extends Callable<T>> tasks, final long timeout, final TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        return this.getExecutor().invokeAny(tasks, timeout, unit);
    }

    @Override
    public void execute(final Runnable command) {
        this.getExecutor().execute(command);
    }

    private ExecutorService getExecutor() {
        this.checkOpen();
        final ExecutorService result = this.executor;
        if (result == null) {
            this.throwNotOpen();
        }
        return result;
    }
}
