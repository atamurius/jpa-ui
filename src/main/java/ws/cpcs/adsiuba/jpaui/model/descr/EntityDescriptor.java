package ws.cpcs.adsiuba.jpaui.model.descr;

import ws.cpcs.adsiuba.jpaui.model.Ident;

import javax.persistence.Entity;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static java.util.function.Function.identity;
import static java.util.stream.Collectors.toMap;
import static ws.cpcs.adsiuba.jpaui.model.descr.NameUtils.dashSeparated;
import static ws.cpcs.adsiuba.jpaui.model.descr.NameUtils.plural;
import static ws.cpcs.adsiuba.jpaui.model.descr.NameUtils.spaceSeparated;

/**
 * Describes entity for UI
 */
public class EntityDescriptor<T extends Ident> {

    private Class<T> entityClass;
    private String name;
    private String pluralName;
    private String id;
    private String iconId;
    private Map<String,List<Property<?>>> views = new HashMap<>();
    private Map<String,Property<?>> properties;

    private EntityDescriptor() { }

    public static class Builder {
        private EntityDescriptor<?> descriptor = new EntityDescriptor<>();

        @SuppressWarnings("unchecked")
        public Builder(Class<? extends Ident> entity) {
            requireNonNull(entity);
            requireNonNull(entity.getAnnotation(Entity.class), "Entity class should be annotated with @Entity");
            descriptor.entityClass = (Class) entity;
            descriptor.iconId = "file";
        }

        public Builder withName(String name) {
            descriptor.name = name;
            return this;
        }

        public Builder withPlural(String name) {
            descriptor.pluralName = name;
            return this;
        }

        public Builder withIcon(String name) {
            descriptor.iconId = name;
            return this;
        }

        public Builder withId(String name) {
            descriptor.id = name;
            return this;
        }

        public Builder withView(String name, Class<?> view) {
            if (! view.isAssignableFrom(descriptor.entityClass)) {
                throw new IllegalArgumentException(view +" is not interface of "+ descriptor.entityClass);
            }
            descriptor.views.put(name, Property.extract(view));
            return this;
        }

        public EntityDescriptor<?> build() {
            if (descriptor.name == null) {
                descriptor.name = spaceSeparated(descriptor.entityClass.getSimpleName());
            }
            if (descriptor.pluralName == null) {
                descriptor.pluralName = plural(descriptor.name);
            }
            if (descriptor.id == null) {
                descriptor.id = dashSeparated(descriptor.pluralName);
            }
            List<Property<?>> properties =
                    Property.extract(descriptor.entityClass);
            properties.removeIf(p -> p.getName().equals("Id"));
            descriptor.properties = properties.stream()
                    .collect(toMap(Property::getName,
                            identity(), (a, b) -> b, LinkedHashMap::new));
            descriptor.views.put("default", properties);
            return descriptor;
        }
    }

    public Class<T> getType() {
        return entityClass;
    }

    public String getName() {
        return name;
    }

    public String getPluralName() {
        return pluralName;
    }

    public String getIconId() {
        return iconId;
    }

    public String getId() {
        return id;
    }

    public List<Property<?>> getProperties() {
        return views.get("default");
    }

    public List<Property<?>> getProperties(String view) {
        return views.computeIfAbsent(view, v -> views.get("default"));
    }

    public Property<?> getProperty(String name) {
        return properties.computeIfAbsent(name, n -> {
            throw new IllegalArgumentException("Undefined property "+ entityClass.getCanonicalName() +"."+ name); });
    }

    public String getDescription(T item) {
        return name +" "+ item.getId(); // TODO title
    }
}
