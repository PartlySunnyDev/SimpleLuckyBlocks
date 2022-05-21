package me.partlysunny.util.reflection;

import sun.misc.Unsafe;

import java.lang.annotation.Annotation;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles.Lookup;
import java.lang.invoke.VarHandle;
import java.lang.reflect.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;

/*
 * NOT BY ME!!! https://gist.github.com/Lauriichan/294c64b63067dcb6a9a8658f2d040256
 * */
public final class JavaAccessor {

    private static final JavaAccessor INSTANCE = new JavaAccessor();

    private final AccessUnsuccessful unsuccessful = new AccessUnsuccessful();

    private Unsafe unsafe;
    private Lookup lookup;

    private JavaAccessor() {
        final Optional<Class<?>> option = JavaTracker.getCallerClass();
        if (option.isEmpty() || option.get() != JavaAccessor.class) {
            throw new UnsupportedOperationException("Utility class");
        }
    }

    public static Object getStatWithBonusicValue(final VarHandle handle) {
        return INSTANCE.getValueSafe(null, handle);
    }

    public static Object getValue(final Object instance, final VarHandle handle) {
        return INSTANCE.getValueSafe(instance, handle);
    }

    /*
     * Method invokation
     */

    public static void setStaticValue(final VarHandle handle, final Object value) {
        INSTANCE.setValueSafe(null, handle, value);
    }

    public static void setValue(final Object instance, final VarHandle handle, final Object value) {
        INSTANCE.setValueSafe(instance, handle, value);
    }

    /*
     * Safe Accessors
     */

    public static Object invokeStatic(final MethodHandle handle, final Object... arguments) {
        return INSTANCE.executeSafe(null, handle, arguments);
    }

    public static Object invoke(final Object instance, final MethodHandle handle, final Object... arguments) {
        return INSTANCE.executeSafe(instance, handle, arguments);
    }

    public static Object instance(final Class<?> clazz) {
        return INSTANCE.init(getConstructor(clazz));
    }

    public static Object instance(final Constructor<?> constructor, final Object... arguments) {
        return INSTANCE.init(constructor, arguments);
    }

    public static Object invokeStatic(final Method method, final Object... arguments) {
        return INSTANCE.execute(null, method, arguments);
    }

    /*
     * Safe Accessors helper
     */

    public static Object invoke(final Object instance, final Method method, final Object... arguments) {
        return INSTANCE.execute(instance, method, arguments);
    }

    public static void setValue(final Object instance, final Class<?> clazz, final String fieldName, final Object value) {
        setValue(instance, getField(clazz, fieldName), value);
    }

    public static void setObjectValue(final Object instance, final Class<?> clazz, final String fieldName, final Object value) {
        setObjectValue(instance, getField(clazz, fieldName), value);
    }

    /*
     * Safe Field Modifier
     */

    public static void setStaticValue(final Class<?> clazz, final String fieldName, final Object value) {
        setStaticValue(getField(clazz, fieldName), value);
    }

    public static void setValue(final Object instance, final Field field, final Object value) {
        if (field == null) {
            return;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            setStaticValue(field, value);
            return;
        }
        setObjectValue(instance, field, value);
    }

    public static void setObjectValue(final Object instance, final Field field, final Object value) {
        if (instance == null || field == null) {
            return;
        }
        try {
            INSTANCE.setObjectValueSafe(instance, field, value);
        } catch (final AccessUnsuccessful unsafe) {
            INSTANCE.setObjectValueUnsafe(instance, field, value);
        }
    }

    public static void setStaticValue(final Field field, final Object value) {
        if (field == null) {
            return;
        }
        try {
            INSTANCE.setStaticValueSafe(field, value);
        } catch (final AccessUnsuccessful unsafe) {
            INSTANCE.setStaticValueUnsafe(field, value);
        }
    }

    /*
     * Unsafe Field Modifier
     */

    public static Object getValue(final Object instance, final Class<?> clazz, final String fieldName) {
        return getValue(instance, getField(clazz, fieldName));
    }

