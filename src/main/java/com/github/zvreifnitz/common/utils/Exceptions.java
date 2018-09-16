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

package com.github.zvreifnitz.common.utils;

public final class Exceptions {

    public static <T> T throwUnchecked(final Throwable throwable) {
        if (throwable instanceof Error) {
            throw (Error)throwable;
        }
        if (throwable instanceof RuntimeException) {
            throw (RuntimeException)throwable;
        }
        throw new RuntimeException(throwable);
    }

    public static <T> T throwNullPointerException(final String argName) {
        throw new NullPointerException("Argument '" + argName + "' is null");
    }

    public static <T> T throwIllegalStateException(final String msg) {
        throw new IllegalStateException(msg);
    }

    public static <T> T throwRuntimeException(final String msg) {
        throw new RuntimeException(msg);
    }

    public static <T> T thrownUnsupportedMethodException(final String methodName) {
        throw new UnsupportedOperationException("Method '" + methodName + "' is not supported");
    }
}
