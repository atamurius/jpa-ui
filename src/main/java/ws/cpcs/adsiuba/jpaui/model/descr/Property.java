package ws.cpcs.adsiuba.jpaui.model.descr;

import com.fasterxml.jackson.annotation.JsonIgnore;
import ws.cpcs.adsiuba.jpaui.model.UIEntity;
import ws.cpcs.adsiuba.jpaui.model.UIPropertiesOrder;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;

/**
 * Bean Property object
 */
public class Property<T> {
    private final EntityDescriptor<?> owner;
    private final Class<T> type;
    private final String name;
    private final String displayName;
    private final Annotation[] annotations;
    private final Method getter;
    private final Method setter;

    private Property(EntityDescriptor<?> owner, Class<T> type, String name, String displayName,
                    Annotation[] annotations, Method getter, Method setter) {
        this.owner = owner;
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

    public static Property<?> extract(EntityDescriptor<?> owner, String name, Map<String, Method> methods) {
        Method getter = methods.getOrDefault("get"+ name, methods.get("is"+ name));
        if (getter == null || ! name.equals(getterName(getter))) {
            throw new IllegalArgumentException("Invalid getter: "+ getter);
        }
        Method setter = methods.get("set" + name);
        if (! isSetterOfType(setter, getter.getReturnType())) {
            setter = null;
        }
        return new Property<>(
                owner,
                getter.getReturnType(),
                name,
                NameUtils.spaceSeparated(name), // TODO annotations?
                getter.getAnnotations(),
                getter,
                setter);
    }

    public static Map<String, Property<?>> extract(EntityDescriptor<?> owner, Class<?> type) {
        Map<String,Method> methods = Stream.of(type.getMethods())
                .filter(m -> ! m.getDeclaringClass().equals(Object.class))
                .collect(toMap(
                        Method::getName,
                        identity(),
                        (a,b) -> a.getReturnType().isAssignableFrom(b.getReturnType())
                            ? b : a,
                        LinkedHashMap::new));

        Map<String,Property<?>> props = methods.values().stream()
                .map(Property::getterName)
                .filter(Objects::nonNull)
                .collect(toMap(
                        identity(),
                        name -> extract(owner, name, methods),
                        (a,b) -> b,
                        LinkedHashMap::new));

        UIPropertiesOrder order = type.getAnnotation(UIPropertiesOrder.class);
        if (order != null) {
            Map<String,Property<?>> ordered = new LinkedHashMap<>();
            Stream.of(order.value())
                    .forEach(p -> ordered.put(p, props.get(p)));
            ordered.putAll(props);
            return ordered;
        }
        else {
            return props;
        }
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

    @JsonIgnore
    public EntityDescriptor<? extends UIEntity<?>> getOwner() {
        return (EntityDescriptor) owner;
    }

    @Override
    public String toString() {
        return getter.getDeclaringClass().getCanonicalName() +"."+ name +": "+ type;
    }

    public boolean isAnnotated(Class<? extends Annotation> aType) {
        return Stream.of(annotations).anyMatch(a -> aType.isAssignableFrom(a.getClass()));
    }
}