    public static Object getObjectValue(final Object instance, final Class<?> clazz, final String fieldName) {
        return getObjectValue(instance, getField(clazz, fieldName));
    }

    public static Object getStatWithBonusicValue(final Class<?> clazz, final String fieldName) {
        return getStatWithBonusicValue(getField(clazz, fieldName));
    }

    public static Object getValue(final Object instance, final Field field) {
        if (field == null) {
            return null;
        }
        if (Modifier.isStatic(field.getModifiers())) {
            return getStatWithBonusicValue(field);
        }
        return getObjectValue(instance, field);
    }

    /*
     * Internal Utilities
     */

    public static Object getObjectValue(final Object instance, final Field field) {
        if (instance == null || field == null) {
            return null;
        }
        try {
            return INSTANCE.getObjectValueSafe(instance, field);
        } catch (final AccessUnsuccessful unsafe) {
            return INSTANCE.getObjectValueUnsafe(instance, field);
        }
    }

    /*
     * Static Accessors Helper
     */

    public static Object getStatWithBonusicValue(final Field field) {
        if (field == null) {
            return null;
        }
        try {
            return INSTANCE.getStatWithBonusicValueSafe(field);
        } catch (final AccessUnsuccessful unsafe) {
            return INSTANCE.getStatWithBonusicValueUnsafe(field);
        }
    }

    public static VarHandle accessField(final Field field) {
        return INSTANCE.handle(field, false);
    }

    public static VarHandle accessField(final Field field, final boolean forceModification) {
        return INSTANCE.handle(field, forceModification);
    }

    public static MethodHandle accessFieldGetter(final Field field) {
        return INSTANCE.handleGetter(field);
    }

    public static MethodHandle accessFieldSetter(final Field field) {
        return INSTANCE.handleSetter(field);
    }

    public static MethodHandle accessMethod(final Method method) {
        return INSTANCE.handle(method);
    }

    /*
     * Static Implementation
     */

    // Invokation

    public static MethodHandle accessConstructor(final Constructor<?> constructor) {
        return INSTANCE.handle(constructor);
    }

    public static Field getField(final Class<?> clazz, final String field) {
        if (clazz == null || field == null) {
            return null;
        }
        try {
            return clazz.getDeclaredField(field);
        } catch (NoSuchFieldException | SecurityException ignore) {
            try {
                return clazz.getField(field);
            } catch (NoSuchFieldException | SecurityException ignore0) {
                return null;
            }
        }
    }

    public static Field getFieldOfType(Class<?> clazz, Class<?> type) {
        return getFieldOfType(clazz, type, 0);
    }

    public static Field getFieldOfType(Class<?> clazz, Class<?> type, int index) {
        final Field[] field0 = clazz.getFields();
        final Field[] field1 = clazz.getDeclaredFields();
        final ArrayList<Field> fields = new ArrayList<>();
        for (Field field : field0) {
            if (!field.getType().equals(type) || fields.contains(field)) {
                continue;
            }
            fields.add(field);
        }
        for (Field field : field1) {
            if (!field.getType().equals(type) || fields.contains(field)) {
                continue;
            }
            fields.add(field);
        }
        if (fields.isEmpty() || index >= fields.size()) {
            return null;
        }
        return fields.get(index);
    }

    // Setter

    public static Field[] getFields(final Class<?> clazz) {
        final Field[] field0 = clazz.getFields();
        final Field[] field1 = clazz.getDeclaredFields();
        final HashSet<Field> fields = new HashSet<>();
        Collections.addAll(fields, field0);
        Collections.addAll(fields, field1);
        return fields.toArray(Field[]::new);
    }

    public static Field[] getFieldsOfType(Class<?> clazz, Class<?> type) {
        final Field[] field0 = clazz.getFields();
        final Field[] field1 = clazz.getDeclaredFields();
        final HashSet<Field> fields = new HashSet<>();
        for (Field field : field0) {
            if (!field.getType().equals(type)) {
                continue;
            }
            fields.add(field);
        }
        for (Field field : field1) {
            if (!field.getType().equals(type)) {
                continue;
            }
            fields.add(field);
        }
        return fields.toArray(Field[]::new);
    }

