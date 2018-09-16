package com.github.zvreifnitz.common.utils;

import com.github.zvreifnitz.common.lifecycle.Openable;

public final class Openables {

    public static <T> boolean isOpenable(final T instance) {
        return (instance instanceof Openable);
    }

    public static <T> void initAsDependency(final T instance, final Openable owner) {
        if (instance instanceof Openable) {
            ((Openable)instance).initAsDependency(owner);
        }
    }

    public static <T> void openAsDependency(final T instance, final Openable owner) {
        if (instance instanceof Openable) {
            ((Openable)instance).openAsDependency(owner);
        }
    }

    public static <T> void closeAsDependency(final T instance, final Openable owner) {
        if (instance instanceof Openable) {
            ((Openable)instance).closeAsDependency(owner);
        }
    }
}
