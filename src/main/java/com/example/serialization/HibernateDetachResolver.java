package com.example.serialization;

import lombok.SneakyThrows;
import org.hibernate.Hibernate;
import org.hibernate.collection.internal.*;
import org.hibernate.proxy.HibernateProxy;
import org.hibernate.proxy.pojo.javassist.SerializableProxy;
import org.jboss.marshalling.ObjectResolver;

import java.lang.reflect.Field;
import java.util.*;

public class HibernateDetachResolver implements ObjectResolver {

    private final static Set<Class<?>> hibernateCollectionClasses = new TreeSet<>((o1, o2) -> o1.getName().compareTo(o2.getName()));

    static {
        Collections.addAll(hibernateCollectionClasses, PersistentList.class, PersistentSet.class, PersistentMap.class, PersistentSortedMap.class,
                PersistentBag.class, PersistentSortedSet.class);

    }

    @Override
    public Object readResolve(Object replacement) {
        return replacement;
    }

    @Override
    public Object writeReplace(Object original) {
        Object cleanObject = clean(original);
        return cleanCastCollection(cleanObject, Object.class);
    }


    /**
     * Clean the object for any Hibernate Collections types and Proxy information
     *
     * @param source - The object it should clean
     * @return the clean object
     */
    public <T> T cleanCastCollection(final Object source, Class<T> type) {
        Class<?> sourceClass = source.getClass();
        if (isProxyCollectionClass(sourceClass)) {
            return type.cast(cleanInitializedObject(source, sourceClass));
        }
        return type.cast(source);
    }

    @SuppressWarnings("unchecked")
    public static <If, Impl extends If> Impl clean(final If object) {
        if (isProxy(object)) {
            final HibernateProxy proxy = (HibernateProxy) object;
            final Impl impl = (Impl) proxy.getHibernateLazyInitializer().getImplementation();
            Hibernate.initialize(impl);
            return impl;
        } else if (isSerializableProxy(object)) {
            Hibernate.initialize(object);
            return (Impl) object;
        }
        return (Impl) object;
    }

    @SneakyThrows
    private Object cleanInitializedObject(final Object source, final Class<?> sourceClass) {
        if (sourceClass == null || Object.class.equals(sourceClass)) {
            return null;
        }

        Hibernate.initialize(source);

        Field[] fields = sourceClass.getDeclaredFields();

        for (Field field : fields) {
            String name = field.getName();
            if ("map".equals(name)) { //$NON-NLS-1$
                field.setAccessible(true);
                Object object = field.get(source);
                return Collections.emptyMap().getClass().isInstance(object) || object == null ? new HashMap<Object, Object>() : object;
            } else if ("set".equals(name)) { //$NON-NLS-1$
                field.setAccessible(true);
                Object object = field.get(source);
                return Collections.emptySet().getClass().isInstance(object) || object == null ? new HashSet<Object>() : object;
            } else if ("list".equals(name)) { //$NON-NLS-1$
                field.setAccessible(true);
                Object object = field.get(source);
                return Collections.emptyList().getClass().isInstance(object) || object == null ? new ArrayList<Object>() : object;
            } else if ("bag".equals(name)) { //$NON-NLS-1$
                field.setAccessible(true);
                Object object = field.get(source);
                return Collections.emptyList().getClass().isInstance(object) || object == null ? new ArrayList<Object>() : object;
            }
        }

        return cleanInitializedObject(source, sourceClass.getSuperclass());
    }

    /**
     * @param type - the class it should test for hibernate collections classes
     * @return true if it's a hibernate collection type
     */
    private boolean isProxyCollectionClass(final Class<?> type) {
        return hibernateCollectionClasses.contains(type);
    }

    private static boolean isProxy(Object o) {
        return (o instanceof HibernateProxy);
    }

    private static boolean isSerializableProxy(Object o) {
        return (o instanceof SerializableProxy);
    }

}