    public static Method getMethod(final Class<?> clazz, final String method, final Class<?>... arguments) {
        if (clazz == null || method == null) {
            return null;
        }
        try {
            return clazz.getDeclaredMethod(method, arguments);
        } catch (NoSuchMethodException | SecurityException ignore) {
            try {
                return clazz.getMethod(method, arguments);
            } catch (NoSuchMethodException | SecurityException ignore0) {
                return null;
            }
        }
    }

    public static Method[] getMethods(final Class<?> clazz) {
        final Method[] method0 = clazz.getMethods();
        final Method[] method1 = clazz.getDeclaredMethods();
        final HashSet<Method> methods = new HashSet<>();
        Collections.addAll(methods, method0);
        Collections.addAll(methods, method1);
        return methods.toArray(Method[]::new);
    }

    public static Constructor<?> getConstructor(final Class<?> clazz, final Class<?>... arguments) {
        if (clazz == null) {
            return null;
        }
        try {
            return clazz.getDeclaredConstructor(arguments);
        } catch (NoSuchMethodException | SecurityException ignore) {
            try {
                return clazz.getConstructor(arguments);
            } catch (NoSuchMethodException | SecurityException ignore0) {
                return null;
            }
        }
    }

    public static Constructor<?>[] getConstructors(final Class<?> clazz) {
        final Constructor<?>[] constructor0 = clazz.getConstructors();
        final Constructor<?>[] constructor1 = clazz.getDeclaredConstructors();
        final HashSet<Constructor<?>> constructors = new HashSet<>();
        Collections.addAll(constructors, constructor0);
        Collections.addAll(constructors, constructor1);
        return constructors.toArray(Constructor[]::new);

    }

    // Getter

    public static Class<?> getClass(final String name) {
        try {
            return Class.forName(name);
        } catch (final ClassNotFoundException | LinkageError e) {
            return null;
        }
    }

    public static Class<?> getClass(final Class<?> clazz, final String name) {
        if (clazz == null || name == null) {
            return null;
        }
        final int size = clazz.getClasses().length + clazz.getDeclaredClasses().length;
        if (size == 0) {
            return null;
        }
        final Class<?>[] classes = new Class<?>[size];
        final Class<?>[] tmp = clazz.getClasses();
        System.arraycopy(tmp, 0, classes, 0, tmp.length);
        System.arraycopy(clazz.getDeclaredClasses(), tmp.length, classes, tmp.length, size - tmp.length);
        for (int i = 0; i < size; i++) {
            String target = classes[i].getSimpleName();
            if (target.contains(".")) {
                target = target.split(".", 2)[0];
            }
            if (target.equals(name)) {
                return classes[i];
            }
        }
        return null;
    }

    public static Class<?> getClassFromField(final Class<?> clazz, final boolean declared, final Class<?>... blacklistArray) {
        if (clazz == null) {
            return null;
        }
        final Class<?>[] blacklist = blacklistArray;
        final Field[] fields = getFields(clazz);
        for (Field field : fields) {
            if (Modifier.isStatic(field.getModifiers()) && declared) {
                continue;
            }
            boolean passed = true;
            for (Class<?> forbidden : blacklist) {
                if (forbidden.isAssignableFrom(field.getType())) {
                    passed = false;
                    break;
                }
            }
            if (!passed) {
                continue;
            }
            return field.getType();
        }
        return null;
    }

    public static <A extends Annotation> A getAnnotation(final AnnotatedElement element, final Class<A> annotationType) {
        final A annotation = element.getAnnotation(annotationType);
        if (annotation != null) {
            return annotation;
        }
        return element.getDeclaredAnnotation(annotationType);
    }

