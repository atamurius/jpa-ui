package ws.cpcs.adsiuba.jpaui.model.descr;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toMap;

/**
 * Bean Property object
 */
public class Property<T> {
    private final Class<T> type;
    private final String name;
    private final String displayName;
    private final Annotation[] annotations;
    private final Method getter;
    private final Method setter;

    private Property(Class<T> type, String name, String displayName,
                    Annotation[] annotations, Method getter, Method setter) {
        this.type = type;
        this.name = name;
        this.displayName = displayName;
        this.annotations = annotations;
        this.getter = getter;
        this.setter = setter;
    }

    public static String getterName(Method m) {
        String name = m.getName();
        if (name.matches("(get|is)[A-Z].*")
                && m.getParameterCount() == 0) {
            return name.substring(name.charAt(0) == 'g' ? 3 : 2);
        } else {
            return null;
        }
    }

    public static boolean isSetterOfType(Method m, Class type) {
        return m != null && m.getReturnType().equals(Void.TYPE)
                && m.getParameterCount() == 1
                && m.getParameterTypes()[0].equals(type);
    }

    public static Property<?> extract(String name, Map<String, Method> methods) {
        Method getter = methods.getOrDefault("get"+ name, methods.get("is"+ name));
        if (getter == null || ! name.equals(getterName(getter))) {
            throw new IllegalArgumentException("Invalid getter: "+ getter);
        }
        Method setter = methods.get("set" + name);
        if (! isSetterOfType(setter, getter.getReturnType())) {
            setter = null;
        }
        return new Property<>(
                getter.getReturnType(),
                name,
                NameUtils.spaceSeparated(name), // TODO annotations?
                getter.getAnnotations(),
                getter,
                setter);
    }

    public static List<Property<?>> extract(Class<?> type) {
        Map<String,Method> methods = Stream.of(type.getMethods())
                .filter(m -> ! m.getDeclaringClass().equals(Object.class))
                .collect(toMap(
                        Method::getName,
                        identity(),
                        (a,b) -> a.getReturnType().isAssignableFrom(b.getReturnType())
                            ? b : a));
        return methods.values().stream()
                .map(Property::getterName)
                .filter(Objects::nonNull)
                .map(n -> extract(n, methods))
                .collect(toList());
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return displayName;
    }

    public Class<T> getType() {
        return type;
    }

    public Stream<Annotation> getAnnotations(Class<? extends Annotation> type) {
        return Stream.of(annotations).filter(a -> type.isAssignableFrom(a.getClass()));
    }

    @SuppressWarnings("unchecked")
    public T read(Object inst) {
        try {
            return (T) getter.invoke(requireNonNull(inst));
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    "Getter "+ getter +" invocation failed on "+ inst.getClass());
        }
    }

    public void write(Object inst, T value) {
        try {
            setter.invoke(requireNonNull(inst), value);
        } catch (ReflectiveOperationException e) {
            throw new IllegalArgumentException(
                    "Setter "+ setter +" invocation failed on "+ inst.getClass() +
                    " with "+ (value == null ? "null" : value.getClass()));
        }
    }

    public boolean isReadOnly() {
        return setter == null;
    }

    @Override
    public String toString() {
        return getter.getDeclaringClass().getCanonicalName() +"."+ name +": "+ type;
    }
}



