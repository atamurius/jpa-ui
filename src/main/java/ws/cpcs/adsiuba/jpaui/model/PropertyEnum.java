package ws.cpcs.adsiuba.jpaui.model;

import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

import java.beans.PropertyDescriptor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.util.stream.Collectors.toMap;
import static ws.cpcs.adsiuba.jpaui.model.NameUtils.spaceSeparated;

/**
 * Entity property enumeration
 */
public class PropertyEnum {

    private List<Property> properties;

    public interface Property {
        String getName();
        String getDisplayName();
        Class getType();
        boolean isReadOnly();
    }

    public PropertyEnum(Class type) {
        Map<String, Method> methods = Stream.of(type.getMethods())
                .collect(toMap(
                        Method::getName,
                        Function.identity(),
                        (a,b) -> a.getDeclaringClass().isAssignableFrom(b.getDeclaringClass()) ? b : a));

        properties = methods.entrySet().stream()
                .filter(e -> isReader(e.getValue()) && ! e.getKey().equals("getClass") && ! e.getKey().equals("getId"))
                .map(e -> {
                    String name = e.getKey().substring(e.getKey().charAt(0) == 'g' ? 3 : 2);
                    Method write = methods.get("set"+ name);
                    if (! isWriter(write, e.getValue().getReturnType()))
                        write = null;
                    return new BeanProperty(name, null, e.getValue(), write);
                })
                .collect(Collectors.toList());
    }

    private boolean isReader(Method m) {
        return m != null && m.getName().matches("(get|is)[A-Z].*")
                && ! m.getReturnType().equals(Void.TYPE)
                && m.getParameterCount() == 0;
    }

    private boolean isWriter(Method m, Class type) {
        return m != null && m.getReturnType().equals(Void.TYPE)
                && m.getParameterCount() == 1
                && m.getParameterTypes()[0].equals(type);
    }

    public List<Property> getProperties() {
        return properties;
    }

    private static class BeanProperty implements Property {
        final String name;
        final String displayName;
        final Method readMethod;
        final Method writeMethod;
        BeanProperty(String name, String displayName, Method read, Method write) {
            this.name = name;
            this.displayName = displayName != null ? displayName : spaceSeparated(name);
            this.readMethod = read;
            this.writeMethod = write;
        }
        public String getName() {
            return name;
        }
        public String getDisplayName() {
            return displayName;
        }
        public Class getType() {
            return readMethod.getReturnType();
        }
        public boolean isReadOnly() {
            return writeMethod != null;
        }
    }
}