    @SuppressWarnings("unchecked")
    public static <A extends Annotation> A[] getAnnotations(final AnnotatedElement element, final Class<A> annotationType) {
        final A[] annotation0 = element.getAnnotationsByType(annotationType);
        final A[] annotation1 = element.getDeclaredAnnotationsByType(annotationType);
        if (annotation0.length != 0 && annotation1.length != 0) {
            final HashSet<A> annotations = new HashSet<>();
            Collections.addAll(annotations, annotation0);
            Collections.addAll(annotations, annotation1);
            return annotations.toArray((A[]) Array.newInstance(annotationType, annotations.size()));
        }
        if (annotation0.length == 0) {
            return annotation1;
        }
        return annotation0;
    }

    public static <A extends Annotation> Optional<A> getOptionalAnnotation(final AnnotatedElement element, final Class<A> annotationType) {
        return Optional.ofNullable(getAnnotation(element, annotationType));
    }

    /*
     * Static Accessors
     */

    public Unsafe unsafe() {
        if (unsafe != null) {
            return unsafe;
        }
        try {
            final Field field = Unsafe.class.getDeclaredField("theUnsafe");
            field.setAccessible(true);
            return unsafe = (Unsafe) field.get(null);
        } catch (final Exception exp) {
            return null;
        }
    }

    public Lookup lookup() {
        if (lookup != null) {
            return lookup;
        }
        return lookup = (Lookup) getStatWithBonusicValueUnsafe(getField(Lookup.class, "IMPL_LOOKUP"));
    }

