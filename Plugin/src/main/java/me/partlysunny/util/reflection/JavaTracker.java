package me.partlysunny.util.reflection;

import java.util.Optional;

/**
 * NOT BY ME!!! <a href="https://gist.github.com/Lauriichan/294c64b63067dcb6a9a8658f2d040256">https://gist.github.com/Lauriichan/294c64b63067dcb6a9a8658f2d040256</a>
 */
public final class JavaTracker {

    private JavaTracker() {
        throw new UnsupportedOperationException("Utility class");
    }

    private static StackTraceElement[] getStack() {
        return new Throwable().getStackTrace();
    }

    public static Optional<Class<?>> getClassFromStack(final int offset) {
        final StackTraceElement element = getStack()[3 + offset];
        if (element == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(JavaAccessor.getClass(element.getClassName()));
    }

    public static Optional<Class<?>> getCallerClass() {
        return getClassFromStack(1);
    }

}