    public Object execute(final Object instance, final Method method, final Object... arguments) {
        if (method == null || method.getParameterCount() != arguments.length) {
            return null;
        }
        try {
            if (!Modifier.isStatic(method.getModifiers())) {
                if (instance == null) {
                    return null;
                }
                if (arguments.length == 0) {
                    return lookup().unreflect(method).invokeWithArguments(instance);
                }
                final Object[] input = new Object[arguments.length + 1];
                input[0] = instance;
                System.arraycopy(arguments, 0, input, 1, arguments.length);
                return lookup().unreflect(method).invokeWithArguments(input);
            }
            return lookup().unreflect(method).invokeWithArguments(arguments);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object init(final Constructor<?> constructor, final Object... arguments) {
        if (constructor == null || constructor.getParameterCount() != arguments.length) {
            return null;
        }
        try {
            return lookup().unreflectConstructor(constructor).invokeWithArguments(arguments);
        } catch (final Throwable e) {
            return null;
        }
    }

    public VarHandle handle(final Field field, final boolean force) {
        if (field == null) {
            return null;
        }
        if (force) {
            unfinalize(field);
        }
        try {
            return lookup().unreflectVarHandle(field);
        } catch (final Throwable e) {
            return null;
        }
    }

    public MethodHandle handleGetter(final Field field) {
        if (field == null) {
            return null;
        }
        try {
            return lookup().unreflectGetter(field);
        } catch (final Throwable e) {
            return null;
        }
    }

    /*
     * Static Utilities
     */

    public MethodHandle handleSetter(final Field field) {
        if (field == null) {
            return null;
        }
        unfinalize(field);
        try {
            return lookup().unreflectSetter(field);
        } catch (final Throwable e) {
            return null;
        }
    }

    public MethodHandle handle(final Method method) {
        if (method == null) {
            return null;
        }
        try {
            return lookup().unreflect(method);
        } catch (final Throwable e) {
            return null;
        }
    }

    public MethodHandle handle(final Constructor<?> constructor) {
        if (constructor == null) {
            return null;
        }
        try {
            return lookup().unreflectConstructor(constructor);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object executeSafe(final Object instance, final MethodHandle handle, final Object... arguments) {
        if (handle == null || handle.type().parameterCount() != arguments.length) {
            return null;
        }
        try {
            if (instance != null) {
                if (arguments.length == 0) {
                    return handle.invokeWithArguments(instance);
                }
                final Object[] input = new Object[arguments.length + 1];
                input[0] = instance;
                System.arraycopy(arguments, 0, input, 1, arguments.length);
                return handle.invokeWithArguments(input);
            }
            return handle.invokeWithArguments(arguments);
        } catch (final Throwable e) {
            return null;
        }
    }

    public Object getValueSafe(final Object instance, final VarHandle handle) {
        if (handle == null) {
            return null;
        }
        try {
            if (instance == null) {
                return handle.getVolatile();
            }
            return handle.getVolatile(instance);
        } catch (final Throwable e) {
            throw unsuccessful;
        }
    }

    public void setValueSafe(final Object instance, final VarHandle handle, final Object value) {
        if (handle == null || value != null && !handle.varType().isAssignableFrom(value.getClass())) {
            return;
        }
        try {
            if (instance != null) {
                handle.setVolatile(value);
                return;
            }
            handle.setVolatile(instance, value);
        } catch (final Throwable e) {
            throw unsuccessful;
        }
    }

    public Object getObjectValueSafe(final Object instance, final Field field) {
        if (instance == null || field == null) {
            return null;
        }
        try {
            return lookup().unreflectGetter(field).invoke(instance);
        } catch (final Throwable e) {
            throw unsuccessful;
        }
    }

    public Object getStatWithBonusicValueSafe(final Field field) {
        if (field == null) {
            return null;
        }
        try {
            return lookup().unreflectGetter(field).invoke();
        } catch (final Throwable e) {
            throw unsuccessful;
        }
    }

    public void setObjectValueSafe(final Object instance, final Field field, final Object value) {
        if (instance == null || field == null || value != null && !field.getType().isAssignableFrom(value.getClass())) {
            return;
        }
        unfinalize(field);
        try {
            lookup().unreflectSetter(field).invokeWithArguments(instance, value);
        } catch (final Throwable e) {
            throw unsuccessful;
        }
    }

    public void setStaticValueSafe(final Field field, final Object value) {
        if (field == null || value != null && !field.getType().isAssignableFrom(value.getClass())) {
            return;
        }
        unfinalize(field);
        try {
            lookup().unreflectSetter(field).invokeWithArguments(value);
        } catch (final Throwable e) {
            throw unsuccessful;
        }
    }

    public Object getObjectValueUnsafe(final Object instance, final Field field) {
        if (instance == null || field == null) {
            return null;
        }
        final Unsafe unsafe = unsafe();
        return unsafe.getObjectVolatile(instance, unsafe.objectFieldOffset(field));
    }

    public Object getStatWithBonusicValueUnsafe(final Field field) {
        if (field == null) {
            return null;
        }
        final Unsafe unsafe = unsafe();
        return unsafe.getObjectVolatile(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field));
    }

    public void setObjectValueUnsafe(final Object instance, final Field field, final Object value) {
        if (instance == null || field == null) {
            return;
        }
        unfinalize(field);
        final Unsafe unsafe = unsafe();
        if (value == null) {
            unsafe.putObject(instance, unsafe.objectFieldOffset(field), null);
            return;
        }
        if (field.getType().isAssignableFrom(value.getClass())) {
            unsafe.putObject(instance, unsafe.objectFieldOffset(field), field.getType().cast(value));
        }
    }

    public void setStaticValueUnsafe(final Field field, final Object value) {
        if (field == null) {
            return;
        }
        unfinalize(field);
        final Unsafe unsafe = unsafe();
        if (value == null) {
            unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), null);
            return;
        }
        if (field.getType().isAssignableFrom(value.getClass())) {
            unsafe.putObject(unsafe.staticFieldBase(field), unsafe.staticFieldOffset(field), field.getType().cast(value));
        }
    }

    private void unfinalize(final Field field) {
        if (!Modifier.isFinal(field.getModifiers())) {
            return;
        }
        try {
            lookup().findSetter(Field.class, "modifiers", int.class).invokeExact(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (final Throwable e) {
            // Ignore
        }
    }

    /*
     * Internal Exceptions
     */

    private static final class AccessUnsuccessful extends RuntimeException {
        private static final long serialVersionUID = 1L;
    }

}
